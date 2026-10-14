package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Report transforms beyond Lesson 7 of the quickstart: rows-chain-relative
 * (period-over-period BETWEEN rows) and unpivot (columns to rows).
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class ReportMoreTest {

	@Autowired
	LightDao lightDao;

	@Test
	@SuppressWarnings("rawtypes")
	void rowsChainRelativeComputesDownTheMonths() {
		List rows = lightDao.find("sc_rows_relative", new HashMap<String, Object>());
		System.out.println("rows-chain-relative:");
		rows.forEach(r -> {
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			StringBuilder sb = new StringBuilder("  ");
			for (Object v : a) {
				sb.append(v == null ? "-" : v).append("\t");
			}
			System.out.println(sb);
		});
		// 9 source rows; each product gains a relative column, rows keep count
		assertEquals(9, rows.size());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void unpivotTurnsMeasuresIntoRows() {
		List rows = lightDao.find("sc_unpivot", new HashMap<String, Object>());
		System.out.println("unpivot rows: " + rows.size());
		rows.stream().limit(4).forEach(r -> {
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			System.out.println("  " + a[0] + "\t" + a[1] + "\t" + a[2] + "\t" + a[3]);
		});
		// 9 months x 2 measures = 18 rows
		assertEquals(18, rows.size());
	}
}
