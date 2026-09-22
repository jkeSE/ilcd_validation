package com.okworx.ilcd.validation;

import com.okworx.ilcd.validation.common.Constants;
import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.profile.Profile;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.*;
import net.java.truevfs.access.TFileInputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Validates a set of given datasets against the XML Schemas.
 *
 * @author oliver.kusche
 */
public class SchemaValidator extends AbstractReferenceObjectsAwareValidator implements IValidator {

    public static final String ASPECT_NAME = "XML Schema Validity";

    /** Constant <code>SCHEMAS="SCHEMAS"</code> */
    public static final String SCHEMAS = "SCHEMAS";

    /** Constant <code>PREFIX="PREFIX"</code> */
    public static final String PREFIX = "PREFIX";

//	private String[] schemas = null;
//
//	private String urlPrefix = null;

    private Profile.Bundle<String[]>[] schemaBundles = null;

    private String aspectName = ASPECT_NAME;

    private final String aspectDescription = "Checks against the supplied XML Schema documents";

    /**
     * Default constructor. By default, the default profile will be set (if available).
     */
    public SchemaValidator() {
        super();
        this.setProfile(ProfileManager.getInstance().getDefaultProfile());
    }

    /** {@inheritDoc} */
    @Override
    public String getAspectName() {
        return this.aspectName;
    }

    /**
     * <p>Getter for the field <code>aspectDescription</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAspectDescription() {
        return aspectDescription;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate() throws InterruptedException {

        super.validate();

        updateStatusValidating();

//		if (this.schemas == null)
//			throw new IllegalArgumentException("no schemas registered");
//		if (this.urlPrefix == null)
//			throw new IllegalArgumentException("no schemas location registered");

        if (this.schemaBundles == null)
            throw new IllegalArgumentException("no schemas registered");

        this.unitsTotal = this.objectsToValidate.size();

        PartitionedList<IDatasetReference> partList = new PartitionedList<>(
                this.objectsToValidate.values());

        Collection<Callable<TaskResult>> tasks = new ArrayList<>();

        for (List<IDatasetReference> refList : partList.getPartitions()) {
            for (Profile.Bundle<String[]> bundle : this.schemaBundles) {
                tasks.add(new ValidateTask(this, refList, bundle.getResource(), PrefixBuilder.buildPrefix(bundle.getJarPath(), bundle.getUrlPrefix())));
            }
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

        log.info(this.eventsList.getEvents().size() + " events ");

        updateProgress(1);
        updateStatusDone();

        return this.getEventsList().isPositive();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.okworx.ilcd.validation.AbstractDatasetsValidator#setProfile(com.okworx
     * .ilcd.validation.profile.Profile)
     */

    /** {@inheritDoc} */
    @Override
    public void setProfile(Profile profile) {
        super.setProfile(profile);
        if (profile != null) {
            this.schemaBundles = profile.getSchemaBundles();
        }
    }

    /**
     * register XML schema documents with the validator
     *
     * @param pathToJar
     *            path to the Jar file containing the schemas
     * @param schemasDir
     *            folder insider the Jar where the schemas are contained
     * @param schemasDir
     *            folder insider the Jar where the schemas are contained
     * @param schemas
     *            the names of the XML schema documents
     */
    public void registerSchemas(String pathToJar, String schemasDir, String[] schemas) {
        this.schemaBundles = new Profile.Bundle[]{new Profile.Bundle(pathToJar, schemasDir, schemas)};
    }

    /**
     * <p>registerDefaultSchemas.</p>
     */
    public void registerDefaultSchemas() {
        String[] schemas = new String[8];
        schemas[0] = Constants.PROCESS_SCHEMA_NAME;
        schemas[1] = Constants.FLOW_SCHEMA_NAME;
        schemas[2] = Constants.FLOW_PROPERTY_SCHEMA_NAME;
        schemas[3] = Constants.UNIT_GROUP_SCHEMA_NAME;
        schemas[4] = Constants.SOURCE_SCHEMA_NAME;
        schemas[5] = Constants.CONTACT_SCHEMA_NAME;
        schemas[6] = Constants.LCIAMETHOD_SCHEMA_NAME;
        schemas[7] = Constants.LCMODEL_SCHEMA_NAME;

        this.registerSchemas(this.getClass().getClassLoader().getResource(Constants.DEFAULT_PROFILE_JAR).getPath(),
                Constants.ILCD_SCHEMAS_PATH_PREFIX, schemas);

        this.setAspectName("ILCD XML Schemas Validity");
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

        private final String[] schemas;
        private final String urlPrefix;

        ValidateTask(AbstractDatasetsValidator validator, Collection<IDatasetReference> files, String[] schemas,
                     String urlPrefix) {
            this.files = files;
            this.schemas = schemas;
            this.validator = validator;
            this.urlPrefix = urlPrefix;
            this.resolver = new ResourceResolver(this.urlPrefix);
        }

        public TaskResult call() throws Exception {
            return new TaskResult(validate(files), this.statistics);
        }

        private final ResourceResolver resolver;

        private Collection<IValidationEvent> validate(Collection<IDatasetReference> files) throws Exception {

            SchemaErrorHandler errorHandler = new SchemaErrorHandler(SchemaValidator.this);
            Validator validator = setupValidator(errorHandler);

            int count = 0;

            for (IDatasetReference ref : files) {
                if (Thread.currentThread().isInterrupted()) {
                    log.info("operation was interrupted, aborting");
                    updateStatusCancelled();
                    throw new InterruptedException();
                    // break;
                }

                if (ref.getDatasetType().equals(DatasetType.EXTERNAL_FILE)) {
                    this.statistics.update(ref, true);
                    continue;
                }

                int eventCount = errorHandler.getEvents().size();

                Source xmlFile = new StreamSource(wrapInputStream(new TFileInputStream(ref.getAbsoluteFileName())));
                errorHandler.setReference(ref);
                validator.validate(xmlFile);

                boolean success = (eventCount == errorHandler.getEvents().size());
                this.statistics.update(ref, success);

                if (success && SchemaValidator.this.reportSuccesses)
                    errorHandler.getEvents().add(new ValidationEvent(SchemaValidator.this.getAspectName(), Severity.SUCCESS, ref, ValidationEvent.SUCCESS_MESSAGE));

                count = updateChunkCount(count);

            }

            return errorHandler.getEvents();
        }

        private Validator setupValidator(SchemaErrorHandler errorHandler) throws Exception {
            Locale.setDefault(Locale.UK);
            Schema schema = setupSchema();
            Validator validator = schema.newValidator();
            validator.setResourceResolver(resolver);
            validator.setErrorHandler(errorHandler);
            return validator;
        }

        private Schema setupSchema() throws Exception {

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(this.resolver);

            StreamSource[] sources = new StreamSource[this.schemas.length];

            if (SchemaValidator.this.log.isTraceEnabled())
                SchemaValidator.this.log.trace("setting up " + this.schemas.length + " schemas");

            for (int i = 0; i < this.schemas.length; i++) {
                if (SchemaValidator.this.log.isTraceEnabled())
                    SchemaValidator.this.log.trace("setting up schema " + this.schemas[i]);
                sources[i] = new StreamSource(this.urlPrefix + this.schemas[i]);
            }
            return schemaFactory.newSchema(sources);
        }
    }

    /**
     * <p>Getter for the field <code>urlPrefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
//	public String getUrlPrefix() {
//		return urlPrefix;
//	}

}
