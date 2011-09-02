/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.kit.aifb.eorg.datacollector.client.DataCollectorService;
import edu.kit.aifb.eorg.datacollector.client.DataCollectorServiceService;

/**
 * abstract writer superclass. writes a timestamp plus version number to the
 * cloud
 * 
 * @author David Bermbach
 * 
 *         created on: 01.09.2011
 */
public abstract class AbstractWriter {

	protected static final Logger log = Logger.getLogger(AbstractWriter.class);

	private static String datacollectoraddress;
	private static DataCollectorService datacollector;
	private static long writeIntervalInMillis;
	private static String filename;
	private static RandomAccessFile file;
	private static String senderIdentifier;

	/**
	 * writes a timestamp version tuple to the cloud.
	 * 
	 * @param key
	 * @return null if an error occurs. May never throw an exception.
	 */
	protected abstract void writeToCloud(String key, String value);

	/**
	 * receives configuration parameters from the ExtendedStarter class
	 * 
	 * @param args
	 */
	protected abstract void configure(String[] args) throws Exception;

	/**
	 * @param args
	 */
	public void runWriter(String[] args) {
		PropertyConfigurator.configureAndWatch("log4j.properties");
		try {
			if (args.length < 4) {
				log.error("Start with parameters: data collector wsdl address, write interval in millis, file name, sender identifier plus additional parameters");
				System.exit(-1);
			}
			datacollectoraddress = args[0];
			datacollector = new DataCollectorServiceService(new URL(
					datacollectoraddress)).getDataCollectorServicePort();
			writeIntervalInMillis = Long.parseLong(args[1]);
			filename = args[2];
			senderIdentifier = args[3];

			file = new RandomAccessFile("writer_output.csv", "rw");
			datacollector = new DataCollectorServiceService(new URL(
					datacollectoraddress)).getDataCollectorServicePort();
			file.writeBytes("Writer" + "\nWrite interval: "
					+ writeIntervalInMillis + "ms\nStart Time:"
					+ Calendar.getInstance(TimeZone.getTimeZone("UTC"))
					+ "\nData format: Time in Millis: Message\n");

			String[] newargs = null;
			if (args.length > 4) {
				newargs = new String[args.length - 4];
				for (int i = 4; i < args.length; i++)
					newargs[i - 4] = args[i];
			}
			this.configure(newargs);
		} catch (Exception e) {
			log.error("Initialization failed", e);
			System.exit(-1);
		}
		// initialization complete
		Calendar start, end;
		int counter = 0;
		while (true) {
			try {
				Thread.sleep(writeIntervalInMillis);
				log.info("Running test " + counter);
				// upload timestamp and version number
				start = Calendar.getInstance();
				this.writeToCloud(filename,
						(counter++ + " " + start.getTimeInMillis()));
				end = Calendar.getInstance();
				System.out.println("done");
				file.writeBytes(start.getTime() + ": writing" + (counter - 1)
						+ "\n");
				file.writeBytes(end.getTime() + ": finished writing after "
						+ (end.getTimeInMillis() - start.getTimeInMillis())
						+ "ms\n");
				datacollector
						.publishData(senderIdentifier, (end
								.getTimeInMillis() - start.getTimeInMillis()),
								"" + (counter - 1));

			} catch (Exception e) {
				log.error("Error while writing...", e);
				e.printStackTrace();
			}
		}
	}

}
