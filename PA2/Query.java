package CS550.iit;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
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
	private QueryHitEvent queryHitEvent;	// a fake event to listen time elapse from query start to query hit.
	
	public Query(Peer peer){
		this.p = peer;
		this.queryhitResult = new ArrayList<FileEntry>();
		peer.getQueryList().add(this);
	}
	
	public void startQuery(String filename){
		// For every query, peer will have a new query result.
		this.filename = filename;
		
		// Initialize TTL
		int TTL = 10;
		
		// Generate a messageId
		String messageId = String.format("%d-%s-%d", (new Date()).getTime(), p.getLocalIP(), p.getLocalPort());
				
		// Do query
		this.doQuery(messageId, filename, TTL, new Address(p.getLocalIP(), p.getLocalPort()));
	}
	
	public void doQuery(String messageId, String filename, int TTL, Address source){
		this.searchLocalFiles(messageId, filename);
		if (TTL>0){
			this.broadcast(messageId, filename, TTL-1, source);
		}
	}
	
	public void searchLocalFiles(String messageId, String filename){
		ArrayList<File> fl = p.getFileList();
		
		// If hit a file, then go to doQueryHit.
		for(File f : fl){
			if (f.getName().equals(filename)){
				this.doQueryHit(new File(f.getAbsolutePath()), messageId);
			}
		}
	}
	
	public void broadcast(String messageId, String filename, int TTL, Address source){
		ArrayList<Address> neighbor = p.getNeighborList();
		for (Address n : neighbor){
			if (!n.equals(source)){
				try{
					Socket s = new Socket(n.getIP(), n.getPort());
					PrintWriter write = new PrintWriter(s.getOutputStream());
					write.println("query");
					write.println(messageId+" "+filename+" "+TTL+" "+p.getLocalIP()+" "+p.getLocalPort());
					write.flush();
					
					write.close();
					s.close();
				}
				catch(Exception e)
				{
					System.out.println("Connection error!");
				}
			}
		}
	}

	public void doQueryHit(File f, String messageId){
		// Open a socket connection to inform source peer.
		try{
			String[] message = messageId.split("-");
			Socket s = new Socket(message[1], Integer.parseInt(message[2]));
			
			PrintWriter write = new PrintWriter(s.getOutputStream());
			write.println("queryhit");
			FileEntry fe = new FileEntry();
			fe.setIP(p.getLocalIP());
			fe.setPort(String.valueOf(p.getLocalPort()));
			fe.setFileName(f.getName());
			fe.setDirectory(f.getParent());
			write.println(fe.toString());
			write.flush();
			
			write.close();
			s.close();
		}
		catch(Exception e){
			System.out.println("Connection error!");
		}
	}
	
	public void endQueryHit(FileEntry fe, boolean print){
		// Add it to query hit result
		if(!queryhitResult.contains(fe)){
			queryhitResult.add(fe);
			
			// Then output a query hit information
			if(this.queryHitEvent != null){
				this.queryHitEvent.onQueryHit();
				return;
			}
			
			if(print){
				System.out.print(String.format("[%d] Queryhit %s in %s %s\n$ ", queryhitResult.size(),
						fe.getFileName(), fe.getIP(), fe.getDirectory()));
			}
		}
	}
	
	public void regQueryHitListener(QueryHitEvent queryHitEvent) {
		this.queryHitEvent = queryHitEvent;
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

	/**
	 * An fake listener interface to mimic java event.
	 * it is not a true java event, just mimic a java event for test time elapse.
	 * @author Xincheng Yang
	 *
	 */
	public interface QueryHitEvent{
		void onQueryHit();
	}
}