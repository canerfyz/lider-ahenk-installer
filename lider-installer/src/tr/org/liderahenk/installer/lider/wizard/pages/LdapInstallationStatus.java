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

public class LdapInstallationStatus extends WizardPage implements ILdapPage, InstallationStatusPage, ControlNextEvent {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;
	
	boolean isInstallationFinished = false;

	boolean canGoBack = false;
	
	public LdapInstallationStatus(LiderSetupConfig config) {
		super(LdapInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("LDAP_INSTALLATION"));
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
		// Start LDAP installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					printMessage("Initializing installation...");
					setProgressBar(10);

					printMessage("Setting up parameters for database password.");
					final String[] debconfValues = generateDebconfValues();
					setProgressBar(20);

					printMessage("Installing package...");

					if (config.getLdapInstallMethod() == InstallMethod.APT_GET) {
						try {
							SetupUtils.installPackageNoninteractively(config.getLdapIp(),
									config.getLdapAccessUsername(), config.getLdapAccessPasswd(), config.getLdapPort(),
									config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), config.getLdapPackageName(), null, debconfValues);
							setProgressBar(90);
							isInstallationFinished = true;
							printMessage("Successfully installed package: " + config.getLdapPackageName());
						} catch (Exception e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getLdapInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getLdapDebFileName());
						try {
							SetupUtils.installPackageNonInteractively(config.getLdapIp(),
									config.getLdapAccessUsername(), config.getLdapAccessPasswd(), config.getLdapPort(),
									config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), deb, debconfValues, PackageInstaller.GDEBI);
							setProgressBar(90);
							isInstallationFinished = true;
							printMessage("Successfully installed package: " + deb.getName());
						} catch (Exception e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
					} else if (config.getLdapInstallMethod() == InstallMethod.WGET) {
						try {
							printMessage("Downloading OpenLDAP .deb package from: "
									+ config.getLdapDownloadUrl());
							
							// In case of folder name clash use current time as postfix
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
							String timestamp = dateFormat.format(date);
							
							SetupUtils.downloadPackage(config.getLdapIp(), config.getLdapAccessUsername(),
									config.getLdapAccessPasswd(), config.getLdapPort(),
									config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), "openLdapTmp" + timestamp, "openldap.deb",
									config.getLdapDownloadUrl());
							
							setProgressBar(30);
							
							printMessage("Successfully downloaded file.");
							
							printMessage("OpenLDAP is being installed to: " + config.getLdapIp()
							+ " from downloaded .deb file.");
							SetupUtils.installDownloadedPackage(config.getLdapIp(), config.getLdapAccessUsername(),
									config.getLdapAccessPasswd(), config.getLdapPort(),
									config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), "openLdapTmp" + timestamp, "openldap.deb", PackageInstaller.GDEBI);
							
							printMessage("OpenLDAP has been successfully installed to: " + config.getLdapIp());
						} catch (CommandExecutionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						} catch (SSHConnectionException e) {
							isInstallationFinished = false;
							canGoBack = true;
							printMessage("Error occurred: " + e.getMessage());
							e.printStackTrace();
						}
						
					} else {
						isInstallationFinished = false;
						printMessage("Invalid installation method. Installation cancelled.");
					}
					
					// TODO dosyayı gönder
					// TODO chmod
					// TODO execute
					File ldapConfigFile;
					try {
						ldapConfigFile = new File(config.getLdapAbsPathConfFile());

						SetupUtils.copyFile(config.getLdapIp(), config.getLdapAccessUsername(),
										config.getLdapAccessPasswd(), config.getLdapPort(),
										config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), ldapConfigFile, "/tmp/");
						
						// Maket it executable
						SetupUtils.executeCommand(config.getLdapIp(), config.getLdapAccessUsername(),
										config.getLdapAccessPasswd(), config.getLdapPort(),
										config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), "chmod +x /tmp/" + ldapConfigFile.getName());

						// Run LDAP config script
						SetupUtils.executeCommand(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(),
								config.getLdapAccessKeyPath(), config.getLdapAccessPassphrase(), "/tmp/" + ldapConfigFile.getName());
						
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

		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);
	}

	/**
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("ldap.debconf.password1") + " " + config.getLdapRootPassword();
		String debconfPwdAgain = PropertyReader.property("ldap.debconf.password2") + " " + config.getLdapRootPassword();
		String debconfAdminPwd = PropertyReader.property("ldap.debconf.adminpw") + " "
				+ config.getLdapRootPassword();
		String debconfGeneratedPwd = PropertyReader.property("ldap.debconf.generated.password") + " "
				+ config.getLdapRootPassword();
		return new String[] { debconfPwd, debconfPwdAgain, debconfAdminPwd, debconfGeneratedPwd };
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
