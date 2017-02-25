package CS550.iit;

/**
 * 
 * @author Xincheng Yang
 * @version 1.0
 * 
 * An neighbor object. 
 * Methods:
 *	getNeighbor : get a neighbor instance from a neighbor string(because we need to check the format of neighbor string).
 * 	
 */
public class Neighbor {
	private String ip;
	private int port;
	
	/**
	 * Constructor
	 * @param ip
	 * @param port
	 */
	public Neighbor(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * For neighbor string, we have to get neighbor by this function, 
	 * because we need to check the format of neighbor string.
	 * 
	 * @param neighborString
	 * @return a neighbor instance
	 */
	public static Neighbor getNeighbor(String neighborString){
		try {
			String[] s = neighborString.split(":");
			if(s.length == 2){
				int port = Integer.parseInt(s[1]);
				return new Neighbor(s[0], port);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getIP() {
		return ip;
	}
	public void setIP(String iP) {
		ip = iP;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
