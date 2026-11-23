# 📚 Índice de Documentação do CycleTrack

## 📖 Documentos Principais

### Configuração & Setup
- **[CLAUDE.md](CLAUDE.md)** — Guia de desenvolvimento (desenvolvimento em Kotlin, build, testes, CI/CD)
- **[gradle.properties](gradle.properties)** — Configurações Gradle e versões de dependências
- **[.editorconfig](.editorconfig)** — Formatação de código (indent, line length, charset)

### Análise de Projeto
- **[PROJECT_STATUS.md](PROJECT_STATUS.md)** — Análise COMPLETA do que falta no projeto
  - 12 problemas principais identificados
  - Tabela de status de features
  - Plano de ação priorizado (4 fases)
  - Checklist de deploy

### Documentação de Fase 1
- **[PHASE1_STATUS.md](PHASE1_STATUS.md)** — Status em tempo real da Fase 1
  - Checklist de tarefas
  - Arquivos criados/modificados
  - Notas sobre decisões técnicas

- **[PHASE1_SUMMARY.md](PHASE1_SUMMARY.md)** — Resumo detalhado do trabalho realizado
  - Descrição de cada componente criado
  - Código de exemplo
  - Métricas (25 arquivos, ~400 linhas)

- **[PHASE1_COMPLETE.md](PHASE1_COMPLETE.md)** — Conclusão da Fase 1
  - Build SUCCESS ✅
  - Próximos passos (Fase 2)
  - Decisões técnicas explicadas

### Design & UX
- **[docs/design-system.md](docs/design-system.md)** — Mapeamento Figma → Compose (644 linhas)
  - Cores, tipografia, espaçamento, componentes
  - Material3 tokens
  - Acessibilidade (WCAG 2.1 AA)

### Infraestrutura & Deploy
- **[docs/infrastructure-setup.md](docs/infrastructure-setup.md)** — Setup completo (574 linhas)
  - Firebase, Play Console, Sentry, AppsFlyer
  - CI/CD branch strategy
  - Checklist de produção

### Legal & Compliance
- **[docs/legal/privacy-policy.md](docs/legal/privacy-policy.md)** — Política de Privacidade (209 linhas)
  - LGPD/GDPR/CCPA compliance
  - Dados coletados, retenção, direitos

- **[docs/legal/terms-of-service.md](docs/legal/terms-of-service.md)** — Termos de Serviço (297 linhas)
  - Elegibilidade, assinaturas, lei aplicável

### Linting & Quality
- **[detekt.yml](detekt.yml)** — Configuração Detekt (static analysis)
  - Thresholds: complexidade 15, método 60 linhas
  - Padrões de nomeação (camelCase, PascalCase)

- **[ktlint.yml](ktlint.yml)** — Configuração Ktlint (code formatting)
  - Indentation: 4 espaços, 8 continuação
  - Max line length: 120 caracteres

---

## 🗂️ Estrutura de Arquivos por Área

### `/app/src/main/`
```
java/com/seuapp/cycletrack/
├── CycleTrackApplication.kt         ⭐ Hilt setup
├── di/HiltModules.kt                ⭐ Hilt providers
├── data/
│   ├── model/Models.kt              ✅ Data classes
│   ├── demo/DemoStore.kt            ⚠️ Dados fake (a remover)
│   └── local/realm/
│       ├── RealmRepository.kt       ⭐ Realm singleton
│       ├── dao/                     ⭐ 11 DAOs
│       └── entities/                ⭐ 11 Realm objects
├── presentation/
│   ├── MainActivity.kt              ✅ Compose setup
│   ├── navigation/CycleTrackApp.kt  ✅ Navegação
│   └── ui/                          ✅ Telas Compose
└── util/

res/
├── values/colors.xml                ✅ Material3 colors
├── values/themes.xml                ✅ Material3 themes
├── values/strings.xml               ✅ Strings PT-BR
├── font/inter_variable.ttf          ✅ Typography
└── drawable/ic_launcher_*.xml       ✅ App icons
```

### `/app/src/test/`
```
java/com/seuapp/cycletrack/
├── domain/algorithm/CyclePredictionEngineTest.kt
└── domain/usecase/cycle/CycleUseCasesTest.kt
```

### `/app/src/androidTest/`
```
java/com/seuapp/cycletrack/ui/
├── onboarding/OnboardingFlowTest.kt  (246 linhas)
├── home/HomeScreenTest.kt            (372 linhas)
├── calendar/CalendarScreenTest.kt    (458 linhas)
└── logging/LoggingScreenTest.kt      (506 linhas)
```

### `.github/workflows/`
```
ci.yml  → Pipeline completo (lint → test → build → deploy)
```

---

## 🎯 Por Onde Começar

### Para Novos Desenvolvedores
1. Leia **CLAUDE.md** para entender como desenvolver
2. Leia **PROJECT_STATUS.md** para entender o estado geral
3. Leia **PHASE1_COMPLETE.md** para saber onde estamos

### Para Implementar Fase 2
1. Leia **PHASE1_COMPLETE.md** seção "Próximos Passos"
2. Use **PROJECT_STATUS.md** como referência de arquitetura
3. Implemente baseado em **PHASE1_SUMMARY.md** (skeleton está pronto)

### Para Deploy
1. Consulte **docs/infrastructure-setup.md**
2. Siga checklist em **PROJECT_STATUS.md**
3. Configure secrets do GitHub Actions

### Para Design
1. Consulte **docs/design-system.md**
2. Implemente em **app/src/main/res/values/**
3. Use **app/src/main/java/.../presentation/ui/theme/Theme.kt**

---

## 📊 Status Geral (2026-11-19)

| Área | Status | Próximo |
|------|--------|---------|
| **Build & DI** | ✅ Hilt OK | ViewModels |
| **Database** | ✅ Realm OK | Implementar DAOs |
| **UI/UX** | ✅ Telas OK | Conectar ViewModels |
| **Testes** | ✅ Structure OK | Implementar lógica |
| **Legal** | ✅ Docs OK | Publicar no domínio |
| **Firebase** | ⚠️ Placeholder | Configurar real |
| **Sentry** | ⚠️ Placeholder | Configurar real |
| **Health Connect** | ❌ Não implementado | Fase 3 |

---

## 🔄 Fluxo de Desenvolvimento

```
1. Fase 1: Foundation (✅ CONCLUÍDA)
   └─ Hilt DI + Realm setup

2. Fase 2: ViewModels & Repositórios (📝 PRÓXIMA)
   └─ Mover lógica de negócio

3. Fase 3: Health Connect (📋 PLANEJADA)
   └─ Integração de saúde

4. Fase 4: Release (📋 PLANEJADA)
   └─ Deploy no Play Console
```

---

## 🔗 Links Úteis

**Referências Externas:**
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Realm Kotlin SDK](https://www.mongodb.com/docs/realm/sdk/kotlin/)
- [Jetpack Compose](https://developer.android.com/compose)
- [Material Design 3](https://m3.material.io/)
- [Android Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect)

**Repositórios:**
- Código principal: `app/src/main/`
- Testes: `app/src/test/` e `app/src/androidTest/`
- Código legado: `app/src/legacy/` (a migrar gradualmente)

---

## 📝 Convenções

### Git
- Branch: `feature/`, `bugfix/`, `release/`
- Commit: `tipo(área): descrição` (feat, fix, refactor, chore)
- Release notes obrigatórias em `docs/releases/CURRENT.md`

### Código
- Linguagem: Kotlin 1.9.22
- Lint: Detekt + Ktlint (rodar antes de commit)
- Tests: JUnit 4 (unit), Espresso (UI)
- Padrão: Clean Architecture (domain, data, presentation)

### Pull Requests
- Título: < 70 caracteres
- Descrição: Por quê, não o quê
- Checklist: Testes, lint, documentação

---

**Última atualização:** 2026-11-19  
**Próxima fase:** Fase 2 (ViewModels)  
**Status:** ✅ Fase 1 Concluída
