package com.okworx.ilcd.validation.events;

import org.apache.commons.lang3.StringUtils;

import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>Abstract AbstractValidationEvent class.</p>
 *
 * @author oli
 * @version $Id: $Id
 */
public abstract class AbstractValidationEvent implements IValidationEvent {

	protected String aspect;

	protected String aspectDescription;

	protected String message;

	protected String altMessage;

	public String getAltMessage() {
		return altMessage;
	}

	public void setAltMessage(String altMessage) {
		this.altMessage = altMessage;
	}

	protected IDatasetReference reference = null;

	protected IDatasetReference messageReference = null;

	protected Type type = Type.GENERIC;

	protected Severity severity;

	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param type a {@link com.okworx.ilcd.validation.events.Type} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public AbstractValidationEvent(String aspect, String aspectDescription, Severity severity, Type type, IDatasetReference reference, String message) {
		this.aspect = aspect;
		this.aspectDescription = aspectDescription;
		this.severity = severity;
		this.type = type;
		this.reference = reference;
		this.message = message;
	}
	
	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param type a {@link com.okworx.ilcd.validation.events.Type} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public AbstractValidationEvent(String aspect, Severity severity, Type type, IDatasetReference reference, String message) {
		this.aspect = aspect;
		this.severity = severity;
		this.type = type;
		this.reference = reference;
		this.message = message;
	}

	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public AbstractValidationEvent(String aspect, Severity severity, IDatasetReference reference, String message) {
		this(aspect, null, severity, reference, message);
	}
	
	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param aspectDescription a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public AbstractValidationEvent(String aspect, String aspectDescription, Severity severity, IDatasetReference reference, String message) {
		this.aspect = aspect;
		this.aspectDescription = aspectDescription;
		this.severity = severity;
		this.type = Type.GENERIC;
		this.reference = reference;
		this.message = message;
	}
	
	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 */
	public AbstractValidationEvent(String aspect) {
		this.aspect = aspect;
	}

	/**
	 * <p>Constructor for AbstractValidationEvent.</p>
	 */
	public AbstractValidationEvent() {
	}

	/**
	 * <p>Getter for the field <code>message</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMessage() {
		return message;
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
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.Type} object.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * <p>Getter for the field <code>severity</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.Severity} object.
	 */
	public Severity getSeverity() {
		return severity;
	}

	/** {@inheritDoc} */
	public void setMessage(String message) {
		this.message = message;
	}

	/** {@inheritDoc} */
	public void setReference(IDatasetReference reference) {
		this.reference = reference;
	}

	/** {@inheritDoc} */
	public void setType(Type type) {
		this.type = type;
	}

	/** {@inheritDoc} */
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	/**
	 * <p>Getter for the field <code>aspect</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspect() {
		return aspect;
	}

	/**
	 * <p>Setter for the field <code>aspect</code>.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 */
	public void setAspect(String aspect) {
		this.aspect = aspect;
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		StringBuilder buf = new StringBuilder(this.getAspect());
		buf.append(" ");
		buf.append(StringUtils.isNotBlank(this.getAspectDescription()) ? this.getAspectDescription() : "");
		buf.append(" ");
		buf.append(this.severity.getValue());
		buf.append(" ");
		buf.append(this.type.getValue());
		buf.append(" ");
		buf.append(StringUtils.isNotBlank(this.getReference().getUuid()) ? this.getReference().getUuid() + " ": "");
		buf.append(StringUtils.isNotBlank(this.getReference().getName()) ? this.getReference().getName() + " ": "");
		buf.append(this.getReference().getShortFileName());
		buf.append(" ");
		buf.append(this.getReference().getDatasetType()!=null ? this.getReference().getDatasetType() : "");
		buf.append(" ");
		buf.append(this.message);
		return buf.toString();
	}

	public IDatasetReference getMessageReference() {
		return messageReference;
	}

	public void setMessageReference(IDatasetReference messageReference) {
		this.messageReference = messageReference;
	}

	public String getAspectDescription() {
		return aspectDescription;
	}

	public void setAspectDescription(String aspectDescription) {
		this.aspectDescription = aspectDescription;
	}
}
