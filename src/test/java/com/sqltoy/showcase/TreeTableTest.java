package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.TreeTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.TreeNode;

/**
 * Tree tables: save the tree once with wrapTreeTableRoute and NODE_ROUTE / NODE_LEVEL /
 * IS_LEAF are maintained automatically; the &lt;tree-sort&gt; query directive orders the
 * hierarchy and rolls child values up onto the parents.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class TreeTableTest {

	@Autowired
	LightDao lightDao;

	@Test
	void wrapTreeTableRouteMaintainsHierarchyColumns() {
		// real-world usage: when a node is saved, wrapTreeTableRoute maintains THIS node and
		// its children; the parent chain above must already carry its level (the root does,
		// see schema-ds0.sql). The entry node must carry BOTH its id and its pid.
		TreeNode shanghai = new TreeNode().setNodeId("SH").setPid("CN");
		boolean changed = lightDao.wrapTreeTableRoute(new TreeTableModel(shanghai).idField("nodeId").pidField("pid"));
		System.out.println("wrapTreeTableRoute changed rows: " + changed);

		TreeNode sh = lightDao.load(new TreeNode().setNodeId("SH"));
		TreeNode songjiang = lightDao.load(new TreeNode().setNodeId("Songjiang"));
		System.out.println("Shanghai route=" + sh.getNodeRoute() + ", level=" + sh.getNodeLevel());
		System.out.println("Songjiang route=" + songjiang.getNodeRoute() + ", level=" + songjiang.getNodeLevel()
				+ ", isLeaf=" + songjiang.getIsLeaf());
		assertNotNull(sh.getNodeRoute());
		assertEquals(Integer.valueOf(2), sh.getNodeLevel());
		assertEquals(Integer.valueOf(3), songjiang.getNodeLevel());
		assertEquals(Integer.valueOf(1), songjiang.getIsLeaf());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void treeSortOrdersAndRollsUp() {
		List rows = lightDao.find("sc_tree_rollup", new HashMap<String, Object>());
		System.out.println("tree rollup result:");
		rows.forEach(r -> {
			Object[] a = (r instanceof Object[]) ? (Object[]) r : ((List) r).toArray();
			System.out.println("  " + a[2] + "\tvalue=" + a[3]);
		});

		// depth-first order, siblings sorted by rolled-up value descending
		String[] expectOrder = { "China", "Shanghai", "Pudong", "Songjiang", "Jiangsu" };
		// child values roll up onto the parents: Shanghai=300+120+180=600, China=0+600+270=870
		Map<String, Integer> expectValue = new HashMap<>();
		expectValue.put("China", 870);
		expectValue.put("Shanghai", 600);
		expectValue.put("Pudong", 180);
		expectValue.put("Songjiang", 120);
		expectValue.put("Jiangsu", 270);

		assertEquals(5, rows.size());
		for (int i = 0; i < rows.size(); i++) {
			Object[] a = (rows.get(i) instanceof Object[]) ? (Object[]) rows.get(i)
					: ((List) rows.get(i)).toArray();
			assertEquals(expectOrder[i], a[2], "node order at position " + i);
			assertEquals(expectValue.get((String) a[2]).intValue(), ((Number) a[3]).intValue(),
					"rolled-up value of " + a[2]);
		}
	}
}
