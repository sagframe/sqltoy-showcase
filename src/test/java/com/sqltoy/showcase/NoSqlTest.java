package com.sqltoy.showcase;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * NoSQL demos (Elasticsearch / MongoDB) — these need running instances, so they are
 * gated on environment variables and skipped by default (including in CI).
 *
 * To run them locally:
 *
 *   # Elasticsearch with the elasticsearch-sql plugin (https://github.com/NLPchina/elasticsearch-sql)
 *   ES_SQL=http://localhost:9200/_sql
 *
 *   # MongoDB
 *   MONGO_URI=mongodb://localhost:27017
 *
 * Typical usage once the environment is set:
 *
 *   // Elasticsearch (sql mode) — sqlId from a sql file, or a raw ES-SQL statement
 *   List&lt;CompanyVO&gt; companies = (List&lt;CompanyVO&gt;) lightDao.elastic()
 *           .sql("es_find_company").values(params).resultType(CompanyVO.class).find();
 *
 *   // MongoDB — chainable query/aggregation builder with cache translate support
 *   List&lt;TransDetailVO&gt; details = (List&lt;TransDetailVO&gt;) lightDao.mongo()
 *           .query("collectionName").fields(...).filter(...).resultType(TransDetailVO.class).find();
 */
class NoSqlTest {

	@Test
	void elasticsearch() {
		Assumptions.assumeTrue(System.getenv("ES_SQL") != null,
				"set ES_SQL to the elasticsearch-sql endpoint to run this demo");
	}

	@Test
	void mongodb() {
		Assumptions.assumeTrue(System.getenv("MONGO_URI") != null,
				"set MONGO_URI to a running MongoDB to run this demo");
	}
}
