/**
 * 
 */
package com.sagframe.sqltoy.showcase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.service.SqlToyCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson.JSON;
import com.sagframe.sqltoy.SqlToyApplication;
import com.sagframe.sqltoy.showcase.vo.StaffInfoVO;
import com.sagframe.sqltoy.utils.ShowCaseUtils;

/**
 * @project sqltoy-showcase
 * @description 普通的CRUD操作演示，无需额外写service方法，由sqltoy提供SqlToyCRUDServcie提供默认的操作即可
 * @author chenrenfei <a href="mailto:zhongxuchen@gmail.com">联系作者</a>
 * @version id:CrudCaseServiceTest.java,Revision:v1.0,Date:2019年7月12日
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class CrudCaseServiceTest {
	@Autowired
	private SqlToyCRUDService sqlToyCRUDService;

	// 演示对象操作分库分表,当前策略是采用hash取模方式,保存、修改、加载都会根据取模字段值自动匹配对应数据库
	/**
	 * 创建一条员工记录
	 */
	@Test
	public void saveStaffInfo() {
		List<StaffInfoVO> staffs = new ArrayList<StaffInfoVO>();
		for (int i = 1; i < 10; i++) {
			StaffInfoVO staffInfo = new StaffInfoVO();
			staffInfo.setStaffId("S1907150" + i);
			staffInfo.setStaffCode("S1907150" + i);
			staffInfo.setStaffName("测试员工" + i);
			staffInfo.setSexType("M");
			staffInfo.setEmail("test12@aliyun.com");
			staffInfo.setEntryDate(LocalDateTime.now());
			staffInfo.setStatus(1);
			staffInfo.setOrganId("C0001");
			staffInfo.setPhoto(
					ShowCaseUtils.getBytes(ShowCaseUtils.getFileInputStream("classpath:/mock/staff_photo.jpg")));
			staffInfo.setCountry("86");
			staffs.add(staffInfo);
		}
		// sqlToyCRUDService.saveAll(staffs);
		// saveOrUpdate 也是可以的
		sqlToyCRUDService.saveOrUpdateAll(staffs);
	}

	@Test
	public void loadAll() {
		List<StaffInfoVO> staffs = new ArrayList<StaffInfoVO>();
		for (int i = 1; i < 10; i++) {
			StaffInfoVO staffInfo = new StaffInfoVO();
			staffInfo.setStaffId("S1907150" + i);
			staffs.add(staffInfo);
		}
		List<StaffInfoVO> result = sqlToyCRUDService.loadAll(staffs);
		for (StaffInfoVO staff : result) {
			System.err.println(JSON.toJSONString(staff));
		}

	}
}
