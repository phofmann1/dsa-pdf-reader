package de.pho.dsapdfreader.book.analysis;

import java.util.Objects;

/**
 * Faktischer Stil-Schluessel eines Zeichens.
 * <p>
 * Im Gegensatz zur Stripped-Family in {@code TextInterpreter.classifyChar}
 * (die {@code -Bold}, {@code -SC700} etc. wegwirft), behaelt dieser Key den
 * <b>vollen</b> Font-Namen — Bold und SmallCaps werden zusaetzlich als Flags
 * geprueft, kollabieren aber nicht in den Family-String.
 * <p>
 * SizeBin: Float-Font-Sizes haben durch PDF-Transform-Matrizen leichten Jitter
 * (13.0 vs 13.0001). Wir binnen daher auf 0.25pt — fein genug, um 13.0pt von
 * 13.5pt zu unterscheiden, grob genug fuer Render-Rauschen.
 */
public record FontStyleKey(
        String fontName,
        float sizeBin,
        boolean bold,
        boolean italic,
        boolean smallCaps
) {

    private static final float SIZE_BIN_STEP = 0.25f;

    /** Bin-Breite, mit der raw.fontSize geclustered wird. */
    public static float sizeBin(float rawSize) {
        return Math.round(rawSize / SIZE_BIN_STEP) * SIZE_BIN_STEP;
    }

    /** Erkennt SmallCaps-/Weight-Marker im Font-Namen, ohne den Namen zu reduzieren. */
    public static boolean isSmallCapsName(String fontName) {
        if (fontName == null) return false;
        String n = fontName;
        return n.contains("SmallCaps") || n.contains("SC700") || n.contains("SC600")
                || n.contains("SC800") || n.contains("SC900");
    }

    public static boolean isBoldName(String fontName, boolean rawBold) {
        if (rawBold) return true;
        if (fontName == null) return false;
        String n = fontName;
        return n.contains("Bold") || n.contains("bold")
                || n.contains("SC700") || n.contains("SC800") || n.contains("SC900")
                || n.contains("Heavy") || n.contains("Black");
    }

    public static boolean isItalicName(String fontName, boolean rawItalic) {
        if (rawItalic) return true;
        if (fontName == null) return false;
        String n = fontName;
        return n.contains("Italic") || n.contains("italic")
                || n.contains("Oblique") || n.contains("oblique");
    }

    public static FontStyleKey of(String fontName, float rawSize, boolean rawBold, boolean rawItalic) {
        String name = fontName == null ? "" : stripPrefix(fontName);
        return new FontStyleKey(
                name,
                sizeBin(rawSize),
                isBoldName(name, rawBold),
                isItalicName(name, rawItalic),
                isSmallCapsName(name));
    }

    /** PostScript-Subset-Prefix entfernen ("ABCDEF+GentiumBasic-Bold" → "GentiumBasic-Bold"). */
    private static String stripPrefix(String fontName) {
        int plus = fontName.indexOf('+');
        return plus >= 0 ? fontName.substring(plus + 1) : fontName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FontStyleKey k)) return false;
        return Float.compare(k.sizeBin, sizeBin) == 0
                && bold == k.bold
                && italic == k.italic
                && smallCaps == k.smallCaps
                && Objects.equals(fontName, k.fontName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fontName, sizeBin, bold, italic, smallCaps);
    }
}
