#!/usr/bin/env python3
"""Undo markdown table formatting in text files - restore to plain text."""

import os
import re
from pathlib import Path

TEXT_BASE = Path("D:/develop/project/java/dsa-pdf-reader/export/claude/text")

# Only process categories 00-05 (which the script touched)
AFFECTED_CATEGORIES = ["00 - Fluff", "01 - Regeln", "02 - Ausrustung",
                        "03 - Welt", "04 - Mysterien", "04 - Regionen",
                        "05 - Abenteuer"]


def undo_tables_in_file(filepath):
    """Remove markdown table formatting, restore to plain text."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    if '|' not in content:
        return False

    lines = content.split('\n')
    new_lines = []
    changed = False

    for line in lines:
        # Skip separator lines like "| --- | --- |"
        if re.match(r'^\|\s*---', line):
            changed = True
            continue

        # Convert table row "| cell1 | cell2 |" back to "cell1 cell2"
        if re.match(r'^\|.*\|$', line.rstrip()):
            # Strip leading/trailing pipes and split cells
            inner = line.strip().strip('|')
            cells = [c.strip() for c in inner.split('|')]
            restored = ' '.join(c for c in cells if c)
            new_lines.append(restored)
            changed = True
        else:
            new_lines.append(line)

    if changed:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(new_lines))

    return changed


def main():
    total_fixed = 0

    for cat in AFFECTED_CATEGORIES:
        cat_path = TEXT_BASE / cat
        if not cat_path.exists():
            continue

        for root, dirs, files in os.walk(cat_path):
            for f in sorted(files):
                if not f.startswith('seite_') or not f.endswith('.md'):
                    continue
                filepath = Path(root) / f
                try:
                    if undo_tables_in_file(filepath):
                        total_fixed += 1
                except Exception as e:
                    print(f"  ERROR: {filepath}: {e}", flush=True)

    print(f"Fixed {total_fixed} files", flush=True)


if __name__ == '__main__':
    main()
