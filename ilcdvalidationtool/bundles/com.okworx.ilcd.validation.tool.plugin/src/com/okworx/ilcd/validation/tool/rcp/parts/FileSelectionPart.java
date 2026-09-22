 
package com.okworx.ilcd.validation.tool.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.tool.resource.Icon;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;
import com.okworx.ilcd.validation.tool.util.ValidatorFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

/**
 * Handles selection of files to be validated.
 * Files can be selected via dialog or by drag-and-dropping them
 * 
 * @author Oliver Armbruster
 *
 */

public class FileSelectionPart extends AbstractPart {
	
	private Display display;
	
	private TableViewer tableViewer;
	private Table table;
	private TableColumnLayout tableColumnLayout;
	
	private Button buttonAdd;
	private Button buttonRemove;
	private Button buttonClear;
	private Button buttonStart;
	private Button buttonStop;
	
	private Button chkPositiveResults;

	private LocalResourceManager resourceManager;
	private Image oldImage = null;
	private Image dropIcon;
	private Image successIcon;
	private Image failureIcon;
		
	private Map<String, Image> iconMap = new HashMap<String, Image>();
	private List<ProgressBar> tableBars = new ArrayList<ProgressBar>();
	private List<TableEditor> barEditors = new ArrayList<TableEditor>();
	
	private ProgressBar currentBar = null;
	private double lastProgress = 0.0;
	
	private int currentRow = -1;
	private boolean validationRunning = false;
	
	@Inject
	@Translation
	private LocalizationString localizationString;
	@Inject
	private IEventBroker eventBroker;
	
	@PostConstruct
	public void postConstruct() {		
		display = Display.getCurrent();
	
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);
		dropIcon = Icon.DROP.getImage(resourceManager);
		successIcon = Icon.SUCCESS.getImage(resourceManager);
		failureIcon = Icon.FAILURE.getImage(resourceManager);
		
		Composite tableComposite = new Composite(parentComposite, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));
		
		GridLayout layout = new GridLayout(8, false);
		parentComposite.setLayout(layout);
		
		tableViewer = new TableViewer(tableComposite, SWT.MULTI | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void dispose() {
			}
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			@Override
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
		});

		int transferOps = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT;
		Transfer[] transferTypes = new Transfer[] {FileTransfer.getInstance()};
		tableViewer.addDropSupport(transferOps, transferTypes, new FileDropListener(tableViewer));
		
		buttonAdd = new Button(parentComposite, SWT.FLAT);
		buttonAdd.setToolTipText(localizationString.FileSelectionPart_AddButtonName);
		buttonAdd.setSize(32, 32);
		buttonAdd.setImage(Icon.ADD_FILE.getImage(resourceManager));
		buttonAdd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		buttonAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parentComposite.getShell(), SWT.MULTI);
				dialog.setText(localizationString.FileSelectionPart_FileDialogText);
				
				dialog.open();
				String[] files = dialog.getFileNames();
				String filterPath = dialog.getFilterPath();
				
				for(String file : files) {
					String path = filterPath + "/" + file; //$NON-NLS-1$
					addFile(path);
				};
			}
		});
		
		buttonRemove = new Button(parentComposite, SWT.FLAT);
		buttonRemove.setToolTipText(localizationString.FileSelectionPart_RemoveButtonName);
		buttonRemove.setSize(32, 32);
		buttonRemove.setImage(Icon.REMOVE_FILE.getImage(resourceManager));
		buttonRemove.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		buttonRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeFiles(table.getSelectionIndices());
			}
		});
		
		buttonClear = new Button(parentComposite, SWT.FLAT);
		buttonClear.setToolTipText(localizationString.FileSelectionPart_ClearButtonName);
		buttonClear.setSize(32, 32);
		buttonClear.setImage(Icon.CLEAR_LIST.getImage(resourceManager));
		buttonClear.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
		buttonClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearTableViewer();
				eventBroker.send(EventConstants.RESET_STATISTICS, "");
				eventBroker.send(EventConstants.TABLE_CLEAR, ""); 
			}
		});
		
		chkPositiveResults = new Button(parentComposite,SWT.CHECK);
		chkPositiveResults.setText(localizationString.FileSelectionPart_ChkPositiveResultsText); 
		chkPositiveResults.setToolTipText(localizationString.FileSelectionPart_ChkPositiveResultsTooltip);
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1);
		gd.widthHint = 80;
		chkPositiveResults.setLayoutData(gd);
		chkPositiveResults.setImage(Icon.INFO.getImage(resourceManager));
		chkPositiveResults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button chk = (Button) e.getSource();
				eventBroker.send(EventConstants.SET_REPORTSUCCESSES, chk.getSelection());
			}
		});

		buttonStart = new Button(parentComposite, SWT.FLAT);
		buttonStart.setToolTipText(localizationString.FileSelectionPart_StartButtonName);
		buttonStart.setSize(32, 32);
		buttonStart.setImage(Icon.START.getImage(resourceManager));
		buttonStart.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		buttonStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startValidation();
			}
		});
		
		buttonStop = new Button(parentComposite, SWT.FLAT);
		buttonStop.setToolTipText(localizationString.FileSelectionPart_StopButtonName);
		buttonStop.setSize(32, 32);
		buttonStop.setImage(Icon.STOP.getImage(resourceManager));
		buttonStop.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 1, 1));
		buttonStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				eventBroker.send(EventConstants.STOP_VALIDATION, "");
			}
		});
		
		createViewerColumns();
		createContextMenu();
		
		checkButtonActivation();
		
		parentComposite.addListener (SWT.Resize, new Listener () {
			@Override
			public void handleEvent (Event event) {
				drawBackground();
			}
		});
	}
	
	/**
	 * creates the columns of the ViewerTable with appropriate LabelProviders
	 */	
	private void createViewerColumns() {
		TableViewerColumn column = createViewerColumn("", 25, false); //$NON-NLS-1$
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ""; //$NON-NLS-1$
			}
			
			@Override
			public Image getImage(Object element) {
				if (!(element instanceof String)) {
					return null;
				}
				String str = (String) element;
				
				return iconMap.get(str); //$NON-NLS-1$
			}
		});
		
		column = createViewerColumn(localizationString.FileSelectionPart_FilenameColumnName, 1, true);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof String)) {
					return ""; //$NON-NLS-1$
				}
				String str = (String) element;
				
				int index = str.lastIndexOf('\\');
				int index2 = str.lastIndexOf('/');
				index = Math.max(index, index2);

				return str.substring(index + 1);
			}
		});
		
		column = createViewerColumn(localizationString.FileSelectionPart_PathColumnName, 3, true);
		column.setLabelProvider(new ColumnLabelProvider());
		
		column = createViewerColumn(localizationString.FileSelectionPart_ProgressColumnName, 1, true);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return "";
			}
		});
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
	 * creates the context menu of the TableViewer
	 * 
	 */		
	private void createContextMenu() {
		Menu contextMenu = new Menu(table);
		MenuItem menuItem = new MenuItem(contextMenu, SWT.PUSH);
		menuItem.setText("show in file system");
		
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				TableItem[] tableItems = table.getSelection();
				if (tableItems.length < 1) {
					return;
				}
				
				Object itemData = tableItems[0].getData();
				if (!(itemData instanceof String)) {
					return;
				}
				
				String filename = (String) itemData;
				
				openInBrowser(filename);
			}
		});
		
		table.setMenu(contextMenu);
	}
	
	/**
	 * opens the given path in the default file browser of the OS and selects the respective file
	 * 
	 * @param path
	 * 		the path to be opened in the file browser
	 */		
	public void openInBrowser(String path) {
		try  {
			File file = new File(path);
			String canonicalPath = file.getCanonicalPath();
			String canoncialURI = file.getCanonicalFile().toURI().toString();
			
			String command = "";
			if (Util.isGtk()) {
				command = "dbus-send --print-reply --dest=org.freedesktop.FileManager1 /org/freedesktop/FileManager1 org.freedesktop.FileManager1.ShowItems array:string:\""+canoncialURI+"\" string:\"\""; //$NON-NLS-1$
			} else if (Util.isWindows()) {
				command = "explorer /E,/select=" + canonicalPath; //$NON-NLS-1$
			} else if (Util.isMac()) {
				command = "open -R \""+canonicalPath+"\""; //$NON-NLS-1$
			}
			
			if (Util.isLinux() || Util.isMac()) {
				Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command }, null, null);
			} else {
				Runtime.getRuntime().exec(command, null, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Custom draws the background of the tableViewer
	 */		
	private void drawBackground() {
		
		Rectangle rect = table.getClientArea();
		if (rect.width <= 0 || rect.height <= 0) {
			return;
		}
		
		ImageData imgData = dropIcon.getImageData();
		Image newImage;
		
		if ((rect.width - imgData.width < 0) || (rect.height - imgData.height < 0)) {
			imgData = imgData.scaledTo(rect.width, rect.height);
			newImage = new Image (display, imgData);
		} else
			newImage = new Image (display, rect.width, rect.height + 40);
		
		GC gc = new GC (newImage);
		
		double width = rect.x+rect.width*0.5-imgData.width*0.5;
		double height = rect.y+rect.height*0.5-imgData.height*0.5;
		
		if (width < 0 || height < 0) {
			width = 0;
			height = 0;
		}
		
		Point drawPoint = convertSize(width, height); 
		
		gc.drawImage(dropIcon, drawPoint.x, drawPoint.y);
		
		table.setBackgroundImage(newImage);
		
		if (oldImage != null) oldImage.dispose();
		oldImage = newImage;
		table.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
            	oldImage.dispose();
            }});
		gc.dispose();
	}
	
	/**
	 * Converts two double values (usually indication width and height) to a Point class
	 * 
	 * @param width
	 * 		first double value
	 * @param height
	 * 		second double value
	 */		
	private Point convertSize(double width, double height) {
		return new Point((int)width, (int)height);
	}
	
	/**
	 * starts the validation process for every filename added to the tableViewer
	 */	
	private void startValidation() {
		if (table.getItemCount() <= 0) {
			return;
		}

		resetProgressBars();
		iconMap.clear();
		tableViewer.update(getTableElements(), null);
		
		validationRunning = true;
		checkButtonActivation();
			
		ValidatorFactory validatorFactory = getAspectsSelectionPart().getFactory();
		
		currentRow = -1;
		validateNextFile();
		
		ValidationPart validationPart = getValidationPart();
		if (validationPart != null) {
			validationPart.startValidation(getTableElements(), validatorFactory);
		}
	}
	
	/**
	 * Signals that the progress bar has to be updated with the given progress
	 * if a value of -1.0 is given, the progressBar is reseted
	 * 
	 * @param progress
	 * 		percentage value representing the advanced progress (between 0 and 1)
	 */	
	@Inject @Optional
	public void updateProgressBar(@UIEventTopic(EventConstants.UPDATE_PROGRESS) double progress) {
		progress = progress < 0.0 ? 0.0 : progress;
		progress = progress > 100.0 ? 100.0 : progress;
		if (lastProgress >= progress) {
			return;
		}
		
		currentBar.setSelection((int)progress);
		lastProgress = progress;
	}
	
	/**
	 * Signals that the running validation of the current file is finished and reports success or failure
	 * 
	 * @param success
	 * 		if the validation process finished successfully
	 */
	@Inject @Optional
	public void fileValidated(@UIEventTopic(EventConstants.FILE_VALIDATED) boolean success) {
		if (currentBar == null || currentRow < 0) {
			return;
		}

		lastProgress = 100.0;
		currentBar.setSelection(100);
		
		String filename = (String) getTableElement(currentRow);
		if (success) {
			iconMap.put(filename, successIcon);
		} else {
			iconMap.put(filename, failureIcon);
		}
		tableViewer.update(filename, null);
		
		validateNextFile();
	}
	
	/**
	 * Validates the next filename in the TableViewer if one exists
	 */
	private void validateNextFile() {
		currentRow++;
		if (currentRow < 0 || currentRow >= table.getItemCount() || currentRow >= tableBars.size()) {
			currentRow = -1;
			currentBar = null;
			return;
		}
		
		currentBar = tableBars.get(currentRow);
		lastProgress = 0.0;
	}
	
	/**
	 * Signals that the validation process is finished so that this part's controls can appropriately be 
	 * re-enabled
	 */	
	@Inject @Optional
	public void validationFinished(@UIEventTopic(EventConstants.STOP_VALIDATION) String dummy) {
		validationRunning = false;
		checkButtonActivation();
	}

	/**
	 * Convenience method which return the currently active instance of the ValidationPart
	 * or null if no appropriate instance could be retrieved
	 */	
	private ValidationPart getValidationPart() {
		MPart mPart = partService.findPart("com.okworx.ilcd.validation.tool.parts.validation"); //$NON-NLS-1$
		if (mPart == null) {
			return null;
		}
		
		return (ValidationPart)mPart.getObject();
	}
	
	/**
	 * Convenience method which return the currently active instance of the AspectsSelectionPart
	 * or null if no appropriate instance could be retrieved
	 */	
	private AspectsSelectionPart getAspectsSelectionPart() {
		MPart mPart = partService.findPart("com.okworx.ilcd.validation.tool.part.aspectsselectionpartname"); //$NON-NLS-1$
		if (mPart == null) {
			return null;
		}
		
		return (AspectsSelectionPart)mPart.getObject();
	}
	
	/**
	 * Adds the file to the list of selected files and creates a matching ProgressBar
	 * The file is only added if no duplicate entry was found
	 * 
	 * @param filename
	 * 		the file to add
	 */		
	public void addFile(String filename) {
		boolean duplicateFound = false;
		for (int i = 0; i < table.getItemCount(); i++) {
			if (filename.equals(getTableElement(i))) {
				duplicateFound = true;
				break;
			}
		}
		if (!duplicateFound) {
			tableViewer.add(filename);
			
			TableEditor tableEditor = new TableEditor(table);
			tableEditor.grabHorizontal = true;
			tableEditor.grabVertical = true;
			
			TableItem tableItem = table.getItem(table.getItemCount() - 1);
			ProgressBar progressBar = new ProgressBar(table, SWT.NONE);
			tableEditor.setEditor(progressBar, tableItem, 3);
			
			tableBars.add(progressBar);
			barEditors.add(tableEditor);
			
			resetProgressBars();
			checkButtonActivation();
		}
	}
	
	/**
	 * Removes all files from the tableViewer at the specified indeces.
	 * The matching ProgressBars and Icons are also disposed.
	 * 
	 * @param indices
	 * 		the row numbers in the TableViewer
	 */		
	public void removeFiles(int[] indices) {
		for (int i = indices.length - 1; i >= 0; i--) {
			int index = indices[i];
			if (index < tableBars.size()) {
				tableBars.get(index).dispose();
				tableBars.remove(index);
				barEditors.get(index).dispose();
				barEditors.remove(index);
			}
			
			String filename = getTableElement(index);
			iconMap.remove(filename);
		}
		
		// reset ProgessBars and Icons to correct row
		for (int i = 0; i < barEditors.size(); i++) {
			TableEditor editor = barEditors.get(i);
			ProgressBar bar = tableBars.get(i);
			editor.setEditor(bar, table.getItem(i), 3);
		}
		
		table.remove(indices);
		
		resetProgressBars();
		checkButtonActivation();
	}
	
	/**
	 * Convenience method which allows other parts to programmatically press the "Add.." Button
	 */	
	public void pressButtonAdd() {
		buttonAdd.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Convenience method which allows other parts to programmatically press the "Remove" Button
	 */	
	public void pressButtonRemove() {
		buttonRemove.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Convenience method which allows other parts to programmatically press the "Clear" Button
	 */	
	public void pressButtonClear() {
		buttonClear.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Convenience method which allows other parts to programmatically press the "Start" Button
	 */	
	public void pressButtonStart() {
		buttonStart.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Convenience method which allows other parts to programmatically press the "Stop" Button
	 */	
	public void pressButtonStop() {
		buttonStop.notifyListeners(SWT.Selection, null);
	}
	
	/**
	 * Convenience method to clear the content of the table viewer
	 */	
	public void clearTableViewer() {
		int[] indices = new int[table.getItemCount()];
		for (int i = 0; i < table.getItemCount(); i++) {
			indices[i] = i;
		}
		
		removeFiles(indices);
	}
	
	/**
	 * Returns the table entry (i.e. a selected filename) at the given index in the part's TableViewer
	 *
	 * @param index
	 * 		the index to retrieve the table entry
	 * @return the found table entry or null if there is no table entry at this index exists
	 */
	public String getTableElement(int index) {
		if (table.getItemCount() <= index) {
			return null;
		}
		return (String)tableViewer.getElementAt(index);
	}
	
	/**
	 * Returns the entire table entry of the part's TableViewer as a String array
	 * Note: if the table contains no elements, an empty array is returned
	 *
	 * @return the table entry as a String array
	 */
	public String[] getTableElements() {
		int count = table.getItemCount();
		String[] elements = new String[count];
		for (int i = 0; i < count; i++) {
			elements[i] = getTableElement(i);
		}
		return elements;
	}
	
	@Focus
	public void setFocus() {
		table.setFocus();
	}
	
	/**
	 * Resets the progress of all ProgressBar 
	 */	
	private void resetProgressBars() {
		for (ProgressBar progressBar : tableBars) {
			progressBar.setSelection(0);
		}
	}
	
	/**
	 * Enables or disables the appropriate buttons depending on whether validation was started and whether 
	 * the TableViewer contains filenames 
	 */	
	private void checkButtonActivation() {
		buttonAdd.setEnabled(!validationRunning);
		eventBroker.send(EventConstants.ADD_MENU_ACTIVATION, !validationRunning);
		
		buttonRemove.setEnabled(!validationRunning);
		eventBroker.send(EventConstants.REMOVE_MENU_ACTIVATION, !validationRunning);
		
		buttonClear.setEnabled(!validationRunning);
		eventBroker.send(EventConstants.CLEAR_MENU_ACTIVATION, !validationRunning);
		
		boolean startActive = (!validationRunning) && (table.getItemCount() > 0);
		buttonStart.setEnabled(startActive);
		eventBroker.send(EventConstants.START_MENU_ACTIVATION, startActive);

		buttonStop.setEnabled(validationRunning);
		eventBroker.send(EventConstants.STOP_MENU_ACTIVATION, validationRunning);
	}
	
	@PreDestroy
	public void preDestroy() {
		resourceManager.dispose();
	}

	/**
	 * simple private DropAdapter class which handles drag-and-drop input of files/ folders.
	 */
	private class FileDropListener extends ViewerDropAdapter {
		public FileDropListener(Viewer viewer) {
			super(viewer);
		}

		/**
		 * The implementation of this parent method simply adds the filename of every dropped filen
		 */
		@Override
		public boolean performDrop(Object data) {
			if (!(data instanceof Object[])) {
				return false;
			}
			Object[] objects = (Object[])data;
			
			for (int i = 0; i < objects.length; i++) {
				if (!(objects[i] instanceof String)) {
					continue;
				}
				
				addFile((String) objects[i]);
			}
			return false;
		}

		@Override
		public boolean validateDrop(Object target, int operation, TransferData transferType) {
			return true;
		}
	}
}