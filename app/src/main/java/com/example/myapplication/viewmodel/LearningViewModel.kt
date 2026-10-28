package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.TopicDetailData
import com.example.myapplication.data.model.response.TopicItemDto
import com.example.myapplication.data.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TopicsUiState {
    object Loading : TopicsUiState()
    data class Success(val topics: List<TopicItemDto>) : TopicsUiState()
    data class Error(val message: String) : TopicsUiState()
}

sealed class TopicDetailUiState {
    object Loading : TopicDetailUiState()
    data class Success(val data: TopicDetailData) : TopicDetailUiState()
    data class Error(val message: String) : TopicDetailUiState()
}

class LearningViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LearningRepository(application)

    private val _topicsState = MutableStateFlow<TopicsUiState>(TopicsUiState.Loading)
    val topicsState: StateFlow<TopicsUiState> = _topicsState.asStateFlow()

    private val _detailState = MutableStateFlow<TopicDetailUiState>(TopicDetailUiState.Loading)
    val detailState: StateFlow<TopicDetailUiState> = _detailState.asStateFlow()

    fun loadTopics() {
        _topicsState.value = TopicsUiState.Loading
        viewModelScope.launch {
            repository.getTopics().fold(
                onSuccess = { list -> _topicsState.value = TopicsUiState.Success(list) },
                onFailure = { err -> _topicsState.value = TopicsUiState.Error(err.message ?: "Failed to load topics") }
            )
        }
    }

    fun loadTopicDetail(topicId: Int) {
        _detailState.value = TopicDetailUiState.Loading
        viewModelScope.launch {
            repository.getTopicDetail(topicId).fold(
                onSuccess = { data -> _detailState.value = TopicDetailUiState.Success(data) },
                onFailure = { err -> _detailState.value = TopicDetailUiState.Error(err.message ?: "Failed to load topic") }
            )
        }
    }

    fun completeLesson(lessonId: Int, topicId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.completeLesson(lessonId).onSuccess {
                loadTopicDetail(topicId)
                onComplete()
            }
        }
    }
}

