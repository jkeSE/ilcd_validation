package com.okworx.ilcd.validation.profile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.okworx.ilcd.validation.profile.update.MavenVersionChecker;
import com.okworx.ilcd.validation.profile.update.MavenVersionComparator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.common.Constants;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.util.Locator;
import com.okworx.ilcd.validation.util.StandaloneLocator;

/**
 * This purpose of this class is to hold references to various profiles that
 * might be used for validation. See the Profiles section in the documentation
 * for more information on profiles.
 *
 * Example for passing parameters during initialization:
 *
 * new ProfileManager.ProfileManagerBuilder().cacheDir(File dir).locator(new
 * EclipseLocator()).build();
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ProfileManager {

	public static final String MAVEN_CENTRAL_URL = "https://repo1.maven.org/maven2/";

	private static volatile ProfileManager SINGLETON_INSTANCE;

	private final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	private Map<MavenCoordinates, Profile> profileStore = new HashMap<>();

	private Locator locator = new StandaloneLocator();

	private File cacheDir = new File(org.apache.commons.io.FileUtils.getTempDirectoryPath() + File.separator
			+ System.currentTimeMillis());

	private boolean registerDefaultProfile = true;

	private boolean registerDefaultSecondaryProfiles = false;

	private URL defaultProfileURL;
	private MavenCoordinates defaultProfileMavenCoordinates;

	final String MAX_XPATH_GROUP_LIMIT = "jdk.xml.xpathExprGrpLimit";
	final String MAX_XPATH_OPERATOR_LIMIT = "jdk.xml.xpathExprOpLimit";
	final String MAX_XPATH_TOTAL = "jdk.xml.xpathTotalOpLimit";

	private ProfileManager() {
		init();
	}

	private ProfileManager(ProfileManagerBuilder builder) {
		this.locator = builder.locator;
		this.cacheDir = builder.cacheDir;
		this.registerDefaultProfile = builder.registerDefaultProfile;
		this.registerDefaultSecondaryProfiles = builder.registerDefaultSecondaryProfiles;
		init();
	}

	/**
	 * <p>getInstance.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.profile.ProfileManager} object.
	 */
	public static ProfileManager getInstance() {
		if(SINGLETON_INSTANCE == null) {
			synchronized(ProfileManager.class) {
				if(SINGLETON_INSTANCE == null) {
					SINGLETON_INSTANCE = new ProfileManager();
				}
			}
		}
		return SINGLETON_INSTANCE;
	}

	private void init() {
		try {

//			System.setProperty(MAX_XPATH_GROUP_LIMIT, "10");
			System.setProperty(MAX_XPATH_OPERATOR_LIMIT, "200");
//			System.setProperty(MAX_XPATH_TOTAL, "10000");

			if (!this.cacheDir.exists()) {
				if (log.isTraceEnabled()) {
                    log.trace("cache dir {} does not exist. Creating...", this.cacheDir.getAbsolutePath());
				}
				FileUtils.forceMkdir(this.cacheDir);
				FileUtils.forceDeleteOnExit(this.cacheDir);
			}

			// register default profile
			if (this.registerDefaultProfile) {
				registerDefaultProfile();
			}

			// if loading of secondary default profiles is configured (which is false by default), register them 
			if (this.registerDefaultSecondaryProfiles) {
				registerDefaultSecondaryProfiles();
			}

		} catch (Exception e) {
			log.error(e);
		}
	}

	public void registerDefaultSecondaryProfiles()
			throws MalformedURLException, URISyntaxException, InvalidProfileException {
		if (log.isDebugEnabled())
			log.debug("Registering secondary default profiles");
		for (String profilePath : Constants.DEFAULT_SECONDARY_PROFILE_JARS) {
			URL url = this.getClass().getClassLoader().getResource(profilePath).toURI().toURL();
			registerProfile(url);
		}
	}

	public void registerDefaultProfile() throws IOException, InvalidProfileException {
		this.defaultProfileURL = this.getClass().getClassLoader().getResource(Constants.DEFAULT_PROFILE_JAR);
		this.defaultProfileMavenCoordinates = new MavenCoordinates(Constants.DEFAULT_PROFILE_GROUPID, Constants.DEFAULT_PROFILE_ARTIFACTID, Constants.DEFAULT_PROFILE_VERSION);

		if (log.isTraceEnabled()) {
			log.trace("trying to register default profile " + Constants.DEFAULT_PROFILE_JAR);
            log.trace("profile URL is  {}", this.defaultProfileURL);
            log.trace("resolving to {}", locator.resolve(this.defaultProfileURL).toString());
            log.trace("using locator {}", this.locator.getClass().getCanonicalName());
            log.trace("using cache dir {}", this.cacheDir.getPath());
		}

		registerProfile(this.defaultProfileURL);
	}

	public void reset() {
		reset(false);
	}

	public void reset(boolean hardReset) {
		this.profileStore = new HashMap<>();

		if (hardReset || this.registerDefaultProfile) {
			try {
				this.registerDefaultProfile();
			} catch (IOException | InvalidProfileException e) {
				log.error("could not register default profile", e);
			}
		}

		// if loading of secondary default profiles is configured (which is false by default), register them 
		if (this.registerDefaultSecondaryProfiles) {
			try {
				this.registerDefaultSecondaryProfiles();
			} catch (IOException | InvalidProfileException | URISyntaxException e) {
				log.error("could not register default secondary profiles", e);
			}
		}
	}

	public Profile registerProfile(MavenCoordinates mavenCoordinates) throws InvalidProfileException {
		if (StringUtils.isBlank(mavenCoordinates.getVersion())) {
			String latestVersion = MavenVersionChecker.checkForNewerVersion(mavenCoordinates);
			mavenCoordinates = new MavenCoordinates(mavenCoordinates.getGroupId(), mavenCoordinates.getArtifactId(), latestVersion);
		}
		String profileUrl = MAVEN_CENTRAL_URL + mavenCoordinates.getGroupId().replace('.', '/') + "/" + mavenCoordinates.getArtifactId() + "/" + mavenCoordinates.getVersion() + "/" + mavenCoordinates.getArtifactId() + "-" + mavenCoordinates.getVersion() + ".jar";
        try {
            return registerProfile(new URL(profileUrl));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

		/**
         * <p>registerProfile.</p>
         *
         * @param url a {@link java.net.URL} object.
         * @return a {@link com.okworx.ilcd.validation.profile.Profile} object.
         * @throws com.okworx.ilcd.validation.exception.InvalidProfileException if any.
         */
	public Profile registerProfile(URL url) throws InvalidProfileException {

		if (url == null) throw new InvalidProfileException();

        log.debug("registering profile at {}", url);

		try {
			Profile profile = getProfile(url);

			this.profileStore.put(profile.getMavenCoordinates(), profile);

			return profile;

		} catch (IOException e) {
			log.error(e);
			throw new InvalidProfileException();
		}
	}

	/**
	 * Parse a profile under the given URL and return a {@link com.okworx.ilcd.validation.profile.Profile} object
	 * 
	 * @param url
	 * @return the {@link com.okworx.ilcd.validation.profile.Profile}
	 * @throws IOException
	 * @throws InvalidProfileException
	 */
	public Profile getProfile(URL url) throws IOException, InvalidProfileException {

        log.trace("resolving to {}", locator.resolve(url));

		File physicalJar = extractJar(locator.resolve(url));

		JarFile jar = new JarFile(physicalJar);

		Manifest m = jar.getManifest();

		Profile profile = new Profile(url);

		profile.setJarFile(jar);
		profile.setPath(physicalJar);

		MavenCoordinates mavenCoordinates = MavenCoordinatesReader.readMavenCoordinatesFromJar(physicalJar.toPath());
		profile.setMavenCoordinates(mavenCoordinates);

		Attributes atts = m.getAttributes("ILCD-Validator-Profile");

		if (atts == null) {
			jar.close();
			throw new InvalidProfileException();
		}

		return ProfileManifestParser.parseManifest(profile, atts);
	}

	public Profile getProfile(MavenCoordinates mavenCoordinates) {
		return this.profileStore.get(mavenCoordinates);
	}

	public MavenCoordinates updateProfile(MavenCoordinates mavenCoordinates) throws InvalidProfileException {
		String newerVersion = MavenVersionChecker.checkForNewerVersion(mavenCoordinates);
		if (newerVersion != null) {
			registerProfile(new MavenCoordinates(mavenCoordinates.getGroupId(), mavenCoordinates.getArtifactId(), newerVersion));
			return new MavenCoordinates(mavenCoordinates.getGroupId(), mavenCoordinates.getArtifactId(), newerVersion);
		}
		return null;
	}

	public void updateProfiles() throws InvalidProfileException {
		for (MavenCoordinates c : this.profileStore.keySet())
			updateProfile(c);
	}

	public Profile getLatestProfile(MavenCoordinates mavenCoordinates) {
		return this.profileStore.get(getLatestCoordinates(mavenCoordinates));
	}

	/**
	 * Finds the MavenCoordinates with the highest version for a given MavenCoordinates' groupId and artifactId.
	 *
	 * @param coordinates The MavenCoordinates containing groupId and artifactId
	 * @return Optional containing the MavenCoordinates with the highest version, or empty if none found
	 */
	protected Optional<MavenCoordinates> getLatestCoordinates(MavenCoordinates coordinates) {

		// Use MavenVersionComparator for version comparison
		Comparator<String> versionComparator = new MavenVersionComparator();

		return profileStore.keySet().stream()
				.filter(coords -> coordinates.getGroupId().equals(coords.getGroupId()) &&
						coordinates.getArtifactId().equals(coords.getArtifactId()))
				.max(Comparator.comparing(MavenCoordinates::getVersion,
						Comparator.nullsLast(versionComparator)));
	}

	/**
	 * <p>deregisterProfile.</p>
	 *
	 * @param profile a {@link com.okworx.ilcd.validation.profile.Profile} object.
	 */
	public void deregisterProfile(Profile profile) {
		this.profileStore.remove(profile.getMavenCoordinates());
	}

	private File extractJar(URL path) {
		try {

			if (log.isDebugEnabled())
                log.debug("extracting profile with URL {} to cache dir", path);
			
			String plainName = FilenameUtils.getName(path.getFile());

			File extractedJar = new File(this.cacheDir.getAbsolutePath() + File.separator + plainName);

			if (!path.equals(extractedJar.toURI().toURL())) {
				FileUtils.copyInputStreamToFile(path.openStream(), extractedJar);				
			}

			if (log.isDebugEnabled())
                log.debug("extracted at {}", extractedJar.getAbsolutePath());
			
			return extractedJar;
		} catch (Exception ex) {
			log.error(ex);
		}

		return null;
	}

	protected boolean supportsMetaDataVersion(Profile profile, Double version) {
		if (version == null)
			return false;
		return (profile.getProfileMetaDataVersion() >= version);
	}

	/**
	 * <p>getProfiles.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<Profile> getProfiles() {
		return this.profileStore.values();
	}

	/**
	 * <p>getDefaultProfile.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.profile.Profile} object.
	 */
	public Profile getDefaultProfile() {
		return this.profileStore.get(this.defaultProfileMavenCoordinates);
	}

	/**
	 * <p>Getter for the field <code>locator</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.Locator} object.
	 */
	public Locator getLocator() {
		return locator;
	}

	/**
	 * <p>Setter for the field <code>locator</code>.</p>
	 *
	 * @param locator a {@link com.okworx.ilcd.validation.util.Locator} object.
	 */
	public void setLocator(Locator locator) {
		this.locator = locator;
	}

	/**
	 * <p>Getter for the field <code>cacheDir</code>.</p>
	 *
	 * @return a {@link java.io.File} object.
	 */
	public File getCacheDir() {
		return cacheDir;
	}

	/**
	 * <p>Setter for the field <code>cacheDir</code>.</p>
	 *
	 * @param cacheDir a {@link java.io.File} object.
	 */
	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}

	private static void build(ProfileManagerBuilder builder) {
		SINGLETON_INSTANCE = new ProfileManager(builder);
	}

	/**
	 * @return registerDefaultProfile
	 */
	public boolean isRegisterDefaultProfile() {
		return registerDefaultProfile;
	}

	/**
	 * @param registerDefaultProfile the registerDefaultProfile to set
	 */
	public void setRegisterDefaultProfile(boolean registerDefaultProfile) {
		this.registerDefaultProfile = registerDefaultProfile;
	}

	/**
	 * @return registerDefaultSecondaryProfiles
	 */
	public boolean isRegisterDefaultSecondaryProfiles() {
		return registerDefaultSecondaryProfiles;
	}

	/**
	 * @param registerDefaultSecondaryProfiles registerDefaultSecondaryProfiles to set
	 */
	public void setRegisterDefaultSecondaryProfiles(boolean registerDefaultSecondaryProfiles) {
		this.registerDefaultSecondaryProfiles = registerDefaultSecondaryProfiles;
	}

	/**
	 * Use this to build an instance of ProfileManager with a custom
	 * configuration
	 *
	 */
	public static class ProfileManagerBuilder {

		private Locator locator = new StandaloneLocator();
		private File cacheDir = new File(org.apache.commons.io.FileUtils.getTempDirectoryPath() + File.separator
				+ System.currentTimeMillis());
		private boolean registerDefaultProfile = true;
		private boolean registerDefaultSecondaryProfiles = false;

		public ProfileManagerBuilder() {
		}

		public ProfileManagerBuilder locator(Locator locator) {
			this.locator = locator;
			return this;
		}

		public ProfileManagerBuilder cacheDir(File cacheDir) {
			this.cacheDir = cacheDir;
			return this;
		}

		public ProfileManagerBuilder registerDefaultProfiles(boolean regDefault, boolean regDefaultSec) {
			this.registerDefaultProfile = regDefault;
			this.registerDefaultSecondaryProfiles = regDefaultSec;
			return this;
		}

		public void build() {
			ProfileManager.build(this);
		}
	}
}
