package com.okworx.ilcd.validation.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>Statistics class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class Statistics {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected String aspects; 
	
	protected Set<IDatasetReference> validProcesses = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validFlows = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validFlowProperties = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validUnitGroups = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validSources = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validContacts = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validLCIAMethods = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validLCModels = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> validExternalFiles = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidProcesses = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidFlows = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidFlowProperties = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidUnitGroups = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidSources = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidContacts = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidLCIAMethods = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidLCModels = new HashSet<IDatasetReference>();

	protected Set<IDatasetReference> invalidExternalFiles = new HashSet<IDatasetReference>();

	/**
	 * <p>update.</p>
	 *
	 * @param reference a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param success a boolean.
	 */
	public void update(IDatasetReference reference, boolean success) {
		if (log.isTraceEnabled())
			log.trace("counting " + (success ? "" : "un") + "successful validation of " + reference.getDatasetType());

		switch (reference.getDatasetType()) {
		case CONTACT:
			if (success) {
				this.validContacts.add(reference);
			} else {
				this.invalidContacts.add(reference);
				this.validContacts.remove(reference);
			}
			break;
		case EXTERNAL_FILE:
			if (success) {
				this.validExternalFiles.add(reference);
			} else {
				this.invalidExternalFiles.add(reference);
				this.validExternalFiles.remove(reference);
			}
			break;
		case FLOW:
			if (success) {
				this.validFlows.add(reference);
			} else {
				this.invalidFlows.add(reference);
				this.validFlows.remove(reference);
			}
			break;
		case FLOWPROPERTY:
			if (success) {
				this.validFlowProperties.add(reference);
			} else {
				this.invalidFlowProperties.add(reference);
				this.validFlowProperties.remove(reference);
			}
			break;
		case LCIAMETHOD:
			if (success) {
				this.validLCIAMethods.add(reference);
			} else {
				this.invalidLCIAMethods.add(reference);
				this.validLCIAMethods.remove(reference);
			}
			break;
		case PROCESS:
			if (success) {
				this.validProcesses.add(reference);
			} else {
				this.invalidProcesses.add(reference);
				this.validProcesses.remove(reference);
			}
			break;
		case SOURCE:
			if (success) {
				this.validSources.add(reference);
			} else {
				this.invalidSources.add(reference);
				this.validSources.remove(reference);
			}
			break;
		case UNITGROUP:
			if (success) {
				this.validUnitGroups.add(reference);
			} else {
				this.invalidUnitGroups.add(reference);
				this.validUnitGroups.remove(reference);
			}
			break;
		case LCMODEL:
			if (success) {
				this.validLCModels.add(reference);
			} else {
				this.invalidLCModels.add(reference);
				this.validLCModels.remove(reference);
			}
			break;
		}
	}

	/**
	 * This method combines two statistics by merely adding them.
	 * Use only for statistics from sets of disjunct data.
	 *
	 * @param stats a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public void add(Statistics stats) {
		this.validContacts.addAll(stats.getValidContacts());
		this.validFlowProperties.addAll(stats.getValidFlowProperties());
		this.validFlows.addAll(stats.getValidFlows());
		this.validLCIAMethods.addAll(stats.getValidLCIAMethods());
		this.validProcesses.addAll(stats.getValidProcesses());
		this.validSources.addAll(stats.getValidSources());
		this.validUnitGroups.addAll(stats.getValidUnitGroups());
		this.validLCModels.addAll(stats.getValidLCModels());
		this.validExternalFiles.addAll(stats.getValidExternalFiles());
		
		this.invalidContacts.addAll(stats.getInvalidContacts());
		this.invalidFlowProperties.addAll(stats.getInvalidFlowProperties());
		this.invalidFlows.addAll(stats.getInvalidFlows());
		this.invalidLCIAMethods.addAll(stats.getInvalidLCIAMethods());
		this.invalidProcesses.addAll(stats.getInvalidProcesses());
		this.invalidSources.addAll(stats.getInvalidSources());
		this.invalidUnitGroups.addAll(stats.getInvalidUnitGroups());
		this.invalidLCModels.addAll(stats.getInvalidLCModels());
		this.invalidExternalFiles.addAll(stats.getInvalidExternalFiles());
		
		this.aspects = stats.getAspects();
	}

	/**
	 * Merges the results of two Statistics. As if a dataset has been found
	 * invalid once, its status cannot revert back to valid, thus the number
	 * of valid datasets will be constant or reduced with each merge operation
	 * but never growing.
	 *
	 * @param stats a {@link com.okworx.ilcd.validation.util.Statistics} object.
	 */
	public void merge(Statistics stats) {

		this.invalidContacts.addAll(stats.getInvalidContacts());
		this.invalidFlowProperties.addAll(stats.getInvalidFlowProperties());
		this.invalidFlows.addAll(stats.getInvalidFlows());
		this.invalidLCIAMethods.addAll(stats.getInvalidLCIAMethods());
		this.invalidProcesses.addAll(stats.getInvalidProcesses());
		this.invalidSources.addAll(stats.getInvalidSources());
		this.invalidUnitGroups.addAll(stats.getInvalidUnitGroups());
		this.invalidLCModels.addAll(stats.getInvalidLCModels());
		this.invalidExternalFiles.addAll(stats.getInvalidExternalFiles());

		this.validContacts.removeAll(stats.getInvalidContacts());
		this.validFlowProperties.removeAll(stats.getInvalidFlowProperties());
		this.validFlows.removeAll(stats.getInvalidFlows());
		this.validLCIAMethods.removeAll(stats.getInvalidLCIAMethods());
		this.validProcesses.removeAll(stats.getInvalidProcesses());
		this.validSources.removeAll(stats.getInvalidSources());
		this.validUnitGroups.removeAll(stats.getInvalidUnitGroups());
		this.validLCModels.removeAll(stats.getInvalidLCModels());
		this.validExternalFiles.removeAll(stats.getInvalidExternalFiles());
		
		this.aspects = stats.getAspects();
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Total files processed: ").append(this.getTotalValidCount() + this.getTotalInvalidCount());
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		builder.append("Process datasets found valid: ").append(this.getValidProcessesCount()).append(" / invalid: ").append(this.getInvalidProcessesCount());
		builder.append(System.lineSeparator());
		builder.append("Flow datasets found valid: ").append(this.getValidFlowsCount()).append(" / invalid: ").append(this.getInvalidFlowsCount());
		builder.append(System.lineSeparator());
		builder.append("Source datasets found valid: ").append(this.getValidSourcesCount()).append(" / invalid: ").append(this.getInvalidSourcesCount());
		builder.append(System.lineSeparator());
		builder.append("Contact datasets found valid: ").append(this.getValidContactsCount()).append(" / invalid: ").append(this.getInvalidContactsCount());
		builder.append(System.lineSeparator());
		builder.append("LCIA Method datasets found valid: ").append(this.getValidLCIAMethodsCount()).append(" / invalid: ").append(this.getInvalidLCIAMethodsCount());
		builder.append(System.lineSeparator());
		builder.append("Flow Property datasets found valid: ").append(this.getValidFlowPropertiesCount()).append(" / invalid: ").append(this.getInvalidFlowPropertiesCount());
		builder.append(System.lineSeparator());
		builder.append("Unit Group datasets found valid: ").append(this.getValidUnitGroupsCount()).append(" / invalid: ").append(this.getInvalidUnitGroupsCount());
		builder.append(System.lineSeparator());
		builder.append("Life Cycle Model datasets found valid: ").append(this.getValidLCModelsCount()).append(" / invalid: ").append(this.getInvalidLCModelsCount());
		builder.append(System.lineSeparator());
		builder.append("External files found valid: ").append(this.getValidExternalFilesCount()).append(" / invalid: ").append(this.getInvalidExternalFilesCount());
		builder.append(System.lineSeparator());
		
		return builder.toString();
	}

	/**
	 * <p>getTotalProcessesCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalProcessesCount() {
		return validProcesses.size() + invalidProcesses.size();
	}

	/**
	 * <p>getTotalFlowsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalFlowsCount() {
		return validFlows.size() + invalidFlows.size();
	}

	/**
	 * <p>getTotalFlowPropertiesCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalFlowPropertiesCount() {
		return validFlowProperties.size() + invalidFlowProperties.size();
	}

	/**
	 * <p>getTotalUnitGroupsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalUnitGroupsCount() {
		return validUnitGroups.size() + invalidUnitGroups.size();
	}

	/**
	 * <p>getTotalSourcesCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalSourcesCount() {
		return validSources.size() + invalidSources.size();
	}

	/**
	 * <p>getTotalContactsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalContactsCount() {
		return validContacts.size() + invalidContacts.size();
	}

	/**
	 * <p>getTotalLCIAMethodsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalLCIAMethodsCount() {
		return validLCIAMethods.size() + invalidLCIAMethods.size();
	}

	/**
	 * <p>getTotalLCModelsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalLCModelsCount() {
		return validLCModels.size() + invalidLCModels.size();
	}

	/**
	 * <p>getTotalExternalFilesCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalExternalFilesCount() {
		return validExternalFiles.size() + invalidExternalFiles.size();
	}

	/**
	 * <p>getValidProcessesCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidProcessesCount() {
		return validProcesses.size();
	}

	/**
	 * <p>getValidFlowsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidFlowsCount() {
		return validFlows.size();
	}

	/**
	 * <p>getValidFlowPropertiesCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidFlowPropertiesCount() {
		return validFlowProperties.size();
	}

	/**
	 * <p>getValidUnitGroupsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidUnitGroupsCount() {
		return validUnitGroups.size();
	}

	/**
	 * <p>getValidSourcesCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidSourcesCount() {
		return validSources.size();
	}

	/**
	 * <p>getValidContactsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidContactsCount() {
		return validContacts.size();
	}

	/**
	 * <p>getValidLCIAMethodsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidLCIAMethodsCount() {
		return validLCIAMethods.size();
	}

	/**
	 * <p>getValidLCModelsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidLCModelsCount() {
		return validLCModels.size();
	}

	/**
	 * <p>getValidExternalFilesCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidExternalFilesCount() {
		return validExternalFiles.size();
	}

	/**
	 * <p>getInvalidProcessesCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidProcessesCount() {
		return invalidProcesses.size();
	}

	/**
	 * <p>getInvalidFlowsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidFlowsCount() {
		return invalidFlows.size();
	}

	/**
	 * <p>getInvalidFlowPropertiesCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidFlowPropertiesCount() {
		return invalidFlowProperties.size();
	}

	/**
	 * <p>getInvalidUnitGroupsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidUnitGroupsCount() {
		return invalidUnitGroups.size();
	}

	/**
	 * <p>getInvalidSourcesCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidSourcesCount() {
		return invalidSources.size();
	}

	/**
	 * <p>getInvalidContactsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidContactsCount() {
		return invalidContacts.size();
	}

	/**
	 * <p>getInvalidLCIAMethodsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidLCIAMethodsCount() {
		return invalidLCIAMethods.size();
	}

	/**
	 * <p>getInvalidLCModelsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidLCModelsCount() {
		return invalidLCModels.size();
	}

	/**
	 * <p>getInvalidExternalFilesCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidExternalFilesCount() {
		return invalidExternalFiles.size();
	}

	/**
	 * <p>getTotalValidCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalValidCount() {
		return validContacts.size() + validFlowProperties.size() + validFlows.size() + validLCIAMethods.size()
				+ validProcesses.size() + validSources.size() + validUnitGroups.size() + validLCModels.size() + validExternalFiles.size();
	}

	/**
	 * <p>getTotalInvalidCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalInvalidCount() {
		return invalidContacts.size() + invalidFlowProperties.size() + invalidFlows.size() + invalidLCIAMethods.size()
				+ invalidProcesses.size() + invalidSources.size() + invalidUnitGroups.size() + invalidLCModels.size() + invalidExternalFiles.size();
	}
	
	/**
	 * <p>getValidDatasetsCount.</p>
	 *
	 * @return a int.
	 */
	public int getValidDatasetsCount() {
		return validContacts.size() + validFlowProperties.size() + validFlows.size() + validLCIAMethods.size()
				+ validProcesses.size() + validSources.size() + validUnitGroups.size() + validLCModels.size();
	}

	/**
	 * <p>getInvalidDatasetsCount.</p>
	 *
	 * @return a int.
	 */
	public int getInvalidDatasetsCount() {
		return invalidContacts.size() + invalidFlowProperties.size() + invalidFlows.size() + invalidLCIAMethods.size()
				+ invalidProcesses.size() + invalidSources.size() + invalidUnitGroups.size() + invalidLCModels.size();
	}
	
	/**
	 * <p>getTotalCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalCount() {
		return getTotalValidCount() + getTotalInvalidCount();
	}
	
	/**
	 * <p>getTotalDatasetsCount.</p>
	 *
	 * @return a int.
	 */
	public int getTotalDatasetsCount() {
		return getValidDatasetsCount() + getInvalidDatasetsCount();
	}
	
	/**
	 * <p>Getter for the field <code>validProcesses</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidProcesses() {
		return validProcesses;
	}

	/**
	 * <p>Setter for the field <code>validProcesses</code>.</p>
	 *
	 * @param validProcesses a {@link java.util.Set} object.
	 */
	public void setValidProcesses(Set<IDatasetReference> validProcesses) {
		this.validProcesses = validProcesses;
	}

	/**
	 * <p>Getter for the field <code>validFlows</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidFlows() {
		return validFlows;
	}

	/**
	 * <p>Setter for the field <code>validFlows</code>.</p>
	 *
	 * @param validFlows a {@link java.util.Set} object.
	 */
	public void setValidFlows(Set<IDatasetReference> validFlows) {
		this.validFlows = validFlows;
	}

	/**
	 * <p>Getter for the field <code>validFlowProperties</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidFlowProperties() {
		return validFlowProperties;
	}

	/**
	 * <p>Setter for the field <code>validFlowProperties</code>.</p>
	 *
	 * @param validFlowProperties a {@link java.util.Set} object.
	 */
	public void setValidFlowProperties(Set<IDatasetReference> validFlowProperties) {
		this.validFlowProperties = validFlowProperties;
	}

	/**
	 * <p>Getter for the field <code>validUnitGroups</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidUnitGroups() {
		return validUnitGroups;
	}

	/**
	 * <p>Setter for the field <code>validUnitGroups</code>.</p>
	 *
	 * @param validUnitGroups a {@link java.util.Set} object.
	 */
	public void setValidUnitGroups(Set<IDatasetReference> validUnitGroups) {
		this.validUnitGroups = validUnitGroups;
	}

	/**
	 * <p>Getter for the field <code>validSources</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidSources() {
		return validSources;
	}

	/**
	 * <p>Setter for the field <code>validSources</code>.</p>
	 *
	 * @param validSources a {@link java.util.Set} object.
	 */
	public void setValidSources(Set<IDatasetReference> validSources) {
		this.validSources = validSources;
	}

	/**
	 * <p>Getter for the field <code>validContacts</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidContacts() {
		return validContacts;
	}

	/**
	 * <p>Setter for the field <code>validContacts</code>.</p>
	 *
	 * @param validContacts a {@link java.util.Set} object.
	 */
	public void setValidContacts(Set<IDatasetReference> validContacts) {
		this.validContacts = validContacts;
	}

	/**
	 * <p>Getter for the field <code>validLCIAMethods</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidLCIAMethods() {
		return validLCIAMethods;
	}

	/**
	 * <p>Setter for the field <code>validLCIAMethods</code>.</p>
	 *
	 * @param validLCIAMethods a {@link java.util.Set} object.
	 */
	public void setValidLCIAMethods(Set<IDatasetReference> validLCIAMethods) {
		this.validLCIAMethods = validLCIAMethods;
	}

	/**
	 * <p>Getter for the field <code>invalidProcesses</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidProcesses() {
		return invalidProcesses;
	}

	/**
	 * <p>Setter for the field <code>invalidProcesses</code>.</p>
	 *
	 * @param invalidProcesses a {@link java.util.Set} object.
	 */
	public void setInvalidProcesses(Set<IDatasetReference> invalidProcesses) {
		this.invalidProcesses = invalidProcesses;
	}

	/**
	 * <p>Getter for the field <code>invalidFlows</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidFlows() {
		return invalidFlows;
	}

	/**
	 * <p>Setter for the field <code>invalidFlows</code>.</p>
	 *
	 * @param invalidFlows a {@link java.util.Set} object.
	 */
	public void setInvalidFlows(Set<IDatasetReference> invalidFlows) {
		this.invalidFlows = invalidFlows;
	}

	/**
	 * <p>Getter for the field <code>invalidFlowProperties</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidFlowProperties() {
		return invalidFlowProperties;
	}

	/**
	 * <p>Setter for the field <code>invalidFlowProperties</code>.</p>
	 *
	 * @param invalidFlowProperties a {@link java.util.Set} object.
	 */
	public void setInvalidFlowProperties(Set<IDatasetReference> invalidFlowProperties) {
		this.invalidFlowProperties = invalidFlowProperties;
	}

	/**
	 * <p>Getter for the field <code>invalidUnitGroups</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidUnitGroups() {
		return invalidUnitGroups;
	}

	/**
	 * <p>Setter for the field <code>invalidUnitGroups</code>.</p>
	 *
	 * @param invalidUnitGroups a {@link java.util.Set} object.
	 */
	public void setInvalidUnitGroups(Set<IDatasetReference> invalidUnitGroups) {
		this.invalidUnitGroups = invalidUnitGroups;
	}

	/**
	 * <p>Getter for the field <code>invalidSources</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidSources() {
		return invalidSources;
	}

	/**
	 * <p>Setter for the field <code>invalidSources</code>.</p>
	 *
	 * @param invalidSources a {@link java.util.Set} object.
	 */
	public void setInvalidSources(Set<IDatasetReference> invalidSources) {
		this.invalidSources = invalidSources;
	}

	/**
	 * <p>Getter for the field <code>invalidContacts</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidContacts() {
		return invalidContacts;
	}

	/**
	 * <p>Setter for the field <code>invalidContacts</code>.</p>
	 *
	 * @param invalidContacts a {@link java.util.Set} object.
	 */
	public void setInvalidContacts(Set<IDatasetReference> invalidContacts) {
		this.invalidContacts = invalidContacts;
	}

	/**
	 * <p>Getter for the field <code>invalidLCIAMethods</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidLCIAMethods() {
		return invalidLCIAMethods;
	}

	/**
	 * <p>Setter for the field <code>invalidLCIAMethods</code>.</p>
	 *
	 * @param invalidLCIAMethods a {@link java.util.Set} object.
	 */
	public void setInvalidLCIAMethods(Set<IDatasetReference> invalidLCIAMethods) {
		this.invalidLCIAMethods = invalidLCIAMethods;
	}

	/**
	 * <p>Getter for the field <code>validExternalFiles</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getValidExternalFiles() {
		return validExternalFiles;
	}

	/**
	 * <p>Setter for the field <code>validExternalFiles</code>.</p>
	 *
	 * @param validExtFiles a {@link java.util.Set} object.
	 */
	public void setValidExternalFiles(Set<IDatasetReference> validExtFiles) {
		this.validExternalFiles = validExtFiles;
	}

	/**
	 * <p>Getter for the field <code>invalidExternalFiles</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<IDatasetReference> getInvalidExternalFiles() {
		return invalidExternalFiles;
	}

	/**
	 * <p>Setter for the field <code>invalidExternalFiles</code>.</p>
	 *
	 * @param invalidExtFiles a {@link java.util.Set} object.
	 */
	public void setInvalidExternalFiles(Set<IDatasetReference> invalidExtFiles) {
		this.invalidExternalFiles = invalidExtFiles;
	}

	/**
	 * @return the validLCModels
	 */
	public Set<IDatasetReference> getValidLCModels() {
		return validLCModels;
	}

	/**
	 * @param validLCModels the validLCModels to set
	 */
	public void setValidLCModels(Set<IDatasetReference> validLCModels) {
		this.validLCModels = validLCModels;
	}

	/**
	 * @return the invalidLCModels
	 */
	public Set<IDatasetReference> getInvalidLCModels() {
		return invalidLCModels;
	}

	/**
	 * @param invalidLCModels the invalidLCModels to set
	 */
	public void setInvalidLCModels(Set<IDatasetReference> invalidLCModels) {
		this.invalidLCModels = invalidLCModels;
	}

	/**
	 * <p>Getter for the field <code>aspects</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspects() {
		return aspects;
	}

	/**
	 * <p>Setter for the field <code>aspects</code>.</p>
	 *
	 * @param aspects a {@link java.lang.String} object.
	 */
	public void setAspects(String aspects) {
		this.aspects = aspects;
	}

}
