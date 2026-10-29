package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.AuthData
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val authData: AuthData) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun isLoggedIn(): Boolean = repository.isLoggedIn()

    fun getSavedUser(): UserDto? = repository.getSavedUser()

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.register(name, email, pass).fold(
                onSuccess = { data ->
                    _uiState.value = AuthUiState.Success(data)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Registration failed")
                }
            )
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(email, pass).fold(
                onSuccess = { data ->
                    _uiState.value = AuthUiState.Success(data)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Invalid credentials")
                }
            )
        }
    }

    fun loginWithGoogle(onSuccess: () -> Unit) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.loginWithGoogle().fold(
                onSuccess = { data ->
                    _uiState.value = AuthUiState.Success(data)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.message ?: "Google login failed")
                }
            )
        }
    }

    fun personalize(goal: String, level: String, dailyMinutes: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.personalize(goal, level, dailyMinutes)
            onComplete()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = AuthUiState.Idle
            onComplete()
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}

