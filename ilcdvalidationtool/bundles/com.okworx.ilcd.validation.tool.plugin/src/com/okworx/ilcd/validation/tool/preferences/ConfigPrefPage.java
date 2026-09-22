package com.okworx.ilcd.validation.tool.preferences;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.exception.InvalidProfileException;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.NaturalOrderComparator;

public class ConfigPrefPage extends BasePrefPage{
	private final Logger log = LogManager.getLogger(this.getClass());
	
	private TableViewer tableViewer;
	private Table table;
	
	private TableColumnLayout tableColumnLayout;

	private LocalizationString localizationString;
	
	private Text nameText;
	private Text pathText;
	private Text versionText;
	private String newPath;
	
	private Button buttonAdd;
	private Button buttonReplace;
	private Button buttonRemove;
	private Button buttonClear;
	private Button buttonRemoveOutdated;
	
	private PreferenceHandler prefHandler;
	
	private String[] fileFilters;
	
	/**
	 * Creates a new PreferencePage used for configuration

	 * @param title
	 * 		page title
	 * @param preferenceID
	 * 		the ID to retrieve values from the PreferenceStore
	 * @param locString
	 * 		object referring to the LocalizationString
	 * @param filters
	 * 		String array of file filters that can be selected
	 */		
	public ConfigPrefPage(String title, String preferenceID, LocalizationString locString, PreferenceHandler prefHandler, String[] filters) {
		super(title);
		setDirty(false);
		this.prefHandler = prefHandler;
		localizationString = locString;
		fileFilters = filters;
	}

	@Override
	public boolean performOk() {
		prefHandler.backupToPrefStore();
		return true;
	}
	
	@Override
	public boolean performCancel() {
		return true;
	}
	  
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
	    composite.setLayout(new GridLayout(1, false));
	    
	    
	    Composite textComposite = new Composite(composite, SWT.NONE);
	    textComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    textComposite.setLayout(new GridLayout(3, false));
	    
	    Label nameLabel = new Label(textComposite, SWT.NONE);
	    nameLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	    nameLabel.setText(localizationString.PrefPageCategory_labelNameText);
	    
	    nameText = new Text(textComposite, SWT.BORDER);
	    nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				enableButtons();
			}
		});

	    Label pathLabel = new Label(textComposite, SWT.NONE);
	    pathLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	    pathLabel.setText(localizationString.PrefPageCategory_labelPathText);
	    
	    pathText = new Text(textComposite, SWT.BORDER);
	    pathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    pathText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				enableButtons();
			}
		});
	    
	    Button buttonOpenfile = new Button(textComposite, SWT.FLAT);
	    buttonOpenfile.setText("...");
	    buttonOpenfile.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	    buttonOpenfile.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		FileDialog dialog = new FileDialog(parent.getShell(), SWT.NONE);
				dialog.setText(localizationString.FileSelectionPart_FileDialogText);
				dialog.setFilterExtensions(fileFilters);
				//dialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
				
				dialog.open();
				String file = dialog.getFileName();
				String filterPath = dialog.getFilterPath();
				
				if (file != "") {
					String absolutePath = filterPath + File.separator + file;
					absolutePath.replace("file:/C", "file:C");
					log.debug("Absolute Path of selected profile (as String): "+ absolutePath);
					
					pathText.setText(absolutePath); //$NON-NLS-1$
					
					ProfileManager profileManager = ProfileManager.getInstance();
					Profile profile = null;
					
					try {
						File jarFile = new File(absolutePath);
						log.debug("Absolute Path of selected profile (as File): " + jarFile.toString());
						profile = profileManager.registerProfile(jarFile.toURI().toURL());
					} catch (MalformedURLException | InvalidProfileException e) {
						e.printStackTrace();
						profile = profileManager.getDefaultProfile();
					}
					
					versionText.setText(profile.getVersion());
					nameText.setText(profile.getName());
					newPath = profile.getPath().getAbsolutePath();
				}
	    	}
		});
	    
	    Label versionLabel = new Label(textComposite, SWT.NONE);
	    versionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
	    versionLabel.setText(localizationString.PrefPageCategory_labelVersionText);
	    
	    versionText = new Text(textComposite, SWT.BORDER);
	    versionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    versionText.setEditable(false);
	    
	    Composite buttonComposite = new Composite(composite, SWT.NONE);
	    buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    buttonComposite.setLayout(new GridLayout(5, false));
	    
	    buttonAdd = new Button(buttonComposite, SWT.FLAT);
	    buttonAdd.setText(localizationString.PrefPageCategory_buttonAddText);
	    buttonAdd.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	    buttonAdd.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		if (nameText.getText() != "" && newPath != "" && newPath != null) {
	    			prefHandler.addProfile(new ProfileDto(nameText.getText(), versionText.getText(), newPath));
	    			setDirty(true);
	    			updateTableViewer();
	    		}
	    	}
		});
	    buttonAdd.setEnabled(false);
	    
	    buttonReplace = new Button(buttonComposite, SWT.FLAT);
	    buttonReplace.setText(localizationString.PrefPageCategory_buttonEditText);
	    buttonReplace.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	    buttonReplace.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {	    		
	    		if (nameText.getText() != "" && pathText.getText() != "" && table.getSelectionIndex() >= 0) {
	    			prefHandler.removeProfile(table.getSelectionIndex());
	    			prefHandler.addProfile(new ProfileDto(nameText.getText(), versionText.getText(), newPath));
	    			setDirty(true);
	    			updateTableViewer();
	    		}
	    	}
		});
	    buttonReplace.setEnabled(false);
	    
	    buttonRemove = new Button(buttonComposite, SWT.FLAT);
	    buttonRemove.setText(localizationString.PrefPageCategory_buttonRemoveText);
	    buttonRemove.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	    buttonRemove.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {	    		
	    		if (table.getSelectionIndex() >= 0) {
		    		prefHandler.removeProfile(table.getSelectionIndex());
		    		setDirty(true);
		    		updateTableViewer();
	    		}
	    	}
		});
	    buttonRemove.setEnabled(false);
 
	    buttonClear = new Button(buttonComposite, SWT.FLAT);
	    buttonClear.setText(localizationString.PrefPageCategory_buttonClearText);
	    buttonClear.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	    buttonClear.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
	    		dialog.setText(localizationString.PrefPageCategory_DialogLabelClearProfiles);
	    		dialog.setMessage(localizationString.PrefPageCategory_DialogMessageClearProfiles);
	    		
	    		if(dialog.open() == SWT.CANCEL) {
	    			return;
	    		}
	    		
	    		prefHandler.clearProfiles();
	    		setDirty(true);
	    		updateTableViewer();
	    	}
		});
	    
	    buttonRemoveOutdated = new Button(buttonComposite, SWT.FLAT);
	    buttonRemoveOutdated.setText(localizationString.PrefPageCategory_buttonRemoveOutdated);
	    buttonRemoveOutdated.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
	    buttonRemoveOutdated.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent event) {
	    		MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
	    		dialog.setText(localizationString.PrefPageCategory_DialogLabelRemoveOutdated);
	    		dialog.setMessage(localizationString.PrefPageCategory_DialogMessageRemoveOutdated);
	    		
	    		if(dialog.open() == SWT.CANCEL) {
	    			return;
	    		}
	    		
	    		
	    		
	    		List<ProfileDto> profiles = prefHandler.getProfiles();	    		
	    		for (int i = 0; i < profiles.size(); i++) {
	    			ProfileDto profile = profiles.get(i);
	    			
	    			int outdatedIndex = -1;
	    			
	    			NaturalOrderComparator nOComparator = new NaturalOrderComparator();
	    			
	    			for(int k = 0; k < profiles.size(); k++) {
	    				if(i != k && profiles.get(k).getName().equals(profile.getName())) {
	    					if (nOComparator.compare(profile.getVersion(), profiles.get(k).getVersion()) > 0) {
	    						outdatedIndex = k;
	    					} else {
	    						outdatedIndex = i;
	    					}
	    				}
	    			}
	    			
	    			if(outdatedIndex != -1) {
			    		setDirty(true);
	    				prefHandler.removeProfile(outdatedIndex);
	    			}
	    		}
	    		
	    		updateTableViewer();	    		
	    	}
		});
	    

	    
	    Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		
	    tableViewer = new TableViewer(tableComposite, SWT.MULTI | SWT.FULL_SELECTION);
	    tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (table.getSelectionIndex() >= 0) {
					TableItem item = table.getItem(table.getSelectionIndex());
					
					nameText.setText(item.getText(0));
					pathText.setText(item.getText(1));
					versionText.setText(item.getText(2));
				}
				
				enableButtons();
			}
		});
	    
	    table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
	    
	    createViewerColumns();
	    updateTableViewer();

	    return composite;
	}

	/**
	 * creates the columns of the ViewerTable with appropriate LabelProviders
	 */	
	private void createViewerColumns() {
		createViewerColumn(localizationString.PrefPageCategory_NameColumnName, 1, true);
		createViewerColumn(localizationString.PrefPageCategory_PathColumnName, 3, true);
		createViewerColumn(localizationString.PrefPageCategory_VersionColumnName, 1, true);
	}

	/**
	 * creates a single TableColumn with the given title and the give width/ weight
	 * 
	 * @param text
	 * 		column title
	 * @param width
	 * 		column width/ weight
	 * @param weightUsed
	 * 		if the parameter 'width' indicates the width or the weight of the column
	 */	
	private TableViewerColumn createViewerColumn(String text, int width, boolean weightUsed) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(text);
		column.setResizable(true);
		column.setMoveable(true);
		
		if (weightUsed) {
			tableColumnLayout.setColumnData(column, new ColumnWeightData(width, 50, true));
		} else {
			tableColumnLayout.setColumnData(column, new ColumnPixelData(width));
		}
		return viewerColumn;
	}
	
	/**
	 * enables all currently valid buttons
	 */		
	private void enableButtons() {
		buttonAdd.setEnabled(true);
		buttonReplace.setEnabled(false);
		buttonRemove.setEnabled(false);

		if (table.getSelectionIndex() >= 0) {
			buttonRemove.setEnabled(true);
			buttonReplace.setEnabled(true);
		}
		
//		if (nameText.getText() != "" && pathText.getText() != "") {
//			if (!prefHandler.getNameValues().contains(nameText)) {
//				buttonAdd.setEnabled(true);
//				
//				String text = nameText.getText();
//				for (String name : prefHandler.getNameValues()) {
//					if (name.compareTo(text) == 0) {
//						buttonAdd.setEnabled(false);
//					}
//				}
//			}
//			
//			if (table.getSelectionIndex() >= 0) {
//				buttonReplace.setEnabled(true);
//			}
//		}
	}
	
	/**
	 * fills the content of the TableViewer with data retrieved from the PreferenceManager
	 */		
	private void updateTableViewer() {
		table.removeAll();
		
//		List<String> nameValues = prefHandler.getNameValues();
//		List<String> pathValues = prefHandler.getPathValues();
//		List<String> versionValues = prefHandler.getVersionValues();
		List<ProfileDto> profiles = prefHandler.getProfiles();
		
		for (int i = 0; i < profiles.size(); i++) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(new String[] {profiles.get(i).getName(), profiles.get(i).getPath(), profiles.get(i).getVersion()});
		}
		
		enableButtons();
	}
	
	private void setDirty(boolean isDirty) {
		setValid(isDirty);
	}
}

