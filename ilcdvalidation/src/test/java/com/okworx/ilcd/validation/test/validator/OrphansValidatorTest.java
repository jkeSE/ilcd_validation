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

import com.okworx.ilcd.validation.OrphansValidator;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrphansValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this
			.getClass());

	protected OrphansValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);
		this.validator = new OrphansValidator();
		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		return runValidation(file, false);
	}
	
	private boolean runValidation(File file, boolean reportSuccesses) {
		validator.setObjectsToValidate(file);

		validator.setReportSuccesses(reportSuccesses);

		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());

		log.info("validating " + validator.getObjectsToValidate().size()
				+ " objects");

		boolean result = false;
		
		try {
			result = validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
		}

//		logEvents(validator);

		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_pass.zip"));

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(1399, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
		
	}

	@Test
	public void testPassWithReportSuccesses() {

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_pass.zip"), true);

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isPositive());

		assertEquals(1399, validator.getEventsList().size());

		assertEquals(1399, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
		
	}

	@Test
	public void testFail() {

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_fail.zip"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(4, validator.getEventsList().size());

		assertEquals(3, validator.getStatistics().getInvalidExternalFilesCount());
		assertEquals(1, validator.getStatistics().getInvalidContactsCount());

		assertEquals(1390, validator.getStatistics().getTotalValidCount());
		assertEquals(4, validator.getStatistics().getTotalInvalidCount());
		
	}

	@Test
	public void testFailNotIgnoreRefObjects() {

		validator.reset();
		
		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_ignorerefobjects.zip"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(12, validator.getEventsList().size());

		assertEquals(1032, validator.getStatistics().getTotalValidCount());
		assertEquals(12, validator.getStatistics().getTotalInvalidCount());
		
	}


	@Test
	public void testPassIgnoreRefObjects() {

		validator.reset();
		validator.setParameter(OrphansValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);

		Profile p = TestConfig.getEFProfile();
		validator.setProfile(p);

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_ignorerefobjects.zip"));

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(1032, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
		
	}


	
	@Test
	public void testPassZIP() {

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_pass.zip"));

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testFailZIP() {

		boolean result = runValidation(new File(
				"src/test/resources/datasets/Orphans/test_fail.zip"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(4, validator.getEventsList().size());

	}

}
