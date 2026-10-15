package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sqltoy.showcase.dto.StaffDoc;

/**
 * NoSQL demos — real engines through Testcontainers (require Docker; skipped gracefully
 * without it, including in environments that cannot run containers):
 *
 *  - MongoDB: an xml &lt;mql&gt; json query with DYNAMIC &lt;#&gt; segments, executed through
 *    sqltoy's mongo() chain (backed by Spring MongoTemplate) — query, $in filter, findPage
 *  - Elasticsearch: a native json REST &lt;eql&gt; query with a dynamic segment, executed
 *    through sqltoy's elastic(); the mode="sql" variant needs the elasticsearch-sql plugin
 */
@SpringBootTest(classes = ShowcaseApplication.class)
@Testcontainers(disabledWithoutDocker = true)
class NoSqlTest {

	// started eagerly in a static block: guaranteed up before Spring reads the properties.
	// MongoDB 8.0.x — the same version the sqltoy-verify harness runs against.
	private static final GenericContainer<?> MONGO = new GenericContainer<>("docker.io/library/mongo:8.0.6")
			.withExposedPorts(27017)
			.waitingFor(org.testcontainers.containers.wait.strategy.Wait
					.forLogMessage(".*Waiting for connections.*\\n", 1).withStartupTimeout(Duration.ofSeconds(120)));

	// Elasticsearch 9.5.x, aligned with the sqltoy-verify harness (same image coordinate and
	// JVM options). ES 9 binds the HTTP port well before the cluster accepts requests, so an
	// explicit readiness wait is required — waiting for the port alone makes seeding flaky.
	private static final GenericContainer<?> ES = new GenericContainer<>(
			"docker.elastic.co/elasticsearch/elasticsearch:9.5.2")
			.withEnv("discovery.type", "single-node").withEnv("xpack.security.enabled", "false")
			.withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
			// a low-free-space CI disk would otherwise trip the watermark and turn the index read-only
			.withEnv("cluster.routing.allocation.disk.threshold_enabled", "false").withExposedPorts(9200)
			.waitingFor(org.testcontainers.containers.wait.strategy.Wait.forHttp("/").forPort(9200)
					.forStatusCode(200).withStartupTimeout(Duration.ofSeconds(180)));

	static {
		MONGO.start();
		ES.start();
	}

	@Autowired
	LightDao lightDao;

	@Autowired
	MongoTemplate mongoTemplate;

	@org.springframework.test.context.DynamicPropertySource
	static void registerEndpoints(org.springframework.test.context.DynamicPropertyRegistry registry) {
		// Spring Boot 4 moved the property from spring.data.mongodb.uri to spring.mongodb.uri
		registry.add("spring.mongodb.uri",
				() -> "mongodb://" + MONGO.getHost() + ":" + MONGO.getMappedPort(27017) + "/showcase");
		registry.add("spring.data.mongodb.uri",
				() -> "mongodb://" + MONGO.getHost() + ":" + MONGO.getMappedPort(27017) + "/showcase");
		registry.add("spring.sqltoy.elastic.endpoints[0].id", () -> "default");
		registry.add("spring.sqltoy.elastic.endpoints[0].url",
				() -> "http://" + ES.getHost() + ":" + ES.getMappedPort(9200));
	}

	private void seedStaff() {
		if (!mongoTemplate.getCollectionNames().contains("staff_info")) {
			mongoTemplate.insert(List.of(
					new StaffDoc().setStaffId("S0001").setStaffName("Alice Chen").setOrganId("100005").setStatus(1),
					new StaffDoc().setStaffId("S0002").setStaffName("Bob Zhang").setOrganId("100005").setStatus(1),
					new StaffDoc().setStaffId("S0003").setStaffName("Carol Wang").setOrganId("100007").setStatus(1),
					new StaffDoc().setStaffId("S0004").setStaffName("David Li").setOrganId("100001").setStatus(0)),
					"staff_info");
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	void mongoDynamicJsonQuery() {
		seedStaff();
		// dynamic <#> segments: status always filters, organIds ($in) joins when set
		List<StaffDoc> active = (List<StaffDoc>) lightDao.mongo().sql("sc_mongo_staff")
				.names("status").values(1).resultType(StaffDoc.class).find();
		System.out.println("mongo active staff: " + active.size());
		active.forEach(s -> System.out.println("  " + s.getStaffId() + "\t" + s.getStaffName() + "\t" + s.getOrganId()));
		assertEquals(3, active.size());

		List<StaffDoc> inOrgan = (List<StaffDoc>) lightDao.mongo().sql("sc_mongo_staff")
				.names("status", "organIds").values(1, new String[] { "100005" }).resultType(StaffDoc.class).find();
		System.out.println("mongo active in 100005: " + inOrgan.size());
		assertEquals(2, inOrgan.size());

		Page<?> page = lightDao.mongo().sql("sc_mongo_staff").names("status").values(1)
				.resultType(StaffDoc.class).findPage(new Page(2, 1));
		System.out.println("mongo page1 rows=" + page.getRows().size() + " total=" + page.getRecordCount());
		assertEquals(2, page.getRows().size());
		assertEquals(3, page.getRecordCount());
	}

	@Test
	@SuppressWarnings("unchecked")
	void elasticsearchJsonQuery() throws Exception {
		seedEsIndex();
		// json REST query with a dynamic <#> segment: status filters, organId joins when set
		List<StaffDoc> active = (List<StaffDoc>) lightDao.elastic().sql("sc_es_staff_json")
				.names("status").values(1).resultType(StaffDoc.class).find();
		System.out.println("es active staff: " + active.size());
		active.forEach(s -> System.out.println("  " + s.getStaffId() + "\t" + s.getStaffName() + "\t" + s.getOrganId()));
		assertEquals(3, active.size());

		List<StaffDoc> inOrgan = (List<StaffDoc>) lightDao.elastic().sql("sc_es_staff_json")
				.names("status", "organId").values(1, "100007").resultType(StaffDoc.class).find();
		System.out.println("es active in 100007: " + inOrgan.size());
		assertEquals(1, inOrgan.size());
	}

	private void seedEsIndex() throws Exception {
		HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
		String base = "http://" + ES.getHost() + ":" + ES.getMappedPort(9200);
		http.send(HttpRequest.newBuilder(URI.create(base + "/staff_info")).PUT(HttpRequest.BodyPublishers.ofString("{}"))
				.build(), HttpResponse.BodyHandlers.ofString());
		String bulk = """
				{"index":{"_index":"staff_info","_id":"1"}}
				{"staffId":"S0001","staffName":"Alice Chen","organId":"100005","status":1}
				{"index":{"_index":"staff_info","_id":"2"}}
				{"staffId":"S0002","staffName":"Bob Zhang","organId":"100005","status":1}
				{"index":{"_index":"staff_info","_id":"3"}}
				{"staffId":"S0003","staffName":"Carol Wang","organId":"100007","status":1}
				{"index":{"_index":"staff_info","_id":"4"}}
				{"staffId":"S0004","staffName":"David Li","organId":"100001","status":0}
				""";
		http.send(HttpRequest.newBuilder(URI.create(base + "/_bulk?refresh=true"))
				.header("Content-Type", "application/x-ndjson")
				.POST(HttpRequest.BodyPublishers.ofString(bulk)).build(), HttpResponse.BodyHandlers.ofString());
		for (int i = 0; i < 30; i++) {
			Thread.sleep(500);
			String cnt = http.send(HttpRequest.newBuilder(URI.create(base + "/staff_info/_count")).GET().build(),
					HttpResponse.BodyHandlers.ofString()).body();
			if (cnt.contains("count\":4")) {
				return;
			}
		}
		assertTrue(false, "elasticsearch documents not indexed in time");
	}
}
