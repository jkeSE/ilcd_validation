package com.okworx.ilcd.validation.tool.util;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import com.okworx.ilcd.validation.util.Locator;

public class EclipseLocator implements Locator {

	@Override
	public URL resolve(URL url) throws IOException {
		return FileLocator.resolve(url);
	}

}
