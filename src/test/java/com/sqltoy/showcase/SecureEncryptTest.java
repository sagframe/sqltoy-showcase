package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.SecureCase;

/**
 * Field encryption: @SecureConfig on the entity encrypts telNo/address on save
 * (RSA key pair from application.yml) and derives telMask — a masked search index,
 * because ciphertext cannot be searched with LIKE. Loading the entity decrypts
 * the fields back automatically.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class SecureEncryptTest {

	private static final String PLAIN_TEL = "13918799460";
	private static final String PLAIN_ADDRESS = "No. 88 Zhongshan Road, Shanghai";

	@Autowired
	LightDao lightDao;

	@Test
	void fieldsAreStoredEncryptedAndDeriveASearchMask() {
		SecureCase entity = new SecureCase().setStaffId("SEC-01").setStaffName("Alice Chen")
				.setTelNo(PLAIN_TEL).setAddress(PLAIN_ADDRESS).setStatus(1);
		lightDao.save(entity);

		// read the raw row through plain JDBC: telNo/address must be ciphertext, telMask a masked index
		lightDao.getSqlToyContext();
		Object[] raw = (Object[]) org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), lightDao.getDataSource(),
				new org.sagacity.sqltoy.callback.DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement().executeQuery(
								"select TEL_NO,TEL_MASK,ADDRESS from SQLTOY_SECURE_CASE where STAFF_ID='SEC-01'")) {
							rs.next();
							setResult(new Object[] { rs.getString(1), rs.getString(2), rs.getString(3) });
						}
					}
				});
		System.out.println("raw TEL_NO   = " + raw[0]);
		System.out.println("raw TEL_MASK = " + raw[1]);
		System.out.println("raw ADDRESS  = " + String.valueOf(raw[2]).substring(0, 40) + "...");
		assertNotEquals(PLAIN_TEL, raw[0]);
		assertNotEquals(PLAIN_ADDRESS, raw[2]);
		assertTrue(String.valueOf(raw[1]).contains("*"), "mask keeps searchable head/tail: " + raw[1]);

		// loading the entity decrypts automatically
		SecureCase loaded = lightDao.load(new SecureCase().setStaffId("SEC-01"));
		System.out.println("decrypted telNo=" + loaded.getTelNo() + ", address=" + loaded.getAddress());
		assertEquals(PLAIN_TEL, loaded.getTelNo());
		assertEquals(PLAIN_ADDRESS, loaded.getAddress());
	}

	@Test
	void maskIndexMakesCiphertextSearchable() {
		SecureCase entity = new SecureCase().setStaffId("SEC-02").setStaffName("Bob Zhang")
				.setTelNo("13801357999").setAddress("No. 12 Keji Road, Shenzhen").setStatus(1);
		lightDao.save(entity);

		// ciphertext columns cannot be LIKE-searched; the mask column can (prefix on the kept head)
		Map<String, Object> params = MapKit.keys("telHead").values("138%");
		@SuppressWarnings("rawtypes")
		List rows = lightDao.find(
				"select STAFF_ID,STAFF_NAME from SQLTOY_SECURE_CASE where TEL_MASK like :telHead",
				params);
		System.out.println("found by mask prefix: " + rows.size() + " row(s)");
		assertTrue(rows.size() >= 1);
	}
}
