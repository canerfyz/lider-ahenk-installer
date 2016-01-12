package tr.org.pardus.mys.liderahenksetup.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LiderInstallationUtil {

	private static final Logger logger = 
			Logger.getLogger(SetupUtils.class.getName());
	
	public static void installMariaDB(LiderSetupConfig config) {
		if (config.isMariaUseSSH()) {
			logger.log(Level.INFO, 
					"Installer will connect with private key to: " + config.getMariaDbIp());
			if (config.isMariaUseRepository()) {
				logger.log(Level.INFO, 
						"Installer will try to install package: mariadb-server using repositories.");
				
			}
			else {
				logger.log(Level.INFO, 
						"Installer will try to install package: mariadb-server using given deb file.");
			}
		}
		else {
			if (config.isMariaUseRepository()) {
				
				logger.log(Level.INFO, 
						"Installer will try to install package: mariadb-server using repositories.");
				
				if (SetupUtils.canConnectViaSsh(config.getMariaDbIp(), 
						config.getMariaDbSu(), config.getMariaDbSuPass())) {
					
					try {
						// TODO port null mı olacak hep?
						if (SetupUtils.packageExists(config.getMariaDbIp(), config.getMariaDbSu(), 
								config.getMariaDbSuPass(), null, null, "mariadb-server", null)) {
							System.out.println("asdasdasdasdsa");
							SetupUtils.installPackage(config.getMariaDbIp(), config.getMariaDbSu(),
									config.getMariaDbSuPass(), null, null, "mariadb-server", null);
							logger.log(Level.INFO, 
									"Installer successfully installed package: mariadb-server");
						}
					} catch (CommandExecutionException | SSHConnectionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else {
					// TODO throw error "cannot connect via SSH using these username and password"
				}
			}
			else {
				logger.log(Level.INFO, 
						"Installer will try to install package: mariadb-server using given deb file.");
			}
		}
		
		// TODO try catch
		System.out.println("MariaDB installed.");
	}
	
	private void modifyDebConf() {
		// TODO
		// TODO
		// ** debconf-utils'in kurulu olduğunu varsayıyoruz.
		// 1) önce var olanları sil. echo PURGE | debconf-communicate packagename
		// echo PURGE | debconf-communicate mariadb-server-10.0
		
		// 2) export DEBIAN_FRONTEND=noninteractive çalıştır.

		// 3) Soruları ve cevapları ekle
//		debconf-set-selections <<< 'mariadb-server-10.0 mysql-server/root_password password 1'
//		debconf-set-selections <<< 'mariadb-server-10.0 mysql-server/root_password_again password 1'
		
		// 4) sudo -S apt-get install -y mariadb-server
	}
	
	private void deleteExistingQuestions() {
		
	}
	
	private void setDebianFrontend() {
		
	}
	
	private void addQuestionToDebConf() {
		
	}
	
	// service mysql restart
	private void executeServiceCommands() {
		
	}
}
