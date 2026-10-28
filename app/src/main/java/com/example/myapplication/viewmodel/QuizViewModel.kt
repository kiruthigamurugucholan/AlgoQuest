package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.request.QuizAnswerItem
import com.example.myapplication.data.model.response.QuizDetailsDto
import com.example.myapplication.data.model.response.QuizResultData
import com.example.myapplication.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuizUiState {
    object Idle : QuizUiState()
    object Loading : QuizUiState()
    data class Loaded(
        val quiz: QuizDetailsDto,
        val selectedAnswers: Map<Int, Int> = emptyMap(),
        val isSubmitting: Boolean = false
    ) : QuizUiState()
    data class Result(val result: QuizResultData) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(application)

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Idle)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuiz(lessonId: Int) {
        _uiState.value = QuizUiState.Loading
        viewModelScope.launch {
            repository.getQuiz(lessonId).fold(
                onSuccess = { quiz ->
                    _uiState.value = QuizUiState.Loaded(quiz = quiz)
                },
                onFailure = { err ->
                    _uiState.value = QuizUiState.Error(err.message ?: "Failed to load quiz")
                }
            )
        }
    }

    fun selectOption(questionId: Int, optionId: Int) {
        val current = _uiState.value
        if (current is QuizUiState.Loaded) {
            val updated = current.selectedAnswers.toMutableMap()
            updated[questionId] = optionId
            _uiState.value = current.copy(selectedAnswers = updated)
        }
    }

    fun submitQuiz(quizId: Int) {
        val current = _uiState.value
        if (current is QuizUiState.Loaded) {
            _uiState.value = current.copy(isSubmitting = true)
            viewModelScope.launch {
                val answersList = current.selectedAnswers.map { (qId, optId) ->
                    QuizAnswerItem(qId, optId)
                }
                repository.submitQuiz(quizId, answersList).fold(
                    onSuccess = { resultData ->
                        _uiState.value = QuizUiState.Result(resultData)
                    },
                    onFailure = { err ->
                        _uiState.value = QuizUiState.Error(err.message ?: "Failed to submit quiz")
                    }
                )
            }
        }
    }

    fun reset() {
        _uiState.value = QuizUiState.Idle
    }
}

