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
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}snippet" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}section"/>
 *       &lt;/choice>
 *       &lt;attribute name="partition_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Partition_115"/>
 *             &lt;enumeration value="Partition_123"/>
 *             &lt;enumeration value="Partition_133"/>
 *             &lt;enumeration value="Partition_590"/>
 *             &lt;enumeration value="Partition_591"/>
 *             &lt;enumeration value="Partition_593"/>
 *             &lt;enumeration value="Partition_620"/>
 *             &lt;enumeration value="Partition_645"/>
 *             &lt;enumeration value="Partition_659"/>
 *             &lt;enumeration value="Partition_679"/>
 *             &lt;enumeration value="Partition_680"/>
 *             &lt;enumeration value="Partition_681"/>
 *             &lt;enumeration value="Partition_683"/>
 *             &lt;enumeration value="Partition_692"/>
 *             &lt;enumeration value="Partition_724"/>
 *             &lt;enumeration value="Partition_730"/>
 *             &lt;enumeration value="Partition_743"/>
 *             &lt;enumeration value="Partition_747"/>
 *             &lt;enumeration value="Partition_764"/>
 *             &lt;enumeration value="Partition_783"/>
 *             &lt;enumeration value="Partition_795"/>
 *             &lt;enumeration value="Partition_800"/>
 *             &lt;enumeration value="Partition_825"/>
 *             &lt;enumeration value="Partition_826"/>
 *             &lt;enumeration value="Partition_827"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="005A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="01EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="02EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="04EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="04EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="066C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="07E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="07EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="096C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="0DE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0DED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0FBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="105E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="10839BB6-A283-69ED-7C14-689353716BA9"/>
 *             &lt;enumeration value="10E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="10ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="14E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="14EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="17E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="17EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="195D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="1ABF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="1AEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1BE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1DEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1EE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="20E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="26699AB6-2F8B-AF47-C003-689353716BA9"/>
 *             &lt;enumeration value="26C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="26DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="27ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="285F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="28EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="29E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2A5F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2AE19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2AED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2BDE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2BEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2CE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2D5F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="30EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="315B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="31BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="31E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="32C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="335B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="33659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="33E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="34DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="355A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="35659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="356B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="35E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="35EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="365B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="37599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="375A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="376B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="37E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="38659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="38DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="38E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="38EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3A5A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="3A6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3BE19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3BE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3C6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3CBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="3D6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3DEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="40EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="40ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="42689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="43ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="44689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="44E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="466D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="47689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="486D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="48DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4AE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4B6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="4DE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="50579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="51EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="525E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="52DA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="52E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="54EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5B6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5C569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5D5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5EE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5EE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="61E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="61E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="625F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="62609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="64E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="66E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="67609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="67EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="69579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="69EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6AE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6AEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6CEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6DE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6DE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6DE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6FE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="70E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="72E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="735D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="746D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="756D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="76E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="77E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="79E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="79E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7AE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7CE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7EEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7FE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="81EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="82579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="82E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="83E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="86E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="88E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="895D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8BE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8BE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8BE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8DE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8EE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8EE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8EE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="90E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="91BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="97E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="98659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="995C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="99659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9AE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9B579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9B589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9D6A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9F659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9F6A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9F6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9FE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9FE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A0E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A0EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A1659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A16B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A26A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A2E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A3E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A3EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A46B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="A4BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="A5E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A5EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A7E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A8EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A9EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="AAE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ABE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ACEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ADBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="AEE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B35E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B4E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B5E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B75D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B7BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B7E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B8E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B9BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B9E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B9E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B9F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="BAEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BDF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="BEE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BFEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BFEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C1BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C2559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C2E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C2EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C2EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C3E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C6E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C6E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C7679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="C8BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C8E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C9679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CAEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CBBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CBE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CC679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CCE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CD579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CDEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CF559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CFE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CFE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D1659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D2589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D2BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D2E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D3659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D3E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D45D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D5E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D5EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D6659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D6E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D6E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D8EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D95D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="DA6C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="DBE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DC6C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="DDBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DEBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DF6C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="E0DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E25D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="E2BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E2EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E3E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E5EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E6579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="E9BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E9E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E9EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EBE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ECE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ECEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EDE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EEE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EEE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EF569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="EFDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EFEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F0BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F0E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F2EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F4E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F7E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F8559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F8DF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F8EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F95A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FAE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FBEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FCDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FFBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="FFEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="11577"/>
 *             &lt;enumeration value="11587"/>
 *             &lt;enumeration value="11592"/>
 *             &lt;enumeration value="11594"/>
 *             &lt;enumeration value="11630"/>
 *             &lt;enumeration value="11632"/>
 *             &lt;enumeration value="11650"/>
 *             &lt;enumeration value="11652"/>
 *             &lt;enumeration value="11658"/>
 *             &lt;enumeration value="11660"/>
 *             &lt;enumeration value="11670"/>
 *             &lt;enumeration value="11675"/>
 *             &lt;enumeration value="11677"/>
 *             &lt;enumeration value="11693"/>
 *             &lt;enumeration value="11695"/>
 *             &lt;enumeration value="11709"/>
 *             &lt;enumeration value="11711"/>
 *             &lt;enumeration value="11717"/>
 *             &lt;enumeration value="11719"/>
 *             &lt;enumeration value="11747"/>
 *             &lt;enumeration value="11749"/>
 *             &lt;enumeration value="11756"/>
 *             &lt;enumeration value="11758"/>
 *             &lt;enumeration value="11773"/>
 *             &lt;enumeration value="11775"/>
 *             &lt;enumeration value="11789"/>
 *             &lt;enumeration value="11791"/>
 *             &lt;enumeration value="11797"/>
 *             &lt;enumeration value="11825"/>
 *             &lt;enumeration value="11827"/>
 *             &lt;enumeration value="11845"/>
 *             &lt;enumeration value="11860"/>
 *             &lt;enumeration value="11862"/>
 *             &lt;enumeration value="11872"/>
 *             &lt;enumeration value="11874"/>
 *             &lt;enumeration value="11884"/>
 *             &lt;enumeration value="11886"/>
 *             &lt;enumeration value="11894"/>
 *             &lt;enumeration value="11896"/>
 *             &lt;enumeration value="11907"/>
 *             &lt;enumeration value="11909"/>
 *             &lt;enumeration value="11920"/>
 *             &lt;enumeration value="11922"/>
 *             &lt;enumeration value="11928"/>
 *             &lt;enumeration value="11930"/>
 *             &lt;enumeration value="11937"/>
 *             &lt;enumeration value="11939"/>
 *             &lt;enumeration value="11947"/>
 *             &lt;enumeration value="11949"/>
 *             &lt;enumeration value="11959"/>
 *             &lt;enumeration value="11961"/>
 *             &lt;enumeration value="11979"/>
 *             &lt;enumeration value="11981"/>
 *             &lt;enumeration value="11990"/>
 *             &lt;enumeration value="11992"/>
 *             &lt;enumeration value="12016"/>
 *             &lt;enumeration value="12038"/>
 *             &lt;enumeration value="12040"/>
 *             &lt;enumeration value="12047"/>
 *             &lt;enumeration value="12049"/>
 *             &lt;enumeration value="12056"/>
 *             &lt;enumeration value="12058"/>
 *             &lt;enumeration value="12065"/>
 *             &lt;enumeration value="12067"/>
 *             &lt;enumeration value="12073"/>
 *             &lt;enumeration value="12075"/>
 *             &lt;enumeration value="12082"/>
 *             &lt;enumeration value="12084"/>
 *             &lt;enumeration value="12102"/>
 *             &lt;enumeration value="12122"/>
 *             &lt;enumeration value="12124"/>
 *             &lt;enumeration value="12143"/>
 *             &lt;enumeration value="12145"/>
 *             &lt;enumeration value="12163"/>
 *             &lt;enumeration value="12165"/>
 *             &lt;enumeration value="12181"/>
 *             &lt;enumeration value="12183"/>
 *             &lt;enumeration value="12189"/>
 *             &lt;enumeration value="12191"/>
 *             &lt;enumeration value="12207"/>
 *             &lt;enumeration value="12209"/>
 *             &lt;enumeration value="12216"/>
 *             &lt;enumeration value="12218"/>
 *             &lt;enumeration value="12226"/>
 *             &lt;enumeration value="12228"/>
 *             &lt;enumeration value="12234"/>
 *             &lt;enumeration value="12236"/>
 *             &lt;enumeration value="12270"/>
 *             &lt;enumeration value="12272"/>
 *             &lt;enumeration value="12279"/>
 *             &lt;enumeration value="12281"/>
 *             &lt;enumeration value="12298"/>
 *             &lt;enumeration value="12300"/>
 *             &lt;enumeration value="12317"/>
 *             &lt;enumeration value="12319"/>
 *             &lt;enumeration value="12327"/>
 *             &lt;enumeration value="12329"/>
 *             &lt;enumeration value="12336"/>
 *             &lt;enumeration value="12338"/>
 *             &lt;enumeration value="12344"/>
 *             &lt;enumeration value="12346"/>
 *             &lt;enumeration value="12352"/>
 *             &lt;enumeration value="12354"/>
 *             &lt;enumeration value="12360"/>
 *             &lt;enumeration value="12362"/>
 *             &lt;enumeration value="12370"/>
 *             &lt;enumeration value="12372"/>
 *             &lt;enumeration value="12392"/>
 *             &lt;enumeration value="12397"/>
 *             &lt;enumeration value="12399"/>
 *             &lt;enumeration value="12406"/>
 *             &lt;enumeration value="12408"/>
 *             &lt;enumeration value="12415"/>
 *             &lt;enumeration value="12417"/>
 *             &lt;enumeration value="12425"/>
 *             &lt;enumeration value="12427"/>
 *             &lt;enumeration value="12434"/>
 *             &lt;enumeration value="12436"/>
 *             &lt;enumeration value="12443"/>
 *             &lt;enumeration value="12445"/>
 *             &lt;enumeration value="12453"/>
 *             &lt;enumeration value="12455"/>
 *             &lt;enumeration value="12463"/>
 *             &lt;enumeration value="12471"/>
 *             &lt;enumeration value="12473"/>
 *             &lt;enumeration value="12506"/>
 *             &lt;enumeration value="12508"/>
 *             &lt;enumeration value="12524"/>
 *             &lt;enumeration value="12526"/>
 *             &lt;enumeration value="12536"/>
 *             &lt;enumeration value="12538"/>
 *             &lt;enumeration value="12546"/>
 *             &lt;enumeration value="12548"/>
 *             &lt;enumeration value="12555"/>
 *             &lt;enumeration value="12557"/>
 *             &lt;enumeration value="12565"/>
 *             &lt;enumeration value="12567"/>
 *             &lt;enumeration value="12578"/>
 *             &lt;enumeration value="12580"/>
 *             &lt;enumeration value="12594"/>
 *             &lt;enumeration value="12614"/>
 *             &lt;enumeration value="12710"/>
 *             &lt;enumeration value="12727"/>
 *             &lt;enumeration value="12741"/>
 *             &lt;enumeration value="12755"/>
 *             &lt;enumeration value="12768"/>
 *             &lt;enumeration value="12778"/>
 *             &lt;enumeration value="12783"/>
 *             &lt;enumeration value="12830"/>
 *             &lt;enumeration value="12844"/>
 *             &lt;enumeration value="12849"/>
 *             &lt;enumeration value="12854"/>
 *             &lt;enumeration value="12867"/>
 *             &lt;enumeration value="13102"/>
 *             &lt;enumeration value="13181"/>
 *             &lt;enumeration value="13341"/>
 *             &lt;enumeration value="13364"/>
 *             &lt;enumeration value="13411"/>
 *             &lt;enumeration value="13525"/>
 *             &lt;enumeration value="13533"/>
 *             &lt;enumeration value="13589"/>
 *             &lt;enumeration value="13592"/>
 *             &lt;enumeration value="13593"/>
 *             &lt;enumeration value="13624"/>
 *             &lt;enumeration value="13627"/>
 *             &lt;enumeration value="13628"/>
 *             &lt;enumeration value="13635"/>
 *             &lt;enumeration value="13638"/>
 *             &lt;enumeration value="13639"/>
 *             &lt;enumeration value="13690"/>
 *             &lt;enumeration value="13692"/>
 *             &lt;enumeration value="13693"/>
 *             &lt;enumeration value="13699"/>
 *             &lt;enumeration value="13999"/>
 *             &lt;enumeration value="14004"/>
 *             &lt;enumeration value="14005"/>
 *             &lt;enumeration value="14011"/>
 *             &lt;enumeration value="14016"/>
 *             &lt;enumeration value="14017"/>
 *             &lt;enumeration value="14023"/>
 *             &lt;enumeration value="14028"/>
 *             &lt;enumeration value="14029"/>
 *             &lt;enumeration value="14035"/>
 *             &lt;enumeration value="14038"/>
 *             &lt;enumeration value="14039"/>
 *             &lt;enumeration value="14091"/>
 *             &lt;enumeration value="14093"/>
 *             &lt;enumeration value="14094"/>
 *             &lt;enumeration value="14100"/>
 *             &lt;enumeration value="14104"/>
 *             &lt;enumeration value="14105"/>
 *             &lt;enumeration value="14123"/>
 *             &lt;enumeration value="14169"/>
 *             &lt;enumeration value="14173"/>
 *             &lt;enumeration value="14174"/>
 *             &lt;enumeration value="14254"/>
 *             &lt;enumeration value="14256"/>
 *             &lt;enumeration value="14257"/>
 *             &lt;enumeration value="14310"/>
 *             &lt;enumeration value="14314"/>
 *             &lt;enumeration value="14315"/>
 *             &lt;enumeration value="14339"/>
 *             &lt;enumeration value="14343"/>
 *             &lt;enumeration value="14344"/>
 *             &lt;enumeration value="14350"/>
 *             &lt;enumeration value="14396"/>
 *             &lt;enumeration value="14401"/>
 *             &lt;enumeration value="14402"/>
 *             &lt;enumeration value="14487"/>
 *             &lt;enumeration value="14545"/>
 *             &lt;enumeration value="14601"/>
 *             &lt;enumeration value="14692"/>
 *             &lt;enumeration value="14696"/>
 *             &lt;enumeration value="14697"/>
 *             &lt;enumeration value="14722"/>
 *             &lt;enumeration value="14768"/>
 *             &lt;enumeration value="14771"/>
 *             &lt;enumeration value="14772"/>
 *             &lt;enumeration value="24195"/>
 *             &lt;enumeration value="24211"/>
 *             &lt;enumeration value="24222"/>
 *             &lt;enumeration value="24230"/>
 *             &lt;enumeration value="24242"/>
 *             &lt;enumeration value="24253"/>
 *             &lt;enumeration value="24259"/>
 *             &lt;enumeration value="24269"/>
 *             &lt;enumeration value="24277"/>
 *             &lt;enumeration value="24290"/>
 *             &lt;enumeration value="24295"/>
 *             &lt;enumeration value="24342"/>
 *             &lt;enumeration value="24359"/>
 *             &lt;enumeration value="24407"/>
 *             &lt;enumeration value="24427"/>
 *             &lt;enumeration value="24440"/>
 *             &lt;enumeration value="24486"/>
 *             &lt;enumeration value="24492"/>
 *             &lt;enumeration value="24499"/>
 *             &lt;enumeration value="24507"/>
 *             &lt;enumeration value="24514"/>
 *             &lt;enumeration value="24538"/>
 *             &lt;enumeration value="24559"/>
 *             &lt;enumeration value="24565"/>
 *             &lt;enumeration value="28457"/>
 *             &lt;enumeration value="28981"/>
 *             &lt;enumeration value="28985"/>
 *             &lt;enumeration value="28987"/>
 *             &lt;enumeration value="29246"/>
 *             &lt;enumeration value="29249"/>
 *             &lt;enumeration value="29251"/>
 *             &lt;enumeration value="29284"/>
 *             &lt;enumeration value="29290"/>
 *             &lt;enumeration value="29292"/>
 *             &lt;enumeration value="29353"/>
 *             &lt;enumeration value="29356"/>
 *             &lt;enumeration value="29358"/>
 *             &lt;enumeration value="29362"/>
 *             &lt;enumeration value="29519"/>
 *             &lt;enumeration value="29524"/>
 *             &lt;enumeration value="29527"/>
 *             &lt;enumeration value="29543"/>
 *             &lt;enumeration value="29562"/>
 *             &lt;enumeration value="29568"/>
 *             &lt;enumeration value="29573"/>
 *             &lt;enumeration value="29586"/>
 *             &lt;enumeration value="29592"/>
 *             &lt;enumeration value="29597"/>
 *             &lt;enumeration value="29602"/>
 *             &lt;enumeration value="29607"/>
 *             &lt;enumeration value="29612"/>
 *             &lt;enumeration value="29619"/>
 *             &lt;enumeration value="29624"/>
 *             &lt;enumeration value="29629"/>
 *             &lt;enumeration value="29634"/>
 *             &lt;enumeration value="29640"/>
 *             &lt;enumeration value="33399"/>
 *             &lt;enumeration value="68538"/>
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
    "snippet",
    "section"
})
@XmlRootElement(name = "section")
public class Section {

  protected List<Snippet> snippet;
  protected Section section;
  @XmlAttribute(name = "partition_id", required = true)
  protected String partitionId;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected int signature;

  /**
   * Gets the value of the snippet property.
   *
   * <p>
   * This accessor method returns a reference to the live list,
   * not a snapshot. Therefore any modification you make to the
   * returned list will be present inside the JAXB object.
   * This is why there is not a <CODE>set</CODE> method for the snippet property.
   *
   * <p>
   * For example, to add a new item, do as follows:
   * <pre>
   *    getSnippet().add(newItem);
   * </pre>
   *
   *
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link Snippet }
   */
  public List<Snippet> getSnippet() {
    if (snippet == null) {
      snippet = new ArrayList<Snippet>();
    }
    return this.snippet;
  }

  /**
   * Ruft den Wert der section-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Section }
   */
  public Section getSection() {
    return section;
  }

  /**
   * Legt den Wert der section-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Section }
   */
  public void setSection(Section value) {
    this.section = value;
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
