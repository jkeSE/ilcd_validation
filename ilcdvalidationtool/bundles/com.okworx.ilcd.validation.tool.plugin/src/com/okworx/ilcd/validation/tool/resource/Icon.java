package com.okworx.ilcd.validation.tool.resource;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import com.okworx.ilcd.validation.tool.rcp.Activator;

public enum Icon {
	SUCCESS("dialog-clean.png"), //$NON-NLS-1$
	FAILURE("dialog-cancel-2-sm.png"), //$NON-NLS-1$
	ERROR("dialog-no-3.png"), //$NON-NLS-1$
	WARNING("dialog-warning-4.png"), //$NON-NLS-1$
	INFO("dialog-information-4.png"), //$NON-NLS-1$
	ADD_FILE("list-add-3.png"), //$NON-NLS-1$
	REMOVE_FILE("list-remove-3.png"), //$NON-NLS-1$
	CLEAR_LIST("edit-clear-list.png"), //$NON-NLS-1$
	START("arrow-right-3.png"), //$NON-NLS-1$
	STOP("dialog-cancel-2.png"), //$NON-NLS-1$
	DROP("dropzone.png"), //$NON-NLS-1$
	ILCD("ILCDValidator.png"); //$NON-NLS-1$
	
	private ImageDescriptor imageDescriptor;
	
	private Icon(String path) {
		this.imageDescriptor = Activator.getImageDescriptor(path);
	}
	
	public Image getImage(LocalResourceManager resourceManager) {
		return resourceManager.createImageWithDefault(imageDescriptor);
	}
	
	public Image getImage(LocalResourceManager resourceManager, int width, int height) {
		ImageData imageData = imageDescriptor.getImageData().scaledTo(width, height);
		ImageDescriptor scaledDescriptor = ImageDescriptor.createFromImageData(imageData);
		return resourceManager.createImageWithDefault(scaledDescriptor);
	}
	
}