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
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class XmppInstallationStatus extends WizardPage implements IXmppPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	boolean isInstallationFinished = false;

	private static final String EJABBERD_REGISTER = "ejabberdctl register {0} {1} {2}";

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
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {
		// Start XMPP installation here.
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

					if (config.getXmppInstallMethod() == InstallMethod.APT_GET) {
						try {
							SetupUtils.installPackage(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									config.getXmppPackageName(), null);
							printMessage("Successfully installed package: " + config.getXmppPackageName());
							setProgressBar(50);
							
							printMessage("Now, installer starts configuring Ejabberd, please wait..");
							
							//------------ Set ejabberd.yml configuration file -----------//
							File file = new File(config.getXmppAbsPathConfFile());

							SetupUtils.copyFile(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									file, "/etc/ejabberd/");
							printMessage("Configuration file successfully set.");
							setProgressBar(60);

							// Delete the configuration file created in /tmp
							deleteFile("ejabberd.yml");
							//------------------------------------------------------------//

							//------------ Configure /etc/hosts file ---------------//
							printMessage("Configuring hosts file for Ejabberd..");

							String script = PropertyReader.property("hosts.file.configuration");
							script = script.replaceAll("##", config.getXmppHostname());
							
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(), script);
							setProgressBar(70);
							//------------------------------------------------------//
							
							//------------ Restart Ejabberd service ----------------//
							printMessage("Restarting Ejabberd service to apply changes.");

							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(), 
									"service ejabberd restart");
							setProgressBar(80);
							//------------------------------------------------------//

							//---------- Create Ejabberd users -----------//
							printMessage("Creating Ejabberd users..");

							// Prepare register command for admin user
							String command = prepareCommand(EJABBERD_REGISTER,
									new Object[] { "admin", config.getXmppHostname(), config.getXmppAdminPwd() });

							// Register admin user in Ejabberd
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									command);
							printMessage("Ejabberd user: admin has been successfully created");
							setProgressBar(90);

							// Prepare register command for Lider user
							command = prepareCommand(EJABBERD_REGISTER, new Object[] { config.getXmppLiderUsername(),
									config.getXmppHostname(), config.getXmppLiderPassword() });
							
							// Register Lider server user in Ejabberd
							SetupUtils.executeCommand(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									command);
							printMessage("Ejabberd user: " + config.getXmppLiderUsername()
							+ " has been successfully created");
							setProgressBar(100);
							//--------------------------------------------//
							
							isInstallationFinished = true;

						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getXmppInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getXmppDebFileName());
						try {
							SetupUtils.installPackage(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									deb);
							setProgressBar(90);
							printMessage("Successfully installed package: " + deb.getName());

							File file = new File(config.getXmppAbsPathConfFile());

							SetupUtils.copyFile(config.getXmppIp(), config.getXmppAccessUsername(),
									config.getXmppAccessPasswd(), config.getXmppPort(), config.getXmppAccessKeyPath(),
									file, "/etc/ejabberd/");
							printMessage("Configuration file successfully sent.");

							// Delete the configuration file created in /tmp
							deleteFile("ejabberd.yml");

							isInstallationFinished = true;

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

					setProgressBar(100);
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

}
