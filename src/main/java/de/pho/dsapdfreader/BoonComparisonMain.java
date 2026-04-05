package de.pho.dsapdfreader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Vergleicht Vor- und Nachteile aus den _headings.md-Dateien
 * mit den Angular-JSON-Daten im ng-dsa-Projekt.
 */
public class BoonComparisonMain
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String HEADINGS_BASE = "export/markdown/text";
    private static final String ANGULAR_BOONS_DIR = "D:/develop/project/angular/ng-dsa/src/app/_data/boons";

    private static final String TYPE_VORTEIL = "Vorteil";
    private static final String TYPE_NACHTEIL = "Nachteil";
    private static final String TYPE_UNBEKANNT = "?";

    // ## Sektionen, die Vor-/Nachteile enthalten
    private static final Pattern BOON_SECTION_PATTERN = Pattern.compile(
        "^##\\s+(" +
            "Vort$" +
            "|Vorteile$" +
            "|Nachteile$" +
            "|Neue Vorteile$" +
            "|Neue Nachteile$" +
            "|Neuer Vorteil$" +
            "|Neuer Nachteil$" +
            "|Vor- und Nachteile$" +
            "|Ahnenblut$" +
            "|.*blut-Vorteile.*" +
            "|.*geborene-Vorteile.*" +
            ")"
    );

    // Sektionen, die NICHT als Boon-Sektion gelten
    private static final Pattern EXCLUDED_SECTION_PATTERN = Pattern.compile(
        "(?i)(Kreaturen|Tiergef.hrten|Tiervor|Kampfsonderfertigkeit|Allgemeine Regeln|Sonderfertigkeit)"
    );

    // Bücher, die ausgeschlossen werden
    private static final Set<String> EXCLUDED_BOOKS = Set.of(
        "Tiergefahrten",
        "Regelwerk"
    );

    // Verzeichnisse (Kategorie-Ebene), die komplett ignoriert werden
    private static final Set<String> EXCLUDED_DIRECTORIES = Set.of(
        "50 - Scriptorium",
        "98 - Material",
        "Florian Don-Schauen",
        "Mona",
        "Unbekannt"
    );

    private static final Pattern BOON_ENTRY_PATTERN = Pattern.compile("^###\\s+(.+)$");
    private static final Pattern SECTION_HEADING_PATTERN = Pattern.compile("^##\\s+(.+)$");

    // Römische Zahlen am Ende, optional mit Hyphen/En-Dash Bereich
    private static final Pattern LEVEL_STRIP_PATTERN = Pattern.compile(
        "\\s+[IVX]+([-–][IVX]+)?\\s*$"
    );

    private static final int MIN_NAME_LENGTH = 3;

    // ---------------------------------------------------------------
    // Eintrag mit Typ und Buch
    // ---------------------------------------------------------------

    private record BoonEntry(String name, String book, String type,
                              boolean hasRegel, boolean hasAP) {}

    private static final String INCOMPLETE = " [UNVOLLSTÄNDIG]";

    public static void main(String[] args) throws Exception
    {
        LOGGER.info("=== Boon-Vergleich: headings.md vs Angular JSON ===");
        LOGGER.info("");

        List<BoonEntry> headingsBoons = collectBoonsFromHeadings();
        List<BoonEntry> angularBoons = collectBoonsFromAngular();

        compareAndReport(headingsBoons, angularBoons);
    }

    // ---------------------------------------------------------------
    // Headings.md Parsing
    // ---------------------------------------------------------------

    private static List<BoonEntry> collectBoonsFromHeadings() throws IOException
    {
        List<BoonEntry> allBoons = new ArrayList<>();
        Path basePath = Paths.get(HEADINGS_BASE);

        List<Path> headingsFiles;
        try (Stream<Path> files = Files.walk(basePath))
        {
            headingsFiles = files
                .filter(p -> p.getFileName().toString().equals("_headings.md"))
                .filter(p -> !isInExcludedDirectory(p))
                .collect(Collectors.toList());
        }

        for (Path file : headingsFiles)
        {
            String bookName = extractBookName(file);
            if (EXCLUDED_BOOKS.contains(bookName))
            {
                continue;
            }
            allBoons.addAll(extractBoonsFromFile(file, bookName));
        }
        return allBoons;
    }

    private static String extractBookName(Path headingsFile)
    {
        Path parent = headingsFile.getParent();
        while (parent != null)
        {
            String dirName = parent.getFileName().toString();
            if (dirName.matches(".*\\(\\d+\\)$"))
            {
                return dirName.replaceAll("\\s*\\(\\d+\\)$", "").trim();
            }
            parent = parent.getParent();
        }
        return headingsFile.getParent().getFileName().toString();
    }

    private static boolean isInExcludedDirectory(Path file)
    {
        for (Path component : file)
        {
            if (EXCLUDED_DIRECTORIES.contains(component.toString()))
            {
                return true;
            }
        }
        return false;
    }

    private static List<BoonEntry> extractBoonsFromFile(Path file, String bookName) throws IOException
    {
        List<BoonEntry> boons = new ArrayList<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        boolean inBoonSection = false;
        String currentSectionName = "";
        String currentType = TYPE_UNBEKANNT;

        // Aktueller Eintrag: Name + gesammelter Body
        String pendingName = null;
        boolean pendingHasRegel = false;
        boolean pendingHasAP = false;

        for (String line : lines)
        {
            // HTML-Kommentare überspringen — Text seitenübergreifend zusammenfassen
            if (line.trim().startsWith("<!--"))
            {
                continue;
            }

            Matcher sectionMatcher = SECTION_HEADING_PATTERN.matcher(line);
            if (sectionMatcher.matches())
            {
                String heading = sectionMatcher.group(1).trim();

                // Seitenumbruch-Fortsetzungen erkennen BEVOR Eintrag geschlossen wird
                // "## eile" ist Rest von "## Vort(eile)" nach Seitenumbruch
                if (line.startsWith("## ") && !line.startsWith("### "))
                {
                    if (heading.equals("eile") && currentSectionName.equals("Vort"))
                    {
                        // kein Sektionswechsel, kein Eintrag-Abschluss — weiter sammeln
                        continue;
                    }
                }

                // Offenen Eintrag abschließen bei echtem Sektionswechsel
                if (pendingName != null)
                {
                    boons.add(new BoonEntry(pendingName, bookName, currentType,
                        pendingHasRegel, pendingHasAP));
                    pendingName = null;
                }

                Matcher boonMatcher = BOON_SECTION_PATTERN.matcher(line);
                if (boonMatcher.matches() && !EXCLUDED_SECTION_PATTERN.matcher(heading).find())
                {
                    inBoonSection = true;
                    currentSectionName = heading;
                    currentType = classifySectionType(heading);

                    if (heading.equalsIgnoreCase("Vor- und Nachteile"))
                    {
                        inBoonSection = false;
                    }
                }
                else if (line.startsWith("## ") && !line.startsWith("### "))
                {
                    inBoonSection = false;
                }
            }

            if (inBoonSection)
            {
                Matcher entryMatcher = BOON_ENTRY_PATTERN.matcher(line);
                if (entryMatcher.matches())
                {
                    // Vorherigen Eintrag abschließen
                    if (pendingName != null)
                    {
                        boons.add(new BoonEntry(pendingName, bookName, currentType,
                            pendingHasRegel, pendingHasAP));
                    }

                    // Neuen Eintrag starten
                    pendingName = normalizeName(entryMatcher.group(1).trim());
                    pendingHasRegel = false;
                    pendingHasAP = false;
                }
                else if (pendingName != null)
                {
                    // Body-Zeilen auf Regel und AP-Wert prüfen
                    if (line.contains("Regel:") || line.contains("**Regel:**"))
                    {
                        pendingHasRegel = true;
                    }
                    if (line.contains("AP-Wert") || line.contains("AP-Kosten")
                        || line.contains("Abenteuerpunkte"))
                    {
                        pendingHasAP = true;
                    }
                }
            }
        }

        // Letzten offenen Eintrag abschließen
        if (pendingName != null)
        {
            boons.add(new BoonEntry(pendingName, bookName, currentType,
                pendingHasRegel, pendingHasAP));
        }

        return boons;
    }

    private static String classifySectionType(String heading)
    {
        String lower = heading.toLowerCase();
        if (lower.contains("nachteil"))
        {
            return TYPE_NACHTEIL;
        }
        if (lower.contains("vorteil") || lower.contains("vort")
            || lower.contains("blut") || lower.contains("geborene"))
        {
            return TYPE_VORTEIL;
        }
        return TYPE_UNBEKANNT;
    }

    // ---------------------------------------------------------------
    // Angular JSON Parsing
    // ---------------------------------------------------------------

    private static List<BoonEntry> collectBoonsFromAngular() throws IOException
    {
        List<BoonEntry> allBoons = new ArrayList<>();
        Path boonsDir = Paths.get(ANGULAR_BOONS_DIR);

        List<Path> jsonFiles;
        try (Stream<Path> files = Files.list(boonsDir))
        {
            jsonFiles = files
                .filter(p -> p.toString().endsWith(".json"))
                .collect(Collectors.toList());
        }

        for (Path file : jsonFiles)
        {
            String fileName = file.getFileName().toString();
            String bookName = fileName.replace("_BOONS.json", "").replace("_", " ");

            List<Map<String, Object>> entries = MAPPER.readValue(
                file.toFile(),
                new TypeReference<>() {}
            );

            for (Map<String, Object> entry : entries)
            {
                String rawName = (String) entry.get("name");
                int category = (int) entry.get("category");
                String type = category == 0 ? TYPE_VORTEIL : TYPE_NACHTEIL;
                String name = normalizeName(rawName);
                if (name != null)
                {
                    allBoons.add(new BoonEntry(name, bookName, type, true, true));
                }
            }
        }
        return allBoons;
    }

    // ---------------------------------------------------------------
    // Gemeinsame Normalisierung (beide Seiten identisch)
    // ---------------------------------------------------------------

    private static String normalizeName(String name)
    {
        // 1. Markdown-Formatierung entfernen
        name = name.replace("*", "");

        // 2. Leere Klammern entfernen: "Greifschwanz ()" → "Greifschwanz"
        //    und (*) Markierungen: "Altersresistenz (*)" → "Altersresistenz"
        name = name.replaceAll("\\s*\\(\\s*\\*?\\s*\\)\\s*", "").trim();

        // 3. Nachgestellte Punkte/Auslassungen entfernen
        name = name.replaceAll("[.…]+$", "").trim();

        // 4. Ellipsis normalisieren: "…" → "..."
        name = name.replace("…", "...");

        // 5. Römische Zahlen am Ende entfernen (NACH Klammer/Punkt-Bereinigung)
        name = LEVEL_STRIP_PATTERN.matcher(name).replaceAll("").trim();

        // 6. Sektionspräfixe in kombinierten Namen entfernen
        name = name.replaceAll("^\\S+-Vorteile\\s+", "");
        name = name.replaceAll("^\\S+-Nachteile\\s+", "");

        // 7. Mehrfach-Leerzeichen normalisieren
        name = name.replaceAll("\\s{2,}", " ").trim();

        // 8. Garbage filtern
        if (name.length() < MIN_NAME_LENGTH)
        {
            return null;
        }
        if (name.startsWith("Regel:") || name.startsWith("Voraussetzung")
            || name.startsWith("Stufe ") || name.startsWith("Beispiele")
            || name.startsWith("Vor- und Nachteile in"))
        {
            return null;
        }

        return name;
    }

    // ---------------------------------------------------------------
    // Vergleich und Report
    // ---------------------------------------------------------------

    private static void compareAndReport(
        List<BoonEntry> headingsBoons,
        List<BoonEntry> angularBoons)
    {
        Comparator<BoonEntry> boonOrder = Comparator
            .comparing(BoonEntry::book)
            .thenComparing(BoonEntry::type, BoonComparisonMain::typeOrder)
            .thenComparing(BoonEntry::name);

        // -- Unvollständige Einträge ermitteln und aus Statistik entfernen --
        List<BoonEntry> incomplete = headingsBoons.stream()
            .filter(e -> e.name() != null)
            .filter(e -> !e.hasRegel() || !e.hasAP())
            .sorted(boonOrder)
            .collect(Collectors.toList());

        Set<String> incompleteKeys = incomplete.stream()
            .map(e -> e.book() + "|" + e.name())
            .collect(Collectors.toSet());

        List<BoonEntry> validHeadings = headingsBoons.stream()
            .filter(e -> e.name() != null)
            .filter(e -> !incompleteKeys.contains(e.book() + "|" + e.name()))
            .collect(Collectors.toList());

        // -- Statistik auf Basis gültiger Einträge --
        Set<String> allAngularNames = angularBoons.stream()
            .map(BoonEntry::name)
            .collect(Collectors.toSet());

        Set<String> validHeadingsNames = validHeadings.stream()
            .map(BoonEntry::name)
            .collect(Collectors.toSet());

        LOGGER.info("=== ZUSAMMENFASSUNG ===");
        LOGGER.info("Headings.md : {} gültige unique Namen in {} Büchern (+ {} unvollständig)",
            validHeadingsNames.size(),
            validHeadings.stream().map(BoonEntry::book).distinct().count(),
            incomplete.size());
        LOGGER.info("Angular JSON: {} unique Namen in {} Dateien",
            allAngularNames.size(),
            angularBoons.stream().map(BoonEntry::book).distinct().count());

        // -- Pro Buch (headings, nur gültige) --
        LOGGER.info("");
        LOGGER.info("=== HEADINGS.MD PRO BUCH ===");
        validHeadings.stream()
            .collect(Collectors.groupingBy(BoonEntry::book, TreeMap::new, Collectors.toList()))
            .forEach((book, entries) ->
                LOGGER.info("  {} : {} Einträge", book, entries.size()));

        // -- Pro Datei (Angular) --
        LOGGER.info("");
        LOGGER.info("=== ANGULAR JSON PRO DATEI ===");
        angularBoons.stream()
            .collect(Collectors.groupingBy(BoonEntry::book, TreeMap::new, Collectors.toList()))
            .forEach((book, entries) ->
                LOGGER.info("  {} : {} Einträge", book, entries.size()));

        // -- Delta (nur gültige headings) --
        Set<String> onlyInHeadingsNames = new TreeSet<>(validHeadingsNames);
        onlyInHeadingsNames.removeAll(allAngularNames);

        Set<String> onlyInAngularNames = new TreeSet<>(allAngularNames);
        onlyInAngularNames.removeAll(validHeadingsNames);

        Set<String> inBothNames = new TreeSet<>(validHeadingsNames);
        inBothNames.retainAll(allAngularNames);

        LOGGER.info("");
        LOGGER.info("=== DELTA ===");
        LOGGER.info("In beiden:             {}", inBothNames.size());
        LOGGER.info("Nur in headings.md:    {}", onlyInHeadingsNames.size());
        LOGGER.info("Nur in Angular JSON:   {}", onlyInAngularNames.size());
        LOGGER.info("Unvollständig:         {}", incomplete.size());

        // -- Unvollständige Einträge --
        if (!incomplete.isEmpty())
        {
            LOGGER.info("");
            LOGGER.info("=== UNVOLLSTÄNDIGE EINTRÄGE (fehlt Regel und/oder AP) ===");
            String lastBook = "";
            for (BoonEntry entry : incomplete)
            {
                if (!entry.book().equals(lastBook))
                {
                    if (!lastBook.isEmpty())
                    {
                        LOGGER.info("");
                    }
                    LOGGER.info("  --- {} ---", entry.book());
                    lastBook = entry.book();
                }
                String missing = (!entry.hasRegel() && !entry.hasAP()) ? "Regel, AP"
                    : !entry.hasRegel() ? "Regel"
                    : "AP";
                LOGGER.info("    [{}] {} (fehlt: {})", entry.type(), entry.name(), missing);
            }
        }

        // -- Nur in headings.md (sortiert, nur gültige) --
        if (!onlyInHeadingsNames.isEmpty())
        {
            LOGGER.info("");
            LOGGER.info("=== NUR IN HEADINGS.MD (fehlt in Angular) ===");
            printSortedEntries(validHeadings, onlyInHeadingsNames, boonOrder);
        }

        // -- Nur in Angular (sortiert) --
        if (!onlyInAngularNames.isEmpty())
        {
            LOGGER.info("");
            LOGGER.info("=== NUR IN ANGULAR JSON (fehlt in headings.md) ===");
            printSortedEntries(angularBoons, onlyInAngularNames, boonOrder);
        }
    }

    private static int typeOrder(String a, String b)
    {
        return typeRank(a) - typeRank(b);
    }

    private static int typeRank(String type)
    {
        return switch (type)
        {
            case TYPE_VORTEIL -> 0;
            case TYPE_NACHTEIL -> 1;
            default -> 2;
        };
    }

    private static void printSortedEntries(
        List<BoonEntry> allEntries,
        Set<String> filterNames,
        Comparator<BoonEntry> order)
    {
        // Nur Einträge deren Name im Filter-Set ist, dedupliziert nach Name
        List<BoonEntry> filtered = allEntries.stream()
            .filter(e -> filterNames.contains(e.name()))
            .sorted(order)
            .collect(Collectors.toList());

        // Gruppiert ausgeben: bei Buchwechsel Leerzeile
        String lastBook = "";
        for (BoonEntry entry : filtered)
        {
            if (!entry.book().equals(lastBook))
            {
                if (!lastBook.isEmpty())
                {
                    LOGGER.info("");
                }
                LOGGER.info("  --- {} ---", entry.book());
                lastBook = entry.book();
            }
            LOGGER.info("    [{}] {}", entry.type(), entry.name());
        }
    }
}
