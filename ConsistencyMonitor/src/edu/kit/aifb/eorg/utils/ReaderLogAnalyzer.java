/**
 * 
 */
package edu.kit.aifb.eorg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 
 * This class reads and parses the individual reader log files and analyzes them
 * for
 * 
 * * monotonic read consistency violatiosn <br>* read errors<br>* more to come later
 * 
 * 
 * @author David Bermbach
 * 
 *         created on: 07.09.2011
 */
public class ReaderLogAnalyzer {

	/** log file that shall be analyzed */
	private final static File logfile = new File("c:/temp/mon11.csv");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(logfile));
		System.out.println("Evaluating file with following header:");
		System.out.println(br.readLine());
		System.out.println(br.readLine());
		System.out.println(br.readLine());
		System.out.println(br.readLine());
		String line = br.readLine();
		int pos = 0, pos2 = 0;
		int counter = 1;
		long version = -1, oldversion = -1;
		long errors = 0, nonMonotonicReads = 0;
		while (line != null) {
			if (counter % 5000000 == 0)
				System.out.println("Processing line " + counter);
			try {
				pos = line.indexOf(":");
				pos2 = line.indexOf(" ");
				version = Long.parseLong(line.substring(pos + 1, pos2));
				if (version < oldversion)
					nonMonotonicReads++;
				else
					oldversion = version;
			} catch (Exception e) {
				System.out.println("Found error read: " + line);
				errors++;
			}
			line = br.readLine();
			counter++;
		}
		System.out.println("Done processing " + (counter - 1) + " reads");
		System.out.println("Found "+errors+" error reads");
		System.out.println("Found "+nonMonotonicReads+" violations of monotonic read consistency");
		br.close();

	}
}
