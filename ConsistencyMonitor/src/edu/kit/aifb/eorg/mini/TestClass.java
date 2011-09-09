/**
 * 
 */
package edu.kit.aifb.eorg.mini;

import java.io.FileNotFoundException;

/**
 * @author David Bermbach
 *
 * created on: 13.05.2011
 */
public class TestClass {
	
	public static void main(String[] args) {
//		new StorageEngine().put("testfile", "testcontent".getBytes());
//		System.out.println(new String(new StorageEngine().get("testfile")));
		try {
			LogEngine.createInstance("output.csv");
		} catch (FileNotFoundException e1) {
			System.out.println("LogEngine could not be created:" + e1
					+ "\n\tThe system is shutting down.");
			System.exit(-1);
		}
		Hermes h = new Hermes();
		new Thread(h).start();
		
	}

}
