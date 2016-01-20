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
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class DatabaseInstallationStatus extends WizardPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	boolean isInstallationFinished = false;

	public DatabaseInstallationStatus(LiderSetupConfig config) {
		super(DatabaseInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.4 " + Messages.getString("MARIA_DB_INSTALLATION_METHOD") + " - "
				+ Messages.getString("DB_SETUP_METHOD_DESC"));
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
		// Start MariaDB installation here.
		// To prevent triggering installMariaDB method again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished) {

			printMessage("Initializing installation...");

			printMessage("Setting up parameters for database password.");
			final String[] debconfValues = generateDebconfValues();

			printMessage("Installing package...");

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					Display.getCurrent().syncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setSelection(50);
						}
					});

					if (config.getDatabaseInstallMethod() == InstallMethod.APT_GET) {
						SetupUtils.installPackageNoninteractively(config.getDatabaseIp(),
								config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
								config.getDatabasePort(), config.getDatabaseAccessKeyPath(),
								config.getDatabasePackageName(), null, debconfValues);
					} else if (config.getDatabaseInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getDebFileName());
						SetupUtils.installPackageNonInteractively(config.getDatabaseIp(),
								config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
								config.getDatabasePort(), config.getDatabaseAccessKeyPath(), deb, debconfValues);
					} else {
						printMessage("Invalid installation method. Installation cancelled.");
					}
					// TODO handle failed installation attempts!

					isInstallationFinished = true;
					setPageComplete(isInstallationFinished);
				}

			};

			Thread thread = new Thread(runnable);
			thread.start();

		}

		return super.getNextPage();
	}

	/**
	 * Prints log message to the log console widget
	 * 
	 * @param message
	 */
	public void printMessage(final String message) {
		Display.getCurrent().asyncExec(new Runnable() {
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
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("database.debconf.password") + " "
				+ config.getDatabaseRootPassword();
		String deboconfPwdAgain = PropertyReader.property("database.debconf.password.again") + " "
				+ config.getDatabaseRootPassword();
		return new String[] { debconfPwd, deboconfPwdAgain };
	}

	@Override
	public IWizardPage getPreviousPage() {
		/**
		 * Do not allow to go back from this page.
		 */
		return null;
	}

}
