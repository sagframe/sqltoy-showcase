package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;
import org.sagacity.sqltoy.config.annotation.Secure;
import org.sagacity.sqltoy.config.annotation.SecureConfig;
import org.sagacity.sqltoy.model.SecureType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Field-encryption demo entity.
 *
 * @SecureConfig works on save/load:
 *  - telNo/address are stored ENCRYPTED (RSA key pair configured in application.yml);
 *    loading the entity decrypts them back automatically.
 *  - telMask is derived from telNo as a masked SEARCH INDEX — ciphertext cannot be
 *    searched with LIKE, so queries filter on the mask instead.
 */
@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_SECURE_CASE")
@SecureConfig(secures = {
		@Secure(field = "telNo", secureType = SecureType.ENCRYPT),
		@Secure(field = "address", secureType = SecureType.ENCRYPT),
		@Secure(field = "telMask", secureType = SecureType.TEL, sourceField = "telNo") })
public class SecureCase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "STAFF_ID", length = 22)
	private String staffId;

	@Column(name = "STAFF_NAME", length = 30)
	private String staffName;

	@Column(name = "TEL_NO", length = 500)
	private String telNo;

	@Column(name = "TEL_MASK", length = 30)
	private String telMask;

	@Column(name = "ADDRESS", length = 500)
	private String address;

	@Column(name = "CREATE_TIME")
	private LocalDateTime createTime;

	@Column(name = "STATUS")
	private Integer status;
}
