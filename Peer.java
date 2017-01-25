package cs550.iit;

import java.io.*;
import java.net.*;
import java.text.Format;
import java.util.Scanner;

/**
 * @author Xincheng Yang
 * @version 1.0
 * 
 * 1.	This is peer system to share a file and request information for server.
 * 2.	Peer system have a command thread to handle user command input.
 * 3.	For each socket request, peer will initialize a new thread to handle 
 * 		the socket request and transfer its file to another peer.
 * 4. 	Inner class 'CommandThread' and 'ServiceThread' contain the detail of how to handle the request.
 * 5. 	Public functions 'peerRetrive', 'register', 'lookup' contain the detail of the peer functionalities.
 */
public class Peer {
	//Local setting.
	private String server;
	private int serverPort;
	private int localPort;
	private Scanner sc;
	private ServerSocket ss;
	public boolean runable;				//Determine whether the file share system should be run. 
	
	//Command string.
	public static final String COMMAND_DETAIL_STRING = "\nCommand detail:\n" +
			"  Register/R: register a file to server.\n" +
			"  Lookup/L: find a file from server.\n" +
			"  PeerRetrieve/P: get a file from another peer. \n" +
			"  Exit/E: exit and shutdown file share system.";
	public static final String COMMAND_STRING = "Please input a command:";

	/**
	 * Start a peer.
	 */
	public static void main(String[] args) {
		Peer p = new Peer();
		try {
			p.start();
		} catch (Exception e) {
			System.out.println("An error occur:");
			e.printStackTrace();
		}
	}
	
	public void start() throws IOException{
		//Initialize local setting.
		this.initSetting();
		
		//Start a command GUI with a new thread.
		this.startGUI();
				
		//Start to listen socket request(each request has its own thread to handle them)
		this.startFileShare();
	}
	
	public void register(String fileName){
		Socket socket;
		try {
			socket = new Socket(this.server, this.serverPort);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			write.println(fileName);
			write.flush();
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void lookup(String fileName){
		Socket socket;
		try {
			socket = new Socket(this.server, this.serverPort);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			write.println(fileName);
			write.flush();
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void peerRetrive(String ip, int port, String fileName){
		Socket socket;
		try {
			socket = new Socket(ip, port);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			write.println(fileName);
			write.flush();
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initSetting(){
		this.sc = new Scanner(System.in);
		this.runable = true;
		
		System.out.println("Peer starting...");
		System.out.println("Please input server ip address:");
		this.server = sc.nextLine().trim();
		System.out.println("Please input server port:");
		this.serverPort = Util.getInt(sc.nextLine().trim());
		System.out.println("Please input local port to share files:");
		this.localPort = Util.getInt(sc.nextLine().trim());
		System.out.println("Peer successfully initialized!");
	}
	
	private void startGUI() {
		//Use a new thread to handle peer command.
		CommandThread cmd = new CommandThread(this);
		cmd.start();
	}
	
	private void startFileShare() throws IOException{
		Socket s = null;
		this.ss = new ServerSocket(this.localPort);
		
		while (this.runable) {
			try {
				s = ss.accept();
				ServiceThread st = new ServiceThread(s);
				st.run();
			} catch (SocketException e) {
				if(this.runable){
					throw e;
				}
			}
		}
	}
	
	private void closeFileShare(){
		try {
			this.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class CommandThread extends Thread{
		Peer p;
		
		public CommandThread(Peer p) {
			this.p = p;
		}
		
		public void run(){
			System.out.println(COMMAND_DETAIL_STRING);
			while (true) {
				System.out.print(COMMAND_STRING);
				String command = sc.nextLine().trim();
				String s = command.substring(0, 1).toUpperCase();
				if(s.equals("R")){
					System.out.println("Please input the file name: ");
					String file = sc.nextLine();
					p.register(file);
				} else if(s.equals("L")){
					System.out.println("Please input the file name: ");
					String file = sc.nextLine();
					p.lookup(file);
				} else if(s.equals("P")){
					System.out.println("Please input peer address:");
					String address = sc.nextLine();
					System.out.println("Please input peer port:");
					int port = Util.getInt(sc.nextLine());
					System.out.println("Please input the file name: ");
					String file = sc.nextLine();
					p.peerRetrive(address, port, file);
				} else if(s.equals("E")){
					p.runable = false;
					p.closeFileShare();
					break;
				} else {
					System.out.println("The command is incorrect.");
				}
			}
		}
	}
	
	class ServiceThread extends Thread{
		Socket socket;
	    BufferedReader input; 
	    PrintWriter output;
	    String filepath;
	    
		public ServiceThread(Socket s) throws IOException{
			this.socket = s;
			input = new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8")); 
		}
		public void run(){
			try {
				this.filepath = input.readLine();
				System.out.println("\nA new request for file:[" + this.filepath + 
						"] from:" + socket.getRemoteSocketAddress());
				this.input.close();
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
