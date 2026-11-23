# Migração HomeScreen: DemoStore → ViewModel

## Padrão Atual (❌ Remover)

```kotlin
@Composable
fun HomeScreen() {
    val store = AppContainer.store  // ❌ Dados fake em memória
    val cycles by store.cycles.collectAsState()
    val logs by store.logs.collectAsState()
    // ... usa dados fake
}
```

## Padrão Novo (✅ Usar)

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()  // ✅ Injetar via Hilt
) {
    val currentCycle by viewModel.currentCycle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // ... mostra dados reais do Realm
}
```

## Mudanças Necessárias

### 1. Importe o ViewModel
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
import com.seuapp.cycletrack.presentation.viewmodel.HomeViewModel
```

### 2. Adicione parâmetro ao Composable
```kotlin
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel())
```

### 3. Substitua acesso a dados
```kotlin
// ANTES
val cycles by store.cycles.collectAsState()
val dayOfCycle = store.currentDayOfCycle
val phase = store.currentPhase

// DEPOIS
val currentCycle by viewModel.currentCycle.collectAsState()
val dayOfCycle = currentCycle?.let { ... } ?: 0
val phase = calculatePhase(currentCycle)  // Função helper
```

### 4. Adicione tratamento de loading/erro
```kotlin
if (isLoading) {
    CircularProgressIndicator()
} else if (error != null) {
    ErrorContent(error!!)
} else if (currentCycle != null) {
    CycleContent(currentCycle!!)
}
```

## Dados Esperados do ViewModel

| Property | Type | Description |
|----------|------|-------------|
| `currentCycle` | `StateFlow<Cycle?>` | Ciclo atual |
| `isLoading` | `StateFlow<Boolean>` | Carregando |
| `error` | `StateFlow<String?>` | Mensagem de erro |

## Helper Functions Necessárias

```kotlin
fun calculatePhase(cycle: Cycle?): CyclePhase {
    if (cycle == null) return CyclePhase.FOLICULAR
    // Implementar lógica baseada em datas
    return when {
        // ... lógica
        else -> CyclePhase.FOLICULAR
    }
}

fun calculateDayOfCycle(cycle: Cycle?): Int {
    if (cycle == null) return 0
    val today = LocalDate.now()
    return ChronoUnit.DAYS.between(cycle.startDate, today).toInt() + 1
}
```

## Checklist de Migração

- [ ] Adicionar import do ViewModel
- [ ] Adicionar parâmetro `hiltViewModel()` ao Composable
- [ ] Remover `AppContainer.store`
- [ ] Remover imports de `DemoStore`
- [ ] Substituir todas as leituras de `store.*` por `viewModel.*`
- [ ] Adicionar tratamento de loading/erro
- [ ] Testar compilação
- [ ] Verificar que dados aparecem corretamente

## Status: 📋 Aguardando compilação

Será implementado após validar que `HomeViewModel` compila com sucesso.
