package de.pho.dsapdfreader.pdf.model;

import com.opencsv.bean.CsvBindByName;

public class TextWithMetaInfo
{
    @CsvBindByName
    public int onPage;
    @CsvBindByName
    public boolean isBold;
    @CsvBindByName
    public boolean isItalic;
    @CsvBindByName
    public int size;
    @CsvBindByName
    public String font;
    @CsvBindByName
    public String text;

    public TextWithMetaInfo(
        String text,
        boolean isBold,
        boolean isItalic,
        int size,
        String font,
        int onPage
    )
    {
        this.text = text;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.size = size;
        this.font = font;
        this.onPage = onPage;
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
