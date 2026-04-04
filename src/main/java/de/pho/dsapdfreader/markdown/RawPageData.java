package de.pho.dsapdfreader.markdown;

import java.util.List;

/**
 * Rohdaten einer einzelnen PDF-Seite.
 * Enthält alle Zeichenpositionen mit vollständigen Metadaten.
 * Wird als JSON serialisiert und dient als Eingabe für die Text-Interpretation.
 */
public class RawPageData
{
    public int pageNumber;
    public float pageWidth;
    public float pageHeight;
    public List<RawChar> chars;
    public List<RawImage> images;
    public List<RawRect> rects;

    public RawPageData()
    {
    }

    public RawPageData(int pageNumber, float pageWidth, float pageHeight, List<RawChar> chars)
    {
        this.pageNumber = pageNumber;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.chars = chars;
    }

    public RawPageData(int pageNumber, float pageWidth, float pageHeight,
                       List<RawChar> chars, List<RawImage> images)
    {
        this.pageNumber = pageNumber;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.chars = chars;
        this.images = images;
    }

    /**
     * Ein einzelnes Zeichen mit allen PDF-Metadaten.
     */
    public static class RawChar
    {
        public String text;
        public float x;
        public float y;
        public float width;
        public float height;
        public float fontSize;
        public String fontName;
        public boolean bold;
        public boolean italic;

        public RawChar()
        {
        }

        public RawChar(String text, float x, float y, float width, float height,
                       float fontSize, String fontName, boolean bold, boolean italic)
        {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fontSize = fontSize;
            this.fontName = fontName;
            this.bold = bold;
            this.italic = italic;
        }
    }

    /**
     * Ein einzelnes Bild mit Position und Metadaten aus dem PDF.
     */
    public static class RawImage
    {
        public int imageIndex;
        public float x;
        public float y;
        public float width;
        public float height;
        public int pixelWidth;
        public int pixelHeight;
        public String suffix;
        public int colorSpaceComponents;
        public int bitsPerComponent;
        public String checksum;
        public String filename;
        public String origin; // Original-Dateiname bei Duplikaten, sonst null

        public RawImage()
        {
        }

        public RawImage(int imageIndex, float x, float y, float width, float height,
                        int pixelWidth, int pixelHeight, String suffix,
                        int colorSpaceComponents, int bitsPerComponent,
                        String checksum, String filename, String origin)
        {
            this.imageIndex = imageIndex;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.pixelWidth = pixelWidth;
            this.pixelHeight = pixelHeight;
            this.suffix = suffix;
            this.colorSpaceComponents = colorSpaceComponents;
            this.bitsPerComponent = bitsPerComponent;
            this.checksum = checksum;
            this.filename = filename;
            this.origin = origin;
        }
    }

    /**
     * Ein gefuelltes Rechteck aus dem PDF (Vektor-Grafik).
     * Typisch fuer transparente Textboxen, farbige Hintergruende etc.
     */
    public static class RawRect
    {
        public float x;
        public float y;
        public float width;
        public float height;
        public float[] fillColor;  // RGB [0-1]
        public float opacity;      // 0=transparent, 1=deckend

        public RawRect()
        {
        }

        public RawRect(float x, float y, float width, float height,
                       float[] fillColor, float opacity)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fillColor = fillColor;
            this.opacity = opacity;
        }
    }
}
