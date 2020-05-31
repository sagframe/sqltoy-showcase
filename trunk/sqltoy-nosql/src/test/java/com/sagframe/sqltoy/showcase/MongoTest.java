package com.sagframe.sqltoy.showcase;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sagacity.sqltoy.dao.SqlToyLazyDao;
import org.sagacity.sqltoy.model.PaginationModel;
import org.sagacity.sqltoy.utils.DateUtil;
import org.sagacity.sqltoy.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alibaba.fastjson.JSON;
import com.sagframe.sqltoy.SqlToyApplication;
import com.sagframe.sqltoy.showcase.vo.PospTransDetailVO;
import com.sagframe.sqltoy.utils.ExcelUtil;

/**
 * 1、纯粹的crud则可以使用spring-data提供的相关template进行操作 2、sqltoy提供mongo和es集成的出发点: 1)
 * 充分发挥sqltoy动态条件组织的优势 2) 结合sqltoy的缓存翻译部分解决es和mongo关联一些配置表的复杂性(或者ETL清洗的复杂性) 3)
 * 利用sqltoy的提供的一些数据集合处理算法:脱敏、格式化、旋转、分组汇总等，简化直接写查询(或聚合)的复杂度 4)
 * 便于跟外部低代码框架集成，如报表平台，可以实现配置化数据查询和处理
 * 
 * @author zhongxuchen
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class MongoTest {
	@Autowired
	private SqlToyLazyDao sqlToyLazyDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void testDropCollection() throws Exception {
		mongoTemplate.dropCollection("fact_trans_details");
	}

	// mongo直接对象操作,参见对象上的注解@Document(collection="") @Id 以及@Field,这里不做介绍
	// sqltoy集成mongo主要是利用sqltoy的缓存翻译、以及一些提供的算法
	@Test
	public void testMockData() throws Exception {
		// 读取excel
		List dataSet = ExcelUtil.read("classpath:mock/trans_detail.xlsx", null, null, null, null);
		List<PospTransDetailVO> transData = new ArrayList<PospTransDetailVO>();
		List row;
		// 从第2行开始(跳过标题行)
		for (int i = 1; i < dataSet.size(); i++) {
			row = (List) dataSet.get(i);
			PospTransDetailVO vo = new PospTransDetailVO();
			vo.setTransId(row.get(0).toString());
			vo.setSrefNo((String) row.get(1));
			vo.setMerchantCode((String) row.get(2));
			vo.setTerminalCode(row.get(3).toString());
			vo.setAgentOrg(row.get(4).toString());
			vo.setTransDate(DateUtil.asLocalDate(DateUtil.convertDateObject(row.get(5))));
			vo.setTransTime(DateUtil.asLocalDateTime(DateUtil.convertDateObject(row.get(6))));
			vo.setTransCode((String) row.get(7));
			vo.setTransType((String) row.get(8));
			vo.setTransAmt(Double.parseDouble(row.get(9).toString()));
			vo.setPayAmt(Double.parseDouble(StringUtil.toString(row.get(10))));
			vo.setCupsFeeAmt(Double.parseDouble(StringUtil.toString(row.get(11))));
			vo.setFeeAmt(Double.parseDouble(StringUtil.toString(row.get(12))));
			transData.add(vo);
		}
		mongoTemplate.insertAll(transData);
	}

	@Test
	public void testSearch() {
		List<PospTransDetailVO> result = (List<PospTransDetailVO>) sqlToyLazyDao.mongo().sql("sqltoy_mongo_find")
				.resultType(PospTransDetailVO.class).names("transType", "transAmt").values("N", 0).find();
		for (PospTransDetailVO item : result) {
			System.err.println(JSON.toJSONString(item));
		}
	}

	@Test
	public void testFindPage() {
		PaginationModel page = sqlToyLazyDao.mongo().sql("sqltoy_mongo_find").resultType(PospTransDetailVO.class)
				.names("transType", "transAmt").values("N", null).findPage(new PaginationModel<>());
		System.err.println("总记录数量=" + page.getRecordCount());
		for (PospTransDetailVO item : (List<PospTransDetailVO>) page.getRows()) {
			System.err.println(JSON.toJSONString(item));
		}
	}

	/**
	 * 测试聚合查询
	 */
	@Test
	public void testAgg() {

	}
}
