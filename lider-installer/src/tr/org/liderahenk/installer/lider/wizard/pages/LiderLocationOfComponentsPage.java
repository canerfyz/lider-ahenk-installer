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
	private Button btnInstallDistributed;
	private Text txtDatabaseIp;
	private Text txtLdapIp;
	private Text txtXmppIp;
	private Text txtLiderIp;

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
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.createComposite(mainContainer, gl, new GridData());

		// Install locally
		btnLocal = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("LOCAL_COMPUTER"));
		GUIHelper.createLabel(childContainer, "");

		// Install to a remote computer
		btnRemote = GUIHelper.createButton(childContainer, SWT.RADIO, Messages.getString("REMOTE_COMPUTER"));

		// Creating a text field with width 150px.
		GridData gdForTextField = new GridData();
		gdForTextField.widthHint = 150;
		txtRemoteIp = GUIHelper.createText(childContainer, gdForTextField);

		btnInstallDistributed = GUIHelper.createButton(mainContainer, SWT.RADIO,
				Messages.getString("INSTALL_COMPONENT_TO_DIFFERENT_COMPUTERS"));

		// Creating another child container.
		Composite secondChild = GUIHelper.createComposite(mainContainer, gl, new GridData());

		// IP's for components will be taken in this section.
		GUIHelper.createLabel(secondChild, Messages.getString("DATABASE") + " 1.0");

		txtDatabaseIp = GUIHelper.createText(secondChild, gdForTextField);

		GUIHelper.createLabel(secondChild, Messages.getString("LDAP") + " 1.0");

		txtLdapIp = GUIHelper.createText(secondChild, gdForTextField);

		GUIHelper.createLabel(secondChild, Messages.getString("XMPP") + " 1.0");

		txtXmppIp = GUIHelper.createText(secondChild, gdForTextField);

		GUIHelper.createLabel(secondChild, Messages.getString("LIDER") + " 1.0");

		txtLiderIp = GUIHelper.createText(secondChild, gdForTextField);

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
	}

	// This method organizes button, fields etc.
	// according to selections etc.
	private void organizeFields() {

		if (btnInstallDistributed.getSelection()) {

			// Disable first option
			btnLocal.setEnabled(false);

			btnRemote.setEnabled(false);

			txtRemoteIp.setEnabled(false);

			// Enable second option
			txtDatabaseIp.setEnabled(true);

			txtLdapIp.setEnabled(true);

			txtXmppIp.setEnabled(true);

			txtLiderIp.setEnabled(true);
		} else {
			// Enable first option
			btnLocal.setEnabled(true);

			btnRemote.setEnabled(true);

			organizeInnerFields();

			// Disable second option
			txtDatabaseIp.setEnabled(false);

			txtLdapIp.setEnabled(false);

			txtXmppIp.setEnabled(false);

			txtLiderIp.setEnabled(false);
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
		} else {
			if (checkRequiredIps()) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}
		}
	}

	private boolean checkRequiredIps() {
		// If a component is selected
		// then entering a valid IP is mandatory.
		if (config.isInstallDatabase() && !NetworkUtils.isIpValid(txtDatabaseIp.getText())) {
			return false;
		}
		if (config.isInstallLdap() && !NetworkUtils.isIpValid(txtLdapIp.getText())) {
			return false;
		}
		if (config.isInstallXmpp() && !NetworkUtils.isIpValid(txtXmppIp.getText())) {
			return false;
		}
		if (config.isInstallLider() && !NetworkUtils.isIpValid(txtLiderIp.getText())) {
			return false;
		}
		// If all IP's are entered and valid then return true.
		return true;
	}

	private void setConfigVariables() {
		// If components will be installed to same machine
		if (btnInstallCentral.getSelection()) {
			// If all components will be installed to localhost
			if (btnLocal.getSelection()) {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp("localhost");
				}
				if (config.isInstallLdap()) {
					config.setLdapIp("localhost");
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp("localhost");
				}
				if (config.isInstallLider()) {
					config.setLiderIp("localhost");
				}
			}
			// If all components will be installed to a remote computer
			else {
				// Set only selected components
				if (config.isInstallDatabase()) {
					config.setDatabaseIp(txtRemoteIp.getText());
				}
				if (config.isInstallLdap()) {
					config.setLdapIp(txtRemoteIp.getText());
				}
				if (config.isInstallXmpp()) {
					config.setXmppIp(txtRemoteIp.getText());
				}
				if (config.isInstallLider()) {
					config.setLiderIp(txtRemoteIp.getText());
				}
			}
		}
		// If components will be installed distributed.
		else {
			// Set only selected components
			if (config.isInstallDatabase()) {
				config.setDatabaseIp(txtDatabaseIp.getText());
			}
			if (config.isInstallLdap()) {
				config.setLdapIp(txtLdapIp.getText());
			}
			if (config.isInstallXmpp()) {
				config.setXmppIp(txtXmppIp.getText());
			}
			if (config.isInstallLider()) {
				config.setLiderIp(txtLiderIp.getText());
			}
		}
	}

	/**
	 * This method decides next page according to user's component choices
	 * 
	 * @return
	 */
	private IWizardPage selectNextPage() {
		// TODO get these list indices programmatically!
		LinkedList<IWizardPage> pagesList = ((LiderSetupWizard) this.getWizard()).getPagesList();
		if (config.isInstallDatabase()) {
			return pagesList.get(2);
		} else if (config.isInstallLdap()) {
			return pagesList.get(6);
		} else if (config.isInstallXmpp()) {
			return pagesList.get(10);
		} else { // Lider
			return pagesList.get(14);
		}
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
		return selectNextPage();
	}

}
