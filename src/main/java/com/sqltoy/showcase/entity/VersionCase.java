package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.DataVersion;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Optimistic data-version demo: updates carry a version condition and bump the
 * version column automatically (see @DataVersion).
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_VERSION_CASE")
public class VersionCase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", length = 22)
	private String id;

	@Column(name = "TITLE", length = 60)
	private String title;

	@DataVersion
	@Column(name = "DATA_VERSION")
	private Long dataVersion;

	@Column(name = "UPDATE_BY", length = 22)
	private String updateBy;

	@Column(name = "UPDATE_TIME")
	private LocalDateTime updateTime;

	@Column(name = "STATUS")
	private Integer status;
}
