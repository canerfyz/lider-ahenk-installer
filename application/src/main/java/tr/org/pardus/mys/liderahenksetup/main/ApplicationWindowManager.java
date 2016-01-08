package tr.org.pardus.mys.liderahenksetup.main;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import tr.org.pardus.mys.liderahenksetup.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.lider.wizard.LiderSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;

public class ApplicationWindowManager {

	private static final Logger logger = Logger.getLogger(ApplicationWindowManager.class.getName());

	private Composite comp;
	@PostConstruct
	public Control createContents(final Composite composite) {
		
		comp = new Composite(composite, SWT.NONE);
		GridLayout gl = new GridLayout(2, true);
		gl.marginTop = 300;
		gl.marginLeft = 550;
		comp.setLayout(gl);
		
		Image backgroundImage = new Image(Display.getCurrent(), getInputStream("KurulumEkran.png"));
		comp.setBackgroundImage(backgroundImage);
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		comp.getShell().setSize(1200, 800);
		comp.getShell().setMinimumSize(1200, 800);
		
		Image ahenkImage = new Image(Display.getCurrent(), getInputStream("AhenkButon.png"));
		Image liderImage = new Image(Display.getCurrent(), getInputStream("LiderButon.png"));

		// Lider
		LiderAhenkUtils.ImageButton(comp, liderImage, ahenkImage, new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
//				WizardDialog wizardDialog = new WizardDialog(composite.getShell(), new LiderSetupWizard());
				WizardDialog wizardDialog = LiderAhenkUtils.WizardDialog(composite.getShell(), 
						new LiderSetupWizard(), new Point(800, 600));
				wizardDialog.open();
			}
			@Override
			public void mouseDown(MouseEvent e) {
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		// Ahenk
		LiderAhenkUtils.ImageButton(comp, ahenkImage, liderImage, new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
//				WizardDialog wizardDialog = new WizardDialog(composite.getShell(), new AhenkSetupWizard());
				WizardDialog wizardDialog = LiderAhenkUtils.WizardDialog(composite.getShell(), 
						new AhenkSetupWizard(), new Point(800, 600));
				wizardDialog.open();
			}
			@Override
			public void mouseDown(MouseEvent e) {
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		logger.log(Level.FINE, "Created installer table");

		return comp;
	}

	private InputStream getInputStream(String filename) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(filename);
		return stream;
	}

}