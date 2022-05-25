//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.05.25 um 06:19:00 PM CEST 
//


package de.pho.dsapdfreader.config.generated.topicstrategymapping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "index",
    "key",
    "value"
})
@XmlRootElement(name = "parameter")
public class Parameter
{

    protected int index;
    @XmlElement(required = true)
    protected String key;
    @XmlElement(required = true)
    protected String value;

    /**
     * Ruft den Wert der index-Eigenschaft ab.
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * Legt den Wert der index-Eigenschaft fest.
     */
    public void setIndex(int value)
    {
        this.index = value;
    }

    /**
     * Ruft den Wert der key-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Legt den Wert der key-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setKey(String value)
    {
        this.key = value;
    }

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setValue(String value)
    {
        this.value = value;
    }

}
