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
	 * These variable are true 
	 * if SSH is chosen as 
	 * connection method
	 * (for each component).
	 */
	private boolean mariaUseSSH;
	private boolean karafUseSSH;
	private boolean ldapUseSSH;
	
	/**
	 * These variables contain
	 * given username to access
	 * a computer via SSH
	 * (for each component).
	 */
	private String mariaDbSu;
	private String karafSu;
	private String ldapSu;

	/**
	 * These variables contain
	 * given password to access
	 * a computer via SSH 
	 * (for each component).
	 */
	private String mariaDbSuPass;
	private String karafSuPass;
	private String ldapSuPass;
	
	/**
	 * These variables contain given 
	 * SSH key's absolute path 
	 * (for each component).
	 */
	private String mariaKeyAbsPath;
	private String karafKeyAbsPath;
	private String ldapKeyAbsPath;
	
	/**
	 * These variables contains given 
	 * passphrase of the SSH key
	 * (for each component).
	 */
	private String mariaPassphrase;
	private String karafPassphrase;
	private String ldapPassphrase;
	
	/**
	 * These variables are true 
	 * if repository is chosen 
	 * as installation method 
	 * (for each component).
	 */
	private Boolean mariaUseRepository = true;
	private Boolean karafUseRepository = true;
	private Boolean ldapUseRepository = true;
	
	/**
	 * These variables keep absolute path to
	 * deb files (for each component). 
	 */
	private String mariaDebAbsPath;
	private String karafDebAbsPath;
	private String ldapDebAbsPath;
	
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

	public String getMariaDebAbsPath() {
		return mariaDebAbsPath;
	}

	public void setMariaDebAbsPath(String mariaDebAbsPath) {
		this.mariaDebAbsPath = mariaDebAbsPath;
	}

	public boolean isMariaUseSSH() {
		return mariaUseSSH;
	}

	public void setMariaUseSSH(boolean mariaUseSSH) {
		this.mariaUseSSH = mariaUseSSH;
	}

	public boolean isKarafUseSSH() {
		return karafUseSSH;
	}

	public void setKarafUseSSH(boolean karafUseSSH) {
		this.karafUseSSH = karafUseSSH;
	}

	public String getMariaKeyAbsPath() {
		return mariaKeyAbsPath;
	}

	public void setMariaKeyAbsPath(String mariaKeyAbsPath) {
		this.mariaKeyAbsPath = mariaKeyAbsPath;
	}

	public String getKarafKeyAbsPath() {
		return karafKeyAbsPath;
	}

	public void setKarafKeyAbsPath(String karafKeyAbsPath) {
		this.karafKeyAbsPath = karafKeyAbsPath;
	}

	public String getMariaPassphrase() {
		return mariaPassphrase;
	}

	public void setMariaPassphrase(String mariaPassphrase) {
		this.mariaPassphrase = mariaPassphrase;
	}

	public String getKarafPassphrase() {
		return karafPassphrase;
	}

	public void setKarafPassphrase(String karafPassphrase) {
		this.karafPassphrase = karafPassphrase;
	}

	public Boolean isMariaUseRepository() {
		return mariaUseRepository;
	}

	public void setMariaUseRepository(Boolean mariaUseRepository) {
		this.mariaUseRepository = mariaUseRepository;
	}

	public Boolean isKarafUseRepository() {
		return karafUseRepository;
	}

	public void setKarafUseRepository(Boolean karafUseRepository) {
		this.karafUseRepository = karafUseRepository;
	}

	public String getKarafDebAbsPath() {
		return karafDebAbsPath;
	}

	public void setKarafDebAbsPath(String karafDebAbsPath) {
		this.karafDebAbsPath = karafDebAbsPath;
	}

	public boolean isLdapUseSSH() {
		return ldapUseSSH;
	}

	public void setLdapUseSSH(boolean ldapUseSSH) {
		this.ldapUseSSH = ldapUseSSH;
	}

	public String getKarafSu() {
		return karafSu;
	}

	public void setKarafSu(String karafSu) {
		this.karafSu = karafSu;
	}

	public String getLdapSu() {
		return ldapSu;
	}

	public void setLdapSu(String ldapSu) {
		this.ldapSu = ldapSu;
	}

	public String getKarafSuPass() {
		return karafSuPass;
	}

	public void setKarafSuPass(String karafSuPass) {
		this.karafSuPass = karafSuPass;
	}

	public String getLdapSuPass() {
		return ldapSuPass;
	}

	public void setLdapSuPass(String ldapSuPass) {
		this.ldapSuPass = ldapSuPass;
	}

	public String getLdapKeyAbsPath() {
		return ldapKeyAbsPath;
	}

	public void setLdapKeyAbsPath(String ldapKeyAbsPath) {
		this.ldapKeyAbsPath = ldapKeyAbsPath;
	}

	public String getLdapPassphrase() {
		return ldapPassphrase;
	}

	public void setLdapPassphrase(String ldapPassphrase) {
		this.ldapPassphrase = ldapPassphrase;
	}

	public Boolean isLdapUseRepository() {
		return ldapUseRepository;
	}

	public void setLdapUseRepository(Boolean ldapUseRepository) {
		this.ldapUseRepository = ldapUseRepository;
	}

	public String getLdapDebAbsPath() {
		return ldapDebAbsPath;
	}

	public void setLdapDebAbsPath(String ldapDebAbsPath) {
		this.ldapDebAbsPath = ldapDebAbsPath;
	}

	public Boolean getMariaUseRepository() {
		return mariaUseRepository;
	}

	public Boolean getKarafUseRepository() {
		return karafUseRepository;
	}
	
}
