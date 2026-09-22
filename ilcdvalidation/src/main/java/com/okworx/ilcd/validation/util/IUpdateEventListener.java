package com.okworx.ilcd.validation.util;

/**
 * <p>IUpdateEventListener interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IUpdateEventListener {
	
	/**
	 * <p>updateProgress.</p>
	 *
	 * @param percentFinished a double.
	 */
	public void updateProgress(double percentFinished);

	/**
	 * <p>updateStatus.</p>
	 *
	 * @param statusMessage a {@link java.lang.String} object.
	 */
	public void updateStatus(String statusMessage);

}
