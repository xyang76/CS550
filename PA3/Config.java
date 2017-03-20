package CS550.iit;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


/**
 * 
 * @author Xincheng Yang
 * @version 2.0
 * 
 * Statically load a configuration file.
 * Default configuration file is "config.txt" in the same path of your executable Peer.jar directory.
 * If you do not have default config file, you can dynamically load config file with command:
 *	$ config [config file path]
 * Our program only recognize 'neighbor', 'file', 'approach' and 'ttr' properties in config file.
 * in this example, ttr = 30 means the time to refresh is 30 seconds.
 * 
 * ---------------------- an example of configuration file -------------------
 * neighbor=192.168.1.2:8080
 * neighbor = 192.168.1.3:888
 * file = D:\sample.txt 
 * file=D:\share 
 * file = notexist.txt
 * approach = push
 * ttr = 30  			
 * 
 * Updates for PA3:
 * 1. add a file listener for file change events in method 	"addLocalFile".
 * 2. add a new function "setApproach".
 * 3. add a new function "setTTR".				
 * ------------------------------------------------------------------------
 */
public class Config {
	private static final String DEFAULT_PATH = "config.txt";
	
	public static boolean load(Peer peer, String filepath) {
		if(filepath == null || "".equals(filepath)){
			filepath = DEFAULT_PATH;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(filepath))));
			String value = null;
			while((value = reader.readLine()) != null){
				int index = value.indexOf('=');
				if(index > 0){
					String type = value.substring(0, index).trim().toLowerCase();
					if("neighbor".equals(type)){
						addNeighbor(peer.getNeighborList(), value.substring(index + 1).trim());
					} else if("file".equals(type)){
						addLocalFile(peer, value.substring(index+1).trim());
					} else if("approach".equals(type)){
						setApproach(value.substring(index+1).trim().toLowerCase());
					} else if("ttr".equals(type)){
						setTTR(value.substring(index+1).trim().toLowerCase());
					}
				}
			}
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void setApproach(String apporach) {
		if("push".equals(apporach)){
			Consistency.isPushApproach = true;
		} else if("pull".equals(apporach)){
			Consistency.isPushApproach = false;
		}
		
	}

	private static void setTTR(String TTRStr) {
		int TTR = 30;			// Default is 30s.
		try {
			TTR = Integer.parseInt(TTRStr);
		} catch (Exception e) {
			System.err.println("TTR is not currect in configuration file, set default TTR = 30s");
		}
		Consistency.TTR = TTR;
	}

	public static boolean addNeighbor(ArrayList<Address> neighborList, String neighbor){
		Address n = Address.getAddress(neighbor);
		if(n == null || neighborList.contains(n)) return false;
		
		neighborList.add(n);
		return true;
	}
	
	public static boolean addLocalFile(Peer peer, String filepath){
		Vector<FileEntry> fileList = peer.getFileList();
		File file = new File(filepath);
		Address addr = new Address(peer.getLocalIP(), peer.getLocalPort());
		
		if(!file.exists())	return false;
		
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				fileList.add(new FileEntry(f, addr));
			}
		} else {
			fileList.add(new FileEntry(file, addr));
		}
		
		//Reg fileListener
		peer.getListener().regFileListener(file);
		
		return true;
	}
	
	public static boolean addRemoteFile(Peer peer, FileEntry rmfile, String savePath){
		File file = new File(savePath);
		if(!file.exists()) return false;
		
		File f = new File(file.getAbsolutePath());
		rmfile.setDirectory(f.getParent());
		rmfile.setFileName(f.getName());
		rmfile.setSourceIP(peer.getLocalIP());
		rmfile.setSourcePort(String.valueOf(peer.getLocalPort()));
		
		peer.getFileList().add(rmfile);
		
		//Reg fileListener
		//peer.getListener().regFileListener(f);
		
		//Reg timer
		if(Consistency.isPushApproach == false){
			regTimer(rmfile);
		}
		return true;
	}

	public static void regTimer(FileEntry f) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
	        public void run() {
	        	f.setOutDate(true);
	            timer.cancel(); 
	        }
	    }, Consistency.TTR * 1000);
	}
	
	
	
}
