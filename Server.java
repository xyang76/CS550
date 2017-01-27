package cs550.iit;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import static java.lang.System.out;


public class Server {

	Integer port, FileNum = 0;
	static String ListName = "fl.txt";
	FileEntry[] FE = new FileEntry[100];
	File file = new File(ListName);
	
	public static void main(String[] args)throws Exception
	{
		// TODO Auto-generated method stub		
		Server server = new Server();
		server.ReadPort();
		server.ReadList();
		
	}
	
	private void CreateList()
	{
		try
		{
			FileNum = 0;
			file.delete();
			System.out.println(file.createNewFile()+ " when creating");
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
				FileNum++;
			}
			out.println("File list read.\n" + FileNum + " file(s) in list.");
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
					out.println("Please specify server port:");
					port = Integer.valueOf(br.readLine());
					flag = false;
					out.println("Port set at "+port+'.');
				}
				catch (NumberFormatException e)
				{
					out.println("Not a valid integer!");
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

	public void ShowFileList()
	{
		for (int i = 0; i < FileNum; i++)
		{
			System.out.println(FE[i].toString());
		}
	}
	
	private void StartListenning()
	{
		
	}
}
