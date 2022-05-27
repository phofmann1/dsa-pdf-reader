//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.05.26 um 10:14:07 AM CEST 
//


package de.pho.dsapdfreader.config.generated.topicstrategymapping;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="strategy">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{}Strategy">
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "strategy"
})
@XmlRootElement(name = "topic-strategies")
public class TopicStrategies
{

    protected List<TopicStrategies.Strategy> strategy;

    /**
     * Gets the value of the strategy property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the strategy property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStrategy().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TopicStrategies.Strategy }
     */
    public List<TopicStrategies.Strategy> getStrategy()
    {
        if (strategy == null)
        {
            strategy = new ArrayList<TopicStrategies.Strategy>();
        }
        return this.strategy;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{}Strategy">
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Strategy
        extends de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy
    {

        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "index")
        protected Integer index;

        /**
         * Ruft den Wert der name-Eigenschaft ab.
         *
         * @return possible object is
         * {@link String }
         */
        public String getName()
        {
            return name;
        }

        /**
         * Legt den Wert der name-Eigenschaft fest.
         *
         * @param value allowed object is
         *              {@link String }
         */
        public void setName(String value)
        {
            this.name = value;
        }

        /**
         * Ruft den Wert der index-Eigenschaft ab.
         *
         * @return possible object is
         * {@link Integer }
         */
        public Integer getIndex()
        {
            return index;
        }

        /**
         * Legt den Wert der index-Eigenschaft fest.
         *
         * @param value allowed object is
         *              {@link Integer }
         */
        public void setIndex(Integer value)
        {
            this.index = value;
        }

    }

}
