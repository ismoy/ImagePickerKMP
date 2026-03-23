#  Contributing to ImagePickerKMP

We’re excited to welcome contributions to **ImagePickerKMP** — whether it's fixing bugs, improving documentation, or adding new features!

---

##  Steps to Contribute

1. **Fork the repository**
   > ⚠️ **Important:** Do not clone this repo directly.  
   Always **fork** it to your own GitHub account first — otherwise, you won’t be able to submit Pull Requests (PRs) to the main repository.

2. **Create a new feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Build & Test**
   ```bash
   # Compile the project
   ./gradlew build

   # Run unit tests (JVM — fast, no emulator needed)
   ./gradlew :library:jvmTest

   # Run Android unit tests
   ./gradlew :library:testDebugUnitTest

   # Run all tests
   ./gradlew :library:allTests

   # Run static analysis
   ./gradlew detekt
   ```

4. **Check Test Coverage**

   Generate the coverage report and verify it meets the minimum threshold (97%):
   ```bash
   # Generate HTML + XML report and verify the 97% threshold
   ./gradlew :library:koverVerify

   # Generate only the HTML report (open in browser)
   ./gradlew :library:koverHtmlReport

   # Generate only the XML report (used by Codecov)
   ./gradlew :library:koverXmlReport
   ```

   After running `koverHtmlReport`, open the report in your browser:
   ```bash
   open library/build/reports/kover/html/index.html
   ```

   > ⚠️ **Minimum required coverage: 97% line coverage.**  
   > All PRs must pass `koverVerify` without errors. If you add new code, add tests to keep coverage above the threshold.

4. **Follow Code Style**
   - Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
   - Use **4 spaces** for indentation
   - Limit lines to **≤120 characters**
   - Always use **trailing commas**
   - Use `expect/actual` for platform-specific code implementations

5. **Commit & Push**
   ```bash
   git commit -m "feat: add custom permission dialog"
   git push origin feature/your-feature-name
   ```

6. **Open a Pull Request**
   - Provide a **clear description** of the change
   - Link any **related issues**
   - Include **tests** and **documentation updates** if needed

---

##  Issues &  Feature Requests

If you find a bug or have an idea for a new feature:

- For **bugs**: include clear reproduction steps, expected vs. actual behavior, and device/OS information.
- For **features**: describe the use case, your proposal, and any alternatives you’ve considered.

Please check for **existing issues** before opening a new one to avoid duplicates.

---

##  Branching Model

| Branch | Purpose |
|--------|----------|
| `main` | Stable, production-ready code |
| `develop` | Active development |
| `feature/*` | New features |
| `fix/*` | Bug fixes |
| `release/*` / `hotfix/*` | Pre-release or urgent fixes |

---

## 💬 Need Help?

- [Open an Issue](https://github.com/ismoy/ImagePickerKMP/issues)
- [Join Discussions](https://github.com/ismoy/ImagePickerKMP/discussions)
- Discord community *(coming soon)*

---

### ✅ TL;DR

**Fork → New Branch → Code + Tests → `./gradlew :library:koverVerify` (≥97%) → `./gradlew detekt` → Commit → Push → PR**

---
