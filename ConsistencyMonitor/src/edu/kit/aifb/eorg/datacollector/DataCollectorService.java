/**
 * 
 */
package edu.kit.aifb.eorg.datacollector;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author David Bermbach
 * 
 *         created on: 28.03.2011
 */
@WebService
public class DataCollectorService {

	@WebMethod
	public void publishData(
			@WebParam(name = "senderIdentifier") String senderIdentifier,
			@WebParam(name = "durationInMillis") int durationInMillis,
			@WebParam(name = "testrunID") String testrunID) {
		DataCollector d = DataCollector.getInstance();
		d.writeData(senderIdentifier, durationInMillis, testrunID);
//		System.out.println("invoked!");
	}
	

}
