package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class AhenkInstallationStatusPage extends WizardPage implements ControlNextEvent, InstallationStatusPage {

	private AhenkSetupConfig config = null;

	// Widgets
	private Composite mainContainer = null;

	private ProgressBar progressBar;

	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

	private int progressBarPercent;

	// Status variable for the possible errors on this page
	IStatus ipStatus;

	public AhenkInstallationStatusPage(AhenkSetupConfig config) {
		super(AhenkInstallationStatusPage.class.getName(), Messages.getString("INSTALLATION_OF_AHENK"), null);

		setDescription(Messages.getString("INSTALLATION_STATUS"));

		this.config = config;

		ipStatus = new Status(IStatus.OK, "not_used", 0, "", null);
	}

	@Override
	public void createControl(Composite parent) {

		// create main container
		mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		txtLogConsole = GUIHelper.createText(mainContainer, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(mainContainer, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);

		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		progressBar.setLayoutData(progressGd);

	}

	@Override
	public IWizardPage getNextPage() {
		// Start Ahenk installation here. To prevent triggering installation
		// again, set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			canGoBack = false;

			// Create a thread pool
			final ExecutorService executor = Executors.newCachedThreadPool();

			setProgressBar(10, Display.getCurrent());

			printMessage("Initializing installation...", Display.getCurrent());

			// Get display before new main runnable
			final Display display = Display.getCurrent();

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation I have to wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {
					// Check installation method
					if (config.getAhenkInstallMethod() == InstallMethod.APT_GET) {

						// Calculate progress bar increment size
						final Integer increment = (Integer) (90 / config.getIpList().size());

						for (final String ip : config.getIpList()) {

							// Execute each installation in a new runnable.
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										printMessage("Trying to connect to: " + ip, display);

										// Check authorization before starting
										// installation
										final boolean canConnect = SetupUtils.canConnectViaSsh(ip,
												config.getUsernameCm(), config.getPasswordCm(), config.getPort(),
												config.getPrivateKeyAbsPath(), config.getPassphrase());

										// If we can connect to machine install
										// Ahenk
										if (canConnect) {
											printMessage("Successfully connected to: " + ip, display);

											printMessage("Ahenk is being installed to: " + ip + " from catalog.",
													display);

											// TODO gedit değiştirilecek
											SetupUtils.installPackage(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(), "gedit",
													null);

											setProgressBar(increment, display);

											printMessage("Ahenk has been successfully installed to: " + ip, display);

										} else {
											printMessage(
													"Could not connect to: " + ip + " | Passing over this machine..",
													display);

											setProgressBar(increment, display);
										}

									} catch (SSHConnectionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									} catch (CommandExecutionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									}
								}
							};

							executor.execute(runnable);

						}

					} else if (config.getAhenkInstallMethod() == InstallMethod.PROVIDED_DEB) {

						// Calculate progress bar increment size
						final Integer increment = (Integer) (90 / config.getIpList().size());

						for (final String ip : config.getIpList()) {

							// Execute each installation in a new runnable.
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										printMessage("Trying to connect to: " + ip, display);

										// Check authorization before starting
										// installation
										final boolean canConnect = SetupUtils.canConnectViaSsh(ip,
												config.getUsernameCm(), config.getPasswordCm(), config.getPort(),
												config.getPrivateKeyAbsPath(), config.getPassphrase());

										// If we can connect to machine install
										// Ahenk
										if (canConnect) {
											printMessage("Successfully connected to: " + ip, display);

											printMessage(
													"Ahenk is being installed to: " + ip + " from provided DEB file.",
													display);

											File debPackage = new File(config.getDebFileAbsPath());

											SetupUtils.installPackage(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(), debPackage);

											setProgressBar(increment, display);

											printMessage("Ahenk has been successfully installed to: " + ip, display);

										} else {
											printMessage(
													"Could not connect to: " + ip + " | Passing over this machine..",
													display);

											setProgressBar(increment, display);
										}

									} catch (SSHConnectionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									} catch (CommandExecutionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									}
								}
							};

							executor.execute(runnable);

						}

					} else if (config.getAhenkInstallMethod() == InstallMethod.WGET) {

						// Calculate progress bar increment size
						final Integer increment = (Integer) (90 / config.getIpList().size());

						for (final String ip : config.getIpList()) {

							// Execute each installation in a new runnable.
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										printMessage("Trying to connect to: " + ip, display);

										// Check authorization before starting
										// installation
										final boolean canConnect = SetupUtils.canConnectViaSsh(ip,
												config.getUsernameCm(), config.getPasswordCm(), config.getPort(),
												config.getPrivateKeyAbsPath(), config.getPassphrase());

										// If we can connect to machine install
										// Ahenk
										if (canConnect) {
											printMessage("Successfully connected to: " + ip, display);

											printMessage("Downloading Ahenk .deb package from: "
													+ config.getAhenkDownloadUrl(), display);

											SetupUtils.downloadPackage(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(), "ahenk.deb",
													config.getAhenkDownloadUrl());

											printMessage("Successfully downloaded file", display);

											printMessage("Ahenk is being installed to: " + ip
													+ " from downloaded .deb file.", display);
											SetupUtils.installDownloadedPackage(ip, config.getUsernameCm(),
													config.getPasswordCm(), config.getPort(),
													config.getPrivateKeyAbsPath(), config.getPassphrase(), "ahenk.deb");

											setProgressBar(increment, display);

											printMessage("Ahenk has been successfully installed to: " + ip, display);

										} else {
											printMessage(
													"Could not connect to: " + ip + " | Passing over this machine..",
													display);

											setProgressBar(increment, display);
										}

									} catch (SSHConnectionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									} catch (CommandExecutionException e) {
										// Also update progress bar when
										// installation fails
										setProgressBar(increment, display);

										isInstallationFinished = false;

										// If any error occured user should be
										// able to go back and change selections
										// etc.
										canGoBack = true;

										printMessage("Error occurred: " + e.getMessage(), display);

										e.printStackTrace();
									}
								}
							};

							executor.execute(runnable);

						}

					} else {
						// If installation method is not set, show an error
						// message and do not try to install
						isInstallationFinished = false;

						// If any error occured user should be able to go back
						// and change selections etc.
						canGoBack = true;

						// Set progress bar to complete
						setProgressBar(100, Display.getCurrent());

						printMessage("Invalid installation method. Installation cancelled.", Display.getCurrent());
					}

					executor.shutdown();

					try {
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					isInstallationFinished = true;

					// Set progress bar to complete
					setProgressBar(100, display);

					printMessage("Installation finished.", display);

					config.setInstallationFinished(isInstallationFinished);

					// To enable finish button
					setPageCompleteAsync(isInstallationFinished, display);
				}
			};

			Thread thread = new Thread(mainRunnable);
			thread.start();
		}
		return super.getNextPage();
	}

	/**
	 * Prints log message to the log console widget
	 * 
	 * @param message
	 */
	private void printMessage(final String message, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
						? txtLogConsole.getText() + "\n" : "") + message);
			}
		});
	}

	/**
	 * Sets progress bar selection (Increases progress bar percentage by
	 * increment value.)
	 * 
	 * @param selection
	 */
	private void setProgressBar(final int increment, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressBarPercent += increment;

				progressBar.setSelection(progressBarPercent);
			}
		});
	}

	/**
	 * Sets page complete status asynchronously.
	 * 
	 * @param isComplete
	 */
	private void setPageCompleteAsync(final boolean isComplete, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setPageComplete(isComplete);
			}
		});
	}

	@Override
	public IWizardPage getPreviousPage() {
		// Do not allow to go back from this page if installation completed
		// successfully.
		if (canGoBack) {
			return super.getPreviousPage();
		} else {
			return null;
		}
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}
}
