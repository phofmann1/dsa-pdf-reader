package de.pho.dsapdfreader.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.pho.dsapdfreader.markdown.RawPageData;
import de.pho.dsapdfreader.markdown.TextInterpreter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Regeneriert seite_NNN.md fuer ein einzelnes Buch (nicht den gesamten Korpus).
 *
 * <p>Args: rawDir textDir [sourcePath]
 * <ul>
 *   <li>rawDir   — enthaelt page_NNN.json
 *   <li>textDir  — Output-Verzeichnis fuer seite_NNN.md
 *   <li>sourcePath — optional, fuer den "Quelle:"-Kommentar im Markdown
 * </ul>
 *
 * <p>Im Gegensatz zu {@code RegenerateText} (der den ganzen Korpus durchlaeuft)
 * arbeitet diese Klasse nur auf einem Buch — sinnvoll fuer iteratives Tuning
 * des {@link TextInterpreter}, ohne dass alle anderen Buecher ueberschrieben werden.
 */
public final class RegenerateBookText {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RegenerateBookText() {}

    public static int regenerate(Path rawDir, Path textDir, String sourcePath) throws IOException {
        Files.createDirectories(textDir);
        TextInterpreter interpreter = new TextInterpreter();

        List<Path> rawFiles;
        try (Stream<Path> files = Files.list(rawDir)) {
            rawFiles = files
                    .filter(p -> p.getFileName().toString().matches("page_\\d+\\.json"))
                    .sorted()
                    .toList();
        }

        StringBuilder fullText = new StringBuilder();
        if (sourcePath != null) fullText.append("# Quelle: ").append(sourcePath).append("\n\n");

        int pages = 0;
        for (Path rawFile : rawFiles) {
            RawPageData page = MAPPER.readValue(rawFile.toFile(), RawPageData.class);
            String markdown = interpreter.interpretPage(page);

            Path pageFile = textDir.resolve(String.format("seite_%03d.md", page.pageNumber));
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(pageFile, StandardCharsets.UTF_8))) {
                pw.println("# Seite " + page.pageNumber);
                if (sourcePath != null) {
                    pw.println("<!-- Quelle: " + sourcePath + ".pdf -->");
                }
                pw.println();
                pw.print(markdown);
            }

            fullText.append("\n---\n# Seite ").append(page.pageNumber).append("\n\n");
            fullText.append(markdown);
            pages++;
        }

        Path fullFile = textDir.resolve("_volltext.md");
        Files.writeString(fullFile, fullText.toString(), StandardCharsets.UTF_8);

        return pages;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: RegenerateBookText <rawDir> <textDir> [sourcePath]");
            System.exit(1);
        }
        Path rawDir = Path.of(args[0]);
        Path textDir = Path.of(args[1]);
        String sourcePath = args.length > 2 ? args[2] : null;
        int n = regenerate(rawDir, textDir, sourcePath);
        System.out.println("Regenerated " + n + " pages from " + rawDir);
    }
}
