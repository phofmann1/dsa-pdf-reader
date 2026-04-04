package de.pho.dsapdfreader.markdown;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

import java.awt.geom.Point2D;

/**
 * Extrahiert gefuellte Rechtecke (Vektor-Boxen) aus PDF-Seiten.
 * Erfasst Position, Groesse, Fuellfarbe und Transparenz.
 * Typischer Anwendungsfall: transparente Textboxen, farbige Hintergruende.
 */
public class RawRectExtractor extends PDFGraphicsStreamEngine
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<Integer, List<RawPageData.RawRect>> pageRects = new HashMap<>();
    private int currentPage;
    private float currentPageHeight;
    private GeneralPath currentPath;

    public RawRectExtractor(PDPage dummy)
    {
        super(dummy);
    }

    public Map<Integer, List<RawPageData.RawRect>> extract(PDDocument document) throws IOException
    {
        pageRects.clear();
        currentPage = 0;

        for (PDPage page : document.getPages())
        {
            currentPage++;
            currentPageHeight = page.getMediaBox().getHeight();
            currentPath = new GeneralPath();
            processPage(page);
        }

        return pageRects;
    }

    // --- Pfad-Operatoren ---

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException
    {
        currentPath.moveTo((float) p0.getX(), (float) p0.getY());
        currentPath.lineTo((float) p1.getX(), (float) p1.getY());
        currentPath.lineTo((float) p2.getX(), (float) p2.getY());
        currentPath.lineTo((float) p3.getX(), (float) p3.getY());
        currentPath.closePath();
    }

    @Override
    public void moveTo(float x, float y) throws IOException
    {
        currentPath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) throws IOException
    {
        currentPath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException
    {
        currentPath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() throws IOException
    {
        return currentPath.getCurrentPoint();
    }

    @Override
    public void closePath() throws IOException
    {
        currentPath.closePath();
    }

    @Override
    public void endPath() throws IOException
    {
        currentPath = new GeneralPath();
    }

    @Override
    public void clip(int windingRule) throws IOException
    {
        // Clipping ignorieren
    }

    // --- Fill-Operatoren: hier erfassen wir die Rechtecke ---

    @Override
    public void fillPath(int windingRule) throws IOException
    {
        processFilledPath();
        currentPath = new GeneralPath();
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException
    {
        processFilledPath();
        currentPath = new GeneralPath();
    }

    @Override
    public void strokePath() throws IOException
    {
        // Nur Umrandung, kein Fill — ignorieren
        currentPath = new GeneralPath();
    }

    private void processFilledPath()
    {
        Rectangle2D bounds = currentPath.getBounds2D();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();

        // Zu kleine Rechtecke ignorieren (Linien, Punkte)
        if (w < 10 || h < 10) return;

        // Fuellfarbe auslesen
        float[] fillColor = new float[]{0, 0, 0};
        try
        {
            PDColor color = getGraphicsState().getNonStrokingColor();
            if (color != null && color.getComponents() != null)
            {
                fillColor = color.getComponents();
                // Bei CMYK oder anderen Farbräumen auf die Rohwerte beschraenken
                if (fillColor.length < 3)
                {
                    fillColor = new float[]{fillColor[0], fillColor[0], fillColor[0]};
                }
            }
        }
        catch (Exception e)
        {
            // Fallback: schwarz
        }

        // Transparenz auslesen
        float opacity = (float) getGraphicsState().getNonStrokeAlphaConstant();

        // PDF-Koordinaten (bottom-left) → top-left
        float pdfX = (float) bounds.getX();
        float pdfY = (float) bounds.getY();
        float rectY = currentPageHeight - pdfY - h;

        RawPageData.RawRect rect = new RawPageData.RawRect(
            pdfX, rectY, w, h, fillColor, opacity
        );

        pageRects.computeIfAbsent(currentPage, k -> new ArrayList<>()).add(rect);
    }

    // --- Bild-Operatoren: muessen implementiert werden, ignorieren wir ---

    @Override
    public void drawImage(PDImage pdImage) throws IOException
    {
        // Bilder ignorieren, nur Rechtecke interessieren uns
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException
    {
        // Shading ignorieren
    }
}
