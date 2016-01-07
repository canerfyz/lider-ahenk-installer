package tr.org.pardus.mys.liderahenksetup.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

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
	public static Label ImageButton(Composite parent, final Image image, 
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
}
