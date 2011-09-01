/**
 * 
 */
package edu.kit.aifb.eorg.mini;

import java.util.ArrayList;
import java.util.Date;

/**
 * coordinates the efforts of all MiniStorage instances
 * 
 * 
 * @author David Bermbach
 * 
 *         created on: 13.05.2011
 */
public class Coordinator {

	private static Coordinator instance = new Coordinator();

	private final ArrayList<String> hosts = new ArrayList<String>();
	private final ArrayList<Integer> ports = new ArrayList<Integer>();
	private final MiniClient miniclient = new MiniClient();

	public static Coordinator getInstance() {
		return instance;
	}

	void addInstance(String host, int port) {
		hosts.add(host);
		ports.add(port);
		info("Instance added: port " + port + " at " + host);
	}

	void forwardPut(String key, byte[] value) {
		int counter = 0;
		for (String host : hosts) {
			int port = ports.get(counter++);
			miniclient.sendPut(key, value, host, port, true);
		}
	}

	void forwardDelete(String key) {
		int counter = 0;
		for (String host : hosts) {
			int port = ports.get(counter++);
			miniclient.sendDelete(key, host, port, true);
		}
	}

	public static void main(String[] args) {
		Coordinator c = Coordinator.getInstance();
		c.addInstance("localhost", 8082);
		c.forwardDelete("mykey");
	}

	void debug(String s) {
		System.out.println("DEBUG [Coordinator]" + new Date() + ":" + s);
	}

	void debug(Exception e) {
		System.out.println("DEBUG [Coordinator] " + new Date() + ":");
		e.printStackTrace();
	}

	void info(String s) {
		System.out.println("INFO  [Coordinator] " + new Date() + ":" + s);
	}

}
