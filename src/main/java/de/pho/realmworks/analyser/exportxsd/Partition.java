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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}purpose"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}facet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="partition_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Partition_116"/>
 *             &lt;enumeration value="Partition_117"/>
 *             &lt;enumeration value="Partition_118"/>
 *             &lt;enumeration value="Partition_119"/>
 *             &lt;enumeration value="Partition_120"/>
 *             &lt;enumeration value="Partition_121"/>
 *             &lt;enumeration value="Partition_122"/>
 *             &lt;enumeration value="Partition_135"/>
 *             &lt;enumeration value="Partition_136"/>
 *             &lt;enumeration value="Partition_274"/>
 *             &lt;enumeration value="Partition_275"/>
 *             &lt;enumeration value="Partition_276"/>
 *             &lt;enumeration value="Partition_277"/>
 *             &lt;enumeration value="Partition_278"/>
 *             &lt;enumeration value="Partition_279"/>
 *             &lt;enumeration value="Partition_280"/>
 *             &lt;enumeration value="Partition_281"/>
 *             &lt;enumeration value="Partition_282"/>
 *             &lt;enumeration value="Partition_301"/>
 *             &lt;enumeration value="Partition_318"/>
 *             &lt;enumeration value="Partition_319"/>
 *             &lt;enumeration value="Partition_320"/>
 *             &lt;enumeration value="Partition_321"/>
 *             &lt;enumeration value="Partition_322"/>
 *             &lt;enumeration value="Partition_323"/>
 *             &lt;enumeration value="Partition_324"/>
 *             &lt;enumeration value="Partition_325"/>
 *             &lt;enumeration value="Partition_326"/>
 *             &lt;enumeration value="Partition_327"/>
 *             &lt;enumeration value="Partition_328"/>
 *             &lt;enumeration value="Partition_329"/>
 *             &lt;enumeration value="Partition_330"/>
 *             &lt;enumeration value="Partition_331"/>
 *             &lt;enumeration value="Partition_332"/>
 *             &lt;enumeration value="Partition_333"/>
 *             &lt;enumeration value="Partition_334"/>
 *             &lt;enumeration value="Partition_335"/>
 *             &lt;enumeration value="Partition_336"/>
 *             &lt;enumeration value="Partition_337"/>
 *             &lt;enumeration value="Partition_338"/>
 *             &lt;enumeration value="Partition_339"/>
 *             &lt;enumeration value="Partition_340"/>
 *             &lt;enumeration value="Partition_341"/>
 *             &lt;enumeration value="Partition_342"/>
 *             &lt;enumeration value="Partition_343"/>
 *             &lt;enumeration value="Partition_344"/>
 *             &lt;enumeration value="Partition_345"/>
 *             &lt;enumeration value="Partition_346"/>
 *             &lt;enumeration value="Partition_347"/>
 *             &lt;enumeration value="Partition_348"/>
 *             &lt;enumeration value="Partition_349"/>
 *             &lt;enumeration value="Partition_451"/>
 *             &lt;enumeration value="Partition_452"/>
 *             &lt;enumeration value="Partition_453"/>
 *             &lt;enumeration value="Partition_454"/>
 *             &lt;enumeration value="Partition_455"/>
 *             &lt;enumeration value="Partition_456"/>
 *             &lt;enumeration value="Partition_457"/>
 *             &lt;enumeration value="Partition_458"/>
 *             &lt;enumeration value="Partition_459"/>
 *             &lt;enumeration value="Partition_460"/>
 *             &lt;enumeration value="Partition_461"/>
 *             &lt;enumeration value="Partition_462"/>
 *             &lt;enumeration value="Partition_463"/>
 *             &lt;enumeration value="Partition_464"/>
 *             &lt;enumeration value="Partition_465"/>
 *             &lt;enumeration value="Partition_466"/>
 *             &lt;enumeration value="Partition_467"/>
 *             &lt;enumeration value="Partition_468"/>
 *             &lt;enumeration value="Partition_469"/>
 *             &lt;enumeration value="Partition_470"/>
 *             &lt;enumeration value="Partition_551"/>
 *             &lt;enumeration value="Partition_552"/>
 *             &lt;enumeration value="Partition_553"/>
 *             &lt;enumeration value="Partition_554"/>
 *             &lt;enumeration value="Partition_555"/>
 *             &lt;enumeration value="Partition_556"/>
 *             &lt;enumeration value="Partition_557"/>
 *             &lt;enumeration value="Partition_558"/>
 *             &lt;enumeration value="Partition_559"/>
 *             &lt;enumeration value="Partition_560"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Beschreibung"/>
 *             &lt;enumeration value="Besonderheiten"/>
 *             &lt;enumeration value="Beziehungen"/>
 *             &lt;enumeration value="Details"/>
 *             &lt;enumeration value="Exemplare"/>
 *             &lt;enumeration value="Geschichten"/>
 *             &lt;enumeration value="Gr��e und Einfluss"/>
 *             &lt;enumeration value="Herausforderungen"/>
 *             &lt;enumeration value="Hindernisse"/>
 *             &lt;enumeration value="Hintergrund"/>
 *             &lt;enumeration value="Hintergrundgeschichte"/>
 *             &lt;enumeration value="Hintergrundwissen"/>
 *             &lt;enumeration value="Ideologie"/>
 *             &lt;enumeration value="Inhalt"/>
 *             &lt;enumeration value="Kurzprofil"/>
 *             &lt;enumeration value="Methoden"/>
 *             &lt;enumeration value="Mitglieder"/>
 *             &lt;enumeration value="Organisation"/>
 *             &lt;enumeration value="Ort"/>
 *             &lt;enumeration value="Profil"/>
 *             &lt;enumeration value="Regeln"/>
 *             &lt;enumeration value="Richtlinien"/>
 *             &lt;enumeration value="Ursache und Wirkung"/>
 *             &lt;enumeration value="Weitere Details"/>
 *             &lt;enumeration value="Wertgegenst�nde"/>
 *             &lt;enumeration value="Zus�tzliche Details"/>
 *             &lt;enumeration value="Zus�tzliche Informationen"/>
 *             &lt;enumeration value="�bersicht"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="04F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="06F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="07F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="08F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="09F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="0E2B9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="0F059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="14029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="15059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="16029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="17059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="19059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1A059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1B029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1C029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1C059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1E029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="1E059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="20029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="22029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="22059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="24029BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="24059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="25059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="342A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="452A9BB6-D81E-70B4-BE86-6B9353716BA9"/>
 *             &lt;enumeration value="51069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="56069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="5B069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="60069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="65069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="69069BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6C049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="6E049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="70049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="71049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="72049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="73049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="74049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="75049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="76049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="77049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="79019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7B019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7C019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7D019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7E019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="7F019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="80019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="81019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="82F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="84F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="85F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="86F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="95059BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="BF049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="C4049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CA049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CC049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CE019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="CE049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D0019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D0049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D2049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D4049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D5019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D6019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D6049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D8019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="D8049BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="DA019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="DC019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="DE019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E1019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E3019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E8019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="E9019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="EB019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="ED019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="EF019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *             &lt;enumeration value="F1019BB6-1BDF-5464-CC6B-6A9353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="2505"/>
 *             &lt;enumeration value="2510"/>
 *             &lt;enumeration value="2514"/>
 *             &lt;enumeration value="2518"/>
 *             &lt;enumeration value="2521"/>
 *             &lt;enumeration value="2523"/>
 *             &lt;enumeration value="2527"/>
 *             &lt;enumeration value="2532"/>
 *             &lt;enumeration value="2536"/>
 *             &lt;enumeration value="2776"/>
 *             &lt;enumeration value="2777"/>
 *             &lt;enumeration value="2778"/>
 *             &lt;enumeration value="2780"/>
 *             &lt;enumeration value="2781"/>
 *             &lt;enumeration value="2782"/>
 *             &lt;enumeration value="2783"/>
 *             &lt;enumeration value="2785"/>
 *             &lt;enumeration value="2788"/>
 *             &lt;enumeration value="2790"/>
 *             &lt;enumeration value="2791"/>
 *             &lt;enumeration value="2792"/>
 *             &lt;enumeration value="2793"/>
 *             &lt;enumeration value="2794"/>
 *             &lt;enumeration value="2795"/>
 *             &lt;enumeration value="2796"/>
 *             &lt;enumeration value="2798"/>
 *             &lt;enumeration value="2800"/>
 *             &lt;enumeration value="2801"/>
 *             &lt;enumeration value="2802"/>
 *             &lt;enumeration value="2803"/>
 *             &lt;enumeration value="2804"/>
 *             &lt;enumeration value="2805"/>
 *             &lt;enumeration value="2806"/>
 *             &lt;enumeration value="2808"/>
 *             &lt;enumeration value="2810"/>
 *             &lt;enumeration value="2811"/>
 *             &lt;enumeration value="2812"/>
 *             &lt;enumeration value="2813"/>
 *             &lt;enumeration value="2814"/>
 *             &lt;enumeration value="2815"/>
 *             &lt;enumeration value="2816"/>
 *             &lt;enumeration value="2818"/>
 *             &lt;enumeration value="2820"/>
 *             &lt;enumeration value="2821"/>
 *             &lt;enumeration value="2822"/>
 *             &lt;enumeration value="2823"/>
 *             &lt;enumeration value="2824"/>
 *             &lt;enumeration value="2825"/>
 *             &lt;enumeration value="2826"/>
 *             &lt;enumeration value="2831"/>
 *             &lt;enumeration value="2835"/>
 *             &lt;enumeration value="2839"/>
 *             &lt;enumeration value="2840"/>
 *             &lt;enumeration value="2841"/>
 *             &lt;enumeration value="2842"/>
 *             &lt;enumeration value="2843"/>
 *             &lt;enumeration value="2844"/>
 *             &lt;enumeration value="2845"/>
 *             &lt;enumeration value="2846"/>
 *             &lt;enumeration value="2848"/>
 *             &lt;enumeration value="2852"/>
 *             &lt;enumeration value="2856"/>
 *             &lt;enumeration value="2857"/>
 *             &lt;enumeration value="2858"/>
 *             &lt;enumeration value="2859"/>
 *             &lt;enumeration value="2860"/>
 *             &lt;enumeration value="2861"/>
 *             &lt;enumeration value="2862"/>
 *             &lt;enumeration value="2863"/>
 *             &lt;enumeration value="2897"/>
 *             &lt;enumeration value="2902"/>
 *             &lt;enumeration value="2903"/>
 *             &lt;enumeration value="2904"/>
 *             &lt;enumeration value="2905"/>
 *             &lt;enumeration value="2906"/>
 *             &lt;enumeration value="2907"/>
 *             &lt;enumeration value="2911"/>
 *             &lt;enumeration value="2912"/>
 *             &lt;enumeration value="2913"/>
 *             &lt;enumeration value="53805"/>
 *             &lt;enumeration value="53814"/>
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
    "purpose",
    "facet"
})
@XmlRootElement(name = "partition")
public class Partition {

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String purpose;
    protected List<Facet> facet;
    @XmlAttribute(name = "partition_id", required = true)
    protected String partitionId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "original_uuid", required = true)
    protected String originalUuid;
    @XmlAttribute(name = "signature", required = true)
    protected int signature;

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
     * Ruft den Wert der purpose-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Legt den Wert der purpose-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPurpose(String value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the facet property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the facet property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFacet().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Facet }
     */
    public List<Facet> getFacet() {
        if (facet == null) {
            facet = new ArrayList<Facet>();
        }
        return this.facet;
    }

    /**
     * Ruft den Wert der partitionId-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPartitionId() {
        return partitionId;
    }

    /**
     * Legt den Wert der partitionId-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPartitionId(String value) {
        this.partitionId = value;
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

}
