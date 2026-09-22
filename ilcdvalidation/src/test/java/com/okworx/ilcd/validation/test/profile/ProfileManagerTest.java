package com.okworx.ilcd.validation.test.profile;

import com.okworx.ilcd.validation.profile.update.MavenVersionComparator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.okworx.ilcd.validation.profile.MavenCoordinates;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.test.TestConfig;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProfileManagerTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	@Before
	public void reset() {
		ProfileManager.getInstance().reset(true);
	}

	@Test
	public void testLoadProfile() throws IOException {

		ProfileManager pm = ProfileManager.getInstance();
		try (final InputStream is = pm.getDefaultProfile().getResourceAsStream("eu/europa/ec/jrc/lca/ilcd/schemas/ext/xml.xsd")) {
			log.info(is.available());
		}
	}

	@Test
	public void testLoadDefaultProfile() {
		ProfileManager.getInstance();
	}

	@Test
	public void testLoadEPDProfile() throws InvalidProfileException {

		ProfileManager pm = ProfileManager.getInstance();
		pm.registerProfile(TestConfig.getEPDProfileURL());

	}

	@Test
	public void testProfileHasVersion() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		assertEquals(pm.getProfile(TestConfig.getEFProfileURL()).getVersion(), TestConfig.EF_PROFILE_VERSION);
	}

	@Test
	public void testProfileIsComposed() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		assertFalse(pm.getProfile(TestConfig.getELProfileURL()).isComposed());
		assertTrue(pm.getProfile(TestConfig.getSchemaTestProfileURL()).isComposed());
	}

	@Test
	public void schemaBundlesExists() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile.Bundle<String[]>[] paths = pm.getProfile(TestConfig.getSchemaTestProfileURL()).getSchemaBundles();
		assertEquals(paths.length, 2);
		assertEquals(paths[0].getResource().length, 1);
		assertEquals(paths[1].getResource().length, 7);
	}

	@Test
	public void stylesheetBundlesExists() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile.Bundle<String>[] paths = pm.getProfile(TestConfig.getAddStylesheetTestProfileURL()).getStylesheetBundles();
		assertEquals(paths.length, 2);
		assertNotNull(paths[0].getResource());
		assertNotNull(paths[1].getResource());
	}

	@Test
	public void correctCategoriesFile() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile.Bundle<String> categoriesFile = pm.getProfile(TestConfig.getCategoriesTestProfileURL()).getCategoriesBundle();
		assertEquals(categoriesFile.getResource(), "com/okworx/test/add_categories/categories/CPC_2_1.xml");
	}

	@Test
	public void hasChangelog() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		assertNotNull(pm.getProfile(TestConfig.getEFProfileURL()).getSemanticChangelog());
		assertNotNull(pm.getProfile(TestConfig.getEFProfileURL()).getTechnicalChangelog());
	}

	@Test
	public void overridesElementaryFlow() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile.Bundle<String> elFlow = pm.getProfile(TestConfig.getRefObjectsTestProfileURL()).getReferenceElementaryFlows();
		assertTrue(elFlow.getJarPath().contains("refobjects"));
		assertEquals(elFlow.getUrlPrefix(), "com/okworx/test/reference");
		assertEquals(elFlow.getResource(), "EPD_elementary_flows_03a_2019.ser.z");
	}

	@Test
	public void addRefObjects() throws IOException, InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile.Bundle<String>[] bundles = pm.getProfile(TestConfig.getRefObjectsTestProfileURL()).getReferenceObjectsOther();

		assertEquals(bundles.length, 2);
		assertTrue(bundles[0].getJarPath().contains("refobjects"));
		assertEquals(bundles[0].getUrlPrefix(), "com/okworx/test/reference");
		assertEquals(bundles[0].getResource(), "EPD_reference_objects_03a_2019.ser.z");
		assertTrue(bundles[1].getJarPath().contains("test-base"));
		assertEquals(bundles[1].getUrlPrefix(), "com/okworx/test");
		assertEquals(bundles[1].getResource(), "EF_reference_objects_rev3.0.ser.z");
	}

	@Test
	public void testReadMavenCoordinates() throws InvalidProfileException, IOException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile efProfile = pm.getProfile(TestConfig.getEFProfileURL());
		assertEquals(TestConfig.EF_PROFILE_GROUPID, efProfile.getMavenCoordinates().getGroupId());
		assertEquals(TestConfig.EF_PROFILE_ARTIFACTID, efProfile.getMavenCoordinates().getArtifactId());
		assertEquals(TestConfig.EF_PROFILE_VERSION, efProfile.getMavenCoordinates().getVersion());
		assertEquals(TestConfig.EF_PROFILE_VERSION, efProfile.getVersion());
	}

	@Test
	public void testLoadRemoteProfileByUrl() throws InvalidProfileException, IOException {
		ProfileManager pm = ProfileManager.getInstance();
		Profile epdProfile = pm.registerProfile(new URL("https://repo1.maven.org/maven2/com/okworx/ilcd/validation/profiles/EF-1.0/1.0.11/EF-1.0-1.0.11.jar"));
		log.debug(epdProfile.getMavenCoordinates());
		Profile epdProfileLocal = pm.registerProfile(new File("src/test/resources/profiles/EF-1.0-1.0.11.jar").toURI().toURL());
		log.debug(epdProfileLocal.getMavenCoordinates());

		log.debug("all profiles: ");
		pm.getProfiles().forEach(p -> log.debug(p.getMavenCoordinates()));

		assertEquals(2, pm.getProfiles().size());
		assertEquals(epdProfileLocal.getMavenCoordinates(), epdProfile.getMavenCoordinates());
	}

	@Test
	public void testLoadRemoteProfileByCoordinates() throws InvalidProfileException {
		ProfileManager pm = ProfileManager.getInstance();
		MavenCoordinates mc = new MavenCoordinates("com.okworx.ilcd.validation.profiles", "EPD-1.2-Generic-EN15804", "2.5.1");
		Profile epdProfile = pm.registerProfile(mc);
		assertEquals(mc, epdProfile.getMavenCoordinates());
	}

	@Test
	public void testLoadRemoteTwice() throws InvalidProfileException, IOException {
		testLoadRemoteProfileByUrl();
		testLoadRemoteProfileByUrl();
	}

	@Test
	public void testRemoteUpdate() throws InvalidProfileException {
		final ProfileManager pm = ProfileManager.getInstance();
		pm.reset(true);
		final MavenCoordinates mc = new MavenCoordinates("com.okworx.ilcd.validation.profiles", "EPD-1.2-Generic-EN15804", null);
		final Profile profile = pm.registerProfile(mc);
		final MavenCoordinates mc2 = profile.getMavenCoordinates();
		assertNotEquals(mc, mc2);
		assertEquals(mc.getGroupId(), mc2.getGroupId());
		assertEquals(mc.getArtifactId(), mc2.getArtifactId());
		assertEquals(-1, new MavenVersionComparator().compare(mc.getVersion(), mc2.getVersion()));
	}
}
