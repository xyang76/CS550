package cs550.iit;

public class FileEntry {

	String IP, Port, FileName;
	
	public FileEntry(String msg) throws Exception
	{
		String[] s = msg.split(" ");
		IP = s[0];
		Port = s[1];
		FileName = s[2];
	}
	
	public String toString()
	{
		return (IP+" "+Port+" "+FileName);
	}
	
	public String getFileName()
	{
		return FileName;
	}
	
	public void setFileName(String filename)
	{
		FileName = filename;
	}
	
	public String getIP()
	{
		return IP;
	}
	
	public void setIP(String ip)
	{
		IP = ip;
	}
	
	public String getPort()
	{
		return Port;
	}
	
	public void setPort(String port)
	{
		Port = port;
	}
}
