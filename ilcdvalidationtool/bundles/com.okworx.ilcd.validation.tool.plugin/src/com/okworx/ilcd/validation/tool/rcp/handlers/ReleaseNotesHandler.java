package com.okworx.ilcd.validation.tool.rcp.handlers;

import jakarta.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;

public class ReleaseNotesHandler {
	
	@Inject
	@Translation
	private LocalizationString localizationString;
	
	@Inject
	EPartService partService;

	@Execute
	public void execute() {
		partService.showPart("com.okworx.ilcd.validation.tool.part.releasenotes", PartState.ACTIVATE);
	}
}
