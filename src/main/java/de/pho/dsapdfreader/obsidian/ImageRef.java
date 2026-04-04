package de.pho.dsapdfreader.obsidian;

import java.nio.file.Path;

/**
 * Referenz auf ein Content-Bild mit Seitenzuordnung.
 */
public class ImageRef
{
    public Path sourcePath;
    public String targetFilename;
    public int pageNumber;

    public ImageRef(Path sourcePath, String targetFilename, int pageNumber)
    {
        this.sourcePath = sourcePath;
        this.targetFilename = targetFilename;
        this.pageNumber = pageNumber;
    }
}
