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
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.model.ParallelConfig;
import org.sagacity.sqltoy.model.ParallelQuery;
import org.sagacity.sqltoy.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffInfo;
import com.sqltoy.showcase.entity.TransLog;

/**
 * parallelQuery — run independent queries concurrently (one page loading several
 * unrelated blocks). The three forms from the documentation, plus sharding routing:
 *
 *  1. basic: a list of ParallelQuery shares ONE parameter map; sqltoy extracts the
 *     parameters each sql actually needs; results keep the input order
 *  2. ParallelConfig: tune the thread pool (maxThreads / maxWaitSeconds ...)
 *  3. mixed pagination: a query with .page(...) comes back as a Page result,
 *     plain queries as row lists — in the same call
 *
 * Not for use inside transactions (see the parallel_query doc).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class ParallelQueryTest {

	@Autowired
	LightDao lightDao;

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void basicParallelQueriesKeepResultOrder() {
		// two independent queries share one parameter map and execute in parallel
		Map<String, Object> params = new HashMap<>();
		List results = lightDao.parallelQuery(
				Arrays.asList(ParallelQuery.create().sql("sc_tenant_staff").resultType(StaffInfo.class),
						ParallelQuery.create().sql("sc_staff_link")),
				params);
		List<QueryResult> typed = (List<QueryResult>) results;
		System.out.println("parallel query 1 rows: " + typed.get(0).getRows().size());
		System.out.println("parallel query 2 rows: " + typed.get(1).getRows().size());
		assertEquals(2, typed.size());
		assertTrue(typed.get(0).getRows().size() >= 1);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void parallelConfigTunesTheThreadPool() {
		// the 3-argument overload: same queries, explicit thread configuration
		List results = lightDao.parallelQuery(
				Arrays.asList(ParallelQuery.create().sql("sc_tenant_staff").resultType(StaffInfo.class),
						ParallelQuery.create().sql("sc_staff_link"),
						ParallelQuery.create().sql("sc_tenant_staff").resultType(StaffInfo.class)),
				new HashMap<>(), ParallelConfig.create().maxThreads(2));
		List<QueryResult> typed = (List<QueryResult>) results;
		assertEquals(3, typed.size());
		typed.forEach(r -> System.out.println("query rows: " + r.getRows().size() + ", took " + r.getExecuteTime() + "ms"));
		// same sql twice -> same row count
		assertEquals(typed.get(0).getRows().size(), typed.get(2).getRows().size());
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void mixedPagedAndPlainQueriesInOneCall() {
		// setting .page(...) marks that query as paginated; the result carries a Page
		List results = lightDao.parallelQuery(
				Arrays.asList(ParallelQuery.create().sql("sc_tenant_staff").resultType(StaffInfo.class),
						ParallelQuery.create().sql("sc_tenant_staff").resultType(StaffInfo.class)
								.page(new Page(2, 1))),
				new HashMap<>());
		List<QueryResult> typed = (List<QueryResult>) results;
		List plainRows = typed.get(0).getRows();
		Page page = typed.get(1).getPageResult();
		System.out.println("plain rows: " + plainRows.size() + ", paged: rows=" + page.getRows().size()
				+ " recordCount=" + page.getRecordCount() + " pageSize=" + page.getPageSize());
		// the paged query sees the same total, but only one page of rows
		assertEquals(plainRows.size(), page.getRecordCount());
		assertEquals(2, page.getPageSize());
		assertTrue(page.getRows().size() <= 2);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void shardingRoutingWorksInsideParallelQuery() {
		// controlled experiment: save the row FIRST (no ordering dependency on other tests),
		// then run the sharding query inside parallelQuery — if routing works, the row is found
		TransLog log = new TransLog().setTransId("S-PAR").setUserId("U1001").setTransAmt(java.math.BigDecimal.ONE)
				.setTransDate(java.time.LocalDate.now()).setStatus(1);
		lightDao.save(log);

		Map<String, Object> params = MapKit.keys("userId").values("U1001");
		List results = lightDao.parallelQuery(
				Arrays.asList(ParallelQuery.create().sql("sc_trans_by_user").resultType(TransLog.class)), params);
		List rows = ((List<QueryResult>) results).get(0).getRows();
		System.out.println("parallel + sharding-datasource rows: " + rows.size());
		rows.forEach(r -> System.out.println("  " + ((TransLog) r).getTransId()));
		assertTrue(rows.stream().anyMatch(r -> "S-PAR".equals(((TransLog) r).getTransId())));
	}
}
