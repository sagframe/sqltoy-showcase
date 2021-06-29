/**
 * 
 */
package com.sagframe.sqltoy.showcase;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson.JSON;
import com.sagframe.sqltoy.SqlToyApplication;
import com.sagframe.sqltoy.showcase.vo.CompanyInfoVO;

/**
 * 
 * @author zhongxuchen
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class ElasticTest {
	@Autowired
	private SqlToyLazyDao sqlToyLazyDao;

	// @Autowired
	// private RestHighLevelClient highLevelClient;

	@Test
	public void mockData() {
		// highLevelClient.
	}

	/**
	 * 演示普通的查询
	 */
	@Test
	public void testSqlSearch() {
		// elasticsearch-sql https://github.com/NLPchina/elasticsearch-sql
		String sql = "es_find_company";
		List<CompanyInfoVO> result = (List<CompanyInfoVO>) sqlToyLazyDao.elastic().sql(sql)
				.resultType(CompanyInfoVO.class).find();
		for (CompanyInfoVO company : result) {
			System.err.println(JSON.toJSONString(company));
		}
	}

	/**
	 * 演示分页查询，基于sql分页请使用elasticsearch-sql插件
	 */
	@Test
	public void testSqlFindPage() {
		// elasticsearch-sql https://github.com/NLPchina/elasticsearch-sql
		String sql = "es_find_company_page";
		Page pageModel = new Page();
		Page result = (Page) sqlToyLazyDao.elastic().sql(sql).resultType(CompanyInfoVO.class)
				.findPage(pageModel);
		System.err.println("resultCount=" + result.getRecordCount());
		for (CompanyInfoVO company : (List<CompanyInfoVO>) result.getRows()) {
			System.err.println(JSON.toJSONString(company));
		}
	}

	@Test
	public void testJsonSearch() {
		String sql = "sys_elastic_test_json";
		String[] paramNames = { "companyTypes" };
		Object[] paramValues = { new Object[] { "1", "2" } };

		List<CompanyInfoVO> result = (List<CompanyInfoVO>) sqlToyLazyDao.elastic().sql(sql).names(paramNames)
				.values(paramValues).resultType(CompanyInfoVO.class).find();
		for (CompanyInfoVO company : result) {
			System.err.println(JSON.toJSONString(company));
		}
	}

	@Test
	public void testJsonFindPage() {
		String sql = "sys_elastic_test_json";
		String[] paramNames = { "companyTypes" };
		Object[] paramValues = { new Object[] { "1", "2" } };
		Page pageModel = new Page();
		Page result = (Page) sqlToyLazyDao.elastic().sql(sql).names(paramNames)
				.values(paramValues).resultType(CompanyInfoVO.class).findPage(pageModel);
		System.err.println("resultCount=" + result.getRecordCount());
		for (CompanyInfoVO company : (List<CompanyInfoVO>) result.getRows()) {
			System.err.println(JSON.toJSONString(company));
		}
	}

	@Test
	public void testAgg() throws Exception {
	}
}
