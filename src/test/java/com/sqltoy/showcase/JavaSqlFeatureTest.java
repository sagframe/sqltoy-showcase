package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.config.model.Translate;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.model.ParamsFilter;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.sagacity.sqltoy.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffInfo;

/**
 * The xml-tag capabilities, driven from a PLAIN JAVA sql string via QueryExecutor:
 *
 *  - filters   -> .filters(ParamsFilter...) - the toolbox has Java equivalents
 *                 (eq/blank/toDate/primary/cacheArg/like helpers, ...)
 *  - translate -> .translates(Translate...) - cache translate on code sql
 *  - @fast     -> it is SQL TEXT, so writing it in the java sql works unchanged
 *  - pagination-> findPageByQuery(page, executor), count auto-optimized
 *
 * NOTE: page-optimize (cached count for identical filters) currently has NO
 * QueryExecutor equivalent - it is xml-only. Point the executor at a sqlId that
 * carries the page-optimize tag when you need the cached count.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class JavaSqlFeatureTest {

	@Autowired
	LightDao lightDao;

	// ORGAN_ID is selected twice: once as the key, once as ORGAN_NAME whose values
	// the cache translate replaces with the organization name in memory
	private static final String SQL = """
			select t1.STAFF_ID,t1.STAFF_NAME,t1.ORGAN_ID,t1.ORGAN_ID ORGAN_NAME
			from @fast(select t.STAFF_ID,t.STAFF_NAME,t.ORGAN_ID
			           from SQLTOY_STAFF_INFO t where t.STATUS=1
			           #[and t.ORGAN_ID=:organId] order by t.ENTRY_DATE) t1
			""";

	private QueryExecutor executor(Object organId) {
		return new QueryExecutor(SQL)
				.names("organId").values(organId)
				// ParamsFilter.eq: "-1" (the "all" dropdown value) turns organId into null
				.filters(new ParamsFilter("organId").eq("-1"))
				.translates(new Translate("organIdName").setColumn("ORGAN_NAME"))
				.resultType(StaffInfo.class);
	}

	@Test
	@SuppressWarnings("unchecked")
	void filtersTranslateAndFastFromPlainJavaSql() {
		List<StaffInfo> rows = (List<StaffInfo>) lightDao.findByQuery(executor("-1")).getRows();
		System.out.println("java-sql rows: " + rows.size());
		rows.stream().limit(3).forEach(
				r -> System.out.println("  " + r.getStaffId() + "	" + r.getOrganId() + "	" + r.getOrganName()));
		// order-independent: other tests may insert staff rows
		assertTrue(rows.size() >= 6);
		assertTrue(rows.get(0).getOrganName() != null, "organName must be cache-translated");
	}

	@Test
	void paginationWithAutoCountOptimization() {
		Page page = new Page();
		page.setPageSize(4);
		QueryResult result = lightDao.findPageByQuery(page, executor(null));
		Page<?> pageResult = result.getPageResult();
		System.out.println("java-sql page: recordCount=" + pageResult.getRecordCount()
				+ ", rows=" + pageResult.getRows().size());
		assertTrue(pageResult.getRecordCount() >= 6);
		assertEquals(4, pageResult.getRows().size());
	}
}
