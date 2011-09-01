
package edu.kit.aifb.eorg.datacollector.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for publishData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="publishData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="senderIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="durationInMillis" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="testrunID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "publishData", propOrder = {
    "senderIdentifier",
    "durationInMillis",
    "testrunID"
})
public class PublishData {

    protected String senderIdentifier;
    protected int durationInMillis;
    protected String testrunID;

    /**
     * Gets the value of the senderIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    /**
     * Sets the value of the senderIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderIdentifier(String value) {
        this.senderIdentifier = value;
    }

    /**
     * Gets the value of the durationInMillis property.
     * 
     */
    public int getDurationInMillis() {
        return durationInMillis;
    }

    /**
     * Sets the value of the durationInMillis property.
     * 
     */
    public void setDurationInMillis(int value) {
        this.durationInMillis = value;
    }

    /**
     * Gets the value of the testrunID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestrunID() {
        return testrunID;
    }

    /**
     * Sets the value of the testrunID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestrunID(String value) {
        this.testrunID = value;
    }

}
