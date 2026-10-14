package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.SecureCase;

/**
 * Decrypt-then-mask on read: the columns are stored encrypted (@SecureConfig, see
 * SecureEncryptTest); this query decrypts them with &lt;secure-decrypt&gt; and masks
 * them with &lt;secure-mask&gt; — consumers see neither plaintext nor ciphertext.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class SecureMaskTest {

	@Autowired
	LightDao lightDao;

	@Test
	@SuppressWarnings("rawtypes")
	void encryptedColumnsAreDecryptedAndMaskedOnRead() {
		SecureCase entity = new SecureCase().setStaffId("MASK-01").setStaffName("Carol Wang")
				.setTelNo("13755556666").setAddress("No. 3 Jiefang Ave, Wuhan").setStatus(1);
		lightDao.save(entity);

		List rows = lightDao.find("sc_secure_list", new HashMap<String, Object>());
		boolean found = false;
		for (Object r : rows) {
			// raw rows come back as List (Object[] is also accepted by toArray)
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			System.out.println(a[1] + "\t" + a[2] + "\t" + a[3]);
			if ("MASK-01".equals(a[0])) {
				found = true;
				String tel = String.valueOf(a[2]);
				String address = String.valueOf(a[3]);
				assertTrue(tel.contains("*"), "tel must be masked after decrypt: " + tel);
				assertTrue(address.contains("*"), "address must be masked after decrypt: " + address);
				assertNotEquals("13755556666", tel);
			}
		}
		assertTrue(found, "the saved row must be returned");
	}
}
