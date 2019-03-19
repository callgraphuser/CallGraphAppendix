package helper;

public class Debug {
	public static boolean isDebugOn=true;

	public static void debug(String s) {
		if (isDebugOn) {
			System.out.println(s);
		}
	}
}
