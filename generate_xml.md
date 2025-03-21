# Generieren der XML Elemente

Bei einer �nderung in einer der XSDs m�ssen auch entsprechend die generierten Objekte neu erzeugt werden. Es folgt eine Auflistung der XSDs und der
dazugeh�rigen Befehle, welche im Wurzelverzeichnis des Projekts ausgef�hrt werden m�ssen:

**XSD** | **Command-Line** | **Beschreibung**
---|---|---
[topic-mapping.xsd](./src/main/resources/topic-mapping.xsd) | ```xjc -d src/main/java -p de.pho.dsapdfreader.config.generated.topicstrategymapping -encoding UTF-8 ./src/main/resources/topic-mapping.xsd``` | Beschreibt Strategien, welche auf die Konvertierung dieser Konfiguration angewendet werden k�nnen
