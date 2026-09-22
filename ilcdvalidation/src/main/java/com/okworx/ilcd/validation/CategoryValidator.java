package com.okworx.ilcd.validation;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.util.PrefixBuilder;

/**
 * Validates category information in datasets - checks whether the declared
 * categories comply with the specified categories file.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class CategoryValidator extends XSLTStylesheetValidator implements IValidator {

	public static final String ASPECT_NAME = "Categories";

	/** Constant <code>CATEGORIES="CATEGORIES"</code> */
	public static final String CATEGORIES = "CATEGORIES";

	protected boolean mandatoryClassification = false;
	
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
		return "Checks whether the declared categories comply with the specified categories file.";
	}
	
	/**
	 * <p>isMandatoryClassification.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isMandatoryClassification() {
		return this.mandatoryClassification;
	}
	
	/**
	 * <p>Setter for the field <code>mandatoryClassification</code>.</p>
	 *
	 * @param mandatoryClassification a boolean.
	 */
	public void setMandatoryClassification(boolean mandatoryClassification) {
		this.mandatoryClassification = mandatoryClassification;
		this.setTransformationParameter("noEmptyClassification", this.mandatoryClassification);
	}

	/** {@inheritDoc} */
	@Override
	public void setProfile(Profile profile) {
		super.setProfile(profile);
		if ( profile != null ) {
			Profile.Bundle<String> bundle = profile.getCategoriesBundle();
			registerStylesheet(profile.getPath().toString(), profile.getStylesheetsPath(), "validate-categories.xsl");
			this.setTransformationParameter("categoriesFile", bundle.getResource());
			this.resolverMappings.put(bundle.getResource(),
					PrefixBuilder.buildPath(bundle.getJarPath(), bundle.getResource()));
		}
	}
}
