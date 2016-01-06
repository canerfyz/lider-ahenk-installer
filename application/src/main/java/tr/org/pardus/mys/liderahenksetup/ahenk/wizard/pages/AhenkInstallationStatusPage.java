package tr.org.pardus.mys.liderahenksetup.ahenk.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;

import tr.org.pardus.mys.liderahenksetup.ahenk.config.AhenkSetupConfig;
import tr.org.pardus.mys.liderahenksetup.i18n.Messages;


/**
 * @author caner  
 * Caner Feyzullahoğlu
 * caner.feyzullahoglu@agem.com.tr
 */

public class AhenkInstallationStatusPage extends WizardPage {

	private AhenkSetupConfig config = null;

	//Widgets
	private Composite mainContainer = null;
	
	private ProgressBar progressBar = null;
	
	private Button startInstallation = null;
	
	private Table table;
	
	private Label label;

	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	public AhenkInstallationStatusPage(AhenkSetupConfig config) {
		super(AhenkInstallationStatusPage.class.getName(),
			Messages.getString("INSTALLATION_OF_AHENK"), null);
		
		setDescription(Messages.getString("INSTALLATION_STATUS"));
		
		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", 0, "", null);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		//create main container
		mainContainer = new Composite(parent, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		setControl(mainContainer);

		label = new Label(mainContainer, SWT.NONE);
		label.setText(Messages.getString("AHENK_WILL_BE_INSTALLED_TO_MACHINES_WITH_IPS_GIVEN_BELOW") + " " + Messages.getString("WOULD_YOU_LIKE_TO_CONTINUE"));		
		
		progressBar = new ProgressBar(mainContainer, SWT.SMOOTH);
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setSelection(0);
		startInstallation = new Button(mainContainer, SWT.PUSH);
		startInstallation.setText("start process");
		startInstallation.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
					try {
						AhenkInstallationUtil.installAhenk(config, progressBar, table, label);
					} catch (Exception exception) {
						
					}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
	}

	@Override
	public boolean canFlipToNextPage() {
		
		return super.canFlipToNextPage();
	}
	
	
	
}
