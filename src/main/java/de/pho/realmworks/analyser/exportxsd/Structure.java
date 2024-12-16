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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}domain_global" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}domain" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}domain_global"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}category_global" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "structure")
public class Structure {

    @XmlElementRefs({
        @XmlElementRef(name = "domain_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = DomainGlobal.class, required = false),
        @XmlElementRef(name = "domain", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Domain.class, required = false),
        @XmlElementRef(name = "category_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = CategoryGlobal.class, required = false)
    })
    protected List<Object> content;

    /**
     * Ruft das restliche Contentmodell ab.
     *
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "DomainGlobal" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 5302 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
     * Zeile 5300 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
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
     * {@link Domain }
     * {@link CategoryGlobal }
     * {@link DomainGlobal }
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

}
