package com.sqltoy.dyndatasource.service.impl;

import java.util.List;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.sqltoy.dyndatasource.service.DynamicService;
import com.sqltoy.dyndatasource.service.OrderInfoService;
import com.sqltoy.dyndatasource.vo.OrderInfoVO;

@Service
public class DynamicServiceImpl implements DynamicService {

	@Autowired
	private OrderInfoService orderInfoService;

	@Autowired
	private SqlToyLazyDao sqlToyLazyDao;

	@Override
	@DSTransactional
	public void saveOrder(List<OrderInfoVO> orderInfoVOAs, List<OrderInfoVO> orderInfoVOBs) {
		orderInfoService.saveLocalAOrders(orderInfoVOAs);
		orderInfoService.saveLocalBOrders(orderInfoVOBs);

	}
	
	//primary 则无需每个都指定
	//@DS("datasourceA")
	@Transactional
	public void executeSql(String sql) {
		sqlToyLazyDao.executeSql(sql, MapKit.map());
	}

}
