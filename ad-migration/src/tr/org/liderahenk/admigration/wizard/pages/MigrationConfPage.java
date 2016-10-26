package tr.org.liderahenk.admigration.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.admigration.config.MigrationConfig;
import tr.org.liderahenk.admigration.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MigrationConfPage extends WizardPage implements ControlNextEvent {

	private MigrationConfig config;
	private NextPageEventType nextPageEventType;

	public MigrationConfPage(MigrationConfig config) {
		super(MigrationConfPage.class.getName(), Messages.getString("AHENK_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("AHENK_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		container = new ScrolledComposite(container, SWT.V_SCROLL);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// TODO

		setPageComplete(false);
		updatePageCompleteStatus();
	}

	@Override
	public IWizardPage getNextPage() {

		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
			updatePageCompleteStatus();
		}

		MigrationConfirmPage confPage = (MigrationConfirmPage) super.getNextPage();
		// TODO
		return confPage;
	}

	private void updatePageCompleteStatus() {
		// TODO
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
