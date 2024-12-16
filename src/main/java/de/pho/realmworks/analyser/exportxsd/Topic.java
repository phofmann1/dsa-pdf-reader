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
import javax.xml.datatype.XMLGregorianCalendar;


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
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}alias"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}section" maxOccurs="unbounded"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_assign" maxOccurs="unbounded"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}connection" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}section" maxOccurs="unbounded"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}tag_assign" maxOccurs="unbounded"/>
 *           &lt;choice minOccurs="0">
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}dconnection"/>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}connection"/>
 *             &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}topic" maxOccurs="unbounded"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="topic_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Topic_1"/>
 *             &lt;enumeration value="Topic_10"/>
 *             &lt;enumeration value="Topic_100"/>
 *             &lt;enumeration value="Topic_101"/>
 *             &lt;enumeration value="Topic_102"/>
 *             &lt;enumeration value="Topic_103"/>
 *             &lt;enumeration value="Topic_104"/>
 *             &lt;enumeration value="Topic_105"/>
 *             &lt;enumeration value="Topic_106"/>
 *             &lt;enumeration value="Topic_107"/>
 *             &lt;enumeration value="Topic_108"/>
 *             &lt;enumeration value="Topic_109"/>
 *             &lt;enumeration value="Topic_11"/>
 *             &lt;enumeration value="Topic_110"/>
 *             &lt;enumeration value="Topic_111"/>
 *             &lt;enumeration value="Topic_112"/>
 *             &lt;enumeration value="Topic_113"/>
 *             &lt;enumeration value="Topic_114"/>
 *             &lt;enumeration value="Topic_115"/>
 *             &lt;enumeration value="Topic_116"/>
 *             &lt;enumeration value="Topic_117"/>
 *             &lt;enumeration value="Topic_118"/>
 *             &lt;enumeration value="Topic_119"/>
 *             &lt;enumeration value="Topic_12"/>
 *             &lt;enumeration value="Topic_120"/>
 *             &lt;enumeration value="Topic_121"/>
 *             &lt;enumeration value="Topic_122"/>
 *             &lt;enumeration value="Topic_123"/>
 *             &lt;enumeration value="Topic_124"/>
 *             &lt;enumeration value="Topic_125"/>
 *             &lt;enumeration value="Topic_126"/>
 *             &lt;enumeration value="Topic_127"/>
 *             &lt;enumeration value="Topic_128"/>
 *             &lt;enumeration value="Topic_129"/>
 *             &lt;enumeration value="Topic_13"/>
 *             &lt;enumeration value="Topic_130"/>
 *             &lt;enumeration value="Topic_131"/>
 *             &lt;enumeration value="Topic_132"/>
 *             &lt;enumeration value="Topic_133"/>
 *             &lt;enumeration value="Topic_134"/>
 *             &lt;enumeration value="Topic_135"/>
 *             &lt;enumeration value="Topic_136"/>
 *             &lt;enumeration value="Topic_137"/>
 *             &lt;enumeration value="Topic_138"/>
 *             &lt;enumeration value="Topic_139"/>
 *             &lt;enumeration value="Topic_14"/>
 *             &lt;enumeration value="Topic_140"/>
 *             &lt;enumeration value="Topic_141"/>
 *             &lt;enumeration value="Topic_142"/>
 *             &lt;enumeration value="Topic_143"/>
 *             &lt;enumeration value="Topic_144"/>
 *             &lt;enumeration value="Topic_145"/>
 *             &lt;enumeration value="Topic_146"/>
 *             &lt;enumeration value="Topic_147"/>
 *             &lt;enumeration value="Topic_148"/>
 *             &lt;enumeration value="Topic_149"/>
 *             &lt;enumeration value="Topic_15"/>
 *             &lt;enumeration value="Topic_150"/>
 *             &lt;enumeration value="Topic_151"/>
 *             &lt;enumeration value="Topic_152"/>
 *             &lt;enumeration value="Topic_153"/>
 *             &lt;enumeration value="Topic_154"/>
 *             &lt;enumeration value="Topic_155"/>
 *             &lt;enumeration value="Topic_156"/>
 *             &lt;enumeration value="Topic_157"/>
 *             &lt;enumeration value="Topic_158"/>
 *             &lt;enumeration value="Topic_159"/>
 *             &lt;enumeration value="Topic_16"/>
 *             &lt;enumeration value="Topic_160"/>
 *             &lt;enumeration value="Topic_161"/>
 *             &lt;enumeration value="Topic_162"/>
 *             &lt;enumeration value="Topic_163"/>
 *             &lt;enumeration value="Topic_164"/>
 *             &lt;enumeration value="Topic_165"/>
 *             &lt;enumeration value="Topic_166"/>
 *             &lt;enumeration value="Topic_167"/>
 *             &lt;enumeration value="Topic_17"/>
 *             &lt;enumeration value="Topic_18"/>
 *             &lt;enumeration value="Topic_19"/>
 *             &lt;enumeration value="Topic_2"/>
 *             &lt;enumeration value="Topic_20"/>
 *             &lt;enumeration value="Topic_21"/>
 *             &lt;enumeration value="Topic_22"/>
 *             &lt;enumeration value="Topic_23"/>
 *             &lt;enumeration value="Topic_24"/>
 *             &lt;enumeration value="Topic_25"/>
 *             &lt;enumeration value="Topic_26"/>
 *             &lt;enumeration value="Topic_27"/>
 *             &lt;enumeration value="Topic_28"/>
 *             &lt;enumeration value="Topic_29"/>
 *             &lt;enumeration value="Topic_3"/>
 *             &lt;enumeration value="Topic_30"/>
 *             &lt;enumeration value="Topic_31"/>
 *             &lt;enumeration value="Topic_32"/>
 *             &lt;enumeration value="Topic_33"/>
 *             &lt;enumeration value="Topic_34"/>
 *             &lt;enumeration value="Topic_35"/>
 *             &lt;enumeration value="Topic_36"/>
 *             &lt;enumeration value="Topic_37"/>
 *             &lt;enumeration value="Topic_38"/>
 *             &lt;enumeration value="Topic_39"/>
 *             &lt;enumeration value="Topic_4"/>
 *             &lt;enumeration value="Topic_40"/>
 *             &lt;enumeration value="Topic_41"/>
 *             &lt;enumeration value="Topic_42"/>
 *             &lt;enumeration value="Topic_43"/>
 *             &lt;enumeration value="Topic_44"/>
 *             &lt;enumeration value="Topic_45"/>
 *             &lt;enumeration value="Topic_46"/>
 *             &lt;enumeration value="Topic_47"/>
 *             &lt;enumeration value="Topic_48"/>
 *             &lt;enumeration value="Topic_49"/>
 *             &lt;enumeration value="Topic_5"/>
 *             &lt;enumeration value="Topic_50"/>
 *             &lt;enumeration value="Topic_51"/>
 *             &lt;enumeration value="Topic_52"/>
 *             &lt;enumeration value="Topic_53"/>
 *             &lt;enumeration value="Topic_54"/>
 *             &lt;enumeration value="Topic_55"/>
 *             &lt;enumeration value="Topic_56"/>
 *             &lt;enumeration value="Topic_57"/>
 *             &lt;enumeration value="Topic_58"/>
 *             &lt;enumeration value="Topic_59"/>
 *             &lt;enumeration value="Topic_6"/>
 *             &lt;enumeration value="Topic_60"/>
 *             &lt;enumeration value="Topic_61"/>
 *             &lt;enumeration value="Topic_62"/>
 *             &lt;enumeration value="Topic_63"/>
 *             &lt;enumeration value="Topic_64"/>
 *             &lt;enumeration value="Topic_65"/>
 *             &lt;enumeration value="Topic_66"/>
 *             &lt;enumeration value="Topic_67"/>
 *             &lt;enumeration value="Topic_68"/>
 *             &lt;enumeration value="Topic_69"/>
 *             &lt;enumeration value="Topic_7"/>
 *             &lt;enumeration value="Topic_70"/>
 *             &lt;enumeration value="Topic_71"/>
 *             &lt;enumeration value="Topic_72"/>
 *             &lt;enumeration value="Topic_73"/>
 *             &lt;enumeration value="Topic_74"/>
 *             &lt;enumeration value="Topic_75"/>
 *             &lt;enumeration value="Topic_76"/>
 *             &lt;enumeration value="Topic_77"/>
 *             &lt;enumeration value="Topic_78"/>
 *             &lt;enumeration value="Topic_79"/>
 *             &lt;enumeration value="Topic_8"/>
 *             &lt;enumeration value="Topic_80"/>
 *             &lt;enumeration value="Topic_81"/>
 *             &lt;enumeration value="Topic_82"/>
 *             &lt;enumeration value="Topic_83"/>
 *             &lt;enumeration value="Topic_84"/>
 *             &lt;enumeration value="Topic_85"/>
 *             &lt;enumeration value="Topic_86"/>
 *             &lt;enumeration value="Topic_87"/>
 *             &lt;enumeration value="Topic_88"/>
 *             &lt;enumeration value="Topic_89"/>
 *             &lt;enumeration value="Topic_9"/>
 *             &lt;enumeration value="Topic_90"/>
 *             &lt;enumeration value="Topic_91"/>
 *             &lt;enumeration value="Topic_92"/>
 *             &lt;enumeration value="Topic_93"/>
 *             &lt;enumeration value="Topic_94"/>
 *             &lt;enumeration value="Topic_95"/>
 *             &lt;enumeration value="Topic_96"/>
 *             &lt;enumeration value="Topic_97"/>
 *             &lt;enumeration value="Topic_98"/>
 *             &lt;enumeration value="Topic_99"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="public_name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="prefix">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="-1.063"/>
 *             &lt;enumeration value="-1.332"/>
 *             &lt;enumeration value="-1.334"/>
 *             &lt;enumeration value="-1.341"/>
 *             &lt;enumeration value="-1.447"/>
 *             &lt;enumeration value="-1.700"/>
 *             &lt;enumeration value="-16"/>
 *             &lt;enumeration value="-198"/>
 *             &lt;enumeration value="-507"/>
 *             &lt;enumeration value="-508"/>
 *             &lt;enumeration value="-709"/>
 *             &lt;enumeration value="-799"/>
 *             &lt;enumeration value="-872"/>
 *             &lt;enumeration value="-882"/>
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="1.019"/>
 *             &lt;enumeration value="1.021-05"/>
 *             &lt;enumeration value="1.021-08-01"/>
 *             &lt;enumeration value="1.021-08-30"/>
 *             &lt;enumeration value="1.021-10"/>
 *             &lt;enumeration value="1.021-12"/>
 *             &lt;enumeration value="1.022"/>
 *             &lt;enumeration value="1.022-03"/>
 *             &lt;enumeration value="1.022-05"/>
 *             &lt;enumeration value="1.027-12"/>
 *             &lt;enumeration value="1.028-03-00"/>
 *             &lt;enumeration value="1.028-03-04"/>
 *             &lt;enumeration value="1.028-03-07"/>
 *             &lt;enumeration value="1.028-03-10"/>
 *             &lt;enumeration value="1.028-03-11"/>
 *             &lt;enumeration value="1.028-03-12"/>
 *             &lt;enumeration value="1.028-03-13"/>
 *             &lt;enumeration value="1.028-03-14"/>
 *             &lt;enumeration value="1.028-03-18"/>
 *             &lt;enumeration value="1.028-03-20"/>
 *             &lt;enumeration value="1.028-03-21"/>
 *             &lt;enumeration value="1.028-03-25"/>
 *             &lt;enumeration value="1.028-03-28"/>
 *             &lt;enumeration value="1.028-04-.04"/>
 *             &lt;enumeration value="1.028-04-01"/>
 *             &lt;enumeration value="1.028-04-03"/>
 *             &lt;enumeration value="1.028-04-07"/>
 *             &lt;enumeration value="1.028-10"/>
 *             &lt;enumeration value="1.029"/>
 *             &lt;enumeration value="1.031-01"/>
 *             &lt;enumeration value="1.035"/>
 *             &lt;enumeration value="1.037"/>
 *             &lt;enumeration value="1.040-05"/>
 *             &lt;enumeration value="1.041"/>
 *             &lt;enumeration value="251"/>
 *             &lt;enumeration value="255"/>
 *             &lt;enumeration value="590"/>
 *             &lt;enumeration value="592"/>
 *             &lt;enumeration value="593"/>
 *             &lt;enumeration value="595"/>
 *             &lt;enumeration value="603"/>
 *             &lt;enumeration value="766"/>
 *             &lt;enumeration value="792"/>
 *             &lt;enumeration value="922"/>
 *             &lt;enumeration value="923"/>
 *             &lt;enumeration value="992"/>
 *             &lt;enumeration value="994"/>
 *             &lt;enumeration value="997"/>
 *             &lt;enumeration value="A"/>
 *             &lt;enumeration value="AU"/>
 *             &lt;enumeration value="Aranien"/>
 *             &lt;enumeration value="B"/>
 *             &lt;enumeration value="C"/>
 *             &lt;enumeration value="D"/>
 *             &lt;enumeration value="E"/>
 *             &lt;enumeration value="EV"/>
 *             &lt;enumeration value="Elburien"/>
 *             &lt;enumeration value="F"/>
 *             &lt;enumeration value="G"/>
 *             &lt;enumeration value="Mysterium"/>
 *             &lt;enumeration value="PV"/>
 *             &lt;enumeration value="Y"/>
 *             &lt;enumeration value="Z"/>
 *             &lt;enumeration value="Zorgana"/>
 *             &lt;enumeration value="regional"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="suffix">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="#mhaharanya"/>
 *             &lt;enumeration value="Aranien"/>
 *             &lt;enumeration value="Aranien bis heute"/>
 *             &lt;enumeration value="Dramatis Personae"/>
 *             &lt;enumeration value="Emir"/>
 *             &lt;enumeration value="Emirat"/>
 *             &lt;enumeration value="Emirat, Vasallenstaat"/>
 *             &lt;enumeration value="Ende TSA"/>
 *             &lt;enumeration value="F�rstin"/>
 *             &lt;enumeration value="Gemahl"/>
 *             &lt;enumeration value="Gruppen"/>
 *             &lt;enumeration value="Historische Pers�nlichkeiten"/>
 *             &lt;enumeration value="K�nig"/>
 *             &lt;enumeration value="Mhaharanya"/>
 *             &lt;enumeration value="Mhaharanyat"/>
 *             &lt;enumeration value="Mysteria &amp; Arcana"/>
 *             &lt;enumeration value="Mythos &amp; Historie"/>
 *             &lt;enumeration value="PER"/>
 *             &lt;enumeration value="Qabalim"/>
 *             &lt;enumeration value="Qabalym"/>
 *             &lt;enumeration value="Radjarat"/>
 *             &lt;enumeration value="Rahjageweihte"/>
 *             &lt;enumeration value="Stammesf�rstin"/>
 *             &lt;enumeration value="Sternkundige"/>
 *             &lt;enumeration value="Sultan"/>
 *             &lt;enumeration value="geographisch"/>
 *             &lt;enumeration value="politisch"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="match_priority">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Never"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="category_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Category_100"/>
 *             &lt;enumeration value="Category_104"/>
 *             &lt;enumeration value="Category_108"/>
 *             &lt;enumeration value="Category_111"/>
 *             &lt;enumeration value="Category_114"/>
 *             &lt;enumeration value="Category_115"/>
 *             &lt;enumeration value="Category_116"/>
 *             &lt;enumeration value="Category_117"/>
 *             &lt;enumeration value="Category_118"/>
 *             &lt;enumeration value="Category_122"/>
 *             &lt;enumeration value="Category_124"/>
 *             &lt;enumeration value="Category_125"/>
 *             &lt;enumeration value="Category_129"/>
 *             &lt;enumeration value="Category_32"/>
 *             &lt;enumeration value="Category_36"/>
 *             &lt;enumeration value="Category_43"/>
 *             &lt;enumeration value="Category_96"/>
 *             &lt;enumeration value="Category_97"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="original_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="01EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="03E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0AE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0AED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="0C5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="0CBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="0D839BB6-A283-69ED-7C14-689353716BA9"/>
 *             &lt;enumeration value="11BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="11E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="11EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="12E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="12E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="12EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="155D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="17EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="18E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1CE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1DDD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="1EC09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="23699AB6-2F8B-AF47-C003-689353716BA9"/>
 *             &lt;enumeration value="245F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="24ED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="25EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="26E19BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="26E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="27DE9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2AC09BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="2D5B9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="2DE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2DEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="2EBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="2F659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="315A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="316B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="32E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="32EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="33599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="35E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="38E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="39BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="3AEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3DED9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3E689BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="3EE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="3FDD9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="426D9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="47E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4D579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="4E5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="4EE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4EEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="4FDA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="53E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="58569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="586E9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="595C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5BE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5BE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="5F5F9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="5F609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="63E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="64609BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="64EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="66579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="66EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="67E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="69E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6AE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6CE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="6F5D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="73E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="74E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="76E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7BEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7CE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="7F579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="80E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="82E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="85E29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="865D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="88BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="88E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="88E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="88E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8AE29BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="8AE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="94E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="955C9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="97589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="98579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="996A9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9B659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9B6B9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="9BBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="9BE69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9CE79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9DE89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="9DEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A1E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A2EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A4E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A6EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="A8E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="AABE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="ACF49BB6-A107-C91F-C71D-689353716BA9"/>
 *             &lt;enumeration value="AEBF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="AF5E9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B1E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B2E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B45D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="B5E09BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B5E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B6BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="B7EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="B8E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BBE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BCEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="BEBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="BF559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="C0E39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C2BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C3679BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="C5BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="C5E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C7EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="C9E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CA579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CC559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CCE49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="CD659BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="CE589BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="CFBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="D0E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D15D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D1E59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D2EB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D3E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="D65D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="D66C9BB6-DBEC-F0B0-466F-689353716BA9"/>
 *             &lt;enumeration value="D6BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DABE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DCDC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="DE5D9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="DFBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="DFEC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E0E49BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E3579BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="E6BE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="E6E69BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E6EA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="E8E99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EAE39BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EAE59BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EB569BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="EBDF9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="ECEB9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="EDBE9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="F1E89BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F3559BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F4E79BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F55A9BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="F5EC9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="F7BF9BB6-7B5F-9D3B-EF1A-689353716BA9"/>
 *             &lt;enumeration value="FC599BB6-7BD6-B926-896E-689353716BA9"/>
 *             &lt;enumeration value="FCEA9BB6-3AA8-1A22-F71C-689353716BA9"/>
 *             &lt;enumeration value="FEE99BB6-3AA8-1A22-F71C-689353716BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="signature" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;enumeration value="11576"/>
 *             &lt;enumeration value="11586"/>
 *             &lt;enumeration value="11591"/>
 *             &lt;enumeration value="11629"/>
 *             &lt;enumeration value="11649"/>
 *             &lt;enumeration value="11657"/>
 *             &lt;enumeration value="11669"/>
 *             &lt;enumeration value="11674"/>
 *             &lt;enumeration value="11692"/>
 *             &lt;enumeration value="11708"/>
 *             &lt;enumeration value="11716"/>
 *             &lt;enumeration value="11746"/>
 *             &lt;enumeration value="11755"/>
 *             &lt;enumeration value="11772"/>
 *             &lt;enumeration value="11788"/>
 *             &lt;enumeration value="11796"/>
 *             &lt;enumeration value="11824"/>
 *             &lt;enumeration value="11844"/>
 *             &lt;enumeration value="11859"/>
 *             &lt;enumeration value="11871"/>
 *             &lt;enumeration value="11883"/>
 *             &lt;enumeration value="11892"/>
 *             &lt;enumeration value="11906"/>
 *             &lt;enumeration value="11919"/>
 *             &lt;enumeration value="11927"/>
 *             &lt;enumeration value="11936"/>
 *             &lt;enumeration value="11946"/>
 *             &lt;enumeration value="11958"/>
 *             &lt;enumeration value="11978"/>
 *             &lt;enumeration value="11989"/>
 *             &lt;enumeration value="12015"/>
 *             &lt;enumeration value="12037"/>
 *             &lt;enumeration value="12046"/>
 *             &lt;enumeration value="12055"/>
 *             &lt;enumeration value="12064"/>
 *             &lt;enumeration value="12072"/>
 *             &lt;enumeration value="12081"/>
 *             &lt;enumeration value="12101"/>
 *             &lt;enumeration value="12121"/>
 *             &lt;enumeration value="12142"/>
 *             &lt;enumeration value="12162"/>
 *             &lt;enumeration value="12180"/>
 *             &lt;enumeration value="12188"/>
 *             &lt;enumeration value="12206"/>
 *             &lt;enumeration value="12215"/>
 *             &lt;enumeration value="12225"/>
 *             &lt;enumeration value="12233"/>
 *             &lt;enumeration value="12269"/>
 *             &lt;enumeration value="12278"/>
 *             &lt;enumeration value="12297"/>
 *             &lt;enumeration value="12316"/>
 *             &lt;enumeration value="12326"/>
 *             &lt;enumeration value="12335"/>
 *             &lt;enumeration value="12343"/>
 *             &lt;enumeration value="12351"/>
 *             &lt;enumeration value="12359"/>
 *             &lt;enumeration value="12369"/>
 *             &lt;enumeration value="12391"/>
 *             &lt;enumeration value="12396"/>
 *             &lt;enumeration value="12405"/>
 *             &lt;enumeration value="12414"/>
 *             &lt;enumeration value="12424"/>
 *             &lt;enumeration value="12433"/>
 *             &lt;enumeration value="12442"/>
 *             &lt;enumeration value="12452"/>
 *             &lt;enumeration value="12462"/>
 *             &lt;enumeration value="12470"/>
 *             &lt;enumeration value="12505"/>
 *             &lt;enumeration value="12523"/>
 *             &lt;enumeration value="12535"/>
 *             &lt;enumeration value="12545"/>
 *             &lt;enumeration value="12554"/>
 *             &lt;enumeration value="12564"/>
 *             &lt;enumeration value="12577"/>
 *             &lt;enumeration value="12593"/>
 *             &lt;enumeration value="12613"/>
 *             &lt;enumeration value="12709"/>
 *             &lt;enumeration value="12726"/>
 *             &lt;enumeration value="12740"/>
 *             &lt;enumeration value="12754"/>
 *             &lt;enumeration value="12767"/>
 *             &lt;enumeration value="12777"/>
 *             &lt;enumeration value="12782"/>
 *             &lt;enumeration value="12829"/>
 *             &lt;enumeration value="12843"/>
 *             &lt;enumeration value="12848"/>
 *             &lt;enumeration value="12853"/>
 *             &lt;enumeration value="12866"/>
 *             &lt;enumeration value="13101"/>
 *             &lt;enumeration value="13180"/>
 *             &lt;enumeration value="13340"/>
 *             &lt;enumeration value="13363"/>
 *             &lt;enumeration value="13410"/>
 *             &lt;enumeration value="13524"/>
 *             &lt;enumeration value="13532"/>
 *             &lt;enumeration value="13588"/>
 *             &lt;enumeration value="13623"/>
 *             &lt;enumeration value="13633"/>
 *             &lt;enumeration value="13689"/>
 *             &lt;enumeration value="13698"/>
 *             &lt;enumeration value="13998"/>
 *             &lt;enumeration value="14010"/>
 *             &lt;enumeration value="14022"/>
 *             &lt;enumeration value="14034"/>
 *             &lt;enumeration value="14090"/>
 *             &lt;enumeration value="14099"/>
 *             &lt;enumeration value="14122"/>
 *             &lt;enumeration value="14168"/>
 *             &lt;enumeration value="14253"/>
 *             &lt;enumeration value="14309"/>
 *             &lt;enumeration value="14338"/>
 *             &lt;enumeration value="14349"/>
 *             &lt;enumeration value="14395"/>
 *             &lt;enumeration value="14486"/>
 *             &lt;enumeration value="14544"/>
 *             &lt;enumeration value="14600"/>
 *             &lt;enumeration value="14691"/>
 *             &lt;enumeration value="14721"/>
 *             &lt;enumeration value="14767"/>
 *             &lt;enumeration value="24194"/>
 *             &lt;enumeration value="24210"/>
 *             &lt;enumeration value="24221"/>
 *             &lt;enumeration value="24229"/>
 *             &lt;enumeration value="24241"/>
 *             &lt;enumeration value="24252"/>
 *             &lt;enumeration value="24258"/>
 *             &lt;enumeration value="24268"/>
 *             &lt;enumeration value="24276"/>
 *             &lt;enumeration value="24289"/>
 *             &lt;enumeration value="24294"/>
 *             &lt;enumeration value="24341"/>
 *             &lt;enumeration value="24358"/>
 *             &lt;enumeration value="24406"/>
 *             &lt;enumeration value="24426"/>
 *             &lt;enumeration value="24439"/>
 *             &lt;enumeration value="24485"/>
 *             &lt;enumeration value="24491"/>
 *             &lt;enumeration value="24498"/>
 *             &lt;enumeration value="24506"/>
 *             &lt;enumeration value="24513"/>
 *             &lt;enumeration value="24537"/>
 *             &lt;enumeration value="24558"/>
 *             &lt;enumeration value="24564"/>
 *             &lt;enumeration value="25769"/>
 *             &lt;enumeration value="28456"/>
 *             &lt;enumeration value="28980"/>
 *             &lt;enumeration value="29245"/>
 *             &lt;enumeration value="29283"/>
 *             &lt;enumeration value="29352"/>
 *             &lt;enumeration value="29518"/>
 *             &lt;enumeration value="29523"/>
 *             &lt;enumeration value="29542"/>
 *             &lt;enumeration value="29561"/>
 *             &lt;enumeration value="29567"/>
 *             &lt;enumeration value="29572"/>
 *             &lt;enumeration value="29585"/>
 *             &lt;enumeration value="29591"/>
 *             &lt;enumeration value="29596"/>
 *             &lt;enumeration value="29601"/>
 *             &lt;enumeration value="29606"/>
 *             &lt;enumeration value="29611"/>
 *             &lt;enumeration value="29617"/>
 *             &lt;enumeration value="29623"/>
 *             &lt;enumeration value="29628"/>
 *             &lt;enumeration value="29633"/>
 *             &lt;enumeration value="29638"/>
 *             &lt;enumeration value="33398"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_revealed" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="reveal_date">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}dateTime">
 *             &lt;enumeration value="2021-03-26T18:21:23Z"/>
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
@XmlRootElement(name = "topic")
public class Topic {

  @XmlElementRefs({
      @XmlElementRef(name = "connection", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Connection.class, required = false),
      @XmlElementRef(name = "alias", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Alias.class, required = false),
      @XmlElementRef(name = "topic", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Topic.class, required = false),
      @XmlElementRef(name = "tag_assign", namespace = "urn:lonewolfdevel.com:realm-works-export", type = TagAssign.class, required = false),
      @XmlElementRef(name = "section", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Section.class, required = false),
      @XmlElementRef(name = "dconnection", namespace = "urn:lonewolfdevel.com:realm-works-export", type = Dconnection.class, required = false)
  })
  protected List<Object> content;
  @XmlAttribute(name = "topic_id", required = true)
  protected String topicId;
  @XmlAttribute(name = "public_name", required = true)
  protected String publicName;
  @XmlAttribute(name = "prefix")
  protected String prefix;
  @XmlAttribute(name = "suffix")
  protected String suffix;
  @XmlAttribute(name = "match_priority")
  protected String matchPriority;
  @XmlAttribute(name = "category_id", required = true)
  protected String categoryId;
  @XmlAttribute(name = "original_uuid", required = true)
  protected String originalUuid;
  @XmlAttribute(name = "signature", required = true)
  protected int signature;
  @XmlAttribute(name = "is_revealed")
  protected Boolean isRevealed;
  @XmlAttribute(name = "reveal_date")
  protected XMLGregorianCalendar revealDate;

  /**
   * Ruft das restliche Contentmodell ab.
   *
   * <p>
   * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
   * Der Feldname "Section" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
   * Zeile 12242 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
   * Zeile 12237 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
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
   * {@link Connection }
   * {@link TagAssign }
   * {@link Alias }
   * {@link Topic }
   * {@link Section }
   * {@link Dconnection }
   */
  public List<Object> getContent() {
    if (content == null) {
      content = new ArrayList<Object>();
    }
    return this.content;
  }

  /**
   * Ruft den Wert der topicId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getTopicId() {
    return topicId;
  }

  /**
   * Legt den Wert der topicId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setTopicId(String value) {
    this.topicId = value;
  }

  /**
   * Ruft den Wert der publicName-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getPublicName() {
    return publicName;
  }

  /**
   * Legt den Wert der publicName-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setPublicName(String value) {
    this.publicName = value;
  }

  /**
   * Ruft den Wert der prefix-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Legt den Wert der prefix-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setPrefix(String value) {
    this.prefix = value;
  }

  /**
   * Ruft den Wert der suffix-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * Legt den Wert der suffix-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setSuffix(String value) {
    this.suffix = value;
  }

  /**
   * Ruft den Wert der matchPriority-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getMatchPriority() {
    return matchPriority;
  }

  /**
   * Legt den Wert der matchPriority-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setMatchPriority(String value) {
    this.matchPriority = value;
  }

  /**
   * Ruft den Wert der categoryId-Eigenschaft ab.
   *
   * @return possible object is
   * {@link String }
   */
  public String getCategoryId() {
    return categoryId;
  }

  /**
   * Legt den Wert der categoryId-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link String }
   */
  public void setCategoryId(String value) {
    this.categoryId = value;
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
   * Ruft den Wert der isRevealed-Eigenschaft ab.
   *
   * @return possible object is
   * {@link Boolean }
   */
  public Boolean isIsRevealed() {
    return isRevealed;
  }

  /**
   * Legt den Wert der isRevealed-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link Boolean }
   */
  public void setIsRevealed(Boolean value) {
    this.isRevealed = value;
  }

  /**
   * Ruft den Wert der revealDate-Eigenschaft ab.
   *
   * @return possible object is
   * {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getRevealDate() {
    return revealDate;
  }

  /**
   * Legt den Wert der revealDate-Eigenschaft fest.
   *
   * @param value allowed object is
   *              {@link XMLGregorianCalendar }
   */
  public void setRevealDate(XMLGregorianCalendar value) {
    this.revealDate = value;
  }

}
