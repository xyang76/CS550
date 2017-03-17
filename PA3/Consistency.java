package CS550.iit;

import java.io.File;
import java.util.ArrayList;

public class Consistency {
	public static boolean approach = true;		//Default approach is push
	public static int TTR = 30;					//Default TTR = 30s
	
	/**
	 * This is a poll request from the client
	 */
	public static void poll(FileEntry fe){
		
	}

	/**
	 * This is a poll handler in the server
	 * @param fileList
	 * @param fe
	 * @return
	 */
	public static boolean doPoll(ArrayList<FileEntry> fileList, FileEntry fe) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Broadcast the invalidate message to all neighbors
	 * @param neighborList
	 * @param fe
	 */
	public static void doInvalidate(ArrayList<Address> neighborList,
			FileEntry fe, Address source) {
		// TODO Auto-generated method stub
		
	}
}
