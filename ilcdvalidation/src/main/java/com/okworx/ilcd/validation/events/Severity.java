package com.okworx.ilcd.validation.events;

/**
 * <p>Severity class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public enum Severity {

	SUCCESS("SUCCESS"),

	WARNING("WARNING"),

	ERROR("ERROR"),

	FATAL("FATAL");

	/** The value. */
	private final String value;

	/**
	 * Instantiates a new type of process value.
	 *
	 * @param v
	 *            the value
	 */
	Severity(String v) {
		value = v;
	}

	/**
	 * Value.
	 *
	 * @return the string
	 */
	public String value() {
		return value;
	}

	/**
	 * From value.
	 *
	 * @param v
	 *            the value
	 * @return the type of process value
	 */
	public static Severity fromValue(String v) {
		for (Severity c : Severity.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
