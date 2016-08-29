package tr.org.liderahenk.installer.lider.callables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class DatabaseOnlyConfigureNodeCallable implements Callable<Boolean> {

	private static final Logger logger = Logger.getLogger(DatabaseOnlyConfigureNodeCallable.class.getName());

	private String nodeIp;
	private String nodeRootPwd;
	private String nodeName;

	private Display display;
	private LiderSetupConfig config;
	private Text txtLogConsole;
	
	public DatabaseOnlyConfigureNodeCallable(String nodeIp, String nodeRootPwd, String nodeName, Display display,
			LiderSetupConfig config, Text txtLogConsole) {
		super();
		this.nodeIp = nodeIp;
		this.nodeRootPwd = nodeRootPwd;
		this.nodeName = nodeName;
		this.display = display;
		this.config = config;
		this.txtLogConsole = txtLogConsole;
	}
	
	@Override
	public Boolean call() throws Exception {

		return configureClusterNode();
	}

	private boolean configureClusterNode() {
		
		SSHManager manager = null;

		boolean successfullSetup = false;
		try {
			// Check SSH connection
			try {
				printMessage(Messages.getString("CHECKING_CONNECTION_TO") + " " + nodeIp, display);

				manager = new SSHManager(nodeIp, "root", nodeRootPwd, config.getDatabasePort(), config.getDatabaseAccessKeyPath(), config.getDatabaseAccessPassphrase());
				manager.connect();

				printMessage(Messages.getString("CONNECTION_ESTABLISHED_TO") + " " + nodeIp, display);
				logger.log(Level.INFO, "Connection established to: {0} with username: {1}",
						new Object[] { nodeIp, "root" });

			} catch (SSHConnectionException e) {
				printMessage(Messages.getString("COULD_NOT_CONNECT_TO_NODE") + " " + nodeIp, display);
				printMessage(Messages.getString("CHECK_SSH_ROOT_PERMISSONS_OF" + " " + nodeIp), display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				e.printStackTrace();
				logger.log(Level.SEVERE, e.getMessage());
				throw new Exception();
			}

			// Stop mysql service
			// Send galera.cnf
			try {
				printMessage(Messages.getString("STOPPING_MYSQL_SERVICE_AT") + nodeIp, display);
				manager.execCommand("service mysql stop", new Object[] {});
				printMessage(Messages.getString("SUCCESSFULLY_STOPPED_MYSQL_SERVICE_AT") + nodeIp, display);

				printMessage(Messages.getString("CREATING_CNF_FILE"), display);
				String galeraCnf = readFile("galera.cnf");
				Map<String, String> map = new HashMap<>();
				map.put("#CLUSTER_NAME", config.getDatabaseClusterName());
				map.put("#CLUSTER_ADDRESS", config.getDatabaseClusterAddress());
				map.put("#SST_USERNAME", config.getDatabaseSstUsername());
				map.put("#SST_PWD", config.getDatabaseSstPwd());
				map.put("#NODE_ADDRESS", nodeIp);
				map.put("#NODE_NAME", nodeName);

				galeraCnf = SetupUtils.replace(map, galeraCnf);
				File galeraCnfFile = SetupUtils.writeToFile(galeraCnf, "galera.cnf");
				printMessage(Messages.getString("SUCCESSFULLY_CREATED_CNF_FILE"), display);

				printMessage(Messages.getString("SENDING_CNF_FILE_TO") + " " + nodeIp, display);
				manager.copyFileToRemote(galeraCnfFile, "/etc/mysql/conf.d/", false);
				printMessage(Messages.getString("SUCCESSFULLY_SENT_CNF_FILE_TO") + " " + nodeIp, display);
				logger.log(Level.INFO, "Successfully sent galera.cnf to: {0}", new Object[] { nodeIp });

			} catch (CommandExecutionException e) {
				printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CONFIGURING_MYSQL_AT") + " " + nodeIp, display);
				printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + nodeIp, display);
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
				throw new Exception();
			}

			printMessage(Messages.getString("CONFIGURATION_COMPLETED_SUCCESSFULLY_AT") + " " + nodeIp, display);
			successfullSetup = true;

		} catch (Exception e) {
			printMessage(Messages.getString("CONFIGURATION_FAILED_AT") + " " + nodeIp, display);
			e.printStackTrace();
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}

		return successfullSetup;
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
				txtLogConsole.setSelection(txtLogConsole.getCharCount() - 1);			}
		});
	}
	
	/**
	 * Reads file from classpath location of current project
	 * 
	 * @param fileName
	 */
	private String readFile(String fileName) {

		BufferedReader br = null;
		InputStream inputStream = null;

		String readingText = "";

		try {
			String currentLine;

			inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			while ((currentLine = br.readLine()) != null) {
				// Platform independent line separator.
				readingText += currentLine + System.getProperty("line.separator");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return readingText;
	}
}
