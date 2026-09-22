package com.okworx.ilcd.validation.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.Logger;

/**
 * <p>ValidatorListener class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ValidatorListener implements ErrorListener {

	public static final String VALIDATION_ERROR_PREFIX = "Validation error: ";
	public static final String VALIDATION_WARNING_PREFIX = "Validation warning: ";

	/** Constant <code>log</code> */
	protected static Logger log = org.apache.logging.log4j.LogManager.getLogger(ValidatorListener.class);

	private final List<String> results = new ArrayList<>();

	private final List<String> warnings = new ArrayList<>();

	private final List<String> errors = new ArrayList<>();

	/**
	 * <p>Getter for the field <code>results</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getResults() {
		return results;
	}

	/**
	 * <p>Getter for the field <code>errors</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * <p>Getter for the field <code>warnings</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<String> getWarnings() {
		return warnings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.xml.transform.ErrorListener#error(javax.xml.transform.
	 * TransformerException)
	 */
	/** {@inheritDoc} */
	public void error(TransformerException arg0) throws TransformerException {
		logEvent(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.xml.transform.ErrorListener#fatalError(javax.xml.transform.
	 * TransformerException)
	 */
	/** {@inheritDoc} */
	public void fatalError(TransformerException arg0) throws TransformerException {
		logEvent(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.xml.transform.ErrorListener#warning(javax.xml.transform.
	 * TransformerException)
	 */
	/** {@inheritDoc} */
	public void warning(TransformerException arg0) throws TransformerException {
		logEvent(arg0);
	}

	/**
	 * <p>logEvent.</p>
	 *
	 * @param arg0 a {@link javax.xml.transform.TransformerException} object.
     */
	protected void logEvent(TransformerException arg0) {
		if (log.isDebugEnabled())
			log.debug(arg0.getMessage()); // arg0.getLocator().getLineNumber()
		if (arg0.getException() != null)
			logEvent(arg0.getException().getMessage());
		else
			logEvent(arg0.getMessage());
	}

	protected void logEvent(String message) {
		if (message.startsWith(VALIDATION_ERROR_PREFIX))
			errors.add(message);
		else if (message.startsWith(ValidatorListener.VALIDATION_WARNING_PREFIX))
			warnings.add(message);
		results.add(message);
	}


}
