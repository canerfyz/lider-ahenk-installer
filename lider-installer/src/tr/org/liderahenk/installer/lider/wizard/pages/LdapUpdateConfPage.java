package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

public class LdapUpdateConfPage extends WizardPage implements ILdapPage {

	private LiderSetupConfig config;

	private Button btnUpdateConfigUser;

	private Text txtCnConfigDn;

	private Text txtCnConfigPwd;

	private Text txtBaseDn;

	private Text txtLdapDbAdminDn;

	private Text txtLdapDbAdminPwd;

	private Text txtLiderIp;

	private Text txtLiderAdminPwd;

	private StyledText st;

	public LdapUpdateConfPage(LiderSetupConfig config) {
		super(LdapUpdateConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.2 " + Messages.getString("LDAP_UPDATE_OR_INSTALL") + " - "
				+ Messages.getString("LDAP_UPDATE_OR_INSTALL_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite compMain = GUIHelper.createComposite(parent, 1);
		setControl(compMain);

		Composite compChild = GUIHelper.createComposite(compMain, 2);

		btnUpdateConfigUser = GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("UPDATE_CONFIG_USER"));
		btnUpdateConfigUser.setSelection(true);
		GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("DO_NOT_UPDATE_CONFIG_USER"));

		GUIHelper.createLabel(compChild, Messages.getString("CN_CONFIG_ADMIN_DN"));
		txtCnConfigDn = GUIHelper.createText(compChild);
		txtCnConfigDn.setMessage("EG_CN_CONFIG_DN");

		GUIHelper.createLabel(compChild, Messages.getString("CN_CONFIG_ADMIN_PWD"));
		txtCnConfigPwd = GUIHelper.createText(compChild);
		txtCnConfigPwd.setMessage("EG_CN_CONFIG_DN_PWD");

		GUIHelper.createLabel(compChild, Messages.getString("BASE_DN"));
		txtBaseDn = GUIHelper.createText(compChild);
		txtBaseDn.setMessage("EG_BASE_DN");

		GUIHelper.createLabel(compChild, Messages.getString("LDAP_DB_ADMIN_DN"));
		txtLdapDbAdminDn = GUIHelper.createText(compChild);
		txtLdapDbAdminDn.setMessage("EG_LDAP_DB_ADMIN_DN");

		GUIHelper.createLabel(compChild, Messages.getString("LDAP_DB_ADMIN_PWD"));
		txtLdapDbAdminPwd = GUIHelper.createText(compChild);
		txtLdapDbAdminPwd.setMessage("EG_LDAP_DB_ADMIN_PWD");

		GUIHelper.createLabel(compChild, Messages.getString("LIDER_SERVER_ADDRESS"));
		txtLiderIp = GUIHelper.createText(compChild);
		txtLiderIp.setMessage("EG_LIDER_SERVER_ADDRESS");

		GUIHelper.createLabel(compChild, Messages.getString("LIDER_ADMIN_PWD"));
		txtLiderAdminPwd = GUIHelper.createText(compChild);
		txtLiderAdminPwd.setMessage("EG_LIDER_ADMIN_PWD");

		// Add a text area for configuration.
		st = new StyledText(compMain, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		st.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add a menu which pops up when right clicked.
		final Menu rightClickMenu = new Menu(st);

		// Add items to new menu
		MenuItem copy = new MenuItem(rightClickMenu, SWT.PUSH);
		copy.setText("Copy");
		copy.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.copy();
			}
		});

		MenuItem paste = new MenuItem(rightClickMenu, SWT.PUSH);
		paste.setText("Paste");
		paste.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.paste();
			}
		});

		MenuItem cut = new MenuItem(rightClickMenu, SWT.PUSH);
		cut.setText("Cut");
		cut.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.cut();
			}
		});

		MenuItem selectAll = new MenuItem(rightClickMenu, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.selectAll();
			}
		});

		// Set menu for text area
		st.setMenu(new Menu(compMain));
		// Listen for right clicks only.
		st.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				rightClickMenu.setVisible(true);
			}
		});

		// Add CTRL+A select all key binding.
		st.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL && (event.keyCode == 'a')) {
					st.selectAll();
				}
			}
		});

		st.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// If config content is entered user can click next.
				if (!"".equals(st.getText()) && st.getText() != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		// Read from file and bring default configuration
		// in the opening of page
		readFile("update_ldap", st);

		setPageComplete(false);

	}

	/**
	 * Reads file from classpath location for current project and sets it to a
	 * text in a GUI.
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName, final StyledText guiText) {

		BufferedReader br = null;
		InputStream inputStream = null;

		try {
			String currentLine;

			inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			String readingText = "";

			while ((currentLine = br.readLine()) != null) {
				// Platform independent line separator.
				readingText += currentLine + System.getProperty("line.separator");
			}

			final String tmpText = readingText;
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					guiText.setText(tmpText);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
