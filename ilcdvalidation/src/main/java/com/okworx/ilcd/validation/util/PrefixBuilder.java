package com.okworx.ilcd.validation.util;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>PrefixBuilder class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class PrefixBuilder {
	/**
	 * <p>buildPrefix.</p>
	 *
	 * @param pathToJar a {@link java.lang.String} object.
	 * @param resourcePath a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String buildPrefix(String pathToJar, String resourcePath) {
		// build "jar:file:" + pathToJar + "!/" + resourcePath;
		StringBuffer buf = new StringBuffer(buildPath(pathToJar, resourcePath));
		if (StringUtils.isNotBlank(pathToJar) && StringUtils.isNotBlank(resourcePath) && !resourcePath.endsWith("/"))
			buf.append("/");
		return buf.toString();
	}

	/**
	 * <p>buildPath.</p>
	 *
	 * @param pathToJar a {@link java.lang.String} object.
	 * @param resourcePath a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String buildPath(String pathToJar, String resourcePath) {
		// build "jar:file:" + pathToJar + "!/" + resourcePath;
		StringBuffer buf = new StringBuffer();
		if (!pathToJar.startsWith("file:"))
			buf.append("jar:file:");
		else
			buf.append("jar:");
		buf.append(pathToJar);
		buf.append("!/");
		buf.append(resourcePath);
		return buf.toString();
	}
}
