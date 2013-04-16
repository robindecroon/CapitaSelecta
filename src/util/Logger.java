package util;

public class Logger {
	private static final boolean DEBUG = false;
	private static final boolean WARNING = false;
	private static final boolean INFO = false;

	public static void Debug(String message) {
		if (DEBUG)
			System.out.println("Debug: " + message + "...");
	}

	public static void Info(String message) {
		if (INFO)
			System.out.println("Info: " + message + "...");
	}

	public static void Warning(String message) {
		if (WARNING)
			System.err.println("Warning: " + message + "!");
	}

	public static void Severe(String message) {
		System.err.println("Warning: " + message + "!!!");
	}
}
