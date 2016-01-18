package tr.org.liderahenk.installer.ahenk.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.liderahenk.installer.ahenk.utils.AhenkInstallationUtil;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class AhenkInstallationStatusPage extends WizardPage {

	private AhenkSetupConfig config = null;

	// Widgets
	private Composite mainContainer = null;

	private ProgressBar progressBar = null;

	private Button startInstallation = null;

	private Table table;

	private Label label;

	private Composite childContainer = null;

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
		mainContainer = new Composite(parent, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		setControl(mainContainer);

		childContainer = new Composite(mainContainer, SWT.NONE);
		childContainer.setLayout(new GridLayout(2, false));

		label = new Label(childContainer, SWT.NONE);
		label.setText(Messages.getString("AHENK_WILL_BE_INSTALLED_TO_MACHINES_WITH_IPS_GIVEN_BELOW") + " "
				+ Messages.getString("WOULD_YOU_LIKE_TO_CONTINUE"));

		startInstallation = new Button(childContainer, SWT.PUSH);
		startInstallation.setText(Messages.getString("START_INSTALLATION"));
		startInstallation.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					progressBar.setVisible(true);
					startInstallation.setVisible(false);
					AhenkInstallationUtil.installAhenk(config, progressBar, table, label);
				} catch (Exception exception) {

				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		progressBar = new ProgressBar(mainContainer, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setVisible(false);
		GridData progressGd = new GridData();
		progressGd.heightHint = 40;
		progressGd.widthHint = 500;
		progressBar.setLayoutData(progressGd);

	}

	@Override
	public boolean canFlipToNextPage() {

		return super.canFlipToNextPage();
	}

}
