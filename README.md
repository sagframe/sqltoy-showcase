English | [简体中文](#中文说明)

# SqlToy Showcase

[![CI](https://github.com/sagframe/sqltoy-showcase/actions/workflows/ci.yml/badge.svg)](https://github.com/sagframe/sqltoy-showcase/actions/workflows/ci.yml)
[![SqlToy](https://img.shields.io/badge/SqlToy-6.0.6-blue)](https://github.com/sagframe/sagacity-sqltoy)
![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)

**Full-feature demos for SqlToy ORM** — everything beyond the [quickstart lessons](https://github.com/sagframe/sqltoy-quickstart): sharding, multi-datasource, data masking, tree tables, and advanced query features.

Everything runs on **two in-memory H2 databases** that simulate a sharded topology — clone and `mvn test`, nothing to install. Each demo is a JUnit test you can read and run in your IDE.

## Demos

| Test | Demonstrates |
| --- | --- |
| [`ShardingTest`](src/test/java/com/sqltoy/showcase/ShardingTest.java) | Database & table sharding: `@Sharding` entity strategies hash rows across ds0/ds1 by user and route between the 15-day and history tables by date; queries route via `<sharding-datasource>` / `<sharding-table>` |
| [`MultiDataSourceTest`](src/test/java/com/sqltoy/showcase/MultiDataSourceTest.java) | Named datasources: every `DataSource` bean registers with sqltoy under its bean name; the `@Primary` serves unnamed calls |
| [`SecureEncryptTest`](src/test/java/com/sqltoy/showcase/SecureEncryptTest.java) | Field encryption: `@SecureConfig` encrypts tel/address on save (RSA keys in application.yml), loads decrypt automatically, and derives `tel_mask` — a masked search index, because ciphertext cannot be LIKE-searched |
| [`SecureMaskTest`](src/test/java/com/sqltoy/showcase/SecureMaskTest.java) | Decrypt-then-mask on read: `<secure-decrypt>` + `<secure-mask>` together — consumers see neither plaintext nor ciphertext |
| [`TreeTableTest`](src/test/java/com/sqltoy/showcase/TreeTableTest.java) | Tree tables: `wrapTreeTableRoute` maintains NODE_ROUTE/NODE_LEVEL/IS_LEAF on save; `<tree-sort>` orders the hierarchy and rolls values up |
| [`AdvancedQueryTest`](src/test/java/com/sqltoy/showcase/AdvancedQueryTest.java) | `<link>` group concatenation, the `@loop()` list-expansion macro, and `parallelQuery` running independent queries concurrently |
| [`TenantAuthTest`](src/test/java/com/sqltoy/showcase/TenantAuthTest.java) | Tenant-style data authorization: `:authedOrganIds` is force-injected by the UnifyFieldsHandler, so out-of-scope rows are filtered even when the caller passes nothing |
| [`MoreQueryFeatureTest`](src/test/java/com/sqltoy/showcase/MoreQueryFeatureTest.java) | Classic xml showcase features with runnable proofs: tuple IN `((a,b) in (:xs,:ys))` matches pairs not a cartesian product; `@value()` dynamic ORDER BY; IN with 1000+ values auto-split into OR chunks; `<cache-arg>` reverse cache match (fuzzy name -> precise ids, no JOIN/LIKE) |
| [`JsonGeometryTest`](src/test/java/com/sqltoy/showcase/JsonGeometryTest.java) | Special column types on H2: JSON columns ↔ List/Map properties, GEOMETRY columns ↔ JTS Geometry (with in-memory spatial analysis), enum auto-conversion (plain enums by name, getKey enums by code); VECTOR is pgvector-specific (documented in the test) |
| [`NoSqlTest`](src/test/java/com/sqltoy/showcase/NoSqlTest.java) | Real MongoDB + Elasticsearch through Testcontainers (auto-skipped without Docker): `<mql>` dynamic json query / `$in` / findPage via `mongo()`, `<eql>` json REST query via `elastic()`; the `mode="sql"` ES variant needs the elasticsearch-sql plugin |

## How it works

- [`DataSourceConfig`](src/main/java/com/sqltoy/showcase/config/DataSourceConfig.java) creates the two named H2 datasources (ds0 primary, ds1) and applies each schema single-threaded — avoiding the H2-INIT-vs-pool race.
- The demo sql lives in [`showcase.sql.xml`](src/main/java/com/sqltoy/showcase/showcase.sql.xml); schemas and seed data in [`mock/`](src/main/resources/mock) with the standard audit columns (`create_by/create_time/update_by/update_time/status`).
- A random database name per context keeps test contexts isolated.

## Related

📖 [SqlToy documentation](https://sagframe.github.io/sqltoy-docs/#/en/) · [Quickstart lessons](https://github.com/sagframe/sqltoy-quickstart) (start there) · [Main repository](https://github.com/sagframe/sagacity-sqltoy)

## 中文说明

SqlToy 全功能演示：分库分表（双 H2 库模拟分片拓扑、按用户哈希分库、按日期分表）、多数据源、数据脱敏、树形表维护与汇总、`<link>` 拼接、`@loop` 宏、并行查询、多租户数据权限。全部测试即演示，clone 后 `mvn test` 零安装运行；Elasticsearch/MongoDB 通过 Testcontainers 起真实容器演示（无 Docker 时自动跳过）。

入门请从 [sqltoy-quickstart](https://github.com/sagframe/sqltoy-quickstart)（7 节课程）开始，[中文文档](https://sagframe.github.io/sqltoy-docs/#/)。
