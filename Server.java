package CS550.iit;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * 
 * @author Yi Zhang
 * 
 * 1. This is a server class to response user request.
 * 2. Server has its own thread called 'ServerThread'.
 *
 */
public class Server {

	Integer port;
	static String ListName = "fl.txt";
	ArrayList<FileEntry> FE = new  ArrayList<FileEntry>();
	File file = new File(ListName);
	ServerSocket ss = null;
	Socket cs = null;
	
	public static void main(String[] args) 
	{	
		Server server = new Server();
		server.ReadPort();
		server.ReadList();
		server.StartListenning();
	}
	
	private void CreateList()
	{
		try
		{
			PrintWriter writer = new PrintWriter(ListName);
			for (FileEntry ite : FE)
			{
				writer.println(ite.toString());
			}
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void ReadList()
	{
		try
		{
			Path path = Paths.get(ListName);
			BufferedReader reader = Files.newBufferedReader(path);
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				FE.add(new FileEntry(line));
			}
			System.out.println("File list read.");
			ShowFileList();
		}
		catch (Exception e)
		{
			System.out.println("Can't read file list. Creating a new list.");
			CreateList();
		}
	}
	
	private void ReadPort()
	{
		try
		{
			InputStreamReader r = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(r);
			boolean flag = true;
			do
			{
				try
				{
					System.out.println("Please specify server port:");
					port = Integer.valueOf(br.readLine());
					flag = false;
					System.out.println("Port set at "+port+'.');
				}
				catch (NumberFormatException e)
				{
					System.out.println("Not a valid integer!");
				}
			} 
			while (flag);
			br.close();
			r.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void ShowFileList()
	{
		System.out.println(FE.size() + " file(s) in list.");
		for (int i = 0; i < FE.size(); i++)
		{
			System.out.println(FE.get(i).toString());
		}
		System.out.println();
	}
	
	private void StartListenning()
	{
		try
		{
			System.out.println("Start listening.");
			ss = new ServerSocket(port);
			while (true)
			{
				cs = ss.accept();
				ServerThread st = new ServerThread(cs);
				st.start();
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
		
	}
	
	class ServerThread extends Thread
	{
		Socket client;
		BufferedReader input;
		PrintWriter output;
		
		public ServerThread(Socket c)
		{
			this.client = c;
			InputStreamReader reader;
			OutputStreamWriter writer;
			try
			{
				reader = new InputStreamReader(client.getInputStream());
				writer = new OutputStreamWriter(client.getOutputStream());
				input = new BufferedReader(reader);
				output = new PrintWriter(writer, true);
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		@Override
		public void run()
		{
			String str = null;
			System.out.println("New thread created.");
			try
			{
				str = input.readLine();
			
				if (str.equals("LOOKUP"))
				{
					boolean bl = false;
					str = input.readLine();
					System.out.println("Looking up \""+str+"\".");
					for (FileEntry f : FE)
					{
						if(f.getFileName().equals(str)){
							bl = true;
							output.println(f.toString());
						}
					}
					output.flush();
					if (bl)
						System.out.println("File found.\n");
					else
						System.out.println("File not found.\n");
				}
				else if (str.equals("REGISTER"))
				{
					FileWriter fw = new FileWriter(ListName, true);
					PrintWriter writer = new PrintWriter(fw);
					
					FileEntry f = null;
					str = input.readLine();
					System.out.println("Registering.");
					while(str != null){
						f = new FileEntry(str);
						FE.add(f);
						writer.println(f.toString());
						System.out.println("\"" + f.getFileName() + "\" registered.");
						str = input.readLine();
					}
					writer.close();
				} 
				else if (str.equals("DELETE"))
				{
					boolean bl = false;
					FileEntry f = new FileEntry();
					str = input.readLine();
					f.setIP(str);
					str = input.readLine();
					f.setPort(str);
					str = input.readLine();
					f.setFileName(str);
					str = input.readLine();
					f.setDirectory(str);;
					bl = FE.remove(f);
					CreateList();
					if (bl)
						System.out.println("Entry deleted.");
					else
						System.out.println("Entry not found.");
				}
				else
				{
					System.out.println("Invalid request.");
				}
				client.close();
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			System.out.println("Thread ended.\n");
		}
	}
}