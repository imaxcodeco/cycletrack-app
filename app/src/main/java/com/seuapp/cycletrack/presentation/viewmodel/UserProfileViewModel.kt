package com.seuapp.cycletrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuapp.cycletrack.data.session.UserSession
import com.seuapp.cycletrack.domain.model.UserProfile
import com.seuapp.cycletrack.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val userSession: UserSession
) : ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile = _profile.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    fun loadProfile() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _profile.value = userProfileRepository.getProfile(userSession.currentUserId())
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error loading profile")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateGoal(goal: String) {
        viewModelScope.launch {
            try {
                val userId = userSession.currentUserId()
                val current = _profile.value ?: userProfileRepository.getProfile(userId)
                val updated = (current ?: UserProfile(id = userId, email = "")).copy(goal = goal)
                userProfileRepository.upsertProfile(updated)
                _profile.value = updated
            } catch (e: Exception) {
                _error.value = e.message
                Timber.e(e, "Error updating goal")
            }
        }
    }
}
