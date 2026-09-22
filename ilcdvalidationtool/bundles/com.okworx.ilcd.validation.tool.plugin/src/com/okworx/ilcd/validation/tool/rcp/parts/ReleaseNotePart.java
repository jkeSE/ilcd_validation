package com.okworx.ilcd.validation.tool.rcp.parts;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class ReleaseNotePart extends AbstractPart {
	Logger logger = LogManager.getLogger(getClass());
	
	@Inject
	@Translation
	private LocalizationString localizationString;

	@PostConstruct
	public void postConstruct(Composite parent, final IProvisioningAgent agent, final Shell shell,
			final UISynchronize sync, final IWorkbench workbench)
			throws InvocationTargetException, InterruptedException {
		showReleaseNotesScreen(parent);
	}

	public void showReleaseNotesScreen(Composite parent) {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("com.okworx.ilcd.validation.tool.plugin");
		String lastVersion = prefs.get("last-version", "?");
		String currentVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();
		logger.info("Last version: " + lastVersion + "; current: " + currentVersion);
		if (!currentVersion.equals(lastVersion) || true) {

			Browser browser = new Browser(parent, SWT.NONE);
			browser.setText(readHtmlFile());
			browser.setLayout(new FillLayout());
			browser.getShell().open();

			prefs.put("last-version", currentVersion);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				logger.error("Could not store preferences.", e);
			}
		}
	}

	public String readHtmlFile() {
		URL url;
		String html = "";
		try {
			url = new URL("platform:/plugin/com.okworx.ilcd.validation.tool.plugin/resources/release-notes.html");
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				html += inputLine;
			}

			in.close();

		} catch (IOException e) {
			logger.error("Could not read 'release-notes.html'", e);
		}
		return html;
	}
}
