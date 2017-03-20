package CS550.iit;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author Xincheng Yang, Yi Zhang
 * @version 1.0
 *
 * A thread to share file to other peers.
 * 
 * Methods:
 * 	doQuery: query a file and propagate to other peers.
 * 	doSendFile: send a file to another peer.
 */
public class ShareThread extends Thread{
	Peer peer;
	Socket socket;
	BufferedReader input; 
	DataOutputStream output;
	ArrayList<String> messages = new ArrayList<String>();
	
	public ShareThread(Peer p, Socket s) throws IOException{
		this.peer = p;
		this.socket = s;
		input = new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8")); 
		output = new DataOutputStream(s.getOutputStream());
	}
	
	public void run(){
		try {
			String req = input.readLine();
			if("obtain".equals(req)){
				doSendFile();
			} else if("query".equals(req)){
				doQuery();
			} else if("queryhit".equals(req)){
				doQueryHit();
			} else if("invalidate".equals(req)){
				doInvalidate();
			} else if("poll".equals(req)){
				doPoll();
			}
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	private void doPoll() throws IOException {
		// If the approach is push, then return;
		if(Consistency.isPushApproach == true) return;
		
		// Read the file from input.
		FileEntry fe = new FileEntry(input.readLine());
		output.writeBoolean(Consistency.doPoll(peer.getFileList(), fe));
		output.flush();
	}

	private void doInvalidate() throws IOException {
		// If the approach is pull, then return;
		if(Consistency.isPushApproach == false) return;
		
		// Read the file from input. True means out-dated.
		FileEntry fe = new FileEntry(input.readLine());
		String[] source = input.readLine().split(" ");
		Consistency.doInvalidate(peer, fe, new Address(source[0], Integer.valueOf(source[1])), Integer.valueOf(source[2]));
	}

	public void doQuery() throws IOException{
		Query q = new Query(peer);
		String line = input.readLine();
		String[] args = (line.split(" "));
		
//		Check the messageID first!
		if (!messages.contains(args[0])) {
			messages.add(args[0]);
			q.doQuery(args[0], args[1], Integer.valueOf(args[2]), new Address(args[3],Integer.valueOf(args[4])));
		}
	}
	
	public void doQueryHit() throws IOException{
		String line;
		line = input.readLine();
		FileEntry fe = new FileEntry(line);
		
		for(int i=peer.getQueryList().size()-1; i >= 0; i--){
			String filename = peer.getQueryList().get(i).getFilename();
		
			if(fe.getFileName().equals(filename)){
				// Only print the last query file hit information
				if(i == peer.getQueryList().size()-1){
					peer.getQueryList().get(i).endQueryHit(fe, true);
				} else {
					peer.getQueryList().get(i).endQueryHit(fe, false);
				}
			}
		}
	}

	public void doSendFile() throws IOException{
		
		String filepath = input.readLine();
		System.out.print("\nA new request for file:[" + filepath + 
				"] from:" + socket.getRemoteSocketAddress() + "\n$ ");
		
		File f = new File(filepath);
		if(!f.exists()){
			output.writeInt(0);
			output.flush();
		} else if(f.isDirectory()) {
			output.writeInt(1);
			output.flush();
		} else {
			output.writeInt(2);
			output.flush();
			
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(filepath)));
			
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
		}
	}
	
}