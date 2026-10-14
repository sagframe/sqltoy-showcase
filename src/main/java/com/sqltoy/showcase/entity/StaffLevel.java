package com.sqltoy.showcase.entity;

/**
 * Plain enum (no custom fields): sqltoy stores {@code name()} and converts back
 * with valueOf — no mapping code on either side.
 */
public enum StaffLevel {
	JUNIOR, SENIOR, EXPERT
}
