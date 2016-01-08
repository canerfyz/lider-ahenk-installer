package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import tr.org.pardus.mys.liderahenksetup.ahenk.wizard.pages.AhenkInstallationStatusPage;
import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class MariaDBInstallationStatus extends WizardPage{

	private LiderSetupConfig config;
	
	private ProgressBar progressBar = null;
	
	private Label status = null; 
	
	/**
	 *  Status variable for the possible errors on this page
	 */
	IStatus ipStatus;
	
	public MariaDBInstallationStatus(LiderSetupConfig config) {
		super(AhenkInstallationStatusPage.class.getName(), 
				Messages.getString("LIDER_INSTALLATION"), null);
		
		setDescription("2.4 " + Messages.getString("MARIA_DB_INSTALLATION_METHOD") + " - " + Messages.getString("DB_SETUP_METHOD_DESC"));
		this.config = config;
	
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);
		
		
	}
	
}
