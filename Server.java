package cs550.iit;

import java.io.*;
import java.net.*;
import java.nio.file.*;


public class Server {

	Integer port, FileNum = 0;
	static String ListName = "./fl.txt";
	FileEntry[] FE = new FileEntry[100];
	File file = new File(ListName);
	ServerSocket server = null;
	Socket client = null;
	
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
			FileNum = 0;
			System.out.println(file.delete());
			file.createNewFile();
			System.out.println("New file list created.");
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
				FE[FileNum] = new FileEntry(line);
				//FE[FileNum] = new FileEntry();
				FileNum++;
			}
			System.out.println("File list read.\n" + FileNum + " file(s) in list.");
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
		for (int i = 0; i < FileNum; i++)
		{
			System.out.println(FE[i].toString());
		}
	}
	
	private void StartListenning()
	{
		try
		{
			server = new ServerSocket(port);
			while (true)
			{
				client = server.accept();
				ServerThread st = new ServerThread(client);
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
			boolean done = false;
			try
			{
				str = input.readLine();
			
				if (str.equals("LOOKUP"))
				{
					str = input.readLine();
					for (int i = 0; i<FileNum; i++)
					{
						if (FE[i].getFileName().equals(str))
						{
							output.println(FE[i].getIP());
							output.println(FE[i].getPort());
						}
					}
				}
				else if (str.equals("REGISTER"))
				{
					FE[FileNum] = new FileEntry();
					str = input.readLine();
					FE[FileNum].setIP(str);
					str = input.readLine();
					FE[FileNum].setPort(str);
					str = input.readLine();
					FE[FileNum].setFileName(str);
					FileNum++;
				}
				else
				{
					System.out.println("Invalid request.");
				}
				client.close();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}
