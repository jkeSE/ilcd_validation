
package com.okworx.ilcd.validation.tool.rcp.parts;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.okworx.ilcd.validation.tool.preferences.PreferenceHandler;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class ProfileSelectionOptionsPart extends AbstractPart {
	private Button showPreviousProfileVersions;

	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	private IEventBroker eventBroker;
	
	@Inject
	private PreferenceHandler prefHandler;

	@PostConstruct
	public void postConstruct() {

		GridLayout layout = new GridLayout(2, false);
		layout.marginTop = 10;
		parentComposite.setLayout(layout);

		showPreviousProfileVersions = createCheckBox(parentComposite, localizationString.PrefPageCategory_ShowPreviousProfileVersionsText, localizationString.PrefPageCategory_ShowPreviousProfileVersionsText, 2);
		showPreviousProfileVersions.setData(!prefHandler.hideOutdated());
		showPreviousProfileVersions.setSelection(!prefHandler.hideOutdated());
		showPreviousProfileVersions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				prefHandler.retrieveFromPrefStore();
				prefHandler.hideOutdated(!showPreviousProfileVersions.getSelection());
				prefHandler.backupToPrefStore();
				eventBroker.send(EventConstants.HIDE_OUTDATED_PROFILES, showPreviousProfileVersions.getSelection());
			}
		});
	}

	private Button createCheckBox(Composite parent, String text, String tooltipText, int horizontalSpan) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText("  " + text);
		button.setData(false);
		button.setToolTipText(tooltipText);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, false, horizontalSpan, 1);
		gd.horizontalIndent = 8;
		button.setLayoutData(gd);
		return button;
	}
}