package com.sqltoy.showcase.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.sagacity.sqltoy.config.annotation.OneToMany;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Parent side of the hierarchy-packaging demo (see HierarchyPackagingTest): ONE left-join
 * query returns flat rows and sqltoy folds them back into the 1..n object structure.
 *
 * notNullField is the point of this demo: with a LEFT JOIN, a parent without children
 * (EMPTY_TYPE) still produces one row whose child columns are all null — sqltoy checks
 * the notNullField property (dictKey) and skips that phantom child instead of packaging
 * an empty stub object.
 */
@Data
@Accessors(chain = true)
public class DictTypeVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dictType;
	private String dictTypeName;
	private String comments;
	private Integer showIndex;
	private Integer status;

	// parent dictType -> child dictType. NOTE: orderBy applies to the cascade-LOAD path
	// (loading children by a second query); when packaging a flat join result set the
	// child order is the SQL's ORDER BY — see HierarchyPackagingTest
	@OneToMany(fields = { "dictType" }, mappedFields = { "dictType" }, orderBy = "showIndex desc",
			notNullField = "dictKey")
	private List<DictDetailVO> dictDetailVOs = new ArrayList<DictDetailVO>();
}
