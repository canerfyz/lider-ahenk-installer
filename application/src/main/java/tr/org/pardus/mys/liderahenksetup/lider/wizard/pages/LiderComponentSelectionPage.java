package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

// This "unused" suppress warning is just for 
// variables: sentence, question and emptySpace.
// (Because in createControl method, I just create their instances.)
@SuppressWarnings("unused")  
public class LiderComponentSelectionPage extends WizardPage {

	private LiderSetupConfig config;

	// Widgets
	private Label sentence;
	private Label question;
	
	private Button checkAll;
	private Button checkMariaDb;
	private Button checkLdap;
	private Button checkEjabberd;
	private Button checkLider;
	
	private Label emptySpace;
	
	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	public LiderComponentSelectionPage(LiderSetupConfig config) {
		super(LiderComponentSelectionPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);

		setDescription("1.1 " + 
				Messages.getString("CHOOSING_COMPONENTS_THAT_WILL_BE_INSTALLED"));
		
		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}

	@Override
	public void createControl(Composite parent) {
		
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		setControl(mainContainer);
		
		// Information about required components
		sentence = GUIHelper.createLabel(mainContainer, 
				Messages.getString("BELOW_COMPONENTS_MUST_BE_INSTALLED_FOR_LIDER"));
		
		// Ask user for the components that would be installed
		question = GUIHelper.createLabel(mainContainer, 
				Messages.getString("WHICH_COMPONENTS_WILL_BE_INSTALLED"));
		
		// Just for empty line
		emptySpace = GUIHelper.createLabel(mainContainer, "");
		
		// List all components
		// Note: Numbers that appended to messages are versions of components.
		checkAll = GUIHelper.createButton(mainContainer, SWT.CHECK, 
				Messages.getString("ALL(RECOMMENDED)"));

		checkMariaDb = GUIHelper.createButton(mainContainer, SWT.CHECK, 
				Messages.getString("MARIA_DB_VERSION") + " 1.0");
		
		checkLdap = GUIHelper.createButton(mainContainer, SWT.CHECK, 
				Messages.getString("OPENLDAP_VERSION") + " 1.0");

		checkEjabberd = GUIHelper.createButton(mainContainer, SWT.CHECK, 
				Messages.getString("EJABBERD_VERSION") + " 1.0");

		checkLider = GUIHelper.createButton(mainContainer, SWT.CHECK, 
				Messages.getString("LIDER_VERSION") + " 1.0");
		
		// Adding "select all" functionality to checkAll button.
		// And updating page complete status.
		checkAll.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectAll();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// Select/Deselect checkAll button
		// according to other components' checks.
		// And updating page complete status.
		checkMariaDb.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		checkLdap.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		checkEjabberd.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		checkLider.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				canNext();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// All components should come as selected
		// in the opening of page.
		checkAll.setSelection(true);

		checkMariaDb.setSelection(true);
		
		checkLdap.setSelection(true);
		
		checkEjabberd.setSelection(true);
		
		checkLider.setSelection(true);
		
	}
	
	// Selecting all components
	private void selectAll() {
		if (checkAll.getSelection()) {
			
			checkMariaDb.setSelection(true);
			
			checkLdap.setSelection(true);
			
			checkEjabberd.setSelection(true);
			
			checkLider.setSelection(true);
		}
	}
	
	private void setSelectionOfCheckAll() {
		
		// Set selection of checkAll button to true
		// if all components are selected else set to false.
		if (checkMariaDb.getSelection() && checkLdap.getSelection() 
				&& checkEjabberd.getSelection() && checkLider.getSelection()) {
			
			checkAll.setSelection(true); 
		}
		else {
			checkAll.setSelection(false);
		}
	}
	
	// This method decides to next button's status
	private void canNext() {
		
		// If any of the components is selected then enable next button.
		if (checkMariaDb.getSelection() || checkLdap.getSelection() 
				|| checkEjabberd.getSelection() || checkLider.getSelection()) {

			setPageComplete(true);
		}
		else {
			setPageComplete(false);
		}
	}

	// Setting component selections to the map in LiderSetupConfig
	private void setConfigVariables() {
		
		Map<String, Boolean> componentSelections = new HashMap<String, Boolean>();
		
		if (checkMariaDb.getSelection()) {
			componentSelections.put("MariaDB", true);
		}
		else {
			componentSelections.put("MariaDB", false);
		}
	
		if (checkLdap.getSelection()) {
			componentSelections.put("LDAP", true);
		}
		else {
			componentSelections.put("LDAP", false);
		}

		if (checkEjabberd.getSelection()) {
			componentSelections.put("Ejabberd", true);
		}
		else {
			componentSelections.put("Ejabberd", false);
		}
		
		if (checkLider.getSelection()) {
			componentSelections.put("Lider", true);
		}
		else {
			componentSelections.put("Lider", false);
		}
		
		config.setComponentsToBeInstalled(componentSelections);
		
	}
	
	@Override
	public IWizardPage getNextPage() {
		
		// Set variables before going to next page.
		setConfigVariables();
		
		// Edit next page's fields according to selections
		disableNotSelectedComponents(
				(LiderLocationOfComponentsPage) getWizard().
				getPage(LiderLocationOfComponentsPage.class.getName()));
		
		return super.getNextPage();
	}
	
	public void disableNotSelectedComponents(LiderLocationOfComponentsPage page) {
		
		// If a component is not selected
		// then change its style to disabled.
		if (!(config.getComponentsToBeInstalled().get("MariaDB"))) {
			setDisableStyle(page.getMariaDb(), page.getMariaDbIp());
		}
		// Else set to enabled.
		else {
			setEnableStyle(page.getMariaDb(), page.getMariaDbIp(), 
					page.getInstallDistributed());
		}
		
		if (!(config.getComponentsToBeInstalled().get("LDAP"))) {
			setDisableStyle(page.getLdap(), page.getLdapIp());
		}
		else {
			setEnableStyle(page.getLdap(), page.getLdapIp(), 
					page.getInstallDistributed());
		}
		
		if (!(config.getComponentsToBeInstalled().get("Ejabberd"))) {
			setDisableStyle(page.getEjabberd(), page.getEjabberdIp());
		}
		else {
			setEnableStyle(page.getEjabberd(), page.getEjabberdIp(), 
					page.getInstallDistributed());
		}
		
		if (!(config.getComponentsToBeInstalled().get("Lider"))) {
			setDisableStyle(page.getLider(), page.getLiderIp());
		}
		else {
			setEnableStyle(page.getLider(), page.getLiderIp(), 
					page.getInstallDistributed());
		}
		
	}
	
	// Set a component's style to disabled
	private void setDisableStyle(Label label, Text text) {
		
		label.setForeground(new Color(getShell().getDisplay(), 
				new RGB(158, 158, 158)));
		
		label.setFont(new Font(getShell().getDisplay(), 
				label.getFont().toString(), 9, SWT.ITALIC));
		
		text.setText(Messages.getString("NOT_SELECTED_FOR_INSTALLATION"));
		
		text.setEnabled(false);
		
		text.setFont(new Font(getShell().getDisplay(), 
				label.getFont().toString(), 9, SWT.ITALIC));
	}

	// Set a component's style to enabled
	private void setEnableStyle(Label label, Text text, Button button) {

		label.setForeground(new Color(getShell().getDisplay(), 
				new RGB(33, 33, 33)));
		
		label.setFont(new Font(getShell().getDisplay(), 
				label.getFont().toString(), 9, SWT.NORMAL));
		
		text.setText("");
		
		if (button.getSelection()) {

			text.setEnabled(true);
		}
		
		text.setFont(new Font(getShell().getDisplay(), 
				label.getFont().toString(), 9, SWT.NORMAL));
	}

}
