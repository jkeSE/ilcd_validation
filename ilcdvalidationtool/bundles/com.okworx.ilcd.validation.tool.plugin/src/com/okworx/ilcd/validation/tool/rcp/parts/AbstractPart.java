package com.okworx.ilcd.validation.tool.rcp.parts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import jakarta.inject.Inject;

public class AbstractPart {

	protected Logger logger = LogManager.getLogger(getClass());

	@Inject
	protected EPartService partService;

	@Inject
	@Optional
	protected Composite parentComposite;

	public AbstractPart() {
		super();
	}

	@Focus
	public void setFocus() {
		// This is called when part becomes active in stack
		final Composite composite = parentComposite; // Capture the reference to prevent race condition
		if (composite != null && !composite.isDisposed()) {
			// Force layout and redraw
			composite.getDisplay().asyncExec(() -> {
				if (!composite.isDisposed()) {
					composite.layout(true, true);
					composite.redraw();
					composite.update();
				}
			});
		}
	}
}