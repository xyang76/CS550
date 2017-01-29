package CS550.iit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Util {
	public static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
    
    public static String getIP() throws Exception {
        URL url = new URL("http://checkip.amazonaws.com");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String ip = br.readLine();
        return ip;
    }
	
}
