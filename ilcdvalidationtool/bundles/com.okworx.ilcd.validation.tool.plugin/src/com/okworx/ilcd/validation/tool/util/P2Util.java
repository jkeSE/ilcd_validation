package com.okworx.ilcd.validation.tool.util;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.okworx.ilcd.validation.tool.resource.LocalizationString;

@Creatable
@Singleton
public class P2Util {
	Logger logger = LogManager.getLogger(getClass());
	
	private static final String REPOSITORY_LOC = System.getProperty("UpdateHandler.Repo",
			"https://www.okworx.com/software/ILCDValidationTool/");

	private boolean doUpdatesIfAvailable = false;
	private boolean showNoUpdatesMessages;
	private boolean askBeforeUpdate;

	@Inject
	private UISynchronize sync;

	@Inject
	EPartService partService;

	@Inject
	IProvisioningAgent agent;

	@Inject
	@Translation
	private LocalizationString localizationString;

	public void update(IWorkbench workbench, Shell shell, boolean showNoUpdatesMessage, boolean askBeforeUpdate) {
		
		logger.debug("checking for updates from {}", REPOSITORY_LOC);
		
		// reset state variables
		doUpdatesIfAvailable = false;
		this.showNoUpdatesMessages = showNoUpdatesMessage;
		this.askBeforeUpdate = askBeforeUpdate;
		
		Job updateJob = new Job("Checking for updates ...") {			
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				return checkForUpdates(agent, shell, sync, workbench, monitor);
			}
		};
		updateJob.schedule();
	}

	private IStatus checkForUpdates(final IProvisioningAgent agent, final Shell shell, final UISynchronize sync,
			final IWorkbench workbench, IProgressMonitor monitor) {

		UpdateOperation operation;
		
		try {
			logger.debug("configure update operation");
			
			// configure update operation
			final ProvisioningSession session = new ProvisioningSession(agent);

			operation = new UpdateOperation(session);

			configureUpdate(operation);

			// check for updates, this causes I/O
			final IStatus status = operation.resolveModal(monitor);

			logger.debug("status is {}", status.getCode());
			
			// failed to find updates (inform user and exit)
			if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
				if (showNoUpdatesMessages) {
					showMessage(shell, sync);
				}
				logger.debug("nothing to update.");
				return Status.CANCEL_STATUS;
			}

			if (askBeforeUpdate) {
				logger.debug("asking whether we should update");
				showAvailableUpdateMessage(shell, sync);
			} else {
				doUpdatesIfAvailable = true;
			}
			if (!doUpdatesIfAvailable) {
				return Status.CANCEL_STATUS;
			}

			logger.debug("now run installation");

			// run installation
			sync.syncExec(new Runnable() {
				
				@Override
				public void run() {
					ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(shell);
					try {
						progressMonitorDialog.run(true, false, new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor rootMonitor) throws InvocationTargetException, InterruptedException {
								SubMonitor monitor = SubMonitor.convert(rootMonitor, "Updating Application...", 100);
								final ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
	
								// updates cannot run from within Eclipse IDE!
								if (provisioningJob == null) {
									logger.error("Trying to update from the Eclipse IDE? This won't work!");
									return;
								}
								configureProvisioningJob(provisioningJob, shell, sync, workbench); 
								
								provisioningJob.setUser(true);
								provisioningJob.schedule();
								provisioningJob.join();
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
						e.printStackTrace();
					}				
				}
			});

		} catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
		}

		return Status.OK_STATUS;
	}

	private void configureProvisioningJob(ProvisioningJob provisioningJob, final Shell shell, final UISynchronize sync,
			final IWorkbench workbench) {

		// register a job change listener to track
		// installation progress and notify user upon success
		provisioningJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					sync.syncExec(new Runnable() {

						@Override
						public void run() {
							boolean restart = MessageDialog.openQuestion(shell,
									localizationString.P2Util_UpdatesInstalledRestartQuestionTitle,
									localizationString.P2Util_UpdatesInstalledRestartQuestion);
							if (restart) {
								logger.debug("restarting workbench");
								workbench.restart();
							}
						}
					});

				}
				super.done(event);
			}
		});

	}

	private void showAvailableUpdateMessage(final Shell parent, final UISynchronize sync) {
		sync.syncExec(() -> {
			doUpdatesIfAvailable = MessageDialog.openQuestion(parent,
					localizationString.P2Util_UpdatesFoundQuestionTitle,
					localizationString.P2Util_UpdatesFoundQuestion);
		});
	}

	private void showMessage(final Shell parent, final UISynchronize sync) {
		sync.syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openWarning(parent, localizationString.P2Util_NoUpdatesMessageTitle,
						localizationString.P2Util_NoUpdatesMessage);
			}
		});
	}

	private UpdateOperation configureUpdate(final UpdateOperation operation) {
		
		logger.debug("configuring update");
		
		// create uri and check for validity
		URI uri = null;
		try {
			uri = new URI(REPOSITORY_LOC);
		} catch (final URISyntaxException e) {
			logger.error(e);
			return null;
		}
		
		logger.debug("using URI {}", uri.toString());

		// set location of artifact and metadata repo
		logger.debug("setting artifact repos");
		operation.getProvisioningContext().setArtifactRepositories(new URI[] { uri });
		logger.debug("setting metadata repos");
		operation.getProvisioningContext().setMetadataRepositories(new URI[] { uri });
		return operation;
	}
}