/**
 * 
 */
package com.sagframe.sqltoy.showcase.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @project sagacity-core
 * @description 交易流水对象
 * @author chenrenfei <a href="mailto:作者联系方式">联系作者</a>
 * @version id:PospTransDetailVO.java,Revision:v1.0,Date:2016年8月9日
 */
@Document(collection = "fact_trans_details")
public class PospTransDetailVO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 交易流水号
	 */
	@Id
	private String transId;

	// @Field可以缺省
	/**
	 * 系统参考号
	 */
	private String srefNo;

	/**
	 * 商户号
	 */
	@Field(name = "merchantCode")
	private String merchantCode;

	/**
	 * 物理终端号
	 */
	private String terminalCode;

	/**
	 * 交易代码
	 */
	@Indexed
	private String transCode;

	/**
	 * 交易日期
	 */
	@Indexed
	private LocalDate transDate;

	/**
	 * 交易时间
	 */
	private LocalDateTime transTime;

	/**
	 * 签约机构
	 */
	private String agentOrg;

	/**
	 * 交易类型
	 */
	@Indexed
	private String transType;

	/**
	 * 交易金额
	 */
	private Double transAmt;

	/**
	 * 支付金额
	 */
	private Double payAmt;

	/**
	 * 银联实收成本
	 */
	private Double cupsFeeAmt;

	/**
	 * 支付卡号
	 */
	private String payCardNo;

	/**
	 * 交易成本
	 */
	private Double feeAmt;

	@Transient
	private String transTypeName;

	@Transient
	private String transCodeName;

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getSrefNo() {
		return srefNo;
	}

	public void setSrefNo(String srefNo) {
		this.srefNo = srefNo;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	public LocalDate getTransDate() {
		return transDate;
	}

	public void setTransDate(LocalDate transDate) {
		this.transDate = transDate;
	}

	public LocalDateTime getTransTime() {
		return transTime;
	}

	public void setTransTime(LocalDateTime transTime) {
		this.transTime = transTime;
	}

	public String getAgentOrg() {
		return agentOrg;
	}

	public void setAgentOrg(String agentOrg) {
		this.agentOrg = agentOrg;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public Double getPayAmt() {
		return payAmt;
	}

	public void setPayAmt(Double payAmt) {
		this.payAmt = payAmt;
	}

	public Double getCupsFeeAmt() {
		return cupsFeeAmt;
	}

	public void setCupsFeeAmt(Double cupsFeeAmt) {
		this.cupsFeeAmt = cupsFeeAmt;
	}

	public Double getFeeAmt() {
		return feeAmt;
	}

	public void setFeeAmt(Double feeAmt) {
		this.feeAmt = feeAmt;
	}

	public String getPayCardNo() {
		return payCardNo;
	}

	public void setPayCardNo(String payCardNo) {
		this.payCardNo = payCardNo;
	}

	/**
	 * @return the transTypeName
	 */
	public String getTransTypeName() {
		return transTypeName;
	}

	/**
	 * @param transTypeName the transTypeName to set
	 */
	public void setTransTypeName(String transTypeName) {
		this.transTypeName = transTypeName;
	}

	/**
	 * @return the transCodeName
	 */
	public String getTransCodeName() {
		return transCodeName;
	}

	/**
	 * @param transCodeName the transCodeName to set
	 */
	public void setTransCodeName(String transCodeName) {
		this.transCodeName = transCodeName;
	}

}
