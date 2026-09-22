package com.okworx.ilcd.validation.tool.options;

import java.util.*;

import jakarta.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Creatable;

import com.okworx.ilcd.validation.LinkValidator;
import com.okworx.ilcd.validation.OrphansValidator;
import com.okworx.ilcd.validation.profile.Profile;

@Creatable
@Singleton
public class ValidationOptions  {
	
    private final Map<Object, Runnable> listeners = new HashMap<Object, Runnable>();


	public static final String OPTION_DS_SUMMARY = "summary";
	public static final String OPTION_FLOWS_ANALYSIS = "flowsAnalysis";
	public static final String OPTION_FLOWS_ANALYSIS_INCLUDE_SUMS = "flowsAnalysis_withSums";
	public static final String OPTION_BATCHMODE = "batchMode";
	public static final String OPTION_IGNORE_REFERENCE_OBJECTS = "ignoreReferenceObjects";
	
	protected final Logger log = LogManager.getLogger(this.getClass());

	private Map<String, Object> generalOptions = new HashMap<String, Object>();

	private Map<String, Object> linkOptions = new HashMap<String, Object>();

	private Map<String, Object> orphansOptions = new HashMap<String, Object>();
	
	private Profile profile = null;
	
	public Map<String, Object> getGeneralOptions() {
		return generalOptions;
	}
	

	public Map<String, Object> getLinkOptions() {
		return linkOptions;
	}

	public Map<String, Object> getOrphansOptions() {
		return orphansOptions;
	}
	
	public void addObserver(Runnable update, Object self) {
		this.listeners.put(self, update);
	}
	
	public void deleteObserver(Object self) {
		this.listeners.remove(self);
	}
	

	public ValidationOptions() {
		setDefaults();
	}

	protected void setDefaults() {
		this.generalOptions.put(OPTION_BATCHMODE, false);
		this.generalOptions.put(OPTION_DS_SUMMARY, false);
		this.generalOptions.put(OPTION_FLOWS_ANALYSIS, false);
		this.generalOptions.put(OPTION_FLOWS_ANALYSIS_INCLUDE_SUMS, false);
		this.generalOptions.put(OPTION_IGNORE_REFERENCE_OBJECTS, true);
		
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS, true);
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS, true);
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_INCLUDEDPROCESSES, true);
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION, true);
		this.linkOptions.put(LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS, false);

		this.orphansOptions.put(OrphansValidator.PARAM_IGNORE_REFERENCE_OBJECTS, true);
		this.orphansOptions.put(OrphansValidator.PARAM_IGNORE_LCIAMETHODS, true);
	}
	
	public Profile getProfile() {
		return this.profile;
	}
	
	public void setProfile(Profile newProfile) {
		this.profile = newProfile;
		this.notifyObservers();
	}
	
	private void notifyObservers() {
		for (Runnable runnable : listeners.values()) {
			runnable.run();
		}
	}
	
	public String dumpOptions() {
		StringBuilder sb = new StringBuilder();
		sb.append("  Links");
		sb.append(System.lineSeparator());
		sb.append(dumpOptions(this.linkOptions));
		sb.append(System.lineSeparator());
		
		sb.append("  Orphaned items");
		sb.append(System.lineSeparator());
		sb.append(dumpOptions(this.orphansOptions));
		sb.append(System.lineSeparator());
		return sb.toString();
	}

	public String dumpOptions(Map<String, Object> options) {
		StringBuilder sb = new StringBuilder();
		for (String option : options.keySet()) {
			sb.append("    ");
			sb.append(option);
			sb.append(": " );
			sb.append(options.get(option));
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}
