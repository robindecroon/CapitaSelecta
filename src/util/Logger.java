package util;

public class Logger {
	private static final boolean DEBUG = false;

	public static void Debug(String message) {
		if (DEBUG)
			System.out.println("Debug: " + message + "...");
	}

	public static void Info(String message) {
		System.out.println("Info: " + message + "...");
	}

	public static void Warning(String message) {
		System.err.println("Warning: " + message + "!");
	}

	public static void Severe(String message) {
		System.err.println("Warning: " + message + "!!!");
	}
}
