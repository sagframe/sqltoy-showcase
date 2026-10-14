package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.config.annotation.Sharding;
import org.sagacity.sqltoy.config.annotation.Strategy;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Sharding demo entity: rows are hash-sharded across ds0/ds1 by userId,
 * and date-sharded between the 15-day table and the history table by transDate.
 * The strategies are the Spring beans from DataSourceConfig.
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_TRANS_LOG_15D")
@Sharding(db = @Strategy(name = "hashDataSource", fields = { "userId" }),
		table = @Strategy(name = "realHisTable", fields = { "transDate" }))
public class TransLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TRANS_ID", length = 32)
	private String transId;

	@Column(name = "USER_ID", length = 32)
	private String userId;

	@Column(name = "TRANS_AMT", precision = 12, scale = 2)
	private BigDecimal transAmt;

	@Column(name = "TRANS_DATE")
	private LocalDate transDate;

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
}
