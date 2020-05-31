/**
 * 
 */
package com.sagframe.sqltoy.showcase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sagframe.sqltoy.SqlToyApplication;

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

	@Test
	public void testFindByJSON() throws Exception {
	}

	@Test
	public void testFindBySql() throws Exception {
	}

	@Test
	public void testFindPageByJSON() throws Exception {
	}

	@Test
	public void testAgg() throws Exception {
	}
}
