/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import edu.kit.aifb.eorg.connectors.SimpleDBConnector;

/**
 * @author Jasmin Giemsch
 * 
 *         created on: 10.12.2011
 */
public class SimpleDBPoller extends AbstractPoller {

	private String domain;
	
	@Override
	public String readFromCloud(String key) {
		return SimpleDBConnector.readFromSDB(domain,key);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 3 || args[0] == null || args[1] == null
				|| args[2] == null) {
			log.error("Missing parameters: domain name, aws access key, aws secret access key");
			throw new Exception("Missing parameters: domain name, aws access key, aws secret access key");
		}
		domain = args[0];
		SimpleDBConnector.doInitialize(args[2], args[1]);
	}
}
