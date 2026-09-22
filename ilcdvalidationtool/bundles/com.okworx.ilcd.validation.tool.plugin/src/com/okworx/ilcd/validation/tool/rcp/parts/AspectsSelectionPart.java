
package com.okworx.ilcd.validation.tool.rcp.parts;

import java.util.*;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.okworx.ilcd.validation.tool.options.ValidationOptions;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.ValidatorFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

/**
 * Allows to select the required validation aspects. The default scheme provides
 * pre-configured aspects.
 * 
 * @author Oliver Armbruster
 *
 */

public class AspectsSelectionPart extends AbstractPart {
	private Button buttonArchiveValidator;
	private Button buttonCategoryValidator;
	private Button buttonLinkValidator;
	private Button buttonOrphansValidator;
	private Button buttonReferenceFlowValidator;
	private Button buttonSchemaValidator;
	private Button buttonXSLTStylesheetValidator;

	private ArrayList<String> profileAspectConfig;
	private ArrayList<String> profileSupportedAspects;

	private List<Button> buttons = new ArrayList<Button>();
	private LocalResourceManager resourceManager;

	private Button unselectAllBtn;
	private Button selectAllBtn;
	private Button defaultsBtn;

	@SuppressWarnings("unused")
	private Set<String> registeredProfiles = new HashSet<String>();

	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	ValidationOptions validationOptions;

    @PostConstruct
	public void postConstruct() {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);

		GridLayout layout = new GridLayout(3, false);
		parentComposite.setLayout(layout);

		createSelectionHelperButtons(parentComposite);

		buttonArchiveValidator = createButton(parentComposite, localizationString.SchemaSelectionPart_buttonArchiveValidatorText,
				localizationString.SchemaSelectionPart_buttonArchiveValidatorTooltipText, false, 1);

		buttonCategoryValidator = createButton(parentComposite,
				localizationString.SchemaSelectionPart_buttonCategoryValidatorText,
				localizationString.SchemaSelectionPart_buttonCategoryValidatorTooltipText, false, 3);

		buttonLinkValidator = createButton(parentComposite, localizationString.SchemaSelectionPart_buttonLinkValidatorText,
				localizationString.SchemaSelectionPart_buttonLinkValidatorTooltipText, false, 1);

		buttonOrphansValidator = createButton(parentComposite, localizationString.SchemaSelectionPart_buttonOrphansValidatorText,
				localizationString.SchemaSelectionPart_buttonOrphansValidatorTooltipText, false, 3);

		buttonReferenceFlowValidator = createButton(parentComposite,
				localizationString.SchemaSelectionPart_buttonReferenceFlowValidatorText,
				localizationString.SchemaSelectionPart_buttonReferenceFlowValidatorTooltipText, false, 3);

		buttonSchemaValidator = createButton(parentComposite, localizationString.SchemaSelectionPart_buttonSchemaValidatorText,
				localizationString.SchemaSelectionPart_buttonSchemaValidatorTooltipText, false, 3);

		buttonXSLTStylesheetValidator = createButton(parentComposite,
				localizationString.SchemaSelectionPart_buttonXSLTStylesheetValidatorText,
				localizationString.SchemaSelectionPart_buttonXSLTStylesheetValidatorTooltipText, false, 3);

		buttonSchemaValidator.setSelection(true);
		buttonXSLTStylesheetValidator.setSelection(true);

		getOptionsMPart().setVisible(true);

		handleProfileUpdate();
		
		Runnable update = () -> {handleProfileUpdate();};
		validationOptions.addObserver(update, this);
	}

	/**
	 * Creates check button with the given text and set the appropriate
	 * SelectionListener
	 * 
	 * @param parent        the parent Composite
	 * @param text          the button text
	 * @param defaultButton if this button represents the default settings
	 */
	private Button createButton(Composite parent, String text, String tooltipText, boolean defaultButton,
			int horizontalSpan) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText("  " + text);
		button.setToolTipText(tooltipText);
		button.setData(false);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, horizontalSpan, 1));

		button.setSelection(false);
		// button.setEnabled(false);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button thisButton = (Button) e.widget;
				thisButton.setData(thisButton.getSelection());
			}
		});

		buttons.add(button);

		return button;
	}

	/**
	 * Creates defaults, unselect all and select all buttons
	 * 
	 * @param parent the parent Composite
	 */
	private void createSelectionHelperButtons(Composite parent) {
		defaultsBtn = new Button(parent, SWT.BUTTON1);
		defaultsBtn.setText(localizationString.AspectsSelectionPart_defaultsButton);
		defaultsBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		defaultsBtn.setEnabled(false);
		defaultsBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAspectsFromProfileConfig(profileAspectConfig);
				setSupportedAspectsFromProfileConfig(profileSupportedAspects);
			}
		});

		selectAllBtn = new Button(parent, SWT.BUTTON1);
		selectAllBtn.setText(localizationString.AspectsSelectionPart_selectAllButton);
		selectAllBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		selectAllBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for (Button button : buttons) {
					button.setSelection(true);
					button.setData(true);
				}
			}
		});

		unselectAllBtn = new Button(parent, SWT.BUTTON1);
		unselectAllBtn.setText(localizationString.AspectsSelectionPart_unselectAllButton);
		unselectAllBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		unselectAllBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for (Button button : buttons) {
					button.setSelection(false);
					button.setData(false);
				}
			}
		});
	}

	/**
	 * Updates aspects when profile selection has changed
	 */
	private void handleProfileUpdate() {
		String activeAspectsString = validationOptions.getProfile().getActiveAspects();
		String supportedAspectsString = validationOptions.getProfile().getSupportedAspects();
		logger.debug("Selected profile aspects: {}", activeAspectsString);
		logger.debug("Selected supported profile aspects: {}", supportedAspectsString);
		if (activeAspectsString == null) {
			defaultsBtn.setEnabled(false);
		} else {
			profileAspectConfig = new ArrayList<String>(Arrays.asList(activeAspectsString.split(",")));
			defaultsBtn.setEnabled(true);
			setAspectsFromProfileConfig(profileAspectConfig);			
		}
		if (supportedAspectsString == null) {
			profileSupportedAspects = null;
		} else {
			profileSupportedAspects = new ArrayList<String>(Arrays.asList(supportedAspectsString.split(",")));			
		}
		setSupportedAspectsFromProfileConfig(profileSupportedAspects);
		

	}

	/**
	 * Updates aspects selection with given array of aspect strings
	 * 
	 * @param activeAspects Array of aspects to enable
	 */
	private void setAspectsFromProfileConfig(ArrayList<String> activeAspects) {
		if (activeAspects == null) {
			return;
		}
		buttonArchiveValidator.setSelection(activeAspects.contains("Archive Structure"));		
		buttonLinkValidator.setSelection(activeAspects.contains("Links"));		
		buttonOrphansValidator.setSelection(activeAspects.contains("Orphaned Items"));		
		buttonReferenceFlowValidator.setSelection(activeAspects.contains("Reference Flows"));		
		buttonSchemaValidator.setSelection(activeAspects.contains("XML Schema Validity"));		
		buttonCategoryValidator.setSelection(activeAspects.contains("Categories"));		
		buttonXSLTStylesheetValidator.setSelection(activeAspects.contains("Custom Validity") || activeAspects.contains("Profile Specific Rules"));
	}
	
	private void setSupportedAspectsFromProfileConfig(ArrayList<String> supportedAspects) {

		buttonArchiveValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Archive Structure"));
		buttonLinkValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Links"));
		buttonOrphansValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Orphaned Items"));
		buttonReferenceFlowValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Reference Flows"));
		buttonSchemaValidator.setEnabled(supportedAspects == null || supportedAspects.contains("XML Schema Validity"));
		buttonCategoryValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Categories"));
		buttonXSLTStylesheetValidator.setEnabled(supportedAspects == null || supportedAspects.contains("Custom Validity") || supportedAspects.contains("Profile Specific Rules"));
	}

	/**
	 * Create a new ValidatorFactory according to the currently checked Buttons
	 */
	public ValidatorFactory getFactory() {
		ValidatorFactory factory = new ValidatorFactory(AspectsSelectionPart.this.validationOptions);

		if (buttonArchiveValidator.getSelection()) {
			factory.addArchiveValidator = true;
		}
		if (buttonCategoryValidator.getSelection()) {
			factory.addCategoryValidator = true;
		}
		if (buttonLinkValidator.getSelection()) {
			factory.addLinkValidator = true;
		}
		if (buttonOrphansValidator.getSelection()) {
			factory.addOrphansValidator = true;
		}
		if (buttonReferenceFlowValidator.getSelection()) {
			factory.addReferenceFlowValidator = true;
		}
		if (buttonSchemaValidator.getSelection()) {
			factory.addSchemaValidator = true;
		}
		if (buttonXSLTStylesheetValidator.getSelection()) {
			factory.addXSLTStylesheetValidator = true;
		}

		return factory;
	}

	@PreDestroy
	public void preDestroy() {
		resourceManager.dispose();
	}

	private MPart getOptionsMPart() {
		return partService.findPart("com.okworx.ilcd.validation.tool.part.validationoptionspartname");
	}


}