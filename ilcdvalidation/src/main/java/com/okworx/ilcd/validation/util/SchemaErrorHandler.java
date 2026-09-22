package com.okworx.ilcd.validation.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>SchemaErrorHandler class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class SchemaErrorHandler implements ErrorHandler {

	private IDatasetReference reference;
	
	private SchemaValidator validator;

	private ConcurrentLinkedQueue<IValidationEvent> events = new ConcurrentLinkedQueue<IValidationEvent>();

	/**
	 * <p>Constructor for SchemaErrorHandler.</p>
	 *
	 * @param schemaValidator a {@link com.okworx.ilcd.validation.SchemaValidator} object.
	 */
	public SchemaErrorHandler(SchemaValidator schemaValidator) {
		this.validator = schemaValidator;
	}

	/**
	 * <p>Getter for the field <code>events</code>.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<IValidationEvent> getEvents() {
		return events;
	}

	/**
	 * <p>Getter for the field <code>reference</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public IDatasetReference getReference() {
		return reference;
	}

	/**
	 * <p>Setter for the field <code>reference</code>.</p>
	 *
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public void setReference(IDatasetReference reference) {
		this.reference = reference;
	}

	/** {@inheritDoc} */
	public void error(SAXParseException arg0) throws SAXException {
		this.events.add(new ValidationEvent(this.validator.getAspectName(), Severity.ERROR, this.reference, arg0.getLineNumber() + ","
				+ arg0.getColumnNumber() + " " + arg0.getMessage()));
	}

	/** {@inheritDoc} */
	public void fatalError(SAXParseException arg0) throws SAXException {
		this.events.add(new ValidationEvent(this.validator.getAspectName(), Severity.ERROR, this.reference, arg0.getMessage()));
	}

	/** {@inheritDoc} */
	public void warning(SAXParseException arg0) throws SAXException {
		this.events.add(new ValidationEvent(this.validator.getAspectName(), Severity.WARNING, this.reference, arg0.getMessage()));

	}

}
