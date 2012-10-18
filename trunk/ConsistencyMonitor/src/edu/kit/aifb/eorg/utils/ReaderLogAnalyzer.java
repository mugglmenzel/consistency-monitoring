/**
 * 
 */
package edu.kit.aifb.eorg.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * This class reads and parses the individual reader log files and analyzes them
 * for
 * 
 * * monotonic read consistency violatiosn <br>
 * * read errors<br>
 * * more to come later
 * 
 * 
 * @author David Bermbach
 * 
 *         created on: 07.09.2011
 */
public class ReaderLogAnalyzer {

	/** For old log files without latencies set to true */
	private final static boolean useOldFormat = false;

	private final static String pathPrefix ="c:/temp/";
	
	/** list of log files */
	private final static String[] logfiles = { 
		pathPrefix+"1a.csv", pathPrefix+"2a.csv",pathPrefix+"3a.csv", pathPrefix+"4a.csv",
		pathPrefix+"5b.csv", pathPrefix+"6b.csv",pathPrefix+"7b.csv", pathPrefix+"8b.csv",
		pathPrefix+"9c.csv", pathPrefix+"10c.csv",pathPrefix+"11c.csv", pathPrefix+"12c.csv"
		
		/*
												 * "c:/temp/mon01.csv",
												 * "c:/temp/mon02.csv",
												 * "c:/temp/mon03.csv",
												 * "c:/temp/mon04.csv",
												 * "c:/temp/mon05.csv",
												 * "c:/temp/mon06.csv",
												 * "c:/temp/mon07.csv",
												 * "c:/temp/mon08.csv",
												 * "c:/temp/mon09.csv",
												 * "c:/temp/mon10.csv",
												 * "c:/temp/mon11.csv",
												 * "c:/temp/mon12.csv"
												 */
	/*"c:/temp/mon1.csv", "c:/temp/mon2.csv", "c:/temp/mon3.csv",
			"c:/temp/mon4.csv", "c:/temp/mon5.csv", "c:/temp/mon6.csv",
			"c:/temp/mon7.csv", "c:/temp/mon8.csv", "c:/temp/mon9.csv",
			"c:/temp/mon10.csv", "c:/temp/mon11.csv", "c:/temp/mon12.csv"*/

	/*
	 * "c:/temp/mon1b.csv", "c:/temp/mon2b.csv", "c:/temp/mon3b.csv",
	 * "c:/temp/mon4b.csv", "c:/temp/mon5b.csv", "c:/temp/mon6b.csv",
	 * "c:/temp/mon7b.csv", "c:/temp/mon8b.csv", "c:/temp/mon9b.csv",
	 */
	/*
	 * "c:/temp/mon1a.csv", "c:/temp/mon2a.csv", "c:/temp/mon3a.csv",
	 * "c:/temp/mon4a.csv", "c:/temp/mon5a.csv", "c:/temp/mon6a.csv",
	 * "c:/temp/mon7a.csv", "c:/temp/mon8a.csv", "c:/temp/mon9a.csv"
	 */};

	/** all latencies are written to this file */
	private final static File latencyOutFile = new File("c:/temp/latencies.txt");
	/** all violations of monotonic read consistency are written to this file */
	private final static File monotonicOutFile = new File(
			"c:/temp/monotonicReadViolations.txt");
	/**
	 * the distribution of stale and fresh reads over time is written to this
	 * file
	 */
	private final static File consistenciesOutFile = new File(
			"c:/temp/consistencies.txt");

	/**
	 * interval size in ms used to calculate the probability of reading an old
	 * version based on time elapsed since update
	 */
	private final static int intervalSize = 5;
	private final static TreeMap<Long, Long> consistentReads = new TreeMap<Long, Long>();
	private final static TreeMap<Long, Long> staleReads = new TreeMap<Long, Long>();
	private final static HashMap<Long, Long> writeTimestamps = new HashMap<Long, Long>();
	private final static TreeSet<Long> intervals = new TreeSet<Long>();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		getWriteTimestamps();
		if (useOldFormat)
			analyzeOldLog();
		else {
			analyzeNewLogs();
		}
		System.out.println("Analysis completed.");
	}

	private static void getWriteTimestamps() throws Exception {
		System.out.println("Extracting all write timestamps from the logs");
		for (String file : logfiles) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			System.out.println("Extracting from file " + file);
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			String line = br.readLine();
			int pos = 0, pos2 = 0;
			long version = -1;
			long writeTimestamp = 0;
			int linecounter = 0;
			while (line != null) {
				try {
					linecounter++;
					pos = line.indexOf(":");
					pos2 = line.indexOf(" ");
					version = Long.parseLong(line.substring(pos + 1, pos2));
					if (line.matches("(\\d)*"))
						writeTimestamp = Long.parseLong(line
								.substring(pos2 + 1));
					else {
						line = line.substring(pos2 + 1);
						writeTimestamp = Long.parseLong(line.substring(0,
								line.indexOf(" ")));
					}
					writeTimestamps.put(version, writeTimestamp);
				} catch (Exception e) {
					System.out.println("Exception in line " + linecounter
							+ " of file " + file);
					System.out.println("line was:\n" + line);
					e.printStackTrace();
				}
				line = br.readLine();
			}
			br.close();
		}
		System.out.println("Done extracting " + writeTimestamps.size()
				+ " write timestamps. Now, analyzing logs.");
	}

	private static void analyzeNewLogs() throws Exception {
		PrintWriter latencyPrinter = new PrintWriter(latencyOutFile);
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("Log_ID Reads Errors Violations");
		for (String file : logfiles) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			System.out.println("Evaluating file " + file);
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			String line = br.readLine();
			int pos = 0, pos2 = 0;
			int counter = 1;
			long version = -1, oldversion = -1;
			long errors = 0, nonMonotonicReads = 0, diff = 0, latency;
			long readTimestamp = 0;
			Long latestWriteTimestamp = null, latestVersion = null, oldval = null, timestamp = null;
			while (line != null) {
				if (counter % 5000000 == 0)
					System.out.println("Processing line " + counter);
				try {
					pos = line.indexOf(":");
					pos2 = line.indexOf(" ");
					version = Long.parseLong(line.substring(pos + 1, pos2));
					readTimestamp = Long.parseLong(line.substring(0, pos));
					line = line.substring(pos2);
					pos = line.indexOf(":");
					if (pos == -1) {
						System.out
								.println("The log file does not contain latencies. Use old mode instead. Terminating evaluation.");
						return;
					}
					latency = Long.parseLong(line.substring(pos + 1).trim());
					latencyPrinter.println(latency);
					// System.out.println(line + " ->"+ readTimestamp +
					// ": "+version+" "+ writeTimestamp);
					latestWriteTimestamp = null;
					for (long i = version - 10; i <= version + 10; i++) {
						// analyze 10 preceding and succeding writes of this
						// version and find timestamp of last write before this
						// read
						timestamp = writeTimestamps.get(i);
						// System.out.println(timestamp);
						if (timestamp == null)
							continue;
						if (timestamp <= readTimestamp) {
							latestWriteTimestamp = timestamp;
							latestVersion = i;
						} else {
							// write timestamps are increasing monotonically
							break;
						}
					}
					// System.out.println("latest timestamp for read "
					// + readTimestamp + " is "+ latestWriteTimestamp);
					if (latestWriteTimestamp != null) {
						// it's not the head of a log file => we can analyze the
						// probabilities of a stale reads over time
						diff = readTimestamp - latestWriteTimestamp;
						long index = diff / intervalSize;
						intervals.add(index);
						if (version >= latestVersion) {
							// System.out.println("FRESH: "+ version + " " +
							// latestVersion);
							// fresh read
							oldval = consistentReads.get(index);
							if (oldval == null)
								consistentReads.put(index, 1L);
							else
								consistentReads.put(index, oldval + 1L);
						} else {
							// System.out.println("STALE: " + version + " "+
							// latestVersion);
							// stale read
							oldval = staleReads.get(index);
							if (oldval == null)
								staleReads.put(index, 1L);
							else
								staleReads.put(index, oldval + 1L);
						}
					}

					// check for violations of monotonic read consistency
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
			lines.add(file + " " + (counter - 1) + " " + errors + " "
					+ nonMonotonicReads);
			System.out.println("Done processing " + (counter - 1) + " reads");
			System.out.println("Found " + errors + " error reads");
			System.out.println("Found " + nonMonotonicReads
					+ " violations of monotonic read consistency");
			br.close();
		}
		System.out
				.println("Persisting results for monotonic read consistency violations:");
		PrintWriter pw = new PrintWriter(monotonicOutFile);
		for (String s : lines) {
			pw.println(s);
		}
		pw.close();
		System.out
				.println("Persisting results for distribution of stale vs. fresh reads over time");
		pw = new PrintWriter(consistenciesOutFile);
		pw.println("Lower_Value_(included) Higher_Value_(Not_Included) #Fresh_Reads #Stale_Reads");
		long lastinterval = -1;
		Long stale = null, fresh = null;
		String print = null;
		for (long l : intervals) {
			if (l > lastinterval + 1L) {
				for (long i = lastinterval + 1; i < l; i++) {
					pw.println(i * intervalSize + " "
							+ ((i + 1) * intervalSize) + " 0 0");
				}
			}
			fresh = consistentReads.get(l);
			stale = staleReads.get(l);
			print = (l * intervalSize) + " " + ((l + 1) * intervalSize);
			if (fresh == null)
				print += " 0";
			else
				print += " " + fresh;
			if (stale == null)
				print += " 0";
			else
				print += " " + stale;
			pw.println(print);
			// System.out.println(print);
			lastinterval = l;
		}
		latencyPrinter.close();
		pw.close();
	}

	private static void analyzeOldLog() throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("Log_ID Reads Errors Violations");
		for (String file : logfiles) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			System.out.println("Evaluating file " + file);
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			String line = br.readLine();
			int pos = 0, pos2 = 0;
			int counter = 1;
			long version = -1, oldversion = -1;
			long errors = 0, nonMonotonicReads = 0, diff = 0;
			long readTimestamp = 0;
			Long latestWriteTimestamp = null, latestVersion = null, oldval = null, timestamp = null;
			while (line != null) {
				if (counter % 5000000 == 0)
					System.out.println("Processing line " + counter);
				try {
					pos = line.indexOf(":");
					pos2 = line.indexOf(" ");
					version = Long.parseLong(line.substring(pos + 1, pos2));
					readTimestamp = Long.parseLong(line.substring(0, pos));
					// System.out.println(line + " ->"+ readTimestamp +
					// ": "+version+" "+ writeTimestamp);
					latestWriteTimestamp = null;
					for (long i = version - 10; i <= version + 10; i++) {
						// analyze 10 preceding and succeding writes of this
						// version and find timestamp of last write before this
						// read
						timestamp = writeTimestamps.get(i);
						// System.out.println(timestamp);
						if (timestamp == null)
							continue;
						if (timestamp <= readTimestamp) {
							latestWriteTimestamp = timestamp;
							latestVersion = i;
						} else {
							// write timestamps are increasing monotonically
							break;
						}
					}
					// System.out.println("latest timestamp for read " +
					// readTimestamp + " is "+ latestWriteTimestamp);
					if (latestWriteTimestamp != null) {
						// it's not the head of a log file => we can analyze the
						// probabilities of a stale reads over time
						diff = readTimestamp - latestWriteTimestamp;
						long index = diff / intervalSize;
						intervals.add(index);
						if (version >= latestVersion) {
							// fresh read
							oldval = consistentReads.get(index);
							if (oldval == null)
								consistentReads.put(index, 1L);
							else
								consistentReads.put(index, oldval + 1L);
						} else {
							// stale read
							oldval = staleReads.get(index);
							if (oldval == null)
								staleReads.put(index, 1L);
							else
								staleReads.put(index, oldval + 1L);
						}
					}

					// check for violations of monotonic read consistency
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
			lines.add(file + " " + (counter - 1) + " " + errors + " "
					+ nonMonotonicReads);
			System.out.println("Done processing " + (counter - 1) + " reads");
			System.out.println("Found " + errors + " error reads");
			System.out.println("Found " + nonMonotonicReads
					+ " violations of monotonic read consistency");
			br.close();
		}
		System.out
				.println("Persisting results for monotonic read consistency violations:");
		PrintWriter pw = new PrintWriter(monotonicOutFile);
		for (String s : lines) {
			pw.println(s);
		}
		pw.close();
		System.out
				.println("Persisting results for distribution of stale vs. fresh reads over time");
		pw = new PrintWriter(consistenciesOutFile);
		pw.println("Lower_Value_(included) Higher_Value_(Not_Included) #Fresh_Reads #Stale_Reads");
		long lastinterval = -1;
		Long stale = null, fresh = null;
		String print = null;
		for (long l : intervals) {
			if (l > lastinterval + 1L) {
				for (long i = lastinterval + 1; i < l; i++) {
					pw.println(i * intervalSize + " "
							+ ((i + 1) * intervalSize) + " 0 0");
				}
			}
			fresh = consistentReads.get(l);
			stale = staleReads.get(l);
			print = l * intervalSize + " " + ((l + 1) * intervalSize);
			if (fresh == null)
				print += " 0";
			else
				print += " " + fresh;
			if (stale == null)
				print += " 0";
			else
				print += " " + stale;
			pw.println(print);
			lastinterval = l;
		}
	}
}
