/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import java.util.ArrayList;

import edu.kit.aifb.eorg.mini.MiniClient;

/**
 * @author David Bermbach
 * 
 *         created on: 29.03.2011
 */
public class MiniWriter extends AbstractWriter {

	private static ArrayList<String> hosts = new ArrayList<String>();
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static MiniClient mc = new MiniClient();

	@Override
	public void writeToCloud(String key, String value) {
		// pick random MiniStorage replica
		int replica = (int) (Math.random() * hosts.size());
		mc.sendPut(key, value.getBytes(), hosts.get(replica),
				ports.get(replica), false);
	}

	@Override
	public void configure(String[] args) throws Exception {
		if (args.length < 6) {
			log.error("Missing list of host/port pairs");
			throw new Exception("Missing list of host/port pairs");
		}
		int counter = 0;
		while (counter + 1 < args.length) {
			String host = args[counter++];
			int port = Integer.parseInt(args[counter++]);
			log.info("Adding host " + host + ":" + port);
			hosts.add(host);
			ports.add(port);
			log.info("successful");
		}
		log.info("Known Hosts:\n" + hosts + "\nRunning on ports:\n" + ports);
	}
}
