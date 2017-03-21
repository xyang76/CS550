package CS550.iit;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Consistency {
	public static boolean isPushApproach = true;	//Default approach is push
	public static int TTR = 3;						//Default TTR = 30s
	private static int TTL = 10;					//Default invalidation broadcast TTL = 10
	
	/**
	 * This is a poll request from the client
	 */
	public static boolean poll(FileEntry fe){
		// Please check if the peer is polling itself in control thread!
		boolean outdate = true;
		try {
			Socket s = new Socket(fe.getOriginIP(), Integer.valueOf(fe.getOriginPort()));
			PrintWriter writer = new PrintWriter(s.getOutputStream());
			DataInputStream input = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			writer.println("poll");
			writer.println(fe.toString());
			writer.flush();
			outdate = input.readBoolean();;
			if(!outdate){
				fe.setOutDate(outdate);
				Config.regTimer(fe);
			} 
			writer.close();
			s.close();
		} catch (Exception e) {
			System.out.print(String.format("Connection error, can not connect to %s:%d!\n%s ", fe.getOriginIP(), fe.getOriginPort()));
		}
		return outdate;
	}

	/**
	 * This is a poll handler in the server
	 * @param vector
	 * @param fe
	 * @return
	 */
	public static boolean doPoll(Vector<FileEntry> vector, FileEntry fe) {
		System.out.print(String.format("Received an poll request for file %s.\n%$ ", fe.getFileName()));
		for (FileEntry ite : vector) {
			if (ite.fileName.equals(fe.fileName)) {
				if (ite.getVersion() > fe.getVersion()){
					return true;
				}
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
			if (ite.fileName.equals(ite.fileName) && ite.originIP.equals(fe.originIP) && ite.originPort.equals(fe.originPort)){
				found = true;
				if (ite.getVersion() < fe.getVersion()) {
					System.out.print(String.format("Received an invalidate message and set the file %s outdate.\n%$ ", fe.getFileName()));
					ite.setOutDate(true);
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
						System.out.print(String.format("Connection error, can not connect to %s:%d!\n%s ", ite.getIP(), ite.getPort()));
					}
				}
			}
		}
		
	}

	/**
	 * Author: Xincheng Yang
	 *  update the version of exist file. this method only called by file listener.
	 * @param peer
	 * @param dir
	 * @param changed
	 */
	public static void updateVersion(Peer peer, String dir, String changed, boolean del) {
		ArrayList<FileEntry> rm = new ArrayList<FileEntry>();
		
		System.out.print(String.format("Detected file %s changed and the version is updated automatically.\n%$ ", changed));
		
		//Find file from fileEntry
		for(FileEntry fe : peer.getFileList()){
			if(fe.getDirectory().equals(dir) && fe.getFileName().equals(changed)){
				if(del){
					rm.add(fe);
				} else {
					fe.setVersion(fe.getVersion() + 1);
				}
				// If push approach, then broadcast to neighbors
				if(Consistency.isPushApproach == true){
					invalidate(peer, fe);
				}
				break;
			}
		}
		for(FileEntry fe : rm){
			peer.getFileList().remove(fe);
		}
	}


}
