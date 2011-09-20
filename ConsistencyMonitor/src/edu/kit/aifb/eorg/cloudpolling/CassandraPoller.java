/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.cassandra.thrift.ConsistencyLevel;

import edu.kit.aifb.eorg.connectors.CassandraConnector;

/**
 * @author mugglmenzel
 *
 */
public class CassandraPoller extends AbstractPoller {

	private Set<String> fields = new HashSet<String>();
	private Map<String, String> result = new HashMap<String, String>();
	
	
	
	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudpolling.AbstractPoller#readFromCloud(java.lang.String)
	 */
	/**
	 * 
	 */
	public CassandraPoller() {
		super();
		fields.add("timestamp");
	}

	@Override
	public String readFromCloud(String key) {
		result.clear();
		CassandraConnector.read("usertable", key, fields, result);
		
		return result.get("timestamp");
	}

	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudpolling.AbstractPoller#configure(java.lang.String[])
	 */
	@Override
	public void configure(String[] args) throws Exception {
		CassandraConnector.init(args[0], ConsistencyLevel.valueOf(args[1]));
	}

}
