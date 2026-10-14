package com.sqltoy.showcase.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Document shape for the MongoDB / Elasticsearch staff collections.
 */
@Data
@Accessors(chain = true)
public class StaffDoc implements Serializable {

	private static final long serialVersionUID = 1L;

	private String staffId;
	private String staffName;
	private String organId;
	private Integer status;
}
