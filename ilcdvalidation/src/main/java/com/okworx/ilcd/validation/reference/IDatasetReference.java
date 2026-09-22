package com.okworx.ilcd.validation.reference;

import com.okworx.ilcd.validation.common.DatasetType;

/**
 * <p>IDatasetReference interface.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public interface IDatasetReference {

	/**
	 * <p>getUuid.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getUuid();

	/**
	 * <p>getVersion.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getVersion();

	/**
	 * <p>getUri.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getUri();

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getName();
	
	/**
	 * <p>getAbsoluteFileName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getAbsoluteFileName();

	/**
	 * <p>getShortFileName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getShortFileName();

	/**
	 * <p>getOrigin.</p>
	 *
	 * This is the originating element in the dataset where the reference is stated.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getOrigin();

	/**
	 * <p>getOrigin.</p>
	 *
	 * The UUID of the originating dataset where the reference is stated.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public abstract String getOriginDatasetUUID();

	public void setOriginDatasetUUID(String originDatasetUUID);

	/**
	 * <p>equals.</p>
	 *
	 * @param obj a {@link java.lang.Object} object.
	 * @return a boolean.
	 */
	public abstract boolean equals(Object obj);

	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public abstract DatasetType getDatasetType();

	/**
	 * <p>setType.</p>
	 *
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public abstract void setDatasetType(DatasetType type);

	/**
	 * <p>setName.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public abstract void setName(String name);

}
