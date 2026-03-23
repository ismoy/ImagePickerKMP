# Code Coverage

This document describes the code coverage setup and requirements for the ImagePickerKMP project.

## Coverage Requirements

### Thresholds
- **Line Coverage**: Minimum 70%

> The threshold is enforced automatically on every build via `koverVerify`. The build will fail if coverage drops below 70%.

### Current Coverage
The project uses **Kover** (JetBrains' official Kotlin coverage tool) to measure code coverage.
Kover works natively with Kotlin Multiplatform — no instrumentation workarounds needed.

## Generating Reports

```bash
# Generate HTML + XML reports
./gradlew koverXmlReport koverHtmlReport

# Verify thresholds (fails build if below 70%)
./gradlew koverVerify
```

## Coverage Reports

### HTML Report
- Location: `library/build/reports/kover/html/index.html`
- Provides detailed coverage information with line-by-line analysis

### XML Report
- Location: `library/build/reports/kover/report.xml`
- Used by CI/CD and Codecov

### Coverage Badge
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)

## Exclusions

The following are excluded from coverage measurement:
- Composable UI components (`presentation/ui/components/`, `presentation/ui/screens/`)
- Compose config classes with UI dependencies (`UiConfig`, `ImagePickerConfig`, etc.)
- Camera hardware layer (`data/camera/`, `data/managers/`)
- Generated Kotlin classes (`ComposableSingletons`, `$Companion`, `$WhenMappings`)

## CI/CD Integration

Coverage is automatically checked in the CI pipeline on every push and pull request:
1. Tests are run
2. Kover XML report is generated
3. `koverVerify` enforces the 70% minimum threshold
4. Report is uploaded to Codecov

## Local Development

```bash
# Run tests, generate report, and verify threshold
./gradlew test koverXmlReport koverVerify

# Open HTML report (macOS)
open library/build/reports/kover/html/index.html
```

## Coverage Tools

- **Kover**: Primary coverage tool (Kotlin-native, replaces JaCoCo)
- **Codecov**: External coverage reporting and badge service
- **GitHub Actions**: CI/CD integration
