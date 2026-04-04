package de.pho.dsapdfreader.obsidian;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stufe 3 der Pipeline: Erzeugt einen Obsidian-Vault aus _headings.md.
 *
 * Struktur:
 *   - Pro Publikation ein Ordner unter VAULT_BASE/Regionen/
 *   - Headings werden hierarchisch als Ordner/Notizen abgebildet
 *   - Heading mit Unter-Headings → Ordner + Folder Note (gleichnamige .md)
 *   - Heading ohne Unter-Headings → einfache Notiz (.md)
 *   - Body-Text landet in der jeweiligen Notiz/Folder Note
 */
public class ObsidianVaultGenerator
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String TEXT_BASE = "export/markdown/text";
    private static final String VAULT_BASE = "D:/Daten/Obsidian/Aventurien/Regionen";

    private static final String[] SCAN_SUBDIRS = {
        "04 - Regionen"
    };

    // Headings die uebersprungen werden (Impressum, TOC, Index, etc.)
    private static final String[] SKIP_HEADINGS = {
        "IMPRESSUM", "INHALTSVERZEICHNIS", "INDEX", "MYSTERIA & ARCANA"
    };

    public static void main(String[] args) throws Exception
    {
        Path textBase = Paths.get(TEXT_BASE);

        for (String subdir : SCAN_SUBDIRS)
        {
            Path scanDir = textBase.resolve(subdir);
            if (!Files.exists(scanDir))
            {
                LOGGER.warn("Verzeichnis existiert nicht: {}", scanDir);
                continue;
            }

            // Alle Buch-Verzeichnisse finden
            List<Path> bookDirs = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(scanDir, 3))
            {
                walk.filter(p -> p.getFileName().toString().equals("_headings.md"))
                    .map(Path::getParent)
                    .forEach(bookDirs::add);
            }

            for (Path bookDir : bookDirs)
            {
                Path headingsFile = bookDir.resolve("_headings.md");
                String bookName = extractBookName(headingsFile);
                if (bookName == null || bookName.isBlank())
                {
                    bookName = bookDir.getFileName().toString();
                }

                LOGGER.info("=== Vault: {} ===", bookName);
                Path vaultDir = Paths.get(VAULT_BASE).resolve(sanitizeFilename(bookName));
                processBook(headingsFile, vaultDir, bookName);
            }
        }

        LOGGER.info("=== Vault-Generierung abgeschlossen ===");
    }

    /**
     * Extrahiert den Buchtitel aus der ersten H2-Ueberschrift der _headings.md.
     */
    private static String extractBookName(Path headingsFile) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);
        for (String line : lines)
        {
            if (line.startsWith("## "))
            {
                return line.substring(3).trim();
            }
        }
        return null;
    }

    /**
     * Verarbeitet ein Buch: Parsed die _headings.md in einen Baum und schreibt Vault-Dateien.
     */
    private static void processBook(Path headingsFile, Path vaultDir, String bookName) throws IOException
    {
        List<String> lines = Files.readAllLines(headingsFile, StandardCharsets.UTF_8);

        // Headings und Body parsen
        List<HeadingNode> roots = parseHeadings(lines);

        // Vault-Verzeichnis erstellen und Dateien schreiben
        if (Files.exists(vaultDir))
        {
            // Altes Vault-Verzeichnis loeschen
            deleteRecursive(vaultDir);
        }
        Files.createDirectories(vaultDir);

        int totalFiles = 0;
        for (HeadingNode root : roots)
        {
            totalFiles += writeNode(root, vaultDir);
        }

        LOGGER.info("  {} Dateien geschrieben -> {}", totalFiles, vaultDir);
    }

    /**
     * Parsed _headings.md in eine Baumstruktur.
     * H1 (# ) = Kapitel-Wurzeln
     * H2 (## ) = Hauptabschnitte unter dem Kapitel
     * H3 (### ) = Unterabschnitte
     * H4 (#### ) = Detail-Abschnitte
     */
    private static List<HeadingNode> parseHeadings(List<String> lines)
    {
        List<HeadingNode> roots = new ArrayList<>();
        HeadingNode currentH1 = null;
        HeadingNode currentH2 = null;
        HeadingNode currentH3 = null;
        HeadingNode currentH4 = null;
        HeadingNode currentNode = null; // aktueller Knoten fuer Body-Text

        Pattern headingPattern = Pattern.compile("^(#{1,4})\\s+(.+)$");

        for (String line : lines)
        {
            // Seitenkommentare ignorieren
            if (line.startsWith("<!--") && line.contains("Seite")) continue;

            Matcher m = headingPattern.matcher(line);
            if (m.matches())
            {
                int level = m.group(1).length();
                String title = m.group(2).trim();

                // Falsche Headings filtern: zu lang (>120 Zeichen) = Body-Text mit #
                if (title.length() > 120)
                {
                    // Als Body-Text behandeln
                    if (currentNode != null) currentNode.body.add(line);
                    continue;
                }

                // Skip-Headings filtern
                if (shouldSkip(title)) continue;

                HeadingNode node = new HeadingNode(level, title);

                switch (level)
                {
                    case 1:
                        roots.add(node);
                        currentH1 = node;
                        currentH2 = null;
                        currentH3 = null;
                        currentH4 = null;
                        currentNode = node;
                        break;
                    case 2:
                        if (currentH1 != null)
                        {
                            currentH1.children.add(node);
                        }
                        else
                        {
                            roots.add(node);
                        }
                        currentH2 = node;
                        currentH3 = null;
                        currentH4 = null;
                        currentNode = node;
                        break;
                    case 3:
                        if (currentH2 != null)
                        {
                            currentH2.children.add(node);
                        }
                        else if (currentH1 != null)
                        {
                            currentH1.children.add(node);
                        }
                        else
                        {
                            roots.add(node);
                        }
                        currentH3 = node;
                        currentH4 = null;
                        currentNode = node;
                        break;
                    case 4:
                        if (currentH3 != null)
                        {
                            currentH3.children.add(node);
                        }
                        else if (currentH2 != null)
                        {
                            currentH2.children.add(node);
                        }
                        else if (currentH1 != null)
                        {
                            currentH1.children.add(node);
                        }
                        else
                        {
                            roots.add(node);
                        }
                        currentH4 = node;
                        currentNode = node;
                        break;
                }
            }
            else
            {
                // Body-Text zum aktuellen Knoten hinzufuegen
                if (currentNode != null && !line.isBlank())
                {
                    currentNode.body.add(line);
                }
                else if (currentNode != null && !currentNode.body.isEmpty())
                {
                    // Leerzeile beibehalten wenn schon Body vorhanden
                    currentNode.body.add("");
                }
            }
        }

        return roots;
    }

    /**
     * Prueft ob eine Ueberschrift uebersprungen werden soll.
     */
    private static boolean shouldSkip(String title)
    {
        String upper = title.toUpperCase().replaceAll("[*#]", "").trim();
        for (String skip : SKIP_HEADINGS)
        {
            if (upper.contains(skip)) return true;
        }
        return false;
    }

    /**
     * Schreibt einen HeadingNode als Vault-Datei oder -Ordner.
     * - Hat Kinder → Ordner + Folder Note (gleichnamige .md im Ordner)
     * - Hat keine Kinder → einfache .md Notiz
     */
    private static int writeNode(HeadingNode node, Path parentDir) throws IOException
    {
        String safeName = sanitizeFilename(node.title);
        if (safeName.isBlank()) return 0;

        int count = 0;

        if (node.children.isEmpty())
        {
            // Einfache Notiz
            Path noteFile = parentDir.resolve(safeName + ".md");
            writeNote(noteFile, node);
            count = 1;
        }
        else
        {
            // Ordner + Folder Note
            Path folder = parentDir.resolve(safeName);
            Files.createDirectories(folder);

            // Folder Note = gleichnamige Datei im Ordner
            Path folderNote = folder.resolve(safeName + ".md");
            writeNote(folderNote, node);
            count = 1;

            // Kinder rekursiv schreiben
            for (HeadingNode child : node.children)
            {
                count += writeNode(child, folder);
            }
        }

        return count;
    }

    /**
     * Schreibt eine Notiz-Datei mit Titel und Body-Text.
     */
    private static void writeNote(Path file, HeadingNode node) throws IOException
    {
        StringBuilder content = new StringBuilder();

        // Body-Text
        boolean hasBody = node.body.stream().anyMatch(l -> !l.isBlank());
        if (hasBody)
        {
            for (String line : node.body)
            {
                content.append(line).append("\n");
            }
            // Trailing Leerzeilen entfernen
            while (content.length() > 0 && content.charAt(content.length() - 1) == '\n'
                && content.length() > 1 && content.charAt(content.length() - 2) == '\n')
            {
                content.setLength(content.length() - 1);
            }
        }

        // Kinder als Links auflisten (fuer Folder Notes)
        if (!node.children.isEmpty())
        {
            if (hasBody) content.append("\n");
            for (HeadingNode child : node.children)
            {
                content.append("- [[").append(child.title).append("]]\n");
            }
        }

        Files.writeString(file, content.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Entfernt ungueltige Zeichen aus Dateinamen.
     */
    private static String sanitizeFilename(String name)
    {
        // Markdown-Formatierung entfernen
        name = name.replaceAll("\\*+", "");
        // Ungueltige Dateinamen-Zeichen ersetzen
        name = name.replaceAll("[<>:\"/\\\\|?*#]", "");
        // Mehrfache Leerzeichen normalisieren
        name = name.replaceAll("\\s+", " ");
        name = name.trim();
        // Laengenbegrenzung (Windows MAX_PATH)
        if (name.length() > 100) name = name.substring(0, 100).trim();
        return name;
    }

    /**
     * Loescht ein Verzeichnis rekursiv.
     */
    private static void deleteRecursive(Path dir) throws IOException
    {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir))
        {
            walk.sorted((a, b) -> b.toString().compareTo(a.toString()))
                .forEach(p ->
                {
                    try { Files.delete(p); }
                    catch (IOException e) { LOGGER.warn("Konnte nicht loeschen: {}", p); }
                });
        }
    }

    // =====================================================================
    // Datenmodell
    // =====================================================================

    static class HeadingNode
    {
        final int level;
        final String title;
        final List<HeadingNode> children = new ArrayList<>();
        final List<String> body = new ArrayList<>();

        HeadingNode(int level, String title)
        {
            this.level = level;
            this.title = title;
        }

        @Override
        public String toString()
        {
            return "#".repeat(level) + " " + title + " (" + children.size() + " children)";
        }
    }
}
