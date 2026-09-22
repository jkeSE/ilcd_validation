package com.okworx.ilcd.validation.tool.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BasePrefPage extends PreferencePage{
	
	/**
	 * Creates a new empty PreferencePage

	 * @param title
	 * 		page title
	 */			
	  public BasePrefPage(String title) {
	    super(title);
		
		noDefaultAndApplyButton();
	  }

	@Override
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
