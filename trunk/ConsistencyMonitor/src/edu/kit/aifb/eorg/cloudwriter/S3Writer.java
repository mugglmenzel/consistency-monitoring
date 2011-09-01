/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import edu.kit.aifb.eorg.simples3library.S3Connector;

/**
 * @author David Bermbach
 * 
 *         created on: 29.03.2011
 */
public class S3Writer extends AbstractWriter {

	@Override
	protected void writeToCloud(String key, String value) {
		S3Connector.writeToS3(key, value);
	}

	@Override
	protected void configure(String[] args) throws Exception {
		if (args.length < 6 || args[0] == null || args[1] == null
				|| args[2] == null || args[3] == null || args[4] == null
				|| args[5] == null) {
			log.error("Missing parameters: bucket name, aws access key, aws secret access key");
			throw new Exception(
					"Missing parameters: bucket name, aws access key, aws secret access key");
		}
		S3Connector.doInitialize(args[0], args[2], args[1]);

	}
}
