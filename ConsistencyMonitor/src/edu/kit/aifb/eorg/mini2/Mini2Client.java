/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import edu.kit.aifb.dbe.hermes.Request;
import edu.kit.aifb.dbe.hermes.Response;
import edu.kit.aifb.dbe.hermes.Sender;

/**
 * @author David Bermbach
 * 
 *         created on: 30.04.2012
 */
public class Mini2Client {

	private static Response sendMessage(String host, int port, Request req) {
		Sender s = new Sender(host, port);
		return s.sendMessage(req);
	}

	public static boolean put(String host, int port, String key, byte[] value) {
		Request req = new Request(key, "put");
		req.addItem(value);
		Response resp = sendMessage(host, port, req);
		return resp.responseCode();
	}

	public static byte[] get(String host, int port, String key) {
		Request req = new Request(key, "get");
		Response resp = sendMessage(host, port, req);
		if (resp.responseCode())
			return (byte[]) resp.getItems().get(0);
		else
			return null;
	}

	public static void delete(String host, int port, String key) {
		Request req = new Request(key, "delete");
		Response resp = sendMessage(host, port, req);
	}

	public static boolean addSlave(String host, int port, MiniHost slave) {
		Request req = new Request(slave, "addSlave");
		Response resp = sendMessage(host, port, req);
		return resp.responseCode();
	}

}
