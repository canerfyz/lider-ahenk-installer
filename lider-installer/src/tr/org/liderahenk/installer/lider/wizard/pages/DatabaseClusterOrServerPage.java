package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class DatabaseClusterOrServerPage extends WizardPage implements IDatabasePage {

	private LiderSetupConfig config;

	private Button btnSetupCluster;
	
	public DatabaseClusterOrServerPage(LiderSetupConfig config) {
		super(DatabaseClusterOrServerPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.1 " + Messages.getString("DATABASE_CLUSTER_OR_SERVER"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite compMain = GUIHelper.createComposite(parent, 1);
		setControl(compMain);

		Composite compChild = GUIHelper.createComposite(compMain, 1);

		GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("INSTALL_SERVER"));

		btnSetupCluster = GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("SETUP_CLUSTER"));
		btnSetupCluster.setSelection(true);

	}

	@Override
	public IWizardPage getNextPage() {

		if (btnSetupCluster.getSelection()) {
			return getWizard().getPage(DatabaseAccessPage.class.getName());
		} else {
			config.setDatabaseCluster(true);
			DatabaseClusterConfPage dbClusterConfPage = (DatabaseClusterConfPage) getWizard().getPage(DatabaseClusterConfPage.class.getName());
//			dbClusterConfPage.setInputValues();
			return dbClusterConfPage;
		}
	}

	@Override
	public IWizardPage getPreviousPage() {

		((ControlNextEvent) super.getPreviousPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getPreviousPage();
	}

}
