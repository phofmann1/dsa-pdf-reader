package de.pho.dsapdfreader;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/** Dumps fillPath/strokePath rectangles for a single page to investigate profile-box rendering. */
public class InspectGraphics extends PDFGraphicsStreamEngine {
    private GeneralPath path = new GeneralPath();

    public InspectGraphics(PDPage p) { super(p); }

    public static void main(String[] args) throws Exception {
        String pdfPath = args[0];
        int pageNum = Integer.parseInt(args[1]); // 1-based
        PDFParser parser = new PDFParser(new RandomAccessFile(new java.io.File(pdfPath), "r"));
        parser.parse();
        try (PDDocument doc = new PDDocument(parser.getDocument())) {
            PDPage page = doc.getPage(pageNum - 1);
            float h = page.getMediaBox().getHeight();
            System.out.println("Page height: " + h);
            InspectGraphics ig = new InspectGraphics(page);
            ig.processPage(page);
        }
    }

    private void dump(String op) {
        Rectangle2D b = path.getBounds2D();
        double w = b.getWidth(); double he = b.getHeight();
        if (w < 5 && he < 5) return;
        System.out.printf("%s x=%.1f..%.1f y=%.1f..%.1f w=%.1f h=%.1f%n",
                op, b.getMinX(), b.getMaxX(), b.getMinY(), b.getMaxY(), w, he);
    }

    @Override public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        path.moveTo((float)p0.getX(), (float)p0.getY());
        path.lineTo((float)p1.getX(), (float)p1.getY());
        path.lineTo((float)p2.getX(), (float)p2.getY());
        path.lineTo((float)p3.getX(), (float)p3.getY());
        path.closePath();
    }
    @Override public void moveTo(float x, float y) { path.moveTo(x, y); }
    @Override public void lineTo(float x, float y) { path.lineTo(x, y); }
    @Override public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) { path.curveTo(x1,y1,x2,y2,x3,y3); }
    @Override public Point2D getCurrentPoint() { return path.getCurrentPoint(); }
    @Override public void closePath() { path.closePath(); }
    @Override public void endPath() { path = new GeneralPath(); }
    @Override public void clip(int wr) {}
    @Override public void fillPath(int wr) { dump("FILL"); path = new GeneralPath(); }
    @Override public void fillAndStrokePath(int wr) { dump("FILLSTROKE"); path = new GeneralPath(); }
    @Override public void strokePath() { dump("STROKE"); path = new GeneralPath(); }
    @Override public void drawImage(PDImage img) {}
    @Override public void shadingFill(COSName n) {}
}
