package com.okworx.ilcd.validation.test.validator;

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
import com.okworx.ilcd.validation.Validator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

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
	public void testPass() throws InterruptedException {

		Validator.validate(new File("src/test/resources/datasets/Schema/test_pass.zip"), new ILCDFormatValidator());

	}

	@Test
	public void testPassZIP() throws InterruptedException {

		Validator.validate(new File("src/test/resources/datasets/Schema/test_pass.zip"), new ILCDFormatValidator());

	}

	@Test
	public void testFail() {
		// validator.setObjectsToValidate(new
		// File("src/test/resources/datasets/Schema/test_fail"));
	}

}
