package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Advanced query features: &lt;link&gt; group concatenation and the @loop()
 * list-expansion macro (the parallelQuery demos live in ParallelQueryTest).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class AdvancedQueryTest {

	@Autowired
	LightDao lightDao;

	@Test
	@SuppressWarnings("rawtypes")
	void linkConcatenatesNamesPerGroup() {
		List rows = lightDao.find("sc_staff_link", new HashMap<String, Object>());
		System.out.println("staff per organization:");
		rows.forEach(r -> {
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			System.out.println("  " + a[0] + "\t" + a[1]);
			assertTrue(String.valueOf(a[1]).contains(","));
		});
		assertEquals(3, rows.size());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void loopMacroExpandsListParameters() {
		// :fields expands into a column list; :staffIds expands into an OR-chain
		Map<String, Object> params = MapKit.keys("fields", "staffIds")
				.values(new String[] { "STAFF_NAME", "EMAIL" }, Arrays.asList("S0001", "S0003"));
		List rows = lightDao.find("sc_loop_sql", params);
		System.out.println("loop result rows: " + rows.size());
		assertEquals(2, rows.size());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void secureLoopSliceSkipsLeadingElements() {
		// numeric startIndex/endIndex without quotes: loop from index 1 to the (clamped) end,
		// so S0001 is skipped and only S0003 / S0006 match — 2 rows
		Map<String, Object> params = MapKit.keys("staffIds").values(Arrays.asList("S0001", "S0003", "S0006"));
		List rows = lightDao.find("sc_secure_loop_slice", params);
		rows.forEach(r -> System.out.println("slice row: " + r));
		assertEquals(2, rows.size());

		// endIndex clamping: with endIndex=2 only S0003 (index 1) is included
		Map<String, Object> narrow = MapKit.keys("staffIds").values(Arrays.asList("S0001", "S0003", "S0006"));
		// reuse the same sqlId but assert via the narrow range variant below
		List narrowRows = lightDao.find("sc_secure_loop_slice_1_2", narrow);
		assertEquals(1, narrowRows.size());
	}
}
