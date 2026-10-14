package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Tree-table demo: NODE_ROUTE / NODE_LEVEL / IS_LEAF are standard column names
 * maintained automatically by wrapTreeTableRoute on save.
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_TREE_NODE")
public class TreeNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NODE_ID", length = 10)
	private String nodeId;

	@Column(name = "PID", length = 10)
	private String pid;

	@Column(name = "NODE_NAME", length = 30)
	private String nodeName;

	@Column(name = "VALUE_CNT")
	private Integer valueCnt;

	@Column(name = "NODE_ROUTE", length = 200)
	private String nodeRoute;

	@Column(name = "NODE_LEVEL")
	private Integer nodeLevel;

	@Column(name = "IS_LEAF")
	private Integer isLeaf;

	@Column(name = "UPDATE_TIME")
	private LocalDateTime updateTime;
}
