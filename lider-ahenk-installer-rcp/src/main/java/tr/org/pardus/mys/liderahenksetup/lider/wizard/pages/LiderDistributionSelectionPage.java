package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */

public class LiderDistributionSelectionPage extends WizardPage {

	private LiderSetupConfig config;
	
	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	Button fromRepoBtn;
	
	Button fromFtpBtn;
	Text ftpTxt;
	
	Button fromDebBtn;
	Text debTxt;
	Button debUploadBtn;
	
	public LiderDistributionSelectionPage(LiderSetupConfig config) {
		super(LiderDistributionSelectionPage.class.getName(), 
				Messages.getString(""), null);
		this.config = config;
		
		setDescription("2.2 " + 
				Messages.getString(""));
		
		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", "");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		setControl(mainContainer);
		
		// From repo
		fromRepoBtn = GUIHelper.createButton(mainContainer, SWT.PUSH, 
				Messages.getString(""));
		
		// From FTP
		fromRepoBtn = GUIHelper.createButton(mainContainer, SWT.PUSH, 
				Messages.getString(""));
		
		// From .deb
		fromRepoBtn = GUIHelper.createButton(mainContainer, SWT.PUSH, 
				Messages.getString(""));
		
		
		
		
	}

}
