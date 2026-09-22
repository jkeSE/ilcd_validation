package com.okworx.ilcd.validation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * When processing large collections of objects using multiple threads, we use
 * this class to calculate the number of threads depending on the number of
 * available processors and the resulting chunk size. Subsequently, the
 * collection handed over as an argument will be partitioned accordingly into
 * ArrayList objects.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class PartitionedList<T extends Object> {

	/** Constant <code>DEFAULT_THRESHOLD=20</code> */
	public static final int DEFAULT_THRESHOLD = 20;
	
	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	protected List<List<T>> partitions = new ArrayList<List<T>>();

	private int chunkSize;

	private int numThreads;
	
	private int threshold;

	/**
	 * <p>Constructor for PartitionedList.</p>
	 *
	 * @param coll a {@link java.util.Collection} object.
	 */
	public PartitionedList(Collection<T> coll) {
		this(coll, DEFAULT_THRESHOLD);
	}
	
	/**
	 * <p>Constructor for PartitionedList.</p>
	 *
	 * @param coll a {@link java.util.Collection} object.
	 * @param threshold a int.
	 */
	public PartitionedList(Collection<T> coll, int threshold) {

		int numberOfObjects = coll.size();

		this.threshold = threshold;
		
		calcNumThreads( numberOfObjects );

		calcChunkSize( numberOfObjects );

		log.trace("validating " + numberOfObjects + " objects");
		log.trace("using " + this.numThreads + " threads");
		log.trace("chunk size is " + this.chunkSize);

		if (numberOfObjects > 1)
			this.partitions = Lists.partition(new ArrayList<T>(coll), chunkSize);
		else {
			this.partitions = new ArrayList<List<T>>();
			this.partitions.add(new ArrayList<T>(coll));
		}
	}

	private void calcChunkSize( int numberOfObjects ) {
		this.chunkSize = numberOfObjects / this.numThreads;
		if (this.chunkSize == 0)
			this.chunkSize = numberOfObjects;
	}

	private void calcNumThreads( int numberOfObjects ) {
		if (numberOfObjects < Runtime.getRuntime().availableProcessors() || numberOfObjects < this.threshold)
			this.numThreads = 1;
		else
			this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	/**
	 * <p>Getter for the field <code>chunkSize</code>.</p>
	 *
	 * @return the chunk size
	 */
	public int getChunkSize() {
		return chunkSize;
	}

	/**
	 * <p>Getter for the field <code>numThreads</code>.</p>
	 *
	 * @return the number of threads
	 */
	public int getNumThreads() {
		return numThreads;
	}

	/**
	 * <p>Getter for the field <code>partitions</code>.</p>
	 *
	 * @return the resulting partitions as java.util.ArrayList objects
	 */
	public List<List<T>> getPartitions() {
		return partitions;
	}

}
