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
		// To prevent triggering installation again
		// // (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					
					setPageCompleteAsync(false);
					
					printMessage("Initializing installation...");
					setProgressBar(10);
					
					printMessage("Installing package...");
					
					// If installation method is not set, show an error message
					// and do not try to install
					if (config.getAhenkInstallMethod() == InstallMethod.APT_GET
							|| config.getAhenkInstallMethod() == InstallMethod.PROVIDED_DEB) {
						
						for (final String ip : config.getIpList()) {
							
							final Display display = Display.getCurrent();
							
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									try {
										if (config.getAhenkInstallMethod() == InstallMethod.APT_GET) {
											printMessage("Ahenk is being installed to: " + ip + " from catalog.");
											// port eklenecek
											SetupUtils.installPackage(ip, config.getUsername(), config.getPassword(), 22, config.getPrivateKeyAbsPath(), "ahenk", null);
										} else {
											printMessage("Ahenk is being installed to: " + ip + " from given DEB package.");
											
											File debPackage = new File(config.getDebFileAbsPath());
											// port eklenecek
											SetupUtils.installPackage(ip, config.getUsername(), config.getPassword(), 22, config.getPrivateKeyAbsPath(), debPackage);
										}

										printMessage("Ahenk has been successfully installed to: " + ip);
										
										// TODO message: konfigürasyon yapılacak lütfen bekleyin.
										// TODO konfigürasyonu yap.
										
									} catch (SSHConnectionException e) {
										isInstallationFinished = false;
										// If any error occured user should be
										// able to go
										// back and change selections etc.
										canGoBack = true;
										printMessage("Error occurred: " + e.getMessage());
										e.printStackTrace();
									} catch (CommandExecutionException e) {
										isInstallationFinished = false;
										// If any error occured user should be
										// able to go
										// back and change selections etc.
										canGoBack = true;
										printMessage("Error occurred: " + e.getMessage());
										e.printStackTrace();
									}
									
									
								}

							};
							
							Thread thread = new Thread(runnable);
							thread.start();
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

	@Override
	public NextPageEventType getNextPageEventType() {
		return nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}
}
