/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import edu.kit.aifb.eorg.connectors.SimpleDBConnector;

/**
 * @author Jasmin Giemsch
 * 
 *         created on: 10.12.2011
 */
public class SimpleDBWriter extends AbstractWriter {

	private String domain;

	@Override
	public void writeToCloud(String key, String value) {
		SimpleDBConnector.writeToSDB(domain, key, value);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 3 || args[0] == null || args[1] == null
				|| args[2] == null) {
			log.error("Missing parameters: domain name, aws access key, aws secret access key");
			throw new Exception(
					"Missing parameters: domain name, aws access key, aws secret access key");
		}
		domain = args[0];
		SimpleDBConnector.doInitialize(args[2], args[1]);

	}
}
