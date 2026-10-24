package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.callback.StreamResultHandler;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffInfo;

/**
 * Streaming queries (fetchStream): rows are consumed one by one through the
 * StreamResultHandler callback instead of materializing the whole result set —
 * the export/ETL pattern for large queries.
 *
 * The handler contract: start() once with the column metadata, consume(row, rowIndex)
 * per row, doNextConsume(row, rowIndex) per row (return false to stop early), end()
 * when the stream is done.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class FetchStreamTest {

	@Autowired
	LightDao lightDao;

	// scoped to the seeded rows (S0001..S0006): other tests (e.g. MultiDataSourceTest's MD01)
	// insert staff into the same shared H2 database, and surefire's default filesystem run
	// order differs between machines (ext4 hash order is random per VM) — an unscoped count
	// makes this test fail whenever such a test happens to run first
	private static final String STAFF_SQL = "select * from SQLTOY_STAFF_INFO t where t.STATUS=1"
			+ " and t.STAFF_ID like 'S000_' order by t.STAFF_ID";

	@Test
	void streamRowsOneByOne() {
		List<StaffInfo> streamed = new ArrayList<>();
		AtomicBoolean ended = new AtomicBoolean(false);
		lightDao.fetchStream(new QueryExecutor(STAFF_SQL).resultType(StaffInfo.class), new StreamResultHandler() {
			@Override
			public void consume(Object row, int rowIndex) {
				streamed.add((StaffInfo) row);
			}

			@Override
			public void end() {
				ended.set(true);
			}
		});
		assertTrue(ended.get(), "end() must be called when the stream completes");
		assertEquals(6, streamed.size());
		streamed.forEach(s -> System.out.println("streamed: " + s.getStaffId() + "\t" + s.getStaffName()));
	}

	@Test
	void doNextConsumeStopsTheStreamEarly() {
		List<StaffInfo> streamed = new ArrayList<>();
		lightDao.fetchStream(new QueryExecutor(STAFF_SQL).resultType(StaffInfo.class), new StreamResultHandler() {
			@Override
			public void consume(Object row, int rowIndex) {
				streamed.add((StaffInfo) row);
			}

			@Override
			public boolean doNextConsume(Object row, int rowIndex) {
				// the point of streaming: stop reading as soon as you have enough
				return streamed.size() < 2;
			}
		});
		System.out.println("early-stopped after " + streamed.size() + " rows");
		assertEquals(2, streamed.size());
	}
}
