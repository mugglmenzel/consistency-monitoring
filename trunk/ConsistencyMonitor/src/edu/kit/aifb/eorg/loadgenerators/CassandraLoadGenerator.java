/**
 * 
 */
package edu.kit.aifb.eorg.loadgenerators;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import edu.kit.aifb.eorg.connectors.Cassandra121Connector;

/**
 * @author David Bermbach
 * 
 *         created on: 20.09.2011
 */
public class CassandraLoadGenerator {

	private RandomAccessFile outfile;
	private String field;
	private int noOfWriteThreads = 0;
	private int noOfReadThreads = 0;
	private int sizeForWrites = 0;
	private Random rand = new Random();

	private Long reads = 0L;
	private Long writes = 0L;
	private Long readTries = 0L;
	private Long writeTries = 0L;

	private Cassandra121Connector conn = new Cassandra121Connector();
	
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
		field="loadKey";
		conn
				.configure(hosts, consistencyLvl);
		this.sizeForWrites = sizeForWrites;
		outfile = new RandomAccessFile("loadgenerator.csv", "rw");
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
				private HashMap<String, String> values = new HashMap<String, String>();

				@Override
				public void run() {
//					System.out.println("New write thread started!");
					while (true) {
						try {
							values.clear();
							byte[] payload = new byte[sizeForWrites];
							rand.nextBytes(payload);
							values.put("loadKey", new String(payload));
							if (conn.insert("usertable",
									"loadKey", values) == Cassandra121Connector.Error)
								throw new RuntimeException(
										"A write has returned an error.");
							synchronized (writes) {
								writes++;
							}
						} catch (Exception e) {
							System.out.println("Caught exception of type "
									+ e.getMessage());
							synchronized (writes) {
								writeTries++;
							}
						}
					}
				}
			}).start();
		}
		for (int i = 0; i < noOfReadThreads; i++) {
			new Thread(new Runnable() {
				private HashMap<String, String> result = new HashMap<String, String>();

				@Override
				public void run() {
//					System.out.println("New read thread started.");
					while (true) {
						try {
							result.clear();
							if (conn.read("usertable", "loadKey",
									field, result) == Cassandra121Connector.Error)
								throw new RuntimeException(
										"A read has returned an error.");
							synchronized (reads) {
								reads++;
							}
						} catch (Exception e) {
							System.out.println("Caught exception of type "
									+ e.getMessage());
							synchronized (reads) {
								readTries++;
							}
						}

					}
				}
			}).start();
		}
		long oldReads = 0, oldWrites = 0;
		double load = 0;
		while (true) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
			String log = null;
			synchronized (reads) {
				diff = (new Date().getTime() - readStart.getTime()) / 1000.0;
				load = (reads - oldReads) / diff;
//				System.out
//						.println("diff=" + diff + ", reads=" + reads
//								+ ", oldreads=" + oldReads + ", readTries="
//								+ readTries);
				log = "Current read load: " + load + " req./s (an additional "
						+ (readTries / diff) + " req./s failed.";
				System.out.println(log);
				oldReads = reads;
				readTries = 0L;
				readStart = new Date();
			}
			try {
				outfile.writeBytes(new Date() + ": " + log + "\n");
			} catch (IOException e) {
				System.out.println("Could not log:" + log);
			}
			synchronized (writes) {
				diff = (new Date().getTime() - writeStart.getTime()) / 1000.0;
				load = (writes - oldWrites) / diff;
				// System.out.println("diff=" + diff + ", writes=" + writes
				// + ", oldwrites=" + oldWrites + ", writeTries="
				// + writeTries);
				log = "Current write load: " + load + " req./s (an additional "
						+ (writeTries / diff) + " req./s failed.";
				System.out.println(log);
				oldWrites = writes;
				writeTries = 0L;
				writeStart = new Date();
			}
			try {
				outfile.writeBytes(new Date() + ": " + log + "\n.");
			} catch (IOException e) {
				System.out.println("Could not log write load of " + load
						+ "req./s");
			}
		}

	}

}
