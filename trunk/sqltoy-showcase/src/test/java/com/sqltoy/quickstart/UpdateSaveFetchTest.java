/**
 *
 */
package com.sqltoy.quickstart;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson2.JSON;
import com.sqltoy.quickstart.vo.StaffInfoVO;

/**
 * @project sqltoy-quickstart
 * @description updateSaveFetch 演示：锁查询、记录不存在则插入、存在则修改，并返回修改后结果
 *              一次数据库交互完成，适用于秒杀、库存台账、资金台账等强事务高并发场景
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class UpdateSaveFetchTest {
	@Autowired
	private LightDao lightDao;

	/**
	 * 以staffId为唯一性依据：不存在则插入，存在则锁定并修改，返回修改后的最新数据
	 */
	@Test
	public void updateSaveFetch() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S0001");
		staffInfo.setStaffName("测试员工");
		StaffInfoVO result = lightDao.updateSaveFetch(staffInfo, (entity, rowIndex) -> {
			// entity为代理对象：get相当于rs.getXxx()，set提交修改
			entity.setEmail("sqltoy@github.com").setStatus(1).setUpdateTime(LocalDateTime.now());
		});
		System.err.println(JSON.toJSONString(result));
	}

	/**
	 * 指定锁等待时长（秒），防止高并发下长时间等待
	 */
	@Test
	public void updateSaveFetchWithTimeout() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S0001");
		staffInfo.setStaffName("测试员工");
		StaffInfoVO result = lightDao.updateSaveFetch(staffInfo, (entity, rowIndex) -> {
			entity.setEmail("updateSaveFetch@github.com");
		}, 30);
		System.err.println(JSON.toJSONString(result));
	}
}
