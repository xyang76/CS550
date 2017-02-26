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
	String filepath;
	int serial;
	Address source;
	ArrayList<String> messages = new ArrayList<String>();
	
	public ShareThread(Peer p, Socket s) throws IOException{
		this.peer = p;
		this.socket = s;
		serial = 0;
		source = new Address(peer.getLocalIP(),peer.getLocalPort());
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
			}
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void doQuery() throws IOException{
		Query q = new Query(peer);
		String line;
		line = input.readLine();
		String[] args = (line.split(" "));
		
//		Check the messageID first!
		if (!messages.contains(args[0])) {
			messages.add(args[0]);
			q.doQuery(args[0], args[1], Integer.valueOf(args[2]), new Address(args[3],Integer.valueOf(args[4])));
		}
	}
	
	public void doQueryHit() throws IOException{
		Query q = new Query(peer);
		String line;
		line = input.readLine();
		String[] args = (line.split(" "));
//		format in args: filename, IP, Port
//		q.endQueryHit();
	}

	public void doSendFile(){
		try {
			this.filepath = input.readLine();
			System.out.print("\nA new request for file:[" + this.filepath + 
					"] from:" + socket.getRemoteSocketAddress() + "\n$ ");
			
			File f = new File(this.filepath);
			if(!f.exists()){
				output.writeInt(0);
				output.flush();
			} else if(f.isDirectory()) {
				output.writeInt(1);
				output.flush();
			} else {
				output.writeInt(2);
				output.flush();
				
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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
// Create a new messageID in startQuery!

	public String messageID(){
		serial++;
		return (peer.getLocalIP()+serial);
	}
}
