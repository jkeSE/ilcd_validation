package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DetectInvalidFilesTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected SchemaValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new SchemaValidator();
		this.validator.setValidateArchives(true);
		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());
	}

	@After
	public void tearDown() throws Exception {
	}

	protected boolean runValidation(File file) {
		this.validator.setObjectsToValidate(file);

		boolean result = false;

		try {
			result = validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
		}

		logEvents(validator);

		return result;
	}


	@Test
	public void testFailEmptyFile() {

		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/empty_file.xml"));

		assertFalse(result);

		assertEquals(1, this.validator.getEventsList().size());
	}

	@Test
	public void testFailInvalidXML() {

		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/invalid_XML.xml"));

		assertFalse(result);

		assertEquals(1, this.validator.getEventsList().size());
	}

	@Test
	public void testFailInvalidXML2() {

		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/invalid_XML2.xml"));

		assertFalse(result);

		assertEquals(1, this.validator.getEventsList().size());
	}

	@Test
	public void testFailNotWellFormed() {

		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/not_well_formed.xml"));

		assertFalse(result);

		assertEquals(1, this.validator.getEventsList().size());
	}


	
}
