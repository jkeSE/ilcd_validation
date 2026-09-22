package com.okworx.ilcd.validation;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.BooleanUtils;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.util.PrefixBuilder;

/**
 * <p>Abstract AbstractReferenceObjectsAwareValidator class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public abstract class AbstractReferenceObjectsAwareValidator extends AbstractDatasetsValidator {

	/** Constant <code>PARAM_IGNORE_REFERENCE_OBJECTS="ignoreReferenceObjects"</code> */
	public static final String PARAM_IGNORE_REFERENCE_OBJECTS = "ignoreReferenceObjects";

	public static final String ATTRIBUTE_TYPE = "type";

	public static final String ATTRIBUTE_NAME = "name";

	public static final String ATTRIBUTE_ORIGIN = "origin";

	public static final String ATTRIBUTE_URI = "uri";

	public static final String ATTRIBUTE_REF_OBJECT_ID = "refObjectId";

	public static final String URL_PREFIX_HTTPS = "https://";

	public static final String URL_PREFIX_HTTP = "http://";

	protected HashMap<String, String> referenceElementaryFlows = new HashMap<String, String>();
	protected HashMap<String, String> referenceObjectsOther = new HashMap<String, String>();

	protected AbstractReferenceObjectsAwareValidator() {
		this.parameters.put(PARAM_IGNORE_REFERENCE_OBJECTS, true);
	}
	
	@Override
	public boolean validate() throws InterruptedException {
		super.validate();
		
		boolean skipReferenceObjects = BooleanUtils.isTrue((Boolean) this.parameters.get(PARAM_IGNORE_REFERENCE_OBJECTS));

		log.debug("skipping reference objects: {}", skipReferenceObjects);

		if (skipReferenceObjects) {
			// remove all elements that are reference objects from objects to validate
			for (String key : referenceElementaryFlows.keySet()) {
				this.objectsToValidate.remove(key);
			}
			for (String key : referenceObjectsOther.keySet()) {
				this.objectsToValidate.remove(key);
			}
		}
		
		return false;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setProfile(Profile profile) {
		super.setProfile(profile);
		log.debug("profile is null: " + (profile == null));
		if ( profile != null ) {
			log.debug("getReferenceElementaryFlows is null: " + (profile.getReferenceElementaryFlows() == null));
			if (profile.getReferenceElementaryFlows() != null)
				setupReferenceElementaryFlows(profile.getReferenceElementaryFlows());
			log.debug("getReferenceObjectsOther is null: " + (profile.getReferenceObjectsOther() == null));
			if (profile.getReferenceObjectsOther() != null)
				setupReferenceObjectsOther(profile.getReferenceObjectsOther());
		}
	}

	/**
	 * <p>Setter for the field <code>referenceElementaryFlows</code>.</p>
	 *
	 * @param map a {@link java.util.HashMap} object.
	 */
	public void setReferenceElementaryFlows(final HashMap<String, String> map) {
		this.referenceElementaryFlows = map;
	}

	/**
	 * <p>Getter for the field <code>referenceElementaryFlows</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, String> getReferenceElementaryFlows() {
		return this.referenceElementaryFlows;
	}

	/**
	 * <p>Getter for the field <code>referenceObjectsOther</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, String> getReferenceObjectsOther() {
		return referenceObjectsOther;
	}

	/**
	 * <p>Setter for the field <code>referenceObjectsOther</code>.</p>
	 *
	 * @param referenceObjectsOther a {@link java.util.HashMap} object.
	 */
	public void setReferenceObjectsOther(final HashMap<String, String> referenceObjectsOther) {
		this.referenceObjectsOther = referenceObjectsOther;
	}

	private void setupReferenceElementaryFlows(Profile.Bundle<String> bundle) {
		HashMap<String, String> map = readReferenceObjects(bundle);
		this.setReferenceElementaryFlows(map);
	}

	private void setupReferenceObjectsOther(Profile.Bundle<String>[] bundles) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Profile.Bundle<String> bundle : bundles) {
			HashMap<String, String> bundleMap = readReferenceObjects(bundle);
			if (log.isDebugEnabled()) {
				if ( bundleMap != null )
					log.debug("adding " + bundleMap.size() + " other reference objects");
				else
					log.debug("no other reference objects found");
			}
			map.putAll(bundleMap);
		}
		this.setReferenceObjectsOther(map);
		if (log.isDebugEnabled())
			log.debug("using total of " + map.size() + " other reference objects");
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, String> readReferenceObjects(Profile.Bundle<String> bundle) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		try {
			URL url = new URL(PrefixBuilder.buildPath(bundle.getJarPath(), bundle.getUrlPrefix()) + "/" + bundle.getResource());
	
			InputStream is = url.openStream();
			GZIPInputStream gz = new GZIPInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(gz);
			map = (HashMap<String, String>) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (ClassNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} catch (Exception e) {
			log.error(e);
		}
	
		log.debug("reference objects map has " + map.size() + " items");

		return map;
	}


}
