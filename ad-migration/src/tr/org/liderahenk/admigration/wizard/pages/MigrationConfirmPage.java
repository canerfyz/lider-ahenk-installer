package tr.org.liderahenk.admigration.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.admigration.config.MigrationConfig;
import tr.org.liderahenk.admigration.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MigrationConfirmPage extends WizardPage {

	private MigrationConfig config;

	private StyledText ipTextArea;
	private Label accessLabel;
	private Label installLabel;

	public MigrationConfirmPage(MigrationConfig config) {
		super(MigrationConfirmPage.class.getName(), Messages.getString("AHENK_INSTALLATION"), null);
		setDescription("4.3 " + Messages.getString("AHENK_INSTALLATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// IP list label
		GUIHelper.createLabel(container, Messages.getString("MACHINES_THAT_AHENK_WILL_BE_INSTALLED"));

		// Add a text area for IP list
		ipTextArea = new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		GridData txtAreaGd = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAreaGd.heightHint = 100;

		ipTextArea.setEditable(false);
		ipTextArea.setLayoutData(txtAreaGd);
		ipTextArea.setText("localhost");

		GridData gd = new GridData();
		gd.widthHint = 500;
		gd.minimumWidth = 500;

		accessLabel = GUIHelper.createLabel(container);
		accessLabel.setLayoutData(gd);

		installLabel = GUIHelper.createLabel(container);
		installLabel.setLayoutData(gd);

		GUIHelper.createLabel(container, Messages.getString("AHENK_WILL_BE_INSTALLED") + " "
				+ Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {
		// Set the IP info in the opening of page
		ipTextArea.setText("TODO");
		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);
		return super.getNextPage();
	}

	public Label getAccessLabel() {
		return accessLabel;
	}

	public Label getInstallLabel() {
		return installLabel;
	}

}
