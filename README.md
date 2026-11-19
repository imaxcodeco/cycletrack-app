# 🩸 CycleTrack — Menstrual Cycle & Health Tracking App

**Android application for cycle prediction, symptom logging, and health tracking with partner sharing and subscription features.**

## 📱 Features

### Core Features (MVP)
- 🔄 **Menstrual Cycle Tracking** — Log period start/end dates
- 📊 **Cycle Predictions** — Automatic prediction of next period, ovulation, fertile window
- 🩹 **Symptom Logging** — Track symptoms with severity levels
- 📈 **Cycle Analytics** — View statistics, regularity, and health insights
- 🎨 **Phase Indicators** — Color-coded cycle phases

### Premium Features
- 👥 **Partner Mode** — Share cycle data with partner
- 🤰 **Pregnancy Mode** — Track pregnancy milestones
- 📤 **Data Export** — Export as CSV or PDF
- 🏥 **Health Connect Integration** — Sync health data

## 🏗️ Architecture

**Clean Architecture + MVVM**

- **Presentation:** Composables + ViewModels
- **Domain:** Use Cases + Models (11 repositories, 9 use cases)
- **Data:** Repositories + DAOs + Mappers
- **Infrastructure:** Realm + Hilt DI

## Tech Stack
- **Language:** Kotlin 1.9.22
- **UI:** Jetpack Compose + Material 3
- **DI:** Hilt
- **Database:** Realm + Room
- **State:** StateFlow + ViewModel
- **Build:** Gradle 8.7+

## 🚀 Getting Started

### Prerequisites
- Java 17
- Android SDK 35
- Gradle 8.7+
- Android Studio 2024.1.1+

### Build & Run
```bash
git clone https://github.com/imaxcodeco/cycletrack-app.git
cd cycletrack-app
./gradlew clean
./gradlew assembleDebug
./gradlew build
./gradlew ktlintFormat
./gradlew detekt
```

## 🎯 Development Phases

✅ **Phase 1-3:** Foundation + Architecture + Business Logic (Complete)
⏳ **Phase 4:** Backend Integration (Firebase, Firestore, Partner Sharing)
⏳ **Phase 5:** Advanced Features (Health Connect, Pregnancy Mode, Export)
⏳ **Phase 6:** Polish & Release

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

1. Create feature branch: `git checkout -b feature/my-feature`
2. Make changes
3. Run checks: `./gradlew build`
4. Format: `./gradlew ktlintFormat`
5. Commit: `feat(area): description`
6. Create pull request

## 📄 License

MIT License

---

**Made with ❤️ for menstrual health tracking**

[GitHub](https://github.com/imaxcodeco/cycletrack-app)
