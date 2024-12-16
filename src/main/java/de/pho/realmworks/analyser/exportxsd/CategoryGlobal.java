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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}partition_global" maxOccurs="unbounded"/>
 *         &lt;choice minOccurs="0">
 *           &lt;sequence>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category" maxOccurs="unbounded"/>
 *             &lt;sequence minOccurs="0">
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category_global" maxOccurs="unbounded"/>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category"/>
 *             &lt;/sequence>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" maxOccurs="unbounded"/>
 *           &lt;/sequence>
 *           &lt;sequence>
 *             &lt;sequence minOccurs="0">
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category_global" maxOccurs="unbounded"/>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category" maxOccurs="unbounded"/>
 *             &lt;/sequence>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" maxOccurs="unbounded"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="category_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Category_1"/>
 *             &lt;enumeration value="Category_10"/>
 *             &lt;enumeration value="Category_11"/>
 *             &lt;enumeration value="Category_12"/>
 *             &lt;enumeration value="Category_13"/>
 *             &lt;enumeration value="Category_14"/>
 *             &lt;enumeration value="Category_15"/>
 *             &lt;enumeration value="Category_16"/>
 *             &lt;enumeration value="Category_17"/>
 *             &lt;enumeration value="Category_18"/>
 *             &lt;enumeration value="Category_19"/>
 *             &lt;enumeration value="Category_20"/>
 *             &lt;enumeration value="Category_21"/>
 *             &lt;enumeration value="Category_22"/>
 *             &lt;enumeration value="Category_23"/>
 *             &lt;enumeration value="Category_24"/>
 *             &lt;enumeration value="Category_25"/>
 *             &lt;enumeration value="Category_26"/>
 *             &lt;enumeration value="Category_27"/>
 *             &lt;enumeration value="Category_28"/>
 *             &lt;enumeration value="Category_29"/>
 *             &lt;enumeration value="Category_3"/>
 *             &lt;enumeration value="Category_30"/>
 *             &lt;enumeration value="Category_31"/>
 *             &lt;enumeration value="Category_32"/>
 *             &lt;enumeration value="Category_36"/>
 *             &lt;enumeration value="Category_37"/>
 *             &lt;enumeration value="Category_38"/>
 *             &lt;enumeration value="Category_39"/>
 *             &lt;enumeration value="Category_4"/>
 *             &lt;enumeration value="Category_40"/>
 *             &lt;enumeration value="Category_41"/>
 *             &lt;enumeration value="Category_42"/>
 *             &lt;enumeration value="Category_43"/>
 *             &lt;enumeration value="Category_44"/>
 *             &lt;enumeration value="Category_46"/>
 *             &lt;enumeration value="Category_47"/>
 *             &lt;enumeration value="Category_48"/>
 *             &lt;enumeration value="Category_49"/>
 *             &lt;enumeration value="Category_5"/>
 *             &lt;enumeration value="Category_50"/>
 *             &lt;enumeration value="Category_58"/>
 *             &lt;enumeration value="Category_59"/>
 *             &lt;enumeration value="Category_6"/>
 *             &lt;enumeration value="Category_62"/>
 *             &lt;enumeration value="Category_63"/>
 *             &lt;enumeration value="Category_65"/>
 *             &lt;enumeration value="Category_66"/>
 *             &lt;enumeration value="Category_7"/>
 *             &lt;enumeration value="Category_71"/>
 *             &lt;enumeration value="Category_72"/>
 *             &lt;enumeration value="Category_73"/>
 *             &lt;enumeration value="Category_74"/>
 *             &lt;enumeration value="Category_75"/>
 *             &lt;enumeration value="Category_76"/>
 *             &lt;enumeration value="Category_77"/>
 *             &lt;enumeration value="Category_79"/>
 *             &lt;enumeration value="Category_8"/>
 *             &lt;enumeration value="Category_83"/>
 *             &lt;enumeration value="Category_84"/>
 *             &lt;enumeration value="Category_87"/>
 *             &lt;enumeration value="Category_88"/>
 *             &lt;enumeration value="Category_89"/>
 *             &lt;enumeration value="Category_9"/>
 *             &lt;enumeration value="Category_90"/>
 *             &lt;enumeration value="Category_91"/>
 *             &lt;enumeration value="Category_93"/>
 *             &lt;enumeration value="Category_94"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Allgemeines Thema"/>
 *             &lt;enumeration value="Artikel: Allgemein"/>
 *             &lt;enumeration value="Artikel: Ausr�stung Allgemein"/>
 *             &lt;enumeration value="Artikel: F�higkeiten Allgemein"/>
 *             &lt;enumeration value="Artikel: Gameplay Allgemein"/>
 *             &lt;enumeration value="Artikel: Gefahren Allgemein"/>
 *             &lt;enumeration value="Artikel: Spielleiter Allgemein"/>
 *             &lt;enumeration value="Aufgabe/Klasse/Karriere"/>
 *             &lt;enumeration value="Ausr�stung"/>
 *             &lt;enumeration value="Besondere Gegenst�nde"/>
 *             &lt;enumeration value="Bewohner"/>
 *             &lt;enumeration value="Ereignis"/>
 *             &lt;enumeration value="Fahrzeug"/>
 *             &lt;enumeration value="Fahrzeuge"/>
 *             &lt;enumeration value="Falle"/>
 *             &lt;enumeration value="Fertigkeit/F�higkeit"/>
 *             &lt;enumeration value="Gallerie: Mechanik"/>
 *             &lt;enumeration value="Gefahren"/>
 *             &lt;enumeration value="Gift"/>
 *             &lt;enumeration value="Gottheit"/>
 *             &lt;enumeration value="Gruppe: Ethisch - OLD"/>
 *             &lt;enumeration value="Gruppe: Familie"/>
 *             &lt;enumeration value="Gruppe: Handel - OLD"/>
 *             &lt;enumeration value="Gruppe: Kriminell - OLD"/>
 *             &lt;enumeration value="Gruppe: Milit�r - OLD"/>
 *             &lt;enumeration value="Gruppe: Politisch - OLD"/>
 *             &lt;enumeration value="Gruppe: Religi�s OLD"/>
 *             &lt;enumeration value="H�ndler"/>
 *             &lt;enumeration value="Kampagnen Element"/>
 *             &lt;enumeration value="Konzept"/>
 *             &lt;enumeration value="Krankheit/Gebrechen"/>
 *             &lt;enumeration value="Liste: Andere"/>
 *             &lt;enumeration value="Liste: Ereignisse"/>
 *             &lt;enumeration value="Liste: Gegenst�nde"/>
 *             &lt;enumeration value="Liste: Gruppen"/>
 *             &lt;enumeration value="Liste: Orte"/>
 *             &lt;enumeration value="Liste: Protagonisten"/>
 *             &lt;enumeration value="Location"/>
 *             &lt;enumeration value="Mechanics Source"/>
 *             &lt;enumeration value="Merkmal/Hintergrund"/>
 *             &lt;enumeration value="Monster/Gegner"/>
 *             &lt;enumeration value="Mundane Gegenst�nde"/>
 *             &lt;enumeration value="Mundane Schutzausr�stung"/>
 *             &lt;enumeration value="Mundane Waffen"/>
 *             &lt;enumeration value="Objektsammlung"/>
 *             &lt;enumeration value="Planetary Body"/>
 *             &lt;enumeration value="Plot Idee"/>
 *             &lt;enumeration value="Rassen/Spezies"/>
 *             &lt;enumeration value="Region: Dimension"/>
 *             &lt;enumeration value="Region: Geographisch"/>
 *             &lt;enumeration value="Region: Himmlisch"/>
 *             &lt;enumeration value="Region: Politisch - OLD"/>
 *             &lt;enumeration value="Region: Stadt"/>
 *             &lt;enumeration value="Schutzausr�stung"/>
 *             &lt;enumeration value="Spezialisierung/Archetyp"/>
 *             &lt;enumeration value="Spruch/Kraft"/>
 *             &lt;enumeration value="Story Gallery"/>
 *             &lt;enumeration value="Story Source"/>
 *             &lt;enumeration value="Story: Handlungsstrang OLD"/>
 *             &lt;enumeration value="Story: Plot Hook"/>
 *             &lt;enumeration value="Talent/Vorteil"/>
 *             &lt;enumeration value="Titel / Position"/>
 *             &lt;enumeration value="Waffeneigenschaft"/>
 *             &lt;enumeration value="Weitere Informationen"/>
 *             &lt;enumeration value="Wichtige Gegenst�nde"/>
 *             &lt;enumeration value="Zeitliche Periode"/>
 *             &lt;enumeration value="Zusammenh�nge: Spieler"/>
 *             &lt;enumeration value="Zusammenh�nge: Spielleiter"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="global_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="1EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="21B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="22B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="23B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="24B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="25B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="26B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="27B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="28B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="29B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="2FB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="30B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="31B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3301287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3F01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4001287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4101287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4A01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5A00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5F00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6801287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6901287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6A00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6A01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="70082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7701287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8308287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8D08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8E08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A81C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A91C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AA1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B501287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="B900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BA00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BB00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BC00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DB01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
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
    "content"
})
@XmlRootElement(name = "category_global")
public class CategoryGlobal {

    @XmlElementRefs({
        @XmlElementRef(name = "partition_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = PartitionGlobal.class, required = false),
        @XmlElementRef(name = "text_override", namespace = "urn:lonewolfdevel.com:realm-works-export", type = TextOverride.class, required = false),
        @XmlElementRef(name = "category", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Category.class, required = false),
        @XmlElementRef(name = "category_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = CategoryGlobal.class, required = false)
    })
    protected List<Object> content;
    @XmlAttribute(name = "category_id", required = true)
    protected String categoryId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "global_uuid", required = true)
    protected String globalUuid;

    /**
     * Ruft das restliche Contentmodell ab.
     *
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "Category" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 218 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
     * Zeile 215 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
     * <p>
     * Um diese Eigenschaft zu entfernen, wenden Sie eine Eigenschaftenanpassung f�r eine
     * der beiden folgenden Deklarationen an, um deren Namen zu �ndern:
     * Gets the value of the content property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PartitionGlobal }
     * {@link TextOverride }
     * {@link Category }
     * {@link CategoryGlobal }
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
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
     * Ruft den Wert der globalUuid-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGlobalUuid() {
        return globalUuid;
    }

    /**
     * Legt den Wert der globalUuid-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGlobalUuid(String value) {
        this.globalUuid = value;
    }

}
