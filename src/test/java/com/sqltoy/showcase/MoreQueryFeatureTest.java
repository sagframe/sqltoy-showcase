package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Query features from the classic xml showcase, each with a runnable proof:
 *
 *  - tuple IN      : ((a,b) in (:xs;:ys)) matches PAIRS, not a cartesian product
 *  - @value() order: ORDER BY driven by parameters, injection-guarded by sqltoy
 *  - in auto-split : 1000+ values are rewritten into OR-chunked IN lists automatically
 *  - cache-arg     : a fuzzy name is reverse-matched to precise ids via the translate
 *                    cache, replacing a JOIN + LIKE
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class MoreQueryFeatureTest {

	@Autowired
	LightDao lightDao;

	private List<Object[]> rows(String sqlId, Map<String, Object> params) {
		@SuppressWarnings("rawtypes")
		List raw = lightDao.find(sqlId, params);
		List<Object[]> out = new ArrayList<Object[]>();
		for (Object r : raw) {
			out.add((r instanceof Object[]) ? (Object[]) r : ((List) r).toArray());
		}
		return out;
	}

	@Test
	void tupleInMatchesPairsNotACartesianProduct() {
		// paired tuples (100005,F) and (100007,M): Alice + Frank — NOT the 4-row cartesian
		Map<String, Object> params = MapKit.keys("organs", "sexes")
				.values(new String[] { "100005", "100007" }, new String[] { "F", "M" });
		List<Object[]> result = rows("sc_tuple_in", params);
		result.forEach(r -> System.out.println("tuple match: " + r[0] + "\t" + r[1] + "\t" + r[2] + "\t" + r[3]));
		assertEquals(2, result.size());
		assertTrue(result.stream().anyMatch(r -> "Alice Chen".equals(r[1])));
		assertTrue(result.stream().anyMatch(r -> "Frank Wu".equals(r[1])));
	}

	@Test
	void valueOrderFollowsParameters() {
		Map<String, Object> byDate = MapKit.keys("orderField", "orderWay").values("ENTRY_DATE", "desc");
		Map<String, Object> byName = MapKit.keys("orderField", "orderWay").values("STAFF_NAME", "asc");
		List<Object[]> dateFirst = rows("sc_value_order", byDate);
		List<Object[]> nameFirst = rows("sc_value_order", byName);
		System.out.println("by ENTRY_DATE desc: " + dateFirst.get(0)[1] + " first");
		System.out.println("by STAFF_NAME asc: " + nameFirst.get(0)[1] + " first");
		// Frank Wu entered last -> first by date desc; Alice Chen -> first by name asc
		assertEquals("Frank Wu", dateFirst.get(0)[1]);
		assertEquals("Alice Chen", nameFirst.get(0)[1]);
		assertNotEquals(dateFirst.get(0)[1], nameFirst.get(0)[1]);
	}

	@Test
	void inWithMoreThanAThousandValuesIsAutoSplit() {
		// 1500 ids: only S0001..S0006 exist; the framework rewrites the single IN into
		// OR-chunked INs (max 1000 each) — the query just works
		List<String> ids = new ArrayList<String>();
		ids.addAll(List.of("S0001", "S0002", "S0003", "S0004", "S0005", "S0006"));
		for (int i = 0; i < 1494; i++) {
			ids.add("X" + i);
		}
		List<Object[]> result = rows("sc_in_split", MapKit.keys("ids").values(ids));
		System.out.println("1500-value in matched rows: " + result.size());
		assertEquals(6, result.size());
	}

	@Test
	void cacheArgReverseMatchesNamesToIds() {
		// exact name -> one id
		Map<String, Object> alice = MapKit.keys("staffName").values("Alice");
		List<Object[]> exact = rows("sc_cache_arg_case", alice);
		System.out.println("reverse-match 'Alice': " + exact.size() + " row(s)");
		assertEquals(1, exact.size());
		assertEquals("Alice Chen", exact.get(0)[1]);

		// fuzzy name -> every staff whose name contains it, still without any LIKE on the table
		Map<String, Object> fuzzy = MapKit.keys("staffName").values("a");
		List<Object[]> many = rows("sc_cache_arg_case", fuzzy);
		System.out.println("reverse-match 'a' (fuzzy): " + many.size() + " row(s)");
		assertTrue(many.size() >= 4, "Alice/Carol Wang/David Li/Frank Wu all contain 'a'");
	}

	@Test
	void translateCacheFeedsUiComponentsWithoutAQuery() {
		// raw form: id -> [ALL cache columns] (cols[0] repeats the key, cols[1] is the name);
		// the whole "select options" list comes straight from the in-memory cache — no database round trip
		java.util.HashMap<String, Object[]> raw = lightDao.getTranslateCache("staffIdName", null);
		System.out.println("dropdown options from cache: " + raw.size());
		raw.forEach((id, cols) -> System.out.println("  " + id + "	" + cols[1]));
		assertEquals(6, raw.size());

		// object form: columns map onto the VO properties, ready for the frontend
		List<com.sqltoy.showcase.dto.OptionVO> options = lightDao.getTranslateCache("staffIdName", null,
				com.sqltoy.showcase.dto.OptionVO.class);
		System.out.println("typed options: " + options.get(0).getStaffId() + " -> " + options.get(0).getStaffName());
		assertEquals(6, options.size());
		assertEquals("Alice Chen", options.get(0).getStaffName());
	}

	@Test
	void suggestFilteringRunsOnTheCacheInMemory() {
		// a suggest/autocomplete box filters the cache entries by name in memory —
		// zero database queries while the user types
		java.util.HashMap<String, Object[]> cache = lightDao.getTranslateCache("staffIdName", null);
		List<String> matched = new ArrayList<String>();
		cache.forEach((id, cols) -> {
			if (String.valueOf(cols[1]).toLowerCase().contains("chen")) {
				matched.add(id + ":" + cols[1]);
			}
		});
		System.out.println("suggest 'chen' -> " + matched);
		assertEquals(1, matched.size());
	}
}
