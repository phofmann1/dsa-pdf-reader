package de.pho.dsapdfreader.book.analysis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ergebnis der buchweiten Font-Cluster-Analyse.
 * <p>
 * Enthaelt alle distinkten Stil-Cluster, die im Buch vorkommen, mit:
 * <ul>
 *   <li>{@link #bodyKey} — der Cluster, der den Fliesstext stellt (haeufigster Cluster)</li>
 *   <li>{@link #tierByKey} — Tier-Index pro Cluster (0 = Body, 1+ = Heading-Ebenen)</li>
 *   <li>{@link #charCount} — Anzahl Zeichen pro Cluster (Inspektion / Downstream-Filter)</li>
 * </ul>
 * <p>
 * Tier-Vergabe:
 * <ul>
 *   <li>Tier 0 = Body (haeufigster Cluster)</li>
 *   <li>Tier 1..N = alle Cluster groesser als Body, sortiert absteigend nach
 *       Effektiv-Prominenz (Size, dann Bold, dann SmallCaps). Beliebig viele.</li>
 * </ul>
 * <p>
 * Body-Size-Bold und andere kleinere Bold-Cluster bekommen <b>keinen eigenen
 * Tier</b> — das ist im Regelfall Inline-Hervorhebung im Fliesstext. Ob ein
 * Heading-Knoten ein atomarer Block (Sonderfertigkeit etc.) ist, entscheidet
 * der nachgelagerte Aggregator anhand der Pflichtfelder im Body.
 */
public class BookFontClusters {

    public FontStyleKey bodyKey;
    public Map<FontStyleKey, Integer> tierByKey = new LinkedHashMap<>();
    public Map<FontStyleKey, Long> charCount = new LinkedHashMap<>();

    /** Liefert den Tier-Index fuer ein Zeichen. Default: -1 (unbekannter Cluster). */
    public int tierFor(FontStyleKey key) {
        return tierByKey.getOrDefault(key, -1);
    }

    /** Cluster sortiert nach Tier (0 = Body, 1+ aufsteigend Heading-Tiefe), dann nach Char-Count. */
    public List<FontStyleKey> orderedKeys() {
        List<FontStyleKey> all = new ArrayList<>(tierByKey.keySet());
        all.sort(Comparator
                .comparingInt((FontStyleKey k) -> tierByKey.getOrDefault(k, Integer.MAX_VALUE))
                .thenComparingLong(k -> -charCount.getOrDefault(k, 0L)));
        return all;
    }
}
