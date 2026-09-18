/**
 *
 */
package com.sqltoy.quickstart.cache;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.integration.AppContext;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.translate.DynamicCacheFetch;

/**
 * @project sqltoy-quickstart
 * @description 超大规模缓存数据动态获取(DynamicCacheFetch)实现范例
 *
 *              适用场景:平台型电商SKU、行政区划、号码归属地等百万级以上的配置数据,
 *              全量加载不现实。框架在本地仅通过FIFOMap保留最常用的数据
 *              (如dynamic-cache-maxSize配置的10万条),翻译时缓存未命中的key
 *              会经过本接口按需批量加载。
 *
 *              生产实践建议:数据规模特别大时,一般本地FIFOMap未命中后先从
 *              redis等分布式缓存批量读取(可通过initialize获取redisTemplate),
 *              redis也没有时再回源数据库。
 *
 * @author zhongxuchen
 */
public class BigDictDynamicCacheFetch implements DynamicCacheFetch {

	/**
	 * 记录appContext,使用时再获取LightDao
	 * 说明:initialize在SqlToyContext初始化阶段被调用,此时LightDao等bean可能尚未就绪,
	 * 直接getBean易形成循环依赖,因此采用懒加载
	 */
	private AppContext appContext;

	private volatile LightDao lightDao;

	@Override
	public void initialize(AppContext appContext) {
		this.appContext = appContext;
		// 如需对接redis,可在此处获取容器中的redisTemplate等资源
		// Object redisTemplate = appContext.getBean("redisTemplate");
	}

	/*
	 * 单key获取:单对象翻译等非批量场景调用,直接复用批量逻辑,避免维护两套取数代码
	 */
	@Override
	public Object[] getCache(String cacheName, String cacheType, String sid, String[] properties, String key) {
		Map<String, Object[]> datas = getCache(cacheName, cacheType, sid, properties, new String[] { key });
		return (datas == null) ? null : datas.get(key);
	}

	/*
	 * 批量获取(5.6.67版本增强):一次结果集中未命中的key经去重后一次性传入,显著减少IO交互次数
	 * 返回结构:Map<key, Object[]{key,name1,name2,...}> 第0列是key,后续列与缓存翻译cache-indexs对应
	 * (sid、properties是local-translate中配置的标识和属性,便于通用型实现区分不同缓存的数据来源)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object[]> getCache(String cacheName, String cacheType, String sid, String[] properties,
			String[] keys) {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		if (keys == null || keys.length == 0) {
			return result;
		}
		// resultType=Array.class 返回行为Object[]数组
		List rows = getLightDao().find(
				"select DICT_KEY,DICT_NAME,STATUS from sqltoy_dict_detail where DICT_KEY in (:keys)",
				MapKit.map("keys", Arrays.asList(keys)), Array.class);
		for (Object row : rows) {
			Object[] rowData = (Object[]) row;
			result.put(rowData[0].toString(), rowData);
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
}
