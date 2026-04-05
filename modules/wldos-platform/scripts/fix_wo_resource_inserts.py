#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Add NULL for menu_region in wo_resource INSERTs (init.sql or init-h2.sql)."""
import re
import sys
from pathlib import Path

base = Path(__file__).resolve().parent.parent / "src/main/resources/db"
if len(sys.argv) > 1:
    path = Path(sys.argv[1]).resolve()
else:
    path = base / "init-h2.sql"

content = path.read_text(encoding="utf-8")
lines = content.split("\n")
out = []
for line in lines:
    if "INSERT INTO `wo_resource`" in line:
        # display_order 后、is_valid('0'|'1') 前插入 menu_region NULL
        line = re.sub(r", (\d+), '([01])', ", r", \1, NULL, '\2', ", line)
    out.append(line)
path.write_text("\n".join(out), encoding="utf-8")
print("done:", path.name)
