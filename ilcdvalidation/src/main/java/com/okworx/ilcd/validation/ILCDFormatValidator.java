package com.okworx.ilcd.validation;


/**
 * Validates category information in datasets.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ILCDFormatValidator extends ValidatorChain implements IValidator {

	public static final String ASPECT_NAME = "ILCD Format Validity";

	/** {@inheritDoc} */
	@Override
	public String getAspectName() {
		return ASPECT_NAME;
	}

	/**
	 * <p>Constructor for ILCDFormatValidator.</p>
	 */
	public ILCDFormatValidator() {

		AbstractDatasetsValidator sv = setupSchemaValidator();

		XSLTStylesheetValidator xslv = setupXSLValidator();

		this.validators.add(sv);
		this.validators.add(xslv);

	}

	private XSLTStylesheetValidator setupXSLValidator() {
		XSLTStylesheetValidator xslv = new XSLTStylesheetValidator();

		xslv.setAspectName("ILCD Format Validity");

//		xslv.setProfile(ProfileManager.INSTANCE.getDefaultProfile());
//		xslv.registerStylesheet(this.getClass().getClassLoader().getResource(Constants.DEFAULT_PROFILE_JAR).getPath(), Constants.STYLESHEETS_PATH_PREFIX, Constants.VALIDATE_STYLESHEET_NAME);
		
		return xslv;
	}

	private AbstractDatasetsValidator setupSchemaValidator() {
		SchemaValidator sv = new SchemaValidator();

//		sv.registerDefaultSchemas();
		
		return sv;
	}
}
