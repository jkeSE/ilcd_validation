package com.okworx.ilcd.validation.tool.rcp.parts;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.tool.options.ValidationOptions;
import com.okworx.ilcd.validation.tool.resource.LocalizationString;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class ProfileChangelogView extends AbstractPart {
	Logger logger = LogManager.getLogger(getClass());
	
	@Inject
	@Translation
	private LocalizationString localizationString;

	@Inject
	ValidationOptions validationOptions;
	
	FillLayout layout;
	
	Browser semanticBrowser;
	Browser technicalBrowser;

	Parser markdownParser;
	HtmlRenderer htmlRenderer;



	@PostConstruct
	public void postConstruct(Composite parent, final IProvisioningAgent agent, final Shell shell,
			final UISynchronize sync, final IWorkbench workbench)
			throws InvocationTargetException, InterruptedException {
		logger.info("initialize ProfileChangelogView");
		layout = new FillLayout(SWT.HORIZONTAL);
		parent.setLayout(layout);
		this.semanticBrowser = new Browser(parent, SWT.NONE);
		this.technicalBrowser = new Browser(parent, SWT.NONE);
		

		markdownParser = Parser.builder().build();
		htmlRenderer = HtmlRenderer.builder().escapeHtml(true).build();
		
		handleProfileUpdate();
		
		Runnable update = () -> {
			logger.info("profile selection updated");
			handleProfileUpdate();
		};
		
		
		validationOptions.addObserver(update, this);
	}
	
	@PreDestroy
	public void preDestroy() {
		validationOptions.deleteObserver(this);
	}
	
	private void handleProfileUpdate() {
		logger.debug("ProfileChangelogView: profile update!");
		logger.debug(validationOptions.getProfile().getName());
		
		Profile profile = validationOptions.getProfile();
		if(profile.getSemanticChangelog() != null && profile.getTechnicalChangelog() != null) {
			String semanticChangelog = readFile(buildPath(profile.getPath().getAbsolutePath(), profile.getSemanticChangelog()));
			String technicalChangelog = readFile(buildPath(profile.getPath().getAbsolutePath(), profile.getTechnicalChangelog()));

			setChangelogText(semanticChangelog, technicalChangelog);
		} else {
			setChangelogText(localizationString.ProfileChangelogPart_NoUpdateText, localizationString.ProfileChangelogPart_NoUpdateText);
		}
	}

	public String readFile(String path) {
		
		try (InputStream in = new URL(path).openStream()) {
			logger.info("read changelog file: " + path);
			return IOUtils.toString(in, StandardCharsets.UTF_8);
		}		 catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		return null;
	}
	
	private void setChangelogText(String semantic, String technical) {
		logger.info("parse and display changelog content");
		String semanticPrepared = localizationString.ProfileChangelogPart_SemnaticUpdatesTitle + "\n";
		String technicalPrepared = localizationString.ProfileChangelogPart_TechnicalUpdatesTitle + "\n";
		if(semantic != null) {
			semanticPrepared += semantic;
		} else {
			semanticPrepared += localizationString.ProfileChangelogPart_NoUpdateText;
		}
		if(technical != null) {
			technicalPrepared += technical;
		} else {
			technicalPrepared += localizationString.ProfileChangelogPart_NoUpdateText;
		}
		semanticBrowser.setText(renderToMarkdown(semanticPrepared));
		technicalBrowser.setText(renderToMarkdown(technicalPrepared));
	}

	private String renderToMarkdown(String markdownText) {
		if(markdownText == null) {
			markdownText = localizationString.ProfileChangelogPart_NoUpdateText;
		}
		Node document = markdownParser.parse(markdownText);
		String html =  htmlRenderer.render(document);
		return html;
	}
	
	private static String buildPath(String pathToJar, String resourcePath) {
		// build "jar:file:" + pathToJar + "!/" + resourcePath;
		StringBuffer buf = new StringBuffer();
		if (!pathToJar.startsWith("file:"))
			buf.append("jar:file:");
		else
			buf.append("jar:");
		buf.append(pathToJar);
		buf.append("!/");
		buf.append(resourcePath);
		return buf.toString();
	}
}
