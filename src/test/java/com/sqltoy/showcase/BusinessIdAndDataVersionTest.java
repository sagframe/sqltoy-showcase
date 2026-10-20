package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.VersionCase;

/**
 * Business primary keys and optimistic data versions (see the sqltoy_primarykey doc).
 *
 * The dataVersion test runs on H2. The @BusinessId part is a DISABLED reference: the
 * Redis-backed generator needs a redisTemplate bean, which this showcase does not ship —
 * for a live run see the quickstart's Lesson 1 (it starts an embedded Redis and generates
 * PO26101400000001-style keys for real).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class BusinessIdAndDataVersionTest {

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

	/**
	 * @BusinessId reference implementation (needs redis — see the class comment).
	 *
	 * Entity side: a Redis INCR counter produces sequential, rule-based keys that are
	 * unique and strictly increasing across all application nodes:
	 *
	 * <pre>
	 * &#64;Id
	 * &#64;BusinessId(generator = "org.sagacity.sqltoy.plugins.id.impl.RedisIdGenerator",
	 *             signature = "PO@df(yyMMdd)", length = 16)
	 * &#64;Column(name = "ORDER_ID", length = 16)
	 * private String orderId;
	 * </pre>
	 *
	 * save() then assigns orderId BEFORE the insert (PO + yyMMdd + 8-digit sequence,
	 * e.g. PO26101400000001). Programmatic forms through LightDao:
	 *
	 * <pre>
	 * // 1) from the entity's @BusinessId config
	 * String orderId = lightDao.generateBizId(orderInfo);
	 * // 2) dynamic: signature supports @case/@day/@df/@substr macros; keys are managed per table
	 * String bizId = lightDao.generateBizId("sqltoy_order_info",
	 *         "HW@case(orderType,SALE,SC,BUY,PO)@day(yyMMdd)",
	 *         MapKit.map("orderType", "SALE"), null, 20, 5);
	 * // 3) simple form: unique sign + increment
	 * long id = lightDao.generateBizId("ORDER", 1);
	 * </pre>
	 */
	@Test
	@Disabled("reference implementation: RedisIdGenerator needs a redisTemplate bean;"
			+ " see the quickstart Lesson 1 for a live run with embedded Redis")
	void businessIdGeneratesSequentialRuleBasedKeys() {
		// intentionally empty — the javadoc above is the reference
	}
}
