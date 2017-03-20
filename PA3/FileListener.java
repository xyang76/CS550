package CS550.iit;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * @version 1.0
 * @author Xincheng Yang
 * 
 * A file listener to listen the file change events.(PA3)
 * 
 * Properties:
 * 	watchTable : a hashtable contains <dir, watchthread>.
 *  peer : this peer object.
 * Methods:
 * 	regFileListener : register a file or a directory.
 * Inner class WatchThread: a thread to listen a specified directory.
 * 
 */
public class FileListener {
	private Hashtable<String, WatchThread> watchTable;
	private Peer peer;
	
	public FileListener(Peer p){
		this.peer = p;
		watchTable = new Hashtable<String, WatchThread>();
	}
	
	public void regFileListener(File f){
		if(!f.exists()) return;
		
		try {
			String dir = null;
			if(f.isDirectory()){
				dir = f.getAbsolutePath();
			} else {
				File absf = new File(f.getAbsolutePath());
				dir = absf.getParent();
			}
			
			if(watchTable.containsKey(dir)){
				if(f.isDirectory()){
					watchTable.get(dir).isDirectory = true;
				} else if(!watchTable.get(dir).isDirectory){
					watchTable.get(dir).filelist.add(f.getName());
				}
			} else {
				WatchService ws = FileSystems.getDefault().newWatchService();
				WatchThread wt = new WatchThread(ws, peer);
				
				wt.dir = dir;
				Paths.get(dir).register(ws,
				        StandardWatchEventKinds.ENTRY_CREATE, 
				        StandardWatchEventKinds.ENTRY_MODIFY, 
				        StandardWatchEventKinds.ENTRY_DELETE);
				
				if(f.isDirectory()){
					wt.isDirectory = true;
				} else {
					wt.filelist.add(f.getName());
				}
				
				watchTable.put(dir, wt);
				wt.start();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		Enumeration<WatchThread> e = watchTable.elements();
		
		while(e.hasMoreElements()){
			WatchThread wt = e.nextElement();
			wt.runable = false;
			try {
				wt.ws.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	class WatchThread extends Thread{
		private WatchService ws;
		private Peer peer;
		private boolean runable;
		private boolean isDirectory;
		private ArrayList<String> filelist;
		private String dir;
		
		public WatchThread(WatchService ws, Peer peer){
			this.ws = ws;
			this.peer = peer;
			this.runable = true;
			this.filelist = new ArrayList<String>();
		}
		
		public void run(){
			while (this.runable) {
	            WatchKey key;
				try {
					key = ws.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						String changed = event.context().toString();
						String eventString = event.kind().toString();
						if(isDirectory || filelist.contains(changed)){
							if(eventString.equals("ENTRY_CREATE")){
								String path = Paths.get(dir).resolve(changed).toString();
								Config.addLocalFile(peer, path);
							} else if(eventString.equals("ENTRY_DELETE")){
								Consistency.updateVersion(peer, dir, changed, true);
							} else {
								Consistency.updateVersion(peer, dir, changed, false);
							}
						}
						
		            }
		            if (!key.reset()) {
		                break;
		            }
				} catch (Exception e) {
					this.runable = false;
				}
	            
	        }
			try {
				this.ws.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
