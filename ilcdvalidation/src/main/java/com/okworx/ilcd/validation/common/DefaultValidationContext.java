package com.okworx.ilcd.validation.common;

import java.util.Locale;

/**
 * <p>DefaultValidationContext class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class DefaultValidationContext extends ValidationContext {

	/**
	 * <p>Constructor for DefaultValidationContext.</p>
	 */
	public DefaultValidationContext() {
		this.locale = Locale.getDefault();
	}

}
