package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.callback.DataSourceCallbackHandler;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffInfo;

/**
 * Multiple named datasources: every DataSource bean is registered with sqltoy under its
 * bean name; the @Primary one (ds0) serves calls that name no datasource, and any specific
 * datasource can be addressed explicitly.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class MultiDataSourceTest {

	@Autowired
	LightDao lightDao;

	@Autowired
	@Qualifier("ds0")
	DataSource ds0;

	@Autowired
	@Qualifier("ds1")
	DataSource ds1;

	private int staffCount(DataSource ds) {
		return (Integer) org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), ds, new DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement()
								.executeQuery("select count(*) from SQLTOY_STAFF_INFO")) {
							rs.next();
							setResult(rs.getInt(1));
						}
					}
				});
	}

	@Test
	void defaultCallsGoToThePrimaryDatasource() {
		// staff lives only on ds0; the default (unnamed) datasource is ds0.
		// delta-based: other tests may have inserted staff before this one ran
		int before = staffCount(ds0);
		StaffInfo staff = new StaffInfo().setStaffId("MD01").setStaffCode("MD01").setStaffName("Multi DS")
				.setOrganId("100005").setSexType("M").setStatus(1);
		lightDao.save(staff);
		assertEquals(before + 1, staffCount(ds0));
		assertEquals(0, staffCount(ds1));
		// leave the shared H2 database as it was: the row is this test's scaffolding, and
		// order-sensitive count assertions elsewhere must not see it
		lightDao.deleteByIds(StaffInfo.class, "MD01");
		assertEquals(before, staffCount(ds0));
	}
}
