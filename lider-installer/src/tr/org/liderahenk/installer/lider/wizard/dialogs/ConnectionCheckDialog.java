package tr.org.liderahenk.installer.lider.wizard.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
		
		// Wait image
		Composite container = GUIHelper.createComposite(mainContainer, 2);
		image = GUIHelper.createLabel(container);
		image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/wait.png")));
		
		// Message to user
		message = GUIHelper.createLabel(container, "Checking authentication parameters please wait..");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 350;
		message.setLayoutData(gridData);
		
		// Progress bar while process going on
		Composite secondCon = GUIHelper.createComposite(mainContainer, 1);
		progBar = new ProgressBar(secondCon, SWT.INDETERMINATE);
		
		//TODO ok button, invis, closes when clicked.
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
		
		startAuthorizationCheck();
		
		return mainContainer;
	}

	private void startAuthorizationCheck() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (config.getXmppAccessMethod() == AccessMethod.USERNAME_PASSWORD) {
					Runnable runnable = new Runnable() {
						
						@Override
						public void run() {
							canAuthorize = SetupUtils.canConnectViaSsh(config.getXmppIp(), config.getXmppAccessUsername(), config.getXmppAccessPasswd());
						}
					};
					
					Thread thread = new Thread(runnable);
					thread.start();
					
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
				else {
					SetupUtils.canConnectViaSshWithoutPassword(config.getXmppIp(), config.getXmppAccessUsername(), config.getXmppAccessKeyPath());
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();		
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		// TODO ok butonu ekle disabled olsun başlangıçta
		createButton(parent, IDialogConstants.OK_ID, "Ok", true);
	}

	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(500, 200);
	}

	protected void okPressed() {
		close();
	}

	public boolean getCanAuthorize() {
		return canAuthorize;
	}

	public void setCanAuthorize(boolean canAuthorize) {
		this.canAuthorize = canAuthorize;
	}
}
