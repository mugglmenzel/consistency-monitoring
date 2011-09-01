package edu.kit.aifb.eorg.datacollector.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.6 in JDK 6 Generated
 * source version: 2.1
 * 
 */
@WebServiceClient(name = "DataCollectorServiceService", targetNamespace = "http://datacollector.eorg.aifb.kit.edu/")
public class DataCollectorServiceService extends Service {

	public DataCollectorServiceService(URL wsdlLocation) {
		super(wsdlLocation, new QName(
				"http://datacollector.eorg.aifb.kit.edu/",
				"DataCollectorServiceService"));
	}

	/**
	 * 
	 * @return returns DataCollectorService
	 */
	@WebEndpoint(name = "DataCollectorServicePort")
	public DataCollectorService getDataCollectorServicePort() {
		return super.getPort(new QName(
				"http://datacollector.eorg.aifb.kit.edu/",
				"DataCollectorServicePort"), DataCollectorService.class);
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns DataCollectorService
	 */
	@WebEndpoint(name = "DataCollectorServicePort")
	public DataCollectorService getDataCollectorServicePort(
			WebServiceFeature... features) {
		return super.getPort(new QName(
				"http://datacollector.eorg.aifb.kit.edu/",
				"DataCollectorServicePort"), DataCollectorService.class,
				features);
	}

}
