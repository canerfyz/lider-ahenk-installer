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
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
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
									config.getLiderAccessKeyPath(), config.getLiderAccessPassphrase(), deb);
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

}
