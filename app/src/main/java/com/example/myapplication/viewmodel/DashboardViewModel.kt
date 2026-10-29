package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.DashboardData
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

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            repository.getDashboard().fold(
                onSuccess = { data ->
                    _uiState.value = DashboardUiState.Success(data)
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

