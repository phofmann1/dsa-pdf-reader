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
 *       &lt;attribute name="alias_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Alias_1"/>
 *             &lt;enumeration value="Alias_2"/>
 *             &lt;enumeration value="Alias_3"/>
 *             &lt;enumeration value="Alias_4"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Aranische Exodus"/>
 *             &lt;enumeration value="Arkos der Gro�e"/>
 *             &lt;enumeration value="Naisirabad"/>
 *             &lt;enumeration value="Sybia"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_show_nav_pane" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="67E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6F689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CFFF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="DBE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="11893"/>
 *             &lt;enumeration value="13634"/>
 *             &lt;enumeration value="29618"/>
 *             &lt;enumeration value="29639"/>
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
@XmlRootElement(name = "alias")
public class Alias {

    @XmlAttribute(name = "alias_id", required = true)
    protected String aliasId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "is_show_nav_pane", required = true)
    protected boolean isShowNavPane;
    @XmlAttribute(name = "original_uuid", required = true)
    protected String originalUuid;
    @XmlAttribute(name = "signature", required = true)
    protected short signature;

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
     * Ruft den Wert der isShowNavPane-Eigenschaft ab.
     */
    public boolean isIsShowNavPane() {
        return isShowNavPane;
    }

    /**
     * Legt den Wert der isShowNavPane-Eigenschaft fest.
     */
    public void setIsShowNavPane(boolean value) {
        this.isShowNavPane = value;
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
