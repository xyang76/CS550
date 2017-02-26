package CS550.iit;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
 */
public class Peer {
	//Local setting.
	private int localPort;
	private ServerSocket ss;
	private boolean runable;				
	private ArrayList<Address> neighborList;
	private ArrayList<File> fileList;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException{
		//Initialize local setting.
		this.initSetting();
		
		//Load config.
		Config.load(this, null);
		
		//Start a command GUI with a new thread.
		this.startGUI();
				
		//Start to listen socket request(each request has its own thread to handle them)
		this.startFileShare();
	}
	
	private void initSetting(){
		this.runable = true;
		this.neighborList = new ArrayList<Address>();
		this.fileList = new ArrayList<File>();
	}
	
	private void startGUI() {
		//Use a new thread to handle peer command.
		CommandThread cmd = new CommandThread(this);
		cmd.start();
	}
	
	private void startFileShare() throws IOException {
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
		try {
			this.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean obtain(String ip, int port, String filename, String savepath){
		Socket socket;
		try {
			socket = new Socket(ip, port);
			byte[] buf = new byte[4096];
			DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savepath)));
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			
			write.println("obtain");
			write.println(filename);
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
		String ip = null;
		try {
			URL url = new URL("http://checkip.amazonaws.com");
	        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	        ip = br.readLine();
		} catch (Exception e) {
			System.out.println("Can not get local IP address.");
		}
        
        return ip;
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

	public ArrayList<File> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<File> fileList) {
		this.fileList = fileList;
	}

	/****************************** Getter and Setter for local properties **********************************/
}
