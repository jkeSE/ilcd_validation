package com.okworx.ilcd.validation.reference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.common.DefaultValidationContext;
import com.okworx.ilcd.validation.common.IContextAwareComponent;
import com.okworx.ilcd.validation.common.IValidationContext;
import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.ILCDNameSpaceContext;
import com.okworx.ilcd.validation.util.PartitionedList;

/**
 * <p>ReferenceBuilder class.</p>
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ReferenceBuilder implements IContextAwareComponent {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	private HashMap<String, IDatasetReference> references;

	/**
	 * <p>Getter for the field <code>references</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, IDatasetReference> getReferences() {
		return references;
	}

	private String aspectName = null;
	
	protected EventsList eventsList = new EventsList(this.aspectName);

	protected IValidationContext validationContext = new DefaultValidationContext();
	
	/**
	 * <p>Constructor for ReferenceBuilder.</p>
	 */
	public ReferenceBuilder() {
	}
	
	/**
	 * <p>Constructor for ReferenceBuilder.</p>
	 *
	 * @param aspectName a {@link java.lang.String} object.
	 */
	public ReferenceBuilder(String aspectName) {
		this.aspectName = aspectName;
	}
	
	// TODO: improve performance by using a Stax implementation instead
	/**
	 * <p>build.</p>
	 *
	 * @param origSource a {@link java.io.File} object.
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, IDatasetReference> build(File origSource) {

		TFile source = new TFile(origSource);

		if (!source.isFile() && !source.isArchive() && !source.isDirectory())
			throw new IllegalArgumentException(source.getAbsolutePath() + " is neither a file nor a directory nor a ZIP archive");

		Collection<TFile> files = new ArrayList<TFile>();

		if (source.isFile()) {
			files.add(source);
		} else {
			// add all XML datasets
			// as this will include any other XML files, let's filter out those which we know not to be well-formed
			Collection<File> xmlfiles = FileUtils.listFiles(source, FileFilterUtils.and(FileFilterUtils.suffixFileFilter(".xml", IOCase.INSENSITIVE), 
					FileFilterUtils.notFileFilter(FileFilterUtils.or(
							FileFilterUtils.nameFileFilter("compliance1.xml"),
							FileFilterUtils.nameFileFilter("compliance2.xml"),
							FileFilterUtils.nameFileFilter("compliance3.xml"),
							FileFilterUtils.nameFileFilter("compliance4.xml"),
							FileFilterUtils.nameFileFilter("complianceOur.xml")
					))),
					TrueFileFilter.INSTANCE);
			// now add any files in external_docs - scan the rest and add only files in a folder named external_docs
			Collection<File> extfiles = FileUtils.listFiles(source,
					FileFilterUtils.notFileFilter(FileFilterUtils.suffixFileFilter(".xml", IOCase.INSENSITIVE)), TrueFileFilter.INSTANCE);
			if (log.isDebugEnabled()) {
				log.debug("found " + xmlfiles.size() + " XML files and " + extfiles.size() + " others");
			}
			for (File f : xmlfiles) {
				if (log.isTraceEnabled())
					log.trace("adding " + f.getName());
				files.add(new TFile(f));
			}
			for (File f : extfiles) {
				if (f.getParent().endsWith("external_docs") && !f.getName().startsWith(".")) {
					if (log.isTraceEnabled())
						log.trace("adding external file " + f.getName());
					files.add(new TFile(f));
				}
			}
		}
		this.references = new HashMap<String, IDatasetReference>();
		ConcurrentHashMap<String, IDatasetReference> map = new ConcurrentHashMap<String, IDatasetReference>();

		PartitionedList<TFile> partList = new PartitionedList<TFile>(files);

		Collection<Callable<HashMap<String, IDatasetReference>>> tasks = new ArrayList<Callable<HashMap<String, IDatasetReference>>>();

		for (List<TFile> fileList : partList.getPartitions()) {
			tasks.add(new ExtractReferencesTask(fileList));
		}

		try {
			ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());
			List<Future<HashMap<String, IDatasetReference>>> results = executor.invokeAll(tasks);
			for (Future<HashMap<String, IDatasetReference>> result : results) {
				map.putAll(result.get());
			}
			executor.shutdown();
		} catch (Exception e) {
			log.error(e);
		}

		if (log.isDebugEnabled())
			log.debug(map.size() + " references extracted");

		this.references.putAll(map);
		return this.references;
	}

	final protected class ExtractReferencesTask extends AbstractDatasetsTask implements Callable<HashMap<String, IDatasetReference>> {

		protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

		ExtractReferencesTask(List<TFile> files) {
			this.files = files;
		}

		public HashMap<String, IDatasetReference> call() throws Exception {
			return parse(files);
		}

		private final List<TFile> files;

		private HashMap<String, IDatasetReference> parse(List<TFile> files) {
			HashMap<String, IDatasetReference> result = new HashMap<String, IDatasetReference>();

			DocumentBuilder builder;
			XPathExpression xpRootElement;
			XPathExpression xpUuid;
			XPathExpression xpVersion;
			XPathExpression xpNameProcessFlowModel;
			XPathExpression xpNameProcessFlowModelLocalized;
			XPathExpression xpNameSource;
			XPathExpression xpNameSourceLocalized;
			XPathExpression xpNameOther;
			XPathExpression xpNameOtherLocalized;
			XPathExpression xpTypeFlow;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				builder = factory.newDocumentBuilder();

				// Create XPathFactory object
				XPathFactory xpathFactory = XPathFactory.newInstance();

				// Create XPath object
				XPath xpath = xpathFactory.newXPath();
				xpath.setNamespaceContext(new ILCDNameSpaceContext());

				// TODO make sure the file we're reading is an ILCD dataset
				xpRootElement = xpath.compile("local-name(/*)");

				xpUuid = xpath.compile("/*/*/*[local-name()='dataSetInformation']/common:UUID/text()");
				xpVersion = xpath.compile("/*/*[local-name()='administrativeInformation']/*[local-name()='publicationAndOwnership']/common:dataSetVersion/text()");
				
				// name can be in multiple languages
				// if a CustomValidationContext is given, use the language from the Context's locale
				// if not, use the default locale
				// if no data according to these settings is found, take the first entry
				String lang = ReferenceBuilder.this.validationContext.getLocale().getLanguage();
				
				xpNameProcessFlowModel = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='name']/*[local-name()='baseName']/text()");
				xpNameProcessFlowModelLocalized = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='name']/*[local-name()='baseName' and @xml:lang='" + lang + "']/text()");
				xpNameSource = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='shortName']/text()");
				xpNameSourceLocalized = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='shortName' and @xml:lang='" + lang + "']/text()");
				xpNameOther = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='name']/text()");
				xpNameOtherLocalized = xpath.compile("/*/*/*[local-name()='dataSetInformation']/*[local-name()='name' and @xml:lang='" + lang + "']/text()");

				xpTypeFlow = xpath.compile("/f:flowDataSet/f:modellingAndValidation/f:LCIMethod/f:typeOfDataSet/text()");
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				return null;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			}

			Document doc = null;

			for (TFile file : files) {

				if (log.isDebugEnabled())
					log.debug("parsing file " + file.getName());

				if (StringUtils.endsWithIgnoreCase(file.getName(), ".xml")) {

					try {
						doc = builder.parse(new TFileInputStream(file));

						String rootElement = (String) xpRootElement.evaluate(doc, XPathConstants.STRING);

						DatasetType type = DatasetType.fromRootElementName(rootElement);

						if (type == null)
							continue;

						// evaluate expression result on XML document
						String uuid = (String) xpUuid.evaluate(doc, XPathConstants.STRING);

						if (uuid == null)
							continue;

						uuid = uuid.toLowerCase();

						if (log.isDebugEnabled())
							log.debug(" found UUID: "+ uuid);

						String version = (String) xpVersion.evaluate(doc, XPathConstants.STRING);
						
						IDatasetReference ref = new DatasetReference(uuid, version, file.getAbsolutePath(), file.getName());
						ref.setDatasetType(type);

						switch (type) {
						case FLOW:
							ref = new FlowDatasetReference(uuid, version, file.getAbsolutePath(), file.getName());
							ref.setDatasetType(type);
							((FlowDatasetReference) ref).setFlowType((String) xpTypeFlow.evaluate(doc, XPathConstants.STRING));
						case PROCESS:
						case LCMODEL:
							ref.setName(evaluateName(doc, xpNameProcessFlowModel, xpNameProcessFlowModelLocalized));
							if (log.isTraceEnabled())
								log.trace(" type, name: " + type + " " + ref.getName());
							break;
						case SOURCE:
							ref.setName(evaluateName(doc, xpNameSource, xpNameSourceLocalized));
							if (log.isTraceEnabled())
								log.trace(" type, name: " + type + " " + ref.getName());
							break;
						case CONTACT:
						case FLOWPROPERTY:
						case LCIAMETHOD:
						case UNITGROUP:
							ref.setName(evaluateName(doc, xpNameOther, xpNameOtherLocalized));
							if (log.isTraceEnabled())
								log.trace(" type, name: " + type + " " + ref.getName());
							break;
						default:
							break;
						}

						result.put(uuid.toLowerCase(), ref);
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						ReferenceBuilder.this.eventsList.add(new ValidationEvent("General", Severity.ERROR, new DatasetReference(file.getAbsolutePath(), file.getName()), "error parsing file: " + e.getMessage()));
						log.warn("possibly invalid XML", e);
					} catch (IOException e) {
						ReferenceBuilder.this.eventsList.add(new ValidationEvent("General", Severity.ERROR, new DatasetReference(file.getAbsolutePath(), file.getName()), "error reading file" + e.getMessage()));
						log.warn("possibly invalid XML", e);
					}
				} else {
					DatasetType type = DatasetType.EXTERNAL_FILE;

					log.debug("putting " + file.getName() + " in reference cache");

					IDatasetReference ref = new DatasetReference(null, null, file.getAbsolutePath(), file.getName());
					ref.setDatasetType(type);

					result.put(file.getName(), ref);
				}
			}
			log.debug("returning " + result.size() + " results");
			return result;
		}
	}
	
	private String evaluateName(Document doc, XPathExpression expr, XPathExpression exprLocalized) throws XPathExpressionException {
		String result = (String) exprLocalized.evaluate(doc, XPathConstants.STRING);
		if (StringUtils.isBlank(result))
			result = (String) expr.evaluate(doc, XPathConstants.STRING);
		return result;
	}

	/**
	 * <p>Getter for the field <code>aspectName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspectName() {
		return aspectName;
	}

	/**
	 * <p>Setter for the field <code>aspectName</code>.</p>
	 *
	 * @param aspectName a {@link java.lang.String} object.
	 */
	public void setAspectName(String aspectName) {
		this.aspectName = aspectName;
	}

	/**
	 * <p>Getter for the field <code>eventsList</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.events.EventsList} object.
	 */
	public EventsList getEventsList() {
		return eventsList;
	}

	/**
	 * <p>Setter for the field <code>eventsList</code>.</p>
	 *
	 * @param eventsList a {@link com.okworx.ilcd.validation.events.EventsList} object.
	 */
	public void setEventsList(EventsList eventsList) {
		this.eventsList = eventsList;
	}

	/**
	 * <p>Getter for the field <code>validationContext</code>.</p>
	 *
	 * @return a {@link com.okworx.ilcd.validation.common.IValidationContext} object.
	 */
	public IValidationContext getValidationContext() {
		return validationContext;
	}

	/** {@inheritDoc} */
	public void setValidationContext(IValidationContext validationContext) {
		this.validationContext = validationContext;
	}
}
