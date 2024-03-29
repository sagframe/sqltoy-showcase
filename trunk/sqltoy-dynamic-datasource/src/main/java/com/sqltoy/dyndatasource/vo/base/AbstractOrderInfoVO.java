/**
 *@Generated by sagacity-quickvo 4.18
 */
package com.sqltoy.dyndatasource.vo.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;

/**
 * @project sqltoy-dyndatasource
 * @version 1.0.0
 * Table: sqltoy_order_info,Remark:支付订单表  
 */
@Entity(tableName="sqltoy_order_info",pk_constraint="PRIMARY")
public abstract class AbstractOrderInfoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8251563955692306139L;
	
	/**
	 * jdbcType:VARCHAR
	 * 订单ID
	 */
	@Id(strategy="generator",generator="org.sagacity.sqltoy.plugins.id.impl.DefaultIdGenerator")
	@Column(name="ORDER_ID",length=32L,type=java.sql.Types.VARCHAR,nullable=false)
	protected String orderId;
	
	/**
	 * jdbcType:DECIMAL
	 * 订单金额
	 */
	@Column(name="ORDER_AMT",length=12L,type=java.sql.Types.DECIMAL,nullable=false)
	protected BigDecimal orderAmt;
	
	/**
	 * jdbcType:DECIMAL
	 * 订单数量
	 */
	@Column(name="ORDER_CNT",length=8L,type=java.sql.Types.DECIMAL,nullable=false)
	protected BigDecimal orderCnt;
	
	/**
	 * jdbcType:VARCHAR
	 * 订单备注
	 */
	@Column(name="COMMENTS",length=500L,type=java.sql.Types.VARCHAR,nullable=true)
	protected String comments;
	
	/**
	 * jdbcType:DATETIME
	 * 创建时间
	 */
	@Column(name="CREATE_TIME",length=19L,type=java.sql.Types.DATE,nullable=false)
	protected LocalDateTime createTime;
	
	/**
	 * jdbcType:DATETIME
	 * 修改时间
	 */
	@Column(name="UPDATE_TIME",length=19L,type=java.sql.Types.DATE,nullable=false)
	protected LocalDateTime updateTime;
	
	/** default constructor */
	public AbstractOrderInfoVO() {
	}
	
	/** pk constructor */
	public AbstractOrderInfoVO(String orderId)
	{
		this.orderId=orderId;
	}
	
	/**
	 *@param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId=orderId;
	}
		
	/**
	 *@return the OrderId
	 */
	public String getOrderId() {
	    return this.orderId;
	}
	
	/**
	 *@param orderAmt the orderAmt to set
	 */
	public void setOrderAmt(BigDecimal orderAmt) {
		this.orderAmt=orderAmt;
	}
		
	/**
	 *@return the OrderAmt
	 */
	public BigDecimal getOrderAmt() {
	    return this.orderAmt;
	}
	
	/**
	 *@param orderCnt the orderCnt to set
	 */
	public void setOrderCnt(BigDecimal orderCnt) {
		this.orderCnt=orderCnt;
	}
		
	/**
	 *@return the OrderCnt
	 */
	public BigDecimal getOrderCnt() {
	    return this.orderCnt;
	}
	
	/**
	 *@param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments=comments;
	}
		
	/**
	 *@return the Comments
	 */
	public String getComments() {
	    return this.comments;
	}
	
	/**
	 *@param createTime the createTime to set
	 */
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime=createTime;
	}
		
	/**
	 *@return the CreateTime
	 */
	public LocalDateTime getCreateTime() {
	    return this.createTime;
	}
	
	/**
	 *@param updateTime the updateTime to set
	 */
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime=updateTime;
	}
		
	/**
	 *@return the UpdateTime
	 */
	public LocalDateTime getUpdateTime() {
	    return this.updateTime;
	}


	/**
     * @todo vo columns to String
     */
    @Override
	public String toString() {
		StringBuilder columnsBuffer=new StringBuilder();
		columnsBuffer.append("orderId=").append(getOrderId()).append("\n");
		columnsBuffer.append("orderAmt=").append(getOrderAmt()).append("\n");
		columnsBuffer.append("orderCnt=").append(getOrderCnt()).append("\n");
		columnsBuffer.append("comments=").append(getComments()).append("\n");
		columnsBuffer.append("createTime=").append(getCreateTime()).append("\n");
		columnsBuffer.append("updateTime=").append(getUpdateTime()).append("\n");
		return columnsBuffer.toString();
	}
	
}
