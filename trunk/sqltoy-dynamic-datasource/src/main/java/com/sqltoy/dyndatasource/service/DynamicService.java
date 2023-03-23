package com.sqltoy.dyndatasource.service;

import java.util.List;

import com.sqltoy.dyndatasource.vo.OrderInfoVO;

public interface DynamicService {
	public void saveOrder(List<OrderInfoVO> orderInfoVOAs,List<OrderInfoVO> orderInfoVOBs);
	
	public void executeSql(String sql);
}
