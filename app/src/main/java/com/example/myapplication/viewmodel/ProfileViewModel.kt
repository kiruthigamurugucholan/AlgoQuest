package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.remote.TokenManager
import com.example.myapplication.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Empty : ProfileUiState()
    data class Success(val profile: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(application)
    private val tokenManager = TokenManager.getInstance(application)

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    fun loadProfile() {
        val currentUserId = tokenManager.getUserId()
        if (currentUserId.isNullOrBlank()) {
            Log.w(TAG, "Cannot load profile: User is not authenticated")
            _uiState.value = ProfileUiState.Empty
            return
        }

        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            Log.d(TAG, "loadProfile called for user $currentUserId, dispatching fetch...")
            repository.getProfile().fold(
                onSuccess = { data ->
                    // Verify that the loaded profile matches the active authenticated user's ID
                    if (data.user.id.toString() == currentUserId) {
                        Log.d(TAG, "Profile verified for active user: ${data.user.name} (UID: ${data.user.id})")
                        _uiState.value = ProfileUiState.Success(data)
                    } else {
                        Log.w(TAG, "Profile UID ${data.user.id} does not match active session UID $currentUserId. Refreshing.")
                        val cached = tokenManager.getUser()
                        if (cached != null && cached.id.toString() == currentUserId) {
                            _uiState.value = ProfileUiState.Success(repository.buildCachedProfile(cached))
                        } else {
                            _uiState.value = ProfileUiState.Error("Session error. Please log in again.")
                        }
                    }
                },
                onFailure = { err ->
                    Log.e(TAG, "Failed to load profile: ${err.message}", err)
                    val cached = tokenManager.getUser()
                    if (cached != null && cached.id.toString() == currentUserId) {
                        _uiState.value = ProfileUiState.Success(repository.buildCachedProfile(cached))
                    } else {
                        _uiState.value = ProfileUiState.Error(err.message ?: "Failed to load profile. Please tap Retry.")
                    }
                }
            )
        }
    }

    fun clearState() {
        Log.d(TAG, "clearState called - resetting in-memory ProfileUiState")
        _uiState.value = ProfileUiState.Empty
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            repository.updateProfile(name).onSuccess {
                loadProfile()
            }
        }
    }
}
