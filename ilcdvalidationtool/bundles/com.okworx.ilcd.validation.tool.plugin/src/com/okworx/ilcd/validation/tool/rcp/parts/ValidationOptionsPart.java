 
package com.okworx.ilcd.validation.tool.rcp.parts;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.LinkValidator;
import com.okworx.ilcd.validation.OrphansValidator;
import com.okworx.ilcd.validation.tool.options.ValidationOptions;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Allows to set options for different validation aspects.
 *
 */

public class ValidationOptionsPart extends AbstractPart {
	
	private LocalResourceManager resourceManager;

	// General
	private Button btnBatchMode;
	private Button btnSummary;
	private Button btnElementaryAnalysis;
	private Button btnElementaryAnalysisIncludeSums;
	private Button btnIgnoreReferenceObjects;
	
	// Links
	private Button btnLinksIgnoreCP;
	private Button btnLinksIgnoreIP;
	private Button btnLinksIgnoreLCIAM;
	private Button btnLinksIgnorePrecedingDSVersion;
	private Button btnLinksIgnoreRefsWithRemoteLinks;

	// Orphans
	private Button btnOrphansIgnoreLCIAM;

	@Inject
	private ValidationOptions validationOptions;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	@Translation
	private LocalizationString localizationString;
	
	@PostConstruct
	public void postConstruct() {				
		GridLayout layout = new GridLayout(2, false);
		parentComposite.setLayout(layout);
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);

		Label generalLabel = new Label(parentComposite, SWT.NONE);
		generalLabel.setText(localizationString.OptionsPart_lblGeneral);
		generalLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false, 1, 1));

		FontDescriptor boldDescriptor = 
				FontDescriptor.createFrom(generalLabel.getFont()).setStyle(SWT.BOLD);
		Font boldFont = resourceManager.createFont(boldDescriptor);
		generalLabel.setFont(boldFont);
		
		final int horizontalSpan = 1;
		final int indent = 2;

		btnBatchMode = createCheckBox(parentComposite, localizationString.OptionsPart_btnGeneralBatchModeText, localizationString.OptionsPart_btnGeneralBatchModeTooltipText, horizontalSpan, indent, false);
		initCheckBox(btnBatchMode, validationOptions.getGeneralOptions(), ValidationOptions.OPTION_BATCHMODE, localizationString.OptionsPart_btnGeneralBatchModeStatusBarEnable);

		btnSummary = createCheckBox(parentComposite, localizationString.OptionsPart_btnGeneralSummaryText, localizationString.OptionsPart_btnGeneralSummaryTooltipText, horizontalSpan, indent + 3, true);
		initCheckBox(btnSummary, btnBatchMode, btnBatchMode, validationOptions.getGeneralOptions(), ValidationOptions.OPTION_DS_SUMMARY);

		btnElementaryAnalysis = createCheckBox(parentComposite, localizationString.OptionsPart_btnGeneralFlowAnalysisText, localizationString.OptionsPart_btnGeneralFlowAnalysisTooltipText, horizontalSpan, indent + 3, true);
		initCheckBox(btnElementaryAnalysis, btnBatchMode, btnBatchMode, validationOptions.getGeneralOptions(), ValidationOptions.OPTION_FLOWS_ANALYSIS);

		btnElementaryAnalysisIncludeSums = createCheckBox(parentComposite, localizationString.OptionsPart_btnGeneralFlowAnalysisIncludeSumsText, localizationString.OptionsPart_btnGeneralFlowAnalysisIncludeSumsTooltipText, horizontalSpan, 5 + indent, true);
		initCheckBox(btnElementaryAnalysisIncludeSums, List.of(btnElementaryAnalysis), List.of(btnBatchMode, btnElementaryAnalysis), validationOptions.getGeneralOptions(), ValidationOptions.OPTION_FLOWS_ANALYSIS_INCLUDE_SUMS);

		btnIgnoreReferenceObjects = createCheckBox(parentComposite, localizationString.OptionsPart_btnGeneralIgnoreReferenceObjectsText, localizationString.OptionsPart_btnGeneralIgnoreReferenceObjectsTooltipText, horizontalSpan, indent, true);
		initCheckBox(btnIgnoreReferenceObjects, validationOptions.getGeneralOptions(), ValidationOptions.OPTION_IGNORE_REFERENCE_OBJECTS);
		
		Label spacerLabel = new Label(parentComposite, SWT.NONE);
		spacerLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1));
		
		Label linksLabel = new Label(parentComposite, SWT.NONE);
		linksLabel.setText(localizationString.OptionsPart_lblLinks);
		linksLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false, 1, 1));
		linksLabel.setFont(boldFont);

		// create options
		btnLinksIgnoreCP = createCheckBox(parentComposite, localizationString.OptionsPart_btnLinksIgnoreComplementingProcessText, localizationString.OptionsPart_btnLinksIgnoreComplementingProcessTooltipText, horizontalSpan, indent, false);
		initCheckBox(btnLinksIgnoreCP, validationOptions.getLinkOptions(), LinkValidator.PARAM_IGNORE_COMPLEMENTINGPROCESS);

		btnLinksIgnoreIP = createCheckBox(parentComposite, localizationString.OptionsPart_btnLinksIgnoreIncludedProcessesText, localizationString.OptionsPart_btnLinksIgnoreIncludedProcessesTooltipText, horizontalSpan, indent, true);
		initCheckBox(btnLinksIgnoreIP, validationOptions.getLinkOptions(), LinkValidator.PARAM_IGNORE_INCLUDEDPROCESSES);

		btnLinksIgnoreLCIAM = createCheckBox(parentComposite, localizationString.OptionsPart_btnLinksIgnoreReferenceToLCIAMethodsText, localizationString.OptionsPart_btnLinksIgnoreReferenceToLCIAMethodsTooltipText, horizontalSpan, indent, true);
		initCheckBox(btnLinksIgnoreLCIAM, validationOptions.getLinkOptions(), LinkValidator.PARAM_IGNORE_REFS_TO_LCIAMETHODS);

		btnLinksIgnorePrecedingDSVersion = createCheckBox(parentComposite, localizationString.OptionsPart_btnLinksIgnoreReferenceToPrecedingDSVersionText, localizationString.OptionsPart_btnLinksIgnoreReferenceToPrecedingDSVersionTooltipText, horizontalSpan, indent, true);
		initCheckBox(btnLinksIgnorePrecedingDSVersion, validationOptions.getLinkOptions(), LinkValidator.PARAM_IGNORE_PRECEDINGDATASETVERSION);

		btnLinksIgnoreRefsWithRemoteLinks = createCheckBox(parentComposite, localizationString.OptionsPart_btnLinksIgnoreRefsWithRemoteLinksText, localizationString.OptionsPart_btnLinksIgnoreRefsWithRemoteLinksTooltipText, horizontalSpan, indent, true);
		initCheckBox(btnLinksIgnoreRefsWithRemoteLinks, validationOptions.getLinkOptions(), LinkValidator.PARAM_IGNORE_REFS_WITH_REMOTE_LINKS);

		Label spacerLabel2 = new Label(parentComposite, SWT.NONE);
		spacerLabel2.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1));
		
		Label orphansLabel = new Label(parentComposite, SWT.NONE);
		orphansLabel.setText(localizationString.OptionsPart_lblOrphans);
		orphansLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false, 1, 1));
		orphansLabel.setFont(boldFont);

		btnOrphansIgnoreLCIAM =  createCheckBox(parentComposite, localizationString.OptionsPart_btnOrphansIgnoreLCIAMethodsText, localizationString.OptionsPart_btnOrphansIgnoreLCIAMethodsTooltipText, horizontalSpan, indent, false);
		initCheckBox(btnOrphansIgnoreLCIAM, validationOptions.getOrphansOptions(), OrphansValidator.PARAM_IGNORE_LCIAMETHODS);
	}

	private Button createCheckBox(Composite parent, String text, String tooltipText, int horizontalSpan, boolean spacer) {
		return createCheckBox(parent, text, tooltipText, horizontalSpan, 0, spacer);
	}

	private Button createCheckBox(Composite parent, String text, String tooltipText, int horizontalSpan, int indent, boolean spacer) {
		if (spacer)
			new Label(parent, SWT.NONE);
		Button button = new Button(parent, SWT.CHECK);
		button.setText("  " + text);
		button.setToolTipText(tooltipText);
		button.setData(false);
		GridData gd = new GridData(SWT.BEGINNING, SWT.TOP, false, false, horizontalSpan, 1);
		gd.horizontalIndent = 8;
		if (indent > 0)
			gd.horizontalIndent = indent * 8;
		button.setLayoutData(gd);
		return button;
	}

	private void initCheckBox(Button button, Map<String, Object> options, String param) {
		initCheckBox(button, null, null, options, param, null);
	}

	private void initCheckBox(Button button, Map<String, Object> options, String param, String statusMessage) {
		initCheckBox(button, null, null, options, param, null);
	}

	private void initCheckBox(Button button, final Button enableWith, final Button disableWith, Map<String, Object> options, String param) {
		initCheckBox(button, List.of(enableWith), List.of(disableWith), options, param, null);
	}

	private void initCheckBox(Button button, final List<Button> enableWith, final List<Button> disableWith, Map<String, Object> options, String param) {
		initCheckBox(button, enableWith, disableWith, options, param, null);
	}

	private void initCheckBox(Button button, final List<Button> enableWith, final List<Button> disableWith, Map<String, Object> options, String param, String statusMessage) {
		button.setSelection((boolean) options.get(param));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button thisButton = (Button)e.widget;
				thisButton.setData(thisButton.getSelection());
			    options.put(param, thisButton.getSelection());
				if (statusMessage != null) {
					if (thisButton.getSelection())
						eventBroker.send(EventConstants.SET_STATUS_TEXT, statusMessage);
					else
						eventBroker.send(EventConstants.SET_STATUS_TEXT, "");
				}
			}
		});
		if (enableWith != null && !enableWith.isEmpty()) {
			for (Button dependsOnButton : enableWith) {
				button.setEnabled(dependsOnButton.getSelection());
				dependsOnButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button thisButton = (Button)e.widget;
							if (thisButton.getSelection())
								button.setEnabled(true);
					}
				});
			}
		}
		if (disableWith != null && !disableWith.isEmpty()) {
			for (Button dependsOnButton : disableWith) {
				button.setEnabled(dependsOnButton.getSelection());
				dependsOnButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Button thisButton = (Button)e.widget;
							if (!thisButton.getSelection()) {
								button.setSelection(false);
								button.setEnabled(false);
							    options.put(param, button.getSelection());
							}
					}
				});
			}
		}
	}
}