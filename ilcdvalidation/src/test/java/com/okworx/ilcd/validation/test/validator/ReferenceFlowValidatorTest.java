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

import com.okworx.ilcd.validation.ReferenceFlowValidator;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReferenceFlowValidatorTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	public static final int numberOfFlows = 41360;
	
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

	private ReferenceFlowValidator runValidation(File toValidate) {
		return runValidation(toValidate, false);
	}

	private ReferenceFlowValidator runValidation(File toValidate, boolean reportSuccesses ) {
		ReferenceFlowValidator validator = new ReferenceFlowValidator();
		validator.setValidateArchives(false);
		validator.setUpdateEventListener(new SimpleUpdateEventListener());
		validator.setReportSuccesses(reportSuccesses);
//		try {
//			Profile p = ProfileManager.INSTANCE.registerProfile(new URL("file:///Users/oliver.kusche/git/ilcdvalidation/target/profiles/EF_profile_1.0.8.jar"));
//			validator.setProfile(p);
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (InvalidProfileException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		validator.setObjectsToValidate(toValidate);

		try {
			validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
		}

		// logEvents(validator);

		return validator;
	}

	@Test
	public void testSuccessWithDefaultFlows() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_pass"));
		assertTrue(validator.getEventsList().isEmpty());
	}

	@Test
	public void testSuccess() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_pass"));

		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

		log.info(validator.getStatistics());
		
		assertEquals(4, validator.getStatistics().getValidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

//	@Test
//	public void testSuccessEF() {
//		ReferenceFlowValidator validator = runValidation(new File(
//				"/Users/oliver.kusche/Nextcloud/Projects/PEF/2017_tenders/data/thinkstep/EOL/v1/04 to remodellers/thinkstep_EoL_data_EF-v1.8.9_(14_07_2017).zip"));
//
//
//		logEvents(validator);
//
//		log.info(validator.getStatistics());
//		
//		assertTrue(validator.getEventsList().isEmpty());
//
//	}
//
	
	@Test
	public void testSuccessWithReportSuccesses() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_pass"), true);

		logEvents(validator);
		
		log.info(validator.getStatistics());

		assertFalse(validator.getEventsList().isEmpty());

		assertTrue(validator.getEventsList().isPositive());

		assertEquals(4, validator.getEventsList().size());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

		assertEquals(4, validator.getStatistics().getValidProcessesCount());

		assertEquals(5, validator.getStatistics().getTotalValidCount());
		assertEquals(0, validator.getStatistics().getTotalInvalidCount());

	}

//	@Test
	public void testEFSuccess() {
		ReferenceFlowValidator validator = runValidation(new File(
				"/Users/oliver.kusche/Desktop/EF_energy_transport_data"));

		logEvents(validator);

		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

		
	}

	@Test
	public void testSuccessSingleFile() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_pass/ILCD/processes/0a1b40db-5645-4db8-a887-eb09300b7b74.xml"));

		logEvents(validator);

		assertEquals(2, validator.getEventsList().size());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

	}

	@Test
	public void testFailSingleFile() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_fail/ILCD/processes/0a1b40db-5645-4db8-a887-eb09300b7b74.xml"));

		logEvents(validator);

		assertEquals(8, validator.getEventsList().size());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

	}

	@Test
	public void testFailSingleFile2() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_fail/ILCD/processes/0b1b40db-5645-4db8-a887-eb09300b7b74.xml"));

		logEvents(validator);

		assertEquals(1, validator.getEventsList().size());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

	}

	@Test
	public void testFail() {

		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_fail"));

		logEvents(validator);

		log.info(validator.getStatistics());
		assertFalse(validator.getEventsList().isEmpty());
		assertEquals(19, validator.getEventsList().size());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());
		
		assertEquals(1, validator.getStatistics().getValidProcessesCount());
		assertEquals(4, validator.getStatistics().getInvalidProcessesCount());

		assertEquals(2, validator.getStatistics().getTotalValidCount());
		assertEquals(4, validator.getStatistics().getTotalInvalidCount());

	}

	@Test
	public void testSuccessZIP() {

		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_pass.zip"));

		assertTrue(validator.getEventsList().isEmpty());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());

	}

	@Test
	public void testFailZIP() {
		ReferenceFlowValidator validator = runValidation(new File(
				"src/test/resources/datasets/ReferenceFlows/test_fail.zip"));

		logEvents(validator);

		assertFalse(validator.getEventsList().isEmpty());

		assertEquals(numberOfFlows, validator.getReferenceElementaryFlows().size());
	}

//	@Test
	public void testELCDIII() {

		long start = System.currentTimeMillis();

		ReferenceFlowValidator validator = new ReferenceFlowValidator();
		validator.setUpdateEventListener(new SimpleUpdateEventListener());

		validator.setObjectsToValidate(new File("/Users/oliver.kusche/Downloads/ELCDIII"));

		try {
			validator.validate();
		} catch (InterruptedException e) {
			log.error(e);
		}

		long stop = System.currentTimeMillis();

//		logEvents(validator);

		log.info("time to validate: " + (stop - start) + " ms");

		log.info(validator.getEventsList().size());

		assertTrue(validator.getEventsList().isEmpty());
	}

}
