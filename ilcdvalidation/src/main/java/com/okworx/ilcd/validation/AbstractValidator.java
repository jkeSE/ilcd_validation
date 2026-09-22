package com.okworx.ilcd.validation;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.common.DefaultValidationContext;
import com.okworx.ilcd.validation.common.IContextAwareComponent;
import com.okworx.ilcd.validation.common.IValidationContext;
import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.util.IUpdateEventListener;

/**
 * <p>Abstract AbstractValidator class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public abstract class AbstractValidator implements IValidator, IContextAwareComponent {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	/**
	 * <p>getAspectName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getAspectName();

	/**
	 * <p>getAspectDescription.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getAspectDescription();

	protected EventsList eventsList = new EventsList(this.getAspectName());

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 * @throws java.lang.InterruptedException if any.
	 */
	public abstract boolean validate() throws InterruptedException;
	
	protected IUpdateEventListener updateEventListener;

	protected boolean reportSuccesses = false;

	protected Map<String, Object> parameters = new HashMap<String, Object>();
	
	protected IValidationContext validationContext = new DefaultValidationContext();
	
	/**
	 * <p>Getter for the field <code>eventsList</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.EventsList} object.
	 */
	public EventsList getEventsList() {
		return eventsList;
	}

	/** {@inheritDoc} */
	public void setParameter(String parameterName, Object obj) {
		if (obj != null)
			this.parameters.put(parameterName, obj);
		else
			this.parameters.remove(parameterName);
	}

	/**
	 * <p>getParameter.</p>
	 *
	 * @param parameterName a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object getParameter(String parameterName) {
		return this.parameters.get(parameterName);
	}
	
	/**
	 * @return the Map with all parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * <p>reset.</p>
	 */
	public void reset() {
		this.eventsList = new EventsList(this.getAspectName());
		this.parameters = new HashMap<String, Object>();
	}

	/**
	 * <p>Constructor for AbstractValidator.</p>
	 */
	public AbstractValidator() {
	}
	
	/**
	 * <p>Getter for the field <code>updateEventListener</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.IUpdateEventListener} object.
	 */
	public IUpdateEventListener getUpdateEventListener() {
		return updateEventListener;
	}

	/**
	 * <p>Setter for the field <code>updateEventListener</code>.</p>
	 *
	 * @param updateEventListener a {@link com.okworx.ilcd.validation.util.IUpdateEventListener} object.
	 */
	public void setUpdateEventListener(
			IUpdateEventListener updateEventListener) {
		this.updateEventListener = updateEventListener;
	}

	/**
	 * <p>updateProgress.</p>
	 *
	 * @param percentFinished a double.
	 */
	protected void updateProgress(double percentFinished) {
		if (this.getUpdateEventListener() != null)
			this.getUpdateEventListener().updateProgress(percentFinished);
	}

	/**
	 * <p>updateStatus.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	protected void updateStatus(String message) {
		if (this.getUpdateEventListener() != null)
			this.getUpdateEventListener().updateStatus(message);
	}

	/**
	 * <p>updateStatusSetup.</p>
	 */
	protected void updateStatusSetup() {
		updateStatus("Setting up...");
	}

	/**
	 * <p>updateStatusValidating.</p>
	 */
	protected void updateStatusValidating() {
		updateStatus("Validating...");
	}

	/**
	 * <p>updateStatusDone.</p>
	 */
	protected void updateStatusDone() {
		updateStatus("Done.");
	}

	/**
	 * <p>updateStatusCancelled.</p>
	 */
	protected void updateStatusCancelled() {
		updateStatus("Cancelled");
	}
	
	/**
	 * <p>interrupted.</p>
	 *
	 * @param e a {@link java.lang.InterruptedException} object.
	 * @throws java.lang.InterruptedException if any.
	 */
	protected void interrupted(InterruptedException e) throws InterruptedException {
		log.info("operation was interrupted, aborting");
		updateStatusCancelled();
		updateProgress(0);
		throw e;
	}

	/**
	 * <p>interrupted.</p>
	 *
	 * @throws java.lang.InterruptedException if any.
	 */
	protected void interrupted() throws InterruptedException {
		interrupted(new InterruptedException());
	}

	/**
	 * <p>isReportSuccesses.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isReportSuccesses() {
		return reportSuccesses;
	}

	/** {@inheritDoc} */
	public void setReportSuccesses(boolean reportSuccesses) {
		this.reportSuccesses = reportSuccesses;
	}
	
	/**
	 * <p>wrapInputStream.</p>
	 *
	 * @param inputStream a {@link java.io.InputStream} object.
	 * @return a {@link java.io.InputStream} object.
	 */
	public InputStream wrapInputStream(InputStream inputStream) {
		BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
	    return new BufferedInputStream(bOMInputStream);
	}

	/**
	 * <p>Getter for the field <code>validationContext</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.IValidationContext} object.
	 */
	public IValidationContext getValidationContext() {
		return validationContext;
	}

	/** {@inheritDoc} */
	public void setValidationContext(IValidationContext validationContext) {
		this.validationContext = validationContext;
	}

	/*
	  If an error occurs during processing, add a FATAL validation event and log the error
	 */
	protected void raiseApplicationError(String message, Throwable exception) {
		log.error("error configuring transformer", exception);
		this.eventsList.add(new ValidationEvent(this.getAspectName(), null, Severity.FATAL, null, message + " Cause: " + exception.getMessage()));
		exception.printStackTrace();
	}
}
