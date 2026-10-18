package com.sqltoy.showcase.config;

import com.sqltoy.showcase.plugins.Procedures;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * GraalVM native-image metadata for what only THIS application can know.
 *
 * Everything configured through spring.sqltoy.* is registered by the sqltoy starter
 * itself since 6.0.8: entity reflection (scanned from packagesToScan at build time),
 * the sqlResourcesDir / translateConfig / secure-key resource patterns, the class-name
 * configured extensions (unifyFieldsHandler / typeHandler) and the effective
 * JsonProvider — see NativeAotTest for the proof. What is left is application-owned:
 *
 *  - mock/** : the schema scripts that DataSourceConfig loads itself via
 *    ResourceDatabasePopulator (the RSA keys under mock/ are covered twice — they are
 *    also spring.sqltoy.securePublicKey/PrivateKey values, which the starter registers)
 *  - Procedures : referenced BY NAME from H2's CREATE ALIAS in mock/schema-ds0.sql,
 *    so H2 loads it via Class.forName at startup — no framework can infer that
 *
 * Rule of thumb: whatever spring.sqltoy.* knows about, 6.0.8 registers for you;
 * whatever your own code or your SQL scripts load by name/path, you register.
 *
 * Inert on a regular JVM: hints are only consumed by Spring AOT processing
 * (mvn -Pnative native:compile).
 */
public class ShowcaseRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.resources().registerPattern("mock/**");
		hints.reflection().registerType(Procedures.class, MemberCategory.values());
	}
}
