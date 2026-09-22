package com.okworx.ilcd.validation.tool.util;

import org.eclipse.core.runtime.Platform;

import com.okworx.ilcd.validation.*;
import com.okworx.ilcd.validation.analyze.SummaryExtractor;
import com.okworx.ilcd.validation.analyze.flows.FlowsAnalyzer;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.tool.options.ValidationOptions;

/**
 * Facilitates the creation of Validators. The Factory has to be notified which
 * aspects to incorporate by setting the appropriate booleans to true. Then,
 * validator instances are retrieved by calling createValidator().
 * 
 * @author Oliver Armbruster
 *
 */

public class ValidatorFactory {
	public boolean useDefaultValidator = false;

	public boolean addArchiveValidator = false;
	public boolean addCategoryValidator = false;
	public String categoryFilename = "";
	public boolean addFilenameValidator = false;
	public boolean addLinkValidator = false;
	public boolean addOrphansValidator = false;
	public boolean addReferenceFlowValidator = false;
	public boolean addSchemaValidator = false;
	public String schemaFilename = "";
	public boolean addXSLTStylesheetValidator = false;
	
	private ValidationOptions validationOptions;
	
	public Profile profile = null;

	public ValidatorFactory(ValidationOptions validationOptions) {
		this.validationOptions = validationOptions;
		this.validationOptions.dumpOptions();
		this.profile = validationOptions.getProfile();
	}
	
	public ValidatorChain createValidator() {

		// // in order to register another than the default profile from the
		// filesystem, register it using a file: URL
		//
		// Profile profile = null;
		// try {
		// profile = ProfileManager.INSTANCE.registerProfile(new URL(
		// "file:/path/to/EPD_1.1_profile_1.0.jar"));
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // then simply assign it to the validator(s)
		// SchemaValidator sv = new SchemaValidator();
		// sv.setProfile(profile);
		//
		// XSLTStylesheetValidator xslv = new XSLTStylesheetValidator();
		// xslv.setProfile(profile);

		if (useDefaultValidator) {
			return new ILCDFormatValidator();
		}

		ValidatorChain validatorChain = new ValidatorChain();
		
		validatorChain.getExtraProperties().put("by:", "ILCD Validation Tool " + Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString());

		validatorChain.setBatchMode((boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_BATCHMODE));
		
		if (profile != null)
			validatorChain.setProfile(profile);

		if (addArchiveValidator) {
			
			// validatorChain.add(new ArchiveValidator());
		}

		if (addSchemaValidator) {
			SchemaValidator sv = new SchemaValidator();
			sv.setAspectName("ILCD Format XML Schema Validity");
			sv.setParameter(SchemaValidator.PARAM_IGNORE_REFERENCE_OBJECTS, validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_IGNORE_REFERENCE_OBJECTS));
			validatorChain.add(sv);
		}

		if (addXSLTStylesheetValidator) {
			XSLTStylesheetValidator xslv = new XSLTStylesheetValidator();
			xslv.setAspectName("ILCD Format Validity");
			xslv.setParameter(XSLTStylesheetValidator.PARAM_IGNORE_REFERENCE_OBJECTS, validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_IGNORE_REFERENCE_OBJECTS));
			validatorChain.add(xslv);
		}
		
		if (addReferenceFlowValidator) {
			ReferenceFlowValidator rfv = new ReferenceFlowValidator();
			validatorChain.add(rfv);
		}

		if (addCategoryValidator) {
			CategoryValidator cv = new CategoryValidator();
			validatorChain.add(cv);
		}

		if (addLinkValidator) {
			LinkValidator lv = new LinkValidator();
			lv.setParameter(LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS, (boolean) validationOptions.getLinkOptions().get(LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS));
			lv.setParameter(LinkValidator.PARAM_IGNORE_INCLUDEDPROCESSES, (boolean) validationOptions.getLinkOptions().get(LinkValidator.PARAM_IGNORE_INCLUDEDPROCESSES));
			lv.setParameter(LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS, (boolean) validationOptions.getLinkOptions().get(LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS));
			lv.setParameter(LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION, (boolean) validationOptions.getLinkOptions().get(LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION));
			lv.setParameter(LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS, (boolean) validationOptions.getLinkOptions().get(LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS));
			lv.setParameter(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS, (boolean) validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_IGNORE_REFERENCE_OBJECTS));
			validatorChain.add(lv);
		}

		if (addOrphansValidator) {
			OrphansValidator ov = new OrphansValidator();
			ov.setParameter(OrphansValidator.PARAM_IGNORE_LCIAMETHODS, (boolean) validationOptions.getOrphansOptions().get(OrphansValidator.PARAM_IGNORE_LCIAMETHODS));
			ov.setParameter(OrphansValidator.PARAM_IGNORE_REFERENCE_OBJECTS, (boolean) validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_IGNORE_REFERENCE_OBJECTS));
			validatorChain.add(ov);
		}

		if (addFilenameValidator) {
			validatorChain.add(new FilenameValidator());
		}
		
		// if a dataset summary is requested AND we are in batch mode, we add a SummaryExtractor as the last validator in the chain
		if ((boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_BATCHMODE) && (boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_DS_SUMMARY)) {
			validatorChain.add(new SummaryExtractor());	
		}
		// same for elementary flows analysis
		if ((boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_BATCHMODE) && (boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_FLOWS_ANALYSIS)) {
			FlowsAnalyzer efa = new FlowsAnalyzer();
			validatorChain.add(efa);
			if ((boolean) this.validationOptions.getGeneralOptions().get(ValidationOptions.OPTION_FLOWS_ANALYSIS_INCLUDE_SUMS))
				efa.setParameter(FlowsAnalyzer.PARAM_INCLUDE_SUMS, true);
		}

		return validatorChain;
	}
	
}
