package CS550.iit;

import java.util.Scanner;

public class CommandThread extends Thread{
	private Peer p;
	private Scanner sc;
	
	//Command string.
	public static final String COMMAND_DETAIL_STRING = "\nCommand detail:\n" +
			"  Config [filepath] -- Load a file.\n" +
			"  Register [filepath] -- Register a file or a directory.\n" +
			"  Query [filename] -- Query a file. \n" +
			"  Obtain [index] -- Obtain a file from peer. \n" +
			"  Exit -- exit and shutdown file share system.";
	public static final String COMMAND_STRING = "Please input a command:";
	
	public CommandThread(Peer p) {
		this.p = p;
	}
	
	public void run(){
		System.out.println(COMMAND_DETAIL_STRING);
	}
}
