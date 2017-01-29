package cs550.iit;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;


public class Server {

	Integer port;
	static String ListName = "fl.txt";
	ArrayList<FileEntry> FE = new  ArrayList<FileEntry>();
	File file = new File(ListName);
	ServerSocket ss = null;
	Socket cs = null;
	
	public static void main(String[] args)throws Exception
	{
		// TODO Auto-generated method stub		
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
			System.out.println("File list read.\n" + FE.size() + " file(s) in list.");
			ShowFileList();
		}
		catch (Exception e)
		{
			System.out.println("Can't read file list.");
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
		for (int i = 0; i < FE.size(); i++)
		{
			System.out.println(FE.get(i).toString());
		}
	}
	
	private void StartListenning()
	{
		try
		{
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
//		Integer FileNum;
//		FileEntry[] FE = null;
		
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
			try
			{
				str = input.readLine();
			
				if (str.equals("LOOKUP"))
				{
					str = input.readLine();
					for (int i = 0; i<FE.size(); i++)
					{
						FileEntry f = FE.get(i);
						File file = new File(f.getFileName());
						if (file.getName().equals(str))
						{
							output.println(f.toString());
						}
					}
					output.flush();
				}
				else if (str.equals("REGISTER"))
				{
					FileEntry f = new FileEntry();
					str = input.readLine();
					f.setIP(str);
					str = input.readLine();
					f.setPort(str);
					str = input.readLine();
					f.setFileName(str);
					FE.add(f);
					FileWriter fw = new FileWriter(ListName, true);
					PrintWriter writer = new PrintWriter(fw);
					writer.println(f.toString());
					writer.close();
				} 
				else if (str.equals("DELETE"))
				{
					
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
		}
	}
}
