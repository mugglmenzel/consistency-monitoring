/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import java.util.HashMap;
import java.util.Map;

import org.apache.cassandra.thrift.ConsistencyLevel;

import edu.kit.aifb.eorg.connectors.CassandraConnector;

/**
 * @author mugglmenzel
 *
 */
public class CassandraWriter extends AbstractWriter {

	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudwriter.AbstractWriter#writeToCloud(java.lang.String, java.lang.String)
	 */
	@Override
	protected void writeToCloud(String key, String value) {
		//TODO: table name in cassandra DB
		Map<String, String> values = new HashMap<String, String>();
		values.put("timestamp", value);
		CassandraConnector.insert("usertable", key, values);

	}

	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudwriter.AbstractWriter#configure(java.lang.String[])
	 */
	@Override
	protected void configure(String[] args) throws Exception {
		CassandraConnector.init(args[0], ConsistencyLevel.valueOf(args[1]));
	}

}
