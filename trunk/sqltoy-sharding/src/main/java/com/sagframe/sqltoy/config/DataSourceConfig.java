package com.sagframe.sqltoy.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfig {
	@Autowired
	private Environment env;

	@Bean(name = "dataSource")
	@Primary
	public DataSource primaryDataSource() throws Exception {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		// druid+mysql8 连接有问题
		// ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
		ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
		ds.setUniqueResourceName("dataSource");
		ds.setPoolSize(5);
		ds.setXaProperties(build("spring.datasource.primary."));
		return ds;
	}

	@Bean(name = "sharding")
	public DataSource dataSource() throws Exception {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		// ds.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
		ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
		ds.setUniqueResourceName("sharding");
		ds.setPoolSize(5);
		ds.setXaProperties(build("spring.datasource.secondary."));
		return ds;
	}

	private Properties build(String prefix) {
		Properties prop = new Properties();
		prop.put("url", env.getProperty(prefix + "url"));
		prop.put("user", env.getProperty(prefix + "username"));
		prop.put("password", env.getProperty(prefix + "password"));
		// druid 连接存在问题
		// prop.put("driverClassName", env.getProperty(prefix + "driverClassName"));
		return prop;
	}
}