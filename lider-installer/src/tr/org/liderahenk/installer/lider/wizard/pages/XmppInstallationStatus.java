package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;

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
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class XmppInstallationStatus extends WizardPage implements IXmppPage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

	private static final String EJABBERD_REGISTER = "{0}ejabberdctl register {1} {2} {3}";

	private static final String EJABBERD_SRG_CREATE = "{0}ejabberdctl srg-create everyone {1} \"everyone\" this_is_everyone everyone";

	private static final String EJABBERD_SRG_ADD_ALL = "{0}ejabberdctl srg-user-add @all@ {1} everyone {2}";

	private static final String CHOWN_EJABBERD = "chown -R ejabberd:ejabberd {0}";

	public XmppInstallationStatus(LiderSetupConfig config) {
		super(XmppInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("4.4 " + Messages.getString("XMPP_INSTALLATION"));
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
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {

		// Start XMPP installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					// Clear text log console and progress bar before starting
					// installation.
					clearLogConsole();
					setProgressBar(0);

					setPageCompleteAsync(isInstallationFinished);

					printMessage("Initializing installation...");
					setProgressBar(10);

					printMessage("Installing package...");
					// If installation method is not set, show an error message
					// and do not try to install
					if (config.getXmppInstallMethod() == InstallMethod.PROVIDED_DEB
							|| config.getXmppInstallMethod() == InstallMethod.WGET) {
						try {
							//
							// PROVIDED DEB
							//
							if (config.getXmppInstallMethod() == InstallMethod.PROVIDED_DEB) {
								File deb = new File(config.getXmppDebFileName());
								SetupUtils.installPackage(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(), deb,
										PackageInstaller.DPKG);
								printMessage("Successfully installed package: " + deb.getName());
							}
							//
							// WGET
							//
							else if (config.getXmppInstallMethod() == InstallMethod.WGET) {
								printMessage("Downloading Ejabberd .deb package from: " + config.getXmppDownloadUrl());

								SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
										"rm -rf /tmp/ejabberd-temp && mkdir -p /tmp/ejabberd-temp");

								SetupUtils.downloadPackage(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
										"ejabberd-temp", "ejabberd.deb", config.getXmppDownloadUrl());

								printMessage("Successfully downloaded file.");
								printMessage("Ejabberd is being installed to: " + config.getXmppIp()
										+ " from downloaded .deb file.");
								SetupUtils.installDownloadedPackage(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
										"ejabberd-temp", "ejabberd.deb", PackageInstaller.DPKG);
								printMessage("Ejabberd has been successfully installed to: " + config.getXmppIp());
							}
							setProgressBar(30);
							printMessage("Now, installer starts configuring Ejabberd, please wait..");

							//
							// Set ejabberd.yml
							//
							File file = new File(config.getXmppAbsPathConfFile());
							SetupUtils.copyFile(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(), file, PropertyReader.property("xmpp.conf.path"));
							printMessage("Configuration file successfully set.");
							setProgressBar(35);

							//
							// Change owner
							//
							printMessage("Changing owner of Ejabberd directory");
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(),
									CHOWN_EJABBERD.replace("{0}", PropertyReader.property("xmpp.path")));
							printMessage("Changed owner of Ejabberd directory");
							setProgressBar(40);

							//
							// Start Ejabberd
							//
							printMessage("Starting Ejabberd server");
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(),
									PropertyReader.property("xmpp.bin.path") + "ejabberdctl start");
							printMessage("Started Ejabberd server");
							setProgressBar(45);
							try {
								Thread.sleep(20000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								e.printStackTrace();
							}
							setProgressBar(50);

							//
							// Create roster group
							//
							printMessage("Creating shared roster group");
							String createSrg = prepareCommand(EJABBERD_SRG_CREATE, new Object[] {
									PropertyReader.property("xmpp.bin.path"), config.getXmppHostname() });
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(), createSrg);
							printMessage("Created shared roster group");
							setProgressBar(55);

							//
							// Configure roster group
							//
							printMessage("Configuring roster group");
							String addAll = prepareCommand(EJABBERD_SRG_ADD_ALL,
									new Object[] { PropertyReader.property("xmpp.bin.path"), config.getXmppHostname(),
											config.getXmppHostname() });
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(), addAll);
							printMessage("Configured roster group");
							setProgressBar(60);

							//
							// Restart Ejabberd
							//
							printMessage("Restarting Ejabberd server");
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(),
									PropertyReader.property("xmpp.bin.path") + "ejabberdctl restart");
							setProgressBar(65);
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								e.printStackTrace();
							}
							printMessage("Restarted Ejabberd server");
							setProgressBar(70);

							//
							// Create admin user
							//
							String register = null;
							try {
								printMessage("Creating admin user");
								register = prepareCommand(EJABBERD_REGISTER,
										new Object[] { PropertyReader.property("xmpp.bin.path"), "admin",
												config.getXmppHostname(), config.getXmppAdminPwd() });
								SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(), register);
								printMessage("Created admin user");
							} catch (Exception e) {
								e.printStackTrace();
								printMessage("Could not create admin user: " + e.getMessage());
								printMessage("Please run the following command on target machine to create user: "
										+ register);
							}
							setProgressBar(75);

							//
							// Restart Ejabberd
							//
							printMessage("Restarting Ejabberd server");
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(),
									PropertyReader.property("xmpp.bin.path") + "ejabberdctl restart");
							setProgressBar(80);
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								e.printStackTrace();
							}
							printMessage("Restarted Ejabberd server");
							setProgressBar(85);

							//
							// Create Lider user
							//
							try {
								printMessage("Creating Lider user");
								register = prepareCommand(EJABBERD_REGISTER,
										new Object[] { PropertyReader.property("xmpp.bin.path"),
												config.getXmppLiderUsername(), config.getXmppHostname(),
												config.getXmppLiderPassword() });
								SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
										config.getXmppAccessPasswd(), config.getXmppPort(),
										config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(), register);
								printMessage("Created Lider user");
							} catch (Exception e) {
								e.printStackTrace();
								printMessage("Could not create Lider user: " + e.getMessage());
								printMessage("Please run the following command manually on target machine to create user: "
										+ register);
							}
							setProgressBar(90);

							//
							// Restart Ejabberd
							//
							printMessage("Restarting Ejabberd server");
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppAccessPassphrase(),
									PropertyReader.property("xmpp.bin.path") + "ejabberdctl restart");
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								e.printStackTrace();
							}
							printMessage("Restarted Ejabberd server");
							setProgressBar(95);

							isInstallationFinished = true;

							// Clear temp file
							if (isInstallationFinished) {
								deleteFile("ejabberd.yml");
							}
							config.setInstallationFinished(isInstallationFinished);

							printMessage("Installation finished..");
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							// If any error occured user should be able to go
							// back and change selections etc.
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
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

	/**
	 * Deletes a file from temporary file directory.
	 * 
	 * @param content
	 * @param namePrefix
	 * @param nameSuffix
	 * @return absolute path of created temp file
	 */
	private void deleteFile(String fileName) {
		try {
			if (!fileName.isEmpty()) {
				File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

				file.delete();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String prepareCommand(String command, Object[] params) {
		String tmpCmd = command;

		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (params[i] != null) {
					tmpCmd = tmpCmd.replaceAll("\\{" + i + "\\}", params[i].toString());
				}
			}
		}
		return tmpCmd;
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
