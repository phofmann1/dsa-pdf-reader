//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.05.26 um 10:14:07 AM CEST 
//


package de.pho.dsapdfreader.config.generated.topicstrategymapping;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the de.pho.dsapdfreader.config.generated.topicstrategymapping package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory
{


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.pho.dsapdfreader.config.generated.topicstrategymapping
     */
    public ObjectFactory()
    {
    }

    /**
     * Create an instance of {@link TopicStrategies }
     */
    public TopicStrategies createTopicStrategies()
    {
        return new TopicStrategies();
    }

    /**
     * Create an instance of {@link de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy }
     */
    public de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy createStrategy()
    {
        return new de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy();
    }

    /**
     * Create an instance of {@link TopicStrategies.Strategy }
     */
    public TopicStrategies.Strategy createTopicStrategiesStrategy()
    {
        return new TopicStrategies.Strategy();
    }

    /**
     * Create an instance of {@link Parameter }
     */
    public Parameter createParameter()
    {
        return new Parameter();
    }

    /**
     * Create an instance of {@link de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy.Params }
     *
     */
    public de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy.Params createStrategyParams()
    {
        return new de.pho.dsapdfreader.config.generated.topicstrategymapping.Strategy.Params();
    }

}
