package com.okworx.ilcd.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.reference.DatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.ILCDNameSpaceContext;
import com.okworx.ilcd.validation.util.PartitionedList;
import com.okworx.ilcd.validation.util.TransletCache;

/**
 * Checks a set of datasets for objects that are not referenced by others.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class OrphansValidator extends AbstractReferenceObjectsAwareValidator implements IValidator {

	public static final String ASPECT_NAME = "Orphaned Items";

	private static final String MESSAGE_DATASET_NOT_REFERENCED = "dataset is not referenced anywhere";

	/** Constant <code>PARAM_IGNORE_LCIAMETHODS="ignoreLCIAMethods"</code> */
	public static final String PARAM_IGNORE_LCIAMETHODS = "ignoreLCIAMethods";

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
		return "Checks a set of datasets for objects that are not referenced by any others.";
	}

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 * @throws java.lang.InterruptedException if any.
	 */
	public boolean validate() throws InterruptedException {
		super.validate();

		boolean result = true;

		updateStatusValidating();

		this.unitsTotal = this.objectsToValidate.size();

		try {

			XPathExpression expr = setupXpathExpr();

			PartitionedList<IDatasetReference> partList = new PartitionedList<IDatasetReference>(
					this.objectsToValidate.values());

			Collection<Callable<List<IDatasetReference>>> tasks = new ArrayList<Callable<List<IDatasetReference>>>();

			for (List<IDatasetReference> refList : partList.getPartitions()) {
				tasks.add(new LinkExtractorTask(refList, TransletCache.getInstance().getTranslet(), expr, this));
			}

			ConcurrentLinkedQueue<IDatasetReference> links = new ConcurrentLinkedQueue<IDatasetReference>();

			ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());

			try {
				List<Future<List<IDatasetReference>>> taskResults = executor.invokeAll(tasks);
				for (Future<List<IDatasetReference>> taskResult : taskResults) {
					if (taskResult.get() != null)
						links.addAll(taskResult.get());
				}
				executor.shutdown();
			} catch (InterruptedException e) {
				executor.shutdown();
				interrupted(e);
			} catch (Exception e) {
				log.error(e);
			}

			if (log.isDebugEnabled())
				log.debug("checking " + links.size() + " links");

			ConcurrentMap<String, IDatasetReference> newMap = new ConcurrentHashMap<String, IDatasetReference>(
					this.objectsToValidate);

			removeLinkedItems(links, newMap);

			removeTopLevelObjects(newMap, DatasetType.PROCESS);
			removeTopLevelObjects(newMap, DatasetType.LCMODEL);
			
			if (BooleanUtils.isTrue((Boolean) this.getParameter(PARAM_IGNORE_REFERENCE_OBJECTS)))
				removeReferenceObjects(newMap);
			
			if (BooleanUtils.isTrue((Boolean) this.getParameter(PARAM_IGNORE_LCIAMETHODS)))
				removeTopLevelObjects(newMap, DatasetType.LCIAMETHOD);

			for (IDatasetReference ref : this.objectsToValidate.values()) {
				boolean success = (!newMap.containsValue(ref));

				this.statistics.update(ref, success);
				
				if (success && OrphansValidator.this.reportSuccesses)
					this.eventsList.add(new ValidationEvent(OrphansValidator.this.getAspectName(), Severity.SUCCESS, ref, ValidationEvent.SUCCESS_MESSAGE));

			}
			
			// also count external files for statistics
			for (IDatasetReference ref : newMap.values()) {
				if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE))
					this.statistics.update(ref, false);
			}
			
			updateProgress(1);
			updateStatusDone();

			// if any are left, these are the orphans
			if (newMap.isEmpty())
				return true;
			
			for (IDatasetReference ref : newMap.values()) {
				this.eventsList.add(new ValidationEvent(OrphansValidator.this.getAspectName(), Severity.ERROR, ref,	MESSAGE_DATASET_NOT_REFERENCED));
			}
			log.info(newMap.size() + " events");

			return false;

		}

		catch (Exception e) {
			log.error(e);
		}

		return result;
	}

	private XPathExpression setupXpathExpr() throws XPathExpressionException {
		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(new ILCDNameSpaceContext());
		XPathExpression expr = xpath.compile("/*/*");
		return expr;
	}

	private void removeTopLevelObjects(ConcurrentMap<String, IDatasetReference> newMap, DatasetType type) {
		// now remove all top level datasets (process datasets) from the map
		for (IDatasetReference ref : newMap.values()) {
			if (ref.getDatasetType().equals(type)) {
				newMap.remove(ref.getUuid());
			}
		}
	}

	private void removeReferenceObjects(ConcurrentMap<String, IDatasetReference> newMap) {
		newMap.keySet().removeAll(referenceObjectsOther.keySet());
		newMap.keySet().removeAll(referenceElementaryFlows.keySet());
	}
	
	private void removeLinkedItems(ConcurrentLinkedQueue<IDatasetReference> links,
			ConcurrentMap<String, IDatasetReference> newMap) {
		// remove all items that are linked from the map
		for (IDatasetReference ref : links) {
			if (newMap.containsKey(ref.getUuid())) {
				newMap.remove(ref.getUuid());
			} else if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE) && newMap.containsKey(ref.getShortFileName())) {
				newMap.remove(ref.getShortFileName());
			}
		}
	}

	final class LinkExtractorTask extends AbstractDatasetsTask implements Callable<List<IDatasetReference>> {

		private XPathExpression expr;

		private Templates templates;

		LinkExtractorTask(List<IDatasetReference> files, Templates templates, XPathExpression expr,
				AbstractDatasetsValidator validator) {
			this.files = files;
			this.expr = expr;
			this.templates = templates;
			this.validator = validator;
		}

		public List<IDatasetReference> call() throws Exception {
			return extract(files);
		}

		private List<IDatasetReference> extract(Collection<IDatasetReference> files) throws Exception {

			if (log.isDebugEnabled())
				log.debug("extracting links from " + files.size() + " objects");

			Transformer transformer = templates.newTransformer();

			List<IDatasetReference> links = new ArrayList<IDatasetReference>();

			int count = 0;

			for (IDatasetReference reference : files) {
				if (Thread.currentThread().isInterrupted()) {
					log.info("operation was interrupted, aborting");
					updateStatusCancelled();
					break;
				}

				if (log.isTraceEnabled()) {
					log.trace("extracting links from " + reference.getAbsoluteFileName());
				}

				if (!reference.getDatasetType().equals(DatasetType.EXTERNAL_FILE))
					checkReference(transformer, links, reference);

				count = updateChunkCount(count);

			}

			if (log.isDebugEnabled())
				log.debug("returning " + links.size() + " links");

			return links;
		}

		private void checkReference(Transformer transformer, List<IDatasetReference> links, IDatasetReference reference)
				throws TransformerException, XPathExpressionException, IOException {
			DOMResult transformResult = new DOMResult();

			transformer.transform(new StreamSource(wrapInputStream(new TFileInputStream(reference.getAbsoluteFileName()))),
					transformResult);

			Document resultDoc = (Document) transformResult.getNode();

			NodeList refs = (NodeList) expr.evaluate(resultDoc, XPathConstants.NODESET);

			for (int i = 0; i < refs.getLength(); i++) {
				Node ref = refs.item(i);
				String uuid = ref.getAttributes().getNamedItem(ATTRIBUTE_REF_OBJECT_ID).getNodeValue();
				String uri = ref.getAttributes().getNamedItem(ATTRIBUTE_URI).getNodeValue();

				// skip remote links
				if (StringUtils.startsWithIgnoreCase(uri.trim(), URL_PREFIX_HTTP) || StringUtils.startsWithIgnoreCase(uri.trim(), URL_PREFIX_HTTPS))
					continue;

				String type = ref.getAttributes().getNamedItem(ATTRIBUTE_TYPE).getNodeValue();

				DatasetType dsType = null;
				try {
					dsType = DatasetType.fromValue(type);
				} catch (Exception e) {
					log.error("invalid dataset type " + type);
				}

				links.add(new DatasetReference(uuid, uri, dsType, null,
						dsType.equals(DatasetType.EXTERNAL_FILE) ? FilenameUtils.getName(uri.trim()) : null));

			}
		}
	}
}
