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
	private Hashtable<String, Address> messages;
	
	public Query(Peer peer){
		this.p = peer;
	}
	
	public void startQuery(String filename){
		// For every query, peer will have a new query result.
		p.setQueryhitResult(new ArrayList<FileEntry>());
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
	}
	
	public void broadcast(){
		ArrayList<Address> neighbor = p.getNeighborList();
	}

	public void doQueryHit(){
		// Open a socket connection to inform source peer.
	}
	
	public void endQueryHit(){
		ArrayList<FileEntry> results = p.getQueryhitResult();
		// If file name are the same, and this is the original peer, then put data to peer - queryhitResult.
		/*
		if(!results.contains(fileEntry) && this.filename.equals(fileEntry.getFileName())){
			results.add(fileEntry);
		}
		*/
		// Then output a query hit information

	}
}
