package tr.org.pardus.mys.liderahenksetup.ahenk.config;

import java.util.List;


/**
 * Contains configuration variables used throughout the whole setup process. 
 *
 */
public class AhenkSetupConfig {
	
	/**
	 * IP list that Ahenk will be installed 
	 */
	private List<String> ipList;
	
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
	 * AhenkSetupLocationPage variables
	 */

	private boolean installAhenkLocally;
	private boolean performNetworkScanning;
	private boolean installOnGivenIps;
//	private String[] remoteIpList;

	/**
	 * AhenkSetupNetworkScanPage variables
	 */

	private boolean showSystemInfo;
	private String username;
	private String password;
	
	/**
	 * AhenkSetupConnectionMethodPage variables
	 * (Cm: Connection Method)
	 */
	
	private boolean usePrivateKey;
	private boolean useUsernameAndPass;
	private String usernameCm;
	private String passwordCm;
	private String privateKeyAbsPath;
	private String passphrase;
	
	/**
	 * AhenkSetupInstallationMethodPage variables
	 */
	
	private boolean installByAptGet;
	private boolean installByDebFile;
	private String debFileAbsPath;
	
	/**
	 * AhenkSetupDistributionMethodPage variables
	 */
	private boolean useScp;
	private boolean useTorrent;
	
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

	public boolean isInstallAhenkLocally() {
		return installAhenkLocally;
	}

	public void setInstallAhenkLocally(boolean installAhenkLocally) {
		this.installAhenkLocally = installAhenkLocally;
	}

	public boolean isPerformNetworkScanning() {
		return performNetworkScanning;
	}

	public void setPerformNetworkScanning(boolean performNetworkScanning) {
		this.performNetworkScanning = performNetworkScanning;
	}

	public boolean isInstallOnGivenIps() {
		return installOnGivenIps;
	}

	public void setInstallOnGivenIps(boolean installOnGivenIps) {
		this.installOnGivenIps = installOnGivenIps;
	}

//	public String[] getRemoteIpList() {
//		return remoteIpList;
//	}
//
//	public void setRemoteIpList(String[] remoteIpList) {
//		this.remoteIpList = remoteIpList;
//	}

	public boolean isShowSystemInfo() {
		return showSystemInfo;
	}

	public void setShowSystemInfo(boolean showSystemInfo) {
		this.showSystemInfo = showSystemInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isUsePrivateKey() {
		return usePrivateKey;
	}

	public void setUsePrivateKey(boolean usePrivateKey) {
		this.usePrivateKey = usePrivateKey;
	}

	public String getUsernameCm() {
		return usernameCm;
	}

	public void setUsernameCm(String usernameCm) {
		this.usernameCm = usernameCm;
	}

	public String getPasswordCm() {
		return passwordCm;
	}

	public void setPasswordCm(String passwordCm) {
		this.passwordCm = passwordCm;
	}

	public String getPrivateKeyAbsPath() {
		return privateKeyAbsPath;
	}

	public void setPrivateKeyAbsPath(String privateKeyAbsPath) {
		this.privateKeyAbsPath = privateKeyAbsPath;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public List<String> getIpList() {
		return ipList;
	}

	public void setIpList(List<String> ipList) {
		this.ipList = ipList;
	}

	public boolean isInstallByAptGet() {
		return installByAptGet;
	}

	public void setInstallByAptGet(boolean installByAptGet) {
		this.installByAptGet = installByAptGet;
	}

	public boolean isInstallByDebFile() {
		return installByDebFile;
	}

	public void setInstallByDebFile(boolean installByDebFile) {
		this.installByDebFile = installByDebFile;
	}

	public String getDebFileAbsPath() {
		return debFileAbsPath;
	}

	public void setDebFileAbsPath(String debFileAbsPath) {
		this.debFileAbsPath = debFileAbsPath;
	}

	public boolean isUseScp() {
		return useScp;
	}

	public void setUseScp(boolean useScp) {
		this.useScp = useScp;
	}

	public boolean isUseTorrent() {
		return useTorrent;
	}

	public void setUseTorrent(boolean useTorrent) {
		this.useTorrent = useTorrent;
	}

	public boolean isUseUsernameAndPass() {
		return useUsernameAndPass;
	}

	public void setUseUsernameAndPass(boolean useUsernameAndPass) {
		this.useUsernameAndPass = useUsernameAndPass;
	}

}
