package tr.org.liderahenk.installer.ahenk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * @author caner Caner Feyzullahoğlu caner.feyzullahoglu@agem.com.tr
 */
public class AhenkInstallationUtil {

	public static void installAhenk(final AhenkSetupConfig config, final ProgressBar progressBar, final Table table,
			Label label) {

		if (config.isUseUsernameAndPass()) {

			if (config.getPassphrase() != null && !"".equals(config.getPassphrase())) {

			} else {

			}

			if (config.isInstallByAptGet()) {

			} else if (config.isInstallByDebFile()) {

				final File debFile = new File(config.getDebFileAbsPath());

				// TODO this will be divided into threads later
				for (int i = 0; i < config.getIpList().size(); i++) {

					final int j = i;

					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							table.getItem(j).setText(1, Messages.getString("INSTALLATION_STARTED"));

							try {
								SetupUtils.installPackage(config.getIpList().get(j), config.getUsernameCm(),
										config.getPasswordCm(), null, null, debFile, false);

								table.getItem(j).setText(1, Messages.getString("INSTALLATION_SUCCESSFULLY_COMPLETED"));

								progressBar.setSelection(progressBar.getSelection() + 1);

							} catch (SSHConnectionException | CommandExecutionException e) {

								table.getItem(j).setText(1, Messages.getString("AN_ERROR_OCCURED_DURING_INSTALLATION"));

								e.printStackTrace();
							}
						}
					});
				}
			}
		}

		else if (config.isUsePrivateKey()) {

			if (config.getPassphrase() != null && !"".equals(config.getPassphrase())) {

			} else {

			}

			if (config.isInstallByAptGet()) {

			} else if (config.isInstallByDebFile()) {

				final File debFile = new File(config.getDebFileAbsPath());

				// TODO Caner Feyzullahoğlu: no need to read from file. Can be
				// removed later.
				final String privateKey = readFromFile(config.getPrivateKeyAbsPath());

				// TODO this will be divided into threads later
				for (int i = 0; i < config.getIpList().size(); i++) {

					final int j = i;

					table.getItem(j).setText(1, Messages.getString("INSTALLATION_STARTED"));

					Display.getCurrent().asyncExec(new Runnable() {

						@Override
						public void run() {
							try {
								SetupUtils.installPackage(config.getIpList().get(j), "root", null, null,
										config.getPrivateKeyAbsPath(), debFile, false);

								table.getItem(j).setText(1, Messages.getString("INSTALLATION_SUCCESSFULLY_COMPLETED"));

							} catch (SSHConnectionException | CommandExecutionException e) {

								table.getItem(j).setText(1, Messages.getString("AN_ERROR_OCCURED_DURING_INSTALLATION"));

								e.printStackTrace();
							}

							progressBar.setSelection(progressBar.getSelection() + 1);
						}
					});
				}

				label.setText(Messages.getString("AHENK_INSTALLATION_PROCESS_FINISHED"));
			}
		}
	}

	// TODO Caner Feyzullahoğlu: no need to read from file. Can be removed
	// later.
	private static String readFromFile(String privateKeyAbsPath) {

		BufferedReader br = null;

		StringBuffer privateKey = null;

		String temp = null;

		try {

			br = new BufferedReader(new FileReader(privateKeyAbsPath));

			privateKey = new StringBuffer();

			temp = br.readLine();

			while (temp != null) {

				privateKey.append(temp);

				temp = br.readLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return privateKey.toString();
	}
}
