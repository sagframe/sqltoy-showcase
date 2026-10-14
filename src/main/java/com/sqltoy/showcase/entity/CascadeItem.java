package com.sqltoy.showcase.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.sagacity.sqltoy.config.annotation.Column;
import org.sagacity.sqltoy.config.annotation.Entity;
import org.sagacity.sqltoy.config.annotation.Id;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity(tableName = "SQLTOY_CASCADE_ITEM")
public class CascadeItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID", length = 32)
	private String id;

	@Column(name = "TRANS_DATE")
	private LocalDate transDate;

	@Column(name = "TRANS_ID", length = 30)
	private String transId;

	@Column(name = "PRODUCT_ID", length = 32)
	private String productId;

	@Column(name = "QUANTITY", precision = 8, scale = 3)
	private BigDecimal quantity;

	@Column(name = "PRICE", precision = 8, scale = 2)
	private BigDecimal price;

	@Column(name = "AMT", precision = 10, scale = 2)
	private BigDecimal amt;

	@Column(name = "CREATE_BY", length = 22)
	private String createBy;

	@Column(name = "CREATE_TIME")
	private LocalDateTime createTime;

	@Column(name = "STATUS")
	private Integer status;
}
