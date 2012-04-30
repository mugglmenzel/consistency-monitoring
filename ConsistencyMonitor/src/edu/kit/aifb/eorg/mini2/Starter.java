/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.kit.aifb.dbe.hermes.Receiver;
import edu.kit.aifb.dbe.hermes.RequestHandlerRegistry;

/**
 * @author David Bermbach
 * 
 *         created on: 17.05.2011
 */
public class Starter {

	private static final Logger log = Logger.getLogger(Starter.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		if (args != null
				&& (args.length == 1 || args[0].equalsIgnoreCase("help"))) {
			log.error("Start with own port as parameter");
			return;
		}
		int port = Integer.parseInt(args[0]);
		Receiver receiver = new Receiver(port);
		RequestHandlerRegistry reg = RequestHandlerRegistry.getInstance();
		reg.registerHandler("addSlave", new HermesAddSlaveHandler());
		reg.registerHandler("delete", new HermesDeleteHandler());
		reg.registerHandler("get", new HermesGetHandler());
		reg.registerHandler("put", new HermesPutHandler());
		receiver.start();
	}

}
