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
 *       &lt;attribute name="directions" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="start" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="100"/>
 *             &lt;enumeration value="101"/>
 *             &lt;enumeration value="102"/>
 *             &lt;enumeration value="103"/>
 *             &lt;enumeration value="105"/>
 *             &lt;enumeration value="1051"/>
 *             &lt;enumeration value="1054"/>
 *             &lt;enumeration value="106"/>
 *             &lt;enumeration value="107"/>
 *             &lt;enumeration value="1084"/>
 *             &lt;enumeration value="1099"/>
 *             &lt;enumeration value="111"/>
 *             &lt;enumeration value="112"/>
 *             &lt;enumeration value="1129"/>
 *             &lt;enumeration value="114"/>
 *             &lt;enumeration value="116"/>
 *             &lt;enumeration value="117"/>
 *             &lt;enumeration value="118"/>
 *             &lt;enumeration value="121"/>
 *             &lt;enumeration value="124"/>
 *             &lt;enumeration value="1240"/>
 *             &lt;enumeration value="125"/>
 *             &lt;enumeration value="127"/>
 *             &lt;enumeration value="131"/>
 *             &lt;enumeration value="135"/>
 *             &lt;enumeration value="1369"/>
 *             &lt;enumeration value="1372"/>
 *             &lt;enumeration value="1386"/>
 *             &lt;enumeration value="139"/>
 *             &lt;enumeration value="140"/>
 *             &lt;enumeration value="1406"/>
 *             &lt;enumeration value="142"/>
 *             &lt;enumeration value="143"/>
 *             &lt;enumeration value="1437"/>
 *             &lt;enumeration value="144"/>
 *             &lt;enumeration value="145"/>
 *             &lt;enumeration value="146"/>
 *             &lt;enumeration value="1466"/>
 *             &lt;enumeration value="148"/>
 *             &lt;enumeration value="1495"/>
 *             &lt;enumeration value="156"/>
 *             &lt;enumeration value="157"/>
 *             &lt;enumeration value="159"/>
 *             &lt;enumeration value="163"/>
 *             &lt;enumeration value="1720"/>
 *             &lt;enumeration value="177"/>
 *             &lt;enumeration value="179"/>
 *             &lt;enumeration value="181"/>
 *             &lt;enumeration value="182"/>
 *             &lt;enumeration value="185"/>
 *             &lt;enumeration value="186"/>
 *             &lt;enumeration value="188"/>
 *             &lt;enumeration value="190"/>
 *             &lt;enumeration value="191"/>
 *             &lt;enumeration value="1913"/>
 *             &lt;enumeration value="1921"/>
 *             &lt;enumeration value="193"/>
 *             &lt;enumeration value="1942"/>
 *             &lt;enumeration value="196"/>
 *             &lt;enumeration value="197"/>
 *             &lt;enumeration value="199"/>
 *             &lt;enumeration value="201"/>
 *             &lt;enumeration value="202"/>
 *             &lt;enumeration value="2051"/>
 *             &lt;enumeration value="2091"/>
 *             &lt;enumeration value="21"/>
 *             &lt;enumeration value="215"/>
 *             &lt;enumeration value="2151"/>
 *             &lt;enumeration value="2158"/>
 *             &lt;enumeration value="222"/>
 *             &lt;enumeration value="223"/>
 *             &lt;enumeration value="2238"/>
 *             &lt;enumeration value="228"/>
 *             &lt;enumeration value="2307"/>
 *             &lt;enumeration value="232"/>
 *             &lt;enumeration value="234"/>
 *             &lt;enumeration value="2341"/>
 *             &lt;enumeration value="237"/>
 *             &lt;enumeration value="239"/>
 *             &lt;enumeration value="240"/>
 *             &lt;enumeration value="249"/>
 *             &lt;enumeration value="2528"/>
 *             &lt;enumeration value="255"/>
 *             &lt;enumeration value="256"/>
 *             &lt;enumeration value="257"/>
 *             &lt;enumeration value="259"/>
 *             &lt;enumeration value="267"/>
 *             &lt;enumeration value="269"/>
 *             &lt;enumeration value="276"/>
 *             &lt;enumeration value="277"/>
 *             &lt;enumeration value="2826"/>
 *             &lt;enumeration value="2873"/>
 *             &lt;enumeration value="290"/>
 *             &lt;enumeration value="291"/>
 *             &lt;enumeration value="2915"/>
 *             &lt;enumeration value="293"/>
 *             &lt;enumeration value="307"/>
 *             &lt;enumeration value="311"/>
 *             &lt;enumeration value="312"/>
 *             &lt;enumeration value="313"/>
 *             &lt;enumeration value="321"/>
 *             &lt;enumeration value="324"/>
 *             &lt;enumeration value="329"/>
 *             &lt;enumeration value="330"/>
 *             &lt;enumeration value="331"/>
 *             &lt;enumeration value="334"/>
 *             &lt;enumeration value="338"/>
 *             &lt;enumeration value="3447"/>
 *             &lt;enumeration value="345"/>
 *             &lt;enumeration value="350"/>
 *             &lt;enumeration value="3506"/>
 *             &lt;enumeration value="3540"/>
 *             &lt;enumeration value="364"/>
 *             &lt;enumeration value="366"/>
 *             &lt;enumeration value="367"/>
 *             &lt;enumeration value="368"/>
 *             &lt;enumeration value="3774"/>
 *             &lt;enumeration value="378"/>
 *             &lt;enumeration value="3839"/>
 *             &lt;enumeration value="3875"/>
 *             &lt;enumeration value="388"/>
 *             &lt;enumeration value="3911"/>
 *             &lt;enumeration value="392"/>
 *             &lt;enumeration value="400"/>
 *             &lt;enumeration value="421"/>
 *             &lt;enumeration value="4336"/>
 *             &lt;enumeration value="445"/>
 *             &lt;enumeration value="45"/>
 *             &lt;enumeration value="461"/>
 *             &lt;enumeration value="48"/>
 *             &lt;enumeration value="4824"/>
 *             &lt;enumeration value="483"/>
 *             &lt;enumeration value="489"/>
 *             &lt;enumeration value="495"/>
 *             &lt;enumeration value="4950"/>
 *             &lt;enumeration value="505"/>
 *             &lt;enumeration value="517"/>
 *             &lt;enumeration value="53"/>
 *             &lt;enumeration value="530"/>
 *             &lt;enumeration value="5306"/>
 *             &lt;enumeration value="54"/>
 *             &lt;enumeration value="5421"/>
 *             &lt;enumeration value="545"/>
 *             &lt;enumeration value="546"/>
 *             &lt;enumeration value="56"/>
 *             &lt;enumeration value="5605"/>
 *             &lt;enumeration value="567"/>
 *             &lt;enumeration value="57"/>
 *             &lt;enumeration value="58"/>
 *             &lt;enumeration value="5862"/>
 *             &lt;enumeration value="591"/>
 *             &lt;enumeration value="592"/>
 *             &lt;enumeration value="593"/>
 *             &lt;enumeration value="60"/>
 *             &lt;enumeration value="61"/>
 *             &lt;enumeration value="612"/>
 *             &lt;enumeration value="617"/>
 *             &lt;enumeration value="62"/>
 *             &lt;enumeration value="63"/>
 *             &lt;enumeration value="634"/>
 *             &lt;enumeration value="64"/>
 *             &lt;enumeration value="6449"/>
 *             &lt;enumeration value="65"/>
 *             &lt;enumeration value="662"/>
 *             &lt;enumeration value="67"/>
 *             &lt;enumeration value="677"/>
 *             &lt;enumeration value="68"/>
 *             &lt;enumeration value="684"/>
 *             &lt;enumeration value="689"/>
 *             &lt;enumeration value="69"/>
 *             &lt;enumeration value="6917"/>
 *             &lt;enumeration value="7025"/>
 *             &lt;enumeration value="707"/>
 *             &lt;enumeration value="71"/>
 *             &lt;enumeration value="7243"/>
 *             &lt;enumeration value="73"/>
 *             &lt;enumeration value="74"/>
 *             &lt;enumeration value="7494"/>
 *             &lt;enumeration value="75"/>
 *             &lt;enumeration value="76"/>
 *             &lt;enumeration value="77"/>
 *             &lt;enumeration value="772"/>
 *             &lt;enumeration value="781"/>
 *             &lt;enumeration value="784"/>
 *             &lt;enumeration value="79"/>
 *             &lt;enumeration value="80"/>
 *             &lt;enumeration value="800"/>
 *             &lt;enumeration value="809"/>
 *             &lt;enumeration value="81"/>
 *             &lt;enumeration value="83"/>
 *             &lt;enumeration value="84"/>
 *             &lt;enumeration value="85"/>
 *             &lt;enumeration value="855"/>
 *             &lt;enumeration value="87"/>
 *             &lt;enumeration value="88"/>
 *             &lt;enumeration value="89"/>
 *             &lt;enumeration value="91"/>
 *             &lt;enumeration value="92"/>
 *             &lt;enumeration value="93"/>
 *             &lt;enumeration value="95"/>
 *             &lt;enumeration value="956"/>
 *             &lt;enumeration value="96"/>
 *             &lt;enumeration value="97"/>
 *             &lt;enumeration value="98"/>
 *             &lt;enumeration value="986"/>
 *             &lt;enumeration value="99"/>
 *             &lt;enumeration value="993"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="length" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="10"/>
 *             &lt;enumeration value="11"/>
 *             &lt;enumeration value="12"/>
 *             &lt;enumeration value="14"/>
 *             &lt;enumeration value="15"/>
 *             &lt;enumeration value="16"/>
 *             &lt;enumeration value="17"/>
 *             &lt;enumeration value="18"/>
 *             &lt;enumeration value="19"/>
 *             &lt;enumeration value="20"/>
 *             &lt;enumeration value="21"/>
 *             &lt;enumeration value="22"/>
 *             &lt;enumeration value="24"/>
 *             &lt;enumeration value="26"/>
 *             &lt;enumeration value="3"/>
 *             &lt;enumeration value="30"/>
 *             &lt;enumeration value="35"/>
 *             &lt;enumeration value="36"/>
 *             &lt;enumeration value="38"/>
 *             &lt;enumeration value="39"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="42"/>
 *             &lt;enumeration value="49"/>
 *             &lt;enumeration value="5"/>
 *             &lt;enumeration value="50"/>
 *             &lt;enumeration value="51"/>
 *             &lt;enumeration value="52"/>
 *             &lt;enumeration value="53"/>
 *             &lt;enumeration value="6"/>
 *             &lt;enumeration value="63"/>
 *             &lt;enumeration value="64"/>
 *             &lt;enumeration value="68"/>
 *             &lt;enumeration value="69"/>
 *             &lt;enumeration value="7"/>
 *             &lt;enumeration value="70"/>
 *             &lt;enumeration value="75"/>
 *             &lt;enumeration value="77"/>
 *             &lt;enumeration value="78"/>
 *             &lt;enumeration value="8"/>
 *             &lt;enumeration value="87"/>
 *             &lt;enumeration value="9"/>
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
@XmlRootElement(name = "span")
public class Span {

  @XmlAttribute(name = "directions", required = true)
  protected byte directions;
  @XmlAttribute(name = "start", required = true)
  protected short start;
  @XmlAttribute(name = "length", required = true)
  protected byte length;

  /**
   * Ruft den Wert der directions-Eigenschaft ab.
   */
  public byte getDirections() {
    return directions;
  }

  /**
   * Legt den Wert der directions-Eigenschaft fest.
   */
  public void setDirections(byte value) {
    this.directions = value;
  }

  /**
   * Ruft den Wert der start-Eigenschaft ab.
   */
  public short getStart() {
    return start;
  }

  /**
   * Legt den Wert der start-Eigenschaft fest.
   */
  public void setStart(short value) {
    this.start = value;
  }

  /**
   * Ruft den Wert der length-Eigenschaft ab.
   */
  public byte getLength() {
    return length;
  }

  /**
   * Legt den Wert der length-Eigenschaft fest.
   */
  public void setLength(byte value) {
    this.length = value;
  }

}
