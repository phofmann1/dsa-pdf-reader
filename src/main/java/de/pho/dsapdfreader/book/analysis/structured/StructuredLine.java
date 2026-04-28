package de.pho.dsapdfreader.book.analysis.structured;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.pho.dsapdfreader.book.analysis.FontStyleKey;

/**
 * Eine atomare Text-Linie auf einer Seite, mit voller faktischer Stil-Info.
 * <p>
 * Eine Linie gehoert zu genau einer Seite und einer Spalte; Bloecke (in
 * {@link StructuredBlock}) koennen mehrere Linien aggregieren — auch
 * seitenuebergreifend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StructuredLine {

    /** Seite, auf der die Linie steht (1-basiert, wie in RawPageData). */
    public int page;

    /** Linke obere Ecke der Linie (PDF-Koordinaten — y waechst nach unten). */
    public float y;
    public float xStart;
    public float xEnd;

    /** Spalten-Index. 0 = ueber die ganze Breite, 1..N = einzelne Spalten von links. */
    public int columnIndex;

    /** Reine Textinhalt der Linie. */
    public String text;

    /** Dominantes Cluster-Tier dieser Linie, 0 = Body, 1+ = Heading-Ebenen. -1 = unbekannt. */
    public int tier = -1;

    /** Stil-Schluessel des dominanten Clusters auf dieser Linie. */
    public FontStyleKey style;

    /** Optional: wenn die Linie als {@code **Feldname:** Wert} erkannt wurde, Feld-Key. */
    public String fieldKey;
    /** Optional: zugehoeriger Feld-Wert. */
    public String fieldValue;

    /** Optional: wenn die Linie ein Block-Marker mit Klammer-Typ ist ("**Wuchtschlag** (passiv)"). */
    public String typeMarker;

    /** Reading-Order-Index innerhalb des Buches (gesetzt durch BookStructuredBuilder). */
    public int readingOrder;
}
