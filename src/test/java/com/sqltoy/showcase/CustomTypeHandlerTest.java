package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffExt;
import com.sqltoy.showcase.plugins.Money;

/**
 * Custom TypeHandler — for SPECIAL cases only.
 *
 * Note that JSON / VECTOR / GEOMETRY columns and enum conversion are all handled
 * by sqltoy OUT OF THE BOX (see JsonGeometryTest) and need no custom handler.
 * A custom TypeHandler is reserved for genuinely special value objects — like the
 * {@link Money} amount+currency type here, mapped onto a varchar column
 * ("USD:150000.00") in both directions via spring.sqltoy.typeHandler.
 * Unrecognized types fall through untouched.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class CustomTypeHandlerTest {

	@Autowired
	LightDao lightDao;

	@Test
	void moneyValueObjectRoundTripsThroughAStringColumn() throws Exception {
		Money budget = new Money("USD", new java.math.BigDecimal("150000.00"));
		StaffExt ext = new StaffExt().setStaffId("TH-01")
				.setBudget(budget).setStatus(1);
		lightDao.save(ext);

		// raw storage is the compact string form
		Object raw = org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), lightDao.getDataSource(),
				new org.sagacity.sqltoy.callback.DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement().executeQuery(
								"select BUDGET from SQLTOY_STAFF_EXT where STAFF_ID='TH-01'")) {
							rs.next();
							setResult(rs.getString(1));
						}
					}
				});
		System.out.println("raw BUDGET column: " + raw);
		assertEquals("USD:150000.00", raw);

		// load converts the string back into the Money value object
		StaffExt loaded = lightDao.load(new StaffExt().setStaffId("TH-01"));
		System.out.println("loaded budget: " + loaded.getBudget());
		assertEquals(budget, loaded.getBudget());

		// update path goes through the handler as well
		loaded.setBudget(new Money("EUR", new java.math.BigDecimal("120000.00")));
		lightDao.update(loaded);
		StaffExt reloaded = lightDao.load(new StaffExt().setStaffId("TH-01"));
		System.out.println("updated budget: " + reloaded.getBudget());
		assertEquals(new Money("EUR", new java.math.BigDecimal("120000.00")), reloaded.getBudget());
		assertNotEquals(budget, reloaded.getBudget());
	}

	@Test
	void jsonAndEnumHandlingStillWorkAlongsideTheCustomHandler() {
		// same entity, same table: JSON columns and enum conversion untouched
		StaffExt ext = new StaffExt().setStaffId("TH-02").setSkills(List.of("Java"))
				.setProfile(java.util.Map.of("level", "senior"))
				.setLevel(com.sqltoy.showcase.entity.StaffLevel.EXPERT)
				.setState(com.sqltoy.showcase.entity.WorkState.ACTIVE)
				.setBudget(new Money("CNY", new java.math.BigDecimal("88000.00"))).setStatus(1);
		lightDao.save(ext);

		StaffExt loaded = lightDao.load(new StaffExt().setStaffId("TH-02"));
		assertEquals(1, loaded.getSkills().size());
		assertEquals("senior", loaded.getProfile().get("level"));
		assertEquals(com.sqltoy.showcase.entity.StaffLevel.EXPERT, loaded.getLevel());
		assertEquals(com.sqltoy.showcase.entity.WorkState.ACTIVE, loaded.getState());
		assertEquals(new Money("CNY", new java.math.BigDecimal("88000.00")), loaded.getBudget());
		System.out.println("TH-02: skills=" + loaded.getSkills() + ", level=" + loaded.getLevel()
				+ ", state=" + loaded.getState() + ", budget=" + loaded.getBudget());
	}
}
