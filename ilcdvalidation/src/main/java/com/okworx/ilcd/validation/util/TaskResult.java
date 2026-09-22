package com.okworx.ilcd.validation.util;

import java.util.Collection;

import com.okworx.ilcd.validation.events.IValidationEvent;

/**
 * <p>TaskResult class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class TaskResult {

	private Collection<IValidationEvent> validationEvents;
	
	private Statistics statistics;

	/**
	 * <p>Constructor for TaskResult.</p>
	 *
	 * @param events a {@link java.util.Collection} object.
	 * @param stats a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public TaskResult(Collection<IValidationEvent> events, Statistics stats) {
		this.validationEvents = events;
		this.statistics = stats;
	}

	/**
	 * <p>Getter for the field <code>validationEvents</code>.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<IValidationEvent> getValidationEvents() {
		return validationEvents;
	}

	/**
	 * <p>Setter for the field <code>validationEvents</code>.</p>
	 *
	 * @param validationEvents a {@link java.util.Collection} object.
	 */
	public void setValidationEvents(Collection<IValidationEvent> validationEvents) {
		this.validationEvents = validationEvents;
	}

	/**
	 * <p>Getter for the field <code>statistics</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public Statistics getStatistics() {
		return statistics;
	}

	/**
	 * <p>Setter for the field <code>statistics</code>.</p>
	 *
	 * @param statistics a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
}
