# Status do Projeto CycleTrack - Análise Completa

Data: 2026-11-19  
Status: **Em Desenvolvimento (Prototype → Production)**

---

## 📊 Resumo Executivo

O projeto está em estado **semi-completo**:
- ✅ **UI/UX:** Telas Compose, design system, testes UI implementados
- ✅ **Arquitetura:** Clean Architecture definida (legacy/)
- ✅ **Testes:** UI tests para fluxos críticos
- ✅ **CI/CD:** GitHub Actions pipeline configurado
- ❌ **Data Layer:** Hilt/Realm desconectados do código principal
- ❌ **Firebase:** google-services.json é placeholder
- ❌ **Integração:** ViewModels e repositórios não estão wired
- ❌ **Docs:** README.md falta

---

## 🎯 Análise Detalhada

### 1. ESTRUTURA DO PROJETO

**Estado:** ⚠️ Dupla camada de código (conflitante)

```
app/src/main/
├── java/com/seuapp/cycletrack/
│   ├── CycleTrackApplication.kt         (vazio, sem Hilt)
│   ├── data/demo/
│   │   └── DemoStore.kt                 (dados em memória, não persistidos)
│   ├── data/model/
│   │   └── Models.kt                    (enums e data classes - OK)
│   ├── presentation/
│   │   ├── MainActivity.kt              (OK, Compose setup)
│   │   ├── navigation/CycleTrackApp.kt  (navegação OK)
│   │   └── ui/
│   │       ├── calendar/CalendarScreen.kt
│   │       ├── home/HomeScreen.kt
│   │       ├── logging/LoggingScreen.kt
│   │       ├── onboarding/OnboardingScreen.kt
│   │       ├── insights/InsightsScreen.kt
│   │       ├── profile/ProfileScreen.kt
│   │       └── theme/Theme.kt           (Material3 OK)
│   └── util/                            (vazio)
│
app/src/legacy/kotlin/                   (código completo, não integrado)
├── com/seuapp/cycletrack/
│   ├── di/
│   │   ├── HiltModules.kt               (Hilt providers - Realm, Room, Prefs)
│   │   └── AppModule.kt
│   ├── domain/
│   │   ├── model/
│   │   ├── repository/
│   │   └── usecase/
│   ├── data/
│   │   ├── local/
│   │   │   ├── realm/entities/          (Realm objects - 10+ entidades)
│   │   │   ├── room/entities/           (Room analytics)
│   │   │   ├── datastore/               (Preferences)
│   │   │   └── migrations/
│   │   ├── remote/
│   │   │   ├── api/CycleTrackApi.kt
│   │   │   ├── dto/
│   │   │   └── mappers/
│   │   └── repository/                  (10+ repositórios)
│   ├── presentation/
│   │   ├── viewmodel/                   (11+ ViewModels)
│   │   ├── ui/                          (telas completas)
│   │   └── state/
│   ├── healthconnect/
│   ├── billing/
│   ├── work/
│   └── util/
```

**Problema:** Duas arquiteturas paralelas
- `main/` → Telas Compose + dados fake (DemoStore)
- `legacy/` → Código completo (Hilt, Realm, repositórios, ViewModels)

---

### 2. FALTA DE INTEGRAÇÃO HILT

**Status:** ❌ Não conectado

**Arquivo:** `app/src/main/java/.../CycleTrackApplication.kt`
```kotlin
// ATUAL (vazio)
class CycleTrackApplication : Application() {
    override fun onCreate() { super.onCreate() }
}

// ESPERADO
@HiltAndroidApp
class CycleTrackApplication : Application()
```

**Dependências que faltam:**
- `@HiltAndroidApp` annotation na Application
- Hilt não está inicializando
- Nenhum módulo Hilt está sendo carregado
- ViewModels não possuem `@HiltViewModel`
- Repositórios não estão sendo injetados

**Impacto:**
- Telas usam `DemoStore` hardcoded (dados em memória)
- Sem persistência (Realm)
- Sem sincronização com backend
- Sem cache (DataStore)

---

### 3. BANCO DE DADOS

**Status:** ❌ Realm não está inicializado

**O que existe em legacy:**
```
app/src/legacy/kotlin/com/seuapp/cycletrack/data/local/realm/
├── entities/
│   ├── CycleRealm.kt
│   ├── SymptomRealm.kt
│   ├── UserProfileRealm.kt
│   ├── PredictionRealm.kt
│   ├── PregnancyRealm.kt
│   ├── PartnerRealm.kt
│   ├── SubscriptionRealm.kt
│   ├── ContentRealm.kt
│   ├── HealthDataPointRealm.kt
│   ├── NotificationRealm.kt
│   └── ExportRealm.kt
├── migrations/
└── RealmRepository.kt
```

**O que falta:**
1. Inicialização do Realm no `HiltModules.kt`
2. Conexão entre `DemoStore` e `RealmRepository`
3. ViewModels precisam injetar repositórios Realm
4. Migrações não estão sendo aplicadas
5. Health Connect não está persistindo dados em Realm

**Código atual (DemoStore - dados em memória):**
```kotlin
// ATUAL - dados perdem quando app fecha
class DemoStore {
    private val cyclesState = MutableStateFlow(seedCycles())
    val cycles: StateFlow<List<Cycle>> = cyclesState.asStateFlow()
}

// ESPERADO - persistência com Realm
class CycleRepository @Inject constructor(
    private val realm: Realm
) {
    fun getCycles(): Flow<List<Cycle>> = realm.query<CycleRealm>()
        .asFlow()
        .map { it.list().map { entity -> entity.toDomain() } }
}
```

---

### 4. FIREBASE

**Status:** ❌ Placeholder / Não configurado

**Arquivo:** `app/src/main/google-services.json`
```json
{
  "project_number": "000000000000",    // ❌ Placeholder
  "project_id": "cycletrack-placeholder",
  "api_key": "AIzaSyPLACEHOLDER0000000000000000000000000"
}
```

**O que falta:**
1. Criar Firebase Project em console.firebase.google.com
2. Baixar `google-services.json` real
3. Configurar Authentication (opcional, para future sync)
4. Configurar Firestore (opcional, para future backend)
5. Configurar Cloud Messaging (para notificações)
6. Configurar Crashlytics (error tracking)

**Configurações necessárias:**
```gradle
// App já tem dependências Firebase adicionadas em build.gradle.kts:
implementation("com.google.firebase:firebase-analytics-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
// Mas google-services plugin e google-services.json precisam ser de verdade
```

---

### 5. SENTRY (Error Tracking)

**Status:** ❌ Não configurado

**O que existe:**
- Dependência no build.gradle.kts: ✅ `io.sentry:sentry-android:7.11.0`
- Feature flag no gradle.properties: ✅ `sentry.enabled=true`

**O que falta:**
1. Inicialização em `CycleTrackApplication.kt`
2. Sentry.init() com DSN real
3. Configuração de sample rates (já em gradle.properties)
4. Mapas de symbols para release builds
5. GitHub Actions integration para upload de symbols

**Código faltante:**
```kotlin
// CycleTrackApplication.kt
import io.sentry.android.core.SentryAndroid

@HiltAndroidApp
class CycleTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Necessário DSN do Sentry
        SentryAndroid.init(this) { options ->
            options.dsn = BuildConfig.SENTRY_DSN
            options.tracesSampleRate = 0.1
            options.profilesSampleRate = 0.1
            options.isDebug = BuildConfig.DEBUG
        }
    }
}
```

**Passos necessários:**
1. Criar projeto em sentry.io
2. Obter DSN
3. Adicionar DSN ao `local.properties` ou `BuildConfig`

---

### 6. HEALTH CONNECT

**Status:** ⚠️ Documentado mas não implementado

**O que existe em legacy:**
```
app/src/legacy/kotlin/com/seuapp/cycletrack/healthconnect/
├── models/
├── repository/
└── managers/
```

**O que falta em main:**
1. Inicializar Health Connect client
2. Implementar lógica de sync no MainActivity
3. Implementar WorkManager para background sync
4. Mapear permissões Health Connect → Realm
5. Implementar UI para permissões Health Connect

**Código faltante:**
```kotlin
// HealthConnectViewModel precisa:
@HiltViewModel
class HealthConnectViewModel @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository
) : ViewModel() {
    // Implementação
}
```

---

### 7. VIEW MODELS

**Status:** ❌ Existem em legacy mas não em main

**O que falta conectar:**
```
Telas em main/                      ViewModels em legacy/
├── HomeScreen                      → HomeViewModel
├── CalendarScreen                  → CalendarViewModel
├── LoggingScreen                   → LoggingViewModel
├── OnboardingScreen                → OnboardingViewModel
├── InsightsScreen                  → InsightsViewModel
├── ProfileScreen                   → ProfileViewModel
└── ...                             └── 11+ ViewModels
```

**Padrão esperado:**
```kotlin
// ATUAL (DemoStore hardcoded)
@Composable
fun HomeScreen() {
    val cycles by remember { DemoStore().cycles.collectAsState() }
}

// ESPERADO (ViewModel com Hilt)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val cycles by viewModel.cycles.collectAsStateWithLifecycle()
}
```

---

### 8. REPOSITÓRIOS

**Status:** ❌ Implementados em legacy mas não usados em main

**11+ Repositórios em legacy:**
- CycleRepository
- SymptomRepository
- UserProfileRepository
- PredictionRepository
- PregnancyRepository
- PartnerRepository
- SubscriptionRepository
- ContentRepository
- HealthDataPointRepository
- NotificationRepository
- AnalyticsRepository

**Padrão faltante:**
```kotlin
interface CycleRepository {
    fun getCycles(): Flow<List<Cycle>>
    fun addCycle(cycle: Cycle): Flow<Boolean>
    suspend fun updateCycle(cycle: Cycle)
}

class CycleRepositoryImpl @Inject constructor(
    private val realmRepository: RealmRepository
) : CycleRepository {
    // Implementation
}
```

---

### 9. DOCUMENTAÇÃO

**Status:** ⚠️ Incompleta

**Arquivos existentes:** ✅
- `docs/design-system.md` (644 linhas)
- `docs/infrastructure-setup.md` (574 linhas)
- `docs/legal/privacy-policy.md` (209 linhas)
- `docs/legal/terms-of-service.md` (297 linhas)
- `CLAUDE.md` (recém criado)

**Arquivos faltando:** ❌
- `README.md` - documentação principal do projeto
- `CONTRIBUTING.md` - guia para contribuidores
- `API.md` - documentação de API (se houver backend)
- `TROUBLESHOOTING.md` - FAQs e troubleshooting

---

### 10. TESTES

**Status:** ⚠️ UI tests OK, mas sem testes unitários de lógica

**O que existe:**
```
app/src/androidTest/
├── onboarding/OnboardingFlowTest.kt    (246 linhas)
├── home/HomeScreenTest.kt               (372 linhas)
├── calendar/CalendarScreenTest.kt       (458 linhas)
└── logging/LoggingScreenTest.kt         (506 linhas)

app/src/test/
├── domain/algorithm/CyclePredictionEngineTest.kt
└── domain/usecase/cycle/CycleUseCasesTest.kt
```

**O que falta:**
1. Testes unitários para Repositórios
2. Testes para ViewModels (mockando Repositórios)
3. Testes de integração com Realm
4. Testes de Health Connect integration
5. Testes de serialização (DTO → Domain)
6. Test fixtures/builders para DemoStore

---

### 11. FEATURES NÃO IMPLEMENTADAS

**Status:** ❌ Referenciadas mas sem lógica real

| Feature | UI | Logic | BD | Tests |
|---------|----|----|-------|-------|
| Cycle Tracking | ✅ | ❌ | ❌ | ⚠️ |
| Symptom Logging | ✅ | ❌ | ❌ | ⚠️ |
| Cycle Prediction | ✅ | ⚠️ (legacy) | ❌ | ✅ |
| Calendar View | ✅ | ❌ | ❌ | ⚠️ |
| Health Connect | ❌ | ⚠️ (legacy) | ❌ | ❌ |
| Partner Mode | ✅ | ❌ | ❌ | ❌ |
| Pregnancy Mode | ✅ | ❌ | ❌ | ❌ |
| Subscriptions | ⚠️ | ⚠️ (legacy) | ❌ | ❌ |
| Data Export (CSV/PDF) | ⚠️ | ⚠️ (legacy) | ❌ | ❌ |
| Biometric Auth | ❌ | ❌ | ❌ | ❌ |

---

### 12. CI/CD PIPELINE

**Status:** ⚠️ Configurado mas sem secrets

**O que existe:**
- `.github/workflows/ci.yml` completo
- Lint (detekt/ktlint) ✅
- Tests (unit + UI) ✅
- Build (debug/release) ✅
- Deploy (Play Console) ⚠️

**O que falta:**
1. GitHub Secrets configurados:
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
   - `PLAY_CONSOLE_KEY`
2. Keystore gerado (script existe: `scripts/generate-keystore.bat`)
3. GitHub repository criado
4. Branchs strategy definida (main/develop/release)

---

## 🛠️ Plano de Ação (Priorizado)

### Fase 1: Foundation (Semana 1)
**Objetivo:** Conectar dados + Hilt

- [ ] Adicionar `@HiltAndroidApp` em CycleTrackApplication
- [ ] Carregue código de legacy/ para main/
- [ ] Inicializar Realm em HiltModules
- [ ] Converter ViewModels para usar repositórios reais
- [ ] Remover DemoStore
- [ ] Testar com dados persistidos em Realm

### Fase 2: Firebase + Sentry (Semana 2)
**Objetivo:** Integração de serviços

- [ ] Criar Firebase Project
- [ ] Baixar google-services.json real
- [ ] Adicionar Sentry.init() em Application
- [ ] Configurar Crashlytics
- [ ] Adicionar error boundaries em Composables

### Fase 3: Health Connect (Semana 3)
**Objetivo:** Integração de saúde

- [ ] Implementar HealthConnectManager
- [ ] Adicionar fluxo de permissões
- [ ] Sincronizar dados Health Connect → Realm
- [ ] Configurar WorkManager para sync background

### Fase 4: Release Build (Semana 4)
**Objetivo:** Preparar para Play Console

- [ ] Gerar keystore release
- [ ] Configurar GitHub Secrets
- [ ] Build AAB release
- [ ] Testar signed APK
- [ ] Upload para Play Console internal testing

---

## 📋 Checklist de Deploy

### Pré-requisitos
- [ ] Keystore gerado e salvo com segurança
- [ ] google-services.json real baixado
- [ ] Firebase Project criado
- [ ] Play Console account ($25)
- [ ] Domínio cycletrack.app registrado
- [ ] Sentry Project criado

### Configurações
- [ ] GitHub Secrets adicionados
- [ ] BuildConfig.SENTRY_DSN definido
- [ ] CI/CD pipeline testado
- [ ] Release branch criado

### App
- [ ] Todos os testes passando
- [ ] Detekt/Ktlint sem erros
- [ ] Dados persistidos em Realm
- [ ] Health Connect funcionando
- [ ] Testes UI em emulator real

### Publicação
- [ ] Privacy Policy publicada
- [ ] Terms of Service publicada
- [ ] Screenshots para Play Console
- [ ] Descrição do app em PT-BR
- [ ] Release notes criadas

---

## 🔗 Recursos

**Documentação do projeto:**
- `CLAUDE.md` — Setup de desenvolvimento
- `docs/design-system.md` — UI components
- `docs/infrastructure-setup.md` — Infra completa
- `docs/legal/` — Políticas legais

**Referências externas:**
- [Hilt DI Guide](https://dagger.dev/hilt/)
- [Realm Kotlin SDK](https://www.mongodb.com/docs/realm/sdk/kotlin/)
- [Health Connect API](https://developer.android.com/health-and-fitness/guides/health-connect)
- [Firebase Setup](https://firebase.google.com/docs/android/setup)
- [Sentry Android](https://docs.sentry.io/platforms/android/)

---

**Última atualização:** 2026-11-19  
**Status geral:** 45% completo (UI + arquitetura OK, dados layer incompleto)
