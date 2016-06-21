package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseClusterNodeModel;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class DatabaseClusterInstallationStatus extends WizardPage
		implements IDatabasePage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

	private int progressBarPercent;

	private static final Logger logger = Logger.getLogger(DatabaseClusterInstallationStatus.class.getName());

	public DatabaseClusterInstallationStatus(LiderSetupConfig config) {
		super(DatabaseClusterInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.4 " + Messages.getString("DATABASE_CLUSTER_INSTALLATION"));
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

			canGoBack = false;

			// Get display before new main runnable
			final Display display = Display.getCurrent();

			clearLogConsole(display);

			// Calculate progress bar increment size
			final Integer increment = (Integer) (90 / config.getDatabaseNodeMap().size());

			// Create a thread pool
			final ExecutorService executor = Executors.newCachedThreadPool();

			setProgressBar(10, display);

			printMessage("Initializing installation...", display);

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation I have to wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {

					for (Iterator<Entry<Integer, DatabaseClusterNodeModel>> iterator = config.getDatabaseNodeMap()
							.entrySet().iterator(); iterator.hasNext();) {
						Entry<Integer, DatabaseClusterNodeModel> entry = iterator.next();
						final DatabaseClusterNodeModel clusterNode = entry.getValue();

						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								try {
									SetupUtils.installDatabaseCluster(clusterNode.getTxtNodeIp().getText(), "root",
											clusterNode.getTxtNodeRootPwd().getText(), 22, null, null, clusterNode);
								} catch (SSHConnectionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (CommandExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						};

						executor.execute(runnable);
					}

					try {
						executor.shutdown();
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			};
			Thread thread = new Thread(mainRunnable);
			thread.start();

		}
		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);
	}

	/**
	 * Prints log message to the log console widget
	 * 
	 * @param message
	 */
	private void printMessage(final String message, Display display) {
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
	 * Sets progress bar selection (Increases progress bar percentage by
	 * increment value.)
	 * 
	 * @param selection
	 */
	private void setProgressBar(final int increment, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressBarPercent += increment;

				progressBar.setSelection(progressBarPercent);
			}
		});
	}

	/**
	 * Sets page complete status asynchronously.
	 * 
	 * @param isComplete
	 */
	private void setPageCompleteAsync(final boolean isComplete, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setPageComplete(isComplete);
			}
		});
	}

	/**
	 * Clears log console by set its content to empty string.
	 */
	private void clearLogConsole(Display display) {
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

	private void setupClusterNode(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, DatabaseClusterNodeModel clusterNode, Display display)
					throws SSHConnectionException, CommandExecutionException {

		SSHManager manager = null;

		try {
			// Check SSH connection
			try {
				printMessage(Messages.getString("CHECKING_CONNECTION_TO") + " " + ip, display);

				manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
						passphrase);
				manager.connect();

				printMessage(Messages.getString("CONNECTION_ESTABLISHED_TO") + " " + ip, display);
				logger.log(Level.INFO, "Connection established to: {0} with username: {1}",
						new Object[] { ip, username });

			} catch (SSHConnectionException e) {
				printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE") + " " + ip, display);
				printMessage(Messages.getString("CHECK_SSH_ROOT_PERMISSONS_OF" + " " + ip), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
			}

			// Update package list
			try {
				printMessage(Messages.getString("UPDATING_PACKAGE_LIST_OF") + " " + ip, display);
				manager.execCommand("apt-get update", new Object[] {});

				printMessage(Messages.getString("SUCCESSFULLY_UPDATED_PACKAGE_LIST_OF") + " " + ip, display);
				logger.log(Level.INFO, "Successfully updated package list of {0}", new Object[] { ip });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("COULD_NOT_UPDATE_PACKAGE_LIST_OF") + " " + ip, display);
				printMessage(Messages.getString("CHECK_INTERNET_CONNECTION_OF") + " " + ip, display);
				printMessage(Messages.getString("CHECK_REPOSITORY_LISTS_OF") + " " + ip, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception("INSTALLATION_FAILED_AT" + " " + ip);
			}

			// Install software-properties-common
			// Add keyserver
			// Add repository
			try {
				printMessage(Messages.getString("INSTALLING_PACKAGE") + " 'software-properties-common' to: " + ip,
						display);
				manager.execCommand("apt-get -y --force-yes install software-properties-common", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_PACKAGE") + " 'software-properties-common' to: "
						+ ip, display);

				printMessage(Messages.getString("ADDING_KEYSERVER_TO") + " " + ip, display);
				manager.execCommand("apt-key adv --recv-keys --keyserver keyserver.ubuntu.com 0xcbcb082a1bb943db",
						new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_ADDED_KEYSERVER_TO") + " " + ip, display);

				printMessage(
						Messages.getString("ADDING_REPOSITORY")
								+ " 'ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main' to " + ip,
						display);
				manager.execCommand(
						"add-apt-repository -y 'deb [arch=amd64,i386] ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main'",
						new Object[] {});
				printMessage(
						Messages.getString("SUCCESSFULLY_ADDED_REPOSITORY")
								+ " 'ftp://ftp.ulak.net.tr/pub/MariaDB/repo/10.1/debian jessie main' to " + ip,
						display);

				printMessage(Messages.getString("UPDATING_PACKAGE_LIST_OF") + " " + ip, display);
				manager.execCommand("apt-get update", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_UPDATED_PACKAGE_LIST_OF") + " " + ip, display);
				logger.log(Level.INFO, "Successfully done prerequiste part at: {0}", new Object[] { ip });
			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_DURING_PREREQUSITES_AT") + " " + ip, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception("INSTALLATION_FAILED_AT" + " " + ip);
			}

			// Set frontend as noninteractive
			// Set debconf values
			try {
				printMessage(Messages.getString("SETTING_DEBIAN_FRONTEND_AT") + " " + ip, display);
				manager.execCommand("export DEBIAN_FRONTEND='noninteractive'", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_SET_DEBIAN_FRONTEND_AT") + " " + ip, display);

				final String[] debconfValues = generateDebconfValues();

				printMessage(Messages.getString("SETTING_DEB_CONF_SELECTIONS_AT") + " " + ip, display);
				for (String value : debconfValues) {
					manager.execCommand("debconf-set-selections <<< '{0}'", new Object[] { value });
				}
				printMessage(Messages.getString("SUCCESSFULLY_SET_DEB_CONF_SELECTIONS_AT") + " " + ip, display);
				logger.log(Level.INFO, "Successfully done debconf selections part at: {0}", new Object[] { ip });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_DURING_DEBCONF_AT") + " " + ip, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception("INSTALLATION_FAILED_AT" + " " + ip);
			}

			// Install mariadb-server-10.1
			try {
				printMessage(Messages.getString("INSTALLING_PACKAGE") + " 'mariadb-server-10.1' to: " + ip, display);
				manager.execCommand("apt-get -y --force-yes install mariadb-server-10.1", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_PACKAGE") + " 'software-properties-common' to: "
						+ ip, display);
				logger.log(Level.INFO, "Successfully installed package mariadb-server-10.1 at: {0}",
						new Object[] { ip });
			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("COULD_NOT_INSTALL_MARIADB_TO") + " " + ip, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception("INSTALLATION_FAILED_AT" + " " + ip);
			}

			// TODO
			// Start mysql service
			// Execute mysql commands(first normal server commands)
			try {
				printMessage(Messages.getString("STARTING_MYSQL_SERVER_AT") + ip, display);
				manager.execCommand("service mysql start", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_STARTED_MYSQL_SERVER_AT") + ip, display);

				printMessage(Messages.getString("EXECUTING_MYSQL_COMMANDS_AT") + ip, display);
				manager.execCommand(
						"mysql -uroot -p{0} -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '{1}' WITH GRANT OPTION;\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"DELETE FROM mysql.user WHERE user='';\"",
						new Object[] { config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT ALL ON *.* TO 'root'@'%' IDENTIFIED BY '{1}';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseRootPassword() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT USAGE ON *.* to {1}@'%' IDENTIFIED BY '{2}';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseSstUsername(),
								config.getDatabaseSstPwd() });
				manager.execCommand("mysql -uroot -p{0} -e \"GRANT ALL PRIVILEGES on *.* to {1}r@'%';\"",
						new Object[] { config.getDatabaseRootPassword(), config.getDatabaseSstUsername() });
				manager.execCommand("mysql -uroot -p{0} -e \"FLUSH PRIVILEGES;\"",
						new Object[] { config.getDatabaseRootPassword() });
				printMessage(Messages.getString("SUCCESSFULLY_EXECUTED_MYSQL_COMMANDS_AT") + ip, display);
				logger.log(Level.INFO, "Successfully mysql commands at: {0}",
						new Object[] { ip });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_ON_MYSQL_SERVICE_AT") + " " + ip, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + ip, display);
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception("INSTALLATION_FAILED_AT" + " " + ip);
			}

			// TODO
			// Stop mysql service
			// Send galera conf

		} catch (Exception e) {
			// TODO
			// TODO
			// TODO
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}

		// Set frontend as noninteractive
		manager.execCommand("export DEBIAN_FRONTEND='noninteractive'", new Object[] {});

		manager.disconnect();
		manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
		manager.connect();

		manager.disconnect();

	}

	/**
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("database.cluster.debconf.password") + " "
				+ config.getDatabaseRootPassword();
		String debconfPwdAgain = PropertyReader.property("database.cluster.debconf.password.again") + " "
				+ config.getDatabaseRootPassword();
		return new String[] { debconfPwd, debconfPwdAgain };
	}

}
