package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class KarafConfirmPage extends WizardPage {

	private LiderSetupConfig config;
	
	// Widgets
	private Label karaf;
	private Label ip;
	private Label connectionMethod;
	private Label setupMethod;
	private Label question;
	
	public KarafConfirmPage(LiderSetupConfig config) {
		super(KarafConfirmPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);
		
		setDescription("5.3 " + Messages.getString("KARAF_INSTALLATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);
		
		karaf = GUIHelper.createLabel(container, "Apache Karaf " + 
				Messages.getString("VERSION") + ": " + config.getLiderVersion() + ";");
		
		GridData gd = new GridData();
		gd.widthHint = 200;
		gd.minimumWidth = 200;
		ip = GUIHelper.createLabel(container, "localhost");
		ip.setLayoutData(gd);
		
		connectionMethod = GUIHelper.createLabel(container, "- " + 
				Messages.getString(config.isKarafUseSSH() ? 
						"ACCESSING_WITH_PRIVATE_KEY" : "ACCESSING_WITH_USERNAME_AND_PASSWORD"));
		
		setupMethod = GUIHelper.createLabel(container, "- " +
				Messages.getString(config.isKarafUseRepository() ?
						"USE_REPOSITORY" : "USE_GIVEN_DEB"));
		
		question = GUIHelper.createLabel(container, 
				Messages.getString("KARAF_WILL_BE_INSTALLED") + " " +
				Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}
	
	@Override
	public IWizardPage getNextPage() {
		/**
		 * Set the IP info in the opening of page
		 */
		ip.setText("- IP: " + config.getLiderIp());
		
		return super.getNextPage();
	}
}
