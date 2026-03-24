#!/usr/bin/env python3
"""
Fetches contributors from GitHub API and updates the README.md
between <!-- CONTRIBUTORS-LIST:START --> and <!-- CONTRIBUTORS-LIST:END --> markers.
"""
import json, os, re, sys

raw = os.environ.get("CONTRIBUTORS_JSON", "[]")
try:
    data = json.loads(raw)
except Exception as e:
    print(f"JSON parse error: {e}")
    sys.exit(0)

# Filter out bots
humans = [u for u in data if u.get("type", "") != "Bot" and "[bot]" not in u.get("login", "")]
print(f"Found {len(humans)} human contributors")

if not humans:
    print("No contributors found — skipping update.")
    sys.exit(0)

# Build table — 7 per row, matching GitHub's all-contributors style
PER_ROW = 7
rows = ""
for i in range(0, len(humans), PER_ROW):
    chunk = humans[i:i+PER_ROW]
    cells = ""
    for u in chunk:
        login    = u.get("login", "")
        avatar   = u.get("avatar_url", f"https://github.com/{login}.png")
        profile  = u.get("html_url",   f"https://github.com/{login}")
        contribs = u.get("contributions", 0)
        suffix   = "s" if contribs != 1 else ""
        cells += (
            f'\n        <td align="center" valign="top" width="14.28%">'
            f'\n          <a href="{profile}">'
            f'\n            <img src="{avatar}&s=100" width="80px;" alt="{login}"/><br />'
            f'\n            <sub><b>{login}</b></sub>'
            f'\n          </a><br />'
            f'\n          <sub>{contribs} commit{suffix}</sub>'
            f'\n        </td>'
        )
    rows += f"      <tr>{cells}\n      </tr>\n"

table = (
    "<table>\n"
    "      <tbody>\n"
    + rows +
    "      </tbody>\n"
    "    </table>"
)

# Replace block between markers in README.md
readme_path = os.environ.get("README_PATH", "README.md")
with open(readme_path, "r", encoding="utf-8") as f:
    content = f.read()

new_block = (
    "<!-- CONTRIBUTORS-LIST:START - Do not remove or modify this section -->\n"
    + table + "\n"
    + "<!-- CONTRIBUTORS-LIST:END -->"
)

pattern = r"<!-- CONTRIBUTORS-LIST:START.*?-->.*?<!-- CONTRIBUTORS-LIST:END -->"
updated = re.sub(pattern, new_block, content, flags=re.DOTALL)

if updated == content:
    print("No changes detected — README already up to date.")
    sys.exit(0)

with open(readme_path, "w", encoding="utf-8") as f:
    f.write(updated)

print("README.md contributors section updated successfully.")
