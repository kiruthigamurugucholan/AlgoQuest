package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCompletionResult
import com.example.myapplication.data.model.response.TopicDetailData
import com.example.myapplication.data.model.response.TopicItemDto
import com.example.myapplication.data.remote.TokenManager
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
    private val cataloguePrefs = application.getSharedPreferences(
        "algoquest_catalogue_progress",
        Context.MODE_PRIVATE
    )

    private val _topicsState = MutableStateFlow<TopicsUiState>(TopicsUiState.Loading)
    val topicsState: StateFlow<TopicsUiState> = _topicsState.asStateFlow()

    private val _detailState = MutableStateFlow<TopicDetailUiState>(TopicDetailUiState.Loading)
    val detailState: StateFlow<TopicDetailUiState> = _detailState.asStateFlow()

    private val _completedCatalogueLessonIds = MutableStateFlow<Set<String>>(emptySet())
    val completedCatalogueLessonIds: StateFlow<Set<String>> = _completedCatalogueLessonIds.asStateFlow()

    private val _isCompletingCatalogueLesson = MutableStateFlow(false)
    val isCompletingCatalogueLesson: StateFlow<Boolean> = _isCompletingCatalogueLesson.asStateFlow()

    private val _catalogueCompletionResult = MutableStateFlow<LessonCompletionResult?>(null)
    val catalogueCompletionResult: StateFlow<LessonCompletionResult?> = _catalogueCompletionResult.asStateFlow()

    init {
        loadCatalogueProgress()
    }

    private fun cataloguePrefsKey(): String {
        val userId = TokenManager.getInstance(getApplication()).getUserId() ?: "guest"
        return "completed_catalogue_lessons_$userId"
    }

    fun loadCatalogueProgress() {
        val saved = cataloguePrefs.getStringSet(cataloguePrefsKey(), emptySet()) ?: emptySet()
        _completedCatalogueLessonIds.value = saved.toSet()
        viewModelScope.launch {
            repository.getCatalogueProgress().onSuccess { data ->
                val merged = _completedCatalogueLessonIds.value + data.completedLessonIds
                _completedCatalogueLessonIds.value = merged
                cataloguePrefs.edit().putStringSet(cataloguePrefsKey(), merged).apply()
            }
        }
    }

    private fun persistCompletedCatalogueLesson(lessonId: String) {
        val updated = _completedCatalogueLessonIds.value + lessonId
        _completedCatalogueLessonIds.value = updated
        cataloguePrefs.edit().putStringSet(cataloguePrefsKey(), updated).apply()
    }

    fun isCatalogueLessonCompleted(lessonId: String): Boolean {
        return _completedCatalogueLessonIds.value.contains(lessonId)
    }

    fun clearCatalogueCompletionResult() {
        _catalogueCompletionResult.value = null
    }

    fun loadTopics() {
        loadCatalogueProgress()
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
                onSuccess = { data ->
                    _detailState.value = TopicDetailUiState.Success(data)
                    checkAndNotifyAvailableQuizzes(data)
                },
                onFailure = { err -> _detailState.value = TopicDetailUiState.Error(err.message ?: "Failed to load topic") }
            )
        }
    }

    private fun checkAndNotifyAvailableQuizzes(data: TopicDetailData) {
        val prefs = com.example.myapplication.reminder.ReminderPreferences.getInstance(getApplication())
        if (!prefs.isQuizNotificationEnabled()) return

        val availableQuiz = data.lessons.firstOrNull { lesson ->
            lesson.lessonType.equals("QUIZ", ignoreCase = true) && !lesson.isLocked && !lesson.isCompleted
        }

        if (availableQuiz != null) {
            com.example.myapplication.reminder.ReminderScheduler.showQuizNotification(
                context = getApplication(),
                quizLessonId = availableQuiz.id,
                isTest = false
            )
        }
    }

    fun completeLesson(lessonId: Int, topicId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.completeLesson(lessonId).onSuccess {
                com.example.myapplication.reminder.ReminderPreferences.getInstance(getApplication()).markDailyActivityCompleted()
                loadTopicDetail(topicId)
                com.example.myapplication.data.repository.ProfileRepository(getApplication())
                    .getProfile()
                    .onSuccess { profileData ->
                        com.example.myapplication.reminder.ReminderScheduler.checkAndNotifyAchievements(getApplication(), profileData)
                    }
                onComplete()
            }
        }
    }

    /**
     * Marks a structured DSA Catalogue lesson as completed.
     * - If `lesson.backendLessonId` is mapped to the PHP/MySQL `lessons` table, delegates XP
     *   awarding exclusively to the existing server-authoritative `repository.completeLesson` API.
     * - If not yet mapped on the backend, saves completion progress locally per user without
     *   fabricating fake backend XP.
     */
    fun completeCatalogueLesson(
        lesson: DsaLessonContent,
        onSyncedWithBackend: () -> Unit = {},
        onResult: (LessonCompletionResult) -> Unit = {}
    ) {
        if (_isCompletingCatalogueLesson.value) return

        // Idempotent guard: already completed
        if (_completedCatalogueLessonIds.value.contains(lesson.lessonId)) {
            val alreadyDone = LessonCompletionResult(
                lessonId = lesson.lessonId,
                isCompleted = true,
                xpAwarded = 0,
                syncedWithBackend = lesson.backendLessonId != null,
                message = "Lesson already completed. Progress preserved."
            )
            _catalogueCompletionResult.value = alreadyDone
            onResult(alreadyDone)
            return
        }

        val backendLessonId = lesson.backendLessonId
        if (backendLessonId != null) {
            _isCompletingCatalogueLesson.value = true
            viewModelScope.launch {
                repository.completeLesson(backendLessonId).fold(
                    onSuccess = { data ->
                        persistCompletedCatalogueLesson(lesson.lessonId)
                        com.example.myapplication.reminder.ReminderPreferences
                            .getInstance(getApplication())
                            .markDailyActivityCompleted()
                        lesson.backendTopicId?.let { loadTopicDetail(it) }
                        com.example.myapplication.data.repository.ProfileRepository(getApplication())
                            .getProfile()
                            .onSuccess { profileData ->
                                com.example.myapplication.reminder.ReminderScheduler
                                    .checkAndNotifyAchievements(getApplication(), profileData)
                            }
                        val msg = if (data.xpAwarded > 0) {
                            "Lesson completed! +${data.xpAwarded} XP synced to your profile."
                        } else {
                            "Lesson completed! Progress preserved."
                        }
                        val result = LessonCompletionResult(
                            lessonId = lesson.lessonId,
                            isCompleted = true,
                            xpAwarded = data.xpAwarded,
                            syncedWithBackend = true,
                            message = msg
                        )
                        _catalogueCompletionResult.value = result
                        _isCompletingCatalogueLesson.value = false
                        onSyncedWithBackend()
                        onResult(result)
                    },
                    onFailure = { err ->
                        val result = LessonCompletionResult(
                            lessonId = lesson.lessonId,
                            isCompleted = false,
                            xpAwarded = 0,
                            syncedWithBackend = false,
                            message = err.message ?: "Could not sync completion with server. Please try again."
                        )
                        _catalogueCompletionResult.value = result
                        _isCompletingCatalogueLesson.value = false
                        onResult(result)
                    }
                )
            }
        } else {
            // Dedicated catalogue lesson completion synced to PHP / MySQL backend
            _isCompletingCatalogueLesson.value = true
            viewModelScope.launch {
                repository.completeCatalogueLesson(
                    subtopicId = lesson.subtopicId,
                    lessonId = lesson.lessonId,
                    xpReward = lesson.xpReward
                ).fold(
                    onSuccess = { data ->
                        persistCompletedCatalogueLesson(lesson.lessonId)
                        com.example.myapplication.reminder.ReminderPreferences
                            .getInstance(getApplication())
                            .markDailyActivityCompleted()
                        com.example.myapplication.data.repository.ProfileRepository(getApplication())
                            .getProfile()
                            .onSuccess { profileData ->
                                com.example.myapplication.reminder.ReminderScheduler
                                    .checkAndNotifyAchievements(getApplication(), profileData)
                            }
                        val msg = if (data.xpAwarded > 0) {
                            "Lesson completed! +${data.xpAwarded} XP synced to your profile."
                        } else {
                            "Lesson completed! Progress preserved."
                        }
                        val result = LessonCompletionResult(
                            lessonId = lesson.lessonId,
                            isCompleted = true,
                            xpAwarded = data.xpAwarded,
                            syncedWithBackend = true,
                            message = msg
                        )
                        _catalogueCompletionResult.value = result
                        _isCompletingCatalogueLesson.value = false
                        onSyncedWithBackend()
                        onResult(result)
                    },
                    onFailure = {
                        // Offline-resilient fallback: save locally so user progress is never lost
                        persistCompletedCatalogueLesson(lesson.lessonId)
                        com.example.myapplication.reminder.ReminderPreferences
                            .getInstance(getApplication())
                            .markDailyActivityCompleted()
                        val result = LessonCompletionResult(
                            lessonId = lesson.lessonId,
                            isCompleted = true,
                            xpAwarded = 0,
                            syncedWithBackend = false,
                            message = "Lesson saved locally. Will sync with server when back online."
                        )
                        _catalogueCompletionResult.value = result
                        _isCompletingCatalogueLesson.value = false
                        onResult(result)
                    }
                )
            }
        }
    }

    fun clearState() {
        _topicsState.value = TopicsUiState.Loading
        _detailState.value = TopicDetailUiState.Loading
        _completedCatalogueLessonIds.value = emptySet()
        _isCompletingCatalogueLesson.value = false
        _catalogueCompletionResult.value = null
    }
}

