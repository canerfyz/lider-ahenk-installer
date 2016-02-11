package tr.org.liderahenk.installer.lider.wizard.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class ConnectionCheckDialog extends Dialog{
	
	private LiderSetupConfig config;
	
	private Label image;
	private Label message;
	private ProgressBar progBar;
	private Button okBtn;
	
	private boolean canAuthorize;
	

	public ConnectionCheckDialog(Shell parentShell, LiderSetupConfig config) {
		super(parentShell);

		// Set LiderSetupConfig
		this.config = config;
		
		// Do not show close on the title bar and lock parent window.
		super.setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		Composite container = GUIHelper.createComposite(mainContainer, 2);
		
		// Wait-Success-Fail image
		image = GUIHelper.createLabel(container);
		image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/wait.png")));
		
		// Message to user
		message = GUIHelper.createLabel(container, "Checking authentication parameters please wait..", SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 350;
		message.setLayoutData(gridData);
		
		// Progress bar while process going on
		Composite secondCon = GUIHelper.createComposite(mainContainer, 1);
		
		progBar = new ProgressBar(secondCon, SWT.INDETERMINATE);
		GridData barGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		progBar.setLayoutData(barGridData);
		
		// Ok button to close dialog
		okBtn = GUIHelper.createButton(secondCon, SWT.PUSH, "Ok");
		okBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		okBtn.setVisible(false);
		GridData gdButton = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gdButton.widthHint = 100;
		okBtn.setLayoutData(gdButton);
		
		startAuthorizationCheck();
		
		return mainContainer;
	}

	private void startAuthorizationCheck() {
		if (config.getXmppAccessMethod() == AccessMethod.USERNAME_PASSWORD) {
			
			final Display display = Display.getCurrent(); 
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					canAuthorize = SetupUtils.canConnectViaSsh(config.getXmppIp(), config.getXmppAccessUsername(), config.getXmppAccessPasswd());
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (canAuthorize) {
								image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/success.png")));
								message.setText("Authentication successfull.");
								progBar.setVisible(false);
								okBtn.setVisible(true);
							}
							else {
								image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/fail.png")));
								message.setText("Authentication failed. Please check connection information..");
								progBar.setVisible(false);
								okBtn.setVisible(true);
							}
						}
					});
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
			
		}
		else {
			final Display display = Display.getCurrent(); 
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					canAuthorize = SetupUtils.canConnectViaSshWithoutPassword(config.getXmppIp(), config.getXmppAccessUsername(), config.getXmppAccessKeyPath());
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (canAuthorize) {
								image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/success.png")));
								message.setText("Authentication successfull.");
								progBar.setVisible(false);
								okBtn.setVisible(true);
							}
							else {
								image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/fail.png")));
								message.setText("Authentication failed. Please check connection information..");
								progBar.setVisible(false);
								okBtn.setVisible(true);
							}
						}
					});
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
			
		}
	}

	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(450, 155);
	}

	public boolean getCanAuthorize() {
		return canAuthorize;
	}

	public void setCanAuthorize(boolean canAuthorize) {
		this.canAuthorize = canAuthorize;
	}
}
