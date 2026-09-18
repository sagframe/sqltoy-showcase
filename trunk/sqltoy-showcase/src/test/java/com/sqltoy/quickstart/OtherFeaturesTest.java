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
import com.sqltoy.quickstart.vo.StaffInfoVO;

/**
 * @project sqltoy-quickstart
 * @description 其他特性演示：慢SQL收集、TableApi 元数据、对象转换、跨库验证说明
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class OtherFeaturesTest {
	@Autowired
	private LightDao lightDao;

	/**
	 * 1、慢SQL收集：超过 printSqlTimeoutMillis 阈值的sql进入收集器，
	 *    getSlowestSql 获取最慢的sql清单（第二个参数表示是否重置统计）
	 */
	@Test
	public void slowestSql() {
		System.err.println("最慢sql清单:"
				+ lightDao.getSqlToyContext().getSlowestSql(10, true));
	}

	/**
	 * 2、TableApi：获取表/列元数据，便于动态表单、代码生成等场景
	 */
	@Test
	public void tableApi() {
		System.err.println("相关表:"
				+ JSON.toJSONString(lightDao.tableApi().getTables(null, null, "sqltoy%")));
		System.err.println("列信息:"
				+ JSON.toJSONString(lightDao.tableApi().getTableColumns(null, null, "sqltoy_staff_info")));
	}

	/**
	 * 3、对象转换：convertType 快速完成 DTO 与 POJO 互转（支持 @SqlToyFieldAlias 别名字段）
	 */
	@Test
	public void convertType() {
		StaffInfoVO staff = new StaffInfoVO();
		staff.setStaffId("S0001").setStaffName("测试员工");
		StaffInfoVO copy = lightDao.convertType(staff, StaffInfoVO.class);
		System.err.println("copy 的 staffId=" + copy.getStaffId() + ", staffName=" + copy.getStaffName());
	}

	/**
	 * 4、跨数据库适配验证：配置 spring.sqltoy.redoDataSources[0]=其他库 后，
	 *    任意查询会同时在其他库重放执行，用于验证产品化多库适配
	 *    （在 application.properties 中配置即可开启）
	 */
	@Test
	public void multiDbVerifyNote() {
		// spring.sqltoy.redoDataSources[0]=pgdb
		// 开启后 lightDao.find(...) 会同时在 pgdb 上重放执行
		System.err.println("参见 application.properties 中 redoDataSources 的注释配置");
	}
	/**
	 * 5、树形排序：按上下级关系排序（可在 xml 中叠加 sum-columns 逐层汇总）
	 */
	@Test
	public void treeSortDemo() {
		List result = lightDao.find("qstart_tree_sort_demo", MapKit.map());
		result.forEach((r) -> System.err.println(JSON.toJSONString(r)));
	}

	/**
	 * 6、@include(sqlId)：将公共条件片段拼接进当前 sql
	 */
	@Test
	public void includeDemo() {
		List<StaffInfoVO> list = lightDao.find("qstart_include_case",
				MapKit.keys("status").values(1), StaffInfoVO.class);
		System.err.println("include 查询结果:" + list.size());
	}

}
