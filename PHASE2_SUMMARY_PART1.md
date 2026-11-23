# Fase 2: ViewModels + Repositórios - Primeira Parte ✅

**Data:** 2026-11-19  
**Status:** 🔨 Compilando (aguardando `compileDebugKotlin`)

---

## 🎯 Objetivo da Fase 2

Mover a lógica de negócio de `legacy/` para `main/`, implementando:
1. Modelos de Domínio
2. Repositórios que usam DAOs
3. ViewModels com `@HiltViewModel`
4. Atualizar telas Compose para usar ViewModels reais

---

## ✅ Trabalho Realizado (Primeira Parte)

### 1. Modelos de Domínio (5 arquivos)

#### `Cycle.kt` — Ciclo Menstrual Completo
```kotlin
data class Cycle(
    val id: String,
    val userId: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val cycleLength: Int,
    val periodLength: Int,
    // ... 8 campos adicionais
)
```
- Factory method `createManual()` para criar ciclos
- Constants para sources (manual, predicted, server, health_connect)

#### `Symptom.kt` — Sintoma com Data
```kotlin
data class Symptom(
    val id: String,
    val userId: String,
    val date: LocalDate,
    val name: String,
    val severity: Int = 2,
    // ...
)
```

#### `Prediction.kt` — Previsões com Enum
```kotlin
enum class PredictionType(val key: String) {
    PERIOD_START, OVULATION, PMS, ...
}

data class Prediction(
    val type: String,
    val predictedDate: LocalDate,
    val confidence: Float = 0.0f,
    // ...
)
```

#### `UserProfile.kt` — Perfil com Builder
```kotlin
data class UserProfile(
    val id: String,
    val email: String,
    val displayName: String = "",
    // ... 6 campos adicionais
) {
    class Builder { }  // Para construção incremental
}
```

#### `Pregnancy.kt` — Gravidez + Partner
```kotlin
data class Pregnancy(
    val id: String,
    val dueDate: LocalDate,
    val currentWeek: Int = 0,
    // ...
)

data class PartnerConnection(
    val partnerId: String? = null,
    val shareLevel: String = "none",
    // ...
)
```

### 2. Repositório Pattern (CycleRepository)

#### Interface `CycleRepository.kt`
```kotlin
interface CycleRepository {
    fun getCycles(userId: String): Flow<List<Cycle>>
    suspend fun getCycle(cycleId: String): Cycle?
    suspend fun getCyclesInRange(...): List<Cycle>
    suspend fun upsertCycle(cycle: Cycle)
    // ... 10+ métodos
}
```

**Padrão:** Contrato sem expor implementação (Clean Architecture).

#### Implementação `CycleRepositoryImpl.kt`
```kotlin
class CycleRepositoryImpl @Inject constructor(
    private val cycleDao: CycleDao
) : CycleRepository {
    // Usar CycleDao do Realm
    // Mapear Cycle ↔ CycleRealm
}
```

**Mappers implementados:**
- `Cycle.toRealm(): CycleRealm` — Domain → Realm
- `CycleRealm.toDomain(): Cycle` — Realm → Domain
- Conversão de datas (LocalDate ↔ Long epoch days)

#### Hilt Registration
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideCycleRepository(cycleDao: CycleDao): CycleRepository {
        return CycleRepositoryImpl(cycleDao)
    }
}
```

**Resultado:** `CycleRepository` injetável em qualquer ViewModel.

### 3. ViewModel com Hilt

#### `HomeViewModel.kt`
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cycleRepository: CycleRepository
) : ViewModel() {
    
    private val _currentCycle = MutableStateFlow<Cycle?>(null)
    val currentCycle = _currentCycle.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    init {
        loadCurrentCycle()
    }
    
    fun refreshCurrentCycle() { /* ... */ }
    fun addCycle(cycle: Cycle) { /* ... */ }
}
```

**Features:**
- Injeção de `CycleRepository` via Hilt
- State management com `StateFlow`
- Loading e error handling
- Coroutines com `viewModelScope`
- Timber logging integrado

---

## 📂 Arquivos Criados (10 novos)

```
app/src/main/java/com/seuapp/cycletrack/
├── domain/
│   ├── model/
│   │   ├── Cycle.kt                  (58 linhas)
│   │   ├── Symptom.kt                (11 linhas)
│   │   ├── Prediction.kt             (25 linhas)
│   │   ├── UserProfile.kt            (38 linhas)
│   │   └── Pregnancy.kt              (25 linhas)
│   └── repository/
│       └── CycleRepository.kt        (21 linhas)
├── data/
│   └── repository/
│       └── CycleRepositoryImpl.kt     (119 linhas)
└── presentation/
    └── viewmodel/
        └── HomeViewModel.kt          (63 linhas)
```

**Total: 10 arquivos | ~360 linhas de código**

---

## 🔧 Configurações Atualizadas

**HiltModules.kt** — Adicionado `RepositoryModule`
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideCycleRepository(cycleDao: CycleDao): CycleRepository {
        return CycleRepositoryImpl(cycleDao)
    }
}
```

---

## 📊 Comparação: DemoStore vs ViewModel

| Aspecto | DemoStore | ViewModel |
|---------|-----------|-----------|
| **Dados** | Fake, em memória | Reais, em Realm |
| **Persistência** | ❌ Não | ✅ Sim |
| **Reuso** | ❌ Não (amarrado ao Compose) | ✅ Sim (desacoplado) |
| **Testes** | ❌ Difícil | ✅ Fácil (mock repo) |
| **Injeção** | ❌ Manual | ✅ Hilt automático |
| **State Management** | ✅ Flow simples | ✅ Flow + ViewModel lifecycle |

---

## 🧪 Status de Compilação

**Estado:** 🔨 Compilando `compileDebugKotlin`

Esperado: ✅ BUILD SUCCESS

---

## 📋 Próximos Passos (Depois da Compilação)

### Imediato
1. Validar compilação bem-sucedida
2. Corrigir erros (se houver)
3. Atualizar `HomeScreen.kt` para usar `HomeViewModel`

### Fase 2 Continua
1. Criar 10+ repositórios adicionais (baseado em CycleRepository)
2. Mover 10+ ViewModels (padrão igual a HomeViewModel)
3. Atualizar telas Compose para usar ViewModels

### Remover DemoStore
1. Remover AppContainer e DemoStore
2. Remover importações de `data.demo.*`
3. Atualizar referências em teste e builds

---

## 🎓 Padrão Estabelecido

### Para cada entidade (Symptom, Prediction, etc):

1. **Modelo Domain** (`domain/model/X.kt`)
   ```kotlin
   @Serializable
   data class X(...)
   ```

2. **Interface Repository** (`domain/repository/XRepository.kt`)
   ```kotlin
   interface XRepository {
       suspend fun getX(...): X?
       suspend fun upsertX(x: X)
       // ...
   }
   ```

3. **Implementação** (`data/repository/XRepositoryImpl.kt`)
   ```kotlin
   class XRepositoryImpl @Inject constructor(
       private val xDao: XDao
   ) : XRepository {
       // Mapear X ↔ XRealm
   }
   ```

4. **Hilt Provider** (em `HiltModules.kt`)
   ```kotlin
   @Provides @Singleton
   fun provideXRepository(xDao: XDao): XRepository {
       return XRepositoryImpl(xDao)
   }
   ```

5. **ViewModel** (`presentation/viewmodel/XViewModel.kt`)
   ```kotlin
   @HiltViewModel
   class XViewModel @Inject constructor(
       private val xRepository: XRepository
   ) : ViewModel() { }
   ```

---

## 🎯 Métricas Fase 2 Parte 1

| Métrica | Valor |
|---------|-------|
| Arquivos criados | 10 |
| Arquivos atualizados | 1 |
| Modelos implementados | 5 |
| Repositórios implementados | 1 |
| ViewModels implementados | 1 |
| Total linhas código | ~360 |
| Status compilação | 🔨 Aguardando |

---

## ✨ Resultado Esperado

Após compilação com sucesso:
- ✅ Hilt injetando CycleRepository
- ✅ HomeViewModel carregando dados de Realm
- ✅ Padrão estabelecido para 10+ repositórios
- ✅ Pronto para mover mais ViewModels

---

**Próxima fase:** Atualizar `HomeScreen.kt` + implementar repositórios adicionais
