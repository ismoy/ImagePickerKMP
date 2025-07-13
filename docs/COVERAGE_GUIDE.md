# Coverage Guide

This guide explains how code coverage works in the ImagePickerKMP project and how to manage coverage thresholds.

## Current Coverage Status

- **Line Coverage**: 4% (minimum threshold: 4%)
- **Branch Coverage**: 0% (minimum threshold: 0%)

## Coverage Thresholds

The project uses JaCoCo to enforce minimum coverage thresholds:

- **Line Coverage**: Minimum 4% (configurable in `library/build.gradle.kts`)
- **Branch Coverage**: Minimum 0% (configurable in `library/build.gradle.kts`)

## How to Increase Coverage

### 1. Add More Unit Tests

Focus on testing the core logic classes:

```kotlin
// Example: Add tests for ImageProcessor
@Test
fun `test image processing with different formats`() {
    // Test image processing logic
}
```

### 2. Test UI Components (When Ready)

Once Compose testing is properly configured, add tests for UI components:

```kotlin
@Test
fun `test image picker launcher composable`() {
    // Test UI components
}
```

### 3. Test Platform-Specific Code

Add tests for Android and iOS specific implementations.

## Coverage Commands

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```

### Verify Coverage Thresholds
```bash
./gradlew jacocoTestCoverageVerification
```

### View HTML Report
Open `library/build/reports/jacoco/jacocoTestReport/html/index.html` in your browser.

## Adjusting Thresholds

To increase coverage thresholds:

1. **Gradually increase thresholds** in `library/build.gradle.kts`:
   ```kotlin
   minimum = "0.10".toBigDecimal() // 10% line coverage
   minimum = "0.05".toBigDecimal() // 5% branch coverage
   ```

2. **Add tests** to meet the new thresholds.

3. **Repeat** until you reach your target coverage (e.g., 80% line, 70% branch).

## CI Integration

- Coverage is automatically checked in CI
- Reports are uploaded to Codecov
- PR comments show coverage summary
- Coverage badges are updated automatically

## Coverage Goals

### Short Term (Next Release)
- Line Coverage: 20%
- Branch Coverage: 10%

### Medium Term (3-6 months)
- Line Coverage: 50%
- Branch Coverage: 30%

### Long Term (6+ months)
- Line Coverage: 80%
- Branch Coverage: 70%

## Best Practices

1. **Test Core Logic First**: Focus on business logic and data classes
2. **Test Error Cases**: Ensure error handling is covered
3. **Test Edge Cases**: Cover boundary conditions
4. **Keep Tests Simple**: Write readable, maintainable tests
5. **Use Descriptive Names**: Make test names explain what they test

## Troubleshooting

### Coverage Not Increasing
- Check if new code is being executed by tests
- Verify test files are in the correct source set
- Ensure tests are actually running

### Thresholds Too High
- Lower thresholds temporarily
- Add more tests gradually
- Focus on high-impact areas first

### CI Failing on Coverage
- Check if new code is covered
- Verify threshold values are reasonable
- Add tests for uncovered code

## Resources

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Codecov Documentation](https://docs.codecov.io/)
- [Kotlin Testing Guide](https://kotlinlang.org/docs/testing.html) 