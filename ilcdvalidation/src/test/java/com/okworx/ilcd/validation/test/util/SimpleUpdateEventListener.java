package com.okworx.ilcd.validation.test.util;

import org.apache.logging.log4j.Logger;

import com.okworx.ilcd.validation.util.IUpdateEventListener;

public class SimpleUpdateEventListener implements IUpdateEventListener {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	public void updateProgress(double percentFinished) {
		log.trace(" " + percentFinished);
	}

	public void updateStatus(String statusMessage) {
		log.info(statusMessage);
	}

}
