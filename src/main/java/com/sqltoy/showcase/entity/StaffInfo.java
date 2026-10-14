package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Staff entity for the link / tenant data-auth demos.
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_STAFF_INFO")
public class StaffInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "STAFF_ID", length = 22)
	private String staffId;

	@Column(name = "STAFF_CODE", length = 22)
	private String staffCode;

	@Column(name = "STAFF_NAME", length = 30)
	private String staffName;

	@Column(name = "ORGAN_ID", length = 8)
	private String organId;

	@Column(name = "SEX_TYPE", length = 1)
	private String sexType;

	@Column(name = "ENTRY_DATE")
	private LocalDate entryDate;

	@Column(name = "CREATE_BY", length = 22)
	private String createBy;

	@Column(name = "CREATE_TIME")
	private LocalDateTime createTime;

	@Column(name = "STATUS")
	private Integer status;

	// not a table column: filled in memory by the cache translate (see JavaSqlFeatureTest)
	private String organName;
}
