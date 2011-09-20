/**
 * 
 */
package edu.kit.aifb.eorg.loadgenerators;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.cassandra.thrift.ConsistencyLevel;

import edu.kit.aifb.eorg.connectors.CassandraConnector;

/**
 * @author David Bermbach
 * 
 *         created on: 20.09.2011
 */
public class CassandraLoadGenerator {

	private Set<String> fields = new HashSet<String>();
	private int noOfWriteThreads = 0;
	private int noOfReadThreads = 0;
	private int sizeForWrites = 0;
	private Random rand = new Random();

	private Long reads = 0L;
	private Long writes = 0L;

	/**
	 * @param fields
	 * @param result
	 * @param noOfWriteThreads
	 * @param noOfReadThreads
	 */
	public CassandraLoadGenerator(int noOfWriteThreads, int noOfReadThreads,
			String hosts, String consistencyLvl, int sizeForWrites)
			throws Exception {
		this.noOfWriteThreads = noOfWriteThreads;
		this.noOfReadThreads = noOfReadThreads;
		fields.add("timestamp");
		CassandraConnector
				.init(hosts, ConsistencyLevel.valueOf(consistencyLvl));
		this.sizeForWrites = sizeForWrites;
		System.out.println("CassandraLoadGenerator fully configured.");
	}

	public void createLoad() {
		System.out.println("Now creating load with " + noOfWriteThreads
				+ " write threads (payload size is " + sizeForWrites
				+ " Bytes) and " + noOfReadThreads + " read threads.");
		Date readStart = new Date(), writeStart = new Date();
		double diff = 0;
		for (int i = 0; i < noOfWriteThreads; i++) {
			new Thread(new Runnable() {
				private Map<String, String> values = new HashMap<String, String>();

				@Override
				public void run() {
					while (true) {
						values.clear();
						byte[] payload = new byte[sizeForWrites];
						rand.nextBytes(payload);
						values.put("loadKey", new String(payload));
						CassandraConnector.insert("usertable", "loadKey",
								values);
						synchronized (writes) {
							writes++;
						}
					}
				}
			}).start();
		}
		for (int i = 0; i < noOfReadThreads; i++) {
			new Thread(new Runnable() {
				private Map<String, String> result = new HashMap<String, String>();

				@Override
				public void run() {
					result.clear();
					CassandraConnector.read("usertable", "loadKey", fields,
							result);
					synchronized (reads) {
						reads++;
					}
				}
			}).start();
		}
		long oldReads = 0, oldWrites = 0;
		while (true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
			synchronized (reads) {
				diff = (new Date().getTime() - readStart.getTime()) / 1000.0;
				System.out.println("Current read load: "
						+ ((reads - oldReads) / diff) + " req./s");
				oldReads = reads;
				readStart = new Date();
			}
			synchronized (writes) {
				diff = (new Date().getTime() - writeStart.getTime()) / 1000.0;
				System.out.println("Current write load: "
						+ ((writes - oldWrites) / diff) + " req./s");
				oldWrites = writes;
				writeStart = new Date();
			}
		}

	}

}
