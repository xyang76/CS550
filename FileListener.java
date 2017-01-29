package cs550.iit;

import java.io.IOException;
import java.nio.file.*;

public class FileListener {
	private WatchService ws;
	private WatchThread wt;
	private Path folderpath;
	private String file;
	
	public FileListener(Peer peer, String folderpath, String file){
		try {
			this.folderpath = Paths.get(folderpath);
			this.file = file;
			ws = FileSystems.getDefault().newWatchService();
			if(this.file == null){
				this.folderpath.register(this.ws,
			        StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
			} else {
				this.folderpath.register(this.ws, StandardWatchEventKinds.ENTRY_DELETE);
			}
			wt = new WatchThread(ws, peer);
			wt.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			this.ws.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class WatchThread extends Thread{
		private WatchService ws;
		private Peer peer;
		
		public WatchThread(WatchService ws, Peer peer){
			this.ws = ws;
			this.peer = peer;
		}
		
		public void run(){
			while (true) {
	            WatchKey key;
				try {
					key = ws.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						String changed = event.context().toString();
						String eventString = event.kind().toString();
						if(eventString.equals("ENTRY_CREATE")){
							peer.register(changed);
						} else if(eventString.equals("ENTRY_DELETE")){
							if(file == null || changed.equals(file)){
								peer.delete(changed);
							}
						}
		            }
		            if (!key.reset()) {
		                break;
		            }
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	            
	        }
		}
	}
}
