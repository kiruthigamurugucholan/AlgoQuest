package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.AiMessageDto
import com.example.myapplication.data.repository.AiChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiChatUiState(
    val messages: List<AiMessageDto> = emptyList(),
    val isThinking: Boolean = false,
    val error: String? = null
)

class AiChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiChatRepository(application)

    private val _uiState = MutableStateFlow(
        AiChatUiState(
            messages = listOf(
                AiMessageDto(
                    id = "init_1",
                    sender = "ai",
                    text = "Hey explorer! I'm Quest AI, your personal Data Structures & Algorithms ally.\n\nAsk me anything — from binary search invariants to two-pointer techniques!",
                    codeSnippet = null,
                    timestamp = "Online",
                    tags = listOf("AI Tutor", "DSA Ready")
                )
            )
        )
    )
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            repository.getHistory().onSuccess { historyList ->
                if (historyList.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(messages = historyList)
                }
            }
        }
    }

    fun sendMessage(query: String) {
        val userMsg = AiMessageDto(
            id = "user_${System.currentTimeMillis()}",
            sender = "user",
            text = query,
            codeSnippet = null,
            timestamp = "Just now"
        )
        val updated = _uiState.value.messages.toMutableList().apply { add(userMsg) }
        _uiState.value = _uiState.value.copy(messages = updated, isThinking = true, error = null)

        viewModelScope.launch {
            repository.sendMessage(query).fold(
                onSuccess = { aiMsg ->
                    val withAi = _uiState.value.messages.toMutableList().apply { add(aiMsg) }
                    _uiState.value = _uiState.value.copy(messages = withAi, isThinking = false)
                },
                onFailure = { err ->
                    // Fallback to local offline explanation if backend is temporarily unreachable
                    val fallbackAi = AiMessageDto(
                        id = "ai_${System.currentTimeMillis()}",
                        sender = "ai",
                        text = "Great question! Binary search operates by repeatedly dividing a sorted interval in half. Each step eliminates 50% of candidate items, maintaining an O(log N) time bound.",
                        codeSnippet = "val mid = low + (high - low) / 2",
                        timestamp = "Offline Mode",
                        tags = listOf("DSA Concept", "O(log N)")
                    )
                    val withFallback = _uiState.value.messages.toMutableList().apply { add(fallbackAi) }
                    _uiState.value = _uiState.value.copy(messages = withFallback, isThinking = false)
                }
            )
        }
    }
}

