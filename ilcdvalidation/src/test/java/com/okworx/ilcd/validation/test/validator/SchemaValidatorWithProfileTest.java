package com.okworx.ilcd.validation.test.validator;

import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.util.SimpleUpdateEventListener;

// TODO add more test vectors 
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchemaValidatorWithProfileTest extends SchemaValidatorTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	@Override
	public void setUp() throws Exception {
		ProfileManager.getInstance().reset(true);

		this.validator = new SchemaValidator();

		this.validator.setValidateArchives(true);

		this.validator.setUpdateEventListener(new SimpleUpdateEventListener());

		this.validator.setProfile(ProfileManager.getInstance().getDefaultProfile());
	}

//	@Test
//	public void validateWithProfile() throws InvalidProfileException, InterruptedException {
//		this.validator.setProfile(ProfileManager.INSTANCE.registerProfile(TestConfig.getEFProfileURL()));
//		this.validator.setParameter(SchemaValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);
//		
//		this.validator.setObjectsToValidate(new File("/Users/oliver.kusche/Downloads/Delivery+project+EF+3.0+-+aggs+1st+level+disagg+and+complementing+2019_10_23/ILCD/flows"));
//		
//		boolean result = this.validator.validate();
//		log.info(result);
//	}
	
}
