# 📐 FASE 3 Blueprint — Use Cases & Business Logic

**Target Date:** Próxima sessão  
**Effort Estimate:** 4-6 horas  
**Priority:** High — Unlocks app functionality

---

## 🎯 Fase 3 Objectives

### Primary
1. ✅ Create domain/usecase/ layer with 8-10 use cases
2. ✅ Implement cycle calculations (day of cycle, phase detection)
3. ✅ Implement cycle predictions (next period, ovulation date, fertile window)
4. ✅ Wire use cases into ViewModels
5. ✅ Remove DemoStore completely

### Secondary
6. Health Connect integration (if time permits)
7. Partner sharing logic (if time permits)
8. Subscription validation (if time permits)

---

## 📋 Use Cases to Implement

### High Priority (Must Have)

```kotlin
// Cycle Calculation & Prediction
GetCurrentCyclePhaseUseCase
  Input: userId, cycleId
  Output: CyclePhase (MENSTRUAL, FOLLICULAR, OVULATION, LUTEAL)
  Logic: Calculate based on startDate + currentDate

CalculateCycleDayUseCase
  Input: userId, cycleId
  Output: Int (day 1-28+)
  Logic: ChronoUnit.DAYS.between(cycle.startDate, today)

PredictNextPeriodUseCase
  Input: userId
  Output: LocalDate
  Logic: latestCycle.startDate + latestCycle.cycleLength

CalculateFertileWindowUseCase
  Input: userId
  Output: Pair<LocalDate, LocalDate> (start, end)
  Logic: Ovulation ± 4 days based on cycle average

CalculateOvulationDateUseCase
  Input: userId
  Output: LocalDate
  Logic: startDate + (cycleLength / 2)

GetCycleStatsUseCase
  Input: userId
  Output: CycleStats (avgLength, avgPeriodLength, regularity)
  Logic: Aggregate data from last 6 cycles

GetSymptomInsightsUseCase
  Input: userId
  Output: List<SymptomInsight> (symptom, count, phase)
  Logic: Group symptoms by cycle phase
```

### Medium Priority (Should Have)

```kotlin
LogSymptomUseCase
  Input: userId, date, symptom, severity
  Output: Unit
  Logic: Validate + create Symptom + save via repository

GetSymptomHistoryUseCase
  Input: userId, limit=30
  Output: List<Symptom>
  Logic: Fetch from repository

ExportCycleDataUseCase
  Input: userId, format (CSV/PDF)
  Output: File
  Logic: Aggregate cycles + symptoms + export

ShareWithPartnerUseCase
  Input: userId, partnerId, permissionLevel
  Output: PartnerConnection
  Logic: Create share record + notify partner
```

### Low Priority (Nice to Have)

```kotlin
SyncHealthConnectUseCase
  Input: userId
  Output: HealthDataSync
  Logic: Fetch steps, heart rate, temperature from HC

RefreshPredictionsUseCase
  Input: userId
  Output: List<Prediction>
  Logic: Recalculate all predictions based on latest data

ValidateSubscriptionUseCase
  Input: userId
  Output: SubscriptionStatus
  Logic: Check expiration date + features
```

---

## 🏗️ Implementation Pattern

### 1. Use Case Interface
```kotlin
// domain/usecase/GetCurrentCyclePhaseUseCase.kt
interface GetCurrentCyclePhaseUseCase {
    suspend operator fun invoke(userId: String, cycleId: String): CyclePhase
}
```

### 2. Use Case Implementation
```kotlin
// domain/usecase/impl/GetCurrentCyclePhaseUseCaseImpl.kt
class GetCurrentCyclePhaseUseCaseImpl @Inject constructor(
    private val cycleRepository: CycleRepository
) : GetCurrentCyclePhaseUseCase {
    override suspend fun invoke(userId: String, cycleId: String): CyclePhase {
        val cycle = cycleRepository.getCycle(cycleId) ?: return CyclePhase.UNKNOWN
        val today = LocalDate.now()
        val dayOfCycle = ChronoUnit.DAYS.between(cycle.startDate, today).toInt() + 1
        
        return when {
            dayOfCycle <= cycle.periodLength -> CyclePhase.MENSTRUAL
            dayOfCycle <= cycle.cycleLength / 2 -> CyclePhase.FOLLICULAR
            dayOfCycle <= cycle.cycleLength / 2 + 2 -> CyclePhase.OVULATION
            else -> CyclePhase.LUTEAL
        }
    }
}
```

### 3. Hilt Binding
```kotlin
// data/di/UseCaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetCurrentCyclePhaseUseCase(
        cycleRepository: CycleRepository
    ): GetCurrentCyclePhaseUseCase = GetCurrentCyclePhaseUseCaseImpl(cycleRepository)
    
    // ... 10+ more @Provides methods
}
```

### 4. ViewModel Integration
```kotlin
// presentation/viewmodel/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository,
    private val getCycleDayUseCase: CalculateCycleDayUseCase,
    private val getCyclePhaseUseCase: GetCurrentCyclePhaseUseCase,
    private val predictNextPeriodUseCase: PredictNextPeriodUseCase
) : ViewModel() {
    private val _dayOfCycle = MutableStateFlow(1)
    val dayOfCycle = _dayOfCycle.asStateFlow()
    
    private val _cyclePhase = MutableStateFlow<CyclePhase?>(null)
    val cyclePhase = _cyclePhase.asStateFlow()
    
    private val _nextPeriod = MutableStateFlow<LocalDate?>(null)
    val nextPeriod = _nextPeriod.asStateFlow()
    
    init { loadCycleData() }
    
    private fun loadCycleData() = viewModelScope.launch {
        try {
            val cycle = cycleRepository.getLatestCycle(userId) ?: return@launch
            _dayOfCycle.value = getCycleDayUseCase(userId, cycle.id)
            _cyclePhase.value = getCyclePhaseUseCase(userId, cycle.id)
            _nextPeriod.value = predictNextPeriodUseCase(userId)
        } catch (e: Exception) {
            Timber.e(e, "Error loading cycle data")
        }
    }
}
```

---

## 📂 Directory Structure (Post-Fase 3)

```
app/src/main/java/com/seuapp/cycletrack/
├── domain/
│   ├── model/                           ✅ (from Fase 2)
│   ├── repository/                      ✅ (from Fase 2)
│   └── usecase/                         📝 NEW
│       ├── GetCurrentCyclePhaseUseCase.kt
│       ├── CalculateCycleDayUseCase.kt
│       ├── PredictNextPeriodUseCase.kt
│       ├── CalculateFertileWindowUseCase.kt
│       ├── GetCycleStatsUseCase.kt
│       ├── LogSymptomUseCase.kt
│       ├── GetSymptomInsightsUseCase.kt
│       ├── ExportCycleDataUseCase.kt
│       ├── ShareWithPartnerUseCase.kt
│       └── impl/                        📝 NEW
│           └── <UseCase>Impl.kt (9 files)
├── data/
│   ├── di/                              📝 MODIFY
│   │   └── UseCaseModule.kt             📝 NEW
│   ├── repository/                      ✅ (from Fase 2)
│   └── local/                           ✅ (from Fase 2)
└── presentation/
    ├── viewmodel/                       📝 MODIFY
    │   └── HomeViewModel.kt             (integrate use cases)
    └── ui/                              ✅ (from Fase 2)
```

---

## 🔄 Data Flow (Post-Fase 3)

```
Composable
    ↓ (collectAsState)
ViewModel
    ↓ (invoke use case)
UseCase
    ↓ (call repository)
Repository
    ↓ (query DAO)
RealmDAO
    ↓ (read/write)
Realm Database
```

### Example: HomeScreen Loading
```
1. HomeScreen init → HomeViewModel init
2. ViewModel.loadCycleData()
3. cycleRepository.getLatestCycle(userId)
4. CalculateCycleDayUseCase(userId, cycleId)
5. GetCurrentCyclePhaseUseCase(userId, cycleId)
6. PredictNextPeriodUseCase(userId)
7. All StateFlows update
8. HomeScreen recomposes with fresh data
```

---

## ✅ Testing Strategy (Fase 3+)

### Unit Tests (domain/usecase/)
```kotlin
// domain/usecase/GetCurrentCyclePhaseUseCaseTest.kt
class GetCurrentCyclePhaseUseCaseTest {
    @Mock lateinit var cycleRepository: CycleRepository
    @InjectMocks lateinit var useCase: GetCurrentCyclePhaseUseCaseImpl
    
    @Test
    fun testMenstrualPhase() {
        // given: cycle started today
        // when: invoke use case
        // then: returns MENSTRUAL phase
    }
}
```

### ViewModel Tests
```kotlin
// presentation/viewmodel/HomeViewModelTest.kt
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Test
    fun testLoadCycleData() {
        // given: mock repositories
        // when: viewModel.loadCycleData()
        // then: StateFlows updated with correct values
    }
}
```

### Integration Tests (data/repository/)
```kotlin
// data/repository/CycleRepositoryTest.kt
class CycleRepositoryTest {
    @Test
    fun testCyclePersistence() {
        // given: in-memory Realm
        // when: upsert cycle
        // then: cycle retrieved with same data
    }
}
```

---

## 🚀 Rollout Plan

### Step 1: Create Use Cases (1-2 hours)
- [ ] Create domain/usecase/ interfaces (9 files)
- [ ] Create domain/usecase/impl/ implementations (9 files)
- [ ] Add imports + error handling

### Step 2: Hilt Module (30 min)
- [ ] Create data/di/UseCaseModule.kt
- [ ] Add @Provides for each use case
- [ ] Install into HiltModules.kt

### Step 3: ViewModel Integration (1 hour)
- [ ] Inject use cases into HomeViewModel
- [ ] Wire StateFlows to use case outputs
- [ ] Add error handling + loading states

### Step 4: Compose Integration (30 min)
- [ ] Update HomeScreen to use new StateFlows
- [ ] Update CalendarScreen predictions
- [ ] Update InsightsScreen stats

### Step 5: Testing (1-2 hours, optional for now)
- [ ] Add unit tests for 3-5 use cases
- [ ] Add integration tests for cycle calculations

### Step 6: Polish (30 min)
- [ ] Remove DemoStore references entirely
- [ ] Final compilation + build validation
- [ ] Update documentation

---

## 🔍 Key Decisions

### Why Operator Overload (`invoke`)?
- Allows `useCase(params)` syntax
- Cleaner than `useCase.execute(params)`
- Idiomatic Kotlin pattern

### Why Interface + Implementation?
- Testability (mock interfaces)
- Loose coupling (swap implementations)
- Future: multi-implementation pattern (online vs offline predictions)

### Why Hilt Bindings?
- Type-safe dependency injection
- Compile-time verification
- Singleton scope prevents duplicate instances

### Why Not LiveData?
- StateFlow is more modern
- Better coroutine integration
- Easier to test

---

## 🎓 Key Calculations

### Day of Cycle
```kotlin
val dayOfCycle = ChronoUnit.DAYS.between(cycle.startDate, today).toInt() + 1
// Day 1 = first day of period, Day N = last day before next period
```

### Cycle Phase
```kotlin
when {
    dayOfCycle <= periodLength → MENSTRUAL (days 1-5)
    dayOfCycle <= cycleLength/2 → FOLLICULAR (days 6-14)
    dayOfCycle <= cycleLength/2 + 2 → OVULATION (days 14-16)
    else → LUTEAL (days 17-28)
}
```

### Ovulation & Fertile Window
```kotlin
val ovulationDay = cycleLength / 2
val ovulationDate = cycle.startDate.plusDays(ovulationDay.toLong())
val fertileStart = ovulationDate.minusDays(4)
val fertileEnd = ovulationDate.plusDays(1)
```

### Next Period
```kotlin
val nextPeriodStart = cycle.startDate.plusDays(cycleLength.toLong())
```

### Cycle Stats (from last 6 cycles)
```kotlin
val avgCycleLength = cycles.takeLast(6).map { it.cycleLength }.average()
val avgPeriodLength = cycles.takeLast(6).map { it.periodLength }.average()
val regularity = when (stdDev(cycles.map { it.cycleLength })) {
    in 0f..2f → "Muito Regular"
    in 2f..5f → "Regular"
    in 5f..10f → "Irregular"
    else → "Muito Irregular"
}
```

---

## 🛠️ Common Pitfalls to Avoid

❌ **Don't:** Calculate phases in Composables
✅ **Do:** Use use cases, let ViewModels orchestrate

❌ **Don't:** Hardcode cycle length to 28 days
✅ **Do:** Use actual cycle from latest cycle record

❌ **Don't:** Calculate on every recomposition
✅ **Do:** Cache in ViewModel StateFlow, recalculate only on data change

❌ **Don't:** Mix use case logic with repository methods
✅ **Do:** Repositories access data, use cases apply business logic

---

## 📚 Next Phase Topics

After Fase 3:

### Fase 4: Backend Integration
- Firebase Authentication
- Firestore sync (optional)
- Push notifications for reminders

### Fase 5: Advanced Features
- Health Connect sync
- Partner sharing
- Export (CSV, PDF)
- Pregnancy mode

### Fase 6: Polish & Release
- UI/UX polish
- Performance optimization
- Testing + QA
- Release to Play Store

---

## 📖 Reference

**Previous Phases:**
- CLAUDE.md — Project guidelines
- PHASE2_COMPLETE.md — Architecture + deliverables
- SESSION_SUMMARY.md — What was done this session

**New Files (Fase 3):**
- domain/usecase/ — All 9 use case interfaces
- domain/usecase/impl/ — All 9 implementations
- data/di/UseCaseModule.kt — Hilt bindings
- Tests (optional) — Unit + integration tests

---

**Status:** 🟢 Ready for Fase 3 implementation  
**Next Step:** Create domain/usecase/ interfaces + implementations
