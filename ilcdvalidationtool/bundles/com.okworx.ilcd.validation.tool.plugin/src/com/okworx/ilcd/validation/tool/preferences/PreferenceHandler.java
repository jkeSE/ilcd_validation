package com.okworx.ilcd.validation.tool.preferences;

import java.io.IOException;
import java.util.*;

import jakarta.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.jface.preference.PreferenceStore;

import jakarta.annotation.PostConstruct;

@Creatable
@Singleton
public class PreferenceHandler {
	private final Logger log = LogManager.getLogger(this.getClass());
	
	private static PreferenceStore prefStore = null;
	private String prefID = "";

	private List<ProfileDto> profiles = new ArrayList<ProfileDto>();

	/**
	 * Creates a new PreferenceHandler
	 */
	public PreferenceHandler() {
		log.debug("instantiating PreferenceHandler");
		prefID = PreferenceConstants.PAGEID_GENERAL;
		getPreferenceStore();
		retrieveFromPrefStore();	
	}

	@PostConstruct
	private void init() {
		log.debug("instantiated PreferenceHandler");
	}

	/**
	 * Creates the singleton PreferenceStore if it was not created yet
	 */
	public static PreferenceStore getPreferenceStore() {
		if (prefStore == null) {
			String prefStorePath = Platform.getLocation().addTrailingSeparator()
					.append(PreferenceConstants.PREFSTORE_FILENAME).toFile().getPath();
			prefStore = new PreferenceStore(prefStorePath);
			// disable for 1.0 release
			try {
				prefStore.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}

		return prefStore;
	}

	/**
	 * remove every profile
	 */
	public void clearProfiles() {
		profiles.clear();
	}

	/**
	 * Add profile to list and re-sorts the list. After the execution of this profiles,
	 * local copies of profiles must be updated!
	 * 
	 * @param profile Profile to add
	 */
	public void addProfile(ProfileDto profile) {
		if (!profiles.contains(profile)) {
			profiles.add(profile);
			sortProfiles();
		} else {
			log.debug("Profile was not added, because the same profile already exists!");
		}
	}

	/**
	 * Remove profile at given index and re-sorts the list. After the execution of
	 * this profiles, local copies of profiles must be updated!
	 * 
	 * @param index Index of profile to remove
	 */
	public void removeProfile(int index) {
		profiles.remove(index);
		sortProfiles();
	}

	/**
	 * Update profile at given index and re-sorts the list. After the execution of this profiles,
	 * local copies of profiles must be updated!
	 * 
	 * @param index   Index of profile to update
	 * @param profile New profile
	 */
	public void updateProfile(int index, ProfileDto profile) {
		profiles.set(index, profile);
		sortProfiles();
	}

	public List<ProfileDto> getProfiles() {
		return profiles;
	}

	/**
	 * updates all saved preference values to the PreferenceStore
	 */
	public void backupToPrefStore() {
		PreferenceStore prefStore = getPreferenceStore();

		String hideOutdatedId = prefID + "_hideOutdated";
		prefStore.setValue(hideOutdatedId, hideOutdated);

		String prefCountID = prefID + "_count";
		int oldCount = prefStore.getInt(prefCountID);
		int newCount = profiles.size();
		prefStore.setValue(prefCountID, newCount);

		for (int i = 0; i < newCount; i++) {
			prefStore.setValue(getNameKey(i), profiles.get(i).getName());
			prefStore.setValue(getVersionKey(i), profiles.get(i).getVersion());
			prefStore.setValue(getPathKey(i), profiles.get(i).getPath());

		}

		for (int i = newCount; i < oldCount; i++) {
			prefStore.setToDefault(getNameKey(i));
			prefStore.setToDefault(getVersionKey(i));
			prefStore.setToDefault(getPathKey(i));
		}
		try {
			prefStore.save();
		} catch (IOException e) {
			System.err.println("Error while saving preferences.");
			e.printStackTrace();
		}
	}

	/**
	 * retrieve preference values from the PreferenceStore
	 */
	public void retrieveFromPrefStore() {

		profiles = new ArrayList<ProfileDto>();

		PreferenceStore prefStore = getPreferenceStore();

		String hideOutdatedId = prefID + "_hideOutdated";
		if (prefStore.contains(hideOutdatedId)) {
			this.hideOutdated = prefStore.getBoolean(hideOutdatedId);			
		} else {
			this.hideOutdated = true;
		}

		String prefCountID = prefID + "_count";
		int prefCount = prefStore.getInt(prefCountID);

		for (int i = 0; i < prefCount; i++) {
			ProfileDto profile = new ProfileDto();
			profile.setName(prefStore.getString(getNameKey(i)));
			profile.setVersion(prefStore.getString(getVersionKey(i)));
			profile.setPath(prefStore.getString(getPathKey(i)));
			profiles.add(profile);
		}
		sortProfiles();
	}

	private boolean hideOutdated = true;

	public void hideOutdated(boolean hideOutdated) {
		this.hideOutdated = hideOutdated;
	}

	public boolean hideOutdated() {
		return this.hideOutdated;
	}

	private String getNameKey(int i) {
		return prefID + "_" + i + "_name";
	}

	private String getVersionKey(int i) {
		return prefID + "_" + i + "_version";
	}

	private String getPathKey(int i) {
		return prefID + "_" + i + "_path";
	}
	
	private void sortProfiles() {
		Collections.sort(profiles);		
	}
}
