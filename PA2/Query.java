package CS550.iit;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

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
	
	// For all queries, they should have a shared message hash table. So this property is static.
	private static Hashtable<String, Address> messages = new Hashtable<String, Address>();
	
	public Query(Peer peer){
		this.p = peer;
		this.queryhitResult = new ArrayList<FileEntry>();
	}
	
	public void startQuery(String filename){
		// For every query, peer will have a new query result.
		this.filename = filename;
		
		// Initialize TTL
		int TTL = 10;
		
		// Generate a messageID(IP, port, sequenceNumber)
		
		
		// Do query
		this.doQuery("", filename, TTL, new Address(p.getLocalIP(), p.getLocalPort()));
	}
	
	public void doQuery(String messageID, String filename, int TTL, Address source){
		if(messages.containsKey(messageID)) return;
		
		messages.put(messageID, source);
		this.searchLocalFiles();
		this.broadcast();
	}
	
	public void searchLocalFiles(){
		ArrayList<File> fl = p.getFileList();
		
		// If hit a file, then go to doQueryHit.
		for(File f : fl){
			
		}
	}
	
	public void broadcast(){
		ArrayList<Address> neighbor = p.getNeighborList();
	}

	public void doQueryHit(){
		// Open a socket connection to inform source peer.
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
