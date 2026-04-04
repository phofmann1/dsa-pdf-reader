package de.pho.dsapdfreader.obsidian;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ordnet Content-Bilder den passenden Artikeln zu, basierend auf der Seitennummer.
 */
public class ImageMapper
{
    /**
     * Ordnet jedes Bild dem Artikel zu, dessen Seitenbereich die Bild-Seite enthaelt.
     */
    public static Map<Article, List<ImageRef>> mapImages(List<ImageRef> images, List<Article> articles)
    {
        Map<Article, List<ImageRef>> result = new LinkedHashMap<>();

        for (ImageRef img : images)
        {
            Article best = findArticleForPage(img.pageNumber, articles);
            if (best != null)
            {
                result.computeIfAbsent(best, k -> new ArrayList<>()).add(img);
            }
        }

        return result;
    }

    private static Article findArticleForPage(int page, List<Article> articles)
    {
        // Exaktes Match: Seite liegt im [startPage, endPage] Bereich
        for (Article a : articles)
        {
            if (page >= a.startPage && page <= a.endPage)
            {
                return a;
            }
        }

        // Fallback: naechstliegenden Artikel finden
        Article closest = null;
        int minDist = Integer.MAX_VALUE;
        for (Article a : articles)
        {
            int dist = Math.min(Math.abs(page - a.startPage), Math.abs(page - a.endPage));
            if (dist < minDist)
            {
                minDist = dist;
                closest = a;
            }
        }
        return closest;
    }
}
