package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.VersionCase;

/**
 * Optimistic data version: the @DataVersion field is checked and bumped on update —
 * a stale version simply updates nothing (lost updates become visible instead of silent).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class DataVersionTest {

	@Autowired
	LightDao lightDao;

	@Test
	void updateBumpsTheVersionAndStaleVersionsUpdateNothing() {
		VersionCase entity = new VersionCase().setId("VER-01").setTitle("v0").setStatus(1);
		lightDao.save(entity);

		VersionCase first = lightDao.load(new VersionCase().setId("VER-01"));
		System.out.println("after save, version = " + first.getDataVersion());
		assertEquals(Long.valueOf(1L), first.getDataVersion());

		// NOTE: sqltoy bumps the version ON the passed entity in place, so capture it first
		long staleVersion = first.getDataVersion();
		first.setTitle("v1");
		lightDao.update(first);
		VersionCase second = lightDao.load(new VersionCase().setId("VER-01"));
		System.out.println("after update, version = " + second.getDataVersion() + ", title = " + second.getTitle());
		assertTrue(second.getDataVersion() > staleVersion);

		// simulate a stale writer: it still carries the old version -> the update FAILS LOUDLY
		// (a DataAccessException naming the version conflict) instead of silently overwriting
		VersionCase stale = new VersionCase().setId("VER-01").setTitle("stale-overwrite");
		stale.setDataVersion(staleVersion);
		org.sagacity.sqltoy.exception.DataAccessException conflict = assertThrows(
				org.sagacity.sqltoy.exception.DataAccessException.class, () -> lightDao.update(stale));
		System.out.println("stale update rejected: " + conflict.getMessage());
		VersionCase after = lightDao.load(new VersionCase().setId("VER-01"));
		assertEquals(second.getTitle(), after.getTitle());
		System.out.println("title still = " + after.getTitle() + " (no lost update)");
	}
}
