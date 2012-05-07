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
 * created on: 30.04.2012
 */
public class HermesPutHandler implements IRequestHandler {

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
		byte [] value = (byte[]) req.getItems().get(1);
		System.out.println("Got "+value.length+" bytes for key "+key);
		StorageEngine.getInstance().put(key, value);
		Response r = new Response(StorageEngine.getInstance().get(key),
				"Result for Put:", true);
		Coordinator.getInstance().forwardPut(key, value);
		return r;
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
