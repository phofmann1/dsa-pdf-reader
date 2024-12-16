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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}description"/>
 *       &lt;/sequence>
 *       &lt;attribute name="facet_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Facet_207"/>
 *             &lt;enumeration value="Facet_208"/>
 *             &lt;enumeration value="Facet_209"/>
 *             &lt;enumeration value="Facet_210"/>
 *             &lt;enumeration value="Facet_211"/>
 *             &lt;enumeration value="Facet_212"/>
 *             &lt;enumeration value="Facet_213"/>
 *             &lt;enumeration value="Facet_214"/>
 *             &lt;enumeration value="Facet_215"/>
 *             &lt;enumeration value="Facet_216"/>
 *             &lt;enumeration value="Facet_217"/>
 *             &lt;enumeration value="Facet_218"/>
 *             &lt;enumeration value="Facet_219"/>
 *             &lt;enumeration value="Facet_220"/>
 *             &lt;enumeration value="Facet_221"/>
 *             &lt;enumeration value="Facet_227"/>
 *             &lt;enumeration value="Facet_228"/>
 *             &lt;enumeration value="Facet_229"/>
 *             &lt;enumeration value="Facet_230"/>
 *             &lt;enumeration value="Facet_264"/>
 *             &lt;enumeration value="Facet_265"/>
 *             &lt;enumeration value="Facet_266"/>
 *             &lt;enumeration value="Facet_267"/>
 *             &lt;enumeration value="Facet_268"/>
 *             &lt;enumeration value="Facet_269"/>
 *             &lt;enumeration value="Facet_270"/>
 *             &lt;enumeration value="Facet_271"/>
 *             &lt;enumeration value="Facet_272"/>
 *             &lt;enumeration value="Facet_273"/>
 *             &lt;enumeration value="Facet_274"/>
 *             &lt;enumeration value="Facet_275"/>
 *             &lt;enumeration value="Facet_327"/>
 *             &lt;enumeration value="Facet_328"/>
 *             &lt;enumeration value="Facet_329"/>
 *             &lt;enumeration value="Facet_330"/>
 *             &lt;enumeration value="Facet_331"/>
 *             &lt;enumeration value="Facet_332"/>
 *             &lt;enumeration value="Facet_333"/>
 *             &lt;enumeration value="Facet_44"/>
 *             &lt;enumeration value="Facet_45"/>
 *             &lt;enumeration value="Facet_46"/>
 *             &lt;enumeration value="Facet_47"/>
 *             &lt;enumeration value="Facet_48"/>
 *             &lt;enumeration value="Facet_49"/>
 *             &lt;enumeration value="Facet_50"/>
 *             &lt;enumeration value="Facet_51"/>
 *             &lt;enumeration value="Facet_52"/>
 *             &lt;enumeration value="Facet_53"/>
 *             &lt;enumeration value="Facet_54"/>
 *             &lt;enumeration value="Facet_55"/>
 *             &lt;enumeration value="Facet_56"/>
 *             &lt;enumeration value="Facet_57"/>
 *             &lt;enumeration value="Facet_58"/>
 *             &lt;enumeration value="Facet_59"/>
 *             &lt;enumeration value="Facet_60"/>
 *             &lt;enumeration value="Facet_61"/>
 *             &lt;enumeration value="Facet_62"/>
 *             &lt;enumeration value="Facet_63"/>
 *             &lt;enumeration value="Facet_64"/>
 *             &lt;enumeration value="Facet_65"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Art"/>
 *             &lt;enumeration value="Atmosph�re"/>
 *             &lt;enumeration value="Aufbewahrung"/>
 *             &lt;enumeration value="Autor"/>
 *             &lt;enumeration value="Banner"/>
 *             &lt;enumeration value="Basisman�ver"/>
 *             &lt;enumeration value="Beschreibung"/>
 *             &lt;enumeration value="Bild"/>
 *             &lt;enumeration value="Datum"/>
 *             &lt;enumeration value="Einfluss"/>
 *             &lt;enumeration value="Erscheinungsdatum"/>
 *             &lt;enumeration value="Erster Eindruck"/>
 *             &lt;enumeration value="Format"/>
 *             &lt;enumeration value="Gr��e"/>
 *             &lt;enumeration value="Hinweise"/>
 *             &lt;enumeration value="H�ufigkeit"/>
 *             &lt;enumeration value="Inhaltsqualit�t"/>
 *             &lt;enumeration value="Karte"/>
 *             &lt;enumeration value="Legalit�t"/>
 *             &lt;enumeration value="Milit�rische Rolle"/>
 *             &lt;enumeration value="Mitglied"/>
 *             &lt;enumeration value="Oberhaupt"/>
 *             &lt;enumeration value="Organisation"/>
 *             &lt;enumeration value="Organisationseinheit"/>
 *             &lt;enumeration value="Regeltechnik"/>
 *             &lt;enumeration value="Regeltext"/>
 *             &lt;enumeration value="Ressourcen"/>
 *             &lt;enumeration value="Seitenzahl"/>
 *             &lt;enumeration value="Sprache und Schrifts"/>
 *             &lt;enumeration value="Studium-Voraussetzungen"/>
 *             &lt;enumeration value="Verbreitung"/>
 *             &lt;enumeration value="Verf�gbarkeit"/>
 *             &lt;enumeration value="Vollst�ndiger Name"/>
 *             &lt;enumeration value="Vorraussetzungen"/>
 *             &lt;enumeration value="Wahlspruch"/>
 *             &lt;enumeration value="Wappen"/>
 *             &lt;enumeration value="Wert"/>
 *             &lt;enumeration value="Wiki"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Game"/>
 *             &lt;enumeration value="Labeled_Text"/>
 *             &lt;enumeration value="Multi_Line"/>
 *             &lt;enumeration value="Picture"/>
 *             &lt;enumeration value="Smart_Image"/>
 *             &lt;enumeration value="Tag_Standard"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="05F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="10059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="11059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="12059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="13059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="15029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="18CF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="19CF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1ACF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1BCF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1CCF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1DCF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1ECF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1F059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1FCF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="20059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="20CF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="21059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="21CF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="22CF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="2ECF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="352A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="35F59BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="432A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="442A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="462A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="472A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="482A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="4FCF9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="52069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="53069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="57069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="58069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="5A069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="5C069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="5D069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="61069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="62069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="64069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="66069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="67069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6A069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6D049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6F049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="79049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7A019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7A049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="96059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="97059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="9F059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="B5049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="B7049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C0049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C1049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C2049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C5049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C7049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C9049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CF019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E2019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="2506"/>
 *             &lt;enumeration value="2507"/>
 *             &lt;enumeration value="2508"/>
 *             &lt;enumeration value="2509"/>
 *             &lt;enumeration value="2511"/>
 *             &lt;enumeration value="2512"/>
 *             &lt;enumeration value="2515"/>
 *             &lt;enumeration value="2516"/>
 *             &lt;enumeration value="2517"/>
 *             &lt;enumeration value="2519"/>
 *             &lt;enumeration value="2520"/>
 *             &lt;enumeration value="2524"/>
 *             &lt;enumeration value="2525"/>
 *             &lt;enumeration value="2526"/>
 *             &lt;enumeration value="2528"/>
 *             &lt;enumeration value="2529"/>
 *             &lt;enumeration value="2533"/>
 *             &lt;enumeration value="2534"/>
 *             &lt;enumeration value="2535"/>
 *             &lt;enumeration value="2537"/>
 *             &lt;enumeration value="2538"/>
 *             &lt;enumeration value="2539"/>
 *             &lt;enumeration value="2773"/>
 *             &lt;enumeration value="2774"/>
 *             &lt;enumeration value="2789"/>
 *             &lt;enumeration value="2799"/>
 *             &lt;enumeration value="2809"/>
 *             &lt;enumeration value="2819"/>
 *             &lt;enumeration value="2832"/>
 *             &lt;enumeration value="2833"/>
 *             &lt;enumeration value="2834"/>
 *             &lt;enumeration value="2836"/>
 *             &lt;enumeration value="2837"/>
 *             &lt;enumeration value="2838"/>
 *             &lt;enumeration value="2849"/>
 *             &lt;enumeration value="2850"/>
 *             &lt;enumeration value="2851"/>
 *             &lt;enumeration value="2853"/>
 *             &lt;enumeration value="2854"/>
 *             &lt;enumeration value="2855"/>
 *             &lt;enumeration value="2898"/>
 *             &lt;enumeration value="2899"/>
 *             &lt;enumeration value="2900"/>
 *             &lt;enumeration value="2901"/>
 *             &lt;enumeration value="2908"/>
 *             &lt;enumeration value="2909"/>
 *             &lt;enumeration value="2910"/>
 *             &lt;enumeration value="53806"/>
 *             &lt;enumeration value="53807"/>
 *             &lt;enumeration value="53808"/>
 *             &lt;enumeration value="53809"/>
 *             &lt;enumeration value="53810"/>
 *             &lt;enumeration value="53811"/>
 *             &lt;enumeration value="53812"/>
 *             &lt;enumeration value="53813"/>
 *             &lt;enumeration value="53815"/>
 *             &lt;enumeration value="53816"/>
 *             &lt;enumeration value="53817"/>
 *             &lt;enumeration value="53818"/>
 *             &lt;enumeration value="53819"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="label_style">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Normal"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="thumbnail_size">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="144x144"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_lock_domain" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="is_multi_tag" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="domain_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Domain_1"/>
 *             &lt;enumeration value="Domain_26"/>
 *             &lt;enumeration value="Domain_27"/>
 *             &lt;enumeration value="Domain_30"/>
 *             &lt;enumeration value="Domain_5"/>
 *             &lt;enumeration value="Domain_58"/>
 *             &lt;enumeration value="Domain_59"/>
 *             &lt;enumeration value="Domain_60"/>
 *             &lt;enumeration value="Domain_61"/>
 *             &lt;enumeration value="Domain_65"/>
 *             &lt;enumeration value="Domain_66"/>
 *             &lt;enumeration value="Domain_67"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="tag_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Tag_1325"/>
 *             &lt;enumeration value="Tag_16"/>
 *             &lt;enumeration value="Tag_20"/>
 *             &lt;enumeration value="Tag_717"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="date_format">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Only_Short"/>
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
    "description"
})
@XmlRootElement(name = "facet")
public class Facet {

  @XmlElement(required = true)
  protected String description;
  @XmlAttribute(name = "facet_id", required = true)
  protected String facetId;
  @XmlAttribute(name = "name", required = true)
  protected String name;
  @XmlAttribute(name = "type", required = true)
  protected String type;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected int signature;
  @XmlAttribute(name = "label_style")
  protected String labelStyle;
  @XmlAttribute(name = "thumbnail_size")
  protected String thumbnailSize;
  @XmlAttribute(name = "is_lock_domain")
  protected Boolean isLockDomain;
  @XmlAttribute(name = "is_multi_tag")
  protected Boolean isMultiTag;
  @XmlAttribute(name = "domain_id")
  protected String domainId;
  @XmlAttribute(name = "tag_id")
  protected String tagId;
  @XmlAttribute(name = "date_format")
  protected String dateFormat;

  /**
   * Ruft den Wert der description-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getDescription() {
    return description;
  }

  /**
   * Legt den Wert der description-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
   * Ruft den Wert der facetId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getFacetId() {
    return facetId;
  }

  /**
   * Legt den Wert der facetId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setFacetId(String value) {
    this.facetId = value;
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
   * Ruft den Wert der type-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getType() {
    return type;
  }

  /**
   * Legt den Wert der type-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setType(String value) {
    this.type = value;
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
  public int getSignature() {
    return signature;
  }

  /**
   * Legt den Wert der signature-Eigenschaft fest.
   */
  public void setSignature(int value) {
    this.signature = value;
  }

  /**
   * Ruft den Wert der labelStyle-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getLabelStyle() {
    return labelStyle;
  }

  /**
   * Legt den Wert der labelStyle-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setLabelStyle(String value) {
    this.labelStyle = value;
  }

  /**
   * Ruft den Wert der thumbnailSize-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getThumbnailSize() {
    return thumbnailSize;
  }

  /**
   * Legt den Wert der thumbnailSize-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setThumbnailSize(String value) {
    this.thumbnailSize = value;
  }

  /**
   * Ruft den Wert der isLockDomain-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Boolean }
   */
  public Boolean isIsLockDomain() {
    return isLockDomain;
  }

  /**
   * Legt den Wert der isLockDomain-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Boolean }
   */
  public void setIsLockDomain(Boolean value) {
    this.isLockDomain = value;
  }

  /**
   * Ruft den Wert der isMultiTag-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Boolean }
   */
  public Boolean isIsMultiTag() {
    return isMultiTag;
  }

  /**
   * Legt den Wert der isMultiTag-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Boolean }
   */
  public void setIsMultiTag(Boolean value) {
    this.isMultiTag = value;
  }

  /**
   * Ruft den Wert der domainId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getDomainId() {
    return domainId;
  }

  /**
   * Legt den Wert der domainId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setDomainId(String value) {
    this.domainId = value;
  }

  /**
   * Ruft den Wert der tagId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTagId() {
    return tagId;
  }

  /**
   * Legt den Wert der tagId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTagId(String value) {
    this.tagId = value;
  }

  /**
   * Ruft den Wert der dateFormat-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getDateFormat() {
    return dateFormat;
  }

  /**
   * Legt den Wert der dateFormat-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setDateFormat(String value) {
    this.dateFormat = value;
  }

}
