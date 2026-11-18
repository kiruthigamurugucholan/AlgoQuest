package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.response.LearningResourceDto
import com.example.myapplication.data.model.response.LearningResourceTopicDto
import com.example.myapplication.data.repository.LearningResourceRepository
import com.example.myapplication.data.repository.ResourceNotFoundException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LearningResourceUiState {
    object Idle : LearningResourceUiState()
    data class Loading(val topicLabel: String) : LearningResourceUiState()
    data class Success(val resource: LearningResourceDto, val cached: Boolean) : LearningResourceUiState()
    data class Empty(val query: String) : LearningResourceUiState()
    data class Error(val message: String, val failedTopic: String) : LearningResourceUiState()
}

class LearningResourceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LearningResourceRepository(application)

    private val _topics = MutableStateFlow(LearningResourceRepository.defaultSupportedTopics)
    val topics: StateFlow<List<LearningResourceTopicDto>> = _topics.asStateFlow()

    private val _categories = MutableStateFlow(LearningResourceRepository.defaultCategories)
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedTopicSlug = MutableStateFlow<String?>(null)
    val selectedTopicSlug: StateFlow<String?> = _selectedTopicSlug.asStateFlow()

    private val _uiState = MutableStateFlow<LearningResourceUiState>(LearningResourceUiState.Idle)
    val uiState: StateFlow<LearningResourceUiState> = _uiState.asStateFlow()

    private var lastRequestedTopic: String? = null

    fun loadTopics() {
        viewModelScope.launch {
            repository.getSupportedTopics().onSuccess { data ->
                if (data.topics.isNotEmpty()) {
                    _topics.value = data.topics
                }
                if (data.categories.isNotEmpty()) {
                    _categories.value = data.categories
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            val matches = filterTopics(_topics.value, trimmed, _selectedCategory.value)
            if (matches.isEmpty() && _uiState.value !is LearningResourceUiState.Loading) {
                _uiState.value = LearningResourceUiState.Empty(trimmed)
            } else if (_uiState.value is LearningResourceUiState.Empty) {
                _uiState.value = LearningResourceUiState.Idle
            }
        } else if (_uiState.value is LearningResourceUiState.Empty) {
            _uiState.value = LearningResourceUiState.Idle
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun getFilteredTopics(): List<LearningResourceTopicDto> {
        return filterTopics(_topics.value, _searchQuery.value, _selectedCategory.value)
    }

    fun fetchResource(topicSlugOrName: String, forceRefresh: Boolean = false) {
        val trimmed = topicSlugOrName.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = LearningResourceUiState.Empty("")
            return
        }

        val matchedTopic = findMatchingTopic(trimmed)
        val requestKey = matchedTopic?.slug ?: LearningResourceRepository.normalizeTopicQuery(trimmed)
        val displayLabel = matchedTopic?.name ?: trimmed

        lastRequestedTopic = requestKey
        _selectedTopicSlug.value = requestKey
        _uiState.value = LearningResourceUiState.Loading(displayLabel)

        viewModelScope.launch {
            repository.getResourceForTopic(requestKey, forceRefresh = forceRefresh).fold(
                onSuccess = { data ->
                    val res = data.resource
                    if (res.title.isBlank() && res.description.isBlank() && res.content.isEmpty()) {
                        _uiState.value = LearningResourceUiState.Empty(displayLabel)
                    } else {
                        _uiState.value = LearningResourceUiState.Success(
                            resource = res,
                            cached = data.cached || res.fromCache
                        )
                    }
                },
                onFailure = { err ->
                    if (err is ResourceNotFoundException) {
                        _uiState.value = LearningResourceUiState.Empty(displayLabel)
                    } else {
                        _uiState.value = LearningResourceUiState.Error(
                            message = err.message ?: "Unable to retrieve learning resource.",
                            failedTopic = requestKey
                        )
                    }
                }
            )
        }
    }

    fun submitSearch() {
        val q = _searchQuery.value.trim()
        if (q.isEmpty()) return

        val match = findMatchingTopic(q)
        if (match != null) {
            fetchResource(match.slug)
        } else {
            val filtered = filterTopics(_topics.value, q, null)
            if (filtered.isNotEmpty()) {
                fetchResource(filtered.first().slug)
            } else {
                _uiState.value = LearningResourceUiState.Empty(q)
            }
        }
    }

    fun retry() {
        val target = lastRequestedTopic ?: _selectedTopicSlug.value ?: _searchQuery.value.trim()
        if (target.isNotEmpty()) {
            fetchResource(target, forceRefresh = false)
        }
    }

    fun refreshCurrent() {
        val target = lastRequestedTopic ?: _selectedTopicSlug.value
        if (!target.isNullOrBlank()) {
            fetchResource(target, forceRefresh = true)
        }
    }

    fun clearState() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedTopicSlug.value = null
        lastRequestedTopic = null
        _uiState.value = LearningResourceUiState.Idle
    }

    private fun findMatchingTopic(query: String): LearningResourceTopicDto? {
        val norm = LearningResourceRepository.normalizeTopicQuery(query)
        val lower = query.trim().lowercase()
        return _topics.value.firstOrNull { topic ->
            topic.slug == norm ||
                topic.name.lowercase() == lower ||
                LearningResourceRepository.normalizeTopicQuery(topic.name) == norm ||
                topic.name.lowercase().contains(lower)
        }
    }

    companion object {
        fun filterTopics(
            allTopics: List<LearningResourceTopicDto>,
            query: String,
            category: String?
        ): List<LearningResourceTopicDto> {
            val q = query.trim().lowercase()
            return allTopics.filter { topic ->
                val matchesCategory = category == null || topic.category.equals(category, ignoreCase = true)
                val matchesQuery = q.isEmpty() ||
                    topic.name.lowercase().contains(q) ||
                    topic.slug.lowercase().contains(q) ||
                    topic.category.lowercase().contains(q)
                matchesCategory && matchesQuery
            }
        }
    }
}
