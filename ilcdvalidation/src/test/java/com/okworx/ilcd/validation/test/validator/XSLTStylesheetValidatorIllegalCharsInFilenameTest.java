package com.okworx.ilcd.validation.test.validator;

import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class XSLTStylesheetValidatorIllegalCharsInFilenameTest extends AbstractValidatorTest {

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
	public void testLinkExternalDocFilenameIllegalCharacters() {

		Profile p = TestConfig.getEFProfile();
		validator.setProfile(p);

		boolean result = runValidation(new File("src/test/resources/datasets/Links/test_images_extensions_fail/ILCD/sources/3cb18489-4359-11dd-ae16-0800200c9a66.xml"));

		assertFalse(result);
		assertEquals(1, validator.getEventsList().size());
	}
}
