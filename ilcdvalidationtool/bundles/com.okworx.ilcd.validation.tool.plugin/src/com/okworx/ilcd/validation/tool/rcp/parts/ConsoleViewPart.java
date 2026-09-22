package com.okworx.ilcd.validation.tool.rcp.parts;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.okworx.ilcd.validation.tool.logging.BufferedLog;

import jakarta.annotation.PostConstruct;

public class ConsoleViewPart extends AbstractPart{
	private Text text;

	@PostConstruct
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);

		text.append(BufferedLog.getInstance().getMessages());
	}

	@Focus
	public void setFocus() {
		text.setFocus();
	}
}
