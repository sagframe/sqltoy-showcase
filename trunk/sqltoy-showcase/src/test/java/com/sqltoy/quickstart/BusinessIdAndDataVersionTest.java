/**
 *
 */
package com.sqltoy.quickstart;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import com.sqltoy.quickstart.vo.StaffInfoVO;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @project sqltoy-quickstart
 * @description redis 规则业务主键与数据版本控制的参考实现
 *              标记 @Disabled：业务主键依赖 redis，数据版本控制需要表中存在版本号列
 * @author zhongxuchen
 */
@Disabled("依赖 redis 与特定表结构，作为参考实现保留")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class BusinessIdAndDataVersionTest {
	@Autowired
	private LightDao lightDao;

	/**
	 * 1、generateBizId 生成业务主键：当一个表里面涉及多个业务主键时
	 *    （配置层面只支持单个），通过此方法自行生成后赋值
	 *    signature 支持 @case(name,v1,then1,v2,then2)、@day(yyMMdd)、@substr(name,start,length) 等宏
	 */
	@Test
	public void generateBizId() {
		// 简单形态：唯一标识符号 + 增量
		long id = lightDao.generateBizId("ORDER", 1);
		System.err.println("业务ID:" + id);
		// 动态规则形态：按表维度隔离管理，signature 用宏组织规则
		String bizId = lightDao.generateBizId("sqltoy_order_info",
				"HW@case(orderType,SALE,SC,BUY,PO)@day(yyMMdd)",
				MapKit.map("orderType", "SALE"), null, 20, 5);
		System.err.println("业务单据号:" + bizId);
	}

	/**
	 * 2、@DataVersion 数据版本控制：在 POJO 类上标注 @DataVersion(field = "版本号属性")，
	 *    修改时页面携带原有版本号一起提交，框架校验版本一致则更新并递增，不一致抛出异常
	 */
	@Test
	public void dataVersion() {
		StaffInfoVO staff = new StaffInfoVO();
		staff.setStaffId("S0001");
		staff.setStatus(2);
		Long rows = lightDao.update(staff);
		System.err.println("更新记录:" + rows);
	}
}
