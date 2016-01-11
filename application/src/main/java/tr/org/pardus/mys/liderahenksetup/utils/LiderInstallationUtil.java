package tr.org.pardus.mys.liderahenksetup.utils;

import tr.org.pardus.mys.liderahenksetup.lider.config.LiderSetupConfig;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LiderInstallationUtil {

	public static void installMariaDB(LiderSetupConfig config) {
		if (config.isMariaUseSSH()) {
			if (config.isMariaUseRepository()) {
				
			}
			else {
				
			}
		}
		else {
			if (config.isMariaUseRepository()) {
				if (SetupUtils.canConnectViaSsh(config.getMariaDbIp(), 
						config.getMariaDbSu(), config.getMariaDbSuPass())) {
					if (SetupUtils.packageExists(config.getMariaDbIp(), config.getMariaDbSu(), config.getMariaDbSuPass(), port, privateKey, packageName, version)) {
						
					}
					
				}
				else {
					// TODO throw error "cannot connect via SSH"
				}
			}
			else {
				
			}
		}
		
		// TODO try catch
		System.out.println("MariaDB installed.");
	}
}
