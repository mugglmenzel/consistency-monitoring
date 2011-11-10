/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import edu.kit.aifb.eorg.connectors.GAEConnector;

/**
 * @author Robin Hoffmann
 * 
 *         created on: 10.11.2011
 */
public class GAEWriter extends AbstractWriter {

	@Override
	public void writeToCloud(String key, String value) {
		GAEConnector.writeToGAE(key, value);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args[0]==null) {
			log.error("Missing parameters: GAE Url");
			throw new Exception(
					"Missing parameters: GAE Url");
		}
		GAEConnector.doInitialize(args[0]);
	}
}
