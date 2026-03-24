#!/usr/bin/env python3
"""
Fetches sponsors from Firebase Realtime Database and updates the README.md
between <!-- SPONSORS-LIST:START --> and <!-- SPONSORS-LIST:END --> markers.
"""
import json, os, re, sys

FIREBASE_URL = "https://belzspeedscan-default-rtdb.firebaseio.com/ImagePickerKMPSponsor.json"

# Read JSON passed via env var (fetched by curl in the workflow)
raw = os.environ.get("SPONSORS_JSON", "")

if not raw or raw.strip() == "null":
    print("No sponsors data — keeping placeholder.")
    nodes = []
else:
    try:
        data = json.loads(raw)
        nodes = list(data.values()) if isinstance(data, dict) else data
    except Exception as e:
        print(f"JSON parse error: {e}")
        nodes = []

print(f"Found {len(nodes)} sponsors")

PER_ROW = 7

if not nodes:
    table = (
        '<table>\n'
        '  <tr>\n'
        '    <td align="center">\n'
        '      <a href="https://github.com/sponsors/ismoy">\n'
        '        <img src="https://via.placeholder.com/80x80.png?text=You%3F" width="80px" alt="Your name here"/><br/>\n'
        '        <sub><b>Be the first sponsor!</b></sub>\n'
        '      </a>\n'
        '    </td>\n'
        '  </tr>\n'
        '</table>'
    )
else:
    rows = ""
    for i in range(0, len(nodes), PER_ROW):
        chunk = nodes[i:i+PER_ROW]
        cells = ""
        for s in chunk:
            login  = s.get("login", "")
            tier   = s.get("tier", "")
            avatar = f"https://avatars.githubusercontent.com/{login}?s=80"
            url    = f"https://github.com/{login}"
            title  = f"{login} · {tier}" if tier else login
            cells += (
                f'\n    <td align="center">'
                f'\n      <a href="{url}" title="{title}">'
                f'\n        <img src="{avatar}" width="80px" alt="{login}"/><br/>'
                f'\n        <sub><b>{login}</b></sub>'
                f'\n      </a>'
                f'\n    </td>'
            )
        rows += f"  <tr>{cells}\n  </tr>\n"
    table = f"<table>\n{rows}</table>"

# Replace block in README.md
readme_path = os.environ.get("README_PATH", "README.md")
with open(readme_path, "r", encoding="utf-8") as f:
    content = f.read()

new_block = (
    "<!-- SPONSORS-LIST:START - Do not remove or modify this section -->\n"
    + table + "\n"
    + "<!-- SPONSORS-LIST:END -->"
)

pattern = r"<!-- SPONSORS-LIST:START.*?-->.*?<!-- SPONSORS-LIST:END -->"
updated = re.sub(pattern, new_block, content, flags=re.DOTALL)

if updated == content:
    print("No changes detected — README already up to date.")
    sys.exit(0)

with open(readme_path, "w", encoding="utf-8") as f:
    f.write(updated)

print("README.md sponsors section updated successfully.")
