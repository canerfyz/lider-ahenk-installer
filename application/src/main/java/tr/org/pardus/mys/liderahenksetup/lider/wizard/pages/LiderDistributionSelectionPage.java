package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class LiderDistributionSelectionPage extends WizardPage {

	private LiderSetupConfig config;
	
	public LiderDistributionSelectionPage(LiderSetupConfig config) {
		super(LiderDistributionSelectionPage.class.getName(), 
				Messages.getString(""), null);
		this.config = config;
		
		
	}
	
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		setControl(mainContainer);
	}

}
