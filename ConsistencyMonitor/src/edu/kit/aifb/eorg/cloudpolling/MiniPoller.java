/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import java.util.ArrayList;

import edu.kit.aifb.eorg.mini.MiniClient;

/**
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
public class MiniPoller extends AbstractPoller {

	private static ArrayList<String> hosts = new ArrayList<String>();
	private static ArrayList<Integer> ports = new ArrayList<Integer>();
	private static MiniClient mc = new MiniClient();

	@Override
	protected String readFromCloud(String key) {
		try {
			// pick random MiniStorage replica
			int replica = (int) (Math.random() * hosts.size());
			return new String(mc.sendGet(key, hosts.get(replica),
					ports.get(replica), false));
		} catch (Exception e) {
			log.error("Error while polling");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void configure(String[] args) throws Exception {
		if (args.length < 2)
			throw new Exception("Missing list of host/port pairs");
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
