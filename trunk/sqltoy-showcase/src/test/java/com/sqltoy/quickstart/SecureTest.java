/**
 *
 */
package com.sqltoy.quickstart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.EntityQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson2.JSON;
import com.sqltoy.quickstart.vo.StaffInfoVO;

/**
 * @project sqltoy-quickstart
 * @description 字段加密存储与查询自动解密演示
 *              1、telNo 在 AbstractStaffInfoVO 上标注了 @Secure(field = "telNo")，
 *              保存时 tel_no 在数据库中为密文
 *              2、查询时框架自动解密，对象中拿到的是明文
 *              3、密钥配置在 spring-sqltoy.xml 的 securePrivateKey/securePublicKey
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class SecureTest {
	@Autowired
	private LightDao lightDao;

	@Test
	public void saveSecureStaff() {
		StaffInfoVO staffInfo = new StaffInfoVO();
		staffInfo.setStaffId("S190715005");
		staffInfo.setStaffCode("S190715005");
		staffInfo.setStaffName("加密测试员工");
		staffInfo.setSexType("M");
		staffInfo.setEmail("secure@aliyun.com");
		// 该字段在数据库中为密文存储
		staffInfo.setTelNo("13927672376");
		staffInfo.setCountry("86");
		staffInfo.setStatus(1);
		staffInfo.setOrganId("C0001");
		lightDao.save(staffInfo);
	}

	@Test
	public void loadSecureStaff() {
		// 查询时自动解密：telNo 返回明文
		StaffInfoVO staff = lightDao.load(new StaffInfoVO("S190715005"));
		System.err.println(JSON.toJSONString(staff));
	}

	// 清理测试数据
	@Test
	public void deleteSecureData() {
		lightDao.deleteByQuery(StaffInfoVO.class, EntityQuery.create().where("staffId=?").values("S190715005"));
	}
}
