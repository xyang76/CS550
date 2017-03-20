package CS550.iit;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Consistency {
	public static boolean approach = true;		//Default approach is push
	public static int TTR = 30;					//Default TTR = 30s
	public static int TTL = 10;					//Default invalidation broadcast TTL = 10
	
	/**
	 * This is a poll request from the client
	 */
	public static void poll(FileEntry fe){
		// Please check if the peer is polling itself in control thread!
		try {
			Socket s = new Socket(fe.getOriginIP(), Integer.valueOf(fe.getOriginPort()));
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			writer.println("poll");
			writer.println(fe.toString());
			writer.flush();
			writer.close();
			s.close();
		} catch (Exception e) {
			System.out.println(String.format("Connection error, can not connect to %s:%d!", fe.getOriginIP(), fe.getOriginPort()));
			
		}
	}

	/**
	 * This is a poll handler in the server
	 * @param fileList
	 * @param fe
	 * @return
	 */
	public static boolean doPoll(ArrayList<FileEntry> fileList, FileEntry fe) {
		// TODO Auto-generated method stub
		for (FileEntry ite : fileList) {
			if (ite.fileName.equals(fe.fileName)) {
				if (ite.getVersion() > fe.getVersion())
					return true;
				else return false;
			}
		}
		// If file not found(deleted on origin), return true(out-dated).
		return true;
	}
	
	
	public static void invalidate(Peer peer, FileEntry fe) {
		for (Address ite : peer.getNeighborList()) {
			try {
				Socket s = new Socket(ite.getIP(), ite.getPort());
				PrintWriter writer = new PrintWriter(s.getOutputStream());
				writer.println("invalidate");
				writer.println(fe.toString());
				writer.println(peer.getLocalIP() + " " + peer.getLocalPort() + " " + (TTL));
				writer.flush();
				writer.close();
				s.close();
			} catch (Exception e) {
				System.out.println(String.format("Connection error, can not connect to %s:%d!", ite.getIP(), ite.getPort()));
			}
		}
	}
	/**
	 * Broadcast the invalidate message to all neighbors
	 * @param neighborList
	 * @param fe
	 */
	public static void doInvalidate(Peer peer, FileEntry fe, Address source, int TTL) {
		//Checking for local file entry
		boolean outDate = false;
		boolean found = false;
		for (FileEntry ite : peer.getFileList()) {
			if ((ite.fileName.equals(ite.fileName)) && (ite.originIP.equals(ite.originIP)) && (ite.originPort.equals(ite.originPort))) {
				found = true;
				if (ite.getVersion() < fe.getVersion()) {
					outDate = true;
					ite.setOutDate(outDate);
					break;
				}
			}
		}
		
		// Terminate old invalidation broadcast
		if (found && (!outDate)) return;
		
		//Broadcast invalidation with reduced TTL
		if (TTL > 1) {
			for (Address ite : peer.getNeighborList()) {
				if (!ite.equals(source)) {
					try {
						Socket s = new Socket(ite.getIP(), ite.getPort());
						PrintWriter writer = new PrintWriter(s.getOutputStream());
						writer.println("invalidate");
						writer.println(fe.toString());
						writer.println(peer.getLocalIP() + " " + peer.getLocalPort() + " " + (TTL - 1));
						writer.flush();
						writer.close();
						s.close();
					} catch (Exception e) {
						System.out.println(String.format("Connection error, can not connect to %s:%d!", ite.getIP(), ite.getPort()));
					}
				}
			}
		}
		
	}
}
