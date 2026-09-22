package com.okworx.ilcd.validation.reference;

import org.apache.commons.lang3.StringUtils;

import com.okworx.ilcd.validation.common.DatasetType;

/**
 * <p>DatasetReference class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class DatasetReference implements IDatasetReference {

	protected String uuid;

	protected String version;

	protected String uri;

	protected String name;

	protected String absoluteFileName;

	protected String shortFileName;

	protected DatasetType datasetType;

	protected String origin;

	protected String originDatasetUUID;

	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public DatasetReference(String uuid, DatasetType type) {
		this(uuid, null, null, type, null, null, null);
	}
	
	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public DatasetReference(String uuid, String version, DatasetType type) {
		this(uuid, version, null, type, null, null, null);
	}
	
	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String version, String absoluteFileName, String shortFileName) {
		this.uuid = uuid;
		this.version = version;
		this.absoluteFileName = absoluteFileName;
		this.shortFileName = shortFileName;
	}

	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public DatasetReference(String absoluteFileName, String shortFileName) {
		this(null, null, null, null, absoluteFileName, shortFileName, null);
	}
	
	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String uri, DatasetType type, String absoluteFileName, String shortFileName) {
		this(uuid, null, uri, type, absoluteFileName, shortFileName, null);
	}

	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 * @param origin a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String uri, DatasetType type, String absoluteFileName, String shortFileName, String origin) {
		this(uuid, null, uri, type, absoluteFileName, shortFileName, origin);
	}

	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String version, String uri, String absoluteFileName, String shortFileName) {
		this(uuid, version, uri, null, absoluteFileName, shortFileName, null);
	}

	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String version, String uri, DatasetType type, String absoluteFileName, String shortFileName) {
		this(uuid, version, uri, type, absoluteFileName, shortFileName, null);
	}
	
	/**
	 * <p>Constructor for DatasetReference.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param type a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 * @param absoluteFileName a {@link java.lang.String} object.
	 * @param shortFileName a {@link java.lang.String} object.
	 * @param origin a {@link java.lang.String} object.
	 */
	public DatasetReference(String uuid, String version, String uri, DatasetType type, String absoluteFileName, String shortFileName, String origin) {
		this.uuid = uuid;
		this.version = version;
		this.uri = uri;
		this.datasetType = type;
		this.absoluteFileName = absoluteFileName;
		this.shortFileName = shortFileName;
		this.origin = origin;
	}

	/**
	 * <p>Getter for the field <code>uuid</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * <p>Setter for the field <code>uuid</code>.</p>
	 *
	 * @param uuid a {@link java.lang.String} object.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * <p>Getter for the field <code>uri</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * <p>Setter for the field <code>uri</code>.</p>
	 *
	 * @param uri a {@link java.lang.String} object.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * <p>Getter for the field <code>absoluteFileName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAbsoluteFileName() {
		return absoluteFileName;
	}

	/**
	 * <p>Getter for the field <code>absoluteFileName</code>.</p>
	 *
	 * @param fileName a {@link java.lang.String} object.
	 */
	public void getAbsoluteFileName(String fileName) {
		this.absoluteFileName = fileName;
	}

	/**
	 * <p>Getter for the field <code>shortFileName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getShortFileName() {
		return shortFileName;
	}

	/**
	 * <p>Setter for the field <code>shortFileName</code>.</p>
	 *
	 * @param shortFileName a {@link java.lang.String} object.
	 */
	public void setShortFileName(String shortFileName) {
		this.shortFileName = shortFileName;
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public DatasetType getDatasetType() {
		return this.datasetType;
	}

	/** {@inheritDoc} */
	public void setDatasetType(DatasetType type) {
		this.datasetType = type;
	}

	/**
	 * @deprecated renamed to getDatasetType()
	 * @return the dataset type
	 */
	@Deprecated
	public DatasetType getType() {
		return this.datasetType;
	}

	/**
	 * @deprecated renamed to setDatasetType()
	 * @param type the dataset type
	 */
	@Deprecated
	public void setType(DatasetType type) {
		this.datasetType = type;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	public void setName(String name) {
		this.name = name;
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

	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IDatasetReference))
			return false;
		IDatasetReference other = (IDatasetReference) obj;
		
		// compare uuid, if set
		if ( (this.uuid == null && other.getUuid() != null) || (this.uuid != null && !this.uuid.equalsIgnoreCase( other.getUuid() )) ) {
			return false;
		}

		// compare version, if set
		if ( (this.version == null && other.getVersion() != null) || (this.version != null && !this.version.equals( other.getVersion())) ) {
			return false;
		}

		// compare absolute file name, if set
		if ( (this.absoluteFileName == null && other.getAbsoluteFileName() != null) || (this.absoluteFileName != null && !this.absoluteFileName.equals( other.getAbsoluteFileName())) ) {
			return false;
		}

		return true;
	}
	
	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		int hashCode = 31;
		if (StringUtils.isNotEmpty(this.uuid))
			hashCode += this.uuid.toLowerCase().hashCode();
		if (StringUtils.isNotEmpty(this.absoluteFileName))
			hashCode += this.absoluteFileName.hashCode();
		if (StringUtils.isNotEmpty(this.name))
			hashCode += this.name.hashCode();
		if (StringUtils.isNotEmpty(this.version))
			hashCode += this.version.hashCode();
		if (this.datasetType != null)
			hashCode += this.datasetType.hashCode();
		return hashCode;
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.datasetType != null)
			builder.append("type: ").append(this.datasetType).append(" # ");
		if (StringUtils.isNotEmpty(this.uuid))
			builder.append("uuid: ").append(this.uuid.toLowerCase()).append(" # ");
		if (StringUtils.isNotEmpty(this.version))
			builder.append("version: ").append(this.version).append(" # ");
		if (StringUtils.isNotEmpty(this.name))
			builder.append("name: ").append(this.name).append(" # ");
		if (StringUtils.isNotEmpty(this.origin))
			builder.append("origin: ").append(this.origin).append(" # ");
		if (StringUtils.isNotEmpty(this.originDatasetUUID))
			builder.append("originDatasetUUID: ").append(this.originDatasetUUID).append(" # ");
		if (StringUtils.isNotEmpty(this.absoluteFileName))
			builder.append("abs. filename: ").append(this.absoluteFileName).append(" # ");
		builder.append(hashCode());
		return builder.toString();
	}

	/**
	 * <p>Getter for the field <code>origin</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * <p>Setter for the field <code>origin</code>.</p>
	 *
	 * @param origin a {@link java.lang.String} object.
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * <p>Getter for the field <code>originDatasetUUID</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getOriginDatasetUUID() {
		return originDatasetUUID;
	}

	/**
	 * <p>Setter for the field <code>originDatasetUUID</code>.</p>
	 *
	 * @param originDatasetUUID a {@link java.lang.String} object.
	 */
	public void setOriginDatasetUUID(String originDatasetUUID) {
		this.originDatasetUUID = originDatasetUUID;
	}
}