/**
 *
 */
package com.sqltoy.quickstart;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson2.JSON;
import com.sqltoy.quickstart.vo.DictDetailVO;
import com.sqltoy.quickstart.vo.DictTypeVO;

/**
 * @project sqltoy-quickstart
 * @description 查询结果自动对象分组分层封装演示：
 *              主子表(字典类型+字典明细)通过一条join sql查询，
 *              框架按VO上的@OneToMany注解自动将平面结果封装成父子对象(1..n层级)
 *
 *              AbstractDictTypeVO 的 @OneToMany 已配置 notNullField = "dictKey"，
 *              用于辨别join出的记录子表是否有数据
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class HierarchyPackagingTest {
	@Autowired
	private LightDao lightDao;

	@Test
	public void testHierarchyPackaging() {
		// 一条join sql同时查出字典类型 + 字典明细
		String sql = "select sdt.*, sdd.* from sqltoy_dict_type sdt "
				+ " left join sqltoy_dict_detail sdd on sdt.DICT_TYPE=sdd.DICT_TYPE";
		List<DictTypeVO> result = lightDao.findByQuery(new QueryExecutor(sql)
				.resultType(DictTypeVO.class)
				// 开启层次封装（若已设置 hiberarchyFieldsMap 则无需再设置）
				.hiberarchy(true)
				// 父子对象存在同名属性(如都有status)时，将结果集label与对象属性对应：
				// key 为查询结果的label(别名)，value 为实际列/属性名
				.hiberarchyFieldsMap(DictTypeVO.class,
						MapKit.keys("dictTypeComments", "dictTypeStatus").values("COMMENTS", "STATUS"))
				.hiberarchyFieldsMap(DictDetailVO.class,
						MapKit.keys("dictStatus").values("STATUS")))
				.getRows();
		// 输出即为1..n层级结构：每个字典类型下挂dictDetailVOs列表
		for (DictTypeVO dictType : result) {
			System.err.println(JSON.toJSONString(dictType));
		}
	}
}
