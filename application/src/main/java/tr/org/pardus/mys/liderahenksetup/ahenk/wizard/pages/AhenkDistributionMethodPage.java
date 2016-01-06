package tr.org.pardus.mys.liderahenksetup.ahenk.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tr.org.pardus.mys.liderahenksetup.ahenk.config.AhenkSetupConfig;
import tr.org.pardus.mys.liderahenksetup.i18n.Messages;

/**
 * @author caner  
 * Caner Feyzullahoğlu
 * caner.feyzullahoglu@agem.com.tr
 */

public class AhenkDistributionMethodPage extends WizardPage {

	private AhenkSetupConfig config = null;

	//Widgets
	private Composite mainContainer = null;
	
	private Button useScpBtn = null;

	private Button useTorrentBtn = null;

	// Status variable for the possible errors on this page
	IStatus ipStatus;
	
	public AhenkDistributionMethodPage(AhenkSetupConfig config) {
		super(AhenkDistributionMethodPage.class.getName(),
			Messages.getString("INSTALLATION_OF_AHENK"), null);
		
		setDescription(Messages.getString("HOW_TO_DISTRIBUTE_PACKAGES"));
		
		this.config = config;
		
		ipStatus = new Status(IStatus.OK, "not_used", 0, "", null);
	}
	
	@Override
	public void createControl(Composite parent) {
		
		//create main container
		mainContainer = new Composite(parent, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		setControl(mainContainer);

		//Distribute by scp
		useScpBtn = new Button(mainContainer, SWT.RADIO);
		useScpBtn.setText(Messages.getString("DISTRUBUTE_WITH_SECURE_COPY(SCP)_METHOD"));
		useScpBtn.setSelection(true);
		
		//Distribute by torrent
		useTorrentBtn = new Button(mainContainer, SWT.RADIO);
		useTorrentBtn.setText(Messages.getString("DISTRUBUTE_VIA_TORRENT"));
	}

	@Override
	public IWizardPage getNextPage() {
		
		if (useScpBtn.getSelection()) {
			config.setUseScp(true);
			config.setUseTorrent(false);
		}
		else {
			config.setUseScp(false);
			config.setUseTorrent(true);
		}
		
		return super.getNextPage();
	}
	
	
}
