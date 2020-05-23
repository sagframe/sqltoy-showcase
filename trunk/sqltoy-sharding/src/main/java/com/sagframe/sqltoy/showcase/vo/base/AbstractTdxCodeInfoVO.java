/**
 *@Generated by sagacity-quickvo 4.11
 */
package com.sagframe.sqltoy.showcase.vo.base;

import java.io.Serializable;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.config.annotation.Column;
import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * @project sqltoy-oracle
 * @version 1.0.0
 * Table: sqltoy_tdx_code_info,Remark:代码基本信息表  
 */
@Entity(tableName="sqltoy_tdx_code_info",pk_constraint="PRIMARY")
public abstract class AbstractTdxCodeInfoVO implements Serializable,
	java.lang.Cloneable {
	 /*--------------- properties string,handier to copy ---------------------*/
	 //full properties 
	 //market,code,outstanding,province,industry,updatedDate,ipoDate,totals,countryShares,launchingShares,corporateShares,bShares,hShares,staffShares,totalAssets,liquidAssets,fixedAssets,intangibleAssets,holders,liquidLiabilities,longLiabilities,capitalFund,netAssets,mainIncome,mainProfit,receivables,operatingProfit,investmentsProfit,operatingCashFlow,totalCashFlow,remainder,totalProfit,afterTaxProfit,netProfit,undividedProfit,reserve1,reserve2
	 
	 //not null properties
	 //market,code

	/**
	 * 
	 */
	private static final long serialVersionUID = 2399158524102167197L;
	
	/**
	 * 市场
	 */
	@Id
	@Column(name="market",length=3L,type=java.sql.Types.INTEGER,nullable=false)
	protected Integer market;
	
	/**
	 * 代码
	 */
	@Id
	@Column(name="code",length=20L,type=java.sql.Types.VARCHAR,nullable=false)
	protected String code;
	
	/**
	 * 流通股本
	 */
	@Column(name="outstanding",length=20L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal outstanding;
	
	/**
	 * 所属省份
	 */
	@Column(name="province",length=3L,type=java.sql.Types.INTEGER,nullable=true)
	protected Integer province;
	
	/**
	 * 所属行业
	 */
	@Column(name="industry",length=3L,type=java.sql.Types.INTEGER,nullable=true)
	protected Integer industry;
	
	/**
	 * 财务更新日期
	 */
	@Column(name="updated_date",length=10L,type=java.sql.Types.DATE,nullable=true)
	protected LocalDate updatedDate;
	
	/**
	 * 上市日期
	 */
	@Column(name="ipo_date",length=10L,type=java.sql.Types.DATE,nullable=true)
	protected LocalDate ipoDate;
	
	/**
	 * 总股本
	 */
	@Column(name="totals",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal totals;
	
	/**
	 * 国家股
	 */
	@Column(name="country_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal countryShares;
	
	/**
	 * 发起人法人股
	 */
	@Column(name="launching_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal launchingShares;
	
	/**
	 * 法人股
	 */
	@Column(name="corporate_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal corporateShares;
	
	/**
	 * B股
	 */
	@Column(name="b_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal bShares;
	
	/**
	 * H股
	 */
	@Column(name="h_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal hShares;
	
	/**
	 * 职工股
	 */
	@Column(name="staff_shares",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal staffShares;
	
	/**
	 * 总资产
	 */
	@Column(name="total_assets",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal totalAssets;
	
	/**
	 * 流动资产
	 */
	@Column(name="liquid_assets",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal liquidAssets;
	
	/**
	 * 固定资产
	 */
	@Column(name="fixed_assets",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal fixedAssets;
	
	/**
	 * 无形资产
	 */
	@Column(name="intangible_assets",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal intangibleAssets;
	
	/**
	 * 股东人数
	 */
	@Column(name="holders",length=10L,type=java.sql.Types.INTEGER,nullable=true)
	protected Long holders;
	
	/**
	 * 流动负债
	 */
	@Column(name="liquid_liabilities",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal liquidLiabilities;
	
	/**
	 * 长期负债
	 */
	@Column(name="long_liabilities",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal longLiabilities;
	
	/**
	 * 资本公积金
	 */
	@Column(name="capital_fund",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal capitalFund;
	
	/**
	 * 净资产
	 */
	@Column(name="net_assets",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal netAssets;
	
	/**
	 * 主营收入
	 */
	@Column(name="main_income",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal mainIncome;
	
	/**
	 * 主营利润
	 */
	@Column(name="main_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal mainProfit;
	
	/**
	 * 应收帐款
	 */
	@Column(name="receivables",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal receivables;
	
	/**
	 * 营业利润
	 */
	@Column(name="operating_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal operatingProfit;
	
	/**
	 * 投资收益
	 */
	@Column(name="investments_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal investmentsProfit;
	
	/**
	 * 经营现金流
	 */
	@Column(name="operating_cash_flow",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal operatingCashFlow;
	
	/**
	 * 总现金流
	 */
	@Column(name="total_cash_flow",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal totalCashFlow;
	
	/**
	 * 存货
	 */
	@Column(name="remainder",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal remainder;
	
	/**
	 * 利润总额
	 */
	@Column(name="total_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal totalProfit;
	
	/**
	 * 税后利润
	 */
	@Column(name="after_tax_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal afterTaxProfit;
	
	/**
	 * 净利润
	 */
	@Column(name="net_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal netProfit;
	
	/**
	 * 未分利润
	 */
	@Column(name="undivided_profit",length=18L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal undividedProfit;
	
	/**
	 * 保留
	 */
	@Column(name="reserve1",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal reserve1;
	
	/**
	 * 保留
	 */
	@Column(name="reserve2",length=15L,type=java.sql.Types.DECIMAL,nullable=true)
	protected BigDecimal reserve2;
	

	/** default constructor */
	public AbstractTdxCodeInfoVO() {
	}
	
	/** pk constructor */
	public AbstractTdxCodeInfoVO(Integer market,String code)
	{
		this.market=market;
		this.code=code;
	}


	/** full constructor */
	public AbstractTdxCodeInfoVO(Integer market,String code,BigDecimal outstanding,Integer province,Integer industry,LocalDate updatedDate,LocalDate ipoDate,BigDecimal totals,BigDecimal countryShares,BigDecimal launchingShares,BigDecimal corporateShares,BigDecimal bShares,BigDecimal hShares,BigDecimal staffShares,BigDecimal totalAssets,BigDecimal liquidAssets,BigDecimal fixedAssets,BigDecimal intangibleAssets,Long holders,BigDecimal liquidLiabilities,BigDecimal longLiabilities,BigDecimal capitalFund,BigDecimal netAssets,BigDecimal mainIncome,BigDecimal mainProfit,BigDecimal receivables,BigDecimal operatingProfit,BigDecimal investmentsProfit,BigDecimal operatingCashFlow,BigDecimal totalCashFlow,BigDecimal remainder,BigDecimal totalProfit,BigDecimal afterTaxProfit,BigDecimal netProfit,BigDecimal undividedProfit,BigDecimal reserve1,BigDecimal reserve2)
	{
		this.market=market;
		this.code=code;
		this.outstanding=outstanding;
		this.province=province;
		this.industry=industry;
		this.updatedDate=updatedDate;
		this.ipoDate=ipoDate;
		this.totals=totals;
		this.countryShares=countryShares;
		this.launchingShares=launchingShares;
		this.corporateShares=corporateShares;
		this.bShares=bShares;
		this.hShares=hShares;
		this.staffShares=staffShares;
		this.totalAssets=totalAssets;
		this.liquidAssets=liquidAssets;
		this.fixedAssets=fixedAssets;
		this.intangibleAssets=intangibleAssets;
		this.holders=holders;
		this.liquidLiabilities=liquidLiabilities;
		this.longLiabilities=longLiabilities;
		this.capitalFund=capitalFund;
		this.netAssets=netAssets;
		this.mainIncome=mainIncome;
		this.mainProfit=mainProfit;
		this.receivables=receivables;
		this.operatingProfit=operatingProfit;
		this.investmentsProfit=investmentsProfit;
		this.operatingCashFlow=operatingCashFlow;
		this.totalCashFlow=totalCashFlow;
		this.remainder=remainder;
		this.totalProfit=totalProfit;
		this.afterTaxProfit=afterTaxProfit;
		this.netProfit=netProfit;
		this.undividedProfit=undividedProfit;
		this.reserve1=reserve1;
		this.reserve2=reserve2;
	}
	
	/**
	 *@param market the market to set
	 */
	public void setMarket(Integer market) {
		this.market=market;
	}
		
	/**
	 *@return the Market
	 */
	public Integer getMarket() {
	    return this.market;
	}
	
	/**
	 *@param code the code to set
	 */
	public void setCode(String code) {
		this.code=code;
	}
		
	/**
	 *@return the Code
	 */
	public String getCode() {
	    return this.code;
	}
	
	/**
	 *@param outstanding the outstanding to set
	 */
	public void setOutstanding(BigDecimal outstanding) {
		this.outstanding=outstanding;
	}
		
	/**
	 *@return the Outstanding
	 */
	public BigDecimal getOutstanding() {
	    return this.outstanding;
	}
	
	/**
	 *@param province the province to set
	 */
	public void setProvince(Integer province) {
		this.province=province;
	}
		
	/**
	 *@return the Province
	 */
	public Integer getProvince() {
	    return this.province;
	}
	
	/**
	 *@param industry the industry to set
	 */
	public void setIndustry(Integer industry) {
		this.industry=industry;
	}
		
	/**
	 *@return the Industry
	 */
	public Integer getIndustry() {
	    return this.industry;
	}
	
	/**
	 *@param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(LocalDate updatedDate) {
		this.updatedDate=updatedDate;
	}
		
	/**
	 *@return the UpdatedDate
	 */
	public LocalDate getUpdatedDate() {
	    return this.updatedDate;
	}
	
	/**
	 *@param ipoDate the ipoDate to set
	 */
	public void setIpoDate(LocalDate ipoDate) {
		this.ipoDate=ipoDate;
	}
		
	/**
	 *@return the IpoDate
	 */
	public LocalDate getIpoDate() {
	    return this.ipoDate;
	}
	
	/**
	 *@param totals the totals to set
	 */
	public void setTotals(BigDecimal totals) {
		this.totals=totals;
	}
		
	/**
	 *@return the Totals
	 */
	public BigDecimal getTotals() {
	    return this.totals;
	}
	
	/**
	 *@param countryShares the countryShares to set
	 */
	public void setCountryShares(BigDecimal countryShares) {
		this.countryShares=countryShares;
	}
		
	/**
	 *@return the CountryShares
	 */
	public BigDecimal getCountryShares() {
	    return this.countryShares;
	}
	
	/**
	 *@param launchingShares the launchingShares to set
	 */
	public void setLaunchingShares(BigDecimal launchingShares) {
		this.launchingShares=launchingShares;
	}
		
	/**
	 *@return the LaunchingShares
	 */
	public BigDecimal getLaunchingShares() {
	    return this.launchingShares;
	}
	
	/**
	 *@param corporateShares the corporateShares to set
	 */
	public void setCorporateShares(BigDecimal corporateShares) {
		this.corporateShares=corporateShares;
	}
		
	/**
	 *@return the CorporateShares
	 */
	public BigDecimal getCorporateShares() {
	    return this.corporateShares;
	}
	
	/**
	 *@param bShares the bShares to set
	 */
	public void setBShares(BigDecimal bShares) {
		this.bShares=bShares;
	}
		
	/**
	 *@return the BShares
	 */
	public BigDecimal getBShares() {
	    return this.bShares;
	}
	
	/**
	 *@param hShares the hShares to set
	 */
	public void setHShares(BigDecimal hShares) {
		this.hShares=hShares;
	}
		
	/**
	 *@return the HShares
	 */
	public BigDecimal getHShares() {
	    return this.hShares;
	}
	
	/**
	 *@param staffShares the staffShares to set
	 */
	public void setStaffShares(BigDecimal staffShares) {
		this.staffShares=staffShares;
	}
		
	/**
	 *@return the StaffShares
	 */
	public BigDecimal getStaffShares() {
	    return this.staffShares;
	}
	
	/**
	 *@param totalAssets the totalAssets to set
	 */
	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets=totalAssets;
	}
		
	/**
	 *@return the TotalAssets
	 */
	public BigDecimal getTotalAssets() {
	    return this.totalAssets;
	}
	
	/**
	 *@param liquidAssets the liquidAssets to set
	 */
	public void setLiquidAssets(BigDecimal liquidAssets) {
		this.liquidAssets=liquidAssets;
	}
		
	/**
	 *@return the LiquidAssets
	 */
	public BigDecimal getLiquidAssets() {
	    return this.liquidAssets;
	}
	
	/**
	 *@param fixedAssets the fixedAssets to set
	 */
	public void setFixedAssets(BigDecimal fixedAssets) {
		this.fixedAssets=fixedAssets;
	}
		
	/**
	 *@return the FixedAssets
	 */
	public BigDecimal getFixedAssets() {
	    return this.fixedAssets;
	}
	
	/**
	 *@param intangibleAssets the intangibleAssets to set
	 */
	public void setIntangibleAssets(BigDecimal intangibleAssets) {
		this.intangibleAssets=intangibleAssets;
	}
		
	/**
	 *@return the IntangibleAssets
	 */
	public BigDecimal getIntangibleAssets() {
	    return this.intangibleAssets;
	}
	
	/**
	 *@param holders the holders to set
	 */
	public void setHolders(Long holders) {
		this.holders=holders;
	}
		
	/**
	 *@return the Holders
	 */
	public Long getHolders() {
	    return this.holders;
	}
	
	/**
	 *@param liquidLiabilities the liquidLiabilities to set
	 */
	public void setLiquidLiabilities(BigDecimal liquidLiabilities) {
		this.liquidLiabilities=liquidLiabilities;
	}
		
	/**
	 *@return the LiquidLiabilities
	 */
	public BigDecimal getLiquidLiabilities() {
	    return this.liquidLiabilities;
	}
	
	/**
	 *@param longLiabilities the longLiabilities to set
	 */
	public void setLongLiabilities(BigDecimal longLiabilities) {
		this.longLiabilities=longLiabilities;
	}
		
	/**
	 *@return the LongLiabilities
	 */
	public BigDecimal getLongLiabilities() {
	    return this.longLiabilities;
	}
	
	/**
	 *@param capitalFund the capitalFund to set
	 */
	public void setCapitalFund(BigDecimal capitalFund) {
		this.capitalFund=capitalFund;
	}
		
	/**
	 *@return the CapitalFund
	 */
	public BigDecimal getCapitalFund() {
	    return this.capitalFund;
	}
	
	/**
	 *@param netAssets the netAssets to set
	 */
	public void setNetAssets(BigDecimal netAssets) {
		this.netAssets=netAssets;
	}
		
	/**
	 *@return the NetAssets
	 */
	public BigDecimal getNetAssets() {
	    return this.netAssets;
	}
	
	/**
	 *@param mainIncome the mainIncome to set
	 */
	public void setMainIncome(BigDecimal mainIncome) {
		this.mainIncome=mainIncome;
	}
		
	/**
	 *@return the MainIncome
	 */
	public BigDecimal getMainIncome() {
	    return this.mainIncome;
	}
	
	/**
	 *@param mainProfit the mainProfit to set
	 */
	public void setMainProfit(BigDecimal mainProfit) {
		this.mainProfit=mainProfit;
	}
		
	/**
	 *@return the MainProfit
	 */
	public BigDecimal getMainProfit() {
	    return this.mainProfit;
	}
	
	/**
	 *@param receivables the receivables to set
	 */
	public void setReceivables(BigDecimal receivables) {
		this.receivables=receivables;
	}
		
	/**
	 *@return the Receivables
	 */
	public BigDecimal getReceivables() {
	    return this.receivables;
	}
	
	/**
	 *@param operatingProfit the operatingProfit to set
	 */
	public void setOperatingProfit(BigDecimal operatingProfit) {
		this.operatingProfit=operatingProfit;
	}
		
	/**
	 *@return the OperatingProfit
	 */
	public BigDecimal getOperatingProfit() {
	    return this.operatingProfit;
	}
	
	/**
	 *@param investmentsProfit the investmentsProfit to set
	 */
	public void setInvestmentsProfit(BigDecimal investmentsProfit) {
		this.investmentsProfit=investmentsProfit;
	}
		
	/**
	 *@return the InvestmentsProfit
	 */
	public BigDecimal getInvestmentsProfit() {
	    return this.investmentsProfit;
	}
	
	/**
	 *@param operatingCashFlow the operatingCashFlow to set
	 */
	public void setOperatingCashFlow(BigDecimal operatingCashFlow) {
		this.operatingCashFlow=operatingCashFlow;
	}
		
	/**
	 *@return the OperatingCashFlow
	 */
	public BigDecimal getOperatingCashFlow() {
	    return this.operatingCashFlow;
	}
	
	/**
	 *@param totalCashFlow the totalCashFlow to set
	 */
	public void setTotalCashFlow(BigDecimal totalCashFlow) {
		this.totalCashFlow=totalCashFlow;
	}
		
	/**
	 *@return the TotalCashFlow
	 */
	public BigDecimal getTotalCashFlow() {
	    return this.totalCashFlow;
	}
	
	/**
	 *@param remainder the remainder to set
	 */
	public void setRemainder(BigDecimal remainder) {
		this.remainder=remainder;
	}
		
	/**
	 *@return the Remainder
	 */
	public BigDecimal getRemainder() {
	    return this.remainder;
	}
	
	/**
	 *@param totalProfit the totalProfit to set
	 */
	public void setTotalProfit(BigDecimal totalProfit) {
		this.totalProfit=totalProfit;
	}
		
	/**
	 *@return the TotalProfit
	 */
	public BigDecimal getTotalProfit() {
	    return this.totalProfit;
	}
	
	/**
	 *@param afterTaxProfit the afterTaxProfit to set
	 */
	public void setAfterTaxProfit(BigDecimal afterTaxProfit) {
		this.afterTaxProfit=afterTaxProfit;
	}
		
	/**
	 *@return the AfterTaxProfit
	 */
	public BigDecimal getAfterTaxProfit() {
	    return this.afterTaxProfit;
	}
	
	/**
	 *@param netProfit the netProfit to set
	 */
	public void setNetProfit(BigDecimal netProfit) {
		this.netProfit=netProfit;
	}
		
	/**
	 *@return the NetProfit
	 */
	public BigDecimal getNetProfit() {
	    return this.netProfit;
	}
	
	/**
	 *@param undividedProfit the undividedProfit to set
	 */
	public void setUndividedProfit(BigDecimal undividedProfit) {
		this.undividedProfit=undividedProfit;
	}
		
	/**
	 *@return the UndividedProfit
	 */
	public BigDecimal getUndividedProfit() {
	    return this.undividedProfit;
	}
	
	/**
	 *@param reserve1 the reserve1 to set
	 */
	public void setReserve1(BigDecimal reserve1) {
		this.reserve1=reserve1;
	}
		
	/**
	 *@return the Reserve1
	 */
	public BigDecimal getReserve1() {
	    return this.reserve1;
	}
	
	/**
	 *@param reserve2 the reserve2 to set
	 */
	public void setReserve2(BigDecimal reserve2) {
		this.reserve2=reserve2;
	}
		
	/**
	 *@return the Reserve2
	 */
	public BigDecimal getReserve2() {
	    return this.reserve2;
	}



	/**
     * @todo vo columns to String
     */
    @Override
	public String toString() {
		StringBuilder columnsBuffer=new StringBuilder();
		columnsBuffer.append("market=").append(getMarket()).append("\n");
		columnsBuffer.append("code=").append(getCode()).append("\n");
		columnsBuffer.append("outstanding=").append(getOutstanding()).append("\n");
		columnsBuffer.append("province=").append(getProvince()).append("\n");
		columnsBuffer.append("industry=").append(getIndustry()).append("\n");
		columnsBuffer.append("updatedDate=").append(getUpdatedDate()).append("\n");
		columnsBuffer.append("ipoDate=").append(getIpoDate()).append("\n");
		columnsBuffer.append("totals=").append(getTotals()).append("\n");
		columnsBuffer.append("countryShares=").append(getCountryShares()).append("\n");
		columnsBuffer.append("launchingShares=").append(getLaunchingShares()).append("\n");
		columnsBuffer.append("corporateShares=").append(getCorporateShares()).append("\n");
		columnsBuffer.append("bShares=").append(getBShares()).append("\n");
		columnsBuffer.append("hShares=").append(getHShares()).append("\n");
		columnsBuffer.append("staffShares=").append(getStaffShares()).append("\n");
		columnsBuffer.append("totalAssets=").append(getTotalAssets()).append("\n");
		columnsBuffer.append("liquidAssets=").append(getLiquidAssets()).append("\n");
		columnsBuffer.append("fixedAssets=").append(getFixedAssets()).append("\n");
		columnsBuffer.append("intangibleAssets=").append(getIntangibleAssets()).append("\n");
		columnsBuffer.append("holders=").append(getHolders()).append("\n");
		columnsBuffer.append("liquidLiabilities=").append(getLiquidLiabilities()).append("\n");
		columnsBuffer.append("longLiabilities=").append(getLongLiabilities()).append("\n");
		columnsBuffer.append("capitalFund=").append(getCapitalFund()).append("\n");
		columnsBuffer.append("netAssets=").append(getNetAssets()).append("\n");
		columnsBuffer.append("mainIncome=").append(getMainIncome()).append("\n");
		columnsBuffer.append("mainProfit=").append(getMainProfit()).append("\n");
		columnsBuffer.append("receivables=").append(getReceivables()).append("\n");
		columnsBuffer.append("operatingProfit=").append(getOperatingProfit()).append("\n");
		columnsBuffer.append("investmentsProfit=").append(getInvestmentsProfit()).append("\n");
		columnsBuffer.append("operatingCashFlow=").append(getOperatingCashFlow()).append("\n");
		columnsBuffer.append("totalCashFlow=").append(getTotalCashFlow()).append("\n");
		columnsBuffer.append("remainder=").append(getRemainder()).append("\n");
		columnsBuffer.append("totalProfit=").append(getTotalProfit()).append("\n");
		columnsBuffer.append("afterTaxProfit=").append(getAfterTaxProfit()).append("\n");
		columnsBuffer.append("netProfit=").append(getNetProfit()).append("\n");
		columnsBuffer.append("undividedProfit=").append(getUndividedProfit()).append("\n");
		columnsBuffer.append("reserve1=").append(getReserve1()).append("\n");
		columnsBuffer.append("reserve2=").append(getReserve2()).append("\n");
		return columnsBuffer.toString();
	}
}
