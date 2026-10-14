package com.sqltoy.showcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.sagacity.sqltoy.dao.LightDao;
import org.sagacity.sqltoy.model.MapKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sqltoy.showcase.entity.GeoPlace;
import com.sqltoy.showcase.entity.StaffLevel;
import com.sqltoy.showcase.entity.WorkState;
import com.sqltoy.showcase.entity.StaffExt;

/**
 * Special column types on H2:
 *
 *  - JSON:     List/Map properties map to the JSON column in both directions
 *              (sqltoy's built-in handling, JdbcTypes.JSON); H2 json functions work on the column
 *  - GEOMETRY: a JTS Geometry property maps to the GEOMETRY column (WKT/WKB by sqltoy);
 *              spatial predicates (ST_Contains, ST_Distance) run directly in SQL
 *  - VECTOR:   pgvector-specific — see the note at the bottom of this class
 */
@SpringBootTest(classes = ShowcaseApplication.class)
class JsonGeometryTest {

	@Autowired
	LightDao lightDao;

	@Test
	void jsonColumnsRoundTripListsAndMaps() throws Exception {
		StaffExt ext = new StaffExt().setStaffId("JG-01")
				.setSkills(List.of("Java", "SQL", "Kubernetes"))
				.setProfile(Map.of("level", "senior", "years", 8))
				.setStatus(1);
		lightDao.save(ext);

		// raw storage is real JSON text
		String raw = (String) rawQuery("select SKILLS from SQLTOY_STAFF_EXT where STAFF_ID='JG-01'");
		System.out.println("raw SKILLS column: " + raw);
		assertTrue(raw.contains("Kubernetes"));

		// load maps JSON back into List / Map properties
		StaffExt loaded = lightDao.load(new StaffExt().setStaffId("JG-01"));
		System.out.println("skills=" + loaded.getSkills() + ", level=" + loaded.getProfile().get("level"));
		assertEquals(3, loaded.getSkills().size());
		assertEquals("senior", loaded.getProfile().get("level"));

		// JSON path queries inside SQL depend on the database function set:
		// PostgreSQL/MySQL:  where PROFILE->>'level' = :level   /   JSON_EXTRACT(PROFILE,'$.level')
		// H2 2.x ships no JSON path functions, so query-side filtering here would be a LIKE on the text
	}

	@Test
	void geometryColumnRoundTripAndSpatialQueries() throws Exception {
		Geometry hq = new WKTReader().read("POINT(121.47 31.23)");
		Geometry campus = new WKTReader().read("POLYGON((121.40 31.20, 121.50 31.20, 121.50 31.28, 121.40 31.28, 121.40 31.20))");
		lightDao.save(new GeoPlace().setPlaceId("G-01").setPlaceName("HQ").setLocation(hq).setStatus(1));
		lightDao.save(new GeoPlace().setPlaceId("G-02").setPlaceName("Campus").setLocation(campus).setStatus(1));

		// load returns JTS Geometry objects
		GeoPlace loaded = lightDao.load(new GeoPlace().setPlaceId("G-01"));
		System.out.println("loaded geometry: " + loaded.getLocation().toText());
		assertEquals("POINT (121.47 31.23)", loaded.getLocation().toText());

		// spatial analysis on the loaded JTS Geometry (H2 has no ST_* functions;
		// on PostGIS you would run ST_Contains(LOCATION, :point) directly in SQL)
		Geometry campusShape = lightDao.load(new GeoPlace().setPlaceId("G-02")).getLocation();
		boolean contains = campusShape.contains(new WKTReader().read("POINT(121.45 31.24)"));
		System.out.println("campus polygon contains (121.45,31.24): " + contains);
		assertTrue(contains);
	}

	/*
	 * VECTOR (pgvector): sqltoy maps vector columns through JdbcTypes.VECTOR and
	 * wraps parameters as PGobject with 'cast(? as vector)' — PostgreSQL with the
	 * pgvector extension only, so it cannot run on H2:
	 *
	 *   @Column(name = "EMBEDDING", type = JdbcTypes.VECTOR)
	 *   private float[] embedding;   // or PGvector / String "[0.1,0.2,...]"
	 *
	 *   select * from items order by EMBEDDING <-> :queryVector limit 10;
	 */
	@Test
	void vectorColumnTypeIsPgvectorSpecific() {
		org.junit.jupiter.api.Assumptions.assumeTrue(false,
				"VECTOR columns need PostgreSQL + pgvector; see the comment above for the mapping");
	}

	@Test
	void enumColumnsConvertBothWays() throws Exception {
		StaffExt ext = new StaffExt().setStaffId("JG-02").setSkills(List.of("Go"))
				.setLevel(StaffLevel.SENIOR).setState(WorkState.SUSPENDED).setStatus(1);
		lightDao.save(ext);

		// storage: plain enum -> name(), getKey enum -> the key value
		Object[] raw = (Object[]) rawTwo("select LEVEL,STATE from SQLTOY_STAFF_EXT where STAFF_ID='JG-02'");
		System.out.println("raw LEVEL=" + raw[0] + ", raw STATE=" + raw[1]);
		assertEquals("SENIOR", raw[0]);
		assertEquals("S", raw[1]);

		// load: values convert back into the enum properties
		StaffExt loaded = lightDao.load(new StaffExt().setStaffId("JG-02"));
		System.out.println("level=" + loaded.getLevel() + ", state=" + loaded.getState());
		assertEquals(StaffLevel.SENIOR, loaded.getLevel());
		assertEquals(WorkState.SUSPENDED, loaded.getState());
	}

	private Object rawTwo(String sql) {
		return org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), lightDao.getDataSource(),
				new org.sagacity.sqltoy.callback.DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
							rs.next();
							setResult(new Object[] { rs.getString(1), rs.getString(2) });
						}
					}
				});
	}

	private Object rawQuery(String sql) {
		return org.sagacity.sqltoy.utils.DataSourceUtils.processDataSource(
				lightDao.getSqlToyContext(), lightDao.getDataSource(),
				new org.sagacity.sqltoy.callback.DataSourceCallbackHandler() {
					@Override
					public void doConnection(Connection conn, org.sagacity.sqltoy.model.DBProfile dbProfile)
							throws Exception {
						try (ResultSet rs = conn.createStatement().executeQuery(sql)) {
							rs.next();
							setResult(rs.getString(1));
						}
					}
				});
	}
}
