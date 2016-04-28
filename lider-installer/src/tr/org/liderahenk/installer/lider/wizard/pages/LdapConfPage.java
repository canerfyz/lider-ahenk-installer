package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LdapConfPage extends WizardPage implements ILdapPage {

	private LiderSetupConfig config;

	private StyledText st;
	
	private Text configAdminDn;
	private Text configAdminDnPwd;
	private Text cname;
	private Text baseDn;
	private Text baseCn;
	private Text organization;
	private Text adminCn;
	private Text adminCnPwd;
	private Text liderIp;
	private Text liderConsoleUser;
	private Text liderConsoleUserPwd;
	
	public LdapConfPage(LiderSetupConfig config) {
		super(LdapConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("LDAP_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);
		
		Label label = GUIHelper.createLabel(mainContainer,
				"Hazır gelen değerler daha önceki kurulumlara veya\nvarsayılan değerlere göre getirilmiştir.\nLütfen kontrol ediniz.");
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
		
		Composite propertyContainer = GUIHelper.createComposite(mainContainer, 1);
		
		Composite lineCont = GUIHelper.createComposite(propertyContainer, 2);
		
		GUIHelper.createLabel(lineCont, "Organization");
		organization = GUIHelper.createText(lineCont);
		organization.setText("Pardus MYS");

		GUIHelper.createLabel(lineCont, "Organization CN");
		cname = GUIHelper.createText(lineCont);
		cname.setText("mys.pardus.org");

		GUIHelper.createLabel(lineCont, "Base DN");
		baseDn = GUIHelper.createText(lineCont);
		baseDn.setText("dc=mys,dc=pardus,dc=org");

		GUIHelper.createLabel(lineCont, "Base CN");
		baseCn = GUIHelper.createText(lineCont);
		baseCn.setText("mys");

		GUIHelper.createLabel(lineCont, "Config Admin DN");
		configAdminDn = GUIHelper.createText(lineCont);
		configAdminDn.setText("cn=admin,cn=config");

		GUIHelper.createLabel(lineCont, "Config Admin DN Password");
		configAdminDnPwd = GUIHelper.createText(lineCont);
		configAdminDnPwd.setText("secret");

		GUIHelper.createLabel(lineCont, "Admin CN");
		adminCn = GUIHelper.createText(lineCont);
		adminCn.setText("admin");
		
		GUIHelper.createLabel(lineCont, "Admin CN Password");
		adminCnPwd = GUIHelper.createText(lineCont);
		adminCnPwd.setText("secret");
		
		GUIHelper.createLabel(lineCont, "Lider IP address");
		liderIp = GUIHelper.createText(lineCont);
		
		GUIHelper.createLabel(lineCont, "Lider Console User Password");
		liderConsoleUserPwd = GUIHelper.createText(lineCont);
		liderConsoleUserPwd.setText("p@ssw0rd");
		
		GUIHelper.createLabel(lineCont, "Lider Console User");
		liderConsoleUser = GUIHelper.createText(lineCont);
		liderConsoleUser.setText("lider_console");
		
		Composite container = GUIHelper.createComposite(mainContainer, 1);
		
		GUIHelper.createLabel(container, Messages.getString("LDAP_ENTER_CONF_CONTENT"));

		// Add a text area for configuration.
		st = new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
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
		st.setMenu(new Menu(container));
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
		readFile("ldapconfig", st);

		setPageComplete(false);
	}

	@Override
	public IWizardPage getNextPage() {

		// Set default or predefined values to inputs
		liderIp.setText(config.isInstallLider() ? config.getLiderIp() : "lider.mys.pardus.org.tr");
		
		// Set config variables before going to next page
		String text = st.getText();
		Map<String, String> map = new HashMap<>();
		map.put("#CNAME", cname.getText());
		map.put("#BASEDN", baseDn.getText());
		map.put("#BASECN", baseCn.getText());
		map.put("#ORGANIZATION", organization.getText());
		map.put("#ADMINCN", adminCn.getText());
		map.put("#ADMINPASSWD", adminCnPwd.getText());
		map.put("#CNCONFIGADMINDN", configAdminDn.getText());
		map.put("#CNCONFIGADMINPASSWD", configAdminDnPwd.getText());
		map.put("#LIDERIP", liderIp.getText());
		map.put("#LIDERCONSOLEUSER", liderConsoleUser.getText());
		map.put("#LIDERCONSOLEPWD", liderConsoleUserPwd.getText());
		
		text = SetupUtils.replace(map, text);
		config.setLdapConfContent(text);
		config.setLdapAbsPathConfFile(writeToFile(text, "ldapconfig"));
		
		config.setLdapBaseDn(this.baseDn.getText());
		config.setLdapAdminCn(this.adminCn.getText());
		config.setLdapAdminCnPwd(this.adminCnPwd.getText());

		return super.getNextPage();
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

	/**
	 * Creates file under temporary file directory and writes configuration to
	 * it. Returns absolute path of created temp file.
	 * 
	 * @param content
	 * @param namePrefix
	 * @param nameSuffix
	 * @return absolute path of created temp file
	 */
	private String writeToFile(String content, String fileName) {

		String absPath = null;

		try {
			File temp = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);

			FileWriter fileWriter = new FileWriter(temp.getAbsoluteFile());

			BufferedWriter buffWriter = new BufferedWriter(fileWriter);

			buffWriter.write(content);
			buffWriter.close();

			absPath = temp.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return absPath;
	}
	
}
