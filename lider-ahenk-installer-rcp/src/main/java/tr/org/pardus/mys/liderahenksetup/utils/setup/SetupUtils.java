package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.StringUtils;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * Utility class which provides common command execution methods (such as
 * installing/un-installing a package, checking version of a package etc.)
 * locally or remotely
 *
 */
public class SetupUtils {

	private static final Logger logger = Logger.getLogger(SetupUtils.class
			.getName());

	/**
	 * Command used to check a package with the certain version number exists.
	 */
	private static final String CHECK_PACKAGE_EXIST_CMD = "sudo -S apt-cache policy {0}";

	/**
	 * Command used to check a package with the certain version number
	 * installed.
	 */
	private static final String CHECK_PACKAGE_INSTALLED_CMD = "sudo -S dpkg -l  | grep \"{0}\" | awk '{ print $3; }'";

	/**
	 * Install package via apt-get
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD = "sudo -S apt-get install -y {0}={1}";
	/**
	 * Install package via apt-get (without version)
	 */
	private static final String INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION = "sudo -S apt-get install -y {0}";

	/**
	 * Install given package via dpkg
	 */
	private static final String INSTALL_PACKAGE = "sudo -S dpkg -i {0}";

	/**
	 * Uninstall package via apt-get
	 */
	private static final String UNINSTALL_PACKAGE_CMD = "sudo -S apt-get remove --purge -y {0}";

	/**
	 * Add new repository
	 */
	private static final String ADD_APP_REPO_CMD = "sudo -S add-apt-repository -y {0} && sudo apt-get update";

	/**
	 * Tries to connect via SSH. It uses username-password pair to connect.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean canConnectViaSsh(String ip, String username,
			String password) {
		return canConnectViaSsh(ip, username, password, null, null);
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
	public static boolean canConnectViaSshWithoutPassword(String ip,
			String username, String privateKey) {
		return canConnectViaSsh(ip, username == null ? "root" : username, null,
				null, privateKey);
	}

	/**
	 * Tries to connect via SSH. If password parameter is null, then it tries to
	 * connect via SSH key
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @return true if an SSH connection can be established successfully, false
	 *         otherwise
	 */
	public static boolean canConnectViaSsh(String ip, String username,
			String password, Integer port, String privateKey) {
		SSHManager manager = null;
		try {
			manager = new SSHManager(ip, username, password, port, privateKey);
			manager.connect();
			logger.log(Level.INFO,
					"Connection established to: {0} with username: {1}",
					new Object[] { ip, username });
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
	 * @param packageName
	 * @param version
	 * @return true if the given package with the given version number exists,
	 *         false otherwise
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static boolean packageExists(String ip, String username,
			final String password, Integer port, String privateKey,
			String packageName, String version)
			throws CommandExecutionException, SSHConnectionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Checking package locally.");

			BufferedReader reader = null;

			try {

				String command = CHECK_PACKAGE_EXIST_CMD.replace("{0}",
						packageName);
				Process process = Runtime.getRuntime().exec(command);

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}

				// If input stream starts with "N:"
			    // it means that there is not such package.
				if (version == null || "".equals(version)) {
					boolean exists = !StringUtils.convertStream(
							process.getInputStream()).startsWith("N:");
					
					return exists;
				}
				else {
					boolean exists = StringUtils.convertStream(
							process.getInputStream()).contains(version);
					
					logger.log(Level.INFO, "Does package {0}:{1} exist: {2}",
							new Object[] { packageName, version, exists });
					
					return exists;
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {

			logger.log(Level.INFO,
					"Checking package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username, password, port,
					privateKey);
			manager.connect();
			String versions = manager.execCommand(CHECK_PACKAGE_EXIST_CMD,
					new Object[] { packageName }, new IOutputStreamProvider() {
						@Override
						public byte[] getStreamAsByteArray() {
							return (password + "\n").getBytes();
						}
					});
			manager.disconnect();

			/**
			 * If input stream starts with "N:"
			 * it means that there is not such package.
			 */
			if (version == null || "".equals(version)) {
				boolean exists = !versions.startsWith("N:");
				return exists;
			}
			else {
				boolean exists = versions.contains(version);
				logger.log(Level.INFO, "Does package {0}:{1} exist: {2}",
						new Object[] { packageName, version, exists });
				
				return exists;
			}
		}

		logger.log(Level.INFO, "Does package {0}:{1} exist: {2}", new Object[] {
				packageName, version, false });

		return false;
	}

	/**
	 * Checks if a package with a specific version INSTALLED in the repository.
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param packageName
	 * @param version
	 * @return true if the given package with the given version number exists,
	 *         false otherwise
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static boolean packageInstalled(String ip, String username,
			final String password, Integer port, String privateKey,
			String packageName, String version)
			throws CommandExecutionException, SSHConnectionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Checking package locally.");

			BufferedReader reader = null;

			try {

				String command = CHECK_PACKAGE_INSTALLED_CMD.replace("{0}",
						packageName);
				Process process = Runtime.getRuntime().exec(
						new String[] { "/bin/sh", "-c", command });

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
					// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}

				boolean installed = StringUtils.convertStream(
						process.getInputStream()).contains(version);

				logger.log(Level.INFO, "Is package {0}:{1} installed: {2}",
						new Object[] { packageName, version, installed });

				return installed;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			try {
				logger.log(Level.INFO,
						"Checking package remotely on: {0} with username: {1}",
						new Object[] { ip, username });
				
				SSHManager manager = new SSHManager(ip, username, password, port,
						privateKey);
				manager.connect();
				String versions = manager.execCommand(CHECK_PACKAGE_INSTALLED_CMD,
						new Object[] { packageName }, new IOutputStreamProvider() {
					@Override
					public byte[] getStreamAsByteArray() {
						return (password + "\n").getBytes();
					}
				});
				manager.disconnect();
				
				boolean installed = versions.contains(version);
				
				logger.log(Level.INFO, "Is package {0}:{1} installed: {2}",
						new Object[] { packageName, version, installed });
				
				return installed;
				
			} catch (CommandExecutionException | SSHConnectionException e) {
				e.printStackTrace();
			}
		}

		logger.log(Level.INFO, "Is package {0}:{1} installed: {2}",
				new Object[] { packageName, version, false });

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
	 * @param packageName
	 * @param version
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(String ip, String username,
			final String password, Integer port, String privateKey,
			String packageName, String version) throws SSHConnectionException,
			CommandExecutionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Installing package locally.");

			try {
				
				String command;
				String logMessage;
				
				// If version is not given
				if (version == null || "".equals(version)) {
					command = INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION.
							replace("{0}", packageName);
					logMessage = "Package {0} installed successfully";
				}
				else {
					command = INSTALL_PACKAGE_FROM_REPO_CMD.replace("{0}",
							packageName).replace("{1}", version);
					logMessage = "Package {0}:{1} installed successfully";
				}
				
				Process process = Runtime.getRuntime().exec(command);	

				// TODO eğer lokale sshla bağlanacaksam case'ini ele almak lazım.
				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}
				if (version == null || "".equals(version)) {
					logger.log(Level.INFO, logMessage,
							new Object[] { packageName, version });
				}
				else {
					logger.log(Level.INFO, logMessage,
							new Object[] { packageName });
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			try {
				logger.log(Level.INFO,
						"Installing package remotely on: {0} with username: {1}",
						new Object[] { ip, username });
				
				SSHManager manager = new SSHManager(ip, username, password, port,
						privateKey);
				manager.connect();
				
				// If version is not given
				if (version == null || "".equals(version)) {
					manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD_WITHOUT_VERSION, 
							new Object[] { packageName }, new IOutputStreamProvider() {
						@Override
						public byte[] getStreamAsByteArray() {
							return (password + "\n").getBytes();
						}
					});

					logger.log(Level.INFO, "Package {0} installed successfully",
							new Object[] { packageName });
				}
				else {
					manager.execCommand(INSTALL_PACKAGE_FROM_REPO_CMD, new Object[] {
							packageName, version }, new IOutputStreamProvider() {
						@Override
						public byte[] getStreamAsByteArray() {
							return (password + "\n").getBytes();
						}
					});

					logger.log(Level.INFO, "Package {0}:{1} installed successfully",
							new Object[] { packageName, version });
				}
				manager.disconnect();
				
			} catch (CommandExecutionException | SSHConnectionException e) {
				e.printStackTrace();
			}
		}

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
	 * @param packageName
	 * @param version
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void executeCommdGetResult(String ip, String username,final String password, Integer port, String privateKey,String param) 
			throws SSHConnectionException,	CommandExecutionException {

			logger.log(Level.INFO, "Installing package locally.");

			try {

//				String command = INSTALL_PACKAGE_FROM_REPO_CMD.replace("{0}",packageName).replace("{1}", version);
				String command = "deluge-console info";
				Process process = Runtime.getRuntime().exec(command);

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}

				BufferedReader stdInput = new BufferedReader(new 
					     InputStreamReader(process.getInputStream()));

				String s = null;
				while ((s = stdInput.readLine()) != null) {
				    System.out.println(s);
				}
				
//				logger.log(Level.INFO,
//						"Package {0}:{1} installed successfully",
//						new Object[] { packageName, version });

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	

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
	 * @param debPackage
	 * @param useTorrent
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void installPackage(String ip, String username,
			final String password, Integer port, String privateKey, File debPackage,
			boolean useTorrent) throws SSHConnectionException,
			CommandExecutionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Installing package locally.");

			try {

				if (!useTorrent) {
					copyFile(ip, username, password, port, privateKey,
							debPackage, "/tmp/");
				} else {
					// TODO copy debPackage to /tmp/
					// via torrent!
					throw new CommandExecutionException("Not yet implemented.");
				}

				String command = INSTALL_PACKAGE.replace("{0}", "/tmp/"
						+ debPackage.getName());
				Process process = Runtime.getRuntime().exec(command);

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}

				logger.log(Level.INFO, "Package {0} installed successfully",
						debPackage.getName());

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {

			logger.log(Level.INFO,
					"Installing package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			copyFile(ip, username, password, port, privateKey, debPackage,
					"/tmp/");

			SSHManager manager = new SSHManager(ip, username, password, port,
					privateKey);
			manager.connect();
			manager.execCommand(INSTALL_PACKAGE, new Object[] { "/tmp/"
					+ debPackage.getName() }, new IOutputStreamProvider() {
						@Override
						public byte[] getStreamAsByteArray() {
							return (password + "\n").getBytes();
						}
					});
			manager.disconnect();

			logger.log(Level.INFO, "Package {0} installed successfully",
					debPackage.getName());
		}
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param packageName
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void uninstallPackage(String ip, String username,
			final String password, Integer port, String privateKey, String packageName)
			throws CommandExecutionException, SSHConnectionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Uninstalling package locally.");

			try {

				String command = UNINSTALL_PACKAGE_CMD.replace("{0}",
						packageName);
				Process process = Runtime.getRuntime().exec(command);

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}

				logger.log(Level.INFO, "Package {0} uninstalled successfully",
						packageName);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {

			logger.log(Level.INFO,
					"Uninstalling package remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username, password, port,
					privateKey);
			manager.connect();
			manager.execCommand(UNINSTALL_PACKAGE_CMD,
					new Object[] { packageName }, new IOutputStreamProvider() {
						@Override
						public byte[] getStreamAsByteArray() {
							return (password + "\n").getBytes();
						}
					});
			manager.disconnect();
			
			logger.log(Level.INFO, "Package {0} uninstalled successfully",
					packageName);
		}
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param repository
	 * @throws CommandExecutionException
	 * @throws SSHConnectionException
	 */
	public static void addRepository(String ip, String username,
			final String password, Integer port, String privateKey, String repository)
			throws CommandExecutionException, SSHConnectionException {
		if (NetworkUtils.isLocal(ip)) {

			logger.log(Level.INFO, "Adding repository locally.");

			try {

				String command = ADD_APP_REPO_CMD.replace("{0}", repository);
				Process process = Runtime.getRuntime().exec(command);

				if (password != null) {
					OutputStream stdIn = process.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(stdIn));
					writer.write(password);
					writer.write("\n"); // write newline char to mimic 'enter'
										// press.
					writer.flush();
				}

				int exitValue = process.waitFor();
				if (exitValue != 0) {
					logger.log(
							Level.SEVERE,
							"Process ends with exit value: {0} - err: {1}",
							new Object[] {
									process.exitValue(),
									StringUtils.convertStream(process
											.getErrorStream()) });
					throw new CommandExecutionException(
							"Failed to execute command: " + command);
				}
				
				logger.log(Level.INFO, "Repository {0} added successfully",
						repository);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {

			logger.log(Level.INFO,
					"Adding repository remotely on: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username, password, port,
					privateKey);
			manager.connect();
			manager.execCommand(ADD_APP_REPO_CMD, new Object[] { repository }, new IOutputStreamProvider() {
				@Override
				public byte[] getStreamAsByteArray() {
					return (password + "\n").getBytes();
				}
			});
			manager.disconnect();
			
			logger.log(Level.INFO, "Repository {0} added successfully",
					repository);
		}

	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 * @param fileToTranster
	 * @param destDirectory
	 * @throws SSHConnectionException
	 * @throws CommandExecutionException
	 */
	public static void copyFile(String ip, String username, String password,
			Integer port, String privateKey, File fileToTranster,
			String destDirectory) throws SSHConnectionException,
			CommandExecutionException {
		if (NetworkUtils.isLocal(ip)) {

			if (!destDirectory.endsWith("/")) {
				destDirectory += "/";
			}
			destDirectory += fileToTranster.getName();

			logger.log(Level.INFO, "Copying file to: {0}", destDirectory);

			InputStream in = null;
			OutputStream out = null;

			try {

				in = new FileInputStream(fileToTranster);
				out = new FileOutputStream(destDirectory);

				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				
				logger.log(Level.INFO, "File {0} copied successfully",
						fileToTranster.getName());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {

			logger.log(Level.INFO, "Copying file to: {0} with username: {1}",
					new Object[] { ip, username });

			SSHManager manager = new SSHManager(ip, username, password, port,
					privateKey);
			manager.connect();
			manager.copyFileToRemote(fileToTranster, destDirectory, false);
			manager.disconnect();
			
			logger.log(Level.INFO, "File {0} copied successfully",
					fileToTranster.getName());
		}
	}

}