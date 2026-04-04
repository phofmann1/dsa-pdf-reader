package de.pho.dsapdfreader.pdf.model;

import com.opencsv.bean.CsvBindByName;

public class ImageWithMetaInfo
{
    @CsvBindByName
    public String publication;
    @CsvBindByName
    public int onPage;
    @CsvBindByName
    public int imageIndex;
    @CsvBindByName
    public float x;
    @CsvBindByName
    public float y;
    @CsvBindByName
    public float width;
    @CsvBindByName
    public float height;
    @CsvBindByName
    public int imageWidth;
    @CsvBindByName
    public int imageHeight;
    @CsvBindByName
    public String suffix;
    @CsvBindByName
    public int colorSpaceComponents;
    @CsvBindByName
    public int bitsPerComponent;
    @CsvBindByName
    public String checksum;
    @CsvBindByName
    public String filename;

    public ImageWithMetaInfo()
    {
    }

    public ImageWithMetaInfo(
        String publication,
        int onPage,
        int imageIndex,
        float x,
        float y,
        float width,
        float height,
        int imageWidth,
        int imageHeight,
        String suffix,
        int colorSpaceComponents,
        int bitsPerComponent,
        String checksum,
        String filename
    )
    {
        this.publication = publication;
        this.onPage = onPage;
        this.imageIndex = imageIndex;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.suffix = suffix;
        this.colorSpaceComponents = colorSpaceComponents;
        this.bitsPerComponent = bitsPerComponent;
        this.checksum = checksum;
        this.filename = filename;
    }
}
