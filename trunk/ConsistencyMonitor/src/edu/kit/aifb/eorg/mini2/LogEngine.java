/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * logs write notifications to the file system
 * 
 * 
 * @author David Bermbach
 * 
 *         created on: 26.05.2011
 */
public class LogEngine {

	private static LogEngine instance;

	/** output file */
	private final RandomAccessFile file;

	/**
	 * @param outputfile
	 * @throws FileNotFoundException
	 */
	private LogEngine(String outputfile) throws FileNotFoundException {
		super();
		file = new RandomAccessFile(outputfile, "rw");
	}

	public static LogEngine getInstance() {
		return instance;
	}

	public static void createInstance(String outputfile)
			throws FileNotFoundException {
		if (instance == null)
			instance = new LogEngine(outputfile);
	}

	public synchronized void log(long timeInMillis, String content) {
		String prefix = "\"", suffix = "\";";
		try {
			file.writeBytes(prefix + timeInMillis + suffix + prefix
					+ content + suffix + "\n");
			// log.info("persisted data");
		} catch (IOException e) {
			info("Could not persist the following item:\n\n" + prefix
					+ timeInMillis + suffix + prefix + content + suffix
					+ "\n\n");
		}
	}

	void debug(String s) {
		System.out.println("DEBUG [LogEngine]" + new Date() + ":" + s);
	}

	void debug(Exception e) {
		System.out.println("DEBUG [LogEngine] " + new Date() + ":");
		e.printStackTrace();
	}

	void info(String s) {
		System.out.println("INFO  [LogEngine] " + new Date() + ":" + s);
	}

}
