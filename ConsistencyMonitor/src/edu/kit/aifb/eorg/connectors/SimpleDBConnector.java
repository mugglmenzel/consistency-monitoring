/**
 * 
 */
package edu.kit.aifb.eorg.connectors;

import java.util.ArrayList;
import java.util.List;
/**
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
*/
/**
 * 
 * This class provides some very fast but hard-coded access methods for S3
 * 
 * @author Jasmin Giemsch
 * 
 *         created on: 25.11.2011
 */
public final class SimpleDBConnector {

	private static AmazonSimpleDBClient sdb;
	
	//private static AWSAuthConnection aws;

	public final static void doInitialize(String secretkey,
			String publickey) throws Exception {
		
		sdb = new AmazonSimpleDBClient(new BasicAWSCredentials(publickey, secretkey));;
		
	}

	public final static void writeToSDB(final String domain, final String key, final String value) {
		try {
			
			sdb.createDomain(new CreateDomainRequest(domain));

			List<ReplaceableItem> data = new ArrayList<ReplaceableItem>();			
			data.add(new ReplaceableItem().withName(key).withAttributes(
					 new ReplaceableAttribute().withName("Value").withValue(value)));
			
			// System.out.println(aws.put(domain, key, obj, null).connection
			//		.getResponseMessage());

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public final static String readFromSDB(final String domain, final String key) {
		try {
			String disQry = "select * from `" + domain + "` where itemName() = '" + key +"'";
			SelectRequest selectRequest = new SelectRequest(disQry);
			Item item = sdb.select(selectRequest).getItems().get(0); 
			return item.getAttributes().get(0).getValue();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
