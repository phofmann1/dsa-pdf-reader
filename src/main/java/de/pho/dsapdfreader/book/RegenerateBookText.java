package de.pho.dsapdfreader.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Regeneriert die Markdown-Ausgabe fuer ein einzelnes Buch.
 *
 * <p>Output-Layout (pro Buch unterhalb von {@code outDir}):
 * <pre>
 *   raw/    — pro-Seite-MDs (seite_NNN.md, Eingabe fuer downstream Analysen)
 *   result/ — finale Buchfassung: _volltext.md + ausgelagerte Kasten-MDs
 *   log/    — RESERVED fuer manuelle Analysen/Logfiles. Nicht von dieser Klasse befuellt
 *             oder geleert — Inhalt bleibt zwischen Laeufen erhalten.
 * </pre>
 *
 * <p>Vor dem Schreiben werden {@code raw/} und {@code result/} geleert,
 * damit veraltete Dateien aus frueheren Laeufen nicht uebrig bleiben.
 *
 * <p>Args: rawJsonDir outDir [sourcePath]
 */
public final class RegenerateBookText {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RegenerateBookText() {}

    public static int regenerate(Path rawJsonDir, Path outDir, String sourcePath) throws IOException {
        Path pageMdDir = outDir.resolve("raw");
        Path resultDir = outDir.resolve("result");

        cleanDirectory(pageMdDir);
        cleanDirectory(resultDir);
        cleanLegacyFlatLayout(outDir);
        Files.createDirectories(pageMdDir);
        Files.createDirectories(resultDir);

        TextInterpreter interpreter = new TextInterpreter();
        // Boxen sollen NICHT mehr inline im _volltext.md erscheinen,
        // sondern als separate Dateien in result/ landen.
        interpreter.setEmitBoxesInline(false);

        List<Path> rawFiles;
        try (Stream<Path> files = Files.list(rawJsonDir)) {
            rawFiles = files
                    .filter(p -> p.getFileName().toString().matches("page_\\d+\\.json"))
                    .sorted()
                    .toList();
        }

        StringBuilder fullText = new StringBuilder();
        if (sourcePath != null) fullText.append("# Quelle: ").append(sourcePath).append("\n\n");

        // Pro Seite ein Index-Counter fuer Boxen mit identischer Heading-Vermutung,
        // damit Dateinamen kollisionsfrei bleiben.
        Map<String, Integer> boxNameCounts = new HashMap<>();

        // Bildkommentare werden NICHT in den Volltext geschrieben (User-Entscheidung
        // 2026-05-07). Aufruf von getLastImageComments() bleibt nur fuer Reset-Semantik.

        int pages = 0;
        for (Path rawFile : rawFiles) {
            RawPageData page = MAPPER.readValue(rawFile.toFile(), RawPageData.class);
            String markdown = interpreter.interpretPage(page);

            // Pro-Seite-MD in raw/
            Path pageFile = pageMdDir.resolve(String.format("seite_%03d.md", page.pageNumber));
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(pageFile, StandardCharsets.UTF_8))) {
                pw.println("# Seite " + page.pageNumber);
                if (sourcePath != null) {
                    pw.println("<!-- Quelle: " + sourcePath + ".pdf -->");
                }
                pw.println();
                pw.print(markdown);
            }

            // Volltext: KEINE Seitenzahl-Header mehr — Volltext ist die finale Buchfassung.
            fullText.append(markdown);

            // Bildkommentare bewusst NICHT in den Volltext schreiben — sie zerreissen
            // Bloecke und liefern fuer den Talent-Importer keinen Mehrwert. (User-Entscheidung
            // 2026-05-07: "Entferne es wenn ich darueber nachdenke".)
            interpreter.getLastImageComments();

            // Ausgelagerte Kaesten als eigene Dateien in result/
            for (TextInterpreter.BoxRendering box : interpreter.getLastBoxes()) {
                String fileName = buildBoxFileName(box, boxNameCounts);
                Path boxFile = resultDir.resolve(fileName);
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(boxFile, StandardCharsets.UTF_8))) {
                    String heading = box.headingGuess.isEmpty() ? "Anmerkung" : box.headingGuess;
                    pw.println("# " + heading);
                    pw.println();
                    pw.println("<!-- Seite " + page.pageNumber
                            + ", Box " + box.index
                            + ", y=" + (int) box.y + " x=" + (int) box.x + " -->");
                    pw.println();
                    pw.print(box.markdown);
                }
            }
            pages++;
        }

        Path fullFile = resultDir.resolve("_volltext.md");
        Files.writeString(fullFile, fullText.toString(), StandardCharsets.UTF_8);

        return pages;
    }

    /**
     * Entfernt veraltete Artefakte aus dem alten Flat-Layout (vor der
     * log/result-Aufteilung): {@code seite_NNN.md} und {@code _volltext.md}
     * direkt im Buch-Verzeichnis (nicht in Unterordnern).
     */
    static void cleanLegacyFlatLayout(Path outDir) throws IOException {
        if (!Files.exists(outDir)) return;
        try (Stream<Path> files = Files.list(outDir)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> {
                     String name = p.getFileName().toString();
                     return name.matches("seite_\\d+\\.md") || name.equals("_volltext.md");
                 })
                 .forEach(p -> {
                     try { Files.deleteIfExists(p); }
                     catch (IOException e) { throw new RuntimeException("delete failed: " + p, e); }
                 });
        }
    }

    /** Loescht rekursiv den Verzeichnisinhalt (das Verzeichnis selbst bleibt bestehen). */
    static void cleanDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                .filter(p -> !p.equals(dir))
                .forEach(p -> {
                    try { Files.deleteIfExists(p); }
                    catch (IOException e) { throw new RuntimeException("delete failed: " + p, e); }
                });
        }
    }

    /**
     * Dateiname fuer ausgelagerte Kasten: "<3-stellig> - <Heading>.md".
     * Bei Kollision wird ein " (n)"-Suffix angehaengt.
     */
    static String buildBoxFileName(TextInterpreter.BoxRendering box,
                                   Map<String, Integer> nameCounts) {
        String heading = box.headingGuess.isEmpty() ? "Anmerkung" : box.headingGuess;
        String safe = sanitizeFileName(heading);
        String base = String.format("%03d - %s", box.pageNumber, safe);
        Integer n = nameCounts.get(base);
        if (n == null) {
            nameCounts.put(base, 1);
            return base + ".md";
        }
        nameCounts.put(base, n + 1);
        return base + " (" + (n + 1) + ").md";
    }

    /** Entfernt Zeichen, die in Windows-Dateinamen verboten sind. */
    static String sanitizeFileName(String s) {
        String cleaned = s.replaceAll("[\\\\/:*?\"<>|]", " ").replaceAll("\\s+", " ").strip();
        if (cleaned.isEmpty()) cleaned = "Anmerkung";
        return cleaned;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: RegenerateBookText <rawDir> <outDir> [sourcePath]");
            System.exit(1);
        }
        Path rawDir = Path.of(args[0]);
        Path outDir = Path.of(args[1]);
        String sourcePath = args.length > 2 ? args[2] : null;
        int n = regenerate(rawDir, outDir, sourcePath);
        System.out.println("Regenerated " + n + " pages from " + rawDir);
    }
}
