package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.io.File;

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

		GridData progressGd = new GridData();
		progressGd.heightHint = 40;
		progressBar.setLayoutData(progressGd);

	}

	@Override
	public IWizardPage getNextPage() {
		// Start Ahenk installation here.
		// To prevent triggering installation again (i.e. when clicked "next"
		// after installation finished), set isInstallationFinished to true when
		// its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			setPageCompleteAsync(false, Display.getCurrent());

			setProgressBar(10, Display.getCurrent());

			printMessage("Initializing installation...", Display.getCurrent());
			
			// Check installation method
			if (config.getAhenkInstallMethod() == InstallMethod.APT_GET) {

				// Calculate progress bar increment size
				final Integer increment = (Integer) (70 / config.getIpList().size());

				for (int i = 0; i < config.getIpList().size(); i++) {

					final int index = i;
					
					final String ip = config.getIpList().get(i);
					
					final Display display = Display.getCurrent();

					final int selection = progressBar.getSelection();

					// Execute each installation in a new thread.
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {
								printMessage("Trying to connect to: " + ip, display);

								// Check authorization before starting
								// installation
								// TODO port eklenecek
								final boolean canConnect = SetupUtils.canConnectViaSsh(ip, config.getUsernameCm(),
										config.getPasswordCm(), 22, config.getPrivateKeyAbsPath());

								// If we can connect to machine
								if (canConnect) {
									printMessage("Successfully connected to: " + ip, display);

									printMessage("Ahenk is being installed to: " + ip + " from catalog.", display);

									// TODO port eklenecek
									SetupUtils.installPackage(ip, config.getUsernameCm(), config.getPasswordCm(), 22,
											config.getPrivateKeyAbsPath(), "ahenk", null);

									setProgressBar(selection + increment, display);

									printMessage("Ahenk has been successfully installed to: " + ip, display);
									
									if (index == config.getIpList().size() - 1) {
										printMessage("Installation finished.", display);
										
										setProgressBar(100, display);
										
										updateFinishButton(display);
									}
								}

							} catch (SSHConnectionException e) {
								// Also update progress bar when installation
								// fails
								setProgressBar(selection + increment, display);

								isInstallationFinished = false;

								// If any error occured user should be able to
								// go back and change selections etc.
								canGoBack = true;

								printMessage("Error occurred: " + e.getMessage(), display);

								e.printStackTrace();
							} catch (CommandExecutionException e) {
								// Also update progress bar when installation
								// fails
								setProgressBar(selection + increment, display);

								isInstallationFinished = false;

								// If any error occured user should be able to
								// go back and change selections etc.
								canGoBack = true;

								printMessage("Error occurred: " + e.getMessage(), display);

								e.printStackTrace();
							}
						}
					};

					Thread thread = new Thread(runnable);
					thread.start();
				}

				printMessage("Ahenk installation finished.", Display.getCurrent());
			} else if (config.getAhenkInstallMethod() == InstallMethod.PROVIDED_DEB) {

				// Calculate progress bar increment size
				final Integer increment = (Integer) (70 / config.getIpList().size());

				final int selection = progressBar.getSelection();
				
				// Execute each installation in a new thread.
				for (int i = 0; i < config.getIpList().size(); i++) {

					final int index = i;
					
					final String ip = config.getIpList().get(i);
					
					final Display display = Display.getCurrent();

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {

								printMessage("Trying to connect to: " + ip, display);

								// TODO port eklenecek
								final boolean canConnect = SetupUtils.canConnectViaSsh(ip, config.getUsernameCm(),
										config.getPasswordCm(), 22, config.getPrivateKeyAbsPath());

								if (canConnect) {
									printMessage("Successfully connected to: " + ip, display);

									printMessage("Ahenk is being installed to: " + ip + " from given DEB package.",
											display);

									File debPackage = new File(config.getDebFileAbsPath());

									// TODO port eklenecek
									SetupUtils.installPackage(ip, config.getUsernameCm(), config.getPasswordCm(), 22,
											config.getPrivateKeyAbsPath(), debPackage);

									setProgressBar(selection + increment, display);

									printMessage("Ahenk has been successfully installed to: " + ip, display);
									
									if (index == config.getIpList().size() - 1) {
										printMessage("Installation finished.", display);
										
										setProgressBar(100, display);
										
										isInstallationFinished = true;
										
										updateFinishButton(display);
									}
								}

							} catch (SSHConnectionException e) {
								// Also update progress bar when installation
								// fails
								setProgressBar(selection + increment, display);

								isInstallationFinished = false;

								// If any error occured user should be able to
								// go back and change selections etc.
								canGoBack = true;

								printMessage("Error occurred: " + e.getMessage(), display);

								e.printStackTrace();
							} catch (CommandExecutionException e) {
								// Also update progress bar when installation
								// fails
								setProgressBar(selection + increment, display);

								isInstallationFinished = false;

								// If any error occured user should be able to
								// go back and change selections etc.
								canGoBack = true;

								printMessage("Error occurred: " + e.getMessage(), display);

								e.printStackTrace();
							}
						}
					};

					Thread thread = new Thread(runnable);
					thread.start();
				}

			} else {
				// If installation method is not set, show an error message and
				// do not try to install
				isInstallationFinished = false;

				// If any error occured user should be able to go back and
				// change selections etc.
				canGoBack = true;

				// Set progress bar to complete
				setProgressBar(100, Display.getCurrent());

				printMessage("Invalid installation method. Installation cancelled.", Display.getCurrent());
			}
			
			setPageCompleteAsync(isInstallationFinished, Display.getCurrent());
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
	 * Sets progress bar selection
	 * 
	 * @param selection
	 */
	private void setProgressBar(final int selection, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressBar.setSelection(selection);
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
	
	/**
	 * Updates finish button asynchronously.
	 */
	private void updateFinishButton(Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				getContainer().updateButtons();
			}
		});
	}
	
	@Override
	public IWizardPage getPreviousPage() {
		// Do not allow to go back from this page if installation completed successfully.
		if (canGoBack) {
			return super.getPreviousPage();
		}
		else {
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
