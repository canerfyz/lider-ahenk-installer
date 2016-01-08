package tr.org.pardus.mys.liderahenksetup.ahenk.wizard.pages;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.ahenk.config.AhenkSetupConfig;
import tr.org.pardus.mys.liderahenksetup.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

public class AhenkSetupLocationPage extends WizardPage {

	private AhenkSetupConfig config;

	// Widgets
	private Button btnGivenIp;
	private Button btnNetworkScan;
	private Button btnLocal;
	private Text txtRemoteIp;

	// Status variable for the possible errors on this page
	IStatus ipStatus;

	public AhenkSetupLocationPage(AhenkSetupConfig config) {
		super(AhenkSetupLocationPage.class.getName(), Messages
				.getString("INSTALLATION_OF_AHENK"), null);
		setDescription(Messages
				.getString("WHERE_WOULD_YOU_LIKE_TO_INSTALL_AHENK"));
		this.config = config;
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}

	@Override
	public void createControl(final Composite parent) {
		
		Composite container = GUIHelper.createComposite(parent,
				new GridLayout(1, false), new GridData(GridData.FILL,
						GridData.FILL, false, false));
		
		setControl(container);

		Composite containerForButtons = GUIHelper.createComposite(container,
				new GridLayout(1, false), new GridData(GridData.FILL,
						GridData.FILL, true, false));
		Composite containerForGivenIp = GUIHelper.createComposite(container,
				new GridLayout(2, false), new GridData(GridData.FILL,
						GridData.FILL, true, false));

		// Perform network scan
		btnNetworkScan = GUIHelper
				.createButton(
						containerForButtons,
						SWT.RADIO,
						Messages.getString("I_WANT_TO_CHOOSE_IP_ADDRESSES_VIA_NETWORK_SCANNING"));
		btnNetworkScan.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnNetworkScan.getSelection()) {
					config.setInstallOnGivenIps(false);
					config.setPerformNetworkScanning(true);
					config.setInstallAhenkLocally(false);
					btnGivenIp.setSelection(false);
					setIpFieldDisabled();
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnNetworkScan.setSelection(true);

		// Install locally
		btnLocal = GUIHelper
				.createButton(
						containerForButtons,
						SWT.RADIO,
						Messages.getString("I_WANT_TO_INSTALL_TO_COMPUTER_WHICH_I_AM_ALREADY_WORKING_ON(LOCAL)"));
		btnLocal.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnLocal.getSelection()) {
					config.setInstallOnGivenIps(false);
					config.setPerformNetworkScanning(false);
					config.setInstallAhenkLocally(true);

					btnGivenIp.setSelection(false);

					setIpFieldDisabled();
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Install to given IPs.
		btnGivenIp = GUIHelper.createButton(containerForGivenIp, SWT.RADIO,
				Messages.getString("INSTALL_TO_THESE_IP_ADDRESSES"));
		btnGivenIp.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnGivenIp.getSelection()) {
					config.setInstallOnGivenIps(true);
					config.setPerformNetworkScanning(false);
					config.setInstallAhenkLocally(false);

					btnLocal.setSelection(false);
					btnNetworkScan.setSelection(false);

					// Enable txtRemoteIp only if btnGivenIp is selected
					txtRemoteIp.setEnabled(btnGivenIp.getSelection());
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Get remote machine IP if necessary
		txtRemoteIp = GUIHelper.createText(containerForGivenIp);
		txtRemoteIp.setEnabled(false);
		txtRemoteIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

	}

	// Check IP's one by one whether it is valid or not.
	// And return true if all are valid.
	private boolean isRemoteIpListValid(String txtRemoteIpValue) {

		String[] remoteIps = txtRemoteIpValue.split(", ");

		//Create IP list for config
		List<String> remoteIpList = new ArrayList<String>();
		
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);

		if (remoteIps == null || remoteIps.toString().isEmpty()) {
			status = new Status(IStatus.ERROR, "not_used", 0,
					Messages.getString("EMPTY_IP_ERROR"), null);
		} 
		else {
			for (int i = 0; i < remoteIps.length; i++) {
				if (!NetworkUtils.isIpValid(remoteIps[i])) {
					status = new Status(IStatus.ERROR, "not_used", 0,
							Messages.getString("INVALID_IP_FORMAT_ERROR")
									+ ": " + remoteIps[i], null);
					return false;
				}
				
				//Add to list
				//it will be used for IP list in config
				remoteIpList.add(remoteIps[i]);
				
				//There is no need to check if the IP is reacheable. It delays the screen a few seconds.
//				if (!NetworkUtils.isIpReachable(remoteIpList[i])) {
//					status = new Status(IStatus.ERROR, "not_used", 0,
//							Messages.getString("IP_UNREACHABLE_ERROR") + ": "
//									+ remoteIpList[i], null);
//					return false;
//				}
			}
		}

		//Set the IP list in config
		config.setIpList(remoteIpList);

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

	protected void updatePageCompleteStatus() {
		setPageComplete(btnLocal.getSelection()
				|| (btnNetworkScan.getSelection())
				// Check if entered ip list is valid.
				|| (btnGivenIp.getSelection() && isRemoteIpListValid(txtRemoteIp
						.getText())));
	}

	@Override
	public IWizardPage getNextPage() {
		LinkedList<IWizardPage> pagesList = ((AhenkSetupWizard) this
				.getWizard()).getPagesList();
		if (this.btnNetworkScan.getSelection()) {
			if (!AhenkNetworkScanPage.class.getName().equals(
					pagesList.get(1).getName())) {
				AhenkNetworkScanPage secondPage = new AhenkNetworkScanPage(
						config);
				secondPage.setWizard(getWizard());
				pagesList.add(1, secondPage);
			}
		} else if (AhenkNetworkScanPage.class.getName().equals(
				pagesList.get(1).getName())) {
			pagesList.remove(1);
		}
		
		if (btnLocal.getSelection()) {
			//Create IP list for config
			List<String> remoteIpList = new ArrayList<String>();
			remoteIpList.add("localhost");
			config.setIpList(remoteIpList);
			
			//If Ahenk will be installed to local,
			//then there is no need to ask for distribution method
			pagesList.remove(3);
		}

		return super.getNextPage();
	}

	private void setIpFieldDisabled() {
		txtRemoteIp.setEnabled(false);
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

}