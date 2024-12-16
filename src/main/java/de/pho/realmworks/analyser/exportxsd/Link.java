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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}span_info"/>
 *       &lt;/sequence>
 *       &lt;attribute name="target_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Topic_109"/>
 *             &lt;enumeration value="Topic_112"/>
 *             &lt;enumeration value="Topic_113"/>
 *             &lt;enumeration value="Topic_116"/>
 *             &lt;enumeration value="Topic_117"/>
 *             &lt;enumeration value="Topic_119"/>
 *             &lt;enumeration value="Topic_121"/>
 *             &lt;enumeration value="Topic_122"/>
 *             &lt;enumeration value="Topic_123"/>
 *             &lt;enumeration value="Topic_124"/>
 *             &lt;enumeration value="Topic_127"/>
 *             &lt;enumeration value="Topic_129"/>
 *             &lt;enumeration value="Topic_130"/>
 *             &lt;enumeration value="Topic_132"/>
 *             &lt;enumeration value="Topic_133"/>
 *             &lt;enumeration value="Topic_136"/>
 *             &lt;enumeration value="Topic_137"/>
 *             &lt;enumeration value="Topic_139"/>
 *             &lt;enumeration value="Topic_144"/>
 *             &lt;enumeration value="Topic_149"/>
 *             &lt;enumeration value="Topic_151"/>
 *             &lt;enumeration value="Topic_165"/>
 *             &lt;enumeration value="Topic_18"/>
 *             &lt;enumeration value="Topic_23"/>
 *             &lt;enumeration value="Topic_25"/>
 *             &lt;enumeration value="Topic_28"/>
 *             &lt;enumeration value="Topic_40"/>
 *             &lt;enumeration value="Topic_42"/>
 *             &lt;enumeration value="Topic_49"/>
 *             &lt;enumeration value="Topic_51"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="00EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="03C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="04229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="04C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="09E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0A239AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="0D229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="0F229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="10229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="16EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="20E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="23229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="245C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="25E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="26E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="26EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2CE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2CEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2F5A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2F619BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="305A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="31599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="3AED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3BED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3EBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="3FE19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="42DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="43DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="46E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="46E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="52E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5AEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="61659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="626B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="65DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6AA29BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="6FE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="71E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="72E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="73E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7BE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7BEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7CEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="83EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="87E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8C5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8FEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="94DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="95DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="96A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="9EE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9FE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A0E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A95F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B0BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B0E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B1BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B1E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B2BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B3BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B4BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B5BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B6EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BAE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BAEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="BCE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BDBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C1BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C6609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C7E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C8E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C9F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="CABE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CAF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="CBBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CC6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CDBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CFE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CFE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D1EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D1FF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="D2FF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="D4E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D5BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D5E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D8BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D9BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DB219AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="DCE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DCEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E4A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="E4BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E4E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E5A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="E5E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E6A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="E6EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EC5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="EE5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FB219AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="FBEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FDE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="11596"/>
 *             &lt;enumeration value="11634"/>
 *             &lt;enumeration value="11635"/>
 *             &lt;enumeration value="11662"/>
 *             &lt;enumeration value="11663"/>
 *             &lt;enumeration value="11664"/>
 *             &lt;enumeration value="11665"/>
 *             &lt;enumeration value="11679"/>
 *             &lt;enumeration value="11680"/>
 *             &lt;enumeration value="11751"/>
 *             &lt;enumeration value="11760"/>
 *             &lt;enumeration value="11829"/>
 *             &lt;enumeration value="11864"/>
 *             &lt;enumeration value="11865"/>
 *             &lt;enumeration value="11888"/>
 *             &lt;enumeration value="11899"/>
 *             &lt;enumeration value="11900"/>
 *             &lt;enumeration value="11911"/>
 *             &lt;enumeration value="11913"/>
 *             &lt;enumeration value="11914"/>
 *             &lt;enumeration value="11915"/>
 *             &lt;enumeration value="11941"/>
 *             &lt;enumeration value="11942"/>
 *             &lt;enumeration value="11952"/>
 *             &lt;enumeration value="11953"/>
 *             &lt;enumeration value="11954"/>
 *             &lt;enumeration value="11984"/>
 *             &lt;enumeration value="11985"/>
 *             &lt;enumeration value="11996"/>
 *             &lt;enumeration value="11997"/>
 *             &lt;enumeration value="12051"/>
 *             &lt;enumeration value="12088"/>
 *             &lt;enumeration value="12126"/>
 *             &lt;enumeration value="12127"/>
 *             &lt;enumeration value="12147"/>
 *             &lt;enumeration value="12221"/>
 *             &lt;enumeration value="12238"/>
 *             &lt;enumeration value="12283"/>
 *             &lt;enumeration value="12302"/>
 *             &lt;enumeration value="12322"/>
 *             &lt;enumeration value="12375"/>
 *             &lt;enumeration value="12377"/>
 *             &lt;enumeration value="12410"/>
 *             &lt;enumeration value="12419"/>
 *             &lt;enumeration value="12420"/>
 *             &lt;enumeration value="12429"/>
 *             &lt;enumeration value="12438"/>
 *             &lt;enumeration value="12448"/>
 *             &lt;enumeration value="12458"/>
 *             &lt;enumeration value="12475"/>
 *             &lt;enumeration value="12476"/>
 *             &lt;enumeration value="12529"/>
 *             &lt;enumeration value="12571"/>
 *             &lt;enumeration value="12572"/>
 *             &lt;enumeration value="12763"/>
 *             &lt;enumeration value="12771"/>
 *             &lt;enumeration value="12773"/>
 *             &lt;enumeration value="12787"/>
 *             &lt;enumeration value="12833"/>
 *             &lt;enumeration value="12834"/>
 *             &lt;enumeration value="12836"/>
 *             &lt;enumeration value="12857"/>
 *             &lt;enumeration value="12862"/>
 *             &lt;enumeration value="13535"/>
 *             &lt;enumeration value="13591"/>
 *             &lt;enumeration value="14026"/>
 *             &lt;enumeration value="14102"/>
 *             &lt;enumeration value="14171"/>
 *             &lt;enumeration value="14398"/>
 *             &lt;enumeration value="14400"/>
 *             &lt;enumeration value="14603"/>
 *             &lt;enumeration value="14694"/>
 *             &lt;enumeration value="14770"/>
 *             &lt;enumeration value="24197"/>
 *             &lt;enumeration value="24198"/>
 *             &lt;enumeration value="24202"/>
 *             &lt;enumeration value="24203"/>
 *             &lt;enumeration value="24204"/>
 *             &lt;enumeration value="24205"/>
 *             &lt;enumeration value="24206"/>
 *             &lt;enumeration value="24214"/>
 *             &lt;enumeration value="24215"/>
 *             &lt;enumeration value="24216"/>
 *             &lt;enumeration value="24232"/>
 *             &lt;enumeration value="24233"/>
 *             &lt;enumeration value="24235"/>
 *             &lt;enumeration value="24244"/>
 *             &lt;enumeration value="24245"/>
 *             &lt;enumeration value="24261"/>
 *             &lt;enumeration value="24442"/>
 *             &lt;enumeration value="24494"/>
 *             &lt;enumeration value="24501"/>
 *             &lt;enumeration value="24502"/>
 *             &lt;enumeration value="24509"/>
 *             &lt;enumeration value="24540"/>
 *             &lt;enumeration value="24541"/>
 *             &lt;enumeration value="28983"/>
 *             &lt;enumeration value="28991"/>
 *             &lt;enumeration value="29289"/>
 *             &lt;enumeration value="29294"/>
 *             &lt;enumeration value="29360"/>
 *             &lt;enumeration value="29531"/>
 *             &lt;enumeration value="29534"/>
 *             &lt;enumeration value="29643"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Alias"/>
 *             &lt;enumeration value="Arbitrary"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="alias_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Alias_2"/>
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
    "spanInfo"
})
@XmlRootElement(name = "link")
public class Link {

  @XmlElement(name = "span_info", required = true)
  protected SpanInfo spanInfo;
  @XmlAttribute(name = "target_id", required = true)
  protected String targetId;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected short signature;
  @XmlAttribute(name = "type")
  protected String type;
  @XmlAttribute(name = "alias_id")
  protected String aliasId;

  /**
   * Ruft den Wert der spanInfo-Eigenschaft ab.
   *
   * @return possible object is
   * {@link SpanInfo }
   */
  public SpanInfo getSpanInfo() {
    return spanInfo;
  }

  /**
   * Legt den Wert der spanInfo-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link SpanInfo }
   */
  public void setSpanInfo(SpanInfo value) {
    this.spanInfo = value;
  }

  /**
   * Ruft den Wert der targetId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTargetId() {
    return targetId;
  }

  /**
   * Legt den Wert der targetId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTargetId(String value) {
    this.targetId = value;
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
   * Ruft den Wert der aliasId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getAliasId() {
    return aliasId;
  }

  /**
   * Legt den Wert der aliasId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setAliasId(String value) {
    this.aliasId = value;
  }

}
