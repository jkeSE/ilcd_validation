package com.okworx.ilcd.validation.events;

import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>IValidationEvent interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IValidationEvent {

	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.Type} object.
	 */
	public Type getType();

	/**
	 * <p>getSeverity.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.Severity} object.
	 */
	public Severity getSeverity();

	/**
	 * <p>getMessage.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMessage();

	/**
	 * <p>getAltMessage.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAltMessage();

	/**
	 * <p>getAspect.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspect();

	/**
	 * <p>getAspectDescription.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspectDescription();

	/**
	 * <p>getReference.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public IDatasetReference getReference();

	/**
	 * <p>setMessage.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public void setMessage(String message);

	/**
	 * <p>setAltMessage.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public void setAltMessage(String message);

	/**
	 * <p>setType.</p>
	 *
	 * @param type a {@link com.okworx.ilcd.validation.events.Type} object.
	 */
	public void setType(Type type);

	/**
	 * <p>setReference.</p>
	 *
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public void setReference(IDatasetReference reference);

	/**
	 * <p>setSeverity.</p>
	 *
	 * @param severity a {@link com.okworx.ilcd.validation.events.Severity} object.
	 */
	public void setSeverity(Severity severity);

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString();

	/**
	 * <p>getMessageReference.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public IDatasetReference getMessageReference();

	/**
	 * <p>setMessageReference.</p>
	 *
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 */
	public void setMessageReference(IDatasetReference reference);

}
