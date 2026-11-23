# 🚀 FASE 4 — Backend Integration Blueprint

**Data:** 2026-11-19  
**Status:** 📋 Planejamento  
**Objetivo:** Integrar Firebase Authentication, Firestore, Partner Sharing e Push Notifications

---

## 📌 Visão Geral

A Fase 4 transforma CycleTrack de um aplicativo local-only para um app com sincronização em nuvem, compartilhamento de dados e comunicação em tempo real via push notifications.

### Componentes da Fase 4

| Componente | Descrição | Prioridade |
|------------|-----------|-----------|
| **Firebase Auth** | Autenticação com email/senha, Google Sign-In | 🔴 Alta |
| **Firestore Sync** | Sincronização bidirecional de ciclos com Firestore | 🔴 Alta |
| **Partner Sharing** | Compartilhar ciclo com parceiro | 🟠 Média |
| **Push Notifications** | FCM para lembretes e eventos importantes | 🟠 Média |
| **Data Encryption** | Criptografia de dados sensíveis em trânsito | 🟠 Média |

---

## 🏗️ Arquitetura — Fase 4

```
Presentation Layer
├── AuthScreen (Login + Sign-up)
├── ProfileScreen (gerenciar conta)
├── PartnerScreen (adicionar/gerenciar parceiro)
└── NotificationsScreen (preferências)

Domain Layer
├── AuthRepository (interface)
├── SyncRepository (interface)
├── PartnerRepository (interface)
└── Use Cases
    ├── SignUpUseCase
    ├── SignInUseCase
    ├── SyncCycleDataUseCase
    ├── AddPartnerUseCase
    ├── RemovePartnerUseCase
    └── SaveNotificationPrefsUseCase

Data Layer
├── Firebase Auth (email, Google Sign-In)
├── Firestore (users, cycles, partners)
├── WorkManager (sync background jobs)
├── FCM (push notifications)
└── Repositories (implementações)

Infrastructure
├── FirebaseModule (Hilt)
├── AuthModule (Hilt)
├── SyncWorker (background sync)
└── FCMService (push handler)
```

---

## 📋 Tarefas Detalhadas

### Fase 4.1: Firebase Authentication (2-3 horas)

#### 4.1.1 Configuração Firebase Console
- [ ] Criar Firebase Project em console.firebase.google.com
- [ ] Ativar Authentication (Email/Password + Google)
- [ ] Configurar OAuth consent screen
- [ ] Habilitar Firestore Database
- [ ] Habilitar Cloud Messaging (FCM)
- [ ] Baixar `google-services.json` → `app/`
- [ ] Adicionar SHA-1 (debug keystore) no Firebase Console

**SHA-1 Debug Keystore:**
```bash
# Windows
keytool -list -v -keystore C:\Users\%USERNAME%\.android\debug.keystore -storepass android -keypass android
```

#### 4.1.2 Integrar Firebase Auth na App
- [ ] Adicionar `apply plugin: 'com.google.gms.google-services'` em app/build.gradle.kts
- [ ] Adicionar dependências Firebase (já estão em build.gradle.kts)
- [ ] Criar `AuthRepository` (interface em domain/)
- [ ] Implementar `FirebaseAuthRepository` (data/remote/)
- [ ] Criar `FirebaseAuthModule` (data/di/)
- [ ] Registrar módulo em `AppModule.kt`

**Métodos a implementar:**
```kotlin
interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun observeAuthState(): Flow<AuthState>
}
```

#### 4.1.3 Criar AuthScreen
- [ ] Composable `AuthScreen` (presentation/ui/auth/)
- [ ] ViewModel `AuthViewModel` (presentation/viewmodel/)
- [ ] Use Cases: `SignUpUseCase`, `SignInUseCase`, `SignOutUseCase`
- [ ] Integrar Google Sign-In com PlayServices
- [ ] Validação de email/senha
- [ ] Tratamento de erros (email já existe, senha fraca, etc)
- [ ] Navegação post-login → HomeScreen

#### 4.1.4 Atualizar App Navigation
- [ ] Adicionar `AuthGraph` (antes de HomeGraph)
- [ ] Verificar auth state ao iniciar app
- [ ] Redirecionar não autenticado → AuthScreen
- [ ] Redirecionar autenticado → HomeScreen

---

### Fase 4.2: Firestore Sync (3-4 horas)

#### 4.2.1 Estrutura Firestore
```
users/{userId}
├── email: String
├── name: String
├── createdAt: Timestamp
└── lastSyncedAt: Timestamp

users/{userId}/cycles/{cycleId}
├── startDate: LocalDate
├── endDate: LocalDate?
├── cycleLength: Int
├── periodLength: Int
└── syncedAt: Timestamp

users/{userId}/partners/{partnerId}
├── email: String
├── name: String
├── addedAt: Timestamp
└── canView: Boolean
```

#### 4.2.2 Criar SyncRepository
- [ ] Criar `SyncRepository` (interface em domain/)
- [ ] Implementar `FirestoreSyncRepository` (data/remote/)
- [ ] Métodos:
  - `uploadCycle(userId, cycle): Result<Unit>`
  - `downloadCycles(userId): Result<List<Cycle>>`
  - `deleteRemoteCycle(userId, cycleId): Result<Unit>`
  - `observeRemoteCycles(userId): Flow<List<Cycle>>`

#### 4.2.3 Integrar WorkManager para Background Sync
- [ ] Criar `SyncWorker` (data/workers/)
- [ ] Configurar sync schedule (a cada 6 horas, ou on-demand)
- [ ] Sincronizar ciclos local → Firestore
- [ ] Baixar ciclos Firestore → local
- [ ] Resolver conflitos (last-write-wins)
- [ ] Registrar WorkManager request em AppModule

#### 4.2.4 Atualizar Repositórios Locais
- [ ] `CycleRepository` agora sincroniza com Firestore após cada operação
- [ ] Adicionar campo `syncedAt` em Cycle (Realm)
- [ ] Adicionar campo `synced: Boolean` em Cycle
- [ ] UseCase `SyncCycleDataUseCase` para sincronizar manualmente

#### 4.2.5 Behandlung Offline
- [ ] Queue local de operações pendentes
- [ ] Detectar conectividade (ConnectivityManager)
- [ ] Sincronizar quando voltar online
- [ ] Mostrar indicador "Sincronizando..." na UI

---

### Fase 4.3: Partner Sharing (2-3 horas)

#### 4.3.1 Criar PartnerRepository
- [ ] Interface `PartnerRepository` (domain/)
- [ ] Implementar `FirestorePartnerRepository` (data/remote/)
- [ ] Métodos:
  - `addPartner(userId, partnerEmail): Result<User>`
  - `removePartner(userId, partnerId): Result<Unit>`
  - `getPartners(userId): Flow<List<Partner>>`
  - `getSharedCycles(userId): Flow<List<SharedCycle>>`

#### 4.3.2 Criar PartnerScreen
- [ ] Composable `PartnerScreen` (presentation/ui/partner/)
- [ ] ViewModel `PartnerViewModel` (presentation/viewmodel/)
- [ ] Use Cases:
  - `AddPartnerUseCase(email)`
  - `RemovePartnerUseCase(partnerId)`
  - `GetPartnersUseCase`

#### 4.3.3 Funcionalidades
- [ ] Buscar/adicionar parceiro por email
- [ ] Listar parceiros conectados
- [ ] Remover parceiro
- [ ] Ver ciclo do parceiro (read-only)
- [ ] Aceitar/rejeitar compartilhamento

#### 4.3.4 Permissões Firestore
```
match /users/{userId}/partners/{partnerId} {
  allow read, write: if request.auth.uid == userId;
  allow read: if request.auth.uid == partnerId && exists(/databases/$(database)/documents/users/$(userId)/partners/$(request.auth.uid));
}
```

---

### Fase 4.4: Push Notifications (2 horas)

#### 4.4.1 Configuração FCM
- [ ] Habilitar Cloud Messaging em Firebase Console
- [ ] Gerar Server Key (para testing)
- [ ] Configurar `google-services.json`

#### 4.4.2 Criar FCMService
- [ ] Estender `FirebaseMessagingService`
- [ ] Implementar `onMessageReceived(remoteMessage)`
- [ ] Tipos de notificações:
  - Ciclo mudou de fase
  - Ovulação em 3 dias
  - Próximo período em 1 dia
  - Parceiro adicionou um evento
  - Lembrete de log

#### 4.4.3 Criar NotificationPrefsScreen
- [ ] Permitir ativar/desativar notificações por tipo
- [ ] Configurar horários (ex: "lembretes entre 8h-22h")
- [ ] Usar DataStore para persistir preferências
- [ ] Atualizar tópicos FCM subscribed (ex: "user_{userId}_phase_change")

#### 4.4.4 Implementação
- [ ] Criar `NotificationManager` em data/local/
- [ ] Usar `NotificationCompat` para criar notifications
- [ ] Adicionar ícone + cores da marca
- [ ] Configurar deep-linking para abrir tela relevante

---

## 📊 Arquivos a Criar/Modificar

### Domain Layer (11 arquivos)

**Use Cases:**
```
domain/usecase/
├── SignUpUseCase.kt
├── SignInUseCase.kt
├── SignOutUseCase.kt
├── SyncCycleDataUseCase.kt
├── AddPartnerUseCase.kt
├── RemovePartnerUseCase.kt
├── GetPartnersUseCase.kt
└── impl/
    ├── SignUpUseCaseImpl.kt
    ├── SignInUseCaseImpl.kt
    ├── SignOutUseCaseImpl.kt
    ├── SyncCycleDataUseCaseImpl.kt
    ├── AddPartnerUseCaseImpl.kt
    ├── RemovePartnerUseCaseImpl.kt
    └── GetPartnersUseCaseImpl.kt
```

**Repositories & Models:**
```
domain/
├── models/
│   ├── AuthState.kt
│   ├── User.kt
│   ├── Partner.kt
│   └── SharedCycle.kt
├── repository/
│   ├── AuthRepository.kt
│   ├── SyncRepository.kt
│   └── PartnerRepository.kt
```

### Data Layer (15 arquivos)

**Remote:**
```
data/remote/
├── firebase/
│   ├── FirebaseAuthRepository.kt
│   ├── FirestoreSyncRepository.kt
│   ├── FirestorePartnerRepository.kt
│   └── mappers/
│       ├── FirestoreUserMapper.kt
│       ├── FirestoreCycleMapper.kt
│       └── FirestorePartnerMapper.kt
├── workers/
│   ├── SyncWorker.kt
│   └── NotificationWorker.kt
└── services/
    ├── FCMService.kt
    └── AuthService.kt
```

**DI Modules:**
```
data/di/
├── FirebaseModule.kt
├── AuthModule.kt
├── SyncModule.kt
└── PartnerModule.kt
```

### Presentation Layer (8 arquivos)

**Screens:**
```
presentation/ui/
├── auth/
│   ├── AuthScreen.kt
│   ├── SignUpScreen.kt
│   └── SignInScreen.kt
├── partner/
│   └── PartnerScreen.kt
├── settings/
│   └── NotificationsScreen.kt

presentation/viewmodel/
├── AuthViewModel.kt
├── PartnerViewModel.kt
└── NotificationViewModel.kt
```

**Navigation:**
```
presentation/navigation/
├── AuthGraph.kt
├── MainGraph.kt
└── AppNavigation.kt (atualizada)
```

### Configuration Files

```
app/src/main/
├── AndroidManifest.xml (adicionar FCM receiver)
├── res/xml/
│   ├── backup_rules.xml (excluir credenciais)
│   └── data_extraction_rules.xml
│
app/
├── google-services.json (do Firebase Console)

gradle.properties
├── FIRESTORE_EMULATOR_HOST=... (desenvolvimento local)
└── FCM_ENABLED=true
```

---

## 🔐 Segurança — Checklist

- [ ] Usar Android Keystore para armazenar credenciais
- [ ] Implementar certificate pinning (OkHttp)
- [ ] Validar JWT tokens no backend
- [ ] Usar HTTPS para todas as chamadas Firebase
- [ ] Não logar dados sensíveis (email, senha, tokens)
- [ ] Usar `@Suppress("DataClassPrivateConstructor")` em models sensíveis
- [ ] Testar Firestore Security Rules antes de deploy
- [ ] Implementar rate-limiting no backend (Cloud Functions)

---

## 📱 Testing Strategy — Fase 4

### Unit Tests
```
test/
├── firebase/
│   ├── FirebaseAuthRepositoryTest.kt
│   ├── FirestoreSyncRepositoryTest.kt
│   └── FCMServiceTest.kt
├── viewmodel/
│   ├── AuthViewModelTest.kt
│   └── PartnerViewModelTest.kt
└── usecase/
    ├── SignUpUseCaseTest.kt
    └── SyncCycleDataUseCaseTest.kt
```

**Coverage Target:** 70%+

### Integration Tests
```
androidTest/
├── AuthFlowTest.kt (Sign-up → Sign-in → Home)
├── SyncFlowTest.kt (Offline → Online → Sync)
└── PartnerSharingTest.kt (Add → View → Remove)
```

### Manual Testing Checklist
- [ ] Sign-up com email válido
- [ ] Sign-up com email duplicado → erro
- [ ] Sign-in com credenciais corretas
- [ ] Sign-in com senha errada → erro
- [ ] Google Sign-In flow
- [ ] Logout
- [ ] Offline → criar ciclo → voltar online → sincronizar
- [ ] Adicionar parceiro com email válido
- [ ] Ver ciclo do parceiro
- [ ] Receber push notification
- [ ] Deep-link de notificação funciona

---

## 📚 Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // User collection
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      allow read: if false; // Prevent leaking user list
      
      // Cycles subcollection
      match /cycles/{cycleId} {
        allow read, write: if request.auth.uid == userId;
      }
      
      // Partners subcollection
      match /partners/{partnerId} {
        allow read, write: if request.auth.uid == userId;
        // Partner can read this user's cycles
        allow read: if request.auth.uid == partnerId && exists(/databases/$(database)/documents/users/$(userId)/partners/$(partnerId));
      }
    }
  }
}
```

---

## 🚀 Rollout Strategy

### Phase 4.1 (Firebase Auth)
1. Implementar AuthRepository + Firebase Auth
2. Criar AuthScreen
3. Testar sign-up/sign-in
4. Atualizar navigation
5. Build 14 (Auth completa)

### Phase 4.2 (Firestore Sync)
1. Implementar SyncRepository
2. Integrar WorkManager
3. Atualizar CycleRepository
4. Testar sync offline/online
5. Build 15 (Sync completa)

### Phase 4.3 (Partner Sharing)
1. Implementar PartnerRepository
2. Criar PartnerScreen
3. Testar partner sharing
4. Build 16 (Partners completa)

### Phase 4.4 (Push Notifications)
1. Criar FCMService
2. Implementar notification handling
3. Criar NotificationsScreen
4. Testar push delivery
5. Build 17 (Final Phase 4)

---

## 📈 Métricas de Sucesso

✅ **Build 14:** Autenticação funcional  
✅ **Build 15:** Sincronização com Firestore  
✅ **Build 16:** Compartilhamento com parceiro  
✅ **Build 17:** Push notifications entregues  
✅ **Coverage:** 70%+ de testes  
✅ **Security:** Firestore Rules validadas  

---

## ⏰ Estimativa de Tempo

| Fase | Horas | Build |
|------|-------|-------|
| 4.1 — Firebase Auth | 2-3h | 14 |
| 4.2 — Firestore Sync | 3-4h | 15 |
| 4.3 — Partner Sharing | 2-3h | 16 |
| 4.4 — Push Notifications | 2h | 17 |
| **Total** | **9-12h** | **14-17** |

---

## 🎯 Next Steps (Próximas Ações)

1. ✅ Criar Firebase Project
2. ✅ Configurar `google-services.json`
3. ✅ Implementar AuthRepository
4. ✅ Criar AuthScreen
5. ✅ Testar autenticação
6. ✅ Build 14 validation

**Pronto para começar?**

---

**Status:** 📋 Blueprint completo  
**Próximo:** Implementação da Fase 4.1 — Firebase Auth
