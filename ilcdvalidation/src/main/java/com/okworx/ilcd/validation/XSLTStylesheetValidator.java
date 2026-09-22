package com.okworx.ilcd.validation;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.NullOutputStream;

import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.CURIResolver;
import com.okworx.ilcd.validation.util.PartitionedList;
import com.okworx.ilcd.validation.util.PrefixBuilder;
import com.okworx.ilcd.validation.util.TaskResult;
import com.okworx.ilcd.validation.util.ValidatorListener;

import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileInputStream;

/**
 * Validates a set of given datasets against an XSLT Stylesheet.
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class XSLTStylesheetValidator extends AbstractReferenceObjectsAwareValidator implements IValidator {

	public static final String ASPECT_NAME = "Profile specific rules";

	private String aspectName = ASPECT_NAME;

	private String aspectDescription = "Checks against a custom XSLT stylesheet.";

	protected Profile.Bundle<String>[] stylesheetBundles = null;

	protected Map<String, Object> transformationParameters = new HashMap<>();

	protected Map<String, String> resolverMappings = new HashMap<>();

	/**
	 * <p>setTransformationParameter.</p>
	 *
	 * @param string a {@link java.lang.String} object.
	 * @param object a {@link java.lang.Object} object.
	 */
	public void setTransformationParameter(String string, Object object) {
		this.transformationParameters.put(string, object);
	}

	/**
	 * <p>Constructor for XSLTStylesheetValidator.</p>
	 */
	public XSLTStylesheetValidator() {
		super();
		this.setProfile(ProfileManager.getInstance().getDefaultProfile());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAspectName() {
		return this.aspectName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAspectDescription() {
		return this.aspectDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProfile(Profile profile) {
		super.setProfile(profile);
		if (profile != null) {
			this.stylesheetBundles = profile.getStylesheetBundles();
			this.aspectDescription = profile.getDescription();
		}
	}

	/**
	 * <p>registerStylesheet.</p>
	 *
	 * @param pathToJar      a {@link java.lang.String} object.
	 * @param stylesheetsDir a {@link java.lang.String} object.
	 * @param stylesheetName a {@link java.lang.String} object.
	 */
	public void registerStylesheet(String pathToJar, String stylesheetsDir, String stylesheetName) {
		this.stylesheetBundles = new Profile.Bundle[]{new Profile.Bundle<>(pathToJar, stylesheetsDir, stylesheetName)};
	}

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean indicating whether validation succeeded or not
	 * @throws java.lang.InterruptedException if any.
	 */
	public boolean validate() throws InterruptedException {

		updateStatusSetup();

		super.validate();

		if (this.stylesheetBundles == null)
			throw new IllegalArgumentException("no stylesheet bundle");

		this.unitsTotal = this.objectsToValidate.size();

		PartitionedList<IDatasetReference> partList = new PartitionedList<>(
				this.objectsToValidate.values());

		Collection<Callable<TaskResult>> tasks = new ArrayList<>();

		for (List<IDatasetReference> refList : partList.getPartitions()) {
			for (Profile.Bundle<String> bundle : stylesheetBundles) {
				try {
					String absoluteUrlPrefix = PrefixBuilder.buildPrefix(bundle.getJarPath(), bundle.getUrlPrefix());
					tasks.add(new ValidateTask(this, refList, bundle.getDescription(), absoluteUrlPrefix, bundle.getResource(), this.resolverMappings));
				} catch (TransformerConfigurationException e) {
					raiseApplicationError("error configuring transformer", e);
					return false;
				} catch (TransformerFactoryConfigurationError e) {
					raiseApplicationError("error configuring transformer factory", e);
					return false;
				}
			}
		}

		ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());

		updateStatusValidating();

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
			raiseApplicationError("an error ocurred during processing", e);
			return false;
		}

		log.info(this.eventsList.getEvents().size() + " events ");

		updateProgress(1);
		updateStatusDone();

		return this.getEventsList().isPositive();

	}

	private javax.xml.transform.Transformer setupTransformer(ValidatorListener listener, String urlPrefix, String stylesheetName)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException {
		TransformerFactory tFactory = TransformerFactory.newInstance();

//		CURIResolver resolver = new CURIResolver(this.urlPrefix);

//		for (String key : this.resolverMappings.keySet())
//			resolver.registerMapping(key, this.resolverMappings.get(key));

//		tFactory.setURIResolver(resolver);

		javax.xml.transform.Transformer transformer = tFactory.newTransformer(new StreamSource(urlPrefix + stylesheetName));

		transformer.setErrorListener(listener);

		for (String key : this.transformationParameters.keySet()) {
			if (log.isDebugEnabled())
				log.debug("setting transformer variable '" + key + "' to " + this.transformationParameters.get(key));
			transformer.setParameter(key, this.transformationParameters.get(key));
		}

		return transformer;
	}

	/**
	 * <p>Setter for the field <code>aspectName</code>.</p>
	 *
	 * @param aspectName a {@link java.lang.String} object.
	 */
	public void setAspectName(String aspectName) {
		this.aspectName = aspectName;
		this.eventsList.setAspectName(aspectName);
	}

	final class ValidateTask extends AbstractDatasetsTask implements Callable<TaskResult> {

		private final String description;
		private final String urlPrefix;
		private final ValidatorListener vListener;
		private final Transformer transformer;
		private final Map<String, String> resolverMappings;

		ValidateTask(AbstractDatasetsValidator validator, Collection<IDatasetReference> files, String description, String urlPrefix, String stylesheetName, Map<String, String> resolverMappings) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
			this.files = files;
			this.description = description;
			this.validator = validator;
			this.urlPrefix = urlPrefix;
			this.resolverMappings = resolverMappings;
			this.vListener = new ValidatorListener();
			this.transformer = setupTransformer(this.vListener, urlPrefix, stylesheetName);
		}

		public TaskResult call() throws Exception {
			return new TaskResult(validate(files), this.statistics);
		}

		private Collection<IValidationEvent> validate(Collection<IDatasetReference> files) throws Exception {

			InputStream is;

			Collection<IValidationEvent> events = new ArrayList<>();

			int count = 0;

			for (IDatasetReference ref : files) {
				if (Thread.currentThread().isInterrupted()) {
					log.info("operation was interrupted, aborting");
					updateStatusCancelled();
					throw new InterruptedException();
				}

				if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE)) {
					this.statistics.update(ref, true);
					continue;
				}

				is = wrapInputStream(new TFileInputStream(ref.getAbsoluteFileName()));
				try {
					String path = new TFile(ref.getAbsoluteFileName()).getParent().concat(File.separator);
					transformer.setURIResolver(new CURIResolver(this.urlPrefix, path, this.resolverMappings));
					transformer.setParameter("pathPrefix", URLEncoder.encode(path, "UTF-8"));
					transformer.transform(new StreamSource(is), new StreamResult(NullOutputStream.NULL_OUTPUT_STREAM));
				} catch (Exception e) {
					String message = e.getMessage().replaceAll("com.sun.org.apache.xalan.internal.xsltc.TransletException:", "").replaceAll("java.io.FileNotFoundException", "File not found");
					Severity severity = (message.trim().startsWith("File not found") ? Severity.WARNING : Severity.ERROR);
					events.add(new ValidationEvent(XSLTStylesheetValidator.this.getAspectName(), this.description, severity, ref, message));
				}

				boolean success = (vListener.getErrors().isEmpty());
				this.statistics.update(ref, success);

				if (success && XSLTStylesheetValidator.this.reportSuccesses)
					events.add(new ValidationEvent(XSLTStylesheetValidator.this.getAspectName(), this.description, Severity.SUCCESS, ref, ValidationEvent.SUCCESS_MESSAGE));

				count = updateChunkCount(count);

				if (log.isDebugEnabled())
					log.debug(ref.getUuid() + ": " + vListener.getResults().size() + " events occurred");
				// extract message
				for (String rawMessage : vListener.getResults()) {
					if (rawMessage.startsWith(ValidatorListener.VALIDATION_ERROR_PREFIX)) {
						rawMessage = rawMessage.split(ValidatorListener.VALIDATION_ERROR_PREFIX)[1];
						events.add(new ValidationEvent(XSLTStylesheetValidator.this.getAspectName(), this.description, Severity.ERROR, ref, rawMessage));
					} else if (rawMessage.startsWith(ValidatorListener.VALIDATION_WARNING_PREFIX)) {
						rawMessage = rawMessage.split(ValidatorListener.VALIDATION_WARNING_PREFIX)[1];
						events.add(new ValidationEvent(XSLTStylesheetValidator.this.getAspectName(), this.description, Severity.WARNING, ref, rawMessage));
					}
				}
				vListener.getResults().clear();
			}
			return events;

		}
	}

}
