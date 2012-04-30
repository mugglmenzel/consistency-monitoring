/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;

/**
 * responsible for storing data
 * 
 * @author David Bermbach
 * 
 *         created on: 13.05.2011
 */
public class StorageEngine {

	private static final StorageEngine instance = new StorageEngine();

	private static LogEngine log = LogEngine.getInstance();

	private boolean debug = true;

	/** if true MiniStorage will be an in memory database only */
	public static boolean doInMemoryStorage = true;

	private final HashMap<String, byte[]> inMemoryDB = new HashMap<String, byte[]>();

	/** output directory for all persisted data */
	private String directory = ".";

	/**
	 * 
	 * @return the singleton
	 */
	public static StorageEngine getInstance() {
		return instance;
	}

	/**
	 * persists the value in a file of name key
	 * 
	 * 
	 * @param key
	 * @param value
	 */
	void put(String key, byte[] value) {
		if (value == null)
			return;
		if (doInMemoryStorage) {
			synchronized (inMemoryDB) {
				inMemoryDB.put(key, value);
			}
			log.log(new Date().getTime(), new String(value));
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(new File(directory
					+ "/" + key));
			fos.write(value);
			fos.close();
			log.log(new Date().getTime(), new String("stored: " + value));
		} catch (Exception e) {
			if (debug)
				debug(e);
		}
	}

	/**
	 * retrieves the content of the file with name key
	 * 
	 * @param key
	 * @return
	 */
	byte[] get(String key) {
		if (doInMemoryStorage) {
			synchronized (inMemoryDB) {
				return inMemoryDB.get(key);
			}
		}
		try {
			File f = new File(directory + "/" + key);
			FileInputStream fis = new FileInputStream(f);
			byte[] res = new byte[(int) f.length()];
			fis.read(res);
			return res;
		} catch (Exception e) {
			if (debug)
				debug(e);
			return null;
		}
	}

	/**
	 * deletes the file with name key
	 * 
	 * @param key
	 */
	void delete(String key) {
		if (doInMemoryStorage) {
			synchronized (inMemoryDB) {
				inMemoryDB.remove(key);
			}
			return;
		}
		File f = new File(directory + "/" + key);
		f.delete();

	}

	void debug(String s) {
		System.out.println("DEBUG [StorageEngine] " + new Date() + ":" + s);
	}

	void debug(Exception e) {
		System.out.println("DEBUG [StorageEngine] " + new Date() + ":");
		e.printStackTrace();
	}

	void info(String s) {
		System.out.println("INFO  [StorageEngine] " + new Date() + ":" + s);
	}

}
