# 📱 Migração de Telas para ViewModels (Hilt)

**Status:** 3/7 telas migrando ✅

---

## ✅ Telas Migradas

| Tela | ViewModel | Status |
|------|-----------|--------|
| **HomeScreen** | HomeViewModel | 🔄 Compilando |
| **CalendarScreen** | HomeViewModel | 🔄 Compilando |
| **LoggingScreen** | SymptomViewModel | 🔄 Compilando |
| **InsightsScreen** | PredictionViewModel | ⏳ Próximo |
| **ProfileScreen** | UserProfileViewModel | ⏳ Próximo |
| **OnboardingScreen** | UserProfileViewModel | ⏳ Próximo |
| **SettingsScreen** | (criará) | ⏳ Próximo |

---

## Padrão de Migração

Cada tela segue este padrão:

### 1. **Importações**
```kotlin
// ✅ Adicionar
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.<EntityName>ViewModel

// ❌ Remover
import androidx.compose.runtime.collectAsState
import com.seuapp.cycletrack.data.demo.AppContainer
```

### 2. **Injetar ViewModel**
```kotlin
@Composable
fun <ScreenName>(viewModel: <EntityName>ViewModel = hiltViewModel()) {
    val state by viewModel.<stateProperty>.collectAsStateWithLifecycle()
    // ...
}
```

### 3. **Remover AppContainer.store**
Substituir todas as referências de `AppContainer.store` por dados do ViewModel.

---

## 📝 Mudanças em Cada Tela

### HomeScreen
- ✅ Injetado HomeViewModel
- ✅ Substituído AppContainer.store por StateFlows
- ✅ Calculado dayOfCycle, cycleLength, progress
- 📝 TODO: Implementar cálculo de fase, previsões

### CalendarScreen
- ✅ Injetado HomeViewModel
- ✅ Refatorado buildCycleDays() para usar currentCycle
- ✅ Removido DemoStore.store

### LoggingScreen
- ✅ Injetado SymptomViewModel
- ✅ Adicionar enums FlowLevel e Mood localmente
- ✅ Removidas todas as chamadas a store.log*()
- 📝 TODO: Implementar save de sintomas/fluxo/humor/peso

---

**Build 6:** Compilando 3 telas refatoradas...
