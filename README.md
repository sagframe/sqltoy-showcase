English | [简体中文](#中文说明)

# SqlToy Showcase

**Full-feature demos for SqlToy ORM** — everything beyond the [quickstart lessons](https://github.com/sagframe/sqltoy-quickstart): sharding, multi-datasource, NoSQL and the core feature catalog.

If you are new to SqlToy, start with the [quickstart](https://github.com/sagframe/sqltoy-quickstart) — seven runnable JUnit lessons on in-memory H2, nothing to install. The modules here demonstrate the infrastructure-level features and therefore expect real databases.

## Modules

| Module | Demonstrates | Requires |
| --- | --- | --- |
| [sqltoy-showcase](trunk/sqltoy-showcase) | The core feature catalog: dynamic SQL, cache translate, pivot/summary, secure encrypt/decrypt, link queries, stored procedures | MySQL |
| [sqltoy-sharding](trunk/sqltoy-sharding) | Database & table sharding strategies (`hash`, `weight`, real-time/history table split); init script in [docs](trunk/sqltoy-sharding/docs) | MySQL |
| [sqltoy-dynamic-datasource](trunk/sqltoy-dynamic-datasource) | Multi-datasource routing and transactions | MySQL |
| [sqltoy-nosql](trunk/sqltoy-nosql) | Elasticsearch (sql/json dual mode) and MongoDB (query/aggregation with cache translate) | Elasticsearch, MongoDB |

## Running a module

Each module is a standalone Spring Boot project:

1. Point `spring.datasource` in its `application.yml` at your database (credentials and URL).
2. Create the schema with the module's `docs/*.sql` script where present.
3. Run the module's tests — every demo is a JUnit test you can read and run in your IDE.

📖 [SqlToy documentation](https://sagframe.github.io/sqltoy-docs/#/en/) · [Quickstart lessons](https://github.com/sagframe/sqltoy-quickstart) · [Main repository](https://github.com/sagframe/sagacity-sqltoy)

## 中文说明

SqlToy 全功能演示仓库：分库分表、多数据源、NoSQL 以及核心功能全集（动态 sql、缓存翻译、行列转换、安全脱敏、存储过程等）。每个子项目都是独立的 Spring Boot 工程，配置好数据库连接并按 `docs/` 下的脚本初始化后，直接运行各模块的 JUnit 测试即可。

入门请从 [sqltoy-quickstart](https://github.com/sagframe/sqltoy-quickstart)（7 节课程，H2 内存库零安装）开始，[中文文档](https://sagframe.github.io/sqltoy-docs/#/)。
