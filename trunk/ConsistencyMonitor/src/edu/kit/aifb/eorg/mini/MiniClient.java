/**
 * 
 */
package edu.kit.aifb.eorg.mini;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * @author David Bermbach
 * 
 *         created on: 13.05.2011
 */
public class MiniClient {

	private boolean debug = false;

	public byte[] sendGet(final String key, final String host, final int port,
			final boolean isForward) throws Exception {
		if (isForward)
			return send(key, null, host, port, 3);
		else
			return send(key, null, host, port, 0);
	}

	public void sendPut(final String key, final byte[] value,
			final String host, final int port, final boolean isForward) {
		if (isForward)
			send(key, value, host, port, 4);
		else
			send(key, value, host, port, 1);
	}

	public void sendDelete(final String key, final String host, final int port,
			final boolean isForward) {
		if (isForward)
			send(key, null, host, port, 5);
		else
			send(key, null, host, port, 2);
	}

	public byte[] send(String key, byte[] val, String host, int port,
			int operation) {
		try {
			if (val == null)
				val = new byte[0];
			if (debug)
				System.out.println("sending: key=" + key + ", value=" + val
						+ "operation=" + operation + ", host=" + host
						+ ", port=" + port);
			Socket s = new Socket(host, port);
//			System.out.println("socket opened");
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
//			System.out.println("object output stream generated");
			oos.writeInt(operation);
//			System.out.println("operation written");
			oos.writeObject(key);
//			System.out.println("key written");
			oos.writeObject(val);
//			System.out.println("value written");
			// read response
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
//			System.out.println("input stream generated");
			byte[] res = (byte[]) ois.readObject();
//			System.out.println("result read");
			s.close();
//			System.out.println("stream closed");
			return res;
		} catch (Exception e) {
			if (debug) {
				debug(e);
				e.printStackTrace();
			}
			return null;
		}
	}

	void debug(String s) {
		System.out.println("DEBUG [MiniClient]" + new Date() + ":" + s);
	}

	void debug(Exception e) {
		System.out.println("DEBUG [MiniClient] " + new Date() + ":");
		e.printStackTrace();
	}

	void info(String s) {
		System.out.println("INFO  [MiniClient] " + new Date() + ":" + s);
	}

	public static void main(String[] args) throws Exception {
		MiniClient m = new MiniClient();
		System.out.println(new String(m.send("mykey", "content".getBytes(),
				"localhost", 8082, 4)));
	}

}
