package com.okworx.ilcd.validation.tool.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.*;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.ValidatorChain;
import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.tool.options.ValidationOptions;
import com.okworx.ilcd.validation.tool.resource.Icon;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;
import com.okworx.ilcd.validation.tool.util.EventConstants;
import com.okworx.ilcd.validation.tool.util.ValidatorFactory;
import com.okworx.ilcd.validation.util.IUpdateEventListener;
import com.okworx.ilcd.validation.util.Statistics;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

/**
 * Handles output of validation results. Positive feedback or any occurring
 * error is printed
 * 
 * @author Oliver Armbruster
 *
 */
public class ValidationPart extends AbstractPart {

	private TableViewer tableViewer;
	private Table table;

	private Label statusLabel;

	private LocalResourceManager resourceManager;
	private Image WarningIcon;
	private Image ErrorIcon;
	private Image SuccessIcon;

	private boolean validationRunning = false;
	private boolean reportSuccesses = false;

	private ExecutorService executor;
	private Future<Void> future = null;
	
	private String optionsLog = "";

	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	private IEventBroker eventBroker;
	
	private TableColumnLayout tableColumnLayout;

	@Inject
	private Display display;
	
	@Inject
	private ValidationOptions validationOptions;

	private Statistics statistics;

	@PostConstruct
	public void postConstruct() {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);
		WarningIcon = Icon.WARNING.getImage(resourceManager);
		ErrorIcon = Icon.ERROR.getImage(resourceManager);
		SuccessIcon = Icon.INFO.getImage(resourceManager);

		GridLayout layout = new GridLayout(1, false);
		parentComposite.setLayout(layout);

		Composite tableComposite = new Composite(parentComposite, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer = new TableViewer(tableComposite, SWT.FULL_SELECTION | SWT.H_SCROLL);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		createViewerColumns();
		createContextMenu();

		tableViewer.getTable().addListener(SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Table table = (Table) event.widget;
				int columnCount = table.getColumnCount();
				if (columnCount == 0)
					return;
				Rectangle area = table.getClientArea();
				int totalAreaWdith = area.width;
				int lineWidth = table.getGridLineWidth();
				int totalGridLineWidth = (columnCount - 1) * lineWidth;
				int totalColumnWidth = 0;
				for (TableColumn column : table.getColumns()) {
					totalColumnWidth = totalColumnWidth + column.getWidth();
				}
				int diff = totalAreaWdith - (totalColumnWidth + totalGridLineWidth);

				TableColumn lastCol = table.getColumns()[columnCount - 1];

				// check diff is valid or not. setting negative width doesn't
				// make sense.
				lastCol.setWidth(diff + lastCol.getWidth());
			}
		});

		statusLabel = new Label(parentComposite, SWT.NONE);
		statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		executor = Executors.newFixedThreadPool(1);
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
				if (!(element instanceof IValidationEvent)) {
					return null;
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				switch (valEvent.getSeverity()) {
				case ERROR:
					return ErrorIcon;
				case SUCCESS:
					return SuccessIcon;
				case WARNING:
					return WarningIcon;
				default:
					return null;
				}
			}
		});

		column = createViewerColumn("", 0, false); //$NON-NLS-1$
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IValidationEvent valEvent = (IValidationEvent) element;
				return valEvent.getSeverity().getValue();
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_AspectNameColumnName, 120, false);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				return valEvent.getAspect();
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_FilenameColumnName, 150, false);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				return valEvent.getReference().getShortFileName();
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_DatasetNameColumnName, 200, false);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				return valEvent.getReference().getName();
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_UUIDColumnName, 150, false);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				try {
					return valEvent.getReference().getUuid();
				} catch (NullPointerException e) {
					return null;
				}
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_DatasetTypeColumnName, 130, false);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				try {
					return valEvent.getReference().getDatasetType().getValue();
				} catch (NullPointerException e) {
					return null;
				}
			}
		});

		column = createViewerColumn(localizationString.ValidationPart_MessageColumnName, 6, true, 50);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (!(element instanceof IValidationEvent)) {
					return ""; //$NON-NLS-1$
				}

				IValidationEvent valEvent = (IValidationEvent) element;
				return valEvent.getMessage();
			}
		});

		/*
		 * column =
		 * createViewerColumn(Messages.ValidationPart_EventTypeColumnName, 80);
		 * column.setLabelProvider(new ColumnLabelProvider() {
		 * 
		 * @Override public String getText(Object element) { if (!(element
		 * instanceof IValidationEvent)) { return ""; //$NON-NLS-1$ }
		 * 
		 * IValidationEvent valEvent = (IValidationEvent) element; return
		 * valEvent.getType().getValue(); } });
		 */
	}

	/**
	 * creates a single TableColumn with the given title and the give width/
	 * weight
	 * 
	 * @param text
	 *            column title
	 * @param width
	 *            column width/ weight
	 * @param weightUsed
	 *            if the parameter 'width' indicates the width or the weight of
	 *            the column
	 */
	private TableViewerColumn createViewerColumn(String text, int width, boolean weightUsed) {
		return createViewerColumn(text, width, weightUsed, 50);
	}

	private TableViewerColumn createViewerColumn(String text, int width, boolean weightUsed, int weight) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(text);
		column.setResizable(true);
		column.setMoveable(true);

		if (weightUsed) {
			tableColumnLayout.setColumnData(column, new ColumnWeightData(width, weight, true));
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
		MenuItem menuItemShowInFS = new MenuItem(contextMenu, SWT.PUSH);
		menuItemShowInFS.setText(localizationString.ValidationPart_ShowInFileSystem);

		menuItemShowInFS.addSelectionListener(new SelectionAdapter() {
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

		MenuItem menuItemCopy = new MenuItem(contextMenu, SWT.PUSH);
		menuItemCopy.setText(localizationString.ValidationPart_CopyItem);

		menuItemCopy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				TableItem[] tableItems = table.getSelection();
				if (tableItems.length < 1) {
					return;
				}

				StringBuffer buf = new StringBuffer();

				buf.append(tableItems[0].getText(1));
				buf.append(" - ");
				buf.append(tableItems[0].getText(2));
				buf.append(" - ");
				buf.append(tableItems[0].getText(3));
				buf.append(" - ");
				buf.append(tableItems[0].getText(4));
				buf.append(" - ");
				buf.append(tableItems[0].getText(5));
				buf.append(" - ");
				buf.append(tableItems[0].getText(6));
				buf.append(" - ");
				buf.append(tableItems[0].getText(7));

				copyToClipBoard(buf.toString());
			}
		});

		MenuItem menuItemCopyAll = new MenuItem(contextMenu, SWT.PUSH);
		menuItemCopyAll.setText(localizationString.ValidationPart_CopyAll);

		menuItemCopyAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent selectionEvent) {
				TableItem[] tableItems = table.getItems();

				StringBuffer buf = new StringBuffer();
				buf.append("## Validation Log ##");
				buf.append(System.lineSeparator());
				buf.append(System.lineSeparator());
				buf.append("** generated by ");
				buf.append(localizationString.ProductName);
				buf.append(" ");
				buf.append(localizationString.AppVersion);
				buf.append(" on ");
				buf.append(new Date().toString());
				buf.append(" **");
				buf.append(System.lineSeparator());
				buf.append(System.lineSeparator());

				buf.append("Profile: ");
				buf.append(ValidationPart.this.validationOptions.getProfile().getName());
				buf.append(" version ");
				buf.append(ValidationPart.this.validationOptions.getProfile().getVersion());
				buf.append(System.lineSeparator());
				buf.append(System.lineSeparator());

				buf.append("Aspects: ");
				buf.append(ValidationPart.this.statistics.getAspects());
				buf.append(System.lineSeparator());
				buf.append(System.lineSeparator());

				buf.append("Options:");
				buf.append(System.lineSeparator());
				buf.append(ValidationPart.this.optionsLog);
				buf.append(System.lineSeparator());
				
				buf.append(ValidationPart.this.statistics);

				buf.append(System.lineSeparator());
				buf.append(System.lineSeparator());

				// // add data sources
				// buf.append("Data source(s):\n\n");
				// for (String s : fileSelectionPart.getTableElements()) {
				// buf.append(s);
				// buf.append("\n");
				// }

				if (tableItems.length > 0) {
					buf.append(tableItems.length);
					buf.append(" events:");
					buf.append(System.lineSeparator());
					buf.append(System.lineSeparator());
					for (TableItem item : tableItems) {

						buf.append(item.getText(1));
						buf.append("; ");
						buf.append(item.getText(2));
						buf.append("; ");
						buf.append(item.getText(3));
						buf.append("; ");
						buf.append(item.getText(4).replace(";", ","));
						buf.append("; ");
						buf.append(item.getText(5));
						buf.append("; ");
						buf.append(item.getText(6));
						buf.append("; ");
						buf.append(item.getText(7));
						buf.append(System.lineSeparator());
					}
				} else {
					buf.append("0 events.");
					buf.append(System.lineSeparator());
					buf.append(System.lineSeparator());
				}

				copyToClipBoard(buf.toString());
			}
		});

		table.setMenu(contextMenu);
	}

	/**
	 * opens the given path in the default file browser of the OS and selects
	 * the respective file
	 * 
	 * @param path
	 *            the path to be opened in the file browser
	 */
	public void openInBrowser(String path) {
		try {
			File file = new File(path);
			String canonicalPath = file.getCanonicalPath();
			String canoncialURI = file.getCanonicalFile().toURI().toString();

			String command = "";
			if (Util.isGtk()) {
				command = "dbus-send --print-reply --dest=org.freedesktop.FileManager1 /org/freedesktop/FileManager1 org.freedesktop.FileManager1.ShowItems array:string:\"" + canoncialURI + "\" string:\"\""; //$NON-NLS-1$
			} else if (Util.isWindows()) {
				command = "explorer /E,/select=" + canonicalPath; //$NON-NLS-1$
			} else if (Util.isMac()) {
				command = "open -R \"" + canonicalPath + "\""; //$NON-NLS-1$
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
	 * Copies the given text to the clipboard
	 * 
	 * @param text
	 */
	public void copyToClipBoard(String text) {
		final Clipboard cb = new Clipboard(this.display);

		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[] { text }, new Transfer[] { textTransfer });
	}

	/**
	 * Allows other threads to set the text of the status Label
	 * 
	 * @param text
	 *            text String
	 */
	@Inject
	@Optional
	public void setStatusText(@UIEventTopic(EventConstants.SET_STATUS_TEXT) String text) {
		if (statusLabel == null) {
			return;
		}

		statusLabel.setText(text);
	}

	@Inject
	@Optional
	public void setReportSuccesses(@UIEventTopic(EventConstants.SET_REPORTSUCCESSES) Boolean reportSuccesses) {
		this.reportSuccesses = reportSuccesses;
	}

	/**
	 * Allows other threads to add events lists into this part's table which are
	 * displayed accordingly
	 * 
	 * @param text
	 *            EventsList to be added to the table
	 */
	@Inject
	@Optional
	public void addEventsToTable(@UIEventTopic(EventConstants.TABLE_ADD_EVENTS) EventsList eventsList) {
		int count = 0;
		boolean confirmed = false;
		
		for (IValidationEvent event : eventsList.getEvents()) {
			count++;
			if (!confirmed && count > 30000) {
				confirmed = askForContinue();
				if (confirmed) 
					continue;
				else
					return;
			}
				
			tableViewer.add(event);
			String absolutePath = event.getReference().getAbsoluteFileName();
			table.getItem(table.getItemCount() - 1).setData(absolutePath);
		}
	}
	
	private boolean askForContinue() {
		return MessageDialog.openConfirm(this.display.getActiveShell(), localizationString.ValidationPart_ConfirmManyEventsText, localizationString.ValidationPart_ConfirmManyEventsMessage);
	}

	/**
	 * Signals that the validation process is finished or should be aborted
	 */
	@Inject
	@Optional
	public void validationFinished(@UIEventTopic(EventConstants.STOP_VALIDATION) String dummy) {
		if (!validationRunning) {
			return;
		}

		if (future != null) {
			if (!future.isDone() && !future.isCancelled()) {
				future.cancel(true);
				future = null;
			}
		}
	}

	/**
	 * Allows other threads to clear the content of this part's table
	 */
	@Inject
	@Optional
	public void clearTableViewer(@UIEventTopic(EventConstants.TABLE_CLEAR) String dummy) {

		table.clearAll();
		table.setItemCount(0);
	}

	/**
	 * Starts the validation process for the each of the give filenames and with
	 * give ValidatorFactory
	 * 
	 * @param filenames
	 *            filenames to be validated
	 * @param validatorFactory
	 *            ValidatorFactory to be used for validation
	 */
	public void startValidation(String[] filenames, ValidatorFactory validatorFactory) {
		this.optionsLog = validationOptions.dumpOptions();
		future = executor.submit(new ValidationCallable(filenames, validatorFactory));
	}

	@Focus
	public void setFocus() {
		table.setFocus();
	}

	@PreDestroy
	public void preDestroy() {
		eventBroker.send(EventConstants.STOP_VALIDATION, "");
		executor.shutdown();
		resourceManager.dispose();
	}

	/**
	 * Private callable class which handles the validation process of the
	 * selected files. Instances of this class are given an array of filenames
	 * which are subsequently validated in call()
	 */
	private class ValidationCallable implements IUpdateEventListener, Callable<Void> {
		String[] filenames;
		ValidatorFactory validatorFactory;

		/**
		 * creates an instance with an array of filenames to be validated
		 * 
		 * @param filenames
		 *            String array of filenames to be validated
		 */
		public ValidationCallable(String[] filenames, ValidatorFactory validatorFactory) {
			this.filenames = filenames;
			this.validatorFactory = validatorFactory;
		}

		@Override
		public Void call() throws Exception {
			eventBroker.send(EventConstants.TABLE_CLEAR, ""); //$NON-NLS-1$
			validationRunning = true;

			Statistics statistics = new Statistics();

			for (String filename : filenames) {
				Statistics stats = validateFile(filename);
				statistics.add(stats);
			}
			
			validationRunning = false;
			eventBroker.send(EventConstants.STOP_VALIDATION, "");

			eventBroker.send(EventConstants.UPDATE_STATISTICS, statistics);

			ValidationPart.this.statistics = statistics;

			return null;
		}

		/**
		 * Validates the given file and sends appropriate events containing
		 * detected validation errors
		 * 
		 * @param filename
		 *            filename to be validated
		 */
		private Statistics validateFile(String filename) {
			eventBroker.send(EventConstants.SET_STATUS_TEXT, localizationString.ValidationPart_ValidatingFileText + " \"" + filename + "\"...");

			ValidatorChain validator = validatorFactory.createValidator();

			validator.setObjectsToValidate(new File(filename));
			validator.setUpdateEventListener(this);

			validator.setReportSuccesses(ValidationPart.this.reportSuccesses);

			boolean success = validator.validate();
			if (success) {
				eventBroker.send(EventConstants.SET_STATUS_TEXT, localizationString.ValidationPart_ValidationCorrectText);
				eventBroker.send(EventConstants.FILE_VALIDATED, true);
				eventBroker.send(EventConstants.TABLE_ADD_EVENTS, validator.getEventsList());
			} else {
				int errors = validator.getEventsList().getErrorCount();
				int warnings = validator.getEventsList().getWarningCount();

				StringBuilder message = new StringBuilder(localizationString.ValidationPart_ValidationErrorsText1);
				message.append(" ").append(errors).append(" ").append(localizationString.ValidationPart_ValidationErrorsText2);
				if (warnings > 0) {
					message.append(" ").append(localizationString.ValidationPart_ValidationErrorsText3).append(" ").append(warnings);
					message.append(" ").append(localizationString.ValidationPart_ValidationErrorsText4);
				}
				message.append(localizationString.ValidationPart_ValidationErrorsText5);

				eventBroker.send(EventConstants.FILE_VALIDATED, false);
				
				if (!validator.isBatchMode()) {
					eventBroker.send(EventConstants.SET_STATUS_TEXT, message.toString());
					eventBroker.send(EventConstants.TABLE_ADD_EVENTS, validator.getEventsList());
				} else {
					eventBroker.send(EventConstants.SET_STATUS_TEXT, localizationString.OptionsPart_btnGeneralBatchModeStatusBarFinish);
				}
			}

			return validator.getStatistics();
		}

		@Override
		public void updateProgress(double arg0) {
			eventBroker.send(EventConstants.UPDATE_PROGRESS, arg0 * 100);
		}

		@Override
		public void updateStatus(String arg0) {
			eventBroker.send(EventConstants.SET_STATUS_TEXT, arg0);
		}
	}
	
}