package CS550.iit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Xincheng Yang
 * @version 1.0
 * 
 * The Util class contains several utils for common functional propose.
 */
public class Util {
	
	/**
	 * Get a Int value from String.
	 * @param String
	 * @return int
	 */
	public static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
    
	/**
	 * Get local IP address.
	 * @return
	 * @throws Exception
	 */
    public static String getIP() throws Exception {
        URL url = new URL("http://checkip.amazonaws.com");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String ip = br.readLine();
        return ip;
    }
	
}
