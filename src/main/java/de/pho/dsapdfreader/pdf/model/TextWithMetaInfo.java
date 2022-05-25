package de.pho.dsapdfreader.pdf.model;

public class TextWithMetaInfo
{
    public String text;
    public boolean isBold;
    public boolean isItalic;
    public int size;
    public String font;

    public TextWithMetaInfo(
        String text,
        boolean isBold,
        boolean isItalic,
        int size,
        String font
    )
    {
        this.text = text;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.size = size;
        this.font = font;
    }

    @Override
    public String toString()
    {
        return "TextWithMetaInfo{" +
            "text='" + text + '\'' +
            ", isBold=" + isBold +
            ", isItalic=" + isItalic +
            ", size=" + size +
            ", font='" + font + '\'' +
            '}';
    }
}
