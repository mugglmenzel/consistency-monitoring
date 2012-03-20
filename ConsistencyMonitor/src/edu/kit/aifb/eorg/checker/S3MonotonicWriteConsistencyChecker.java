/**
 * 
 */
package edu.kit.aifb.eorg.checker;

import edu.kit.aifb.eorg.connectors.S3Connector;

/**
 * @author David Bermbach
 * 
 *         created on: 19.03.2012
 */
public class S3MonotonicWriteConsistencyChecker extends
		AbstractMonotonicWriteConsistencyChecker {

	private String secretKey=S3Credentials.secretKey;
	private String publicKey=S3Credentials.publicKey;
	private String bucket="eorganization";

	/**
	 * 
	 */
	public S3MonotonicWriteConsistencyChecker() {
		super();
		try {
			S3Connector.doInitialize(secretKey, publicKey);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void writeToCloud(String key, String value) {
		S3Connector.writeToS3(bucket, key, value);

	}

	@Override
	public String readFromCloud(String key) {
		return S3Connector.readFromS3(bucket, key);
	}

}
