/**
 *
 */
package com.sqltoy.quickstart;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson2.JSON;
import com.sqltoy.quickstart.service.OrganInfoService;
import com.sqltoy.quickstart.vo.OrganInfoVO;

/**
 * @project sqltoy-quickstart
 * @description 请在此说明类的功能
 * @author zhongxuchen
 * @version v1.0, Date:2020-7-20
 * @modify 2020-7-20,修改说明
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class TreeTableTest {
	@Autowired
	OrganInfoService organInfoService;

	@Autowired
	LightDao lightDao;

	/**
	 * @TODO 通过保存机构演示节点路径、节点等级、是否叶子节点等数据的生成
	 */
	@Test
	public void testWrapTreeTable() {
		OrganInfoVO organ = new OrganInfoVO();
		organ.setOrganId("100015");
		organ.setOrganCode("100015");
		organ.setOrganPid("100007");
		organ.setOrganName("ERP开发部");
		organ.setStatus(1);
		organInfoService.saveOrganInfo(organ);
	}

	@Test
	public void searchTree() {
		List<OrganInfoVO> organs = lightDao.find("qstart_treeTable_search",
				MapKit.keys("nodeRoute").values("100007"), OrganInfoVO.class);
		for (OrganInfoVO vo : organs) {
			System.err.println(JSON.toJSONString(vo));
		}
	}
}
