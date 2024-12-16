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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}span_info"/>
 *       &lt;/sequence>
 *       &lt;attribute name="target_original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="04589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="04E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="07DB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0A279AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="0CDE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="16DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18AB9BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="20DB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="285C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="35589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="382E9BB6-07AD-EFDA-766D-689353716BA9"/>
 *             &lt;enumeration value="3AE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3F569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="3FBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="41DF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4B489BB6-B497-97CB-006E-689353716BA9"/>
 *             &lt;enumeration value="55E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="58DA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5E5B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="62659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="64599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="662F9BB6-07AD-EFDA-766D-689353716BA9"/>
 *             &lt;enumeration value="66589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="6BDB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6C329BB6-07AD-EFDA-766D-689353716BA9"/>
 *             &lt;enumeration value="6DE09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6DF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="6EE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="71BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="71DA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="775D9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="8B619AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="8F269AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="8F5B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="935A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="95599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="9DDB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A3E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4F79BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="AF269AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="B1579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B7F59BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="B95D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B9689AB6-2F8B-AF47-C003-689353716BA9"/>
 *             &lt;enumeration value="BA2E9AB6-711A-25BE-6200-689353716BA9"/>
 *             &lt;enumeration value="C05B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C2699BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="C6599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C75D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CBF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="CC5D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D2969BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="D3259AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="D3F39BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="D5DA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D7F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="E0689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="E6F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="EEDA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F15B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F8259AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="F8F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="FF589BB6-7BD6-B926-896E-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Alias"/>
 *             &lt;enumeration value="Arbitrary"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="06609AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="07609AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="08E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="08F59BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="09ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="09F59BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="0AF59BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="0B229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="0BF59BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="10EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="15EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1EE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1FE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1FED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="20E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="23199AB6-144A-622D-E81E-689353716BA9"/>
 *             &lt;enumeration value="23ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="24199AB6-144A-622D-E81E-689353716BA9"/>
 *             &lt;enumeration value="255C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="265C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="275C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2BEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="30619BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="30DD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="32599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="38619AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="38ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3CED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3F6D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="406D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="40DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="41E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="42E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="44DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="53E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="54E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="585F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="595F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="60659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="61A29BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="62EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="636B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="63EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="64DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6BDC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6CDC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="70689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="70E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="786D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="7AEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7AEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8D5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="8E5C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="90EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="92A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="935F9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="9A019AB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="9B5F9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="9B609AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="9C019AB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="9CDC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9CE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A2DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A35F9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="B1619AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="B1EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BBBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="BBEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BDE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C1229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="C6E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C7F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="C8F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="CC659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CCBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CD6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CEBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="CEE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D0EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D2609AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="D3FF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="D4FF9BB6-6467-7730-0B1E-689353716BA9"/>
 *             &lt;enumeration value="D6219AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="D9E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DADC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DAE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DBEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DDEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DEEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E5BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E7E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E9009BB6-CD1A-951A-3D30-689353716BA9"/>
 *             &lt;enumeration value="EB5F9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="EBBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="ECBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="ED229AB6-82B4-1E0B-451F-689353716BA9"/>
 *             &lt;enumeration value="ED5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F2BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F3BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F3EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F4BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F4EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F5679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="F9599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FA599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FAE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FAEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FB599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FBE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FCE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FCE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}short">
 *             &lt;enumeration value="11597"/>
 *             &lt;enumeration value="11866"/>
 *             &lt;enumeration value="11867"/>
 *             &lt;enumeration value="11876"/>
 *             &lt;enumeration value="11877"/>
 *             &lt;enumeration value="11878"/>
 *             &lt;enumeration value="11879"/>
 *             &lt;enumeration value="11901"/>
 *             &lt;enumeration value="11902"/>
 *             &lt;enumeration value="11912"/>
 *             &lt;enumeration value="11932"/>
 *             &lt;enumeration value="11951"/>
 *             &lt;enumeration value="11983"/>
 *             &lt;enumeration value="11994"/>
 *             &lt;enumeration value="11995"/>
 *             &lt;enumeration value="12042"/>
 *             &lt;enumeration value="12060"/>
 *             &lt;enumeration value="12077"/>
 *             &lt;enumeration value="12086"/>
 *             &lt;enumeration value="12087"/>
 *             &lt;enumeration value="12148"/>
 *             &lt;enumeration value="12193"/>
 *             &lt;enumeration value="12211"/>
 *             &lt;enumeration value="12220"/>
 *             &lt;enumeration value="12274"/>
 *             &lt;enumeration value="12284"/>
 *             &lt;enumeration value="12303"/>
 *             &lt;enumeration value="12321"/>
 *             &lt;enumeration value="12331"/>
 *             &lt;enumeration value="12364"/>
 *             &lt;enumeration value="12365"/>
 *             &lt;enumeration value="12374"/>
 *             &lt;enumeration value="12401"/>
 *             &lt;enumeration value="12447"/>
 *             &lt;enumeration value="12457"/>
 *             &lt;enumeration value="12510"/>
 *             &lt;enumeration value="12528"/>
 *             &lt;enumeration value="12530"/>
 *             &lt;enumeration value="12531"/>
 *             &lt;enumeration value="12540"/>
 *             &lt;enumeration value="12541"/>
 *             &lt;enumeration value="12550"/>
 *             &lt;enumeration value="12559"/>
 *             &lt;enumeration value="12560"/>
 *             &lt;enumeration value="12569"/>
 *             &lt;enumeration value="12570"/>
 *             &lt;enumeration value="12573"/>
 *             &lt;enumeration value="12757"/>
 *             &lt;enumeration value="12760"/>
 *             &lt;enumeration value="12761"/>
 *             &lt;enumeration value="12762"/>
 *             &lt;enumeration value="12785"/>
 *             &lt;enumeration value="12788"/>
 *             &lt;enumeration value="12837"/>
 *             &lt;enumeration value="12838"/>
 *             &lt;enumeration value="12839"/>
 *             &lt;enumeration value="12856"/>
 *             &lt;enumeration value="12860"/>
 *             &lt;enumeration value="12861"/>
 *             &lt;enumeration value="13344"/>
 *             &lt;enumeration value="13536"/>
 *             &lt;enumeration value="13626"/>
 *             &lt;enumeration value="13637"/>
 *             &lt;enumeration value="14001"/>
 *             &lt;enumeration value="14002"/>
 *             &lt;enumeration value="14003"/>
 *             &lt;enumeration value="14014"/>
 *             &lt;enumeration value="14015"/>
 *             &lt;enumeration value="14025"/>
 *             &lt;enumeration value="14027"/>
 *             &lt;enumeration value="14037"/>
 *             &lt;enumeration value="14103"/>
 *             &lt;enumeration value="14172"/>
 *             &lt;enumeration value="14312"/>
 *             &lt;enumeration value="14313"/>
 *             &lt;enumeration value="14341"/>
 *             &lt;enumeration value="14399"/>
 *             &lt;enumeration value="14695"/>
 *             &lt;enumeration value="24199"/>
 *             &lt;enumeration value="24200"/>
 *             &lt;enumeration value="24213"/>
 *             &lt;enumeration value="24217"/>
 *             &lt;enumeration value="24234"/>
 *             &lt;enumeration value="24236"/>
 *             &lt;enumeration value="24237"/>
 *             &lt;enumeration value="24246"/>
 *             &lt;enumeration value="24262"/>
 *             &lt;enumeration value="24263"/>
 *             &lt;enumeration value="24264"/>
 *             &lt;enumeration value="24271"/>
 *             &lt;enumeration value="24272"/>
 *             &lt;enumeration value="24279"/>
 *             &lt;enumeration value="24280"/>
 *             &lt;enumeration value="24281"/>
 *             &lt;enumeration value="24282"/>
 *             &lt;enumeration value="24283"/>
 *             &lt;enumeration value="24284"/>
 *             &lt;enumeration value="24285"/>
 *             &lt;enumeration value="24344"/>
 *             &lt;enumeration value="28989"/>
 *             &lt;enumeration value="28990"/>
 *             &lt;enumeration value="28992"/>
 *             &lt;enumeration value="29253"/>
 *             &lt;enumeration value="29286"/>
 *             &lt;enumeration value="29355"/>
 *             &lt;enumeration value="29361"/>
 *             &lt;enumeration value="29364"/>
 *             &lt;enumeration value="29529"/>
 *             &lt;enumeration value="29530"/>
 *             &lt;enumeration value="29532"/>
 *             &lt;enumeration value="29535"/>
 *             &lt;enumeration value="29536"/>
 *             &lt;enumeration value="29537"/>
 *             &lt;enumeration value="29538"/>
 *             &lt;enumeration value="29642"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="alias_original_uuid">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="03979BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="39A19BB6-01D2-071C-E515-689353716BA9"/>
 *             &lt;enumeration value="52E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6A489BB6-B497-97CB-006E-689353716BA9"/>
 *             &lt;enumeration value="745E9AB6-907F-DD5C-4703-689353716BA9"/>
 *             &lt;enumeration value="98F49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="F1DC9BB6-3AA8-1A22-F71C-689353716BA9"/>
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
    "spanInfo"
})
@XmlRootElement(name = "dlink")
public class Dlink {

  @XmlElement(name = "span_info", required = true)
  protected SpanInfo spanInfo;
  @XmlAttribute(name = "target_original_uuid", required = true)
  protected String targetOriginalUuid;
  @XmlAttribute(name = "type")
  protected String type;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected short signature;
  @XmlAttribute(name = "alias_original_uuid")
  protected String aliasOriginalUuid;

  /**
   * Ruft den Wert der spanInfo-Eigenschaft ab.
   *
   * @return possible object is
   * {@link SpanInfo }
   */
  public SpanInfo getSpanInfo() {
    return spanInfo;
  }

  /**
   * Legt den Wert der spanInfo-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link SpanInfo }
   */
  public void setSpanInfo(SpanInfo value) {
    this.spanInfo = value;
  }

  /**
   * Ruft den Wert der targetOriginalUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTargetOriginalUuid() {
    return targetOriginalUuid;
  }

  /**
   * Legt den Wert der targetOriginalUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTargetOriginalUuid(String value) {
    this.targetOriginalUuid = value;
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
  public short getSignature() {
    return signature;
  }

  /**
   * Legt den Wert der signature-Eigenschaft fest.
   */
  public void setSignature(short value) {
    this.signature = value;
  }

  /**
   * Ruft den Wert der aliasOriginalUuid-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getAliasOriginalUuid() {
    return aliasOriginalUuid;
  }

  /**
   * Legt den Wert der aliasOriginalUuid-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setAliasOriginalUuid(String value) {
    this.aliasOriginalUuid = value;
  }

}
