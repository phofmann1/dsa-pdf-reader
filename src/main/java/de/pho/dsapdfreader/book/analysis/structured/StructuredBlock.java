package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ein logischer Block — kann mehrere Linien und mehrere Seiten umfassen.
 * <p>
 * Ein Block beginnt entweder mit einem Block-Marker (z.B.
 * {@code **Wuchtschlag** (passiv)}) oder mit einer Heading-Linie eines
 * Block-Tiers. Er endet, sobald ein neuer Block-Marker oder ein Heading des
 * gleichen oder hoeheren Tiers folgt.
 * <p>
 * Bloecke sind die Einheit, die seitenuebergreifend bestand hat — wenn eine
 * Sonderfertigkeit auf Seite N startet und auf Seite N+1 fortgesetzt wird,
 * sammelt der Block alle zugehoerigen Linien aus beiden Seiten.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StructuredBlock {

    public String id;

    /** Welche Art Block — abgeleitet aus dem fuehrenden Cluster.
     *  "heading" | "block_marker" | "block_marker_italic" */
    public String kind;

    /** Tier des fuehrenden Clusters (siehe {@link de.pho.dsapdfreader.book.analysis.BookFontClusters}). */
    public int tier;

    /** Anzeige-Text — der Inhalt der fuehrenden Linie ohne Type-Marker-Klammer. */
    public String name;

    /** Klammer-Type-Marker, falls vorhanden ("passiv", "aktiv", ...). */
    public String typeMarker;

    /** Erste und letzte Seite, ueber die sich der Block erstreckt. */
    public Integer startPage;
    public Integer endPage;

    /** ID des umschliessenden Hierarchie-Knotens, falls vorhanden. */
    public String parentHierarchyId;

    /** Felder, die im Block-Body als {@code **Key:** Value} erkannt wurden. Multi-line wird zusammengefuehrt. */
    public Map<String, String> fields = new LinkedHashMap<>();

    /** Gesamter Body-Text als ein zusammenhaengender String — fuer Pattern-Suche. */
    public String bodyText;

    /** Alle Linien, die zu diesem Block gehoeren — in Reading-Order, ueber Seiten hinweg. */
    public List<StructuredLine> lines = new ArrayList<>();
}
