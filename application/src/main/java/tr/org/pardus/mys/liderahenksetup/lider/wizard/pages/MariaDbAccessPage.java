package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class MariaDbAccessPage extends WizardPage {

	private LiderSetupConfig config;
	
	private Button usernamePassword;
	private Label username;
	private Label password;
	private Text usernameTxt; 
	private Text passwordTxt; 
	
	private Button usePrivateKey;
	private Text privateKeyTxt;
	private Button uploadKey;
	private FileDialog dialog; 
	private String selectedFile;
	private Label passphrase;
	private Text passphraseTxt;
	
	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	public MariaDbAccessPage(LiderSetupConfig config) {
		super(MariaDbAccessPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);

		setDescription("2.1 " + 
				Messages.getString("MARIA_DB_ACCESS_FOR_INSTALLATION"));
		
		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		setControl(mainContainer);
		
		// Access with username and password
		usernamePassword = GUIHelper.createButton(mainContainer, 
				SWT.RADIO, Messages.getString("WITH_USERNAME_AND_PASSWORD"));
		
		// Creating a child container with two columns
		// and extra indent.  
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 30;
		Composite childContainer = GUIHelper.
				createComposite(mainContainer, gl, new GridData());
		
		username = GUIHelper.createLabel(childContainer, 
				Messages.getString("USERNAME"));
		
		usernamePassword.setSelection(true);
		
		// Creating a text field with width 150px.
		GridData gdForTextField = new GridData();
		gdForTextField.widthHint = 150;
		
		usernameTxt = GUIHelper.createText(childContainer, gdForTextField);

		password = GUIHelper.createLabel(childContainer, 
				Messages.getString("PASSWORD"));
		
		// Creating password style text field.
		passwordTxt = GUIHelper.createText(childContainer, gdForTextField, 
				SWT.PASSWORD | SWT.NONE | SWT.BORDER | SWT.SINGLE);
		
		usePrivateKey = GUIHelper.createButton(mainContainer, 
				SWT.RADIO, Messages.getString("USE_PRIVATE_KEY"));

		// Creating another child container.
		Composite secondChild = GUIHelper.
				createComposite(mainContainer, gl, new GridData());
		
		GridData gdPrivateTxt = new GridData();
		gdPrivateTxt.widthHint = 250;
		privateKeyTxt = GUIHelper.createText(secondChild, gdPrivateTxt);
	
		
		// User should not be able to write
		// anything to this text field.
		privateKeyTxt.setEditable(false);
		
		//Create a dialog window.
		dialog = new FileDialog(mainContainer.getShell(), SWT.SAVE);
		dialog.setText(Messages.getString("UPLOAD_KEY"));
		
		uploadKey = GUIHelper.createButton(secondChild, 
				SWT.PUSH, Messages.getString("UPLOAD_KEY"));
		
		// Creating another child container.
		Composite thirdChild = GUIHelper.
				createComposite(mainContainer, gl, new GridData());
		
		passphrase = GUIHelper.createLabel(thirdChild, 
				Messages.getString("PASSPHRASE(OPTIONAL)"));
		
		passphraseTxt = GUIHelper.createText(thirdChild, gdForTextField, 
				SWT.PASSWORD | SWT.NONE | SWT.BORDER | SWT.SINGLE);
		
		// Select username and password option
		// as default.
		usernamePassword.setSelection(true);
		
		// And organize fields according to
		// default selection.
		organizeFields();
		
		// Add selection listeners for
		// all text fields and buttons.
		usernamePassword.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		usernameTxt.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});

		passwordTxt.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				canNext();
			}
		});
		
		usePrivateKey.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				organizeFields();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		} );
		
		uploadKey.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// When clicked open a dialog.
				openDialog();
				
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		setPageComplete(false);
		
	}

	// This method organizes button, fields etc.
	// according to selections etc.
	private void organizeFields() {
		
		if (usernamePassword.getSelection()) {
			
			usernameTxt.setEnabled(true);
			
			passwordTxt.setEnabled(true);
			
			uploadKey.setEnabled(false);
			
			privateKeyTxt.setEnabled(false);
			
			passphraseTxt.setEnabled(false);
		}
		else {
			usernameTxt.setEnabled(false);
			
			passwordTxt.setEnabled(false);
			
			uploadKey.setEnabled(true);

			privateKeyTxt.setEnabled(true);
			
			passphraseTxt.setEnabled(true);
		}
		
	}
	
	// This method opens a dialog when triggered,
	// and sets the private key text field.
	private void openDialog() {
		
		selectedFile = dialog.open();
		
		if(selectedFile != null && !"".equals(selectedFile)) {
			privateKeyTxt.setText(selectedFile);
		}
	}

	// This method decides to next button's status
	private void canNext() {
		if (usernamePassword.getSelection()) {
			
			if (!LiderAhenkUtils.isEmpty(usernameTxt.getText()) 
					&& !LiderAhenkUtils.isEmpty(passwordTxt.getText())) {
				
				setPageComplete(true);
			}
			else {
				setPageComplete(false);
			}
		}
		else {
			if (!LiderAhenkUtils.isEmpty(privateKeyTxt.getText())) {
				setPageComplete(true);
			}
			else {
				setPageComplete(false);
			}
		}
	}
	
	// This method sets info which taken from user
	// to appropriate variables in LiderSetupConfig.
	private void setConfigVariables() {
		
		if (usernamePassword.getSelection()) {
			
			config.setUseSSHMaria(false);

			config.setMariaDbSu(usernameTxt.getText());
			
			config.setMariaDbSuPass(passwordTxt.getText());
		}
		else {
			config.setUseSSHMaria(true);

			config.setKeyAbsPathMaria(privateKeyTxt.getText());
			
			config.setPassphraseMaria(passphraseTxt.getText());
		}
	}

	@Override
	public IWizardPage getNextPage() {
		
		// Set variables before going to next page.
		setConfigVariables();
		
		return super.getNextPage();
	}
	
	

}
