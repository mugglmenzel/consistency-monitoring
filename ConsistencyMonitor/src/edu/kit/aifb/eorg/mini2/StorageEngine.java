/**
 * 
 */
package edu.kit.aifb.eorg.mini2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * responsible for storing data
 * 
 * @author David Bermbach
 * 
 *         created on: 13.05.2011
 */
public class StorageEngine {

	private static final StorageEngine instance = new StorageEngine();

	private static final Logger log = Logger.getLogger(StorageEngine.class);

	/** if true MiniStorage will be an in memory database only */
	public static boolean doInMemoryStorage = true;

	private final ConcurrentHashMap<String, byte[]> inMemoryDB = new ConcurrentHashMap<String, byte[]>();

	/** output directory for all persisted data */
	private String directory = ".";

	private Integer counter = 0;
	
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
			inMemoryDB.put(key, value);
			synchronized (counter) {
				log.info(new Date().getTime() + " - version " + counter++);	
			}
			
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(new File(directory
					+ "/" + key));
			fos.write(value);
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
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
			return inMemoryDB.get(key);
		}
		try {
			File f = new File(directory + "/" + key);
			FileInputStream fis = new FileInputStream(f);
			byte[] res = new byte[(int) f.length()];
			fis.read(res);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
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
			inMemoryDB.remove(key);
			return;
		}
		File f = new File(directory + "/" + key);
		f.delete();

	}

}
