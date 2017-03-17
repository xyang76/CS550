package CS550.iit;

import java.io.File;

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
	String originIP, originPort, sourceIP, sourcePort, fileName, directory;
	private int Version;
	private boolean outDate;

	public FileEntry(String msg){
		String[] s = msg.split(" ");
		sourceIP = s[0];
		sourcePort = s[1];
		fileName = s[2];
		directory = s[3];
		this.init();
	}
	
	public FileEntry(File f, Address addr){
		File file = new File(f.getAbsolutePath());
		directory = file.getParent();
		fileName = file.getName();
		originIP = addr.getIP();
		originPort = String.valueOf(addr.getPort());
		sourceIP = originIP;
		sourcePort = originPort;
		this.init();
	}

	public FileEntry() {
		this.init();
	}
	
	private void init(){
		Version = 1;
		outDate = false;
	}

	public String toString() {
		return (sourceIP + " " + sourcePort + " " + fileName + " " + directory);
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		directory = directory;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		fileName = filename;
	}

	public String getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(String ip) {
		sourceIP = ip;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String port) {
		sourcePort = port;
	}
	
	public String getOriginIP() {
		return originIP;
	}

	public void setOriginIP(String originIP) {
		this.originIP = originIP;
	}

	public String getOriginPort() {
		return originPort;
	}

	public void setOriginPort(String originPort) {
		this.originPort = originPort;
	}
	
	public int getVersion() {
		return Version;
	}

	public void setVersion(int version) {
		Version = version;
	}

	public boolean isOutDate() {
		return outDate;
	}

	public void setOutDate(boolean outDate) {
		this.outDate = outDate;
	}

	@Override
	public boolean equals(Object target) {
		if ((fileName.equals(((FileEntry) target).getFileName())) && (sourceIP.equals(((FileEntry) target).getSourceIP()))
				&& (sourcePort.equals(((FileEntry) target).getSourcePort()))
				&& (directory.equals(((FileEntry) target).getDirectory())))
			return true;
		else
			return false;
	}
}
