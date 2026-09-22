package com.okworx.ilcd.validation.common;

import java.util.Locale;

/**
 * <p>CustomValidationContext class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class CustomValidationContext extends ValidationContext {

	/**
	 * <p>Constructor for CustomValidationContext.</p>
	 */
	public CustomValidationContext() {
		this.locale = Locale.getDefault();
	}

	/**
	 * <p>Constructor for CustomValidationContext.</p>
	 *
	 * @param locale a {@link java.util.Locale} object.
	 */
	public CustomValidationContext(Locale locale) {
		this.locale = locale;
	}

}
