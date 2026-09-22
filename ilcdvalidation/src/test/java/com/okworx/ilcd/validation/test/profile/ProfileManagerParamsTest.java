package com.okworx.ilcd.validation.test.profile;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.util.StandaloneLocator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileManagerParamsTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	@Before
	public void reset() {
		ProfileManager.getInstance().reset(true);
	}
	
	@Test
	public void testLoadDefaultProfile() throws IOException {
		File tempDir = Files.createTempDirectory("ilcd-test-profiles").toFile();

		new ProfileManager.ProfileManagerBuilder()
			.cacheDir(tempDir)
			.locator(new StandaloneLocator())
			.registerDefaultProfiles(true, false)
			.build();
		log.info(ProfileManager.getInstance().getCacheDir().getAbsolutePath());
		assertEquals(1, ProfileManager.getInstance().getProfiles().size());
	}

	@Test
	public void testLoadAllDefaultProfiles() throws IOException {
		File tempDir = Files.createTempDirectory("ilcd-test-profiles").toFile();
		new ProfileManager.ProfileManagerBuilder()
				.cacheDir(tempDir)
				.locator(new StandaloneLocator())
				.registerDefaultProfiles(true, true)
				.build();
		log.info(ProfileManager.getInstance().getCacheDir().getAbsolutePath());
		assertEquals(6, ProfileManager.getInstance().getProfiles().size());
	}

	@Test
	public void testLoadNoDefaultProfiles() throws IOException {
		File tempDir = Files.createTempDirectory("ilcd-test-profiles").toFile();
		new ProfileManager.ProfileManagerBuilder()
				.cacheDir(tempDir)
				.locator(new StandaloneLocator())
				.registerDefaultProfiles(false, false)
				.build();
		log.info(ProfileManager.getInstance().getCacheDir().getAbsolutePath());
		assertEquals(0, ProfileManager.getInstance().getProfiles().size());
	}

}
