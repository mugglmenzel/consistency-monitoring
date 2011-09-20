/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import edu.kit.aifb.eorg.connectors.S3Connector;

/**
 * Version of {@link S3Poller} that polls one of two files to support
 * correlation checking of periodicities across buckets.
 * 
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
public class S3MultiFilePoller extends AbstractPoller {

	private String bucket;

	@Override
	public String readFromCloud(String key) {
		return S3Connector.readFromS3(bucket, key);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 6 || args[0] == null || args[1] == null
				|| args[2] == null || args[3] == null || args[4] == null
				|| args[5] == null) {
			log.error("Missing parameters: bucket name, aws access key,"
					+ " aws secret access key, boolean value (if true use"
					+ " standard file), alternate bucketname, alternate filename");
			throw new Exception("Missing parameters:"
					+ " bucket name, aws access key, aws secret access key,"
					+ " boolean value (if true use standard file), alternate"
					+ " bucketname, alternate filename");
		}
		boolean useStandardFile = Boolean.parseBoolean(args[3]);
		if (useStandardFile) {
			S3Connector.doInitialize(args[2], args[1]);
			bucket = args[0];
		} else {
			S3Connector.doInitialize(args[2], args[1]);
			AbstractPoller.filename = args[5];
			bucket = args[4];
		}
	}
}
