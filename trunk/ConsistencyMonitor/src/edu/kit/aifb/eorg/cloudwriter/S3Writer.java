/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import edu.kit.aifb.eorg.connectors.S3Connector;

/**
 * @author David Bermbach
 * 
 *         created on: 29.03.2011
 */
public class S3Writer extends AbstractWriter {

	private String bucket;

	@Override
	public void writeToCloud(String key, String value) {
		S3Connector.writeToS3(bucket, key, value);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 3 || args[0] == null || args[1] == null
				|| args[2] == null) {
			log.error("Missing parameters: bucket name, aws access key, aws secret access key");
			throw new Exception(
					"Missing parameters: bucket name, aws access key, aws secret access key");
		}
		bucket = args[0];
		S3Connector.doInitialize(args[2], args[1]);

	}
}
