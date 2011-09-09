/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.kit.aifb.eorg.datacollector.client.DataCollectorService;
import edu.kit.aifb.eorg.datacollector.client.DataCollectorServiceService;

/**
 * abstract superclass for all Poller classes
 * 
 * @author David Bermbach
 * 
 *         created on: 01.09.2011
 */
public abstract class AbstractPoller {

	/**
	 * reads a timestamp version tuple from the cloud.
	 * 
	 * @param key
	 * @return null if an error occurs. May never throw an exception.
	 */
	protected abstract String readFromCloud(String key);

	/**
	 * receives configuration parameters from the ExtendedStarter class
	 * 
	 * @param args
	 */
	protected abstract void configure(String[] args) throws Exception;

	protected static final Logger log = Logger.getLogger(AbstractPoller.class);

	private static String datacollectoraddress;
	private static long pollIntervalInMillis;
	protected static String filename;
	private static String senderIdentifier;
	private static DataCollectorService datacollector;
	private static RandomAccessFile file;
	/** stores the latest time this timestamp was read */
	private static HashMap<Integer, Long> readTimestamps = new HashMap<Integer, Long>();
	/** stores the update date of a particular timestamp */
	private static HashMap<Integer, Long> writeTimestamps = new HashMap<Integer, Long>();
	private static int buffersize = 10; // number of buffered versions in
										// "durations"

	/**
	 * @param args
	 *            String [] first parameters must be data collector wsdl
	 *            address, poll interval in millis, file name, sender
	 *            identifier. Additional parameters are passed to method
	 *            configure(String [] args)
	 */
	public void runPoller(String[] args) {
		PropertyConfigurator.configureAndWatch("log4j.properties");
		try {
			if (args.length < 4) {
				log.error("Start with parameters: data collector wsdl address, poll interval in millis, file name, sender identifier plus additional parameters");
				System.exit(-1);
			}
			datacollectoraddress = args[0];
			datacollector = new DataCollectorServiceService(new URL(
					datacollectoraddress)).getDataCollectorServicePort();
			pollIntervalInMillis = Long.parseLong(args[1]);
			filename = args[2];
			senderIdentifier = args[3];

			file = new RandomAccessFile("Poller_output.csv", "rw");
			file.writeBytes("Sender Identifier: "
					+ senderIdentifier
					+ "\nPoll interval: "
					+ pollIntervalInMillis
					+ "ms\nStart Time:"
					+ Calendar.getInstance(TimeZone.getTimeZone("UTC"))
					+ "\nData format: Time in millis: File content on monitored storage system\n");
			String[] argsnew = null;
			if (args.length > 4) {
				argsnew = new String[args.length - 4];
				for (int i = 4; i < args.length; i++)
					argsnew[i - 4] = args[i];
			}
			this.configure(argsnew);

		} catch (Exception e) {
			log.error("Initialization failed", e);
			System.exit(-1);
		}
		System.out.println("Initialization complete, starting polling.");
		// initialization complete
		Calendar start;
		while (true) {
			try {
				Thread.sleep(pollIntervalInMillis);
				// create Date object and then poll
				start = Calendar.getInstance();
				String data = readFromCloud(filename);
				file.writeBytes(start.getTimeInMillis() + ":" + data + "\n");
				if (data == null)
					continue;
				String[] temp = data.split("\\s");
				int version = Integer.parseInt(temp[0]);
				long date = Long.parseLong(temp[1].trim());
				// buffer intermediate results to truly figure out the
				// slowest replica and publish the buffered results
				// later on
				// update buffered data by comparing the current timestamp
				// where version n was read to the write timestamp of
				// version n+1
				// first store read timestamp
				readTimestamps.put(version, start.getTimeInMillis());
				// second store write timestamp
				writeTimestamps.put(version, date);

				// check whether buffersize is violated
				while (readTimestamps.size() > buffersize) {
					// publish oldest results
					// find lowest key first
					int minKey = readTimestamps.keySet().iterator().next();
					for (int key : readTimestamps.keySet())
						if (key < minKey)
							minKey = key;
					// take latest read time for that version
					long readTime = readTimestamps.remove(minKey);
					// take write time of following version
					long writeTime = writeTimestamps.get(minKey + 1);
					writeTimestamps.remove(minKey-5);
					// publish in timestamps of the last read of n and the
					// write time of n+1
					datacollector.publishData(senderIdentifier, readTime
							- writeTime, "" + (minKey + 1));

					// done repeat until buffersize is no longer violated
				}

			} catch (Exception e) {
				log.error("Error while polling...", e);
			}
		}
	}
}
