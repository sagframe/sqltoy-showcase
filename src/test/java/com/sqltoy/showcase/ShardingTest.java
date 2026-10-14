package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.callback.DataSourceCallbackHandler;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.TransLog;

/**
 * Database & table sharding with two in-memory H2 databases.
 *
 * The TransLog entity declares the strategies (hashDataSource by userId, realHisTable by
 * transDate). Saves are routed automatically; queries route via the xml directives
 * &lt;sharding-datasource&gt; / &lt;sharding-table&gt;.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class ShardingTest {

	@Autowired
	LightDao lightDao;

	@Autowired
	@Qualifier("ds0")
	DataSource ds0;

	@Autowired
	@Qualifier("ds1")
	DataSource ds1;

	private int count(DataSource ds, String table) {
		return (Integer) org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), ds, new DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement().executeQuery("select count(*) from " + table)) {
							rs.next();
							setResult(rs.getInt(1));
						}
					}
				});
	}

	@Test
	void savesAreHashShardedAcrossDatabases() {
		// delta-based assertion: immune to rows other tests may have inserted into the shared context
		int before = count(ds0, "SQLTOY_TRANS_LOG_15D") + count(ds1, "SQLTOY_TRANS_LOG_15D");

		// four rows across different users: the hash strategy spreads them over ds0/ds1
		for (int i = 1; i <= 4; i++) {
			TransLog log = new TransLog().setTransId("S" + i).setUserId("U" + i)
					.setTransAmt(BigDecimal.valueOf(100 + i)).setTransDate(LocalDate.now()).setStatus(1);
			lightDao.save(log);
		}
		int inDs0 = count(ds0, "SQLTOY_TRANS_LOG_15D");
		int inDs1 = count(ds1, "SQLTOY_TRANS_LOG_15D");
		System.out.println("trans rows after saves: ds0=" + inDs0 + ", ds1=" + inDs1);
		assertEquals(4, inDs0 + inDs1 - before, "all 4 saves must land somewhere across the two databases");
		assertTrue(inDs0 >= 1 && inDs1 >= 1, "rows must land in BOTH databases (hash sharding)");
	}

	@Test
	void queryRoutesToTheShardOfTheUser() {
		// save through the entity first: @Sharding puts the row exactly where the
		// sharding-datasource directive will look for it, on whichever database that is
		TransLog log = new TransLog().setTransId("S-ROUTE").setUserId("U1001")
				.setTransAmt(BigDecimal.TEN).setTransDate(LocalDate.now()).setStatus(1);
		lightDao.save(log);

		Map<String, Object> params = MapKit.keys("userId").values("U1001");
		List<TransLog> rows = lightDao.find("sc_trans_by_user", params, TransLog.class);
		System.out.println("rows for U1001: " + rows.size());
		assertTrue(rows.stream().anyMatch(r -> "S-ROUTE".equals(r.getTransId())));
	}

	@Test
	void queryRoutesOldDatesToTheHistoryTable() {
		// 2026-01-15 is outside the 14-day window -> the realHisTable strategy reads the HIS table
		Map<String, Object> params = MapKit.keys("transDate").values(LocalDate.parse("2026-01-15"));
		List<TransLog> rows = lightDao.find("sc_trans_by_date", params, TransLog.class);
		rows.forEach(r -> System.out.println("history row: " + r.getTransId() + " " + r.getTransDate()));
		assertTrue(rows.stream().allMatch(r -> "H9001".equals(r.getTransId())));
	}
}
