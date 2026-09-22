package com.okworx.ilcd.validation.test.dev;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.IDatasetsValidator;
import com.okworx.ilcd.validation.LinkValidator;
import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.validator.AbstractValidatorTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorChainTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected ValidatorChain validator;

	private boolean runValidation(File file) {
		
		validator.reset();
		
		log.info("setting file");
		
		validator.setObjectsToValidate(file);
		
		log.info("validating");
		
		boolean result = validator.validate();

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
			log.info(event);
		}

		return result;
	}
	
//	@Test
	public void testBatchMode() throws InvalidProfileException, MalformedURLException {

		new ProfileManager.ProfileManagerBuilder()
		.registerDefaultProfiles(false, false)
		.build();
		
		this.validator = new ValidatorChain("Composite Validator");

		IDatasetsValidator v1 = new SchemaValidator();
		v1.setParameter(SchemaValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);		
//		this.validator.add(v1);

		IDatasetsValidator v2 = new XSLTStylesheetValidator();
		this.validator.add(v2);
		
		IDatasetsValidator v3 = new LinkValidator();
		v3.setParameter(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);
//		this.validator.add(v3);
		
		//validator.setBatchMode(true);
		validator.getExtraProperties().put("by:", "ILCD Validation Library v2.0.0beta");
		
		validator.setProfile(ProfileManager.getInstance().registerProfile(TestConfig.getEFProfileURL()));
		validator.setProfile(ProfileManager.getInstance().registerProfile(new URL("file:/path/to/profile.jar")));
		boolean result = runValidation(new File("/path/to/file.zip"));

		validator.setBatchMode(false);

		assertTrue(result);
		assertEquals(0, validator.getEventsList().size());

	}

}
