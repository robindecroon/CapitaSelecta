package util;

import java.util.ArrayList;
import java.util.Date;

public class Logger {
	private static final boolean DEBUG = false;
	private static final boolean WARNING = false;
	private static final boolean INFO = true;
	private static final ArrayList<LogListener> listeners = new ArrayList<LogListener>();

	public static void addListener(LogListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	public static void Debug(String message) {
		String text = "[" + (new Date().toString()) + "] - Debug: " + message
				+ "...";
		if (DEBUG)
			System.out.println(text);
		for (LogListener l : listeners)
			l.Debug(text);
	}

	public static void Info(String message) {
		String text = "[" + (new Date().toString()) + "] - Info: " + message
				+ "...";
		if (INFO)
			System.out.println(text);
		for (LogListener l : listeners)
			l.Info(text);
	}

	public static void Warning(String message) {
		String text = "[" + (new Date().toString()) + "] - Warning: " + message
				+ "!";
		if (WARNING)
			System.err.println(text);
		for (LogListener l : listeners)
			l.Warning(text);
	}

	public static void Severe(String message) {
		String text = "[" + (new Date().toString()) + "] - Severe: " + message
				+ "!!!";
		System.err.println(text);
		for (LogListener l : listeners)
			l.Severe(text);
	}

	public static void Severe(String message, Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append(message);

		for (StackTraceElement elem : e.getStackTrace())
			builder.append("\n" + elem.toString());

		String text = "[" + (new Date().toString()) + "] - Severe: "
				+ builder.toString() + "!!!";
		System.err.append(text);
		for (LogListener l : listeners)
			l.Severe(text);
	}
}
