/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import org.apache.log4j.BasicConfigurator;

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

	static String parenthost = "46.51.161.64";
	static int parentport = 8081;
	static String newslavehost = "177.71.167.84";
	static int newslaveport = 8081;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
//		System.out.println("Adding slave "
//				+ (Mini2Client.addSlave(parenthost, parentport, new MiniHost(
//						newslavehost, newslaveport)) ? "was successful"
//						: "failed"));
//		
		//test1
//		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("107.20.121.34", 8081)));
//		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("46.51.161.64", 8081)));
//		System.out.println(Mini2Client.addSlave("46.51.161.64", 8081, new MiniHost("177.71.167.84", 8081)));
		//test2
//		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("107.20.121.34", 8081)));
//		System.out.println(Mini2Client.addSlave("107.20.121.34", 8081, new MiniHost("177.71.167.84", 8081)));
//		System.out.println(Mini2Client.addSlave("177.71.167.84", 8081, new MiniHost("46.51.161.64", 8081)));
		//test3
		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("107.20.121.34", 8081)));
		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("177.71.167.84", 8081)));
		System.out.println(Mini2Client.addSlave("122.248.223.71", 8081, new MiniHost("46.51.161.64", 8081)));
	}

}
