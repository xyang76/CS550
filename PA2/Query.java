package CS550.iit;

import java.util.ArrayList;

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
	
	public Query(Peer peer){
		this.p = peer;
	}
	
	public void query(String filename){
		// For every query, peer will have a new query result.
		p.setQueryhitResult(new ArrayList<FileEntry>());
		
		return;
	}
	
	// maybe you do not need it.
	public void broadcast(){
		
	}
	
	public void queryhit(){
		return;
	}
}
