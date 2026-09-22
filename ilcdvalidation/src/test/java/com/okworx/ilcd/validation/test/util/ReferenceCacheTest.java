package com.okworx.ilcd.validation.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.common.CustomValidationContext;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.reference.ReferenceCache;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReferenceCacheTest {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	private ReferenceCache cache = new ReferenceCache();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testFromDir() {
		this.cache.initializeFromDir(new File("src/test/resources/datasets/ReferenceFlows/reference"));
		assertEquals(237, this.cache.getLinks().size());
	}

	@Test
	public void testFromZIP() {
		this.cache.initializeFromArchive(new File("src/test/resources/datasets/ReferenceFlows/reference.zip"));
		assertEquals(237, this.cache.getLinks().size());
	}

	@Test
	public void testSimple() {
		this.cache.initializeFromDir(new File("src/test/resources/datasets/ReferenceFlows/localized"));
		assertEquals(4, this.cache.getLinks().size());
		
		for (IDatasetReference ref : this.cache.getLinks().values()) {
			log.info(ref.getName());
			assertTrue(StringUtils.isNotEmpty(ref.getName()));
		}
	}

	@Test
	public void testLocalizedEn() {
		CustomValidationContext ctx = new CustomValidationContext(Locale.ENGLISH);
		this.cache = new ReferenceCache();
		this.cache.setValidationContext(ctx);
		
		this.cache.initializeFromDir(new File("src/test/resources/datasets/ReferenceFlows/localized"));
		assertEquals(4, this.cache.getLinks().size());

		for (IDatasetReference ref : this.cache.getLinks().values()) {
			log.info(ref.getUuid() + " " + ref.getName());
			assertTrue(StringUtils.isNotEmpty(ref.getName()));
		}

		assertEquals("Process steam from natural gas", this.cache.getLinks().get("0cbf76cc-0192-4617-acd3-0fdb3cecf6c7").getName());
		assertEquals("Electricity Mix", this.cache.getLinks().get("0d87bcf9-cbe5-4df9-8715-659f71c6e288").getName());
		assertEquals("Strommix", this.cache.getLinks().get("fa1b40db-5645-4db8-a887-eb09300b7b74").getName());
		assertEquals("Electricity Mix", this.cache.getLinks().get("0a1b40db-5645-4db8-a887-eb09300b7b74").getName());
	}

	@Test
	public void testLocalizedDe() {
		CustomValidationContext ctx = new CustomValidationContext(Locale.GERMAN);
		this.cache = new ReferenceCache();
		this.cache.setValidationContext(ctx);
		
		this.cache.initializeFromDir(new File("src/test/resources/datasets/ReferenceFlows/localized"));
		assertEquals(4, this.cache.getLinks().size());

		for (IDatasetReference ref : this.cache.getLinks().values()) {
			log.info(ref.getUuid() + " " + ref.getName());
			assertTrue(StringUtils.isNotEmpty(ref.getName()));
		}

		assertEquals("Prozessdampf aus Erdgas", this.cache.getLinks().get("0cbf76cc-0192-4617-acd3-0fdb3cecf6c7").getName());
		assertEquals("Electricity Mix", this.cache.getLinks().get("0d87bcf9-cbe5-4df9-8715-659f71c6e288").getName());
		assertEquals("Strommix", this.cache.getLinks().get("fa1b40db-5645-4db8-a887-eb09300b7b74").getName());
		assertEquals("Strommix", this.cache.getLinks().get("0a1b40db-5645-4db8-a887-eb09300b7b74").getName());
}

}
