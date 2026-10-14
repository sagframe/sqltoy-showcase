package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SQL composition with @include: reuse a fragment by sqlId, or inject a fragment
 * assembled in Java at runtime through a parameter.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class IncludeTest {

	@Autowired
	LightDao lightDao;

	@Test
	void includeReusesASqlFragment() {
		// sc_include_case starts with @include(sc_include_part) and adds one condition
		Map<String, Object> params = MapKit.keys("organId").values("100005");
		@SuppressWarnings("rawtypes")
		List rows = lightDao.find("sc_include_case", params);
		System.out.println("include + organ filter rows: " + rows.size());
		// order-independent: at least the 2 seeded staff, and EVERY row is organ 100005
		assertTrue(rows.size() >= 2);
		rows.forEach(r -> {
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			assertEquals("100005", a[2]);
		});
	}

	@Test
	void includeInjectsARuntimeFragment() {
		// the fragment itself comes from a Java parameter — compose dynamic column sets etc.
		String part = "select t.STAFF_ID,t.STAFF_NAME from SQLTOY_STAFF_INFO t where t.STATUS=1";
		Map<String, Object> params = MapKit.keys("sqlPart").values(part);
		@SuppressWarnings("rawtypes")
		List rows = lightDao.find("@include(:sqlPart)", params);
		System.out.println("runtime include rows: " + rows.size());
		// order-independent: other tests may insert staff; all active staff must appear
		assertTrue(rows.size() >= 6);
	}
}
