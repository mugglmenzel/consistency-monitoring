/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

/**
 * configures an already running instance of MiniStorage 2
 * 
 * adds the slave to the specified parent
 * 
 * @author David Bermbach
 * 
 *         created on: 30.04.2012
 */
public class ManualConfigurator {

	static String parenthost = "";
	static int parentport = 80;
	static String newslavehost = "";
	static int newslaveport = 80;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Adding slave "
				+ (Mini2Client.addSlave(parenthost, parentport, new MiniHost(
						newslavehost, newslaveport)) ? "was successful"
						: "failed"));
	}

}
