package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.liderahenk.installer.lider.wizard.dialogs.DatabaseWarningDialog;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class DatabaseInstallationStatus extends WizardPage
		implements IDatabasePage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

	public DatabaseInstallationStatus(LiderSetupConfig config) {
		super(DatabaseInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.4 " + Messages.getString("DATABASE_INSTALLATION"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		txtLogConsole = GUIHelper.createText(container, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {

		// Start database installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			final Display display = Display.getCurrent();
			final Shell shell = display.getActiveShell();

			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					setPageCompleteAsync(isInstallationFinished);

					// Clear text log console and progress bar before starting
					// installation.
					clearLogConsole();
					setProgressBar(0);

					printMessage("Initializing installation...");
					setProgressBar(10);

					printMessage("Setting up parameters for database password.");
					final String[] debconfValues = generateDebconfValues();
					setProgressBar(20);

					printMessage("Installing package...");

					if (config.getDatabaseInstallMethod() == InstallMethod.APT_GET) {
						try {
							SetupUtils.installPackageNoninteractively(config.getDatabaseIp(),
									config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
									config.getDatabasePort(), config.getDatabaseAccessKeyPath(),
									config.getDatabaseAccessPassphrase(), config.getDatabasePackageName(), null,
									debconfValues);

							setProgressBar(90);

							isInstallationFinished = true;

							printMessage("Successfully installed package: " + config.getDatabasePackageName());
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getDatabaseInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getDatabaseDebFileName());
						try {
							SetupUtils.installPackageNonInteractively(config.getDatabaseIp(),
									config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
									config.getDatabasePort(), config.getDatabaseAccessKeyPath(),
									config.getDatabaseAccessPassphrase(), deb, debconfValues, PackageInstaller.GDEBI);

							setProgressBar(90);

							isInstallationFinished = true;

							printMessage("Successfully installed package: " + deb.getName());
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getDatabaseInstallMethod() == InstallMethod.WGET) {
						try {
							printMessage("Downloading MariaDB .deb package from: "
									+ config.getDatabaseDownloadUrl());
							
							// In case of folder name clash use current time as postfix
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
							String timestamp = dateFormat.format(date);
							
							SetupUtils.downloadPackage(config.getDatabaseIp(),
									config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
									config.getDatabasePort(), config.getDatabaseAccessKeyPath(),
									config.getDatabaseAccessPassphrase(), "mariaDbTmp" + timestamp, "mariadb.deb", config.getDatabaseDownloadUrl());
							
							setProgressBar(30);
							
							printMessage("Successfully downloaded file.");
							
							printMessage("MariaDB is being installed to: " + config.getDatabaseIp()
							+ " from downloaded .deb file.");
							
							SetupUtils.installDownloadedPackageNonInteractively(config.getDatabaseIp(), config.getDatabaseAccessUsername(),
									config.getDatabaseAccessPasswd(), config.getDatabasePort(),
									config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase(), "mariaDbTmp" + timestamp, "mariadb.deb", debconfValues);
							
							printMessage("MariaDB has been successfully installed to: " + config.getDatabaseIp());
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
						
					} else {
						isInstallationFinished = false;
						// If any error occured user should be able to go
						// back and change selections etc.
						canGoBack = true;
						printMessage("Invalid installation method. Installation cancelled.");
					}
					setProgressBar(100);

					config.setInstallationFinished(isInstallationFinished);

					printMessage("Installation finished..");

					setPageCompleteAsync(isInstallationFinished);

					if (!config.getDatabaseIp().equals(config.getLiderIp())) {
						openWarningDialog();
					}

				}

				private void openWarningDialog() {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							// Create a dialog
							DatabaseWarningDialog warningDialog = new DatabaseWarningDialog(shell);

							// Open it
							warningDialog.open();
						}
					});
				}

				/**
				 * Prints log message to the log console widget
				 * 
				 * @param message
				 */
				private void printMessage(final String message) {
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
				 * Clears log console by set its content to empty string.
				 */
				private void clearLogConsole() {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							txtLogConsole.setText("");
						}
					});
				}

				/**
				 * Sets progress bar selection
				 * 
				 * @param selection
				 */
				private void setProgressBar(final int selection) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setSelection(selection);
						}
					});
				}

				/**
				 * Sets page complete status asynchronously.
				 * 
				 * @param isComplete
				 */
				private void setPageCompleteAsync(final boolean isComplete) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							setPageComplete(isComplete);
						}
					});
				}
			};

			Thread thread = new Thread(runnable);
			thread.start();
		}

		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);
	}

	/**
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("database.debconf.password") + " "
				+ config.getDatabaseRootPassword();
		String debconfPwdAgain = PropertyReader.property("database.debconf.password.again") + " "
				+ config.getDatabaseRootPassword();
		return new String[] { debconfPwd, debconfPwdAgain };
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
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
