package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.IOutputStreamProvider;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LiderInstallationStatus extends WizardPage implements ILiderPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	boolean isInstallationFinished = false;

	public LiderInstallationStatus(LiderSetupConfig config) {
		super(LiderInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("5.4 " + Messages.getString("LIDER_INSTALLATION"));
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
		// Start Lider installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished) {

			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					printMessage("Initializing installation...");
					setProgressBar(10);

					printMessage("Installing package...");

					if (config.getLiderInstallMethod() == InstallMethod.APT_GET) {
						try {
							SetupUtils.installPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), "lider", null);
							setProgressBar(90);
							isInstallationFinished = true;
							printMessage("Successfully installed package: " + config.getLiderPackageName());
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getLiderInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getLiderDebFileName());
						try {
							SetupUtils.installPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), deb,
									PackageInstaller.DPKG);
							setProgressBar(90);
							isInstallationFinished = true;
							printMessage("Successfully installed package: " + deb.getName());
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getLiderInstallMethod() == InstallMethod.TAR_GZ) {
						File tar = new File(config.getLiderTarFileName());

						try {
							SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), "rm -rf /tmp/lider-temp && mkdir -p /tmp/lider-temp");
							
							printMessage("Copying TAR file to: " + config.getLiderIp());
							setProgressBar(30);

							SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), tar,
									"/tmp/lider-temp");

							printMessage("TAR file successfully copied to: " + config.getLiderIp());
							setProgressBar(60);
							
							printMessage("Extracting TAR file: " + tar.getName());
							SetupUtils.extractTarFile(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), "/tmp/lider-temp/" + tar.getName(), "/opt/");
							setProgressBar(90);
							isInstallationFinished = true;
							printMessage("Successfully extracted package: " + tar.getName());
							printMessage("Installation completed.");
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}

					} else if (config.getLiderInstallMethod() == InstallMethod.WGET) {
						try {
							printMessage("Downloading Lider .deb package from: " + config.getLiderDownloadUrl());

							// In case of folder name clash use current time as
							// postfix
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
							String timestamp = dateFormat.format(date);

							SetupUtils.downloadPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"liderTmpDir" + timestamp, "lider.deb", config.getLiderDownloadUrl());

							setProgressBar(30);

							printMessage("Successfully downloaded file.");

							printMessage("Lider is being installed to: " + config.getLiderIp()
									+ " from downloaded .deb file.");
							SetupUtils.installDownloadedPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"liderTmpDir" + timestamp, "lider.deb", PackageInstaller.DPKG);

							printMessage("Lider has been successfully installed to: " + config.getLiderIp());
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}

					} else {
						isInstallationFinished = false;
						printMessage("Invalid installation method. Installation cancelled.");
					}
					
					File liderConfigFile;
					File datasourceConfigFile;
					try {
						liderConfigFile = new File(config.getLiderAbsPathConfFile());
						datasourceConfigFile = new File(config.getDatasourceAbsPathConfFile());

						// Copy tr.org.liderahenk.cfg
						SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
										config.getLiderAccessPasswd(), config.getLiderPort(),
										config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), liderConfigFile, "/opt/" + PropertyReader.property("lider.package.name") + "/etc/");
						
						// Copy tr.org.liderahenk.datasource.cfg
						SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(),
								config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), datasourceConfigFile, "/opt/" + PropertyReader.property("lider.package.name") + "/etc/");
						
						String command = "nohup /opt/" + PropertyReader.property("lider.package.name") + "/bin/karaf &";
						System.out.println("Command --> " + command);
						// Start Karaf
						SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(),
								config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), command, new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
						});
						
					} catch (SSHConnectionException e) {
						isInstallationFinished = false;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					} catch (CommandExecutionException e) {
						isInstallationFinished = false;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					}

					setProgressBar(100);

					config.setInstallationFinished(isInstallationFinished);

					setPageCompleteAsync(isInstallationFinished);
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

		return super.getNextPage();
	}

	@Override
	public IWizardPage getPreviousPage() {
		// Do not allow to go back from this page.
		return null;
	}

}
