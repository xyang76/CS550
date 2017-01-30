package CS550.iit;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.text.Format;
import java.util.ArrayList;
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
	private ArrayList<FileListener> fList;
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
		
		//Start a command GUI with a new thread.
		this.startGUI();
				
		//Start to listen socket request(each request has its own thread to handle them)
		this.startFileShare();
	}
	
	public void register(String filepath){
		Socket socket;
		File file = new File(filepath);
		FileListener fl;
		
		if(!file.exists()){
			System.err.println("Error: File do not exist! Please input a correct file with file path!");
			return;
		}
		if(file.isDirectory()){
			fl = new FileListener(this, file.getAbsolutePath(), null);
		} else {
			fl = new FileListener(this, file.getParent(), file.getName());
		}
		fList.add(fl);
		
		try {
			socket = new Socket(this.server, this.serverPort);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			FileEntry fe = new FileEntry();
			
			fe.setIP(Util.getIP());
			fe.setPort(String.valueOf(this.localPort));
			
			write.println("REGISTER");
			if(file.isDirectory()){
				fe.setDirectory(file.getAbsolutePath());
				File[] filelist = file.listFiles();
				for(File f : filelist){
					fe.setFileName(f.getName());
					write.println(fe.toString());
				}			
			} else {
				fe.setDirectory(file.getParent());
				fe.setFileName(file.getName());
				write.println(fe.toString());
			}
			write.flush();
			
			System.out.println("Register success!");
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void delete(String file, String dir) {
		Socket socket;
		
		try {
			socket = new Socket(this.server, this.serverPort);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			
			write.println("DELETE");
			write.println(Util.getIP());
			write.println(this.localPort);
			write.println(file);
			write.println(dir);
			write.flush();
			
			System.out.println("Update success!");
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public ArrayList<FileEntry> lookup(String filename){
		Socket socket;
		ArrayList<FileEntry> file = new ArrayList<FileEntry>();
		try {
			socket = new Socket(this.server, this.serverPort);
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8")); 
			String next;
			
			write.println("LOOKUP");
			write.println(filename);
			write.flush();
			next = input.readLine();
			while(next != null){
				System.out.println(next);
				FileEntry fe = new FileEntry(next);
				file.add(fe);
				next = input.readLine();
			}
			
			input.close();
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public void peerRetrieve(String ip, int port, String filename, String savepath){
		Socket socket;
		try {
			socket = new Socket(ip, port);
			byte[] buf = new byte[4096];
			DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savepath)));
			PrintWriter write = new PrintWriter(socket.getOutputStream());
			
			write.println(filename);
			write.flush();
			
			while (true) {
                int read = 0;
                read = input.read(buf);
                
                if (read == -1) {
                    break;
                }
                file.write(buf, 0, read);
            }
			System.out.println("Retrieve file from " + ip + " success!");
			
			file.close();
			input.close();
			write.close();
			socket.close();
		} catch (UnknownHostException e) {
			System.err.println("Connect to peer has been failed. Probably peer system already closed this port.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void initSetting(){
		this.sc = new Scanner(System.in);
		this.fList = new ArrayList<FileListener>();
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
			for(FileListener fl : fList){
				fl.close();
			}
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
				try {
					System.out.println(COMMAND_STRING);
					String command = sc.nextLine().trim();
					String s = command.substring(0, 1).toUpperCase();
					if (s.equals("R")) {
						System.out.println("Please input the file or directory you want to register to server: ");
						String file = sc.nextLine();
						p.register(file);
					} else if (s.equals("L")) {
						System.out.println("Please input the file name: ");
						String file = sc.nextLine();
						ArrayList<FileEntry> list = p.lookup(file);
						switch (list.size()) {
						case 0:
							System.out.println("No such file exist in the server.");
							break;
						default:
							System.out.println("File results:");
							for (int i = 0; i < list.size(); i++) {
								FileEntry fe = list.get(i);
								System.out.println(String.format("%d: %s in %s", i + 1, fe.getFileName(), fe.getIP()));
							}
							System.out.println("Choose an index to retrieve, or type 'C' to cancel:");
							String op = sc.nextLine().trim();
							if (!"".equals(op) && !"C".equals(op.substring(0, 1).toUpperCase())) {
								System.out.println("Please input the save path of the file:");
								String savepath = sc.nextLine();
								int index = Util.getInt(op);
								if(index > 0 && index <= list.size()){
									FileEntry fe = list.get(index - 1);
									String absolutepath = Paths.get(fe.getDirectory()).resolve(fe.getFileName()).toString();
									p.peerRetrieve(fe.getIP(), Util.getInt(fe.getPort()), absolutepath, savepath);
								}
							}
							break;
						}
					} else if (s.equals("P")) {
						System.out.println("Please input peer address:");
						String address = sc.nextLine();
						System.out.println("Please input peer port:");
						int port = Util.getInt(sc.nextLine());
						System.out.println("Please input the file absolute path: ");
						String file = sc.nextLine();
						System.out.println("Please input the save path of the file: ");
						String savepath = sc.nextLine();
						p.peerRetrieve(address, port, file, savepath);
					} else if (s.equals("E")) {
						p.runable = false;
						p.closeFileShare();
						break;
					} else {
						System.err.println("The command is incorrect.");
					} 
				} catch (StringIndexOutOfBoundsException e) {
					System.err.println("Please input anything!");
				}
			}
		}
	}
	
	class ServiceThread extends Thread{
		Socket socket;
		BufferedReader input; 
		DataOutputStream output;
		String filepath;
	    
		public ServiceThread(Socket s) throws IOException{
			this.socket = s;
			input = new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8")); 
			output = new DataOutputStream(s.getOutputStream());
		}
		public void run(){
			try {
				this.filepath = input.readLine().replace('\\', '/');
				System.out.println("\nA new request for file:[" + this.filepath + 
						"] from:" + socket.getRemoteSocketAddress());
				
				File fi = new File(this.filepath);
				DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(this.filepath)));
				
                byte[] buf = new byte[4096];

                while (true) {
                    int read = 0;
                    if (dis != null) {
                        read = dis.read(buf);
                    }

                    if (read == -1) {
                        break;
                    }
                    output.write(buf, 0, read);
                }
                output.flush();
                
                dis.close();
				this.input.close();
				this.output.close();
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
