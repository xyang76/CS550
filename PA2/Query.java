package CS550.iit;

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
		this.doQuery("", filename, TTL);
	}
	
	public void doQuery(String messageID, String filename, int TTL){
		this.searchLocalFiles();
		this.broadcast();
	}
	
	public void searchLocalFiles(){
		
	}
	
	public void broadcast(){
		
	}
	
	public void endQueryHit(){
		
	}
	
	public void doQueryHit(){
		
	}
}
