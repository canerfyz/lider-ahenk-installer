package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class XmppConfPage extends WizardPage implements IXmppPage {

	private LiderSetupConfig config;

	private Text hostnameTxt;
	private Text adminPwdTxt;
	private Text liderUserTxt;
	private Text liderPwdTxt;

	private StyledText st;

	public XmppConfPage(LiderSetupConfig config) {
		super(XmppConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("XMPP_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);

		setControl(container);

		GridData gdForTxt = new GridData();
		gdForTxt.widthHint = 125;

		// --------- Hostname and Ejabberd Admin Password Inputs ----//
		GUIHelper.createLabel(container, Messages.getString("XMPP_SERVER_HOSTNAME_AND_EJABBERD_ADMIN_PWD"));

		Composite inputsContainer = GUIHelper.createComposite(container, new GridLayout(2, false),
				new GridData(SWT.NO, SWT.NO, true, false));

		GUIHelper.createLabel(inputsContainer, Messages.getString("HOSTNAME"));

		hostnameTxt = GUIHelper.createText(inputsContainer);
		hostnameTxt.setLayoutData(gdForTxt);
		hostnameTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(inputsContainer, Messages.getString("ADMIN_PASSWORD"));

		adminPwdTxt = GUIHelper.createText(inputsContainer, new GridData(), SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		adminPwdTxt.setLayoutData(gdForTxt);
		adminPwdTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});
		// ----------------------------------------------------------//

		// Info message
		Composite infoContainer = GUIHelper.createComposite(container, new GridLayout(1, false),
				new GridData(SWT.FILL, SWT.NO, true, false));
		GUIHelper.createLabel(infoContainer, Messages.getString("LIDER_SERVER_USER_INFO"));

		// ------------- Lider Server Username and Password -----------//
		Composite liderContainer = GUIHelper.createComposite(container, new GridLayout(2, false),
				new GridData(SWT.NO, SWT.NO, true, false));

		GUIHelper.createLabel(liderContainer, Messages.getString("USERNAME"));

		liderUserTxt = GUIHelper.createText(liderContainer);
		liderUserTxt.setLayoutData(gdForTxt);
		liderUserTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});
		
		GUIHelper.createLabel(liderContainer, Messages.getString("PASSWORD"));

		liderPwdTxt = GUIHelper.createText(liderContainer, new GridData(), SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		liderPwdTxt.setLayoutData(gdForTxt);
		liderPwdTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});
		// ------------------------------------------------------------//

		// ----------- Text Editor --------------------//
		GUIHelper.createLabel(container, Messages.getString("XMPP_ENTER_CONF_CONTENT"));

		Composite textAreaContainer = GUIHelper.createComposite(container, 1);

		// Add a text area for configuration.
		st = new StyledText(textAreaContainer, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
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
		// -----------------------------------//

		updatePageCompleteStatus();
		
		// Read from file and bring default configuration
		// in the opening of page
		readFile("ejabberd.yml", st);
	}

	private void updatePageCompleteStatus() {
		if (!"".equals(hostnameTxt.getText()) && !"".equals(adminPwdTxt.getText()) 
				&& !"".equals(liderUserTxt.getText()) && !"".equals(liderPwdTxt.getText())) {
			setPageComplete(true);
		}
		else {
			setPageComplete(false);
		}
	}

	@Override
	public IWizardPage getNextPage() {

		// Set config variables before going to next page
		config.setXmppConfContent(st.getText());
		config.setXmppHostname(hostnameTxt.getText());
		config.setXmppAdminPwd(adminPwdTxt.getText());
		config.setXmppLiderUsername(liderUserTxt.getText());
		config.setXmppLiderPassword(liderPwdTxt.getText());

		// Write configuration to file
		config.setXmppAbsPathConfFile(writeToFile(st.getText(), "ejabberd.yml"));

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
