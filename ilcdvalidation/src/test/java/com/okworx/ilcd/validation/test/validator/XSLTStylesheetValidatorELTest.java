package com.okworx.ilcd.validation.test.validator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XSLTStylesheetValidatorELTest extends AbstractValidatorTest {

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
			
		this.validator.setProfile(TestConfig.getELProfile());

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

//	@Test
//	public void testPass() {
//
//		boolean result = runValidation(new File("/Users/oliver.kusche/ownCloud/Projects/PEF/thinkstep-data/example PEF energy and transports 2016_05_07/ILCD/processes"));
//
//		assertTrue(result);
//		assertTrue(validator.getEventsList().isEmpty());
//
//	}
//	
//	@Test
//	public void testEL() {
//
//		boolean result = runValidation(new File("/Users/oliver.kusche/ownCloud/Projects/PEF/thinkstep-data/example PEF energy and transports 2016_05_07/ILCD/processes"));
//
//	}

//	@Test
	public void testPEF() throws MalformedURLException, InvalidProfileException {

		this.validator.setProfile(ProfileManager.getInstance().registerProfile(new URL("file:target/profiles/EF_profile_1.0.jar")));

		boolean result = runValidation(new File("/Users/oliver.kusche/ownCloud/Projects/PEF/thinkstep-data/example PEF energy and transports 2016_05_07/ILCD/processes"));

	}

//	@Test
	public void testEF() throws MalformedURLException, InvalidProfileException {
		this.validator.setProfile(TestConfig.getEFProfile());

		boolean result = runValidation(new File("/Users/oliver.kusche/Desktop/EF_energy_transport_data"));

		logEvents(validator);
		
	}


//	@Test
//	public void testPassZIP() {
//
//		boolean result = runValidation(new File("src/test/resources/datasets/EPD/EPD.zip"));
//
//		assertTrue(result);
//		assertTrue(validator.getEventsList().isEmpty());
//
//	}

//	@Test
//	public void testFailZIP() {
//		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));
//
//		assertFalse(result);
//		assertEquals(8, validator.getEventsList().size());
//
//	}
//
//	@Test
//	public void testFailZIPInvalidArchive() {
//
//		boolean result = runValidation(new File("src/test/resources/archives/noLCD_fail_Schema.zip"));
//
//		assertFalse(result);
//		assertEquals(9, validator.getEventsList().size());
//
//	}
//
//	@Test
//	public void testFailZIPInvalidArchiveNoArchiveValidation() {
//
//		boolean checkArchives = this.validator.isValidateArchives();
//		this.validator.setValidateArchives(false);
//
//		boolean result = runValidation(new File("src/test/resources/archives/noLCD_fail_Schema.zip"));
//
//		this.validator.setValidateArchives(checkArchives);
//		assertFalse(result);
//		assertEquals(8, validator.getEventsList().size());
//
//	}
//
//	@Test
//	public void testFail() {
//		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail"));
//
//		assertFalse(result);
//		assertEquals(8, validator.getEventsList().size());
//
//	}

}
