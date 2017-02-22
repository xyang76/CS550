package CS550.iit;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * @author Xincheng Yang
 * @version 1.0
 * 
 * 
 */
public class Peer {
	//Local setting.
	private int localPort;
	private ServerSocket ss;
	public boolean runable;				//Determine whether the file share system should be run. 

	/**
	 * Start a peer.
	 */
	public static void main(String[] args) {
		Peer p = new Peer();
		try {
			p.start();
		} catch (BindException e) {
			System.err.println("Port address already in use. please restart the system and select another port.");
			p.closeFileShare();
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
				ShareThread st = new ShareThread(s);
				st.run();
			} catch (SocketException e) {
				if(this.runable){
					throw e;
				}
			}
		}
	}
	
	private void closeFileShare() {
		this.runable = false;
		try {
			this.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
