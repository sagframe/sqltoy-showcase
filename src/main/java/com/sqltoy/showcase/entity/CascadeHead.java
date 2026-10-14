package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.config.annotation.OneToMany;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Cascade demo: saving or deleting the head cascades to SQLTOY_CASCADE_ITEM
 * through the @OneToMany link — head PK (transDate, transCode) maps to the
 * item fields (transDate, transId).
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_CASCADE_HEAD")
public class CascadeHead implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TRANS_DATE")
	private LocalDate transDate;

	@Id
	@Column(name = "TRANS_CODE", length = 30)
	private String transCode;

	@Column(name = "TOTAL_CNT", precision = 12, scale = 3)
	private BigDecimal totalCnt;

	@Column(name = "TOTAL_AMT", precision = 12, scale = 2)
	private BigDecimal totalAmt;

	@Column(name = "CREATE_BY", length = 22)
	private String createBy;

	@Column(name = "CREATE_TIME")
	private LocalDateTime createTime;

	@Column(name = "UPDATE_BY", length = 22)
	private String updateBy;

	@Column(name = "UPDATE_TIME")
	private LocalDateTime updateTime;

	@Column(name = "STATUS")
	private Integer status;

	// head PK fields -> item mapped fields; save/delete on the head cascades
	@OneToMany(fields = { "transDate", "transCode" }, mappedFields = { "transDate", "transId" }, delete = true)
	protected List<CascadeItem> items = new ArrayList<CascadeItem>();
}
