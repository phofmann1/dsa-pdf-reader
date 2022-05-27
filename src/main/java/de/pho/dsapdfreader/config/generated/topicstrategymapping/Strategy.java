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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Strategy complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Strategy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="startPage" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="endPage" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="strategyClass" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="params" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence maxOccurs="unbounded">
 *                   &lt;element name="parameter" type="{}Parameter"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Strategy", propOrder = {
    "startPage",
    "endPage",
    "strategyClass",
    "params"
})
@XmlSeeAlso({
    de.pho.dsapdfreader.config.generated.topicstrategymapping.TopicStrategies.Strategy.class
})
public class Strategy
{

    protected Integer startPage;
    protected Integer endPage;
    @XmlElement(required = true)
    protected String strategyClass;
    protected Strategy.Params params;

    /**
     * Ruft den Wert der startPage-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Integer }
     */
    public Integer getStartPage()
    {
        return startPage;
    }

    /**
     * Legt den Wert der startPage-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setStartPage(Integer value)
    {
        this.startPage = value;
    }

    /**
     * Ruft den Wert der endPage-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getEndPage()
    {
        return endPage;
    }

    /**
     * Legt den Wert der endPage-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setEndPage(Integer value)
    {
        this.endPage = value;
    }

    /**
     * Ruft den Wert der strategyClass-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStrategyClass()
    {
        return strategyClass;
    }

    /**
     * Legt den Wert der strategyClass-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStrategyClass(String value)
    {
        this.strategyClass = value;
    }

    /**
     * Ruft den Wert der params-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Strategy.Params }
     *
     */
    public Strategy.Params getParams()
    {
        return params;
    }

    /**
     * Legt den Wert der params-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Strategy.Params }
     *
     */
    public void setParams(Strategy.Params value)
    {
        this.params = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     *
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence maxOccurs="unbounded">
     *         &lt;element name="parameter" type="{}Parameter"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "parameter"
    })
    public static class Params
    {

        @XmlElement(required = true)
        protected List<Parameter> parameter;

        /**
         * Gets the value of the parameter property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the parameter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getParameter().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Parameter }
         */
        public List<Parameter> getParameter()
        {
            if (parameter == null)
            {
                parameter = new ArrayList<Parameter>();
            }
            return this.parameter;
        }

    }

}
