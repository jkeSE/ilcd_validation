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

import com.okworx.ilcd.validation.AbstractDatasetsValidator;
import com.okworx.ilcd.validation.LinkValidator;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LinkValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected LinkValidator validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);
		this.validator = new LinkValidator();
		this.validator.setParameter(LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS, true);
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(AbstractDatasetsValidator validator, File file) {
		validator.setObjectsToValidate(file);

		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());

		boolean result = false;

		try {
			result = validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
		}

		// logEvents(validator);

		return result;
	}

	@Test
	public void testPass() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_pass"));

		logEvents(validator);

		log.info(validator.getStatistics());

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(7, validator.getStatistics().getValidContactsCount());
		assertEquals(1, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(1, validator.getStatistics().getValidLCIAMethodsCount());
		assertEquals(5, validator.getStatistics().getValidFlowsCount());
		assertEquals(13, validator.getStatistics().getValidSourcesCount());
		assertEquals(2, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testFail() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(17, validator.getEventsList().size());

		log.info(validator.getStatistics());
		
		assertEquals(4, validator.getStatistics().getValidContactsCount());
		assertEquals(1, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(3, validator.getStatistics().getValidFlowsCount());
		assertEquals(8, validator.getStatistics().getValidSourcesCount());
		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(1, validator.getStatistics().getInvalidContactsCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowsCount());
		assertEquals(3, validator.getStatistics().getInvalidSourcesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());
		assertEquals(0, validator.getStatistics().getInvalidUnitGroupsCount());

		assertEquals(5, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testSuccessSkipReferenceObjects() {

		validator.setParameter(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);

		Profile p = TestConfig.getEFProfile();
		validator.setProfile(p);
		
		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_skiprefobjects"));
		logEvents(validator);

		log.info(validator.getStatistics());
		log.info( validator.getEventsList().size() + " events");
		
		assertTrue(result);
		assertEquals(0, validator.getEventsList().size());
		
		assertEquals(2, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testFailSkipReferenceObjects() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_skiprefobjects"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(11, validator.getEventsList().size());

		log.info(validator.getStatistics());
		
		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(2, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(3, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testSuccessSkipCP() {

		validator.reset();
		validator.setParameter(LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS, true);
		validator.setParameter(LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS, true);

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(16, validator.getEventsList().size());

		log.info(validator.getStatistics());
		
		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testSuccessSkipSIAM() {

		validator.setParameter(LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS, true);

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(15, validator.getEventsList().size());

		log.info(validator.getStatistics());
		
		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testSuccessSkipPrecedingDatasetVersion() {

		validator.setParameter(LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION, true);

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(17, validator.getEventsList().size());

		log.info(validator.getStatistics());

		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testSuccessSkipCPAndSIAM() {

		validator.setParameter(LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS, true);
		validator.setParameter(LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS, true);

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(14, validator.getEventsList().size());

		log.info(validator.getStatistics());
		
		assertEquals(0, validator.getStatistics().getValidProcessesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testPassZIP() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_pass.zip"));

		logEvents(validator);

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

	}

	@Test
	public void testFailZIP() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_fail.zip"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(14, validator.getEventsList().size());

	}

	@Test
	public void testUri() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_uri"));

		logEvents(validator);

		assertFalse(result);
		assertEquals(5, validator.getEventsList().size());

	}
	
//	@Test
	public void testPassLCModel() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/LCModel/ILCD"));

		logEvents(validator);

		log.info(validator.getStatistics());

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(17, validator.getStatistics().getValidContactsCount());
		assertEquals(34, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidLCIAMethodsCount());
		assertEquals(620, validator.getStatistics().getValidFlowsCount());
		assertEquals(176, validator.getStatistics().getValidSourcesCount());
		assertEquals(8, validator.getStatistics().getValidProcessesCount());
		assertEquals(20, validator.getStatistics().getValidUnitGroupsCount());
		assertEquals(1, validator.getStatistics().getValidLCModelsCount());

		assertEquals(0, validator.getStatistics().getTotalInvalidCount());
	}

	@Test
	public void testLinkImagesExtensionsPass() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_images_extensions_pass"));

		logEvents(validator);

		assertTrue(result);
		assertEquals(0, validator.getEventsList().size());
	}

	@Test
	public void testLinkImagesExtensionsSucceed() {

		boolean result = runValidation(validator, new File("src/test/resources/datasets/Links/test_images_extensions_fail"));

		logEvents(validator);

		assertTrue(result);
		assertEquals(1, validator.getEventsList().size());
		assertEquals( Integer.valueOf(1), validator.getEventsList().getWarningCount());
	}
}
