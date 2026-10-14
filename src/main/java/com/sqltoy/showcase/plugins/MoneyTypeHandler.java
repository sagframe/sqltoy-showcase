package com.sqltoy.showcase.plugins;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.sagacity.sqltoy.plugins.TypeHandler;

/**
 * Custom TypeHandler demo: maps the {@link Money} value object onto a varchar column
 * in both directions. Registered through spring.sqltoy.typeHandler in application.yml;
 * every conversion it does not recognize simply returns false / null, leaving the
 * framework's default handling (including the built-in JSON support) untouched.
 */
public class MoneyTypeHandler extends TypeHandler {

	/**
	 * write path: Money -> "USD:150000.00"
	 */
	@Override
	public boolean setValue(Integer dbType, PreparedStatement pst, int paramIndex, int jdbcType, Object value)
			throws SQLException {
		if (value instanceof Money) {
			pst.setString(paramIndex, value.toString());
			return true;
		}
		return false;
	}

	/**
	 * read path: "USD:150000.00" -> Money, when the target property type is Money
	 */
	@Override
	public Object toJavaType(String javaTypeName, Class genericType, Object jdbcValue) throws Exception {
		if (jdbcValue != null && javaTypeName.endsWith(".Money")) {
			return Money.parse(String.valueOf(jdbcValue));
		}
		return null;
	}
}
