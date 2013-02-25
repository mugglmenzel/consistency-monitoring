/**
 * 
 */
package edu.kit.aifb.eorg.cloudwriter;

import java.util.HashMap;

import edu.kit.aifb.eorg.connectors.Cassandra121Connector;

/**
 * @author mugglmenzel
 *
 */
public class CassandraWriter extends AbstractWriter {

	private HashMap<String, String> values = new HashMap<String, String>();
	
	private Cassandra121Connector connector;
	
	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudwriter.AbstractWriter#writeToCloud(java.lang.String, java.lang.String)
	 */
	@Override
	public void writeToCloud(String key, String value) {
		values.clear();
		values.put("timestamp", value);
		System.out.println(connector.insert("usertable", key, values));

	}

	/* (non-Javadoc)
	 * @see edu.kit.aifb.eorg.cloudwriter.AbstractWriter#configure(java.lang.String[])
	 */
	@Override
	public void configure(String[] args) throws Exception {
		connector = new Cassandra121Connector();
		connector.configure(args[0], args[1]);
	}

	public static void main(String[] args) throws Exception {
		String hosts = "ec2-54-241-222-33.us-west-1.compute.amazonaws.com";
		String consistency = "ONE";
		String [] params = {hosts,consistency};
		CassandraWriter cw = new CassandraWriter();
		cw.configure(params);
		cw.writeToCloud("testkey", "some new value");
	}
	
}
