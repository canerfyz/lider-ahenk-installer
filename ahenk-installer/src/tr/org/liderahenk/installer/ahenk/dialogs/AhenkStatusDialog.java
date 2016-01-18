package tr.org.liderahenk.installer.ahenk.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.liderahenk.installer.ahenk.utils.AhenkInstallationUtil;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;

/**
 * @author caner Caner Feyzullahoğlu caner.feyzullahoglu@agem.com.tr
 */
public class AhenkStatusDialog extends TitleAreaDialog {

	private AhenkSetupConfig config = new AhenkSetupConfig();
	private Table table;
	private ProgressBar progressBar = null;
	private Button startButton = null;
	private Button stopButton = null;
	private Label label = null;

	public AhenkSetupConfig getConfig() {
		return config;
	}

	public void setConfig(AhenkSetupConfig config) {
		this.config = config;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public AhenkStatusDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		setTitle(Messages.getString("INSTALLATION_OF_AHENK"));

		label = new Label(container, SWT.NONE);
		label.setBounds(20, 20, 700, 20);

		// TODO here set the text according to previous selections
		// and then ask whether continue or not.
		label.setText(Messages.getString("AHENK_WILL_BE_INSTALLED_TO_MACHINES_WITH_IPS_GIVEN_BELOW") + " "
				+ Messages.getString("WOULD_YOU_LIKE_TO_CONTINUE"));

		progressBar = new ProgressBar(container, SWT.SMOOTH);
		progressBar.setBounds(20, 45, 650, 50);
		progressBar.setVisible(false);

		startButton = new Button(container, SWT.NONE);
		startButton.setBounds(20, 45, 200, 40);
		startButton.setText(Messages.getString("YES_START_INSTALLATION"));
		startButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				startButton.setVisible(false);
				progressBar.setVisible(true);
				progressBar.setMaximum(config.getIpList().size());
				label.setText(Messages
						.getString("INSTALLING_AHENK_TO_GIVEN_MACHINES" + " " + Messages.getString("PLEASE_WAIT")));
				AhenkInstallationUtil.installAhenk(config, progressBar, table, label);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		stopButton = new Button(container, SWT.NONE);
		stopButton.setBounds(230, 45, 350, 40);
		stopButton.setText(Messages.getString("NO_I_WOULD_LIKE_TO_CHANGE_SOME_INFORMATION_RETURN_BACK"));
		stopButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Close window dialog
				close();
				// And show parent again
				getParentShell().setVisible(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(20, 100, 650, 650);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn ipCol = new TableColumn(table, SWT.NONE);
		ipCol.setWidth(200);
		ipCol.setText("IP");

		TableColumn statusCol = new TableColumn(table, SWT.NONE);
		statusCol.setText(Messages.getString("STATUS"));
		statusCol.setWidth(450);

		populateTable();

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CLOSE"), false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 800);
	}

	private void populateTable() {
		for (int i = 0; i < config.getIpList().size(); i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(
					new String[] { config.getIpList().get(i), Messages.getString("WAITING_TO_START_INSTALLATION") });
		}
	}
}
