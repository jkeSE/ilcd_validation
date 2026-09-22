package com.okworx.ilcd.validation.test.log;

import java.io.File;

import com.okworx.ilcd.validation.*;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.analyze.SummaryExtractor;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.ProfileManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BatchModeTest {
	
	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected ValidatorChain validator;

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new ValidatorChain("Composite Validator");
		
		this.validator.setBatchMode(true);
		
		IDatasetsValidator v1 = new SchemaValidator();
		this.validator.setProfile(ProfileManager.getInstance().getDefaultProfile());
		
		IDatasetsValidator v2 = new OrphansValidator();

		IDatasetsValidator v3 = new LinkValidator();

		IDatasetsValidator v4 = new SummaryExtractor();

//		this.validator.add(v1);
//		this.validator.add(v2);
//		this.validator.add(v3);
//		this.validator.add(v4);
	}

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
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/LCModel/ILCD"));

	}


//	@Test
	public void testFail() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail"));
		
	}

}
