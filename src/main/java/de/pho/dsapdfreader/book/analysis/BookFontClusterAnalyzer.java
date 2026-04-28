package de.pho.dsapdfreader.book.analysis;

import de.pho.dsapdfreader.markdown.RawPageData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Buchweite Cluster-Analyse: schaut alle Seiten an und ermittelt
 * <ul>
 *   <li>welcher Cluster der Body-Text ist,</li>
 *   <li>welche Cluster Heading-Tiers sind — beliebig viele, sortiert nach Groesse.</li>
 * </ul>
 * <p>
 * Faktisch — keine Reduktion der Originaldaten. Nur die <i>FontSize</i> wird in
 * 0.25pt-Bins gefasst, weil PDFBox-Floats minimal jittern.
 * <p>
 * <b>Kein Block-Marker-Tier:</b> ob ein Heading-Knoten ein atomarer Block
 * (z.B. Sonderfertigkeit) ist, ist eine fachliche Frage und wird im
 * {@code BookStructuredBuilder} entschieden — anhand Pflichtfelder im Body
 * ({@code voraussetzung} + {@code ap-wert}/{@code kosten}), nicht anhand des
 * Fonts. Body-Size-Bold ist haeufig nur Inline-Hervorhebung im Fliesstext.
 */
public class BookFontClusterAnalyzer {

    /**
     * Toleranz in pt, ab der ein Cluster als "groesser als Body" gilt.
     * Schmal genug, um 13.0 von 13.5 zu trennen; gross genug, um Bin-Rauschen
     * herauszuhalten.
     */
    private static final float SIZE_GREATER_TOLERANCE = 0.25f;

    /**
     * Mindest-Char-Count fuer die Tier-Vergabe.
     * <p>
     * <b>1 = keine Verkuerzung</b> — jeder distinct gesehene Cluster bekommt einen
     * Tier. Bewusst keine Schwelle, weil wir auf Fakten arbeiten und nicht
     * vorzeitig reduzieren wollen. Down-stream-Konsumenten koennen anhand der
     * {@code charCount} selbst entscheiden, ob sie seltene Cluster ignorieren.
     */
    private static final long MIN_CHAR_COUNT = 1;

    /** Initialen ({@code raw.fontSize > 30}) ausschliessen — die sind Dekoration. */
    private static final float INITIAL_SIZE_THRESHOLD = 30f;

    public BookFontClusters analyze(List<RawPageData> pages) {
        Map<FontStyleKey, Long> counts = new HashMap<>();

        for (RawPageData p : pages) {
            if (p.chars == null) continue;
            for (RawPageData.RawChar c : p.chars) {
                if (c.text == null || c.text.isBlank()) continue;
                if (c.fontSize > INITIAL_SIZE_THRESHOLD) continue;
                FontStyleKey k = FontStyleKey.of(c.fontName, c.fontSize, c.bold, c.italic);
                counts.merge(k, 1L, Long::sum);
            }
        }

        BookFontClusters result = new BookFontClusters();
        if (counts.isEmpty()) return result;

        // Body = haeufigster Cluster (mehr Chars als jeder andere)
        FontStyleKey body = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
        result.bodyKey = body;
        result.charCount = new LinkedHashMap<>(counts);

        float bodySize = body.sizeBin();

        // Heading-Cluster = alle Cluster groesser als Body (faktisch, kein Block-Marker-Konzept)
        List<FontStyleKey> headingCandidates = counts.keySet().stream()
                .filter(k -> counts.get(k) >= MIN_CHAR_COUNT)
                .filter(k -> k.sizeBin() > bodySize + SIZE_GREATER_TOLERANCE)
                .sorted(Comparator
                        .comparingDouble((FontStyleKey k) -> -k.sizeBin())
                        .thenComparing((FontStyleKey k) -> !k.bold())        // bold first
                        .thenComparing((FontStyleKey k) -> !k.smallCaps()))  // sc first
                .toList();

        // Tier-Vergabe:
        //   0    = Body
        //   1..N = Heading-Tiers absteigend nach Prominence (Size, dann Bold, dann SC)
        // Body-Size-Bold und kleinere bold-Cluster bekommen KEINEN eigenen Tier — sie sind
        // im Body-Cluster bzw. unspezifizierte Inline-Hervorhebung.
        result.tierByKey.put(body, 0);
        int t = 1;
        for (FontStyleKey k : headingCandidates) {
            result.tierByKey.put(k, t++);
        }

        return result;
    }
}
