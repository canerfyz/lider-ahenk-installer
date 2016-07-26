package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

import tr.org.liderahenk.installer.lider.callables.XmppClusterInstallCallable;
import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.utils.PageFlowHelper;
import tr.org.liderahenk.installer.lider.wizard.model.XmppNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class XmppClusterInstallationStatus extends WizardPage
		implements IXmppPage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;
	boolean canGoBack = false;

	private final static String CLUSTER_CLIENTS = "server  server1 #NODE_IP:5222 check fall 3 id #CLIENT_ID inter 5000 rise 3 slowstart 120000 weight 50";
	private final static String CLUSTER_CLIENTS_SSL = "server  server1 #NODE_IP:5223 check fall 3 id #CLIENT_SSL_ID inter 5000 rise 3 slowstart 240000 weight 50";
	private final static String CLUSTER_SERVERS = "server  server1 #NODE_IP:5269 check fall 3 id #SERVER_ID inter 5000 rise 3 slowstart 60000 weight 50";

	private Integer clientId = 1005;
	private Integer clientSslId = 1008;
	private Integer serverId = 10011;

	private static final String EJABBERD_SRG_CREATE = "{0}ejabberdctl srg-create everyone {1} \"everyone\" this_is_everyone everyone";
	private static final String EJABBERD_SRG_ADD_ALL = "{0}ejabberdctl srg-user-add @all@ {1} everyone {2}";
	private static final String EJABBERD_REGISTER = "{0}ejabberdctl register {1} {2} {3}";

	private static final Logger logger = Logger.getLogger(XmppClusterInstallationStatus.class.getName());

	public XmppClusterInstallationStatus(LiderSetupConfig config) {
		super(XmppClusterInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("4.4 " + Messages.getString("XMPP_CLUSTER_INSTALLATION"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		txtLogConsole = GUIHelper.createText(container, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtLogConsole.setTopIndex(txtLogConsole.getLineCount() - 1);

		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);

	}

	@Override
	public IWizardPage getNextPage() {

		// Start Ejabberd installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			canGoBack = false;

			progressBar.setVisible(true);

			// Get display before new main runnable
			final Display display = Display.getCurrent();

			clearLogConsole(display);

			// Create a thread pool
			final ExecutorService executor = Executors.newFixedThreadPool(10);

			// Create future list that will keep the results of callables.
			final List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();

			printMessage(Messages.getString("INITIALIZING_INSTALLATION"), display);

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation I have to wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {
					for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap().entrySet()
							.iterator(); iterator.hasNext();) {

						Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
						final XmppNodeInfoModel clusterNode = entry.getValue();

						if (clusterNode.isNodeNewSetup()) {
							Callable<Boolean> callable = new XmppClusterInstallCallable(clusterNode.getNodeIp(),
									clusterNode.getNodeRootPwd(), clusterNode.getNodeName(), display, config,
									txtLogConsole);
							Future<Boolean> result = executor.submit(callable);
							resultList.add(result);
						} else {
							// TODO
							// TODO run only configure callable
							// TODO
						}
					}

					try {
						executor.shutdown();
						executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					boolean allNodesSuccess = false;

					if (resultList.size() > 0) {
						// Check if all nodes are properly installed
						for (Future<Boolean> future : resultList) {
							try {
								allNodesSuccess = future.get();
								if (!allNodesSuccess) {
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								allNodesSuccess = false;
								break;
							}
						}
					} else {
						allNodesSuccess = true;
					}

					if (allNodesSuccess) {

						try {
							// Get first node
							XmppNodeInfoModel firstNode = config.getXmppNodeInfoMap().get(1);

							if (config.getXmppAccessKeyPath() == null) {
								for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap()
										.entrySet().iterator(); iterator.hasNext();) {

									Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
									final XmppNodeInfoModel clusterNode = entry.getValue();

									if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
										// Send Erlang cookie from first node to
										// others
										sendErlangCookie(firstNode, clusterNode, display);
									}
								}
							} else {

								for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap()
										.entrySet().iterator(); iterator.hasNext();) {

									Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
									final XmppNodeInfoModel clusterNode = entry.getValue();

									// TODO
									// TODO pull erlang cookie to yourself first
									// TODO

								}
							}

							// Start Ejabberd at each node
							for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap()
									.entrySet().iterator(); iterator.hasNext();) {

								Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
								final XmppNodeInfoModel clusterNode = entry.getValue();

								// Send Erlang cookie from first node to others
								startEjabberd(clusterNode, display);
							}

							// Join each node except first node to cluster
							for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap()
									.entrySet().iterator(); iterator.hasNext();) {

								Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
								final XmppNodeInfoModel clusterNode = entry.getValue();

								if (clusterNode.getNodeNumber() != firstNode.getNodeNumber()) {
									// start other nodes.
									joinToCluster(firstNode.getNodeName(), clusterNode, display);
								}
							}

							installHaProxy(config.getXmppProxyAddress(), config.getXmppProxyPwd(),
									config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
									config.getXmppNodeInfoMap(), display);

							// Create shared roster group and users
							createSrgAndUsers(firstNode, display);

							canGoBack = false;

							isInstallationFinished = true;

							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									progressBar.setVisible(false);
								}
							});

							printMessage(Messages.getString("EJABBERD_CLUSTER_INSTALLATION_FINISHED"), display);

							config.setInstallationFinished(isInstallationFinished);

							// To enable finish button
							setPageCompleteAsync(isInstallationFinished, display);

						} catch (Exception e) {
							e.printStackTrace();
							printMessage(Messages.getString("ERROR_OCCURED_WHILE_STARTING_OR_CONFIGURING_NODE"),
									display);
							printMessage(Messages.getString("ERROR_MESSAGE" + " " + e.getMessage()), display);
							isInstallationFinished = false;
							// If any error occured user should be
							// able to go back and change selections
							// etc.
							canGoBack = true;
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									progressBar.setVisible(false);
								}
							});
						}

					} else

					{
						printMessage(Messages.getString("INSTALLER_WONT_CONTINUE_BECAUSE_ONE_OF_NODES_SETUP_FAILED"),
								display);
						isInstallationFinished = false;

						// If any error occured user should be
						// able to go back and change selections
						// etc.
						canGoBack = true;
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								progressBar.setVisible(false);
							}
						});

						// To enable finish button
						setPageCompleteAsync(isInstallationFinished, display);
					}

				}

			};
			Thread thread = new Thread(mainRunnable);
			thread.start();

		}
		// Select next page.
		return PageFlowHelper.selectNextPage(config, this);

	}

	private void createSrgAndUsers(XmppNodeInfoModel firstNode, Display display) throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("CREATING_SHARED_ROSTER_GROUP_AT") + " " + firstNode.getNodeIp(), display);
			manager.execCommand(EJABBERD_SRG_CREATE,
					new Object[] { PropertyReader.property("xmpp.cluster.bin.path"), config.getXmppHostname() });
			printMessage(
					Messages.getString("SUCCESSFULLY_CREATED_SHARED_ROSTER_GROUP_AT") + " " + firstNode.getNodeIp(),
					display);

			printMessage(Messages.getString("ADDING_DEFAULT_SRG_BEHAVIOUR_AT") + " " + firstNode.getNodeIp(), display);
			manager.execCommand(EJABBERD_SRG_ADD_ALL, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					config.getXmppHostname(), config.getXmppHostname() });
			printMessage(
					Messages.getString("SUCCESSFULLY_ADDED_DEFAULT_SRG_BEHAVIOUR_at") + " " + firstNode.getNodeIp(),
					display);
			logger.log(Level.INFO, "Successfully created shared roster group at {0}",
					new Object[] { firstNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO") + " " + firstNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_CREATING_SRG_AT") + firstNode.getNodeIp(), display);
			printMessage(
					Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + firstNode.getNodeIp(),
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		try {
			printMessage(Messages.getString("REGISTERING_ADMIN_USER_AT") + " " + firstNode.getNodeIp(), display);
			manager.execCommand(EJABBERD_REGISTER, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					"admin", config.getXmppHostname(), config.getXmppAdminPwd() });
			printMessage(Messages.getString("SUCCESSFULLY_REGISTERED_ADMIN_USER_AT") + " " + firstNode.getNodeIp(),
					display);

			printMessage(Messages.getString("REGISTERING_USER") + " " + config.getXmppLiderUsername() + " at "
					+ firstNode.getNodeIp(), display);
			manager.execCommand(EJABBERD_REGISTER, new Object[] { PropertyReader.property("xmpp.cluster.bin.path"),
					config.getXmppLiderUsername(), config.getXmppHostname(), config.getXmppLiderPassword() });
			printMessage(Messages.getString("SUCCESSFULLY_REGISTERED_USER") + " " + config.getXmppLiderUsername()
					+ " at " + firstNode.getNodeIp(), display);

			logger.log(Level.INFO, "Successfully registered users at {0}", new Object[] { firstNode.getNodeIp() });

		} catch (CommandExecutionException e) {
			printMessage(
					Messages.getString("EXCEPTION_RAISED_WHILE_REGISTERING_USERS_AT") + " " + firstNode.getNodeIp(),
					display);
			printMessage(
					Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + firstNode.getNodeIp(),
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

	}

	private void installHaProxy(String xmppProxyAddress, String xmppProxyPwd, String xmppAccessKeyPath,
			String xmppAccessPassphrase, Map<Integer, XmppNodeInfoModel> xmppNodeInfoMap, Display display)
			throws Exception {

		SSHManager manager = null;
		try {
			manager = new SSHManager(xmppProxyAddress, "root", xmppProxyPwd, config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("INSTALLING_HAPROXY_PACKAGE_TO") + " " + xmppProxyAddress, display);
			manager.execCommand("apt-get -y --force-yes install haproxy", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_INSTALLED_HAPROXY_PACKAGE_TO") + " " + xmppProxyAddress,
					display);
			logger.log(Level.INFO, "Successfully installed HaProxy to {0}", new Object[] { xmppProxyAddress });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO") + " " + xmppProxyAddress, display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_INSTALLING_HAPROXY_PACKAGE_AT") + xmppProxyAddress,
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + xmppProxyAddress,
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		printMessage(Messages.getString("PREPARING_BACKEND_PROPERTIES"), display);
		Map<String, String> propertyMap = prepareBackendProperties();
		printMessage(Messages.getString("SUCCESSFULLY_PREPARED_BACKEND_PROPERTIES"), display);

		printMessage(Messages.getString("CREATING_HAPROXY_CONFIG_FILE"), display);
		String haproxyCfg = readFile("haproxy.cfg");

		Map<String, String> map = new HashMap<>();
		map.put("#HAPROXY_ADDRESS", config.getXmppProxyAddress());
		map.put("#CLUSTER_CLIENTS", propertyMap.get("CLUSTER_CLIENTS"));
		map.put("#CLUSTER_CLIENTS_SSL", propertyMap.get("CLUSTER_CLIENTS_SSL"));
		map.put("#CLUSTER_SERVERS", propertyMap.get("CLUSTER_SERVERS"));

		haproxyCfg = SetupUtils.replace(map, haproxyCfg);
		File haproxyCfgFile = writeToFile(haproxyCfg, "haproxy.cfg");
		printMessage(Messages.getString("SUCCESSFULLY_CREATED_HAPROXY_CONFIG_FILE"), display);
		logger.log(Level.INFO, "Successfully created haproxy.cfg", new Object[] {});

		try {
			printMessage(Messages.getString("SENDING_HAPROXY_CONFIG_FILE_TO") + " " + xmppProxyAddress, display);
			manager.copyFileToRemote(haproxyCfgFile, "/etc/haproxy", false);
			printMessage(Messages.getString("SUCCESSFULLY_SENT_HAPROXY_CONFIG_FILE_TO") + " " + xmppProxyAddress,
					display);
			logger.log(Level.INFO, "Successfully sent haproxy.cfg to {0}", new Object[] {});

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_HAPROXY_CFG_TO") + " " + xmppProxyAddress,
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + xmppProxyAddress,
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		try {
			printMessage(Messages.getString("RESTARTING_HAPROXY_SERVICE_AT") + " " + xmppProxyAddress, display);
			manager.execCommand("service haproxy restart", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_RESTARTED_HAPROXY_SERVICE_AT") + " " + xmppProxyAddress,
					display);
			logger.log(Level.INFO, "Successfully restarted haproxy service at {0}", new Object[] {});

		} catch (CommandExecutionException e) {
			printMessage(
					Messages.getString("EXCEPTION_RAISED_WHILE_RESTARTING_HAPROXY_SERVICE_AT") + " " + xmppProxyAddress,
					display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + xmppProxyAddress,
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

		printMessage(Messages.getString("SUCCESSFULLY_COMPLETED_INSTALLATION_OF_HAPROXY_AT") + " " + xmppProxyAddress,
				display);
		logger.log(Level.INFO, "Successfully completed installation of HaProxy at: {0}",
				new Object[] { xmppProxyAddress });
	}

	private Map<String, String> prepareBackendProperties() {

		Map<String, String> propertyMap = new HashMap<String, String>();

		String clusterClients = null;
		String clusterClientsSsl = null;
		String clusterServers = null;

		for (Iterator<Entry<Integer, XmppNodeInfoModel>> iterator = config.getXmppNodeInfoMap().entrySet()
				.iterator(); iterator.hasNext();) {

			Entry<Integer, XmppNodeInfoModel> entry = iterator.next();
			final XmppNodeInfoModel clusterNode = entry.getValue();

			clusterClients += CLUSTER_CLIENTS.replace("#NODE_IP", clusterNode.getNodeIp()).replace("#CLIENT_ID",
					clientId.toString());
			clusterClients += "\n\t\t";
			++clientId;
			clusterClientsSsl += CLUSTER_CLIENTS_SSL.replace("#NODE_IP", clusterNode.getNodeIp())
					.replace("#CLIENT_SSL_ID", clientSslId.toString());
			clusterClientsSsl += "\n\t\t";
			++clientSslId;
			clusterServers += CLUSTER_SERVERS.replace("#NODE_IP", clusterNode.getNodeIp()).replace("#SERVER_ID",
					serverId.toString());
			clusterServers += "\n\t\t";
			++serverId;
		}

		propertyMap.put("CLUSTER_CLIENTS", clusterClients);
		propertyMap.put("CLUSTER_CLIENTS_SSL", clusterClientsSsl);
		propertyMap.put("CLUSTER_SERVERS", clusterServers);

		return propertyMap;
	}

	private void joinToCluster(String firstNodeName, XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("JOINING_TO_CLUSTER_AT") + " " + clusterNode.getNodeIp(), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl join_cluster 'ejabber@{0}.{1}'",
					new Object[] { firstNodeName, config.getXmppHostname() });
			printMessage(Messages.getString("SUCCESSFULLY_JOINED_TO_CLUSTER_AT") + " " + clusterNode.getNodeIp(),
					display);
			logger.log(Level.INFO, "Successfully joined to cluster at {0}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO") + " " + clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_JOINING_TO_CLUSTER_AT") + clusterNode.getNodeIp(),
					display);
			printMessage(
					Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + clusterNode.getNodeIp(),
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

	}

	private void startEjabberd(XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(),
					config.getXmppPort(), config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("STARTING_EJABBERD_AT") + " " + clusterNode.getNodeIp(), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl start", new Object[] {});
			printMessage(Messages.getString("SUCCESSFULLY_STARTED_EJABBERD_AT") + " " + clusterNode.getNodeIp(),
					display);
			logger.log(Level.INFO, "Successfully started Ejabberd at {0}", new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO") + " " + clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_STARTING_EJABBERD_AT") + clusterNode.getNodeIp(),
					display);
			printMessage(
					Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + clusterNode.getNodeIp(),
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}

	}

	private void sendErlangCookie(XmppNodeInfoModel firstNode, XmppNodeInfoModel clusterNode, Display display)
			throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(firstNode.getNodeIp(), "root", firstNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("SENDING_ERLANG_COOKIE_FROM") + " " + firstNode.getNodeIp() + " to "
					+ clusterNode.getNodeIp(), display);
			manager.execCommand("scp /opt/ejabberd-16.06/.erlang.cookie root@{0}:/opt/ejabberd-16.06/",
					new Object[] { clusterNode.getNodeIp() });
			printMessage(Messages.getString("SUCCESSFULLY_SENT_ERLANG_COOKIE_FROM") + " " + firstNode.getNodeIp()
					+ " to " + clusterNode.getNodeIp(), display);
			logger.log(Level.INFO, "Successfully sent Erlang cookie from {0} to {1}",
					new Object[] { firstNode.getNodeIp(), clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(Messages.getString("COULD_NOT_CONNECT_TO") + " " + clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_ERLANG_COOKIE_FROM") + " "
					+ firstNode.getNodeIp() + " to " + clusterNode.getNodeIp(), display);
			printMessage(
					Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + firstNode.getNodeIp(),
					display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();
		}
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
				txtLogConsole.setSelection(txtLogConsole.getCharCount() - 1);
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

	/**
	 * Creates file under temporary file directory and writes configuration to
	 * it. Returns the temp file.
	 * 
	 * @param content
	 * @param fileName
	 * @return created temp file
	 */
	private File writeToFile(String content, String fileName) {

		File tempFile = null;

		try {
			tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);

			FileWriter fileWriter = new FileWriter(tempFile.getAbsoluteFile());

			BufferedWriter buffWriter = new BufferedWriter(fileWriter);

			buffWriter.write(content);
			buffWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempFile;
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
