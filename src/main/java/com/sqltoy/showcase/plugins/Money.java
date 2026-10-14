package com.sqltoy.showcase.plugins;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A custom domain type for the TypeHandler demo: a monetary amount with its currency,
 * stored in the database as a single varchar "USD:150000.00" via TagMoneyTypeHandler.
 */
public class Money {

	private final String currency;
	private final BigDecimal amount;

	public Money(String currency, BigDecimal amount) {
		this.currency = currency;
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return currency + ":" + amount.toPlainString();
	}

	public static Money parse(String text) {
		int cut = text.indexOf(':');
		return new Money(text.substring(0, cut), new BigDecimal(text.substring(cut + 1)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Money)) {
			return false;
		}
		Money money = (Money) o;
		return currency.equals(money.currency) && amount.compareTo(money.amount) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(currency, amount.stripTrailingZeros());
	}
}
