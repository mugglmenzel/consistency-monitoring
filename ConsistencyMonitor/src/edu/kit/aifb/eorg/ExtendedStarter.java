package edu.kit.aifb.eorg;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.kit.aifb.eorg.cloudpolling.MiniPoller;
import edu.kit.aifb.eorg.cloudpolling.S3Poller;
import edu.kit.aifb.eorg.cloudwriter.MiniWriter;
import edu.kit.aifb.eorg.cloudwriter.S3Writer;
import edu.kit.aifb.eorg.datacollector.CollectorStarter;
import edu.kit.aifb.eorg.mini.LogEngine;
import edu.kit.aifb.eorg.mini.Starter;
import edu.kit.aifb.eorg.mini.StorageEngine;

/**
 * 
 */

/**
 * This class pulls information from the internet and starts MiniStorage and
 * consistency monitoring instances.
 * 
 * @author David Bermbach
 * 
 *         created on: 01.07.2011
 */
public class ExtendedStarter {

	private static String awspublic;
	private static String awsprivate;
	private static String miniloc1;
	private static String miniloc2;
	private static String miniloc3;
	private static String filename;
	private static String bucketname;
	private static long pollinterval;
	private static long writeinterval;
	private static String collectorurl;
	private static long start;
	private static String miniport;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out
					.println("Start with parameters <id> and <url of config file>");
			System.exit(-1);
		}
		String id = args[0];
		String configUrl = args[1];
		List<String> config = readConfigFile(configUrl);
		// System.out.println(config);
		String line = config.remove(0);
		awspublic = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		awsprivate = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		miniloc1 = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		miniloc2 = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		miniloc3 = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		miniport = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		filename = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		bucketname = line.substring(line.indexOf(":") + 1);
		line = config.remove(0);
		pollinterval = Long.parseLong(line.substring(line.indexOf(":") + 1));
		line = config.remove(0);
		writeinterval = Long.parseLong(line.substring(line.indexOf(":") + 1));
		line = config.remove(0);
		collectorurl = line.substring(line.indexOf(":") + 1);
		String[] params;
		while (config.size() > 0) {
			line = config.remove(0);
			if (line.startsWith("id")) {
				line = line.substring(line.indexOf(":") + 1);
				if (line.equals(id)) {
					// found our template
					line = config.remove(0);
					line = line.substring(line.indexOf(":") + 1);
					if (line.equals("collector")) {
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						line = config.remove(0);
						String endpoint = line.substring(line.indexOf(":") + 1);
						line = config.remove(0);
						String outputfile = line
								.substring(line.indexOf(":") + 1);
						params = new String[2];
						params[0] = endpoint;
						params[1] = outputfile;
						countdown(start);
						CollectorStarter.main(params);
						return;
					} else if (line.equals("ministorage")) {
						LogEngine.createInstance("output.csv");
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						line = config.remove(0);
						String other1 = line.substring(line.indexOf(":") + 1);
						line = config.remove(0);
						String other2 = line.substring(line.indexOf(":") + 1);
						line = config.remove(0);
						boolean inMemory = Boolean.parseBoolean(line
								.substring(line.indexOf(":") + 1));
						params = new String[5];
						params[0] = miniport;
						params[1] = other1;
						params[2] = miniport;
						params[3] = other2;
						params[4] = miniport;
						StorageEngine.doInMemoryStorage = inMemory;
						countdown(start);
						Starter.main(params);
						return;
					} else if (line.equals("s3writer")) {
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						params = new String[6];
						params[0] = collectorurl;
						params[1] = "" + writeinterval;
						params[2] = filename;
						params[3] = "Write Duration";
						params[4] = bucketname;
						params[5] = awspublic;
						params[6] = awsprivate;
						countdown(start);
						S3Writer writer = new S3Writer();
						writer.runWriter(params);
						return;
					} else if (line.equals("miniwriter")) {
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						params = new String[10];
						params[0] = collectorurl;
						params[1] = "" + writeinterval;
						params[2] = filename;
						params[3] = "Write Duration";
						params[4] = miniloc1;
						params[5] = miniport;
						params[6] = miniloc2;
						params[7] = miniport;
						params[8] = miniloc3;
						params[9] = miniport;
						countdown(start);
						MiniWriter writer = new MiniWriter();
						writer.runWriter(params);
						return;
					} else if (line.equals("minimonitor")) {
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						params = new String[10];
						params[0] = collectorurl;
						params[1] = "" + pollinterval;
						params[2] = filename;
						params[3] = id;
						params[4] = miniloc1;
						params[5] = miniport;
						params[6] = miniloc2;
						params[7] = miniport;
						params[8] = miniloc3;
						params[9] = miniport;
						countdown(start);
						MiniPoller poller = new MiniPoller();
						poller.runPoller(params);
						return;
					} else if (line.equals("s3poller")) {
						line = config.remove(0);
						line = line.substring(line.indexOf(":") + 1);
						start = Long.parseLong(line);
						params = new String[7];
						params[0] = collectorurl;
						params[1] = "" + pollinterval;
						params[2] = filename;
						params[3] = id;
						params[4] = bucketname;
						params[5] = awspublic;
						params[6] = awsprivate;
						countdown(start);
						S3Poller poller = new S3Poller();
						poller.runPoller(params);
						return;
					} else {
						System.out.println("Did not recognize role " + line);
						System.exit(-1);
					}
				} else
					continue;
			}
		}

	}

	private static List<String> readConfigFile(String configUrl)
			throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(configUrl)
				.openConnection();
		InputStream inputStream = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		List<String> res = new ArrayList<String>();
		String s = "", line = br.readLine();
		if (line == null)
			throw new Exception();
		while (line != null) {
			s += "\n" + line;
			if (!(line.startsWith("#") || line.trim().length() == 0 || line
					.contains("shared"))) {
				res.add(line);
			}
			line = br.readLine();
		}
		if (s.length() == 0)
			throw new Exception();
		// System.out.println("Config File:\n\n" + s);
		return res;

	}

	private static void countdown(long duration) throws Exception {
		System.out.println("Starting in " + duration + " second(s).");
		while(duration>10){
			//sleep 10 seconds
			duration -=10;
			Thread.sleep(10000);
			System.out.println("Starting in "+duration+" second(s).");
		}
		Thread.sleep(duration*1000);
		System.out.println("Starting now!");
		
	}

}
