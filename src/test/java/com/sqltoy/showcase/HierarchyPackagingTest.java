package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.dto.DictDetailVO;
import com.sqltoy.showcase.dto.DictTypeVO;

/**
 * Hierarchy packaging with @OneToMany notNullField — the LEFT JOIN edge case that
 * HierarchyTest (inner-join style organ/staff) does not cover.
 *
 * ONE left-join query returns flat rows; sqltoy folds them back into the 1..n object
 * structure. A parent WITHOUT children (EMPTY_TYPE) still produces one join row whose
 * child columns are all null — notNullField="dictKey" on the @OneToMany tells sqltoy to
 * check that property and skip the phantom child instead of packaging an empty stub.
 *
 * hiberarchyFieldsMap resolves the name clashes between parent and child columns
 * (both tables have STATUS): the sql aliases them (dictTypeStatus / dictStatus) and
 * the map tells sqltoy which property each alias belongs to. Setting the fields map
 * auto-enables hierarchy packaging — no separate hiberarchy(true) call needed.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class HierarchyPackagingTest {

	@Autowired
	LightDao lightDao;

	@Test
	void leftJoinWithoutChildrenYieldsNoPhantomChild() {
		// child order in hierarchy packaging follows the SQL's ORDER BY (packaging keeps
		// the row order); @OneToMany orderBy applies to the cascade-LOAD path instead
		String sql = """
				select sdt.DICT_TYPE,sdt.DICT_TYPE_NAME,sdt.COMMENTS dictTypeComments,sdt.STATUS dictTypeStatus,
				       sdd.DICT_KEY,sdd.DICT_NAME,sdd.SHOW_INDEX,sdd.STATUS dictStatus
				from SQLTOY_DICT_TYPE sdt
				left join SQLTOY_DICT_DETAIL sdd on sdt.DICT_TYPE=sdd.DICT_TYPE
				order by sdt.SHOW_INDEX,sdd.SHOW_INDEX desc
				""";
		List<DictTypeVO> result = lightDao
				.findByQuery(new QueryExecutor(sql).resultType(DictTypeVO.class)
						.hiberarchyFieldsMap(DictTypeVO.class,
								MapKit.keys("dictTypeComments", "dictTypeStatus").values("comments", "status"))
						.hiberarchyFieldsMap(DictDetailVO.class, MapKit.keys("dictStatus").values("status")))
				.getRows();

		assertEquals(3, result.size());
		result.forEach(t -> {
			System.out.println(t.getDictType() + " (" + t.getComments() + ", status=" + t.getStatus() + "): "
					+ t.getDictDetailVOs().size() + " details");
			t.getDictDetailVOs().forEach(d -> System.out.println("   " + d.getDictKey() + " -> " + d.getDictName()
					+ " (showIndex=" + d.getShowIndex() + ", status=" + d.getStatus() + ")"));
		});

		// aliased parent columns mapped back onto the properties by hiberarchyFieldsMap
		DictTypeVO sexType = result.get(0);
		assertEquals("SEX_TYPE", sexType.getDictType());
		assertEquals("staff gender dictionary", sexType.getComments());
		assertEquals(Integer.valueOf(1), sexType.getStatus());

		// children packaged in the SQL's order (showIndex desc — see the ORDER BY above)
		assertEquals(2, sexType.getDictDetailVOs().size());
		assertEquals("F", sexType.getDictDetailVOs().get(0).getDictKey());
		assertEquals("M", sexType.getDictDetailVOs().get(1).getDictKey());
		// the child STATUS came through the dictStatus alias
		assertEquals(Integer.valueOf(1), sexType.getDictDetailVOs().get(0).getStatus());

		DictTypeVO deviceType = result.get(1);
		assertEquals(4, deviceType.getDictDetailVOs().size());
		assertEquals("PRINTER", deviceType.getDictDetailVOs().get(0).getDictKey());

		// the notNullField proof: EMPTY_TYPE has no details, so the all-null left-join
		// row must NOT become a phantom child object
		DictTypeVO emptyType = result.get(2);
		assertEquals("EMPTY_TYPE", emptyType.getDictType());
		assertTrue(emptyType.getDictDetailVOs().isEmpty(),
				"notNullField=dictKey must skip the phantom child of a childless parent");
	}
}
