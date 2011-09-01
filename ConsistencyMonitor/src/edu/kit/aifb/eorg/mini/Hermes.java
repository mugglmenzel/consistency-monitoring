/**
 * 
 */
package edu.kit.aifb.eorg.mini;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Hermes is responsible for Messaging and exposes the external interfaces
 * (Hermes = messenger)
 * 
 * @author David Bermbach
 * 
 *         created on: 13.05.2011
 */
public class Hermes implements Runnable {

	private boolean debug = false;
	private ServerSocket serversocket;
	static int port = 8082;
	private static int updatePropagationDelay = 1000;

	@Override
	public void run() {
		try {
			serversocket = new ServerSocket(port);
		} catch (IOException e) {
			if (debug)
				debug(e);
			info("Error during start. Terminating...");
			return;
		}
		info("Hermes was started.");
		while (true) {
			try {
				Socket s = serversocket.accept();
				new HermesHandler(s).start();
			} catch (Exception e) {
				info("" + e);
			}
		}

	}

	void debug(String s) {
		System.out
				.println("DEBUG [Hermes@" + port + "]" + new Date() + ":" + s);
	}

	void debug(Exception e) {
		System.out.println("DEBUG [Hermes@" + port + "] " + new Date() + ":");
		e.printStackTrace();
	}

	void info(String s) {
		System.out.println("INFO  [Hermes@" + port + "] " + new Date() + ":"
				+ s);
	}

	class HermesHandler extends Thread {

		final Socket s;

		/**
		 * @param s
		 */
		private HermesHandler(Socket s) {
			super();
			this.s = s;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						s.getInputStream());
				int operation = ois.readInt();
				String key = (String) ois.readObject();
				byte[] data = (byte[]) ois.readObject();
				byte[] resp = new byte[0];
				Coordinator c = Coordinator.getInstance();

				if (debug) {
					debug(key);
					debug("" + operation);
					debug(new String(data));
				}
				switch (operation) {
				case 0: // GET from external client
					resp = StorageEngine.getInstance().get(key);
					respondToClient(resp);
					break;
				case 1: // PUT from external client
					StorageEngine.getInstance().put(key, data);
					respondToClient(resp);
					// artificially delaying update propagation
					if (updatePropagationDelay > 0)
						Thread.sleep(updatePropagationDelay);
					c.forwardPut(key, data);
					break;
				case 2: // DELETE from external client
					StorageEngine.getInstance().delete(key);
					respondToClient(resp);
					c.forwardDelete(key);
					break;
				case 3: // GET from internal client
					resp = StorageEngine.getInstance().get(key);
					respondToClient(resp);
					break;
				case 4: // PUT from internal client
					StorageEngine.getInstance().put(key, data);
					respondToClient(resp);
					break;
				case 5: // DELETE from internal client
					StorageEngine.getInstance().delete(key);
					respondToClient(resp);
					break;
				case 6: // ???
				}
			} catch (Exception e) {
				info("" + e);
				e.printStackTrace();
				return;
			}

		}

		private void respondToClient(byte[] resp) throws Exception {
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(resp);
		}

	}

}
