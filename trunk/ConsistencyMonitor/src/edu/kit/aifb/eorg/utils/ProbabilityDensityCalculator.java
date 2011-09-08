/**
 * 
 */
package edu.kit.aifb.eorg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * aggragates data into a density function
 * 
 * @author David Bermbach
 * 
 *         created on: 08.09.2011
 */
public class ProbabilityDensityCalculator {

	/** log file that shall be analyzed */
	private final static File logfile = new File("c:/temp/data.txt");
	private final static File outfile = new File("c:/temp/out.txt");
	private final static SortedMap<Integer, Integer> densityFunction = new TreeMap<Integer, Integer>();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(logfile));
		String line = br.readLine();
		while (line != null) {
			int i = Integer.parseInt(line.trim());
			Integer old = densityFunction.get(i);
			if (old == null)
				densityFunction.put(i, 1);
			else
				densityFunction.put(i, old + 1);
			line = br.readLine();
		}
		br.close();
		PrintWriter pw = new PrintWriter(outfile);
		// print results and add zeros for unused keys
		int min = densityFunction.keySet().iterator().next();
		for (int i : densityFunction.keySet()) {
			if (i < min)
				min = i;
		}
		int lastvalue = min - 1;
		for (int i : densityFunction.keySet()) {
			
			if (i > lastvalue + 1) {
				int counter = lastvalue+1;
				while (counter < i)
					pw.println(counter++ + " " + "0");
			}
			pw.println(i + " " + densityFunction.get(i));
			lastvalue = i;
		}
		pw.close();
		System.out.println("done");

	}

}
