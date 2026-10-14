package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.model.JdbcTypes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * JSON-column demo: List/Map properties map to the JSON column in both directions
 * through sqltoy's built-in JSON handling (JdbcTypes.JSON since 5.6.79).
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_STAFF_EXT")
public class StaffExt implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "STAFF_ID", length = 22)
	private String staffId;

	@Column(name = "SKILLS", type = JdbcTypes.JSON)
	private List<String> skills;

	@Column(name = "PROFILE", type = JdbcTypes.JSON)
	private Map<String, Object> profile;

	// plain enum: stored as its name() automatically
	@Column(name = "LEVEL", length = 20)
	private StaffLevel level;

	// enum with a getKey() method: stored as the key ("A"/"S") automatically
	@Column(name = "STATE", length = 10)
	private WorkState state;

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
