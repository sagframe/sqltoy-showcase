# 小提示
* quickstart只演示了部分功能,核心是让大家快速上手，详细功能参见文档
*    理论上来sqltoy可以解决您项目上全部数据库交互，我们的erp、数据平台、电商平台已经验证了这一点

# 学习步骤
* 1. 配置pom引入sqltoy的依赖
* 2. 配置正确pom build避免sql文件无法编译到classes下面
* 3. 配置application.yml,注意改用application.properties的配置说明
* 4. 编写springboot 主程序,注意@ComponentScan配置
* 5. 初始化数据库
* 6. 利用quickvo生产VO(或POJO),在出问题时关注schema配置,其他问题请参见quickvo.xml中的注释

## 1. 请参见pom.xml 引入sqltoy,注意版本号使用最新版本

```xml
    <dependency>
		<groupId>com.sagframe</groupId>
		<artifactId>sagacity-sqltoy-starter</artifactId>
		<version>5.2.42</version>
	</dependency>
```

## 2. 注意pom中build的配置,否则导致 *.sql.xml文件无法编译到classes下面去
* 核心配置:src/main/java 下面的<include>**/*.xml</include>

```xml
	<resource>
			<directory>src/main/java</directory>
			<excludes>
				<exclude>**/*.java</exclude>
			</excludes>
			<includes>
				<include>**/*.xml</include>
			</includes>
		</resource>
		<resource>
			<directory>src/main/resources</directory>
			<includes>
				<include>**/*.xml</include>
				<include>**/*.properties</include>
				<include>**/*.yml</include>
				<include>**/*.sql</include>
				<include>**/*.jpg</include>
			</includes>
		</resource>
	</resources>
	<testResources>
		<testResource>
			<directory>src/test/java</directory>
			<excludes>
				<exclude>**/*.java</exclude>
			</excludes>
			<includes>
				<include>**/*.xml</include>
			</includes>
		</testResource>
		<testResource>
			<directory>src/test/resources</directory>
			<includes>
				<include>**/*.xml</include>
				<include>**/*.properties</include>
				<include>**/*.yml</include>
				<include>**/*.sql</include>
			</includes>
		</testResource>
	</testResources>
```

##  3. spring-sqltoy.xml配置
* 常规配置，核心要点:sqlResourcesDir 是路径名,多个路径用逗号分隔,不要填错

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd"
	default-autowire="byName" default-lazy-init="true">
	<!-- 配置sqltoy框架的上下文 -->
	<bean id="sqlToyContext" class="org.sagacity.sqltoy.SqlToyContext"
		init-method="initialize" destroy-method="destroy">
		<!-- 指定sql.xml 文件的路径实现目录的递归查找,多个路径则用逗号分隔 -->
		<property name="sqlResourcesDir"
			value="classpath:com/sqltoy/quickstart" />
		<!-- 5.2.x 关键影响点:appContext -->
    	<property name="appContext">
        	<bean class="org.sagacity.sqltoy.integration.impl.SpringAppContext"/>
    	</property>
		<!--非必须属性: 跨数据库函数自动替换,适用于跨数据库软件产品,如mysql开发，oracle部署 -->
		<property name="functionConverts" value="default" />
		<!--非必须属性: 提供项目对数据库新增、修改操作时完成诸如:
		                创建人、创建时间、修改人、修改时间等关键字段统一赋值，减轻开发者每次赋值操作 -->
		<property name="unifyFieldsHandler">
			<bean class="com.sqltoy.plugins.SqlToyUnifyFieldsHandler" />
		</property>
		<!-- 非必须属性:缓存翻译管理器,默认值为:classpath:sqltoy-translate.xml -->
		<property name="translateConfig"
			value="classpath:sqltoy-translate.xml" />
		<!--非必须属性:默认值为false -->
		<property name="debug" value="${sqltoy.debug}" />
		<!--非必须属性:数据库保留字兼容处理 -->
		<property name="reservedWords" value="maxvalue,minvalue"/>
	</bean>
</beans>

```

* 最简单配置

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd"
	default-autowire="byName" default-lazy-init="true">
	<!-- 配置sqltoy框架的上下文 -->
	<bean id="sqlToyContext" class="org.sagacity.sqltoy.SqlToyContext"
		init-method="initialize" destroy-method="destroy">
		<!-- 指定sql.xml 文件的路径实现目录的递归查找,多个路径则用逗号分隔 -->
		<property name="sqlResourcesDir" value="classpath:com/sqltoy/quickstart" />
		<!--非必须属性:默认值为false -->
		<property name="debug" value="${sqltoy.debug}" />
	</bean>
</beans>
```

## 4. 编写项目主程序,参见:src/main/java 下面的SqlToyApplication
```java
package com.sqltoy.quickstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * 
 * @project sqltoy-quickstart
 * @description quickstart 主程序入口
 * @author zhongxuchen 
 * @version v1.0, Date:2020年7月17日
 * @modify 2020年7月17日,修改说明
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.sqltoy.config", "com.sqltoy.quickstart" })
@EnableTransactionManagement
public class SqlToyApplication {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SqlToyApplication.class, args);
	}
}

```

## 5. 参见src/test/java 下面的InitDataBaseTest,生成数据库表结构和初始化数据

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SqlToyApplication.class)
public class InitDataBaseTest {

	@Autowired
	private InitDBService initDBService;

	@Test
	public void testInitDB() {
		String dbSqlFile = "classpath:mock/quickstart_init.sql";
		System.err.println("开始执行数据库初始化!");
		initDBService.initDatabase(dbSqlFile);
	}
}
```

## 6. 通过quickvo连数据库自动生成POJO
* 将数据库驱动类放于tools/quickvo/libs下面
* 配置tools/quickvo/db.properties 文件

```properties
#############  db config ####################
jdbc.driver_class=com.mysql.cj.jdbc.Driver
# url characterEncoding=utf-8 param is need
jdbc.url=jdbc:mysql://192.168.56.109:3306/quickstart?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
# mysql schema=dbname,oracle schema=username
jdbc.schema=quickstart
jdbc.username=quickstart
jdbc.password=quickstart
```

* 配置tools/quickvo/quickvo.xml 中的任务,关键部分如下

```xml
<!-- db配置文件 -->
<property file="db.properties" />
<property name="project.version" value="1.0.0" />
<property name="project.name" value="sqltoy-quickstart" />
<property name="project.package" value="com.sqltoy" />
<property name="include.schema" value="false" />
<!--set method 是否支持返回对象自身(默认是true),即: public VO setName(String name){this.name=name;return this;} -->
<property name="field.support.linked.set" value="true" />
<!-- schema 对照关系:mysql 对应  db 名称; oracle 对应 用户名称;   -->
<datasource name="quickstart" url="${db.url}"	driver="${db.driver_class}" schema="${db.schema}"
<tasks dist="../../src/main/java" encoding="UTF-8">
	<!-- include 是正则表达式匹配 -->
	<task active="true" author="zhongxuchen" include="^SQLTOY_\w+" datasource="quickstart" swagger-model="false">
		<!-- substr 表示截取表名的前缀部分(一般表会按模块增加前缀),如不截取则substr="" name="#{subName}VO" subName是约定词,VO这两个字符可以随意改变  -->
		<vo package="${project.package}.quickvo.vo" substr="Sqltoy" name="#{subName}VO" />
	</task>
</tasks>
```

* 点击quickvo.bat 即可生产VO了,linux 或 mac 则执行quickvo.sh 
* windows环境下:

```
java -cp ./libs/* org.sagacity.quickvo.QuickVOStart quickvo.xml
```

* mac电脑:

```
java -cp ./libs/\* org.sagacity.quickvo.QuickVOStart ./quickvo.xml
```

# 源码导航
*  阅读的入口 src/test/java com.sqltoy.quickstart
* InitDataBaseTest 数据库初始化测试调用
* StaffInfoServiceTest 演示常规的CRUD
* TreeTableTest 演示树形表结构的构建和查询
* ShardingSearchTest 演示分表记录保存和查询(Sharding策略请参见src/main/java com.sqltoy.config.ShardingStrategyConfig )
* AdvanceQueryTest 查询相关的演示
* UniqueCaseTest 演示唯一性验证
* CascadeCaseTest 演示级联操作 
* LockCaseTest 演示锁记录修改操作
* StoreTest 演示存储过程调用
* JavaCodeSqlTest 演示在代码中写sql实现原本xml中的功能
* DTOConvertPOJOTest 演示在严格分层场景下DTO和POJO互转的范例

# 疑问解答
## 为什么要将*.sql.xml 放在java路径下?
* sqltoy推荐大家项目按照业务划分先分模块(消息中心、系统管理、订单管理等)后分层(web层、service)，sql文件放于模块中便于模块整体迁移和产品化，同时有利于开发过程，让开发者不需要不断的切换目录
* 当然这个是sqltoy推荐做法，开发者则可以根据自身实际情况而定,并非强制!
