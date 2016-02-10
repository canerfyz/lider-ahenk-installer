package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.LinkedList;

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

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderLocationOfComponentsPage extends WizardPage {

	private LiderSetupConfig config;

	private Button btnInstallCentral;
	private Button btnLocal;
	private Button btnRemote;
	private Text txtRemoteIp;
	private Text txtRemotePort;
	private Button btnInstallDistributed;
	private Text txtDatabaseIp;
	private Text txtLdapIp;
	private Text txtXmppIp;
	private Text txtLiderIp;
	private Text txtDatabasePort;
	private Text txtLdapPort;
	private Text txtXmppPort;
	private Text txtLiderPort;

	public LiderLocationOfComponentsPage(LiderSetupConfig config) {
		super(LiderLocationOfComponentsPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("1.2 " + Messages.getString("WHERE_TO_INSTALL_COMPONENTS"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		// Install to same computer
		btnInstallCentral = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("INSTALL_ALL_COMPONENTS_TO_SAME_COMPUTER"));

		// Creating a child container with two columns
		// and extra indent.
		GridLayout gl = new GridLayout(3, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.createComposite(mainContainer, gl, new GridData());

		// Install locally
		btnLocal = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("LOCAL_COMPUTER"));
		GUIHelper.createLabel(childContainer, "");
		GUIHelper.createLabel(childContainer, "");

		// Install to a remote computer
		btnRemote = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("REMOTE_COMPUTER"));

		// Creating a text field with width 150px.
		GridData gdForIpField = new GridData();
		gdForIpField.widthHint = 150;
		txtRemoteIp = GUIHelper.createText(childContainer, gdForIpField);

		GridData gdForPortField = new GridData();
		gdForPortField.widthHint = 50;
		gdForPortField.grabExcessHorizontalSpace = false;
		txtRemotePort = GUIHelper.createText(childContainer);
		txtRemotePort.setText("22");

		btnInstallDistributed = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("INSTALL_COMPONENT_TO_DIFFERENT_COMPUTERS"));

		// Creating another child container.
		Composite secondChild = GUIHelper.createComposite(mainContainer, gl, new GridData());

		// IP's for components will be taken in this section.
		GUIHelper.createLabel(secondChild, Messages.getString("DATABASE"));

		txtDatabaseIp = GUIHelper.createText(secondChild, gdForIpField);

		txtDatabasePort = GUIHelper.createText(secondChild, gdForPortField);
		txtDatabasePort.setText("22");

		GUIHelper.createLabel(secondChild, Messages.getString("LDAP"));

		txtLdapIp = GUIHelper.createText(secondChild, gdForIpField);

		txtLdapPort = GUIHelper.createText(secondChild, gdForPortField);
		txtLdapPort.setText("22");

		GUIHelper.createLabel(secondChild, Messages.getString("XMPP"));

		txtXmppIp = GUIHelper.createText(secondChild, gdForIpField);

		txtXmppPort = GUIHelper.createText(secondChild, gdForPortField);
		txtXmppPort.setText("22");

		GUIHelper.createLabel(secondChild, Messages.getString("LIDER"));

		txtLiderIp = GUIHelper.createText(secondChild, gdForIpField);

		txtLiderPort = GUIHelper.createText(secondChild, gdForPortField);
		txtLiderPort.setText("22");

		// Adding selection listeners for
		// user's choices on radio buttons
		btnInstallCentral.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnInstallDistributed.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnLocal.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRemote.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// canNext must be triggered when IP text fields modified.
		txtRemoteIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		txtDatabaseIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		txtLdapIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		txtXmppIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		txtLiderIp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		// Second option should come as selected
		// in the opening of page.
		btnInstallDistributed.setSelection(true);

		// This method sets fields enable/disable
		// according to user's radio button choices
		organizeFields();
		updatePageCompleteStatus();
	}

	// This method organizes button, fields etc.
	// according to selections etc.
	private void organizeFields() {
		if (btnInstallDistributed.getSelection()) {

			// Disable first option
			btnLocal.setEnabled(false);
			btnRemote.setEnabled(false);
			txtRemoteIp.setEnabled(false);
			txtRemotePort.setEnabled(false);

			// Enable second option
			if (config.isInstallDatabase()) {
				txtDatabaseIp.setEnabled(true);
				txtDatabasePort.setEnabled(true);
			}
			if (config.isInstallLdap()) {
				txtLdapIp.setEnabled(true);
				txtLdapPort.setEnabled(true);
			}
			if (config.isInstallXmpp()) {
				txtXmppIp.setEnabled(true);
				txtXmppPort.setEnabled(true);
			}
			if (config.isInstallLider()) {
				txtLiderIp.setEnabled(true);
				txtLiderPort.setEnabled(true);
			}
		} else {
			// Enable first option
			btnLocal.setEnabled(true);
			btnRemote.setEnabled(true);

			organizeInnerFields();

			// Disable second option
			txtDatabaseIp.setEnabled(false);
			txtDatabasePort.setEnabled(false);
			txtLdapIp.setEnabled(false);
			txtLdapPort.setEnabled(false);
			txtXmppIp.setEnabled(false);
			txtXmppPort.setEnabled(false);
			txtLiderIp.setEnabled(false);
			txtLiderPort.setEnabled(false);
		}
	}

	private void organizeInnerFields() {
		// If it is the first selection
		// then select local installation as default
		// (that means both of them not selected)
		btnLocal.setSelection(!(btnLocal.getSelection() && btnRemote.getSelection()));
		// If 'install to remote' is selected
		// then enable IP text field
		txtRemoteIp.setEnabled(btnRemote.getSelection());
		txtRemotePort.setEnabled(btnRemote.getSelection());
	}

	// This method decides to next button's status.
	private void updatePageCompleteStatus() {
		if (btnInstallCentral.getSelection()) {
			if (btnLocal.getSelection()) {
				setPageComplete(true);
			} else if (btnRemote.getSelection() && NetworkUtils.isIpValid(txtRemoteIp.getText())) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		} else { // btnInstallDistributed
			setPageComplete(checkRequiredIps());
		}
	}

	private boolean checkRequiredIps() {
		return ((config.isInstallDatabase() && NetworkUtils.isIpValid(txtDatabaseIp.getText()))
				|| (config.isInstallLdap() && NetworkUtils.isIpValid(txtLdapIp.getText()))
				|| (config.isInstallXmpp() && NetworkUtils.isIpValid(txtXmppIp.getText()))
				|| (config.isInstallLider() && NetworkUtils.isIpValid(txtLiderIp.getText())));
	}

	private void setConfigVariables() {
		// If components will be installed to same machine
		if (btnInstallCentral.getSelection()) {
			// If all components will be installed to localhost
			if (btnLocal.getSelection()) {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp("localhost");
					config.setDatabasePort(null);
				}
				if (config.isInstallLdap()) {
					config.setLdapIp("localhost");
					config.setLdapPort(null);
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp("localhost");
					config.setXmppPort(null);
				}
				if (config.isInstallLider()) {
					config.setLiderIp("localhost");
					config.setLiderPort(null);
				}
			}
			// If all components will be installed to a remote computer
			else {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp(txtRemoteIp.getText());
					config.setDatabasePort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
				}
				if (config.isInstallLdap()) {
					config.setLdapIp(txtRemoteIp.getText());
					config.setLdapPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp(txtRemoteIp.getText());
					config.setXmppPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
				}
				if (config.isInstallLider()) {
					config.setLiderIp(txtRemoteIp.getText());
					config.setLiderPort(txtRemotePort.getText() != null && !txtRemotePort.getText().isEmpty()
							? new Integer(txtRemotePort.getText()) : null);
				}
			}
		}
		// If components will be installed distributed.
		else {
			// Set only selected components
			if (config.isInstallDatabase()) {
				config.setDatabaseIp(txtDatabaseIp.getText());
				config.setDatabasePort(txtDatabasePort.getText() != null && !txtDatabasePort.getText().isEmpty()
						? new Integer(txtDatabasePort.getText()) : null);
			}
			if (config.isInstallLdap()) {
				config.setLdapIp(txtLdapIp.getText());
				config.setLdapPort(txtLdapPort.getText() != null && !txtLdapPort.getText().isEmpty()
						? new Integer(txtLdapPort.getText()) : null);
			}
			if (config.isInstallXmpp()) {
				config.setXmppIp(txtXmppIp.getText());
				config.setXmppPort(txtXmppPort.getText() != null && !txtXmppPort.getText().isEmpty()
						? new Integer(txtXmppPort.getText()) : null);
			}
			if (config.isInstallLider()) {
				config.setLiderIp(txtLiderIp.getText());
				config.setLiderPort(txtLiderPort.getText() != null && !txtLiderPort.getText().isEmpty()
						? new Integer(txtLiderPort.getText()) : null);
			}
		}
	}

	/**
	 * This method decides next page according to user's component choices
	 * 
	 * @return
	 */
	private IWizardPage selectNextPage() {
		LinkedList<IWizardPage> pagesList = ((LiderSetupWizard) this.getWizard()).getPagesList();
		if (config.isInstallDatabase()) {
			return findFirstInstance(pagesList, IDatabasePage.class);
		} else if (config.isInstallLdap()) {
			return findFirstInstance(pagesList, ILdapPage.class);
		} else if (config.isInstallXmpp()) {
			return findFirstInstance(pagesList, IXmppPage.class);
		} else { // Lider
			return findFirstInstance(pagesList, ILiderPage.class);
		}
	}

	/**
	 * Tries to find the first instance of the provided class in the linked list.
	 * 
	 * @param pagesList
	 * @param cls
	 * @return
	 */
	private IWizardPage findFirstInstance(LinkedList<IWizardPage> pagesList, Class<?> cls) {
		if (pagesList != null) {
			for (IWizardPage page : pagesList) {
				if (cls.isInstance(page)) {
					return page;
				}
			}
		}
		return null;
	}

	public void updatePage() {
		// If a component is not selected
		// then change its style to disabled.
		txtDatabaseIp.setEnabled(config.isInstallDatabase());
		txtLdapIp.setEnabled(config.isInstallLdap());
		txtXmppIp.setEnabled(config.isInstallXmpp());
		txtLiderIp.setEnabled(config.isInstallLider());

		organizeFields();
	}

	@Override
	public IWizardPage getNextPage() {
		setConfigVariables();

		((AccessPage) selectNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);
		
		return selectNextPage();
	}

}
