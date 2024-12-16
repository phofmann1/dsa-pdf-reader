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
 *       &lt;attribute name="source_original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="ACF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="EBDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="target_original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="04E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D3F39BB6-A107-C91F-C71D-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="nature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Master_To_Minion"/>
 *             &lt;enumeration value="Union"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="qualifier_tag_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Tag_1279"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="qualifier">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Parent / Child"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="1DE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C6F49BB6-A107-C91F-C71D-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="28173"/>
 *             &lt;enumeration value="29256"/>
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
@XmlRootElement(name = "dconnection")
public class Dconnection {

  @XmlAttribute(name = "source_original_uuid", required = true)
  protected String sourceOriginalUuid;
  @XmlAttribute(name = "target_original_uuid", required = true)
  protected String targetOriginalUuid;
  @XmlAttribute(name = "nature", required = true)
  protected String nature;
  @XmlAttribute(name = "qualifier_tag_id")
  protected String qualifierTagId;
  @XmlAttribute(name = "qualifier")
  protected String qualifier;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected short signature;

  /**
   * Ruft den Wert der sourceOriginalUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getSourceOriginalUuid() {
    return sourceOriginalUuid;
  }

  /**
   * Legt den Wert der sourceOriginalUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setSourceOriginalUuid(String value) {
    this.sourceOriginalUuid = value;
  }

  /**
   * Ruft den Wert der targetOriginalUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTargetOriginalUuid() {
    return targetOriginalUuid;
  }

  /**
   * Legt den Wert der targetOriginalUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTargetOriginalUuid(String value) {
    this.targetOriginalUuid = value;
  }

  /**
   * Ruft den Wert der nature-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getNature() {
    return nature;
  }

  /**
   * Legt den Wert der nature-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setNature(String value) {
    this.nature = value;
  }

  /**
   * Ruft den Wert der qualifierTagId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getQualifierTagId() {
    return qualifierTagId;
  }

  /**
   * Legt den Wert der qualifierTagId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setQualifierTagId(String value) {
    this.qualifierTagId = value;
  }

  /**
   * Ruft den Wert der qualifier-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * Legt den Wert der qualifier-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setQualifier(String value) {
    this.qualifier = value;
  }

  /**
   * Ruft den Wert der originalUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getOriginalUuid() {
    return originalUuid;
  }

  /**
   * Legt den Wert der originalUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setOriginalUuid(String value) {
    this.originalUuid = value;
  }

  /**
   * Ruft den Wert der signature-Eigenschaft ab.
   */
  public short getSignature() {
    return signature;
  }

  /**
   * Legt den Wert der signature-Eigenschaft fest.
   */
  public void setSignature(short value) {
    this.signature = value;
  }

}
