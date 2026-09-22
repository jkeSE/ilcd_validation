package com.okworx.ilcd.validation.util;

import java.io.IOException;
import java.net.URL;

/**
 * <p>Locator interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface Locator {

	/**
	 * <p>resolve.</p>
	 *
	 * @param url a {@link java.net.URL} object.
	 * @return a {@link java.net.URL} object.
	 * @throws java.io.IOException if any.
	 */
	URL resolve(URL url) throws IOException;

}
