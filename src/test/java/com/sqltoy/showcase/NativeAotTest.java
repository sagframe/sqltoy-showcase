package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplicationAotProcessor;
import org.springframework.context.aot.AbstractAotProcessor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * GraalVM native-image readiness — verified on a plain JVM, no GraalVM required.
 *
 * Runs the exact same Spring Boot AOT processing that `mvn -Pnative native:compile`
 * consumes (SpringApplicationAotProcessor) and asserts the generated native-image
 * metadata covers everything sqltoy needs at runtime:
 *  - reflection for the entity classes: entities are parsed lazily on first use and
 *    Spring AOT processing does not instantiate/bind beans, so the sqltoy starter
 *    scans spring.sqltoy.packagesToScan at build time and registers them itself
 *  - reflection for the class-name configured extensions (unifyFieldsHandler /
 *    typeHandler / dynamicCacheFetch), which sqltoy instantiates via Class.forName at
 *    startup — the starter registers them from the configuration
 *  - resource inclusion for showcase.sql.xml (registered by the starter from
 *    spring.sqltoy.sqlResourcesDir) and the mock/ schema + RSA key files (mock/**
 *    from ShowcaseRuntimeHints; the keys also via spring.sqltoy.secure*) — at native
 *    runtime sqltoy loads sqlResourcesDir as classpath resources, so the files must
 *    be in the image
 *
 * The real native compilation + binary smoke run lives in the native-build GitHub
 * workflow (needs GraalVM, too heavy for the regular test suite).
 */
class NativeAotTest {

	@Test
	void aotProcessingProducesUsableNativeImageMetadata() throws Exception {
		Path out = Files.createTempDirectory("showcase-aot-");
		try {
			AbstractAotProcessor.Settings settings = AbstractAotProcessor.Settings.builder()
					.sourceOutput(out.resolve("sources")).resourceOutput(out.resolve("resources"))
					.classOutput(out.resolve("classes")).groupId("com.sqltoy.showcase")
					.artifactId("sqltoy-showcase").build();
			new SpringApplicationAotProcessor(ShowcaseApplication.class, settings, new String[0]).process();

			// the AOT flag must not leak into other tests sharing this JVM
			assertNull(System.getProperty("spring.aot.processing"),
					"spring.aot.processing must be cleared after AOT processing");

			// Framework 7 writes the unified GraalVM reachability-metadata.json
			// (instead of the legacy reflect-config.json / resource-config.json pair)
			JsonNode metadata = new ObjectMapper().readTree(readGenerated(out, "reachability-metadata.json").toFile());
			Set<String> reflection = collect(metadata, "reflection", "type");
			Set<String> resourceGlobs = collect(metadata, "resources", "glob");

			// entities registered from the packagesToScan package at build time
			assertTrue(reflection.contains("com.sqltoy.showcase.entity.StaffInfo"),
					"StaffInfo reflection missing, sqltoy-related entries: " + sqltoyEntries(reflection));
			assertTrue(reflection.contains("com.sqltoy.showcase.entity.TransLog"), "TransLog reflection missing");
			// class-name configured sqltoy extensions
			assertTrue(reflection.contains("com.sqltoy.showcase.plugins.ShowcaseUnifyFieldsHandler"),
					"unifyFieldsHandler reflection missing");
			assertTrue(reflection.contains("com.sqltoy.showcase.plugins.MoneyTypeHandler"),
					"typeHandler reflection missing");
			assertTrue(reflection.contains("com.sqltoy.showcase.plugins.BigDictDynamicCacheFetch"),
					"dynamicCacheFetch reflection missing");
			// sql files and mock resources must be baked into the image
			assertTrue(resourceGlobs.contains("com/sqltoy/showcase/**"),
					"showcase.sql.xml resource glob missing, globs: " + resourceGlobs);
			assertTrue(resourceGlobs.contains("mock/**"), "mock/ resource glob missing");
		} finally {
			deleteRecursively(out);
		}
	}

	/** collects a string field from each entry of a top-level array section ("type" may be a string or an object) */
	private static Set<String> collect(JsonNode metadata, String section, String field) {
		Set<String> values = new HashSet<>();
		for (JsonNode entry : metadata.path(section)) {
			JsonNode node = entry.path(field);
			if (node.isTextual()) {
				values.add(node.asText());
			} else if (node.isObject()) {
				values.add(node.path("name").asText());
			}
		}
		return values;
	}

	private static String sqltoyEntries(Set<String> reflection) {
		return reflection.stream().filter(name -> name.contains("sqltoy")).sorted()
				.reduce("", (a, b) -> a + "\n  " + b);
	}

	private Path readGenerated(Path out, String fileName) throws IOException {
		try (Stream<Path> paths = Files.walk(out)) {
			Path file = paths.filter(p -> p.getFileName().toString().equals(fileName)).findFirst().orElse(null);
			assertNotNull(file, fileName + " was not generated under " + out);
			return file;
		}
	}

	private void deleteRecursively(Path root) throws IOException {
		if (!Files.exists(root)) {
			return;
		}
		try (Stream<Path> paths = Files.walk(root)) {
			paths.sorted(Comparator.reverseOrder()).forEach(p -> {
				try {
					Files.deleteIfExists(p);
				} catch (IOException ignored) {
					// best-effort cleanup of a temp directory
				}
			});
		}
	}
}
