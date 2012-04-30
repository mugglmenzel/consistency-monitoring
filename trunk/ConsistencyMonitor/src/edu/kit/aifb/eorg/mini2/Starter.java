/**
 * 
 */
package edu.kit.aifb.eorg.mini2;


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
		
	}

}
