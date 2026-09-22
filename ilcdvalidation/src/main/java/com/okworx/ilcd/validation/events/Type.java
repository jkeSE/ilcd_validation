package com.okworx.ilcd.validation.events;

/**
 * <p>Type class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public enum Type {

	GENERIC("GENERIC"),

	SPECIFIC("SPECIFIC");

	/** The value. */
	private final String value;

	/**
	 * Instantiates a new type of process value.
	 *
	 * @param v
	 *            the value
	 */
	Type(String v) {
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
	public static Type fromValue(String v) {
		for (Type c : Type.values()) {
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
