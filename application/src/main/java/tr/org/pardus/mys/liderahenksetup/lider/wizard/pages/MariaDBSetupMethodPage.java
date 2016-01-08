package tr.org.pardus.mys.liderahenksetup.lider.wizard.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import tr.org.pardus.mys.liderahenksetup.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MariaDBSetupMethodPage extends WizardPage {

	private LiderSetupConfig config;

	// Widgets
	private Button btnAptGet;
	private Button btnDebPackage;
	private Text txtFileName;
	private Button btnFileSelect;
	private FileDialog dialog;

	private byte[] debContent;

	public MariaDBSetupMethodPage(LiderSetupConfig config) {
		super(MariaDBSetupMethodPage.class.getName(), Messages
				.getString("LIDER_INSTALLATION"), null);
		setDescription("2.2 " + Messages.getString("MARIA_DB_INSTALLATION_METHOD") + " - " + Messages.getString("DB_SETUP_METHOD_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// Ask user if MariaDB will be installed from a .deb package or via
		// apt-get
		btnAptGet = GUIHelper.createButton(container, SWT.RADIO,
				Messages.getString("DB_SETUP_METHOD_APT_GET"));
		btnAptGet.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnAptGet.getSelection()) {
					config.setInstallViaAptGet(true);
				}
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		btnAptGet.setSelection(true);

		btnDebPackage = GUIHelper.createButton(container, SWT.RADIO,
				Messages.getString("DB_SETUP_METHOD_DEB"));
		btnDebPackage.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnDebPackage.getSelection()) {
					config.setInstallViaAptGet(false);
				}
				// Enable btnFileSelect only if btnDebPackage is selected
				btnFileSelect.setEnabled(btnDebPackage.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group grpDebPackage = GUIHelper.createGroup(container, new GridLayout(2,
				false), new GridData(SWT.FILL, SWT.FILL, false, false));

		txtFileName = GUIHelper.createText(grpDebPackage, new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFileName.setEnabled(false); // do not let user to change it! It will
										// be updated on file selection

		// Upload deb package if necessary
		btnFileSelect = GUIHelper.createButton(grpDebPackage, SWT.NONE,
				Messages.getString("SELECT_FILE"));
		btnFileSelect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.deb" });
				dialog.setFilterNames(new String[] { "DEB" });

				String debFileName = dialog.open();
				if (debFileName != null) {

					txtFileName.setText(debFileName);
					File deb = new File(debFileName);
					debContent = new byte[(int) deb.length()];

					FileInputStream stream;
					try {
						stream = new FileInputStream(deb);
						stream.read(debContent);
						stream.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// Set deb file
					config.setDebFileName(debFileName);
					config.setDebContent(debContent);
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		updatePageCompleteStatus();
	}

	private void updatePageCompleteStatus() {
		setPageComplete(btnAptGet.getSelection()
				|| (btnDebPackage.getSelection() && checkFile()));
	}

	private boolean checkFile() {
		return config.getDebFileName() != null
				&& config.getDebContent() != null;
	}
	
	// This method sets info which taken from user
	// to appropriate variables in LiderSetupConfig.
	private void setConfigVariables() {
		
		if (btnAptGet.getSelection()) {
			config.setUseRepositoryMaria(true);
		}
		else {
			config.setUseRepositoryMaria(false);
			config.setMariaDebAbsPath(txtFileName.getText());
		}
	}

	@Override
	public IWizardPage getNextPage() {
		
		// Set variables before going to next page.
		setConfigVariables();
		
		return super.getNextPage();
	}
	
	
}
