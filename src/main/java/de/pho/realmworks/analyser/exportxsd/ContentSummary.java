//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.11.07 um 04:39:52 PM CET 
//


package de.pho.realmworks.analyser.exportxsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="max_domain_count" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="152"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="max_category_count" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="148"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="topic_count" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="167"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="plot_count" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "content_summary")
public class ContentSummary {

  @XmlAttribute(name = "max_domain_count", required = true)
  protected short maxDomainCount;
  @XmlAttribute(name = "max_category_count", required = true)
  protected short maxCategoryCount;
  @XmlAttribute(name = "topic_count", required = true)
  protected short topicCount;
  @XmlAttribute(name = "plot_count", required = true)
  protected byte plotCount;

  /**
   * Ruft den Wert der maxDomainCount-Eigenschaft ab.
   */
  public short getMaxDomainCount() {
    return maxDomainCount;
  }

  /**
   * Legt den Wert der maxDomainCount-Eigenschaft fest.
   */
  public void setMaxDomainCount(short value) {
    this.maxDomainCount = value;
  }

  /**
   * Ruft den Wert der maxCategoryCount-Eigenschaft ab.
   */
  public short getMaxCategoryCount() {
    return maxCategoryCount;
  }

  /**
   * Legt den Wert der maxCategoryCount-Eigenschaft fest.
   */
  public void setMaxCategoryCount(short value) {
    this.maxCategoryCount = value;
  }

  /**
   * Ruft den Wert der topicCount-Eigenschaft ab.
   */
  public short getTopicCount() {
    return topicCount;
  }

  /**
   * Legt den Wert der topicCount-Eigenschaft fest.
   */
  public void setTopicCount(short value) {
    this.topicCount = value;
  }

  /**
   * Ruft den Wert der plotCount-Eigenschaft ab.
   */
  public byte getPlotCount() {
    return plotCount;
  }

  /**
   * Legt den Wert der plotCount-Eigenschaft fest.
   */
  public void setPlotCount(byte value) {
    this.plotCount = value;
  }

}
