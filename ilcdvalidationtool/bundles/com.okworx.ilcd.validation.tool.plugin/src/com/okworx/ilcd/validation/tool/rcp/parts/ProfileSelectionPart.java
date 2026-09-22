package com.okworx.ilcd.validation.tool.rcp.parts;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.preference.*;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.tool.options.ValidationOptions;
import com.okworx.ilcd.validation.tool.preferences.*;
import com.okworx.ilcd.validation.tool.resource.Icon;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;
import com.okworx.ilcd.validation.tool.util.NaturalOrderComparator;

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

public class ProfileSelectionPart extends AbstractPart {

	private Combo comboProfiles;
	private Label description;
	private Button infoBtn;

	private String selectedProfileName = "";

	private LocalResourceManager resourceManager;
	
	private NaturalOrderComparator nOComparator = new NaturalOrderComparator();

	@SuppressWarnings("unused")
	private Set<String> registeredProfiles = new HashSet<String>();

	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	private PreferenceHandler prefHandler;

	@Inject
	ValidationOptions validationOptions;
	
	@PostConstruct
	public void postConstruct() {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);

		parentComposite.setLayout(new GridLayout(2, false));

		comboProfiles = createCombo(parentComposite, PreferenceConstants.PAGEID_GENERAL);

		infoBtn = new Button(parentComposite, SWT.NONE);
		infoBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		infoBtn.setImage(Icon.INFO.getImage(resourceManager));
		infoBtn.setToolTipText("Show Profile Updates");
		infoBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				partService.showPart("com.okworx.ilcd.validation.tool.part.profilechangelogview", PartState.ACTIVATE);
			}
		});

		description = new Label(parentComposite, SWT.WRAP);
		description.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1));
		FontDescriptor italicDescriptor = FontDescriptor.createFrom(description.getFont()).setStyle(SWT.ITALIC);
		Font italicFont = resourceManager.createFont(italicDescriptor);
		description.setFont(italicFont);


		updateCombos();
		updateValidationOptions();
	}

	/**
	 * Creates combo box
	 * 
	 * @param parent the parent Composite
	 * @param pageID the page ID from which the preference values are retrieved
	 */
	private Combo createCombo(Composite parent, String pageID) {
		Combo combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
//		gridData.widthHint = 1000;
		combo.setLayoutData(gridData);

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (combo.getSelectionIndex() == (combo.getItemCount() - 1)) {

					openPrefDialog(pageID);
				} else {
					if (combo.getSelectionIndex() == 0) {
						selectedProfileName = "";
					} else {
						selectedProfileName = combo.getItem(combo.getSelectionIndex());
					}
				}

				updateValidationOptions();
			}
		});

		combo.setEnabled(true);

		return combo;
	}

	/**
	 * Updates validationOptions and update profile description
	 */
	@SuppressWarnings("unchecked")
	private void updateValidationOptions() {
		ProfileManager profileManager = ProfileManager.getInstance();
		Profile profile = null;

		int selectedIndex = comboProfiles.getSelectionIndex();
		if (selectedIndex > 0 && selectedIndex < comboProfiles.getItemCount() - 1) {
			Object comboData = comboProfiles.getData();
			if (comboData instanceof List<?>) {
				List<ProfileDto> prefPathValues = (List<ProfileDto>) comboData;
				String selectedJarPath = prefPathValues.get(selectedIndex - 1).getPath();
				
				if(selectedJarPath == null || selectedJarPath == "") {
					return;
				}

				try {
					File file = new File(selectedJarPath);
					profile = profileManager.registerProfile(file.toURI().toURL());
				} catch (MalformedURLException | InvalidProfileException e) {
					e.printStackTrace();
					profile = profileManager.getDefaultProfile();
				}
			}
		}

		if (profile == null) {
			profile = profileManager.getDefaultProfile();
		}

		validationOptions.setProfile(profile);

		if (profile.getDescription() == null) {
			description.setText(localizationString.ProfileSelectionPart_noDescriptionText);
		} else {
			description.setText(profile.getDescription());
		}
		// this is needed to resize the label (otherwise text that is longer than before will be cut off):
		parentComposite.layout();
	}

	/**
	 * fills the content of all Combos with data retrieved from the
	 * PreferenceManager
	 */
	private void updateCombos() {
		updateComboContent(comboProfiles, PreferenceConstants.PAGEID_GENERAL);
	}

	/**
	 * fills the content of the given Combo with data retrieved from the
	 * PreferenceManager using the given PreferenceHandler
	 * 
	 * @param combo  the Combo widget to fill
	 * @param pageID the page ID from which the preference values are retrieved
	 */
	private void updateComboContent(Combo combo, String pageID) {

		ProfileManager profileManager = ProfileManager.getInstance();
		Profile defaultProfile = profileManager.getDefaultProfile();

		combo.removeAll();
		combo.add(localizationString.AspectsSelectionPart_defaultILCDText + " - v" + defaultProfile.getVersion());
		combo.select(0);

		if (prefHandler != null) {
			List<ProfileDto> profiles = prefHandler.getProfiles();

			// because the above values from prefHandler may contain the default profile
			// (which we added already, and will be skipped in the for loop!),
			// we need to build a new prefPathValues array which is in sync with the combo
			// items:
			List<ProfileDto> profilesComboSynced = new ArrayList<ProfileDto>();

			for (int i = 0; i < profiles.size(); i++) {
				ProfileDto profile = profiles.get(i);
				
				boolean profileIsOutdated = false;
				if(prefHandler.hideOutdated()) {
					for (int k = 0; k < profiles.size(); k++) {
						if (i != k && profiles.get(k).getName().equals(profiles.get(i).getName())) {
							if (nOComparator.compare(profiles.get(i).getVersion(), profiles.get(k).getVersion()) <= 0) {
								profileIsOutdated = true;
							}
						}
					}					
				}

				if (nOComparator.compare(profile.getName(), defaultProfile.getName()) != 0) {
					String version = profile.getVersion();
					if(version == "") {
						version = "?";
					}
					
					if (!profileIsOutdated) {
						combo.add(profile.getName() + (" - v" + version));
						profilesComboSynced.add(profile);
						
						if (nOComparator.compare(selectedProfileName, profile.getName()) == 0) {
							combo.select(i + 1);
						}						
					}
				}
			}
			if (combo.getSelectionIndex() == 0) {
				selectedProfileName = "";
			}

			combo.setData(profilesComboSynced);
		}

		combo.add(localizationString.AspectsSelectionPart_addProfileText);
	}

	/**
	 * Opens a PreferenceDialog
	 * 
	 * @param selectedID page ID which should be selected
	 * @return 0 if dialog was closed via Ok button, otherwise 1
	 */
	public int openPrefDialog(String selectedID) {
		PreferenceNode pageGeneral = new PreferenceNode(PreferenceConstants.PAGEID_GENERAL,
				((IPreferencePage) new PrefPageGeneral(localizationString, prefHandler)));

		PreferenceManager prefManager = new PreferenceManager();
		prefManager.addToRoot(pageGeneral);
		
		PreferenceDialog prefDialog = new PreferenceDialog(null, prefManager);
		prefDialog.setPreferenceStore(PreferenceHandler.getPreferenceStore());

		prefDialog.setSelectedNode(selectedID);

		int ret = prefDialog.open();
		updateCombos();

		return ret;
	}

	@PreDestroy
	public void preDestroy() {
		resourceManager.dispose();
	}
	

	@Inject
	@Optional
	public void handleHideOutdatedChanged(@UIEventTopic(EventConstants.HIDE_OUTDATED_PROFILES) boolean hideOutdated) {
		updateCombos();
	}

}