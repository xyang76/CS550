package CS550.iit;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * @author Yi Zhang, Xincheng Yang
 * @version 1.0
 * 
 * 	This java class is a file object to transmit file information between peer and server.
 * 
 */
public class Query {
	private Peer peer;
	private String filename;
	private ArrayList<FileEntry> queryhitResult;
	private QueryHitEvent queryHitEvent;	// a fake event to listen time elapse from query start to query hit.
	
	public Query(Peer peer){
		this.peer = peer;
	}
	
	public void startQuery(final String filename){
		// For every query, peer will have a new query result.
		this.filename = filename;
		
		// Initialize query hit result
		this.queryhitResult = new ArrayList<FileEntry>();
		
		// Add it to queryList.
		peer.getQueryList().add(this);
		
		// Do query with a new thread
		new Thread(){
			public void run(){
				// Initialize TTL
				int TTL = 10;
				
				// Generate a messageId
				String messageId = String.format("%d-%s-%d", (new Date()).getTime(), peer.getLocalIP(), peer.getLocalPort());
				
				// Do query
				doQuery(messageId, filename, TTL, new Address(peer.getLocalIP(), peer.getLocalPort()));
				
				// Sleep 30000ms
				try {
					Thread.currentThread().sleep(30000);
					if(queryhitResult.size() == 0){
						System.out.print(String.format("Query for file %s time out, no result found.\n$ ", filename));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void doQuery(String messageId, String filename, int TTL, Address source){
		this.searchLocalFiles(messageId, filename);
		if (TTL>0){
			this.broadcast(messageId, filename, TTL-1, source);
		}
	}
	
	public void searchLocalFiles(String messageId, String filename){
		ArrayList<FileEntry> fl = peer.getFileList();
		
		// If hit a file, then go to doQueryHit.
		for(FileEntry f : fl){
			if (f.getFileName().equals(filename)){
				this.doQueryHit(f, messageId);
			}
		}
	}
	
	public void broadcast(String messageId, String filename, int TTL, Address source){
		ArrayList<Address> neighbor = peer.getNeighborList();
		for (Address n : neighbor){
			if (!n.equals(source)){
				try{
					Socket s = new Socket(n.getIP(), n.getPort());
					PrintWriter write = new PrintWriter(s.getOutputStream());
					write.println("query");
					write.println(messageId+"-"+s.getLocalAddress().getHostAddress()+" "+filename+" "+TTL+" "+peer.getLocalIP()+" "+peer.getLocalPort());
					write.flush();
					
					write.close();
					s.close();
				}
				catch(Exception e)
				{
					System.out.println(String.format("Connection error, can not connect to %s:%d!", n.getIP(), n.getPort()));
				}
			}
		}
	}

	public void doQueryHit(FileEntry f, String messageId){
		// Open a socket connection to inform source peer.
		String[] message = messageId.split("-");
		Socket s = null;
		try{
			s = new Socket(message[1], Integer.parseInt(message[2]));
		}
		catch(Exception e){
			try {
				s = new Socket(message[3], Integer.parseInt(message[2]));
			} catch (Exception e1) {
				System.out.println(String.format("Connection error, can not connect to %s:%s!", message[3], message[2]));
			}
		}
		
		try{
			PrintWriter write = new PrintWriter(s.getOutputStream());
			write.println("queryhit");
			write.println(f.toString());
			write.flush();
			
			write.close();
			s.close();
		}
		catch(Exception e){
			System.out.println(String.format("Connection error, can not connect to %s:%s!", message[1], message[2]));
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
				System.out.print(String.format("[%d] Queryhit %s in %s:%s %s\n$ ", queryhitResult.size(),
						fe.getFileName(), fe.getSourceIP(), fe.getSourcePort(), fe.getDirectory()));
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