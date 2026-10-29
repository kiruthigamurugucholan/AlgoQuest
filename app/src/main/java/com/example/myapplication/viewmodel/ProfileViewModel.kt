package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.ProfileData
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

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            Log.d(TAG, "loadProfile called, dispatching fetch...")
            repository.getProfile().fold(
                onSuccess = { data ->
                    Log.d(TAG, "Profile loaded successfully for ${data.user.name}")
                    _uiState.value = ProfileUiState.Success(data)
                },
                onFailure = { err ->
                    Log.e(TAG, "Failed to load profile: ${err.message}", err)
                    _uiState.value = ProfileUiState.Error(err.message ?: "Failed to load profile. Please tap Retry.")
                }
            )
        }
    }

    fun clearState() {
        Log.d(TAG, "clearState called - resetting in-memory ProfileUiState")
        _uiState.value = ProfileUiState.Loading
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            repository.updateProfile(name).onSuccess {
                loadProfile()
            }
        }
    }
}
