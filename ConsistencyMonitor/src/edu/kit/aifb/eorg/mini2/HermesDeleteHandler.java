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
public class HermesDeleteHandler implements IRequestHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.kit.aifb.dbe.hermes.IRequestHandler#handleRequest(edu.kit.aifb.dbe
	 * .hermes.Request)
	 */
	@Override
	public Response handleRequest(Request req) {
		String key = (String) req.getItems().get(0);
		StorageEngine.getInstance().delete(key);
		Coordinator.getInstance().forwardDelete(key);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.kit.aifb.dbe.hermes.IRequestHandler#requiresResponse()
	 */
	@Override
	public boolean requiresResponse() {
		return false;
	}

}
