package com.okworx.ilcd.validation.common;

import java.util.Locale;

/**
 * <p>Abstract ValidationContext class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public abstract class ValidationContext implements IValidationContext {

	protected Locale locale;

	/**
	 * <p>Getter for the field <code>locale</code>.</p>
	 *
	 * @return a {@link java.util.Locale} object.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * <p>Setter for the field <code>locale</code>.</p>
	 *
	 * @param locale a {@link java.util.Locale} object.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}
