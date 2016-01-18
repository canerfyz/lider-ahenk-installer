//package tr.org.pardus.mys.liderahenksetup.torrent.management;
//
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
////import org.apache.log4j.BasicConfigurator;
//
//import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;
//import tr.org.pardus.mys.liderahenksetup.exception.SSHConnectionException;
//import tr.org.pardus.mys.liderahenksetup.utils.setup.IOutputStreamProvider;
//import tr.org.pardus.mys.liderahenksetup.utils.setup.SSHManager;
//import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;
//import tr.org.pardus.mys.liderahenksetup.utils.torrent.TorrentUtil;
//
//import com.turn.ttorrent.tracker.Tracker;
//
//
//
//public class Run {
//
//	private static String LOCALHOST="localhost";
//	private static String TMP="/tmp/";
//	
//	private static String CMD_EXECUTION_PERMISSION="sudo -S chmod 777 %s ";//TODO değiştir
//	private static String CMD_UPDATE="sudo -S apt-get update";
//	
//	private static String CMD_DELUGE_CONSOLE="deluge-console";
//	private static String CMD_DELUGE_ADD_TORRENT="deluge-console add %s ";
//	private static String CMD_DELUGED="deluged";
//	
////	private static String CMD_KILL_ALL_DELUGED="sudo -S pkill deluged";
//	private static String CMD_KILL_ALL_DELUGED="sudo -S killall deluged";
//	
//	private static String CMD_ACTIVATE_EXECUTE_PLUGIN="deluge-console 'plugin -e Execute'";
//	private static String CMD_CREATE_SCRIPT="printf '#!/bin/bash\\n %s ' >> %s%s";
//	private static String CMD_UPDATE_REPOSITORY="sudo -S add-apt-repository %s ";
//	
//	private static String CMD_DELUGE_CONSOLE_PARAMETERS=" add -p %s %s";//TODO
//	
//	private static String EXECUTE_CONF_CONTENT="{\"file\": 1,\"format\": 1}{\"commands\": [[\"1\", \"complete\", \"%s%s\"]]}";
//	private static String EXECUTE_CONF_PATH="~/.config/deluge/execute.conf";
//	private static String EXECUTE_CONF_EDIT="printf '%s' > %s";
//	
//	
//	public static void main(String[] args) {
//
//		Run r = new Run();
//		
//		String scriptContent="echo \"Torrentimiz inmistir\" >> /tmp/compete.log ";
//		
//		String torrentFilePath="/tmp/batman.torrent";										//torrent dosyasının yolu
//		String torrentDataPath="/home/volkan/Desktop/batman/batman.zip";				//torrent ile paylaşılacak dosyanın yolu
//		String announcedFilePath ="/home/volkan/Desktop/batman/";						//tracker bu klasör içindeki torrentlerin dağıtım işi ile ilgilenir
//		String trackerIp =TorrentUtil.getMyIpAddres();									// dosya paylaşımının izleyecek makinenin ip adresi
//		Integer trackerSocket=40415;													// paylaşım sırasında kullanılacak port numarası
//		String scriptName="run.sh";
//		
////		BasicConfigurator.configure(); log4j multiple deps problem - emre
////-----------------------------------------------------------------------------------------------------//
////		TODO tracker aç
//				
//		String trackerUrl = new StringBuilder("http://").append(trackerIp.toString()).append(":").append(trackerSocket).append("/announce").toString();
//		Tracker tracker = TorrentUtil.getTracker(trackerIp, trackerSocket, announcedFilePath);
//		tracker.start();
//		
//		
////		tracker.stop();
//		
//		
////-----------------------------------------------------------------------------------------------------//
////		TODO torrent oluştur + sonuna tarih koy
////		TorrentFactory.createTorrent(torrentFilePath, torrentDataPath, trackerUrl, null);
////-----------------------------------------------------------------------------------------------------//
////		seed yap + kontroller
//		
////		r.checkAndInstallPackage( Arrays.asList(new SSHManager(LOCALHOST, "volkan", "volkan5644")),r.getRequiredPackages());
//		r.seedThisTorrent(announcedFilePath,torrentFilePath);
////-----------------------------------------------------------------------------------------------------//
////		r.checkAndInstallPackage(r.getSelectedDevices(),r.getRequiredPackages());
////		r.torrentClientConfiguration(r.getSelectedDevices(),scriptName,scriptContent);
////		r.startDownload(r.getSelectedDevices(),torrentFilePath);
//		
//		r.checkPeers(new SSHManager(LOCALHOST, "volkan", "volkan5644"));
//		
//	}
//
//	private void checkPeers(SSHManager ssh) {
//
//		//TODO bu torrent id ile eşleşen torrent infosunun peer sayısı 0 a inerse tracker ı stop et
//		try {
//			SetupUtils.executeCommdGetResult(ssh.getIp(), ssh.getUsername(), ssh.getPassword(), null,null, "");
//		} catch (SSHConnectionException e) {
//			e.printStackTrace();
//		} catch (CommandExecutionException e) {
//			e.printStackTrace();
//		}
//		
//	}
//
//	private  ArrayList<SSHManager> getSelectedDevices() {
//		ArrayList<SSHManager> list = new ArrayList<SSHManager>();
////		list.add(new SSHManager("192.168.1.106","volkan", "volkan5644"));
//		list.add(new SSHManager("192.168.1.236","ahenk", "1"));
////		list.add(new SSHManager("192.168.1.151","caner", "caner5644"));
////		list.add(new SSHManager("192.168.1.191","emre", "agem5644"));
//		return list;
//	}
//	
//	private void torrentClientConfiguration(ArrayList<SSHManager> selectedDevices, String scriptName,String scriptContent) {
//
//		for (SSHManager ssh : selectedDevices) {
//			try {
//				ssh.connect();
//				//TODO 
//				
//				executeCommand(ssh,String.format(CMD_CREATE_SCRIPT, scriptContent,TMP,scriptName));
//				executeCommand(ssh,String.format(CMD_EXECUTION_PERMISSION, TMP+""+scriptName));
//				
//				executeCommand(ssh, CMD_KILL_ALL_DELUGED);
//				executeCommand(ssh, "deluged &");
//				
//				executeCommand(ssh, CMD_DELUGE_CONSOLE);
//				executeCommand(ssh, CMD_ACTIVATE_EXECUTE_PLUGIN);
//				
//				
//				String conf =String.format(EXECUTE_CONF_CONTENT, TMP,scriptName);
//				executeCommand(ssh, String.format(EXECUTE_CONF_EDIT, conf,EXECUTE_CONF_PATH));
//				
//				ssh.disconnect();
//				
//			} catch (SSHConnectionException e) {
//				e.printStackTrace();
//			} 
//		}
//	}
//
//	private void executeCommand(final SSHManager ssh,String command) {
//
//		try {
//			String a = ssh.execCommand(command,new IOutputStreamProvider() {
//				@Override
//				public byte[] getStreamAsByteArray() {
//					return (ssh.getPassword() + "\n").getBytes();
//				}
//			});
//			
//			System.out.println(a);
//		} catch (CommandExecutionException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void checkAndInstallPackage(List<SSHManager> list,Map<String, String> map) {
//
//		for (SSHManager ssh : list) {
//			
//			for (Map.Entry<String, String> entry : map.entrySet())
//			{
////				if(!installationControl(ssh,entry.getKey(),entry.getValue())){
//					installPackage(ssh,entry.getKey(),entry.getValue());
////				}
//			}
//		}
//	}
//
//
//	private void addRepository(SSHManager ssh, String repo) {
//		
//		try {
//			ssh.connect();
//			executeCommand(ssh, String.format(CMD_UPDATE_REPOSITORY, repo));
//			executeCommand(ssh, CMD_UPDATE);
//			
//		} catch (SSHConnectionException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private boolean repositoryControl(SSHManager ssh, String packageName, String version) {
//		
//		try {
//			return SetupUtils.packageExists(ssh.getIp(), ssh.getUsername(), ssh.getPassword(),ssh.getPort(), ssh.getPrivateKey(), packageName, version);
//		} catch (CommandExecutionException e) {
//			e.printStackTrace();
//		} catch (SSHConnectionException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	private void installPackage(SSHManager ssh, String packageName, String version) {
//
//		try {
//			SetupUtils.installPackage(ssh.getIp(), ssh.getUsername(), ssh.getPassword(),ssh.getPort(), ssh.getPrivateKey(), packageName, version);
//		} catch (SSHConnectionException e) {
//			e.printStackTrace();
//		} catch (CommandExecutionException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private  boolean installationControl(SSHManager ssh, String packageName, String version) {
//		
//		try {
//			return SetupUtils.packageInstalled(ssh.getIp(), ssh.getUsername(), ssh.getPassword(),ssh.getPort(), ssh.getPrivateKey(), packageName, version);
//		} catch (SSHConnectionException e) {
//			e.printStackTrace();
//		} catch (CommandExecutionException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//
//
//
//	private void startDownload(ArrayList<SSHManager> list, String torrentFilePath) {
//		
//		for (SSHManager ssh : list) {
//			
//			try {
//				ssh.connect();
//				ssh.copyFileToRemote(new File(torrentFilePath), TMP, false);
//				executeCommand(ssh, String.format(CMD_DELUGE_ADD_TORRENT,torrentFilePath));
//			
//			} catch (CommandExecutionException e) {
//				e.printStackTrace();
//			} catch (SSHConnectionException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//
//	private void seedThisTorrent(String announcedFilePath,String torrentFilePath) {
//
//		try {
//			
//		Runtime rt = Runtime.getRuntime();
//		
//		Process prConnect =rt.exec(CMD_DELUGED);
//		int exitValueConnect = prConnect.waitFor();
//		executionResult(exitValueConnect,prConnect);
//		
//		Process pr =rt.exec(new String[]{CMD_DELUGE_CONSOLE,String.format(CMD_DELUGE_CONSOLE_PARAMETERS,announcedFilePath,torrentFilePath)});
//		int exitValue = pr.waitFor();
//		executionResult(exitValue,pr);
//		
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} 
//	}
//
//	public static void executionResult(int exitValue, Process pr) {
//
//		BufferedReader reader=null;
//		
//		if(exitValue==0)
//			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//		else
//			reader = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
//
//		
//		StringBuilder versions = new StringBuilder();
//		String line = "";
//		try {
//			while ((line = reader.readLine()) != null) {
//				versions.append(line);
//			}
//				System.out.println(versions.toString());	
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private Map<String, String> getRequiredPackages() {
//		Map<String,String> map =  new HashMap<String, String>();
//		map.put("deluged", "1.3.10-3");
//		map.put("deluge-console", "1.3.10-3");
//		map.put("deluge-common", "1.3.10-3");
//		map.put("python-libtorrent", "0.16.18-1");
//		return map;
//	}
//}