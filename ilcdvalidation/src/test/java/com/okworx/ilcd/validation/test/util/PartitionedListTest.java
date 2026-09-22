package com.okworx.ilcd.validation.test.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.okworx.ilcd.validation.util.PartitionedList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PartitionedListTest {

	private int processors = Runtime.getRuntime().availableProcessors();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNumThreads() {
		Collection<String> coll = new ArrayList<String>();
		for (int i = 0; i < processors; i++)
			coll.add("foo");
		PartitionedList<String> pl = new PartitionedList<String>(coll, 0);
		assertEquals(Runtime.getRuntime().availableProcessors(), pl.getNumThreads());
	}

	@Test
	public void testNumThreads2() {
		Collection<String> coll = new ArrayList<String>();
		for (int i = 0; i < processors - 1; i++)
			coll.add("foo");
		PartitionedList<String> pl = new PartitionedList<String>(coll, 0);
		assertEquals(1, pl.getNumThreads());
	}

	@Test
	public void testSingleEntry() {
		Collection<String> coll = new ArrayList<String>();
		coll.add("foo");
		PartitionedList<String> pl = new PartitionedList<String>(coll, 0);
		assertEquals(1, pl.getPartitions().size());
	}

	@Test
	public void testEmptyList() {
		Collection<String> coll = new ArrayList<String>();
		PartitionedList<String> pl = new PartitionedList<String>(coll, 0);
	}


}
