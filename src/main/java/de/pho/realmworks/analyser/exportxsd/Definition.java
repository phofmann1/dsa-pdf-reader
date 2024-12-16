//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.11.07 um 04:39:52 PM CET 
//


package de.pho.realmworks.analyser.exportxsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}details"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}content_summary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "details",
    "contentSummary"
})
@XmlRootElement(name = "definition")
public class Definition {

  @XmlElement(required = true)
  protected Details details;
  @XmlElement(name = "content_summary", required = true)
  protected ContentSummary contentSummary;

  /**
   * Ruft den Wert der details-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Details }
   */
  public Details getDetails() {
    return details;
  }

  /**
   * Legt den Wert der details-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Details }
   */
  public void setDetails(Details value) {
    this.details = value;
  }

  /**
   * Ruft den Wert der contentSummary-Eigenschaft ab.
   *
   * @return possible object is
   * {@link ContentSummary }
   */
  public ContentSummary getContentSummary() {
    return contentSummary;
  }

  /**
   * Legt den Wert der contentSummary-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link ContentSummary }
   */
  public void setContentSummary(ContentSummary value) {
    this.contentSummary = value;
  }

}
