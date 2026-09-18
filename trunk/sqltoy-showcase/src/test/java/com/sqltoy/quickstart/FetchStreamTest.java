/**
 *
 */
package com.sqltoy.quickstart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.callback.StreamResultHandler;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.QueryExecutor;
import com.sqltoy.quickstart.vo.StaffInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @project sqltoy-quickstart
 * @description fetchStream 流式查询演示：超大数据量逐行回调处理，不会将全部结果加载进内存
 *              适合大数据量导出文件、批量计算等场景
 * @author zhongxuchen
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class FetchStreamTest {
	@Autowired
	private LightDao lightDao;

	@Test
	public void fetchStream() {
		// start: 开始提取时回调（可拿到列名与类型）; consume: 逐行回调; end: 结束回调
		lightDao.fetchStream(new QueryExecutor("select * from sqltoy_staff_info t where t.STATUS=1")
				.resultType(StaffInfoVO.class), new StreamResultHandler() {
					private int count = 0;

					@Override
					public void consume(Object row, int rowIndex) {
						count++;
						// 这里逐行处理数据，如写入文件或其他存储
					}

					@Override
					public void end() {
						System.err.println("流式处理完成，共处理 " + count + " 条记录!");
					}
				});
	}
}
