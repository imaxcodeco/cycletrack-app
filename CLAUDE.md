# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**CycleTrack** is an Android application for menstrual cycle and health tracking. The app provides cycle predictions, symptom logging, pregnancy/partner modes, Health Connect integration, and subscription-based premium features.

- **Target:** Android 26+ (Android 8.0, Health Connect requirement)
- **Language:** Kotlin 1.9.22
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** Clean Architecture with Domain/Data/Presentation layers
- **State Management:** Hilt DI + ViewModel
- **Databases:** Realm (primary), Room (analytics/events)

## Development Setup

### Prerequisites
- Java 17 (JVM target)
- Android SDK 35
- Gradle 8.7+
- Kotlin Plugin 1.9.22

### Build & Run

```bash
# Clean build
./gradlew clean

# Debug build
./gradlew assembleDebug

# Release build (requires keystore)
./gradlew bundleRelease

# Run all checks (lint, unit tests, UI tests)
./gradlew detekt ktlintCheck testDebugUnitTest connectedDebugAndroidTest

# Individual tasks
./gradlew detekt            # Detekt static analysis
./gradlew ktlintFormat      # Auto-fix code style
./gradlew testDebugUnitTest # Unit tests only
./gradlew connectedDebugAndroidTest  # Instrumented tests
```

### IDE Setup
- **Android Studio:** 2024.1.1+ (recommended Kotlin plugin 1.9.22)
- **.editorconfig:** Already configured for 4-space indentation, 120 char limit
- **Kotlin Code Style:** Official (configured in gradle.properties)

## Architecture & Code Organization

### Directory Structure
```
app/src/
├── main/
│   ├── java/com/seuapp/cycletrack/
│   │   ├── data/              # Data layer (Realm, API, repositories)
│   │   ├── presentation/      # UI layer (Compose screens, ViewModels)
│   │   ├── domain/            # Domain layer (entities, use cases, business logic)
│   │   └── util/              # Utilities, constants, logging
│   ├── res/                   # Resources (strings, colors, drawables)
│   └── proto/                 # Protocol Buffers definitions
├── test/                      # Unit tests (JUnit 4/5, Mockito)
├── androidTest/               # Instrumented tests (Espresso, Compose UI tests)
└── legacy/                    # Legacy code (Realm/Room migrations)
```

### Dependency Injection (Hilt)
- **Module:** Hilt modules configured in respective packages
- **Scope:** Application, Activity, Fragment scopes used appropriately
- **ViewModel:** `@HiltViewModel` for all screens

Example:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val predictionRepository: PredictionRepository
) : ViewModel() {
    // Implementation
}
```

### Database Patterns
- **Realm:** Primary database for user data (cycles, symptoms, predictions, pregnancy state)
- **Room:** Secondary database for analytics events and sessions
- **Migrations:** Legacy migrations stored in `src/legacy/` for reference
- **Never use:** `RealmObject.deleteFromRealm()` on UI thread without background scheduling

## Code Style & Quality

### Detekt (Static Analysis)
- **Config:** `detekt.yml` at root
- **Max Line Length:** 120 characters
- **Naming:** `camelCase` for functions/properties, `PascalCase` for classes
- **Complexity Thresholds:**
  - Cyclomatic complexity: 15
  - Method length: 60 lines
  - Parameter list: 6 (ignoring defaults)
  - Block depth: 4

**Run Detekt:**
```bash
./gradlew detekt
# Report: app/build/reports/detekt/
```

### Ktlint (Code Formatting)
- **Config:** `ktlint.yml` at root
- **Indentation:** 4 spaces, 8-space continuation
- **Auto-fix:** `./gradlew ktlintFormat`
- **Check only:** `./gradlew ktlintCheck`

### Code Conventions
1. **Imports:** No wildcard imports (ktlint enforces)
   - Exception: `kotlinx.coroutines.*` and `androidx.compose.*` allowed in config
2. **Trailing commas:** Allowed and encouraged for multiline structures
3. **New line:** Required at end of file (ktlint enforces)
4. **Blank lines:** No consecutive blank lines
5. **Type inference:** Prefer explicit types in public APIs, inferred in private/local

## Testing Strategy

### Unit Tests
- **Framework:** JUnit 4 + Mockito
- **Location:** `src/test/java/`
- **Scope:** Domain logic, repositories, ViewModels
- **Command:** `./gradlew testDebugUnitTest`

Example:
```kotlin
class CycleRepositoryTest {
    @Mock lateinit var realmInstance: Realm
    @InjectMocks lateinit var repository: CycleRepository
    
    @Before fun setup() = MockitoAnnotations.openMocks(this)
    
    @Test fun testCycleCreation() {
        // Arrange, Act, Assert
    }
}
```

### Instrumented Tests (UI Tests)
- **Framework:** Espresso + Compose UI test APIs
- **Location:** `src/androidTest/java/`
- **Scope:** Navigation flows, screen interactions, edge cases
- **Command:** `./gradlew connectedDebugAndroidTest` (requires running emulator)
- **Coverage:** Onboarding, Home, Calendar, Logging flows fully tested

**Note:** Tests use mock ViewModels (no Firebase/backend required).

### Test Data
- **Fixtures:** Create test data builders in test package
- **Mocks:** Use Mockito for repositories and services
- **Fakes:** Provide fake implementations for small services

## CI/CD Pipeline

**GitHub Actions:** `.github/workflows/ci.yml`

**Pipeline stages:**
1. **Lint & Analysis:** detekt, ktlint validation
2. **Unit Tests:** JUnit 4/5 test suite
3. **UI Tests:** Instrumented tests on emulator (macOS)
4. **Build:** Debug and Release AAB generation
5. **Security:** OWASP dependency check
6. **Deploy:** Internal testing track + release creation

**Triggers:** Pushes to main/develop, PRs

**Key Secrets Required:**
- `KEYSTORE_PASSWORD` — Release keystore password
- `KEY_ALIAS` — Keystore key alias
- `KEY_PASSWORD` — Key password
- `PLAY_CONSOLE_KEY` — JSON key for Play Console API
- `FIREBASE_CONFIG` — Firebase credentials (if using backend)

## Important Dependencies & Patterns

### Core Android
- **Activity/Fragment:** Compose navigation (no XML layouts except themes)
- **Lifecycle:** `androidx.lifecycle:lifecycle-viewmodel-compose` with `collectAsStateWithLifecycle()`
- **WorkManager:** Offline sync and periodic tasks (Realm → Backend sync)

### Network & Serialization
- **Retrofit:** HTTP client with Moshi for JSON serialization
- **OkHttp:** Certificate pinning configured (see `network_security_config.xml`)
- **Serialization:** `kotlinx.serialization` for Kotlin-native types

### Database
- **Realm:** Use Realm transactions for multi-object writes
  ```kotlin
  realm.write {
      copyToRealm(cycleData)
      copyToRealm(predictionData)
  }
  ```
- **Coroutines:** Database operations off main thread (use `withContext(Dispatchers.IO)`)

### Compose & UI
- **Material 3:** All components from `androidx.compose.material3`
- **Icons:** Extended Material icons (`androidx.compose.material:material-icons-extended`)
- **Image Loading:** Coil for remote images
- **Navigation:** `androidx.navigation:navigation-compose` with type-safe routes

### Firebase Integration
- **Authentication:** Firebase Auth for optional backend sync
- **Messaging:** FCM for push notifications
- **Analytics:** Firebase Analytics events (masked via Sentry)
- **Firestore:** Optional backend if future sync needed

### Health Connect
- **API Level:** Requires 26+ (Android 8.0)
- **Permissions:** Declared in `AndroidManifest.xml` with runtime grant flow
- **Data Types:** Steps, heart rate, menstrual flow (custom types)
- **Experimental API:** Opt-in via kotlin compiler args (already configured)

### Error Tracking
- **Sentry:** Production error tracking + performance monitoring
- **Configuration:** `gradle.properties` controls trace/profile sample rates
- **Data Masking:** Automatic via Pino-equivalent (AppLogger)

## Feature Flags

Configured in `gradle.properties`:
```properties
feature.healthConnect.enabled=true
feature.partnerMode.enabled=true
feature.pregnancyMode.enabled=true
feature.perimenopauseMode.enabled=true
feature.anonymousMode.enabled=true
feature.export.enabled=true
feature.biometricAuth.enabled=true
```

Access at runtime:
```kotlin
val isPartnerModeEnabled = BuildConfig.FEATURE_PARTNER_MODE_ENABLED
```

## Common Tasks

### Add a New Screen
1. Create Composable function in `presentation/ui/<feature>/`
2. Create ViewModel in `presentation/viewmodel/`
3. Add data classes in `domain/model/`
4. Create repository in `data/repository/`
5. Add Hilt module if new service needed
6. Write unit tests for ViewModel logic
7. Write UI tests in `androidTest/`

### Add a New Database Entity
1. Create data class in `domain/model/`
2. Create Realm entity in `data/local/realm/entities/`
3. Create DAO in `data/local/room/dao/` if analytics
4. Add migration script in `data/local/migrations/` if needed
5. Update repository with read/write methods
6. Test with integration tests

### Update Design System
1. Edit `docs/design-system.md` for Figma → Compose mapping
2. Update theme colors in `res/values/colors.xml`
3. Update Material3 typography in Compose Theme
4. Test across light/dark modes

### Release Build
1. Run full test suite: `./gradlew build`
2. Generate keystore if first time: `cd scripts && generate-keystore.bat`
3. Build Release AAB: `./gradlew bundleRelease`
4. Sign and upload to Play Console via `app/release/`
5. Create GitHub release with changelog

## Documentation

**Key Docs:**
- `docs/design-system.md` — Component mappings, colors, typography
- `docs/infrastructure-setup.md` — Firebase, Play Console, Sentry setup
- `docs/legal/` — Privacy policy (LGPD/GDPR/CCPA), Terms of Service
- `docs/flo-study/estudo-flo.md` — Estudo detalhado do app concorrente Flo (todas as telas, textos, cores, layout, funcionalidades) + gap analysis vs CycleTrack + plano de implementação faseado (8 fases). Consultar antes de implementar features inspiradas no Flo (onboarding ramificado, registro de sintomas, calendário, modo parceiro, etc.)
- `gradle.properties` — Feature flags and build configuration

## Performance & Best Practices

### Compose
- Use `remember` for derived state (not every recomposition)
- Prefer `collectAsStateWithLifecycle()` over `collectAsState()` for lifecycle-aware collection
- Memoize expensive computations with `remember`
- Avoid state hoisting for all state (only when needed higher)

### Database
- Always batch write operations in `realm.write { }`
- Use `Dispatchers.IO` for database calls
- Lazy load relations (use `@Index` for query columns)
- Monitor Realm file size (export feature may impact)

### Memory
- Gradle configured for 2GB max heap (`gradle.properties`)
- Use `WorkManager` for background tasks (not raw threads)
- Clear image cache regularly in Coil

### Logging
- **Logger:** Use Timber (configured in app)
  ```kotlin
  Timber.d("Debug message: %s", value)
  Timber.e(exception, "Error occurred")
  ```
- **Never:** Raw `println()` or `System.out.println()`
- **Sentry:** Errors auto-captured with context

## Troubleshooting

### Build Issues
- **AGP/Kotlin mismatch:** Ensure AGP 8.6.1 with Kotlin 1.9.22
- **R class errors:** Run `./gradlew clean` then rebuild
- **Compose compiler version:** Must match in `composeOptions {}` (1.5.8)

### Test Failures
- **Espresso timeout:** Increase `Espresso.onIdle()` timeout or mock slower operations
- **Mockito setup:** Ensure `@Mock` annotations before `openMocks()`
- **Instrumented test crash:** Check logcat for Realm/Firebase initialization errors

### Linting Failures
- **Unused imports:** `./gradlew ktlintFormat` auto-fixes
- **Detekt baseline:** Update baseline if rule changes: `./gradlew detektBaseline`
- **Line length:** Refactor method or use `@Suppress("MaxLineLength")`

## Key Resources

- **Android Docs:** https://developer.android.com/compose
- **Realm Docs:** https://www.mongodb.com/docs/realm/sdk/kotlin/
- **Hilt:** https://dagger.dev/hilt/
- **Material 3:** https://m3.material.io/
- **Health Connect:** https://developer.android.com/health-and-fitness/guides/health-connect

---

**Last Updated:** 2026-11-19  
**Maintained by:** Development team
