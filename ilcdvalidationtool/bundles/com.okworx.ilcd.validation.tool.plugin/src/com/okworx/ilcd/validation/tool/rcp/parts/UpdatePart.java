package com.okworx.ilcd.validation.tool.rcp.parts;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.P2Util;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class UpdatePart extends AbstractPart {
	
	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	EModelService modelService;
	@Inject
	MApplication app;
	@Inject
	P2Util p2Util;

	@PostConstruct
	public void postConstruct(final IProvisioningAgent agent, final Shell shell, final UISynchronize sync,
			final IWorkbench workbench) throws InvocationTargetException, InterruptedException {
		
		p2Util.update(workbench, shell, false, true);

		showReleaseNotesScreen();

		logger.debug("Platform version: " + Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion());
	}

	public void showReleaseNotesScreen() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("com.okworx.ilcd.validation.tool.plugin");
		String lastVersion = prefs.get("last-version", "?");
		String currentVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();
		logger.debug("Last version: " + lastVersion + "; current: " + currentVersion);
		if (!currentVersion.equals(lastVersion)) {
			partService.showPart("com.okworx.ilcd.validation.tool.part.releasenotes", PartState.ACTIVATE);

			prefs.put("last-version", currentVersion);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				logger.error("Could not store preferences.", e);
			}
		}
	}
}