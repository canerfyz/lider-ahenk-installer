package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.LinkedList;

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
import org.eclipse.swt.widgets.Label;
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

	// Widgets
	private Button installCentral;
	private Button local;
	private Button remote;
	private Text remoteIp;
	
	private Button installDistributed;
	private Label mariaDb;
	private Label ldap;
	private Label ejabberd;
	private Label lider;
	private Text mariaDbIp;
	private Text ldapIp;
	private Text ejabberdIp;
	private Text liderIp;
	
	// In createControl method, I just create its instance
	// that's why it seems like "unused".
	@SuppressWarnings("unused")
	private Label emptySpace;

	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	public LiderLocationOfComponentsPage(LiderSetupConfig config) {
		
		super(LiderLocationOfComponentsPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);
		
		setDescription("1.2 " + 
				Messages.getString("WHERE_TO_INSTALL_COMPONENTS"));

		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		setControl(mainContainer);
		
		// Install to same computer
		installCentral = GUIHelper.createButton(mainContainer, SWT.RADIO, 
				Messages.getString("INSTALL_ALL_COMPONENTS_TO_SAME_COMPUTER"));

		// Creating a child container with two columns
		// and extra indent.  
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.
				createComposite(mainContainer, gl, new GridData());
		
		// Install locally
		local = GUIHelper.createButton(childContainer, SWT.RADIO, 
				Messages.getString("LOCAL_COMPUTER"));

		// To fill second column of child container.
		emptySpace = GUIHelper.createLabel(childContainer, "");
		
		// Install to a remote computer
		remote = GUIHelper.createButton(childContainer, SWT.RADIO, 
				Messages.getString("REMOTE_COMPUTER"));

		// Creating a text field with width 150px.
		GridData gdForTextField = new GridData();
		gdForTextField.widthHint = 150;
		remoteIp = GUIHelper.createText(childContainer, gdForTextField);
		
		installDistributed = GUIHelper.createButton(mainContainer, SWT.RADIO, 
				Messages.getString("INSTALL_COMPONENT_TO_DIFFERENT_COMPUTERS"));
		
		// Creating another child container.
		Composite secondChild = GUIHelper.
				createComposite(mainContainer, gl, new GridData());
		
		// IP's for components will be taken in this section.
		mariaDb = GUIHelper.createLabel(secondChild, 
				Messages.getString("MARIA_DB_VERSION") + " 1.0");
		
		mariaDbIp = GUIHelper.createText(secondChild, gdForTextField);
		
		ldap = GUIHelper.createLabel(secondChild, 
				Messages.getString("OPENLDAP_VERSION") + " 1.0");

		ldapIp = GUIHelper.createText(secondChild, gdForTextField);
		
		ejabberd = GUIHelper.createLabel(secondChild, 
				Messages.getString("EJABBERD_VERSION") + " 1.0");

		ejabberdIp = GUIHelper.createText(secondChild, gdForTextField);
		
		lider = GUIHelper.createLabel(secondChild, 
				Messages.getString("LIDER_VERSION") + " 1.0");
		
		liderIp = GUIHelper.createText(secondChild, gdForTextField);
		
		// Adding selection listeners for
		// user's choices on radio buttons
		installCentral.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		installDistributed.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
				disableNotSelectedComponents();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		local.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		remote.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeInnerFields();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// canNext must be triggered when IP text fields modified.
		remoteIp.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});

		mariaDbIp.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});
		
		ldapIp.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});

		ejabberdIp.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});

		liderIp.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});
		
		// Second option should come as selected
		// in the opening of page.
		installDistributed.setSelection(true);
		
		// This method sets fields enable/disable
		// according to user's radio button choices 
		organizeFields();
		
	}

	// This method organizes button, fields etc.
	// according to selections etc.
	private void organizeFields() {
		
		if (installDistributed.getSelection()) {
			
			// Disable first option
			local.setEnabled(false);
			
			remote.setEnabled(false);
			
			remoteIp.setEnabled(false);
			
			// Enable second option
			mariaDbIp.setEnabled(true);

			ldapIp.setEnabled(true);
			
			ejabberdIp.setEnabled(true);
			
			liderIp.setEnabled(true);
		}
		else {
			// Enable first option
			local.setEnabled(true);
			
			remote.setEnabled(true);
			
			organizeInnerFields();
			
			// Disable second option
			mariaDbIp.setEnabled(false);
			
			ldapIp.setEnabled(false);
			
			ejabberdIp.setEnabled(false);
			
			liderIp.setEnabled(false);
		}
	}
	
	private void organizeInnerFields() {
		// If it is the first selection 
		// then select local installation as default
		// (that means both of them not selected)
		if (installCentral.getSelection()) {
			if (!(local.getSelection() && remote.getSelection())) {
				local.setSelection(true);
			}
		}
		
		// If install to remote is selected 
		// then enable IP text field 
		if (remote.getSelection()) {
			remoteIp.setEnabled(true);
			local.setSelection(false);
		}
		else {
			remoteIp.setEnabled(false);
		}
	}
	
public void disableNotSelectedComponents() {
		
		// If a component is not selected
		// then change its style to disabled.
		if (!(config.getComponentsToBeInstalled().get("MariaDB"))) {
			setDisableStyle(mariaDb, mariaDbIp);
		}
		
		if (!(config.getComponentsToBeInstalled().get("LDAP"))) {
			setDisableStyle(ldap, ldapIp);
		}
		
		if (!(config.getComponentsToBeInstalled().get("Ejabberd"))) {
			setDisableStyle(ejabberd, ejabberdIp);
		}
		
		if (!(config.getComponentsToBeInstalled().get("Lider"))) {
			setDisableStyle(lider, liderIp);
		}
	}
	
	// Set a component's style to disabled
	private void setDisableStyle(Label label, Text text) {
		
		text.setEnabled(false);
	}

	// This method decides to next button's status.
	private void canNext() {
		
		if (installCentral.getSelection()) {
			if (local.getSelection()) {
				setPageComplete(true);
			}
			else if (remote.getSelection() && 
					NetworkUtils.isIpValid(remoteIp.getText())) {
				setPageComplete(true);
			}
			else {
				setPageComplete(false);
			}
		}
		else {
			if (checkRequiredIps()) {
				setPageComplete(true);
			}
			else {
				setPageComplete(false);
			}
		}
	}
	
	private boolean checkRequiredIps() {
		
		boolean mariaDbIpValid = true;
		
		boolean ldapIpValid = true;
		
		boolean ejabberdIpValid = true;
		
		boolean liderIpValid = true;
		
		// If component is selected
		// then entering a valid IP is mandatory.
		if (config.getComponentsToBeInstalled().get("MariaDB")) {
			if (NetworkUtils.isIpValid(mariaDbIp.getText())) {
				mariaDbIpValid = true;
			}
			else {
				mariaDbIpValid = false;
			}
		}

		if (config.getComponentsToBeInstalled().get("LDAP")) {
			if (NetworkUtils.isIpValid(ldapIp.getText())) {
				ldapIpValid = true;
			}
			else {
				ldapIpValid = false;
			}
		}

		if (config.getComponentsToBeInstalled().get("Ejabberd")) {
			if (NetworkUtils.isIpValid(ejabberdIp.getText())) {
				ejabberdIpValid = true;
			}
			else {
				ejabberdIpValid = false;
			}
		}

		if (config.getComponentsToBeInstalled().get("Ejabberd")) {
			if (NetworkUtils.isIpValid(ejabberdIp.getText())) {
				liderIpValid = true;
			}
			else {
				liderIpValid = false;
			}
		}
		
		// If all IP's are entered and valid then return true.
		return (mariaDbIpValid && ldapIpValid 
				&& ejabberdIpValid && liderIpValid);
	}

	// This method sets the info taken
	// from user to variables in config.
	private void setConfigVariables() {
		/**
		 * If components will be installed to same machine
		 */
		if (installCentral.getSelection()) {
			/**
			 * If all components will be installed to localhost
			 */
			if (local.getSelection()) {
				/**
				 * Set only selected components
				 */
				if (config.getComponentsToBeInstalled().get("MariaDB")) {
					config.setMariaDbIp("localhost");
				}
				
				if (config.getComponentsToBeInstalled().get("LDAP")) {
					config.setLdapIp("localhost");
				}
				
				if (config.getComponentsToBeInstalled().get("Ejabberd")) {
					config.setEjabberdIp("localhost");
				}
				
				if (config.getComponentsToBeInstalled().get("Lider")) {
					config.setLiderIp("localhost");
				}
			}
			/**
			 * If all components will be installed to a remote computer
			 */
			else {
				/**
				 * Set only selected components
				 */
				if (config.getComponentsToBeInstalled().get("MariaDB")) {
					config.setMariaDbIp(remoteIp.getText());
				}
				
				if (config.getComponentsToBeInstalled().get("LDAP")) {
					config.setLdapIp(remoteIp.getText());
				}
				
				if (config.getComponentsToBeInstalled().get("Ejabberd")) {
					config.setEjabberdIp(remoteIp.getText());
				}
				
				if (config.getComponentsToBeInstalled().get("Lider")) {
					config.setLiderIp(remoteIp.getText());
				}
			}
		}
		/**
		 * If components will be installed distributed.
		 */
		else {
			/**
			 * Set only selected components
			 */
			if (config.getComponentsToBeInstalled().get("MariaDB")) {
				config.setMariaDbIp(mariaDbIp.getText());
			}
			
			if (config.getComponentsToBeInstalled().get("LDAP")) {
				config.setLdapIp(ldapIp.getText());
			}
			
			if (config.getComponentsToBeInstalled().get("Ejabberd")) {
				config.setEjabberdIp(ejabberdIp.getText());
			}
			
			if (config.getComponentsToBeInstalled().get("Lider")) {
				config.setLiderIp(liderIp.getText());
			}
		}
	}
	
	// This method decides next page
	// according to user's component choices
	private IWizardPage selectNextPage() {
		LinkedList<IWizardPage> pagesList = 
				((LiderSetupWizard) this.getWizard()).getPagesList();

		if (config.getComponentsToBeInstalled().get("MariaDB")) {
			return pagesList.get(2);
		}
		else if (config.getComponentsToBeInstalled().get("LDAP")) {
			return pagesList.get(6);
		}
		else if (config.getComponentsToBeInstalled().get("Ejabberd")) {
			return pagesList.get(10);
		}
		else {
			return pagesList.get(14);
		}
	}
	
	@Override
	public IWizardPage getNextPage() {
		
		// Set variables before going to next page.
		setConfigVariables();
		
		// selectNextPage returns proper page.
		return selectNextPage();
	}
	
	public Label getMariaDb() {
		return mariaDb;
	}

	public void setMariaDb(Label mariaDb) {
		this.mariaDb = mariaDb;
	}

	public Label getLdap() {
		return ldap;
	}

	public void setLdap(Label ldap) {
		this.ldap = ldap;
	}

	public Label getEjabberd() {
		return ejabberd;
	}

	public void setEjabberd(Label ejabberd) {
		this.ejabberd = ejabberd;
	}

	public Label getLider() {
		return lider;
	}

	public void setLider(Label lider) {
		this.lider = lider;
	}

	public Text getMariaDbIp() {
		return mariaDbIp;
	}

	public void setMariaDbIp(Text mariaDbIp) {
		this.mariaDbIp = mariaDbIp;
	}

	public Text getLdapIp() {
		return ldapIp;
	}

	public void setLdapIp(Text ldapIp) {
		this.ldapIp = ldapIp;
	}

	public Text getEjabberdIp() {
		return ejabberdIp;
	}

	public void setEjabberdIp(Text ejabberdIp) {
		this.ejabberdIp = ejabberdIp;
	}

	public Text getLiderIp() {
		return liderIp;
	}

	public void setLiderIp(Text liderIp) {
		this.liderIp = liderIp;
	}

	public Button getInstallDistributed() {
		return installDistributed;
	}

	public void setInstallDistributed(Button installDistributed) {
		this.installDistributed = installDistributed;
	}

}
