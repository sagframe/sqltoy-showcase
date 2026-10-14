package com.sqltoy.showcase.plugins;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Stored-procedure bodies for the H2 CREATE ALIAS demos.
 *
 * - listStaff: a single result set
 * - multiResults: returns ResultSet[], which H2 exposes as MULTIPLE result sets —
 *   consumed through StoreResult.getMoreResults() (the "moreResult" concept)
 */
public class Procedures {

	/** single result set */
	public static ResultSet listStaff(Connection conn) throws SQLException {
		return conn.createStatement().executeQuery(
				"select STAFF_ID,STAFF_NAME,ORGAN_ID from SQLTOY_STAFF_INFO where STATUS=1 order by STAFF_ID");
	}

	/** multiple result sets: organs + a staff count */
	public static ResultSet[] multiResults(Connection conn) throws SQLException {
		return new ResultSet[] {
				conn.createStatement().executeQuery(
						"select ORGAN_ID,ORGAN_NAME from SQLTOY_ORGAN_INFO order by ORGAN_ID"),
				conn.createStatement().executeQuery(
						"select count(*) CNT from SQLTOY_STAFF_INFO where STATUS=1") };
	}
}
