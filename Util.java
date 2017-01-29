package CS550.iit;

public class Util {
	public static int getInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
}
