package com.example.myapplication.data.model.catalogue

/**
 * Categorizes educational blocks within a structured DSA lesson.
 * Designed to map cleanly to future PHP/MySQL lesson_sections tables.
 */
enum class LessonSectionType(val badgeLabel: String) {
    INTRODUCTION("INTRODUCTION"),
    CONCEPT_EXPLANATION("CORE CONCEPT"),
    USE_CASES("WHERE IT'S USED"),
    REAL_WORLD_EXAMPLES("REAL-WORLD ANALOGY"),
    PERFORMANCE_IMPACT("PERFORMANCE & COMPLEXITY"),
    KEY_TAKEAWAYS("KEY TAKEAWAYS")
}

/**
 * Explains a specific line or logical block within a lesson code example.
 */
data class CodeStepExplanation(
    val stepNumber: Int,
    val lineReference: String,
    val explanation: String
)

/**
 * Code snippet paired with a beginner-friendly step-by-step walkthrough.
 */
data class LessonCodeBlock(
    val title: String,
    val language: String,
    val code: String,
    val stepByStepExplanation: List<CodeStepExplanation>
)

/**
 * Individual educational section inside a lesson screen.
 */
data class LessonSection(
    val id: String,
    val type: LessonSectionType,
    val title: String,
    val body: String,
    val bulletPoints: List<String> = emptyList(),
    val highlightNote: String? = null
)

/**
 * Interactive practice question / quiz embedded directly within a lesson.
 */
data class LessonPracticeQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

/**
 * Complete, structured educational content for a single DSA lesson.
 *
 * Uses stable string IDs linked to DsaSubtopic.id and optional integer IDs
 * (`backendLessonId` / `backendTopicId`) so future PHP/MySQL syncing can
 * plug in without changing UI components or topic catalogue IDs.
 */
data class DsaLessonContent(
    val lessonId: String,
    val subtopicId: String,
    val lessonNumber: Int,
    val title: String,
    val subtitle: String,
    val estimatedMinutes: Int,
    val xpReward: Int,
    val timeComplexity: String? = null,
    val spaceComplexity: String? = null,
    val backendLessonId: Int? = null,
    val backendTopicId: Int? = null,
    val learningObjectives: List<String> = emptyList(),
    val sections: List<LessonSection>,
    val codeExamples: List<LessonCodeBlock> = emptyList(),
    val keyTakeaways: List<String> = emptyList(),
    val practiceQuestions: List<LessonPracticeQuestion> = emptyList()
)

/**
 * Outline entry representing a lesson/module inside a DSA subtopic.
 * Enables listing lessons and prev/next navigation even for topics
 * whose full lesson prose is still in development.
 */
data class DsaLessonOutline(
    val lessonId: String,
    val subtopicId: String,
    val lessonNumber: Int,
    val title: String,
    val duration: String,
    val xpReward: Int,
    val lessonType: String, // "THEORY", "VISUALIZER", "QUIZ"
    val hasContent: Boolean,
    val visualizerSlug: String? = null,
    val quizLessonId: Int? = null,
    val backendLessonId: Int? = null
)

/**
 * Computed lesson completion progress for a single DSA subtopic.
 */
data class TopicLessonProgress(
    val completedCount: Int,
    val totalCount: Int,
    val progressPercentage: Int
)

/**
 * Result returned after attempting to mark a catalogue lesson complete.
 */
data class LessonCompletionResult(
    val lessonId: String,
    val isCompleted: Boolean,
    val xpAwarded: Int,
    val syncedWithBackend: Boolean,
    val message: String
)
