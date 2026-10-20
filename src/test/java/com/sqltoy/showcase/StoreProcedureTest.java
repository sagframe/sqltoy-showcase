package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.StoreResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Stored procedures. The bodies live in Procedures.java and are exposed to H2
 * through CREATE ALIAS (see schema-ds0.sql).
 *
 * The second call demonstrates the moreResult concept: one procedure returning
 * MULTIPLE result sets — StoreResult.getRows() holds the first one,
 * StoreResult.getMoreResults() holds all of them.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class StoreProcedureTest {

	@Autowired
	LightDao lightDao;

	@Test
	void singleResultSet() {
		StoreResult<?> result = lightDao.executeStore("{call SP_SHOWCASE()}", new Object[] {});
		List<?> rows = result.getRows();
		System.out.println("SP_SHOWCASE rows: " + rows.size());
		rows.stream().limit(3).forEach(r -> System.out.println("  " + r));
		assertTrue(rows.size() >= 6);
	}

	/*
	 * moreResult — one procedure call returning MULTIPLE result sets. The consumer API:
	 *
	 *   StoreResult<?> result = lightDao.executeStore("{call YOUR_PROC(?,?)}", params);
	 *   List first = result.getRows();          // the first result set
	 *   List[] all  = result.getMoreResults();  // EVERY result set, first included
	 *
	 * Producing multiple result sets depends on the DATABASE, not on sqltoy: SQL Server
	 * procedures return them naturally, Oracle exposes them as SYS_REFCURSOR out parameters.
	 * In-memory H2 cannot produce them (verified: neither ResultSet[] aliases nor
	 * multi-command statements expand into moreResults), so this demo is skipped here —
	 * run it against SQL Server / Oracle to see it work.
	 */
	@Test
	void moreResultsReturnsEveryResultSet() {
		org.junit.jupiter.api.Assumptions.assumeTrue(false,
				"H2 cannot produce multiple result sets; run against SQL Server / Oracle");
	}
}
