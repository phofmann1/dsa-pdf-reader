package de.pho.dsapdfreader.obsidian;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Filtert Ornament-Bilder (Pergament-Hintergruende, Trennleisten) aus
 * und behaelt nur Content-Bilder (Illustrationen, Karten, Portraits).
 *
 * Heuristik:
 * - Dateigroessen, die >10x vorkommen = Hintergrund-Textur (identisch auf jeder Seite)
 * - Dateien <25 KB = kleine Ornamente/Trennleisten
 */
public class ImageFilter
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Pattern IMAGE_PATTERN = Pattern.compile("seite_(\\d+)_bild_(\\d+)\\.png");
    private static final int MIN_SIZE_BYTES = 25_000;       // Unter 25KB = Ornament
    private static final int BACKGROUND_REPEAT_THRESHOLD = 10; // >10 gleiche Groesse = Hintergrund

    /**
     * Filtert Content-Bilder aus einem Bildverzeichnis.
     * @return Liste von ImageRef mit Seitenzuordnung
     */
    public static List<ImageRef> filterContentImages(Path imageDir, String prefix) throws IOException
    {
        if (imageDir == null || !Files.isDirectory(imageDir)) return List.of();

        // Alle PNG-Dateien sammeln
        List<Path> allImages = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(imageDir))
        {
            walk.filter(p -> p.toString().toLowerCase().endsWith(".png"))
                .filter(p -> IMAGE_PATTERN.matcher(p.getFileName().toString()).matches())
                .forEach(allImages::add);
        }

        if (allImages.isEmpty()) return List.of();

        // Schritt 1: Nach Dateigroesse gruppieren
        Map<Long, List<Path>> sizeGroups = new HashMap<>();
        for (Path img : allImages)
        {
            long size = Files.size(img);
            sizeGroups.computeIfAbsent(size, k -> new ArrayList<>()).add(img);
        }

        // Schritt 2: Hintergrund-Groessen identifizieren (>10 identische)
        var backgroundSizes = new java.util.HashSet<Long>();
        for (Map.Entry<Long, List<Path>> entry : sizeGroups.entrySet())
        {
            if (entry.getValue().size() > BACKGROUND_REPEAT_THRESHOLD)
            {
                backgroundSizes.add(entry.getKey());
                LOGGER.debug("Background size: {} bytes ({} files)", entry.getKey(), entry.getValue().size());
            }
        }

        // Schritt 3: Filtern
        List<ImageRef> contentImages = new ArrayList<>();
        int skippedBackground = 0;
        int skippedSmall = 0;

        for (Path img : allImages)
        {
            long size = Files.size(img);

            if (backgroundSizes.contains(size))
            {
                skippedBackground++;
                continue;
            }

            if (size < MIN_SIZE_BYTES)
            {
                skippedSmall++;
                continue;
            }

            String filename = img.getFileName().toString();
            Matcher m = IMAGE_PATTERN.matcher(filename);
            if (m.matches())
            {
                int page = Integer.parseInt(m.group(1));
                String targetName = prefix.isEmpty() ? filename : prefix + filename;
                contentImages.add(new ImageRef(img, targetName, page));
            }
        }

        LOGGER.info("Images in {}: {} total, {} content, {} background, {} small ornaments",
            imageDir.getFileName(), allImages.size(), contentImages.size(),
            skippedBackground, skippedSmall);

        return contentImages;
    }
}
