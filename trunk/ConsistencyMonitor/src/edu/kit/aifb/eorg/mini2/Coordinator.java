/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * 
 * manages the replication network
 * 
 * @author David Bermbach
 * 
 *         created on: 30.04.2012
 */
public class Coordinator {

	private static final Logger log = Logger.getLogger(Coordinator.class);

	/**
	 * holds all slaves of this node. Slaves will be forwarded delete and put
	 * requests
	 */
	private static final HashSet<MiniHost> slaves = new HashSet<MiniHost>();

	private static final Coordinator instance = new Coordinator();

	private Coordinator() {
	}

	/**
	 * @return the singleton
	 */
	public static Coordinator getInstance() {
		return instance;
	}

	public boolean addSlave(MiniHost slave) {
		if (slaves.contains(slave))
			return false;
		else {
			slaves.add(slave);
			return true;
		}
	}

	public void forwardPut(final String key, final byte[] value) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (MiniHost m : slaves) {
					Mini2Client.put(m.host, m.port, key, value);
				}
			}
		}).start();
	}

	public void forwardDelete(final String key) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (MiniHost m : slaves) {
					Mini2Client.delete(m.host, m.port, key);
				}
			}
		}).start();
	}

}
