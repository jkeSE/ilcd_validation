package com.okworx.ilcd.validation.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.common.Constants;

import net.java.truevfs.access.TFileInputStream;

/**
 * <p>CURIResolver class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class CURIResolver implements URIResolver {

	/** Constant <code>log</code> */
	protected static Logger log = org.apache.logging.log4j.LogManager.getLogger(CURIResolver.class);

	private String urlPrefix;

	private String localBase;

	/**
	 * <p>Getter for the field <code>urlPrefix</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUrlPrefix() {
		return urlPrefix;
	}

	/**
	 * <p>Setter for the field <code>urlPrefix</code>.</p>
	 *
	 * @param urlPrefix a {@link java.lang.String} object.
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	private Map<String, String> mappings = new HashMap<String, String>();

	/**
	 * <p>Constructor for CURIResolver.</p>
	 *
	 * @param urlPrefix a {@link java.lang.String} object.
	 */
	public CURIResolver(String urlPrefix) {
		this(urlPrefix, null);
	}

	/**
	 * <p>Constructor for CURIResolver.</p>
	 *
	 * @param urlPrefix a {@link java.lang.String} object.
	 * @param localBase a {@link java.lang.String} object.
	 */
	public CURIResolver(String urlPrefix, String localBase) {
		this(urlPrefix, null, null);
	}

	/**
	 * <p>Constructor for CURIResolver.</p>
	 *
	 * @param urlPrefix a {@link java.lang.String} object.
	 * @param localBase a {@link java.lang.String} object.
	 * @param mappings a {@link java.util.Map} object.
	 */
	public CURIResolver(String urlPrefix, String localBase, Map<String, String> mappings) {
		if (log.isDebugEnabled()) {
			log.debug("instantiating CURIResolver with prefix " + urlPrefix + " and localBase " + localBase);
			if (mappings != null) {
				log.debug(" resolver mappings:");
				for (String key : mappings.keySet())
					log.debug(" " + key + " - " + mappings.get(key));
			}
		}
		this.urlPrefix = urlPrefix;
		this.localBase = localBase;
		this.mappings = mappings;
	}

	/**
	 * <p>registerMapping.</p>
	 *
	 * @param suffix a {@link java.lang.String} object.
	 * @param result a {@link java.lang.String} object.
	 */
	public void registerMapping(String suffix, String result) {
		this.mappings.put(suffix, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.transform.URIResolver#resolve(java.lang.String,
	 * java.lang.String)
	 */
	/** {@inheritDoc} */
	public Source resolve(String href, String base) throws TransformerException {
		log.debug("trying to resolve href " + href + ", base " + base);
		String newHRef = "";
		
		try {
			if (href.startsWith(this.urlPrefix)) {
				// if the URL is already pointing to the correct place, there's nothing to do here
				return new StreamSource(href);
			} else if (href.equalsIgnoreCase("../../stylesheets/common.xsl") || href.equalsIgnoreCase("../../stylesheets/common-1.0.xsl")) {
				newHRef = urlPrefix + "common.xsl";
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(".xsl") || href.endsWith("_Reference.xml") || href.endsWith("reference_types.xml") || href.endsWith("reference_types-1.0.xml")) {
				newHRef = urlPrefix + href;
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(Constants.DEFAULT_ILCD_CATEGORIES_NAME)) {
				newHRef = urlPrefix + href.replaceAll("\\.\\.\\/", "");
				// if we're using different L&C, resolve these
				// TODO: resolve actual L&C and use default ones if not
				// available
				// if (cl.getResource(newHRef) == null) {
				newHRef = urlPrefix + Constants.REFERENCE_CATEGORIES_FILE_NAME;
				// }
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(Constants.DEFAULT_ILCD_FLOW_CATEGORIES_NAME)) {
				newHRef = urlPrefix + href.replaceAll("\\.\\.\\/", "");
				// if we're using different L&C, resolve these
				// TODO: resolve actual L&C and use default ones if not
				// available
				// if (cl.getResource(newHRef) == null) {
				newHRef = urlPrefix + Constants.REFERENCE_FLOW_CATEGORIES_FILE_NAME;
				// }
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(Constants.DEFAULT_ILCD_LOCATIONS_NAME)) {
				newHRef = urlPrefix + href.replaceAll("\\.\\.\\/", "");
				// if we're using different L&C, resolve these
				// TODO: resolve actual L&C and use default ones if not
				// available
				// if (cl.getResource(newHRef) == null) {
				newHRef = urlPrefix + Constants.REFERENCE_LOCATIONS_FILE_NAME;
				// }
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(Constants.DEFAULT_ILCD_LCIA_METHODOLOGIES_NAME)) {
				newHRef = urlPrefix + href.replaceAll("\\.\\.\\/", "");
				// if we're using different L&C, resolve these
				// TODO: resolve actual L&C and use default ones if not
				// available
				// if (cl.getResource(newHRef) == null) {
				newHRef = urlPrefix + Constants.REFERENCE_LCIA_METHODOLOGIES_FILE_NAME;
				// }
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.endsWith(Constants.REFERENCE_CATEGORIES_FILE_NAME)) {
				newHRef = urlPrefix + href;
				log.debug("returning " + newHRef);
				return new StreamSource(Constants.REFERENCE_CATEGORIES);
			} else if (href.contains("../schemas/") || href.contains("../sources/")) {
				newHRef = Constants.ILCD_PATH_PREFIX + href.replaceAll("\\.\\.\\/", "");
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (href.startsWith("../categories/")) {
				// use base URI to resolve
				log.debug("resolving using provided base URI");
				String baseRef = StringUtils.substringBeforeLast(base, "/");
				baseRef = StringUtils.substringBeforeLast(baseRef, "/"); 
				newHRef = baseRef.concat("/").concat(StringUtils.substringAfter(href, "../"));
				log.debug("returning " + newHRef);
				return new StreamSource(newHRef);
			} else if (!this.mappings.isEmpty()) {
				log.debug("resolving using mappings...");
				for (String suffix : mappings.keySet()) {
					if (href.endsWith(suffix)) {
						log.debug("using mapping ".concat(suffix));
						newHRef = mappings.get(suffix);
						log.debug("returning " + newHRef);
						return new StreamSource(newHRef);
					}
				}
				log.debug("no suitable mapping found, unknown href:" + href);
				return new StreamSource(new BOMInputStream(new TFileInputStream(newHRef)));
			} else if (localBase != null) {
				log.debug("using localBase ".concat(localBase));
				newHRef = localBase.concat(href);
				log.debug("returning " + newHRef);
				return new StreamSource(new BOMInputStream(new TFileInputStream(newHRef)));
			} else {
				log.debug("unknown href:" + href);
				return new StreamSource(new BOMInputStream(new TFileInputStream(newHRef)));
			}
		} catch (Exception e) {
			log.info(e);
			throw new TransformerException(e);
		}
	}

	/**
	 * <p>Getter for the field <code>mappings</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, String> getMappings() {
		return mappings;
	}

	/**
	 * <p>Setter for the field <code>mappings</code>.</p>
	 *
	 * @param mappings a {@link java.util.Map} object.
	 */
	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
}
