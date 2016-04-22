package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.org.pardus.mys.liderahenksetup.constants.PackageInstaller;
import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.StringUtils;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * Utility class which provides common command execution methods (such as
 * installing/un-installing a package, checking version of a package etc.)
 * locally or remotely
 *
 * @author Emre Akkaya <emre.akkaya@agem.com.tr>
 *
 */
public class SetupUtils {

	private static final Logger logger = Logger.getLogger(SetupUtils.class.getName());

	/**
	 * Command used to check a package with the certain version number exists.
	 */
	private static final String CHECK_PACKAGE_EXIST_CMD = "apt-cache policy {0}";

	/**
	 * Command used to check a package with the certain version number
	 * installed.
	 */
	private static final String CHECK_PACKAGE_INSTALLED_CMD = "dpkg -l  | grep \"{0}\" | awk '{ print $3; }'";

	/**
	 * Install package via apt-get
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD = "apt-get install -y --force-yes {0}={1}";

	/**
	 * Install package via apt-get (without version)
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION = "apt-get install -y --force-yes {0}";

	/**
	 * Install given package via dpkg
	 */
	private static final String INSTALL_PACKAGE = "dpkg -i {0}";

	/**
	 * Uninstall package via apt-get
	 */
	private static final String UNINSTALL_PACKAGE_CMD = "apt-get remove --purge -y {0}";

	/**
	 * Add new repository
	 */
	private static final String ADD_APP_REPO_CMD = "add-apt-repository -y {0} && sudo apt-get update";

	/**
	 * Turns off "frontend" (prompts) during installation
	 */
	private static final String SET_DEBIAN_FRONTEND = "export DEBIAN_FRONTEND='noninteractive'";

	/**
	 * Sets default values which used during the noninteractive installation
	 */
	private static final String DEBCONF_SET_SELECTIONS = "debconf-set-selections <<< '{0}'";

	/**
	 * Download file with its default file name on the server from provided URL.
	 * Downloaded file will be in /tmp/{0} folder.
	 */
	private static final String DOWNLOAD_PACKAGE = "wget ‐‐directory-prefix=/tmp/{0}/ {1}";

	/**
	 * DowNload file with provided file name from provided URL. Downloaded file
	 * will be in /tmp/{0} folder.
	 */
	private static final String DOWNLOAD_PACKAGE_WITH_FILENAME = "wget --output-document=/tmp/{0}/{1} {2}";

	private static final String INSTALL_PACKAGE_GDEBI = "gdebi -n {0}";
	
	private static final String EXTRACT_FILE = "tar -xzvf {0} --directory {1}";

	/**
	 * Tries to connect via SSH. It uses username-password pair to connect.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param passphrase
	 * @return
	 */
	public static boolean canConnectViaSsh(final String ip, final String username, final String password,
			final String passphrase) {
		return canConnectViaSsh(ip, username, password, null, null, passphrase);
	}

	/**
	 * Tries to connect via SSH key. It uses SSH private key to connect.
	 * 
	 * @param ip
	 * @param username
	 *            default value is 'root'
	 * @param privateKey
	 * @return true if an SSH connection can be established successfully, false
	 *         otherwise
	 */
	public static boolean canConnectViaSshWithoutPassword(final String ip, final String username,
			final String privateKey, final String passphrase) {
		return canConnectViaSsh(ip, username == null ? "root" : username, null, null, privateKey, passphrase);
	}

	/**
	 * Tries to connect via SSH. If password parameter is null, then it tries to
	 * connect via SSH key
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @return true if an SSH connection can be established successfully, false
	 *         otherwise
	 */
	public static boolean canConnectViaSsh(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase) {
		SSHManager manager = null;
		try {
			manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
			manager.connect();
			logger.log(Level.INFO, "Connection established to: {0} with username: {1}",
					new Object[] { ip, username == null ? "root" : username });
			return true;
		} catch (SSHConnectionException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} finally {
			if (manager != null) {
				manager.disconnect();
			}
		}
		return false;
	}

	/**
	 * Checks if a package with a specific version EXISTS (it may be installed
	 * or candidate!) in the repository. If it exists, it can be installed via
	 * installPackage()
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @param version
	 * @return true if the given package with the given version number exists,
	 *         false otherwise
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static boolean packageExists(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName,
			final String version) throws CommandExecutionException, SSHConnectionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Checking package locally.");
//
//			BufferedReader reader = null;
//
//			try {
//
//				String command = CHECK_PACKAGE_EXIST_CMD.replace("{0}", packageName);
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				// If input stream starts with "N:"
//				// it means that there is not such package.
//				if (version == null || "".equals(version)) {
//					boolean exists = !StringUtils.convertStream(process.getInputStream()).startsWith("N:");
//
//					return exists;
//				} else {
//					boolean exists = StringUtils.convertStream(process.getInputStream()).contains(version);
//
//					logger.log(Level.INFO, "Does package {0}:{1} exist: {2}",
//							new Object[] { packageName, version, exists });
//
//					return exists;
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					try {
//						reader.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} else {

			logger.log(Level.INFO, "Checking package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			String versions = null; 
			 manager.execCommand(CHECK_PACKAGE_EXIST_CMD,new Object[] { packageName });
			manager.disconnect();

			/**
			 * If input stream starts with "N:" it means that there is not such
			 * package.
			 */
			if (version == null || "".equals(version)) {
				boolean exists = !versions.startsWith("N:");
				return exists;
			} else {
				boolean exists = versions.contains(version);
				logger.log(Level.INFO, "Does package {0}:{1} exist: {2}",
						new Object[] { packageName, version, exists });

				return exists;
			}
//		}

//		logger.log(Level.INFO, "Does package {0}:{1} exist: {2}", new Object[] { packageName, version, false });
//		return false;
	}

	/**
	 * Checks if a package with a specific version INSTALLED in the repository.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @param version
	 * @return true if the given package with the given version number exists,
	 *         false otherwise
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static boolean packageInstalled(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName,
			final String version) throws CommandExecutionException, SSHConnectionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Checking package locally.");
//
//			BufferedReader reader = null;
//
//			try {
//
//				String command = CHECK_PACKAGE_INSTALLED_CMD.replace("{0}", packageName);
//				Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				boolean installed = StringUtils.convertStream(process.getInputStream()).contains(version);
//
//				logger.log(Level.INFO, "Is package {0}:{1} installed: {2}",
//						new Object[] { packageName, version, installed });
//
//				return installed;
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					try {
//						reader.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} else {
			try {
				logger.log(Level.INFO, "Checking package remotely on: {0} with username: {1}",
						new Object[] { ip, username });

				SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port,
						privateKey, passphrase);
				manager.connect();
				String versions = null; // manager.execCommand(CHECK_PACKAGE_INSTALLED_CMD,
										// new Object[] { packageName });
				manager.disconnect();

				boolean installed = versions.contains(version);

				logger.log(Level.INFO, "Is package {0}:{1} installed: {2}",
						new Object[] { packageName, version, installed });

				return installed;

			} catch (SSHConnectionException e) {
				e.printStackTrace();
			}
//		}

		logger.log(Level.INFO, "Is package {0}:{1} installed: {2}", new Object[] { packageName, version, false });

		return false;
	}

	/**
	 * Installs a package which specified by package name and version. Before
	 * calling this method, package existence should be ensured by calling
	 * packageExists() method.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @param version
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String packageName, final String version)
					throws SSHConnectionException, CommandExecutionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Installing package locally.");
//
//			try {
//
//				String command;
//				String logMessage;
//
//				// If version is not given
//				if (version == null || version.isEmpty()) {
//					command = INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION.replace("{0}", packageName);
//					logMessage = "Package {0} installed successfully";
//				} else {
//					command = INSTALL_PACKAGE_FROM_REPO_CMD.replace("{0}", packageName).replace("{1}", version);
//					logMessage = "Package {0}:{1} installed successfully";
//				}
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//				if (version == null || "".equals(version)) {
//					logger.log(Level.INFO, logMessage, new Object[] { packageName, version });
//				} else {
//					logger.log(Level.INFO, logMessage, new Object[] { packageName });
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {
//			try {
				logger.log(Level.INFO, "Installing package remotely on: {0} with username: {1}",
						new Object[] { ip, username });

				SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port,
						privateKey, passphrase);
				manager.connect();

				// If version is not given
				if (version == null || "".equals(version)) {
					manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION, new Object[] { packageName });
					logger.log(Level.INFO, "Package {0} installed successfully", new Object[] { packageName });
				} else {
					manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD, new Object[] { packageName, version });
					logger.log(Level.INFO, "Package {0}:{1} installed successfully",
							new Object[] { packageName, version });
				}
				manager.disconnect();
//
//			} catch (SSHConnectionException e) {
//				e.printStackTrace();
//			}
//		}

	}

	/**
	 * Installs a package 'non-interactively' which specified by package name
	 * and version. Before installing the package it uses debconf-set-selections
	 * to insert default values which asked during the interactive installation.
	 * 
	 * Before calling this method, package existence should be ensured by
	 * calling packageExists() method.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param packageName
	 * @param passphrase
	 * @param version
	 * @param debconfValues
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void installPackageNoninteractively(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName,
			final String version, final String[] debconfValues)
					throws Exception {

//		if (NetworkUtils.isLocal(ip)) {
//
//				// Set frontend as noninteractive
//				String command = SET_DEBIAN_FRONTEND;
//				Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				// Set debconf values
//				for (String value : debconfValues) {
//
//					command = DEBCONF_SET_SELECTIONS.replace("{0}", value);
//					process = new ProcessBuilder("sudo", "-u", username, "/bin/bash", "-c", command).start();
//
//					exitValue = process.waitFor();
//					OutputStream stream = process.getOutputStream();
//					stream.write((password + "\n").getBytes(StandardCharsets.UTF_8));
//					if (exitValue != 0) {
//						logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}", new Object[] {
//								process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//						throw new CommandExecutionException("Failed to execute command: " + command);
//					}
//
//				}
//
//				// Finally, install the package
//				SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, packageName, version);
//
//		} else {

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			// Set frontend as noninteractive
			manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

			manager.disconnect();
			manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
			manager.connect();

			// Set debconf values
			for (String value : debconfValues) {
				manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
			}

			manager.disconnect();

			// Finally, install the package
			SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, packageName, version);
//		}

	}

	/**
	 * Installs a deb package file. This can be used when a specified deb
	 * package is already provided
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param debPackage
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final File debPackage,
			final PackageInstaller packageInstaller) throws SSHConnectionException, CommandExecutionException {
		
		String command;
		
		if (packageInstaller == PackageInstaller.DPKG) {
			command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + debPackage.getName());
		} else {
			command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/" + debPackage.getName());
		}

//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Installing package locally.");
//
//			try {
//
//				copyFile(ip, username, password, port, privateKey, passphrase, debPackage, "/tmp/");
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Package {0} installed successfully", debPackage.getName());
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {

			logger.log(Level.INFO, "Installing package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			copyFile(ip, username, password, port, privateKey, passphrase, debPackage, "/tmp/");

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			manager.execCommand(command, new Object[] {});
			manager.disconnect();

			logger.log(Level.INFO, "Package {0} installed successfully", debPackage.getName());
//		}
	}

	/**
	 * Installs a deb package file. This can be used when a specified deb
	 * package is already provided. Before installing the package it uses
	 * debconf-set-selections to insert default values which asked during the
	 * interactive installation.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param debPackage
	 * @param debconfValues
	 */
	public static void installPackageNonInteractively(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final File debPackage,
			final String[] debconfValues, final PackageInstaller packageInstaller) throws Exception {
//		if (NetworkUtils.isLocal(ip)) {
//
//				// Set frontend as noninteractive
//				String command = SET_DEBIAN_FRONTEND;
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				// Set debconf values
//				for (String value : debconfValues) {
//
//					command = DEBCONF_SET_SELECTIONS.replace("{0}", value);
//					process = Runtime.getRuntime().exec(command);
//
//					exitValue = process.waitFor();
//					if (exitValue != 0) {
//						logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}", new Object[] {
//								process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//						throw new CommandExecutionException("Failed to execute command: " + command);
//					}
//
//				}
//
//				// Finally, install the package
//				SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, debPackage, packageInstaller);
//
//		} else {

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			// Set frontend as noninteractive
			manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

			manager.disconnect();
			manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
			manager.connect();

			// Set debconf values
			for (String value : debconfValues) {
				manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
			}

			manager.disconnect();

			// Finally, install the package
			SetupUtils.installPackage(ip, username, password, port, privateKey, passphrase, debPackage, packageInstaller);
//		}

	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param packageName
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void uninstallPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String packageName)
					throws CommandExecutionException, SSHConnectionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Uninstalling package locally.");
//
//			try {
//
//				String command = UNINSTALL_PACKAGE_CMD.replace("{0}", packageName);
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Package {0} uninstalled successfully", packageName);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {

			logger.log(Level.INFO, "Uninstalling package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			manager.execCommand(UNINSTALL_PACKAGE_CMD, new Object[] { packageName });
			manager.disconnect();

			logger.log(Level.INFO, "Package {0} uninstalled successfully", packageName);
//		}
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param repository
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void addRepository(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String repository)
					throws CommandExecutionException, SSHConnectionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Adding repository locally.");
//
//			try {
//
//				String command = ADD_APP_REPO_CMD.replace("{0}", repository);
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Repository {0} added successfully", repository);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {

			logger.log(Level.INFO, "Adding repository remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			manager.execCommand(ADD_APP_REPO_CMD, new Object[] { repository });
			manager.disconnect();

			logger.log(Level.INFO, "Repository {0} added successfully", repository);
//		}

	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param fileToTranster
	 * @param destDirectory
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void copyFile(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final File fileToTranster, final String destDirectory)
					throws SSHConnectionException, CommandExecutionException {
		String destinationDir = destDirectory;
		if (!destinationDir.endsWith("/")) {
			destinationDir += "/";
		}
		
//		if (NetworkUtils.isLocal(ip)) {
//
//			String destinationDir = destDirectory;
//			if (!destinationDir.endsWith("/")) {
//				destinationDir += "/";
//			}
//			destinationDir += fileToTranster.getName();
//
//			logger.log(Level.INFO, "Copying file to: {0}", destinationDir);
//
//			InputStream in = null;
//			OutputStream out = null;
//
//			try {
//
//				in = new FileInputStream(fileToTranster);
//				out = new FileOutputStream(destinationDir);
//
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = in.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//
//				logger.log(Level.INFO, "File {0} copied successfully", fileToTranster.getName());
//
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				if (in != null) {
//					try {
//						in.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				if (out != null) {
//					try {
//						in.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//		} else {
			logger.log(Level.INFO, "Copying file to: {0} with username: {1}", new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			manager.copyFileToRemote(fileToTranster, destinationDir, false);
			manager.disconnect();

			logger.log(Level.INFO, "File {0} copied successfully", fileToTranster.getName());
//		}
	}

	/**
	 * 
	 * Executes a command on the given machine.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param command
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void executeCommand(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String command)
					throws SSHConnectionException, CommandExecutionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Executing command locally.");
//
//			try {
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Command: '{0}' executed successfully.", new Object[] { command });
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {
			logger.log(Level.INFO, "Executing command remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			manager.execCommand(command, new Object[] {});
			logger.log(Level.INFO, "Command: '{0}' executed successfully.", new Object[] { command });

			manager.disconnect();
//		}

	}
	
	public static void executeCommand(final String ip, final String username, final String password, final Integer port,
			final String privateKey, final String passphrase, final String command, IOutputStreamProvider outputStreamProvider)
					throws SSHConnectionException, CommandExecutionException {
		logger.log(Level.INFO, "Executing command remotely on: {0} with username: {1}",
				new Object[] { ip, username });

		SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
				passphrase);
		manager.connect();

		manager.execCommand(command, new Object[] {}, outputStreamProvider);
		logger.log(Level.INFO, "Command: '{0}' executed successfully.", new Object[] { command });

		manager.disconnect();
	}

	/**
	 * Installs a deb package which has been downloaded before by
	 * downloadPackage method. It searches the file in /tmp/{tmpDir} folder.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param tmpDir
	 * @param filename
	 * @param packageInstaller
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installDownloadedPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String tmpDir,
			final String filename, final PackageInstaller packageInstaller) throws SSHConnectionException, CommandExecutionException {
		
		String command;

		if (packageInstaller == PackageInstaller.DPKG) {
			// Prepare command
			if (!"".equals(filename)) {
				command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + tmpDir + "/" + filename);
			} else {
				command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + tmpDir + "/*.deb");
			}
		} else {
			if (!"".equals(filename)) {
				command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/" + tmpDir + "/" + filename);
			} else {
				command = INSTALL_PACKAGE_GDEBI.replace("{0}", "/tmp/" + tmpDir + "/*.deb");
			}
		}
		
//		// Prepare command
//		if (!"".equals(filename)) {
//			command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + tmpDir + "/" + filename);
//		} else {
//			command = INSTALL_PACKAGE.replace("{0}", "/tmp/" + tmpDir + "/*.deb");
//		}

//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Installing package locally.");
//
//			try {
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {} - err: {}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Package {} installed successfully", filename);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {

			logger.log(Level.INFO, "Installing package remotely on: {} with username: {}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();
			manager.execCommand(command, new Object[] {});
			manager.disconnect();

			logger.log(Level.INFO, "Package {} installed successfully", filename);
//		}
	}

	/**
	 * Downloads a file from given URL to given machine. It creates another
	 * folder with provided name under /tmp to prevent duplication of files.
	 * (e.g.: If tmpDir parameter is given as "ahenkTmpDir" then downloaded file
	 * will be under /tmp/ahenkTmpDir/ folder.)
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param passphrase
	 * @param filename
	 * @param downloadUrl
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void downloadPackage(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase, final String tmpDir,
			final String filename, final String downloadUrl) throws SSHConnectionException, CommandExecutionException {

		String command;

		if (filename == null || "".equals(filename)) {
			command = DOWNLOAD_PACKAGE.replace("{0}", tmpDir).replace("{1}", downloadUrl);
		} else {
			command = DOWNLOAD_PACKAGE_WITH_FILENAME.replace("{0}", tmpDir).replace("{1}", filename).replace("{2}",
					downloadUrl);
		}

//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Executing command locally.");
//
//			try {
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Command: '{0}' executed successfully.", new Object[] { command });
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {
			logger.log(Level.INFO, "Executing command remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			manager.execCommand(command, new Object[] {});
			logger.log(Level.INFO, "Command: '{0}' executed successfully.",
					new Object[] { DOWNLOAD_PACKAGE.replace("{0}", filename).replace("{1}", downloadUrl) });

			manager.disconnect();
//		}

	}

	/**
	 * Installs a deb package which has been downloaded before by
	 * downloadPackage method. Before installing the package it uses
	 * debconf-set-selections to insert default values which asked during the
	 * interactive installation. It searches the file in /tmp folder.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param debPackage
	 * @param debconfValues
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installDownloadedPackageNonInteractively(final String ip, final String username,
			final String password, final Integer port, final String privateKey, final String passphrase,
			final String tmpDir, final String filename, final String[] debconfValues, final PackageInstaller packageInstaller)
					throws SSHConnectionException, CommandExecutionException {
//		if (NetworkUtils.isLocal(ip)) {
//
//			try {
//
//				// Set frontend as noninteractive
//				String command = SET_DEBIAN_FRONTEND;
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				// Set debconf values
//				for (String value : debconfValues) {
//
//					command = DEBCONF_SET_SELECTIONS.replace("{0}", value);
//					process = Runtime.getRuntime().exec(command);
//
//					exitValue = process.waitFor();
//					if (exitValue != 0) {
//						logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}", new Object[] {
//								process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//						throw new CommandExecutionException("Failed to execute command: " + command);
//					}
//
//				}
//
//				// Finally, install the downloaded package
//				SetupUtils.installDownloadedPackage(ip, username, password, port, privateKey, passphrase, tmpDir,
//						filename, packageInstaller);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			// Set frontend as noninteractive
			manager.execCommand(SET_DEBIAN_FRONTEND, new Object[] {});

			manager.disconnect();
			manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey, passphrase);
			manager.connect();

			// Set debconf values
			for (String value : debconfValues) {
				manager.execCommand(DEBCONF_SET_SELECTIONS, new Object[] { value });
			}

			manager.disconnect();

			// Finally, install the downloaded package
			SetupUtils.installDownloadedPackage(ip, username, password, port, privateKey, passphrase, tmpDir, filename, packageInstaller);
//		}
	}
	
	public static void extractTarFile(final String ip, final String username, final String password,
			final Integer port, final String privateKey, final String passphrase,
			final String pathOfFile, final String extracingDestination) throws SSHConnectionException, CommandExecutionException {

		String command = EXTRACT_FILE.replace("{0}", pathOfFile).replace("{1}", extracingDestination);

//		if (NetworkUtils.isLocal(ip)) {
//
//			logger.log(Level.INFO, "Executing command locally.");
//
//			try {
//
//				Process process = Runtime.getRuntime().exec(command);
//
//				int exitValue = process.waitFor();
//				if (exitValue != 0) {
//					logger.log(Level.SEVERE, "Process ends with exit value: {0} - err: {1}",
//							new Object[] { process.exitValue(), StringUtils.convertStream(process.getErrorStream()) });
//					throw new CommandExecutionException("Failed to execute command: " + command);
//				}
//
//				logger.log(Level.INFO, "Command: '{0}' executed successfully.", new Object[] { command });
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		} else {
			logger.log(Level.INFO, "Executing command remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username == null ? "root" : username, password, port, privateKey,
					passphrase);
			manager.connect();

			manager.execCommand(command, new Object[] {});
			logger.log(Level.INFO, "Command: '{0}' executed successfully.",
					new Object[] { EXTRACT_FILE.replace("{0}", pathOfFile).replace("{1}", extracingDestination) });

			manager.disconnect();
//		}

	}
	
	public static String replace(Map<String, String> map, String text) {
		for (Entry<String, String> entry: map.entrySet()) {
			text = text.replaceAll(entry.getKey().replaceAll("#", "\\#"), entry.getValue());
		}
		return text;
	}
	
	public static File streamToFile(InputStream stream, String filename) {
		try {
			File file = new File(System.getProperty("java.io.tmpdir")+File.separator+filename);
			OutputStream outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
	
			while ((read = stream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
	
			outputStream.close();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}