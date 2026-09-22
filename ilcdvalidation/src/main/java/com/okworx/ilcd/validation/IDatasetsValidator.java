package com.okworx.ilcd.validation;

import java.io.File;
import java.util.HashMap;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.IUpdateEventListener;
import com.okworx.ilcd.validation.util.Statistics;

/**
 * <p>IDatasetsValidator interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IDatasetsValidator extends IValidator {

	/**
	 * <p>setObjectsToValidate.</p>
	 *
	 * @param objects a {@link java.util.HashMap} object.
	 */
	public abstract void setObjectsToValidate(HashMap<String, IDatasetReference> objects);

	/**
	 * <p>setObjectsToValidate.</p>
	 *
	 * @param directory a {@link java.io.File} object.
	 */
	public abstract void setObjectsToValidate(File directory);

	/**
	 * <p>getObjectsToValidate.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public abstract HashMap<String, IDatasetReference> getObjectsToValidate();

	/**
	 * <p>setProfile.</p>
	 *
	 * @param profile a {@link com.okworx.ilcd.validation.profile.Profile} object.
	 */
	public abstract void setProfile(Profile profile);

	/**
	 * <p>getProfile.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.profile.Profile} object.
	 */
	public abstract Profile getProfile();

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 * @throws java.lang.InterruptedException if any.
	 */
	public abstract boolean validate() throws InterruptedException;

	/**
	 * <p>getUpdateEventListener.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.IUpdateEventListener} object.
	 */
	public IUpdateEventListener getUpdateEventListener();

	/**
	 * <p>setUpdateEventListener.</p>
	 *
	 * @param listener a {@link com.okworx.ilcd.validation.util.IUpdateEventListener} object.
	 */
	public void setUpdateEventListener(IUpdateEventListener listener);

	/**
	 * <p>setUpdateInterval.</p>
	 *
	 * @param updateInterval a int.
	 */
	public abstract void setUpdateInterval(int updateInterval);

	/**
	 * <p>setValidateArchives.</p>
	 *
	 * @param validateArchives a boolean.
	 */
	public abstract void setValidateArchives(boolean validateArchives);
	
	/**
	 * <p>getStatistics.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public Statistics getStatistics();

	/**
	 * <p>getFileSource.</p>
	 *
	 * @return a File object for the original file (ZIP/folder/XML) if a single file has been handed over for validation.
	 */
	public File getFileSource();


}
