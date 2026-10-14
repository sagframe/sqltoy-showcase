package com.sqltoy.showcase.entity;

import lombok.Getter;

/**
 * Enum with a getKey() method: sqltoy detects the key method and stores the KEY
 * ("A"/"S") instead of the name — database codes stay compact while the Java side
 * stays a typed enum.
 */
@Getter
public enum WorkState {

	ACTIVE("A"), SUSPENDED("S");

	private final String code;

	WorkState(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
