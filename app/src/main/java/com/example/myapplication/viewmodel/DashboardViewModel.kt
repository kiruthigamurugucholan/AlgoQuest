package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.DashboardData
import com.example.myapplication.data.remote.TokenManager
import com.example.myapplication.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val data: DashboardData) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DashboardRepository(application)
    private val tokenManager = TokenManager.getInstance(application)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    fun loadDashboard() {
        val currentUserId = tokenManager.getUserId()
        if (currentUserId.isNullOrBlank()) {
            Log.w(TAG, "Cannot load dashboard: User is not authenticated")
            _uiState.value = DashboardUiState.Loading
            return
        }

        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            repository.getDashboard().fold(
                onSuccess = { data ->
                    // Verify that dashboard data matches the active user session
                    if (data.user.id.toString() == currentUserId) {
                        _uiState.value = DashboardUiState.Success(data)
                    } else {
                        Log.w(TAG, "Dashboard user UID ${data.user.id} does not match active UID $currentUserId")
                        _uiState.value = DashboardUiState.Loading
                    }
                },
                onFailure = { error ->
                    _uiState.value = DashboardUiState.Error(error.message ?: "Failed to load dashboard")
                }
            )
        }
    }

    fun clearState() {
        _uiState.value = DashboardUiState.Loading
    }
}
