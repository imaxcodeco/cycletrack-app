# Contributing to CycleTrack

Thank you for your interest in contributing to CycleTrack!

## Getting Started

### 1. Fork & Clone
```bash
git clone https://github.com/YOUR_USERNAME/cycletrack-app.git
cd cycletrack-app
```

### 2. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Set Up Development Environment
```bash
./gradlew build
./gradlew ktlintFormat
./gradlew detekt
```

## Development Workflow

### Code Style
- **Language:** Kotlin 1.9.22
- **Indentation:** 4 spaces
- **Line length:** 120 characters max
- **Format:** `./gradlew ktlintFormat`

### Architecture
- **Domain:** Use cases, models, repository interfaces
- **Data:** Repository implementations, DAOs, mappers
- **Presentation:** ViewModels, Composables

### Commit Messages
```
feat(area): description      # New feature
fix(area): description       # Bug fix
refactor(area): description  # Code refactoring
test(area): description      # Tests
docs(area): description      # Documentation
```

## Pull Request Process

### Before Creating PR
1. ✅ Code formatted: `./gradlew ktlintFormat`
2. ✅ Lint passes: `./gradlew detekt`
3. ✅ Tests pass: `./gradlew testDebugUnitTest`
4. ✅ Full build succeeds: `./gradlew build`

### Creating PR
1. Push to your fork: `git push origin feature/your-feature`
2. Open PR on GitHub
3. Link related issues: `Closes #123`

## Testing

Write tests for:
- Use cases (domain logic)
- Repositories (data access)
- ViewModels (state management)

```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest
```

## Questions?

- **GitHub Issues:** Technical questions, bug reports
- **GitHub Discussions:** General questions, ideas
