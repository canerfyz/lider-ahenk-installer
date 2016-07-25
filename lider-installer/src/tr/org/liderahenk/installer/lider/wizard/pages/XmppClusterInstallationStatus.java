package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.ArrayList;
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
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;

public class XmppClusterInstallationStatus extends WizardPage
		implements IXmppPage, ControlNextEvent, InstallationStatusPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	private NextPageEventType nextPageEventType;

	boolean isInstallationFinished = false;

	boolean canGoBack = false;

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
									joinToCluster(firstNode.getNodeName(), clusterNode.getNodeIp(), display);
								}
							}

							installHaProxy(config.getXmppProxyAddress(), config.getXmppProxyPwd(),
									config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase(),
									config.getXmppNodeInfoMap());

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
						printMessage(Messages.getString("INSTALLER_WONT_CONTINUE_BECAUSE_NO_OF_NODES_SETUP_FAILED"),
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

	protected void installHaProxy(String xmppProxyAddress, String xmppProxyPwd, String xmppAccessKeyPath,
			String xmppAccessPassphrase, Map<Integer, XmppNodeInfoModel> xmppNodeInfoMap) {
		// TODO Auto-generated method stub

	}

	protected void joinToCluster(String nodeName, String nodeIp, Display display) {
		// TODO Auto-generated method stub

	}

	protected void startEjabberd(XmppNodeInfoModel clusterNode, Display display) throws Exception {
		SSHManager manager = null;
		try {
			manager = new SSHManager(clusterNode.getNodeIp(), "root", clusterNode.getNodeRootPwd(), config.getXmppPort(),
					config.getXmppAccessKeyPath(), config.getXmppAccessPassphrase());
			manager.connect();

			printMessage(Messages.getString("STARTING_EJABBERD_AT") + " " + clusterNode.getNodeIp(), display);
			manager.execCommand("/opt/ejabberd-16.06/bin/ejabberdctl start",
					new Object[] { clusterNode.getNodeIp() });
			printMessage(Messages.getString("SUCCESSFULLY_STARTED_EJABBERD_AT") + " " + clusterNode.getNodeIp(), display);
			logger.log(Level.INFO, "Successfully started Ejabberd at {0}",
					new Object[] { clusterNode.getNodeIp() });

		} catch (SSHConnectionException e) {
			printMessage(clusterNode.getNodeIp() + " " + Messages.getString("COULD_NOT_CONNECT_TO") + " "
					+ clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_STARTING_EJABBERD_AT") + clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + clusterNode.getNodeIp(), display);
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
			printMessage(clusterNode.getNodeIp() + " " + Messages.getString("COULD_NOT_CONNECT_TO") + " "
					+ clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("ERROR_MESSAGE") + " " + e.getMessage(), display);
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
			throw new Exception();

		} catch (CommandExecutionException e) {
			printMessage(Messages.getString("EXCEPTION_RAISED_WHILE_SENDING_ERLANG_COOKIE_FROM") + " "
					+ firstNode.getNodeIp() + " to " + clusterNode.getNodeIp(), display);
			printMessage(Messages.getString("EXCEPTION_MESSAGE") + " " + e.getMessage() + " at " + firstNode.getNodeIp(), display);
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
