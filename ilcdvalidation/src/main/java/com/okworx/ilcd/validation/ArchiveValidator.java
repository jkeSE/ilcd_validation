package com.okworx.ilcd.validation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.java.truevfs.access.TFile;

import com.okworx.ilcd.validation.common.Constants;
import com.okworx.ilcd.validation.events.Severity;
import com.okworx.ilcd.validation.events.ValidationEvent;
import com.okworx.ilcd.validation.reference.DatasetReference;
import com.okworx.ilcd.validation.reference.IDatasetReference;

/**
 * Checks whether a supplied ZIP archive is a compliant ILCD archive.
 *
 * Criteria are:
 *
 * - the file handed over actually is a ZIP archive
 * - a folder with the name "ILCD" is present at root level
 * - within this folder, there are separate folders for each dataset type
 * - a folder "META-INF" containing a MANIFEST.MF file is present at root level
 * - the ILCD format version is indicated in the MANIFEST.MF file
 *
 * TODO check for manifest and version
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ArchiveValidator extends AbstractValidator implements IValidator {

	public static final String ASPECT_NAME = "Archive Structure";

	private File archiveToValidate;

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
		return "Checks whether a supplied ZIP archive is a compliant ILCD archive.";
	}

	/**
	 * <p>Setter for the field <code>archiveToValidate</code>.</p>
	 *
	 * @param source a {@link java.io.File} object.
	 */
	public void setArchiveToValidate(File source) {
		this.archiveToValidate = source;
	}

	/**
	 * <p>reset.</p>
	 */
	public void reset() {
		super.reset();
		this.archiveToValidate = null;
	}

	/**
	 * <p>validate.</p>
	 *
	 * @return a boolean.
	 */
	public boolean validate() {

		boolean result = false;

		IDatasetReference ref = new DatasetReference(null, null,
				this.archiveToValidate.getAbsolutePath(),
				this.archiveToValidate.getName());

		TFile zip = new TFile(this.archiveToValidate);

		if (!(zip.isArchive() && zip.isDirectory() && zip.length() == 0)) {
			this.eventsList.add(new ValidationEvent(this.getAspectName(), Severity.ERROR, ref,
					"File is not a ZIP archive."));
			return result;
		}

		Set<String> rootFolders = new HashSet<String>();

		TFile ilcdFolder = null;

		for (TFile f : zip.listFiles()) {
			if (f.isDirectory()) {
				rootFolders.add(f.getName());
				if (f.getName().equals(Constants.FOLDER_NAME_ARCHIVE_ROOT))
					ilcdFolder = f;
			}
		}

		if (!rootFolders.contains(Constants.FOLDER_NAME_ARCHIVE_ROOT)) {
			this.eventsList.add(new ValidationEvent(this.getAspectName(), Severity.ERROR, ref,
					"No root folder '" + Constants.FOLDER_NAME_ARCHIVE_ROOT
							+ "' found."));
			return result;
		}

		Set<String> datasetFolders = new HashSet<String>();
		for (TFile f : ilcdFolder.listFiles()) {
			datasetFolders.add(f.getName());
		}

		if (!(datasetFolders.contains(Constants.FOLDER_NAME_CONTACT)
				|| datasetFolders.contains(Constants.FOLDER_NAME_FLOW)
				|| datasetFolders.contains(Constants.FOLDER_NAME_FLOW_PROPERTY)
				|| datasetFolders.contains(Constants.FOLDER_NAME_LCIA_METHOD)
				|| datasetFolders.contains(Constants.FOLDER_NAME_PROCESS)
				|| datasetFolders.contains(Constants.FOLDER_NAME_SOURCE) || datasetFolders
					.contains(Constants.FOLDER_NAME_UNIT_GROUP))) {
			this.eventsList.add(new ValidationEvent(this.getAspectName(), Severity.ERROR, ref,
					"No valid dataset folders found."));
			return result;
		}

		result = true;
		
		return result;
	}

}
