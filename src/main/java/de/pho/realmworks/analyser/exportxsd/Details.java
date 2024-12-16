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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}cover_art"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Dornenreich"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="abbrev" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="DORNEN"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="import_tag_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Tag_1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="import_tag_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="17CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="18CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "coverArt"
})
@XmlRootElement(name = "details")
public class Details {

  @XmlElement(name = "cover_art", required = true)
  protected String coverArt;
  @XmlAttribute(name = "name", required = true)
  protected String name;
  @XmlAttribute(name = "abbrev", required = true)
  protected String abbrev;
  @XmlAttribute(name = "import_tag_id", required = true)
  protected String importTagId;
  @XmlAttribute(name = "import_tag_uuid", required = true)
  protected String importTagUuid;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;

  /**
   * Ruft den Wert der coverArt-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getCoverArt() {
    return coverArt;
  }

  /**
   * Legt den Wert der coverArt-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setCoverArt(String value) {
    this.coverArt = value;
  }

  /**
   * Ruft den Wert der name-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getName() {
    return name;
  }

  /**
   * Legt den Wert der name-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Ruft den Wert der abbrev-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getAbbrev() {
    return abbrev;
  }

  /**
   * Legt den Wert der abbrev-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setAbbrev(String value) {
    this.abbrev = value;
  }

  /**
   * Ruft den Wert der importTagId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getImportTagId() {
    return importTagId;
  }

  /**
   * Legt den Wert der importTagId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setImportTagId(String value) {
    this.importTagId = value;
  }

  /**
   * Ruft den Wert der importTagUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getImportTagUuid() {
    return importTagUuid;
  }

  /**
   * Legt den Wert der importTagUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setImportTagUuid(String value) {
    this.importTagUuid = value;
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

}
