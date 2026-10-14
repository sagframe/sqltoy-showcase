package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.config.annotation.OneToMany;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Hierarchy-packaging demo: a single join query returns flat rows; sqltoy assembles
 * them back into this 1..n structure (each organ carries its staff list).
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_ORGAN_INFO")
public class OrganInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ORGAN_ID", length = 8)
	private String organId;

	@Column(name = "ORGAN_NAME", length = 60)
	private String organName;

	@Column(name = "SHOW_INDEX")
	private Integer showIndex;

	@Column(name = "STATUS")
	private Integer status;

	// not a table column: hierarchy packaging assembles the joined staff rows into this list.
	// @OneToMany is what marks it as a cascade collection (organ.organId -> staff.organId)
	@OneToMany(fields = { "organId" }, mappedFields = { "organId" })
	private List<StaffInfo> staffList = new ArrayList<StaffInfo>();
}
