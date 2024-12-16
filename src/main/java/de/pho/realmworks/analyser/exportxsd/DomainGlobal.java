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
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *           &lt;sequence minOccurs="0">
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *             &lt;choice>
 *               &lt;sequence>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                     &lt;choice>
 *                       &lt;sequence>
 *                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                         &lt;choice>
 *                           &lt;sequence>
 *                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                             &lt;choice>
 *                               &lt;sequence>
 *                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                 &lt;choice minOccurs="0">
 *                                   &lt;sequence>
 *                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                     &lt;choice>
 *                                       &lt;sequence>
 *                                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                         &lt;choice>
 *                                           &lt;sequence>
 *                                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                             &lt;choice>
 *                                               &lt;sequence>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                 &lt;choice minOccurs="0">
 *                                                   &lt;sequence>
 *                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                     &lt;choice>
 *                                                       &lt;sequence>
 *                                                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                         &lt;choice>
 *                                                           &lt;sequence>
 *                                                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                             &lt;choice>
 *                                                               &lt;sequence>
 *                                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                 &lt;sequence minOccurs="0">
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag"/>
 *                                                                   &lt;sequence minOccurs="0">
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global"/>
 *                                                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                                   &lt;/sequence>
 *                                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                                                 &lt;/sequence>
 *                                                               &lt;/sequence>
 *                                                               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                                             &lt;/choice>
 *                                                           &lt;/sequence>
 *                                                           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                                         &lt;/choice>
 *                                                       &lt;/sequence>
 *                                                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                                     &lt;/choice>
 *                                                   &lt;/sequence>
 *                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                                 &lt;/choice>
 *                                               &lt;/sequence>
 *                                               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                             &lt;/choice>
 *                                           &lt;/sequence>
 *                                           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                         &lt;/choice>
 *                                       &lt;/sequence>
 *                                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                     &lt;/choice>
 *                                   &lt;/sequence>
 *                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                 &lt;/choice>
 *                               &lt;/sequence>
 *                               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                             &lt;/choice>
 *                           &lt;/sequence>
 *                           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                         &lt;/choice>
 *                       &lt;/sequence>
 *                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                     &lt;/choice>
 *                   &lt;/sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                 &lt;/choice>
 *               &lt;/sequence>
 *               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *             &lt;/choice>
 *           &lt;/sequence>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *           &lt;sequence minOccurs="0">
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *             &lt;choice>
 *               &lt;sequence>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}overlay"/>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" minOccurs="0"/>
 *               &lt;/sequence>
 *               &lt;sequence>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                     &lt;choice>
 *                       &lt;sequence>
 *                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                         &lt;choice>
 *                           &lt;sequence>
 *                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                             &lt;choice minOccurs="0">
 *                               &lt;sequence>
 *                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                 &lt;choice>
 *                                   &lt;sequence>
 *                                     &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                     &lt;choice>
 *                                       &lt;sequence>
 *                                         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                         &lt;choice>
 *                                           &lt;sequence>
 *                                             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag"/>
 *                                             &lt;choice minOccurs="0">
 *                                               &lt;sequence>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag" maxOccurs="unbounded"/>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                 &lt;sequence minOccurs="0">
 *                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag"/>
 *                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_global" maxOccurs="unbounded"/>
 *                                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag"/>
 *                                                 &lt;/sequence>
 *                                                 &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                               &lt;/sequence>
 *                                               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                             &lt;/choice>
 *                                           &lt;/sequence>
 *                                           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                         &lt;/choice>
 *                                       &lt;/sequence>
 *                                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                     &lt;/choice>
 *                                   &lt;/sequence>
 *                                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                                 &lt;/choice>
 *                               &lt;/sequence>
 *                               &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                             &lt;/choice>
 *                           &lt;/sequence>
 *                           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                         &lt;/choice>
 *                       &lt;/sequence>
 *                       &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                     &lt;/choice>
 *                   &lt;/sequence>
 *                   &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override"/>
 *                 &lt;/choice>
 *               &lt;/sequence>
 *             &lt;/choice>
 *           &lt;/sequence>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="domain_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Domain_1"/>
 *             &lt;enumeration value="Domain_10"/>
 *             &lt;enumeration value="Domain_11"/>
 *             &lt;enumeration value="Domain_12"/>
 *             &lt;enumeration value="Domain_13"/>
 *             &lt;enumeration value="Domain_14"/>
 *             &lt;enumeration value="Domain_15"/>
 *             &lt;enumeration value="Domain_16"/>
 *             &lt;enumeration value="Domain_17"/>
 *             &lt;enumeration value="Domain_18"/>
 *             &lt;enumeration value="Domain_19"/>
 *             &lt;enumeration value="Domain_2"/>
 *             &lt;enumeration value="Domain_20"/>
 *             &lt;enumeration value="Domain_21"/>
 *             &lt;enumeration value="Domain_22"/>
 *             &lt;enumeration value="Domain_23"/>
 *             &lt;enumeration value="Domain_24"/>
 *             &lt;enumeration value="Domain_25"/>
 *             &lt;enumeration value="Domain_26"/>
 *             &lt;enumeration value="Domain_27"/>
 *             &lt;enumeration value="Domain_28"/>
 *             &lt;enumeration value="Domain_29"/>
 *             &lt;enumeration value="Domain_3"/>
 *             &lt;enumeration value="Domain_30"/>
 *             &lt;enumeration value="Domain_31"/>
 *             &lt;enumeration value="Domain_32"/>
 *             &lt;enumeration value="Domain_33"/>
 *             &lt;enumeration value="Domain_34"/>
 *             &lt;enumeration value="Domain_35"/>
 *             &lt;enumeration value="Domain_36"/>
 *             &lt;enumeration value="Domain_37"/>
 *             &lt;enumeration value="Domain_38"/>
 *             &lt;enumeration value="Domain_39"/>
 *             &lt;enumeration value="Domain_4"/>
 *             &lt;enumeration value="Domain_40"/>
 *             &lt;enumeration value="Domain_41"/>
 *             &lt;enumeration value="Domain_42"/>
 *             &lt;enumeration value="Domain_43"/>
 *             &lt;enumeration value="Domain_44"/>
 *             &lt;enumeration value="Domain_45"/>
 *             &lt;enumeration value="Domain_46"/>
 *             &lt;enumeration value="Domain_47"/>
 *             &lt;enumeration value="Domain_48"/>
 *             &lt;enumeration value="Domain_49"/>
 *             &lt;enumeration value="Domain_5"/>
 *             &lt;enumeration value="Domain_50"/>
 *             &lt;enumeration value="Domain_51"/>
 *             &lt;enumeration value="Domain_52"/>
 *             &lt;enumeration value="Domain_53"/>
 *             &lt;enumeration value="Domain_54"/>
 *             &lt;enumeration value="Domain_55"/>
 *             &lt;enumeration value="Domain_56"/>
 *             &lt;enumeration value="Domain_57"/>
 *             &lt;enumeration value="Domain_6"/>
 *             &lt;enumeration value="Domain_69"/>
 *             &lt;enumeration value="Domain_7"/>
 *             &lt;enumeration value="Domain_8"/>
 *             &lt;enumeration value="Domain_9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Abenteuergebiet"/>
 *             &lt;enumeration value="Aktivit�t (Familie)"/>
 *             &lt;enumeration value="Alter"/>
 *             &lt;enumeration value="Ausr�stungsart"/>
 *             &lt;enumeration value="Aussehen"/>
 *             &lt;enumeration value="Begegnungsart"/>
 *             &lt;enumeration value="Celestial Region"/>
 *             &lt;enumeration value="Charakter Rolle"/>
 *             &lt;enumeration value="Charakter Zust�nde"/>
 *             &lt;enumeration value="Eigenschaft (K�rper)"/>
 *             &lt;enumeration value="Eigenschaft (Mental)"/>
 *             &lt;enumeration value="Eigenschaft (Pers�nlichkeit)"/>
 *             &lt;enumeration value="Eigenschaft (Sozial)"/>
 *             &lt;enumeration value="Eigenschaft (Status)"/>
 *             &lt;enumeration value="Ethische Identit�t"/>
 *             &lt;enumeration value="Expertise"/>
 *             &lt;enumeration value="Generische Beziehungsart"/>
 *             &lt;enumeration value="Genre"/>
 *             &lt;enumeration value="Geschlecht"/>
 *             &lt;enumeration value="Gr��e (gesellschaftlich)"/>
 *             &lt;enumeration value="G�ter Art"/>
 *             &lt;enumeration value="Import"/>
 *             &lt;enumeration value="Klasse/Rolle"/>
 *             &lt;enumeration value="Klima"/>
 *             &lt;enumeration value="Kommerzielle Aktivit�t"/>
 *             &lt;enumeration value="Kriminelle Aktivit�t"/>
 *             &lt;enumeration value="Milit�rische Rolle"/>
 *             &lt;enumeration value="Organisation"/>
 *             &lt;enumeration value="Organisation (Kommerziell)"/>
 *             &lt;enumeration value="Organisation (Kriminell)"/>
 *             &lt;enumeration value="Organisation (Milit�risch)"/>
 *             &lt;enumeration value="Organisation (Regierung)"/>
 *             &lt;enumeration value="Organisation (Religi�s)"/>
 *             &lt;enumeration value="Ort"/>
 *             &lt;enumeration value="Planetary Body Type"/>
 *             &lt;enumeration value="Plot Fokus"/>
 *             &lt;enumeration value="Plot Stil"/>
 *             &lt;enumeration value="Plot Struktur"/>
 *             &lt;enumeration value="Position Art"/>
 *             &lt;enumeration value="Preis"/>
 *             &lt;enumeration value="Qualit�t"/>
 *             &lt;enumeration value="Rank"/>
 *             &lt;enumeration value="Rasse"/>
 *             &lt;enumeration value="Regierung Rolle"/>
 *             &lt;enumeration value="Region (dimensional)"/>
 *             &lt;enumeration value="Region (geographisch)"/>
 *             &lt;enumeration value="Region (politisch)"/>
 *             &lt;enumeration value="Region (st�dtisch Verwendung)"/>
 *             &lt;enumeration value="Region (st�dtisch)"/>
 *             &lt;enumeration value="Rule System"/>
 *             &lt;enumeration value="Service"/>
 *             &lt;enumeration value="Setting"/>
 *             &lt;enumeration value="Szene (Art)"/>
 *             &lt;enumeration value="Terrain"/>
 *             &lt;enumeration value="Transport Medium"/>
 *             &lt;enumeration value="Umfa�t Beziehungsart"/>
 *             &lt;enumeration value="Umweltbedingung"/>
 *             &lt;enumeration value="Utility"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="global_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0008287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="1D01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="1E01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3EAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="40AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="41AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="42AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="43AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="44AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="45AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="46AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="47AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="48AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="49AE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4AAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4BAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4CAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4DAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4EAE297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="53082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="54082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="55082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="56082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7201287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="741E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="751E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="761E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="791E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7C1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7D1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7E1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7F1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="801E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="811E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="821E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="831E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="841E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="851E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="861E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="871E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="881E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="891E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8D1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8E1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8F1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="901E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="911E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="931E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="941E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="951E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="961E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="971E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="981E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="991E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9C1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B801287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="B901287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DF00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
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
@XmlRootElement(name = "domain_global")
public class DomainGlobal {

    @XmlElementRefs({
        @XmlElementRef(name = "overlay", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Overlay.class, required = false),
        @XmlElementRef(name = "text_override", namespace = "urn:lonewolfdevel.com:realm-works-export", type = TextOverride.class, required = false),
        @XmlElementRef(name = "tag_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = TagGlobal.class, required = false),
        @XmlElementRef(name = "tag", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Tag.class, required = false)
    })
    protected List<Object> content;
    @XmlAttribute(name = "domain_id", required = true)
    protected String domainId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "global_uuid", required = true)
    protected String globalUuid;

    /**
     * Ruft das restliche Contentmodell ab.
     *
     * <p>
     * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
     * Der Feldname "Tag" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
     * Zeile 1079 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
     * Zeile 1076 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
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
     * {@link Overlay }
     * {@link TextOverride }
     * {@link TagGlobal }
     * {@link Tag }
     */
    public List<Object> getContent() {
        if (content == null) {
            content = new ArrayList<Object>();
        }
        return this.content;
    }

    /**
     * Ruft den Wert der domainId-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * Legt den Wert der domainId-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDomainId(String value) {
        this.domainId = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der globalUuid-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGlobalUuid() {
        return globalUuid;
    }

    /**
     * Legt den Wert der globalUuid-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGlobalUuid(String value) {
        this.globalUuid = value;
    }

}
