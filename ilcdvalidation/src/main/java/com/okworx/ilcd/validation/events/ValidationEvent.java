package com.okworx.ilcd.validation.events;

import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>ValidationEvent class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ValidationEvent extends AbstractValidationEvent implements IValidationEvent {

	/** Constant <code>SUCCESS_MESSAGE="The dataset has been successfully valid"{trunked}</code> */
	public static final String SUCCESS_MESSAGE = "The dataset has been successfully validated.";
	
	/**
	 * <p>Constructor for ValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param aspectDescription a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param type a {@link com.okworx.ilcd.validation.events.Type} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public ValidationEvent(String aspect, String aspectDescription, Severity severity, Type type, IDatasetReference reference, String message) {
		super(aspect, aspectDescription, severity, type, reference, message);
	}

	/**
	 * <p>Constructor for ValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param aspectDescription a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public ValidationEvent(String aspect, String aspectDescription, Severity severity, IDatasetReference reference, String message) {
		super(aspect, aspectDescription, severity, reference, message);
	}

	/**
	 * <p>Constructor for ValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param type a {@link com.okworx.ilcd.validation.events.Type} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public ValidationEvent(String aspect, Severity severity, Type type, IDatasetReference reference, String message) {
		super(aspect, severity, type, reference, message);
	}

	/**
	 * <p>Constructor for ValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param message a {@link java.lang.String} object.
	 */
	public ValidationEvent(String aspect, Severity severity, IDatasetReference reference, String message) {
		super(aspect, severity, reference, message);
	}

	/**
	 * <p>Constructor for ValidationEvent.</p>
	 *
	 * @param aspect a {@link java.lang.String} object.
	 */
	public ValidationEvent(String aspect) {
		super(aspect);
	}

	/**
	 * <p>Constructor for ValidationEvent.</p>
	 */
	public ValidationEvent() {
		super();
	}

}
