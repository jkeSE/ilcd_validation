package com.okworx.ilcd.validation.test.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Locale;

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
import com.okworx.ilcd.validation.common.CustomValidationContext;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchemaValidatorTest extends AbstractValidatorTest {

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
	public void testPass() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(0, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidFlowsCount());
		assertEquals(40, validator.getStatistics().getValidSourcesCount());
		assertEquals(42, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(83, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testPassZIP() {

		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_pass.zip"));

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(0, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidFlowsCount());
		assertEquals(40, validator.getStatistics().getValidSourcesCount());
		assertEquals(42, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(83, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testPassZIPWithReportSuccesses() {

		this.validator.setReportSuccesses(true);
		
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_pass.zip"));

		assertTrue(result);
		assertEquals(83, validator.getEventsList().size());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(0, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidFlowsCount());
		assertEquals(40, validator.getStatistics().getValidSourcesCount());
		assertEquals(42, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(83, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testFailZIP() {
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		assertFalse(result);
		assertEquals(10, validator.getEventsList().size());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(0, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidFlowsCount());
		assertEquals(41, validator.getStatistics().getValidSourcesCount());
		assertEquals(43, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(1, validator.getStatistics().getInvalidContactsCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowsCount());
		assertEquals(0, validator.getStatistics().getInvalidSourcesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());
		assertEquals(0, validator.getStatistics().getInvalidUnitGroupsCount());

		assertEquals(85, validator.getStatistics().getTotalValidCount());
		assertEquals(2, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testFailZIPInvalidArchive() {

		boolean result = runValidation(new File("src/test/resources/archives/noLCD_fail_Schema.zip"));

		assertFalse(result);
		assertEquals(9, validator.getEventsList().size());

	}

	@Test
	public void testFailZIPInvalidArchiveNoArchiveValidation() {

		boolean checkArchives = this.validator.isValidateArchives();
		this.validator.setValidateArchives(false);

		boolean result = runValidation(new File("src/test/resources/archives/noLCD_fail_Schema.zip"));

		this.validator.setValidateArchives(checkArchives);
		assertFalse(result);
		assertEquals(8, validator.getEventsList().size());

	}

	@Test
	public void testFail() {
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		assertFalse(result);
		assertEquals(10, validator.getEventsList().size());

		assertEquals(1, validator.getStatistics().getValidContactsCount());
		assertEquals(0, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getValidFlowsCount());
		assertEquals(41, validator.getStatistics().getValidSourcesCount());
		assertEquals(43, validator.getStatistics().getValidProcessesCount());
		assertEquals(0, validator.getStatistics().getValidUnitGroupsCount());

		assertEquals(1, validator.getStatistics().getInvalidContactsCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowPropertiesCount());
		assertEquals(0, validator.getStatistics().getInvalidFlowsCount());
		assertEquals(0, validator.getStatistics().getInvalidSourcesCount());
		assertEquals(1, validator.getStatistics().getInvalidProcessesCount());
		assertEquals(0, validator.getStatistics().getInvalidUnitGroupsCount());

		assertEquals(85, validator.getStatistics().getTotalValidCount());
		assertEquals(2, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testFailLocalizedDe() throws Exception {
		setUp();
		CustomValidationContext ctx = new CustomValidationContext(Locale.GERMAN);
		this.validator.setValidationContext(ctx);
		
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		logEvents(validator);
		assertFalse(result);
		assertEquals(10, validator.getEventsList().size());

		assertEquals("Strommix", validator.getEventsList().getEvents().get(2).getReference().getName());
		
	}

	@Test
	public void testFailLocalizedEn() throws Exception {
		setUp();
		CustomValidationContext ctx = new CustomValidationContext(Locale.ENGLISH);
		this.validator.setValidationContext(ctx);
		
		boolean result = runValidation(new File("src/test/resources/datasets/Schema/test_fail.zip"));

		logEvents(validator);
		assertFalse(result);
		assertEquals(10, validator.getEventsList().size());

		assertEquals("Electricity Mix", validator.getEventsList().getEvents().get(2).getReference().getName());
		
	}

	@Test
	public void testFailInvalidFiles() {
		boolean result = runValidation(new File("src/test/resources/datasets/InvalidXML/empty_file.xml"));

		assertFalse(result);
		assertEquals(1, validator.getEventsList().size());

		assertFalse(validator.getEventsList().isPositive());
	}
	
//	@Test
	// run this as the last one as we're modifying the SchemaValidator's parameters
	public void testPassZZLCModel() {

		this.validator.setParameter(SchemaValidator.PARAM_IGNORE_REFERENCE_OBJECTS, false);
		
		boolean result = runValidation(new File("src/test/resources/datasets/LCModel/ILCD"));

//		assertTrue(result);
//		assertTrue(validator.getEventsList().isEmpty());

		
		System.out.println(validator.getStatistics().toString());
		
		assertEquals(17, validator.getStatistics().getValidContactsCount());
		assertEquals(34, validator.getStatistics().getValidFlowPropertiesCount());
		assertEquals(620, validator.getStatistics().getValidFlowsCount());
		assertEquals(176, validator.getStatistics().getValidSourcesCount());
		assertEquals(8, validator.getStatistics().getValidProcessesCount());
		assertEquals(20, validator.getStatistics().getValidUnitGroupsCount());
		assertEquals(1, validator.getStatistics().getValidLCModelsCount());

		assertEquals(886, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}



}
