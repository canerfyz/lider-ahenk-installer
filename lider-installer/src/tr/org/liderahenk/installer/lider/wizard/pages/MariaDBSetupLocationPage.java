package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MariaDBSetupLocationPage extends WizardPage {

	private LiderSetupConfig config;

	// Widgets
	private Button btnLocal;
	private Button btnRemote;
	private Text txtRemoteIp;

	// Status variable for the possible errors on this page
	IStatus ipStatus;

	public MariaDBSetupLocationPage(LiderSetupConfig config) {
		super(MariaDBSetupLocationPage.class.getName(), Messages
				.getString("DB_SETUP_PAGE_TITLE"), null);
		setDescription(Messages.getString("DB_SETUP_LOCATION_DESC"));
		this.config = config;
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// Ask user if MariaDB will be installed on local or remote machine
		btnLocal = GUIHelper.createButton(container, SWT.RADIO,
				Messages.getString("DB_SETUP_LOCATION_LOCAL"));
		btnLocal.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnLocal.getSelection()) {
					config.setInstallDatabaseOnRemote(false);
					config.setDatabaseIp("127.0.0.1");
				}
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRemote = GUIHelper.createButton(container, SWT.RADIO,
				Messages.getString("DB_SETUP_LOCATION_REMOTE"));
		btnRemote.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnRemote.getSelection()) {
					config.setInstallDatabaseOnRemote(true);
					config.setDatabaseIp(null);
				}
				// Enable txtRemoteIp only if btnRemote is selected
				txtRemoteIp.setEnabled(btnRemote.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group grpRemoteIp = GUIHelper.createGroup(container, new GridLayout(2,
				false), new GridData(SWT.FILL, SWT.FILL, false, false));

		GUIHelper.createLabel(grpRemoteIp,
				Messages.getString("DB_SETUP_LOCATION_REMOTE_IP"));

		// Get remote machine IP if necessary
		txtRemoteIp = GUIHelper.createText(grpRemoteIp, new GridData(SWT.FILL,
				SWT.FILL, true, false));
		txtRemoteIp.setEnabled(false);
		txtRemoteIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		updatePageCompleteStatus();
	}

	protected void updatePageCompleteStatus() {
		setPageComplete(btnLocal.getSelection()
				|| (btnRemote.getSelection() && checkRemoteIp()));
	}

	private boolean checkRemoteIp() {
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);
		if (txtRemoteIp.getText() == null || txtRemoteIp.getText().isEmpty()) {
			status = new Status(IStatus.ERROR, "not_used", 0,
					Messages.getString("EMPTY_IP_ERROR"), null);
			return false;
		}
		if (!NetworkUtils.isIpValid(txtRemoteIp.getText())) {
			status = new Status(IStatus.ERROR, "not_used", 0,
					Messages.getString("INVALID_IP_FORMAT_ERROR"), null);
			return false;
		}
		if (!NetworkUtils.isIpReachable(txtRemoteIp.getText())) {
			status = new Status(IStatus.ERROR, "not_used", 0,
					Messages.getString("IP_UNREACHABLE_ERROR"), null);
			return false;
		}
		ipStatus = status;
		applyToStatusLine(ipStatus);
		getWizard().getContainer().updateButtons();
		return true;
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 */
	private void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0)
			message = null;
		switch (status.getSeverity()) {
		case IStatus.OK:
			setErrorMessage(null);
			setMessage(message);
			break;
		case IStatus.WARNING:
			setErrorMessage(null);
			setMessage(message, WizardPage.WARNING);
			break;
		case IStatus.INFO:
			setErrorMessage(null);
			setMessage(message, WizardPage.INFORMATION);
			break;
		default:
			setErrorMessage(message);
			setMessage(null);
			break;
		}
	}

}
