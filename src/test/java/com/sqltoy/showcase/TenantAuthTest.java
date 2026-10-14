package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.StaffInfo;

/**
 * Tenant-style data authorization: the sql references :authedOrganIds, and the
 * UnifyFieldsHandler injects the authorized organizations with forcelimit=true —
 * the caller passes NOTHING, and out-of-scope rows are still filtered server-side.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class TenantAuthTest {

	@Autowired
	LightDao lightDao;

	@Test
	void authorizedOrganizationsAreForceInjected() {
		Map<String, Object> noFilter = new HashMap<>();
		List<StaffInfo> rows = lightDao.find("sc_tenant_staff", noFilter, StaffInfo.class);
		System.out.println("visible staff (authed organs 100005/100007):");
		rows.forEach(s -> System.out.println("  " + s.getStaffId() + "\t" + s.getOrganId() + "\t" + s.getStaffName()));

		// staff in authorized organs (100005/100007) are visible; the 100001 rows never are,
		// even though the caller passed no filter at all (semantically asserted — other tests
		// may have inserted more staff into the shared context)
		assertTrue(rows.size() >= 4);
		assertTrue(rows.stream().noneMatch(s -> "100001".equals(s.getOrganId())));
		assertTrue(rows.stream().allMatch(s -> "100005".equals(s.getOrganId()) || "100007".equals(s.getOrganId())));
	}
}
