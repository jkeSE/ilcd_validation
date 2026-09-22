package com.okworx.ilcd.validation.util;

import org.apache.logging.log4j.Logger;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 * <p>TransletCache class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class TransletCache {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger( this.getClass() );

	private static final TransletCache instance = new TransletCache();

	private Templates translet;

	/**
	 * <p>Getter for the field <code>translet</code>.</p>
	 *
	 * @return a {@link javax.xml.transform.Templates} object.
	 */
	public Templates getTranslet() {
		return this.translet;
	}

	/**
	 * <p>Constructor for TransletCache.</p>
	 */
	protected TransletCache() {
		TransformerFactory fact = TransformerFactory.newInstance();
		try {
			this.translet = fact.newTemplates(new StreamSource(this.getClass()
					.getClassLoader()
					.getResourceAsStream("stylesheets/extractReferences.xsl")));
		} catch (TransformerConfigurationException e) {
			log.error(e);
		}
	}

	/**
	 * <p>Getter for the field <code>instance</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.TransletCache} object.
	 */
	public static TransletCache getInstance() {
		return instance;
	}
}
