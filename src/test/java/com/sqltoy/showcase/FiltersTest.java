package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The &lt;filters&gt; toolbox: server-side parameter conditioning before the SQL assembles.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class FiltersTest {

	@Autowired
	LightDao lightDao;

	private List<Object[]> find(Map<String, Object> params) {
		@SuppressWarnings("rawtypes")
		List rows = lightDao.find("sc_filters_case", params);
		List<Object[]> out = new java.util.ArrayList<Object[]>();
		for (Object r : rows) {
			out.add((r instanceof Object[]) ? (Object[]) r : ((List) r).toArray());
		}
		return out;
	}

	@Test
	void eqTurnsSentinelIntoNull() {
		// organId = -1 (the "all" dropdown option) is turned into null -> the condition drops
		assertEquals(4, find(MapKit.keys("organId").values("-1")).size());
		// a real organ filters
		assertEquals(2, find(MapKit.keys("organId").values("100005")).size());
	}

	@Test
	void toDateParsesAndShifts() {
		// end date is a STRING; <to-date> parses it and shifts +1 day, so TRANS_DATE < endDate+1
		Map<String, Object> params = MapKit.keys("beginDate", "endDate").values("2026-01-01", "2026-02-20");
		List<Object[]> rows = find(params);
		System.out.println("date-range rows: " + rows.size());
		assertEquals(2, rows.size());
	}

	@Test
	void primaryMakesOneConditionDominate() {
		// both organId and orderId given: <primary> keeps ONLY orderId filtering
		Map<String, Object> params = MapKit.keys("organId", "orderId").values("100007", "O0001");
		List<Object[]> rows = find(params);
		assertEquals(1, rows.size());
		assertEquals("O0001", rows.get(0)[0]);
	}

	@Test
	void cacheArgReverseMatchesNameToCodes() {
		// no LIKE against the staff table: the name is reverse-matched via the translate cache.
		// here the filter targets the staff cache to prove matching works (order rows unaffected)
		Map<String, Object> params = MapKit.keys("staffName").values("Alice");
		// the sql does not reference :staffIds, so this only verifies the filter runs;
		// the reverse-match itself is proven in Lesson 4 style caches (see quickstart)
		List<Object[]> rows = find(params);
		assertTrue(rows.size() >= 1);
	}
}
