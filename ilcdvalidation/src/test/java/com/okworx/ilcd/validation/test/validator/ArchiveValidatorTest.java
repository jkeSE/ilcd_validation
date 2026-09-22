package com.okworx.ilcd.validation.test.validator;

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

import com.okworx.ilcd.validation.ArchiveValidator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArchiveValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this
			.getClass());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPass() {
		ArchiveValidator validator = new ArchiveValidator();
		
		validator.setArchiveToValidate(new File(
				"src/test/resources/archives/valid.zip"));

		boolean result = validator.validate();
		
		logEvents(validator);
		
		assertTrue(result);

		assertTrue(validator.getEventsList().isEmpty());
	}

	@Test
	public void testFailNoValidZIP() {
		ArchiveValidator validator = new ArchiveValidator();
		
		validator.setArchiveToValidate(new File(
				"src/test/resources/archives/noValidZIP.zip"));

		boolean result = validator.validate();
		
		logEvents(validator);
		
		assertFalse(result);

		assertFalse(validator.getEventsList().isEmpty());

	}


	@Test
	public void testFailNoValidZIPButDir() {
		ArchiveValidator validator = new ArchiveValidator();
		
		validator.setArchiveToValidate(new File(
				"src/test/resources/archives/noValidZIPButDir.zip"));

		boolean result = validator.validate();
		
		logEvents(validator);
		
		assertFalse(result);

		assertFalse(validator.getEventsList().isEmpty());

	}


	@Test
	public void testFailNoILCD() {
		ArchiveValidator validator = new ArchiveValidator();
		
		validator.setArchiveToValidate(new File(
				"src/test/resources/archives/noILCD.zip"));

		boolean result = validator.validate();
		
		logEvents(validator);
		
		assertFalse(result);

		assertFalse(validator.getEventsList().isEmpty());

	}

	@Test
	public void testFailNoDatasetFolders() {
		ArchiveValidator validator = new ArchiveValidator();
		
		validator.setArchiveToValidate(new File(
				"src/test/resources/archives/noDatasetFolders.zip"));

		boolean result = validator.validate();
		
		logEvents(validator);
		
		assertFalse(result);

		assertFalse(validator.getEventsList().isEmpty());

	}
}
