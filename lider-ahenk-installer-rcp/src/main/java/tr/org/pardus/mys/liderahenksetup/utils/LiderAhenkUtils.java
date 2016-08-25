package tr.org.pardus.mys.liderahenksetup.utils;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LiderAhenkUtils {

	/**
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param str
	 * @return Returns true if parameter 
	 * <strong><i>str</i></strong> is 
	 * <strong>null</strong> or 
	 * <strong>"" (empty string)</strong>.
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str) || 
				str.isEmpty() || !(str.trim().length() > 0)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param parent 
	 * @param image
	 * @param mouseOverImage
	 * @param mouseListener
	 * @return Creates a label with a custom image (ImageButton) 
	 * which works like a SWT.PUSH button and changes its display when mouse over. 
	 * <strong><i>mouseOverImage</i></strong> and
	 * <strong>mouseListener</strong> parameters can be passed as <strong>null</strong>
	 * if handling such event is not needed.
	 */
	public static Label imageButton(Composite parent, final Image image, 
			final Image mouseOverImage, MouseListener mouseListener) {
		
		final Label imageButton = new Label(parent, SWT.NONE);
		
		if (imageButton != null) {
			imageButton.setImage(image);
		}
		
		if (mouseOverImage != null) {
			imageButton.addListener(SWT.MouseEnter, new Listener() {
				@Override
				public void handleEvent(Event event) {
					imageButton.setImage(mouseOverImage);
				}
			});
			
			imageButton.addListener(SWT.MouseExit, new Listener() {
				@Override
				public void handleEvent(Event event) {
					imageButton.setImage(image);
				}
			});
		}
		
		if (mouseListener != null) {
			imageButton.addMouseListener(mouseListener);
		}
		
		return null;
	}
	
	/**
	 * Creates a new wizard dialog for the given wizard.
	 * And as an extra option to standard <strong>WizardDialog</strong> constructor,
	 * size of dialog can be given with a <strong>Point</point>.
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param parentShell
	 * 		- the parent shell
	 * @param newWizard
	 * 		- the wizard this dialog is working on
	 * @param size
	 * 		- size of the dialog 
	 * 		(x coordinate : width, y coordinate : height)	 
	 */
	public static WizardDialog WizardDialog(Shell parentShell, 
			IWizard newWizard, Point size) {
		if (size == null) {
			return new WizardDialog(parentShell, newWizard);
		}
		else {
			WizardDialog wd = new WizardDialog(parentShell, newWizard);
			// TODO setMinimumPageSize does not work.
			wd.setMinimumPageSize(size);
			wd.setPageSize(size);
			return wd;
		}
	}
}
