package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
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

import com.okworx.ilcd.validation.CategoryValidator;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategoryValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this
			.getClass());

	protected CategoryValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);
		this.validator = new CategoryValidator();		
		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());
		Profile epdProfile = TestConfig.getEPDProfile();
		this.validator.setProfile(epdProfile);
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		validator.setObjectsToValidate(file);

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
				"src/test/resources/datasets/EPD/ILCD/processes/Heizkoerper.xml"));

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
		assertEquals(1, validator.getStatistics().getTotalValidCount());
		assertEquals(1, validator.getStatistics().getValidProcessesCount());
	}

//	@Test
//	public void testFail() {
//
//		boolean result = runValidation(new File(
//				"src/test/resources/datasets/Orphans/test_fail"));
//
//		logEvents(validator);
//
//		assertFalse(result);
//		assertEquals(4, validator.getEventsList().size());
//
//	}
//
//	@Test
//	public void testPassZIP() {
//
//		boolean result = runValidation(new File(
//				"src/test/resources/datasets/Orphans/test_pass.zip"));
//
//		logEvents(validator);
//
//		assertTrue(result);
//		assertTrue(validator.getEventsList().isEmpty());
//
//	}
//
//	@Test
//	public void testFailZIP() {
//
//		boolean result = runValidation(new File(
//				"src/test/resources/datasets/Orphans/test_fail.zip"));
//
//		logEvents(validator);
//
//		assertFalse(result);
//		assertEquals(4, validator.getEventsList().size());
//
//	}

}
