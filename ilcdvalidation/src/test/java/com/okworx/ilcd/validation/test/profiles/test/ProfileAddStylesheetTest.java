package com.okworx.ilcd.validation.test.profiles.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.SchemaValidator;
import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.XSLTStylesheetValidator;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;
import com.okworx.ilcd.validation.test.validator.AbstractValidatorTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileAddStylesheetTest extends AbstractValidatorTest {
	
	public static final URL URL_TEST_ADDXSL = TestConfig.getAddStylesheetTestProfileURL();

	protected Profile addXslProfile;

	@Before
	public void registerProfile() throws IOException, InvalidProfileException {

	 	new ProfileManager.ProfileManagerBuilder().registerDefaultProfiles(false, false).build();

		ProfileManager pm = ProfileManager.getInstance();
		this.addXslProfile = pm.registerProfile(URL_TEST_ADDXSL);
	}
	
	@Test
	public void testSchemas() throws IOException, InvalidProfileException {
		ValidatorChain c = new ValidatorChain();
		
		c.add(new SchemaValidator());
		
		c.setProfile(this.addXslProfile);
		c.setObjectsToValidate(new File("src/test/resources/datasets/Test_Profiles/base"));
		
		boolean result = c.validate();

		log.info("validation result is " + result);
		
		assertTrue(result);
	}
	
	@Test
	public void testSchemasAndStylesheets() throws IOException, InvalidProfileException {
		ValidatorChain c = new ValidatorChain();
		
		c.add(new SchemaValidator());
		c.add(new XSLTStylesheetValidator());
		
		c.setProfile(this.addXslProfile);
		c.setObjectsToValidate(new File("src/test/resources/datasets/Test_Profiles/base"));
		
		boolean result = c.validate();

		log.info("validation result is " + result);
		
		logEvents(c);

		assertEquals(2, c.getEventsList().size());
		
		assertTrue(!result);

	}

}
