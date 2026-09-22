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
import com.okworx.ilcd.validation.OrphansValidator;
import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.ProfileManager;


// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorChainTest extends AbstractValidatorTest {

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
		this.validator.setProfile(ProfileManager.getInstance().getDefaultProfile());
		
		IDatasetsValidator v2 = new OrphansValidator();

//		IDatasetsValidator v3 = new LinkValidator();
		
		this.validator.add(v1);
		this.validator.add(v2);
//		this.validator.add(v3);
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
			log.info(event);
		}

		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/Orphans/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());
		
		assertEquals(1399, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());


	}

	@Test
	public void testFail() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		assertFalse(result);
		assertEquals(42, validator.getEventsList().size());

		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());
		assertEquals(1, validator.getStatistics().getInvalidContactsCount());
		
		assertEquals(53, validator.getStatistics().getTotalValidCount());
		assertEquals(34, validator.getStatistics().getTotalInvalidCount());
		
	}

	@Test
	public void testFailInvalidArchive() {

		this.validator.setValidateArchives(true);

		boolean result = runValidation(new File("src/test/resources/archives/noLCD_fail_Schema.zip"));

		assertFalse(result);
		assertEquals(43, validator.getEventsList().size());

	}
	@Test
	public void testFailInvalidFiles() {

		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/empty_file.xml"));

		assertFalse(result);
		assertEquals(1, validator.getEventsList().size());

	}

	@Test
	public void testFailInvalidFilesBatchMode() {

		validator.setBatchMode(true);
		validator.getExtraProperties().put("by:", "ILCD Validation Library v1.5.19");
		
		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML"));

		validator.setBatchMode(false);
		
		assertFalse(result);
		assertEquals(4, validator.getEventsList().size());

	}


}
