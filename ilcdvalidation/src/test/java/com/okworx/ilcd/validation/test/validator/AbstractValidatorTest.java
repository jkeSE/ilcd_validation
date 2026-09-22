package com.okworx.ilcd.validation.test.validator;

import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.IValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;

public class AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger( this.getClass() );

	public AbstractValidatorTest() {
	}

	public void logEvents(IValidator validator) {
		for ( IValidationEvent event : validator.getEventsList().getEvents() ) {
			log.error(event);
		}
	}

}