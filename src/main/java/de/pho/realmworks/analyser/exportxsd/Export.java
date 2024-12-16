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
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}definition"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}structure"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}contents"/>
 *       &lt;/sequence>
 *       &lt;attribute name="format_version" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="4"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="game_system_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="2"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="source_scope_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="58CD2E7B-91C6-5D3A-00D0-6A9353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="owner_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="56CD2E7B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="export_date" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}dateTime">
 *             &lt;enumeration value="2023-11-07T14:59:47Z"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_structure_only" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "definition",
    "structure",
    "contents"
})
@XmlRootElement(name = "export")
public class Export {

  @XmlElement(required = true)
  protected Definition definition;
  @XmlElement(required = true)
  protected Structure structure;
  @XmlElement(required = true)
  protected Contents contents;
  @XmlAttribute(name = "format_version", required = true)
  protected byte formatVersion;
  @XmlAttribute(name = "game_system_id", required = true)
  protected byte gameSystemId;
  @XmlAttribute(name = "source_scope_uuid", required = true)
  protected String sourceScopeUuid;
  @XmlAttribute(name = "owner_uuid", required = true)
  protected String ownerUuid;
  @XmlAttribute(name = "export_date", required = true)
  protected XMLGregorianCalendar exportDate;
  @XmlAttribute(name = "is_structure_only", required = true)
  protected boolean isStructureOnly;

  /**
   * Ruft den Wert der definition-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Definition }
   */
  public Definition getDefinition() {
    return definition;
  }

  /**
   * Legt den Wert der definition-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Definition }
   */
  public void setDefinition(Definition value) {
    this.definition = value;
  }

  /**
   * Ruft den Wert der structure-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Structure }
   */
  public Structure getStructure() {
    return structure;
  }

  /**
   * Legt den Wert der structure-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Structure }
   */
  public void setStructure(Structure value) {
    this.structure = value;
  }

  /**
   * Ruft den Wert der contents-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Contents }
   */
  public Contents getContents() {
    return contents;
  }

  /**
   * Legt den Wert der contents-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Contents }
   */
  public void setContents(Contents value) {
    this.contents = value;
  }

  /**
   * Ruft den Wert der formatVersion-Eigenschaft ab.
   */
  public byte getFormatVersion() {
    return formatVersion;
  }

  /**
   * Legt den Wert der formatVersion-Eigenschaft fest.
   */
  public void setFormatVersion(byte value) {
    this.formatVersion = value;
  }

  /**
   * Ruft den Wert der gameSystemId-Eigenschaft ab.
   */
  public byte getGameSystemId() {
    return gameSystemId;
  }

  /**
   * Legt den Wert der gameSystemId-Eigenschaft fest.
   */
  public void setGameSystemId(byte value) {
    this.gameSystemId = value;
  }

  /**
   * Ruft den Wert der sourceScopeUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getSourceScopeUuid() {
    return sourceScopeUuid;
  }

  /**
   * Legt den Wert der sourceScopeUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setSourceScopeUuid(String value) {
    this.sourceScopeUuid = value;
  }

  /**
   * Ruft den Wert der ownerUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getOwnerUuid() {
    return ownerUuid;
  }

  /**
   * Legt den Wert der ownerUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setOwnerUuid(String value) {
    this.ownerUuid = value;
  }

  /**
   * Ruft den Wert der exportDate-Eigenschaft ab.
   *
   * @return possible object is
   * {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getExportDate() {
    return exportDate;
  }

  /**
   * Legt den Wert der exportDate-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link XMLGregorianCalendar }
   */
  public void setExportDate(XMLGregorianCalendar value) {
    this.exportDate = value;
  }

  /**
   * Ruft den Wert der isStructureOnly-Eigenschaft ab.
   */
  public boolean isIsStructureOnly() {
    return isStructureOnly;
  }

  /**
   * Legt den Wert der isStructureOnly-Eigenschaft fest.
   */
  public void setIsStructureOnly(boolean value) {
    this.isStructureOnly = value;
  }

}
