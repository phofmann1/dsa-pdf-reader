package de.pho.dsapdfreader.obsidian;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Kontext fuer ein einzelnes Buch waehrend der Vault-Erzeugung.
 */
public class BookContext
{
    public String bookDirName;
    public Path textDir;
    public Path imageDir;
    public String vaultFolder;
    public String imagePrefix;
    public List<Article> articles = new ArrayList<>();

    public BookContext(String bookDirName, Path textDir, Path imageDir,
                       String vaultFolder, String imagePrefix)
    {
        this.bookDirName = bookDirName;
        this.textDir = textDir;
        this.imageDir = imageDir;
        this.vaultFolder = vaultFolder;
        this.imagePrefix = imagePrefix;
    }
}
