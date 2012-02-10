package edu.kit.aifb.eorg.cloudpolling.ThreadingPoller;

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
 * @author David Bermbach, Robin Hoffmann
 * 
 *         changed to Thread-Service on: 24.01.2012
 */
public abstract class AbstractPoller {

	/**
	 * reads a timestamp version tuple from the cloud.
	 * 
	 * @param key
	 * @return null if an error occurs. May never throw an exception.
	 */
	public abstract String readFromCloud(String key);

	/**
	 * receives configuration parameters from the ExtendedStarter class
	 * 
	 * @param args
	 */
	public abstract void configure(String[] args) throws Exception;

	protected static final Logger log = Logger.getLogger(AbstractPoller.class);

	protected static DataCollectorService datacollector;
	protected static String datacollectoraddress;
	protected static long pollIntervalInMillis;
	protected static String filename;
	protected static String senderIdentifier;
	protected static RandomAccessFile file;
	
	/** stores the latest time this timestamp was read */
	protected static HashMap<Integer, Long> readTimestamps = new HashMap<Integer, Long>();
	/** stores the update date of a particular timestamp */
	protected static HashMap<Integer, Long> writeTimestamps = new HashMap<Integer, Long>();

	protected static int buffersize = 10; // number of buffered versions in
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
		
		//Create 5 poller with 10ms between each
		for (int i=0; i<5; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			String senderIdentifierLocal = senderIdentifier+"_thread_"+i;
			new AbstractPollerService(this, pollIntervalInMillis, file, filename, buffersize, datacollector, senderIdentifierLocal).start();
		}
	}
}
