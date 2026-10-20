package com.sqltoy.showcase.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Key/name pair for the FIFO dynamic-cache demo (DynamicCacheTest): the query aliases the
 * key column onto dictName as well, and the bigDictCache translate replaces it in place.
 */
@Data
@Accessors(chain = true)
public class DictEntryVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String dictKey;
	private String dictName;
}
