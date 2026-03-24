#!/usr/bin/env python3
"""
Fetches contributors from GitHub API and updates the README.md
between <!-- ALL-CONTRIBUTORS-LIST:START --> and <!-- ALL-CONTRIBUTORS-LIST:END --> markers.
Runs automatically on every push to main/develop via update-contributors.yml
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

REPO = "ismoy/ImagePickerKMP"

# Build table — 7 per row
PER_ROW = 7
rows = ""
for i in range(0, len(humans), PER_ROW):
    chunk = humans[i:i + PER_ROW]
    cells = ""
    for u in chunk:
        login   = u.get("login", "")
        avatar  = u.get("avatar_url", f"https://avatars.githubusercontent.com/{login}")
        profile = u.get("html_url",   f"https://github.com/{login}")
        contribs = u.get("contributions", 0)
        # Show contribution emoji based on commit count
        if contribs >= 50:
            role = "💻 📖 🚧 🎨 🤔"
        elif contribs >= 10:
            role = "💻 🐛"
        else:
            role = "💻"
        cells += (
            f'\n      <td align="center" valign="top" width="14.28%">'
            f'\n        <a href="{profile}">'
            f'\n          <img src="{avatar}" width="100px;" alt="{login}"/><br />'
            f'\n          <sub><b>{login}</b></sub>'
            f'\n        </a><br />'
            f'\n        <a href="https://github.com/{REPO}/commits?author={login}" title="Contributions">{role}</a>'
            f'\n      </td>'
        )
    rows += f"    <tr>{cells}\n    </tr>\n"

table = (
    "<table>\n"
    "  <tbody>\n"
    + rows +
    "  </tbody>\n"
    "</table>"
)

# Replace block between markers in README.md
readme_path = os.environ.get("README_PATH", "README.md")
with open(readme_path, "r", encoding="utf-8") as f:
    content = f.read()

new_block = (
    "<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->\n"
    "<!-- prettier-ignore-start -->\n"
    "<!-- markdownlint-disable -->\n"
    + table + "\n"
    "<!-- markdownlint-restore -->\n"
    "<!-- prettier-ignore-end -->\n"
    "<!-- ALL-CONTRIBUTORS-LIST:END -->"
)

pattern = r"<!-- ALL-CONTRIBUTORS-LIST:START.*?-->.*?<!-- ALL-CONTRIBUTORS-LIST:END -->"
updated = re.sub(pattern, new_block, content, flags=re.DOTALL)

if updated == content:
    print("No changes detected — README already up to date.")
    sys.exit(0)

with open(readme_path, "w", encoding="utf-8") as f:
    f.write(updated)

print("README.md contributors section updated successfully.")

