package de.pho.dsapdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Extrahiert alle Publikationen aus den _headings.md-Dateien.
 * Pro Buch (identifiziert durch Name + Nummer) wird ein JSON-Eintrag erzeugt
 * mit name, key und publicationYear (aus dem Copyright im Impressum).
 */
public class PublicationExtractorMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String HEADINGS_BASE = "export/markdown/text";
    private static final String OUTPUT_FILE =
        "D:/develop/project/angular/ng-dsa/src/app/_data/publications.json";

    // Verzeichnisse, die komplett übersprungen werden
    private static final Set<String> EXCLUDED_DIRECTORIES = Set.of(
        "50 - Scriptorium", "98 - Material", "Florian Don-Schauen", "Mona",
        "Unbekannt", "06 - Karten", "10 - Abenteuer Zubehor", "11 - Spielplane",
        "20 - Heldenbogen"
    );

    private static final Set<String> SECRET_CATEGORIES = Set.of(
        "04 - Mysterien", "05 - Abenteuer", "05b - Abenteuer DSA 4", "07 - Solo"
    );

    // Copyright © YYYY
    private static final Pattern COPYRIGHT_PATTERN = Pattern.compile(
        "Copyright\\s*©\\s*(\\d{4})"
    );

    // Verzeichnisname: "Buchname (Nummer)"
    private static final Pattern BOOK_DIR_PATTERN = Pattern.compile(
        "^(.+?)\\s*\\((\\d+)\\)$"
    );

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Publikations-Extraktor ===");

        Path basePath = Paths.get(HEADINGS_BASE);
        if (!Files.isDirectory(basePath))
        {
            LOGGER.error("Basisverzeichnis nicht gefunden: {}", basePath.toAbsolutePath());
            return;
        }

        // Alle Buch-Verzeichnisse sammeln (Level 2: Kategorie/Buch)
        Map<String, PublicationInfo> publications = new LinkedHashMap<>();

        List<Path> bookDirs;
        try (Stream<Path> walk = Files.walk(basePath, 2))
        {
            bookDirs = walk
                .filter(Files::isDirectory)
                .filter(p -> p.getNameCount() > basePath.getNameCount() + 1)
                .filter(p -> !isExcluded(p, basePath))
                .collect(Collectors.toList());
        }

        for (Path bookDir : bookDirs)
        {
            String dirName = bookDir.getFileName().toString();
            Matcher dm = BOOK_DIR_PATTERN.matcher(dirName);
            if (!dm.matches()) continue;

            String bookName = dm.group(1).trim();
            String bookNumber = dm.group(2);
            String bookId = bookName + " (" + bookNumber + ")";

            // Schon verarbeitet?
            if (publications.containsKey(bookId)) continue;

            // _headings.md in diesem Verzeichnis suchen
            Path headingsFile = findHeadingsFile(bookDir);
            if (headingsFile == null) continue;

            // Copyright-Jahr extrahieren
            Integer year = extractCopyrightYear(headingsFile);

            // Kategorie bestimmen
            String category = bookDir.getParent().getFileName().toString();
            boolean isSecret = SECRET_CATEGORIES.contains(category);

            PublicationInfo info = new PublicationInfo();
            info.name = bookName;
            info.key = normalizeKey(bookName);
            info.publicationYear = year;
            info.category = category;
            info.secret = isSecret;
            info.bookNumber = Integer.parseInt(bookNumber);

            publications.put(bookId, info);
        }

        // Falls für manche Bücher das Copyright nicht in der aktuellen Kategorie
        // gefunden wurde, versuche die 00 - Fluff-Variante
        for (Map.Entry<String, PublicationInfo> entry : publications.entrySet())
        {
            if (entry.getValue().publicationYear != null) continue;

            String bookId = entry.getKey();
            Path fluffDir = basePath.resolve("00 - Fluff").resolve(bookId);
            if (Files.isDirectory(fluffDir))
            {
                Path headingsFile = findHeadingsFile(fluffDir);
                if (headingsFile != null)
                {
                    Integer year = extractCopyrightYear(headingsFile);
                    if (year != null)
                    {
                        entry.getValue().publicationYear = year;
                    }
                }
            }
        }

        // JSON-Output erzeugen
        List<Map<String, Object>> output = new ArrayList<>();
        for (PublicationInfo info : publications.values())
        {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", info.name);
            entry.put("key", info.key);
            if (info.publicationYear != null)
            {
                entry.put("publicationYear", info.publicationYear);
            }
            entry.put("category", info.category);
            entry.put("secret", info.secret);
            output.add(entry);
        }

        // Sortieren nach Name
        output.sort(Comparator.comparing(e -> (String) e.get("name")));

        Path outputPath = Paths.get(OUTPUT_FILE);
        Files.createDirectories(outputPath.getParent());
        MAPPER.writeValue(outputPath.toFile(), output);

        long withYear = publications.values().stream()
            .filter(p -> p.publicationYear != null).count();
        long secretCount = publications.values().stream()
            .filter(p -> p.secret).count();

        LOGGER.info("=== Gesamt: {} Publikationen ({} mit Jahr, {} secret) -> {} ===",
            publications.size(), withYear, secretCount, OUTPUT_FILE);
    }

    private static Path findHeadingsFile(Path bookDir)
    {
        try (Stream<Path> files = Files.list(bookDir))
        {
            // Direkt im Buchverzeichnis
            Path direct = files
                .filter(p -> p.getFileName().toString().equals("_headings.md"))
                .findFirst()
                .orElse(null);
            if (direct != null) return direct;
        }
        catch (IOException e)
        {
            // ignore
        }

        // In Unterverzeichnissen suchen
        try (Stream<Path> walk = Files.walk(bookDir, 2))
        {
            return walk
                .filter(p -> p.getFileName().toString().equals("_headings.md"))
                .findFirst()
                .orElse(null);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private static Integer extractCopyrightYear(Path headingsFile)
    {
        try
        {
            String content = Files.readString(headingsFile, StandardCharsets.UTF_8);
            // Impressum kann auf den ersten ~15 Seiten stehen
            String head = content.substring(0, Math.min(15000, content.length()));
            Matcher m = COPYRIGHT_PATTERN.matcher(head);
            if (m.find())
            {
                return Integer.parseInt(m.group(1));
            }
        }
        catch (IOException e)
        {
            LOGGER.debug("Fehler beim Lesen von {}: {}", headingsFile, e.getMessage());
        }
        return null;
    }

    private static boolean isExcluded(Path dir, Path basePath)
    {
        Path relative = basePath.relativize(dir);
        for (int i = 0; i < relative.getNameCount(); i++)
        {
            if (EXCLUDED_DIRECTORIES.contains(relative.getName(i).toString()))
            {
                return true;
            }
        }
        return false;
    }

    static String normalizeKey(String name)
    {
        if (name == null) return null;
        return name.toLowerCase()
            .replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").replace("ß", "ss")
            .replaceAll("[^a-z0-9\\s]", "")
            .trim()
            .replaceAll("\\s+", "_");
    }

    private static class PublicationInfo
    {
        String name;
        String key;
        Integer publicationYear;
        String category;
        boolean secret;
        int bookNumber;
    }
}
