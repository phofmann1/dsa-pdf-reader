package de.pho.dsapdfreader.obsidian;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parst eine _volltext.md in eine Liste von Artikeln.
 * Splittet an ## Headings, entfernt Noise-Zeilen, behaelt den Originaltext.
 *
 * Kapitelzuordnung:
 *   Die DSA-Buecher haben Kapitel-Titelseiten mit ALL-CAPS Text wie "LAND & LEUTE".
 *   Diese werden als Kapitelmarker erkannt und setzen den Ordner fuer nachfolgende Artikel.
 *   Artikel VOR dem ersten Kapitelmarker landen im Ordner "Überblick".
 */
public class VolltextParser
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Pattern PAGE_MARKER = Pattern.compile("^# Seite (\\d+)$");
    private static final Pattern HEADING_2 = Pattern.compile("^## (.+)$");
    private static final Pattern SOURCE_HEADER = Pattern.compile("^# Quelle:");
    private static final Pattern COLUMN_MARKER = Pattern.compile("^<!-- (Spalte [12]|Vollbreite) -->$");
    private static final Pattern SEPARATOR = Pattern.compile("^---$");
    private static final Pattern PAGE_FOOTER = Pattern.compile("^(\\d{1,3})\\s+[A-ZÄÖÜ]");

    // Impressum-Schluesselwoerter
    private static final Set<String> IMPRESSUM_KEYWORDS = Set.of(
        "Verlagsleitung", "Redaktionsleitung", "Redaktion", "Regelredaktion",
        "Autor:innen", "Autoren", "Autorinnen", "Lektorat", "Korrektorat",
        "Künstlerische Leitung", "Coverbild", "Satz, Layout", "Layoutdesign",
        "Innenillustrationen", "Ulisses Spiele", "IMPRESSUM"
    );

    // Woerter die in ToC-Seiten als ## Headings auftauchen aber keine echten Artikel sind
    private static final Pattern TOC_LINE_PATTERN = Pattern.compile(".*\\b\\d{1,3}\\b.*\\b\\d{1,3}\\b");

    public List<Article> parse(Path volltextFile, String sourceBook) throws IOException
    {
        List<String> allLines = Files.readAllLines(volltextFile, StandardCharsets.UTF_8);
        List<Article> articles = new ArrayList<>();

        int currentPage = 0;
        boolean inImpressum = false;
        int impressumEndPage = 12;
        Article currentArticle = null;
        String currentChapter = "Überblick"; // Default fuer Einleitung
        String currentSubRegion = null;      // Unterordner in Land und Leute
        RegionClassifier regionClassifier = new RegionClassifier();

        for (int i = 0; i < allLines.size(); i++)
        {
            String line = allLines.get(i);

            // Quellenheader ueberspringen
            if (SOURCE_HEADER.matcher(line).find()) continue;

            // Seitenmarker erkennen und tracken
            Matcher pageMatcher = PAGE_MARKER.matcher(line);
            if (pageMatcher.matches())
            {
                currentPage = Integer.parseInt(pageMatcher.group(1));
                if (currentPage > impressumEndPage) inImpressum = false;
                continue;
            }

            // Trennlinien und Spaltenmarker entfernen
            if (SEPARATOR.matcher(line).matches()) continue;
            if (COLUMN_MARKER.matcher(line).matches()) continue;

            // Seitenfusszeilen entfernen (z.B. "18 Land & Leute", "94 Götter & Dämonen")
            if (isPageFooter(line, currentPage)) continue;

            // Grossbuchstaben-Kapitelheadings erkennen (z.B. "LAND & LEUTE" als Plaintext)
            // Diese stehen als alleinstehende Zeilen auf Kapitel-Titelseiten
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && isStandaloneChapterTitle(trimmed))
            {
                String folder = ChapterClassifier.getChapterFolder(trimmed);
                if (folder != null)
                {
                    currentChapter = folder;
                    currentSubRegion = null; // Sub-Region reset bei Kapitelwechsel!
                    // Vorherigen Artikel abschliessen
                    if (currentArticle != null) currentArticle.endPage = currentPage;
                    currentArticle = null; // Kein eigener Artikel fuer Kapitelseite
                    continue;
                }
            }

            // ## Heading erkennen
            Matcher h2Matcher = HEADING_2.matcher(line);
            if (h2Matcher.matches())
            {
                String headingText = h2Matcher.group(1).trim();

                // Zusammengeklebte Headings auftrennen
                // z.B. "Achmad'Sunni Hadjinim-OrdenEssen & Trinken" -> nur "Essen & Trinken" nehmen
                // Erkennung: Grossbuchstabe mitten im Wort ohne Leerzeichen
                // Wir splitten den Heading am besten in bekannte Kapitel-Teile
                headingText = cleanGluedHeading(headingText);

                // Impressum-Headings ueberspringen
                if (currentPage <= impressumEndPage && isImpressumHeading(headingText))
                {
                    inImpressum = true;
                    continue;
                }

                // Inhaltsverzeichnis ueberspringen (Seiten 4-12, Headings mit vielen Zahlen)
                if (currentPage <= impressumEndPage && isTocContent(headingText)) continue;

                // VORWORT und kurze Gebrauchsanweisung ueberspringen
                if (headingText.equalsIgnoreCase("Vorwort")) continue;
                if (headingText.contains("Gebrauchsanweisung")) continue;

                // Split-Heading erkennen: zwei ## direkt hintereinander
                // (Titel ueber zwei Spalten verteilt)
                if (looksLikeSplitHeading(allLines, i))
                {
                    String merged = mergeSplitHeading(allLines, i);
                    if (merged != null)
                    {
                        headingText = merged;
                        // Naechste ## Zeile ueberspringen
                        i = skipToNextHeading(allLines, i);
                    }
                }

                // Kapitel-Heading das als ## auftaucht (z.B. "## Kultur & Wissenschaft")
                String chapterFolder = ChapterClassifier.getChapterFolder(headingText);
                if (chapterFolder != null)
                {
                    currentChapter = chapterFolder;
                    currentSubRegion = null; // Sub-Region reset bei neuem Kapitel
                    if (currentArticle != null) currentArticle.endPage = currentPage;
                    currentArticle = null;
                    continue;
                }

                // Region-Heading innerhalb von "Land und Leute"?
                // z.B. "Zentrales Kalifat", "Nördliches Kalifat" -> wird Unterordner
                if ("Land und Leute".equals(currentChapter) && regionClassifier.isRegionHeading(headingText))
                {
                    currentSubRegion = headingText;

                    // Region-Heading wird zum Folder-Note-Artikel (hat selbst Content)
                    if (currentArticle != null) currentArticle.endPage = currentPage;
                    currentArticle = new Article(headingText, 2, currentPage, sourceBook);
                    currentArticle.chapterFolder = currentChapter;
                    currentArticle.subFolder = currentSubRegion;
                    currentArticle.isRegionMarker = true;
                    articles.add(currentArticle);
                    continue;
                }

                // "Städte, Dörfer und Oasen" markiert den Beginn der Stadt-Liste
                // Ab hier wird currentSubRegion zurueckgesetzt, damit Staedte
                // per Post-Processing ihrer Region zugeordnet werden
                if ("Land und Leute".equals(currentChapter) && headingText.toLowerCase().contains("städte"))
                {
                    currentSubRegion = null;
                }

                // Neuen Artikel starten
                if (currentArticle != null) currentArticle.endPage = currentPage;

                currentArticle = new Article(headingText, 2, currentPage, sourceBook);
                currentArticle.chapterFolder = currentChapter;
                currentArticle.subFolder = currentSubRegion;
                articles.add(currentArticle);
                continue;
            }

            // Impressum-Inhalt ueberspringen
            if (inImpressum) continue;

            // Zeile zum aktuellen Artikel hinzufuegen
            if (currentArticle != null)
            {
                currentArticle.bodyLines.add(line);
            }
        }

        // Letzten Artikel abschliessen
        if (currentArticle != null) currentArticle.endPage = currentPage;

        // Post-Processing: Staedte in "Land und Leute" der richtigen Region zuordnen
        // basierend auf "**Region:**" Angabe im Body-Text
        assignCitiesToRegions(articles);

        LOGGER.info("Parsed {} articles from {}", articles.size(), volltextFile.getFileName());
        return articles;
    }

    /**
     * Erkennt ob eine Plaintext-Zeile ein alleinstehender Kapiteltitel ist.
     * Kapiteltitel sind ALL-CAPS und bestehen nur aus Buchstaben, Leerzeichen, & und Umlauten.
     * Sie muessen auch ein bekanntes Kapitel-Pattern matchen.
     */
    private boolean isStandaloneChapterTitle(String line)
    {
        // Muss ALL CAPS sein (oder fast - erlaubt & und Umlaute)
        if (!line.matches("^[A-ZÄÖÜ\\s&]+$")) return false;
        // Mindestens 5 Zeichen
        if (line.length() < 5) return false;
        // Muss ein bekanntes Kapitel sein
        return ChapterClassifier.getChapterFolder(line) != null;
    }

    private boolean isImpressumHeading(String heading)
    {
        for (String keyword : IMPRESSUM_KEYWORDS)
        {
            if (heading.contains(keyword)) return true;
        }
        return false;
    }

    private boolean isTocContent(String heading)
    {
        // ToC-Headings enthalten typischerweise Seitenzahlen am Ende
        if (heading.matches(".*\\s+\\d{1,3}\\s*$")) return true;
        if (heading.toUpperCase().contains("INHALTSVERZEICHNIS")) return true;
        // Regeln & Tabellen Karten & Stadtpläne
        if (heading.contains("Tabellen") && heading.contains("Karten")) return true;
        return false;
    }

    /**
     * Erkennt ob ein ## Heading ein Split-Heading ist (ein Titel auf zwei Spalten verteilt).
     *
     * Echtes Split-Heading: "## Das Wüstenreich – Die Wü" / "## ste Khôm und Thalusien"
     *   -> zweiter Teil beginnt mit Kleinbuchstabe (Fortsetzung)
     *
     * KEIN Split: "## Achmad'Sunni" / "## Essen & Trinken"
     *   -> zweiter Teil beginnt mit Grossbuchstabe (neues eigenstaendiges Heading)
     */
    private boolean looksLikeSplitHeading(List<String> lines, int currentIndex)
    {
        for (int j = currentIndex + 1; j < lines.size() && j < currentIndex + 4; j++)
        {
            String next = lines.get(j).trim();
            if (next.isEmpty()) continue;
            if (COLUMN_MARKER.matcher(next).matches()) continue;
            if (SEPARATOR.matcher(next).matches()) continue;
            if (PAGE_MARKER.matcher(next).matches()) continue;

            Matcher m = HEADING_2.matcher(next);
            if (m.matches())
            {
                String nextText = m.group(1).trim();
                // Nur ein echtes Split wenn der Folgetext mit Kleinbuchstabe beginnt
                // (= Wortfortsetzung, kein neuer Satz/Titel)
                if (!nextText.isEmpty() && Character.isLowerCase(nextText.charAt(0)))
                {
                    return true;
                }
                // Oder wenn der erste Teil mit Artikel/Praeposition endet
                // (der, die, das, des, dem, den, von, zu, im, am, zum, zur, in)
                // oder mit Bindestrich - das deutet auf abgebrochenen Titel hin
                Matcher m1 = HEADING_2.matcher(lines.get(currentIndex));
                if (m1.matches())
                {
                    String firstText = m1.group(1).trim();
                    if (firstText.endsWith("-") || firstText.endsWith("–"))
                    {
                        return true;
                    }
                    // Endet mit kurzem Funktionswort?
                    String lastWord = firstText.contains(" ")
                        ? firstText.substring(firstText.lastIndexOf(' ') + 1)
                        : "";
                    if (lastWord.matches("(?i)(der|die|das|des|dem|den|von|zu|im|am|zum|zur|in|und|&|v\\.)"))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * Merged ein Split-Heading: nimmt den Text beider ## Zeilen zusammen.
     */
    private String mergeSplitHeading(List<String> lines, int startIndex)
    {
        String first = HEADING_2.matcher(lines.get(startIndex)).matches()
            ? HEADING_2.matcher(lines.get(startIndex)).replaceFirst("$1").trim()
            : "";

        for (int j = startIndex + 1; j < lines.size() && j < startIndex + 4; j++)
        {
            String next = lines.get(j).trim();
            if (next.isEmpty() || COLUMN_MARKER.matcher(next).matches()
                || SEPARATOR.matcher(next).matches() || PAGE_MARKER.matcher(next).matches())
                continue;

            Matcher m = HEADING_2.matcher(next);
            if (m.matches())
            {
                // Leerzeichen einfuegen wenn nicht vorhanden
                String second = m.group(1).trim();
                if (!first.endsWith(" ") && !first.endsWith("-"))
                {
                    return first + " " + second;
                }
                return first + second;
            }
        }
        return first;
    }

    /**
     * Springt zur naechsten ## Zeile und gibt deren Index zurueck.
     */
    private int skipToNextHeading(List<String> lines, int startIndex)
    {
        for (int j = startIndex + 1; j < lines.size() && j < startIndex + 4; j++)
        {
            if (HEADING_2.matcher(lines.get(j).trim()).matches()) return j;
        }
        return startIndex;
    }

    /**
     * Bereinigt zusammengeklebte Headings die durch die PDF-Extraktion entstehen.
     * z.B. "Spiel & SportHandwerk & Technik" -> "Spiel & Sport"
     * z.B. "Maße und GewichteHandel & Dienstleistungen" -> "Maße und Gewichte"
     * z.B. "Die Kunst derThalushim" -> "Die Kunst der Thalushim"
     *
     * Erkennung: Ein Grossbuchstabe der direkt an einen Kleinbuchstaben grenzt
     * und ein neues Wort beginnt.
     */
    /**
     * Post-Processing: Ordnet Staedte-Artikel in "Land und Leute" der richtigen Region zu.
     * Sucht "**Region:**" im Body-Text und matched gegen bekannte Region-Unterordner.
     * Staedte ohne Region-Angabe oder ohne Match bleiben im Haupt-Land-und-Leute-Ordner.
     */
    private void assignCitiesToRegions(List<Article> articles)
    {
        // Sammle alle bekannten Region-Unterordner
        Set<String> knownRegions = new LinkedHashSet<>();
        for (Article a : articles)
        {
            if (a.isRegionMarker && a.subFolder != null)
            {
                knownRegions.add(a.subFolder);
            }
        }
        if (knownRegions.isEmpty()) return;

        // Pattern fuer "**Region:** xxx" in Body
        Pattern regionInBody = Pattern.compile("\\*\\*Region:?\\*\\*\\s*(.+)");

        for (Article a : articles)
        {
            if (!"Land und Leute".equals(a.chapterFolder)) continue;
            if (a.isRegionMarker) continue; // Regionen selbst nicht aendern
            if (a.subFolder != null && !a.subFolder.isEmpty()) continue; // Schon zugeordnet

            // Suche "**Region:**" in den ersten 10 Zeilen
            for (int i = 0; i < Math.min(10, a.bodyLines.size()); i++)
            {
                String line = a.bodyLines.get(i);
                Matcher m = regionInBody.matcher(line);
                if (m.find())
                {
                    String regionText = m.group(1).trim();
                    // Matche gegen bekannte Regionen
                    for (String region : knownRegions)
                    {
                        if (regionText.toLowerCase().contains(region.toLowerCase()))
                        {
                            a.subFolder = region;
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private String cleanGluedHeading(String heading)
    {
        // Finde Stellen wo ein Kleinbuchstabe direkt von einem Grossbuchstaben gefolgt wird
        // z.B. "SportHandwerk" -> split bei "tH" -> nehme "Sport"
        // z.B. "OrdenEssen" -> split bei "nE" -> nehme "Orden"
        java.util.regex.Matcher m = Pattern.compile("([a-zäöü])([A-ZÄÖÜ][a-zäöü])").matcher(heading);
        if (m.find())
        {
            String firstPart = heading.substring(0, m.start() + 1).trim();
            if (firstPart.length() >= 4) return firstPart;
        }

        // Auch Fälle wie "GewichteHandel" erkennen (Kleinbuchstabe + Grossbuchstabe)
        // wo der Split kein sinnvolles erstes Wort ergibt, dann Leerzeichen einfuegen
        java.util.regex.Matcher m2 = Pattern.compile("([a-zäöü])([A-ZÄÖÜ])").matcher(heading);
        if (m2.find())
        {
            return heading.substring(0, m2.start() + 1) + " " + heading.substring(m2.start() + 1);
        }

        return heading;
    }

    private boolean isPageFooter(String line, int currentPage)
    {
        if (currentPage <= 0) return false;
        String t = line.trim();
        Matcher m = PAGE_FOOTER.matcher(t);
        if (!m.find()) return false;
        int num = Integer.parseInt(m.group(1));
        // Seitenfusszeile: Nummer nahe aktueller Seite UND kurze Zeile
        return Math.abs(num - currentPage) <= 1 && t.length() < 50;
    }
}
