package com.okworx.ilcd.validation;

import com.okworx.ilcd.validation.analyze.SummaryExtractor;
import com.okworx.ilcd.validation.analyze.flows.FlowsAnalyzer;
import com.okworx.ilcd.validation.events.EventsList;
import com.okworx.ilcd.validation.log.XLSLog;
import com.okworx.ilcd.validation.profile.ProfileManager;
import com.okworx.ilcd.validation.util.IUpdateEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.okworx.ilcd.validation.util.Statistics;
import org.apache.commons.lang3.StringUtils;

/**
 * Allows for chaining multiple Validators that all work on the same set of Objects
 *
 * @author oliver.kusche
 * @version $Id: $Id
 */
public class ValidatorChain extends AbstractDatasetsValidator implements IUpdateEventListener {

  protected List<IDatasetsValidator> validators = new ArrayList<>();

  private boolean batchMode = false;

  private String aspectName;

  private String aspectDescription;

  private int finishedValidators = 0;

  private HashMap<String, String> extraProperties = null;

  /**
   * <p>Constructor for ValidatorChain.</p>
   *
   * @param aspectName a {@link java.lang.String} object.
   */
  public ValidatorChain(String aspectName) {
    super();
    this.aspectName = aspectName;
    this.eventsList.setAspectName(aspectName);
  }

  /**
   * <p>Constructor for ValidatorChain.</p>
   */
  public ValidatorChain() {
    super();
    this.setProfile(ProfileManager.getInstance().getDefaultProfile());
  }

  public HashMap<String, String> getExtraProperties() {
		if (extraProperties == null) {
			extraProperties = new LinkedHashMap<>();
		}
    return extraProperties;
  }

  public void setExtraProperties(LinkedHashMap<String, String> extraProperties) {
    this.extraProperties = extraProperties;
  }

  /**
   * <p>validate.</p>
   *
   * @return a boolean.
   */
  public boolean validate() {

    boolean pass = this.eventsList.isPositive();

    boolean validateArchiveFlag = true;

    this.statistics = null;

    for (IDatasetsValidator v : this.validators) {
      if (validateArchiveFlag) {
        v.setValidateArchives(false);
        validateArchiveFlag = false;
      }
      v.setUpdateEventListener(this);
      v.setUpdateInterval(this.getUpdateInterval());
      v.setReportSuccesses(this.reportSuccesses);
      v.setProfile(this.profile);
			if (v instanceof AbstractDatasetsValidator) {
				((AbstractDatasetsValidator) v).setFileSource(this.fileSource);
			}
    }

    try {

      XLSLog xlsLog = null;

      boolean log2Xls = batchMode && (this.fileSource != null);

      // if object to validate is a ZIP or dir, create new XLS log
      if (log2Xls) {
        StringBuilder path = new StringBuilder();
        if (this.getFileSource().getParentFile() != null) {
          path.append(this.getFileSource().getParentFile().getName());
          path.append(File.separator);
        }
        path.append(this.getFileSource().getName());
        xlsLog = new XLSLog(this.validators, path.toString(), extraProperties);
      }

      boolean result;

      EventsList events = new EventsList("General");

      for (IDatasetsValidator v : this.validators) {
        if (Thread.currentThread().isInterrupted()) {
          log.info("operation was interrupted, aborting");
          updateStatusCancelled();
          break;
        }

        v.setObjectsToValidate(this.objectsToValidate);

        if (log2Xls) {
					if (v instanceof FlowsAnalyzer) {
						((FlowsAnalyzer) v).setWorkbook(xlsLog.getWorkbook());
					}
        }

        result = v.validate();

				if (!result || !v.getEventsList().isPositive()) {
					pass = false;
				}

        events.addAll(v.getEventsList().getEvents());

        if (this.statistics == null) {
            this.statistics = v.getStatistics();
        } else {
            this.statistics.merge(v.getStatistics());
        }

        if (log2Xls) {
          // add line to summary
					if (!(v instanceof FlowsAnalyzer)) {
						xlsLog.validationSummaryAddResult(result, v);
					}
          // write messages to log
          xlsLog.writeEvents(v);
        }

        finishedValidators++;
      }

      // any general events like invalid XML are stored in this.eventsList,
      // we need to make sure these are also honored in batch mode
			if (log.isDebugEnabled()) {
				log.debug("general events: " + this.eventsList.getErrorCount());
			}

      this.setAspectName("General");

      boolean passGeneral = !this.eventsList.hasErrors();

      // document any general errors
      if (log2Xls && !passGeneral) {
        // add line to summary
        xlsLog.validationSummaryAddResult(false, this);
        // write messages to log
        xlsLog.writeEvents(this);
      }

      // now that we have processed the general events,
      // we add all events from the validators
      if (this.statistics == null) {
        this.statistics = this.getStatistics();
      } else {
        this.statistics.merge(this.getStatistics());
      }

      this.eventsList.addAll(events.getEvents());

      String aspects = getAllAspects();
      if (!aspects.isEmpty()){
        this.statistics.setAspects(aspects);
      }

      log.info(this.eventsList.getEvents().size() + " events total");

      if (log2Xls) {
        // write stats to log
        xlsLog.writeStats(this.statistics, this.eventsList);

        // write processes summary
        xlsLog.writeDatasetsSummary(this.validators);

        // close log
        xlsLog.writeAndClose(this.getFileSource().getAbsolutePath(), pass);
      }

    } catch (InterruptedException e) {
      updateStatusCancelled();
      updateProgress(0);
      return false;
    } catch (IOException e) {
      return false;
    }

    return pass;
  }

  /**
   * <p>getAllAspects.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAllAspects() {
    StringBuilder sb = new StringBuilder();
    boolean firstItem = true;
    for (IValidator v : this.validators) {
      if (!firstItem) {
        sb.append(", ");
      } else {
        firstItem = false;
      }
      sb.append(v.getAspectName());
      log.debug(v.getAspectName());
    }
    return sb.toString();
  }

  /**
   * <p>add.</p>
   *
   * @param validator a {@link com.okworx.ilcd.validation.IDatasetsValidator} object.
   */
  public void add(IDatasetsValidator validator) {
    this.validators.add(validator);
  }

  @Deprecated
  public void initPresetValidators() {
    if (this.profile != null && StringUtils.isNotBlank(this.profile.getActiveAspects())) {
      StringTokenizer st = new StringTokenizer(profile.getActiveAspects(), ",");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        this.add(createValidator(token));
      }
    }
  }

  private IDatasetsValidator createValidator(String aspectName) {

    switch (aspectName) {
      case "Links":
        return new LinkValidator();
      case "Orphaned Items":
        return new OrphansValidator();
      case "XML Schema Validity":
        return new SchemaValidator();
      case "Profile Specific Rules":
      case "Custom Validity":
        return new XSLTStylesheetValidator();
      case "Reference Flows":
        return new ReferenceFlowValidator();
      case "Categories":
        return new CategoryValidator();
      case "DSSummary":
        return new SummaryExtractor();
    }

    return null;
  }

  /**
   * <p>Getter for the field <code>validators</code>.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<IDatasetsValidator> getValidators() {
    return validators;
  }

  /**
   * <p>Setter for the field <code>validators</code>.</p>
   *
   * @param validators a {@link java.util.List} object.
   */
  public void setValidators(List<IDatasetsValidator> validators) {
    this.validators = validators;
  }

  /**
   * <p>Getter for the field <code>aspectName</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAspectName() {
    return aspectName;
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

  /**
   * <p>Getter for the field <code>aspectDescription</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAspectDescription() {
    return aspectDescription;
  }

  /**
   * <p>Setter for the field <code>aspectDescription</code>.</p>
   *
   * @param aspectDescription a {@link java.lang.String} object.
   */
  public void setAspectDescription(String aspectDescription) {
    this.aspectDescription = aspectDescription;
  }

  /**
   * {@inheritDoc}
   */
  public void updateProgress(double percentFinished) {
		if (this.getUpdateEventListener() != null) {
			this.getUpdateEventListener()
					.updateProgress((this.finishedValidators + percentFinished) / this.validators.size());
		}
  }

  /**
   * {@inheritDoc}
   */
  public void updateStatus(String statusMessage) {
		if (this.getUpdateEventListener() != null) {
			this.getUpdateEventListener().updateStatus(statusMessage);
		}
  }

  public boolean isBatchMode() {
    return batchMode;
  }

  public void setBatchMode(boolean batchMode) {
    this.batchMode = batchMode;
  }

}
