package com.okworx.ilcd.validation;

/**
 * Validates a set of given datasets for matching UUID and filename pattern.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class FilenameValidator extends AbstractDatasetsValidator implements IValidator {

	public static final String ASPECT_NAME = "TFile names";

	/** {@inheritDoc} */
	@Override
	public String getAspectName() {
		return ASPECT_NAME;
	}

	/**
	 * <p>getAspectDescription.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspectDescription() {
		return "Checks for matching UUID and filename pattern.";
	}

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 */
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}
