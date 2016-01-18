package tr.org.liderahenk.installer.lider.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.StringUtils;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.setup.IOutputStreamProvider;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LiderInstallationUtil {

	private static final String DELETE_EXISTING_QUESTIONS = "su -c 'echo PURGE | debconf-communicate {0}'";

	private static final String DEBIAN_FRONTEND_NONINTERACTIVE = "export DEBIAN_FRONTEND=noninteractive";

	private static final Logger logger = Logger.getLogger(SetupUtils.class.getName());

	public static void installMariaDB(LiderSetupConfig config) {
		if (config.isMariaUseSSH()) {
			logger.log(Level.INFO, "Installer will connect with private key to: " + config.getMariaDbIp());
			if (config.isMariaUseRepository()) {
				logger.log(Level.INFO, "Installer will try to install package: mariadb-server using repositories.");

			} else {
				logger.log(Level.INFO, "Installer will try to install package: mariadb-server using given deb file.");
			}
		} else {
			if (config.isMariaUseRepository()) {
				logger.log(Level.INFO, "Installer will try to install package: mariadb-server using repositories.");

				if (SetupUtils.canConnectViaSsh(config.getMariaDbIp(), config.getMariaDbSu(),
						config.getMariaDbSuPass())) {

					try {
						// TODO port null mı olacak hep?
						if (SetupUtils.packageExists(config.getMariaDbIp(), config.getMariaDbSu(),
								config.getMariaDbSuPass(), null, null, "mariadb-server", null)) {

							// TODO burdan sonrasını ayrı thread'e çek.
							// Uninstall all mariadb packages to clear all
							// questions from debconf.
							SetupUtils.uninstallPackage(config.getMariaDbIp(), config.getMariaDbSu(),
									config.getMariaDbSuPass(), null, null, "mariadb-*");
							logger.log(Level.INFO, "Installer successfully uninstalled mariadb packages.");

							logger.log(Level.INFO, "Installer successfully installed package: mariadb-server");
						} else {
							// TODO throw error "package does not exist."
						}
					} catch (CommandExecutionException | SSHConnectionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					// TODO throw error "cannot connect via SSH using these
					// username and password"
				}
			} else {
				logger.log(Level.INFO, "Installer will try to install package: mariadb-server using given deb file.");
			}
		}

		// TODO try catch
		System.out.println("MariaDB installed.");
	}

	private void modifyDebConf() {
		// TODO
		// TODO
		// ** debconf-utils'in kurulu olduğunu varsayıyoruz.
		// 1) önce var olanları sil. echo PURGE | debconf-communicate
		// packagename
		// echo PURGE | debconf-communicate mariadb-server-10.0

		// 2) export DEBIAN_FRONTEND=noninteractive çalıştır.

		// 3) Soruları ve cevapları ekle
		// sudo debconf-set-selections <<< 'mariadb-server-10.0
		// mysql-server/root_password password 1'
		// sudo debconf-set-selections <<< 'mariadb-server-10.0
		// mysql-server/root_password_again password 1'

		// 4) sudo -S apt-get install -y mariadb-server
	}

	// If user chose "access with private key"
	// then this method should be trigerred with "root" username parameter.
	// Because we do not have username and passsword in LiderSetupConfig.
	private static void deleteExistingQuestions(String ip, String username, final String password, Integer port,
			String privateKey, String packageName) throws SSHConnectionException, CommandExecutionException {
		// Check if the target is local or remote
		if (NetworkUtils.isLocal(ip)) {
			try {
				logger.log(Level.INFO, "Locally deleting existing questions for package: {0}",
						new Object[] { packageName });

				String command = DELETE_EXISTING_QUESTIONS.replace("{0}", packageName);

				Process process = Runtime.getRuntime().exec(command);

				// TODO eğer lokale sshla (privatekeyle) bağlanacaksam case'ini
				// ele almak lazım.
				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdIn));
					writer.write(password);
					// write newline char to mimic 'enter' press.
					writer.write("\n");
					writer.flush();
				}

				int exitValue = process.waitFor();
				// If error occured
				if (exitValue != 0) {
					logger.log(Level.SEVERE, "Process ends with exit value: {0}- err: {1}",
							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
					throw new CommandExecutionException("Failed to execute command: " + command);
				}
				// TODO else bloğu silinecek
				else {
					logger.log(Level.SEVERE, "Process ends with exit value DENEME DENEME: {0}- err: {1}",
							new Object[] { process.exitValue(), StringUtils.convertStream(process.getInputStream()) });
				}

				logger.log(Level.INFO, "Local existing questions for package: {0} have been deleted successfully.",
						new Object[] { packageName });
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			try {
				logger.log(Level.INFO, "Remotely deleting existing questions for package: {0}",
						new Object[] { packageName });

				// If target is remote connect with SSH
				SSHManager manager = new SSHManager(ip, username, password, port, privateKey);

				String outputStream = null;

				manager.connect();

				// TODO burada da SSH'la bağlandığımda username ve password yok
				// elimde
				// buna göre düzenlenmesi lazım
				// outputStream = manager.execCommand(DELETE_EXISTING_QUESTIONS,
				// new Object[] {
				// packageName }, new IOutputStreamProvider() {
				// @Override
				// public byte[] getStreamAsByteArray() {
				// return (password + "\n").getBytes();
				// }
				// });
				outputStream = manager.execCommand("sudo rm /home/ahenk/Masaüstü/caner", new IOutputStreamProvider() {
					@Override
					public byte[] getStreamAsByteArray() {
						return (password + "\n").getBytes();
					}
				});

				logger.log(Level.SEVERE, outputStream);

				manager.disconnect();

				logger.log(Level.INFO, "Remote existing questions for package: {0} have been deleted successfully.",
						new Object[] { packageName });
			} catch (CommandExecutionException | SSHConnectionException e) {
				e.printStackTrace();
			}
		}
	}

	private void setDebianFrontend() {

	}

	private void addQuestionToDebConf() {

	}

	// service mysql restart
	private void executeServiceCommands() {

	}
}
