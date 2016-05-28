package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class LiderOrganizationPage extends WizardPage {

	private LiderSetupConfig config;

	private Text txtOrgCn;

	public LiderOrganizationPage(LiderSetupConfig config) {
		super(LiderOrganizationPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("1.1 " + Messages.getString("CHOOSING_ORGANIZATION_CN"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		GUIHelper.createLabel(mainContainer, Messages.getString("ORGANIZATION_CN_DESCRIPTION"));

		Composite cmpOrgCn = GUIHelper.createComposite(mainContainer, new GridLayout(2, false),
				new GridData(SWT.LEFT, SWT.CENTER, false, false));

		GUIHelper.createLabel(cmpOrgCn, Messages.getString("ORGANIZATION_CN"));

		GridData gd = new GridData();
		gd.widthHint = 250;
		txtOrgCn = GUIHelper.createText(cmpOrgCn);
		txtOrgCn.setLayoutData(gd);
	}

	@Override
	public IWizardPage getNextPage() {
		
		prepareDefaults(txtOrgCn.getText());
		
		// Update next page according to selections of this page
		IWizardPage nextPage = super.getNextPage();
		if (nextPage instanceof LiderLocationOfComponentsPage) {
			((LiderLocationOfComponentsPage) nextPage).updatePage();
		}
		
		return super.getNextPage();
	}
	
	private void prepareDefaults(String text) {
		// TODO
		// TODO
		// TODO
	}

}
