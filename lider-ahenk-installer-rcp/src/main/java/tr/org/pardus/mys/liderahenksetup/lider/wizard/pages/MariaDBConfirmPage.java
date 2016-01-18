package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class MariaDBConfirmPage extends WizardPage {

	private LiderSetupConfig config;
	
	// Widgets
	private Label mariaDb;
	private Label ip;
	private Label connectionMethod;
	private Label setupMethod;
	private Label question;
	
	public MariaDBConfirmPage(LiderSetupConfig config) {
		super(MariaDBConfirmPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);
		
		setDescription("2.3 " + Messages.getString("MARIA_DB_INSTALLATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);
		
		mariaDb = GUIHelper.createLabel(container, "Maria DB " + 
				Messages.getString("VERSION") + ": " + config.getMariaDbVersion() + ";");
		
		GridData gd = new GridData();
		gd.widthHint = 200;
		gd.minimumWidth = 200;
		ip = GUIHelper.createLabel(container, "localhost");
		ip.setLayoutData(gd);
		
		connectionMethod = GUIHelper.createLabel(container, "- " + 
				Messages.getString(config.isMariaUseSSH() ? 
						"ACCESSING_WITH_PRIVATE_KEY" : "ACCESSING_WITH_USERNAME_AND_PASSWORD"));
		
		setupMethod = GUIHelper.createLabel(container, "- " +
				Messages.getString(config.isMariaUseRepository() ? 
						"USE_REPOSITORY" : "USE_GIVEN_DEB"));
		
		question = GUIHelper.createLabel(container, 
				Messages.getString("MARIA_DB_WILL_BE_INSTALLED") + " " +
				Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}
	
	@Override
	public IWizardPage getNextPage() {
		/**
		 * Set the IP info in the opening of page
		 */
		ip.setText("- IP: " + config.getMariaDbIp());
		
		return super.getNextPage();
	}
}
