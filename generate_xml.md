# Generieren der XML Elemente

Bei einer Änderung in einer der XSDs müssen auch entsprechend die generierten Objekte neu erzeugt werden. Es folgt eine Auflistung der XSDs und der
dazugehörigen Befehle, welche im Wurzelverzeichnis des Projekts ausgeführt werden müssen:

**XSD** | **Command-Line** | **Beschreibung**
---|---|---
[topic-mapping.xsd](./src/main/resources/topic-mapping.xsd) | ```xjc -d src/main/java -p de.pho.dsapdfreader.config.generated.topicstrategymapping ./src/main/resources/topic-mapping.xsd``` | Beschreibt Strategien, welche auf die Konvertierung dieser Konfiguration angewendet werden können
