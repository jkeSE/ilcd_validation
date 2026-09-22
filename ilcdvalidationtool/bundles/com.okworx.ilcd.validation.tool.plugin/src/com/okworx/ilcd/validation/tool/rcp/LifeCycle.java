package com.okworx.ilcd.validation.tool.rcp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import jakarta.inject.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessAdditions;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.prefs.BackingStoreException;

import com.okworx.ilcd.validation.common.Constants;
import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.tool.preferences.PreferenceHandler;
import com.okworx.ilcd.validation.tool.preferences.ProfileDto;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EclipseLocator;
import com.okworx.ilcd.validation.tool.util.ProfileExtractor;

public class LifeCycle {
	private final Logger log = LogManager.getLogger(this.getClass());

	@Inject
	@Translation
	private LocalizationString localizationString;
	
	@Inject
	PreferenceHandler prefHandler;

	@ProcessAdditions
	public void processAdditions(MApplication app, EModelService modelService, IApplicationContext appContext) {
		MWindow window = (MWindow) modelService.find("com.okworx.ilcd.validation.tool.trimmedwindow.main", app);
		String appVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();
		window.setLabel(localizationString.WindowName.replace("@app-version@", appVersion));
	}

	@PostContextCreate
	public void initializeProfileManager() {
        		
		// initialize ProfileManager.
		// cacheDir is inside the workspace dir.
		log.trace("PlatformLocation: " + Platform.getLocation().toString());
		new ProfileManager.ProfileManagerBuilder()
				.cacheDir(Platform.getLocation().addTrailingSeparator().append("profiles").toFile())
				.locator(new EclipseLocator())
				.registerDefaultProfiles(true, false)
				.build();
		
//		log.error("DefaultProfile is null" + ProfileManager.getInstance().getDefaultProfile().toString());

		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("com.okworx.ilcd.validation.tool.plugin");
		String lastVersion = prefs.get("last-version", "?");
		String currentVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();

		boolean firstStart = prefs.getBoolean("first-start", true);
		if (!currentVersion.equals(lastVersion) || firstStart) {
			log.info("Initializing profiles");
			if (log.isDebugEnabled()) {
				if (!currentVersion.equals(lastVersion)) {
					log.debug("Reason for profile initialization: update");
				}
				if (firstStart) {
					log.debug("Reason for profile initialization: first-start");
				}
			}

			registerDefaultProfiles();
			prefs.putBoolean("first-start", false);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				log.error("Error while saving preferences.");
				e.printStackTrace();
			}
		} else {
			log.trace("Application already initialized, default profiles will not be registered again.");
		}
	}

	private void registerDefaultProfiles() {
		log.trace("registering default profiles {}", Constants.DEFAULT_SECONDARY_PROFILE_JARS.toString());
		String[] profileFileNames = Constants.DEFAULT_SECONDARY_PROFILE_JARS;
		File[] extractedProfiles = (new ProfileExtractor(profileFileNames)).extractProfilesFromLibrary();

		if (extractedProfiles == null) {
			return;
		}

		ProfileManager profileManager = ProfileManager.getInstance();

		Collection<Profile> profiles = profileManager.getProfiles();
		try {
			for (int i = 0; i < extractedProfiles.length; i++) {
				URL profileUrl = extractedProfiles[i].toURI().toURL();
				try {
					Profile profile = profileManager.getProfile(profileUrl);
					if (profile == null || !profiles.contains(profile)) {
						Profile registeredProfile = profileManager.registerProfile(profileUrl);
						prefHandler.addProfile(new ProfileDto(registeredProfile.getName(),
								registeredProfile.getVersion(), registeredProfile.getPath().toString()));
						prefHandler.backupToPrefStore();
					}
				} catch (IOException e) {
					log.error("Error while registering default profiles.");
					e.printStackTrace();
				}
			}
			prefHandler.backupToPrefStore();
		} catch (MalformedURLException | InvalidProfileException e) {
			e.printStackTrace();
		}
	}

}
