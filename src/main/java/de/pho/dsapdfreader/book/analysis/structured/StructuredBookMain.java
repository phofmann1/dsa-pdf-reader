package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.pho.dsapdfreader.markdown.RawPageData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Standalone-CLI: erzeugt fuer ein Buch (oder alle Buecher unter einem
 * raw-Verzeichnis) ein {@code _structured.json} mit der faktischen
 * Cluster-/Linien-/Block-Analyse.
 * <p>
 * Aufruf:
 * <pre>
 *   java -cp ... StructuredBookMain &lt;raw-buch-dir&gt; [&lt;text-buch-dir&gt;]
 *   # oder fuer ein ganzes Wurzel-Verzeichnis:
 *   java -cp ... StructuredBookMain --root &lt;raw-root&gt; --text &lt;text-root&gt; [--filter Kodex]
 * </pre>
 */
public class StructuredBookMain {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final Pattern P_PAGE = Pattern.compile("page_\\d+\\.json$");
    /** Erkennt Buchverzeichnisse "Kodex des Schwertes (171)/Kodex des Schwertes - 01 - Regeln". */
    private static final Pattern P_TITLE = Pattern.compile("^(.+?)\\s+-\\s.*$");

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage:");
            System.err.println("  StructuredBookMain <raw-buch-dir> [<text-buch-dir>]");
            System.err.println("  StructuredBookMain --root <raw-root> --text <text-root> [--filter <substring>]");
            System.exit(1);
        }

        if ("--root".equals(args[0])) {
            runBatch(args);
        } else {
            Path rawDir = Paths.get(args[0]);
            Path textDir = args.length >= 2 ? Paths.get(args[1]) : rawDir;
            runOne(rawDir, textDir);
        }
    }

    private static void runBatch(String[] args) throws IOException {
        Path rawRoot = null, textRoot = null;
        String filter = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--root" -> rawRoot = Paths.get(args[++i]);
                case "--text" -> textRoot = Paths.get(args[++i]);
                case "--filter" -> filter = args[++i];
            }
        }
        if (rawRoot == null || textRoot == null) {
            System.err.println("--root and --text required");
            System.exit(1);
        }

        try (Stream<Path> walk = Files.walk(rawRoot)) {
            for (Path bookDir : walk.filter(Files::isDirectory).filter(StructuredBookMain::hasPageJsons).sorted().toList()) {
                String name = bookDir.getFileName().toString();
                if (filter != null && !name.toLowerCase().contains(filter.toLowerCase())) continue;
                Path rel = rawRoot.relativize(bookDir);
                Path outDir = textRoot.resolve(rel);
                System.out.printf("Buch: %s -> %s%n", bookDir, outDir);
                runOne(bookDir, outDir);
            }
        }
    }

    private static void runOne(Path rawBookDir, Path textBookDir) throws IOException {
        List<RawPageData> pages = loadPages(rawBookDir);
        if (pages.isEmpty()) {
            System.out.println("  (keine Seiten gefunden)");
            return;
        }

        String title = parseTitle(rawBookDir.getFileName().toString());
        String publication = slugify(title);

        // Mapping-Datei pro Buch laden, falls vorhanden
        // src/main/resources/book-mappings/<Slug>.json — z.B. Kodex_des_Schwertes.json
        NameMapping mapping = loadMappingForTitle(title);

        BookStructuredBuilder builder = new BookStructuredBuilder();
        BookStructured structured = builder.build(publication, title, null, pages, mapping);

        Files.createDirectories(textBookDir);
        Path outFile = textBookDir.resolve("_structured.json");
        MAPPER.writeValue(outFile.toFile(), structured);
        System.out.printf("  Seiten: %d, Cluster: %d, Bloecke: %d, Hierarchie: %d%n",
                structured.pages, structured.clusters.size(), structured.blocks.size(),
                structured.hierarchy.size());
        System.out.printf("  -> %s%n", outFile);
    }

    private static List<RawPageData> loadPages(Path bookDir) throws IOException {
        List<Path> files;
        try (Stream<Path> walk = Files.list(bookDir)) {
            files = walk.filter(p -> P_PAGE.matcher(p.getFileName().toString()).find())
                    .sorted().toList();
        }
        List<RawPageData> result = new ArrayList<>(files.size());
        for (Path f : files) {
            result.add(MAPPER.readValue(f.toFile(), RawPageData.class));
        }
        return result;
    }

    private static boolean hasPageJsons(Path dir) {
        try (Stream<Path> walk = Files.list(dir)) {
            return walk.anyMatch(p -> P_PAGE.matcher(p.getFileName().toString()).find());
        } catch (IOException e) {
            return false;
        }
    }

    private static String parseTitle(String dirName) {
        Matcher m = P_TITLE.matcher(dirName);
        if (m.matches()) return m.group(1).trim();
        return dirName.replaceAll("\\s*\\(\\d+\\)\\s*$", "").trim();
    }

    /** Sucht eine Mapping-Datei zum Buch-Titel. Liefert leeres Mapping wenn nicht gefunden. */
    private static NameMapping loadMappingForTitle(String title) {
        if (title == null) return NameMapping.empty();
        // Konvention: Datei-Name ist Title mit "_" statt Whitespace, in src/main/resources/book-mappings/
        String fileSlug = title.replace(' ', '_').replaceAll("[^A-Za-z0-9_äöüÄÖÜß]", "");
        // 1. Im Resource-Pfad
        Path resourcePath = Paths.get("src/main/resources/book-mappings", fileSlug + ".json");
        if (Files.isRegularFile(resourcePath)) {
            try {
                return NameMapping.load(resourcePath);
            } catch (IOException e) {
                System.err.println("  Mapping-Fehler: " + e.getMessage());
            }
        }
        return NameMapping.empty();
    }

    private static String slugify(String text) {
        if (text == null) return "";
        String s = text.toLowerCase()
                .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss");
        s = s.replaceAll("[^a-z0-9_]+", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
        return s;
    }
}
