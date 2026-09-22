package com.okworx.ilcd.validation;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.java.truevfs.access.TFileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.reference.DatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.reference.ReferenceCache;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.ILCDNameSpaceContext;
import com.okworx.ilcd.validation.util.PartitionedList;
import com.okworx.ilcd.validation.util.TaskResult;
import com.okworx.ilcd.validation.util.TransletCache;

/**
 * Checks whether local references (links) within datasets are valid.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class LinkValidator extends AbstractReferenceObjectsAwareValidator implements IValidator {

	public static final String ASPECT_NAME = "Links";

	/** Constant <code>PARAM_IGNORE_REFS_TO_LCIAMETHODS="ignoreReferencesToLCIAMethods"</code> */
	public static final String PARAM_IGNORE_REFS_TO_LCIAMETHODS = "ignoreReferencesToLCIAMethods";

	/** Constant <code>PARAM_IGNORE_COMPLEMENTINGPROCESS="ignoreComplementingProcess"</code> */
	public static final String PARAM_IGNORE_COMPLEMENTINGPROCESS = "ignoreComplementingProcess";

	/** Constant <code>PARAM_IGNORE_INCLUDEDPROCESSES="ignoreIncludedProcesses"</code> */
	public static final String PARAM_IGNORE_INCLUDEDPROCESSES = "ignoreIncludedProcesses";

	/** Constant <code>PARAM_IGNORE_PRECEDINGDATASETVERSION="ignorePrecedingDatasetVersion"</code> */
	public static final String PARAM_IGNORE_PRECEDINGDATASETVERSION = "ignorePrecedingDatasetVersion";

	/** Constant <code>PARAM_IGNORE_REFS_WITH_REMOTE_LINKS="ignoreReferencesWithRemoteLinks"</code> */
	public static final String PARAM_IGNORE_REFS_WITH_REMOTE_LINKS = "ignoreReferencesWithRemoteLinks";

	/** {@inheritDoc} */
	@Override
	public String getAspectName() {
		return ASPECT_NAME;
	}

	/**
	 * <p>getAspectDescription.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAspectDescription() {
		return "Checks whether local references (links) within a set datasets are valid.";
	}

	// allows to add additional references to the references objects cache for which validation
	// events due to missing reference targets are to be suppressed during link validation
	public void addReferences(HashMap<String, IDatasetReference> objects) {
		this.referenceObjectsCache.put(objects);
	}

	protected ReferenceCache referenceObjectsCache = new ReferenceCache();

	// the key is the referenced source's UUID
	protected ConcurrentHashMap<String, IDatasetReference> referencesToSourceWithImages = new ConcurrentHashMap<>();

	// the key is the originating dataset's (source dataset's) UUID
	protected ConcurrentHashMap<String, IDatasetReference> referencesToDigitalFile = new ConcurrentHashMap<>();

	// TODO refactor out link extraction routine
	/**
	 * <p>validate.</p>
	 *
	 * @return boolean.
	 * @throws java.lang.InterruptedException if any.
	 */
	public boolean validate() throws InterruptedException {
		super.validate();

		updateStatusValidating();

		this.unitsTotal = this.objectsToValidate.size();

		this.referenceObjectsCache.put(this.objectsToValidate);
        log.debug("done - {} objects in cache.", this.referenceObjectsCache.getLinks().size());

		boolean result = true;

		try {

			XPathExpression expr = setupXpathRef();

			PartitionedList<IDatasetReference> partList = new PartitionedList<>(this.objectsToValidate.values());

            log.debug("{} total objects split into {} chunks", this.unitsTotal, partList.getPartitions().size());

			Collection<Callable<TaskResult>> tasks = new ArrayList<>();

			for (List<IDatasetReference> refList : partList.getPartitions()) {
				tasks.add(new LinkExtractorTask(refList, TransletCache.getInstance().getTranslet(), expr, this));
			}

			ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());

			try {
				List<Future<TaskResult>> taskResults = executor.invokeAll(tasks);
				for (Future<TaskResult> taskResult : taskResults) {
					if (taskResult.get() != null) {
						TaskResult res = taskResult.get();
						this.eventsList.addAll(res.getValidationEvents());
						this.statistics.add(res.getStatistics());
					}
				}
				executor.shutdown();
			} catch (InterruptedException e) {
				executor.shutdown();
				interrupted(e);
			} catch (Exception e) {
				log.error(e);
			}

			checkImagesReferences();

            log.info("{} events", this.eventsList.getEvents().size());

			updateProgress(1);
			updateStatusDone();

			return this.getEventsList().isPositive();
		}

		catch (Exception e) {
			log.error(e);
		}

		return result;
	}

	private XPathExpression setupXpathRef() throws XPathExpressionException {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new ILCDNameSpaceContext());
		return xpath.compile("/*/*");
	}

	// this check whether any local file attachments referenced for "referenceToTechnologyFlowDiagrammOrPicture" or
	// "referenceToTechnologyPictogramme" have a valid extension of either .png, .jpg or .jpeg (case insensitive)
	private void checkImagesReferences() {

		if (log.isDebugEnabled()) {
			log.debug("{} links to images found", this.referencesToSourceWithImages.size());
			log.debug("referencesToSourceWithImages: {}", referencesToSourceWithImages);
			log.debug("referencesToDigitalFile: {}", referencesToDigitalFile);
		}

		for (String key : this.referencesToSourceWithImages.keySet()) {
			IDatasetReference ref = this.referencesToSourceWithImages.get(key);

			log.debug("source with image: {} - {} ", key, ref);

			if (this.referencesToDigitalFile.containsKey(ref.getUuid())) {
				IDatasetReference digitalFileRef = this.referencesToDigitalFile.get(ref.getUuid());
				String fileName = digitalFileRef.getUri().toLowerCase();
				log.debug("examining {}", fileName);

				// we allow .png, .jpg, .jpeg regardless of case, otherwise we'll generate a warning
				if (! (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ) ) {
					ValidationEvent e = new ValidationEvent(LinkValidator.this.getAspectName(), Severity.WARNING, digitalFileRef, "the image should be in either PNG or JPG format");
					e.setAltMessage("the image should be in either PNG or JPG format");
					this.eventsList.add(e);
				}
			} else {
				log.debug("no referenceToDigitalFile found for key {}", ref.getUuid());
			}

		}
	}

	final class LinkExtractorTask extends AbstractDatasetsTask implements Callable<TaskResult> {

		private static final String MESSAGE_INVALID_REFERENCE = "Invalid (because empty) reference, neither UUID nor URI specified. Reference to ";

		private static final String MESSAGE_TO = "to ";

		private static final String MESSAGE_COULD_NOT_RESOLVE_REFERENCE = "could not resolve reference ";

		private static final String MESSAGE_CASE_SENSITIVE_FILE_NAMES = "Note that file names for external files are case-sensitive and must match exactly with the file name specified in the source dataset!";
		private final XPathExpression expr;

		private final Templates templates;

		LinkExtractorTask(List<IDatasetReference> files, Templates templates, XPathExpression expr,
				LinkValidator validator) {
			this.files = files;
			this.expr = expr;
			this.templates = templates;
			this.validator = validator;
		}

		public TaskResult call() throws Exception {
			return new TaskResult(extract(files), this.statistics);
		}

		// this extracts all references from the given datasets using the extractReferences.xsl stylesheet
		private Collection<IValidationEvent> extract(Collection<IDatasetReference> files) throws Exception {

			Collection<IValidationEvent> events = new ArrayList<>();

			Transformer transformer = templates.newTransformer();

			int count = 0;

			for (IDatasetReference reference : files) {
				if (Thread.currentThread().isInterrupted()) {
					log.info("operation was interrupted, aborting");
					updateStatusCancelled();
					break;
				}

				int eventCount = events.size();
				
				if (!reference.getDatasetType().equals(DatasetType.EXTERNAL_FILE))
					checkReference(events, transformer, reference);

				boolean success = (eventCount == events.size());
				this.statistics.update(reference, success);
				
				if (success && LinkValidator.this.reportSuccesses)
					events.add(new ValidationEvent(LinkValidator.this.getAspectName(), Severity.SUCCESS, reference, ValidationEvent.SUCCESS_MESSAGE));

				count = updateChunkCount(count);
			}

			return events;
		}

		// this will first extract the links and then check them against the global list of references,
		// reporting every reference that is given without the corresponding object being available
		private void checkReference(Collection<IValidationEvent> events, Transformer transformer,
				IDatasetReference reference) throws TransformerException, XPathExpressionException, IOException {

			NodeList refs = extractNodeList(transformer, reference);

			List<IDatasetReference> links = new ArrayList<>();

			boolean paramCheckReferencesWithRemoteLink = !BooleanUtils.isTrue((Boolean) this.validator.getParameter(PARAM_IGNORE_REFS_WITH_REMOTE_LINKS));

			for (int i = 0; i < refs.getLength(); i++) {
				Node ref = refs.item(i);
				String uuid = ref.getAttributes().getNamedItem(ATTRIBUTE_REF_OBJECT_ID).getNodeValue().trim();
				String uri = ref.getAttributes().getNamedItem(ATTRIBUTE_URI).getNodeValue().trim();
				String origin = ref.getAttributes().getNamedItem(ATTRIBUTE_ORIGIN).getNodeValue().trim();
				log.trace("uuid/uri/origin: {} {} {}", uuid, uri, origin);

				String name = null;

				try {
					name = ref.getAttributes().getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();
				} catch (NullPointerException ignored) {
				}
				
				// skip remote links if configured
				if (!paramCheckReferencesWithRemoteLink && (StringUtils.startsWithIgnoreCase(uri, URL_PREFIX_HTTP) || StringUtils.startsWithIgnoreCase(uri, URL_PREFIX_HTTPS))) {
					log.debug("skipping remote URL {}", uri);
					continue;
				}

				String type = ref.getAttributes().getNamedItem(ATTRIBUTE_TYPE).getNodeValue();
				DatasetType dsType = null;
				try {
					dsType = DatasetType.fromValue(type);
				} catch (Exception e) {
                    log.error("invalid dataset type {}", type);
				}
				IDatasetReference dsRef = new DatasetReference(uuid, uri, dsType, null,	DatasetType.EXTERNAL_FILE.equals(dsType) ? FilenameUtils.getName(uri.trim()) : null, origin);
				dsRef.setName(name);
				dsRef.setOriginDatasetUUID(reference.getUuid());
				links.add(dsRef);

				if ("referenceToTechnologyFlowDiagrammOrPicture".equals(dsRef.getOrigin())
						|| "referenceToTechnologyPictogramme".equals(dsRef.getOrigin())
						|| "referenceToDiagram".equals(dsRef.getOrigin())) {
					if (log.isDebugEnabled())
                        log.debug("we've got an image: {}", dsRef.getUuid());
					referencesToSourceWithImages.put(dsRef.getUuid(), dsRef);
				}
			}

            log.debug("checking {} links", links.size());

			boolean skipReferenceObjects = BooleanUtils.isTrue((Boolean) this.validator.parameters.get(LinkValidator.PARAM_IGNORE_REFERENCE_OBJECTS));
			
			for (IDatasetReference ref : links) {
				if (!LinkValidator.this.referenceObjectsCache.contains(ref) && !ref.equals(reference)) {
					
					// if skipReferenceObjects param is set, check whether it's listed there
					if (skipReferenceObjects) {
						if (((LinkValidator) this.validator).referenceElementaryFlows.containsKey(ref.getUuid()) || ((LinkValidator) this.validator).referenceObjectsOther.containsKey(ref.getUuid())) {
							if (log.isDebugEnabled())
                                log.debug("ignoring object found in reference list: {}", ref.getUuid());
							continue;
						}
					}
					
					// check whether we might have an external document
					if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE)) {

						// skip external links under referenceToDigitalFile
						if (ref.getOrigin().equals("referenceToDigitalFile") && (ref.getUri().toLowerCase().startsWith(URL_PREFIX_HTTPS) || ref.getUri().toLowerCase().startsWith(URL_PREFIX_HTTP))) {
							log.debug("skipping external URL given under referenceToDigitalFile: {}", ref.getUri());
							continue;
						}

						// store the reference in a shared map for checking proper file extensions later
						// the key of the map is the UUID of the source dataset containing the reference to the ext. doc
						if (ref.getUuid() != null)
							LinkValidator.this.referencesToDigitalFile.put(reference.getUuid(), ref);
						else if (log.isDebugEnabled())
                            log.debug("UUID is null: {}", ref);

						if (log.isDebugEnabled())
							log.debug("referenceObjectsCache contains key {} for origin {}: {}", ref.getShortFileName(), ref.getOrigin(), LinkValidator.this.referenceObjectsCache.getLinks().containsKey(ref.getShortFileName()));
						if (LinkValidator.this.referenceObjectsCache.getLinks()
								.containsKey(ref.getShortFileName()))
							continue;
					}

					StringBuilder messagePrefix = new StringBuilder(MESSAGE_COULD_NOT_RESOLVE_REFERENCE);
					if (StringUtils.isNotBlank(ref.getOrigin())) {
						messagePrefix.append(ref.getOrigin());
						messagePrefix.append(" ");
					}
					messagePrefix.append(MESSAGE_TO);
					String messageSuffix = "";
					if (StringUtils.isNotBlank(ref.getName()))
						messageSuffix = " (" + ref.getName() + ")";
					
					if (StringUtils.isBlank(ref.getUuid()) && StringUtils.isBlank(ref.getUri())) {
						messagePrefix = new StringBuilder(MESSAGE_INVALID_REFERENCE).append(ref.getDatasetType().getValue());
						messageSuffix = "";
					}

					StringBuilder message = new StringBuilder();
					message.append(" ");
					message.append(ref.getDatasetType().getValue());
					message.append(" ");
					message.append(StringUtils.isNotBlank(ref.getUuid()) ? ref.getUuid() : ref.getUri());

					message.insert(0, messagePrefix);
					message.append(messageSuffix);
					
					IValidationEvent event = new ValidationEvent(LinkValidator.this.getAspectName(), Severity.ERROR, reference, message.toString());
					event.setMessageReference(ref);
					event.setAltMessage(messagePrefix.toString());
					events.add(event);

					if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE)) {
						ref.setName(ref.getShortFileName());
						IValidationEvent caseSensitiveWarning = new ValidationEvent(LinkValidator.this.getAspectName(), Severity.WARNING, reference, MESSAGE_CASE_SENSITIVE_FILE_NAMES);
						caseSensitiveWarning.setMessageReference(ref);
						caseSensitiveWarning.setAltMessage(messagePrefix.toString());
						events.add(caseSensitiveWarning);
					}
				}
			}
		}

		private NodeList extractNodeList(Transformer transformer, IDatasetReference reference)
				throws TransformerException, XPathExpressionException, IOException {
			DOMResult transformResult = new DOMResult();
			
			boolean paramSkipSiam = BooleanUtils.isTrue((Boolean) this.validator.getParameter(PARAM_IGNORE_REFS_TO_LCIAMETHODS));
			if (paramSkipSiam) {
				if (LinkValidator.this.log.isDebugEnabled())
					LinkValidator.this.log.debug("setting skipSupportedImpactAssessmentMethods=true");
				transformer.setParameter(PARAM_IGNORE_REFS_TO_LCIAMETHODS, Boolean.TRUE);
			}

			boolean paramSkipComplementingProcesses = BooleanUtils.isTrue((Boolean) this.validator.getParameter(PARAM_IGNORE_COMPLEMENTINGPROCESS));
			if (paramSkipComplementingProcesses) {
				if (LinkValidator.this.log.isDebugEnabled())
					LinkValidator.this.log.debug("setting skipComplementingProcess=true");
				transformer.setParameter(PARAM_IGNORE_COMPLEMENTINGPROCESS, Boolean.TRUE);
			}

			boolean paramSkipIncludedProcesses = BooleanUtils.isTrue((Boolean) this.validator.getParameter(PARAM_IGNORE_INCLUDEDPROCESSES));
			if (paramSkipIncludedProcesses) {
				if (LinkValidator.this.log.isDebugEnabled())
					LinkValidator.this.log.debug("setting skipIncludedProcesses=true");
				transformer.setParameter(PARAM_IGNORE_INCLUDEDPROCESSES, Boolean.TRUE);
			}

			boolean paramSkipPrecedingDatasetVersion = BooleanUtils.isTrue((Boolean) this.validator.getParameter(PARAM_IGNORE_PRECEDINGDATASETVERSION));
			if (paramSkipPrecedingDatasetVersion) {
				if (LinkValidator.this.log.isDebugEnabled())
					LinkValidator.this.log.debug("setting skipPrecedingDatasetVersion=true");
				transformer.setParameter(PARAM_IGNORE_PRECEDINGDATASETVERSION, Boolean.TRUE);
			}

			transformer.transform(new StreamSource(wrapInputStream(new TFileInputStream(reference.getAbsoluteFileName()))),
					transformResult);

			Document resultDoc = (Document) transformResult.getNode();

			return (NodeList) expr.evaluate(resultDoc, XPathConstants.NODESET);
		}
	}
}
