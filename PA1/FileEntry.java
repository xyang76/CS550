package CS550.iit;

/**
 * 
 * @author Yi Zhang
 * @version 1.0
 * 
 * 	This java class is a file object to transmit file information between peer and server.
 * 
 */
public class FileEntry {
	// A file contain it's original ip address, port, filename and directory.
	String IP, Port, FileName, Directory;

	public FileEntry(String msg) throws Exception {
		String[] s = msg.split(" ");
		IP = s[0];
		Port = s[1];
		FileName = s[2];
		Directory = s[3];
	}

	public FileEntry() {

	}

	public String toString() {
		return (IP + " " + Port + " " + FileName + " " + Directory);
	}

	public String getDirectory() {
		return Directory;
	}

	public void setDirectory(String directory) {
		Directory = directory;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String filename) {
		FileName = filename;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public String getPort() {
		return Port;
	}

	public void setPort(String port) {
		Port = port;
	}

	@Override
	public boolean equals(Object target) {
		if ((FileName.equals(((FileEntry) target).getFileName())) && (IP.equals(((FileEntry) target).getIP()))
				&& (Port.equals(((FileEntry) target).getPort()))
				&& (Directory.equals(((FileEntry) target).getDirectory())))
			return true;
		else
			return false;
	}
}
