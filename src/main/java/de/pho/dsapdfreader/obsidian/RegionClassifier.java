package de.pho.dsapdfreader.obsidian;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Erkennt Regions-Headings innerhalb von "Land und Leute" die als Unterordner dienen.
 *
 * Bekannte Muster:
 * - Himmelsrichtung + Kalifat/Reich/Land (z.B. "Zentrales Kalifat", "Nördliches Kalifat")
 * - Grafschaft/Herzogtum/Fürstentum/Baronie + Name
 * - Benannte Regionen (z.B. "Das Abagund", "Die Muhrsape")
 *
 * Nicht als Region: Einzelne Städte, Oasen, Dörfer (die gehören UNTER eine Region)
 */
public class RegionClassifier
{
    // Bekannte Region-Patterns (Titel die einen Unterordner erzeugen)
    private static final Set<Pattern> REGION_PATTERNS = new LinkedHashSet<>();

    static
    {
        // Himmelsrichtung + politische Einheit
        add("(?i)^(Zentrales|Nördliches|Südliches|Östliches|Westliches|Inneres|Äußeres)\\s+.+$");
        // Politische Einheiten als Präfix
        add("(?i)^(Grafschaft|Herzogtum|Fürstentum|Baronie|Markgrafschaft|Landgrafschaft|Provinz)\\s+.+$");
        // "Das/Die/Der X-land/reich/wald/küste/..."
        add("(?i)^(Das|Die|Der)\\s+\\w+(land|reich|wald|küste|inseln|berge|marschen|moorland|hinterland|ebene|becken|tal|steppe)$");
        // Radjarat (Dornenreich)
        add("(?i)^Radjarat\\s+.+$");
    }

    private static void add(String regex)
    {
        REGION_PATTERNS.add(Pattern.compile(regex));
    }

    // Dynamisch erkannte Regionen pro Buch (aus dem Text gelernt)
    private final Set<String> knownRegions = new LinkedHashSet<>();

    /**
     * Registriert einen Regionsnamen der als Unterordner dient.
     */
    public void registerRegion(String regionName)
    {
        knownRegions.add(regionName);
    }

    /**
     * Prueft ob ein Heading-Text eine Region ist die einen Unterordner verdient.
     */
    public boolean isRegionHeading(String heading)
    {
        if (knownRegions.contains(heading)) return true;

        for (Pattern p : REGION_PATTERNS)
        {
            if (p.matcher(heading).matches()) return true;
        }
        return false;
    }

    /**
     * Findet die passende Region fuer einen Artikel basierend auf Seitennummer.
     * Artikel werden der Region zugeordnet die vor ihnen kommt.
     */
    public String findRegionForPage(int page, java.util.List<Article> articles)
    {
        String lastRegion = null;
        for (Article a : articles)
        {
            if (a.isRegionMarker && a.startPage <= page)
            {
                lastRegion = a.title;
            }
        }
        return lastRegion;
    }
}
