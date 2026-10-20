package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.config.model.Translate;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.dto.DictEntryVO;
import com.sqltoy.showcase.plugins.BigDictDynamicCacheFetch;

/**
 * FIFO dynamic cache for million-row master data (see the sqltoy_FIFO_translate doc).
 *
 * bigDictCache (sqltoy-translate.xml) is a local-translate with dynamic-cache="true":
 * it is NEVER loaded in full. Keys missed in the local FIFOMap are collected across the
 * whole result set, deduplicated, and handed to BigDictDynamicCacheFetch in ONE batch
 * call (registered via spring.sqltoy.dynamicCacheFetch). The FIFOMap is bounded
 * (maxSize=5 here, 100000 by default): the least recently used key is evicted on
 * overflow — frequent keys stay, rare keys are re-fetched on demand.
 *
 * The fetcher carries demo counters so the two design points are provable:
 * one batch IO per result set, and re-fetch after eviction.
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class DynamicCacheTest {

	@Autowired
	LightDao lightDao;

	@Test
	void fifoDynamicCacheBatchFetchesAndEvicts() {
		BigDictDynamicCacheFetch.resetCounters();

		// phase 1 — four device keys against a cold cache: all misses are collected
		// and fetched in ONE batch call (the batch design point)
		List<DictEntryVO> devices = translateDevices();
		assertEquals(4, devices.size());
		assertEquals("Network device", devices.get(0).getDictName());
		assertEquals("Rack server", devices.get(1).getDictName());
		assertEquals("Laptop", devices.get(2).getDictName());
		assertEquals("Printer", devices.get(3).getDictName());
		assertEquals(1, BigDictDynamicCacheFetch.getBatchCalls(), "one batch IO for the whole result set");
		assertEquals(4, BigDictDynamicCacheFetch.getKeysFetched());

		// phase 2 — two more keys (M/F): the FIFOMap holds maxSize=5, so backfilling
		// these evicts the least recently used device key
		List<DictEntryVO> sexes = translateSexTypes();
		assertEquals("Male", sexes.get(0).getDictName());
		assertEquals("Female", sexes.get(1).getDictName());
		assertEquals(2, BigDictDynamicCacheFetch.getBatchCalls());
		assertEquals(6, BigDictDynamicCacheFetch.getKeysFetched());

		// phase 3 — re-translate the device keys: three are still cached, the evicted
		// one is fetched again (exactly one key), proving the FIFO bound works
		List<DictEntryVO> again = translateDevices();
		assertEquals("Network device", again.get(0).getDictName());
		assertEquals("Rack server", again.get(1).getDictName());
		assertEquals("Laptop", again.get(2).getDictName());
		assertEquals("Printer", again.get(3).getDictName());
		assertEquals(3, BigDictDynamicCacheFetch.getBatchCalls());
		assertEquals(7, BigDictDynamicCacheFetch.getKeysFetched());
		again.forEach(e -> System.out.println("device: " + e.getDictKey() + " -> " + e.getDictName()));
	}

	// the query aliases the key column onto dictName as well; the translate replaces
	// that column in place with the cached name — same usage as any full-load cache
	private List<DictEntryVO> translateDevices() {
		return queryWithTranslate("select t.DICT_KEY dictKey,t.DICT_KEY dictName from SQLTOY_DICT_DETAIL t"
				+ " where t.DICT_TYPE='DEVICE_TYPE' order by t.SHOW_INDEX");
	}

	private List<DictEntryVO> translateSexTypes() {
		return queryWithTranslate("select t.DICT_KEY dictKey,t.DICT_KEY dictName from SQLTOY_DICT_DETAIL t"
				+ " where t.DICT_TYPE='SEX_TYPE' order by t.SHOW_INDEX");
	}

	private List<DictEntryVO> queryWithTranslate(String sql) {
		return lightDao.findByQuery(new QueryExecutor(sql).resultType(DictEntryVO.class)
				.translates(new Translate("bigDictCache").setColumn("dictName"))).getRows();
	}
}
