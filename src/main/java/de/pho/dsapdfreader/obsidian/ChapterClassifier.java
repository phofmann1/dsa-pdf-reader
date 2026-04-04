package de.pho.dsapdfreader.obsidian;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Ordnet Artikel anhand ihres Titels einem Kapitel-Ordner zu.
 *
 * DSA-Regionalspielhilfen folgen einer einheitlichen Kapitelstruktur.
 * Kapitel-Headings werden erkannt und setzen den Ordner fuer nachfolgende Artikel.
 */
public class ChapterClassifier
{
    /**
     * Mapping von Regex-Pattern auf Ordnernamen.
     * Reihenfolge ist relevant: erstes Match gewinnt.
     */
    private static final Map<Pattern, String> CHAPTER_PATTERNS = new LinkedHashMap<>();

    static
    {
        // Hauptkapitel - exakte Matches (ALL CAPS Titel auf Kapitelseiten)
        add("^LAND\\s*[&+]\\s*LEUTE$", "Land und Leute");
        add("^KULTUR\\s*[&+]\\s*WISSENSCHAFT$", "Kultur und Wissenschaft");
        add("^HANDEL\\s*[&+]\\s*WANDEL$", "Handel und Wandel");
        add("^HANDEL\\s*[&+]\\s*WIRTSCHAFT$", "Handel und Wandel");
        add("^G.TTER\\s*[&+]\\s*D.MONEN$", "Götter und Dämonen");
        add("^G.TTER\\s*[&+]\\s*MAGIE$", "Götter und Dämonen");
        add("^GLAUBE\\s*[&+]\\s*MYTHOS$", "Götter und Dämonen");
        add("^ZAUBEREI\\s*[&+]\\s*HEXENWERK$", "Zauberei und Hexenwerk");
        add("^ZAUBEREI\\s*[&+]\\s*MAGIE$", "Zauberei und Hexenwerk");
        add("^FLORA\\s*[&+]\\s*FAUNA$", "Flora und Fauna");
        add("^RANG\\s*[&+]\\s*NAMEN$", "Rang und Namen");
        add("^MYTHOS\\s*[&+]\\s*HISTORIE$", "Mythos und Historie");
        add("^MYSTERIA\\s*[&+]\\s*ARCANA$", "Mysteria und Arcana");
        add("^HELDEN\\s", "Helden");
        add("^RECHT\\s*[&+]\\s*GESETZ$", "Handel und Wandel");
        add("^SEEFAHRT\\s*[&+]", "Kultur und Wissenschaft");
        // Auch gemischte Gross/Kleinschreibung (aus ## Headings)
        add("(?i)^Land\\s*[&+]\\s*Leute$", "Land und Leute");
        add("(?i)^Kultur\\s*[&+]\\s*Wissenschaft$", "Kultur und Wissenschaft");
        add("(?i)^Handel\\s*[&+]\\s*Wandel$", "Handel und Wandel");
        add("(?i)^G.tter\\s*[&+]\\s*D.monen$", "Götter und Dämonen");
        add("(?i)^Zauberei\\s*[&+]\\s*Hexenwerk$", "Zauberei und Hexenwerk");
        add("(?i)^Flora\\s*[&+]\\s*Fauna$", "Flora und Fauna");
        add("(?i)^Rang\\s*[&+]\\s*Namen$", "Rang und Namen");
        add("(?i)^Mythos\\s*[&+]\\s*Historie$", "Mythos und Historie");
        add("(?i)^Mysteria\\s*[&+]\\s*Arcana$", "Mysteria und Arcana");
    }

    private static void add(String regex, String folder)
    {
        CHAPTER_PATTERNS.put(Pattern.compile(regex), folder);
    }

    /**
     * Prueft ob ein Heading-Text ein Kapitel-Heading ist (Vollkapitel-Ueberschrift).
     * Diese werden als Ordner-Marker verwendet, nicht als eigene Artikel.
     */
    public static boolean isChapterHeading(String heading)
    {
        String cleaned = heading.replaceAll("\\*", "").trim();
        for (Pattern p : CHAPTER_PATTERNS.keySet())
        {
            if (p.matcher(cleaned).find()) return true;
        }
        return false;
    }

    /**
     * Gibt den Ordnernamen fuer ein Kapitel-Heading zurueck.
     */
    public static String getChapterFolder(String heading)
    {
        String cleaned = heading.replaceAll("\\*", "").trim();
        for (Map.Entry<Pattern, String> entry : CHAPTER_PATTERNS.entrySet())
        {
            if (entry.getKey().matcher(cleaned).find())
            {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Klassifiziert alle Artikel einer Liste: setzt chapterFolder basierend
     * auf Kapitel-Headings und propagiert den Ordner auf nachfolgende Artikel.
     */
    public static void classifyAll(java.util.List<Article> articles)
    {
        String currentChapter = "";

        for (int i = 0; i < articles.size(); i++)
        {
            Article article = articles.get(i);
            String folder = getChapterFolder(article.title);

            if (folder != null)
            {
                // Das ist ein Kapitel-Heading -> als Marker verwenden
                currentChapter = folder;
                article.chapterFolder = folder;
            }
            else
            {
                article.chapterFolder = currentChapter;
            }
        }
    }
}
