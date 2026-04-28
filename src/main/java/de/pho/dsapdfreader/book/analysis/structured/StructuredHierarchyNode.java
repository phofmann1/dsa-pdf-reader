package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Hierarchie-Knoten — entspricht einem Heading mit klassischem
 * Section-Charakter (Heading-Tier, kein Block-Marker).
 * <p>
 * Hierarchie-Knoten bilden die Buch-Struktur (Kapitel → Section → Subsection)
 * und enthalten {@link StructuredBlock}s, die seitenuebergreifend sein duerfen.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StructuredHierarchyNode {

    public String id;

    /** Cluster-Tier des Headings. */
    public int tier;

    /** Heading-Text. */
    public String title;

    /** Erste und letzte Seite des gesamten Knotens (inkl. aller Kinder). */
    public Integer startPage;
    public Integer endPage;

    public List<StructuredHierarchyNode> children = new ArrayList<>();
    public List<StructuredBlock> blocks = new ArrayList<>();

    /** Anzahl der Body-Linien direkt unter diesem Heading (vor Sub-Headings). 0 = nur Inhaltsverzeichnis-Marker. */
    public int bodyLineCount;

    /** Field-Keys die im direkten Body dieses Headings erkannt wurden (fuer Promotion-Check im Fallback). */
    public java.util.Set<String> ownFieldKeys = new java.util.HashSet<>();
}
