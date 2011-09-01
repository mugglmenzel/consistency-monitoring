
package edu.kit.aifb.eorg.datacollector.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.kit.aifb.eorg.datacollector.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PublishDataResponse_QNAME = new QName("http://datacollector.eorg.aifb.kit.edu/", "publishDataResponse");
    private final static QName _PublishData_QNAME = new QName("http://datacollector.eorg.aifb.kit.edu/", "publishData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.kit.aifb.eorg.datacollector.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PublishDataResponse }
     * 
     */
    public PublishDataResponse createPublishDataResponse() {
        return new PublishDataResponse();
    }

    /**
     * Create an instance of {@link PublishData }
     * 
     */
    public PublishData createPublishData() {
        return new PublishData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublishDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://datacollector.eorg.aifb.kit.edu/", name = "publishDataResponse")
    public JAXBElement<PublishDataResponse> createPublishDataResponse(PublishDataResponse value) {
        return new JAXBElement<PublishDataResponse>(_PublishDataResponse_QNAME, PublishDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublishData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://datacollector.eorg.aifb.kit.edu/", name = "publishData")
    public JAXBElement<PublishData> createPublishData(PublishData value) {
        return new JAXBElement<PublishData>(_PublishData_QNAME, PublishData.class, null, value);
    }

}
