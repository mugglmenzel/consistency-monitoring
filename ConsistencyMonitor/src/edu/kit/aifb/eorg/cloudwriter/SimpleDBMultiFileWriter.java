/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.PropertyConfigurator;

import edu.kit.aifb.eorg.connectors.SimpleDBConnector;
import edu.kit.aifb.eorg.datacollector.client.DataCollectorServiceService;

/**
 * This class allows to write more than one file at the same time
 * 
 * @author Jasmin Giemsch
 * 
 *         created on: 10.12.2011
 */
public class SimpleDBMultiFileWriter extends AbstractWriter {

	private String domain1, domain2;
	private String filename2;

	@Override
	public void writeToCloud(String key, String value) {
		throw new RuntimeException(
				"Use Method writeToCloud(String domain, String key, String value) instead!");
	}

	protected void writeToCloud(String domain, String key, String value) {
		SimpleDBConnector.writeToSDB(domain, key, value);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 5 || args[0] == null || args[1] == null
				|| args[2] == null || args[3] == null || args[4] == null) {
			log.error("Missing parameters: domain name, aws access key,"
					+ " aws secret access key, alternate domainname, alternate filename");
			throw new Exception("Missing parameters:"
					+ " domain name, aws access key, aws secret access key,"
					+ " alternate" + " domainname, alternate filename");
		}
		domain1 = args[0];
		domain2 = args[3];
		filename2 = args[4];
		SimpleDBConnector.doInitialize(args[2], args[1]);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.aifb.eorg.cloudwriter.AbstractWriter#runWriter(java.lang.String
	 * [])
	 */
	@Override
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
		Calendar start, end, end2;
		int counter = 0;
		while (true) {
			try {
				Thread.sleep(writeIntervalInMillis);
				log.info("Running test " + counter);
				// upload timestamp and version number for file 1
				start = Calendar.getInstance();
				this.writeToCloud(domain1, filename,
						(counter + " " + start.getTimeInMillis()));
				end = Calendar.getInstance();
				this.writeToCloud(domain2, filename2,
						(counter++ + " " + end.getTimeInMillis()));
				end2 = Calendar.getInstance();
				System.out.println("done");
				file.writeBytes(start.getTime() + ": writing" + (counter - 1)
						+ "\n");
				file.writeBytes(end.getTime() + ": finished writing after "
						+ (end.getTimeInMillis() - start.getTimeInMillis())
						+ "ms and "
						+ (end2.getTimeInMillis() - end.getTimeInMillis())
						+ "ms\n");
				datacollector.publishData(
						"Write Duration File 1 (Delay between write 1 and 2)",
						(end.getTimeInMillis() - start.getTimeInMillis()), ""
								+ (counter - 1));
				datacollector.publishData("Write Duration File 2",
						(end2.getTimeInMillis() - end.getTimeInMillis()), ""
								+ (counter - 1));

			} catch (Exception e) {
				log.error("Error while writing...", e);
				e.printStackTrace();
			}
		}
	}

}
