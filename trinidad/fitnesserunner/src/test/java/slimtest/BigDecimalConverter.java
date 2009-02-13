package slimtest;

import java.math.BigDecimal;

public class BigDecimalConverter implements fitnesse.slim.Converter{
	public Object fromString(String value) {
		return new BigDecimal(value);
	}
	public String toString(Object o) {
		return o.toString();
	}
}
