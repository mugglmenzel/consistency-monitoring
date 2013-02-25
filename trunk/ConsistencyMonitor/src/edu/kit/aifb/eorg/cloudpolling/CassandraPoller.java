/**
 * 
 */
package edu.kit.aifb.eorg.cloudpolling;

import java.util.Date;
import java.util.HashMap;

import edu.kit.aifb.eorg.connectors.Cassandra121Connector;

/**
 * @author mugglmenzel
 *
 */
public class CassandraPoller extends AbstractPoller {

	private String field = "timestamp";
	private HashMap<String, String> result = new HashMap<String, String>();
	private Cassandra121Connector connector;
	
	
	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudpolling.AbstractPoller#readFromCloud(java.lang.String)
	 */
	/**
	 * 
	 */
	public CassandraPoller() {
		super();
		connector = new Cassandra121Connector();
	}

	@Override
	public String readFromCloud(String key) {
		result.clear();
		connector.read("usertable", key, field, result);
		return result.get("timestamp");
	}

	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudpolling.AbstractPoller#configure(java.lang.String[])
	 */
	@Override
	public void configure(String[] args) throws Exception {
		connector.configure(args[0], args[1]);
	}

	public static void main(String[] args) throws Exception {
		String hosts = "ec2-54-241-222-33.us-west-1.compute.amazonaws.com";
		String consistency = "ONE";
		String [] params = {hosts,consistency};
		CassandraPoller cp = new CassandraPoller();
		cp.configure(params);
		Date d = new Date();
		System.out.println(cp.readFromCloud("testkey"));
		System.out.println(new Date().getTime()-d.getTime());
	}
	
}
