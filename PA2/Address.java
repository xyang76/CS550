package CS550.iit;

/**
 * 
 * @author Xincheng Yang
 * @version 1.0
 * 
 *          An address object. Methods: getAddress : get an address instance
 *          from a address string(because we need to check the format of
 *          address string).
 * 
 */
public class Address {
	private String ip;
	private int port;

	/**
	 * Constructor
	 * 
	 * @param ip
	 * @param port
	 */
	public Address(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	/**
	 * For address string, we have to get address by this function, because we
	 * need to check the format of neighbor string.
	 * 
	 * @param addressString
	 * @return a neighbor instance
	 */
	public static Address getAddress(String addressString) {
		try {
			String[] s = addressString.split(":");
			if (s.length == 2) {
				int port = Integer.parseInt(s[1]);
				return new Address(s[0], port);
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
