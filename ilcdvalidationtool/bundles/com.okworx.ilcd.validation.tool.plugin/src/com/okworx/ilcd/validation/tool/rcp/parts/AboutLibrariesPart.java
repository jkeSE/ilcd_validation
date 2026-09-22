package com.okworx.ilcd.validation.tool.rcp.parts;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class AboutLibrariesPart extends AbstractPart {
	private TableViewer tableViewer;
	private Table table;
	private TableColumnLayout tableColumnLayout;

	@Inject
	@Translation
	private LocalizationString localizationString;

	@PostConstruct
	public void postConstruct() {
		GridLayout layout = new GridLayout(1, false);
		parentComposite.setLayout(layout);

		Composite tableComposite = new Composite(parentComposite, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer = new TableViewer(tableComposite, SWT.NONE);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		
		// read version from packages:
		String ilcdValidationVersion = com.okworx.ilcd.validation.Validator.class.getPackage().getImplementationVersion();
		String trueVfsVersion = net.java.truevfs.access.TApplication.class.getPackage().getImplementationVersion();
		String guavaVersion = com.google.common.base.Defaults.class.getPackage().getImplementationVersion();
		String commonsIoVersion = org.apache.commons.io.FileUtils.class.getPackage().getImplementationVersion();
		String commonsLangVersion = org.apache.commons.lang3.arch.Processor.class.getPackage().getImplementationVersion();
		String log4jVersion = org.apache.logging.log4j.Logger.class.getPackage().getImplementationVersion();
		
		createViewerColumns();
		addTableItem("Apache Commons IO", commonsIoVersion,
				"Apache License 2.0", "http://www.apache.org/licenses/LICENSE-2.0");
		addTableItem("Apache Commons Lang", commonsLangVersion, 
				"Apache License 2.0", "http://www.apache.org/licenses/LICENSE-2.0");
		addTableItem("Guava Collections", guavaVersion, 
				"Apache License 2.0", "http://www.apache.org/licenses/LICENSE-2.0");
		addTableItem("ILCD Validation Library", ilcdValidationVersion, 
				"LGPL v3", "http://www.gnu.org/licenses/lgpl-3.0.txt");
		addTableItem("log4j", log4jVersion, 
				"Apache License 2.0", "http://www.apache.org/licenses/LICENSE-2.0");
		addTableItem("Open Icon Library", "0.11", 
				"various, see installation folder", null);
		addTableItem("TrueVFS", trueVfsVersion,
				"Eclipse Public License 1.0", "http://www.eclipse.org/legal/epl-v10.html");
	}

	/**
	 * creates the columns of the ViewerTable with appropriate LabelProviders
	 */
	private void createViewerColumns() {
		createViewerColumn(localizationString.AboutLibrariesPart_NameColumnName, 3, true);
		createViewerColumn(localizationString.AboutLibrariesPart_VersionColumnName, 1, true);
		createViewerColumn(localizationString.AboutLibrariesPart_LicenseColumnName, 3, true);
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
	 * adds an TableItem to the ViewerTable
	 * 
	 * @param name
	 * 		library name
	 * @param version
	 * 		library version
	 * @param license
	 * 		library license
	 * @param hyperlink
	 * 		link for library license
	 */
	private void addTableItem(String name, String version, String license, String hyperlink) {
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText(new String[] {name, version});
		
		TableEditor tableEditor = new TableEditor(table);
		tableEditor.grabHorizontal = true;
		tableEditor.grabVertical = true;
		
		Link link = new Link(table, SWT.UNDERLINE_LINK);
		
		if (hyperlink != null)
			link.setText("<a>"+license+"</a>");
		else
			link.setText(license);
		
		link.setBackground(table.getBackground());
		link.setSize(10, 10);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (!Desktop.isDesktopSupported()) {
					return;
				}
				Desktop desktop = Desktop.getDesktop();
				
				if (!desktop.isSupported(Desktop.Action.BROWSE)) {
					return;
				}
				try {
					desktop.browse(new URI(hyperlink));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		tableEditor.setEditor(link, tableItem, 2);
	}
}