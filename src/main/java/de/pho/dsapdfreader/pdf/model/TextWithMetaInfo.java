package de.pho.dsapdfreader.pdf.model;

import com.opencsv.bean.CsvBindByName;

public class TextWithMetaInfo
{
    @CsvBindByName
    public String publication;
    @CsvBindByName
    public int onPage;

    @CsvBindByName
    public double onLine;

    @CsvBindByName
    public double order = -1;

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

    public TextWithMetaInfo()
    {
    }

    public TextWithMetaInfo(
        String text,
        boolean isBold,
        boolean isItalic,
        int size,
        String font,
        int onPage,
        double onLine,
        String publication
    )
    {
        this.text = text;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.size = size;
        this.font = font;
        this.onPage = onPage;
        this.onLine = onLine;
        this.publication = publication;
    }

    public double sortIndex()
    {
        return onPage * 10000 + (order > 0
            ? order
            : onLine);
    }
}
