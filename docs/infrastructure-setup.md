# CycleTrack - Infrastructure Setup Guide

**Versão:** 1.0  
**Data:** 17 de novembro de 2026

---

## 1. Visão Geral da Infraestrutura

```
┌─────────────────────────────────────────────────────────────────┐
│                      CycleTrack Infrastructure                   │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Firebase  │  │  Google     │  │  Play       │              │
│  │   Project   │  │  Play       │  │  Console    │              │
│  │             │  │  Console    │  │             │              │
│  │ Auth        │  │             │  │ Internal    │              │
│  │ Firestore   │  │ Subscriptions│ │ Testing     │              │
│  │ Crashlytics │  │ (Billing)   │  │ Production  │              │
│  │ Analytics   │  │             │  │ Tracks      │              │
│  │ Messaging   │  │             │  │             │              │
│  │ Remote Config│  │             │  │             │              │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘              │
│         │                │                │                      │
│         └────────────────┼────────────────┘                      │
│                          ▼                                       │
│  ┌─────────────────────────────────────────────┐                │
│  │              CI/CD (GitHub Actions)          │                │
│  │  Lint → Test → Build → Security → Deploy    │                │
│  └─────────────────────────────────────────────┘                │
│                          │                                       │
│         ┌────────────────┼────────────────┐                      │
│         ▼                ▼                ▼                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Sentry    │  │  AppsFlyer  │  │  Amplitude  │              │
│  │  (Errors)   │  │ (Attribution)│ │ (Analytics) │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Contas Necessárias

### 2.1 Google / Firebase
| Item | Descrição | Link |
|------|-----------|------|
| **Google Cloud Project** | Projeto base para APIs | `https://console.cloud.google.com` |
| **Firebase Project** | Auth, Firestore, Crashlytics, Analytics, Messaging, Remote Config | `https://console.firebase.google.com` |
| **Google Play Console** | Publicação, subscriptions, internal testing | `https://play.google.com/console` |
| **Google Play Billing** | Configuração de produtos de assinatura | Dentro do Play Console |
| **Health Connect** | Permissões de saúde (não precisa de conta separada) | - |

### 2.2 Autenticação e Identidade
| Item | Descrição | Link |
|------|-----------|------|
| **Auth0** (opcional) | Auth social enterprise, MFA, logs de auditoria | `https://auth0.com` |
| **Google Sign-In** | Já incluso no Firebase Auth | - |

### 2.3 Monitoramento e Analytics
| Item | Descrição | Link | Plano |
|------|-----------|------|-------|
| **Sentry** | Error tracking, performance monitoring | `https://sentry.io` | Developer (grátis 5k eventos/mês) |
| **AppsFlyer** | Atribuição de instalação, deep linking | `https://appsflyer.com` | Free tier |
| **Amplitude** | Product analytics, funis, retenção | `https://amplitude.com` | Starter (grátis 10M eventos/mês) |

### 2.4 Domínios e Legal
| Item | Descrição |
|------|-----------|
| **cycletrack.app** | Domínio principal |
| **api.cycletrack.app** | API backend (futuro) |
| **Termos de Serviço** | `https://cycletrack.app/terms` |
| **Política de Privacidade** | `https://cycletrack.app/privacy` |
| **Suporte** | `support@cycletrack.app` |

---

## 3. Passo a Passo de Configuração

### 3.1 Firebase Project

```bash
# 1. Acesse https://console.firebase.google.com
# 2. "Add project" → "CycleTrack"
# 3. Enable Google Analytics (opcional, mas recomendado)
# 4. Selecione conta Analytics ou crie nova
```

**Configuração no Console Firebase:**

1. **Authentication** → Sign-in method → Enable:
   - Email/Password
   - Google
   - Anonymous (para modo anônimo)

2. **Firestore Database** → Create database:
   - Start in **production mode**
   - Location: `southamerica-east1` (São Paulo)
   - Rules: Configurar depois (ver seção 3.1.1)

3. **Crashlytics** → Enable

4. **Cloud Messaging** → Enable (para push notifications)

5. **Remote Config** → Enable

6. **App Check** → Enable com Play Integrity

7. **Project Settings** → General → Add Android app:
   - Package name: `com.seuapp.cycletrack`
   - Debug signing certificate: SHA-1 do debug.keystore
   - Release signing certificate: SHA-1 do release.keystore
   - Download `google-services.json` → `app/src/main/google-services.json`

#### 3.1.1 Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null && request.auth.uid == userId;
    }

    // Cycles - user owns their cycles
    match /cycles/{cycleId} {
      allow read, write: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }

    // Symptoms
    match /symptoms/{symptomId} {
      allow read, write: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }

    // Predictions
    match /predictions/{predictionId} {
      allow read: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow write: if false; // Only backend writes predictions
    }

    // Partner connections
    match /partners/{partnerId} {
      allow read, write: if request.auth != null &&
        (resource.data.userId == request.auth.uid ||
         resource.data.partnerId == request.auth.uid);
    }

    // Exports
    match /exports/{exportId} {
      allow read, write: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }

    // Subscriptions (read-only for user, write by backend)
    match /subscriptions/{subscriptionId} {
      allow read: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow write: if false;
    }

    // Public content (read-only)
    match /content/{contentId} {
      allow read: if true;
      allow write: if false;
    }

    // Analytics events (write-only, no read)
    match /analytics/{eventId} {
      allow read: if false;
      allow write: if request.auth != null;
    }
  }
}
```

### 3.2 Google Play Console

```bash
# 1. Acesse https://play.google.com/console
# 2. Pague taxa de $25 (única)
# 3. Crie aplicação: "CycleTrack"
# 4. Configure:
#    - Idioma padrão: Português (Brasil)
#    - Categoria: Saúde e fitness
#    - Tags: ciclo menstrual, fertilidade, gravidez, saúde da mulher
```

**Configuração de Assinaturas (Subscriptions):**

1. **Monetize** → **Products** → **Subscriptions** → Create subscription:

| Product ID | Name | Price (BRL) | Price (USD) | Period | Free Trial | Grace Period |
|------------|------|-------------|-------------|--------|------------|--------------|
| `premium_monthly` | Premium Mensal | R$ 19,90 | $4.99 | 1 month | 7 days | 3 days |
| `premium_yearly` | Premium Anual | R$ 199,90 | $39.99 | 1 year | 7 days | 3 days |
| `family_monthly` | Família (5 contas) | R$ 29,90 | $7.99 | 1 month | 7 days | 3 days |

2. **Base Plan** para cada:
   - Auto-renew: Enabled
   - Backward compatibility: Enabled
   - Offer tags: `premium`, `family`

3. **Test tracks**: Internal testing → Create release → Upload AAB

**Licença de Teste:**
- Adicione emails de testadores em **Internal testing** → **Testers**

### 3.3 Keystore e Assinatura

```bash
# Execute o script de geração
cd scripts
generate-keystore.bat

# OU manualmente:
keytool -genkeypair \
  -alias cycletrack-release-key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -keystore ../keystore/release.keystore \
  -storepass SUA_SENHA_FORTE \
  -keypass SUA_SENHA_FORTE \
  -dname "CN=CycleTrack App, OU=Mobile Development, O=CycleTrack, L=Sao Paulo, ST=SP, C=BR"
```

**Obter SHA-1 para Firebase/Play Console:**

```bash
# Debug keystore
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# Release keystore
keytool -list -v -keystore ../keystore/release.keystore -alias cycletrack-release-key
```

**Configurar local.properties (NÃO COMMITAR):**

```properties
# local.properties
KEYSTORE_PATH=../keystore/release.keystore
KEYSTORE_PASSWORD=SUA_SENHA_FORTE
KEY_ALIAS=cycletrack-release-key
KEY_PASSWORD=SUA_SENHA_FORTE

# API
API_BASE_URL=https://api.cycletrack.app/v1
```

**GitHub Actions Secrets (Settings → Secrets → Actions):**

| Secret Name | Value |
|-------------|-------|
| `SIGNING_STORE_FILE` | `base64 -i ../keystore/release.keystore` |
| `SIGNING_STORE_PASSWORD` | `SUA_SENHA_FORTE` |
| `SIGNING_KEY_ALIAS` | `cycletrack-release-key` |
| `SIGNING_KEY_PASSWORD` | `SUA_SENHA_FORTE` |
| `GOOGLE_PLAY_SERVICE_ACCOUNT` | JSON da service account |
| `GOOGLE_PLAY_PROJECT_ID` | ID do projeto GCP |
| `SLACK_WEBHOOK` | Webhook URL do Slack |
| `FIREBASE_TOKEN` | `firebase login:ci` |
| `SENTRY_AUTH_TOKEN` | Token do Sentry |
| `SENTRY_ORG` | Organização Sentry |
| `SENTRY_PROJECT` | Projeto Sentry |

### 3.4 Sentry

```bash
# 1. Crie conta em https://sentry.io
# 2. New Project → Android (Kotlin)
# 3. DSN: copiar para local.properties ou buildConfigField
# 4. Configure:
#    - Enable NDK crash reporting
#    - Enable ANR tracking
#    - Sample rate: 1.0 (100%) para debug, 0.1 para produção
```

**build.gradle.kts (app):**

```kotlin
// Sentry
buildConfigField("String", "SENTRY_DSN", "\"https://xxx@sentry.io/xxx\"")

// Or via local.properties
// SENTRY_DSN=xxx
```

### 3.5 AppsFlyer

```bash
# 1. Crie conta em https://appsflyer.com
# 2. Add App → Android → com.seuapp.cycletrack
# 3. Dev Key: copiar para config
# 4. Configure:
#    - OneLink para deep linking
#    - SKAdNetwork (iOS futuro)
#    - Partner integrations (Meta, Google Ads, TikTok)
```

### 3.6 Amplitude

```bash
# 1. Crie conta em https://amplitude.com
# 2. Create Project → CycleTrack
# 3. API Key: copiar para config
# 4. Configure:
#    - Event taxonomy
#    - User properties
#    - Cohorts
#    - Data governance (LGPD/GDPR)
```

---

## 4. Configuração de Domínios e SSL

### 4.1 Registro de Domínio

```bash
# Recomendado: Google Domains, Cloudflare, ou Registro.br
# Domínios:
#   cycletrack.app
#   www.cycletrack.app
#   api.cycletrack.app (para backend futuro)
```

### 4.2 DNS (Cloudflare recomendado)

| Type | Name | Content | Proxy |
|------|------|---------|-------|
| A | @ | IP do servidor/Cloudflare | Proxied |
| CNAME | www | cycletrack.app | Proxied |
| CNAME | api | cycletrack.app | Proxied |
| TXT | @ | `v=spf1 include:_spf.google.com ~all` | - |
| TXT | _dmarc | `v=DMARC1; p=quarantine; rua=mailto:dmarc@cycletrack.app` | - |

### 4.3 SSL/TLS

- **Cloudflare**: Full (Strict) + Certificate Authority Origin
- **HSTS**: Enabled, max-age=31536000, includeSubDomains, preload
- **TLS 1.3**: Minimum

---

## 5. Backend API (Futuro - Opcional)

Se decidir ter backend próprio:

### 5.1 Stack Sugerida
- **Language**: Kotlin (Ktor/Spring Boot) ou Node.js (Fastify)
- **Database**: PostgreSQL (Cloud SQL) + Redis (Memorystore)
- **Auth**: Firebase Auth tokens validation
- **Hosting**: Cloud Run (serverless containers)
- **API Gateway**: Cloud Endpoints ou Kong

### 5.2 Endpoints Principais

```
POST   /auth/register           # Registro
POST   /auth/login              # Login
POST   /auth/refresh            # Refresh token
POST   /auth/forgot-password    # Esqueci senha
POST   /auth/reset-password     # Reset senha

GET    /user/profile            # Perfil do usuário
PUT    /user/profile            # Atualizar perfil
DELETE /user/account            # Excluir conta (LGPD)

GET    /cycles                  # Listar ciclos
POST   /cycles                  # Criar ciclo
GET    /cycles/{id}             # Obter ciclo
PUT    /cycles/{id}             # Atualizar ciclo
DELETE /cycles/{id}             # Soft delete
DELETE /cycles/{id}/hard        # Hard delete

POST   /symptoms                # Registrar sintoma
GET    /symptoms                # Listar sintomas

GET    /predictions/current     # Previsão atual
POST   /predictions/regenerate  # Forçar recálculo

GET    /exports                 # Listar exports
POST   /exports                 # Solicitar export
GET    /exports/{id}/download   # Download arquivo

GET    /partner/connection      # Status conexão
POST   /partner/invite          # Convidar parceiro
POST   /partner/accept          # Aceitar convite
DELETE /partner/revoke          # Revogar acesso

GET    /subscriptions/status    # Status assinatura
POST   /subscriptions/verify    # Verificar purchase token

GET    /content                 # Listar conteúdo
GET    /content/{id}            # Obter artigo
POST   /content/{id}/bookmark   # Favoritar

POST   /analytics/event         # Enviar evento
```

---

## 6. CI/CD Pipeline Detalhado

### 6.1 Branch Strategy

```
main ─────────────────────────────────────────────────────► Production
  │
  ├── develop ──────────────────────────────────────────► Staging
  │    │
  │    ├── feature/onboarding-redesign
  │    ├── feature/health-connect-sync
  │    ├── bugfix/crash-on-startup
  │    └── hotfix/critical-security
  │
  └── release/v1.0.0 ──────────────────────────────────► RC → Production
```

### 6.2 GitHub Actions Workflows

Já criado em `.github/workflows/ci.yml` com:
- Lint (Detekt + Ktlint)
- Unit Tests
- UI Tests (emulator)
- Build Debug/Release
- Security Scan (OWASP Dependency Check)
- Deploy Internal Testing (main branch)
- Release Creation (tags)

### 6.3 Versionamento

```kotlin
// app/build.gradle.kts
defaultConfig {
    versionCode = 1
    versionName = "1.0.0"
}

// Ou via CI:
// versionCode = ${{ github.run_number }}
// versionName = "1.0.${{ github.run_number }}-${{ github.sha::7 }}"
```

### 6.4 Changelog

```markdown
# CHANGELOG.md

## [1.0.0] - 2026-11-17
### Added
- Initial release
- 33-screen onboarding flow
- Cycle tracking with predictions
- Symptom logging (35 symptoms)
- Pregnancy mode
- Partner sharing
- LGPD/GDPR export (JSON/CSV/PDF)
- Health Connect integration
- Premium subscriptions

### Fixed
- N/A

### Security
- Certificate pinning
- Encrypted local storage
- Biometric authentication
```

---

## 7. Checklist de Produção

### 7.1 Pré-Lançamento (Internal Testing)

- [ ] Keystore gerado e salvo com segurança
- [ ] google-services.json configurado (debug + release)
- [ ] Firebase Auth providers habilitados
- [ ] Firestore rules testadas
- [ ] Crashlytics recebendo crashes de teste
- [ ] Play Billing products criados e ativos
- [ ] Health Connect permissions testadas em device real
- [ ] Export LGPD funcionando (JSON/CSV/PDF)
- [ ] Biometric auth funcionando
- [ ] Deep links (AppsFlyer/OneLink) testados
- [ ] Sentry recebendo eventos de teste
- [ ] Amplitude/AppsFlyer eventos trackeados
- [ ] Push notifications (FCM) funcionando
- [ ] Remote Config valores padrão definidos

### 7.2 Testes de Qualidade

- [ ] Unit tests > 80% coverage
- [ ] UI tests: onboarding, home, calendar, logging, insights
- [ ] Testes em: Android 7, 8, 9, 10, 11, 12, 13, 14
- [ ] Testes em: Phone, Tablet, Foldable
- [ ] Testes de acessibilidade (TalkBack, font scale 200%)
- [ ] Testes de performance (LeakCanary, Perfetto)
- [ ] Testes de bateria (WorkManager, sync)
- [ ] Testes offline-first (modo avião)

### 7.3 Legal e Compliance

- [ ] Termos de Serviço publicados
- [ ] Política de Privacidade publicada
- [ ] LGPD: DPO nomeado, registros de processamento
- [ ] GDPR: DPA com subprocessadores, SCCs
- [ ] COPPA: Verificação de idade, modo anônimo
- [ ] Play Console: Data Safety form preenchido
- [ ] App Store (futuro): Privacy labels

### 7.4 Lançamento (Production)

- [ ] Internal testing aprovado por 20+ usuários
- [ ] Closed testing (tracks) se necessário
- [ ] Open testing se necessário
- [ ] Production release: Rollout 10% → 25% → 50% → 100%
- [ ] Monitoramento Sentry/Crashlytics nas primeiras 24h
- [ ] Rollback plan documentado

---

## 8. Custos Estimados (Mensal)

| Serviço | Free Tier | Estimado Pago (escala) |
|---------|-----------|------------------------|
| Firebase (Spark) | Grátis | Blaze: ~$25-100/mês |
| Google Play Console | $25 (único) | - |
| Sentry (Developer) | 5k eventos | Team: $26/mês |
| AppsFlyer | Free | Growth: ~$0.06/install |
| Amplitude (Starter) | 10M eventos | Growth: ~$995/mês |
| Domínio (.app) | - | ~$15-20/ano |
| SSL (Cloudflare) | Grátis | - |
| **Total Inicial** | **$25** | **~$100-500/mês** |

---

## 9. Contatos de Suporte

| Área | Contato | SLA |
|------|---------|-----|
| **Firebase** | Console → Support | 24h (Blaze) |
| **Google Play** | Console → Help | 48h |
| **Sentry** | support@sentry.io | 24h (Team+) |
| **AppsFlyer** | support@appsflyer.com | 24h |
| **Amplitude** | help@amplitude.com | 24h |
| **Auth0** (se usar) | support@auth0.com | 8h (Paid) |

---

## 10. Próximos Passos Pós-Lançamento

1. **Semana 1**: Monitorar crashes, ANRs, feedback usuários
2. **Semana 2**: Analisar funis (onboarding → primeira menstruação registrada → retenção D7/D30)
3. **Mês 1**: A/B test onboarding, paywall, notificações
4. **Mês 2**: Implementar backend próprio se necessário
5. **Contínuo**: iOS version, Perimenopause mode, AI insights

---

*Documento vivo - atualizar a cada mudança de infraestrutura*