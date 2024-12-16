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
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="facet_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Facet_1"/>
 *             &lt;enumeration value="Facet_204"/>
 *             &lt;enumeration value="Facet_205"/>
 *             &lt;enumeration value="Facet_206"/>
 *             &lt;enumeration value="Facet_222"/>
 *             &lt;enumeration value="Facet_223"/>
 *             &lt;enumeration value="Facet_224"/>
 *             &lt;enumeration value="Facet_225"/>
 *             &lt;enumeration value="Facet_226"/>
 *             &lt;enumeration value="Facet_23"/>
 *             &lt;enumeration value="Facet_231"/>
 *             &lt;enumeration value="Facet_232"/>
 *             &lt;enumeration value="Facet_233"/>
 *             &lt;enumeration value="Facet_234"/>
 *             &lt;enumeration value="Facet_235"/>
 *             &lt;enumeration value="Facet_236"/>
 *             &lt;enumeration value="Facet_237"/>
 *             &lt;enumeration value="Facet_238"/>
 *             &lt;enumeration value="Facet_239"/>
 *             &lt;enumeration value="Facet_24"/>
 *             &lt;enumeration value="Facet_240"/>
 *             &lt;enumeration value="Facet_241"/>
 *             &lt;enumeration value="Facet_242"/>
 *             &lt;enumeration value="Facet_243"/>
 *             &lt;enumeration value="Facet_244"/>
 *             &lt;enumeration value="Facet_245"/>
 *             &lt;enumeration value="Facet_246"/>
 *             &lt;enumeration value="Facet_247"/>
 *             &lt;enumeration value="Facet_248"/>
 *             &lt;enumeration value="Facet_249"/>
 *             &lt;enumeration value="Facet_25"/>
 *             &lt;enumeration value="Facet_250"/>
 *             &lt;enumeration value="Facet_251"/>
 *             &lt;enumeration value="Facet_252"/>
 *             &lt;enumeration value="Facet_257"/>
 *             &lt;enumeration value="Facet_258"/>
 *             &lt;enumeration value="Facet_259"/>
 *             &lt;enumeration value="Facet_26"/>
 *             &lt;enumeration value="Facet_260"/>
 *             &lt;enumeration value="Facet_27"/>
 *             &lt;enumeration value="Facet_276"/>
 *             &lt;enumeration value="Facet_277"/>
 *             &lt;enumeration value="Facet_278"/>
 *             &lt;enumeration value="Facet_279"/>
 *             &lt;enumeration value="Facet_28"/>
 *             &lt;enumeration value="Facet_280"/>
 *             &lt;enumeration value="Facet_281"/>
 *             &lt;enumeration value="Facet_29"/>
 *             &lt;enumeration value="Facet_291"/>
 *             &lt;enumeration value="Facet_292"/>
 *             &lt;enumeration value="Facet_293"/>
 *             &lt;enumeration value="Facet_294"/>
 *             &lt;enumeration value="Facet_295"/>
 *             &lt;enumeration value="Facet_296"/>
 *             &lt;enumeration value="Facet_297"/>
 *             &lt;enumeration value="Facet_298"/>
 *             &lt;enumeration value="Facet_299"/>
 *             &lt;enumeration value="Facet_30"/>
 *             &lt;enumeration value="Facet_300"/>
 *             &lt;enumeration value="Facet_301"/>
 *             &lt;enumeration value="Facet_302"/>
 *             &lt;enumeration value="Facet_303"/>
 *             &lt;enumeration value="Facet_304"/>
 *             &lt;enumeration value="Facet_305"/>
 *             &lt;enumeration value="Facet_306"/>
 *             &lt;enumeration value="Facet_307"/>
 *             &lt;enumeration value="Facet_308"/>
 *             &lt;enumeration value="Facet_309"/>
 *             &lt;enumeration value="Facet_31"/>
 *             &lt;enumeration value="Facet_310"/>
 *             &lt;enumeration value="Facet_311"/>
 *             &lt;enumeration value="Facet_312"/>
 *             &lt;enumeration value="Facet_313"/>
 *             &lt;enumeration value="Facet_314"/>
 *             &lt;enumeration value="Facet_315"/>
 *             &lt;enumeration value="Facet_316"/>
 *             &lt;enumeration value="Facet_317"/>
 *             &lt;enumeration value="Facet_318"/>
 *             &lt;enumeration value="Facet_319"/>
 *             &lt;enumeration value="Facet_32"/>
 *             &lt;enumeration value="Facet_320"/>
 *             &lt;enumeration value="Facet_321"/>
 *             &lt;enumeration value="Facet_322"/>
 *             &lt;enumeration value="Facet_323"/>
 *             &lt;enumeration value="Facet_324"/>
 *             &lt;enumeration value="Facet_325"/>
 *             &lt;enumeration value="Facet_326"/>
 *             &lt;enumeration value="Facet_33"/>
 *             &lt;enumeration value="Facet_334"/>
 *             &lt;enumeration value="Facet_335"/>
 *             &lt;enumeration value="Facet_336"/>
 *             &lt;enumeration value="Facet_337"/>
 *             &lt;enumeration value="Facet_338"/>
 *             &lt;enumeration value="Facet_339"/>
 *             &lt;enumeration value="Facet_34"/>
 *             &lt;enumeration value="Facet_340"/>
 *             &lt;enumeration value="Facet_341"/>
 *             &lt;enumeration value="Facet_342"/>
 *             &lt;enumeration value="Facet_35"/>
 *             &lt;enumeration value="Facet_36"/>
 *             &lt;enumeration value="Facet_37"/>
 *             &lt;enumeration value="Facet_38"/>
 *             &lt;enumeration value="Facet_39"/>
 *             &lt;enumeration value="Facet_40"/>
 *             &lt;enumeration value="Facet_41"/>
 *             &lt;enumeration value="Facet_42"/>
 *             &lt;enumeration value="Facet_43"/>
 *             &lt;enumeration value="Facet_66"/>
 *             &lt;enumeration value="Facet_67"/>
 *             &lt;enumeration value="Facet_68"/>
 *             &lt;enumeration value="Facet_69"/>
 *             &lt;enumeration value="Facet_70"/>
 *             &lt;enumeration value="Facet_71"/>
 *             &lt;enumeration value="Facet_72"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Abzeichen"/>
 *             &lt;enumeration value="Aktivit�ten"/>
 *             &lt;enumeration value="Angebotene Dienstleistungen"/>
 *             &lt;enumeration value="Angebotene Waren"/>
 *             &lt;enumeration value="Art"/>
 *             &lt;enumeration value="Atmosphere"/>
 *             &lt;enumeration value="Befehlshaber"/>
 *             &lt;enumeration value="Bekannte Angeh�rige"/>
 *             &lt;enumeration value="Bekannte Mitglieder"/>
 *             &lt;enumeration value="Bild"/>
 *             &lt;enumeration value="Bosse"/>
 *             &lt;enumeration value="Climate"/>
 *             &lt;enumeration value="Datum"/>
 *             &lt;enumeration value="Entscheider"/>
 *             &lt;enumeration value="Ethische Identit�t"/>
 *             &lt;enumeration value="Familienoberh�upter"/>
 *             &lt;enumeration value="Flagge / Symbole"/>
 *             &lt;enumeration value="Genre"/>
 *             &lt;enumeration value="Genre leicht anpassbar auf..."/>
 *             &lt;enumeration value="Gewicht"/>
 *             &lt;enumeration value="Glaubensobere"/>
 *             &lt;enumeration value="Glaubenszentren"/>
 *             &lt;enumeration value="Glaubenszentrum"/>
 *             &lt;enumeration value="Gravity"/>
 *             &lt;enumeration value="Haupt Rasse / Ethnie"/>
 *             &lt;enumeration value="Hauptquartier"/>
 *             &lt;enumeration value="Heraldik"/>
 *             &lt;enumeration value="Ikonographie"/>
 *             &lt;enumeration value="Karte"/>
 *             &lt;enumeration value="Klima"/>
 *             &lt;enumeration value="Logo"/>
 *             &lt;enumeration value="Milit�rische Organisation"/>
 *             &lt;enumeration value="Milit�rische Rolle"/>
 *             &lt;enumeration value="Orbit"/>
 *             &lt;enumeration value="Organisation"/>
 *             &lt;enumeration value="Organisationsstruktur"/>
 *             &lt;enumeration value="Picture"/>
 *             &lt;enumeration value="Plot Foku"/>
 *             &lt;enumeration value="Plot Struktur"/>
 *             &lt;enumeration value="Portrait"/>
 *             &lt;enumeration value="Preis"/>
 *             &lt;enumeration value="Preise"/>
 *             &lt;enumeration value="Prominente Mitglieder"/>
 *             &lt;enumeration value="Qualit�t"/>
 *             &lt;enumeration value="Rang"/>
 *             &lt;enumeration value="Regierung"/>
 *             &lt;enumeration value="Region"/>
 *             &lt;enumeration value="Religion"/>
 *             &lt;enumeration value="Religi�se Organisation"/>
 *             &lt;enumeration value="Rolle in der Regierung"/>
 *             &lt;enumeration value="Schl�sselbegriffe"/>
 *             &lt;enumeration value="Schl�sselworte"/>
 *             &lt;enumeration value="Sitz der Macht"/>
 *             &lt;enumeration value="Spitzenpolitiker"/>
 *             &lt;enumeration value="Sprachen"/>
 *             &lt;enumeration value="Stammsitz"/>
 *             &lt;enumeration value="Stimmung"/>
 *             &lt;enumeration value="Stimmung leicht anpassbar auf..."/>
 *             &lt;enumeration value="St�tzpunkt"/>
 *             &lt;enumeration value="Szene"/>
 *             &lt;enumeration value="Terrain"/>
 *             &lt;enumeration value="Terrain Type"/>
 *             &lt;enumeration value="Transportmittel"/>
 *             &lt;enumeration value="Type"/>
 *             &lt;enumeration value="Verwendung"/>
 *             &lt;enumeration value="Wappen"/>
 *             &lt;enumeration value="Wichtigkeit"/>
 *             &lt;enumeration value="Zeitspanne"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Game"/>
 *             &lt;enumeration value="Date_Range"/>
 *             &lt;enumeration value="Hybrid_Tag"/>
 *             &lt;enumeration value="Labeled_Text"/>
 *             &lt;enumeration value="Picture"/>
 *             &lt;enumeration value="Smart_Image"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="label_style" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Normal"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="thumbnail_size">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="144x144"/>
 *             &lt;enumeration value="288x288"/>
 *             &lt;enumeration value="384x384"/>
 *             &lt;enumeration value="96x96"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="global_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="0205287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0305287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0405287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="08B1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="09B1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0AB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0BB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0CB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0D05287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0DB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0E05287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0EB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="0F05287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="0FB1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="10B1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="1205287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="1305287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="13B1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="1405287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="14B1297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="1505287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="1605287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="1805287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2F00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5101287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="5201287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6705287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6805287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7A082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7B082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7C082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7D082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8701287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8708287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8801287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8A08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9708287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9808287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9908287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9A08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9B08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9C08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9D08287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9F1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A01E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A11E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A21E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A31E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A41E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A51E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A61E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A71E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A81E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A91E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AA1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AB1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AC1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AE04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="B604287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BA1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BC1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BD1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BE1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BF1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C01E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C11E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C21E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C31E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C41E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C91E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CA1E2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D007287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D107287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DA00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DB00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DC00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DD00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DE00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DE04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DF04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E004287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E104287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E204287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E304287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E504287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EC04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ED01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ED04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EE01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EE04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EF01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EF04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F001287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F004287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F101287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F201287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="F204287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="FD04287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="FE06287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="is_lock_domain" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="is_multi_tag" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="domain_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Domain_15"/>
 *             &lt;enumeration value="Domain_18"/>
 *             &lt;enumeration value="Domain_2"/>
 *             &lt;enumeration value="Domain_21"/>
 *             &lt;enumeration value="Domain_23"/>
 *             &lt;enumeration value="Domain_24"/>
 *             &lt;enumeration value="Domain_25"/>
 *             &lt;enumeration value="Domain_26"/>
 *             &lt;enumeration value="Domain_27"/>
 *             &lt;enumeration value="Domain_28"/>
 *             &lt;enumeration value="Domain_29"/>
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
 *             &lt;enumeration value="Domain_43"/>
 *             &lt;enumeration value="Domain_44"/>
 *             &lt;enumeration value="Domain_45"/>
 *             &lt;enumeration value="Domain_46"/>
 *             &lt;enumeration value="Domain_47"/>
 *             &lt;enumeration value="Domain_48"/>
 *             &lt;enumeration value="Domain_50"/>
 *             &lt;enumeration value="Domain_53"/>
 *             &lt;enumeration value="Domain_54"/>
 *             &lt;enumeration value="Domain_7"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="tag_id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Tag_620"/>
 *             &lt;enumeration value="Tag_701"/>
 *             &lt;enumeration value="Tag_702"/>
 *             &lt;enumeration value="Tag_704"/>
 *             &lt;enumeration value="Tag_707"/>
 *             &lt;enumeration value="Tag_710"/>
 *             &lt;enumeration value="Tag_717"/>
 *             &lt;enumeration value="Tag_724"/>
 *             &lt;enumeration value="Tag_854"/>
 *             &lt;enumeration value="Tag_939"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="date_format">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Date_Only_Short"/>
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
    "textOverride"
})
@XmlRootElement(name = "facet_global")
public class FacetGlobal {

    @XmlElement(name = "text_override")
    protected List<TextOverride> textOverride;
    @XmlAttribute(name = "facet_id", required = true)
    protected String facetId;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "label_style", required = true)
    protected String labelStyle;
    @XmlAttribute(name = "thumbnail_size")
    protected String thumbnailSize;
    @XmlAttribute(name = "global_uuid", required = true)
    protected String globalUuid;
    @XmlAttribute(name = "is_lock_domain")
    protected Boolean isLockDomain;
    @XmlAttribute(name = "is_multi_tag")
    protected Boolean isMultiTag;
    @XmlAttribute(name = "domain_id")
    protected String domainId;
    @XmlAttribute(name = "tag_id")
    protected String tagId;
    @XmlAttribute(name = "date_format")
    protected String dateFormat;

    /**
     * Gets the value of the textOverride property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the textOverride property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTextOverride().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TextOverride }
     */
    public List<TextOverride> getTextOverride() {
        if (textOverride == null) {
            textOverride = new ArrayList<TextOverride>();
        }
        return this.textOverride;
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
     * Ruft den Wert der labelStyle-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getLabelStyle() {
        return labelStyle;
    }

    /**
     * Legt den Wert der labelStyle-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setLabelStyle(String value) {
        this.labelStyle = value;
    }

    /**
     * Ruft den Wert der thumbnailSize-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getThumbnailSize() {
        return thumbnailSize;
    }

    /**
     * Legt den Wert der thumbnailSize-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setThumbnailSize(String value) {
        this.thumbnailSize = value;
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

    /**
     * Ruft den Wert der isLockDomain-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isIsLockDomain() {
        return isLockDomain;
    }

    /**
     * Legt den Wert der isLockDomain-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsLockDomain(Boolean value) {
        this.isLockDomain = value;
    }

    /**
     * Ruft den Wert der isMultiTag-Eigenschaft ab.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public Boolean isIsMultiTag() {
        return isMultiTag;
    }

    /**
     * Legt den Wert der isMultiTag-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsMultiTag(Boolean value) {
        this.isMultiTag = value;
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
     * Ruft den Wert der dateFormat-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Legt den Wert der dateFormat-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

}
