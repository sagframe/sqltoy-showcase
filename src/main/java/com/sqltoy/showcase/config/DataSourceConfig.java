package com.sqltoy.showcase.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.sagacity.sqltoy.plugins.sharding.ShardingStrategy;
import org.sagacity.sqltoy.plugins.sharding.impl.DefaultShardingStrategy;
import org.sagacity.sqltoy.plugins.sharding.impl.HashShardingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Two named in-memory H2 datasources simulate a sharded topology:
 *  - ds0 (primary): the demo tables (staff, organ, secure, tree, trans log)
 *  - ds1: its own trans-log tables, so sharding visibly routes rows to both databases
 *
 * Each schema is applied eagerly and single-threaded in the @Bean method
 * (ResourceDatabasePopulator), which avoids the H2 INIT-vs-connection-pool race.
 */
@Configuration
public class DataSourceConfig {

	@Primary
	@Bean(name = "ds0")
	public DataSource ds0(@Value("${spring.datasource.ds0.url}") String url) {
		DataSource ds = DataSourceBuilder.create().url(url).username("sa").password("").build();
		new ResourceDatabasePopulator(new ClassPathResource("mock/schema-ds0.sql")).execute(ds);
		return ds;
	}

	@Bean(name = "ds1")
	public DataSource ds1(@Value("${spring.datasource.ds1.url}") String url) {
		DataSource ds = DataSourceBuilder.create().url(url).username("sa").password("").build();
		new ResourceDatabasePopulator(new ClassPathResource("mock/schema-ds1.sql")).execute(ds);
		return ds;
	}

	/**
	 * database sharding by hash modulo: userId hash 0 -> ds0, 1 -> ds1
	 */
	@Bean(name = "hashDataSource", initMethod = "initialize")
	public ShardingStrategy hashDataSource() {
		HashShardingStrategy strategy = new HashShardingStrategy();
		HashMap<String, String> dataSourceMap = new HashMap<String, String>();
		dataSourceMap.put("0", "ds0");
		dataSourceMap.put("1", "ds1");
		strategy.setDataSourceMap(dataSourceMap);
		return strategy;
	}

	/**
	 * table sharding: queries are written against the 15-day table;
	 * dates outside the window are routed to the history table
	 */
	@Bean(name = "realHisTable", initMethod = "initialize")
	public ShardingStrategy realHisTable() {
		DefaultShardingStrategy strategy = new DefaultShardingStrategy();
		strategy.setDays("14");
		HashMap<String, String> tableMap = new HashMap<String, String>();
		tableMap.put("SQLTOY_TRANS_LOG_15D", "SQLTOY_TRANS_LOG_HIS");
		strategy.setTableNamesMap(tableMap);
		return strategy;
	}
}
