package com.example.myapplication.data.model.catalogue

/**
 * Difficulty levels for DSA topics
 */
enum class TopicDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

/**
 * DSA Category containing multiple subtopics
 */
data class DsaCategory(
    val id: String,
    val orderIndex: Int,
    val name: String,
    val description: String,
    val iconType: String
)

/**
 * Individual DSA Subtopic with unique stable ID, difficulty and feature flags
 */
data class DsaSubtopic(
    val id: String,
    val categoryId: String,
    val orderIndex: Int,
    val name: String,
    val description: String,
    val difficulty: TopicDifficulty,
    val hasLearningContent: Boolean = true,
    val hasVisualization: Boolean = false,
    val visualizerSlug: String? = null,
    val hasQuiz: Boolean = false,
    val quizLessonId: Int? = null,
    val estimatedMinutes: Int = 10,
    val xpReward: Int = 100,
    val contentSummary: String = "",
    val timeComplexity: String = "O(N)",
    val spaceComplexity: String = "O(1)"
) {
    val title: String get() = name
    val summary: String get() = if (contentSummary.isNotEmpty()) contentSummary else description
    val hasVisualizer: Boolean get() = hasVisualization
    val quizId: Int? get() = quizLessonId
}
