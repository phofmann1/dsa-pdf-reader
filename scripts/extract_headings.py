"""
Extrahiert NUR Überschriften aus den Raw-JSON-Dateien (page_NNN.json)
in korrekter Leserichtung (Spalte links → rechts, fullwidth oben/unten).

Gleiche Logik wie TextInterpreter.interpretHeadingsOnly():
  H1: fontSize > 25 (Kapitelüberschriften, z.B. 31pt Andalus)
  H2: fontSize > 14 (Abschnitte, z.B. 18pt Andalus)
  H3: fontSize > bodySize AND bold (Unterabschnitte, z.B. 13pt GentiumBasic-Bold)
  H4: fontSize ~ bodySize AND bold AND short (<60 Zeichen)

Fußzeilen (y > pageHeight - 30) werden ignoriert.
Kalligraphische Initialen werden per Band-Erkennung zusammengeführt.
UPPERCASE-Korrektur für gemischte Fontgrößen.
"""

import json
import os
import sys
from collections import Counter

RAW_DIR = os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
    "export", "markdown", "raw",
    "04 - Regionen", "Das Wustenreich (111)",
    "Das Wustenreich - 04 - Regionen"
)

LINE_Y_TOLERANCE = 2.0
BIN_WIDTH = 2.0
MIN_GAP_BINS = 5
GAP_THRESHOLD = 14.0


def classify_char(ch):
    fn = ch.get("fontName", "")
    clean = fn.split("+", 1)[-1] if "+" in fn else fn

    is_bold = ch.get("bold", False)
    if not is_bold:
        is_bold = any(kw in clean for kw in ["Bold", "bold", "SC700", "SC600", "SC800", "SC900", "Heavy", "Black", "SmallCaps"])

    is_initial = ch["fontSize"] > 25
    return {**ch, "isBold": is_bold, "isInitial": is_initial, "cleanFont": clean}


def build_lines(chars, tolerance=LINE_Y_TOLERANCE):
    if not chars:
        return []
    sorted_chars = sorted(chars, key=lambda c: (c["y"], c["x"]))
    lines = []
    cur_y = None
    cur_line = []
    for ch in sorted_chars:
        if cur_y is None or abs(ch["y"] - cur_y) > tolerance:
            if cur_line:
                lines.append((sum(c["y"] for c in cur_line) / len(cur_line), cur_line))
            cur_y = ch["y"]
            cur_line = [ch]
        else:
            cur_line.append(ch)
    if cur_line:
        lines.append((sum(c["y"] for c in cur_line) / len(cur_line), cur_line))
    # Sort chars within each line by x
    for y, line in lines:
        line.sort(key=lambda c: c["x"])
    return lines


def merge_initials(chars):
    """Kalligraphische Initialen: Folgezeichen auf gleiche Y-Position."""
    anchors = sorted([c for c in chars if c["fontSize"] > 25], key=lambda c: -c["fontSize"])
    if not anchors:
        return
    for anchor in anchors:
        top = anchor["y"] - anchor["height"]
        bottom = anchor["y"]
        for ch in chars:
            if ch is anchor:
                continue
            ch_top = ch["y"] - ch["height"]
            if ch_top >= top - 2 and ch_top <= bottom and ch["x"] >= anchor["x"]:
                ch["y"] = anchor["y"]


def find_column_split(chars, page_width):
    num_bins = int(page_width / BIN_WIDTH) + 1
    histogram = [0] * num_bins
    for ch in chars:
        start_bin = max(0, int(ch["x"] / BIN_WIDTH))
        end_bin = min(num_bins - 1, int((ch["x"] + ch["width"]) / BIN_WIDTH))
        for b in range(start_bin, end_bin + 1):
            histogram[b] += 1

    search_start = num_bins // 3
    search_end = num_bins * 2 // 3

    # Strategy 1: Empty zone
    best_start, best_end, best_width = -1, -1, 0
    gap_start = -1
    for b in range(search_start, search_end + 1):
        if histogram[b] <= 2:
            if gap_start < 0:
                gap_start = b
        else:
            if gap_start >= 0 and b - gap_start > best_width:
                best_width = b - gap_start
                best_start = gap_start
                best_end = b
            gap_start = -1
    if gap_start >= 0 and search_end - gap_start > best_width:
        best_width = search_end - gap_start
        best_start = gap_start
        best_end = search_end
    if best_width >= MIN_GAP_BINS:
        return (best_start + best_end) / 2.0 * BIN_WIDTH

    # Strategy 2: Lowest valley
    window = 8
    lowest_density = float('inf')
    lowest_bin = -1
    total_in_range = sum(histogram[search_start:search_end + 1])
    avg_density = total_in_range / max(1, search_end - search_start + 1)

    for b in range(search_start, search_end - window + 1):
        density = sum(histogram[b:b + window]) / window
        if density < lowest_density:
            lowest_density = density
            lowest_bin = b + window // 2

    if lowest_bin > 0 and lowest_density < avg_density * 0.5:
        split_x = lowest_bin * BIN_WIDTH
        left_count = sum(1 for c in chars if c["x"] < split_x - 20)
        right_count = sum(1 for c in chars if c["x"] > split_x + 20)
        if left_count > len(chars) * 0.2 and right_count > len(chars) * 0.2:
            return split_x

    return -1


def find_body_font_size(chars):
    counts = Counter()
    for ch in chars:
        if ch.get("isInitial"):
            continue
        counts[round(ch["fontSize"])] += 1
    if not counts:
        return 10.0
    return float(counts.most_common(1)[0][0])


def line_text(line_chars):
    """Build text with word gap detection and UPPERCASE correction."""
    if not line_chars:
        return ""

    # Min fontSize for UPPERCASE correction
    non_initial = [c for c in line_chars if not c.get("isInitial") and c["text"].strip()]
    min_fs = min((c["fontSize"] for c in non_initial), default=0) if non_initial else 0

    result = []
    prev = None
    for ch in line_chars:
        if prev:
            gap = ch["x"] - (prev["x"] + prev["width"])
            if gap > prev["width"] * 0.3:
                result.append(" ")

        char_text = ch["text"]
        # UPPERCASE correction: smaller letter with larger fontSize = capital
        if min_fs > 0 and ch["fontSize"] > min_fs + 0.5 and len(char_text) == 1 and char_text.isalpha():
            char_text = char_text.upper()

        result.append(char_text)
        prev = ch

    return "".join(result).strip()


def count_large_gaps(line_chars):
    count = 0
    for i in range(1, len(line_chars)):
        gap = line_chars[i]["x"] - (line_chars[i-1]["x"] + line_chars[i-1]["width"])
        if gap > GAP_THRESHOLD:
            count += 1
    return count


def classify_heading(line_chars, body_font_size):
    """Returns (level, text) or None if not a heading."""
    text = line_text(line_chars)
    if not text or len(text) < 2:
        return None

    # Dominant font size (most frequent, excluding initials — fallback to all if all are initial)
    non_initial = [c for c in line_chars if not c.get("isInitial") and c["text"].strip()]
    if not non_initial:
        non_initial = [c for c in line_chars if c["text"].strip()]
    if not non_initial:
        return None

    size_counts = Counter(round(c["fontSize"]) for c in non_initial)
    dominant_size = float(size_counts.most_common(1)[0][0])

    total = len([c for c in line_chars if c["text"].strip()])
    bold_count = len([c for c in line_chars if c["text"].strip() and c.get("isBold")])
    all_bold = total > 0 and bold_count >= total * 0.9
    is_short = len(text) < 60

    if dominant_size > 25:
        return (1, text)
    elif dominant_size > 14:
        return (2, text)
    elif dominant_size > body_font_size + 0.5 and all_bold:
        return (3, text)
    elif all_bold and is_short and dominant_size >= body_font_size - 0.5 and not text.endswith(":"):
        return (4, text)

    return None


def process_page(page_data):
    chars = page_data.get("chars", [])
    if not chars:
        return ""

    page_height = page_data["pageHeight"]
    page_width = page_data["pageWidth"]

    # Classify all chars
    classified = [classify_char(c) for c in chars]

    # Merge initials
    merge_initials(classified)

    # Remove footer (y > pageHeight - 30)
    footer_threshold = page_height - 30
    classified = [c for c in classified if c["y"] <= footer_threshold]
    if not classified:
        return ""

    body_size = find_body_font_size(classified)
    split_x = find_column_split(chars, page_width)

    # Split into columns
    left, right, fullwidth = [], [], []

    if split_x > 0:
        fullwidth_ys = set()
        for c in classified:
            if c.get("isInitial"):
                fullwidth_ys.add(c["y"])

        pre_lines = build_lines(classified)
        for y, line_chars in pre_lines:
            if not line_chars:
                continue

            # Regel: fontSize > 25 ist IMMER fullwidth (Kapitelueberschriften),
            # unabhaengig von der X-Position
            avg_size = sum(c["fontSize"] for c in line_chars) / len(line_chars)
            if avg_size > 25:
                fullwidth_ys.add(y)
                continue

            sorted_lc = sorted(line_chars, key=lambda c: c["x"])
            min_x = sorted_lc[0]["x"]
            max_x = sorted_lc[-1]["x"] + sorted_lc[-1]["width"]
            if not (min_x < split_x - 30 and max_x > split_x + 30):
                continue

            # Check for large gap overlapping column split = two column headings, not fullwidth
            has_gap_at_split = False
            for j in range(1, len(sorted_lc)):
                prev_end = sorted_lc[j-1]["x"] + sorted_lc[j-1]["width"]
                next_start = sorted_lc[j]["x"]
                gap_size = next_start - prev_end
                # Gap > 3x char width AND column split falls inside the gap
                if gap_size > sorted_lc[j-1]["width"] * 3 and prev_end < split_x and next_start > split_x:
                    has_gap_at_split = True
                    break
            if has_gap_at_split:
                continue

            # Line spans columns without gap at split = fullwidth
            # (e.g. a word bridging the column boundary)
            fullwidth_ys.add(y)

        for ch in classified:
            is_fw = any(abs(ch["y"] - fwy) < LINE_Y_TOLERANCE + 1 for fwy in fullwidth_ys)
            if is_fw:
                fullwidth.append(ch)
            elif ch["x"] + ch["width"] / 2 < split_x:
                left.append(ch)
            else:
                right.append(ch)
    else:
        fullwidth = classified

    # Build lines per column
    left_lines = build_lines(left)
    right_lines = build_lines(right)
    fw_lines = build_lines(fullwidth)

    # Tabellen-Y-Positionen auf den vollen preLines erkennen (vor Spaltenaufteilung)
    # Schritt 1: Zeilen mit >=2 Gaps
    table_ys = set()
    all_pre_lines = build_lines(classified)
    for y, lc in all_pre_lines:
        if count_large_gaps(lc) >= 2:
            table_ys.add(y)
    # Schritt 2: Schmale Tabellen (2 Spalten, 1 Gap) per Repetition erkennen
    for pi, (y, lc) in enumerate(all_pre_lines):
        if y in table_ys:
            continue
        gaps_here = count_large_gaps(lc)
        if gaps_here != 1:
            continue
        # Gap-Position ermitteln
        sorted_lc = sorted(lc, key=lambda c: c["x"])
        gap_pos = None
        for j in range(1, len(sorted_lc)):
            g = sorted_lc[j]["x"] - (sorted_lc[j-1]["x"] + sorted_lc[j-1]["width"])
            if g > GAP_THRESHOLD:
                gap_pos = sorted_lc[j]["x"]
                break
        if gap_pos is None:
            continue
        # Streak zaehlen
        streak = 1
        for pj in range(pi + 1, len(all_pre_lines)):
            ny, nlc = all_pre_lines[pj]
            ng = count_large_gaps(nlc)
            if ng < 1:
                break
            if ng == 1:
                ns = sorted(nlc, key=lambda c: c["x"])
                np = None
                for j in range(1, len(ns)):
                    g = ns[j]["x"] - (ns[j-1]["x"] + ns[j-1]["width"])
                    if g > GAP_THRESHOLD:
                        np = ns[j]["x"]
                        break
                if np is not None and abs(np - gap_pos) < 30:
                    streak += 1
                else:
                    break
            else:
                break
        if streak >= 3:
            for pj in range(pi, pi + streak):
                table_ys.add(all_pre_lines[pj][0])

    # Schritt 3: Propagation — Zeilen mit 1+ Gap neben bekannter Tabellenzeile,
    # aber nur wenn Y-Abstand klein (< 20pt, kein Absatzwechsel)
    changed = True
    while changed:
        changed = False
        for pi, (y, lc) in enumerate(all_pre_lines):
            if y in table_ys:
                continue
            if count_large_gaps(lc) < 1:
                continue
            above = (pi > 0 and all_pre_lines[pi - 1][0] in table_ys
                     and abs(y - all_pre_lines[pi - 1][0]) < 20)
            below = (pi + 1 < len(all_pre_lines) and all_pre_lines[pi + 1][0] in table_ys
                     and abs(all_pre_lines[pi + 1][0] - y) < 20)
            if above or below:
                table_ys.add(y)
                changed = True

    # Collect headings in reading order: (level, text, y)
    # level=-1 = Separator (non-heading line between headings)
    # Fullwidth lines between column text act as dividers:
    # top_fw → (left to divider, right to divider) → divider → ... → bottom_fw
    headings = []
    has_columns = bool(left_lines) and bool(right_lines)

    def collect(lines_list):
        for y, lc in lines_list:
            # Tabellen-Zeile? (erkannt auf vollen preLines)
            is_table = any(abs(y - ty) < LINE_Y_TOLERANCE + 1 for ty in table_ys)
            if is_table:
                if headings and headings[-1][0] > 0:
                    headings.append((-1, "", y))
                continue
            h = classify_heading(lc, body_size)
            if h:
                headings.append((h[0], h[1], y))
            elif headings and headings[-1][0] > 0:
                headings.append((-1, "", y))  # Separator

    if has_columns and fw_lines:
        fw_lines.sort(key=lambda l: l[0])
        left_lines = sorted(left_lines, key=lambda l: l[0])
        right_lines = sorted(right_lines, key=lambda l: l[0])

        col_min_y = min(
            min((l[0] for l in left_lines), default=float('inf')),
            min((l[0] for l in right_lines), default=float('inf'))
        )
        col_max_y = max(
            max((l[0] for l in left_lines), default=0),
            max((l[0] for l in right_lines), default=0)
        )

        top_fw = [(y, lc) for y, lc in fw_lines if y < col_min_y]
        dividers = [(y, lc) for y, lc in fw_lines if col_min_y <= y <= col_max_y]
        bottom_fw = [(y, lc) for y, lc in fw_lines if y > col_max_y]

        # Top fullwidth
        collect(top_fw)

        # Column sections split by dividers
        cut_ys = [y for y, lc in dividers] + [float('inf')]
        li, ri = 0, 0
        for di, cut_y in enumerate(cut_ys):
            # Left column up to cut_y
            while li < len(left_lines) and left_lines[li][0] < cut_y:
                y, lc = left_lines[li]
                is_table = any(abs(y - ty) < LINE_Y_TOLERANCE + 1 for ty in table_ys)
                if is_table:
                    if headings and headings[-1][0] > 0:
                        headings.append((-1, "", y))
                else:
                    h = classify_heading(lc, body_size)
                    if h:
                        headings.append((h[0], h[1], y))
                    elif headings and headings[-1][0] > 0:
                        headings.append((-1, "", y))
                li += 1
            # Right column up to cut_y
            while ri < len(right_lines) and right_lines[ri][0] < cut_y:
                y, lc = right_lines[ri]
                is_table = any(abs(y - ty) < LINE_Y_TOLERANCE + 1 for ty in table_ys)
                if is_table:
                    if headings and headings[-1][0] > 0:
                        headings.append((-1, "", y))
                else:
                    h = classify_heading(lc, body_size)
                    if h:
                        headings.append((h[0], h[1], y))
                    elif headings and headings[-1][0] > 0:
                        headings.append((-1, "", y))
                ri += 1
            # Divider itself
            if di < len(dividers):
                dy, dlc = dividers[di]
                h = classify_heading(dlc, body_size)
                if h:
                    headings.append((h[0], h[1], dy))

        # Bottom fullwidth
        collect(bottom_fw)

    elif has_columns:
        collect(sorted(left_lines, key=lambda l: l[0]))
        collect(sorted(right_lines, key=lambda l: l[0]))
    else:
        collect(sorted(fw_lines, key=lambda l: l[0]))

    # Aufeinanderfolgende Ueberschriften gleicher Ebene zusammenfassen,
    # aber nur wenn:
    # - Y-Abstand klein (mehrzeilige Ueberschrift, max ~50pt)
    # - kein Separator (non-heading Zeile) dazwischen
    merged = []
    had_separator = False
    for level, text, y in headings:
        if level < 0:
            had_separator = True
            continue
        prev_text = merged[-1][1] if merged else ""
        # Nur zusammenfassen wenn Text den vorherigen fortsetzt:
        # - beginnt mit Kleinbuchstabe (Wortfortsetzung)
        # - oder vorheriger endet mit Bindestrich (Silbentrennung)
        continues = (text and (text[0].islower()
                     or prev_text.endswith("-")
                     or prev_text.endswith("\u2013")))
        if (merged and merged[-1][0] == level
                and not had_separator
                and abs(y - merged[-1][2]) < 50
                and continues):
            if prev_text.endswith("-") or prev_text.endswith("\u2013"):
                merged[-1] = (level, prev_text[:-1] + text, y)
            else:
                merged[-1] = (level, prev_text + " " + text, y)
        else:
            merged.append((level, text, y))
        had_separator = False

    return [(level, text) for level, text, y in merged]


def main():
    raw_dir = RAW_DIR
    if len(sys.argv) > 1:
        raw_dir = sys.argv[1]

    if not os.path.isdir(raw_dir):
        print(f"Verzeichnis nicht gefunden: {raw_dir}", file=sys.stderr)
        sys.exit(1)

    files = sorted(f for f in os.listdir(raw_dir) if f.startswith("page_") and f.endswith(".json"))

    output = []
    output.append("# Das Wüstenreich – Überschriften\n")

    in_index = False  # Index-Seiten ueberspringen (keine echten Ueberschriften)

    for f in files:
        page_num = int(f.split("_")[1].split(".")[0])
        with open(os.path.join(raw_dir, f), "r", encoding="utf-8") as fh:
            data = json.load(fh)

        headings = process_page(data)

        # Index-Erkennung: "INHALTSVERZEICHNIS" als H1 markiert den Beginn
        # Index endet wenn die naechste H1 kommt (naechstes Kapitel)
        has_h1 = any(level == 1 for level, text in headings)
        is_toc_start = any(level == 1 and "INHALTSVERZEICHNIS" in text.upper()
                           for level, text in headings)

        if is_toc_start:
            in_index = True
        elif has_h1 and in_index:
            in_index = False  # Neues Kapitel beendet den Index

        if in_index:
            # Im Index nur die H1 selbst ausgeben, sonst nichts
            h1_only = [(l, t) for l, t in headings if l == 1]
            if h1_only:
                output.append(f"<!-- Seite {page_num} -->")
                for level, text in h1_only:
                    output.append(f"# {text}")
            continue

        if headings:
            output.append(f"<!-- Seite {page_num} -->")
            for level, text in headings:
                prefix = "#" * level
                output.append(f"{prefix} {text}")

    result = "\n".join(output) + "\n"

    # Write to file
    out_path = os.path.join(os.path.dirname(raw_dir), "_headings.md")
    with open(out_path, "w", encoding="utf-8") as fh:
        fh.write(result)

    # Also print
    print(result)
    print(f"\n--- Geschrieben nach: {out_path} ---", file=sys.stderr)


if __name__ == "__main__":
    main()
