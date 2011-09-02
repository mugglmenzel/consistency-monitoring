package edu.kit.aifb.eorg.datacollector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

/**
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
public class DataCollector {

	private static final Logger log = Logger.getLogger(DataCollector.class);

	/** output file */
	private final RandomAccessFile file;

	/** singleton */
	private static DataCollector instance;

	/**
	 * 
	 * @return the singleton
	 */
	public static DataCollector getInstance() {
		return instance;
	}

	public static void createInstance(
			String outputFilename) throws FileNotFoundException {
		instance = new DataCollector(outputFilename);
		log.info("Singleton created."
				+ "\noutputFilename=" + outputFilename);
	}

	private DataCollector(String outputFilename)
			throws FileNotFoundException {
		super();
		file = new RandomAccessFile(outputFilename, "rw");
	}

	public synchronized void writeData(String senderIdentifier,
			long durationInMillis, String testrunID) {
		String prefix = "\"", suffix = "\";";
		try {
			file.writeBytes(prefix + senderIdentifier + suffix + prefix
					+ durationInMillis + suffix + prefix + testrunID + suffix+"\n");
//			log.info("persisted data");
		} catch (IOException e) {
			log.error("Could not persist the following item:\n\n" + prefix
					+ senderIdentifier + suffix + prefix + durationInMillis
					+ suffix + prefix + testrunID + suffix + "\n\n");
		}
	}

}
