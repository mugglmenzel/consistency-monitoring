/**
 * 
 */
package edu.kit.aifb.eorg.mini;

import java.io.FileNotFoundException;

/**
 * @author David Bermbach
 * 
 *         created on: 17.05.2011
 */
public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args != null
				&& (args.length == 1 || args[0].equalsIgnoreCase("help"))) {
			System.out
					.println("Start with own port followed by list of alternating parameters <host> and <port>");
			return;
		}
		try {
			LogEngine.createInstance("output.csv");
			while (LogEngine.getInstance() == null)
				Thread.sleep(10);
		} catch (Exception e1) {
			System.out.println("LogEngine could not be created:" + e1
					+ "\n\tThe system is shutting down.");
			System.exit(-1);
		}
		Hermes.port = Integer.parseInt(args[0]);
		Hermes h = new Hermes();
		new Thread(h).start();
		System.out
				.println("This MiniStorage instance is running "
						+ (StorageEngine.doInMemoryStorage ? "in memory"
								: "persistent"));
		// parse parameters and add them to Coordinator
		Coordinator c = Coordinator.getInstance();
		for (int i = 1; i < args.length;) {
			String host = args[i];
			int port = Integer.parseInt(args[i + 1]);
			c.addInstance(host, port);
			i += 2;
		}
	}

}
