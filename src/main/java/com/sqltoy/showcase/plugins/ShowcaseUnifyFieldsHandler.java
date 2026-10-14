package com.sqltoy.showcase.plugins;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sagacity.sqltoy.model.DataAuthFilterConfig;
import org.sagacity.sqltoy.model.IgnoreKeyCaseMap;
import org.sagacity.sqltoy.plugins.IUnifyFieldsHandler;

/**
 * @project sqltoy-showcase
 * @description audit-field auto-fill plus the authedOrganIds data-auth filter
 */
public class ShowcaseUnifyFieldsHandler implements IUnifyFieldsHandler {

	private String defaultUserName = "system-auto";

	@Override
	public Map<String, Object> createUnifyFields() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		LocalDateTime now = LocalDateTime.now();
		map.put("createBy", defaultUserName);
		map.put("createTime", now);
		map.put("updateBy", defaultUserName);
		map.put("updateTime", now);
		return map;
	}

	@Override
	public Map<String, Object> updateUnifyFields() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("updateBy", defaultUserName);
		map.put("updateTime", LocalDateTime.now());
		return map;
	}

	/**
	 * simulated data authorization: sql referencing :authedOrganIds gets the
	 * allowed organizations injected — even when the caller passes nothing
	 * (forcelimit), so out-of-scope rows are filtered server-side.
	 */
	@Override
	public IgnoreKeyCaseMap<String, DataAuthFilterConfig> dataAuthFilters() {
		IgnoreKeyCaseMap<String, DataAuthFilterConfig> map = new IgnoreKeyCaseMap<String, DataAuthFilterConfig>();
		Set<String> organIds = new HashSet<String>();
		organIds.add("100005");
		organIds.add("100007");
		map.put("authedOrganIds", new DataAuthFilterConfig().setForcelimit(true).setValues(organIds));
		return map;
	}
}
