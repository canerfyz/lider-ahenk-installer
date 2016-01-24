package tr.org.liderahenk.installer.lider.config;

import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 * 
 *         Contains configuration variables used throughout the whole setup
 *         process.
 */
public class LiderSetupConfig {

	/**
	 * ###################################
	 * 
	 * Database Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install database server
	 */
	private boolean installDatabase;

	/**
	 * Package name to be installed as database server
	 */
	private String databasePackageName;

	/**
	 * Root password for the database server
	 */
	private String databaseRootPassword;

	/**
	 * Database server IP
	 */
	private String databaseIp;

	/**
	 * Port number which is used for SSH connection
	 */
	private Integer databasePort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod databaseAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String databaseAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String databaseAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String databaseAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String databaseAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod databaseInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String databaseDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] databaseDebFileContent;

	/**
	 * ###################################
	 * 
	 * LDAP Configuration
	 *
	 * ###################################
	 */

	/**
	 * Indicates whether to install LDAP server
	 */
	private boolean installLdap;

	/**
	 * Package name to be installed as LDAP server
	 */
	private String ldapPackageName;

	/**
	 * Root password for the LDAP server
	 */
	private String ldapRootPassword;

	/**
	 * LDAP server IP
	 */
	private String ldapIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer ldapPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod ldapAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String ldapAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String ldapAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String ldapAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String ldapAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod ldapInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String ldapDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] ldapDebFileContent;

	/**
	 * ###################################
	 * 
	 * XMPP Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install XMPP Server
	 */
	private boolean installXmpp;

	/**
	 * Package name to be installed as XMPP server
	 */
	private String xmppPackageName;

	/**
	 * Root password for the XMPP server
	 */
	private String xmppRootPassword;

	/**
	 * XMPP server IP
	 */
	private String xmppIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer xmppPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod xmppAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String xmppAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String xmppAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String xmppAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String xmppAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod xmppInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String xmppDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] xmppDebFileContent;

	/**
	 * ###################################
	 * 
	 * Lider Configuration
	 * 
	 * ###################################
	 */

	/**
	 * Indicates whether to install Lider Server
	 */
	private boolean installLider;

	/**
	 * Package name to be installed as Lider server
	 */
	private String liderPackageName;

	/**
	 * Lider server IP
	 */
	private String liderIp;

	/**
	 * SSH port which is used to connect to server
	 */
	private Integer liderPort;

	/**
	 * Indicates access method which is either via username-password pair or
	 * private key.
	 */
	private AccessMethod liderAccessMethod;

	/**
	 * User name which is used for accessing target machine via SSH
	 */
	private String liderAccessUsername;

	/**
	 * Password which is used for accessing target machine via SSH
	 */
	private String liderAccessPasswd;

	/**
	 * Absolute path of the private key that is used for accessing target
	 * machine via SSH
	 */
	private String liderAccessKeyPath;

	/**
	 * Passphrase which is used for accessing target machine via SSH (Optional)
	 */
	private String liderAccessPassphrase;

	/**
	 * Indicates installation method which is either via apt-get or provided DEB
	 * package
	 */
	private InstallMethod liderInstallMethod;

	/**
	 * File name of the provided DEB package
	 */
	private String liderDebFileName;

	/**
	 * File content of the provided DEB package
	 */
	private byte[] liderDebFileContent;

	/**
	 * ###################################
	 * 
	 * Getter/setter
	 * 
	 * ###################################
	 */

	public boolean isInstallDatabase() {
		return installDatabase;
	}

	public void setInstallDatabase(boolean installDatabase) {
		this.installDatabase = installDatabase;
	}

	public boolean isInstallLdap() {
		return installLdap;
	}

	public void setInstallLdap(boolean installLdap) {
		this.installLdap = installLdap;
	}

	public boolean isInstallXmpp() {
		return installXmpp;
	}

	public void setInstallXmpp(boolean installXmpp) {
		this.installXmpp = installXmpp;
	}

	public boolean isInstallLider() {
		return installLider;
	}

	public void setInstallLider(boolean installLider) {
		this.installLider = installLider;
	}

	public String getDatabaseIp() {
		return databaseIp;
	}

	public void setDatabaseIp(String databaseIp) {
		this.databaseIp = databaseIp;
	}

	public String getLdapIp() {
		return ldapIp;
	}

	public void setLdapIp(String ldapIp) {
		this.ldapIp = ldapIp;
	}

	public String getXmppIp() {
		return xmppIp;
	}

	public void setXmppIp(String xmppIp) {
		this.xmppIp = xmppIp;
	}

	public String getLiderIp() {
		return liderIp;
	}

	public void setLiderIp(String liderIp) {
		this.liderIp = liderIp;
	}

	public String getDatabaseAccessUsername() {
		return databaseAccessUsername;
	}

	public void setDatabaseAccessUsername(String databaseAccessUsername) {
		this.databaseAccessUsername = databaseAccessUsername;
	}

	public String getDatabaseAccessPasswd() {
		return databaseAccessPasswd;
	}

	public void setDatabaseAccessPasswd(String databaseAccessPasswd) {
		this.databaseAccessPasswd = databaseAccessPasswd;
	}

	public String getDatabaseAccessKeyPath() {
		return databaseAccessKeyPath;
	}

	public void setDatabaseAccessKeyPath(String databaseAccessKeyPath) {
		this.databaseAccessKeyPath = databaseAccessKeyPath;
	}

	public String getDatabaseAccessPassphrase() {
		return databaseAccessPassphrase;
	}

	public void setDatabaseAccessPassphrase(String databaseAccessPassphrase) {
		this.databaseAccessPassphrase = databaseAccessPassphrase;
	}

	public InstallMethod getDatabaseInstallMethod() {
		return databaseInstallMethod;
	}

	public void setDatabaseInstallMethod(InstallMethod databaseInstallMethod) {
		this.databaseInstallMethod = databaseInstallMethod;
	}

	public AccessMethod getDatabaseAccessMethod() {
		return databaseAccessMethod;
	}

	public void setDatabaseAccessMethod(AccessMethod databaseAccessMethod) {
		this.databaseAccessMethod = databaseAccessMethod;
	}

	public String getDatabasePackageName() {
		return databasePackageName;
	}

	public void setDatabasePackageName(String databasePackageName) {
		this.databasePackageName = databasePackageName;
	}

	public String getDatabaseRootPassword() {
		return databaseRootPassword;
	}

	public void setDatabaseRootPassword(String databaseRootPassword) {
		this.databaseRootPassword = databaseRootPassword;
	}

	public Integer getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(Integer databasePort) {
		this.databasePort = databasePort;
	}

	public Integer getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(Integer ldapPort) {
		this.ldapPort = ldapPort;
	}

	public Integer getXmppPort() {
		return xmppPort;
	}

	public void setXmppPort(Integer xmppPort) {
		this.xmppPort = xmppPort;
	}

	public Integer getLiderPort() {
		return liderPort;
	}

	public void setLiderPort(Integer liderPort) {
		this.liderPort = liderPort;
	}

	public String getLdapPackageName() {
		return ldapPackageName;
	}

	public void setLdapPackageName(String ldapPackageName) {
		this.ldapPackageName = ldapPackageName;
	}

	public String getLdapRootPassword() {
		return ldapRootPassword;
	}

	public void setLdapRootPassword(String ldapRootPassword) {
		this.ldapRootPassword = ldapRootPassword;
	}

	public AccessMethod getLdapAccessMethod() {
		return ldapAccessMethod;
	}

	public void setLdapAccessMethod(AccessMethod ldapAccessMethod) {
		this.ldapAccessMethod = ldapAccessMethod;
	}

	public String getLdapAccessUsername() {
		return ldapAccessUsername;
	}

	public void setLdapAccessUsername(String ldapAccessUsername) {
		this.ldapAccessUsername = ldapAccessUsername;
	}

	public String getLdapAccessPasswd() {
		return ldapAccessPasswd;
	}

	public void setLdapAccessPasswd(String ldapAccessPasswd) {
		this.ldapAccessPasswd = ldapAccessPasswd;
	}

	public String getLdapAccessKeyPath() {
		return ldapAccessKeyPath;
	}

	public void setLdapAccessKeyPath(String ldapAccessKeyPath) {
		this.ldapAccessKeyPath = ldapAccessKeyPath;
	}

	public String getLdapAccessPassphrase() {
		return ldapAccessPassphrase;
	}

	public void setLdapAccessPassphrase(String ldapAccessPassphrase) {
		this.ldapAccessPassphrase = ldapAccessPassphrase;
	}

	public String getDatabaseDebFileName() {
		return databaseDebFileName;
	}

	public void setDatabaseDebFileName(String databaseDebFileName) {
		this.databaseDebFileName = databaseDebFileName;
	}

	public byte[] getDatabaseDebFileContent() {
		return databaseDebFileContent;
	}

	public void setDatabaseDebFileContent(byte[] databaseDebFileContent) {
		this.databaseDebFileContent = databaseDebFileContent;
	}

	public String getLdapDebFileName() {
		return ldapDebFileName;
	}

	public void setLdapDebFileName(String ldapDebFileName) {
		this.ldapDebFileName = ldapDebFileName;
	}

	public byte[] getLdapDebFileContent() {
		return ldapDebFileContent;
	}

	public void setLdapDebFileContent(byte[] ldapDebFileContent) {
		this.ldapDebFileContent = ldapDebFileContent;
	}

	public InstallMethod getLdapInstallMethod() {
		return ldapInstallMethod;
	}

	public void setLdapInstallMethod(InstallMethod ldapInstallMethod) {
		this.ldapInstallMethod = ldapInstallMethod;
	}

	public AccessMethod getXmppAccessMethod() {
		return xmppAccessMethod;
	}

	public void setXmppAccessMethod(AccessMethod xmppAccessMethod) {
		this.xmppAccessMethod = xmppAccessMethod;
	}

	public String getXmppAccessUsername() {
		return xmppAccessUsername;
	}

	public void setXmppAccessUsername(String xmppAccessUsername) {
		this.xmppAccessUsername = xmppAccessUsername;
	}

	public String getXmppAccessPasswd() {
		return xmppAccessPasswd;
	}

	public void setXmppAccessPasswd(String xmppAccessPasswd) {
		this.xmppAccessPasswd = xmppAccessPasswd;
	}

	public String getXmppAccessKeyPath() {
		return xmppAccessKeyPath;
	}

	public void setXmppAccessKeyPath(String xmppAccessKeyPath) {
		this.xmppAccessKeyPath = xmppAccessKeyPath;
	}

	public String getXmppAccessPassphrase() {
		return xmppAccessPassphrase;
	}

	public void setXmppAccessPassphrase(String xmppAccessPassphrase) {
		this.xmppAccessPassphrase = xmppAccessPassphrase;
	}

	public String getXmppDebFileName() {
		return xmppDebFileName;
	}

	public void setXmppDebFileName(String xmppDebFileName) {
		this.xmppDebFileName = xmppDebFileName;
	}

	public byte[] getXmppDebFileContent() {
		return xmppDebFileContent;
	}

	public void setXmppDebFileContent(byte[] xmppDebFileContent) {
		this.xmppDebFileContent = xmppDebFileContent;
	}

	public InstallMethod getXmppInstallMethod() {
		return xmppInstallMethod;
	}

	public void setXmppInstallMethod(InstallMethod xmppInstallMethod) {
		this.xmppInstallMethod = xmppInstallMethod;
	}

	public AccessMethod getLiderAccessMethod() {
		return liderAccessMethod;
	}

	public void setLiderAccessMethod(AccessMethod liderAccessMethod) {
		this.liderAccessMethod = liderAccessMethod;
	}

	public String getLiderAccessUsername() {
		return liderAccessUsername;
	}

	public void setLiderAccessUsername(String liderAccessUsername) {
		this.liderAccessUsername = liderAccessUsername;
	}

	public String getLiderAccessPasswd() {
		return liderAccessPasswd;
	}

	public void setLiderAccessPasswd(String liderAccessPasswd) {
		this.liderAccessPasswd = liderAccessPasswd;
	}

	public String getLiderAccessKeyPath() {
		return liderAccessKeyPath;
	}

	public void setLiderAccessKeyPath(String liderAccessKeyPath) {
		this.liderAccessKeyPath = liderAccessKeyPath;
	}

	public String getLiderAccessPassphrase() {
		return liderAccessPassphrase;
	}

	public void setLiderAccessPassphrase(String liderAccessPassphrase) {
		this.liderAccessPassphrase = liderAccessPassphrase;
	}

	public InstallMethod getLiderInstallMethod() {
		return liderInstallMethod;
	}

	public void setLiderInstallMethod(InstallMethod liderInstallMethod) {
		this.liderInstallMethod = liderInstallMethod;
	}

	public String getLiderDebFileName() {
		return liderDebFileName;
	}

	public void setLiderDebFileName(String liderDebFileName) {
		this.liderDebFileName = liderDebFileName;
	}

	public byte[] getLiderDebFileContent() {
		return liderDebFileContent;
	}

	public void setLiderDebFileContent(byte[] liderDebFileContent) {
		this.liderDebFileContent = liderDebFileContent;
	}

	public String getLiderPackageName() {
		return liderPackageName;
	}

	public void setLiderPackageName(String liderPackageName) {
		this.liderPackageName = liderPackageName;
	}

	public String getXmppPackageName() {
		return xmppPackageName;
	}

	public void setXmppPackageName(String xmppPackageName) {
		this.xmppPackageName = xmppPackageName;
	}

	public String getXmppRootPassword() {
		return xmppRootPassword;
	}

	public void setXmppRootPassword(String xmppRootPassword) {
		this.xmppRootPassword = xmppRootPassword;
	}

}
