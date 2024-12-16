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
 *       &lt;attribute name="tag_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Tag_1"/>
 *             &lt;enumeration value="Tag_1305"/>
 *             &lt;enumeration value="Tag_1382"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_auto_assign" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="00EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="03089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="03EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="04089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="04CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="06C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="06E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="07C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="07CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="08C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="09C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="09CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="0AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="0B089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="0CE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0CED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0E089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="0EBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="0ECA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="0F5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="0F839BB6-A283-69ED-7C14-689353716BA9"/>
 *             &lt;enumeration value="0FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="10C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="10CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="12C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="13C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="13E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="13EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="14079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="14BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="14C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="14E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="14E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="14EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="16079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="16C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="16CA9BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="185D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="18C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="19089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="19EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1AE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1B089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="1BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="1EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1F089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="1FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="1FE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="20089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="20C09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="20DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="21089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="21C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="22089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="23C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="24C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="25089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="25699AB6-2F8B-AF47-C003-689353716BA9"/>
 *             &lt;enumeration value="26ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="275F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="27C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="27EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="28089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="28E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="29089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="29C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="29E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2ADE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2B089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="2BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="2CC09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="2D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="2D089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="2DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="2E089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="2EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="2FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="2FEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="305B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="30BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="30C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="30E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="31C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="32659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="32C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="33C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="345A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="346B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="34C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="34E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="34EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="35079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="35C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="36599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="36C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="37C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="38C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="38E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="39C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3A089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="3AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3AE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3B089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="3BBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="3BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3CEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="3FED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="40C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="40E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="41C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="42089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="42DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="43C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="44079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="44C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="45089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="456D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="46C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="47079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="47089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="48C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="49C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="49E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="4BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="4C079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="4C089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="4CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="4D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="4DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="4E089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="4F079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="4F579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="4FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="50079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="50C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="50EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="51079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="515E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="51DA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="51E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="52C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="53079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="54089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="54C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="55079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="56C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="56E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="57079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="57C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="58079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="58089AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="59C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="5A6E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="5AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="5B079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="5B569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="5C079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="5C5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="5DE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5DE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5E079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="60079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="60C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="615F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="61609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="61C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="63C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="64079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="64C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="65E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="66079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="66609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="66C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="66EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="67C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="68579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="68C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="68EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="69C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="69E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6A079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6AC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="6B079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="6C079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="6CE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6CE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6E079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="6EE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6F079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="6FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="70079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="70C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="71079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="71C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="72079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="725D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="72C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="73079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="73C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="74079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="74C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="75079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="75C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="75E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="76079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="76C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="76E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="77079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="78079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="78E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="79079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="79C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="7A079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="7B079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="7BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="7C079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="7CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="7D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="7DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="7DEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7E079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="7EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="7EE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7FC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="80079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="80C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="81079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="81579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="82C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="82E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="83C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="84C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="85079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="85E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="86079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="86C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="87079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="87E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="88079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="885D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="89079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="89C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="8A079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="8AE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8AE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8AE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8BBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="8BC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="8CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="8CE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="8DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="8DE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8EC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="8F079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="90C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="91079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="91C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="93079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="94079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="95C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="96079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="96C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="96E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="97079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="98079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="985C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="98C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="99079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9A579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9A589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9C0A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9C6A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9CC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="9D079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9D0A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9DC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="9E079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9E0A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9E659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9E6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9EBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="9EE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9EE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9F0A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="9FE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9FEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A0079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A00A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A1079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A10A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A1C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="A20A9AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A2C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="A3079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A4079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A4E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A5079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A6079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A6E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A7079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A8079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A8C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="A8EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A9079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="A9C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="AAC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="AAE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="AB079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="AC079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="ACBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="AD079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="AE079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="AEC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="AF079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="AFC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="AFF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="B0079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B1079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B1BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B1C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="B25E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B3079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B3E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B4E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B6079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B65D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B6C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="B7C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="B8079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B8BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B8C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="B8E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B8E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B9079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="B9C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="B9EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BA079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="BAC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="BB079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="BBC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="BBE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BC079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="BD079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="BDC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="BDE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BEEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BEEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BFC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="C0BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C1559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C2C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="C2E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C3C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="C5BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C6079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="C6679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="C6C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="C7BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C7C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="C7E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C9079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="C9EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CA079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="CB079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="CBE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CC579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CCC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="CD079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="CDC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="CE079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="CE559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CEC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="CEE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CFC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D0659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D0C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D1589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D1BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D1C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D2079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="D2C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D2E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D3079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="D35D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D3C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D4C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D4C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D4E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D4EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D5E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D7C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D85D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D8BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D9079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="D96C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D9C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="D9C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="DA079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="DAC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="DCBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DCC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="DE079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="DEC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="DF079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="DFDC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E0C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E15D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="E1BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E1EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E2C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E2E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E3C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E4C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E5579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="E5C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E6C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E8BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E8C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="E8E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E8EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EAE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EBC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="EC079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="ECC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="ECE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EDC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="EDE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EE079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="EE569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="EEDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EEEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EFBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="EFC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="F0C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="F0C99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="F3079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="F3E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F4079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="F6E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F7559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F7EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F85A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F8C89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="F9BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="FA079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="FC079AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="FCC89BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="FCC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="FDC99BB6-C4A0-1DD7-7532-689353716BA9"/>
 *             &lt;enumeration value="FEEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FF069AB6-8597-AA70-3724-689353716BA9"/>
 *             &lt;enumeration value="FF599BB6-7BD6-B926-896E-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="11584"/>
 *             &lt;enumeration value="11589"/>
 *             &lt;enumeration value="11599"/>
 *             &lt;enumeration value="11637"/>
 *             &lt;enumeration value="11655"/>
 *             &lt;enumeration value="11667"/>
 *             &lt;enumeration value="11672"/>
 *             &lt;enumeration value="11682"/>
 *             &lt;enumeration value="11698"/>
 *             &lt;enumeration value="11714"/>
 *             &lt;enumeration value="11722"/>
 *             &lt;enumeration value="11753"/>
 *             &lt;enumeration value="11762"/>
 *             &lt;enumeration value="11778"/>
 *             &lt;enumeration value="11794"/>
 *             &lt;enumeration value="11799"/>
 *             &lt;enumeration value="11831"/>
 *             &lt;enumeration value="11847"/>
 *             &lt;enumeration value="11869"/>
 *             &lt;enumeration value="11881"/>
 *             &lt;enumeration value="11890"/>
 *             &lt;enumeration value="11904"/>
 *             &lt;enumeration value="11917"/>
 *             &lt;enumeration value="11925"/>
 *             &lt;enumeration value="11934"/>
 *             &lt;enumeration value="11944"/>
 *             &lt;enumeration value="11956"/>
 *             &lt;enumeration value="11964"/>
 *             &lt;enumeration value="11987"/>
 *             &lt;enumeration value="11999"/>
 *             &lt;enumeration value="12024"/>
 *             &lt;enumeration value="12044"/>
 *             &lt;enumeration value="12053"/>
 *             &lt;enumeration value="12062"/>
 *             &lt;enumeration value="12070"/>
 *             &lt;enumeration value="12079"/>
 *             &lt;enumeration value="12090"/>
 *             &lt;enumeration value="12119"/>
 *             &lt;enumeration value="12129"/>
 *             &lt;enumeration value="12150"/>
 *             &lt;enumeration value="12168"/>
 *             &lt;enumeration value="12186"/>
 *             &lt;enumeration value="12195"/>
 *             &lt;enumeration value="12213"/>
 *             &lt;enumeration value="12223"/>
 *             &lt;enumeration value="12231"/>
 *             &lt;enumeration value="12240"/>
 *             &lt;enumeration value="12276"/>
 *             &lt;enumeration value="12286"/>
 *             &lt;enumeration value="12305"/>
 *             &lt;enumeration value="12324"/>
 *             &lt;enumeration value="12333"/>
 *             &lt;enumeration value="12341"/>
 *             &lt;enumeration value="12349"/>
 *             &lt;enumeration value="12357"/>
 *             &lt;enumeration value="12367"/>
 *             &lt;enumeration value="12380"/>
 *             &lt;enumeration value="12394"/>
 *             &lt;enumeration value="12403"/>
 *             &lt;enumeration value="12412"/>
 *             &lt;enumeration value="12422"/>
 *             &lt;enumeration value="12431"/>
 *             &lt;enumeration value="12440"/>
 *             &lt;enumeration value="12450"/>
 *             &lt;enumeration value="12460"/>
 *             &lt;enumeration value="12468"/>
 *             &lt;enumeration value="12478"/>
 *             &lt;enumeration value="12512"/>
 *             &lt;enumeration value="12533"/>
 *             &lt;enumeration value="12543"/>
 *             &lt;enumeration value="12552"/>
 *             &lt;enumeration value="12562"/>
 *             &lt;enumeration value="12575"/>
 *             &lt;enumeration value="12583"/>
 *             &lt;enumeration value="12611"/>
 *             &lt;enumeration value="12663"/>
 *             &lt;enumeration value="12724"/>
 *             &lt;enumeration value="12730"/>
 *             &lt;enumeration value="12744"/>
 *             &lt;enumeration value="12765"/>
 *             &lt;enumeration value="12775"/>
 *             &lt;enumeration value="12780"/>
 *             &lt;enumeration value="12790"/>
 *             &lt;enumeration value="12841"/>
 *             &lt;enumeration value="12846"/>
 *             &lt;enumeration value="12851"/>
 *             &lt;enumeration value="12864"/>
 *             &lt;enumeration value="13099"/>
 *             &lt;enumeration value="13154"/>
 *             &lt;enumeration value="13246"/>
 *             &lt;enumeration value="13348"/>
 *             &lt;enumeration value="13408"/>
 *             &lt;enumeration value="13487"/>
 *             &lt;enumeration value="13530"/>
 *             &lt;enumeration value="13538"/>
 *             &lt;enumeration value="13596"/>
 *             &lt;enumeration value="13631"/>
 *             &lt;enumeration value="13642"/>
 *             &lt;enumeration value="13696"/>
 *             &lt;enumeration value="13754"/>
 *             &lt;enumeration value="14008"/>
 *             &lt;enumeration value="14020"/>
 *             &lt;enumeration value="14032"/>
 *             &lt;enumeration value="14042"/>
 *             &lt;enumeration value="14097"/>
 *             &lt;enumeration value="14108"/>
 *             &lt;enumeration value="14166"/>
 *             &lt;enumeration value="14177"/>
 *             &lt;enumeration value="14260"/>
 *             &lt;enumeration value="14318"/>
 *             &lt;enumeration value="14347"/>
 *             &lt;enumeration value="14393"/>
 *             &lt;enumeration value="14405"/>
 *             &lt;enumeration value="14542"/>
 *             &lt;enumeration value="14591"/>
 *             &lt;enumeration value="14605"/>
 *             &lt;enumeration value="14700"/>
 *             &lt;enumeration value="14765"/>
 *             &lt;enumeration value="14775"/>
 *             &lt;enumeration value="24208"/>
 *             &lt;enumeration value="24219"/>
 *             &lt;enumeration value="24227"/>
 *             &lt;enumeration value="24239"/>
 *             &lt;enumeration value="24250"/>
 *             &lt;enumeration value="24256"/>
 *             &lt;enumeration value="24266"/>
 *             &lt;enumeration value="24274"/>
 *             &lt;enumeration value="24287"/>
 *             &lt;enumeration value="24292"/>
 *             &lt;enumeration value="24315"/>
 *             &lt;enumeration value="24346"/>
 *             &lt;enumeration value="24362"/>
 *             &lt;enumeration value="24417"/>
 *             &lt;enumeration value="24430"/>
 *             &lt;enumeration value="24444"/>
 *             &lt;enumeration value="24489"/>
 *             &lt;enumeration value="24496"/>
 *             &lt;enumeration value="24504"/>
 *             &lt;enumeration value="24511"/>
 *             &lt;enumeration value="24527"/>
 *             &lt;enumeration value="24543"/>
 *             &lt;enumeration value="24562"/>
 *             &lt;enumeration value="24568"/>
 *             &lt;enumeration value="25772"/>
 *             &lt;enumeration value="28466"/>
 *             &lt;enumeration value="28994"/>
 *             &lt;enumeration value="29255"/>
 *             &lt;enumeration value="29296"/>
 *             &lt;enumeration value="29366"/>
 *             &lt;enumeration value="29521"/>
 *             &lt;enumeration value="29540"/>
 *             &lt;enumeration value="29545"/>
 *             &lt;enumeration value="29565"/>
 *             &lt;enumeration value="29570"/>
 *             &lt;enumeration value="29576"/>
 *             &lt;enumeration value="29589"/>
 *             &lt;enumeration value="29594"/>
 *             &lt;enumeration value="29599"/>
 *             &lt;enumeration value="29604"/>
 *             &lt;enumeration value="29609"/>
 *             &lt;enumeration value="29614"/>
 *             &lt;enumeration value="29621"/>
 *             &lt;enumeration value="29626"/>
 *             &lt;enumeration value="29631"/>
 *             &lt;enumeration value="29636"/>
 *             &lt;enumeration value="29645"/>
 *             &lt;enumeration value="33401"/>
 *             &lt;enumeration value="68259"/>
 *             &lt;enumeration value="68260"/>
 *             &lt;enumeration value="68261"/>
 *             &lt;enumeration value="68262"/>
 *             &lt;enumeration value="68263"/>
 *             &lt;enumeration value="68264"/>
 *             &lt;enumeration value="68265"/>
 *             &lt;enumeration value="68266"/>
 *             &lt;enumeration value="68267"/>
 *             &lt;enumeration value="68268"/>
 *             &lt;enumeration value="68269"/>
 *             &lt;enumeration value="68270"/>
 *             &lt;enumeration value="68271"/>
 *             &lt;enumeration value="68272"/>
 *             &lt;enumeration value="68273"/>
 *             &lt;enumeration value="68274"/>
 *             &lt;enumeration value="68275"/>
 *             &lt;enumeration value="68276"/>
 *             &lt;enumeration value="68277"/>
 *             &lt;enumeration value="68278"/>
 *             &lt;enumeration value="68279"/>
 *             &lt;enumeration value="68280"/>
 *             &lt;enumeration value="68281"/>
 *             &lt;enumeration value="68282"/>
 *             &lt;enumeration value="68283"/>
 *             &lt;enumeration value="68284"/>
 *             &lt;enumeration value="68285"/>
 *             &lt;enumeration value="68286"/>
 *             &lt;enumeration value="68287"/>
 *             &lt;enumeration value="68288"/>
 *             &lt;enumeration value="68289"/>
 *             &lt;enumeration value="68290"/>
 *             &lt;enumeration value="68291"/>
 *             &lt;enumeration value="68293"/>
 *             &lt;enumeration value="68294"/>
 *             &lt;enumeration value="68295"/>
 *             &lt;enumeration value="68296"/>
 *             &lt;enumeration value="68297"/>
 *             &lt;enumeration value="68298"/>
 *             &lt;enumeration value="68299"/>
 *             &lt;enumeration value="68300"/>
 *             &lt;enumeration value="68301"/>
 *             &lt;enumeration value="68302"/>
 *             &lt;enumeration value="68304"/>
 *             &lt;enumeration value="68305"/>
 *             &lt;enumeration value="68306"/>
 *             &lt;enumeration value="68307"/>
 *             &lt;enumeration value="68308"/>
 *             &lt;enumeration value="68309"/>
 *             &lt;enumeration value="68310"/>
 *             &lt;enumeration value="68311"/>
 *             &lt;enumeration value="68312"/>
 *             &lt;enumeration value="68313"/>
 *             &lt;enumeration value="68314"/>
 *             &lt;enumeration value="68315"/>
 *             &lt;enumeration value="68316"/>
 *             &lt;enumeration value="68317"/>
 *             &lt;enumeration value="68318"/>
 *             &lt;enumeration value="68319"/>
 *             &lt;enumeration value="68320"/>
 *             &lt;enumeration value="68321"/>
 *             &lt;enumeration value="68322"/>
 *             &lt;enumeration value="68323"/>
 *             &lt;enumeration value="68324"/>
 *             &lt;enumeration value="68325"/>
 *             &lt;enumeration value="68326"/>
 *             &lt;enumeration value="68327"/>
 *             &lt;enumeration value="68328"/>
 *             &lt;enumeration value="68329"/>
 *             &lt;enumeration value="68330"/>
 *             &lt;enumeration value="68331"/>
 *             &lt;enumeration value="68332"/>
 *             &lt;enumeration value="68333"/>
 *             &lt;enumeration value="68334"/>
 *             &lt;enumeration value="68335"/>
 *             &lt;enumeration value="68336"/>
 *             &lt;enumeration value="68337"/>
 *             &lt;enumeration value="68338"/>
 *             &lt;enumeration value="68339"/>
 *             &lt;enumeration value="68340"/>
 *             &lt;enumeration value="68341"/>
 *             &lt;enumeration value="68342"/>
 *             &lt;enumeration value="68343"/>
 *             &lt;enumeration value="68344"/>
 *             &lt;enumeration value="68345"/>
 *             &lt;enumeration value="68346"/>
 *             &lt;enumeration value="68347"/>
 *             &lt;enumeration value="68348"/>
 *             &lt;enumeration value="68349"/>
 *             &lt;enumeration value="68350"/>
 *             &lt;enumeration value="68351"/>
 *             &lt;enumeration value="68352"/>
 *             &lt;enumeration value="68353"/>
 *             &lt;enumeration value="68354"/>
 *             &lt;enumeration value="68355"/>
 *             &lt;enumeration value="68356"/>
 *             &lt;enumeration value="68357"/>
 *             &lt;enumeration value="68358"/>
 *             &lt;enumeration value="68359"/>
 *             &lt;enumeration value="68360"/>
 *             &lt;enumeration value="68361"/>
 *             &lt;enumeration value="68362"/>
 *             &lt;enumeration value="68363"/>
 *             &lt;enumeration value="68364"/>
 *             &lt;enumeration value="68365"/>
 *             &lt;enumeration value="68367"/>
 *             &lt;enumeration value="68368"/>
 *             &lt;enumeration value="68369"/>
 *             &lt;enumeration value="68370"/>
 *             &lt;enumeration value="68371"/>
 *             &lt;enumeration value="68372"/>
 *             &lt;enumeration value="68373"/>
 *             &lt;enumeration value="68374"/>
 *             &lt;enumeration value="68375"/>
 *             &lt;enumeration value="68376"/>
 *             &lt;enumeration value="68377"/>
 *             &lt;enumeration value="68378"/>
 *             &lt;enumeration value="68379"/>
 *             &lt;enumeration value="68380"/>
 *             &lt;enumeration value="68381"/>
 *             &lt;enumeration value="68382"/>
 *             &lt;enumeration value="68383"/>
 *             &lt;enumeration value="68384"/>
 *             &lt;enumeration value="68385"/>
 *             &lt;enumeration value="68386"/>
 *             &lt;enumeration value="68387"/>
 *             &lt;enumeration value="68388"/>
 *             &lt;enumeration value="68389"/>
 *             &lt;enumeration value="68390"/>
 *             &lt;enumeration value="68391"/>
 *             &lt;enumeration value="68392"/>
 *             &lt;enumeration value="68393"/>
 *             &lt;enumeration value="68394"/>
 *             &lt;enumeration value="68395"/>
 *             &lt;enumeration value="68396"/>
 *             &lt;enumeration value="68397"/>
 *             &lt;enumeration value="68398"/>
 *             &lt;enumeration value="68399"/>
 *             &lt;enumeration value="68400"/>
 *             &lt;enumeration value="68401"/>
 *             &lt;enumeration value="68402"/>
 *             &lt;enumeration value="68403"/>
 *             &lt;enumeration value="68404"/>
 *             &lt;enumeration value="68405"/>
 *             &lt;enumeration value="68406"/>
 *             &lt;enumeration value="68407"/>
 *             &lt;enumeration value="68408"/>
 *             &lt;enumeration value="68411"/>
 *             &lt;enumeration value="68412"/>
 *             &lt;enumeration value="68414"/>
 *             &lt;enumeration value="68415"/>
 *             &lt;enumeration value="68416"/>
 *             &lt;enumeration value="68417"/>
 *             &lt;enumeration value="68419"/>
 *             &lt;enumeration value="68420"/>
 *             &lt;enumeration value="68422"/>
 *             &lt;enumeration value="68423"/>
 *             &lt;enumeration value="68424"/>
 *             &lt;enumeration value="68425"/>
 *             &lt;enumeration value="68426"/>
 *             &lt;enumeration value="68427"/>
 *             &lt;enumeration value="68429"/>
 *             &lt;enumeration value="68430"/>
 *             &lt;enumeration value="68431"/>
 *             &lt;enumeration value="68432"/>
 *             &lt;enumeration value="68433"/>
 *             &lt;enumeration value="68434"/>
 *             &lt;enumeration value="68435"/>
 *             &lt;enumeration value="68436"/>
 *             &lt;enumeration value="68437"/>
 *             &lt;enumeration value="68438"/>
 *             &lt;enumeration value="68439"/>
 *             &lt;enumeration value="68440"/>
 *             &lt;enumeration value="68441"/>
 *             &lt;enumeration value="68443"/>
 *             &lt;enumeration value="68444"/>
 *             &lt;enumeration value="68445"/>
 *             &lt;enumeration value="68446"/>
 *             &lt;enumeration value="68447"/>
 *             &lt;enumeration value="68448"/>
 *             &lt;enumeration value="68449"/>
 *             &lt;enumeration value="68450"/>
 *             &lt;enumeration value="68451"/>
 *             &lt;enumeration value="68452"/>
 *             &lt;enumeration value="68453"/>
 *             &lt;enumeration value="68454"/>
 *             &lt;enumeration value="68455"/>
 *             &lt;enumeration value="68457"/>
 *             &lt;enumeration value="68458"/>
 *             &lt;enumeration value="68460"/>
 *             &lt;enumeration value="68461"/>
 *             &lt;enumeration value="68462"/>
 *             &lt;enumeration value="68463"/>
 *             &lt;enumeration value="68464"/>
 *             &lt;enumeration value="68465"/>
 *             &lt;enumeration value="68466"/>
 *             &lt;enumeration value="68467"/>
 *             &lt;enumeration value="68468"/>
 *             &lt;enumeration value="68469"/>
 *             &lt;enumeration value="68470"/>
 *             &lt;enumeration value="68471"/>
 *             &lt;enumeration value="68472"/>
 *             &lt;enumeration value="68473"/>
 *             &lt;enumeration value="68474"/>
 *             &lt;enumeration value="68475"/>
 *             &lt;enumeration value="68476"/>
 *             &lt;enumeration value="68477"/>
 *             &lt;enumeration value="68478"/>
 *             &lt;enumeration value="68479"/>
 *             &lt;enumeration value="68480"/>
 *             &lt;enumeration value="68481"/>
 *             &lt;enumeration value="68482"/>
 *             &lt;enumeration value="68483"/>
 *             &lt;enumeration value="68484"/>
 *             &lt;enumeration value="68486"/>
 *             &lt;enumeration value="68487"/>
 *             &lt;enumeration value="68489"/>
 *             &lt;enumeration value="68490"/>
 *             &lt;enumeration value="68492"/>
 *             &lt;enumeration value="68493"/>
 *             &lt;enumeration value="68494"/>
 *             &lt;enumeration value="68495"/>
 *             &lt;enumeration value="68496"/>
 *             &lt;enumeration value="68497"/>
 *             &lt;enumeration value="68499"/>
 *             &lt;enumeration value="68500"/>
 *             &lt;enumeration value="68501"/>
 *             &lt;enumeration value="68502"/>
 *             &lt;enumeration value="68503"/>
 *             &lt;enumeration value="68504"/>
 *             &lt;enumeration value="68506"/>
 *             &lt;enumeration value="68507"/>
 *             &lt;enumeration value="68509"/>
 *             &lt;enumeration value="68510"/>
 *             &lt;enumeration value="68511"/>
 *             &lt;enumeration value="68512"/>
 *             &lt;enumeration value="68513"/>
 *             &lt;enumeration value="68514"/>
 *             &lt;enumeration value="68515"/>
 *             &lt;enumeration value="68516"/>
 *             &lt;enumeration value="68517"/>
 *             &lt;enumeration value="68518"/>
 *             &lt;enumeration value="68519"/>
 *             &lt;enumeration value="68520"/>
 *             &lt;enumeration value="68521"/>
 *             &lt;enumeration value="68522"/>
 *             &lt;enumeration value="68523"/>
 *             &lt;enumeration value="68524"/>
 *             &lt;enumeration value="68525"/>
 *             &lt;enumeration value="68526"/>
 *             &lt;enumeration value="68527"/>
 *             &lt;enumeration value="68529"/>
 *             &lt;enumeration value="68530"/>
 *             &lt;enumeration value="68531"/>
 *             &lt;enumeration value="68532"/>
 *             &lt;enumeration value="68533"/>
 *             &lt;enumeration value="68534"/>
 *             &lt;enumeration value="68535"/>
 *             &lt;enumeration value="68536"/>
 *             &lt;enumeration value="68537"/>
 *             &lt;enumeration value="68539"/>
 *             &lt;enumeration value="68540"/>
 *             &lt;enumeration value="68541"/>
 *             &lt;enumeration value="68542"/>
 *             &lt;enumeration value="68543"/>
 *             &lt;enumeration value="68544"/>
 *             &lt;enumeration value="68545"/>
 *             &lt;enumeration value="68547"/>
 *             &lt;enumeration value="68548"/>
 *             &lt;enumeration value="68549"/>
 *             &lt;enumeration value="68551"/>
 *             &lt;enumeration value="68552"/>
 *             &lt;enumeration value="68553"/>
 *             &lt;enumeration value="68554"/>
 *             &lt;enumeration value="68555"/>
 *             &lt;enumeration value="68556"/>
 *             &lt;enumeration value="68557"/>
 *             &lt;enumeration value="68558"/>
 *             &lt;enumeration value="68559"/>
 *             &lt;enumeration value="68560"/>
 *             &lt;enumeration value="68561"/>
 *             &lt;enumeration value="68562"/>
 *             &lt;enumeration value="68563"/>
 *             &lt;enumeration value="68564"/>
 *             &lt;enumeration value="68565"/>
 *             &lt;enumeration value="68566"/>
 *             &lt;enumeration value="68567"/>
 *             &lt;enumeration value="68568"/>
 *             &lt;enumeration value="68569"/>
 *             &lt;enumeration value="68570"/>
 *             &lt;enumeration value="68571"/>
 *             &lt;enumeration value="68572"/>
 *             &lt;enumeration value="68573"/>
 *             &lt;enumeration value="68574"/>
 *             &lt;enumeration value="68575"/>
 *             &lt;enumeration value="68576"/>
 *             &lt;enumeration value="68577"/>
 *             &lt;enumeration value="68578"/>
 *             &lt;enumeration value="68579"/>
 *             &lt;enumeration value="68580"/>
 *             &lt;enumeration value="68581"/>
 *             &lt;enumeration value="68582"/>
 *             &lt;enumeration value="68583"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "tag_assign")
public class TagAssign {

  @XmlAttribute(name = "tag_id", required = true)
  protected String tagId;
  @XmlAttribute(name = "is_auto_assign")
  protected Boolean isAutoAssign;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected int signature;

  /**
   * Ruft den Wert der tagId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTagId() {
    return tagId;
  }

  /**
   * Legt den Wert der tagId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTagId(String value) {
    this.tagId = value;
  }

  /**
   * Ruft den Wert der isAutoAssign-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Boolean }
   */
  public Boolean isIsAutoAssign() {
    return isAutoAssign;
  }

  /**
   * Legt den Wert der isAutoAssign-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Boolean }
   */
  public void setIsAutoAssign(Boolean value) {
    this.isAutoAssign = value;
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
