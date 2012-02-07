/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import edu.kit.aifb.eorg.connectors.SimpleDBConnector;

/**
 * Version of {@link S3Poller} that polls one of two files to support
 * correlation checking of periodicities across buckets.
 * 
 * @author Jasmin Giemsch
 * 
 *         created on: 10.12.2011
 */
public class SimpleDBMultiFilePoller extends AbstractPoller {

	private String domain;

	@Override
	public String readFromCloud(String key) {
		return SimpleDBConnector.readFromSDB(domain, key);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 6 || args[0] == null || args[1] == null
				|| args[2] == null || args[3] == null || args[4] == null
				|| args[5] == null) {
			log.error("Missing parameters: domain name, aws access key,"
					+ " aws secret access key, boolean value (if true use"
					+ " standard file), alternate domainname, alternate filename");
			throw new Exception("Missing parameters:"
					+ " domain name, aws access key, aws secret access key,"
					+ " boolean value (if true use standard file), alternate"
					+ " domainname, alternate filename");
		}
		boolean useStandardFile = Boolean.parseBoolean(args[3]);
		if (useStandardFile) {
			SimpleDBConnector.doInitialize(args[2], args[1]);
			domain = args[0];
		} else {
			SimpleDBConnector.doInitialize(args[2], args[1]);
			AbstractPoller.filename = args[5];
			domain = args[4];
		}
	}
}
