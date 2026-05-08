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
        // Strokes erfassen — wichtig fuer Tabellen-Trennlinien (horizontaler
        // Strich) und Header-Unterstreichungen, die als gestreichelter Pfad
        // gezeichnet sein koennen.
        processStrokedPath();
        currentPath = new GeneralPath();
    }

    private void processStrokedPath()
    {
        Rectangle2D bounds = currentPath.getBounds2D();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();

        // Lange duenne Linien interessieren uns (Trennlinien)
        if (w < 20 && h < 20) return;
        if (w > 5 && h > 5) return; // weder waagerecht-duenn noch senkrecht-duenn
        // Mindestens eine Dimension muss gross sein, die andere klein
        float[] strokeColor = new float[]{0, 0, 0};
        try
        {
            PDColor color = getGraphicsState().getStrokingColor();
            if (color != null && color.getComponents() != null
                    && color.getComponents().length >= 3)
            {
                strokeColor = color.getComponents();
            }
        }
        catch (Exception e) { /* Fallback schwarz */ }

        float opacity = (float) getGraphicsState().getAlphaConstant();
        float pdfX = (float) bounds.getX();
        float pdfY = (float) bounds.getY();
        float rectY = currentPageHeight - pdfY - h;

        RawPageData.RawRect line = new RawPageData.RawRect(
                pdfX, rectY, w, h, strokeColor, opacity, true);
        pageRects.computeIfAbsent(currentPage, k -> new ArrayList<>()).add(line);
    }

    private void processFilledPath()
    {
        Rectangle2D bounds = currentPath.getBounds2D();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();

        // Sehr kleine Rechtecke (Punkte) ignorieren.
        if (w < 1.5f && h < 1.5f) return;
        // Pfade die nichts haben? skip.
        if (w <= 0 || h <= 0) return;
        // Erkennen, ob es eine duenne Linie ist (eine Dimension gross, andere < 2pt)
        boolean isLine = (h < 2f && w >= 20f) || (w < 2f && h >= 20f);
        // Wenn weder Box (w>=10 && h>=10) noch Linie → Punkt/Glyph: skip.
        boolean isBox = w >= 10f && h >= 10f;
        if (!isBox && !isLine) return;

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
            pdfX, rectY, w, h, fillColor, opacity, isLine
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
