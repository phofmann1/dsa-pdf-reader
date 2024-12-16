//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2023.11.07 um 04:39:52 PM CET 
//


package de.pho.realmworks.analyser.exportxsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
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
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}contents"/>
 *           &lt;choice minOccurs="0">
 *             &lt;sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *               &lt;sequence minOccurs="0">
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded" minOccurs="0"/>
 *               &lt;/sequence>
 *             &lt;/sequence>
 *             &lt;sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *               &lt;sequence minOccurs="0">
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded" minOccurs="0"/>
 *               &lt;/sequence>
 *             &lt;/sequence>
 *             &lt;sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}other_spans"/>
 *               &lt;choice minOccurs="0">
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *                   &lt;sequence minOccurs="0">
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" minOccurs="0"/>
 *                   &lt;/sequence>
 *                 &lt;/sequence>
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                   &lt;sequence minOccurs="0">
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" minOccurs="0"/>
 *                   &lt;/sequence>
 *                 &lt;/sequence>
 *               &lt;/choice>
 *             &lt;/sequence>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}game_date"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}annotation" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}gm_directions"/>
 *           &lt;choice minOccurs="0">
 *             &lt;sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" minOccurs="0"/>
 *             &lt;/sequence>
 *             &lt;sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}other_spans"/>
 *               &lt;choice>
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *                   &lt;sequence minOccurs="0">
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                     &lt;sequence minOccurs="0">
 *                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link"/>
 *                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                     &lt;/sequence>
 *                   &lt;/sequence>
 *                 &lt;/sequence>
 *                 &lt;sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink" maxOccurs="unbounded"/>
 *                   &lt;sequence minOccurs="0">
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}link" maxOccurs="unbounded"/>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink"/>
 *                   &lt;/sequence>
 *                 &lt;/sequence>
 *               &lt;/choice>
 *             &lt;/sequence>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dlink"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="facet_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Facet_346"/>
 *             &lt;enumeration value="Facet_407"/>
 *             &lt;enumeration value="Facet_409"/>
 *             &lt;enumeration value="Facet_413"/>
 *             &lt;enumeration value="Facet_414"/>
 *             &lt;enumeration value="Facet_465"/>
 *             &lt;enumeration value="Facet_506"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Game"/>
 *             &lt;enumeration value="Labeled_Text"/>
 *             &lt;enumeration value="Multi_Line"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="00C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="00EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="02EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="03EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="056C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="05EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="05EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="08EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0A6C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="0EE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0EED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="11E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="11ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="16E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="16E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="16EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="19E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="19E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="19EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1BEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1CE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1EEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1FE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1FE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="225C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="235C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="27C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="27DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="28ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="295F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="29EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2AE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2BED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2CEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2D5A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2DE19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2DE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2E5A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2E5F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2EDE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="30599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="30DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="325B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="32BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="33C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="34659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="34E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="34E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="35DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="365A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="366B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="36E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="36EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="375B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="38E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="39DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3B5A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="3B6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3B6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3CE19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3DBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="3E6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3EEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="42E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="43689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="44ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="45E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="476D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="48689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="49DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4BE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4C6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="4EE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="52EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="55EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="575F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5C6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5D6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5E6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5F6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5FE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5FE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="62E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="62E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="635F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="63609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="65E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="68609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="68EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6A579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="6AEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6BE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6BEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6DEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6EE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6EE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="70E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="71E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="736D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="73E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="766D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="776D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="77E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="78E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="78EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="79EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7AE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7AE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7BE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7DE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7FEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="80E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="82EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="83579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="83E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8A5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8A5D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8B5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8CE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8CE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8EE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8FE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8FE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="91E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="92BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="97659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="98E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9A659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9BE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9C579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9E6A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A0659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A06B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A0E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A1E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A1EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A36A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A3E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A5659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A56B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A5BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="A6EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A8E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A9EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="AAEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ABE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ACE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ADEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="AEBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="AFBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="AFE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B5E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B6E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B85D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B8BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B8E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B9E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BABE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="BAF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="BBF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="BCE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BEE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BEF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="BFE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C0EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C1EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C2BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C3BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C3E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C3EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C3EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C4609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C4BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C4E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C5609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C5F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="C7E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C7E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C8679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="C9BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C9E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CBEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CCBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CCE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CD679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CDE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CE579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CEE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CEEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0FF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="D2659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D3BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D3E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D4E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D6BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D7659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D7BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D7E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D7E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D7EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D9EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DB6C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="DCE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DEBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E06C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="E3BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E3EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E6EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E7579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="EABE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="EAE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EAEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ECE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EDE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EDEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EEE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EFE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F0EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F1BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F1E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F3EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F4DF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F7599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F8599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F8E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F9DF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F9EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FBE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FCEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FDDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="11593"/>
 *             &lt;enumeration value="11595"/>
 *             &lt;enumeration value="11631"/>
 *             &lt;enumeration value="11633"/>
 *             &lt;enumeration value="11651"/>
 *             &lt;enumeration value="11653"/>
 *             &lt;enumeration value="11659"/>
 *             &lt;enumeration value="11661"/>
 *             &lt;enumeration value="11676"/>
 *             &lt;enumeration value="11678"/>
 *             &lt;enumeration value="11694"/>
 *             &lt;enumeration value="11696"/>
 *             &lt;enumeration value="11710"/>
 *             &lt;enumeration value="11712"/>
 *             &lt;enumeration value="11718"/>
 *             &lt;enumeration value="11720"/>
 *             &lt;enumeration value="11748"/>
 *             &lt;enumeration value="11750"/>
 *             &lt;enumeration value="11757"/>
 *             &lt;enumeration value="11759"/>
 *             &lt;enumeration value="11774"/>
 *             &lt;enumeration value="11776"/>
 *             &lt;enumeration value="11790"/>
 *             &lt;enumeration value="11792"/>
 *             &lt;enumeration value="11826"/>
 *             &lt;enumeration value="11828"/>
 *             &lt;enumeration value="11861"/>
 *             &lt;enumeration value="11863"/>
 *             &lt;enumeration value="11873"/>
 *             &lt;enumeration value="11885"/>
 *             &lt;enumeration value="11887"/>
 *             &lt;enumeration value="11895"/>
 *             &lt;enumeration value="11897"/>
 *             &lt;enumeration value="11898"/>
 *             &lt;enumeration value="11908"/>
 *             &lt;enumeration value="11910"/>
 *             &lt;enumeration value="11921"/>
 *             &lt;enumeration value="11923"/>
 *             &lt;enumeration value="11929"/>
 *             &lt;enumeration value="11938"/>
 *             &lt;enumeration value="11940"/>
 *             &lt;enumeration value="11948"/>
 *             &lt;enumeration value="11950"/>
 *             &lt;enumeration value="11960"/>
 *             &lt;enumeration value="11962"/>
 *             &lt;enumeration value="11980"/>
 *             &lt;enumeration value="11982"/>
 *             &lt;enumeration value="11991"/>
 *             &lt;enumeration value="11993"/>
 *             &lt;enumeration value="12039"/>
 *             &lt;enumeration value="12041"/>
 *             &lt;enumeration value="12048"/>
 *             &lt;enumeration value="12050"/>
 *             &lt;enumeration value="12057"/>
 *             &lt;enumeration value="12059"/>
 *             &lt;enumeration value="12066"/>
 *             &lt;enumeration value="12068"/>
 *             &lt;enumeration value="12074"/>
 *             &lt;enumeration value="12076"/>
 *             &lt;enumeration value="12083"/>
 *             &lt;enumeration value="12085"/>
 *             &lt;enumeration value="12123"/>
 *             &lt;enumeration value="12125"/>
 *             &lt;enumeration value="12144"/>
 *             &lt;enumeration value="12146"/>
 *             &lt;enumeration value="12164"/>
 *             &lt;enumeration value="12166"/>
 *             &lt;enumeration value="12182"/>
 *             &lt;enumeration value="12184"/>
 *             &lt;enumeration value="12190"/>
 *             &lt;enumeration value="12192"/>
 *             &lt;enumeration value="12208"/>
 *             &lt;enumeration value="12210"/>
 *             &lt;enumeration value="12217"/>
 *             &lt;enumeration value="12219"/>
 *             &lt;enumeration value="12227"/>
 *             &lt;enumeration value="12229"/>
 *             &lt;enumeration value="12235"/>
 *             &lt;enumeration value="12237"/>
 *             &lt;enumeration value="12271"/>
 *             &lt;enumeration value="12273"/>
 *             &lt;enumeration value="12280"/>
 *             &lt;enumeration value="12282"/>
 *             &lt;enumeration value="12299"/>
 *             &lt;enumeration value="12301"/>
 *             &lt;enumeration value="12318"/>
 *             &lt;enumeration value="12320"/>
 *             &lt;enumeration value="12328"/>
 *             &lt;enumeration value="12330"/>
 *             &lt;enumeration value="12337"/>
 *             &lt;enumeration value="12339"/>
 *             &lt;enumeration value="12345"/>
 *             &lt;enumeration value="12347"/>
 *             &lt;enumeration value="12353"/>
 *             &lt;enumeration value="12355"/>
 *             &lt;enumeration value="12361"/>
 *             &lt;enumeration value="12363"/>
 *             &lt;enumeration value="12371"/>
 *             &lt;enumeration value="12373"/>
 *             &lt;enumeration value="12378"/>
 *             &lt;enumeration value="12398"/>
 *             &lt;enumeration value="12400"/>
 *             &lt;enumeration value="12407"/>
 *             &lt;enumeration value="12409"/>
 *             &lt;enumeration value="12416"/>
 *             &lt;enumeration value="12418"/>
 *             &lt;enumeration value="12426"/>
 *             &lt;enumeration value="12428"/>
 *             &lt;enumeration value="12435"/>
 *             &lt;enumeration value="12437"/>
 *             &lt;enumeration value="12444"/>
 *             &lt;enumeration value="12446"/>
 *             &lt;enumeration value="12454"/>
 *             &lt;enumeration value="12456"/>
 *             &lt;enumeration value="12472"/>
 *             &lt;enumeration value="12474"/>
 *             &lt;enumeration value="12507"/>
 *             &lt;enumeration value="12509"/>
 *             &lt;enumeration value="12525"/>
 *             &lt;enumeration value="12527"/>
 *             &lt;enumeration value="12537"/>
 *             &lt;enumeration value="12539"/>
 *             &lt;enumeration value="12547"/>
 *             &lt;enumeration value="12549"/>
 *             &lt;enumeration value="12556"/>
 *             &lt;enumeration value="12558"/>
 *             &lt;enumeration value="12566"/>
 *             &lt;enumeration value="12568"/>
 *             &lt;enumeration value="12579"/>
 *             &lt;enumeration value="12581"/>
 *             &lt;enumeration value="12728"/>
 *             &lt;enumeration value="12742"/>
 *             &lt;enumeration value="12758"/>
 *             &lt;enumeration value="12769"/>
 *             &lt;enumeration value="12770"/>
 *             &lt;enumeration value="12784"/>
 *             &lt;enumeration value="12831"/>
 *             &lt;enumeration value="12835"/>
 *             &lt;enumeration value="12858"/>
 *             &lt;enumeration value="12859"/>
 *             &lt;enumeration value="13342"/>
 *             &lt;enumeration value="13343"/>
 *             &lt;enumeration value="13345"/>
 *             &lt;enumeration value="13346"/>
 *             &lt;enumeration value="13526"/>
 *             &lt;enumeration value="13527"/>
 *             &lt;enumeration value="13528"/>
 *             &lt;enumeration value="13534"/>
 *             &lt;enumeration value="13594"/>
 *             &lt;enumeration value="13625"/>
 *             &lt;enumeration value="13629"/>
 *             &lt;enumeration value="13636"/>
 *             &lt;enumeration value="13640"/>
 *             &lt;enumeration value="13691"/>
 *             &lt;enumeration value="13694"/>
 *             &lt;enumeration value="14000"/>
 *             &lt;enumeration value="14006"/>
 *             &lt;enumeration value="14012"/>
 *             &lt;enumeration value="14013"/>
 *             &lt;enumeration value="14018"/>
 *             &lt;enumeration value="14030"/>
 *             &lt;enumeration value="14040"/>
 *             &lt;enumeration value="14092"/>
 *             &lt;enumeration value="14095"/>
 *             &lt;enumeration value="14101"/>
 *             &lt;enumeration value="14106"/>
 *             &lt;enumeration value="14170"/>
 *             &lt;enumeration value="14175"/>
 *             &lt;enumeration value="14255"/>
 *             &lt;enumeration value="14258"/>
 *             &lt;enumeration value="14311"/>
 *             &lt;enumeration value="14316"/>
 *             &lt;enumeration value="14340"/>
 *             &lt;enumeration value="14342"/>
 *             &lt;enumeration value="14345"/>
 *             &lt;enumeration value="14397"/>
 *             &lt;enumeration value="14403"/>
 *             &lt;enumeration value="14602"/>
 *             &lt;enumeration value="14693"/>
 *             &lt;enumeration value="14698"/>
 *             &lt;enumeration value="14773"/>
 *             &lt;enumeration value="24196"/>
 *             &lt;enumeration value="24223"/>
 *             &lt;enumeration value="24224"/>
 *             &lt;enumeration value="24225"/>
 *             &lt;enumeration value="24231"/>
 *             &lt;enumeration value="24247"/>
 *             &lt;enumeration value="24248"/>
 *             &lt;enumeration value="24254"/>
 *             &lt;enumeration value="24260"/>
 *             &lt;enumeration value="24343"/>
 *             &lt;enumeration value="24360"/>
 *             &lt;enumeration value="24428"/>
 *             &lt;enumeration value="24441"/>
 *             &lt;enumeration value="24487"/>
 *             &lt;enumeration value="24493"/>
 *             &lt;enumeration value="24500"/>
 *             &lt;enumeration value="24539"/>
 *             &lt;enumeration value="24560"/>
 *             &lt;enumeration value="24566"/>
 *             &lt;enumeration value="28982"/>
 *             &lt;enumeration value="28984"/>
 *             &lt;enumeration value="28986"/>
 *             &lt;enumeration value="28988"/>
 *             &lt;enumeration value="29247"/>
 *             &lt;enumeration value="29248"/>
 *             &lt;enumeration value="29250"/>
 *             &lt;enumeration value="29252"/>
 *             &lt;enumeration value="29285"/>
 *             &lt;enumeration value="29287"/>
 *             &lt;enumeration value="29288"/>
 *             &lt;enumeration value="29291"/>
 *             &lt;enumeration value="29293"/>
 *             &lt;enumeration value="29354"/>
 *             &lt;enumeration value="29357"/>
 *             &lt;enumeration value="29359"/>
 *             &lt;enumeration value="29525"/>
 *             &lt;enumeration value="29526"/>
 *             &lt;enumeration value="29533"/>
 *             &lt;enumeration value="29563"/>
 *             &lt;enumeration value="29574"/>
 *             &lt;enumeration value="29587"/>
 *             &lt;enumeration value="29641"/>
 *             &lt;enumeration value="68292"/>
 *             &lt;enumeration value="68303"/>
 *             &lt;enumeration value="68366"/>
 *             &lt;enumeration value="68409"/>
 *             &lt;enumeration value="68410"/>
 *             &lt;enumeration value="68413"/>
 *             &lt;enumeration value="68418"/>
 *             &lt;enumeration value="68421"/>
 *             &lt;enumeration value="68428"/>
 *             &lt;enumeration value="68442"/>
 *             &lt;enumeration value="68456"/>
 *             &lt;enumeration value="68459"/>
 *             &lt;enumeration value="68485"/>
 *             &lt;enumeration value="68488"/>
 *             &lt;enumeration value="68491"/>
 *             &lt;enumeration value="68498"/>
 *             &lt;enumeration value="68505"/>
 *             &lt;enumeration value="68508"/>
 *             &lt;enumeration value="68528"/>
 *             &lt;enumeration value="68546"/>
 *             &lt;enumeration value="68550"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="purpose">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Directions_Only"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="style">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Handout"/>
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
@XmlRootElement(name = "snippet")
public class Snippet {

  @XmlElementRefs({
      @XmlElementRef(name = "contents", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Contents.class, required = false),
      @XmlElementRef(name = "dlink", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Dlink.class, required = false),
      @XmlElementRef(name = "link", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Link.class, required = false),
      @XmlElementRef(name = "game_date", namespace = "urn:lonewolfdevel.com:realm-works-export", type = GameDate.class, required = false),
      @XmlElementRef(name = "gm_directions", namespace = "urn:lonewolfdevel.com:realm-works-export", type = JAXBElement.class, required = false),
      @XmlElementRef(name = "annotation", namespace = "urn:lonewolfdevel.com:realm-works-export", type = JAXBElement.class, required = false),
      @XmlElementRef(name = "other_spans", namespace = "urn:lonewolfdevel.com:realm-works-export", type = OtherSpans.class, required = false)
  })
  protected List<Object> content;
  @XmlAttribute(name = "facet_id")
  protected String facetId;
  @XmlAttribute(name = "type", required = true)
  protected String type;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected int signature;
  @XmlAttribute(name = "purpose")
  protected String purpose;
  @XmlAttribute(name = "style")
  protected String style;

  /**
   * Ruft das restliche Contentmodell ab.
   *
   * <p>
   * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
   * Der Feldname "Link" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
   * Zeile 4405 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
   * Zeile 4402 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
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
   * {@link Contents }
   * {@link Dlink }
   * {@link Link }
   * {@link GameDate }
   * {@link JAXBElement }{@code <}{@link String }{@code >}
   * {@link JAXBElement }{@code <}{@link String }{@code >}
   * {@link OtherSpans }
   */
  public List<Object> getContent() {
    if (content == null) {
      content = new ArrayList<Object>();
    }
    return this.content;
  }

  /**
   * Ruft den Wert der facetId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getFacetId() {
    return facetId;
  }

  /**
   * Legt den Wert der facetId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setFacetId(String value) {
    this.facetId = value;
  }

  /**
   * Ruft den Wert der type-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getType() {
    return type;
  }

  /**
   * Legt den Wert der type-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setType(String value) {
    this.type = value;
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
   * Ruft den Wert der style-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getStyle() {
    return style;
  }

  /**
   * Legt den Wert der style-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setStyle(String value) {
    this.style = value;
  }

}
