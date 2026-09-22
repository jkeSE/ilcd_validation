package com.okworx.ilcd.validation.analyze;

import com.okworx.ilcd.validation.AbstractDatasetsValidator;
import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.*;
import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

// this can be added to a ValidatorChain in order to generate a datasets summary sheet in the batch mode report
public class SummaryExtractor extends AbstractDatasetsValidator {
    @Override
    public String getAspectName() {
        return "Summary";
    }

    @Override
    public String getAspectDescription() {
        return "Metadata summary";
    }

    protected Collection<SummaryDTO> summaryProcesses = new ConcurrentLinkedQueue<>();

    protected Collection<SummaryDTO> summaryLCModels = new ConcurrentLinkedQueue<>();

    @Override
    public boolean validate() throws InterruptedException {
        super.validate();

        updateStatusValidating();

        this.unitsTotal = this.objectsToValidate.size();

        PartitionedList<IDatasetReference> partList = new PartitionedList<>(
                this.objectsToValidate.values());

        Collection<Callable<TaskResult>> tasks = new ArrayList<>();

        for (List<IDatasetReference> refList : partList.getPartitions()) {
            tasks.add(new ExtractTask(this, refList));
        }

        ExecutorService executor = Executors.newFixedThreadPool(partList.getNumThreads());

        try {
            List<Future<TaskResult>> taskResults = executor.invokeAll(tasks);
            for (Future<TaskResult> taskResult : taskResults) {
                if (taskResult.get() != null) {
                    taskResult.get();
                }
            }
            executor.shutdown();
        } catch (InterruptedException e) {
            executor.shutdown();
            interrupted(e);
        } catch (Exception e) {
            log.error(e);
        }

        updateProgress(1);
        updateStatusDone();

        return true;
    }

    public Collection<SummaryDTO> getSummaryProcesses() {
        return summaryProcesses;
    }

    public Collection<SummaryDTO> getSummaryLCModels() {
        return summaryLCModels;
    }

    final class ExtractTask extends AbstractDatasetsTask implements Callable<TaskResult> {

        ExtractTask(AbstractDatasetsValidator validator, Collection<IDatasetReference> files) {
            this.files = files;
            this.validator = validator;
        }

        public TaskResult call() throws Exception {
            return new TaskResult(extract(this.files), this.statistics);
        }

        // extracts metadata for the summary from the process datasets
        private Collection<IValidationEvent> extract(Collection<IDatasetReference> files) throws Exception {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(new ILCDNameSpaceContext());

            // process
            XPathExpression xpProcessDataproviderName = xpath.compile("/p:processDataSet/p:administrativeInformation/p:publicationAndOwnership/common:referenceToOwnershipOfDataSet/common:shortDescription[@xml:lang='en' or not(@xml:lang)]");
//            XPathExpression xpProcessDataproviderUUID = xpath.compile("/p:processDataSet/p:administrativeInformation/p:publicationAndOwnership/common:referenceToOwnershipOfDataSet/@refObjectId");
            XPathExpression xpProcessGeoCode = xpath.compile("/p:processDataSet/p:processInformation/p:geography/p:locationOfOperationSupplyOrProduction/@location");
            XPathExpression xpProcessProcessType = xpath.compile("/p:processDataSet/p:modellingAndValidation/p:LCIMethodAndAllocation/p:typeOfDataSet");
            XPathExpression xpProcessDataCutOffAndCompletenessPrinciples = xpath.compile("/p:processDataSet/p:modellingAndValidation/p:dataSourcesTreatmentAndRepresentativeness/p:dataCutOffAndCompletenessPrinciples[@xml:lang='en' or not(@xml:lang)]");
            XPathExpression xpProcessDeviationsFromLCIMethodApproaches = xpath.compile("/p:processDataSet/p:modellingAndValidation/p:LCIMethodAndAllocation/p:deviationsFromLCIMethodApproaches[@xml:lang='en' or not(@xml:lang)]");

            // lc model
            XPathExpression xpLCModelDataproviderName = xpath.compile("/lcm:lifeCycleModelDataSet/lcm:administrativeInformation/lcm:publicationAndOwnership/common:referenceToOwnershipOfDataSet/common:shortDescription[@xml:lang='en' or not(@xml:lang)]");

            XPathExpression xpLCModelResultingProcess1UUID =    xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[1]/@refObjectId");
            XPathExpression xpLCModelResultingProcess1Version = xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[1]/@version");
            XPathExpression xpLCModelResultingProcess1Name =    xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[1]/common:shortDescription[@xml:lang='en' or not(@xml:lang)]");
            XPathExpression xpLCModelResultingProcess2UUID =    xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[2]/@refObjectId");
            XPathExpression xpLCModelResultingProcess2Version = xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[2]/@version");
            XPathExpression xpLCModelResultingProcess2Name =    xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:dataSetInformation/lcm:referenceToResultingProcess[2]/common:shortDescription[@xml:lang='en' or not(@xml:lang)]");

            XPathExpression xpLCModelReferenceProcessUUID =     xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:technology/lcm:processes/lcm:processInstance[@dataSetInternalID=/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:quantitativeReference/lcm:referenceToReferenceProcess/text()]/lcm:referenceToProcess/@refObjectId");
            XPathExpression xpLCModelReferenceProcessVersion =  xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:technology/lcm:processes/lcm:processInstance[@dataSetInternalID=/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:quantitativeReference/lcm:referenceToReferenceProcess/text()]/lcm:referenceToProcess/@version");
            XPathExpression xpLCModelReferenceProcessName =     xpath.compile("/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:technology/lcm:processes/lcm:processInstance[@dataSetInternalID=/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:quantitativeReference/lcm:referenceToReferenceProcess/text()]/lcm:referenceToProcess/common:shortDescription[@xml:lang='en' or not(@xml:lang)]");

            XPathExpression xpLCModelProcessInstanceCount =     xpath.compile("count(/lcm:lifeCycleModelDataSet/lcm:lifeCycleModelInformation/lcm:technology/lcm:processes/lcm:processInstance)");

            for (IDatasetReference ref : files) {
                // for now, we're considering only processes
                if (DatasetType.PROCESS.equals(ref.getDatasetType())) {
                    Document doc = builder.parse(new TFileInputStream(ref.getAbsoluteFileName()));

                    String dataProviderName = (String) xpProcessDataproviderName.evaluate(doc, XPathConstants.STRING);
//                    String dataProviderUUID = (String) xpProcessDataproviderUUID.evaluate(doc, XPathConstants.STRING);
                    String processGeoCode = (String) xpProcessGeoCode.evaluate(doc, XPathConstants.STRING);
                    String processType = (String) xpProcessProcessType.evaluate(doc, XPathConstants.STRING);
                    String dataCutOffAndCompletenessPrinciples = (String) xpProcessDataCutOffAndCompletenessPrinciples.evaluate(doc, XPathConstants.STRING);
                    String deviationsFromLCIMethodApproaches = (String) xpProcessDeviationsFromLCIMethodApproaches.evaluate(doc, XPathConstants.STRING);

                    ProcessSummaryDTO summaryEntry = new ProcessSummaryDTO();
                    summaryEntry.uuid = ref.getUuid();
                    summaryEntry.version = ref.getVersion();
                    summaryEntry.name = ref.getName();
                    summaryEntry.geo = processGeoCode;
//                    summaryEntry.owner = (StringUtils.isNotEmpty(dataProviderUUID) ? dataProviderName + "  (" + dataProviderUUID + ")" : dataProviderName);
                    summaryEntry.owner = dataProviderName;
                    summaryEntry.processType = processType;
                    summaryEntry.dataCutOffAndCompletenessPrinciples = StringUtils.replaceEachRepeatedly(dataCutOffAndCompletenessPrinciples, new String[]{"\n", "\t", "  "}, new String[]{" ", " ", " "});
                    summaryEntry.deviationsFromLCIMethodApproaches = StringUtils.replaceEachRepeatedly(deviationsFromLCIMethodApproaches, new String[]{"\n", "\t", "  "}, new String[]{" ", " ", " "});

                    SummaryExtractor.this.summaryProcesses.add(summaryEntry);

                } else if (DatasetType.LCMODEL.equals(ref.getDatasetType())) {
                    Document doc = builder.parse(new TFileInputStream(ref.getAbsoluteFileName()));

                    String dataProviderName = (String) xpLCModelDataproviderName.evaluate(doc, XPathConstants.STRING);
                    String resultingProcess1UUID = (String) xpLCModelResultingProcess1UUID.evaluate(doc, XPathConstants.STRING);
                    String resultingProcess1Version = (String) xpLCModelResultingProcess1Version.evaluate(doc, XPathConstants.STRING);
                    String resultingProcess1Name = (String) xpLCModelResultingProcess1Name.evaluate(doc, XPathConstants.STRING);

                    String resultingProcess2UUID = (String) xpLCModelResultingProcess2UUID.evaluate(doc, XPathConstants.STRING);
                    String resultingProcess2Version = (String) xpLCModelResultingProcess2Version.evaluate(doc, XPathConstants.STRING);
                    String resultingProcess2Name = (String) xpLCModelResultingProcess2Name.evaluate(doc, XPathConstants.STRING);

                    String referenceProcessUUID = (String) xpLCModelReferenceProcessUUID.evaluate(doc, XPathConstants.STRING);
                    String referenceProcessVersion = (String) xpLCModelReferenceProcessVersion.evaluate(doc, XPathConstants.STRING);
                    String referenceProcessName = (String) xpLCModelReferenceProcessName.evaluate(doc, XPathConstants.STRING);

                    Integer processInstancesCount = null;
                    try {
                        String result = (String) xpLCModelProcessInstanceCount.evaluate(doc, XPathConstants.STRING);
                        processInstancesCount = Integer.parseInt(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LCModelSummaryDTO summaryEntry = new LCModelSummaryDTO();
                    summaryEntry.uuid = ref.getUuid();
                    summaryEntry.version = ref.getVersion();
                    summaryEntry.name = ref.getName();
                    summaryEntry.owner = dataProviderName;

                    summaryEntry.resultingProcess1UUID = resultingProcess1UUID;
                    summaryEntry.resultingProcess1Version = resultingProcess1Version;
                    summaryEntry.resultingProcess1Name = resultingProcess1Name;
                    summaryEntry.resultingProcess2UUID = resultingProcess2UUID;
                    summaryEntry.resultingProcess2Version = resultingProcess2Version;
                    summaryEntry.resultingProcess2Name = resultingProcess2Name;

                    summaryEntry.referenceProcessUUID = referenceProcessUUID;
                    summaryEntry.referenceProcessVersion = referenceProcessVersion;
                    summaryEntry.referenceProcessName = referenceProcessName;

                    summaryEntry.processInstancesCount = processInstancesCount;

                    log.debug("{}", summaryEntry);

                    SummaryExtractor.this.summaryLCModels.add(summaryEntry);
                }
            }

            return null;
        }
    }

    public static abstract class SummaryDTO {
        public SummaryDTO() {
        }

        public String uuid = null;
        public String version = null;
        public String name = null;
    }

    public static class ProcessSummaryDTO extends SummaryDTO {
        public ProcessSummaryDTO() {
            super();
        }

        public String geo = null;
        public String owner = null;
        public String processType = null;
        public String dataCutOffAndCompletenessPrinciples = null;
        public String deviationsFromLCIMethodApproaches = null;

        @Override
        public String toString() {
            return "ProcessSummaryDTO{" +
                    "uuid='" + uuid + '\'' +
                    ", version='" + version + '\'' +
                    ", name='" + name + '\'' +
                    ", geo='" + geo + '\'' +
                    ", owner='" + owner + '\'' +
                    ", processType='" + processType + '\'' +
                    ", dataCutOffAndCompletenessPrinciples='" + dataCutOffAndCompletenessPrinciples + '\'' +
                    ", deviationsFromLCIMethodApproaches='" + deviationsFromLCIMethodApproaches + '\'' +
                    '}';
        }
    }

    public static class LCModelSummaryDTO extends SummaryDTO {
        public LCModelSummaryDTO() {
            super();
        }

        public String owner = null;
        public String resultingProcess1UUID = null;
        public String resultingProcess1Version = null;
        public String resultingProcess1Name = null;
        public String resultingProcess2UUID = null;
        public String resultingProcess2Version = null;
        public String resultingProcess2Name = null;
        public String referenceProcessUUID = null;
        public String referenceProcessVersion = null;
        public String referenceProcessName = null;

        public Integer processInstancesCount = null;

        @Override
        public String toString() {
            return "LCModelSummaryDTO{" +
                    "uuid='" + uuid + '\'' +
                    ", version='" + version + '\'' +
                    ", name='" + name + '\'' +
                    ", resultingProcess1UUID='" + resultingProcess1UUID + '\'' +
                    ", resultingProcess1Version='" + resultingProcess1Version + '\'' +
                    ", resultingProcess1Name='" + resultingProcess1Name + '\'' +
                    ", resultingProcess2UUID='" + resultingProcess2UUID + '\'' +
                    ", resultingProcess2Version='" + resultingProcess2Version + '\'' +
                    ", resultingProcess2Name='" + resultingProcess2Name + '\'' +
                    ", referenceProcessUUID='" + referenceProcessUUID + '\'' +
                    ", referenceProcessVersion='" + referenceProcessVersion + '\'' +
                    ", referenceProcessName='" + referenceProcessName + '\'' +
                    ", processInstancesCount='" + processInstancesCount + '\'' +
                    '}';
        }
    }

    protected void updateStatusValidating() {
        updateStatus("Processing for datasets summary...");
    }

}
