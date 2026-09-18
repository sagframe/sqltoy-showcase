/**
 *
 */
package com.sqltoy.quickstart;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.sagacity.sqltoy.model.Page;
import org.sagacity.sqltoy.model.ParallelConfig;
import org.sagacity.sqltoy.model.ParallelQuery;
import org.sagacity.sqltoy.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sqltoy.quickstart.vo.DeviceOrderVO;
import com.sqltoy.quickstart.vo.StaffInfoVO;

/**
 * @project sqltoy-quickstart
 * @description 并行查询演示：同时执行多个没有前后依赖关系的sql，变串行为并行，提升整体效率
 *              注意：不要用于事务操作过程中；多个sql共用参数条件(合集)，框架自动为每个sql提取实际参数
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class ParallelQueryTest {
	@Autowired
	private LightDao lightDao;

	@Test
	public void parallelQuery() {
		List<QueryResult<DeviceOrderVO>> list = lightDao.parallelQuery(
				Arrays.asList(
						ParallelQuery.create().sql("qstart_order_search").resultType(DeviceOrderVO.class),
						ParallelQuery.create().sql("qstart_fastPage").resultType(DeviceOrderVO.class)),
				MapKit.keys("orderId", "staffName").values("S0001", "陈"));
		for (QueryResult<DeviceOrderVO> result : list) {
			System.err.println("查询结果数量:" + result.getRows().size());
		}
	}

	@Test
	public void parallelQueryWithConfig() {
		// 通过 ParallelConfig 设置并行线程数与最大等待时长
		List<QueryResult<DeviceOrderVO>> list = lightDao.parallelQuery(
				Arrays.asList(
						ParallelQuery.create().sql("qstart_order_search").resultType(DeviceOrderVO.class),
						ParallelQuery.create().sql("qstart_fastPage").resultType(DeviceOrderVO.class)),
				MapKit.keys("orderId", "staffName").values("S0001", "陈"),
				ParallelConfig.create().maxThreads(10).maxWaitSeconds(60));
		System.err.println("并行查询结果组数:" + list.size());
	}

	@Test
	public void parallelQueryWithPage() {
		// ParallelQuery 设置 page 即为分页查询（分页+非分页可混合并行）
		List<QueryResult<StaffInfoVO>> list = lightDao.parallelQuery(
				Arrays.asList(
						ParallelQuery.create().sql("qstart_fastPage").page(new Page(10, 1))
								.resultType(StaffInfoVO.class),
						ParallelQuery.create().sql("qstart_fastPage").resultType(StaffInfoVO.class)),
				MapKit.keys("staffName").values("陈"));
		System.err.println("分页并行结果总记录:"
				+ list.get(0).getPageResult().getRecordCount());
	}
}
