/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import edu.kit.aifb.eorg.connectors.S3Connector;

/**
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
public class S3Poller extends AbstractPoller {

	
	@Override
	protected String readFromCloud(String key) {
		return S3Connector.readFromS3(key);
	}

	@Override
	protected void configure(String[] args) throws Exception {
		if (args.length < 3 || args[0] == null || args[1] == null
				|| args[2] == null) {
			log.error("Missing parameters: bucket name, aws access key, aws secret access key");
			throw new Exception("Missing parameters: bucket name, aws access key, aws secret access key");
		}
		S3Connector.doInitialize(args[0], args[2], args[1]);
	}
}
