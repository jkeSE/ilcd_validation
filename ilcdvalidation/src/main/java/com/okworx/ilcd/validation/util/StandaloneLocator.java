package com.okworx.ilcd.validation.util;

import java.net.URL;

/**
 * <p>StandaloneLocator class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class StandaloneLocator implements Locator {

	/** {@inheritDoc} */
	public URL resolve(URL url) {
		return url;
	}

}
