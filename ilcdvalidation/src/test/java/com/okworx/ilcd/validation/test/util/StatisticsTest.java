package com.okworx.ilcd.validation.test.util;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.reference.DatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.Statistics;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsTest {

	private final int validProcesses = 1;

	private final int validFlows = 2;

	private final int validFlowProperties = 4;

	private final int validUnitGroups = 8;

	private final int validSources = 16;

	private final int validContacts = 32;

	private final int validLCIAMethods = 64;

	private final int validLCModels = 0;

	private final int invalidProcesses = 2;

	private final int invalidFlows = 4;

	private final int invalidFlowProperties = 8;

	private final int invalidUnitGroups = 16;

	private final int invalidSources = 32;

	private final int invalidContacts = 64;

	private final int invalidLCIAMethods = 128;

	private final int invalidLCModels = 0;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private Set<IDatasetReference> createRefs(DatasetType type, int number) {

		Set<IDatasetReference> references = new HashSet<IDatasetReference>();

		for (int i = 0; i < number; i++) {
			UUID uuid = UUID.randomUUID();
			references.add(new DatasetReference(uuid.toString(), type));
		}

		return references;
	}

	@Test
	public void mergeTest() {
		Statistics stats = new Statistics();
		stats.setInvalidContacts(createRefs(DatasetType.CONTACT, invalidContacts));
		stats.setInvalidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, invalidFlowProperties));
		stats.setInvalidFlows(createRefs(DatasetType.FLOW, invalidFlows));
		stats.setInvalidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, invalidLCIAMethods));
		stats.setInvalidProcesses(createRefs(DatasetType.PROCESS, invalidProcesses));
		stats.setInvalidSources(createRefs(DatasetType.SOURCE, invalidSources));
		stats.setInvalidUnitGroups(createRefs(DatasetType.UNITGROUP, invalidUnitGroups));
		stats.setInvalidLCModels(createRefs(DatasetType.LCMODEL, invalidLCModels));

		stats.setValidContacts(createRefs(DatasetType.CONTACT, validContacts));
		stats.setValidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, validFlowProperties));
		stats.setValidFlows(createRefs(DatasetType.FLOW, validFlows));
		stats.setValidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, validLCIAMethods));
		stats.setValidProcesses(createRefs(DatasetType.PROCESS, validProcesses));
		stats.setValidSources(createRefs(DatasetType.SOURCE, validSources));
		stats.setValidUnitGroups(createRefs(DatasetType.UNITGROUP, validUnitGroups));
		stats.setValidLCModels(createRefs(DatasetType.LCMODEL, validLCModels));

		Statistics stats1 = new Statistics();
		stats1.setInvalidContacts(createRefs(DatasetType.CONTACT, invalidContacts));
		stats1.setInvalidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, invalidFlowProperties));
		stats1.setInvalidFlows(createRefs(DatasetType.FLOW, invalidFlows));
		stats1.setInvalidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, invalidLCIAMethods));
		stats1.setInvalidProcesses(createRefs(DatasetType.PROCESS, invalidProcesses));
		stats1.setInvalidSources(createRefs(DatasetType.SOURCE, invalidSources));
		stats1.setInvalidUnitGroups(createRefs(DatasetType.UNITGROUP, invalidUnitGroups));
		stats1.setInvalidLCModels(createRefs(DatasetType.LCMODEL, invalidLCModels));

		stats1.setValidContacts(createRefs(DatasetType.CONTACT, validContacts));
		stats1.setValidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, validFlowProperties));
		stats1.setValidFlows(createRefs(DatasetType.FLOW, validFlows));
		stats1.setValidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, validLCIAMethods));
		stats1.setValidProcesses(createRefs(DatasetType.PROCESS, validProcesses));
		stats1.setValidSources(createRefs(DatasetType.SOURCE, validSources));
		stats1.setValidUnitGroups(createRefs(DatasetType.UNITGROUP, validUnitGroups));
		stats1.setValidLCModels(createRefs(DatasetType.LCMODEL, validLCModels));

		stats.merge(stats1);

//		assertEquals(2 * invalidContacts, stats.getInvalidContactsCount());
//		assertEquals(2 * invalidFlowProperties, stats.getInvalidFlowPropertiesCount());
//		assertEquals(2 * invalidFlows, stats.getInvalidFlowsCount());
//		assertEquals(2 * invalidLCIAMethods, stats.getInvalidLCIAMethodsCount());
//		assertEquals(2 * invalidProcesses, stats.getInvalidProcessesCount());
//		assertEquals(2 * invalidSources, stats.getInvalidSourcesCount());
//		assertEquals(2 * invalidUnitGroups, stats.getInvalidUnitGroupsCount());
//
//		assertEquals(2 * validContacts, stats.getValidContactsCount());
//		assertEquals(2 * validFlowProperties, stats.getValidFlowPropertiesCount());
//		assertEquals(2 * validFlows, stats.getValidFlowsCount());
//		assertEquals(2 * validLCIAMethods, stats.getValidLCIAMethodsCount());
//		assertEquals(2 * validProcesses, stats.getValidProcessesCount());
//		assertEquals(2 * validSources, stats.getValidSourcesCount());
//		assertEquals(2 * validUnitGroups, stats.getValidUnitGroupsCount());

	}

	@Test
	public void totalsTest() {
		Statistics stats = new Statistics();
		stats.setInvalidContacts(createRefs(DatasetType.CONTACT, invalidContacts));
		stats.setInvalidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, invalidFlowProperties));
		stats.setInvalidFlows(createRefs(DatasetType.FLOW, invalidFlows));
		stats.setInvalidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, invalidLCIAMethods));
		stats.setInvalidProcesses(createRefs(DatasetType.PROCESS, invalidProcesses));
		stats.setInvalidSources(createRefs(DatasetType.SOURCE, invalidSources));
		stats.setInvalidUnitGroups(createRefs(DatasetType.UNITGROUP, invalidUnitGroups));
		stats.setInvalidLCModels(createRefs(DatasetType.LCMODEL, invalidLCModels));

		stats.setValidContacts(createRefs(DatasetType.CONTACT, validContacts));
		stats.setValidFlowProperties(createRefs(DatasetType.FLOWPROPERTY, validFlowProperties));
		stats.setValidFlows(createRefs(DatasetType.FLOW, validFlows));
		stats.setValidLCIAMethods(createRefs(DatasetType.LCIAMETHOD, validLCIAMethods));
		stats.setValidProcesses(createRefs(DatasetType.PROCESS, validProcesses));
		stats.setValidSources(createRefs(DatasetType.SOURCE, validSources));
		stats.setValidUnitGroups(createRefs(DatasetType.UNITGROUP, validUnitGroups));
		stats.setValidLCModels(createRefs(DatasetType.LCMODEL, validLCModels));

		assertEquals(invalidContacts + validContacts, stats.getTotalContactsCount());
		assertEquals(invalidFlowProperties + validFlowProperties, stats.getTotalFlowPropertiesCount());
		assertEquals(invalidFlows + validFlows, stats.getTotalFlowsCount());
		assertEquals(invalidLCIAMethods + validLCIAMethods, stats.getTotalLCIAMethodsCount());
		assertEquals(invalidProcesses + validProcesses, stats.getTotalProcessesCount());
		assertEquals(invalidSources + validSources, stats.getTotalSourcesCount());
		assertEquals(invalidUnitGroups + validUnitGroups, stats.getTotalUnitGroupsCount());
		assertEquals(invalidLCModels + validLCModels, stats.getTotalLCModelsCount());

		assertEquals(validContacts + validFlowProperties + validFlows + validLCIAMethods + validProcesses
				+ validSources + validUnitGroups + validLCModels, stats.getTotalValidCount());
		assertEquals(invalidContacts + invalidFlowProperties + invalidFlows + invalidLCIAMethods + invalidProcesses
				+ invalidSources + invalidUnitGroups + invalidLCModels, stats.getTotalInvalidCount());

	}
}
