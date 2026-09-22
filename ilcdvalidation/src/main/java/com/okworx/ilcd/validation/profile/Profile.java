package com.okworx.ilcd.validation.profile;

import com.okworx.ilcd.validation.util.PrefixBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * <p>Profile class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class Profile {

	private URL jarUrl = null;

	private File path = null;

	private Double profileMetaDataVersion = null;

	private String name = null;
	private String description = null;

	private MavenCoordinates mavenCoordinates = null;
	private String version = null;

	private Bundle<String[]>[] schemaBundles = null;

	private Bundle<String> categoriesBundle;

	private Bundle<String>[] stylesheetBundles = null;
	private String stylesheetsPath = null;

	private Bundle<String> referenceElementaryFlows = null;
	private Bundle<String>[] referenceObjectsOther = null;

	private String activeAspects = null;
	// Profile-SupportedAspects
	private String supportedAspects = null;

	private String readme = null;

	private String technicalChangelog = null;
	private String semanticChangelog = null;

	private IncludedProfile[] includes = null;

	private JarFile jarFile = null;

	/**
	 * <p>Constructor for Profile.</p>
	 *
	 * @param jarUrl a {@link java.net.URL} object.
	 */
	public Profile(URL jarUrl) {
		this.jarUrl = jarUrl;
	}

	/**
	 * <p>getResourceAsStream.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public InputStream getResourceAsStream(String name) throws IOException {
		return jarFile.getInputStream(jarFile.getEntry(name));
	}

	/**
	 * <p>getURLPrefix.</p>
	 *
	 * @param resourcePath a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String getURLPrefix(String resourcePath) {
		return PrefixBuilder.buildPrefix(this.jarUrl.toString(), resourcePath);
	}

	/**
	 * <p>getURLPrefix.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getURLPrefix() {
		return PrefixBuilder.buildPrefix(this.jarUrl.toString(), "");
	}

	/**
	 * <p>getURL.</p>
	 *
	 * @return a {@link java.net.URL} object.
	 */
	public URL getURL() {
		return jarUrl;
	}

	/**
	 * <p>setURL.</p>
	 *
	 * @param jarUrl a {@link java.net.URL} object.
	 */
	public void setURL(URL jarUrl) {
		this.jarUrl = jarUrl;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>Getter for the field <code>schemaBundles</code>.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public Bundle<String[]>[] getSchemaBundles() {
		return schemaBundles;
	}

	/**
	 * <p>Setter for the field <code>schemaBundles</code>.</p>
	 *
	 * @param schemaBundles an array of {@link java.lang.String} objects.
	 */
	public void setSchemaBundles(Bundle<String[]>[] schemaBundles) {
		this.schemaBundles = schemaBundles;
	}

	/**
	 * <p>Getter for the field <code>categoriesFile</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public Bundle<String> getCategoriesBundle() {
		return categoriesBundle;
	}

	/**
	 * <p>Setter for the field <code>categoriesFile</code>.</p>
	 *
	 * @param categoriesBundle a {@link java.lang.String} object.
	 */
	public void setCategoriesBundle(Bundle<String> categoriesBundle) {
		this.categoriesBundle = categoriesBundle;
	}

	/**
	 * <p>Getter for the field <code>stylesheetsPath</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStylesheetsPath() {
		return stylesheetsPath;
	}

	/**
	 * <p>Setter for the field <code>stylesheetsPath</code>.</p>
	 *
	 * @param stylesheetsPath a {@link java.lang.String} object.
	 */
	public void setStylesheetsPath(String stylesheetsPath) {
		this.stylesheetsPath = stylesheetsPath;
	}

	/**
	 * <p>Getter for the field <code>stylesheetBundles</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public Bundle<String>[] getStylesheetBundles() {
		return this.stylesheetBundles;
	}

	/**
	 * <p>Setter for the field <code>stylesheetBundles</code>.</p>
	 *
	 * @param stylesheetBundles a {@link java.lang.String} object.
	 */
	public void setStylesheetBundles(Bundle<String>[] stylesheetBundles) {
		this.stylesheetBundles = stylesheetBundles;
	}


	/**
	 * <p>Getter for the field <code>jarFile</code>.</p>
	 *
	 * @return a {@link java.util.jar.JarFile} object.
	 */
	public JarFile getJarFile() {
		return jarFile;
	}

	/**
	 * <p>Setter for the field <code>jarFile</code>.</p>
	 *
	 * @param jarFile a {@link java.util.jar.JarFile} object.
	 */
	public void setJarFile(JarFile jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Profile)) return false;

		Profile profile = (Profile) o;
		return mavenCoordinates.equals(profile.mavenCoordinates);
	}

	@Override
	public int hashCode() {
		return mavenCoordinates.hashCode();
	}

	/**
	 * <p>Getter for the field <code>mavenCoordinates</code>.</p>
	 *
	 * @return a {@link MavenCoordinates} object.
	 */
	public MavenCoordinates getMavenCoordinates() {
		return mavenCoordinates;
	}

	/**
	 * <p>Setter for the field <code>mavenCoordinates</code>.</p>
	 *
	 * @param mavenCoordinates a {@link MavenCoordinates} object.
	 */
	public void setMavenCoordinates(MavenCoordinates mavenCoordinates) {
		this.mavenCoordinates = mavenCoordinates;
	}

	/**
	 * <p>Getter for the field <code>version</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <p>Setter for the field <code>version</code>.</p>
	 *
	 * @param version a {@link java.lang.String} object.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * <p>Getter for the field <code>path</code>.</p>
	 *
	 * @return a {@link java.io.File} object.
	 */
	public File getPath() {
		return path;
	}

	/**
	 * <p>Setter for the field <code>path</code>.</p>
	 *
	 * @param path a {@link java.io.File} object.
	 */
	public void setPath(File path) {
		this.path = path;
	}

	/**
	 * <p>Getter for the field <code>referenceElementaryFlows</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public Bundle<String> getReferenceElementaryFlows() {
		return referenceElementaryFlows;
	}

	/**
	 * <p>Setter for the field <code>referenceElementaryFlows</code>.</p>
	 *
	 * @param referenceElementaryFlows a {@link java.lang.String} object.
	 */
	public void setReferenceElementaryFlows(Bundle<String> referenceElementaryFlows) {
		this.referenceElementaryFlows = referenceElementaryFlows;
	}

	/**
	 * <p>Getter for the field <code>referenceObjectsOther</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public Bundle<String>[] getReferenceObjectsOther() {
		return referenceObjectsOther;
	}

	/**
	 * <p>Setter for the field <code>referenceObjectsOther</code>.</p>
	 *
	 * @param referenceObjects a {@link java.lang.String} object.
	 */
	public void setReferenceObjectsOther(Bundle<String>[] referenceObjects) {
		this.referenceObjectsOther = referenceObjects;
	}

	/**
	 * <p>Getter for the field <code>description</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 *
	 * @param description a {@link java.lang.String} object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * <p>Getter for the field <code>profileMetaDataVersion</code>.</p>
	 *
	 * @return a {@link java.lang.Double} object.
	 */
	public Double getProfileMetaDataVersion() {
		return profileMetaDataVersion;
	}


	/**
	 * <p>Setter for the field <code>profileMetaDataVersion</code>.</p>
	 *
	 * @param profileMetaDataVersion a {@link java.lang.String} object.
	 */
	public void setProfileMetaDataVersion(Double profileMetaDataVersion) {
		this.profileMetaDataVersion = profileMetaDataVersion;
	}


	/**
	 * <p>Setter for the field <code>profileMetaDataVersion</code>.</p>
	 *
	 * @param profileMetaDataVersion a {@link java.lang.String} object.
	 */
	public void setProfileMetaDataVersion(String profileMetaDataVersion) {
		try {
			this.profileMetaDataVersion = Double.parseDouble(profileMetaDataVersion);
		} catch (NumberFormatException | NullPointerException e) {
			this.profileMetaDataVersion = null;
		}
	}

	public String getActiveAspects() {
		return activeAspects;
	}


	/**
	 * <p>Setter for the field <code>activeAspects</code>.</p>
	 *
	 * @param activeAspects a {@link java.lang.String} object.
	 */
	public void setActiveAspects(String activeAspects) {
		this.activeAspects = activeAspects;
	}

	public String getSupportedAspects() {
		return this.supportedAspects;
	}

	/**
	 * <p>Setter for the field <code>supportedAspects</code>.</p>
	 *
	 * @param supportedAspects a {@link java.lang.String} object.
	 */
	public void setSupportedAspects(String supportedAspects) {
		this.supportedAspects = supportedAspects;
	}

	public boolean isComposed() {
		return includes != null && includes.length > 0;
	}

	public String getReadme() {
		return this.readme;
	}

	/**
	 * <p>Setter for the field <code>readme</code>.</p>
	 *
	 * @param readme a {@link java.lang.String} object.
	 */
	public void setReadme(String readme) {
		this.readme = readme;
	}

	public String getTechnicalChangelog() {
		return this.technicalChangelog;
	}

	/**
	 * <p>Setter for the field <code>technicalChangelog</code>.</p>
	 *
	 * @param technicalChangelog a {@link java.lang.String} object.
	 */
	public void setTechnicalChangelog(String technicalChangelog) {
		this.technicalChangelog = technicalChangelog;
	}

	public String getSemanticChangelog() {
		return this.semanticChangelog;
	}

	/**
	 * <p>Setter for the field <code>semanticChangelog</code>.</p>
	 *
	 * @param semanticChangelog a {@link java.lang.String} object.
	 */
	public void setSemanticChangelog(String semanticChangelog) {
		this.semanticChangelog = semanticChangelog;
	}

	/**
	 * <p>Setter for the field <code>includes</code>.</p>
	 *
	 * @param includes a {@link java.lang.String} object.
	 */
	public void setIncludes(IncludedProfile[] includes) {
		this.includes = includes;
	}

	public IncludedProfile[] getIncludes() {
		return this.includes;
	}

	public static class IncludedProfile {
		private Profile profile;
		private String select;

		public IncludedProfile(Profile profile, String select) {
			this.profile = profile;
			this.select = select;
		}

		public Profile getProfile() {
			return profile;
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
		}

		public String getSelect() {
			return select;
		}

		public void setSelect(String select) {
			this.select = select;
		}
	}

	public static class Bundle<T> {
		private String urlPrefix;
		private String jarPath;
		private String description;
		private T resource;

		public Bundle(String jarPath, String urlPrefix, String description, T resource) {
			this.jarPath = jarPath;
			this.urlPrefix = urlPrefix;
			this.description = description;
			this.resource = resource;
		}

		
		public Bundle(String jarPath, String urlPrefix, T resource) {
			this(jarPath, urlPrefix, null, resource);
		}

		public String getJarPath() {
			return jarPath;
		}

		public void setJarPath(String jarPath) {
			this.jarPath = jarPath;
		}


		public String getUrlPrefix() {
			return urlPrefix;
		}

		public void setUrlPrefix(String urlPrefix) {
			this.urlPrefix = urlPrefix;
		}


		public T getResource() {
			return resource;
		}

		public void setResource(T resource) {
			this.resource = resource;
		}


		public String getDescription() {
			return description;
		}


		public void setDescription(String description) {
			this.description = description;
		}
	}
}
