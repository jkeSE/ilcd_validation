package com.okworx.ilcd.validation.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * <p>ReferenceListCreator class.</p>
 *
 * Run on EF-LCIAMethod_CF XLS workbook
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ReferenceListCreator {

	protected final Logger log = org.apache.logging.log4j.LogManager.getLogger(this.getClass());

	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	public static void main(String[] args) {
		// example args (working dir: ~/git/ilcdvalidation-profiles): reference_data/EF-LCIAMethod_CF(EF-v4.0-BETA).xlsx ef/EF-4.0/src/main/resources/eu/europa/ec/jrc/lca/ilcd/reference EF_elementary_flows_rev4.0beta.ser.z EF_reference_objects_rev4.0beta.ser.z
		if (args.length < 4) {
			System.err.println("Usage: [SourceFile] [DestinationPath] [ReferenceFlowDest] [ReferenceObjectsFilename]");
			System.exit(-1);
		}
		String sourceFile = FileSystems.getDefault().getPath(args[0]).toAbsolutePath().normalize().toString();
		String referenceFlowFilename = FileSystems.getDefault().getPath(args[1] + File.separator + args[2]).toAbsolutePath().normalize().toString();
		String referenceObjectsFilename = FileSystems.getDefault().getPath(args[1] + File.separator + args[3]).toAbsolutePath().normalize().toString();
		System.out.println("Source: '" + sourceFile + "'");
		System.out.println("Reference Flow Filename: '" + referenceFlowFilename + "'");
		System.out.println("Reference Objects Filename: '" + referenceObjectsFilename + "'");

		ReferenceListCreator rlc = new ReferenceListCreator();
		rlc.processXLSv2(sourceFile, referenceFlowFilename, referenceObjectsFilename);
	}

	private void serialize(final HashMap<String, String> map, final String filename) {
		serialize(map, null, filename);
	}

	private void serialize(final HashMap<String, String> map, final String pathPrefix, final String filename) {
		try {
			log.info("serializing map with " + map.size() + " entries");
			String path = ((pathPrefix == null) ? filename : pathPrefix.concat(filename));
			FileOutputStream fos = new FileOutputStream(path);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			oos.writeObject(map);
			oos.close();
		} catch (IOException e) {
			log.error(e);
		}
	}

	private void processXLSv2(String file, String elementaryFlowDest, String referenceObjectsDest) {
		try {
			org.apache.poi.ss.usermodel.Workbook wbk = new XSSFWorkbook(new File(file));

			HashMap<String, String> mapElementaryFlows = new HashMap<String, String>();
			HashMap<String, String> mapOthers = new HashMap<String, String>();

			// read sheet with elem. flows
			readSheet(wbk.getSheetAt(1), mapElementaryFlows, 0, 1, true);

			// read sheet with other flows
			readSheet(wbk.getSheetAt(2), mapElementaryFlows, 0, 1, false);

			// read sheet with LCIA methods
			readSheet(wbk.getSheetAt(3), mapOthers, 0, 1, false);

			// read sheet with unit groups
			readSheet(wbk.getSheetAt(5), mapOthers, 3, 1, false);

			// read sheet with flow properties
			readSheet(wbk.getSheetAt(6), mapOthers, 4, 3, false);

			serialize(mapElementaryFlows, elementaryFlowDest);
			serialize(mapOthers, referenceObjectsDest);


		} catch (IOException e) {
			log.error(e);
		} catch (InvalidFormatException e) {
			log.error(e);
		}

	}

	private void readSheet(org.apache.poi.ss.usermodel.Sheet sheet, Map<String, String> map, int uuidCol, int nameCol, boolean flows) {

		Iterator<Row> iterator = sheet.iterator();

		// skip first line
		iterator.next();

		int line = 1;

		String last = null;

		while (iterator.hasNext()) {

			Row currentRow = iterator.next();

			line++;

			log.trace("processing line " + line);

			String uuid = readCell(currentRow, uuidCol);
			if (uuid != "")
				uuid = uuid.toLowerCase();

			// stop if we've reached the end of the table
			if (StringUtils.isBlank(uuid)) {
				log.info("line " + (line - 1) + "  " + last);
				log.warn("end of document reached in line " + line);
				break;
			}

			String name = readCell(currentRow, nameCol);
			
			log.trace(uuid + " " + name);
			
			String data;

			if (flows) {
				String casNo = readCell(currentRow, 2);
				String category = readCell(currentRow, 4).concat("/")
						.concat(readCell(currentRow, 5)).concat("/")
						.concat(readCell(currentRow, 6));
				log.trace(category);

				data = name.concat("; ").concat(casNo).concat("; ").concat(category);
			} else {
				data = name;
			}

			if (line == 2)
				log.info("line " + line + "  " + uuid.concat(" ").concat(data));

			if (map.containsKey(uuid))
				log.warn("found duplicate key " + uuid);

			map.put(uuid, data);
			last = uuid.concat(" ").concat(data);
		}

		log.info(" processed " + (line - 1) + " rows");
		log.info(" resulting map has " + map.size() + " entries");
	}

	private String readCell(Row row, int col) {
		try {
			return row.getCell(col).getStringCellValue();
		} catch (NullPointerException e) {
			return "";
		}
	}

	private void test() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("15e6da4c-ace4-42a3-abff-9138ac65a7e6",
				"(+)-bornan-2-one; 000464-49-3; Emissions/Emissions to soil/Emissions to agricultural soil");
		map.put("16b7ee1b-798a-46e2-bd79-e2b74c715bfb",
				"(+)-bornan-2-one; 000464-49-3; Emissions/Emissions to soil/Emissions to urban air close to ground");
		map.put("1d10edcf-c1b8-470c-8f20-311f25b4bab8",
				"(+)-bornan-2-one; 000464-49-3; Emissions/Emissions to soil/Emissions to non-urban air or from high stacks");

		serialize(map,
				"src/main/resources/profiles/ILCD_1.1_profile_1.1.contents/eu/europa/ec/jrc/lca/ilcd/reference/elementary_flows.ser.z");

	}

}
