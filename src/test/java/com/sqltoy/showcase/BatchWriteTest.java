package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.EntityQuery;
import org.sagacity.sqltoy.model.ParallelConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.SecureCase;

/**
 * Batch and parallel writes: saveAll inserts a list in one interaction group;
 * the fluent save().parallelConfig(...).many(...) splits the batch across threads.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class BatchWriteTest {

	@Autowired
	LightDao lightDao;

	private List<SecureCase> batch(int from, int size) {
		List<SecureCase> list = new ArrayList<SecureCase>();
		for (int i = 0; i < size; i++) {
			SecureCase c = new SecureCase().setStaffId("BATCH-" + from + "-" + i)
					.setStaffName("Batch " + from + "-" + i)
					.setTelNo("1390000" + String.format("%04d", from * 100 + i))
					.setAddress("No. " + (from * 100 + i) + " Road").setStatus(1);
			list.add(c);
		}
		return list;
	}

	private int count(int from) {
		return lightDao.findEntity(SecureCase.class,
				EntityQuery.create().where("STAFF_ID like ?").values("BATCH-" + from + "-%")).size();
	}

	@Test
	void saveAllInsertsTheWholeList() {
		lightDao.saveAll(batch(1, 20));
		assertEquals(20, count(1));
		System.out.println("saveAll: 20 rows inserted");
	}

	@Test
	void parallelSaveSplitsTheBatchAcrossThreads() {
		// delta-based: whatever ran before, this parallel batch adds exactly 60 rows
		int before = count(2);
		lightDao.save().parallelConfig(ParallelConfig.create().groupSize(500).maxThreads(10)).many(batch(2, 60));
		assertEquals(60, count(2) - before);
		System.out.println("parallel save: 60 rows across up to 10 threads");
	}
}
