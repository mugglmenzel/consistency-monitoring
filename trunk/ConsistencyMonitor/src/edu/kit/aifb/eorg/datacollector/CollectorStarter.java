/**
 * 
 */
package edu.kit.aifb.eorg.datacollector;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
public class CollectorStarter {

	private static final Logger log = Logger.getLogger(CollectorStarter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configureAndWatch("log4j.properties");
		if (args.length < 2 || args[0] == null || args[1] == null) {
			log.error("Start with service endpoint address as first and output filename as second parameter.");
			System.exit(-1);
		}
		try {
			DataCollector.createInstance(args[1]);
			log.info("Created instance. Publishing service at "+args[0]);
			Endpoint.publish(args[0], new DataCollectorService());
		} catch (Exception e) {
			log.error("Could not start service: ", e);
		}

	}

}
