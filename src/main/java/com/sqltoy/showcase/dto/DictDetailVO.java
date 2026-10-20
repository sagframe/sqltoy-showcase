package com.sqltoy.showcase.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Child side of the hierarchy-packaging demo — see DictTypeVO's @OneToMany.
 * Plain VO filled from the join result set, not bound to one table.
 */
@Data
@Accessors(chain = true)
public class DictDetailVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dictType;
	private String dictKey;
	private String dictName;
	private Integer showIndex;
	private Integer status;
}
