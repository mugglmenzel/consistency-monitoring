/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.util.Random;

/**
 * @author David Bermbach
 * 
 *         created on: 03.05.2012
 */
public class TestClient {

	static String host="122.248.223.71";
	static int port=8081;
	static int noOfTests = 500;
	static byte[] value = new byte[56];

	static {
		new Random().nextBytes(value);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < noOfTests; i++) {
			System.out.println("Running test " + i);
			Mini2Client.put(host, port, "key", value);
		}
		System.out.println("Test complete!");
	}

}
