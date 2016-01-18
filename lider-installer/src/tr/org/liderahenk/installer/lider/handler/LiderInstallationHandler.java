package tr.org.liderahenk.installer.lider.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;

public class LiderInstallationHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wizardDialog = LiderAhenkUtils.WizardDialog(Display.getCurrent().getActiveShell(),
				new LiderSetupWizard(), new Point(800, 600));
		wizardDialog.open();
		return null;
	}

}
