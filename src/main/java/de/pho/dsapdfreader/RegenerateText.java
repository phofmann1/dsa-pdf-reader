package de.pho.dsapdfreader;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

/**
 * Regeneriert die Markdown-Ausgabe fuer den gesamten Korpus.
 * Liest nur aus {@code export/markdown/raw/} (page_NNN.json) — kein PDF noetig.
 *
 * <p>Output-Layout pro Buch (unterhalb von {@code export/markdown/text/<book>/}):
 * <pre>
 *   log/    — seite_NNN.md (Debug-Artefakte)
 *   result/ — _volltext.md + ausgelagerte Kasten-MDs (finale Buchfassung)
 * </pre>
 *
 * <p>Vor jedem Lauf wird {@code log/} und {@code result/} pro Buch geleert.
 */
public class RegenerateText
{
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String RAW_BASE = "export/markdown/raw";
    private static final String TEXT_BASE = "export/markdown/text";

    public static void main(String[] args) throws Exception
    {
        Path rawBase = Paths.get(RAW_BASE);
        Path textBase = Paths.get(TEXT_BASE);
        TextInterpreter interpreter = new TextInterpreter();
        interpreter.setEmitBoxesInline(false);

        // Optional Filter: jedes Argument ist ein Substring (case-insensitive),
        // mind. eines muss im Pfad vorkommen. Ohne Argumente werden alle Buecher
        // gerendert.
        java.util.List<String> filters = new java.util.ArrayList<>();
        for (String a : args) if (a != null && !a.isBlank()) filters.add(a.toLowerCase());

        int totalDirs = 0;
        int totalPages = 0;

        try (Stream<Path> walk = Files.walk(rawBase))
        {
            List<Path> rawDirs = walk
                .filter(Files::isDirectory)
                .filter(dir -> {
                    try (Stream<Path> files = Files.list(dir))
                    {
                        return files.anyMatch(f -> f.getFileName().toString().matches("page_\\d+\\.json"));
                    }
                    catch (IOException e)
                    {
                        return false;
                    }
                })
                .filter(dir -> {
                    if (filters.isEmpty()) return true;
                    String pathLower = dir.toString().toLowerCase();
                    for (String f : filters) if (pathLower.contains(f)) return true;
                    return false;
                })
                .sorted()
                .toList();

            for (Path rawDir : rawDirs)
            {
                Path relPath = rawBase.relativize(rawDir);
                Path outDir = textBase.resolve(relPath);
                Path pageMdDir = outDir.resolve("raw");
                Path resultDir = outDir.resolve("result");

                cleanDirectory(pageMdDir);
                cleanDirectory(resultDir);
                cleanLegacyFlatLayout(outDir);
                Files.createDirectories(pageMdDir);
                Files.createDirectories(resultDir);

                String sourcePath = relPath.toString().replace("/", "\\");

                List<Path> rawFiles;
                try (Stream<Path> files = Files.list(rawDir))
                {
                    rawFiles = files
                        .filter(p -> p.getFileName().toString().matches("page_\\d+\\.json"))
                        .sorted()
                        .toList();
                }

                StringBuilder fullText = new StringBuilder();
                fullText.append("# Quelle: ").append(sourcePath).append("\n\n");

                Map<String, Integer> boxNameCounts = new HashMap<>();

                for (Path rawFile : rawFiles)
                {
                    RawPageData page = MAPPER.readValue(rawFile.toFile(), RawPageData.class);
                    String markdown = interpreter.interpretPage(page);

                    Path pageFile = pageMdDir.resolve(String.format("seite_%03d.md", page.pageNumber));
                    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(pageFile, StandardCharsets.UTF_8)))
                    {
                        pw.println("# Seite " + page.pageNumber);
                        pw.println("<!-- Quelle: " + sourcePath + ".pdf -->");
                        pw.println();
                        pw.print(markdown);
                    }

                    // Volltext: keine Seitenzahl-Trenner mehr.
                    fullText.append(markdown);

                    for (TextInterpreter.BoxRendering box : interpreter.getLastBoxes())
                    {
                        String fileName = buildBoxFileName(box, boxNameCounts);
                        Path boxFile = resultDir.resolve(fileName);
                        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(boxFile, StandardCharsets.UTF_8)))
                        {
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
                    totalPages++;
                }

                Path fullFile = resultDir.resolve("_volltext.md");
                Files.writeString(fullFile, fullText.toString(), StandardCharsets.UTF_8);

                totalDirs++;
                if (totalDirs % 50 == 0)
                {
                    System.out.println("  ... " + totalDirs + " books, " + totalPages + " pages ...");
                }
            }
        }

        System.out.println("Done: " + totalDirs + " books, " + totalPages + " pages regenerated.");
    }

    /**
     * Entfernt veraltete Artefakte aus dem alten Flat-Layout (seite_NNN.md
     * und _volltext.md direkt im Buchverzeichnis, vor der log/result-Aufteilung).
     */
    private static void cleanLegacyFlatLayout(Path outDir) throws IOException
    {
        if (!Files.exists(outDir)) return;
        try (Stream<Path> files = Files.list(outDir))
        {
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

    private static void cleanDirectory(Path dir) throws IOException
    {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir))
        {
            walk.sorted(Comparator.reverseOrder())
                .filter(p -> !p.equals(dir))
                .forEach(p -> {
                    try { Files.deleteIfExists(p); }
                    catch (IOException e) { throw new RuntimeException("delete failed: " + p, e); }
                });
        }
    }

    private static String buildBoxFileName(TextInterpreter.BoxRendering box,
                                           Map<String, Integer> nameCounts)
    {
        String heading = box.headingGuess.isEmpty() ? "Anmerkung" : box.headingGuess;
        String safe = sanitizeFileName(heading);
        String base = String.format("%03d - %s", box.pageNumber, safe);
        Integer n = nameCounts.get(base);
        if (n == null)
        {
            nameCounts.put(base, 1);
            return base + ".md";
        }
        nameCounts.put(base, n + 1);
        return base + " (" + (n + 1) + ").md";
    }

    private static String sanitizeFileName(String s)
    {
        String cleaned = s.replaceAll("[\\\\/:*?\"<>|]", " ").replaceAll("\\s+", " ").strip();
        if (cleaned.isEmpty()) cleaned = "Anmerkung";
        return cleaned;
    }
}
