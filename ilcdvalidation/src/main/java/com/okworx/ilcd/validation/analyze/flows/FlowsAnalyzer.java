package com.okworx.ilcd.validation.analyze.flows;

import com.okworx.ilcd.validation.AbstractDatasetsValidator;
import com.okworx.ilcd.validation.AbstractReferenceObjectsAwareValidator;
import com.okworx.ilcd.validation.analyze.flows.util.*;
import com.okworx.ilcd.validation.common.DatasetType;
import com.okworx.ilcd.validation.common.ExchangeDirection;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.reference.FlowDatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;
import com.okworx.ilcd.validation.util.AbstractDatasetsTask;
import com.okworx.ilcd.validation.util.ILCDNameSpaceContext;
import com.okworx.ilcd.validation.util.PartitionedList;
import com.okworx.ilcd.validation.util.TaskResult;
import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.util.*;
import java.util.concurrent.*;

import static com.okworx.ilcd.validation.analyze.flows.util.FlowsMode.ELEMENTARIES;
import static com.okworx.ilcd.validation.analyze.flows.util.FlowsMode.OTHERS;

// this can be added to a ValidatorChain in order to generate a report showing the elementary flows being used
public class FlowsAnalyzer extends AbstractReferenceObjectsAwareValidator {

    public static final String PARAM_INCLUDE_SUMS = "flowsAnalysis_withSums";

    public static final int FIRST_DATA_ROW = 8;
    public static final int FIRST_DATA_COLUMN = 6;
    public static final int AUTOSIZE_COLUMNS = 6;
    public static final int MAX_ROWNUMS_FOR_AUTOSIZING = 20;
    public static final String ELEMENTARY_FLOWS_AMOUNTS = "Flows - Standard (Amounts)";
    public static final String OTHER_FLOWS_AMOUNTS = "Flows - Non-standard (Amounts)";
    public static final String ELEMENTARY_FLOWS_USAGE = "Flows - Standard (Usage)";
    public static final String ELEMENTARY_FLOWS_USAGE_NOTE = "This table shows all elementary, waste and other flows as defined in the ";
    public static final String NOTE_PART_2 = " flow list that are referenced by exchanges in the shown process datasets.";
    public static final String OTHER_FLOWS_USAGE = "Flows - Non-standard (Usage)";
    public static final String OTHER_FLOWS_USAGE_NOTE = "This table shows all flows which are NOT defined in the ";

    public static final String LEGEND_TITLE = "Legend:";

    public static final String LEGEND_ONLY_INPUTS = "only inputs";
    public static final String LEGEND_ONLY_OUTPUTS = "only outputs";
    public static final String LEGEND_BOTH_IN_OUTPUTS = "both inputs and outputs";
    public static final String LEGEND_REFERENCE_INPUTS = "reference input(s)";
    public static final String LEGEND_REFERENCE_OUTPUTS = "reference output(s)";
    public static final String LEGEND_REFERENCE_BOTH_IN_OUTPUTS = "both reference input(s) and output(s)";

    private SXSSFWorkbook workbook;

    private FlowAnalysisCellStyles styles;
    protected boolean includeSums = false;

    @Override
    public String getAspectName() {
        return "Flows Analysis";
    }

    @Override
    public String getAspectDescription() {
        return "analysis of all flows being used";
    }

    // holds the reference elementary flows
    protected SortedSet<String> referenceFlows;

    protected Collection<ProcessSummaryDTO> summaryProcesses = new ConcurrentLinkedQueue<>();

    protected Collection<String> otherFlows = new ConcurrentLinkedQueue<>();

    @Override
    public boolean validate() throws InterruptedException {
        super.validate();

        updateStatusValidating();

        this.includeSums = BooleanUtils.isTrue((Boolean) this.parameters.get(PARAM_INCLUDE_SUMS));

        log.debug("include sums: {}", this.includeSums);

        this.unitsTotal = this.objectsToValidate.size();

        log.debug("{} objects to be processed", this.unitsTotal);

        PartitionedList<IDatasetReference> partList = new PartitionedList<>(
                this.objectsToValidate.values());

        Collection<Callable<TaskResult>> tasks = new ArrayList<>();

        for (List<IDatasetReference> refList : partList.getPartitions()) {
            tasks.add(new ExtractTask(this, refList, includeSums));
        }

        this.referenceFlows = new TreeSet<>(this.referenceElementaryFlows.keySet());

        log.debug("{} using reference flows from profile", this.referenceFlows.size());

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

        // write analysis with occurrences
        writeResults(false);
        // write analysis with sums of the amounts
        if (this.includeSums)
            writeResults(true);

        updateProgress(1);
        updateStatusDone();

        return true;
    }

    private void writeResults(boolean forSums) {

        SXSSFSheet sheetElementaries;
        SXSSFSheet sheetOthers;

        if (forSums) {
            sheetElementaries = workbook.createSheet(ELEMENTARY_FLOWS_AMOUNTS);
            sheetOthers = workbook.createSheet(OTHER_FLOWS_AMOUNTS);
        } else {
            sheetElementaries = workbook.createSheet(ELEMENTARY_FLOWS_USAGE);
            sheetOthers = workbook.createSheet(OTHER_FLOWS_USAGE);
        }

        setupStyles();

        writeSheet(forSums, sheetElementaries, ELEMENTARIES);
        writeSheet(forSums, sheetOthers, OTHERS);

    }

    private void writeSheet(boolean forSums, SXSSFSheet sheet, FlowsMode mode) {

        String suffix = this.profile.getName() + NOTE_PART_2;

        if (ELEMENTARIES.equals(mode))
            writeHeaders(sheet, mode, ELEMENTARY_FLOWS_USAGE_NOTE + suffix);
        else if (OTHERS.equals(mode))
            writeHeaders(sheet, mode, OTHER_FLOWS_USAGE_NOTE + suffix);

        int column = FIRST_DATA_COLUMN;

        // let's sort the processes nicely in the sheet
        SortedSet<ProcessSummaryDTO> summaries = new TreeSet<>(this.summaryProcesses);

        for (ProcessSummaryDTO p : summaries) {
            sheet.getRow(1).createCell(column).setCellValue(p.uuid);
            sheet.getRow(2).createCell(column).setCellValue(p.version);
            sheet.getRow(3).createCell(column).setCellValue(p.name);
            sheet.getRow(4).createCell(column).setCellValue(p.geo);
            sheet.getRow(5).createCell(column).setCellValue(p.processType);
            sheet.getRow(6).createCell(column, CellType.NUMERIC);
            if (ELEMENTARIES.equals(mode))
                sheet.getRow(6).getCell(column).setCellValue(p.getElementaryExchangesCount());
            else if (OTHERS.equals(mode))
                sheet.getRow(6).getCell(column).setCellValue(p.getOtherExchangesCount());

            if (forSums) {
                sheet.getRow(7).createCell(column, CellType.STRING).setCellValue("∑ amnts.");
                sheet.getRow(7).getCell(column).setCellStyle(styles.boldItalicStyle);
            } else {
                sheet.getRow(7).createCell(column, CellType.STRING).setCellValue("# occ.");
                sheet.getRow(7).getCell(column).setCellStyle(styles.boldItalicStyle);
            }

            column++;
        }

        for (int i = 0; i < AUTOSIZE_COLUMNS; i++) {
            sheet.trackColumnForAutoSizing(i);
        }

        int row = FIRST_DATA_ROW;

        int occurrences;
        int sumOccurrences;

        SortedSet<String> flows;

        if (ELEMENTARIES.equals(mode))
            flows = this.referenceFlows;
        else if (OTHERS.equals(mode)) {
            // TODO need some more efficient way
            flows = new TreeSet<>();
            for (ProcessSummaryDTO p : this.summaryProcesses) {
                flows.addAll(p.otherExchanges.keySet());
            }
        } else
            flows = Collections.emptySortedSet();

        for (String flowUuid : flows) {

            // name, CAS number and compartment are contained in the string payload, separated by semicolons
            String flowDescriptor;
            String flowName = "";
            String flowCompartmentOrType = "";

            if (ELEMENTARIES.equals(mode)) {
                flowDescriptor = this.referenceElementaryFlows.get(flowUuid);
                if (flowDescriptor.contains(";"))
                    flowName = StringUtils.substringBefore(flowDescriptor, ";");
                else
                    flowName = flowDescriptor;
                flowCompartmentOrType = StringUtils.substringAfterLast(flowDescriptor, "; ");
            } else if (OTHERS.equals(mode)) {
                IDatasetReference ref = this.objectsToValidate.get(flowUuid);
                if (ref!=null) {
                    flowName = ref.getName();
                    flowCompartmentOrType = ((FlowDatasetReference) ref).getFlowType();
                }
            }

            sheet.createRow(row).createCell(0, CellType.STRING).setCellValue(flowUuid);
            sheet.getRow(row).createCell(1, CellType.STRING).setCellValue(flowName);
            sheet.getRow(row).createCell(2, CellType.STRING).setCellValue(flowCompartmentOrType);
            sheet.getRow(row).createCell(3, CellType.NUMERIC);
            sheet.getRow(row).createCell(4, CellType.NUMERIC);

            occurrences = 0;
            sumOccurrences = 0;

            column = FIRST_DATA_COLUMN;
            if (ELEMENTARIES.equals(mode))
                for (ProcessSummaryDTO p : summaries) {
                    if (p.elementaryExchanges.containsKey(flowUuid)) {
                        occurrences++;
                        FlowData flowData = p.elementaryExchanges.get(flowUuid);
                        sumOccurrences += flowData.occurrences;

                        createDataCell(sheet, row, column, flowData, forSums, p.referenceExchanges.contains(flowUuid));
                    }
                    column++;
                }
            else if (OTHERS.equals(mode))
                for (ProcessSummaryDTO p : summaries) {
                    if (p.otherExchanges.containsKey(flowUuid)) {
                        occurrences++;
                        FlowData flowData = p.otherExchanges.get(flowUuid);
                        sumOccurrences += flowData.occurrences;

                        createDataCell(sheet, row, column, flowData, forSums, p.referenceExchanges.contains(flowUuid));
                    }
                    column++;
                }

            // write sums
            sheet.getRow(row).getCell(3).setCellValue(occurrences);
            sheet.getRow(row).getCell(4).setCellValue(sumOccurrences);

            // do autosizing in last row
            // for very large numbers of rows, we're disabling column auto sizing for the remaining rows
            if (((row == FIRST_DATA_ROW + flows.size() - 1) && row < FIRST_DATA_ROW + MAX_ROWNUMS_FOR_AUTOSIZING) || row == FIRST_DATA_ROW + MAX_ROWNUMS_FOR_AUTOSIZING ) {
                for (int i = 0; i < AUTOSIZE_COLUMNS; i++) {
                    sheet.autoSizeColumn(i);
                    sheet.untrackColumnForAutoSizing(i);
                }
            }

            row++;
        }

        sheet.setColumnWidth(3, 1300);
        sheet.setColumnWidth(4, 1300);

        sheet.setAutoFilter(new CellRangeAddress(7, 7, 0, FIRST_DATA_COLUMN + this.summaryProcesses.size() - 1));

        if (ELEMENTARIES.equals(mode))
            sheet.setZoom(80);
        else
            sheet.setZoom(90);
    }

    private void writeHeaders(SXSSFSheet sheet, FlowsMode mode, String description) {
        // write the header
        sheet.createRow(0);
        sheet.createRow(1);
        sheet.createRow(2);
        sheet.createRow(3);
        sheet.createRow(4);
        sheet.createRow(5);
        sheet.createRow(6);
        sheet.createRow(7);

        // add descriptions
        Cell cell = sheet.getRow(0).createCell(0, CellType.STRING);
        cell.setCellValue(description);
        cell.setCellStyle(styles.descriptionStyle);

        Cell cellLegend = sheet.getRow(2).createCell(0, CellType.STRING);
        cellLegend.setCellStyle(styles.legendStyle);
        cellLegend.setCellValue(LEGEND_TITLE);

        Cell cellEmptyCell = sheet.getRow(2).createCell(1, CellType.STRING);
        cellEmptyCell.setCellStyle(styles.infoBackgroundStyle);

        Cell cellEmptyCell2 = sheet.getRow(2).createCell(2, CellType.STRING);
        cellEmptyCell2.setCellStyle(styles.infoBackgroundStyle);

        Cell cellLegendInput = sheet.getRow(3).createCell(0, CellType.STRING);
        Cell cellLegendOutput = sheet.getRow(4).createCell(0, CellType.STRING);
        Cell cellLegendMixed = sheet.getRow(5).createCell(0, CellType.STRING);
        Cell cellLegendInputReference = sheet.getRow(3).createCell(1, CellType.STRING);
        Cell cellLegendOutputReference = sheet.getRow(4).createCell(1, CellType.STRING);
        Cell cellLegendReferenceMixed = sheet.getRow(5).createCell(1, CellType.STRING);

        cellLegendInput.setCellValue(LEGEND_ONLY_INPUTS);
        cellLegendInput.setCellStyle(styles.infoLegendInputStyle);

        cellLegendOutput.setCellValue(LEGEND_ONLY_OUTPUTS);
        cellLegendOutput.setCellStyle(styles.infoLegendOutputStyle);

        cellLegendMixed.setCellValue(LEGEND_BOTH_IN_OUTPUTS);
        cellLegendMixed.setCellStyle(styles.infoLegendMixedStyle);

        cellLegendInputReference.setCellValue(LEGEND_REFERENCE_INPUTS);
        cellLegendInputReference.setCellStyle(styles.infoLegendReferenceInputStyle);

        cellLegendOutputReference.setCellValue(LEGEND_REFERENCE_OUTPUTS);
        cellLegendOutputReference.setCellStyle(styles.infoLegendReferenceOutputStyle);

        cellLegendReferenceMixed.setCellValue(LEGEND_REFERENCE_BOTH_IN_OUTPUTS);
        cellLegendReferenceMixed.setCellStyle(styles.infoLegendReferenceMixedStyle);

        Cell cellEmptyRow = sheet.getRow(6).createCell(0, CellType.STRING);
        cellEmptyRow.setCellStyle(styles.infoBackgroundStyle);

        // merge cells to make it look nicer
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,4));
        sheet.addMergedRegion(new CellRangeAddress(2,5,2,4));
        sheet.addMergedRegion(new CellRangeAddress(6,6,0,4));

        // add nice header captions
        if (ELEMENTARIES.equals(mode)) {
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 0, "Elementary flow UUID");
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 1, "Elementary flow name");
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 2, "Compartment");
        } else {
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 0, "Flow UUID");
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 1, "Flow name");
            createHeaderCell(sheet, FIRST_DATA_ROW - 1, 2, "Type");
        }

        createHeaderCell(sheet, FIRST_DATA_ROW - 1, 3, "# occ.");
        createHeaderCell(sheet, FIRST_DATA_ROW - 1, 4, "∑ occ.");

        createHeaderCell(sheet,  1, 5, "Process UUID");
        createHeaderCell(sheet, 2, 5, "Version");
        createHeaderCell(sheet, 3, 5, "Name");
        createHeaderCell(sheet, 4, 5, "Location");
        createHeaderCell(sheet, 5, 5, "Type");
        createHeaderCell(sheet, 6, 5, "Total occurrences");

        // freeze for comfortable vert. & horiz. scrolling
        sheet.createFreezePane(6, 8);
    }

    private void setupStyles() {
        this.styles = new FlowAnalysisCellStyles();
        styles.init(workbook);
    }

    private void createHeaderCell(SXSSFSheet sheet, int row, int col, String text) {
        Cell cell = sheet.getRow(row).createCell(col, CellType.STRING);
        cell.setCellValue(text);
        cell.setCellStyle(styles.boldStyle);
    }

    final class ExtractTask extends AbstractDatasetsTask implements Callable<TaskResult> {

        private final boolean includeSums;

        private XPathExpression xpProcessExchangeFlowRef = null;
        private XPathExpression xpProcessExchangeResultingAmount = null;

        ExtractTask(AbstractDatasetsValidator validator, Collection<IDatasetReference> files, boolean includeSums) {
            this.files = files;
            this.validator = validator;
            this.includeSums = includeSums;
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

            XPathExpression xpProcessGeoCode = xpath.compile("/p:processDataSet/p:processInformation/p:geography/p:locationOfOperationSupplyOrProduction/@location");
            XPathExpression xpProcessProcessType = xpath.compile("/p:processDataSet/p:modellingAndValidation/p:LCIMethodAndAllocation/p:typeOfDataSet");

            XPathExpression xpProcessExchangeFlowRefsIn = null;
            XPathExpression xpProcessExchangeFlowRefsOut = null;

            XPathExpression xpProcessExchangesIn = null;
            XPathExpression xpProcessExchangesOut = null;

            if (this.includeSums) {
                xpProcessExchangesIn = xpath.compile("/p:processDataSet/p:exchanges/p:exchange[p:exchangeDirection/text()='Input']");
                xpProcessExchangesOut = xpath.compile("/p:processDataSet/p:exchanges/p:exchange[p:exchangeDirection/text()='Output']");
                xpProcessExchangeFlowRef = xpath.compile("p:referenceToFlowDataSet/@refObjectId");
                xpProcessExchangeResultingAmount = xpath.compile("p:resultingAmount");
            } else {
                xpProcessExchangeFlowRefsIn = xpath.compile("/p:processDataSet/p:exchanges/p:exchange[p:exchangeDirection/text()='Input']/p:referenceToFlowDataSet/@refObjectId");
                xpProcessExchangeFlowRefsOut = xpath.compile("/p:processDataSet/p:exchanges/p:exchange[p:exchangeDirection/text()='Output']/p:referenceToFlowDataSet/@refObjectId");
            }

            XPathExpression xpProcessExchangeReferenceFlowRefs = xpath.compile("/p:processDataSet/p:exchanges/p:exchange[@dataSetInternalID=/p:processDataSet/p:processInformation/p:quantitativeReference/p:referenceToReferenceFlow]/p:referenceToFlowDataSet/@refObjectId");

            String processGeoCode;
            String processType;
            String processUuid;
            String processVersion;
            String processName;

            NodeList nlExchangesIn;
            NodeList nlExchangesOut;

            NodeList nlRefExchanges;
            Document doc;
            List<String> refFlows;

            for (IDatasetReference ref : files) {

                // for now, we're considering only processes
                if (DatasetType.PROCESS.equals(ref.getDatasetType())) {
                    doc = builder.parse(new TFileInputStream(ref.getAbsoluteFileName()));

                    processGeoCode = (String) xpProcessGeoCode.evaluate(doc, XPathConstants.STRING);
                    processType = (String) xpProcessProcessType.evaluate(doc, XPathConstants.STRING);

                    processUuid = ref.getUuid();
                    processVersion = ref.getVersion();
                    processName = ref.getName();

                    // we'll need the reference flows later
                    nlRefExchanges = (NodeList) xpProcessExchangeReferenceFlowRefs.evaluate(doc, XPathConstants.NODESET);
                    refFlows = new ArrayList<>();
                    for (int i = 0; i < nlRefExchanges.getLength(); i++)
                        refFlows.add(nlRefExchanges.item(i).getTextContent());

                    SortedMap<String, FlowData> flowRefs = new TreeMap<>();

                    if (this.includeSums) {
                        nlExchangesIn = (NodeList) xpProcessExchangesIn.evaluate(doc, XPathConstants.NODESET);
                        nlExchangesOut = (NodeList) xpProcessExchangesOut.evaluate(doc, XPathConstants.NODESET);

                        extractFlowRefsWithAmounts(nlExchangesIn, ExchangeDirection.INPUT, flowRefs);
                        extractFlowRefsWithAmounts(nlExchangesOut, ExchangeDirection.OUTPUT, flowRefs);
                    } else {
                        // for each referenced flow, we'll store the number of times it is present in the process dataset
                        nlExchangesIn = (NodeList) xpProcessExchangeFlowRefsIn.evaluate(doc, XPathConstants.NODESET);
                        nlExchangesOut = (NodeList) xpProcessExchangeFlowRefsOut.evaluate(doc, XPathConstants.NODESET);

                        extractFlowRefs(nlExchangesIn, ExchangeDirection.INPUT, flowRefs);
                        extractFlowRefs(nlExchangesOut, ExchangeDirection.OUTPUT, flowRefs);
                    }

                    final int totalExchanges = flowRefs.size();

                    SortedMap<String, FlowData> elementaryFlowRefs = new TreeMap<>(flowRefs);

                    // for list of elementaries, throw out any flows not in the reference list
                    elementaryFlowRefs.keySet().retainAll(FlowsAnalyzer.this.referenceFlows);

                    // for list of others, throw out all those that are in the reference list
                    flowRefs.keySet().removeAll(FlowsAnalyzer.this.referenceFlows);

                    // add other flows to the global list of other flows
                    FlowsAnalyzer.this.otherFlows.addAll(flowRefs.keySet());

                    if (log.isDebugEnabled())
                        log.debug("process " + processName + " with " + totalExchanges + " exchanges, " + elementaryFlowRefs.keySet().size() + " elementary flows and " + flowRefs.keySet().size() + " other flows");

                    ProcessSummaryDTO result = new ProcessSummaryDTO();
                    result.uuid = processUuid;
                    result.version = processVersion;
                    result.name = processName;
                    result.geo = processGeoCode;
                    result.processType = processType;
                    result.elementaryExchanges = elementaryFlowRefs;
                    result.otherExchanges = flowRefs;
                    result.referenceExchanges = refFlows;

                    FlowsAnalyzer.this.summaryProcesses.add(result);
                }
            }

            return null;
        }

        private void extractFlowRefs(NodeList nlExchanges, ExchangeDirection exchangeDirection, SortedMap<String, FlowData> flowRefs) {
            if (nlExchanges != null) {
                log.debug("extracting {} flow refs in direction {}", nlExchanges.getLength(), exchangeDirection.value());
                for (int i = 0; i < nlExchanges.getLength(); i++) {
                    String flowRefId = nlExchanges.item(i).getTextContent();
                    addFlowData(flowRefs, exchangeDirection, flowRefId);
                }
            }
        }

        private void extractFlowRefsWithAmounts(NodeList nlExchanges, ExchangeDirection exchangeDirection, SortedMap<String, FlowData> flowRefs) throws XPathExpressionException {
            if (nlExchanges != null) {
                String flowRefId;
                String strResultingAmount;
                Double resultingAmount;

                log.debug("extracting {} flow refs with amounts in direction {}", nlExchanges.getLength(), exchangeDirection.value());

                for (int i = 0; i < nlExchanges.getLength(); i++) {
                    flowRefId = (String) xpProcessExchangeFlowRef.evaluate(nlExchanges.item(i), XPathConstants.STRING);

                    strResultingAmount = (String) xpProcessExchangeResultingAmount.evaluate(nlExchanges.item(i), XPathConstants.STRING);
                    try {
                        resultingAmount = Double.parseDouble(strResultingAmount);
                    } catch (NumberFormatException e) {
                        resultingAmount = null;
                    }
                    addFlowData(flowRefs, flowRefId, exchangeDirection, resultingAmount);
                }
            }
        }

        private void addFlowData(SortedMap<String, FlowData> flowRefs, ExchangeDirection direction, String flowRefId) {
            addFlowData(flowRefs, flowRefId, direction, null);
        }

        private void addFlowData(SortedMap<String, FlowData> flowRefs, String flowRefId, ExchangeDirection ed, Double resultingAmount) {
            if (!flowRefs.containsKey(flowRefId)) {
                FlowData fd = new FlowData();
                fd.occurrences = 1;

                if (ed != null) {
                    switch (ed) {
                        case INPUT:
                            fd.inputs = 1;
                            break;
                        case OUTPUT:
                            fd.outputs = 1;
                            break;
                    }
                }

                if (this.includeSums)
                    fd.sum = resultingAmount;

                flowRefs.put(flowRefId, fd);
            } else {
                FlowData fd = flowRefs.get(flowRefId);
                fd.occurrences++;

                switch (ed) {
                    case INPUT:
                        if (fd.inputs == null)
                            fd.inputs = 1;
                        else
                            fd.inputs++;
                        break;
                    case OUTPUT:
                        if (fd.outputs == null)
                            fd.outputs = 1;
                        else
                            fd.outputs++;
                        break;
                }

                if (this.includeSums)
                    fd.sum += resultingAmount;
            }
        }
    }

    private void createDataCell(Sheet sheet, int row, int column, FlowData flowData, boolean forSums, boolean reference) {
        Cell cell = sheet.getRow(row).createCell(column, CellType.NUMERIC);

        // set value
        if (forSums)
            cell.setCellValue(flowData.sum);
        else
            cell.setCellValue(flowData.occurrences);

        // format for in/out/mixed
        styleCellIOMixed(cell, flowData, reference);
    }

    private void styleCellIOMixed(Cell cell, FlowData flowData, boolean reference) {
        if (reference) {
            if (FlowDirections.INPUTS_ONLY.equals(flowData.getFlowDirections()))
                cell.setCellStyle(styles.referenceInputStyle);
            else if (FlowDirections.OUTPUTS_ONLY.equals(flowData.getFlowDirections()))
                cell.setCellStyle(styles.referenceOutputStyle);
            else if (FlowDirections.MIXED.equals(flowData.getFlowDirections())) {
                cell.setCellStyle(styles.referenceMixedStyle);
                log.error("reference flow with mixed in/output, this should not happen");
            } else if (FlowDirections.EMPTY.equals(flowData.getFlowDirections()))
                log.error("empty flow data, this shouldn't happen");
            else
                log.error("no flow data, this shouldn't happen");
        } else {
            if (FlowDirections.INPUTS_ONLY.equals(flowData.getFlowDirections()))
                cell.setCellStyle(styles.inputStyle);
            else if (FlowDirections.OUTPUTS_ONLY.equals(flowData.getFlowDirections()))
                cell.setCellStyle(styles.outputStyle);
            else if (FlowDirections.MIXED.equals(flowData.getFlowDirections()))
                cell.setCellStyle(styles.mixedStyle);
            else if (FlowDirections.EMPTY.equals(flowData.getFlowDirections()))
                log.error("empty flowdata, this shouldn't happen");
            else
                log.error("no flow data, this shouldn't happen");
        }

    }

    public void setWorkbook(SXSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    protected void updateStatusValidating() {
        updateStatus("Processing for flow analysis...");
    }

}
