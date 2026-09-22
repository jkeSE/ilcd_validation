package com.okworx.ilcd.validation;



import java.io.File;

import com.okworx.ilcd.validation.events.EventsList;



/**
 * Simplified interface for validating filesystem objects. Simply hand in a folder with the objects to validate and a
 * list of validators and collect any validation events.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class Validator {

	/**
	 * Validates the objects in the given folder with the specified validator.
	 *
	 * @param folder
	 *            the folder with objects to validate
	 * @param validator
	 *            the validator
	 * @return an EventsList object containing all the validation events.
	 * @throws java.lang.InterruptedException if any.
	 */
	public static EventsList validate( File folder, AbstractDatasetsValidator validator ) throws InterruptedException {
		validator.setObjectsToValidate( folder );
		validator.validate();
		return validator.getEventsList();
	}

	/**
	 * Validates the objects in the given folder with the specified validators.
	 *
	 * @param folder
	 *            the folder with objects to validate
	 * @param validators
	 *            a list of validators
	 * @return an EventsList object containing all the validation events.
	 * @throws java.lang.InterruptedException if any.
	 */
	public static EventsList validate( File folder, AbstractDatasetsValidator... validators ) throws InterruptedException  {

		if ( validators == null || validators.length == 0 )
			throw new IllegalArgumentException( "No Validator(s) specified" );

		if ( validators.length == 1 )
			return validate( folder, validators[0] );
		else
			return validateMultiple( folder, validators );

	}

	private static EventsList validateMultiple( File folder, AbstractDatasetsValidator[] validators ) {

		ValidatorChain vChain = new ValidatorChain();
		vChain.setObjectsToValidate( folder );

		for ( int i = 0; i < validators.length; i++ ) {
			vChain.add( validators[i] );
		}

		vChain.validate();

		return vChain.getEventsList();
	}

}
