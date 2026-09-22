package com.okworx.ilcd.validation.util;

import java.util.Collection;

import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.AbstractDatasetsValidator;
import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * <p>AbstractDatasetsTask class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class AbstractDatasetsTask {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());
	
	protected AbstractDatasetsValidator validator;
	
	protected Collection<IDatasetReference> files;
	
	protected Statistics statistics = new Statistics();

	/**
	 * <p>updateChunkCount.</p>
	 *
	 * @param count a int.
	 * @return a int.
	 */
	protected int updateChunkCount(int count) {
		count++;
		if ((count % this.validator.getUpdateInterval() == 0)) {
			this.validator.triggerProgressUpdate(count);
			count = 0;
		}
		return count;
	}

}
