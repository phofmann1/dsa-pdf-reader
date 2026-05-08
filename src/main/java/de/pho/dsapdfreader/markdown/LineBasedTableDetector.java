package de.pho.dsapdfreader.markdown;

import de.pho.dsapdfreader.markdown.RawPageData.RawRect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Erkennt Tabellen anhand horizontaler Trennlinien (RawRect mit isLine=true).
 *
 * <p>Konzept: PDF-Tabellen in DSA5-Buechern haben sichtbare Trennlinien zwischen
 * Reihen. Diese sind im erweiterten {@link RawRectExtractor} als Linien-Rechtecke
 * gespeichert. Aufeinanderfolgende Y-Tracks mit identischen X-Segment-Mustern
 * definieren eine Tabelle: Y-Range, Spalten-Boundaries und Reihen-Trennungen
 * koennen direkt abgelesen werden.
 *
 * <p>Vorteil gegenueber gap-basierter Detection (siehe {@code TextInterpreter#detectTables}):
 * Multi-line Cells (z. B. drei-zeiliger Header oder Zell-Inhalt der ueber mehrere
 * Body-Lines wraps) werden EXAKT durch die Trennlinien abgegrenzt — kein Heuristik-Spagat
 * ueber Y-Spacing oder Cell-Reihen-Marker mehr noetig.
 */
public final class LineBasedTableDetector {

    /** Eine Y-Linien-Spur: alle horizontalen Linien-Segmente bei (etwa) gleicher Y-Position. */
    static final class YTrack {
        final float y;
        /** Sortiert nach x. */
        final List<RawRect> segments = new ArrayList<>();
        YTrack(float y) { this.y = y; }
    }

    /** Eine erkannte Tabelle. */
    public static final class LineBasedTable {
        public final float yTop, yBottom;
        public final float xLeft, xRight;
        /** Spalten-Boundaries (sortiert) — die linke Kante jeder Spalte. Cells: bound[i]..bound[i+1]. */
        public final float[] columnBoundaries;
        /** Y-Werte aller Trennlinien (von oben nach unten). Reihen-Y-Bereiche: tracks[i]..tracks[i+1]. */
        public final float[] rowSeparators;
        public final List<List<String>> cells;

        public LineBasedTable(float yTop, float yBottom, float xLeft, float xRight,
                              float[] columnBoundaries, float[] rowSeparators,
                              List<List<String>> cells) {
            this.yTop = yTop; this.yBottom = yBottom;
            this.xLeft = xLeft; this.xRight = xRight;
            this.columnBoundaries = columnBoundaries;
            this.rowSeparators = rowSeparators;
            this.cells = cells;
        }

        public int rowCount() { return cells.size(); }
        public int colCount() { return columnBoundaries.length - 1; }
    }

    /** Toleranz fuer "gleicher Y-Wert" beim Bilden von Y-Tracks. */
    private static final float Y_TOLERANCE = 1.5f;
    /** Toleranz fuer "gleiche X-Boundary" zwischen Tracks. */
    private static final float X_TOLERANCE = 2.0f;

    /**
     * Liefert alle line-basierten Tabellen einer Seite.
     */
    public static List<LineBasedTable> detect(RawPageData page) {
        List<LineBasedTable> result = new ArrayList<>();
        if (page == null || page.rects == null || page.chars == null) return result;

        // 1) Horizontale Linien sammeln (kurze, breite Rechtecke mit isLine=true).
        List<RawRect> horizLines = new ArrayList<>();
        for (RawRect r : page.rects) {
            if (!r.isLine) continue;
            // Horizontal: width >> height
            if (r.width >= 20f && r.height < 2f) horizLines.add(r);
        }
        if (horizLines.isEmpty()) return result;

        // 2) Nach Y gruppieren → Y-Tracks. Wichtig: Segmente aus unterschiedlichen
        //    Spalten-Clustern duerfen NICHT in denselben Track gelangen, auch wenn
        //    ihre Y-Werte zufaellig zusammenfallen (oft S26: Belastungs-Tabelle in
        //    linker Spalte und Entrueckungs-Tabelle in rechter Spalte teilen y~727).
        //    Cluster-Definition: zwei Segmente gehoeren in denselben Track, wenn ihre
        //    X-Bereiche entweder ueberlappen oder mit einem Gap <= 16pt aneinander
        //    grenzen (typische Spaltentrenn-Luecke INNERHALB einer Tabelle).
        horizLines.sort(Comparator.comparingDouble(r -> r.y));
        List<YTrack> tracks = new ArrayList<>();
        for (RawRect r : horizLines) {
            YTrack target = null;
            // Suche Track mit nah-genuger Y UND ueberlappendem/anschliessendem X-Bereich.
            for (int t = tracks.size() - 1; t >= 0; t--) {
                YTrack tr = tracks.get(t);
                if (Math.abs(r.y - tr.y) > Y_TOLERANCE) break; // weil sortiert nach y
                if (xClusterMatches(tr, r)) { target = tr; break; }
            }
            if (target != null) target.segments.add(r);
            else {
                YTrack nt = new YTrack(r.y);
                nt.segments.add(r);
                tracks.add(nt);
            }
        }
        // Segments pro Track nach x sortieren
        for (YTrack t : tracks) t.segments.sort(Comparator.comparingDouble(s -> s.x));
        // Tracks nach (y, xLeft) sortieren — fuer deterministische Reihenfolge
        tracks.sort(Comparator.<YTrack>comparingDouble(t -> t.y)
                .thenComparingDouble(t -> t.segments.isEmpty() ? 0 : t.segments.get(0).x));

        // 3) Aufeinanderfolgende Tracks mit identischen X-Boundary-Mustern gruppieren.
        //    Anschliessend pruefen, ob innerhalb der Gruppe ein Heading-Split
        //    notwendig ist (siehe maybeSplitOnHeading).
        // Tracks pro Boundary-Pattern sammeln (verschiedene Spalten = verschiedene
        // Tabellen). Ein Track aus einer anderen Spalte (X-disjunkt) darf einen
        // Lauf nicht abbrechen — z. B. liegt auf S26 ein rechte-Spalte-Track
        // genau zwischen zwei linke-Spalte-Tracks, ohne dass das die Belastungs-
        // Tabelle beenden duerfte.
        boolean[] consumed = new boolean[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            if (consumed[i]) continue;
            float[] boundaries = boundariesOf(tracks.get(i));
            if (boundaries.length < 3) { consumed[i] = true; continue; }
            List<YTrack> group = new ArrayList<>();
            group.add(tracks.get(i));
            consumed[i] = true;
            for (int j = i + 1; j < tracks.size(); j++) {
                if (consumed[j]) continue;
                float[] bj = boundariesOf(tracks.get(j));
                if (boundariesMatch(bj, boundaries)) {
                    group.add(tracks.get(j));
                    consumed[j] = true;
                } else if (xRangesOverlap(boundaries, bj)) {
                    // anderes Boundary-Muster im selben X-Bereich → die Tabelle
                    // endet hier definitiv (z. B. eine andere Tabelle drunter).
                    break;
                }
                // sonst: track in anderer Spalte, einfach ueberspringen
            }
            if (group.size() < 2) continue;
            // Spurious-Boundary-Filter: Linien-Endpunkte koennen durch PDF-interne
            // Segment-Aufteilung Zwischen-Boundaries enthalten, die KEINE echten
            // Spalten-Trenner sind. Erkennung: Text in Cells laeuft konitnuierlich
            // ueber so eine Pseudo-Boundary hinweg.
            float[] cleanedBoundaries = removeSpuriousBoundaries(page, group, boundaries);
            if (cleanedBoundaries.length < 3) continue;
            List<List<YTrack>> subgroups = maybeSplitOnHeading(page, group, cleanedBoundaries);
            for (List<YTrack> sub : subgroups) {
                if (sub.size() < 2) continue;
                LineBasedTable tbl = buildTable(page, sub, cleanedBoundaries);
                if (tbl != null && tbl.rowCount() >= 1 && hasAnyContent(tbl)) result.add(tbl);
            }
        }
        return result;
    }

    /**
     * Entfernt Boundaries, die nachweislich keine Spalten-Trenner sind: wenn in
     * den meisten Reihen ein zusammenhaengendes Wort ueber die Boundary laeuft
     * (kein groesserer Whitespace an dieser Stelle), dann ist das nur ein PDF-
     * Render-Artefakt (mehrere Linien-Segmente in einer Spalte).
     */
    private static float[] removeSpuriousBoundaries(RawPageData page, List<YTrack> group, float[] boundaries) {
        if (boundaries.length <= 3) return boundaries; // 2 cols → kann nichts entfernen
        boolean[] keep = new boolean[boundaries.length];
        keep[0] = true;
        keep[boundaries.length - 1] = true;
        // Heuristik: echte Boundary hat in fast allen Reihen ein leeres Fenster
        // (kein Text quer durch). Spurious-Boundary hat in vielen Reihen Text
        // mitten durch das Fenster.
        for (int b = 1; b < boundaries.length - 1; b++) {
            float bx = boundaries[b];
            int rowsWithGap = 0;       // Reihe zeigt Whitespace am bx
            int rowsWithText = 0;      // Reihe zeigt Text am bx (Wort-Crossing)
            for (int r = 0; r + 1 < group.size(); r++) {
                float rowYTop = group.get(r).y;
                float rowYBottom = group.get(r + 1).y;
                int classify = classifyBoundaryInRow(page, rowYTop, rowYBottom, bx);
                if (classify > 0) rowsWithText++;
                else if (classify < 0) rowsWithGap++;
                // 0 = leere Reihe (keine Aussage)
            }
            int contentRows = rowsWithGap + rowsWithText;
            // Spurious: mehrheitlich Text durchs Fenster, mind. 2 Beispiele
            boolean spurious = contentRows >= 2
                    && rowsWithText > rowsWithGap
                    && rowsWithText >= 2;
            keep[b] = !spurious;
        }
        int kept = 0;
        for (boolean k : keep) if (k) kept++;
        if (kept == boundaries.length) return boundaries;
        float[] out = new float[kept];
        int idx = 0;
        for (int i = 0; i < boundaries.length; i++) if (keep[i]) out[idx++] = boundaries[i];
        return out;
    }

    /**
     * Klassifiziert eine Reihe an der gegebenen Boundary:
     *  &gt; 0: Text laeuft quer durch das ±4pt-Fenster (Wort spannt ueber bx).
     *  &lt; 0: Whitespace/Cell-Trenn-Gap an bx (echte Spalten-Trennung).
     *    0: keine Aussage moeglich (leere Reihe an dieser Stelle).
     */
    private static int classifyBoundaryInRow(RawPageData page, float yTop, float yBottom, float bx) {
        // Pro Y-Linie pruefen: gibt es ein Char dessen Bbox bx schneidet
        // ODER zwei Chars eng links/rechts mit kleinem Gap?
        List<RawPageData.RawChar> nearby = new ArrayList<>();
        for (RawPageData.RawChar c : page.chars) {
            if (c.y > yTop - 0.5f && c.y < yBottom + 0.5f
                    && c.x + c.width > bx - 20f && c.x < bx + 20f) {
                nearby.add(c);
            }
        }
        if (nearby.isEmpty()) return 0;
        nearby.sort(Comparator
                .<RawPageData.RawChar>comparingDouble(c -> c.y)
                .thenComparingDouble(c -> c.x));
        // Y-Cluster
        List<List<RawPageData.RawChar>> yLines = new ArrayList<>();
        List<RawPageData.RawChar> current = new ArrayList<>();
        Float lastY = null;
        for (RawPageData.RawChar c : nearby) {
            if (lastY != null && Math.abs(c.y - lastY) > 2f) {
                if (!current.isEmpty()) yLines.add(current);
                current = new ArrayList<>();
            }
            current.add(c);
            lastY = c.y;
        }
        if (!current.isEmpty()) yLines.add(current);
        boolean anyTextThroughBoundary = false;
        boolean anyClearGap = false;
        for (List<RawPageData.RawChar> line : yLines) {
            // Char dessen Bbox bx ueberschneidet → Text quer durch
            boolean charSpans = false;
            RawPageData.RawChar leftMost = null, rightMost = null;
            for (RawPageData.RawChar c : line) {
                if (c.x < bx && c.x + c.width > bx) charSpans = true;
                if (c.x + c.width <= bx + 0.5f) {
                    if (leftMost == null || c.x + c.width > leftMost.x + leftMost.width) leftMost = c;
                }
                if (c.x >= bx - 0.5f) {
                    if (rightMost == null || c.x < rightMost.x) rightMost = c;
                }
            }
            if (charSpans) { anyTextThroughBoundary = true; continue; }
            if (leftMost != null && rightMost != null) {
                float gap = rightMost.x - (leftMost.x + leftMost.width);
                if (gap < 4f) anyTextThroughBoundary = true;
                else anyClearGap = true;
            } else if (leftMost != null || rightMost != null) {
                // nur eine Seite hat Text → das ist eine "Cell endet hier"-Situation
                anyClearGap = true;
            }
        }
        if (anyTextThroughBoundary) return 1;
        if (anyClearGap) return -1;
        return 0;
    }

    /**
     * Wird nicht mehr verwendet (ersetzt durch classifyBoundaryInRow), bleibt
     * fuer den Fall, dass ein Caller diese Logik braucht.
     */
    @SuppressWarnings("unused")
    private static Boolean textCrossesBoundary(RawPageData page, float yTop, float yBottom, float bx) {
        // Sammle Chars im Y-Bereich, sortiere nach x.
        List<RawPageData.RawChar> nearby = new ArrayList<>();
        for (RawPageData.RawChar c : page.chars) {
            if (c.y > yTop - 0.5f && c.y < yBottom + 0.5f
                    && c.x + c.width > bx - 25f && c.x < bx + 25f) {
                nearby.add(c);
            }
        }
        if (nearby.isEmpty()) return null;
        // Pro Y-Level pruefen
        nearby.sort(Comparator
                .<RawPageData.RawChar>comparingDouble(c -> c.y)
                .thenComparingDouble(c -> c.x));
        // Y-Cluster bauen
        List<List<RawPageData.RawChar>> yLines = new ArrayList<>();
        List<RawPageData.RawChar> current = new ArrayList<>();
        Float lastY = null;
        for (RawPageData.RawChar c : nearby) {
            if (lastY != null && Math.abs(c.y - lastY) > 2f) {
                if (!current.isEmpty()) yLines.add(current);
                current = new ArrayList<>();
            }
            current.add(c);
            lastY = c.y;
        }
        if (!current.isEmpty()) yLines.add(current);
        // Pro Y-Linie: gibt es Char direkt links UND direkt rechts der Boundary
        // mit kleinem Gap?
        boolean anyCross = false;
        boolean anyRespect = false;
        for (List<RawPageData.RawChar> line : yLines) {
            RawPageData.RawChar leftMost = null;
            RawPageData.RawChar rightMost = null;
            for (RawPageData.RawChar c : line) {
                float cEnd = c.x + c.width;
                if (cEnd <= bx + 0.5f) {
                    if (leftMost == null || c.x + c.width > leftMost.x + leftMost.width) leftMost = c;
                }
                if (c.x >= bx - 0.5f) {
                    if (rightMost == null || c.x < rightMost.x) rightMost = c;
                }
            }
            if (leftMost != null && rightMost != null) {
                float gap = rightMost.x - (leftMost.x + leftMost.width);
                // Nur sehr enger Abstand zaehlt als "Wort spannt ueber Boundary".
                // Spalten-Inhalte wie "0" und "–" mit mehreren Punkten Abstand
                // duerfen NICHT als Crossing gelten.
                if (gap < 3f) anyCross = true;
                else anyRespect = true;
            }
        }
        if (anyCross && !anyRespect) return Boolean.TRUE;
        if (!anyCross && anyRespect) return Boolean.FALSE;
        if (anyCross) return Boolean.TRUE; // mixed → eher spurious
        return null;
    }

    private static boolean xRangesOverlap(float[] a, float[] b) {
        float aL = a[0], aR = a[a.length - 1];
        float bL = b[0], bR = b[b.length - 1];
        return aR > bL + 1f && bR > aL + 1f;
    }

    /**
     * Splittet eine Track-Gruppe wenn dazwischen eine Headline steht. Heuristik:
     * - Gap zwischen aufeinanderfolgenden Tracks &gt; median + 4pt.
     * - In KEINER Reihe der Gesamt-Gruppe gibt es Multi-Line-Content (max. 1 Y-Level).
     *   (Wenn multi-line-Cells existieren, sind grosse Gaps durch sie erklaert — kein Split.)
     */
    private static List<List<YTrack>> maybeSplitOnHeading(RawPageData page, List<YTrack> group, float[] boundaries) {
        List<List<YTrack>> out = new ArrayList<>();
        // Y-Level-Counts pro Reihe ermitteln
        float xLeft = boundaries[0];
        float xRight = boundaries[boundaries.length - 1];
        boolean anyMultiLine = false;
        List<Float> gaps = new ArrayList<>();
        for (int k = 0; k + 1 < group.size(); k++) {
            float yT = group.get(k).y;
            float yB = group.get(k + 1).y;
            gaps.add(yB - yT);
            int distinctYs = countDistinctYLevels(page, yT, yB, xLeft, xRight);
            if (distinctYs >= 2) anyMultiLine = true;
        }
        if (anyMultiLine || group.size() < 3) {
            out.add(new ArrayList<>(group));
            return out;
        }
        float median = medianOf(gaps);
        // Such Splits an grossen Gaps
        List<Integer> splitAfter = new ArrayList<>();
        for (int k = 0; k + 1 < group.size(); k++) {
            float gap = group.get(k + 1).y - group.get(k).y;
            if (gap > median + 4f && gap > median * 1.4f) splitAfter.add(k);
        }
        if (splitAfter.isEmpty()) {
            out.add(new ArrayList<>(group));
            return out;
        }
        int start = 0;
        for (int s : splitAfter) {
            out.add(new ArrayList<>(group.subList(start, s + 1)));
            start = s + 1;
        }
        out.add(new ArrayList<>(group.subList(start, group.size())));
        return out;
    }

    private static int countDistinctYLevels(RawPageData page, float yTop, float yBottom, float xLeft, float xRight) {
        java.util.Set<Integer> ys = new java.util.HashSet<>();
        for (RawPageData.RawChar c : page.chars) {
            if (c.y > yTop + 0.5f && c.y < yBottom - 0.5f
                    && c.x >= xLeft - 1f && c.x <= xRight + 1f) {
                // Gruppieren mit 2pt Toleranz
                ys.add(Math.round(c.y / 3f));
            }
        }
        return ys.size();
    }

    /**
     * Pruefen, ob das Segment {@code r} in den X-Cluster eines bestehenden Tracks passt.
     * Match wenn X-Bereich ueberlappt ODER der Abstand zur naechsten Boundary <= 16pt
     * liegt (uebliche Cell-Trenn-Luecke).
     */
    private static boolean xClusterMatches(YTrack track, RawRect r) {
        if (track.segments.isEmpty()) return true;
        float rl = r.x;
        float rr = r.x + r.width;
        for (RawRect s : track.segments) {
            float sl = s.x;
            float sr = s.x + s.width;
            // Ueberlapp
            if (rr >= sl - 1f && rl <= sr + 1f) return true;
            // Anschluss-Luecke. Innerhalb einer Tabelle stossen Linien an Spalten-
            // Boundaries direkt aneinander (Gap = 0). Der DSA5-Zwei-Spalten-Gutter
            // zwischen linker und rechter Buchspalte betraegt ca. 14pt — daher hier
            // 10pt als sichere Schwelle.
            float gap = (rl > sr) ? rl - sr : (sl > rr ? sl - rr : 0f);
            if (gap <= 10f) return true;
        }
        return false;
    }

    private static float medianOf(List<Float> values) {
        List<Float> s = new ArrayList<>(values);
        java.util.Collections.sort(s);
        return s.get(s.size() / 2);
    }

    /** X-Boundaries aus einem Track. Letzter Endpunkt = right edge. */
    private static float[] boundariesOf(YTrack track) {
        if (track.segments.isEmpty()) return new float[0];
        List<Float> b = new ArrayList<>();
        b.add(track.segments.get(0).x);
        for (RawRect seg : track.segments) {
            // jedes Segment endet — die naechste Boundary ist seg.x+seg.width
            float end = seg.x + seg.width;
            // Nur als Boundary aufnehmen, wenn nicht schon vorhanden
            float last = b.get(b.size() - 1);
            if (Math.abs(end - last) > X_TOLERANCE) b.add(end);
            // Auch der naechste Segment-Start kann eine Boundary sein (Luecke zwischen Segmenten = Spalten-Trennung)
            // → bereits durch sortierte Segmente abgedeckt
        }
        // Falls Segmente Luecken haben, die Luecken-Mittelpunkte als Boundaries
        // hinzufuegen. (Hier nicht noetig: in DSA5 sind Linien meist durchgehend
        // pro Spalte, mit kleiner Luecke an der Spaltengrenze, was die end+next-Logik
        // schon erfasst.)
        // → Re-build: alle Start- und End-Punkte aller Segmente, dedupliziert.
        List<Float> all = new ArrayList<>();
        all.add(track.segments.get(0).x);
        for (RawRect seg : track.segments) all.add(seg.x + seg.width);
        // Dedup mit Toleranz
        List<Float> dedup = new ArrayList<>();
        for (float v : all) {
            boolean dup = false;
            for (float d : dedup) if (Math.abs(v - d) <= X_TOLERANCE) { dup = true; break; }
            if (!dup) dedup.add(v);
        }
        java.util.Collections.sort(dedup);
        float[] out = new float[dedup.size()];
        for (int k = 0; k < dedup.size(); k++) out[k] = dedup.get(k);
        return out;
    }

    private static boolean hasAnyContent(LineBasedTable t) {
        for (List<String> row : t.cells) {
            for (String c : row) {
                if (c != null && !c.trim().isEmpty()) return true;
            }
        }
        return false;
    }

    private static boolean boundariesMatch(float[] a, float[] b) {
        if (a.length != b.length) return false;
        for (int k = 0; k < a.length; k++) {
            if (Math.abs(a[k] - b[k]) > X_TOLERANCE) return false;
        }
        return true;
    }

    private static LineBasedTable buildTable(RawPageData page, List<YTrack> groupTracks, float[] boundaries) {
        if (groupTracks.size() < 2) return null;
        float yTop = groupTracks.get(0).y;
        float yBottom = groupTracks.get(groupTracks.size() - 1).y;
        float xLeft = boundaries[0];
        float xRight = boundaries[boundaries.length - 1];
        float[] rowSeps = new float[groupTracks.size()];
        for (int k = 0; k < groupTracks.size(); k++) rowSeps[k] = groupTracks.get(k).y;

        int colCount = boundaries.length - 1;
        List<List<String>> rows = new ArrayList<>();

        // Median-Reihen-Hoehe — fuer "open last row"-Heuristik (Tabelle, deren letzte
        // Reihe keinen Schliess-Strich hat, weil PDF-Layout sie nach unten offen laesst).
        List<Float> rowHeights = new ArrayList<>();
        for (int k = 0; k + 1 < rowSeps.length; k++) rowHeights.add(rowSeps[k + 1] - rowSeps[k]);
        float medRowH = rowHeights.isEmpty() ? 16f : medianOf(rowHeights);

        for (int r = 0; r < groupTracks.size() - 1; r++) {
            float rowYTop = rowSeps[r];
            float rowYBottom = rowSeps[r + 1];
            // Chars im Y-Bereich (yTop, yBottom) und X-Bereich (xLeft, xRight)
            // einsammeln — wegen Baseline kann text minimal oberhalb der Linie liegen.
            List<RawPageData.RawChar> rowChars = new ArrayList<>();
            for (RawPageData.RawChar c : page.chars) {
                if (c.y > rowYTop - 1f && c.y < rowYBottom + 1f
                        && c.x >= xLeft - 1f && c.x <= xRight + 1f) {
                    rowChars.add(c);
                }
            }
            // Pro Spalte Cells aufbauen
            List<String> cells = new ArrayList<>();
            for (int col = 0; col < colCount; col++) {
                float cellLeft = boundaries[col];
                float cellRight = boundaries[col + 1];
                StringBuilder cellText = new StringBuilder();
                List<RawPageData.RawChar> cellChars = new ArrayList<>();
                for (RawPageData.RawChar c : rowChars) {
                    float cx = c.x + c.width / 2f;
                    if (cx >= cellLeft - 1f && cx < cellRight - 1f) cellChars.add(c);
                }
                // Sortieren nach (y, x)
                cellChars.sort(Comparator
                        .<RawPageData.RawChar>comparingDouble(c -> c.y)
                        .thenComparingDouble(c -> c.x));
                // Linien-weise rendern: Chars an gleicher y zusammen, mit Space bei groesserem Gap
                Float prevY = null;
                Float prevEnd = null;
                for (RawPageData.RawChar c : cellChars) {
                    if (prevY != null && Math.abs(c.y - prevY) > 2f) {
                        // neue Zeile innerhalb der Cell
                        if (cellText.length() > 0
                                && cellText.charAt(cellText.length() - 1) != ' ')
                            cellText.append(' ');
                        prevEnd = null;
                    } else if (prevEnd != null) {
                        float gap = c.x - prevEnd;
                        if (gap > 1.5f
                                && cellText.length() > 0
                                && cellText.charAt(cellText.length() - 1) != ' ')
                            cellText.append(' ');
                    }
                    cellText.append(c.text);
                    prevY = c.y;
                    prevEnd = c.x + c.width;
                }
                String t = cellText.toString().replaceAll("\\s+", " ").trim();
                cells.add(t);
            }
            rows.add(cells);
        }

        // "Open last row": Manche Tabellen haben unter der letzten Trennlinie noch
        // eine weitere Datenreihe, die nach unten offen ist (kein Schliess-Strich).
        // Wir scannen Chars im X-Range knapp unterhalb yBottom bis yBottom+1.6*medRowH.
        // Findet sich dort Inhalt in mindestens zwei Spalten, gilt das als Final-Row.
        float openYTop = yBottom;
        float openYBottom = yBottom + Math.max(medRowH * 1.6f, medRowH + 6f);
        List<RawPageData.RawChar> openChars = new ArrayList<>();
        for (RawPageData.RawChar c : page.chars) {
            if (c.y > openYTop + 0.5f && c.y < openYBottom
                    && c.x >= xLeft - 1f && c.x <= xRight + 1f) {
                openChars.add(c);
            }
        }
        if (!openChars.isEmpty()) {
            List<String> cells = new ArrayList<>();
            int filled = 0;
            // Auf "echte Reihe" pruefen: chars duerfen nicht weiter als medRowH von
            // der letzten Trennlinie entfernt anfangen (sonst ist's bereits Body).
            float minY = Float.MAX_VALUE;
            for (RawPageData.RawChar c : openChars) minY = Math.min(minY, c.y);
            if (minY - yBottom <= medRowH * 0.9f) {
                for (int col = 0; col < colCount; col++) {
                    float cellLeft = boundaries[col];
                    float cellRight = boundaries[col + 1];
                    StringBuilder cellText = new StringBuilder();
                    List<RawPageData.RawChar> cellChars = new ArrayList<>();
                    for (RawPageData.RawChar c : openChars) {
                        float cx = c.x + c.width / 2f;
                        if (cx >= cellLeft - 1f && cx < cellRight - 1f) cellChars.add(c);
                    }
                    cellChars.sort(Comparator
                            .<RawPageData.RawChar>comparingDouble(c -> c.y)
                            .thenComparingDouble(c -> c.x));
                    Float prevY = null;
                    Float prevEnd = null;
                    for (RawPageData.RawChar c : cellChars) {
                        if (prevY != null && Math.abs(c.y - prevY) > 2f) {
                            if (cellText.length() > 0
                                    && cellText.charAt(cellText.length() - 1) != ' ')
                                cellText.append(' ');
                            prevEnd = null;
                        } else if (prevEnd != null) {
                            float gap = c.x - prevEnd;
                            if (gap > 1.5f
                                    && cellText.length() > 0
                                    && cellText.charAt(cellText.length() - 1) != ' ')
                                cellText.append(' ');
                        }
                        cellText.append(c.text);
                        prevY = c.y;
                        prevEnd = c.x + c.width;
                    }
                    String t = cellText.toString().replaceAll("\\s+", " ").trim();
                    if (!t.isEmpty()) filled++;
                    cells.add(t);
                }
                if (filled >= 2) {
                    rows.add(cells);
                    yBottom = openYBottom;
                }
            }
        }

        return new LineBasedTable(yTop, yBottom, xLeft, xRight, boundaries, rowSeps, rows);
    }
}
