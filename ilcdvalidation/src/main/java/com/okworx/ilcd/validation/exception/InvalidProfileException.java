package com.okworx.ilcd.validation.exception;

/**
 * <p>InvalidProfileException class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class InvalidProfileException extends Exception {

	private static final long serialVersionUID = 1109197146930082118L;

	public InvalidProfileException() {
		super();
	}
	
	public InvalidProfileException(String message) {
		super(message);
	}

	public InvalidProfileException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
