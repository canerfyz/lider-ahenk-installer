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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * Creates a dialog which executes an authorization check with provided when it pops up.
 *  
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class DatabaseWarningDialog extends Dialog {
	
	public DatabaseWarningDialog(Shell parentShell) {
		super(parentShell);

		// Do not show close on the title bar and lock parent window.
		super.setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Control createContents(Composite parent) {
		
		// Disable ESC key in this dialog
		getShell().addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});
		
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		
		Composite container = GUIHelper.createComposite(mainContainer, 2);
		
		// Warning image
		Label image = GUIHelper.createLabel(container);
		image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/warning.png")));
		
		// Message to user
		Label message = GUIHelper.createLabel(container, Messages.getString("BIND_ADDRESS_WARNING"), SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 350;
		message.setLayoutData(gridData);
		
		// Progress bar while process going on
		Composite secondCon = GUIHelper.createComposite(mainContainer, 1);
		
		// Ok button to close dialog
		Button okBtn = GUIHelper.createButton(secondCon, SWT.PUSH, "Ok");
		okBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		GridData gdButton = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gdButton.widthHint = 100;
		okBtn.setLayoutData(gdButton);

		return mainContainer;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 155);
	}

}
