package de.pho.dsapdfreader.obsidian;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Artikel im Obsidian-Vault, erzeugt aus einem ## Heading der _volltext.md.
 */
public class Article
{
    public String title;
    public String chapterFolder;   // z.B. "Land und Leute"
    public String subFolder;       // z.B. "Zentrales Kalifat" (Unterordner in Land und Leute)
    public boolean isRegionMarker; // true wenn dieser Artikel ein Region-Heading ist (wird zum Unterordner)
    public List<String> bodyLines = new ArrayList<>();
    public int startPage;
    public int endPage;
    public int headingLevel; // 2 = ##, 3 = ###
    public String sourceBook;

    public Article(String title, int headingLevel, int startPage, String sourceBook)
    {
        this.title = title;
        this.headingLevel = headingLevel;
        this.startPage = startPage;
        this.endPage = startPage;
        this.sourceBook = sourceBook;
    }

    /**
     * Gibt den vollstaendigen Pfad relativ zum Buch-Ordner zurueck.
     */
    public String relativePath()
    {
        if (subFolder != null && !subFolder.isEmpty())
        {
            return chapterFolder + "/" + subFolder;
        }
        return chapterFolder;
    }

    /**
     * Erzeugt einen sicheren Dateinamen aus dem Titel.
     */
    public String safeFilename()
    {
        return title
            .replaceAll("[<>:\"/\\\\|?*]", "")
            .replaceAll("\\s+", " ")
            .trim();
    }
}
