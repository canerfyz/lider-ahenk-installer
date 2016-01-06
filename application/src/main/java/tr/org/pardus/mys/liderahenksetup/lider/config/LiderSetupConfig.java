package tr.org.pardus.mys.liderahenksetup.lider.config;

import java.util.Map;


/**
 * Contains configuration variables used throughout the whole setup process. 
 *
 */
public class LiderSetupConfig {
	
	/**
	 * This map keeps names of four components
	 * and their corresponding boolean values
	 * (True indicates corresponding component
	 * will be installed.) 
	 */
	private Map<String, Boolean> componentsToBeInstalled;
	
	/**
	 * If all components will be installed to 
	 * same computer, this variable will be
	 * set to true, else false.
	 */
	private boolean sameComputer;
	
	/**
	 * If all components will be installed to 
	 * same computer, the IP which is taken 
	 * from user will be set to this variable.
	 * (or will be set as "localhost"
	 * if components will be installed to local.)
	 */
	private String singleIp;
	
	/**
	 * This variable contains
	 * IP of MariaDB
	 */
	private String mariaDbIp;

	/**
	 * This variable contains
	 * IP of LDAP
	 */
	private String ldapIp;

	/**
	 * This variable contains
	 * IP of Ejabberd
	 */
	private String ejabberdIp;

	/**
	 * This variable contains
	 * IP of Lider
	 */
	private String liderIp;
	
	/**
	 * This variable contains
	 * given username to access
	 * a computer via SSH.
	 */
	private String sudoUsername;

	/**
	 * This variable contains
	 * given password to access
	 * a computer via SSH.
	 */
	private String sudoPassword;
	
	/**
	 * This variable contains given 
	 * SSH key's absolute path. 
	 */
	private String keyAbsolutePath;
	
	/**
	 * This variable contains given 
	 * passphrase of the SSH key.
	 */
	private String passphrase;
	/**
	 * MariaDBSetupLocationPage variables
	 */
	private boolean installDatabaseOnRemote;
	private String databaseIp;
	
	/**
	 * MariaDBSetupMethodPage variables
	 */
	private boolean installViaAptGet;
	private String debFileName;
	private byte[] debContent;

	public boolean isInstallDatabaseOnRemote() {
		return installDatabaseOnRemote;
	}

	public void setInstallDatabaseOnRemote(boolean installDatabaseOnRemote) {
		this.installDatabaseOnRemote = installDatabaseOnRemote;
	}

	public String getDatabaseIp() {
		return databaseIp;
	}

	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}

	public String getDebFileName() {
		return debFileName;
	}

	public void setDebFileName(String debFileName) {
		this.debFileName = debFileName;
	}

	public byte[] getDebContent() {
		return debContent;
	}

	public void setDebContent(byte[] debContent) {
		this.debContent = debContent;
	}

	public boolean isInstallViaAptGet() {
		return installViaAptGet;
	}

	public void setInstallViaAptGet(boolean installViaAptGet) {
		this.installViaAptGet = installViaAptGet;
	}

	public Map<String, Boolean> getComponentsToBeInstalled() {
		return componentsToBeInstalled;
	}

	public void setComponentsToBeInstalled(
			Map<String, Boolean> componentsToBeInstalled) {
		this.componentsToBeInstalled = componentsToBeInstalled;
	}

	public String getSingleIp() {
		return singleIp;
	}

	public void setSingleIp(String singleIp) {
		this.singleIp = singleIp;
	}

	public String getMariaDbIp() {
		return mariaDbIp;
	}

	public void setMariaDbIp(String mariaDbIp) {
		this.mariaDbIp = mariaDbIp;
	}

	public String getLdapIp() {
		return ldapIp;
	}

	public void setLdapIp(String ldapIp) {
		this.ldapIp = ldapIp;
	}

	public String getEjabberdIp() {
		return ejabberdIp;
	}

	public void setEjabberdIp(String ejabberdIp) {
		this.ejabberdIp = ejabberdIp;
	}

	public String getLiderIp() {
		return liderIp;
	}

	public void setLiderIp(String liderIp) {
		this.liderIp = liderIp;
	}

	public boolean isSameComputer() {
		return sameComputer;
	}

	public void setSameComputer(boolean sameComputer) {
		this.sameComputer = sameComputer;
	}

	public String getSudoUsername() {
		return sudoUsername;
	}

	public void setSudoUsername(String sudoUsername) {
		this.sudoUsername = sudoUsername;
	}

	public String getSudoPassword() {
		return sudoPassword;
	}

	public void setSudoPassword(String sudoPassword) {
		this.sudoPassword = sudoPassword;
	}

	public String getKeyAbsolutePath() {
		return keyAbsolutePath;
	}

	public void setKeyAbsolutePath(String keyAbsolutePath) {
		this.keyAbsolutePath = keyAbsolutePath;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
	
}
