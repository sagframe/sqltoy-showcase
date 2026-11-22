package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.config.model.Translate;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import com.sqltoy.showcase.entity.StaffInfo;
import com.sqltoy.showcase.entity.TransLog;

/**
 * 6.0.12 new features in the showcase wiring:
 *
 * 1) Programmatic cache translate on lambda queries — same staffIdName cache as
 * MoreQueryFeatureTest, but declared per query on the lambda builder; the key
 * column is appended to the select list automatically, no JOIN, no annotation.
 *
 * 2) Entity-style updateFetch — lock a sharded SQLTOY_TRANS_LOG_15D row by its
 * primary key, validate and mutate the entity in a callback, one round trip;
 * a missing row returns null without insert (updateSaveFetch would insert).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class NewFeatures61Test {

	@Autowired
	LightDao lightDao;

	@Autowired
	TransactionTemplate transactionTemplate;

	// ------------------------------------------------------------------
	// 1) lambda programmatic cache translate (6.0.12)
	// ------------------------------------------------------------------

	@Test
	void lambdaKeyToTargetTranslateResolvesStaffName() {
		// staffId value resolved through the staffIdName cache into staffName
		// (sqltoy appends "STAFF_ID AS STAFF_NAME" to the query columns)
		List<StaffInfo> rows = lightDao.lambdaQuery(StaffInfo.class)
				.translate("staffIdName", StaffInfo::getStaffId, StaffInfo::getStaffName)
				.eq(StaffInfo::getStatus, 1)
				.orderByAsc(StaffInfo::getStaffId)
				.find();
		assertTrue(rows.size() >= 1);
		rows.forEach(r -> assertNotNull(r.getStaffName(),
				"staffName must be translated from cache, got null for " + r.getStaffId()));
	}

	@Test
	void lambdaTranslatesFullModel() {
		// full Translate model with keyColumn+column, same as EntityQuery/QueryExecutor
		List<StaffInfo> rows = lightDao.lambdaQuery(StaffInfo.class)
				.translates(new Translate("staffIdName").setKeyColumn("staffId").setColumn("staffName"))
				.eq(StaffInfo::getStatus, 1)
				.find();
		assertTrue(rows.size() >= 1);
		rows.forEach(r -> assertNotNull(r.getStaffName(), "translated staffName must not be null"));
	}

	// ------------------------------------------------------------------
	// 2) entity-style updateFetch (6.0.12)
	// ------------------------------------------------------------------

	@Test
	void updateFetchEntityLocksMutatesReturnsOnShardedTable() {
		TransLog updated = transactionTemplate.execute(tx -> {
			TransLog probe = new TransLog();
			probe.setTransId("T9001"); // seeded row
			probe.setUserId("U9001");  // sharding key: routes the lock to the right shard
			try {
				return lightDao.updateFetch(probe, (entity, rowIndex) -> {
					BigDecimal amt = entity.getTransAmt();
					// business validation inside the lock: reject negative results
					if (amt == null || amt.compareTo(BigDecimal.valueOf(10)) < 0) {
						throw new IllegalStateException("transAmt underflow");
					}
					entity.setTransAmt(amt.subtract(BigDecimal.TEN));
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertNotNull(updated, "T9001 exists — the updated entity must come back");
		System.out.println("after updateFetch: transId=" + updated.getTransId() + " amt=" + updated.getTransAmt());
	}

	@Test
	void updateFetchEntityMissingRowReturnsNullNoInsert() {
		TransLog probe = new TransLog();
		probe.setTransId("T-NO-SUCH");
		probe.setUserId("U9001"); // sharding key required for routing
		TransLog result = transactionTemplate.execute(tx -> {
			try {
				return lightDao.updateFetch(probe, (entity, rowIndex) -> entity.setStatus(0));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertNull(result, "missing row returns null (no insert — updateSaveFetch inserts, updateFetch does not)");
		Long cnt = lightDao.find("select count(*) from SQLTOY_TRANS_LOG_15D where TRANS_ID=:id",
				java.util.Map.of("id", "T-NO-SUCH"), Long.class).get(0);
		assertEquals(Long.valueOf(0), cnt);
	}
}
