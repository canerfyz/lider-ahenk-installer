package tr.org.liderahenk.installer.ahenk.wizard.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;

/**
 * @author caner Caner Feyzullahoğlu caner.feyzullahoglu@agem.com.tr
 */
public class AhenkInstallationMethodPage extends WizardPage {

	private AhenkSetupConfig config = null;

	// Widgets
	private Composite mainContainer = null;
	private Composite fileDialogContainer = null;

	private Button useAptGetBtn = null;

	private Button useDebBtn = null;

	private FileDialog fileDialog = null;

	private Text fileDialogText = null;

	private Button fileDialogBtn = null;

	private String fileDialogResult = null;

	// Status variable for the possible errors on this page
	IStatus ipStatus;

	public AhenkInstallationMethodPage(AhenkSetupConfig config) {
		super(AhenkInstallationMethodPage.class.getName(), Messages.getString("INSTALLATION_OF_AHENK"), null);

		setDescription(Messages.getString("BY_WHICH_WAY_WOULD_YOU_LIKE_TO_INSTALL_AHENK"));

		this.config = config;

		ipStatus = new Status(IStatus.OK, "not_used", 0, "", null);
	}

	@Override
	public void createControl(Composite parent) {

		// create main container
		mainContainer = new Composite(parent, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		setControl(mainContainer);

		// Install by apt-get
		useAptGetBtn = new Button(mainContainer, SWT.RADIO);

		useAptGetBtn.setText(Messages.getString("INSTALL_USING_CATALOG(BY_APT-GET)"));
		useAptGetBtn.setSelection(true);

		useAptGetBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (useAptGetBtn.getSelection()) {
					fileDialogText.setEnabled(false);
					fileDialogBtn.setEnabled(false);
					updatePageCompleteStatus();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Install by given .deb package
		useDebBtn = new Button(mainContainer, SWT.RADIO);

		useDebBtn.setText(Messages.getString("INSTALL_FROM_GIVEN_DEB_PACKAGE"));

		useDebBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (useDebBtn.getSelection()) {
					fileDialogText.setEnabled(true);
					fileDialogBtn.setEnabled(true);
					updatePageCompleteStatus();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		fileDialogContainer = new Composite(mainContainer, SWT.NONE);
		GridLayout glFileDialog = new GridLayout(2, false);
		glFileDialog.marginLeft = 15;
		// Adjust button near to text field
		glFileDialog.horizontalSpacing = -3;
		fileDialogContainer.setLayout(glFileDialog);

		// File dialog window
		fileDialog = new FileDialog(mainContainer.getShell(), SWT.SAVE);
		fileDialog.setText(Messages.getString("UPLOAD_AHENK"));
		fileDialog.setFilterExtensions(new String[] { "*.deb" });

		// Upload key text field
		fileDialogText = new Text(fileDialogContainer, SWT.BORDER);
		fileDialogText.setEnabled(false);
		fileDialogText.setEditable(false);
		GridData gdFileDialogTxt = new GridData();
		gdFileDialogTxt.widthHint = 247;
		fileDialogText.setLayoutData(gdFileDialogTxt);
		fileDialogText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		// Upload Ahenk .deb push button
		fileDialogBtn = new Button(fileDialogContainer, SWT.PUSH);
		fileDialogBtn.setText(Messages.getString("UPLOAD_AHENK"));

		GridData gdFileDialogBtn = new GridData();
		gdFileDialogBtn.heightHint = 25;
		gdFileDialogBtn.widthHint = 125;
		fileDialogBtn.setLayoutData(gdFileDialogBtn);
		fileDialogBtn.setEnabled(false);

		fileDialogBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileDialogResult = fileDialog.open();
				if (fileDialogResult != null && !"".equals(fileDialogResult)) {
					fileDialogText.setText(fileDialogResult);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void updatePageCompleteStatus() {

		// If apt-get is selected can go to next page
		if (useAptGetBtn.getSelection()) {
			setPageComplete(true);
		}
		// else path of .deb file must be given
		else if (fileDialogText.getText() != null && !"".equals(fileDialogText.getText())) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	@Override
	public IWizardPage getNextPage() {

		if (useAptGetBtn.getSelection()) {
			config.setInstallByAptGet(true);
			config.setInstallByDebFile(false);
		} else {
			config.setInstallByAptGet(false);
			config.setInstallByDebFile(true);
			config.setDebFileAbsPath(fileDialogText.getText());
		}

		return super.getNextPage();
	}

}