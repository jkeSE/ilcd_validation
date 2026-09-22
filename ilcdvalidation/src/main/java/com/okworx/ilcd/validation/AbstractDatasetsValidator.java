package com.okworx.ilcd.validation;

import java.io.File;
import java.util.HashMap;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.reference.ReferenceBuilder;
import com.okworx.ilcd.validation.util.Statistics;
import org.apache.logging.log4j.Logger;

/**
 * <p>Abstract AbstractDatasetsValidator class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public abstract class AbstractDatasetsValidator extends AbstractValidator implements IValidator, IDatasetsValidator {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());
	protected double unitsDone = 0;

	protected double unitsTotal = 0;

	protected int updateInterval = 5;

	protected boolean validateArchives = false;

	protected HashMap<String, IDatasetReference> objectsToValidate;

	protected Profile profile = null;

	protected Statistics statistics = new Statistics();
	
	protected File fileSource = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.okworx.ilcd.validation.IDatasetsValidator#setObjectsToValidate(java
	 * .util.HashMap)
	 */
	/** {@inheritDoc} */
	public void setObjectsToValidate(final HashMap<String, IDatasetReference> objects) {
		this.objectsToValidate = new HashMap<>(objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.okworx.ilcd.validation.IDatasetsValidator#setObjectsToValidate(java
	 * .io.File)
	 */
	/**
	 * <p>Setter for the field <code>objectsToValidate</code>.</p>
	 *
	 * @param source a {@link java.io.File} object.
	 */
	public void setObjectsToValidate(File source) {

		this.fileSource = source;
		
		ReferenceBuilder builder = new ReferenceBuilder(this.getAspectName());

		builder.setValidationContext(this.validationContext);
		
		this.objectsToValidate = builder.build(source);

		this.eventsList.addAll(builder.getEventsList().getEvents());
		
		if (this.validateArchives) {
			// check whether source is directory, and only otherwise call
			// validateArchive()
			if (!source.isDirectory() && !source.getName().toLowerCase().endsWith(".xml"))
				validateArchive(source);
		}
	}

	private void validateArchive(File source) {
		ArchiveValidator av = new ArchiveValidator();
		av.setArchiveToValidate(source);
		av.validate();
		this.eventsList.addAll(av.getEventsList().getEvents());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okworx.ilcd.validation.IDatasetsValidator#getObjectsToValidate()
	 */
	/**
	 * <p>Getter for the field <code>objectsToValidate</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, IDatasetReference> getObjectsToValidate() {
		return this.objectsToValidate;
	}

	/**
	 * <p>reset.</p>
	 */
	public void reset() {
		super.reset();
		this.objectsToValidate = null;
		if (this.updateEventListener != null)
			this.updateEventListener.updateProgress(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okworx.ilcd.validation.IDatasetsValidator#validate()
	 */
	/** {@inheritDoc} */
	@Override
	public boolean validate() throws InterruptedException {
		log.info("validating");

		if (this.objectsToValidate == null)
			throw new IllegalArgumentException();

		if (this.objectsToValidate.isEmpty()) {
			log.debug("nothing to validate");
			return this.eventsList.isPositive();
		}

		if (log.isDebugEnabled())
			for (String paramName : this.parameters.keySet())
				log.debug("using parameter " + paramName + " : " + this.parameters.get(paramName));

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Assign a specific profile to this validator which may contain XML
	 * schemas, validation stylesheets, categories, reference nomenclature etc.
	 * that will be automatically registered with the validator.
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * <p>triggerProgressUpdate.</p>
	 *
	 * @param count a int.
	 */
	public synchronized void triggerProgressUpdate(int count) {
		this.unitsDone += count;
		if (log.isTraceEnabled())
			log.trace(count + " / " + unitsDone + " / " + unitsTotal + " / " + (unitsDone / unitsTotal));
		updateProgress(unitsDone / unitsTotal);
	}
	
	/**
	 * <p>Getter for the field <code>updateInterval</code>.</p>
	 *
	 * @return a int.
	 */
	public int getUpdateInterval() {
		return updateInterval;
	}

	/** {@inheritDoc} */
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	/**
	 * <p>isValidateArchives.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isValidateArchives() {
		return validateArchives;
	}

	/** {@inheritDoc} */
	public void setValidateArchives(boolean validateArchives) {
		this.validateArchives = validateArchives;
	}

	/**
	 * <p>Getter for the field <code>statistics</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public Statistics getStatistics() {
		return statistics;
	}

	/**
	 * <p>Setter for the field <code>statistics</code>.</p>
	 *
	 * @param statistics a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	/**
	 * <p>Getter for the field <code>profile</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.profile.Profile} object.
	 */
	public Profile getProfile() {
		return profile;
	}

	public File getFileSource() {
		return fileSource;
	}

	public void setFileSource(File fileSource) {
		this.fileSource = fileSource;
	}

}
