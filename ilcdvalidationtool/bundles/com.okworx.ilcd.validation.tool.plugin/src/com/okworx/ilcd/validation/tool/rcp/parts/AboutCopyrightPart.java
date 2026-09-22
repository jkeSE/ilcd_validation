 
package com.okworx.ilcd.validation.tool.rcp.parts;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.okworx.ilcd.validation.tool.resource.Icon;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class AboutCopyrightPart extends AbstractPart {
	private LocalResourceManager resourceManager;
	
	@Inject
	@Optional
	private Shell shell;
	@Inject
	@Translation
	private LocalizationString localizationString;
	
	@PostConstruct
	public void postConstruct() {
		parentComposite.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parentComposite);
		
		GridLayout mainLayout = new GridLayout(1, false);
		mainLayout.marginTop = 20;
		mainLayout.verticalSpacing = 30;
		parentComposite.setLayout(mainLayout);
		
		Composite composite1 = new Composite(parentComposite, SWT.NONE);
		GridLayout compLayout1 = new GridLayout(2, false);
		compLayout1.marginLeft = 30;
		compLayout1.horizontalSpacing = 20;
		compLayout1.verticalSpacing = 30;
		composite1.setLayout(compLayout1);

		Label iconLabel = new Label(composite1, SWT.NONE);
		iconLabel.setImage(Icon.ILCD.getImage(resourceManager));
		
		Composite textComposite = new Composite(composite1, SWT.NONE);
		GridLayout textLayout = new GridLayout(1, false);
		textLayout.marginLeft = 10;
		textComposite.setLayout(textLayout);

		Composite subComposite1 = new Composite(textComposite, SWT.NONE);
		GridLayout subCompLayout1 = new GridLayout(1, false);
		subCompLayout1.marginHeight = 3;
		subComposite1.setLayout(subCompLayout1);
		createLabel(subComposite1, "ILCD Validation Tool", SWT.BOLD);
		
		Composite subComposite2 = new Composite(textComposite, SWT.NONE);
		GridLayout subCompLayout2 = new GridLayout(2, false);
		subCompLayout2.marginHeight = 3;
		subComposite2.setLayout(subCompLayout2);
		createLabel(subComposite2, "version", SWT.BOLD);
		createLabel(subComposite2, localizationString.AppVersion, SWT.NONE);
		
		Composite subComposite3 = new Composite(textComposite, SWT.NONE);
		GridLayout subCompLayout3 = new GridLayout(2, false);
		subCompLayout3.marginHeight = 3;
		subComposite3.setLayout(subCompLayout3);
		createLabel(subComposite3, "copyright\n", SWT.BOLD);
		createLabel(subComposite3, "\u00A9 2015 - 2026 ok*worx\nOliver Kusche Research \u0026\u0026 Consulting", SWT.NONE);
		
		Label develLabel = createLabel(composite1, "Development", SWT.BOLD);
		develLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		createLabel(composite1, "Oliver Armbruster, Oliver Kusche, Dominik Ehrler,\nElias Maier", SWT.NONE);

		createLabel(composite1, "", SWT.NONE);
		createLabel(composite1, localizationString.AboutCopyrightPart_AcknowlegdementText, SWT.NONE);
		
		Composite composite2 = new Composite(parentComposite, SWT.NONE);
		GridLayout compLayout2 = new GridLayout(1, false);
		compLayout2.marginLeft = 20;
		composite2.setLayout(compLayout2);
		
		Composite disclaimerComposite = new Composite(composite2, SWT.NONE);
		GridLayout disclaimerLayout = new GridLayout(1, false);
		disclaimerLayout.verticalSpacing = 15;
		disclaimerComposite.setLayout(disclaimerLayout);
		
		Label disclaimerLabel = new Label(disclaimerComposite, SWT.NONE);
		disclaimerLabel.setText(localizationString.AboutCopyrightPart_LicenseText);
		Link disclaimerLink = new Link(disclaimerComposite, SWT.UNDERLINE_LINK);
		String licenseURL = "https://www.eclipse.org/legal/epl-v10.html";
		disclaimerLink.setText(localizationString.AboutCopyrightPart_LicenseText2+" <a>" + licenseURL +"</a>.");
		disclaimerLink.addSelectionListener(new SelectionAdapter() {
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
					desktop.browse(new URI(licenseURL));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Label fundedByLabel = new Label(disclaimerComposite, SWT.NONE);
		fundedByLabel.setText(localizationString.AboutCopyrightPart_FundedByText);

	}
	
	/**
	 * adds a new Label with the given text with the given font style
	 * 
	 * @param parent
	 * 		parent Composite
	 * @param text
	 * 		text of the Label
	 * @param styleBits
	 * 		SWT style bits
	 * @return
	 * 		the created Label Control
	 */
	private Label createLabel(Composite parent, String text, int styleBits) {
		Label label = new Label(parent, styleBits);
		label.setText(text);
		
		FontDescriptor descriptor = FontDescriptor.createFrom(label.getFont()).setStyle(styleBits);
		label.setFont(resourceManager.createFont(descriptor));
		
		return label;
	}
}