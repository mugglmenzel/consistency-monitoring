/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import edu.kit.aifb.eorg.cloudpolling.multithreaded.AbstractPollerMultiThreaded;
import edu.kit.aifb.eorg.connectors.GAEConnector;

/**
 * @author Robin Hoffmann
 * 
 *         created on: 10.11.2011
 */
public class GAEPoller extends AbstractPollerMultiThreaded {

	
	@Override
	public String readFromCloud(String key) {
		return GAEConnector.readFromGAE(key);
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
