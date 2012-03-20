package edu.kit.aifb.eorg.cloudpolling.multithreaded;

import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.kit.aifb.eorg.cloudpolling.AbstractPoller;
import edu.kit.aifb.eorg.datacollector.client.DataCollectorServiceService;

/**
 * adds multi-threaded polling to {@link AbstractPoller}
 * 
 * @author David Bermbach, Robin Hoffmann
 * 
 *         changed to Thread-Service on: 24.01.2012
 */
public abstract class AbstractPollerMultiThreaded extends AbstractPoller {

	static {
		log = Logger.getLogger(AbstractPollerMultiThreaded.class);
	}

	protected static int numberOfThreads = 5;

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
		System.out.println("Initialization complete, starting poller threads.");
		// initialization complete

		// Create pollers with 10ms between each
		for (int i = 0; i < numberOfThreads; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			String senderIdentifierLocal = senderIdentifier + "_thread_" + i;
			new AbstractPollerServiceMultiThreaded(this, pollIntervalInMillis,
					file, filename, buffersize, datacollector,
					senderIdentifierLocal).start();
		}
	}
}
