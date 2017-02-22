package CS550.iit;

public class Config {
	private static final String defaultPath = "config.txt";
	
	public static void load(Peer peer, String filepath) {
		if(filepath == null || "".equals(filepath)){
			filepath = defaultPath;
		}
	}
	
}
