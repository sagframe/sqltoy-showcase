package com.sqltoy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;

/**
 * 
 * @project sqltoy-quickstart
 * @description quickstart 主程序入口
 * @author zhongxuchen
 * @version v1.0, Date:2020年7月17日
 * @modify 2020年7月17日,修改说明
 */
//注意:这里要排除DruidDataSourceAutoConfigure
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@ComponentScan(basePackages = { "com.sqltoy" })
@EnableTransactionManagement
public class SqlToyApplication {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SqlToyApplication.class, args);
	}
}
