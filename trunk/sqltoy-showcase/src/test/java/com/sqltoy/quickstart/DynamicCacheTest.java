/**
 *
 */
package com.sqltoy.quickstart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.translate.TranslateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson2.JSON;

/**
 * @project sqltoy-quickstart
 * @description 超大规模主数据缓存(FIFO动态缓存)演示
 *
 *              bigDictCache是local-translate + dynamic-cache="true"的动态缓存,
 *              不做全量加载:
 *              1、首次查询时DICT_KEY未命中本地FIFOMap,框架收集去重后通过
 *              BigDictDynamicCacheFetch一次性批量加载,回填缓存并完成翻译
 *              2、再次查询相同key时直接命中本地缓存,不再产生取数IO
 *              3、FIFOMap容量达到dynamic-cache-maxSize后,自动挤掉最不常用的key
 *
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class DynamicCacheTest {

	@Autowired
	LightDao lightDao;

	@Test
	public void testDynamicCacheTranslate() {
		// 查询字典key并自动翻译:translate cache="bigDictCache" columns="DICT_KEY" cache-indexs="1"
		List<Map> result = lightDao.find("qstart_dynamic_cache_case", MapKit.map(), Map.class);
		System.err.println("共翻译输出:" + result.size() + " 条!");
		// 观察前5条:DICT_KEY列的值已被翻译为字典名称
		result.stream().limit(5).forEach((row) -> System.err.println(JSON.toJSONString(row)));

		// 通过TranslateManager查看动态缓存已加载的数据量(证明数据进入了本地FIFOMap)
		TranslateManager translateManager = lightDao.getSqlToyContext().getTranslateManager();
		HashMap<String, Object[]> cacheData = translateManager.getCacheData("bigDictCache", null);
		System.err.println("bigDictCache当前FIFOMap缓存量:" + (cacheData == null ? 0 : cacheData.size()) + " 条!");

		// 第二次查询相同数据:全部命中本地缓存,不会再触发BigDictDynamicCacheFetch取数
		result = lightDao.find("qstart_dynamic_cache_case", MapKit.map(), Map.class);
		System.err.println("第二次查询共翻译输出:" + result.size() + " 条!");
	}
}
