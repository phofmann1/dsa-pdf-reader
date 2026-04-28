package de.pho.dsapdfreader.book.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.RawPageData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Standalone-Inspektor: laedt alle {@code page_NNN.json} eines Buches und
 * druckt die buchweiten Font-Cluster mit Tier-Vergabe nach stdout.
 * <p>
 * Aufruf:
 * <pre>
 *   mvn -q compile exec:java \
 *       -Dexec.mainClass=de.pho.dsapdfreader.book.analysis.ClusterInspectMain \
 *       -Dexec.args="export/markdown/raw/01 - Regeln/Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln"
 * </pre>
 * Oder direkt aus der IDE: Argument = Pfad zum Buch-RAW-Verzeichnis.
 */
public class ClusterInspectMain {

    private static final Pattern P_PAGE = Pattern.compile("page_\\d+\\.json$");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: ClusterInspectMain <pfad-zum-buch-raw-dir>");
            System.exit(1);
        }
        Path bookDir = Paths.get(args[0]);
        if (!Files.isDirectory(bookDir)) {
            System.err.println("Verzeichnis nicht gefunden: " + bookDir.toAbsolutePath());
            System.exit(2);
        }

        List<RawPageData> pages = loadPages(bookDir);
        System.out.printf("Buch-Verzeichnis: %s%n", bookDir);
        System.out.printf("Seiten geladen:   %d%n", pages.size());

        BookFontClusters clusters = new BookFontClusterAnalyzer().analyze(pages);
        if (clusters.bodyKey == null) {
            System.out.println("Keine Cluster gefunden (leere Seiten?).");
            return;
        }

        System.out.printf("%nBody-Cluster: %s%n", format(clusters.bodyKey));
        System.out.printf("Gesamt distinkte Cluster: %d%n", clusters.charCount.size());
        System.out.printf("Davon klassifiziert (>= MIN_COUNT): %d%n%n", clusters.tierByKey.size());

        System.out.println("Tier  Chars     SizeBin  Bold  SC   Italic Font");
        System.out.println("----  --------  -------  ----  ---  ------ ------------------------");
        for (FontStyleKey k : clusters.orderedKeys()) {
            int tier = clusters.tierFor(k);
            long n = clusters.charCount.getOrDefault(k, 0L);
            System.out.printf("%-4d  %-8d  %5.2f    %-4s  %-3s  %-6s %s%n",
                    tier, n, k.sizeBin(),
                    k.bold() ? "yes" : "no",
                    k.smallCaps() ? "yes" : "no",
                    k.italic() ? "yes" : "no",
                    k.fontName());
        }
    }

    private static List<RawPageData> loadPages(Path dir) throws IOException {
        List<Path> files;
        try (Stream<Path> walk = Files.list(dir)) {
            files = walk
                    .filter(p -> P_PAGE.matcher(p.getFileName().toString()).find())
                    .sorted()
                    .toList();
        }
        List<RawPageData> result = new ArrayList<>(files.size());
        for (Path f : files) {
            result.add(MAPPER.readValue(f.toFile(), RawPageData.class));
        }
        return result;
    }

    private static String format(FontStyleKey k) {
        return String.format("[size=%.2f bold=%s sc=%s italic=%s font=%s]",
                k.sizeBin(), k.bold(), k.smallCaps(), k.italic(), k.fontName());
    }
}
