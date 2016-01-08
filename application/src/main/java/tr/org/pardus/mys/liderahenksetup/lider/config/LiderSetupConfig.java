package tr.org.pardus.mys.liderahenksetup.lider.config;

import java.util.Map;


/**
 * Contains configuration variables used throughout the whole setup process. 
 *
 */

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
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
	 * This variable is true 
	 * if SSH is chosen as 
	 * connection method
	 * (for MariaDB).
	 */
	private boolean useSSHMaria;
	
	/**
	 * This variable contains
	 * given username to access
	 * a computer via SSH
	 * (for MariaDB).
	 */
	private String mariaDbSu;

	/**
	 * This variable contains
	 * given password to access
	 * a computer via SSH 
	 * (for MariaDB).
	 */
	private String mariaDbSuPass;
	
	/**
	 * This variable contains given 
	 * SSH key's absolute path 
	 * (for MariaDB).
	 */
	private String keyAbsPathMaria;
	
	/**
	 * This variable contains given 
	 * passphrase of the SSH key
	 * (for MariaDB).
	 */
	private String passphraseMaria;
	
	/**
	 * This variable is true 
	 * if repository is chosen as 
	 * installation method (For MariaDB).
	 */
	private Boolean useRepositoryMaria = true;
	
	/**
	 * Absolute path to MariaDB
	 * deb file. 
	 */
	private String mariaDebAbsPath;
	
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

	/**
	 * Components' versions
	 */
	private String mariaDbVersion = "1.0";
	private String openLDAPVersion = "1.0";
	private String ejabberdVersion = "1.0";
	private String liderVersion = "1.0";
	
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

	public String getMariaDbVersion() {
		return mariaDbVersion;
	}

	public void setMariaDbVersion(String mariaDbVersion) {
		this.mariaDbVersion = mariaDbVersion;
	}

	public String getOpenLDAPVersion() {
		return openLDAPVersion;
	}

	public void setOpenLDAPVersion(String openLDAPVersion) {
		this.openLDAPVersion = openLDAPVersion;
	}

	public String getEjabberdVersion() {
		return ejabberdVersion;
	}

	public void setEjabberdVersion(String ejabberdVersion) {
		this.ejabberdVersion = ejabberdVersion;
	}

	public String getLiderVersion() {
		return liderVersion;
	}

	public void setLiderVersion(String liderVersion) {
		this.liderVersion = liderVersion;
	}

	public String getMariaDbSu() {
		return mariaDbSu;
	}

	public void setMariaDbSu(String mariaDbSu) {
		this.mariaDbSu = mariaDbSu;
	}

	public String getMariaDbSuPass() {
		return mariaDbSuPass;
	}

	public void setMariaDbSuPass(String mariaDbSuPass) {
		this.mariaDbSuPass = mariaDbSuPass;
	}

	public String getKeyAbsPathMaria() {
		return keyAbsPathMaria;
	}

	public void setKeyAbsPathMaria(String keyAbsPathMaria) {
		this.keyAbsPathMaria = keyAbsPathMaria;
	}

	public String getPassphraseMaria() {
		return passphraseMaria;
	}

	public void setPassphraseMaria(String passphraseMaria) {
		this.passphraseMaria = passphraseMaria;
	}

	public boolean isUseSSHMaria() {
		return useSSHMaria;
	}

	public void setUseSSHMaria(boolean useSSHMaria) {
		this.useSSHMaria = useSSHMaria;
	}

	public Boolean isUseRepositoryMaria() {
		return useRepositoryMaria;
	}

	public void setUseRepositoryMaria(Boolean useRepositoryMaria) {
		this.useRepositoryMaria = useRepositoryMaria;
	}

	public String getMariaDebAbsPath() {
		return mariaDebAbsPath;
	}

	public void setMariaDebAbsPath(String mariaDebAbsPath) {
		this.mariaDebAbsPath = mariaDebAbsPath;
	}
	
}
