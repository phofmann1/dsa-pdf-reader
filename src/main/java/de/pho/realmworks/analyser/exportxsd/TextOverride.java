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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}contents"/>
 *       &lt;/sequence>
 *       &lt;attribute name="text_role" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Format"/>
 *             &lt;enumeration value="Description"/>
 *             &lt;enumeration value="Label_Style"/>
 *             &lt;enumeration value="Name"/>
 *             &lt;enumeration value="Purpose"/>
 *             &lt;enumeration value="Summary"/>
 *             &lt;enumeration value="Thumbnail_Size"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="004C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="004F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="00509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="014F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="01509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="024F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="02509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="034F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="03509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="044F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="04509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="054F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="05509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="064F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="06509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="074F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="07509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="07609BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="084C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="084F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="08509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="08609BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="094F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="09509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="09609BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0A4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0A4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0A609BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0B4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0E369BB6-07AD-EFDA-766D-689353716BA9"/>
 *             &lt;enumeration value="0E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="0F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="10509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="114E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="11509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="11809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="12509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="13509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="13809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="14509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="14809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="15509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="15809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="16509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="16809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="174C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="174D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="17509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="17809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="18509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="19509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="19809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="1A4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1A809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="1B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1B809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="1C4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1C559BB6-5CCE-D3EF-356E-689353716BA9"/>
 *             &lt;enumeration value="1C809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="1D4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1D809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="1E4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="1F4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="204F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="214C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="214F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="21809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="224F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="22809BB6-11AF-4FC1-4614-689353716BA9"/>
 *             &lt;enumeration value="234F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="244E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="244F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="254F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="264D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2C4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="2F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="30509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="31509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="32509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="334C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="33509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="34509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="35509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="36509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="372B9BB6-3525-3CAA-2F6D-689353716BA9"/>
 *             &lt;enumeration value="37509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="38509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="394D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="39509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3B4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3C4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3E4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3F4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="3F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="404E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="40509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="414E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="41509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="424E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="42509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="434E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="43509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="444D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="444E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="44509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="454E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="454F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="45509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="464E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="464F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="46509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="474E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="474F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="47509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="484E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="484F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="48509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="494C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="494E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="494F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="49509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4A4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4B4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4B4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4C4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4C4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4D4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4D4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4E4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4E4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4E4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4F4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4F4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="4F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="504E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="504F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="50509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="514F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="524F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="52509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="534F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="53509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="544F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="554F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="55509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="564F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="56509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="574F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="57509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="584F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="58509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="594F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="59509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5D4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="5F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="60509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="61509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="62509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="63509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="64509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="65509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="66509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="67509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="68509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="694C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="69509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6A4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="6F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="70509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="71509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="724C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="72509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="73509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="74509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="75509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="76509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="77509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="78509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="79509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7B4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7C4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7D4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7D4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7E4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7F4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="7F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="804F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="80509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="814F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="81509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="824F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="82509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="834F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="83509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="844C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="844F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="84509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="854F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="85509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="86509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="874E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="874F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="87509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="884E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="884F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="88509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="894E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="894F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="89509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8A4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8B4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8B4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8C4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8C4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8D4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8D4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8E4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8E4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8E4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8F4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8F4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="8F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="904E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="904F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="90509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="914E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="914F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="91509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="924E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="924F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="92509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="934E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="934F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="93509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="944E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="944F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="94509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="954E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="954F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="95509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="964E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="964F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="96509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="974E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="974F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="97509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="984E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="984F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="98509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="994E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="994F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="99509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9A4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9A4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9A509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9B4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9B4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9B509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9C239BB6-0C2E-D425-6E0D-6A9353716BA9"/>
 *             &lt;enumeration value="9C4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9C4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9C509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9D239BB6-0C2E-D425-6E0D-6A9353716BA9"/>
 *             &lt;enumeration value="9D4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9D4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9D509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9E239BB6-0C2E-D425-6E0D-6A9353716BA9"/>
 *             &lt;enumeration value="9E4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9E4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9E4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9E509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9F239BB6-0C2E-D425-6E0D-6A9353716BA9"/>
 *             &lt;enumeration value="9F4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9F4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="9F509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A04F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A0509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A14D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A14F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A1509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A24D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A24F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A2509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A3509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A4509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A5509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A6509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A7509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A8509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A94C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="A9509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AA509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AB4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AB509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AC4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AC509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AD4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AD4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AD4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AD509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AE4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AE509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AF4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="AF509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B04F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B0509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B14F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B1509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B24F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B2509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B34D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B3509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B4509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B5509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B6509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B7509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B8509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="B9509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BA509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BB4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BB4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BB509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BC4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BC509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BD4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BD4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BD509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BE4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BE509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BF4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="BF509BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C04F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C14F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C24F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C64D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="C94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CD4D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CD4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CE4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CF4B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CF4C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="CF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D04B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D14B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D14D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D24B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D34B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D44B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D54B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D54D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D64B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D74B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D84B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D94C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="D94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DB4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DC279BB6-BBB0-C9F0-EF90-6B9353716BA9"/>
 *             &lt;enumeration value="DC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DC4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DD4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DD4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DE4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="DF4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E14F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E24F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E34B9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E54C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="E94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EB4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EC4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="ED4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="ED4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EE4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="EF4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F04C9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F04E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F04F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F14E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F14F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F24E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F24F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F34D9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F34E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F34F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F44E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F44F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F54E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F54F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F6239BB6-0C2E-D425-6E0D-6A9353716BA9"/>
 *             &lt;enumeration value="F64E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F64F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F74E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F74F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F84E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F84F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F94E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="F94F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FA4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FA4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FB4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FB4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FC4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FC4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FD4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FD4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FE4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FE4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FF4E9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *             &lt;enumeration value="FF4F9BB6-12FB-19D7-674B-6B9353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="1822"/>
 *             &lt;enumeration value="1823"/>
 *             &lt;enumeration value="1824"/>
 *             &lt;enumeration value="1825"/>
 *             &lt;enumeration value="20689"/>
 *             &lt;enumeration value="20700"/>
 *             &lt;enumeration value="20705"/>
 *             &lt;enumeration value="20720"/>
 *             &lt;enumeration value="20736"/>
 *             &lt;enumeration value="20745"/>
 *             &lt;enumeration value="20758"/>
 *             &lt;enumeration value="20766"/>
 *             &lt;enumeration value="20777"/>
 *             &lt;enumeration value="20803"/>
 *             &lt;enumeration value="20840"/>
 *             &lt;enumeration value="20868"/>
 *             &lt;enumeration value="20887"/>
 *             &lt;enumeration value="20894"/>
 *             &lt;enumeration value="20917"/>
 *             &lt;enumeration value="20924"/>
 *             &lt;enumeration value="20935"/>
 *             &lt;enumeration value="20945"/>
 *             &lt;enumeration value="20949"/>
 *             &lt;enumeration value="20956"/>
 *             &lt;enumeration value="20967"/>
 *             &lt;enumeration value="20978"/>
 *             &lt;enumeration value="20991"/>
 *             &lt;enumeration value="21011"/>
 *             &lt;enumeration value="21018"/>
 *             &lt;enumeration value="21023"/>
 *             &lt;enumeration value="21036"/>
 *             &lt;enumeration value="21048"/>
 *             &lt;enumeration value="21058"/>
 *             &lt;enumeration value="21073"/>
 *             &lt;enumeration value="21084"/>
 *             &lt;enumeration value="21092"/>
 *             &lt;enumeration value="21098"/>
 *             &lt;enumeration value="21105"/>
 *             &lt;enumeration value="21109"/>
 *             &lt;enumeration value="21113"/>
 *             &lt;enumeration value="21119"/>
 *             &lt;enumeration value="21130"/>
 *             &lt;enumeration value="21138"/>
 *             &lt;enumeration value="21169"/>
 *             &lt;enumeration value="21180"/>
 *             &lt;enumeration value="21191"/>
 *             &lt;enumeration value="21201"/>
 *             &lt;enumeration value="21238"/>
 *             &lt;enumeration value="21272"/>
 *             &lt;enumeration value="21283"/>
 *             &lt;enumeration value="21288"/>
 *             &lt;enumeration value="21289"/>
 *             &lt;enumeration value="21290"/>
 *             &lt;enumeration value="21291"/>
 *             &lt;enumeration value="21292"/>
 *             &lt;enumeration value="21293"/>
 *             &lt;enumeration value="21294"/>
 *             &lt;enumeration value="21295"/>
 *             &lt;enumeration value="21298"/>
 *             &lt;enumeration value="21299"/>
 *             &lt;enumeration value="21300"/>
 *             &lt;enumeration value="21301"/>
 *             &lt;enumeration value="21302"/>
 *             &lt;enumeration value="21303"/>
 *             &lt;enumeration value="21304"/>
 *             &lt;enumeration value="21305"/>
 *             &lt;enumeration value="21306"/>
 *             &lt;enumeration value="21307"/>
 *             &lt;enumeration value="21308"/>
 *             &lt;enumeration value="21309"/>
 *             &lt;enumeration value="21310"/>
 *             &lt;enumeration value="21311"/>
 *             &lt;enumeration value="21312"/>
 *             &lt;enumeration value="21313"/>
 *             &lt;enumeration value="21314"/>
 *             &lt;enumeration value="21315"/>
 *             &lt;enumeration value="21316"/>
 *             &lt;enumeration value="21317"/>
 *             &lt;enumeration value="21318"/>
 *             &lt;enumeration value="21319"/>
 *             &lt;enumeration value="21320"/>
 *             &lt;enumeration value="21321"/>
 *             &lt;enumeration value="21322"/>
 *             &lt;enumeration value="21323"/>
 *             &lt;enumeration value="21324"/>
 *             &lt;enumeration value="21325"/>
 *             &lt;enumeration value="21326"/>
 *             &lt;enumeration value="21327"/>
 *             &lt;enumeration value="21328"/>
 *             &lt;enumeration value="21329"/>
 *             &lt;enumeration value="21330"/>
 *             &lt;enumeration value="21331"/>
 *             &lt;enumeration value="21332"/>
 *             &lt;enumeration value="21333"/>
 *             &lt;enumeration value="21334"/>
 *             &lt;enumeration value="21335"/>
 *             &lt;enumeration value="21336"/>
 *             &lt;enumeration value="21337"/>
 *             &lt;enumeration value="21338"/>
 *             &lt;enumeration value="21339"/>
 *             &lt;enumeration value="21340"/>
 *             &lt;enumeration value="21341"/>
 *             &lt;enumeration value="21342"/>
 *             &lt;enumeration value="21343"/>
 *             &lt;enumeration value="21344"/>
 *             &lt;enumeration value="21345"/>
 *             &lt;enumeration value="21346"/>
 *             &lt;enumeration value="21347"/>
 *             &lt;enumeration value="21348"/>
 *             &lt;enumeration value="21349"/>
 *             &lt;enumeration value="21350"/>
 *             &lt;enumeration value="21351"/>
 *             &lt;enumeration value="21352"/>
 *             &lt;enumeration value="21353"/>
 *             &lt;enumeration value="21354"/>
 *             &lt;enumeration value="21355"/>
 *             &lt;enumeration value="21356"/>
 *             &lt;enumeration value="21357"/>
 *             &lt;enumeration value="21358"/>
 *             &lt;enumeration value="21359"/>
 *             &lt;enumeration value="21360"/>
 *             &lt;enumeration value="21361"/>
 *             &lt;enumeration value="21362"/>
 *             &lt;enumeration value="21363"/>
 *             &lt;enumeration value="21364"/>
 *             &lt;enumeration value="21365"/>
 *             &lt;enumeration value="21366"/>
 *             &lt;enumeration value="21367"/>
 *             &lt;enumeration value="21368"/>
 *             &lt;enumeration value="21369"/>
 *             &lt;enumeration value="21370"/>
 *             &lt;enumeration value="21371"/>
 *             &lt;enumeration value="21372"/>
 *             &lt;enumeration value="21373"/>
 *             &lt;enumeration value="21374"/>
 *             &lt;enumeration value="21375"/>
 *             &lt;enumeration value="21376"/>
 *             &lt;enumeration value="21377"/>
 *             &lt;enumeration value="21378"/>
 *             &lt;enumeration value="21379"/>
 *             &lt;enumeration value="21380"/>
 *             &lt;enumeration value="21381"/>
 *             &lt;enumeration value="21382"/>
 *             &lt;enumeration value="21383"/>
 *             &lt;enumeration value="21384"/>
 *             &lt;enumeration value="21385"/>
 *             &lt;enumeration value="21386"/>
 *             &lt;enumeration value="21387"/>
 *             &lt;enumeration value="21388"/>
 *             &lt;enumeration value="21389"/>
 *             &lt;enumeration value="21390"/>
 *             &lt;enumeration value="21391"/>
 *             &lt;enumeration value="21392"/>
 *             &lt;enumeration value="21393"/>
 *             &lt;enumeration value="21394"/>
 *             &lt;enumeration value="21395"/>
 *             &lt;enumeration value="21396"/>
 *             &lt;enumeration value="21397"/>
 *             &lt;enumeration value="21398"/>
 *             &lt;enumeration value="21399"/>
 *             &lt;enumeration value="21400"/>
 *             &lt;enumeration value="21401"/>
 *             &lt;enumeration value="21402"/>
 *             &lt;enumeration value="21403"/>
 *             &lt;enumeration value="21404"/>
 *             &lt;enumeration value="21405"/>
 *             &lt;enumeration value="21406"/>
 *             &lt;enumeration value="21407"/>
 *             &lt;enumeration value="21408"/>
 *             &lt;enumeration value="21409"/>
 *             &lt;enumeration value="21410"/>
 *             &lt;enumeration value="21411"/>
 *             &lt;enumeration value="21412"/>
 *             &lt;enumeration value="21413"/>
 *             &lt;enumeration value="21414"/>
 *             &lt;enumeration value="21415"/>
 *             &lt;enumeration value="21416"/>
 *             &lt;enumeration value="21417"/>
 *             &lt;enumeration value="21418"/>
 *             &lt;enumeration value="21419"/>
 *             &lt;enumeration value="21420"/>
 *             &lt;enumeration value="21421"/>
 *             &lt;enumeration value="21422"/>
 *             &lt;enumeration value="21423"/>
 *             &lt;enumeration value="21424"/>
 *             &lt;enumeration value="21425"/>
 *             &lt;enumeration value="21426"/>
 *             &lt;enumeration value="21427"/>
 *             &lt;enumeration value="21428"/>
 *             &lt;enumeration value="21429"/>
 *             &lt;enumeration value="21430"/>
 *             &lt;enumeration value="21431"/>
 *             &lt;enumeration value="21432"/>
 *             &lt;enumeration value="21433"/>
 *             &lt;enumeration value="21434"/>
 *             &lt;enumeration value="21435"/>
 *             &lt;enumeration value="21436"/>
 *             &lt;enumeration value="21437"/>
 *             &lt;enumeration value="21438"/>
 *             &lt;enumeration value="21439"/>
 *             &lt;enumeration value="21440"/>
 *             &lt;enumeration value="21441"/>
 *             &lt;enumeration value="21442"/>
 *             &lt;enumeration value="21443"/>
 *             &lt;enumeration value="21444"/>
 *             &lt;enumeration value="21445"/>
 *             &lt;enumeration value="21446"/>
 *             &lt;enumeration value="21447"/>
 *             &lt;enumeration value="21448"/>
 *             &lt;enumeration value="21449"/>
 *             &lt;enumeration value="21450"/>
 *             &lt;enumeration value="21451"/>
 *             &lt;enumeration value="21452"/>
 *             &lt;enumeration value="21453"/>
 *             &lt;enumeration value="21454"/>
 *             &lt;enumeration value="21455"/>
 *             &lt;enumeration value="21456"/>
 *             &lt;enumeration value="21457"/>
 *             &lt;enumeration value="21458"/>
 *             &lt;enumeration value="21459"/>
 *             &lt;enumeration value="21460"/>
 *             &lt;enumeration value="21461"/>
 *             &lt;enumeration value="21462"/>
 *             &lt;enumeration value="21463"/>
 *             &lt;enumeration value="21464"/>
 *             &lt;enumeration value="21465"/>
 *             &lt;enumeration value="21466"/>
 *             &lt;enumeration value="21467"/>
 *             &lt;enumeration value="21468"/>
 *             &lt;enumeration value="21469"/>
 *             &lt;enumeration value="21470"/>
 *             &lt;enumeration value="21471"/>
 *             &lt;enumeration value="21472"/>
 *             &lt;enumeration value="21473"/>
 *             &lt;enumeration value="21474"/>
 *             &lt;enumeration value="21475"/>
 *             &lt;enumeration value="21476"/>
 *             &lt;enumeration value="21477"/>
 *             &lt;enumeration value="21478"/>
 *             &lt;enumeration value="21479"/>
 *             &lt;enumeration value="21480"/>
 *             &lt;enumeration value="21481"/>
 *             &lt;enumeration value="21482"/>
 *             &lt;enumeration value="21483"/>
 *             &lt;enumeration value="21484"/>
 *             &lt;enumeration value="21485"/>
 *             &lt;enumeration value="21486"/>
 *             &lt;enumeration value="21487"/>
 *             &lt;enumeration value="21488"/>
 *             &lt;enumeration value="21536"/>
 *             &lt;enumeration value="21537"/>
 *             &lt;enumeration value="21538"/>
 *             &lt;enumeration value="21539"/>
 *             &lt;enumeration value="21540"/>
 *             &lt;enumeration value="21541"/>
 *             &lt;enumeration value="21542"/>
 *             &lt;enumeration value="21543"/>
 *             &lt;enumeration value="21544"/>
 *             &lt;enumeration value="21545"/>
 *             &lt;enumeration value="21546"/>
 *             &lt;enumeration value="21547"/>
 *             &lt;enumeration value="21548"/>
 *             &lt;enumeration value="21549"/>
 *             &lt;enumeration value="21550"/>
 *             &lt;enumeration value="21551"/>
 *             &lt;enumeration value="21552"/>
 *             &lt;enumeration value="21553"/>
 *             &lt;enumeration value="21554"/>
 *             &lt;enumeration value="21555"/>
 *             &lt;enumeration value="21556"/>
 *             &lt;enumeration value="21557"/>
 *             &lt;enumeration value="21558"/>
 *             &lt;enumeration value="21559"/>
 *             &lt;enumeration value="21560"/>
 *             &lt;enumeration value="21561"/>
 *             &lt;enumeration value="21562"/>
 *             &lt;enumeration value="21563"/>
 *             &lt;enumeration value="21564"/>
 *             &lt;enumeration value="21565"/>
 *             &lt;enumeration value="21566"/>
 *             &lt;enumeration value="21567"/>
 *             &lt;enumeration value="21568"/>
 *             &lt;enumeration value="21569"/>
 *             &lt;enumeration value="21570"/>
 *             &lt;enumeration value="21571"/>
 *             &lt;enumeration value="21572"/>
 *             &lt;enumeration value="21573"/>
 *             &lt;enumeration value="21574"/>
 *             &lt;enumeration value="21575"/>
 *             &lt;enumeration value="21576"/>
 *             &lt;enumeration value="21577"/>
 *             &lt;enumeration value="21578"/>
 *             &lt;enumeration value="21579"/>
 *             &lt;enumeration value="21580"/>
 *             &lt;enumeration value="21581"/>
 *             &lt;enumeration value="21582"/>
 *             &lt;enumeration value="21583"/>
 *             &lt;enumeration value="21584"/>
 *             &lt;enumeration value="21585"/>
 *             &lt;enumeration value="21586"/>
 *             &lt;enumeration value="21587"/>
 *             &lt;enumeration value="21588"/>
 *             &lt;enumeration value="21589"/>
 *             &lt;enumeration value="21590"/>
 *             &lt;enumeration value="21591"/>
 *             &lt;enumeration value="21592"/>
 *             &lt;enumeration value="21593"/>
 *             &lt;enumeration value="21594"/>
 *             &lt;enumeration value="21595"/>
 *             &lt;enumeration value="21596"/>
 *             &lt;enumeration value="21597"/>
 *             &lt;enumeration value="21598"/>
 *             &lt;enumeration value="21599"/>
 *             &lt;enumeration value="21600"/>
 *             &lt;enumeration value="21601"/>
 *             &lt;enumeration value="21602"/>
 *             &lt;enumeration value="21603"/>
 *             &lt;enumeration value="21604"/>
 *             &lt;enumeration value="21605"/>
 *             &lt;enumeration value="21606"/>
 *             &lt;enumeration value="21607"/>
 *             &lt;enumeration value="21608"/>
 *             &lt;enumeration value="21609"/>
 *             &lt;enumeration value="21610"/>
 *             &lt;enumeration value="21611"/>
 *             &lt;enumeration value="21612"/>
 *             &lt;enumeration value="21613"/>
 *             &lt;enumeration value="21614"/>
 *             &lt;enumeration value="21615"/>
 *             &lt;enumeration value="21616"/>
 *             &lt;enumeration value="21617"/>
 *             &lt;enumeration value="21618"/>
 *             &lt;enumeration value="21619"/>
 *             &lt;enumeration value="21620"/>
 *             &lt;enumeration value="21621"/>
 *             &lt;enumeration value="21622"/>
 *             &lt;enumeration value="21623"/>
 *             &lt;enumeration value="21624"/>
 *             &lt;enumeration value="21625"/>
 *             &lt;enumeration value="21626"/>
 *             &lt;enumeration value="21627"/>
 *             &lt;enumeration value="21628"/>
 *             &lt;enumeration value="21629"/>
 *             &lt;enumeration value="21630"/>
 *             &lt;enumeration value="21631"/>
 *             &lt;enumeration value="21632"/>
 *             &lt;enumeration value="21633"/>
 *             &lt;enumeration value="21634"/>
 *             &lt;enumeration value="21635"/>
 *             &lt;enumeration value="21636"/>
 *             &lt;enumeration value="21637"/>
 *             &lt;enumeration value="21638"/>
 *             &lt;enumeration value="21639"/>
 *             &lt;enumeration value="21640"/>
 *             &lt;enumeration value="21641"/>
 *             &lt;enumeration value="21642"/>
 *             &lt;enumeration value="21643"/>
 *             &lt;enumeration value="21644"/>
 *             &lt;enumeration value="21645"/>
 *             &lt;enumeration value="21646"/>
 *             &lt;enumeration value="21647"/>
 *             &lt;enumeration value="21648"/>
 *             &lt;enumeration value="21649"/>
 *             &lt;enumeration value="21650"/>
 *             &lt;enumeration value="21651"/>
 *             &lt;enumeration value="21652"/>
 *             &lt;enumeration value="21653"/>
 *             &lt;enumeration value="21654"/>
 *             &lt;enumeration value="21655"/>
 *             &lt;enumeration value="21656"/>
 *             &lt;enumeration value="21657"/>
 *             &lt;enumeration value="21658"/>
 *             &lt;enumeration value="21659"/>
 *             &lt;enumeration value="21660"/>
 *             &lt;enumeration value="21661"/>
 *             &lt;enumeration value="21662"/>
 *             &lt;enumeration value="21663"/>
 *             &lt;enumeration value="21664"/>
 *             &lt;enumeration value="21665"/>
 *             &lt;enumeration value="21666"/>
 *             &lt;enumeration value="21667"/>
 *             &lt;enumeration value="21668"/>
 *             &lt;enumeration value="21669"/>
 *             &lt;enumeration value="21670"/>
 *             &lt;enumeration value="21671"/>
 *             &lt;enumeration value="21672"/>
 *             &lt;enumeration value="21673"/>
 *             &lt;enumeration value="21674"/>
 *             &lt;enumeration value="21675"/>
 *             &lt;enumeration value="21676"/>
 *             &lt;enumeration value="21677"/>
 *             &lt;enumeration value="21678"/>
 *             &lt;enumeration value="21679"/>
 *             &lt;enumeration value="21696"/>
 *             &lt;enumeration value="21697"/>
 *             &lt;enumeration value="21698"/>
 *             &lt;enumeration value="21699"/>
 *             &lt;enumeration value="21700"/>
 *             &lt;enumeration value="21701"/>
 *             &lt;enumeration value="21702"/>
 *             &lt;enumeration value="21703"/>
 *             &lt;enumeration value="21704"/>
 *             &lt;enumeration value="21705"/>
 *             &lt;enumeration value="21706"/>
 *             &lt;enumeration value="21707"/>
 *             &lt;enumeration value="21723"/>
 *             &lt;enumeration value="21724"/>
 *             &lt;enumeration value="21725"/>
 *             &lt;enumeration value="21726"/>
 *             &lt;enumeration value="21727"/>
 *             &lt;enumeration value="21728"/>
 *             &lt;enumeration value="21729"/>
 *             &lt;enumeration value="21730"/>
 *             &lt;enumeration value="21731"/>
 *             &lt;enumeration value="21732"/>
 *             &lt;enumeration value="21733"/>
 *             &lt;enumeration value="21734"/>
 *             &lt;enumeration value="21735"/>
 *             &lt;enumeration value="21736"/>
 *             &lt;enumeration value="21737"/>
 *             &lt;enumeration value="21738"/>
 *             &lt;enumeration value="21739"/>
 *             &lt;enumeration value="21762"/>
 *             &lt;enumeration value="21763"/>
 *             &lt;enumeration value="21764"/>
 *             &lt;enumeration value="21765"/>
 *             &lt;enumeration value="21766"/>
 *             &lt;enumeration value="21767"/>
 *             &lt;enumeration value="21768"/>
 *             &lt;enumeration value="21769"/>
 *             &lt;enumeration value="21770"/>
 *             &lt;enumeration value="21771"/>
 *             &lt;enumeration value="21772"/>
 *             &lt;enumeration value="21773"/>
 *             &lt;enumeration value="21774"/>
 *             &lt;enumeration value="21775"/>
 *             &lt;enumeration value="21776"/>
 *             &lt;enumeration value="21777"/>
 *             &lt;enumeration value="21778"/>
 *             &lt;enumeration value="21779"/>
 *             &lt;enumeration value="21780"/>
 *             &lt;enumeration value="21781"/>
 *             &lt;enumeration value="21782"/>
 *             &lt;enumeration value="21783"/>
 *             &lt;enumeration value="21784"/>
 *             &lt;enumeration value="21785"/>
 *             &lt;enumeration value="21786"/>
 *             &lt;enumeration value="21787"/>
 *             &lt;enumeration value="21788"/>
 *             &lt;enumeration value="21789"/>
 *             &lt;enumeration value="21790"/>
 *             &lt;enumeration value="21791"/>
 *             &lt;enumeration value="21792"/>
 *             &lt;enumeration value="21793"/>
 *             &lt;enumeration value="21794"/>
 *             &lt;enumeration value="21795"/>
 *             &lt;enumeration value="21796"/>
 *             &lt;enumeration value="21797"/>
 *             &lt;enumeration value="21798"/>
 *             &lt;enumeration value="21799"/>
 *             &lt;enumeration value="21800"/>
 *             &lt;enumeration value="21801"/>
 *             &lt;enumeration value="21802"/>
 *             &lt;enumeration value="21803"/>
 *             &lt;enumeration value="21804"/>
 *             &lt;enumeration value="21805"/>
 *             &lt;enumeration value="21806"/>
 *             &lt;enumeration value="21807"/>
 *             &lt;enumeration value="21808"/>
 *             &lt;enumeration value="21809"/>
 *             &lt;enumeration value="21810"/>
 *             &lt;enumeration value="21811"/>
 *             &lt;enumeration value="21812"/>
 *             &lt;enumeration value="21813"/>
 *             &lt;enumeration value="21814"/>
 *             &lt;enumeration value="21815"/>
 *             &lt;enumeration value="21816"/>
 *             &lt;enumeration value="21817"/>
 *             &lt;enumeration value="21818"/>
 *             &lt;enumeration value="21819"/>
 *             &lt;enumeration value="21820"/>
 *             &lt;enumeration value="21821"/>
 *             &lt;enumeration value="21822"/>
 *             &lt;enumeration value="21823"/>
 *             &lt;enumeration value="21824"/>
 *             &lt;enumeration value="21825"/>
 *             &lt;enumeration value="21826"/>
 *             &lt;enumeration value="21827"/>
 *             &lt;enumeration value="21828"/>
 *             &lt;enumeration value="21829"/>
 *             &lt;enumeration value="21830"/>
 *             &lt;enumeration value="21831"/>
 *             &lt;enumeration value="21832"/>
 *             &lt;enumeration value="21833"/>
 *             &lt;enumeration value="21834"/>
 *             &lt;enumeration value="21835"/>
 *             &lt;enumeration value="21836"/>
 *             &lt;enumeration value="21837"/>
 *             &lt;enumeration value="21838"/>
 *             &lt;enumeration value="21839"/>
 *             &lt;enumeration value="21840"/>
 *             &lt;enumeration value="21841"/>
 *             &lt;enumeration value="21842"/>
 *             &lt;enumeration value="21843"/>
 *             &lt;enumeration value="21844"/>
 *             &lt;enumeration value="21845"/>
 *             &lt;enumeration value="21846"/>
 *             &lt;enumeration value="21847"/>
 *             &lt;enumeration value="21848"/>
 *             &lt;enumeration value="21849"/>
 *             &lt;enumeration value="21850"/>
 *             &lt;enumeration value="21851"/>
 *             &lt;enumeration value="21852"/>
 *             &lt;enumeration value="21853"/>
 *             &lt;enumeration value="21854"/>
 *             &lt;enumeration value="21855"/>
 *             &lt;enumeration value="21856"/>
 *             &lt;enumeration value="21857"/>
 *             &lt;enumeration value="21858"/>
 *             &lt;enumeration value="21859"/>
 *             &lt;enumeration value="21860"/>
 *             &lt;enumeration value="21861"/>
 *             &lt;enumeration value="21862"/>
 *             &lt;enumeration value="21863"/>
 *             &lt;enumeration value="21864"/>
 *             &lt;enumeration value="21865"/>
 *             &lt;enumeration value="21866"/>
 *             &lt;enumeration value="21867"/>
 *             &lt;enumeration value="21868"/>
 *             &lt;enumeration value="21869"/>
 *             &lt;enumeration value="21870"/>
 *             &lt;enumeration value="21871"/>
 *             &lt;enumeration value="21872"/>
 *             &lt;enumeration value="21873"/>
 *             &lt;enumeration value="21874"/>
 *             &lt;enumeration value="21875"/>
 *             &lt;enumeration value="21876"/>
 *             &lt;enumeration value="21877"/>
 *             &lt;enumeration value="21878"/>
 *             &lt;enumeration value="21879"/>
 *             &lt;enumeration value="21880"/>
 *             &lt;enumeration value="21881"/>
 *             &lt;enumeration value="2503"/>
 *             &lt;enumeration value="2530"/>
 *             &lt;enumeration value="2540"/>
 *             &lt;enumeration value="2541"/>
 *             &lt;enumeration value="2542"/>
 *             &lt;enumeration value="2543"/>
 *             &lt;enumeration value="2786"/>
 *             &lt;enumeration value="2827"/>
 *             &lt;enumeration value="2864"/>
 *             &lt;enumeration value="2883"/>
 *             &lt;enumeration value="2884"/>
 *             &lt;enumeration value="2885"/>
 *             &lt;enumeration value="2886"/>
 *             &lt;enumeration value="2887"/>
 *             &lt;enumeration value="2888"/>
 *             &lt;enumeration value="2889"/>
 *             &lt;enumeration value="2890"/>
 *             &lt;enumeration value="2891"/>
 *             &lt;enumeration value="2892"/>
 *             &lt;enumeration value="2893"/>
 *             &lt;enumeration value="2894"/>
 *             &lt;enumeration value="2895"/>
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
    "contents"
})
@XmlRootElement(name = "text_override")
public class TextOverride {

  @XmlElement(required = true)
  protected Contents contents;
  @XmlAttribute(name = "text_role", required = true)
  protected String textRole;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected short signature;

  /**
   * Ruft den Wert der contents-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Contents }
   */
  public Contents getContents() {
    return contents;
  }

  /**
   * Legt den Wert der contents-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Contents }
   */
  public void setContents(Contents value) {
    this.contents = value;
  }

  /**
   * Ruft den Wert der textRole-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTextRole() {
    return textRole;
  }

  /**
   * Legt den Wert der textRole-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTextRole(String value) {
    this.textRole = value;
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
