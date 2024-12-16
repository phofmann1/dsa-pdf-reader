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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}description"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}summary"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}partition" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="category_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Category_33"/>
 *             &lt;enumeration value="Category_34"/>
 *             &lt;enumeration value="Category_35"/>
 *             &lt;enumeration value="Category_45"/>
 *             &lt;enumeration value="Category_60"/>
 *             &lt;enumeration value="Category_61"/>
 *             &lt;enumeration value="Category_64"/>
 *             &lt;enumeration value="Category_67"/>
 *             &lt;enumeration value="Category_68"/>
 *             &lt;enumeration value="Category_69"/>
 *             &lt;enumeration value="Category_70"/>
 *             &lt;enumeration value="Category_81"/>
 *             &lt;enumeration value="Category_82"/>
 *             &lt;enumeration value="Category_92"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Adelshaus"/>
 *             &lt;enumeration value="Answinkrise"/>
 *             &lt;enumeration value="Buch"/>
 *             &lt;enumeration value="Kaiserreich"/>
 *             &lt;enumeration value="Kirchenorden"/>
 *             &lt;enumeration value="Magiergilde"/>
 *             &lt;enumeration value="Magierorden"/>
 *             &lt;enumeration value="Man�ver"/>
 *             &lt;enumeration value="Milit�r - Heer - OLD"/>
 *             &lt;enumeration value="Orkensturm"/>
 *             &lt;enumeration value="Pflanze"/>
 *             &lt;enumeration value="Schlacht"/>
 *             &lt;enumeration value="Tempel"/>
 *             &lt;enumeration value="Theorie"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="abbrev" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="AWK"/>
 *             &lt;enumeration value="CastList"/>
 *             &lt;enumeration value="GrpMilit"/>
 *             &lt;enumeration value="KR"/>
 *             &lt;enumeration value="MagGild"/>
 *             &lt;enumeration value="Man�ver"/>
 *             &lt;enumeration value="OS3"/>
 *             &lt;enumeration value="Obj"/>
 *             &lt;enumeration value="Pflanze"/>
 *             &lt;enumeration value="SCH"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="role" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Topic"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="family_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="2"/>
 *             &lt;enumeration value="3"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="5"/>
 *             &lt;enumeration value="7"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="asset_name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Concept"/>
 *             &lt;enumeration value="Deity"/>
 *             &lt;enumeration value="Flame"/>
 *             &lt;enumeration value="Group: Government"/>
 *             &lt;enumeration value="Helmet"/>
 *             &lt;enumeration value="Magic Staff"/>
 *             &lt;enumeration value="Monster"/>
 *             &lt;enumeration value="Named Object"/>
 *             &lt;enumeration value="Open Book"/>
 *             &lt;enumeration value="Ring"/>
 *             &lt;enumeration value="Sword"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="asset_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0DAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0FAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="23AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2AAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="35AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="B600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F6AD297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="FAAD297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="03F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="0D2B9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="0E059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="13029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="332A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="55069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="5F069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6B049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="78019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="81F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="94059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="BE049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CD019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E0019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="2504"/>
 *             &lt;enumeration value="2513"/>
 *             &lt;enumeration value="2522"/>
 *             &lt;enumeration value="2531"/>
 *             &lt;enumeration value="2771"/>
 *             &lt;enumeration value="2779"/>
 *             &lt;enumeration value="2784"/>
 *             &lt;enumeration value="2787"/>
 *             &lt;enumeration value="2797"/>
 *             &lt;enumeration value="2807"/>
 *             &lt;enumeration value="2817"/>
 *             &lt;enumeration value="2830"/>
 *             &lt;enumeration value="2847"/>
 *             &lt;enumeration value="2896"/>
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
    "description",
    "summary",
    "partition"
})
@XmlRootElement(name = "category")
public class Category {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String summary;
    @XmlElement(required = true)
    protected List<Partition> partition;
    @XmlAttribute(name = "category_id", required = true)
    protected String categoryId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "abbrev", required = true)
    protected String abbrev;
    @XmlAttribute(name = "role", required = true)
    protected String role;
    @XmlAttribute(name = "family_id", required = true)
    protected byte familyId;
    @XmlAttribute(name = "asset_name", required = true)
    protected String assetName;
    @XmlAttribute(name = "asset_uuid", required = true)
    protected String assetUuid;
    @XmlAttribute(name = "original_uuid", required = true)
    protected String originalUuid;
    @XmlAttribute(name = "signature", required = true)
    protected short signature;

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
     * Ruft den Wert der summary-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Legt den Wert der summary-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSummary(String value) {
        this.summary = value;
    }

    /**
     * Gets the value of the partition property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partition property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPartition().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Partition }
     */
    public List<Partition> getPartition() {
        if (partition == null) {
            partition = new ArrayList<Partition>();
        }
        return this.partition;
    }

    /**
     * Ruft den Wert der categoryId-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Legt den Wert der categoryId-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCategoryId(String value) {
        this.categoryId = value;
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
     * Ruft den Wert der role-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getRole() {
        return role;
    }

    /**
     * Legt den Wert der role-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Ruft den Wert der familyId-Eigenschaft ab.
     */
    public byte getFamilyId() {
        return familyId;
    }

    /**
     * Legt den Wert der familyId-Eigenschaft fest.
     */
    public void setFamilyId(byte value) {
        this.familyId = value;
    }

    /**
     * Ruft den Wert der assetName-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Legt den Wert der assetName-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAssetName(String value) {
        this.assetName = value;
    }

    /**
     * Ruft den Wert der assetUuid-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getAssetUuid() {
        return assetUuid;
    }

    /**
     * Legt den Wert der assetUuid-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAssetUuid(String value) {
        this.assetUuid = value;
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
