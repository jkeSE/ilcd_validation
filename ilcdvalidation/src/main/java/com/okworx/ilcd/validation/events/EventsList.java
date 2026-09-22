package com.okworx.ilcd.validation.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Holds a list of validation events for a certain validation aspect.
 *
 * When chaining Validators, the nestedEvents property contains the separate
 * EventsList objects in order to keep track of the respective validation
 * results
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class EventsList {

	protected String aspectName;

	protected boolean hasErrors = false;

	protected boolean hasWarnings = false;

	protected int errorCount = 0;

	protected int warningCount = 0;

	protected int successCount = 0;

	protected List<IValidationEvent> events = new ArrayList<IValidationEvent>();

	/**
	 * <p>Constructor for EventsList.</p>
	 *
	 * @param aspectName a {@link java.lang.String} object.
	 */
	public EventsList(String aspectName) {
		this.aspectName = aspectName;
	}

	/**
	 * <p>add.</p>
	 *
	 * @param event a {@link com.okworx.ilcd.validation.events.IValidationEvent} object.
	 */
	public void add(IValidationEvent event) {
		if (event.getSeverity().equals(Severity.ERROR)) {
			this.hasErrors = true;
			this.errorCount++;
		}
		if (event.getSeverity().equals(Severity.WARNING)) {
			this.hasWarnings = true;
			this.warningCount++;
		}
		if (event.getSeverity().equals(Severity.SUCCESS)) {
			this.successCount++;
		}
		this.events.add(event);
	}

	/**
	 * <p>addAll.</p>
	 *
	 * @param events a {@link java.util.Collection} object.
	 */
	public void addAll(Collection<IValidationEvent> events) {
		for (IValidationEvent event : events)
			this.add(event);
	}

	/**
	 * <p>isEmpty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmpty() {
		return this.events.isEmpty();
	}

	/**
	 * Indicates whether this list does not contain any errors or warnings.
	 *
	 * @return a boolean.
	 */
	public boolean isPositive() {
		return !(this.hasErrors);
	}

	/**
	 * Indicates whether this list contains any errors.
	 *
	 * @return a boolean.
	 */
	public boolean hasErrors() {
		return this.hasErrors;
	}

	/**
	 * Indicates whether this list contains any warnings.
	 *
	 * @return a boolean.
	 */
	public boolean hasWarnings() {
		return this.hasWarnings;
	}

	/**
	 * <p>Getter for the field <code>aspectName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspectName() {
		return aspectName;
	}

	/**
	 * <p>Setter for the field <code>aspectName</code>.</p>
	 *
	 * @param aspectName a {@link java.lang.String} object.
	 */
	public void setAspectName(String aspectName) {
		this.aspectName = aspectName;
	}

	/**
	 * <p>Getter for the field <code>events</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<IValidationEvent> getEvents() {
		return events;
	}

	/**
	 * <p>Setter for the field <code>events</code>.</p>
	 *
	 * @param events a {@link java.util.List} object.
	 */
	public void setEvents(List<IValidationEvent> events) {
		this.events = events;
	}

	/**
	 * <p>size.</p>
	 *
	 * @return a int.
	 */
	public int size() {
		return this.events.size();
	}

	/**
	 * <p>toArray.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] toArray() {
		return (String[]) this.events.toArray();
	}

	/**
	 * <p>Getter for the field <code>errorCount</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getErrorCount() {
		return errorCount;
	}
	
	/**
	 * <p>Getter for the field <code>warningCount</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getWarningCount() {
		return warningCount;
	}

	/**
	 * <p>Getter for the field <code>successCount</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getSuccessCount() {
		return successCount;
	}

}
