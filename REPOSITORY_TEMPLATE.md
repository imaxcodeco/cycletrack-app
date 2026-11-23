# Template: Implementar Novo Repositório

Use este template para implementar repositórios adicionais (Symptom, Prediction, etc).

## Passo 1: Interface (domain/repository/XRepository.kt)

```kotlin
package com.seuapp.cycletrack.domain.repository

import com.seuapp.cycletrack.domain.model.X
import kotlinx.coroutines.flow.Flow

interface XRepository {
    fun getXs(userId: String): Flow<List<X>>
    suspend fun getX(id: String): X?
    suspend fun upsertX(x: X)
    suspend fun deleteX(id: String)
}
```

## Passo 2: Implementação (data/repository/XRepositoryImpl.kt)

```kotlin
package com.seuapp.cycletrack.data.repository

import com.seuapp.cycletrack.data.local.realm.dao.XDao
import com.seuapp.cycletrack.data.local.realm.entities.XRealm
import com.seuapp.cycletrack.domain.model.X
import com.seuapp.cycletrack.domain.repository.XRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class XRepositoryImpl @Inject constructor(
    private val xDao: XDao
) : XRepository {

    override fun getXs(userId: String): Flow<List<X>> {
        return flowOf(emptyList()) // TODO: Implementar com Realm Flow
    }

    override suspend fun getX(id: String): X? {
        val realmX = xDao.getX(id) ?: return null
        return realmX.toDomain()
    }

    override suspend fun upsertX(x: X) {
        val realmX = x.toRealm()
        xDao.upsertX(realmX)
    }

    override suspend fun deleteX(id: String) {
        xDao.deleteX(id)
    }

    // Mappers
    private fun X.toRealm(): XRealm = XRealm().apply {
        this@XRepositoryImpl.id = this@toRealm.id
        // ... map other fields
    }

    private fun XRealm.toDomain(): X = X(
        id = this.id,
        // ... map other fields
    )
}
```

## Passo 3: Hilt Provider (di/HiltModules.kt)

Adicionar ao `RepositoryModule`:

```kotlin
@Provides
@Singleton
fun provideXRepository(xDao: XDao): XRepository {
    return XRepositoryImpl(xDao)
}
```

## Passo 4: ViewModel (presentation/viewmodel/XViewModel.kt)

```kotlin
package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.domain.model.X
import com.seuapp.cycletrack.domain.repository.XRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class XViewModel @Inject constructor(
    private val xRepository: XRepository
) : ViewModel() {

    private val _xs = MutableStateFlow<List<X>>(emptyList())
    val xs = _xs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadXs(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // TODO: Usar Flow quando implementado
                // xRepository.getXs(userId).collect { _xs.value = it }
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error loading Xs")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addX(x: X) {
        viewModelScope.launch {
            try {
                xRepository.upsertX(x)
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error adding X")
            }
        }
    }
}
```

## Checklist de Implementação

### Para cada entidade (Symptom, Prediction, UserProfile, etc):

- [ ] 1. Verificar que modelo Domain existe (`domain/model/X.kt`)
- [ ] 2. Criar interface Repository (`domain/repository/XRepository.kt`)
- [ ] 3. Implementar repositório (`data/repository/XRepositoryImpl.kt`)
- [ ] 4. Implementar mappers (X ↔ XRealm)
- [ ] 5. Adicionar Hilt provider em `RepositoryModule`
- [ ] 6. Criar ViewModel (`presentation/viewmodel/XViewModel.kt`)
- [ ] 7. Testar compilação
- [ ] 8. Atualizar tela correspondente para usar ViewModel

## Repositórios a Implementar

| Entidade | Priority | Status |
|----------|----------|--------|
| **Cycle** | ⭐⭐⭐ | ✅ DONE |
| **Symptom** | ⭐⭐⭐ | ⏳ TODO |
| **Prediction** | ⭐⭐⭐ | ⏳ TODO |
| **UserProfile** | ⭐⭐⭐ | ⏳ TODO |
| **Pregnancy** | ⭐⭐ | ⏳ TODO |
| **Partner** | ⭐⭐ | ⏳ TODO |
| **Subscription** | ⭐⭐ | ⏳ TODO |
| **Content** | ⭐ | ⏳ TODO |
| **HealthDataPoint** | ⭐ | ⏳ TODO |
| **Notification** | ⭐ | ⏳ TODO |
| **Export** | ⭐ | ⏳ TODO |

---

**Use este template com `find+replace` para acelerar implementação.**
