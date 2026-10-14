package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.OrganInfo;
import com.sqltoy.showcase.entity.StaffInfo;

/**
 * Automatic hierarchy packaging: ONE join query returns flat rows, and sqltoy
 * assembles them back into the 1..n object structure — every OrganInfo carries
 * its staffList, no per-row grouping code.
 *
 * hiberarchyFieldsMap resolves NAME CLASHES between parent and child columns:
 * both tables have STATUS, so the staff rows select it as STAFF_STATUS and the
 * map tells sqltoy that the STAFF_STATUS label belongs to StaffInfo.status.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class HierarchyTest {

	@Autowired
	LightDao lightDao;

	@Test
	@SuppressWarnings("unchecked")
	void joinRowsAreAssembledIntoParentChildStructure() {
		// NOTE: the child FK column is ALIASED here and mapped through hiberarchyFieldsMap
		// (STAFF_ORGAN -> staff.organId) — explicit labels work as well as same-named columns
		String sql = """
				select o.ORGAN_ID,o.ORGAN_NAME,o.STATUS,
				       s.STAFF_ID,s.STAFF_NAME, s.ORGAN_ID STAFF_ORGAN, s.STATUS STAFF_STATUS
				from SQLTOY_ORGAN_INFO o
				left join SQLTOY_STAFF_INFO s on o.ORGAN_ID=s.ORGAN_ID and s.STATUS=1
				order by o.SHOW_INDEX,s.STAFF_ID
				""";

		List<OrganInfo> organs = (List<OrganInfo>) lightDao
				.findByQuery(new QueryExecutor(sql)
						.resultType(OrganInfo.class)
						// turn on hierarchy packaging (implied by the fields map, shown for clarity)
						.hiberarchy(true)
						// parent/child column clash: STAFF_* labels belong to StaffInfo's fields
						.hiberarchyFieldsMap(StaffInfo.class,
								MapKit.keys("STAFF_ORGAN", "STAFF_STATUS").values("organId", "status")))
				.getRows();

		System.out.println("organs: " + organs.size());
		for (OrganInfo organ : organs) {
			System.out.println("  " + organ.getOrganId() + " " + organ.getOrganName() + " (status="
					+ organ.getStatus() + ") -> " + organ.getStaffList().size() + " staff");
			organ.getStaffList()
					.forEach(s -> System.out.println("      " + s.getStaffId() + "\t" + s.getStaffName()
							+ "\tstatus=" + s.getStatus()));
		}

		assertEquals(3, organs.size());
		// order-independent membership: the seeded staff hang under the right organs
		// (other tests may insert extra staff, so exact per-organ counts are avoided)
		OrganInfo rd = organs.stream().filter(o -> "100005".equals(o.getOrganId())).findFirst().orElseThrow();
		assertTrue(rd.getStaffList().stream().anyMatch(s2 -> "Alice Chen".equals(s2.getStaffName())));
		assertTrue(rd.getStaffList().stream().anyMatch(s2 -> "Bob Zhang".equals(s2.getStaffName())));
		for (OrganInfo organ : organs) {
			assertTrue(organ.getStaffList().size() >= 2, organ.getOrganName());
		}
		// the clashed column stays on the right object: organ status 1, staff status 1
		assertEquals(Integer.valueOf(1), organs.get(0).getStatus());
		assertEquals(Integer.valueOf(1), organs.get(0).getStaffList().get(0).getStatus());
	}
}
