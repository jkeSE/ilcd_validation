/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class AboutHandler {
	@Inject
	private EModelService modelService;
	
	@Execute
	public void execute(MApplication app) {
		
		MTrimmedWindow window = (MTrimmedWindow) modelService.find("com.okworx.ilcd.validation.tool.trimmedwindow.about", app);
		
		MTrimmedWindow clonedWindow = (MTrimmedWindow)modelService.cloneElement(window, app);
		app.getChildren().add(clonedWindow);
		
		clonedWindow.setToBeRendered(true);
	}
}
