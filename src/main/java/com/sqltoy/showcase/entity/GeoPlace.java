package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Geometry;
import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.model.JdbcTypes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Geometry-column demo: a JTS Geometry property maps to the GEOMETRY column
 * (WKT/WKB conversion handled by sqltoy, JdbcTypes.GEOMETRY).
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_GEO_PLACE")
public class GeoPlace implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PLACE_ID", length = 16)
	private String placeId;

	@Column(name = "PLACE_NAME", length = 60)
	private String placeName;

	@Column(name = "LOCATION", type = JdbcTypes.GEOMETRY)
	private Geometry location;

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
