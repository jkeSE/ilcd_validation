package com.okworx.ilcd.validation.test.validator;

import com.okworx.ilcd.validation.*;
import com.okworx.ilcd.validation.analyze.flows.FlowsAnalyzer;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;
import org.apache.logging.log4j.Logger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ValidatorChainWithElementaryFlowAnalysisTest extends AbstractValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected ValidatorChain validator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new ValidatorChain("Composite Validator");
		
//		IDatasetsValidator v1 = new SchemaValidator();
		IDatasetsValidator v2 = new FlowsAnalyzer();

		v2.setParameter(FlowsAnalyzer.PARAM_INCLUDE_SUMS, false);

//		this.validator.add(v1);
		this.validator.add(v2);

		this.validator.setBatchMode(true);

		this.validator.setProfile(ProfileManager.getInstance().registerProfile(TestConfig.getEFProfileURL()));
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean runValidation(File file) {
		
		validator.reset();
		
		log.info("setting file");
		
		validator.setObjectsToValidate(file);
		
		log.info("validating");
		
		boolean result = validator.validate();

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
			log.info(event);
		}

		return result;
	}

	@Test
	public void testPass() {

		long start = System.currentTimeMillis();

		boolean result = runValidation(new File("src/test/resources/datasets/Elementary_Flows/test_pass"));

		long stop = System.currentTimeMillis();

		log.info("operation took " + (stop-start) + "ms");

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

//		assertEquals(83, validator.getStatistics().getTotalValidCount());
//		assertEquals(0, validator.getStatistics().getTotalInvalidCount());


	}


	// TODO: add tests which check for proper number of sheets, number of entries in summary sheet
//	@Test
	public void testDev() {

		long start = System.currentTimeMillis();

		boolean result = runValidation(new File("/Users/oli/Nextcloud/soda4LCA/data/EF/EF_3.0_Sphera_EnergyTransportPackagingEoL/EF3_0_official_data_energy_transport_packaging_EoL_ILCD.zip"));

		long stop = System.currentTimeMillis();

		log.info("operation took " + (stop-start) + "ms");

		assertTrue(result);
		assertTrue(validator.getEventsList().isEmpty());

//		assertEquals(1399, validator.getStatistics().getTotalValidCount());
//		assertEquals(0, validator.getStatistics().getTotalInvalidCount());


	}
}
