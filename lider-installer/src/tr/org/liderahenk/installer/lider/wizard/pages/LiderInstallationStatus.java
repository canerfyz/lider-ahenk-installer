package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LiderInstallationStatus extends WizardPage implements ILiderPage, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

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

					printMessage(Messages.getString("INITIALIZING_INSTALLATION"));
					setProgressBar(10);

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
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							canGoBack = true;
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
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getLiderInstallMethod() == InstallMethod.TAR_GZ) {
						File tar = new File(config.getLiderTarFileName());

						try {
							SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"rm -rf /tmp/lider-temp && mkdir -p /tmp/lider-temp");

							setProgressBar(30);

							printMessage(Messages.getString("INSTALLING_DEPENDENCIES"));
							SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"apt-get install -y --force-yes openjdk-7-jdk sshpass rsync nmap");
							printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_DEPENDENCIES"));
							setProgressBar(40);

							printMessage(Messages.getString("COPYING_LIDER_TAR_GZ"));
							SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), tar,
									"/tmp/lider-temp");
							printMessage(Messages.getString("SUCCESSFULLY_COPIED_LIDER_TAR_GZ"));
							setProgressBar(60);

							printMessage(Messages.getString("EXTRACTING_TAR_GZ_FILE", tar.getName()));
							SetupUtils.extractTarFile(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"/tmp/lider-temp/" + tar.getName(), "/opt/");
							setProgressBar(90);
							printMessage(Messages.getString("SUCCESSFULLY_EXTRACTED_TAR_GZ_FILE", tar.getName()));

						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage(Messages.getString("ERROR_OCCURED", e.getMessage()));
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage(Messages.getString("ERROR_OCCURED", e.getMessage()));
							e.printStackTrace();
						}

					} else if (config.getLiderInstallMethod() == InstallMethod.WGET) {
						try {
							printMessage(
									Messages.getString("DOWNLOADING_TAR_GZ_FILE_FROM", config.getLiderDownloadUrl()));
							// In case of folder name clash use current time as
							// postfix
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
							String timestamp = dateFormat.format(date);

							SetupUtils.downloadPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"liderTmpDir" + timestamp, "lider.deb", config.getLiderDownloadUrl());
							printMessage(Messages.getString("SUCCESSFULLY_DOWNLOADED_TAR_GZ_FILE_FROM",
									config.getLiderDownloadUrl()));

							setProgressBar(30);

							// TODO
							// TODO buranın devamı tar gz'den çıkarma vs olacak
							printMessage("Lider is being installed to: " + config.getLiderIp()
									+ " from downloaded .deb file.");
							SetupUtils.installDownloadedPackage(config.getLiderIp(), config.getLiderAccessUsername(),
									config.getLiderAccessPasswd(), config.getLiderPort(),
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(),
									"liderTmpDir" + timestamp, "lider.deb", PackageInstaller.DPKG);

							printMessage("Lider has been successfully installed to: " + config.getLiderIp());
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage(Messages.getString("ERROR_OCCURED", e.getMessage()));
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage(Messages.getString("ERROR_OCCURED", e.getMessage()));
							e.printStackTrace();
						}

					} else {
						isInstallationFinished = false;
						printMessage(Messages.getString("INVALID_INSTALLATION_METHOD"));
					}

					File liderConfigFile;
					File datasourceConfigFile;
					File setEnvFile;
					InputStream inputStream;
					try {
						liderConfigFile = new File(config.getLiderAbsPathConfFile());
						datasourceConfigFile = new File(config.getDatasourceAbsPathConfFile());
						inputStream = this.getClass().getClassLoader().getResourceAsStream("setenv");
						setEnvFile = SetupUtils.streamToFile(inputStream, "setenv");

						// Copy tr.org.liderahenk.cfg
						printMessage(Messages.getString("SENDING_LIDER_CFG"));
						SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(), liderConfigFile,
								"/opt/" + PropertyReader.property("lider.package.name") + "/etc/");
						printMessage(Messages.getString("SUCCESSFULLY_SENT_LIDER_CFG"));

						// Copy tr.org.liderahenk.datasource.cfg
						printMessage(Messages.getString("SENDING_LIDER_DATASOURCE_CFG"));
						SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(), datasourceConfigFile,
								"/opt/" + PropertyReader.property("lider.package.name") + "/etc/");
						printMessage(Messages.getString("SUCCESSFULLY_SENT_LIDER_DATASOURCE_CFG"));

						// Copy setenv file
						printMessage(Messages.getString("SENDING_KARAF_SETENV"));
						SetupUtils.copyFile(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(), setEnvFile,
								"/opt/" + PropertyReader.property("lider.package.name") + "/bin/");
						printMessage(Messages.getString("SUCCESSFULLY_SENT_KARAF_SETENV"));

						String command = "nohup /opt/" + PropertyReader.property("lider.package.name")
								+ "/bin/karaf > /dev/null 2>&1 &";

						printMessage(Messages.getString("STARTING_LIDER"));
						SSHManager.USE_PTY = false;
						// Start Karaf
						SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(), command, new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
								});
						SSHManager.USE_PTY = true;
						try {
							Thread.sleep(30000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						printMessage(Messages.getString("SUCCESSFULLY_STARTED_LIDER"));

						printMessage(Messages.getString("DEFINING_KARAF_AS_A_SERVICE"));
						printMessage(Messages.getString("INSTALLING_WRAPPER"));
						SetupUtils.executeCommand(config.getLiderIp(), "karaf", "karaf", 8101, null, null,
								"wrapper:install", new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
								});
						printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_WRAPPER"));

						printMessage(Messages.getString("MODIFYING_KARAF_WRAPPER_CONF"));
						SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(),
								"sed -i '/set.default.JAVA_HOME/c\\set.default.JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre' /opt/"
										+ PropertyReader.property("lider.package.name") + "/etc/karaf-wrapper.conf",
								new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
								});
						printMessage(Messages.getString("SUCCESSFULLY_MODIFIED_KARAF_WRAPPER_CONF"));

						printMessage(Messages.getString("LINKING_SERVICE_SCRIPTS_TO_INIT"));
						SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(),
								"ln -s /opt/" + PropertyReader.property("lider.package.name")
										+ "/bin/karaf-service /etc/init.d/ && update-rc.d karaf-service defaults",
								new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
								});
						printMessage(Messages.getString("SUCCESSFULLY_LINKED_SERVICE_SCRIPTS_TO_INIT"));

						printMessage(Messages.getString("UPDATING_DEFAULT_SERVICES"));
						SetupUtils.executeCommand(config.getLiderIp(), config.getLiderAccessUsername(),
								config.getLiderAccessPasswd(), config.getLiderPort(), config.getLiderAccessKeyPath(),
								config.getLiderAccessPassphrase(), "update-rc.d karaf-service defaults",
								new IOutputStreamProvider() {
									@Override
									public byte[] getStreamAsByteArray() {
										return "\n".getBytes(StandardCharsets.UTF_8);
									}
								});
						printMessage(Messages.getString("SUCCESSFULLY_UPDATED_DEFAULT_SERVICES"));
						printMessage(Messages.getString("SUCCESSFULLY_DEFINED_KARAF_AS_A_SERVICE"));

						printMessage(Messages.getString("LIDER_INSTALLATION_COMPLETED"));
						isInstallationFinished = true;

					} catch (SSHConnectionException e) {
						isInstallationFinished = false;
						canGoBack = true;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					} catch (CommandExecutionException e) {
						isInstallationFinished = false;
						canGoBack = true;
						printMessage("Error occurred: " + e.getMessage());
						e.printStackTrace();
					}

					setProgressBar(100);

					config.setInstallationFinished(isInstallationFinished);

					setPageCompleteAsync(isInstallationFinished);

					if (!isInstallationFinished) {
						try {
							openDownloadUrl();
						} catch (Exception e) {
							e.printStackTrace();
							txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
									? txtLogConsole.getText() + "\n" : "")
									+ Messages.getString("CANNOT_OPEN_BROWSER_PLEASE_GO_TO") + "\n"
									+ PropertyReader.property("troubleshooting.url"));
						}
					}
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

	private void openDownloadUrl() throws IOException {
		Runtime.getRuntime().exec("xdg-open " + PropertyReader.property("troubleshooting.url"));
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

}
