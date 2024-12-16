//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.11.07 um 04:39:52 PM CET 
//


package de.pho.realmworks.analyser.exportxsd;

import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="domain_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Domain_58"/>
 *             &lt;enumeration value="Domain_59"/>
 *             &lt;enumeration value="Domain_60"/>
 *             &lt;enumeration value="Domain_61"/>
 *             &lt;enumeration value="Domain_62"/>
 *             &lt;enumeration value="Domain_63"/>
 *             &lt;enumeration value="Domain_64"/>
 *             &lt;enumeration value="Domain_65"/>
 *             &lt;enumeration value="Domain_66"/>
 *             &lt;enumeration value="Domain_67"/>
 *             &lt;enumeration value="Domain_68"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Einfluss"/>
 *             &lt;enumeration value="H�ufigkeit"/>
 *             &lt;enumeration value="Rang - Adel"/>
 *             &lt;enumeration value="Rang - Gildenmagier"/>
 *             &lt;enumeration value="Rang - Kirche"/>
 *             &lt;enumeration value="Rang - Marine"/>
 *             &lt;enumeration value="Rang - Milit�r"/>
 *             &lt;enumeration value="Rang - Orden"/>
 *             &lt;enumeration value="Ressourcen"/>
 *             &lt;enumeration value="Verbreitung"/>
 *             &lt;enumeration value="Wissen"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="abbrev" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Einfluss"/>
 *             &lt;enumeration value="H�ufigkeit"/>
 *             &lt;enumeration value="Rang - Ade"/>
 *             &lt;enumeration value="Rang - Gil"/>
 *             &lt;enumeration value="Rang - Kir"/>
 *             &lt;enumeration value="Rang - Mar"/>
 *             &lt;enumeration value="Rang - Mil"/>
 *             &lt;enumeration value="Rang - Ord"/>
 *             &lt;enumeration value="Ressourcen"/>
 *             &lt;enumeration value="Verbreitun"/>
 *             &lt;enumeration value="Wissen"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="08059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="12069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="13069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="14069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="15069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="15289BB6-C070-525C-6D4B-6A9353716BA9"/>
 *             &lt;enumeration value="362A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="3B2A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="3C069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="49069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CC199AB6-144A-622D-E81E-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="2390"/>
 *             &lt;enumeration value="2395"/>
 *             &lt;enumeration value="2400"/>
 *             &lt;enumeration value="2410"/>
 *             &lt;enumeration value="2421"/>
 *             &lt;enumeration value="2430"/>
 *             &lt;enumeration value="2443"/>
 *             &lt;enumeration value="2454"/>
 *             &lt;enumeration value="2461"/>
 *             &lt;enumeration value="2466"/>
 *             &lt;enumeration value="2471"/>
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
    "tag"
})
@XmlRootElement(name = "domain")
public class Domain {

    @XmlElement(required = true)
    protected List<Tag> tag;
    @XmlAttribute(name = "domain_id", required = true)
    protected String domainId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "abbrev", required = true)
    protected String abbrev;
    @XmlAttribute(name = "original_uuid", required = true)
    protected String originalUuid;
    @XmlAttribute(name = "signature", required = true)
    protected short signature;

    /**
     * Gets the value of the tag property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tag property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTag().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tag }
     */
    public List<Tag> getTag() {
        if (tag == null) {
            tag = new ArrayList<Tag>();
        }
        return this.tag;
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
