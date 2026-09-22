/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.okworx.ilcd.validation.tool.rcp.handlers;

import jakarta.inject.Inject;

import org.eclipse.e4.core.di.annotations.*;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.okworx.ilcd.validation.tool.rcp.parts.FileSelectionPart;
import com.okworx.ilcd.validation.tool.util.EventConstants;
public class StopHandler {
	
	@Inject
	EPartService partService;
	
	boolean menuEntryActivated = false;

	@Execute
	public void execute(Shell shell){
		MPart mPart = partService.findPart("com.okworx.ilcd.validation.tool.parts.fileselection");
		if (mPart == null) {
			return;
		}
		
		FileSelectionPart fileSelectionPart = (FileSelectionPart)mPart.getObject();
		if (fileSelectionPart == null) {
			return;
		}
		
		fileSelectionPart.pressButtonStop();
	}
	
	@CanExecute
	public boolean canExecute() {
		return menuEntryActivated;
	}
	
	/**
	 * Allows other threads to activated/ deactivate the menu entry
	 * 
	 * @param activate
	 * 		if the menu entry should be activated or deactivated
	 */
	@Inject @Optional
	public void menuEntryActivation(@UIEventTopic(EventConstants.STOP_MENU_ACTIVATION) boolean activate) {
		menuEntryActivated = activate;
	}
}
