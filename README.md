English | [简体中文](#中文说明)

# SqlToy Showcase

[![CI](https://github.com/sagframe/sqltoy-showcase/actions/workflows/ci.yml/badge.svg)](https://github.com/sagframe/sqltoy-showcase/actions/workflows/ci.yml)
[![Native Build](https://github.com/sagframe/sqltoy-showcase/actions/workflows/native-build.yml/badge.svg)](https://github.com/sagframe/sqltoy-showcase/actions/workflows/native-build.yml)
[![SqlToy](https://img.shields.io/badge/SqlToy-6.0.8-blue)](https://github.com/sagframe/sagacity-sqltoy)
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
| [`AdvancedQueryTest`](src/test/java/com/sqltoy/showcase/AdvancedQueryTest.java) | `<link>` group concatenation and the `@loop()` list-expansion macro (with secure slice ranges) |
| [`ParallelQueryTest`](src/test/java/com/sqltoy/showcase/ParallelQueryTest.java) | `parallelQuery`: the basic parallel form, `ParallelConfig` thread tuning, mixed paged + plain queries in one call, and sharding-datasource routing inside a parallel query |
| [`FetchStreamTest`](src/test/java/com/sqltoy/showcase/FetchStreamTest.java) | `fetchStream`: rows consumed one-by-one through a `StreamResultHandler` callback (the export/ETL pattern), with `doNextConsume` stopping the stream early |
| [`TenantAuthTest`](src/test/java/com/sqltoy/showcase/TenantAuthTest.java) | Tenant-style data authorization: `:authedOrganIds` is force-injected by the UnifyFieldsHandler, so out-of-scope rows are filtered even when the caller passes nothing |
| [`MoreQueryFeatureTest`](src/test/java/com/sqltoy/showcase/MoreQueryFeatureTest.java) | Classic xml showcase features with runnable proofs: tuple IN `((a,b) in (:xs,:ys))` matches pairs not a cartesian product; `@value()` dynamic ORDER BY; IN with 1000+ values auto-split into OR chunks; `<cache-arg>` reverse cache match (fuzzy name -> precise ids, no JOIN/LIKE) |
| [`HierarchyTest`](src/test/java/com/sqltoy/showcase/HierarchyTest.java) | Automatic hierarchy packaging: ONE join query returns flat rows and sqltoy folds them into the 1..n object structure; `hiberarchyFieldsMap` resolves parent/child column-name clashes |
| [`HierarchyPackagingTest`](src/test/java/com/sqltoy/showcase/HierarchyPackagingTest.java) | `@OneToMany notNullField`: with a LEFT JOIN, a childless parent's all-null row does NOT become a phantom child object |
| [`CascadeTest`](src/test/java/com/sqltoy/showcase/CascadeTest.java) | Head/item cascade through `@OneToMany`: saving the head persists the items, deleting the head removes them — no per-table code |
| [`DynamicCacheTest`](src/test/java/com/sqltoy/showcase/DynamicCacheTest.java) | FIFO dynamic cache for million-row master data: no full load — missed keys are batch-fetched once per result set through [`BigDictDynamicCacheFetch`](src/main/java/com/sqltoy/showcase/plugins/BigDictDynamicCacheFetch.java), and the bounded FIFOMap provably evicts the least recently used key |
| [`JsonGeometryTest`](src/test/java/com/sqltoy/showcase/JsonGeometryTest.java) | Special column types on H2: JSON columns ↔ List/Map properties, GEOMETRY columns ↔ JTS Geometry (with in-memory spatial analysis), enum auto-conversion (plain enums by name, getKey enums by code); VECTOR is pgvector-specific (documented in the test) |
| [`CustomTypeHandlerTest`](src/test/java/com/sqltoy/showcase/CustomTypeHandlerTest.java) | Custom `TypeHandler`: a `Money` value object maps onto a varchar column (`USD:150000.00`) in both directions via `spring.sqltoy.typeHandler`; JSON/enum handling on the same entity keeps working |
| [`FiltersTest`](src/test/java/com/sqltoy/showcase/FiltersTest.java) | The `<filters>` toolbox: server-side parameter conditioning (eq/blank/toDate/like/primary/cacheArg…) before the SQL assembles |
| [`IncludeTest`](src/test/java/com/sqltoy/showcase/IncludeTest.java) | SQL composition with `@include`: reuse a fragment by sqlId, or inject a fragment assembled in Java at runtime |
| [`JavaSqlFeatureTest`](src/test/java/com/sqltoy/showcase/JavaSqlFeatureTest.java) | The xml-tag capabilities driven from a PLAIN JAVA sql string via `QueryExecutor`: `.filters(...)`, `.translates(...)`, `@fast()` as sql text, and count-optimized pagination |
| [`BatchWriteTest`](src/test/java/com/sqltoy/showcase/BatchWriteTest.java) | Batch & parallel writes: `saveAll` in one interaction group; the fluent `save().parallelConfig(...).many(...)` splits a batch across threads |
| [`AutoDdlTest`](src/test/java/com/sqltoy/showcase/AutoDdlTest.java) | autoDDL: generate CREATE TABLE scripts from the `@Entity` POJOs themselves — the reverse direction of quickvo |
| [`ReportMoreTest`](src/test/java/com/sqltoy/showcase/ReportMoreTest.java) | Report transforms beyond the quickstart's Lesson 7: `<rows-chain-relative>` (period-over-period between rows) and `<unpivot>` (columns to rows) |
| [`StoreProcedureTest`](src/test/java/com/sqltoy/showcase/StoreProcedureTest.java) | Stored-procedure calls through H2 `CREATE ALIAS`; the multiple-result-sets demo is a documented skip (needs SQL Server / Oracle) |
| [`BusinessIdAndDataVersionTest`](src/test/java/com/sqltoy/showcase/BusinessIdAndDataVersionTest.java) | Optimistic `@DataVersion`: stale updates fail loudly instead of silently overwriting; plus the `@BusinessId` Redis-key reference (disabled here — see the quickstart's Lesson 1 for a live run) |
| [`NoSqlTest`](src/test/java/com/sqltoy/showcase/NoSqlTest.java) | Real MongoDB + Elasticsearch through Testcontainers: `<mql>` dynamic json query / `$in` / findPage via `mongo()`, `<eql>` json REST query via `elastic()`; the `mode="sql"` ES variant needs the elasticsearch-sql plugin. Skips gracefully when Docker is missing or a container fails to start (the first run pulls ~3.7 GB of images) |
| [`NativeAotTest`](src/test/java/com/sqltoy/showcase/NativeAotTest.java) | GraalVM native readiness on a plain JVM: runs the real Spring Boot AOT processing and asserts the generated reachability metadata covers the `@Entity` classes, the sql/mock resources and the class-name configured extensions — the actual `native:compile` runs in the [native-build workflow](.github/workflows/native-build.yml) |

## How it works

- [`DataSourceConfig`](src/main/java/com/sqltoy/showcase/config/DataSourceConfig.java) creates the two named H2 datasources (ds0 primary, ds1) and applies each schema single-threaded — avoiding the H2-INIT-vs-pool race.
- The demo sql lives in [`showcase.sql.xml`](src/main/java/com/sqltoy/showcase/showcase.sql.xml); schemas and seed data in [`mock/`](src/main/resources/mock) with the standard audit columns (`create_by/create_time/update_by/update_time/status`).
- A random database name per context keeps test contexts isolated.

## GraalVM native image

The showcase doubles as a native-image reference app (sqltoy 6.0.8+):

- The sqltoy starter registers everything it knows from `spring.sqltoy.*` by itself: entity reflection (scanned from `packagesToScan` at build time — entities are parsed lazily on first use, so a native build must learn them there), the `sqlResourcesDir` / `translateConfig` / secure-key resource patterns, the class-name configured extensions (`unifyFieldsHandler` / `typeHandler`) and the JSON providers present on the classpath (so the one the runtime detects is always registered).
- [`ShowcaseRuntimeHints`](src/main/java/com/sqltoy/showcase/config/ShowcaseRuntimeHints.java) is what's left for the application — only what the framework cannot know: the `mock/**` schema scripts that `DataSourceConfig` loads itself, and the `Procedures` class referenced **by name** from H2's `CREATE ALIAS`.
- [`DataSourceConfig`](src/main/java/com/sqltoy/showcase/config/DataSourceConfig.java) declares the sharding-strategy beans with **concrete return types**: Spring AOT pre-determines bean types from the declaration, and interface-typed `@Bean(initMethod=...)` methods are unresolvable in a native image.
- [`NativeAotTest`](src/test/java/com/sqltoy/showcase/NativeAotTest.java) verifies the entity, extension and resource metadata on a plain JVM (part of `mvn test`) by running the same AOT processing that `native:compile` consumes and inspecting the generated reachability metadata.
- [`NativeSmokeRunner`](src/main/java/com/sqltoy/showcase/config/NativeSmokeRunner.java): run the built binary with `--smoke` to exercise entity load + SQL count + translate cache against the in-memory H2 databases (prints `SMOKE OK`).

Building the real binary needs [GraalVM](https://www.graalvm.org/) **for JDK 23+ (25 recommended)** — GraalVM for JDK 21 chokes on the current reachability metadata repository's `typeReached` conditions; on Windows: Visual Studio Build Tools):

```bash
mvn -Pnative native:compile -DskipTests
./target/sqltoy-showcase --smoke   # starts in ~0.1s, prints SMOKE OK after entity load + query + translate cache
```

The [native-build workflow](.github/workflows/native-build.yml) does exactly this on ubuntu + GraalVM (manual dispatch + weekly). Known 6.0.8 native limitations: `updateSaveFetch` is unavailable (it generates byte-buddy proxies at runtime) and sql hot-reload is off — sql files are baked into the image.

## Related

📖 [SqlToy documentation](https://sagframe.github.io/sqltoy-docs/#/en/) · [Quickstart lessons](https://github.com/sagframe/sqltoy-quickstart) (start there) · [Main repository](https://github.com/sagframe/sagacity-sqltoy)

## 中文说明

SqlToy 全功能演示：分库分表（双 H2 库模拟分片拓扑、按用户哈希分库、按日期分表）、多数据源、数据脱敏、树形表维护与汇总、`<link>` 拼接、`@loop` 宏、并行查询（含 ParallelConfig 线程配置与混合分页）、流式查询（fetchStream）、层次封装（hiberarchy + notNullField）、FIFO 动态缓存（百万级主数据按需取数）、多租户数据权限、乐观锁 dataVersion 与 @BusinessId 业务主键参考。全部测试即演示，clone 后 `mvn test` 零安装运行；Elasticsearch/MongoDB 通过 Testcontainers 起真实容器演示（无 Docker 或镜像拉取失败时自动跳过，不影响其余演示）。`NativeAotTest` 在纯 JVM 上验证 GraalVM native 构建所需的 AOT 元数据（实体反射、sql/资源打包、扩展类注册），真实 native 编译由 native-build workflow 在 ubuntu + GraalVM 上执行（手动/每周触发）。

入门请从 [sqltoy-quickstart](https://github.com/sagframe/sqltoy-quickstart)（课程式入门，零安装）开始，[中文文档](https://sagframe.github.io/sqltoy-docs/#/)。
