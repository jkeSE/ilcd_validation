package com.okworx.ilcd.validation;

import java.util.Map;

import com.okworx.ilcd.validation.events.EventsList;

/**
 * <p>IValidator interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IValidator {

	/**
	 * <p>getAspectName.</p>
	 *
	 * @return the name of the validation aspect covered by this Validator.
	 */
	public abstract String getAspectName();

	/**
	 * <p>getAspectDescription.</p>
	 *
	 * @return a description of the validation aspect covered by this Validator.
	 */
	public abstract String getAspectDescription();

	/**
	 * Invokes the validation
	 *
	 * @return the validation result
	 * @throws java.lang.InterruptedException if any.
	 */
	public boolean validate() throws InterruptedException;

	/**
	 * <p>getEventsList.</p>
	 *
	 * @return the list of validation events
	 */
	public EventsList getEventsList();

	/**
	 * <p>setParameter.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param obj a {@link java.lang.Object} object.
	 */
	public void setParameter( String name, Object obj );

	/**
	 * Whether to report successful validations (default: false)
	 *
	 * @param reportSuccesses a boolean.
	 */
	public void setReportSuccesses(boolean reportSuccesses);

	public Map<String, Object> getParameters();
}
