package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.IDatasetsValidator;
import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;



// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorChainWithProfileTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected ValidatorChain validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new ValidatorChain("Composite Validator");
		
		IDatasetsValidator v1 = new SchemaValidator();

		this.validator.setProfile(ProfileManager.getInstance().registerProfile(TestConfig.getEPDProfileURL()));
			
		IDatasetsValidator v2 = new XSLTStylesheetValidator();

		this.validator.add(v1);
		this.validator.add(v2);
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		
		validator.reset();
		
		log.info("setting file");
		
		validator.setObjectsToValidate(file);
		
		log.info("validating");
		
		boolean result = validator.validate();

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
			log.error(event);
		}

		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/EPD/ILCD/processes/Kalk_(CaO__Feinkalk).xml"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());
		
		assertEquals(1, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());


	}

	@Test
	public void testFail() {

		boolean result = runValidation(new File("src/test/resources/datasets/EPD/fail"));

		assertFalse(result);
		assertEquals(9, validator.getEventsList().size());

		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());
		assertEquals(0, validator.getStatistics().getInvalidContactsCount());
		
		assertEquals(0, validator.getStatistics().getTotalValidCount());
		assertEquals(1, validator.getStatistics().getTotalInvalidCount());
		
	}

}
