package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderInstallMethodPage extends WizardPage implements ILiderPage {

	private LiderSetupConfig config;

	// private Button btnAptGet;
	private Button btnDebPackage;
	private Button btnWget;
	private Button btnTar;
	private Text txtFileName;
	private Text txtTarFileName;
	private Button btnFileSelect;
	private Button btnTarFileSelect;
	private FileDialog dialog;

	private Text downloadUrlTxt;

	private byte[] debContent;

	private byte[] tarContent;

	public LiderInstallMethodPage(LiderSetupConfig config) {
		super(LiderInstallMethodPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("5.2 " + Messages.getString("LIDER_INSTALLATION_METHOD") + " - "
				+ Messages.getString("DB_SETUP_METHOD_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		Composite subcontainer = GUIHelper.createComposite(mainContainer, 1);

		// Ask user if Karaf will be installed from a .deb package or via
		// apt-get
		// btnAptGet = GUIHelper.createButton(subcontainer, SWT.RADIO,
		// Messages.getString("LIDER_SETUP_METHOD_APT_GET"));
		// btnAptGet.addSelectionListener(new SelectionListener() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// downloadUrlTxt.setEnabled(false);
		// updateConfig();
		// updatePageCompleteStatus();
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// }
		// });
		// btnAptGet.setSelection(true);

		btnDebPackage = GUIHelper.createButton(subcontainer, SWT.RADIO, Messages.getString("LIDER_SETUP_METHOD_DEB"));
		btnDebPackage.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				downloadUrlTxt.setEnabled(false);
				updateConfig();
				// Enable btnFileSelect only if btnDebPackage is selected
				btnFileSelect.setEnabled(btnDebPackage.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group grpDebPackage = GUIHelper.createGroup(subcontainer, new GridLayout(2, false),
				new GridData(SWT.FILL, SWT.FILL, false, false));

		txtFileName = GUIHelper.createText(grpDebPackage, new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFileName.setEnabled(false); // do not let user to change it! It will
										// be updated on file selection

		// Upload deb package if necessary
		btnFileSelect = GUIHelper.createButton(grpDebPackage, SWT.NONE, Messages.getString("SELECT_FILE"));
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
					config.setLiderDebFileName(debFileName);
					config.setLiderDebFileContent(debContent);
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnFileSelect.setEnabled(false);

		btnTar = GUIHelper.createButton(subcontainer, SWT.RADIO, Messages.getString("LIDER_SETUP_METHOD_TAR"));
		btnTar.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				downloadUrlTxt.setEnabled(false);
				updateConfig();
				// Enable btnTarFileSelect only if btnTar is selected
				btnTarFileSelect.setEnabled(btnTar.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnTar.setSelection(true);

		Group grpTar = GUIHelper.createGroup(subcontainer, new GridLayout(2, false),
				new GridData(SWT.FILL, SWT.FILL, false, false));

		txtTarFileName = GUIHelper.createText(grpTar, new GridData(SWT.FILL, SWT.FILL, true, false));
		txtTarFileName.setEnabled(false); // do not let user to change it! It
											// will
											// be updated on file selection

		// Copy ejabberd.deb to /tmp and bring it as default deb in page
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("lider_default_1_0_0.tar.gz");
		File liderTar = SetupUtils.streamToFile(inputStream, "lider_default_1_0_0.tar.gz");
		txtTarFileName.setText(liderTar.getAbsolutePath());

		// Set file to config as array of bytes
		debContent = new byte[(int) liderTar.length()];

		FileInputStream stream = null;
		try {
			stream = new FileInputStream(liderTar);
			stream.read(debContent);
			stream.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		config.setLiderTarFileContent(debContent);
		config.setLiderTarFileName(liderTar.getAbsolutePath());

		// Upload deb package if necessary
		btnTarFileSelect = GUIHelper.createButton(grpTar, SWT.NONE, Messages.getString("SELECT_FILE"));
		btnTarFileSelect.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.tar.gz" });
				dialog.setFilterNames(new String[] { "tar.gz" });

				String tarFileName = dialog.open();
				if (tarFileName != null) {

					txtTarFileName.setText(tarFileName);
					File tar = new File(tarFileName);
					tarContent = new byte[(int) tar.length()];

					FileInputStream stream = null;
					try {
						stream = new FileInputStream(tar);
						stream.read(tarContent);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							stream.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					// Set deb file
					config.setLiderTarFileName(tarFileName);
					config.setLiderTarFileContent(tarContent);
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnTarFileSelect.setEnabled(true);

		// Install by given URL
		btnWget = GUIHelper.createButton(subcontainer, SWT.RADIO, Messages.getString("LIDER_INSTALL_FROM_GIVEN_URL"));
		btnWget.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnWget.getSelection()) {
					downloadUrlTxt.setEnabled(true);
					txtFileName.setEnabled(false);
					btnFileSelect.setEnabled(false);
					updateConfig();
					updatePageCompleteStatus();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Composite downloadUrlContainer = GUIHelper.createComposite(subcontainer, 1);
		GridLayout glDownloadUrl = new GridLayout(1, false);
		downloadUrlContainer.setLayout(glDownloadUrl);

		downloadUrlTxt = GUIHelper.createText(downloadUrlContainer);
		GridData gdDownloadUrlTxt = new GridData();
		gdDownloadUrlTxt.widthHint = 350;
		downloadUrlTxt.setLayoutData(gdDownloadUrlTxt);
		downloadUrlTxt.setEnabled(false);

		downloadUrlTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateConfig();
				updatePageCompleteStatus();
			}
		});

		Composite warningComp = GUIHelper.createComposite(downloadUrlContainer, 1);

		Label label1 = GUIHelper.createLabel(warningComp,
				"Kuruluma uygun tar.gz dosyası varsayılan olarak getirilmiştir.");
		Label label2 = GUIHelper.createLabel(warningComp,
				"Hazır getirilen tar.gz dosyasıyla kuruluma devam edebilirsiniz.");
		label1.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		label2.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));

		updateConfig();
		updatePageCompleteStatus();
	}

	private void updatePageCompleteStatus() {
		// if (btnAptGet.getSelection()) {
		// setPageComplete(true);
		// }
		if (btnDebPackage.getSelection()) {
			setPageComplete(checkDebFile());
		} else if (btnTar.getSelection()) {
			setPageComplete(checkTarFile());
		} else {
			setPageComplete(!"".equals(downloadUrlTxt.getText()));
		}

	}

	private boolean checkDebFile() {
		return config.getLiderDebFileName() != null && config.getLiderDebFileContent() != null;
	}

	private boolean checkTarFile() {
		return config.getLiderTarFileName() != null && config.getLiderTarFileContent() != null;
	}

	private void updateConfig() {
		if (btnDebPackage.getSelection()) {
			config.setLiderInstallMethod(InstallMethod.PROVIDED_DEB);
			config.setLiderPackageName(null);
		}
		// else if (btnAptGet.getSelection()) {
		// config.setLiderInstallMethod(InstallMethod.APT_GET);
		// config.setLiderPackageName(PropertyReader.property("lider.package.name"));
		// }
		else if (btnTar.getSelection()) {
			config.setLiderInstallMethod(InstallMethod.TAR_GZ);
			config.setLiderPackageName(null);
		} else {
			config.setLiderInstallMethod(InstallMethod.WGET);
			config.setLiderDownloadUrl(downloadUrlTxt.getText());
		}
	}

	@Override
	public IWizardPage getNextPage() {

		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getNextPage();
	}

	@Override
	public IWizardPage getPreviousPage() {

		((ControlNextEvent) super.getPreviousPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getPreviousPage();
	}

}
