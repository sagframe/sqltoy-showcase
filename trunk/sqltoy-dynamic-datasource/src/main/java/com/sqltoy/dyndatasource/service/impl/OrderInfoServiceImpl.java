package com.sqltoy.dyndatasource.service.impl;

import java.util.List;

import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.sqltoy.dyndatasource.service.OrderInfoService;
import com.sqltoy.dyndatasource.vo.OrderInfoVO;

@Service
public class OrderInfoServiceImpl implements OrderInfoService {
	@Autowired
	private SqlToyLazyDao SqlToyLazyDao;

	@Override
	@DS("datasourceA")
	public long saveLocalAOrders(List<OrderInfoVO> orderInfoVOs) {
		return SqlToyLazyDao.saveAll(orderInfoVOs);
	}

	@Override
	@DS("datasourceB")
	public long saveLocalBOrders(List<OrderInfoVO> orderInfoVOs) {
		return SqlToyLazyDao.saveAll(orderInfoVOs);
	}
	
	

}
