package com.okworx.ilcd.validation.common;

/**
 * <p>DatasetType class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public enum DatasetType {
	PROCESS("process data set"), FLOW("flow data set"), FLOWPROPERTY("flow property data set"), UNITGROUP(
			"unit group data set"), LCIAMETHOD("LCIA method data set"), SOURCE("source data set"), CONTACT(
			"contact data set"), LCMODEL("life cycle model data set"),EXTERNAL_FILE("other external file");

	/**
	 * <p>Constructor for DatasetType.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 */
	DatasetType(String value) {
		this.value = value;
	}

	/**
	 * <p>Getter for the field <code>value</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue() {
		return this.value;
	}

	private String value;

	/**
	 * <p>fromValue.</p>
	 *
	 * @param v a {@link java.lang.String} object.
	 * @return a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public static DatasetType fromValue(String v) {
		for (DatasetType c : DatasetType.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

	/**
	 * Returns the name of the root element for this type.
	 *
	 * @return the name of the root element
	 */
	public String getRootElementName() {
		switch (this) {
		case PROCESS:
			return Constants.PROCESS_ROOT_ELEMENT_NAME;
		case FLOW:
			return Constants.FLOW_ROOT_ELEMENT_NAME;
		case FLOWPROPERTY:
			return Constants.FLOW_PROPERTY_ROOT_ELEMENT_NAME;
		case UNITGROUP:
			return Constants.UNIT_GROUP_ROOT_ELEMENT_NAME;
		case SOURCE:
			return Constants.SOURCE_ROOT_ELEMENT_NAME;
		case CONTACT:
			return Constants.CONTACT_ROOT_ELEMENT_NAME;
		case LCIAMETHOD:
			return Constants.LCIAMETHOD_ROOT_ELEMENT_NAME;
		case LCMODEL:
			return Constants.LCMODEL_ROOT_ELEMENT_NAME;
		default:
			return null;
		}
	}

	/**
	 * <p>fromRootElementName.</p>
	 *
	 * @param rootElementName a {@link java.lang.String} object.
	 * @return a {@link com.okworx.ilcd.validation.common.DatasetType} object.
	 */
	public static DatasetType fromRootElementName(String rootElementName) {
		if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.PROCESS_ROOT_ELEMENT_NAME))
			return DatasetType.PROCESS;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.FLOW_ROOT_ELEMENT_NAME))
			return DatasetType.FLOW;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.FLOW_PROPERTY_ROOT_ELEMENT_NAME))
			return DatasetType.FLOWPROPERTY;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.UNIT_GROUP_ROOT_ELEMENT_NAME))
			return DatasetType.UNITGROUP;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.SOURCE_ROOT_ELEMENT_NAME))
			return DatasetType.SOURCE;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.CONTACT_ROOT_ELEMENT_NAME))
			return DatasetType.CONTACT;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.LCIAMETHOD_ROOT_ELEMENT_NAME))
			return DatasetType.LCIAMETHOD;
		else if (rootElementName.equals(com.okworx.ilcd.validation.common.Constants.LCMODEL_ROOT_ELEMENT_NAME))
			return DatasetType.LCMODEL;
		else
			return null;
	}

	/**
	 * Returns the name of the schema for this dataset type (using the values
	 * defined in {@link Constants}).
	 *
	 * @return the schema name
	 */
	public String getSchemaName() {
		switch (this) {
		case PROCESS:
			return Constants.PROCESS_SCHEMA_NAME;
		case FLOW:
			return Constants.FLOW_SCHEMA_NAME;
		case FLOWPROPERTY:
			return Constants.FLOW_PROPERTY_SCHEMA_NAME;
		case UNITGROUP:
			return Constants.UNIT_GROUP_SCHEMA_NAME;
		case SOURCE:
			return Constants.SOURCE_SCHEMA_NAME;
		case CONTACT:
			return Constants.CONTACT_SCHEMA_NAME;
		case LCIAMETHOD:
			return Constants.LCIAMETHOD_SCHEMA_NAME;
		case LCMODEL:
			return Constants.LCMODEL_SCHEMA_NAME;
		default:
			return null;
		}
	}

}
