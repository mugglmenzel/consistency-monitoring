/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import edu.kit.aifb.dbe.hermes.IRequestHandler;
import edu.kit.aifb.dbe.hermes.Request;
import edu.kit.aifb.dbe.hermes.Response;

/**
 * @author David Bermbach
 * 
 *         created on: 30.04.2012
 */
public class HermesAddSlaveHandler implements IRequestHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.aifb.dbe.hermes.IRequestHandler#handleRequest(edu.kit.aifb.dbe
	 * .hermes.Request)
	 */
	@Override
	public Response handleRequest(Request req) {
		MiniHost m = (MiniHost) req.getItems().get(0);
		System.out.println("Adding new slave: "+m.host+":"+m.port);
		boolean res = Coordinator.getInstance().addSlave(m);
		return new Response("Result from Coordinator:", res);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.aifb.dbe.hermes.IRequestHandler#requiresResponse()
	 */
	@Override
	public boolean requiresResponse() {
		return true;
	}

}
