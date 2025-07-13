# Code Coverage

This document describes the code coverage setup and requirements for the ImagePickerKMP project.

## Coverage Requirements

### Thresholds
- **Line Coverage**: Minimum 80%
- **Branch Coverage**: Minimum 70%

### Current Coverage
The project uses JaCoCo to measure code coverage. You can view the current coverage by running:

```bash
./gradlew jacocoTestReport
```

The HTML report will be generated at: `library/build/reports/jacoco/test/html/index.html`

## Coverage Verification

To verify that coverage meets the minimum thresholds:

```bash
./gradlew jacocoTestCoverageVerification
```

This will fail the build if coverage is below the required thresholds.

## Coverage Reports

### HTML Report
- Location: `library/build/reports/jacoco/test/html/index.html`
- Provides detailed coverage information with line-by-line analysis

### XML Report
- Location: `library/build/reports/jacoco/test/jacocoTestReport.xml`
- Used by CI/CD systems and external tools

### Coverage Badge
The project includes a coverage badge that shows the current coverage percentage:
[![Code Coverage](https://codecov.io/gh/ismoy/ImagePickerKMP/branch/main/graph/badge.svg)](https://codecov.io/gh/ismoy/ImagePickerKMP)

## Improving Coverage

### Areas to Focus On
1. **Common Code**: All shared logic should have comprehensive test coverage
2. **Platform-Specific Code**: Android and iOS implementations should be tested
3. **Error Handling**: All exception paths should be covered
4. **Edge Cases**: Boundary conditions and unusual inputs

### Adding Tests
When adding new features, ensure you also add corresponding tests to maintain or improve coverage.

### Coverage Exclusions
Some code may be excluded from coverage requirements:
- Generated code
- Platform-specific implementations that are difficult to test
- UI components that require integration tests

## CI/CD Integration

Coverage is automatically checked in the CI pipeline:
1. Tests are run
2. Coverage report is generated
3. Coverage thresholds are verified
4. Report is uploaded to Codecov

## Local Development

To check coverage during development:

```bash
# Run tests and generate coverage report
./gradlew test jacocoTestReport

# Verify coverage thresholds
./gradlew jacocoTestCoverageVerification

# Open HTML report
open library/build/reports/jacoco/test/html/index.html
```

## Coverage Tools

- **JaCoCo**: Primary coverage tool
- **Codecov**: External coverage reporting service
- **GitHub Actions**: CI/CD integration
- **Gradle**: Build system integration 