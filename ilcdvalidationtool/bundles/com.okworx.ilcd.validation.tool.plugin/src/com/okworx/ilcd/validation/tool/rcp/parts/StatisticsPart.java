package com.okworx.ilcd.validation.tool.rcp.parts;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;
import com.okworx.ilcd.validation.util.Statistics;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Displays statistical information about the validation.
 * 
 * @author Oliver Kusche
 *
 */

public class StatisticsPart extends AbstractPart {

	private Label lblTotalFilesCount;

	private Label lblTotalDatasetsCount;

	private Label lblTotalProcessesCount;

	private Label lblTotalFlowsCount;

	private Label lblTotalFlowPropertiesCount;

	private Label lblTotalUnitGroupsCount;

	private Label lblTotalSourcesCount;

	private Label lblTotalContactsCount;

	private Label lblTotalLCIAMethodsCount;

	private Label lblTotalLCModelsCount;

	private Label lblTotalExternalFilesCount;

	private Label lblValidDatasetsCount;

	private Label lblValidProcessesCount;

	private Label lblValidFlowsCount;

	private Label lblValidFlowPropertiesCount;

	private Label lblValidUnitGroupsCount;

	private Label lblValidSourcesCount;

	private Label lblValidContactsCount;

	private Label lblValidLCIAMethodsCount;

	private Label lblValidLCModelsCount;

	private Label lblValidExternalFilesCount;

	private Label lblInvalidDatasetsCount;

	private Label lblInvalidProcessesCount;

	private Label lblInvalidFlowsCount;

	private Label lblInvalidFlowPropertiesCount;

	private Label lblInvalidUnitGroupsCount;

	private Label lblInvalidSourcesCount;

	private Label lblInvalidContactsCount;

	private Label lblInvalidLCIAMethodsCount;

	private Label lblInvalidLCModelsCount;

	private Label lblInvalidExternalFilesCount;

	@Inject
	@Translation
	private LocalizationString localizationString;

	@PostConstruct
	public void postConstruct() {
		GridLayout gl_parent = new GridLayout(4, false);
		gl_parent.marginRight = 5;
		parentComposite.setLayout(gl_parent);

		Label placeholder = new Label(parentComposite, SWT.NONE);
		placeholder.setText("");

		Label lblValid = new Label(parentComposite, SWT.RIGHT);
		lblValid.setText(localizationString.StatisticsPart_lblValidText);
		styleValid(lblValid);

		Label lblInvalid = new Label(parentComposite, SWT.RIGHT);
		lblInvalid.setText(localizationString.StatisticsPart_lblInvalidText);
		styleInvalid(lblInvalid);

		Label lblTotal = new Label(parentComposite, SWT.RIGHT);
		lblTotal.setText(localizationString.StatisticsPart_lblTotalText);
		styleTotals(lblTotal);

		Label lblFiles = new Label(parentComposite, SWT.NONE);
		lblFiles.setText(localizationString.StatisticsPart_lblFilesText);
		styleCaption(lblFiles);
		
		Label placeholder1 = new Label(parentComposite, SWT.NONE);
		placeholder1.setText("");

		Label placeholder2 = new Label(parentComposite, SWT.NONE);
		placeholder2.setText("");

		this.lblTotalFilesCount = new Label(parentComposite, SWT.RIGHT);

		Label lblDatasets = new Label(parentComposite, SWT.NONE);
		lblDatasets.setText(localizationString.StatisticsPart_lblDatasetsText);
		styleCaption(lblDatasets);

		this.lblValidDatasetsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidDatasetsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalDatasetsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblProcesses = new Label(parentComposite, SWT.NONE);
		lblProcesses.setText(localizationString.StatisticsPart_lblProcessesText);
		styleCaption(lblProcesses);

		this.lblValidProcessesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidProcessesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalProcessesCount = new Label(parentComposite, SWT.RIGHT);

		Label lblFlows = new Label(parentComposite, SWT.NONE);
		lblFlows.setText(localizationString.StatisticsPart_lblFlowsText);
		styleCaption(lblFlows);
		
		this.lblValidFlowsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidFlowsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalFlowsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblFlowProperties = new Label(parentComposite, SWT.NONE);
		lblFlowProperties.setText(localizationString.StatisticsPart_lblFlowPropertiesText);
		styleCaption(lblFlowProperties);
		
		this.lblValidFlowPropertiesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidFlowPropertiesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalFlowPropertiesCount = new Label(parentComposite, SWT.RIGHT);

		Label lblUnitGroups = new Label(parentComposite, SWT.NONE);
		lblUnitGroups.setText(localizationString.StatisticsPart_lblUnitGroupsText);
		styleCaption(lblUnitGroups);
		
		this.lblValidUnitGroupsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidUnitGroupsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalUnitGroupsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblSources = new Label(parentComposite, SWT.NONE);
		lblSources.setText(localizationString.StatisticsPart_lblSourcesText);
		styleCaption(lblSources);

		this.lblValidSourcesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidSourcesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalSourcesCount = new Label(parentComposite, SWT.RIGHT);

		Label lblContacts = new Label(parentComposite, SWT.NONE);
		lblContacts.setText(localizationString.StatisticsPart_lblContactsText);
		styleCaption(lblContacts);

		this.lblValidContactsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidContactsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalContactsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblLCIAMethods = new Label(parentComposite, SWT.NONE);
		lblLCIAMethods.setText(localizationString.StatisticsPart_lblLCIAMethodsText);
		styleCaption(lblLCIAMethods);

		this.lblValidLCIAMethodsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidLCIAMethodsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalLCIAMethodsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblLCModels = new Label(parentComposite, SWT.NONE);
		lblLCModels.setText(localizationString.StatisticsPart_lblLCModelsText);
		styleCaption(lblLCModels);

		this.lblValidLCModelsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidLCModelsCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalLCModelsCount = new Label(parentComposite, SWT.RIGHT);

		Label lblExtFiles = new Label(parentComposite, SWT.NONE);
		lblExtFiles.setText(localizationString.StatisticsPart_lblExternalFilesText);
		styleCaption(lblExtFiles);

		this.lblValidExternalFilesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblInvalidExternalFilesCount = new Label(parentComposite, SWT.RIGHT);
		this.lblTotalExternalFilesCount = new Label(parentComposite, SWT.RIGHT);

		styleValid(lblValidDatasetsCount);
		styleValid(lblValidProcessesCount);
		styleValid(lblValidFlowsCount);
		styleValid(lblValidFlowPropertiesCount);
		styleValid(lblValidUnitGroupsCount);
		styleValid(lblValidSourcesCount);
		styleValid(lblValidContactsCount);
		styleValid(lblValidLCIAMethodsCount);
		styleValid(lblValidLCModelsCount);
		styleValid(lblValidExternalFilesCount);

		styleTotals(lblTotalFilesCount);
		styleTotals(lblTotalDatasetsCount);
		styleTotals(lblTotalProcessesCount);
		styleTotals(lblTotalFlowsCount);
		styleTotals(lblTotalFlowPropertiesCount);
		styleTotals(lblTotalUnitGroupsCount);
		styleTotals(lblTotalSourcesCount);
		styleTotals(lblTotalContactsCount);
		styleTotals(lblTotalLCIAMethodsCount);
		styleTotals(lblTotalLCModelsCount);
		styleTotals(lblTotalExternalFilesCount);
	
		styleInvalid(lblInvalidDatasetsCount);
		styleInvalid(lblInvalidProcessesCount);
		styleInvalid(lblInvalidFlowsCount);
		styleInvalid(lblInvalidFlowPropertiesCount);
		styleInvalid(lblInvalidUnitGroupsCount);
		styleInvalid(lblInvalidSourcesCount);
		styleInvalid(lblInvalidContactsCount);
		styleInvalid(lblInvalidLCIAMethodsCount);
		styleInvalid(lblInvalidLCModelsCount);
		styleInvalid(lblInvalidExternalFilesCount);

		this.resetStatistics(null);
	}

	/**
	 * Allows other threads to update the statistics information
	 * 
	 * @param text
	 *            text String
	 */
	@Inject
	@Optional
	public void updateStatistics(@UIEventTopic(EventConstants.UPDATE_STATISTICS) Statistics statistics) {

		this.lblTotalFilesCount.setText(String.valueOf(statistics.getTotalCount()));
		this.lblTotalDatasetsCount.setText((String.valueOf(statistics.getTotalDatasetsCount())));
		this.lblTotalProcessesCount.setText(String.valueOf(statistics.getTotalProcessesCount()));
		this.lblTotalFlowsCount.setText(String.valueOf(statistics.getTotalFlowsCount()));
		this.lblTotalFlowPropertiesCount.setText(String.valueOf(statistics.getTotalFlowPropertiesCount()));
		this.lblTotalUnitGroupsCount.setText(String.valueOf(statistics.getTotalUnitGroupsCount()));
		this.lblTotalSourcesCount.setText(String.valueOf(statistics.getTotalSourcesCount()));
		this.lblTotalContactsCount.setText(String.valueOf(statistics.getTotalContactsCount()));
		this.lblTotalLCIAMethodsCount.setText(String.valueOf(statistics.getTotalLCIAMethodsCount()));
		this.lblTotalLCModelsCount.setText(String.valueOf(statistics.getTotalLCModelsCount()));
		this.lblTotalExternalFilesCount.setText(String.valueOf(statistics.getTotalExternalFilesCount()));
		this.lblValidDatasetsCount.setText(String.valueOf(statistics.getValidDatasetsCount()));
		this.lblValidProcessesCount.setText(String.valueOf(statistics.getValidProcessesCount()));
		this.lblValidFlowsCount.setText(String.valueOf(statistics.getValidFlowsCount()));
		this.lblValidFlowPropertiesCount.setText(String.valueOf(statistics.getValidFlowPropertiesCount()));
		this.lblValidUnitGroupsCount.setText(String.valueOf(statistics.getValidUnitGroupsCount()));
		this.lblValidSourcesCount.setText(String.valueOf(statistics.getValidSourcesCount()));
		this.lblValidContactsCount.setText(String.valueOf(statistics.getValidContactsCount()));
		this.lblValidLCIAMethodsCount.setText(String.valueOf(statistics.getValidLCIAMethodsCount()));
		this.lblValidLCModelsCount.setText(String.valueOf(statistics.getValidLCModelsCount()));
		this.lblValidExternalFilesCount.setText(String.valueOf(statistics.getValidExternalFilesCount()));
		this.lblInvalidDatasetsCount.setText(String.valueOf(statistics.getInvalidDatasetsCount()));
		this.lblInvalidProcessesCount.setText(String.valueOf(statistics.getInvalidProcessesCount()));
		this.lblInvalidFlowsCount.setText(String.valueOf(statistics.getInvalidFlowsCount()));
		this.lblInvalidFlowPropertiesCount.setText(String.valueOf(statistics.getInvalidFlowPropertiesCount()));
		this.lblInvalidUnitGroupsCount.setText(String.valueOf(statistics.getInvalidUnitGroupsCount()));
		this.lblInvalidSourcesCount.setText(String.valueOf(statistics.getInvalidSourcesCount()));
		this.lblInvalidContactsCount.setText(String.valueOf(statistics.getInvalidContactsCount()));
		this.lblInvalidLCIAMethodsCount.setText(String.valueOf(statistics.getInvalidLCIAMethodsCount()));
		this.lblInvalidLCModelsCount.setText(String.valueOf(statistics.getInvalidLCModelsCount()));
		this.lblInvalidExternalFilesCount.setText(String.valueOf(statistics.getInvalidExternalFilesCount()));
	}

	/**
	 * Allows other threads to reset the statistics information
	 * 
	 * @param text
	 *            text String
	 */
	@Inject
	@Optional
	public void resetStatistics(@UIEventTopic(EventConstants.RESET_STATISTICS) String dummy) {
		this.lblTotalFilesCount.setText("-");
		this.lblTotalDatasetsCount.setText("-");
		this.lblTotalProcessesCount.setText("-");
		this.lblTotalFlowsCount.setText("-");
		this.lblTotalFlowPropertiesCount.setText("-");
		this.lblTotalUnitGroupsCount.setText("-");
		this.lblTotalSourcesCount.setText("-");
		this.lblTotalContactsCount.setText("-");
		this.lblTotalLCIAMethodsCount.setText("-");
		this.lblTotalLCModelsCount.setText("-");
		this.lblTotalExternalFilesCount.setText("-");
		this.lblValidDatasetsCount.setText("-");
		this.lblValidProcessesCount.setText("-");
		this.lblValidFlowsCount.setText("-");
		this.lblValidFlowPropertiesCount.setText("-");
		this.lblValidUnitGroupsCount.setText("-");
		this.lblValidSourcesCount.setText("-");
		this.lblValidContactsCount.setText("-");
		this.lblValidLCIAMethodsCount.setText("-");
		this.lblValidLCModelsCount.setText("-");
		this.lblValidExternalFilesCount.setText("-");
		this.lblInvalidDatasetsCount.setText("-");
		this.lblInvalidProcessesCount.setText("-");
		this.lblInvalidFlowsCount.setText("-");
		this.lblInvalidFlowPropertiesCount.setText("-");
		this.lblInvalidUnitGroupsCount.setText("-");
		this.lblInvalidSourcesCount.setText("-");
		this.lblInvalidContactsCount.setText("-");
		this.lblInvalidLCIAMethodsCount.setText("-");
		this.lblInvalidLCModelsCount.setText("-");
		this.lblInvalidExternalFilesCount.setText("-");
	}

	private void styleCaption(Label lbl) {
		lbl.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT));
	}

	private void styleValid(Label lbl) {
		GridData ld = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		ld.widthHint = 80;
		lbl.setLayoutData(ld);
		lbl.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT));
	}

	private void styleInvalid(Label lbl) {
		GridData ld = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		ld.widthHint = 80;
		ld.horizontalAlignment = SWT.END;
		lbl.setLayoutData(ld);
		lbl.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
	}

	private void styleTotals(Label lbl) {
		GridData ld = new GridData(SWT.END, SWT.CENTER, true, false);
		ld.widthHint = 80;
		ld.horizontalAlignment = SWT.END;
		lbl.setLayoutData(ld);
		lbl.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
	}

}