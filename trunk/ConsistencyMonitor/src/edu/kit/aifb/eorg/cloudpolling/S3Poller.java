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

	private String bucket;
	
	@Override
	public String readFromCloud(String key) {
		return S3Connector.readFromS3(bucket,key);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 3 || args[0] == null || args[1] == null
				|| args[2] == null) {
			log.error("Missing parameters: bucket name, aws access key, aws secret access key");
			throw new Exception("Missing parameters: bucket name, aws access key, aws secret access key");
		}
		bucket = args[0];
		S3Connector.doInitialize(args[2], args[1]);
	}
}
