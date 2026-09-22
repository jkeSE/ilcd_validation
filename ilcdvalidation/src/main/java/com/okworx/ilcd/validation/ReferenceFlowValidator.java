package com.okworx.ilcd.validation;

import java.io.CharConversionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
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

import net.java.truevfs.access.TFileInputStream;

import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.Type;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.reference.DatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.reference.ReferenceCache;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.ILCDNameSpaceContext;
import com.okworx.ilcd.validation.util.PartitionedList;
import com.okworx.ilcd.validation.util.TaskResult;

/**
 * Checks whether the supplied flow references exclusively reference a set of
 * given reference objects.
 *
 * Use the setReferenceObjects() method to supply a HashMap object that contains
 * the reference objects.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ReferenceFlowValidator extends AbstractReferenceObjectsAwareValidator implements IValidator {

	public static final String ASPECT_NAME = "Reference Flows";

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
		return "Checks whether the supplied flow references exclusively reference a set of given reference objects.";
	}

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	/** Constant <code>REFERENCE_OBJECTS="REFERENCE_OBJECTS"</code> */
	@Deprecated
	public static final String REFERENCE_OBJECTS = "REFERENCE_OBJECTS";

	@Deprecated
	protected ReferenceCache referenceObjectsCache;

	private HashSet<IDatasetReference> nonElementaryFlowReferences;

	private boolean noFlowsPresent;

	/**
	 * <p>Constructor for ReferenceFlowValidator.</p>
	 */
	public ReferenceFlowValidator() {
		super();
		this.setProfile(ProfileManager.getInstance().getDefaultProfile());
	}

	// TODO: improve performance by using a Stax implementation
	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 * @throws java.lang.InterruptedException if any.
	 */
	public boolean validate() throws InterruptedException {

		if (super.validate()) {
			log.debug("skipping validation");
			return true;
		}

		updateStatusValidating();

		this.unitsTotal = this.objectsToValidate.size();

		PartitionedList<IDatasetReference> partList = new PartitionedList<>(
                this.objectsToValidate.values());

		DocumentBuilder builder = null;
		XPath xpath = null;
		XPathExpression flowTypeExpr = null;

		this.nonElementaryFlowReferences = new HashSet<>();

		try {
			builder = initDocBuilder();
			xpath = initXPath();
			flowTypeExpr = xpath.compile("/f:flowDataSet/f:modellingAndValidation/f:LCIMethod/f:typeOfDataSet");
		} catch (ParserConfigurationException | XPathExpressionException e) {
			log.error(e);
		}

		this.noFlowsPresent = true;

		// build a list of non-elementary flow datasets
		for (IDatasetReference ref : this.objectsToValidate.values()) {
			if (Thread.currentThread().isInterrupted()) {
				log.info("operation was interrupted, aborting");
				updateStatusCancelled();
				break;
			}

			if (ref.getDatasetType().equals(DatasetType.FLOW)) {
				if (this.noFlowsPresent)
					this.noFlowsPresent = false;
				indexFlow(this.eventsList.getEvents(), nonElementaryFlowReferences, builder, xpath, flowTypeExpr, ref);
			}
		}
		
		if (log.isDebugEnabled())
			log.debug(nonElementaryFlowReferences.size() + " non-elementary flows indexed");

		Collection<Callable<TaskResult>> tasks = new ArrayList<>();

		for (List<IDatasetReference> referencesList : partList.getPartitions()) {
			tasks.add(new Task(referencesList, this));
		}

		ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());

		try {
			List<Future<TaskResult>> results = executor.invokeAll(tasks);
			for (Future<TaskResult> taskResult : results) {
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
			e.printStackTrace();
		}

		log.info(this.eventsList.getEvents().size() + " events");

		updateProgress(1);
		updateStatusDone();

        return this.eventsList.isEmpty();
	}

	private DocumentBuilder initDocBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder();
	}

	private XPath initXPath() {
		// Create XPathFactory object
		XPathFactory xpathFactory = XPathFactory.newInstance();

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new ILCDNameSpaceContext());

		return xpath;
	}

	// builds a list of non-elementary
	private void indexFlow(List<IValidationEvent> results, Set<IDatasetReference> flowReferences,
			DocumentBuilder builder, XPath xpath, XPathExpression flowTypeExpr, IDatasetReference ref) {

		Document doc;

		if (log.isTraceEnabled())
			log.trace("indexing flow " + ref.getShortFileName());

		doc = parseDocument(ref, builder, results);

		try {
			String flowType = (String) flowTypeExpr.evaluate(doc, XPathConstants.STRING);

			// we're only adding a plain reference consisting of uuid and type, to guarantee
			// a match against any possible references in the process dataset
			if (!flowType.equals("Elementary flow") )
				flowReferences.add(new DatasetReference(ref.getUuid(), ref.getDatasetType()));
		} catch (Exception e) {
			log.error(e);
		}
	}

	final class Task extends AbstractDatasetsTask implements Callable<TaskResult> {

		private static final String XPATH_REFERENCE_TO_REFERENCE_FLOW = "/p:processDataSet/p:exchanges/p:exchange[@dataSetInternalID=/p:processDataSet/p:processInformation/p:quantitativeReference/p:referenceToReferenceFlow]/p:referenceToFlowDataSet/@refObjectId";
		private static final String XPATH_FLOW_REFERENCES = "/p:processDataSet/p:exchanges/p:exchange/p:referenceToFlowDataSet/@refObjectId";
		private static final String XPATH_PROCESS_TYPE = "/p:processDataSet/p:modellingAndValidation/p:LCIMethodAndAllocation/p:typeOfDataSet";
		private static final String PROCESS_TYPE_LCI_RESULT = "LCI result";
		private static final String MESSAGE_NO_FLOWS = "The objects to be checked do not contain any flows, so any non-compliant flows that are detected could as well be product or waste flows. Always include product and waste flows when checking non-aggregated processes.";
		private static final String MESSAGE_NO_FLOWS_MULTILINE = "The objects to be checked do not contain any flows, so any non-compliant flows \nthat are detected could as well be product or waste flows. \nAlways include product and waste flows when checking non-aggregated processes.";
		private static final String MESSAGE_COLON = ": ";
		private static final String MESSAGE_SPACE = " ";
		private static final String MESSAGE_NOT_PART_OF_REF_SYSTEM = "referenced flow is not part of the reference system";
		
		private final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

		Task(List<IDatasetReference> references, AbstractDatasetsValidator validator) {
			this.references = references;
			this.validator = validator;
		}

		public TaskResult call() throws Exception {
			return new TaskResult(check(this.references), this.statistics);
		}

		private final List<IDatasetReference> references;

		private boolean noFlowsMessageIssued = false;
		
		private List<IValidationEvent> check(List<IDatasetReference> references) {

			List<IValidationEvent> results = new ArrayList<>();

			DocumentBuilder builder;
			XPathExpression referencesExpr;
			XPathExpression refFlowExpr;
			XPath xpath;

			try {
				builder = initDocBuilder();
				xpath = initXPath();
				// TODO make sure the file we're reading is an ILCD dataset
				referencesExpr = xpath.compile(XPATH_FLOW_REFERENCES);
				refFlowExpr = xpath.compile(XPATH_REFERENCE_TO_REFERENCE_FLOW);
			} catch (XPathExpressionException | ParserConfigurationException e) {
				log.error(e);
				return null;
			}

            int count = 0;

			for (IDatasetReference ref : references) {
				if (Thread.currentThread().isInterrupted()) {
					log.info("operation was interrupted, aborting");
					updateStatusCancelled();
					break;
				}

				// check only process datasets
				if (ref.getDatasetType().equals(DatasetType.PROCESS)) {
					int eventCount = results.size();
					
					checkReference(results, builder, xpath, referencesExpr, refFlowExpr, ref,
							nonElementaryFlowReferences);
					
					boolean success = (eventCount == results.size());
					this.statistics.update(ref, success);
					
					if (success && ReferenceFlowValidator.this.reportSuccesses)
						results.add(new ValidationEvent(ReferenceFlowValidator.this.getAspectName(), Severity.SUCCESS, ref, ValidationEvent.SUCCESS_MESSAGE));

				} else
					this.statistics.update(ref, true);
				
				count = updateChunkCount(count);

			}

			return results;
		}

		private void checkReference(List<IValidationEvent> results, DocumentBuilder builder, XPath xpath,
				XPathExpression expr, XPathExpression refFlowExpr, IDatasetReference ref,
				Set<IDatasetReference> nonElementaryFlowReferences) {

			Document doc;

			if (log.isDebugEnabled())
				log.debug("parsing process " + ref.getShortFileName());

			doc = parseDocument(ref, builder, results);

			try {	
				// determine type of process
				String processTypeXPath = XPATH_PROCESS_TYPE;
				XPathExpression processTypeExpr = xpath.compile(processTypeXPath);
				String processType = processTypeExpr.evaluate(doc);

				// evaluate expression result on XML document, yielding all flow
				// references
				List<IDatasetReference> flowRefs = evaluateXPathExpression(doc, expr);

				// but we want to exempt the reference flow(s)
				List<IDatasetReference> referenceFlows = evaluateXPathExpression(doc, refFlowExpr);

                Set<IDatasetReference> referenceFlowsSet = new HashSet<>(referenceFlows);

				// for each flow that is not a reference flow AND not a
				// non-elementary flow, check whether it
				// is part of the reference system
				// if not, generate a validation event
				for (IDatasetReference flowRef : flowRefs) {

					// if it's not a non-elementary flow, skip it
					if (nonElementaryFlowReferences.contains(flowRef))
						continue;

					if (!referenceFlows.contains(flowRef)) {						
						checkFlowReference(doc, xpath, results, ref, flowRef, processType);
					}
				}
			} catch (XPathExpressionException | DOMException e) {
				log.error(e);
			}
        }

		private void checkFlowReference(Document doc, XPath xpath, List<IValidationEvent> results, IDatasetReference ref,
				IDatasetReference flowRef, String processType) {
			if (flowRef.getUuid() == null)
				return;

			if (log.isDebugEnabled())
				log.debug("checking reference to flow " + flowRef.getUuid());
			if (!ReferenceFlowValidator.this.referenceElementaryFlows.containsKey(flowRef.getUuid())) {
				log.debug("mismatch, generating validation event");
				
				// TODO this is a very expensive operation, could be
				// improved by doing all the
				// checking on IDatasetReference objects (that include
				// the name) instead of Strings
				String flowNameXPath = "/p:processDataSet/p:exchanges/p:exchange/p:referenceToFlowDataSet[@refObjectId='"
						.concat(flowRef.getUuid()).concat("']/common:shortDescription");
				XPathExpression flowNameExpr;
				String flowRefName = "";
				try {
					flowNameExpr = xpath.compile(flowNameXPath);
					flowRefName = flowNameExpr.evaluate(doc);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
				flowRef.setName(flowRefName);

				// if no flows are contained in the objects to validate and
				// we're checking a non-aggregated process, issue a warning
				// because we can't tell if non-compliant flows may be product flows
				if (ReferenceFlowValidator.this.noFlowsPresent && !processType.equals(PROCESS_TYPE_LCI_RESULT) && !noFlowsMessageIssued) {
					IValidationEvent event = new ValidationEvent(getAspectName());
					event.setSeverity(Severity.WARNING);
					event.setType(Type.SPECIFIC);
					event.setReference(ref);
					event.setMessage(MESSAGE_NO_FLOWS);
					event.setAltMessage(MESSAGE_NO_FLOWS_MULTILINE);
					results.add(event);
					noFlowsMessageIssued = true;
				}

				IValidationEvent event = new ValidationEvent(getAspectName());
				event.setSeverity(Severity.ERROR);
				event.setType(Type.SPECIFIC);
				event.setReference(ref);
				event.setMessage(MESSAGE_NOT_PART_OF_REF_SYSTEM.concat(MESSAGE_COLON).concat(flowRef.getUuid()).concat(MESSAGE_SPACE).concat(flowRefName));
				event.setMessageReference(flowRef);
				event.setAltMessage(MESSAGE_NOT_PART_OF_REF_SYSTEM);
				results.add(event);
			} else {
				if (log.isDebugEnabled())
					log.debug("ok");
			}
		}
	}

	/**
	 * Parses the XML document represented by an IDatasetReference object
	 *
	 * @param ref a {@link com.okworx.ilcd.validation.reference.IDatasetReference} object.
	 * @param builder a {@link javax.xml.parsers.DocumentBuilder} object.
	 * @param results a {@link java.util.List} object.
	 * @return the Document object
	 */
	protected Document parseDocument(IDatasetReference ref, DocumentBuilder builder, List<IValidationEvent> results) {
		Document doc = null;
		try {
			doc = builder.parse(wrapInputStream(new TFileInputStream(ref.getAbsoluteFileName())));
		} catch (SAXException | CharConversionException e) {
			log.error(e);
			IValidationEvent event = new ValidationEvent(getAspectName());
			event.setSeverity(Severity.ERROR);
			event.setType(Type.SPECIFIC);
			event.setReference(ref);
			event.setMessage("malformed XML file, nested cause is ".concat(e.getMessage()));
			results.add(event);
			return doc;
		} catch (IOException e) {
			log.error(e);
			IValidationEvent event = new ValidationEvent(getAspectName());
			event.setSeverity(Severity.ERROR);
			event.setType(Type.SPECIFIC);
			event.setReference(ref);
			event.setMessage("error reading file, nested cause is ".concat(e.getMessage()));
			results.add(event);
			return doc;
		}
		return doc;
	}

	/**
	 * Evaluates an XPath expression on a Document that yields a nodeset of
	 * UUIDs, which will be returned as List<IDatasetReference>
	 * 
	 * @param doc
	 * @param expr
	 * @return
	 */
	private List<IDatasetReference> evaluateXPathExpression(Document doc, XPathExpression expr) {

		List<IDatasetReference> references = new ArrayList<>();

		NodeList results;
		try {
			results = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < results.getLength(); i++) {
				String uuid = results.item(i).getNodeValue();
				DatasetReference ref = new DatasetReference(uuid, DatasetType.FLOW);
				references.add(ref);
			}
		} catch (XPathExpressionException e) {
			log.error(e);
		}

		return references;

	}
}
