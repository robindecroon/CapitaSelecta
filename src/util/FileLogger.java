package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileLogger implements LogListener {
	private File file = new File("data/log.txt");
	private FileWriter writer;
	private BufferedWriter w;

	public void close() {
		try {
			w.close();
			writer.close();
		} catch (Exception e) {
		}
	}

	public FileLogger() {
		try {
			writer = new FileWriter(file);
			w = new BufferedWriter(writer);
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.LogListener#Warning(java.lang.String)
	 */
	@Override
	public void Warning(String message) {
		try {
			w.write(message);
			w.newLine();
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.LogListener#Info(java.lang.String)
	 */
	@Override
	public void Info(String message) {
		try {
			w.write(message);
			w.newLine();
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.LogListener#Severe(java.lang.String)
	 */
	@Override
	public void Severe(String message) {
		try {
			w.write(message);
			w.newLine();
		} catch (Exception e) {
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see util.LogListener#Debug(java.lang.String)
	 */
	@Override
	public void Debug(String message) {
		try {
			w.write(message);
			w.newLine();
		} catch (Exception e) {
		}
	}

}
