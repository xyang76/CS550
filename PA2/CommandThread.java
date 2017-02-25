package CS550.iit;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * 
 * @author Xincheng Yang
 * @version 1.0
 *	
 * This is an independent thread to handle user input. 
 * Properties : 
 *	Peer : a peer object.
 *	scanner : an input scanner.
 * Methods:
 *	config : load a config file [Config.java].
 *	register : dynamically add a file to share to other peers[Peer.java].
 *	query : query a file from other peers [Query.java].
 *	obtain : obtain a file from other peers [Peer.java].
 *	split : split a string command into several args.
 *	help : output a help information.
 */
public class CommandThread extends Thread{
	private Peer p;
	private Scanner sc;
	
	//Command string.
	public static final String COMMAND_DETAIL_STRING = "\nCommand detail:\n" +
			" $ config [filepath] -- Load a file.\n" +
			" $ register [filepath] -- Register a file or a directory.\n" +
			" $ query [filename] -- Query a file from other peers. \n" +
			" $ obtain [resultnumber] [savepath] -- Obtain a file from peer. \n" +
			" $ help -- get help and examples. \n" +
			" $ exit -- exit and shutdown file share system.";
	public static final String COMMAND_STRING = "$ ";
	
	public CommandThread(Peer p) {
		this.sc = new Scanner(System.in);
		this.p = p;
	}
	
	public void run(){
		this.setPeerPort();
		
		System.out.println(COMMAND_DETAIL_STRING);
		while(true){
			try {
				System.out.print(COMMAND_STRING);
				String cmd = sc.nextLine().trim();
				String s = cmd.substring(0, 1).toUpperCase();
				ArrayList<String> args = new ArrayList<String>();
				if(s.equals("C") && spilt(cmd, args, 2)){
					this.config(args.get(1));
				} else if(s.equals("R") && spilt(cmd, args, 2)){
					this.register(args.get(1));
				} else if(s.equals("Q") && spilt(cmd, args, 2)){
					this.query(args.get(1));
				} else if(s.equals("O") && spilt(cmd, args, 3)){
					this.obtain(p.getQueryhitResult(), args.get(1), args.get(2));
				} else if(s.equals("H")){
					this.help();
				} else if(s.equals("E")){
					this.exit();
					break;
				} else {
					System.out.print("Incorrect command or incorrect args.\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean spilt(String cmd, ArrayList<String> args, int argv){
		String[] str = cmd.split(" ");
		
		for(String s : str){
			if(!"".equals(s.trim())){
				args.add(s.trim());
			}
		}

		return args.size() == argv ? true : false;
	}
	
	public void setPeerPort(){
		String portString = "Please input local port to share files:";
		int port = -1;
		System.out.println(portString);
		while(true){
			String v = sc.nextLine().trim();
			try {
				port = Integer.parseInt(v);
				if(port > 0 && port < 65535){
					p.setLocalPort(port);
					break;
				}
				System.err.println("please input a legal port number(port number range: 0 - 65535):");
			} catch (Exception e) {
				System.err.println("Please input a number:");
			}
		}
	}
	
	public void config(String filepath){
		if(!Config.load(p, filepath)){
			System.err.println("Load config failed, file not exist!");
		} else {
			System.out.println("Load config success!");
		}
	}
	
	public void register(String filepath){
		if(!Config.addFile(p.getFileList(), filepath)){
			System.err.println(String.format("Register file %s failed, file not exist.", filepath));
		} else {
			System.out.println("Register success!");
		}
	}
	
	public void help(){
		System.out.println("----------------------------------------------------------");
		System.out.println("{config} : load a config file from local machine.");
		System.out.println("example: $ config D:\\config.txt");
		System.out.println("########### content of D:\\config.txt ###########");
		System.out.println("neighbor=192.168.1.2:8888        //neighbor ip and port");
		System.out.println("neighbor=111.222.111.222:7777    //neighbor ip and port");
		System.out.println("file=D:\\sample.txt      		 //statically share local file to other peers");
		System.out.println("file=D:\\share      		     //share local directory to other peers");
		System.out.println("##################### end #######################\n");
		System.out.println("----------------------------------------------------------");
		
		System.out.println("{register} : dynamically register a local file to other peers");
		System.out.println("example: $ register D:\\sample2.txt\n");
		System.out.println("----------------------------------------------------------");
		
		System.out.println("{query} : query a file from other peers");
		System.out.println("example: $ query sample3.txt\n");
		System.out.println("----------------------------------------------------------");
		
		System.out.println("{obtain} : obtain a file from other peers, make sure you already used query command first");
		System.out.println("################## query result #################");
		System.out.println("1. sample3.txt 192.168.1.2:7777");
		System.out.println("2. sample3.txt 192.168.1.3:8888");
		System.out.println("##################### end #######################");
		System.out.println("$ obtain 1 D:\\savehere.txt\n");
		System.out.println("----------------------------------------------------------");
		
		System.out.println("{exit} : exit and close file share system.");
		System.out.println("----------------------------------------------------------");
	}
	
	public ArrayList<FileEntry> query(String filename){
		return null;
	}
	
	public void exit(){
		p.close();
	}
	
	public void obtain(String ip, int port, String filename, String savepath){
		if(!p.obtain(ip, port, filename, savepath)){
			System.err.println(String.format("Obtain %s from %s failed!", filename, ip));
		} else {
			System.out.println(String.format("Obtain %s from %s success!", filename, ip));
		}
	}
	
	public void obtain(ArrayList<FileEntry> queryResult, String index, String savepath) {
		try {
			int i = Integer.parseInt(index);
			if(i < 1 || i > queryResult.size()){
				System.err.println("Incorrect index.");
			}
			FileEntry fe = queryResult.get(i-1);
			obtain(fe.getIP(), Integer.parseInt(fe.getPort()), fe.getFileName(), savepath);
		} catch (NumberFormatException e) {
			System.err.println("Incorrect index.");
		}
	}

}
