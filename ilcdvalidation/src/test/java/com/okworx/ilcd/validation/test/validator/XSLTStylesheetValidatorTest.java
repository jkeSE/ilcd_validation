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

import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XSLTStylesheetValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected XSLTStylesheetValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new XSLTStylesheetValidator();
		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());
		this.validator.setUpdateInterval( 100 );
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		validator.setObjectsToValidate(file);

		boolean result;
		try {
			result = validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
			return false;
		}

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
				log.info(event);
		}
		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_pass"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());
	}

	@Test
	public void testPassNonURICharsInPath() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_pass[extra]chars%"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());
	}

	@Test
	public void testPassWithReportSuccesses() {

		validator.setReportSuccesses(true);
		
		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_pass"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isPositive());
		assertEquals(4, validator.getEventsList().size());

	}

	@Test
	public void testFail() {
		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_fail"));

		assertFalse(result);
		assertEquals(11, validator.getEventsList().size());

	}

	@Test
	public void testPassZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testFailZIP() {
		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/test_fail.zip"));

		assertFalse(result);
		assertEquals(10, validator.getEventsList().size());

	}
	
	@Test
	public void testCategoriesPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_pass"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testCategoriesFail() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_fail"));

		assertFalse(result);
		assertEquals(1, validator.getEventsList().size());

	}

	@Test
	public void testCategoriesDefaultPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_defaultcats_pass"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testCategoriesPassZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testCategoriesFailZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_fail.zip"));

		assertFalse(result);
		assertEquals(1, validator.getEventsList().size());

	}

	@Test
	public void testCategoriesDefaultPassZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/XSLTStylesheet/categories/test_defaultcats_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}



}
