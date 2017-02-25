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

/**
 * 
 * @author Xincheng Yang
 * @version 1.0
 *
 */
public class ShareThread extends Thread{
	Socket socket;
	BufferedReader input; 
	DataOutputStream output;
	String filepath;
    
	public ShareThread(Socket s) throws IOException{
		this.socket = s;
		input = new BufferedReader(new InputStreamReader(s.getInputStream(),"utf-8")); 
		output = new DataOutputStream(s.getOutputStream());
	}
	
	public void run(){
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
			
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
