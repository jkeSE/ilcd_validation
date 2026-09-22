package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import com.okworx.ilcd.validation.profile.ProfileManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.ILCDFormatValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ILCDFormatValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected ILCDFormatValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);
		ProfileManager.getInstance().registerDefaultProfile();
		this.validator = new ILCDFormatValidator();
		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());
		this.validator.setUpdateInterval( 100 );
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		validator.setObjectsToValidate(file);

		boolean result = validator.validate();

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
				log.info(event);
		}
		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(40, validator.getStatistics().getValidSourcesCount());
		assertEquals(42, validator.getStatistics().getValidProcessesCount());

		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testFail() {
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		assertFalse(result);
		assertEquals(21, validator.getEventsList().size());

//		assertEquals(1, validator.getStatistics().getValidContactsCount());
//		assertEquals(38, validator.getStatistics().getValidSourcesCount());
//		assertEquals(35, validator.getStatistics().getValidProcessesCount());
//
//		assertEquals(1, validator.getStatistics().getInvalidContactsCount());
//		assertEquals(3, validator.getStatistics().getInvalidSourcesCount());
//		assertEquals(9, validator.getStatistics().getInvalidProcessesCount());

//		assertEquals(13, validator.getStatistics().getTotalInvalidCount());
		
		log.info(validator.getStatistics());

	}

	@Test
	public void testPassZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());
		
		log.info(validator.getStatistics());

	}

	@Test
	public void testFailZIP() {
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		assertFalse(result);
		assertEquals(21, validator.getEventsList().size());

	}

}
