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
 *       &lt;choice minOccurs="0">
 *         &lt;sequence>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}facet_global" maxOccurs="unbounded"/>
 *           &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;element ref="{urn:lonewolfdevel.com:realm-works-export}text_override" maxOccurs="unbounded"/>
 *       &lt;/choice>
 *       &lt;attribute name="partition_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Partition_1"/>
 *             &lt;enumeration value="Partition_100"/>
 *             &lt;enumeration value="Partition_101"/>
 *             &lt;enumeration value="Partition_102"/>
 *             &lt;enumeration value="Partition_103"/>
 *             &lt;enumeration value="Partition_104"/>
 *             &lt;enumeration value="Partition_105"/>
 *             &lt;enumeration value="Partition_106"/>
 *             &lt;enumeration value="Partition_107"/>
 *             &lt;enumeration value="Partition_108"/>
 *             &lt;enumeration value="Partition_109"/>
 *             &lt;enumeration value="Partition_110"/>
 *             &lt;enumeration value="Partition_111"/>
 *             &lt;enumeration value="Partition_112"/>
 *             &lt;enumeration value="Partition_113"/>
 *             &lt;enumeration value="Partition_114"/>
 *             &lt;enumeration value="Partition_115"/>
 *             &lt;enumeration value="Partition_12"/>
 *             &lt;enumeration value="Partition_123"/>
 *             &lt;enumeration value="Partition_124"/>
 *             &lt;enumeration value="Partition_125"/>
 *             &lt;enumeration value="Partition_126"/>
 *             &lt;enumeration value="Partition_127"/>
 *             &lt;enumeration value="Partition_128"/>
 *             &lt;enumeration value="Partition_129"/>
 *             &lt;enumeration value="Partition_13"/>
 *             &lt;enumeration value="Partition_130"/>
 *             &lt;enumeration value="Partition_131"/>
 *             &lt;enumeration value="Partition_132"/>
 *             &lt;enumeration value="Partition_133"/>
 *             &lt;enumeration value="Partition_134"/>
 *             &lt;enumeration value="Partition_137"/>
 *             &lt;enumeration value="Partition_138"/>
 *             &lt;enumeration value="Partition_139"/>
 *             &lt;enumeration value="Partition_14"/>
 *             &lt;enumeration value="Partition_140"/>
 *             &lt;enumeration value="Partition_141"/>
 *             &lt;enumeration value="Partition_142"/>
 *             &lt;enumeration value="Partition_143"/>
 *             &lt;enumeration value="Partition_144"/>
 *             &lt;enumeration value="Partition_145"/>
 *             &lt;enumeration value="Partition_146"/>
 *             &lt;enumeration value="Partition_147"/>
 *             &lt;enumeration value="Partition_148"/>
 *             &lt;enumeration value="Partition_149"/>
 *             &lt;enumeration value="Partition_15"/>
 *             &lt;enumeration value="Partition_150"/>
 *             &lt;enumeration value="Partition_151"/>
 *             &lt;enumeration value="Partition_152"/>
 *             &lt;enumeration value="Partition_153"/>
 *             &lt;enumeration value="Partition_154"/>
 *             &lt;enumeration value="Partition_155"/>
 *             &lt;enumeration value="Partition_156"/>
 *             &lt;enumeration value="Partition_16"/>
 *             &lt;enumeration value="Partition_17"/>
 *             &lt;enumeration value="Partition_18"/>
 *             &lt;enumeration value="Partition_19"/>
 *             &lt;enumeration value="Partition_2"/>
 *             &lt;enumeration value="Partition_20"/>
 *             &lt;enumeration value="Partition_21"/>
 *             &lt;enumeration value="Partition_22"/>
 *             &lt;enumeration value="Partition_23"/>
 *             &lt;enumeration value="Partition_24"/>
 *             &lt;enumeration value="Partition_25"/>
 *             &lt;enumeration value="Partition_256"/>
 *             &lt;enumeration value="Partition_257"/>
 *             &lt;enumeration value="Partition_258"/>
 *             &lt;enumeration value="Partition_259"/>
 *             &lt;enumeration value="Partition_26"/>
 *             &lt;enumeration value="Partition_260"/>
 *             &lt;enumeration value="Partition_261"/>
 *             &lt;enumeration value="Partition_262"/>
 *             &lt;enumeration value="Partition_263"/>
 *             &lt;enumeration value="Partition_264"/>
 *             &lt;enumeration value="Partition_265"/>
 *             &lt;enumeration value="Partition_266"/>
 *             &lt;enumeration value="Partition_267"/>
 *             &lt;enumeration value="Partition_268"/>
 *             &lt;enumeration value="Partition_269"/>
 *             &lt;enumeration value="Partition_27"/>
 *             &lt;enumeration value="Partition_270"/>
 *             &lt;enumeration value="Partition_271"/>
 *             &lt;enumeration value="Partition_272"/>
 *             &lt;enumeration value="Partition_273"/>
 *             &lt;enumeration value="Partition_28"/>
 *             &lt;enumeration value="Partition_283"/>
 *             &lt;enumeration value="Partition_284"/>
 *             &lt;enumeration value="Partition_285"/>
 *             &lt;enumeration value="Partition_286"/>
 *             &lt;enumeration value="Partition_287"/>
 *             &lt;enumeration value="Partition_288"/>
 *             &lt;enumeration value="Partition_289"/>
 *             &lt;enumeration value="Partition_29"/>
 *             &lt;enumeration value="Partition_290"/>
 *             &lt;enumeration value="Partition_291"/>
 *             &lt;enumeration value="Partition_292"/>
 *             &lt;enumeration value="Partition_293"/>
 *             &lt;enumeration value="Partition_294"/>
 *             &lt;enumeration value="Partition_295"/>
 *             &lt;enumeration value="Partition_296"/>
 *             &lt;enumeration value="Partition_297"/>
 *             &lt;enumeration value="Partition_298"/>
 *             &lt;enumeration value="Partition_299"/>
 *             &lt;enumeration value="Partition_3"/>
 *             &lt;enumeration value="Partition_30"/>
 *             &lt;enumeration value="Partition_300"/>
 *             &lt;enumeration value="Partition_302"/>
 *             &lt;enumeration value="Partition_303"/>
 *             &lt;enumeration value="Partition_304"/>
 *             &lt;enumeration value="Partition_305"/>
 *             &lt;enumeration value="Partition_306"/>
 *             &lt;enumeration value="Partition_307"/>
 *             &lt;enumeration value="Partition_308"/>
 *             &lt;enumeration value="Partition_309"/>
 *             &lt;enumeration value="Partition_31"/>
 *             &lt;enumeration value="Partition_310"/>
 *             &lt;enumeration value="Partition_311"/>
 *             &lt;enumeration value="Partition_312"/>
 *             &lt;enumeration value="Partition_313"/>
 *             &lt;enumeration value="Partition_314"/>
 *             &lt;enumeration value="Partition_315"/>
 *             &lt;enumeration value="Partition_316"/>
 *             &lt;enumeration value="Partition_317"/>
 *             &lt;enumeration value="Partition_32"/>
 *             &lt;enumeration value="Partition_33"/>
 *             &lt;enumeration value="Partition_34"/>
 *             &lt;enumeration value="Partition_35"/>
 *             &lt;enumeration value="Partition_350"/>
 *             &lt;enumeration value="Partition_351"/>
 *             &lt;enumeration value="Partition_352"/>
 *             &lt;enumeration value="Partition_353"/>
 *             &lt;enumeration value="Partition_354"/>
 *             &lt;enumeration value="Partition_355"/>
 *             &lt;enumeration value="Partition_356"/>
 *             &lt;enumeration value="Partition_357"/>
 *             &lt;enumeration value="Partition_358"/>
 *             &lt;enumeration value="Partition_359"/>
 *             &lt;enumeration value="Partition_36"/>
 *             &lt;enumeration value="Partition_360"/>
 *             &lt;enumeration value="Partition_361"/>
 *             &lt;enumeration value="Partition_362"/>
 *             &lt;enumeration value="Partition_363"/>
 *             &lt;enumeration value="Partition_364"/>
 *             &lt;enumeration value="Partition_365"/>
 *             &lt;enumeration value="Partition_366"/>
 *             &lt;enumeration value="Partition_367"/>
 *             &lt;enumeration value="Partition_368"/>
 *             &lt;enumeration value="Partition_369"/>
 *             &lt;enumeration value="Partition_37"/>
 *             &lt;enumeration value="Partition_370"/>
 *             &lt;enumeration value="Partition_371"/>
 *             &lt;enumeration value="Partition_372"/>
 *             &lt;enumeration value="Partition_373"/>
 *             &lt;enumeration value="Partition_374"/>
 *             &lt;enumeration value="Partition_375"/>
 *             &lt;enumeration value="Partition_376"/>
 *             &lt;enumeration value="Partition_377"/>
 *             &lt;enumeration value="Partition_378"/>
 *             &lt;enumeration value="Partition_379"/>
 *             &lt;enumeration value="Partition_38"/>
 *             &lt;enumeration value="Partition_380"/>
 *             &lt;enumeration value="Partition_381"/>
 *             &lt;enumeration value="Partition_382"/>
 *             &lt;enumeration value="Partition_383"/>
 *             &lt;enumeration value="Partition_384"/>
 *             &lt;enumeration value="Partition_385"/>
 *             &lt;enumeration value="Partition_386"/>
 *             &lt;enumeration value="Partition_387"/>
 *             &lt;enumeration value="Partition_388"/>
 *             &lt;enumeration value="Partition_389"/>
 *             &lt;enumeration value="Partition_39"/>
 *             &lt;enumeration value="Partition_390"/>
 *             &lt;enumeration value="Partition_391"/>
 *             &lt;enumeration value="Partition_392"/>
 *             &lt;enumeration value="Partition_393"/>
 *             &lt;enumeration value="Partition_394"/>
 *             &lt;enumeration value="Partition_395"/>
 *             &lt;enumeration value="Partition_396"/>
 *             &lt;enumeration value="Partition_397"/>
 *             &lt;enumeration value="Partition_398"/>
 *             &lt;enumeration value="Partition_399"/>
 *             &lt;enumeration value="Partition_4"/>
 *             &lt;enumeration value="Partition_40"/>
 *             &lt;enumeration value="Partition_400"/>
 *             &lt;enumeration value="Partition_401"/>
 *             &lt;enumeration value="Partition_402"/>
 *             &lt;enumeration value="Partition_403"/>
 *             &lt;enumeration value="Partition_404"/>
 *             &lt;enumeration value="Partition_405"/>
 *             &lt;enumeration value="Partition_406"/>
 *             &lt;enumeration value="Partition_407"/>
 *             &lt;enumeration value="Partition_408"/>
 *             &lt;enumeration value="Partition_409"/>
 *             &lt;enumeration value="Partition_41"/>
 *             &lt;enumeration value="Partition_410"/>
 *             &lt;enumeration value="Partition_411"/>
 *             &lt;enumeration value="Partition_412"/>
 *             &lt;enumeration value="Partition_413"/>
 *             &lt;enumeration value="Partition_414"/>
 *             &lt;enumeration value="Partition_415"/>
 *             &lt;enumeration value="Partition_416"/>
 *             &lt;enumeration value="Partition_417"/>
 *             &lt;enumeration value="Partition_418"/>
 *             &lt;enumeration value="Partition_419"/>
 *             &lt;enumeration value="Partition_42"/>
 *             &lt;enumeration value="Partition_420"/>
 *             &lt;enumeration value="Partition_421"/>
 *             &lt;enumeration value="Partition_43"/>
 *             &lt;enumeration value="Partition_433"/>
 *             &lt;enumeration value="Partition_434"/>
 *             &lt;enumeration value="Partition_435"/>
 *             &lt;enumeration value="Partition_436"/>
 *             &lt;enumeration value="Partition_437"/>
 *             &lt;enumeration value="Partition_438"/>
 *             &lt;enumeration value="Partition_439"/>
 *             &lt;enumeration value="Partition_44"/>
 *             &lt;enumeration value="Partition_45"/>
 *             &lt;enumeration value="Partition_46"/>
 *             &lt;enumeration value="Partition_47"/>
 *             &lt;enumeration value="Partition_471"/>
 *             &lt;enumeration value="Partition_472"/>
 *             &lt;enumeration value="Partition_473"/>
 *             &lt;enumeration value="Partition_474"/>
 *             &lt;enumeration value="Partition_475"/>
 *             &lt;enumeration value="Partition_476"/>
 *             &lt;enumeration value="Partition_477"/>
 *             &lt;enumeration value="Partition_478"/>
 *             &lt;enumeration value="Partition_479"/>
 *             &lt;enumeration value="Partition_48"/>
 *             &lt;enumeration value="Partition_480"/>
 *             &lt;enumeration value="Partition_481"/>
 *             &lt;enumeration value="Partition_482"/>
 *             &lt;enumeration value="Partition_49"/>
 *             &lt;enumeration value="Partition_5"/>
 *             &lt;enumeration value="Partition_50"/>
 *             &lt;enumeration value="Partition_500"/>
 *             &lt;enumeration value="Partition_501"/>
 *             &lt;enumeration value="Partition_502"/>
 *             &lt;enumeration value="Partition_503"/>
 *             &lt;enumeration value="Partition_504"/>
 *             &lt;enumeration value="Partition_505"/>
 *             &lt;enumeration value="Partition_506"/>
 *             &lt;enumeration value="Partition_507"/>
 *             &lt;enumeration value="Partition_508"/>
 *             &lt;enumeration value="Partition_509"/>
 *             &lt;enumeration value="Partition_51"/>
 *             &lt;enumeration value="Partition_510"/>
 *             &lt;enumeration value="Partition_511"/>
 *             &lt;enumeration value="Partition_512"/>
 *             &lt;enumeration value="Partition_513"/>
 *             &lt;enumeration value="Partition_514"/>
 *             &lt;enumeration value="Partition_515"/>
 *             &lt;enumeration value="Partition_516"/>
 *             &lt;enumeration value="Partition_517"/>
 *             &lt;enumeration value="Partition_518"/>
 *             &lt;enumeration value="Partition_519"/>
 *             &lt;enumeration value="Partition_52"/>
 *             &lt;enumeration value="Partition_520"/>
 *             &lt;enumeration value="Partition_521"/>
 *             &lt;enumeration value="Partition_522"/>
 *             &lt;enumeration value="Partition_523"/>
 *             &lt;enumeration value="Partition_524"/>
 *             &lt;enumeration value="Partition_525"/>
 *             &lt;enumeration value="Partition_526"/>
 *             &lt;enumeration value="Partition_527"/>
 *             &lt;enumeration value="Partition_528"/>
 *             &lt;enumeration value="Partition_529"/>
 *             &lt;enumeration value="Partition_53"/>
 *             &lt;enumeration value="Partition_530"/>
 *             &lt;enumeration value="Partition_531"/>
 *             &lt;enumeration value="Partition_532"/>
 *             &lt;enumeration value="Partition_533"/>
 *             &lt;enumeration value="Partition_534"/>
 *             &lt;enumeration value="Partition_535"/>
 *             &lt;enumeration value="Partition_536"/>
 *             &lt;enumeration value="Partition_537"/>
 *             &lt;enumeration value="Partition_538"/>
 *             &lt;enumeration value="Partition_539"/>
 *             &lt;enumeration value="Partition_54"/>
 *             &lt;enumeration value="Partition_540"/>
 *             &lt;enumeration value="Partition_541"/>
 *             &lt;enumeration value="Partition_542"/>
 *             &lt;enumeration value="Partition_543"/>
 *             &lt;enumeration value="Partition_544"/>
 *             &lt;enumeration value="Partition_545"/>
 *             &lt;enumeration value="Partition_546"/>
 *             &lt;enumeration value="Partition_547"/>
 *             &lt;enumeration value="Partition_548"/>
 *             &lt;enumeration value="Partition_549"/>
 *             &lt;enumeration value="Partition_55"/>
 *             &lt;enumeration value="Partition_550"/>
 *             &lt;enumeration value="Partition_56"/>
 *             &lt;enumeration value="Partition_561"/>
 *             &lt;enumeration value="Partition_562"/>
 *             &lt;enumeration value="Partition_563"/>
 *             &lt;enumeration value="Partition_564"/>
 *             &lt;enumeration value="Partition_565"/>
 *             &lt;enumeration value="Partition_566"/>
 *             &lt;enumeration value="Partition_567"/>
 *             &lt;enumeration value="Partition_568"/>
 *             &lt;enumeration value="Partition_569"/>
 *             &lt;enumeration value="Partition_57"/>
 *             &lt;enumeration value="Partition_570"/>
 *             &lt;enumeration value="Partition_571"/>
 *             &lt;enumeration value="Partition_572"/>
 *             &lt;enumeration value="Partition_573"/>
 *             &lt;enumeration value="Partition_574"/>
 *             &lt;enumeration value="Partition_575"/>
 *             &lt;enumeration value="Partition_576"/>
 *             &lt;enumeration value="Partition_577"/>
 *             &lt;enumeration value="Partition_578"/>
 *             &lt;enumeration value="Partition_58"/>
 *             &lt;enumeration value="Partition_59"/>
 *             &lt;enumeration value="Partition_6"/>
 *             &lt;enumeration value="Partition_60"/>
 *             &lt;enumeration value="Partition_61"/>
 *             &lt;enumeration value="Partition_62"/>
 *             &lt;enumeration value="Partition_63"/>
 *             &lt;enumeration value="Partition_64"/>
 *             &lt;enumeration value="Partition_65"/>
 *             &lt;enumeration value="Partition_66"/>
 *             &lt;enumeration value="Partition_67"/>
 *             &lt;enumeration value="Partition_68"/>
 *             &lt;enumeration value="Partition_69"/>
 *             &lt;enumeration value="Partition_70"/>
 *             &lt;enumeration value="Partition_71"/>
 *             &lt;enumeration value="Partition_72"/>
 *             &lt;enumeration value="Partition_73"/>
 *             &lt;enumeration value="Partition_74"/>
 *             &lt;enumeration value="Partition_75"/>
 *             &lt;enumeration value="Partition_76"/>
 *             &lt;enumeration value="Partition_77"/>
 *             &lt;enumeration value="Partition_78"/>
 *             &lt;enumeration value="Partition_79"/>
 *             &lt;enumeration value="Partition_80"/>
 *             &lt;enumeration value="Partition_81"/>
 *             &lt;enumeration value="Partition_82"/>
 *             &lt;enumeration value="Partition_83"/>
 *             &lt;enumeration value="Partition_84"/>
 *             &lt;enumeration value="Partition_85"/>
 *             &lt;enumeration value="Partition_86"/>
 *             &lt;enumeration value="Partition_87"/>
 *             &lt;enumeration value="Partition_88"/>
 *             &lt;enumeration value="Partition_89"/>
 *             &lt;enumeration value="Partition_90"/>
 *             &lt;enumeration value="Partition_91"/>
 *             &lt;enumeration value="Partition_92"/>
 *             &lt;enumeration value="Partition_93"/>
 *             &lt;enumeration value="Partition_94"/>
 *             &lt;enumeration value="Partition_95"/>
 *             &lt;enumeration value="Partition_96"/>
 *             &lt;enumeration value="Partition_97"/>
 *             &lt;enumeration value="Partition_98"/>
 *             &lt;enumeration value="Partition_99"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Additional Details"/>
 *             &lt;enumeration value="Aufbau"/>
 *             &lt;enumeration value="Auswirkung"/>
 *             &lt;enumeration value="Auswirkungen"/>
 *             &lt;enumeration value="Background"/>
 *             &lt;enumeration value="Bedingungen"/>
 *             &lt;enumeration value="Befugnis und Vorteile"/>
 *             &lt;enumeration value="Belohnung"/>
 *             &lt;enumeration value="Beschreibung"/>
 *             &lt;enumeration value="Besonderheiten"/>
 *             &lt;enumeration value="Bestandteile"/>
 *             &lt;enumeration value="Betrieb"/>
 *             &lt;enumeration value="Beziehungen"/>
 *             &lt;enumeration value="Challenges"/>
 *             &lt;enumeration value="Das Abenteuer abschliessen"/>
 *             &lt;enumeration value="Description"/>
 *             &lt;enumeration value="Details"/>
 *             &lt;enumeration value="Eigenschaften"/>
 *             &lt;enumeration value="Einheiten und Ressourcen"/>
 *             &lt;enumeration value="Entwicklung"/>
 *             &lt;enumeration value="Ergebnisse"/>
 *             &lt;enumeration value="F�higkeiten"/>
 *             &lt;enumeration value="Gallerie"/>
 *             &lt;enumeration value="Gallery"/>
 *             &lt;enumeration value="Gegens�tze"/>
 *             &lt;enumeration value="Gegner"/>
 *             &lt;enumeration value="Geschichte"/>
 *             &lt;enumeration value="Glaube und �berzeugungen"/>
 *             &lt;enumeration value="Gleichgesinnte"/>
 *             &lt;enumeration value="Habitat"/>
 *             &lt;enumeration value="Herausforderungen"/>
 *             &lt;enumeration value="Herstellung"/>
 *             &lt;enumeration value="Hindernisse"/>
 *             &lt;enumeration value="Hintergrund"/>
 *             &lt;enumeration value="Hintergr�nde"/>
 *             &lt;enumeration value="Hook"/>
 *             &lt;enumeration value="Inhaltsbeschreibung"/>
 *             &lt;enumeration value="Integraion in Kampagne"/>
 *             &lt;enumeration value="Internal Relationships"/>
 *             &lt;enumeration value="Macht und Pflichten"/>
 *             &lt;enumeration value="Merkmale"/>
 *             &lt;enumeration value="Methoden"/>
 *             &lt;enumeration value="Motivation und Philosophie"/>
 *             &lt;enumeration value="Nachteile und Einbu�en"/>
 *             &lt;enumeration value="Niedergang"/>
 *             &lt;enumeration value="Notable NPCs"/>
 *             &lt;enumeration value="Obstacles"/>
 *             &lt;enumeration value="Organisation"/>
 *             &lt;enumeration value="Ort"/>
 *             &lt;enumeration value="Outward Relationships"/>
 *             &lt;enumeration value="Overview"/>
 *             &lt;enumeration value="Pflichten und Verantwortung"/>
 *             &lt;enumeration value="Philosophie"/>
 *             &lt;enumeration value="Points of Interest"/>
 *             &lt;enumeration value="Profil"/>
 *             &lt;enumeration value="Profile"/>
 *             &lt;enumeration value="Resourcen"/>
 *             &lt;enumeration value="Schl�ssel-Personen, -Orte, und -Gegenst�nde"/>
 *             &lt;enumeration value="Sehensw�rdigkeiten"/>
 *             &lt;enumeration value="Story"/>
 *             &lt;enumeration value="Storyline Elemente"/>
 *             &lt;enumeration value="Struktur"/>
 *             &lt;enumeration value="Szene umrissen"/>
 *             &lt;enumeration value="Taktik"/>
 *             &lt;enumeration value="Transport"/>
 *             &lt;enumeration value="Unterst�tzer"/>
 *             &lt;enumeration value="Ursache"/>
 *             &lt;enumeration value="Ursache und Wirkung"/>
 *             &lt;enumeration value="Ursprung"/>
 *             &lt;enumeration value="Valuables"/>
 *             &lt;enumeration value="Verbindungen"/>
 *             &lt;enumeration value="Vermeidung und Abmildern"/>
 *             &lt;enumeration value="Vermeidung und Minderung"/>
 *             &lt;enumeration value="Verwendung dieser Idee"/>
 *             &lt;enumeration value="Vorbeugung und Heilung"/>
 *             &lt;enumeration value="Vorgehe"/>
 *             &lt;enumeration value="Vorgehen"/>
 *             &lt;enumeration value="Vorraussetzungen"/>
 *             &lt;enumeration value="Vorraussetzungen und Einschr�nkungen"/>
 *             &lt;enumeration value="Vourraussetzungen und Einschr�nkungen"/>
 *             &lt;enumeration value="Waren und Dienstleistungen"/>
 *             &lt;enumeration value="Was man so h�rt"/>
 *             &lt;enumeration value="Werte"/>
 *             &lt;enumeration value="Wertvolles"/>
 *             &lt;enumeration value="Wichtige NSCs"/>
 *             &lt;enumeration value="Wichtige Orte"/>
 *             &lt;enumeration value="Wichtige Plotelemente"/>
 *             &lt;enumeration value="Wichtige Protagonisten"/>
 *             &lt;enumeration value="Wie Ausr�sten"/>
 *             &lt;enumeration value="Woher bekommen"/>
 *             &lt;enumeration value="Zusammenh�nge"/>
 *             &lt;enumeration value="Zus�tzliche Details"/>
 *             &lt;enumeration value="Zus�tzliche Informationen"/>
 *             &lt;enumeration value="Zus�tzliches Material"/>
 *             &lt;enumeration value="�bersicht"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="global_uuid" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="2100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="2600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3001287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="3401287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="34B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="35B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="36B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="37B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="38B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="39B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="3EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="4201287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4301287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4401287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="4B01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="56B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="57B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="58B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="59B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="5FB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="60B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="61B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="62B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="63B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="64B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="69B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6B01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6C01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6D01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6E01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="6F01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="6FB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7001287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="70B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="71082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="71B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="72082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="72B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="73082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="74082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="75082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="76082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="77082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="77B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7801287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="78082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="78B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="79082A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="79B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7A00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7B01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7C01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="7EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="7F00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8004287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8301287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="83B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8401287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8408287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="84B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8501287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="85B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8601287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="86B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="87B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="88B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="89B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8A00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="8F00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="8FB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="90B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9108287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="91B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9208287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="92B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9308287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="93B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9408287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="94B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9508287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="95B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9608287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="96B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="97B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="98B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="99B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9A00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9AB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9B00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9BB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9C00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9CB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9D00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9DB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9E00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9EB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="9F00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="9FB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A0B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A1B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A2B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A308287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A3B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A4B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A5B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A6B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A7B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A8B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="A900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="A9B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AA00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="AAB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AB00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ABB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AC00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ACB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AD00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ADB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AE00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="AEB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="AF00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="AFB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B0B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B1B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B2B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B3B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B4B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B5B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B601287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="B61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B6B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B7B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B81C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B8B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B91C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="B9B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BA1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BAB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BB1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BBB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BC1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BCB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BD00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BD1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BDB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BE00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BE1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BEB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BF00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="BF1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="BFB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C01C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C0B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C11C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C1B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C21C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C2B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C31C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C3B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C41C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C4B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C5B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C6B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C7B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C800287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C81C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C8B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C900287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="C91C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="C9B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CA00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CA1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CAB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CB00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CB1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CBB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CC00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CC1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CCB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CD00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CD1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CDB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CE00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CE1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CEB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CF00287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="CF1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="CFB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D000287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D01C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D0B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D100287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D11C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D1B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D200287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D21C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D2B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D300287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D31C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D3B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D400287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D41C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D4B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D500287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D5B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D600287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D6B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D700287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="D71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D7B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D81C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D8B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D91C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="D9B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DA1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DB1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DC01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DC1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DD01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DD1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DE01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DE1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="DF01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="DF1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E001287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E01C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E101287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E11C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E201287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E21C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E301287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E31C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E41C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E4B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E501287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E5B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E601287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E6B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E701287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E7B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E801287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E8B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="E901287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="E9B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EA01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="EAB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EBB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EC01287B-91C6-5D3A-00D0-6A935C3C6BA9"/>
 *             &lt;enumeration value="ECB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EDB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EEB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="EFB0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F0B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F1B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F2B0297B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F31C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F41C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F51C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F61C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F71C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F81C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="F91C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
 *             &lt;enumeration value="FA1C2A7B-91C6-5D3A-00D0-6A935D3C6BA9"/>
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
@XmlRootElement(name = "partition_global")
public class PartitionGlobal {

  @XmlElementRefs({
      @XmlElementRef(name = "text_override", namespace = "urn:lonewolfdevel.com:realm-works-export", type = TextOverride.class, required = false),
      @XmlElementRef(name = "facet_global", namespace = "urn:lonewolfdevel.com:realm-works-export", type = FacetGlobal.class, required = false)
  })
  protected List<Object> content;
  @XmlAttribute(name = "partition_id", required = true)
  protected String partitionId;
  @XmlAttribute(name = "name", required = true)
  protected String name;
  @XmlAttribute(name = "global_uuid", required = true)
  protected String globalUuid;

  /**
   * Ruft das restliche Contentmodell ab.
   *
   * <p>
   * Sie rufen diese "catch-all"-Eigenschaft aus folgendem Grund ab:
   * Der Feldname "TextOverride" wird von zwei verschiedenen Teilen eines Schemas verwendet. Siehe:
   * Zeile 2966 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
   * Zeile 2964 von file:/C:/develop/project/dsa-pdf-reader/realmworks/rwexport.xsd
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
   * {@link FacetGlobal }
   * {@link TextOverride }
   */
  public List<Object> getContent() {
    if (content == null) {
      content = new ArrayList<Object>();
    }
    return this.content;
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
