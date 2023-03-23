/**
 * 
 */
package com.sqltoy.dyndatasource.service;

import java.util.List;

import com.sqltoy.dyndatasource.vo.OrderInfoVO;

/**
 * @author zhong
 *
 */
public interface OrderInfoService {
	public long saveLocalAOrders(List<OrderInfoVO> orderInfoVOs);

	public long saveLocalBOrders(List<OrderInfoVO> orderInfoVOs);

	//public long saveABOrders(List<OrderInfoVO> orderInfoVOAs, List<OrderInfoVO> orderInfoVOBs);
}
