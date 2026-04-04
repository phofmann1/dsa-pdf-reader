package de.pho.dsapdfreader.obsidian;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Erzeugt [[Wikilinks]] in Artikeltexten basierend auf den gesammelten Heading-Titeln.
 */
public class WikilinkGenerator
{
    private final Set<String> allTitles = new LinkedHashSet<>();

    /**
     * Sammelt alle Artikel-Titel fuer spaetere Verlinkung.
     */
    public void collectHeadings(List<Article> articles)
    {
        for (Article a : articles)
        {
            if (a.title != null && !a.title.isBlank())
            {
                allTitles.add(a.title.trim());
            }
        }
    }

    /**
     * Ersetzt Vorkommen bekannter Titel im Text durch [[Wikilinks]].
     * Nur das erste Vorkommen pro Titel wird ersetzt.
     * Der eigene Titel wird nicht verlinkt.
     *
     * @param text der Artikeltext
     * @param ownTitle der Titel des Artikels selbst (wird ausgelassen)
     * @return Text mit eingefuegten Wikilinks
     */
    public String applyWikilinks(String text, String ownTitle)
    {
        if (allTitles.isEmpty()) return text;

        // Sortiere nach Laenge absteigend (laengste zuerst, vermeidet Teilmatches)
        List<String> sorted = new ArrayList<>(allTitles);
        sorted.sort(Comparator.comparingInt(String::length).reversed());

        for (String title : sorted)
        {
            if (title.equals(ownTitle)) continue;
            if (title.length() < 10) continue; // Kurze/generische Titel ignorieren
            // Einworttitel nur wenn sie wirklich lang sind (Eigennamen)
            if (!title.contains(" ") && title.length() < 15) continue;
            // Generische Begriffe nie verlinken
            if (title.matches("(?i)(Stadtrundgang|Geschichte|Herrschaft|Bewohner|Tiere|Handel|Räumlichkeiten|Schlafplätze|Kunsthandwerk|Land & Leute|Helden|Regeln)")) continue;

            // Nur ersetzen wenn nicht bereits in [[ ]] oder in einem Heading
            String escaped = Pattern.quote(title);
            // Suche das Wort, aber nicht wenn es Teil eines laengeren Worts ist
            // und nicht wenn es bereits in [[]] steht
            String pattern = "(?<!\\[\\[)(?<![#])\\b" + escaped + "\\b(?!\\]\\])";

            try
            {
                String replacement = "[[" + title + "]]";
                // Nur erstes Vorkommen ersetzen
                text = text.replaceFirst(pattern, replacement);
            }
            catch (Exception e)
            {
                // Regex-Fehler bei speziellen Zeichen ignorieren
            }
        }

        return text;
    }
}
