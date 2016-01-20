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
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
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
			// TODO i18n messages
			appendLog("Initializing installation...");

			appendLog("Setting up setup parameters for database password");
			// TODO read from properties file...
			String debconfPwd = "mariadb-server-10.1 mysql-server/root_password password " + config.getDatabaseRootPassword();
			String deboconfPwdAgain = "mariadb-server-10.1 mysql-server/root_password_again password "
					+ config.getDatabaseRootPassword();
			String[] debconfValues = new String[] { debconfPwd, deboconfPwdAgain };

			appendLog("Installing package...");
			boolean success = false;

			// TODO we might be able to get rid of these if-else blocks if Jsch
			// gives private key priority over username-password or vice
			// versa...
			if (config.getDatabaseAccessMethod() == AccessMethod.PRIVATE_KEY) {
				if (config.getDatabaseInstallMethod() == InstallMethod.APT_GET) {
					SetupUtils.installPackageNoninteractively(config.getDatabaseIp(), null, null,
							config.getDatabasePort(), config.getDatabaseAccessKeyPath(),
							config.getDatabasePackageName(), null, debconfValues);
				} else if (config.getDatabaseInstallMethod() == InstallMethod.PROVIDED_DEB) {
					File deb = new File(config.getDebFileName());
					SetupUtils.installPackageNonInteractively(config.getDatabaseIp(), null, null,
							config.getDatabasePort(), config.getDatabaseAccessKeyPath(), deb, debconfValues);
				} else {
					appendLog("Invalid installation method");
				}
			} else if (config.getDatabaseAccessMethod() == AccessMethod.USERNAME_PASSWORD) {
				if (config.getDatabaseInstallMethod() == InstallMethod.APT_GET) {
					SetupUtils.installPackageNoninteractively(config.getDatabaseIp(),
							config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
							config.getDatabasePort(), null, config.getDatabasePackageName(), null, debconfValues);
				} else if (config.getDatabaseInstallMethod() == InstallMethod.PROVIDED_DEB) {
					File deb = new File(config.getDebFileName());
					SetupUtils.installPackageNonInteractively(config.getDatabaseIp(),
							config.getDatabaseAccessUsername(), config.getDatabaseAccessPasswd(),
							config.getDatabasePort(), null, deb, debconfValues);
				} else {
					appendLog("Invalid installation method");
				}
			} else {
				appendLog("Invalid access method");
			}

			if (success) {
				appendLog("Successfully installed database server: " + config.getDatabasePackageName());
			}

			// TODO handle failed installation attempts!

			isInstallationFinished = true;
			setPageComplete(isInstallationFinished);
		}

		return super.getNextPage();
	}

	public void appendLog(final String message) {
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
						? txtLogConsole.getText() + "\n" : "") + message);
			}
		});
	}

	@Override
	public IWizardPage getPreviousPage() {
		/**
		 * Do not allow to go back from this page.
		 */
		return null;
	}

}
