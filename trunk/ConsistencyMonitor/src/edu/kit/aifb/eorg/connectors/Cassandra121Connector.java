package edu.kit.aifb.eorg.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.Random;
import java.util.Properties;
import java.nio.ByteBuffer;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.cassandra.thrift.*;

//XXXX if we do replication, fix the consistency levels
/**
 * Cassandra 1.0.6 client for YCSB framework
 */
public class Cassandra121Connector {
	static Random random = new Random();
	public static final int Ok = 0;
	public static final int Error = -1;
	public static final ByteBuffer emptyByteBuffer = ByteBuffer
			.wrap(new byte[0]);

	public int ConnectionRetries;
	public int OperationRetries;
	public String column_family;

	public static final String CONNECTION_RETRY_PROPERTY = "cassandra.connectionretries";
	public static final String CONNECTION_RETRY_PROPERTY_DEFAULT = "300";

	public static final String OPERATION_RETRY_PROPERTY = "cassandra.operationretries";
	public static final String OPERATION_RETRY_PROPERTY_DEFAULT = "300";

	public static final String COLUMN_FAMILY_PROPERTY = "cassandra.columnfamily";
	public static final String COLUMN_FAMILY_PROPERTY_DEFAULT = "data";

	public static final String READ_CONSISTENCY_LEVEL_PROPERTY = "cassandra.readconsistencylevel";
	public static final String READ_CONSISTENCY_LEVEL_PROPERTY_DEFAULT = "ONE";

	public static final String WRITE_CONSISTENCY_LEVEL_PROPERTY = "cassandra.writeconsistencylevel";
	public static final String WRITE_CONSISTENCY_LEVEL_PROPERTY_DEFAULT = "ONE";

	public static final String SCAN_CONSISTENCY_LEVEL_PROPERTY = "cassandra.scanconsistencylevel";
	public static final String SCAN_CONSISTENCY_LEVEL_PROPERTY_DEFAULT = "ONE";

	public static final String DELETE_CONSISTENCY_LEVEL_PROPERTY = "cassandra.deleteconsistencylevel";
	public static final String DELETE_CONSISTENCY_LEVEL_PROPERTY_DEFAULT = "ONE";

	TTransport tr;
	Cassandra.Client client;

	boolean _debug = false;

	String _table = "";
	Exception errorexception = null;

	List<Mutation> mutations = new ArrayList<Mutation>();
	Map<String, List<Mutation>> mutationMap = new HashMap<String, List<Mutation>>();
	Map<ByteBuffer, Map<String, List<Mutation>>> record = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();

	ColumnParent parent;

	ConsistencyLevel readConsistencyLevel = ConsistencyLevel.ONE;
	ConsistencyLevel writeConsistencyLevel = ConsistencyLevel.ONE;
	ConsistencyLevel scanConsistencyLevel = ConsistencyLevel.ONE;
	ConsistencyLevel deleteConsistencyLevel = ConsistencyLevel.ONE;

	/**
	 * Initialize any state for this DB. Called once per DB instance; there is
	 * one DB instance per client thread.
	 */
	public void configure(String hosts, String consistencylevel)
			throws Exception {
		if (hosts == null) {
			throw new Exception(
					"Required property \"hosts\" missing for CassandraClient");
		}

		column_family = COLUMN_FAMILY_PROPERTY_DEFAULT;
		parent = new ColumnParent(column_family);

		ConnectionRetries = Integer.parseInt(CONNECTION_RETRY_PROPERTY_DEFAULT);
		OperationRetries = Integer.parseInt(OPERATION_RETRY_PROPERTY_DEFAULT);

		readConsistencyLevel = ConsistencyLevel.valueOf(consistencylevel);
		writeConsistencyLevel = ConsistencyLevel.valueOf(consistencylevel);
		scanConsistencyLevel = ConsistencyLevel.valueOf(consistencylevel);
		deleteConsistencyLevel = ConsistencyLevel.valueOf(consistencylevel);

		String[] allhosts = hosts.split(",");
		String myhost = allhosts[random.nextInt(allhosts.length)];

		Exception connectexception = null;

		for (int retry = 0; retry < ConnectionRetries; retry++) {
			tr = new TFramedTransport(new TSocket(myhost, 9160));
			TProtocol proto = new TBinaryProtocol(tr);
			client = new Cassandra.Client(proto);
			try {
				tr.open();
				connectexception = null;
				break;
			} catch (Exception e) {
				connectexception = e;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		if (connectexception != null) {
			System.err.println("Unable to connect to " + myhost + " after "
					+ ConnectionRetries + " tries");
			throw new Exception(connectexception);
		}

	}

	/**
	 * Cleanup any state for this DB. Called once per DB instance; there is one
	 * DB instance per client thread.
	 */
	public void cleanup() throws Exception {
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
	public int read(String table, String key, String field,
			HashMap<String, String> result) {
		if (!_table.equals(table)) {
			try {
				client.set_keyspace(table);
				_table = table;
			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace(System.out);
				return Error;
			}
		}

		for (int i = 0; i < OperationRetries; i++) {

			try {
				SlicePredicate predicate;
				if (field == null) {
					predicate = new SlicePredicate()
							.setSlice_range(new SliceRange(emptyByteBuffer,
									emptyByteBuffer, false, 1000000));

				} else {
					ArrayList<ByteBuffer> fieldlist = new ArrayList<ByteBuffer>(
							1);

					fieldlist.add(ByteBuffer.wrap(field.getBytes("UTF-8")));

					predicate = new SlicePredicate().setColumn_names(fieldlist);
				}

				List<ColumnOrSuperColumn> results = client.get_slice(
						ByteBuffer.wrap(key.getBytes("UTF-8")), parent,
						predicate, readConsistencyLevel);

				if (_debug) {
					System.out.print("Reading key: " + key);
				}

				Column column;
				String name;
				String value;
				for (ColumnOrSuperColumn oneresult : results) {

					column = oneresult.column;
					name = new String(column.name.array(),
							column.name.position() + column.name.arrayOffset(),
							column.name.remaining());
					value = new String(column.value.array(),
							column.value.position()
									+ column.value.arrayOffset(),
							column.value.remaining());

					result.put(name, value);

					if (_debug) {
						System.out.print("(" + name + "=" + value + ")");
					}
				}

				if (_debug) {
					System.out.println();
					System.out.println("ConsistencyLevel="
							+ readConsistencyLevel.toString());
				}

				return Ok;
			} catch (Exception e) {
				errorexception = e;
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
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
	public int insert(String table, String key, HashMap<String, String> values) {
		if (!_table.equals(table)) {
			try {
				client.set_keyspace(table);
				_table = table;
			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace(System.out);
				return Error;
			}
		}

		for (int i = 0; i < OperationRetries; i++) {
			if (_debug) {
				System.out.println("Inserting key: " + key);
			}

			try {
				ByteBuffer wrappedKey = ByteBuffer.wrap(key.getBytes("UTF-8"));

				Column col;
				ColumnOrSuperColumn column;
				for (Map.Entry<String, String> entry : values.entrySet()) {
					col = new Column();
					col.setName(ByteBuffer.wrap(entry.getKey()
							.getBytes("UTF-8")));
					col.setValue(ByteBuffer.wrap(entry.getValue().getBytes()));
					col.setTimestamp(System.currentTimeMillis());

					column = new ColumnOrSuperColumn();
					column.setColumn(col);

					mutations.add(new Mutation()
							.setColumn_or_supercolumn(column));
				}

				mutationMap.put(column_family, mutations);
				record.put(wrappedKey, mutationMap);

				client.batch_mutate(record, writeConsistencyLevel);

				mutations.clear();
				mutationMap.clear();
				record.clear();

				if (_debug) {
					System.out.println("ConsistencyLevel="
							+ writeConsistencyLevel.toString());
				}

				return Ok;
			} catch (Exception e) {
				errorexception = e;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}

		errorexception.printStackTrace();
		errorexception.printStackTrace(System.out);
		return Error;
	}

	/**
	 * Delete a record from the database.
	 * 
	 * @param table
	 *            The name of the table
	 * @param key
	 *            The record key of the record to delete.
	 * @return Zero on success, a non-zero error code on error
	 */
	public int delete(String table, String key) {
		if (!_table.equals(table)) {
			try {
				client.set_keyspace(table);
				_table = table;
			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace(System.out);
				return Error;
			}
		}

		for (int i = 0; i < OperationRetries; i++) {
			try {
				client.remove(ByteBuffer.wrap(key.getBytes("UTF-8")),
						new ColumnPath(column_family),
						System.currentTimeMillis(), deleteConsistencyLevel);

				if (_debug) {
					System.out.println("Delete key: " + key);
					System.out.println("ConsistencyLevel="
							+ deleteConsistencyLevel.toString());
				}

				return Ok;
			} catch (Exception e) {
				errorexception = e;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		errorexception.printStackTrace();
		errorexception.printStackTrace(System.out);
		return Error;
	}

}
