# ⚙️ FASE 4.1 — Checkpoint 1

**Data:** 2026-11-19  
**Status:** 🔨 Em Desenvolvimento  
**Tarefa:** Firebase Authentication — Implementação Inicial

---

## ✅ Concluído

### 1️⃣ Domain Layer (11 arquivos)

**Modelos:**
- ✅ `AuthState.kt` — sealed class para estados de autenticação
- ✅ `User.kt` — data class com uid, email, name, photoUrl

**Repository (interface):**
- ✅ `AuthRepository.kt` — interface com 8 métodos (signUp, signIn, signOut, etc)

**Use Cases (4 interfaces + 4 implementações):**
- ✅ `SignUpUseCase` + `SignUpUseCaseImpl`
- ✅ `SignInUseCase` + `SignInUseCaseImpl`
- ✅ `SignOutUseCase` + `SignOutUseCaseImpl`
- ✅ `GetCurrentUserUseCase` + `GetCurrentUserUseCaseImpl`

Todos com validação, error handling e Timber logging.

### 2️⃣ Data Layer (2 arquivos)

**Firebase Integration:**
- ✅ `FirebaseAuthRepository.kt` — Implementação completa com Firebase Auth SDK
  - signUp com validação de email/senha (mín 6 chars)
  - signIn com email/password
  - signInWithGoogle (stub para futura integração)
  - signOut, getCurrentUser, observeAuthState
  - deleteAccount, updateProfile

**Hilt DI Module:**
- ✅ `FirebaseModule.kt` — @Provides para FirebaseAuth, AuthRepository + 4 use cases
  - Escopo: @Singleton

### 3️⃣ Presentation Layer (4 arquivos)

**ViewModel:**
- ✅ `AuthViewModel.kt` (@HiltViewModel)
  - Injeta 4 use cases
  - 3 StateFlows: authState, signUpError, signInError, isLoading
  - Métodos: signUp(), signIn(), signOut(), checkCurrentUser()
  - Lifecycle-aware com viewModelScope

**Screens:**
- ✅ `AuthScreen.kt` — Orquestrador que alterna entre Sign-in e Sign-up
  - Monitora authState
  - Callback onAuthSuccess() para navegação
  - Limpa erros ao trocar de tela

- ✅ `SignInScreen.kt` — Composable de login
  - 2 inputs: email, password
  - Button "Fazer Login" (desabilitado se campos vazios)
  - TextButton "Crie uma conta"
  - Exibe error message se houver

- ✅ `SignUpScreen.kt` — Composable de cadastro
  - 4 inputs: nome, email, senha, confirmar senha
  - Validação: senha mín 6 chars, confirmação deve bater
  - Button "Criar Conta" (desabilitado se campos vazios)
  - TextButton "Já tem conta?"
  - Exibe error message

### 4️⃣ Firebase Configuration

- ✅ `google-services.json` — Stub criado (pronto para ser substituído)
- ✅ Plugin `com.google.gms.google-services` já configurado em build.gradle.kts
- ✅ Dependências Firebase (auth-ktx, firestore-ktx, messaging-ktx) já no build.gradle.kts

### 5️⃣ Correções em Fase 3

- ✅ Corrigidos imports de `collectAsStateWithLifecycle` em 7 screens:
  - CalendarScreen, HomeScreen, InsightsScreen, LoggingScreen, OnboardingScreen, ProfileScreen
  - Import correto: `androidx.lifecycle.compose.collectAsStateWithLifecycle` (não `androidx.compose.runtime`)

---

## ⚠️ Problemas Identificados (Fase 3 — Pré-requisitos)

A compilação encontrou **~45 erros** em Fase 3 que precisam ser resolvidos antes de completar Fase 4:

### Problema 1: LocalDate Inconsistente
- **Issue:** Mistura de `java.time.LocalDate` e `kotlinx.datetime.LocalDate`
- **Arquivos afetados:**
  - Domain models: Cycle.kt, Prediction.kt, Pregnancy.kt, Symptom.kt, UserProfile.kt
  - Repositories: CycleRepositoryImpl, SymptomRepositoryImpl, etc
  - Use cases: PredictNextPeriodUseCaseImpl, CalculateFertileWindowUseCaseImpl, etc
  - Screens: CalendarScreen, LoggingScreen
- **Solução:** Usar consistentemente `java.time.LocalDate` (padrão Android)
- **Erros:**
  ```
  Type mismatch: inferred type is java.time.LocalDate but kotlinx.datetime.LocalDate was expected
  Unresolved reference: plusDays (método não existe em kotlinx.datetime)
  Unresolved reference: isAfter
  ```

### Problema 2: Realm Type Issues
- **Issue:** `Cycle.kt` tenta usar `.plusDays()` em lazy property do Realm
- **Erro:** `Unresolved reference: plusDays`
- **Linha:** Cycle.kt:41, 44
- **Solução:** Remover lazy property e calcular no domain/use case

### Problema 3: Repository Return Types
- **Issue:** Repositories retornam `List<Unit>` em vez de tipos esperados
- **Arquivos:**
  - PartnerRepositoryImpl.kt:20
  - PredictionRepositoryImpl.kt:27, 32
  - PregnancyRepositoryImpl.kt:21
  - SymptomRepositoryImpl.kt:26
- **Erros:**
  ```
  Type mismatch: inferred type is List<Unit> but List<PartnerConnection> was expected
  Unresolved reference: id (em lambdas de map())
  ```
- **Raiz:** Métodos Realm estão retornando Unit em vez de objetos mapeados

### Problema 4: Use Case Lambda Errors
- **Files:** GetCycleStatsUseCaseImpl, GetSymptomHistoryUseCaseImpl, GetSymptomInsightsUseCaseImpl
- **Erros:** `Unresolved reference: it` em lambdas
- **Causa:** Repository methods não retornam tipos esperados

### Problema 5: ChronoUnit.DAYS Issue
- **File:** CalculateCycleDayUseCaseImpl.kt:15
- **Erro:** `Type mismatch: inferred type is LocalDate but Temporal! was expected`
- **Solução:** `ChronoUnit.DAYS.between()` retorna `long`, não `LocalDate`

---

## 🔧 Estratégia de Resolução

**Opção A (Recomendada):** Corrigir Phase 3 agora
- 1️⃣ Converter todos `kotlinx.datetime.LocalDate` → `java.time.LocalDate`
- 2️⃣ Adicionar imports de `java.time.temporal.ChronoUnit`
- 3️⃣ Revisar Repository implementations (Realm queries)
- 4️⃣ Testar compilação
- Tempo estimado: 1-2 horas

**Opção B:** Prosseguir com Fase 4 (risco)
- Implementar resto da Fase 4 sem Phase 3 compilar
- Integrar navegação com stub
- Testar Fase 4 isoladamente
- Volta para Phase 3 depois

---

## 📊 Próximas Ações

1. **[BLOQUEANTE]** Corrigir Phase 3:
   - Converter LocalDate imports
   - Verificar Repository return types
   - Validar Use Case lambda types

2. **[Após Phase 3]** Continuaremos Fase 4.1:
   - Integrar AuthScreen na navegação
   - Adicionar AppNavigation com AuthGraph
   - Testar Sign-up/Sign-in flow
   - Build 14 (Auth completa)

3. **[Fase 4.2]** Backend Sync:
   - Firestore integration
   - Offline sync com WorkManager
   - Build 15

---

## 📈 Métricas

| Métrica | Valor |
|---------|-------|
| **Arquivos Criados (Fase 4.1)** | 15 |
| **Arquivos Corrigidos (Fase 3)** | 7 |
| **Erros de Compilação** | ~45 (Phase 3) |
| **Bloqueadores de Build** | 5 (LocalDate, Realm types, ChronoUnit) |
| **Pronto para Build** | ❌ Não (Phase 3 precisa ser corrigido) |

---

## 🎯 Decisão do Time

**Recomendação:** Corrigir Phase 3 AGORA antes de continuar Fase 4.

**Justificativa:**
- Fase 4 depende de domain models corretos (Cycle, User, etc)
- Erros de compilação bloqueiam todos os builds
- Phase 3 é rápido de corrigir (localização centralizada)
- Melhor ter fundação sólida

**Próximo Passo:** Aguardando autorização para corrigir Phase 3 (1-2 horas) → Build validation → Continuar Fase 4

---

**Status:** 🟡 **BLOQUEADO POR PHASE 3**  
**Próxima Revisão:** Após correção de LocalDate + Repositories  
**Data Estimada de Conclusão Fase 4.1:** 2026-11-19 + 1-2h (Phase 3) + 2-3h (Fase 4.1) = **~2h**
