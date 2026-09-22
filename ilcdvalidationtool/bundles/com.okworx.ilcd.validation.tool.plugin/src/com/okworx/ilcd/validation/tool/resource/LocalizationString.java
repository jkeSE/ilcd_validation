package com.okworx.ilcd.validation.tool.resource;

import org.eclipse.core.runtime.Platform;

import jakarta.annotation.PostConstruct;

/**
 * Provides localization functionality. The localization Strings have to be
 * saved in com.okworx.ilcd.validation.tool.localization.messages*.properties
 * 
 * @author Oliver Armbruster
 *
 */
public class LocalizationString {

	@PostConstruct
	public void postConstruct() {
		String appVersion = Platform.getBundle("com.okworx.ilcd.validation.tool.plugin").getVersion().toString();
		WindowName = WindowName.replaceAll("@app-version@", appVersion);
		AppVersion = AppVersion.replaceAll("@app-version@", appVersion);
	}

	public String BundleName;
	public String ProductName;
	public String WindowName;
	public String AppVersion;
	public String AboutWindowName;
	public String FileSelectionPartName;
	public String ValidationMessagesPartName;
	public String AspectsSelectionPartName;
	public String ValidationOptionsPartName;
	public String ProfileSelectionPartName;
	public String ProfileSelectionOptionsPartName;
	public String ProfileChangelogPartName;
	public String StatisticsPartName;
	public String AboutCopyrightPartName;
	public String AboutLibrariesPartName;
	public String ConsoleViewPartName;

	public String FileMenuName;
	public String ValidationMenuName;
	public String HelpMenuName;
	public String AddMenuItemName;
	public String RemoveMenuItemName;
	public String ClearMenuItemName;
	public String QuitMenuItemName;
	public String StartMenuItemName;
	public String StopMenuItemName;
	public String AboutMenuItemName;
	public String UpdateMenuItemName;
	public String ConsoleViewMenuItemName;

	public String FileSelectionPart_AddButtonName;
	public String FileSelectionPart_ClearButtonName;
	public String FileSelectionPart_FileDialogText;
	public String FileSelectionPart_RemoveButtonName;
	public String FileSelectionPart_StartButtonName;
	public String FileSelectionPart_StopButtonName;
	public String FileSelectionPart_TableViewerText;
	public String FileSelectionPart_FilenameColumnName;
	public String FileSelectionPart_PathColumnName;
	public String FileSelectionPart_ProgressColumnName;
	public String FileSelectionPart_ChkPositiveResultsText;
	public String FileSelectionPart_ChkPositiveResultsTooltip;

	public String ValidationPart_DatasetTypeColumnName;
	public String ValidationPart_EventTypeColumnName;
	public String ValidationPart_FilenameColumnName;
	public String ValidationPart_DatasetNameColumnName;
	public String ValidationPart_MessageColumnName;
	public String ValidationPart_UUIDColumnName;
	public String ValidationPart_ValidatingFileText;
	public String ValidationPart_ValidationCorrectText;
	public String ValidationPart_ValidationErrorsText1;
	public String ValidationPart_ValidationErrorsText2;
	public String ValidationPart_ValidationErrorsText3;
	public String ValidationPart_ValidationErrorsText4;
	public String ValidationPart_ValidationErrorsText5;
	public String ValidationPart_AspectNameColumnName;
	public String ValidationPart_ShowInFileSystem;
	public String ValidationPart_CopyItem;
	public String ValidationPart_CopyAll;
	public String ValidationPart_ConfirmManyEventsText;
	public String ValidationPart_ConfirmManyEventsMessage;

	public String SchemaSelectionPart_labelProfileText;
	public String SchemaSelectionPart_buttonDefaultValidatorText;
	public String SchemaSelectionPart_buttonArchiveValidatorText;
	public String SchemaSelectionPart_buttonCategoryValidatorText;
	public String SchemaSelectionPart_buttonFilenameValidatorText;
	public String SchemaSelectionPart_buttonLinkValidatorText;
	public String SchemaSelectionPart_buttonOrphansValidatorText;
	public String SchemaSelectionPart_buttonReferenceFlowValidatorText;
	public String SchemaSelectionPart_buttonSchemaValidatorText;
	public String SchemaSelectionPart_buttonXSLTStylesheetValidatorText;
	public String SchemaSelectionPart_buttonDefaultValidatorTooltipText;

	public String SchemaSelectionPart_buttonArchiveValidatorTooltipText;
	public String SchemaSelectionPart_buttonCategoryValidatorTooltipText;
	public String SchemaSelectionPart_buttonFilenameValidatorTooltipText;
	public String SchemaSelectionPart_buttonLinkValidatorTooltipText;
	public String SchemaSelectionPart_buttonOrphansValidatorTooltipText;
	public String SchemaSelectionPart_buttonReferenceFlowValidatorTooltipText;
	public String SchemaSelectionPart_buttonSchemaValidatorTooltipText;
	public String SchemaSelectionPart_buttonXSLTStylesheetValidatorTooltipText;

	public String OptionsPart_lblGeneral;
	public String OptionsPart_btnGeneralBatchModeText;
	public String OptionsPart_btnGeneralBatchModeTooltipText;
	public String OptionsPart_btnGeneralBatchModeStatusBarEnable;
	public String OptionsPart_btnGeneralBatchModeStatusBarFinish;
	public String OptionsPart_btnGeneralSummaryText;
	public String OptionsPart_btnGeneralSummaryTooltipText;
	public String OptionsPart_btnGeneralFlowAnalysisText;
	public String OptionsPart_btnGeneralFlowAnalysisTooltipText;
	public String OptionsPart_btnGeneralFlowAnalysisIncludeSumsText;
	public String OptionsPart_btnGeneralFlowAnalysisIncludeSumsTooltipText;
	public String OptionsPart_btnGeneralIgnoreReferenceObjectsText;
	public String OptionsPart_btnGeneralIgnoreReferenceObjectsTooltipText;
	public String OptionsPart_lblLinks;
	public String OptionsPart_btnLinksIgnoreComplementingProcessText;
	public String OptionsPart_btnLinksIgnoreComplementingProcessTooltipText;
	public String OptionsPart_btnLinksIgnoreIncludedProcessesText;
	public String OptionsPart_btnLinksIgnoreIncludedProcessesTooltipText;
	public String OptionsPart_btnLinksIgnoreReferenceToLCIAMethodsText;
	public String OptionsPart_btnLinksIgnoreReferenceToLCIAMethodsTooltipText;
	public String OptionsPart_btnLinksIgnoreReferenceToPrecedingDSVersionText;
	public String OptionsPart_btnLinksIgnoreReferenceToPrecedingDSVersionTooltipText;
	public String OptionsPart_btnLinksIgnoreRefsWithRemoteLinksText;
	public String OptionsPart_btnLinksIgnoreRefsWithRemoteLinksTooltipText;

	public String OptionsPart_lblOrphans;
	public String OptionsPart_btnOrphansIgnoreLCIAMethodsText;
	public String OptionsPart_btnOrphansIgnoreLCIAMethodsTooltipText;

	public String AspectsSelectionPart_defaultILCDText;
	public String AspectsSelectionPart_addProfileText;
	public String AspectsSelectionPart_defaultsButton;
	public String AspectsSelectionPart_selectAllButton;
	public String AspectsSelectionPart_unselectAllButton;

	public String ProfileSelectionPart_noDescriptionText;
	public String ProfileSelectionPart_DescriptionLabel;

	public String ProfileChangelogPart_SemnaticUpdatesTitle;
	public String ProfileChangelogPart_TechnicalUpdatesTitle;
	public String ProfileChangelogPart_NoUpdateText;

	public String StatisticsPart_lblTotalText;
	public String StatisticsPart_lblValidText;
	public String StatisticsPart_lblInvalidText;
	public String StatisticsPart_lblDatasetsText;
	public String StatisticsPart_lblFilesText;
	public String StatisticsPart_lblProcessesText;
	public String StatisticsPart_lblFlowsText;
	public String StatisticsPart_lblFlowPropertiesText;
	public String StatisticsPart_lblUnitGroupsText;
	public String StatisticsPart_lblContactsText;
	public String StatisticsPart_lblSourcesText;
	public String StatisticsPart_lblLCIAMethodsText;
	public String StatisticsPart_lblLCModelsText;
	public String StatisticsPart_lblExternalFilesText;

	public String PrefPageCategory_buttonAddText;
	public String PrefPageCategory_buttonEditText;
	public String PrefPageCategory_buttonRemoveText;
	public String PrefPageCategory_buttonClearText;
	public String PrefPageCategory_buttonRemoveOutdated;
	public String PrefPageCategory_NameColumnName;
	public String PrefPageCategory_PathColumnName;
	public String PrefPageCategory_VersionColumnName;
	public String PrefPageCategory_labelNameText;
	public String PrefPageCategory_labelPathText;
	public String PrefPageCategory_labelVersionText;
	public String PrefPageCategory_FileDialogText;
	public String PrefPageCategory_DialogLabelRemoveOutdated;
	public String PrefPageCategory_DialogMessageRemoveOutdated;
	public String PrefPageCategory_ShowPreviousProfileVersionsText;
	public String PrefPageCategory_DialogLabelClearProfiles;
	public String PrefPageCategory_DialogMessageClearProfiles;

	public String AboutCopyrightPart_LicenseText;
	public String AboutCopyrightPart_LicenseText2;
	public String AboutCopyrightPart_FundedByText;
	public String AboutCopyrightPart_AcknowlegdementText;
	
	public String AboutLibrariesPart_NameColumnName;
	public String AboutLibrariesPart_VersionColumnName;
	public String AboutLibrariesPart_LicenseColumnName;
	public String AboutLibrariesPart_LicenseURLColumnName;

	public String P2Util_NoUpdatesMessage;
	public String P2Util_NoUpdatesMessageTitle;
	public String P2Util_UpdatesFoundQuestion;
	public String P2Util_UpdatesFoundQuestionTitle;
	public String P2Util_UpdatesInstalledRestartQuestion;
	public String P2Util_UpdatesInstalledRestartQuestionTitle;

}
