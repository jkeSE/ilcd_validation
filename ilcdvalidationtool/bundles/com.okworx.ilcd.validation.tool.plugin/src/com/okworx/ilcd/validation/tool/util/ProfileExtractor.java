package com.okworx.ilcd.validation.tool.util;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;

import com.okworx.ilcd.validation.util.Locator;

public class ProfileExtractor {
	
	private final Logger log = LogManager.getLogger(this.getClass());

	private File cacheDir;
	private String[] profileFileNames;
	private Locator locator = new EclipseLocator();

	public ProfileExtractor(String[] profileFileNames) {
		this.profileFileNames = profileFileNames;
		this.cacheDir = this.createCacheDir();
	}
	
	public File[] extractProfilesFromLibrary() {

		
//		locator.resolve(getClass().getClassLoader().getResource("target/dependency/ilcd-validation.jar")).getFile()
		// if started from within eclipse, the ilcd-validation.jar isn't packed yet:
		URL extractedValidationTool = null;
		try {
			extractedValidationTool = locator.resolve(getClass().getClassLoader().getResource("target/dependency/ilcd-validation.jar"));
		} catch (IOException e) {
			log.error("Error while locating ilcd-validation.jar.");
			e.printStackTrace();
		}
		
		// if started as standalone we need to extract the ilcd-validation.jar first:
		if (extractedValidationTool == null || extractedValidationTool.getProtocol().equals("jar")) {
			String appVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();
			URL validationToolPluginFile = null;
			try {
				validationToolPluginFile = Platform.getInstallLocation()
						.getDataArea("plugins" + File.separator + "com.okworx.ilcd.validation.tool.plugin_" + appVersion + ".jar");
			} catch (IOException e) {
				log.error("Error while building URL for validation-tool.jar to extract default profiles.");
				e.printStackTrace();
			}
			if(validationToolPluginFile == null || !(new File(validationToolPluginFile.getFile())).exists()) {
				log.trace("InstallationLocation: " + Platform.getInstallLocation());
				log.trace("validationToolPlugin: " + validationToolPluginFile.getFile().toString());
				log.trace("Could not find validation-tool.jar. Skipping profile extraction.");
				return null;
			}
			
			try {
				extractedValidationTool = this.extractLibrary(validationToolPluginFile).toURI().toURL();
			} catch (MalformedURLException e) {
				log.error("Could not extract validation-tool.jar.");
				e.printStackTrace();
			}
			if(extractedValidationTool == null) {
				log.error("Could not extract validation-tool.jar.");
				return null;
			}
		}

		File[] extractedProfiles = null;
		try {
			extractedProfiles = this.extractProfiles(extractedValidationTool.toURI().toURL());
		} catch (MalformedURLException | URISyntaxException e) {
			log.error("Error while extracting profiles from ilcd-validation.jar.");
			e.printStackTrace();
		}		
		
		return extractedProfiles;
	}

	private File createCacheDir() {
		log.trace("Cache directory: " + FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis());
		File cacheDir = new File(
				FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis());
		try {
			FileUtils.forceMkdir(cacheDir);
			FileUtils.forceDeleteOnExit(cacheDir);
		} catch (IOException e) {
			log.error("Error while creating temp directory for extracting profiles.");
			e.printStackTrace();
		}
		
		return cacheDir;
	}

	private File extractLibrary(URL path) {
		try {
			log.error("lib-path: " + path.toString());
			JarFile jarFile = new JarFile(path.getFile());
			ZipEntry entry = jarFile.getEntry("target/dependency/ilcd-validation.jar");

			File extractedJar = new File(cacheDir.getAbsolutePath() + File.separator + "ilcd-validation");
			log.trace("extractedJar: " + extractedJar.getAbsolutePath());
			FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), extractedJar);
			
			jarFile.close();
			return extractedJar;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public File[] extractProfiles(URL path) {
		ArrayList<File> extractedProfiles = new ArrayList<File>();
		log.debug("extracting {} profiles", extractedProfiles.size());
		try {
			JarFile jarFile = new JarFile(new File(path.getFile()));
			for(int i = 0; i < profileFileNames.length; i++) {
				log.debug("extracting profile {}", profileFileNames[i]);
				ZipEntry entry = jarFile.getEntry(profileFileNames[i]);
				if(entry == null) {
					log.error("Could not find validation profile '" + profileFileNames[i] + "'.");
					continue;
				}
				
				File destination = new File(cacheDir.getAbsolutePath() + File.separator + profileFileNames[i]);
				if(!destination.exists()) {
					log.debug("extracting to {}", destination.getAbsolutePath());
					FileUtils.copyInputStreamToFile(jarFile.getInputStream(entry), destination);
					extractedProfiles.add(destination);
				} else {
					log.debug("profile already exists, skipping");
				}
			}
			jarFile.close();

			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		return (File[]) extractedProfiles.toArray(new File[extractedProfiles.size()]);
	}

}