package com.okworx.ilcd.validation.tool.preferences;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;

public class PrefPageGeneral extends ConfigPrefPage {

	public PrefPageGeneral(LocalizationString locString, PreferenceHandler prefHandler) {
		super("Profiles", PreferenceConstants.PAGEID_GENERAL, locString, prefHandler, new String[] { "*.jar", "*.*" });
	}
}
