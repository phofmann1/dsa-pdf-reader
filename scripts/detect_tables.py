#!/usr/bin/env python3
"""Detect tables in raw JSON data and format them as markdown tables in text files.

Table detection strategy:
- Only detect tables WITHIN columns of two-column pages (splitX > 0)
- This avoids false positives on credits pages, TOC pages, etc.
- Validates gap position consistency across rows
"""

import json
import os
import re
import sys
from pathlib import Path

BASE = Path("D:/develop/project/java/dsa-pdf-reader")
RAW_BASE = BASE / "export" / "claude" / "raw"
TEXT_BASE = BASE / "export" / "claude" / "text"

LINE_Y_TOL = 3.0
GAP_THRESHOLD = 14.0
GAP_THRESHOLD_IN_TABLE = 5.0  # lower threshold for lines within a detected table
MIN_TABLE_ROWS = 2
BIN_WIDTH = 2.0
GAP_POS_TOLERANCE = 30.0  # max deviation of gap-end positions across rows

DRY_RUN = "--dry-run" in sys.argv


def find_col_split(chars, pw):
    nb = int(pw / BIN_WIDTH) + 1
    h = [0] * nb
    for c in chars:
        for b in range(max(0, int(c['x'] / BIN_WIDTH)), min(nb - 1, int((c['x'] + c['width']) / BIN_WIDTH)) + 1):
            h[b] += 1
    t = nb // 3
    bs, bl, rs, rl = -1, 0, -1, 0
    for b in range(t, 2 * t):
        if h[b] <= 2:
            if rs < 0:
                rs, rl = b, 1
            else:
                rl += 1
            if rl > bl:
                bs, bl = rs, rl
        else:
            rs, rl = -1, 0
    return (bs + bl / 2) * BIN_WIDTH if bl >= 5 else -1


def make_lines(chars):
    if not chars:
        return []
    sc = sorted(chars, key=lambda c: (c['y'], c['x']))
    lines, cur = [], [sc[0]]
    for ch in sc[1:]:
        if abs(ch['y'] - cur[-1]['y']) <= LINE_Y_TOL:
            cur.append(ch)
        else:
            cur.sort(key=lambda c: c['x'])
            lines.append(cur)
            cur = [ch]
    cur.sort(key=lambda c: c['x'])
    lines.append(cur)
    return lines


def ch2txt(chars):
    if not chars:
        return ""
    t = ""
    for i, c in enumerate(chars):
        if i > 0 and c['x'] - (chars[i - 1]['x'] + chars[i - 1]['width']) > 1.5:
            t += " "
        t += c['text']
    return t.strip()


def split_line_at_gaps(line, threshold=None):
    """Split a line into cells at large gaps.
    Returns (cells, gap_end_positions) or None if no gaps found."""
    if threshold is None:
        threshold = GAP_THRESHOLD

    if len(line) < 2:
        return None

    gaps = []
    for j in range(1, len(line)):
        prev_end = line[j - 1]['x'] + line[j - 1]['width']
        gap_size = line[j]['x'] - prev_end
        if gap_size > threshold:
            gaps.append((prev_end, line[j]['x']))

    if not gaps:
        return None

    gaps.sort(key=lambda g: g[0])
    gap_ends = [g[1] for g in gaps]

    cells, cell = [], []
    gi = 0
    for ch in line:
        if gi < len(gaps) and ch['x'] > gaps[gi][0] + 1:
            cells.append(ch2txt(cell))
            cell = [ch]
            gi += 1
        else:
            cell.append(ch)
    cells.append(ch2txt(cell))

    return cells, gap_ends


def check_gap_consistency(all_gap_ends):
    """Check if gap-end positions are consistent across rows."""
    if len(all_gap_ends) < 2:
        return True

    num_gaps = len(all_gap_ends[0])
    for g in range(num_gaps):
        positions = [ge[g] for ge in all_gap_ends if g < len(ge)]
        if len(positions) < 2:
            continue
        avg = sum(positions) / len(positions)
        max_dev = max(abs(p - avg) for p in positions)
        if max_dev > GAP_POS_TOLERANCE:
            return False
    return True


def detect_tables(lines):
    """Detect tables within a column's lines."""
    tables = []
    rows = []
    gap_ends_list = []
    first_idx = -1
    table_col_count = 0
    had_continuation = False  # only allow one continuation per row

    for i, line in enumerate(lines):
        result = split_line_at_gaps(line)

        if result:
            cells, gap_ends = result
            col_count = len(cells)
            had_continuation = False

            if not rows:
                rows = [cells]
                gap_ends_list = [gap_ends]
                first_idx = i
                table_col_count = col_count
            elif col_count == table_col_count:
                rows.append(cells)
                gap_ends_list.append(gap_ends)
            else:
                if len(rows) >= MIN_TABLE_ROWS and check_gap_consistency(gap_ends_list):
                    tables.append((rows, first_idx))
                rows = [cells]
                gap_ends_list = [gap_ends]
                first_idx = i
                table_col_count = col_count
        else:
            # When inside a table, try lower threshold first
            if rows and line:
                result_low = split_line_at_gaps(line, GAP_THRESHOLD_IN_TABLE)
                if result_low and len(result_low[0]) == table_col_count:
                    cells_low, gap_ends_low = result_low
                    rows.append(cells_low)
                    gap_ends_list.append(gap_ends_low)
                    had_continuation = False
                    continue

            # Check for continuation line (max one per row, short text only)
            if rows and line and not had_continuation:
                line_text = ch2txt(line)
                first_line = lines[first_idx]
                if (first_line
                        and abs(line[0]['x'] - first_line[0]['x']) < 10
                        and len(line_text) < 40
                        and len(rows[-1][0]) < 80):
                    rows[-1][0] += " " + line_text
                    had_continuation = True
                    continue

            if len(rows) >= MIN_TABLE_ROWS and check_gap_consistency(gap_ends_list):
                tables.append((rows, first_idx))
            rows = []
            gap_ends_list = []
            first_idx = -1
            table_col_count = 0
            had_continuation = False

    if len(rows) >= MIN_TABLE_ROWS and check_gap_consistency(gap_ends_list):
        tables.append((rows, first_idx))

    return tables


def norm(s):
    import unicodedata
    s = unicodedata.normalize('NFC', s)
    s = re.sub(r'\*+', '', s)
    s = s.replace('\u00a0', ' ').replace('\u2013', '-').replace('\u2014', '-')
    s = re.sub(r'\s+', ' ', s).strip().lower()
    return s


def words_overlap(a, b):
    wa = set(a.split())
    wb = set(b.split())
    if not wa or not wb:
        return 0
    return len(wa & wb) / max(len(wa), len(wb))


def similar(a, b):
    if not a or not b:
        return False
    if a == b:
        return True
    return words_overlap(a, b) > 0.7


def flatten_row(cells):
    return " ".join(c for c in cells if c)


def build_md_table(rows):
    if not rows:
        return ""
    max_cols = max(len(r) for r in rows)
    for r in rows:
        while len(r) < max_cols:
            r.append("")
    lines = []
    lines.append("| " + " | ".join(rows[0]) + " |")
    lines.append("| " + " | ".join("---" for _ in rows[0]) + " |")
    for row in rows[1:]:
        lines.append("| " + " | ".join(row) + " |")
    return "\n".join(lines)


def find_table_in_text(text_lines, table_rows):
    """Find the line range in text_lines matching the table rows."""
    flat_rows = [norm(flatten_row(row)) for row in table_rows]

    start = -1
    for i, line in enumerate(text_lines):
        nl = norm(line)
        if nl and flat_rows[0] and similar(nl, flat_rows[0]):
            start = i
            break

    if start < 0:
        return None

    end = start
    row_idx = 1
    li = start + 1

    while li < len(text_lines) and row_idx < len(flat_rows):
        nl = norm(text_lines[li])
        if not nl:
            li += 1
            continue

        if similar(nl, flat_rows[row_idx]):
            end = li
            row_idx += 1
            li += 1
        else:
            # Try combining up to 3 consecutive text lines to match one table row
            matched = False
            for span in range(2, 4):  # try 2 or 3 lines combined
                if li + span - 1 >= len(text_lines):
                    break
                parts = [norm(text_lines[li + k]) for k in range(span)]
                combined = " ".join(p for p in parts if p)
                if similar(combined, flat_rows[row_idx]):
                    end = li + span - 1
                    row_idx += 1
                    li += span
                    matched = True
                    break
            if not matched:
                break

    if row_idx < MIN_TABLE_ROWS:
        return None

    return start, end


def process_page(raw_path, text_path):
    with open(raw_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    chars = data.get('chars', [])
    if not chars:
        return 0, []

    pw = data.get('pageWidth', 600)
    split_x = find_col_split(chars, pw)

    # Only detect tables on two-column pages
    if split_x <= 0:
        return 0, []

    col1 = [c for c in chars if c['x'] + c['width'] / 2 < split_x]
    col2 = [c for c in chars if c['x'] + c['width'] / 2 >= split_x]

    all_tables = []
    for col_chars in [col1, col2]:
        col_lines = make_lines(col_chars)
        tables = detect_tables(col_lines)
        all_tables.extend(tables)

    if not all_tables:
        return 0, []

    if not text_path.exists():
        return 0, []

    with open(text_path, 'r', encoding='utf-8') as f:
        text_content = f.read()

    text_lines = text_content.split('\n')
    replacements = []
    previews = []

    for table_rows, _ in all_tables:
        result = find_table_in_text(text_lines, table_rows)
        if result:
            start, end = result
            md_table = build_md_table(table_rows)
            replacements.append((start, end, md_table))
            # Preview: first row of table
            preview = " | ".join(table_rows[0][:3])
            previews.append(f"[{len(table_rows)}rows] {preview[:60]}")

    if not replacements:
        return 0, []

    replacements.sort(key=lambda r: r[0], reverse=True)
    for start, end, md_table in replacements:
        text_lines[start:end + 1] = [md_table]

    # Post-cleanup: remove orphan lines after tables
    # These are continuation fragments that weren't matched
    cleaned = []
    i = 0
    while i < len(text_lines):
        line = text_lines[i]
        if '|' in line and line.strip().startswith('|'):
            # This is a table block - collect all table lines
            table_block = []
            while i < len(text_lines) and text_lines[i].strip().startswith('|'):
                table_block.append(text_lines[i])
                i += 1
            cleaned.extend(table_block)

            # Check following lines for orphans (substrings of table cells)
            table_text = norm(" ".join(table_block))
            while i < len(text_lines):
                next_line = text_lines[i].strip()
                if not next_line:
                    cleaned.append(text_lines[i])
                    i += 1
                    break
                next_norm = norm(next_line)
                if next_norm and len(next_norm) < 60:
                    # Check if all words of this line appear in the table
                    words = set(next_norm.split())
                    if words and words.issubset(set(table_text.split())):
                        i += 1  # skip orphan line
                        continue
                break
        else:
            cleaned.append(line)
            i += 1
    text_lines = cleaned

    new_content = '\n'.join(text_lines)

    if new_content != text_content and not DRY_RUN:
        with open(text_path, 'w', encoding='utf-8') as f:
            f.write(new_content)

    return len(replacements), previews


def main():
    total_tables = 0
    total_files = 0
    total_pages = 0
    errors = 0

    for root, dirs, files in os.walk(RAW_BASE):
        for f in sorted(files):
            if not f.startswith('page_') or not f.endswith('.json'):
                continue
            raw_path = Path(root) / f
            rel = raw_path.relative_to(RAW_BASE)
            page_num = f.replace('page_', '').replace('.json', '')
            text_rel = rel.parent / f"seite_{page_num}.md"
            text_path = TEXT_BASE / text_rel
            total_pages += 1

            try:
                n, previews = process_page(raw_path, text_path)
                if n > 0:
                    total_tables += n
                    total_files += 1
                    for p in previews:
                        print(f"  {rel.parent.name}/page_{page_num}: {p}", flush=True)
            except Exception as e:
                errors += 1
                if errors <= 20:
                    print(f"  ERROR: {rel}: {e}", flush=True)

            if total_pages % 1000 == 0:
                print(f"  ... {total_pages} pages ...", flush=True)

    mode = "DRY RUN" if DRY_RUN else "APPLIED"
    print(f"\n{mode}: {total_tables} tables in {total_files} files ({total_pages} pages), {errors} errors", flush=True)


if __name__ == '__main__':
    main()
