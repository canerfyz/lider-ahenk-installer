package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHManager {

	private static final Logger logger = Logger.getLogger(SSHManager.class.getName());

	private JSch SSHChannel;
	private Session session;
	private Properties config;

	// Connection parameters
	private String username;
	private String password;
	private String ip;
	private int port;
	private String privateKey;

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 */
	public SSHManager(String ip, String username, String password) {
		init();
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.port = Integer.parseInt(PropertyReader.property("connection.port"));
	}

	/**
	 * 
	 * @param ip
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKey
	 */
	public SSHManager(String ip, String username, String password, Integer port, String privateKey) {
		init();
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.port = (port == null ? Integer.parseInt(PropertyReader.property("connection.port")) : port);
		this.privateKey = privateKey;
	}

	private void init() {
		JSch.setLogger(new SSHLogger());
		SSHChannel = new JSch();

		config = new Properties();
		// TODO check kex value
		config.put("kex",
				"diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
		config.put("StrictHostKeyChecking", "no");
	}

	/**
	 * Tries to connect via SSH key or username-password pair
	 * 
	 * @throws SSHConnectionException
	 *             if it fails to connect
	 */
	public void connect() throws SSHConnectionException {
		try {
			if (privateKey != null && !privateKey.isEmpty()) {
				SSHChannel.addIdentity(privateKey); // TODO passphrase
			}
			session = SSHChannel.getSession(username, ip, port);
			if (password != null && !password.isEmpty()) {
				session.setPassword(password);
			}
			session.setConfig(config);
			session.connect(Integer.parseInt(PropertyReader.property("network.timeout")));
		} catch (JSchException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new SSHConnectionException(e.getMessage());
		}
	}

	/**
	 * Executes command string via SSH
	 * 
	 * @param command
	 * @return output of the executed command
	 * @throws CommandExecutionException
	 */
	public String execCommand(String command, IOutputStreamProvider outputStreamProvider)
			throws CommandExecutionException {

		StringBuilder outputBuffer = new StringBuilder();

		logger.log(Level.INFO, "Command: {0}", command);

		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			InputStream inputStream = channel.getInputStream();
			// ((ChannelExec)channel).setErrStream(System.err); // TODO
//			InputStream errStream = ((ChannelExec)channel).getErrStream();
			((ChannelExec)channel).setPty(true);
			OutputStream outputStream = null;
			if (outputStreamProvider != null) {
				outputStream = channel.getOutputStream();
			}
			channel.connect();

			if (outputStream != null) {
				// outputStream.write(outputStreamProvider.getStreamAsByteArray());
				outputStream.write(("oner5644\n").getBytes(StandardCharsets.UTF_8));
				outputStream.flush();
			}

			byte[] tmp = new byte[1024];
			while (true) {
				while (inputStream.available() > 0) {
					int i = inputStream.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("exit status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}

			channel.disconnect();

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new CommandExecutionException(e.getMessage());
		}

		return outputBuffer.toString();
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the params array before execution.
	 * 
	 * @param command
	 * @param params
	 * @return output of the executed command
	 * @throws CommandExecutionException
	 */
	public String execCommand(String command, Object[] params) throws CommandExecutionException {
		return execCommand(command, params, null);
	}

	/**
	 * Executes command string via SSH. Replaces parameter indicators with
	 * values from the params array before execution. While executing the
	 * command feeds its output stream via IOutputStreamProvider instance
	 * 
	 * @param command
	 * @param params
	 * @param outputStreamProvider
	 * @return output of the executed command
	 * @throws CommandExecutionException
	 */
	public String execCommand(String command, Object[] params, IOutputStreamProvider outputStreamProvider)
			throws CommandExecutionException {
		String tmpCommand = command;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				String param = params[i].toString();
				tmpCommand = tmpCommand.replaceAll("\\{" + i + "\\}", param);
			}
		}
		return execCommand(tmpCommand, outputStreamProvider);
	}

	/**
	 * Tries to safe-copy local file to remote server.
	 * 
	 * @param fileToTransfer
	 * @param destDirectory
	 * @param preserveTimestamp
	 * @throws CommandExecutionException
	 */
	public void copyFileToRemote(File fileToTransfer, String destDirectory, boolean preserveTimestamp)
			throws CommandExecutionException {

		FileInputStream fis = null;
		String error = null;

		try {

			String command = "scp " + (preserveTimestamp ? "-p" : "") + " -t " + destDirectory
					+ fileToTransfer.getName();

			logger.log(Level.INFO, "Command: {0}", command);

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);

			OutputStream out = channel.getOutputStream();
			InputStream in = channel.getInputStream();

			channel.connect();

			if ((error = checkAck(in)) != null) {
				throw new CommandExecutionException(error);
			}

			if (preserveTimestamp) {
				command = "T " + (fileToTransfer.lastModified() / 1000) + " 0";
				// The access time should be sent here,
				// but it is not accessible with JavaAPI ;-<
				command += (" " + (fileToTransfer.lastModified() / 1000) + " 0\n");
				out.write(command.getBytes());
				out.flush();
				if ((error = checkAck(in)) != null) {
					throw new CommandExecutionException(error);
				}
			}

			// send scp command
			long filesize = fileToTransfer.length();
			command = "C0644 " + filesize + " " + fileToTransfer.getName() + "\n";
			out.write(command.getBytes());
			out.flush();
			if ((error = checkAck(in)) != null) {
				throw new CommandExecutionException(error);
			}

			// send content of local file
			fis = new FileInputStream(fileToTransfer);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				out.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();
			if ((error = checkAck(in)) != null) {
				throw new CommandExecutionException(error);
			}
			out.close();

			channel.disconnect();

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw new CommandExecutionException(e.getMessage());
		}

	}

	static String checkAck(InputStream in) throws IOException {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1 || b == 2) { // error
				return sb.toString();
			}
		}
		return null;
	}

	public void disconnect() {
		session.disconnect();
	}

	public JSch getSSHChannel() {
		return SSHChannel;
	}

	public Session getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getPrivateKey() {
		return privateKey;
	}

}
