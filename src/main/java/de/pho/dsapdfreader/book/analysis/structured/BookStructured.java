package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.pho.dsapdfreader.book.analysis.BookFontClusters;
import de.pho.dsapdfreader.book.analysis.FontStyleKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Buchweites Analyse-Ergebnis — die <i>faktische</i> Schicht. Wird als
 * {@code _structured.json} pro Buch geschrieben und ist die primaere
 * Datenquelle fuer Analyse-Konsumenten (z.B. dsa-data-Importer).
 * <p>
 * Markdown ist eine separate, reduzierte Sicht auf dieselben Daten.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookStructured {

    public String publication;
    public String title;
    public Integer dsaVersion;
    public Integer pages;
    public String generatedAt;
    public String generatorVersion;

    /** Cluster-Map: Tier-Index → Stil-Schluessel + Char-Count. Read-only-Snapshot. */
    public List<ClusterEntry> clusters = new ArrayList<>();

    /** Hierarchie-Baum (Kapitel → Section → ...). */
    public List<StructuredHierarchyNode> hierarchy = new ArrayList<>();

    /** Flache Block-Liste fuer schnellen Zugriff (auch in der Hierarchie referenziert). */
    public List<StructuredBlock> blocks = new ArrayList<>();

    /** Linien-Liste pro Seite — fuer Debugging und visuelle Kontrolle. */
    public Map<String, List<StructuredLine>> linesByPage = new LinkedHashMap<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ClusterEntry {
        public int tier;
        public FontStyleKey style;
        public Long charCount;

        public static ClusterEntry from(int tier, FontStyleKey k, BookFontClusters bc) {
            ClusterEntry e = new ClusterEntry();
            e.tier = tier;
            e.style = k;
            e.charCount = bc.charCount.get(k);
            return e;
        }
    }
}
