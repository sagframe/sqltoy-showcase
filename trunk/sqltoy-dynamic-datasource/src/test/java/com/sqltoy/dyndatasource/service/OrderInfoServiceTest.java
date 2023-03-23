package com.sqltoy.dyndatasource.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sqltoy.SqlToyApplication;
import com.sqltoy.dyndatasource.vo.OrderInfoVO;

/**
 * 
 * @author zhongxuchen
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class OrderInfoServiceTest {
	@Autowired
	DynamicService dynamicService;

	@Test
	public void testCreateOrder() {
		List<OrderInfoVO> orderInfos = new ArrayList<OrderInfoVO>();
		OrderInfoVO orderInfo = new OrderInfoVO();
		orderInfo.setOrderCnt(new BigDecimal(100d));
		orderInfo.setOrderAmt(new BigDecimal(10000d));

		OrderInfoVO orderInfo1 = new OrderInfoVO();
		orderInfo1.setOrderCnt(new BigDecimal(100d));
		orderInfo1.setOrderAmt(new BigDecimal(10000d));
		orderInfos.add(orderInfo);
		orderInfos.add(orderInfo1);
		
		List<OrderInfoVO> orderInfos1 = new ArrayList<OrderInfoVO>();
		OrderInfoVO orderInfo2 = new OrderInfoVO();
		orderInfo2.setOrderCnt(new BigDecimal(100d));
		orderInfo2.setOrderAmt(new BigDecimal(10000d));

		OrderInfoVO orderInfo3 = new OrderInfoVO();
		//注释掉演示正确场景，打开注释演示失败事务回滚
		//orderInfo3.setOrderId("1111111111111111111111111111111111111111111111111111111111111111");
		orderInfo3.setOrderCnt(new BigDecimal(100d));
		orderInfo3.setOrderAmt(new BigDecimal(10000d));
		orderInfos1.add(orderInfo2);
		orderInfos1.add(orderInfo3);
		dynamicService.saveOrder(orderInfos, orderInfos1);
	}

	
	@Test
	public void testExecuteSql() {
		dynamicService.executeSql("delete from sqltoy_order_info");
	}
}
