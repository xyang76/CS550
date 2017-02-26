package CS550.iit;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.net.Socket;

/**
 * 
 * @author Yi Zhang
 * @version 1.0
 * 
 * 	This java class is a file object to transmit file information between peer and server.
 * 
 */
public class Query {
	private Peer p;
	private String filename;
	private ArrayList<FileEntry> queryhitResult;
	
	
	public Query(Peer peer){
		this.p = peer;
		this.queryhitResult = new ArrayList<FileEntry>();
		
	}
	
	public void startQuery(String messageID, String filename, String IP, int Port){
		// For every query, peer will have a new query result.
		this.filename = filename;
		
		// Initialize TTL
		int TTL = 10;
		
		// Do query
		this.doQuery(messageID, filename, TTL, new Address(IP, Port));
	}
	
	public void doQuery(String messageID, String filename, int TTL, Address source){
		
		this.searchLocalFiles(filename, source);
		if (TTL>0)
			this.broadcast(messageID, filename, TTL-1, source);
	}
	
	public void searchLocalFiles(String filename, Address source){
		ArrayList<File> fl = p.getFileList();
		
		// If hit a file, then go to doQueryHit.
		for(File f : fl){
			if (f.getName().equals(filename))
				this.doQueryHit(source);
				break;
		}
	}
	
	public void broadcast(String messageID, String filename, int TTL, Address source){
		ArrayList<Address> neighbor = p.getNeighborList();
		for (Address n : neighbor){
			if (!n.equals(source)){
				try{
					Socket s = new Socket(n.getIP(),n.getPort());
					PrintWriter write = new PrintWriter(s.getOutputStream());
					write.println("query");
					write.println(messageID+" "+filename+" "+TTL+" "+source.getIP()+" "+source.getPort());
					write.close();
					s.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void doQueryHit(Address source){
		// Open a socket connection to inform source peer.
		try{
			Socket s = new Socket(source.getIP(), source.getPort());
			PrintWriter write = new PrintWriter(s.getOutputStream());
			write.println("queryhit");
			write.println(filename+" "+p.getLocalIP()+" "+p.getLocalPort());
			write.close();
			s.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void endQueryHit(){
		
		// If file name are the same, and this is the original peer, then put data to peer - queryhitResult.
		/*
		if(!results.contains(fileEntry) && this.filename.equals(fileEntry.getFileName())){
			results.add(fileEntry);
		}
		*/
		// Then output a query hit information

	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	

	public ArrayList<FileEntry> getQueryhitResult() {
		return queryhitResult;
	}

	public void setQueryhitResult(ArrayList<FileEntry> queryhitResult) {
		this.queryhitResult = queryhitResult;
	}
}
