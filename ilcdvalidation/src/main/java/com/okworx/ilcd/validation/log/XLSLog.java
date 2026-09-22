package com.okworx.ilcd.validation.log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import com.okworx.ilcd.validation.*;
import com.okworx.ilcd.validation.analyze.SummaryExtractor;
import com.okworx.ilcd.validation.analyze.flows.FlowsAnalyzer;
import com.okworx.ilcd.validation.common.DatasetType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.events.IValidationEvent;
import com.okworx.ilcd.validation.util.Statistics;

public class XLSLog {

	private static final int MAX_ROWNUMS_FOR_AUTOSIZING = 1000;

	private static final String TABLE_HEADING_RESULT = "Result";

	private static final String FAILED = "FAILED";

	private static final String PASSED = "PASSED";

	private static final String FILE_SUFFIX_XLSX = ".xlsx";

	private static final String TABLE_HEADING_REFERENCED_DATASET_NAME = "Referenced dataset name";

	private static final String TABLE_HEADING_REFERENCED_DATASET_UUID = "Referenced dataset UUID";

	private static final String TABLE_HEADING_REFERENCED_DATASET_TYPE = "Referenced dataset type";

	private static final String TABLE_HEADING_MESSAGE = "Message";

	private static final String TABLE_HEADING_NAME = "Name";

	private static final String TABLE_HEADING_VERSION = "Version";

	private static final String TABLE_HEADING_UUID = "UUID";

	private static final String TABLE_HEADING_DATASET_TYPE = "Dataset type";

	private static final String TABLE_HEADING_FILENAME = "Filename";

	private static final String TABLE_HEADING_ASPECT = "Aspect";

	private static final String TABLE_HEADING_SEVERITY = "Severity";

	private static final String TABLE_HEADING_DS_SUMMARY_PROCESS_DATAPROVIDER = "Data provider";

	private static final String TABLE_HEADING_DS_SUMMARY_PROCESS_GEO = "Geo.";

	private static final String TABLE_HEADING_DS_SUMMARY_PROCESS_TYPE = "Type";

	private static final String TABLE_HEADING_DS_SUMMARY_PROCESS_CUTOFF = "Cut-off";

	private static final String TABLE_HEADING_DS_SUMMARY_PROCESS_ALLOCATION = "Allocation";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_UUID = "Resulting process (1) UUID";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_VERSION = "Version";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_NAME = "Resulting process (1) name";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_UUID = "Resulting process (2) UUID";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_VERSION = "Version";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_NAME = "Resulting process (2) name";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_UUID = "Reference process UUID";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_VERSION = "Version";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_NAME = "Reference process name";

	private static final String TABLE_HEADING_DS_SUMMARY_LCM_PROCESS_INSTANCES_COUNT = "# process instances";

	private SXSSFWorkbook workbook;

	private Map<IValidator, Sheet> sheets = new HashMap<>();

	private Sheet validationSummarySheet = null;

	private Sheet processesSummarySheet = null;

	private Sheet lcModelsSummarySheet = null;

	private Sheet generalSheet = null;

	private int summaryRow = 0;

	private CellStyle boldStyle;

	private CellStyle italicStyle;

	private CellStyle redStyle;

	private CellStyle greenStyle;

	private CellStyle summaryStyle;

	private CellStyle summaryStyleBold;

	private CellStyle tableHeadingStyle;

	private CellStyle tableHeadingSmallStyle;

	private CellStyle wrapStyle;

	private CellStyle wrapStyleTopLeft;

	private CellStyle styleTopLeft;

	public XLSLog(List<IDatasetsValidator> validators, String filename, HashMap<String, String> extraProperties) {

		this.workbook = new SXSSFWorkbook(100);

		setupStyles();

		createValidationSummarySheet(filename, validators, extraProperties);

		createLogSheets(validators);

	}

	private void createLogSheets(List<IDatasetsValidator> validators) {
		for (IValidator validator : validators) {
			// skip for process summary sheet
			if (validator instanceof SummaryExtractor || validator instanceof FlowsAnalyzer)
				continue;

			SXSSFSheet sheet = this.workbook.createSheet(validator.getAspectName());
			for (int i=0; i<=11; i++) {
				sheet.trackColumnForAutoSizing(i);
			}
			this.sheets.put(validator, sheet);
			writeEventsHeader(validator);
			
			if (validator instanceof LinkValidator || validator instanceof ReferenceFlowValidator)
				sheet.setAutoFilter(CellRangeAddress.valueOf("A:K"));
			else
				sheet.setAutoFilter(CellRangeAddress.valueOf("A:H"));
		}
	}

	private void createGeneralSheet() {
		this.generalSheet = this.workbook.createSheet("General");
		((SXSSFSheet) this.generalSheet).trackAllColumnsForAutoSizing();
		this.workbook.setSheetOrder("General", 1);
	}
	
	private void createValidationSummarySheet(String filename,
											  List<IDatasetsValidator> validators, HashMap<String, String> extraProperties) {
		this.validationSummarySheet = this.workbook.createSheet("Summary");

		summaryAdd(this.summaryStyleBold, "Validation summary");
		summaryAdd("");
		summaryAdd("File:", filename);
		this.validationSummarySheet.addMergedRegion(new CellRangeAddress(
				this.summaryRow - 1, this.summaryRow - 1, 1, 20));
		summaryAdd("");
		summaryAdd("generated:", new Date().toString());
		if (extraProperties != null) {
			for (String key : extraProperties.keySet()) {
				summaryAdd(key, extraProperties.get(key));
			}
		}
		
		this.validationSummarySheet.addMergedRegion(new CellRangeAddress(
				this.summaryRow - 1, this.summaryRow - 1, 1, 20));
		summaryAdd("");
		summaryAdd("Profile:", validators.get(0).getProfile().getName() + " " + validators.get(0).getProfile().getVersion());
		this.validationSummarySheet.addMergedRegion(new CellRangeAddress(this.summaryRow - 1, this.summaryRow - 1, 1, 20));
		if (validators.get(0).getProfile().getDescription() != null) {
			summaryAdd("Profile description:", validators.get(0).getProfile().getDescription());
			this.validationSummarySheet.addMergedRegion(new CellRangeAddress(this.summaryRow - 1, this.summaryRow - 1, 1, 20));
		}
		summaryAdd("");
		summaryAdd("Options:");
		for (IValidator validator : validators) {
			if (validator.getParameters().keySet().size() > 0) {
				summaryAdd(italicStyle, "    " + validator.getAspectName());
				List<String> params = new ArrayList<>(validator.getParameters().keySet());
				Collections.sort(params);
				for (String paramName : params) {
					summaryAdd(italicStyle, "        " + paramName, validator.getParameters().get(paramName).toString());
				}
			}
		}
		summaryAdd("");
		summaryAdd("");
		summaryAdd(this.summaryStyleBold, TABLE_HEADING_ASPECT, TABLE_HEADING_RESULT);

		((SXSSFSheet) this.validationSummarySheet).trackColumnForAutoSizing(0);
		((SXSSFSheet) this.validationSummarySheet).trackColumnForAutoSizing(1);
		((SXSSFSheet) this.validationSummarySheet).trackColumnForAutoSizing(2);
	}

	private void setupStyles() {
		this.boldStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		this.boldStyle.setFont(boldFont);

		this.italicStyle = workbook.createCellStyle();
		Font italicFont = workbook.createFont();
		italicFont.setItalic(true);
		this.italicStyle.setFont(italicFont);

		this.greenStyle = workbook.createCellStyle();
		Font greenFont = workbook.createFont();
		greenFont.setFontHeightInPoints((short) 14);
		greenFont.setColor(IndexedColors.GREEN.getIndex());
		this.greenStyle.setFont(greenFont);

		this.redStyle = workbook.createCellStyle();
		Font redFont = workbook.createFont();
		redFont.setFontHeightInPoints((short) 14);
		redFont.setColor(IndexedColors.RED.getIndex());
		this.redStyle.setFont(redFont);

		this.summaryStyle = workbook.createCellStyle();
		Font summaryFont = workbook.createFont();
		summaryFont.setFontHeightInPoints((short) 14);
		this.summaryStyle.setFont(summaryFont);

		this.summaryStyleBold = workbook.createCellStyle();
		Font bigFont = workbook.createFont();
		bigFont.setBold(true);
		bigFont.setFontHeightInPoints((short) 14);
		Font smallFont = workbook.createFont();
		smallFont.setBold(true);
		smallFont.setFontHeightInPoints((short) 11);
		this.summaryStyleBold.setFont(bigFont);

		this.tableHeadingStyle = workbook.createCellStyle();
		this.tableHeadingStyle.setFont(bigFont);
		this.tableHeadingStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE
				.getIndex());
		this.tableHeadingStyle.setFillPattern(FillPatternType.SPARSE_DOTS);

		this.tableHeadingSmallStyle = workbook.createCellStyle();
		this.tableHeadingSmallStyle.setFont(smallFont);
		this.tableHeadingSmallStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE
				.getIndex());
		this.tableHeadingSmallStyle.setFillPattern(FillPatternType.SPARSE_DOTS);
		this.tableHeadingSmallStyle.setWrapText(true);

		this.wrapStyle = workbook.createCellStyle();
		this.wrapStyle.setWrapText(true);

		this.wrapStyleTopLeft = workbook.createCellStyle();
		this.wrapStyleTopLeft.setWrapText(true);
		this.wrapStyleTopLeft.setVerticalAlignment(VerticalAlignment.TOP);
		this.wrapStyleTopLeft.setAlignment(HorizontalAlignment.LEFT);

		this.styleTopLeft = workbook.createCellStyle();
		this.styleTopLeft.setVerticalAlignment(VerticalAlignment.TOP);
		this.styleTopLeft.setAlignment(HorizontalAlignment.LEFT);
	}

	public Sheet getSheet(IValidator validator) {
		return this.sheets.get(validator);
	}

	public void summaryAdd(String... strings) {
		summaryAdd(null, strings);
	}

	public void summaryAdd(CellStyle style, String... strings) {

		Row row = this.validationSummarySheet.createRow(summaryRow);

		for (int cellnum = 0; cellnum < strings.length; cellnum++) {
			Cell cell = row.createCell(cellnum);
			cell.setCellValue(strings[cellnum]);
			if (style != null)
				cell.setCellStyle(style);
			else
				cell.setCellStyle(summaryStyle);
		}

		summaryRow++;
	}

	public void summaryAddNumbers(String string, Integer... integers) {

		Row row = this.validationSummarySheet.createRow(summaryRow);

		addCell(row, 0, summaryStyle, string);

		for (int cellnum = 0; cellnum < integers.length; cellnum++) {
			addCell(row, cellnum + 1, summaryStyle, integers[cellnum]);
		}
		
		summaryRow++;
	}

	public void summaryAddRedGreen(String string, Integer int1, Integer int2) {

		Row row = this.validationSummarySheet.createRow(summaryRow);

		Cell cell = row.createCell(0);
		cell.setCellValue(string);
		cell.setCellStyle(summaryStyle);

		cell = row.createCell(1);
		cell.setCellValue(int1);
		cell.setCellStyle(greenStyle);

		cell = row.createCell(2);
		cell.setCellValue(int2);
		cell.setCellStyle(summaryStyle);
		if (int2 > 0)
			cell.setCellStyle(redStyle);

		summaryRow++;
	}

	public void validationSummaryAddResult(boolean result, IValidator validator) {

		Row row = this.validationSummarySheet.createRow(summaryRow);

		Cell cell = row.createCell(0);
		cell.setCellValue(validator.getAspectName());

		// TODO add a comment with the aspect description to each aspect cell

		cell.setCellStyle(this.summaryStyle);

		writeResult(result, row);

		summaryRow++;
	}

	public void writeEventsHeader(IValidator validator) {
		Sheet sheet = (validator instanceof ValidatorChain ? this.generalSheet : getSheet(validator));

		Row row = sheet.createRow(0);

		addCell(row, 0, this.tableHeadingStyle, TABLE_HEADING_SEVERITY);
		addCell(row, 1, this.tableHeadingStyle, TABLE_HEADING_ASPECT);
		addCell(row, 2, this.tableHeadingStyle, TABLE_HEADING_FILENAME);
		addCell(row, 3, this.tableHeadingStyle, TABLE_HEADING_DATASET_TYPE);
		addCell(row, 4, this.tableHeadingStyle, TABLE_HEADING_UUID);
		addCell(row, 5, this.tableHeadingStyle, TABLE_HEADING_VERSION);
		addCell(row, 6, this.tableHeadingStyle, TABLE_HEADING_NAME);
		addCell(row, 7, this.tableHeadingStyle, TABLE_HEADING_MESSAGE);

		if (validator instanceof LinkValidator || validator instanceof ReferenceFlowValidator) {
			addCell(row, 8, this.tableHeadingStyle, TABLE_HEADING_REFERENCED_DATASET_TYPE);
			addCell(row, 9, this.tableHeadingStyle, TABLE_HEADING_REFERENCED_DATASET_UUID);
			addCell(row, 10, this.tableHeadingStyle, TABLE_HEADING_REFERENCED_DATASET_NAME);
		}
	}

	public void writeEvents(IValidator validator) {
		Sheet sheet;
		
		if (validator instanceof ValidatorChain) {
			this.createGeneralSheet();
			this.writeEventsHeader(validator);
			sheet = this.generalSheet;
		} else {
			sheet = getSheet(validator);
		}

		int rowNum = 1;

		for (IValidationEvent event : validator.getEventsList().getEvents()) {
			Row row = sheet.createRow(rowNum);

			addCell(row, 0, styleTopLeft, event.getSeverity().getValue());
			addCell(row, 1, styleTopLeft, event.getAspect());
			addCell(row, 2, styleTopLeft, event.getReference().getShortFileName());

			Cell cell = row.createCell(3);
			if (event.getReference().getDatasetType() != null)
				cell.setCellValue(event.getReference().getDatasetType().getValue());

			addCell(row, 4, styleTopLeft, event.getReference().getUuid());
			addCell(row, 5, styleTopLeft, event.getReference().getVersion());
			addCell(row, 6, styleTopLeft, event.getReference().getName());

			if (validator instanceof LinkValidator || validator instanceof ReferenceFlowValidator) {
				addCell(row, 7, wrapStyle, event.getAltMessage());

				cell = row.createCell(8);
				try {
					cell.setCellValue(event.getMessageReference().getDatasetType().getValue());
				} catch (Exception e) {
				}
	
				cell = row.createCell(9);
				try {
					cell.setCellValue(event.getMessageReference().getUuid());
				} catch (Exception e) {
				}
	
				cell = row.createCell(10);
				try {
					cell.setCellValue(event.getMessageReference().getName());
				} catch (Exception e) {
				}
			} else {
				cell = row.createCell(7);
				cell.setCellValue(event.getMessage());				
			}
			
			rowNum++;

			// for very large numbers of rows, we're disabling column auto sizing for the remaining rows
			if (rowNum == MAX_ROWNUMS_FOR_AUTOSIZING) {
				for (int i=0; i<=11; i++) {
					sheet.autoSizeColumn(i);
					((SXSSFSheet) sheet).untrackColumnForAutoSizing(i);
				}
			}
		}
	}

	public void writeAndClose(String sourceFileName, boolean result)
			throws IOException {

		this.validationSummarySheet.autoSizeColumn(0);
		this.validationSummarySheet.autoSizeColumn(1);
		this.validationSummarySheet.autoSizeColumn(2);

		if (this.generalSheet != null) {
			for (int c = 0; c < 8; c++)
				this.generalSheet.autoSizeColumn(c);
		}
		
		for (Sheet sheet : this.sheets.values()) {
			// remove empty sheets
			if (sheet.getLastRowNum() == 0) {
				workbook.removeSheetAt(workbook.getSheetIndex(sheet));
			} else {
				// autosize remaining sheets unless autosizing has been turned off
				if (sheet.getLastRowNum() < MAX_ROWNUMS_FOR_AUTOSIZING) { 
					for (int c = 0; c < 11; c++) {
						sheet.autoSizeColumn(c);
					}
				}
			}
		}

		String logFileName = sourceFileName.concat("_").concat(
				result ? PASSED : FAILED).concat(FILE_SUFFIX_XLSX);

		FileOutputStream out = new FileOutputStream(logFileName);
		this.workbook.write(out);
		out.close();

		this.workbook.dispose();
	}

	private void writeResult(boolean result, Row row) {
		if (result) {
			addCell(row, 1, greenStyle, PASSED);
		} else {
			addCell(row, 1, redStyle, FAILED);
		}
	}

	public void writeStats(Statistics statistics, EventsList eventsList) {
		summaryAdd("");
		summaryAddNumbers("Errors:", eventsList.getErrorCount());
		if (eventsList.getWarningCount() > 0)
			summaryAddNumbers("Warnings:", eventsList.getWarningCount());
		summaryAdd("");
		summaryAdd("");
		summaryAdd("");
		summaryAdd(this.summaryStyleBold, "Statistics");
		summaryAddNumbers("Total files processed:", statistics.getTotalCount());
		summaryAdd("");
		summaryAdd("", "valid", "invalid");
		summaryAddRedGreen("Process datasets", statistics.getValidProcessesCount(),
				statistics.getInvalidProcessesCount());
		summaryAddRedGreen("Flow datasets", statistics.getValidFlowsCount(), statistics.getInvalidFlowsCount());
		summaryAddRedGreen("Source datasets", statistics.getValidSourcesCount(),
				statistics.getInvalidSourcesCount());
		summaryAddRedGreen("Contact datasets", statistics.getValidContactsCount(),
				statistics.getInvalidContactsCount());
		summaryAddRedGreen("LCIA Method datasets", statistics.getValidLCIAMethodsCount(),
				statistics.getInvalidLCIAMethodsCount());
		summaryAddRedGreen("Flow Property datasets", statistics.getValidFlowPropertiesCount(),
				statistics.getInvalidFlowPropertiesCount());
		summaryAddRedGreen("Unit Group datasets", statistics.getValidUnitGroupsCount(),
				statistics.getInvalidUnitGroupsCount());
		summaryAddRedGreen("Life Cycle Model datasets", statistics.getValidLCModelsCount(),
				statistics.getInvalidLCModelsCount());
		summaryAddRedGreen("External files", statistics.getValidExternalFilesCount(),
				statistics.getInvalidExternalFilesCount());
	}

	public void writeDatasetsSummary(List<IDatasetsValidator> validators) {
		for (IDatasetsValidator v : validators) {
			if (v instanceof SummaryExtractor) {
				if (!((SummaryExtractor) v).getSummaryLCModels().isEmpty()) {
					this.lcModelsSummarySheet = this.workbook.createSheet("DS Summary - Life Cycle Models");
					writeDatasetsSummaryHeader(this.lcModelsSummarySheet, DatasetType.LCMODEL);
					writeDatasetsSummaryEntries(this.lcModelsSummarySheet, DatasetType.LCMODEL, ((SummaryExtractor) v).getSummaryLCModels());
					this.lcModelsSummarySheet.setColumnWidth(0, 10000);
					this.lcModelsSummarySheet.setColumnWidth(1, 2560);
					this.lcModelsSummarySheet.setColumnWidth(2, 12000);
					this.lcModelsSummarySheet.setColumnWidth(3, 10000);
					this.lcModelsSummarySheet.setColumnWidth(4, 10000);
					this.lcModelsSummarySheet.setColumnWidth(5, 2560);
					this.lcModelsSummarySheet.setColumnWidth(6, 16000);
					this.lcModelsSummarySheet.setColumnWidth(7, 10000);
					this.lcModelsSummarySheet.setColumnWidth(8, 2560);
					this.lcModelsSummarySheet.setColumnWidth(9, 16000);
					this.lcModelsSummarySheet.setColumnWidth(10, 10000);
					this.lcModelsSummarySheet.setColumnWidth(11, 2560);
					this.lcModelsSummarySheet.setColumnWidth(12, 16000);
					this.lcModelsSummarySheet.setColumnWidth(13, 2560);
					this.lcModelsSummarySheet.setAutoFilter(CellRangeAddress.valueOf("A:N"));
				}

				if (!((SummaryExtractor) v).getSummaryProcesses().isEmpty()) {
					this.processesSummarySheet = this.workbook.createSheet("DS Summary - Processes");
					writeDatasetsSummaryHeader(this.processesSummarySheet, DatasetType.PROCESS);
					writeDatasetsSummaryEntries(this.processesSummarySheet, DatasetType.PROCESS, ((SummaryExtractor) v).getSummaryProcesses());
					this.processesSummarySheet.setColumnWidth(0, 10000);
					this.processesSummarySheet.setColumnWidth(1, 2560);
					this.processesSummarySheet.setColumnWidth(2, 12000);
					this.processesSummarySheet.setColumnWidth(3, 2800);
					this.processesSummarySheet.setColumnWidth(4, 10000);
					this.processesSummarySheet.setColumnWidth(5, 6000);
					this.processesSummarySheet.setColumnWidth(6, 25600);
					this.processesSummarySheet.setColumnWidth(7, 25600);
					this.processesSummarySheet.setAutoFilter(CellRangeAddress.valueOf("A:H"));
				}

				return;
			}
		}
	}

	public void writeDatasetsSummaryHeader(Sheet sheet, DatasetType dsType) {
		Row row = sheet.createRow(0);

		addCell(row, 0, this.tableHeadingStyle, TABLE_HEADING_UUID);

		addCell(row, 1, this.tableHeadingStyle, TABLE_HEADING_VERSION);
		addCell(row, 2, this.tableHeadingStyle, TABLE_HEADING_NAME);

		if (DatasetType.PROCESS.equals(dsType)) {
			addCell(row, 3, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_GEO);
			addCell(row, 4, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_DATAPROVIDER);
			addCell(row, 5, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_TYPE);
			addCell(row, 6, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_CUTOFF);
			addCell(row, 7, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_ALLOCATION);

		} else if (DatasetType.LCMODEL.equals(dsType)) {
			addCell(row, 3, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_PROCESS_DATAPROVIDER);
			addCell(row, 4, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_UUID);
			addCell(row, 5, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_VERSION);
			addCell(row, 6, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_1_NAME);
			addCell(row, 7, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_UUID);
			addCell(row, 8, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_VERSION);
			addCell(row, 9, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_RESULTINGPROCESS_2_NAME);
			addCell(row, 10, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_UUID);
			addCell(row, 11, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_VERSION);
			addCell(row, 12, this.tableHeadingStyle, TABLE_HEADING_DS_SUMMARY_LCM_REFERENCEPROCESS_NAME);
			addCell(row, 13, this.tableHeadingSmallStyle, TABLE_HEADING_DS_SUMMARY_LCM_PROCESS_INSTANCES_COUNT);
		}
	}

	private void writeDatasetsSummaryEntries(Sheet dsSummarySheet, DatasetType dsType, Collection<SummaryExtractor.SummaryDTO> summaryList) {
		int i = 1;

		for (SummaryExtractor.SummaryDTO entry : summaryList) {
			Row row = dsSummarySheet.createRow(i);

			addCell(row, 0, this.styleTopLeft, entry.uuid);
			addCell(row, 1, this.styleTopLeft, entry.version);
			addCell(row, 2, this.styleTopLeft, entry.name);

			if (DatasetType.PROCESS.equals(dsType)) {
				SummaryExtractor.ProcessSummaryDTO processEntry = (SummaryExtractor.ProcessSummaryDTO) entry;

				addCell(row, 3, this.styleTopLeft, processEntry.geo);
				addCell(row, 4, this.styleTopLeft, processEntry.owner);
				addCell(row, 5, this.styleTopLeft, processEntry.processType);
				addCell(row, 6, this.wrapStyleTopLeft, processEntry.dataCutOffAndCompletenessPrinciples);
				addCell(row, 7, this.wrapStyleTopLeft, processEntry.deviationsFromLCIMethodApproaches);

			} else if (DatasetType.LCMODEL.equals(dsType)) {
				SummaryExtractor.LCModelSummaryDTO lcmEntry = (SummaryExtractor.LCModelSummaryDTO) entry;

				addCell(row, 3, this.styleTopLeft, lcmEntry.owner);
				addCell(row, 4, this.styleTopLeft, lcmEntry.resultingProcess1UUID);
				addCell(row, 5, this.styleTopLeft, lcmEntry.resultingProcess1Version);
				addCell(row, 6, this.styleTopLeft, lcmEntry.resultingProcess1Name);
				addCell(row, 7, this.styleTopLeft, lcmEntry.resultingProcess2UUID);
				addCell(row, 8, this.styleTopLeft, lcmEntry.resultingProcess2Version);
				addCell(row, 9, this.styleTopLeft, lcmEntry.resultingProcess2Name);
				addCell(row, 10, this.styleTopLeft, lcmEntry.referenceProcessUUID);
				addCell(row, 11, this.styleTopLeft, lcmEntry.referenceProcessVersion);
				addCell(row, 12, this.styleTopLeft, lcmEntry.referenceProcessName);
				addCell(row, 13, this.styleTopLeft, lcmEntry.processInstancesCount);
			}

			i++;
		}
	}

	private void addCell(Row row, int column, CellStyle style, String value) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	private void addCell(Row row, int column, CellStyle style, Integer value) {
		Cell cell = row.createCell(column);
		if (value != null)
			cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	public SXSSFWorkbook getWorkbook() {
		return workbook;
	}

}
