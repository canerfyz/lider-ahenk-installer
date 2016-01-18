package tr.org.pardus.mys.liderahenksetup.main;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import tr.org.pardus.mys.liderahenksetup.constants.InstallerConstants;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;

public class ApplicationWindowManager {
	
	@SuppressWarnings("restriction")
	@Inject 
	// See: http://stackoverflow.com/questions/22589884/ecommandservice-discouraged-access
	// for suppressing warnings.
	ECommandService commandService; 

	private static final Logger logger = Logger.getLogger(ApplicationWindowManager.class.getName());

	private Composite comp;

	@PostConstruct
	public Control createContents(final Composite composite) {

		comp = new Composite(composite, SWT.NONE);
		GridLayout gl = new GridLayout(2, true);
		gl.marginTop = 300;
		gl.marginLeft = 550;
		comp.setLayout(gl);

		Image backgroundImage = new Image(Display.getCurrent(), getInputStream("main-view.png"));
		comp.setBackgroundImage(backgroundImage);
		comp.setBackgroundMode(SWT.INHERIT_DEFAULT);
		comp.getShell().setSize(1200, 800);
		comp.getShell().setMinimumSize(1200, 800);

		// Find main view contributions
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(InstallerConstants.ExtensionPoints.MAIN_VIEW);
		IConfigurationElement[] config = extensionPoint.getConfigurationElements();
		
		if (config != null) {
			for (IConfigurationElement e : config) {
				try {
					final String commandId = e.getAttribute("commandId");
					final String icon = e.getAttribute("icon");

					Image image = new Image(Display.getCurrent(), getInputStream(icon));
					LiderAhenkUtils.imageButton(comp, image, image, new MouseListener() {
						@Override
						public void mouseUp(MouseEvent e) {
							@SuppressWarnings("restriction")
							final Command command = commandService.getCommand(commandId);
							IHandler handler = command.getHandler();
							if (command != null) {
								try {
									command.executeWithChecks(new ExecutionEvent());
								} catch (ExecutionException e1) {
									e1.printStackTrace();
								} catch (NotDefinedException e1) {
									e1.printStackTrace();
								} catch (NotEnabledException e1) {
									e1.printStackTrace();
								} catch (NotHandledException e1) {
									e1.printStackTrace();
								}
							}
						}

						@Override
						public void mouseDown(MouseEvent e) {
						}

						@Override
						public void mouseDoubleClick(MouseEvent e) {
						}
					});

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		logger.log(Level.FINE, "Created installer table");

		return comp;
	}

	// TODO use image registrar
	private InputStream getInputStream(String filename) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(filename);
		return stream;
	}

}