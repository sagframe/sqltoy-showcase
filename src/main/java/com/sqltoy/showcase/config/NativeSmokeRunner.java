package com.sqltoy.showcase.config;

import java.util.HashMap;

import org.sagacity.sqltoy.dao.LightDao;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sqltoy.showcase.entity.StaffInfo;

/**
 * Opt-in smoke run for the native binary (works on the JVM jar too): start with `--smoke`
 * and it exercises the runtime paths a native image is most likely to break on —
 * entity load (reflection-based materialization), a literal-SQL count (JDBC + dialect)
 * and the translate cache (SQL-cache load) — then prints SMOKE OK / SMOKE FAILED.
 *
 * Always registered as a bean so Spring AOT bakes it into the native image, but inert
 * unless the `smoke` property is present; ApplicationRunners never execute in
 * {@code @SpringBootTest}, so regular `mvn test` is unaffected.
 */
@Component
public class NativeSmokeRunner implements ApplicationRunner {

	private final LightDao lightDao;

	private final Environment environment;

	public NativeSmokeRunner(LightDao lightDao, Environment environment) {
		this.lightDao = lightDao;
		this.environment = environment;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!environment.containsProperty("smoke")) {
			return;
		}
		try {
			StaffInfo probe = new StaffInfo();
			probe.setStaffId("S0001");
			StaffInfo loaded = lightDao.load(probe);
			Long staffCount = lightDao.getCount("select count(*) from SQLTOY_STAFF_INFO", new HashMap<>());
			HashMap<String, Object[]> translateCache = lightDao.getTranslateCache("staffIdName", null);
			System.out.println("SMOKE OK: loadedStaff=" + (loaded == null ? "null" : loaded.getStaffName())
					+ ", staffCount=" + staffCount + ", translateCacheSize="
					+ (translateCache == null ? -1 : translateCache.size()));
		} catch (Throwable e) {
			System.out.println("SMOKE FAILED: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
