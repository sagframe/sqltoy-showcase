package com.sqltoy.showcase.plugins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.integration.AppContext;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.translate.DynamicCacheFetch;

/**
 * FIFO dynamic-cache fetcher for the bigDictCache demo (see DynamicCacheTest and the
 * local-translate definition in sqltoy-translate.xml; registered through
 * spring.sqltoy.dynamicCacheFetch in application.yml).
 *
 * The cache is NOT loaded in full: keys missed in the local FIFOMap are collected,
 * deduplicated and handed to the batch method ONCE per result set — one IO for the whole
 * batch. Production fetchers of million-row master data usually check Redis first and
 * fall back to the database; this demo queries the dictionary table directly.
 */
public class BigDictDynamicCacheFetch implements DynamicCacheFetch {

	// demo instrumentation: lets the test prove "one batch per result set" and the
	// FIFOMap eviction (a re-fetch of an evicted key bumps the counters again)
	private static final AtomicInteger BATCH_CALLS = new AtomicInteger();
	private static final AtomicInteger KEYS_FETCHED = new AtomicInteger();

	// initialize runs while SqlToyContext is still starting: beans like LightDao may not
	// be ready yet, so capture the AppContext and resolve the bean lazily (avoids a
	// circular dependency). A redisTemplate bean would be fetched here as well.
	private AppContext appContext;
	private volatile LightDao lightDao;

	@Override
	public void initialize(AppContext appContext) {
		this.appContext = appContext;
	}

	// single-key form: reuse the batch logic instead of maintaining two fetch paths
	@Override
	public Object[] getCache(String cacheName, String cacheType, String sid, String[] properties, String key) {
		Map<String, Object[]> datas = getCache(cacheName, cacheType, sid, properties, new String[] { key });
		return (datas == null) ? null : datas.get(key);
	}

	// batch form: every missed key of one result set arrives here deduplicated, once
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object[]> getCache(String cacheName, String cacheType, String sid, String[] properties,
			String[] keys) {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		if (keys == null || keys.length == 0) {
			return result;
		}
		BATCH_CALLS.incrementAndGet();
		KEYS_FETCHED.addAndGet(keys.length);
		// sid/properties come from the cache definition and would select the data source in
		// a generic fetcher; this demo always reads the dictionary detail table.
		// Row 0 is the key, matching cache-indexs=1 (the name column) of the translate.
		List rows = getLightDao().find("select DICT_KEY,DICT_NAME,STATUS from SQLTOY_DICT_DETAIL where DICT_KEY in (:keys)",
				MapKit.map("keys", Arrays.asList(keys)));
		for (Object row : rows) {
			Object[] rowData = ((List<?>) row).toArray();
			result.put(String.valueOf(rowData[0]), rowData);
		}
		return result;
	}

	private LightDao getLightDao() {
		if (lightDao == null) {
			synchronized (this) {
				if (lightDao == null) {
					lightDao = appContext.getBean(LightDao.class);
				}
			}
		}
		return lightDao;
	}

	public static int getBatchCalls() {
		return BATCH_CALLS.get();
	}

	public static int getKeysFetched() {
		return KEYS_FETCHED.get();
	}

	public static void resetCounters() {
		BATCH_CALLS.set(0);
		KEYS_FETCHED.set(0);
	}
}
