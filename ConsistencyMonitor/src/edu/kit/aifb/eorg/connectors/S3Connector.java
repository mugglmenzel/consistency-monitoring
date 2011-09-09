/**
 * 
 */
package edu.kit.aifb.eorg.connectors;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.S3Object;

/**
 * 
 * This class provides some very fast but hard-coded access methods for S3
 * 
 * @author David Bermbach
 * 
 *         created on: 30.03.2011
 */
public final class S3Connector {

	private static AWSAuthConnection aws;

	public final static void doInitialize(String secretkey,
			String publickey) throws Exception {
				aws = new AWSAuthConnection(publickey, secretkey);
	}

	public final static void writeToS3(final String bucket, final String key, final String value) {
		try {
			S3Object obj = new S3Object(value.getBytes(), null);
			System.out.println(aws.put(bucket, key, obj, null).connection
					.getResponseMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public final static String readFromS3(final String bucket, final String key) {
		try {
			return new String(aws.get(bucket, key, null).object.data);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
