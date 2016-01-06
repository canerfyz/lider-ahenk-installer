//package tr.org.pardus.mys.liderahenksetup.torrent.utils;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.InterfaceAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//import java.util.Enumeration;
//
//import com.turn.ttorrent.client.Client;
//import com.turn.ttorrent.client.SharedTorrent;
//import com.turn.ttorrent.tracker.TrackedTorrent;
//import com.turn.ttorrent.tracker.Tracker;
//
//public class TorrentUtil {
//
//	public static Tracker getTracker(String trackerIp, int trackerSocket,String announcedFilePath){
//		Tracker tracker = null;
//		try {
//
//			tracker = new Tracker(new InetSocketAddress(trackerIp,trackerSocket));
//
//		FilenameFilter filter = new FilenameFilter() {
//		@Override
//		public boolean accept(File dir, String name) {
//		    return name.endsWith(".torrent");
//		  }
//		};
//
//		for (File f : new File(announcedFilePath).listFiles(filter)) {
//		  tracker.announce(TrackedTorrent.load(f));
//		}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return tracker;
//	}
//	
//	public static Client getClient(String torrentFilePath,String downloadPath,Double maxDownRate,Double maxUpRate){
//
//			Client client=null;
//			try {
//				client = new Client(InetAddress.getLocalHost(),SharedTorrent.fromFile(new File(torrentFilePath),new File(downloadPath)));
//
//				if(maxDownRate!=null)
//					client.setMaxDownloadRate(maxDownRate);
//				
//				if(maxUpRate!=null)
//					client.setMaxUploadRate(maxUpRate);
//
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return client;
//	}
//	
//	public static String getMyIpAddres() {
//
//		String myIpAddress=null;
//    	Enumeration<NetworkInterface> netInterfaces;
//		try {
//			netInterfaces = NetworkInterface.getNetworkInterfaces();
//	
//    	while (netInterfaces.hasMoreElements()) {
//    		NetworkInterface iface = netInterfaces.nextElement();
//    		if (!iface.getName().contains("lo")) { 
//    			for (InterfaceAddress ifaceAddress : iface.getInterfaceAddresses()) {
//    				if (ifaceAddress.getNetworkPrefixLength() <= (short) 32) { 
//    					myIpAddress = ifaceAddress.getAddress().toString().substring(1);
//    				}
//    			}
//    		}
//    	}
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//		
//		return myIpAddress;
//	}
//	
//}
