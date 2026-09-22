package com.okworx.ilcd.validation.common;

/**
 * <p>Constants class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class Constants {

	public static final String DEFAULT_PROFILE_GROUPID = "com.okworx.ilcd.validation.profiles";
	public static final String DEFAULT_PROFILE_ARTIFACTID = "ILCD-1.1";
	public static final String DEFAULT_PROFILE_VERSION = "1.8.3";
	public static final String DEFAULT_PROFILE_JAR = "profiles/" + DEFAULT_PROFILE_ARTIFACTID + "-" + DEFAULT_PROFILE_VERSION + ".jar";

	public static final String[] DEFAULT_SECONDARY_PROFILE_JARS = { "profiles/ILCD-1.1-EL-1.0.7.jar",
			"profiles/EF-1.0-1.0.11.jar", "profiles/EF-2.0-2.0.10.jar", "profiles/EF-3.0-3.3.1.jar",
			"profiles/EF-3.1-3.2.5.jar" };

	/*
	 * paths
	 */
	/** Constant <code>ILCD_PATH_PREFIX="eu/europa/ec/jrc/lca/ilcd/"</code> */
	public static final String ILCD_PATH_PREFIX = "eu/europa/ec/jrc/lca/ilcd/";

	/** Constant <code>ILCD_SCHEMAS_PATH_PREFIX="ILCD_PATH_PREFIX + schemas/"</code> */
	public static final String ILCD_SCHEMAS_PATH_PREFIX = ILCD_PATH_PREFIX + "schemas/";

	/** Constant <code>STYLESHEETS_PATH_PREFIX="ILCD_PATH_PREFIX + stylesheets/"</code> */
	public static final String STYLESHEETS_PATH_PREFIX = ILCD_PATH_PREFIX + "stylesheets/";

	/** Constant <code>VALIDATE_STYLESHEET_NAME="validate.xsl"</code> */
	public static final String VALIDATE_STYLESHEET_NAME = "validate.xsl";

	/** Constant <code>VALIDATE_STYLESHEET="STYLESHEETS_PATH_PREFIX + validate.xsl"</code> */
	public static final String VALIDATE_STYLESHEET = STYLESHEETS_PATH_PREFIX + "validate.xsl";

	/** Constant <code>DEFAULT_ILCD_LOCATIONS_NAME="ILCDLocations.xml"</code> */
	public static final String DEFAULT_ILCD_LOCATIONS_NAME = "ILCDLocations.xml";

	/** Constant <code>DEFAULT_ILCD_LCIA_METHODOLOGIES_NAME="ILCDLCIAMethodologies.xml"</code> */
	public static final String DEFAULT_ILCD_LCIA_METHODOLOGIES_NAME = "ILCDLCIAMethodologies.xml";

	/** Constant <code>DEFAULT_ILCD_CATEGORIES_NAME="ILCDClassification.xml"</code> */
	public static final String DEFAULT_ILCD_CATEGORIES_NAME = "ILCDClassification.xml";

	/** Constant <code>DEFAULT_ILCD_FLOW_CATEGORIES_NAME="ILCDFlowCategorization.xml"</code> */
	public static final String DEFAULT_ILCD_FLOW_CATEGORIES_NAME = "ILCDFlowCategorization.xml";

	/** Constant <code>REFERENCE_CATEGORIES_FILE_NAME="ILCDClassification_Reference.xml"</code> */
	public static final String REFERENCE_CATEGORIES_FILE_NAME = "ILCDClassification_Reference.xml";

	/** Constant <code>REFERENCE_FLOW_CATEGORIES_FILE_NAME="ILCDFlowCategorization_Reference.xml"</code> */
	public static final String REFERENCE_FLOW_CATEGORIES_FILE_NAME = "ILCDFlowCategorization_Reference.xml";

	/** Constant <code>REFERENCE_CATEGORIES="STYLESHEETS_PATH_PREFIX + REFERENCE_CAT"{trunked}</code> */
	public static final String REFERENCE_CATEGORIES = STYLESHEETS_PATH_PREFIX + REFERENCE_CATEGORIES_FILE_NAME;

	/** Constant <code>REFERENCE_LOCATIONS_FILE_NAME="ILCDLocations_Reference.xml"</code> */
	public static final String REFERENCE_LOCATIONS_FILE_NAME = "ILCDLocations_Reference.xml";

	/** Constant <code>REFERENCE_LOCATIONS="STYLESHEETS_PATH_PREFIX + REFERENCE_LOC"{trunked}</code> */
	public static final String REFERENCE_LOCATIONS = STYLESHEETS_PATH_PREFIX + REFERENCE_LOCATIONS_FILE_NAME;

	/** Constant <code>REFERENCE_LCIA_METHODOLOGIES_FILE_NAME="ILCDLCIAMethodologies_Reference.xml"</code> */
	public static final String REFERENCE_LCIA_METHODOLOGIES_FILE_NAME = "ILCDLCIAMethodologies_Reference.xml";

	/** Constant <code>REFERENCE_LCIA_METHODOLOGIES="STYLESHEETS_PATH_PREFIX+ REFERENCE_LCIA"{trunked}</code> */
	public static final String REFERENCE_LCIA_METHODOLOGIES = STYLESHEETS_PATH_PREFIX
			+ REFERENCE_LCIA_METHODOLOGIES_FILE_NAME;

	/*
	 * namespaces
	 */
	/** Constant <code>NS_SCHEMA_INSTANCE="http://www.w3.org/2001/XMLSchema-instan"{trunked}</code> */
	public static final String NS_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	/** Constant <code>NS_PROCESS="http://lca.jrc.it/ILCD/Process"</code> */
	public static final String NS_PROCESS = "http://lca.jrc.it/ILCD/Process";

	/** Constant <code>NS_LCIAMETHOD="http://lca.jrc.it/ILCD/LCIAMethod"</code> */
	public static final String NS_LCIAMETHOD = "http://lca.jrc.it/ILCD/LCIAMethod";

	/** Constant <code>NS_FLOW="http://lca.jrc.it/ILCD/Flow"</code> */
	public static final String NS_FLOW = "http://lca.jrc.it/ILCD/Flow";

	/** Constant <code>NS_FLOWPROPERTY="http://lca.jrc.it/ILCD/FlowProperty"</code> */
	public static final String NS_FLOWPROPERTY = "http://lca.jrc.it/ILCD/FlowProperty";

	/** Constant <code>NS_UNITGROUP="http://lca.jrc.it/ILCD/UnitGroup"</code> */
	public static final String NS_UNITGROUP = "http://lca.jrc.it/ILCD/UnitGroup";

	/** Constant <code>NS_SOURCE="http://lca.jrc.it/ILCD/Source"</code> */
	public static final String NS_SOURCE = "http://lca.jrc.it/ILCD/Source";

	/** Constant <code>NS_CONTACT="http://lca.jrc.it/ILCD/Contact"</code> */
	public static final String NS_CONTACT = "http://lca.jrc.it/ILCD/Contact";

	/** Constant <code>NS_LCMODEL="http://eplca.jrc.ec.europa.eu/ILCD/LifeCycleModel/2017"</code> */
	public static final String NS_LCMODEL = "http://eplca.jrc.ec.europa.eu/ILCD/LifeCycleModel/2017";

	/** Constant <code>NS_COMMON="http://lca.jrc.it/ILCD/Common"</code> */
	public static final String NS_COMMON = "http://lca.jrc.it/ILCD/Common";

	/*
	 * root elements
	 */
	/** Constant <code>LOCATIONS_ROOT_ELEMENT_NAME="ILCDLocations"</code> */
	public static final String LOCATIONS_ROOT_ELEMENT_NAME = "ILCDLocations";

	/** Constant <code>LCIAMETHODOLOGIES_ROOT_ELEMENT_NAME="ILCDLCIAMethodologies"</code> */
	public static final String LCIAMETHODOLOGIES_ROOT_ELEMENT_NAME = "ILCDLCIAMethodologies";

	/** Constant <code>CATEGORIES_ROOT_ELEMENT_NAME="CategorySystem"</code> */
	public static final String CATEGORIES_ROOT_ELEMENT_NAME = "CategorySystem";

	/** Constant <code>LCIAMETHOD_ROOT_ELEMENT_NAME="LCIAMethodDataSet"</code> */
	public static final String LCIAMETHOD_ROOT_ELEMENT_NAME = "LCIAMethodDataSet";

	/** Constant <code>CONTACT_ROOT_ELEMENT_NAME="contactDataSet"</code> */
	public static final String CONTACT_ROOT_ELEMENT_NAME = "contactDataSet";

	/** Constant <code>SOURCE_ROOT_ELEMENT_NAME="sourceDataSet"</code> */
	public static final String SOURCE_ROOT_ELEMENT_NAME = "sourceDataSet";

	/** Constant <code>UNIT_GROUP_ROOT_ELEMENT_NAME="unitGroupDataSet"</code> */
	public static final String UNIT_GROUP_ROOT_ELEMENT_NAME = "unitGroupDataSet";

	/** Constant <code>FLOW_PROPERTY_ROOT_ELEMENT_NAME="flowPropertyDataSet"</code> */
	public static final String FLOW_PROPERTY_ROOT_ELEMENT_NAME = "flowPropertyDataSet";

	/** Constant <code>FLOW_ROOT_ELEMENT_NAME="flowDataSet"</code> */
	public static final String FLOW_ROOT_ELEMENT_NAME = "flowDataSet";

	/** Constant <code>PROCESS_ROOT_ELEMENT_NAME="processDataSet"</code> */
	public static final String PROCESS_ROOT_ELEMENT_NAME = "processDataSet";

	/** Constant <code>LCMODEL_ROOT_ELEMENT_NAME="lifeCycleModelDataSet"</code> */
	public static final String LCMODEL_ROOT_ELEMENT_NAME = "lifeCycleModelDataSet";

	/*
	 * folder names
	 */
	/** Constant <code>FOLDER_NAME_ARCHIVE_ROOT="ILCD"</code> */
	public static final String FOLDER_NAME_ARCHIVE_ROOT = "ILCD";

	/** Constant <code>FOLDER_NAME_CONTACT="contacts"</code> */
	public static final String FOLDER_NAME_CONTACT = "contacts";

	/** Constant <code>FOLDER_NAME_SOURCE="sources"</code> */
	public static final String FOLDER_NAME_SOURCE = "sources";

	/** Constant <code>FOLDER_NAME_UNIT_GROUP="unitgroups"</code> */
	public static final String FOLDER_NAME_UNIT_GROUP = "unitgroups";

	/** Constant <code>FOLDER_NAME_FLOW_PROPERTY="flowproperties"</code> */
	public static final String FOLDER_NAME_FLOW_PROPERTY = "flowproperties";

	/** Constant <code>FOLDER_NAME_FLOW="flows"</code> */
	public static final String FOLDER_NAME_FLOW = "flows";

	/** Constant <code>FOLDER_NAME_LCIA_METHOD="lciamethods"</code> */
	public static final String FOLDER_NAME_LCIA_METHOD = "lciamethods";

	/** Constant <code>FOLDER_NAME_PROCESS="processes"</code> */
	public static final String FOLDER_NAME_PROCESS = "processes";

	/** Constant <code>FOLDER_NAME_LCMODEL="lifecyclemodels"</code> */
	public static final String FOLDER_NAME_LCMODEL = "lifecyclemodels";

	/*
	 * schema names
	 */
	/** Constant <code>PROCESS_SCHEMA_NAME="ILCD_ProcessDataSet.xsd"</code> */
	public static final String PROCESS_SCHEMA_NAME = "ILCD_ProcessDataSet.xsd";

	/** Constant <code>LCIAMETHOD_SCHEMA_NAME="ILCD_LCIAMethodDataSet.xsd"</code> */
	public static final String LCIAMETHOD_SCHEMA_NAME = "ILCD_LCIAMethodDataSet.xsd";

	/** Constant <code>FLOW_SCHEMA_NAME="ILCD_FlowDataSet.xsd"</code> */
	public static final String FLOW_SCHEMA_NAME = "ILCD_FlowDataSet.xsd";

	/** Constant <code>FLOW_PROPERTY_SCHEMA_NAME="ILCD_FlowPropertyDataSet.xsd"</code> */
	public static final String FLOW_PROPERTY_SCHEMA_NAME = "ILCD_FlowPropertyDataSet.xsd";

	/** Constant <code>UNIT_GROUP_SCHEMA_NAME="ILCD_UnitGroupDataSet.xsd"</code> */
	public static final String UNIT_GROUP_SCHEMA_NAME = "ILCD_UnitGroupDataSet.xsd";

	/** Constant <code>SOURCE_SCHEMA_NAME="ILCD_SourceDataSet.xsd"</code> */
	public static final String SOURCE_SCHEMA_NAME = "ILCD_SourceDataSet.xsd";

	/** Constant <code>CONTACT_SCHEMA_NAME="ILCD_ContactDataSet.xsd"</code> */
	public static final String CONTACT_SCHEMA_NAME = "ILCD_ContactDataSet.xsd";

	/** Constant <code>LCMODEL_SCHEMA_NAME="ILCD_LifeCycleModelDataSet.xsd"</code> */
	public static final String LCMODEL_SCHEMA_NAME = "ILCD_LifeCycleModelDataSet.xsd";

	/*
	 * schemas
	 */
	/** Constant <code>PROCESS_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + PROCESS_SCHE"{trunked}</code> */
	public static final String PROCESS_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + PROCESS_SCHEMA_NAME;

	/** Constant <code>LCIAMETHOD_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + LCIAMETHOD_S"{trunked}</code> */
	public static final String LCIAMETHOD_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + LCIAMETHOD_SCHEMA_NAME;

	/** Constant <code>FLOW_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + FLOW_SCHEMA_"{trunked}</code> */
	public static final String FLOW_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + FLOW_SCHEMA_NAME;

	/** Constant <code>FLOW_PROPERTY_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + FLOW_PROPERT"{trunked}</code> */
	public static final String FLOW_PROPERTY_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + FLOW_PROPERTY_SCHEMA_NAME;

	/** Constant <code>UNIT_GROUP_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + UNIT_GROUP_S"{trunked}</code> */
	public static final String UNIT_GROUP_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + UNIT_GROUP_SCHEMA_NAME;

	/** Constant <code>SOURCE_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + SOURCE_SCHEM"{trunked}</code> */
	public static final String SOURCE_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + SOURCE_SCHEMA_NAME;

	/** Constant <code>CONTACT_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + CONTACT_SCHE"{trunked}</code> */
	public static final String CONTACT_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + CONTACT_SCHEMA_NAME;

	/** Constant <code>LCMODEL_SCHEMA="ILCD_SCHEMAS_PATH_PREFIX + LCMODEL_SCHEMA"{trunked}</code> */
	public static final String LCMODEL_SCHEMA = ILCD_SCHEMAS_PATH_PREFIX + LCMODEL_SCHEMA_NAME;

}
