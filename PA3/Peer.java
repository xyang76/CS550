package CS550.iit;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author Xincheng Yang
 * @version 1.0
 * 
 * This peer object is a socket server, for initialization, peer will create a new command thread and a file share thread.
 * Besides, peer will load default configuration file when it start.
 * 
 * Properties:
 *	localport: a port to share file to other peers.
 *	ss : a server socket, when a new file request come in, it will create a new thread to handle the file request.
 *	runable : determine whether the file share system should be run.  
 *	neighborList : an array list for neighbors.
 *	fileList : an array list for local shared files.
 *  
 * Methods:
 *	main : main entry for whole system.
 *	start : start a new peer, it will initialize setting, load configuration, 
 *			create a command thread and a file share thread.
 *	obtain : obtain a file from other peers. 
 *	getLocalIP : get local ip address.(Since java socket can not get correct ip address for local area network, 
 *			we use an ip address website to get correct ip address.)
 *
 * Updates for PA3:
 * 1. add a new property FileListener listener. 
 * 2. modified function "obtain" to update the shared file information.
 * 3. change local file list from ArrayList to Vector(because vector is thread safety). 
 */
public class Peer {
	//Local setting.
	private int localPort;
	private String localIP;
	private ServerSocket ss;
	private boolean runable;				
	private ArrayList<Address> neighborList;
	private Vector<FileEntry> fileList;
	private ArrayList<Query> queryList;
	private FileListener listener;

	/**
	 * Start a peer.
	 */
	public static void main(String[] args) {
		Peer p = new Peer();
		try {
			p.start();
		} catch (BindException e) {
			System.err.println("Port address already in use. please restart the system and select another port.");
			p.close();
		}  catch (SocketException e) {
			System.err.println("Port address already in use. please restart the system and select another port.");
			p.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() throws Exception{
		//Initialize local setting.
		this.initSetting();
		
		//Start a command GUI with a new thread.
		this.startGUI();
		
		//Load config.
		Config.load(this, null);
		
		//Start to listen socket request(each request has its own thread to handle them)
		this.startFileShare();
	}
	
	protected void initSetting(){
		this.runable = true;
		this.localIP = null;
		this.neighborList = new ArrayList<Address>();
		this.fileList = new Vector<FileEntry>();
		this.queryList = new ArrayList<Query>();
		this.listener = new FileListener(this);
	}
	
	protected void startGUI() {
		//Use a new thread to handle peer command.
		CommandThread cmd = new CommandThread(this);
		cmd.start();
	}
	
	protected void startFileShare() throws Exception {
		//Use a new thread to handle file share system.
		Socket s = null;
		this.ss = new ServerSocket(this.localPort);
		
		while (this.runable) {
			try {
				s = ss.accept();
				ShareThread st = new ShareThread(this, s);
				st.run();
			} catch (SocketException e) {
				if(this.runable){
					throw e;
				}
			}
		}
	}
	
	public void close() {
		this.runable = false;
		this.listener.close();
		try {
			if(this.ss != null){
				this.ss.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean obtain(String ip, int port, String filename, String path, String savepath){
		Socket socket;
		try {
			String filepath = String.format("%s/%s", path, filename).replace('\\', '/');
			socket = new Socket(ip, port);
			byte[] buf = new byte[4096];
			DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savepath)));
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			
			write.println("obtain");
			write.println(filepath);
			write.flush();
			
			int flag = input.readInt();
			if(flag == 2) {
				while (true) {
	                int read = 0;
	                read = input.read(buf);
	                
	                if (read == -1) {
	                    break;
	                }
	                file.write(buf, 0, read);
	            }
				
				file.close();
			}
			input.close();
			write.close();
			socket.close();
			
			return flag == 2 ? true : false;
		} catch (Exception e) {
			return false;
		} 
	}
	
	public String getLocalIP() {
		
		if(this.localIP != null) return this.localIP;
		
		try {
			URL url = new URL("http://checkip.amazonaws.com");
	        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	        this.localIP = br.readLine();
		} catch (Exception e) {
			System.out.println("Can not get local IP address.");
		}
        
        return this.localIP;
    }
	
	
	/****************************** Getter and Setter for local properties **********************************/
	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public boolean isRunable() {
		return runable;
	}

	public void setRunable(boolean runable) {
		this.runable = runable;
	}

	public ArrayList<Address> getNeighborList() {
		return neighborList;
	}

	public void setNeighborList(ArrayList<Address> neighborList) {
		this.neighborList = neighborList;
	}

	public Vector<FileEntry> getFileList() {
		return fileList;
	}

	public void setFileList(Vector<FileEntry> fileList) {
		this.fileList = fileList;
	}
	
	public ArrayList<Query> getQueryList() {
		return queryList;
	}

	public void setQueryList(ArrayList<Query> queryList) {
		this.queryList = queryList;
	}
	
	public FileListener getListener() {
		return listener;
	}

	public void setListener(FileListener listener) {
		this.listener = listener;
	}
	
	/****************************** Getter and Setter for local properties **********************************/
}
