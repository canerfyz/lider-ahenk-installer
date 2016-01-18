package tr.org.liderahenk.installer.ahenk.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.installer.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;

public class AhenkSetupHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog wizardDialog = LiderAhenkUtils.WizardDialog(Display.getCurrent().getActiveShell(),
				new AhenkSetupWizard(), new Point(800, 600));
		wizardDialog.open();
		return null;
	}

}
