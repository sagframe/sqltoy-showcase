/**
 * 
 */
package com.sagframe.sqltoy.showcase;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
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

	@Autowired
	private SqlToyLazyDao sqlToyLazyDao;

	/**
	 * 创建一条员工记录
	 */
	@Test
	public void saveStaffInfo() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S190715004");
		staffInfo.setStaffCode("S190715004");
		staffInfo.setStaffName("测试员工4");
		staffInfo.setSexType("M");
		staffInfo.setEmail("test3@aliyun.com");
		staffInfo.setEntryDate(LocalDate.now());
		staffInfo.setStatus(1);
		staffInfo.setOrganId("C0001");
		staffInfo.setPhoto(ShowCaseUtils.getBytes(ShowCaseUtils.getFileInputStream("classpath:/mock/staff_photo.jpg")));
		staffInfo.setCountry("86");
		sqlToyCRUDService.save(staffInfo);
	}

	/**
	 * 修改员工记录信息
	 */
	// 演示sqltoy修改数据的策略
	// sqltoy 默认的update
	// 操作并不需要先将记录查询出来(普通hibernate则需要先取数据，然后对需要修改的地方进行重新设置值，确保其他字段值不会被覆盖)。
	// sqltoy 利用各种数据库自身特性,未null的字段会被忽略掉不参与更新操作(如果需要强制更新参见下一个范例:forceUpdate)
	@Test
	public void updateStaffInfo() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S190715001");
		// 只修改所在机构,其他字段不会被影响(避免先查询后修改提升了效率,同时避免高并发场景下先查询再修改数据冲突)
		staffInfo.setOrganId("C0002");
		sqlToyCRUDService.update(staffInfo);
	}

	/**
	 * 对员工信息特定字段进行强制修改
	 */
	@Test
	public void forceUpdate() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S190715001");
		// 只修改所在机构,其他字段不会被影响(避免先查询后修改提升了效率,同时避免高并发场景下先查询再修改数据冲突)
		staffInfo.setOrganId("C0002");
		staffInfo.setAddress("测试地址");

		// 第二个数组参数设置需要强制修改的字端,如果该字段的值为null，数据库中的值将被null覆盖
		sqlToyCRUDService.update(staffInfo, new String[] { "address" });
	}

	@Test
	public void saveOrUpdate() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S190715004");
		staffInfo.setStaffCode("S190715004");
		staffInfo.setStaffName("测试员工5");
		staffInfo.setSexType("M");
		staffInfo.setEmail("test3@aliyun.com");
		staffInfo.setEntryDate(LocalDate.now());
		staffInfo.setStatus(1);
		staffInfo.setOrganId("C0001");
		staffInfo.setPhoto(ShowCaseUtils.getBytes(ShowCaseUtils.getFileInputStream("classpath:/mock/staff_photo.jpg")));
		staffInfo.setCountry("86");
		sqlToyCRUDService.saveOrUpdate(staffInfo);
	}

	@Test
	public void load() {
		StaffInfoVO staff = sqlToyCRUDService.load(new StaffInfoVO("S190715003"));
		System.err.println(JSON.toJSONString(staff));
	}

	@Test
	public void search() {
		List<StaffInfoVO> staffVOs = sqlToyLazyDao.findBySql("sqltoy_showcase", new String[] { "status" },
				new Object[] { 1 }, StaffInfoVO.class);
		System.err.println(JSON.toJSONString(staffVOs));
	}

}
