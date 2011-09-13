package edu.kit.aifb.eorg.connectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class CassandraConnector {

	static Random random = new Random();
	public static final int Ok = 0;
	public static final int Error = -1;

	public static int ConnectionRetries;
	public static int OperationRetries;
	public static ConsistencyLevel consistencyLevel;

	public static final String CONNECTION_RETRY_PROPERTY = "cassandra.connectionretries";
	public static final String CONNECTION_RETRY_PROPERTY_DEFAULT = "10";

	public static final String OPERATION_RETRY_PROPERTY = "cassandra.operationretries";
	public static final String OPERATION_RETRY_PROPERTY_DEFAULT = "10";

	public static final String CONSISTENCY_LEVEL = "cassandra.consistencylevel";
	public static final ConsistencyLevel CONSISTENCY_LEVEL_DEFAULT = ConsistencyLevel.ONE;

	static String[] hostList;
	
	static TTransport tr;
	

	/**
	 * Initialize any state for this DB. Called once per DB instance; there is
	 * one DB instance per client thread.
	 */
	public static void init(String hosts, ConsistencyLevel consistencyLvl)
			throws Exception {
		if (hosts == null) {
			throw new Exception(
					"Required property \"hosts\" missing for CassandraClient");
		}
		
		consistencyLevel = consistencyLvl;

		// System.out.println("Consistency Level: " + consistencyLevel.name());
		ConnectionRetries = Integer.parseInt(CONNECTION_RETRY_PROPERTY_DEFAULT);
		OperationRetries = Integer.parseInt(OPERATION_RETRY_PROPERTY_DEFAULT);

		hostList = hosts.split(",");
	}

	
	static Cassandra.Client getClient() throws Exception {
		
		String myhost = hostList[random.nextInt(hostList.length)];
		// System.out.println("Host: ["+myhost+"]");
		// System.exit(0);

		Exception connectexception = null;
		Cassandra.Client client = null;

		for (int retry = 0; retry < ConnectionRetries; retry++) {
			tr = new TSocket(myhost, 9160);
			TProtocol proto = new TBinaryProtocol(tr);
			client = new Cassandra.Client(proto);
			try {
				tr.open();
				connectexception = null;
				break;
			} catch (Exception e) {
				connectexception = e;
			}
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e) {}
			 */
		}
		if (connectexception != null) {
			System.err.println("Unable to connect to " + myhost + " after "
					+ ConnectionRetries + " tries");
			System.out.println("Unable to connect to " + myhost + " after "
					+ ConnectionRetries + " tries");
			throw new Exception(connectexception);
		}
		
		return client;
	}
	
	/**
	 * Cleanup any state for this DB. Called once per DB instance; there is one
	 * DB instance per client thread.
	 */
	public static void cleanup() throws Exception {
		tr.close();
	}

	/**
	 * Read a record from the database. Each field/value pair from the result
	 * will be stored in a HashMap.
	 * 
	 * @param table
	 *            The name of the table
	 * @param key
	 *            The record key of the record to read.
	 * @param fields
	 *            The list of fields to read, or null for all of them
	 * @param result
	 *            A HashMap of field/value pairs for the result
	 * @return Zero on success, a non-zero error code on error
	 */
	public static int read(String table, String key, Set<String> fields,
			Map<String, String> result) {
		Exception errorexception = null;

		try {

			Cassandra.Client client = getClient();
			
			SlicePredicate predicate;
			if (fields == null) {

				SliceRange sliceRange = new SliceRange();
				sliceRange.setStart(new byte[0]);
				sliceRange.setFinish(new byte[0]);
				sliceRange.setCount(1000000);

				predicate = new SlicePredicate();
				predicate.setSlice_range(sliceRange);
			} else {
				Vector<byte[]> fieldlist = new Vector<byte[]>();
				for (String s : fields) {
					fieldlist.add(s.getBytes("UTF-8"));
				}

				predicate = new SlicePredicate();
				predicate.setColumn_names(fieldlist);
			}

			ColumnParent parent = new ColumnParent("data");
			List<ColumnOrSuperColumn> results = client.get_slice(table, key,
					parent, predicate, consistencyLevel);

		

			for (ColumnOrSuperColumn oneresult : results) {
				Column column = oneresult.column;
				result.put(new String(column.name), new String(column.value));

			}

			return Ok;
		} catch (Exception e) {
			errorexception = e;
		}

		errorexception.printStackTrace();
		errorexception.printStackTrace(System.out);
		return Error;

	}

	/**
	 * Insert a record in the database. Any field/value pairs in the specified
	 * values HashMap will be written into the record with the specified record
	 * key.
	 * 
	 * @param table
	 *            The name of the table
	 * @param key
	 *            The record key of the record to insert.
	 * @param values
	 *            A HashMap of field/value pairs to insert in the record
	 * @return Zero on success, a non-zero error code on error
	 */
	public static int insert(String table, String key, Map<String, String> values) {
		Exception errorexception = null;

		for (int i = 0; i < OperationRetries; i++) {
			// insert data
			long timestamp = System.currentTimeMillis();

			HashMap<String, List<ColumnOrSuperColumn>> batch_mutation = new HashMap<String, List<ColumnOrSuperColumn>>();
			Vector<ColumnOrSuperColumn> v = new Vector<ColumnOrSuperColumn>();
			batch_mutation.put("data", v);

			try {
				Cassandra.Client client = getClient();
				
				for (String field : values.keySet()) {
					String val = values.get(field);
					Column col = new Column(field.getBytes("UTF-8"),
							val.getBytes("UTF-8"), timestamp);

					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setColumn(col);
					c.unsetSuper_column();
					v.add(c);
				}

				client.batch_insert(table, key, batch_mutation,
						consistencyLevel);


				return Ok;
			} catch (Exception e) {
				errorexception = e;
			}
		}

		errorexception.printStackTrace();
		errorexception.printStackTrace(System.out);
		return Error;
	}


}