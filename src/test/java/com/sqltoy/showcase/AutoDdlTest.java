package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.plugins.ddl.DDLFactory;
import org.sagacity.sqltoy.utils.DataSourceUtils.DBType;

/**
 * autoDDL: generate CREATE TABLE scripts from the @Entity POJOs themselves —
 * the reverse direction of quickvo (database -> code).
 */
class AutoDdlTest {

	@Test
	void generatesCreateTableScriptsFromEntities() throws Exception {
		Path out = Files.createTempFile("showcase-ddl-", ".sql");
		DDLFactory.createSqlFile(new String[] { "com.sqltoy.showcase.entity" }, out.toString(),
				null, DBType.H2, null, null);
		String ddl = new String(Files.readAllBytes(out));
		System.out.println("generated DDL (first lines):");
		ddl.lines().limit(8).forEach(l -> System.out.println("  " + l));
		assertTrue(ddl.contains("SQLTOY_STAFF_INFO"), "staff table DDL must be generated");
		assertTrue(ddl.toUpperCase().contains("CREATE TABLE"));
		Files.deleteIfExists(out);
	}

	@SuppressWarnings("unused")
	private File unused() {
		return null;
	}

	@SuppressWarnings("unused")
	private IOException unused2() {
		return null;
	}
}
