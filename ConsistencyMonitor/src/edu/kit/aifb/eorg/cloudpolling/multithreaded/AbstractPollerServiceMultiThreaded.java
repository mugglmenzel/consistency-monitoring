/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling.multithreaded;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.kit.aifb.eorg.datacollector.client.DataCollectorService;



/**
 * ThreadService for all implementations of {@link AbstractPollerMultiThreaded}
 * 
 * @author Robin Hoffmann
 * 
 *         created on: 24.01.2012
 */
public class AbstractPollerServiceMultiThreaded extends Thread {

	/** Logger */
	private static final Logger log = Logger
			.getLogger(AbstractPollerServiceMultiThreaded.class);

	/** stores the latest time this timestamp was read */
	private HashMap<Integer, Long> readTimestamps = new HashMap<Integer, Long>();
	/** stores the update date of a particular timestamp */
	private HashMap<Integer, Long> writeTimestamps = new HashMap<Integer, Long>();

	private AbstractPollerMultiThreaded ap;
	private long pollIntervalInMillis;
	private RandomAccessFile file;
	private int buffersize = 10;
	private String filename;
	private DataCollectorService datacollector;
	private String senderIdentifier;

	private Calendar start, end;

	public AbstractPollerServiceMultiThreaded(AbstractPollerMultiThreaded ap,
			long pollIntervalInMillis, RandomAccessFile file, String filename,
			int buffersize, DataCollectorService datacollector,
			String senderIdentifier) {
		this.ap = ap;
		this.file = file;
		this.filename = filename;
		this.buffersize = buffersize;
		this.datacollector = datacollector;
		this.senderIdentifier = senderIdentifier;
		this.pollIntervalInMillis = pollIntervalInMillis;
	}

	/**
	 * @param args
	 *            String [] first parameters must be data collector wsdl
	 *            address, poll interval in millis, file name, sender
	 *            identifier. Additional parameters are passed to method
	 *            configure(String [] args)
	 * @throws IOException
	 */
	public void run() {
		// PropertyConfigurator.configureAndWatch("log4j.properties");

		while (true) {
			try {
				// create Date object and then poll
				start = Calendar.getInstance();
				String data = ap.readFromCloud(filename);
				end = Calendar.getInstance();

				Thread.sleep(pollIntervalInMillis);

				file.writeBytes(start.getTimeInMillis() + ":" + data
						+ " latency:"
						+ (end.getTimeInMillis() - start.getTimeInMillis())
						+ "\n");

				if (data != null) {
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
						writeTimestamps.remove(minKey - 5);
						// publish in timestamps of the last read of n and the
						// write time of n+1
						datacollector.publishData(senderIdentifier,
								Math.max(readTime - writeTime, 0), ""
										+ (minKey + 1));

						// done repeat until buffersize is no longer violated
					}
				}
			} catch (Exception e) {
				log.error(end.getTime() + ": Error while polling...", e);
			}
		}
	}
}
