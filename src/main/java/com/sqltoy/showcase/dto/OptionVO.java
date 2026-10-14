package com.sqltoy.showcase.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Component data source demo: the object form of getTranslateCache maps the cache
 * sql columns (STAFF_ID/STAFF_NAME) onto these properties, ready to serialize
 * straight into a dropdown / checkbox / suggest component.
 */
@Data
@Accessors(chain = true)
public class OptionVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String staffId;
	private String staffName;
}
