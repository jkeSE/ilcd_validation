package com.okworx.ilcd.validation.tool.logging;

import java.util.ArrayList;

public class BufferedLog {

	private static BufferedLog INSTANCE;

	private static int MAX_LINES = 2000;
	private ArrayList<String> log = new ArrayList<String>();

	private BufferedLog() {
			
	}

	public static BufferedLog getInstance() {
		if (INSTANCE == null) {
			BufferedLog.INSTANCE = new BufferedLog();
		}
		return BufferedLog.INSTANCE;
	}

	public void appendToBuffer(String message) {
		log.add(message);

		while (log.size() > MAX_LINES) {
			log.remove(0);
		}
	}
	
	public String getMessages() {
		StringBuffer sb = new StringBuffer();
		for(String line : log) {
			sb.append(line);
		}
		
		return sb.toString();
	}
}
