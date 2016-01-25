package tr.org.liderahenk.installer.ahenk.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.installer.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;

public class AhenkInstallationHandler {

	@Execute
	public void execute(Shell shell) {
		WizardDialog wizardDialog = LiderAhenkUtils.WizardDialog(Display
				.getCurrent().getActiveShell(), new AhenkSetupWizard(),
				new Point(800, 600));
		wizardDialog.open();
	}

}
