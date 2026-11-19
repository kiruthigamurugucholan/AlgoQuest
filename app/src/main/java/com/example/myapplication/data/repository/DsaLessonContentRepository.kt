package com.example.myapplication.data.repository

import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.DsaLessonOutline
import com.example.myapplication.data.model.catalogue.DsaSubtopic
import com.example.myapplication.data.model.catalogue.TopicLessonProgress
import com.example.myapplication.data.repository.catalogue.AdvancedAlgorithmLessons
import com.example.myapplication.data.repository.catalogue.ArrayLessons
import com.example.myapplication.data.repository.catalogue.GraphLessons
import com.example.myapplication.data.repository.catalogue.LinkedListLessons
import com.example.myapplication.data.repository.catalogue.ProgrammingBasicsLessons
import com.example.myapplication.data.repository.catalogue.QueueLessons
import com.example.myapplication.data.repository.catalogue.RecursionLessons
import com.example.myapplication.data.repository.catalogue.SearchingLessons
import com.example.myapplication.data.repository.catalogue.SortingLessons
import com.example.myapplication.data.repository.catalogue.StackLessons
import com.example.myapplication.data.repository.catalogue.StringLessons
import com.example.myapplication.data.repository.catalogue.TreeLessons

/**
 * Centralized repository for structured DSA lesson content across all 65 catalogue topics.
 *
 * Implemented across 12 modular category lesson providers under data.repository.catalogue.
 * Every subtopic has an authored theory lesson (id = "${subtopicId}_l1") complete with
 * learning objectives, rich theory sections, code walkthroughs, complexity analysis,
 * key takeaways, and interactive practice questions.
 */
object DsaLessonContentRepository {

    const val LESSON_INTRO_DS_01_ID = "prog_intro_ds_l1"

    private val implementedLessons: List<DsaLessonContent> = listOf(
        ProgrammingBasicsLessons.lessons,
        ArrayLessons.lessons,
        SearchingLessons.lessons,
        SortingLessons.lessons,
        StringLessons.lessons,
        LinkedListLessons.lessons,
        StackLessons.lessons,
        QueueLessons.lessons,
        RecursionLessons.lessons,
        TreeLessons.lessons,
        GraphLessons.lessons,
        AdvancedAlgorithmLessons.lessons
    ).flatten()

    private val lessonsById: Map<String, DsaLessonContent> =
        implementedLessons.associateBy { it.lessonId }

    private val lessonsBySubtopic: Map<String, List<DsaLessonContent>> =
        implementedLessons.groupBy { it.subtopicId }
            .mapValues { (_, list) -> list.sortedBy { it.lessonNumber } }

    /**
     * Returns all 65 implemented lessons.
     */
    fun getAllLessons(): List<DsaLessonContent> = implementedLessons

    /**
     * Returns full structured lesson content if implemented, or null if coming soon.
     */
    fun getLessonContent(lessonId: String): DsaLessonContent? = lessonsById[lessonId]

    /**
     * Returns all authored structured lessons for a subtopic.
     */
    fun getImplementedLessonsForSubtopic(subtopicId: String): List<DsaLessonContent> =
        lessonsBySubtopic[subtopicId] ?: emptyList()

    /**
     * Returns true strictly when full structured educational content exists for this lesson.
     */
    fun hasImplementedContent(lessonId: String): Boolean = lessonsById.containsKey(lessonId)

    /**
     * Builds the ordered lesson outline for any subtopic in the 65-topic catalogue.
     * Uses authored DsaLessonContent when available.
     */
    fun getLessonOutlinesForSubtopic(subtopic: DsaSubtopic): List<DsaLessonOutline> {
        val outlines = mutableListOf<DsaLessonOutline>()
        val authoredTheoryLessons = getImplementedLessonsForSubtopic(subtopic.id)

        if (authoredTheoryLessons.isNotEmpty()) {
            authoredTheoryLessons.forEach { lesson ->
                outlines.add(
                    DsaLessonOutline(
                        lessonId = lesson.lessonId,
                        subtopicId = subtopic.id,
                        lessonNumber = lesson.lessonNumber,
                        title = lesson.title,
                        duration = "${lesson.estimatedMinutes} min",
                        xpReward = lesson.xpReward,
                        lessonType = "THEORY",
                        hasContent = true,
                        backendLessonId = lesson.backendLessonId
                    )
                )
            }
        } else {
            outlines.add(
                DsaLessonOutline(
                    lessonId = "${subtopic.id}_l1",
                    subtopicId = subtopic.id,
                    lessonNumber = 1,
                    title = "Core Theory & Invariants – ${subtopic.name}",
                    duration = "${(subtopic.estimatedMinutes * 0.4).toInt().coerceAtLeast(3)} min",
                    xpReward = subtopic.xpReward / 2,
                    lessonType = "THEORY",
                    hasContent = false
                )
            )
        }

        val nextNumberAfterTheory = outlines.size + 1

        // Module 2: Interactive Visualizer Lab
        val canViz = subtopic.hasVisualization && !subtopic.visualizerSlug.isNullOrEmpty()
        outlines.add(
            DsaLessonOutline(
                lessonId = "${subtopic.id}_viz",
                subtopicId = subtopic.id,
                lessonNumber = nextNumberAfterTheory,
                title = if (canViz) "Step Through Visualizer Lab" else "Interactive Visualizer Lab",
                duration = "${(subtopic.estimatedMinutes * 0.6).toInt().coerceAtLeast(5)} min",
                xpReward = subtopic.xpReward / 2,
                lessonType = "VISUALIZER",
                hasContent = canViz,
                visualizerSlug = subtopic.visualizerSlug
            )
        )

        // Module 3: Topic Mastery Quiz
        val canQuiz = subtopic.hasQuiz && subtopic.quizLessonId != null
        outlines.add(
            DsaLessonOutline(
                lessonId = "${subtopic.id}_quiz",
                subtopicId = subtopic.id,
                lessonNumber = nextNumberAfterTheory + 1,
                title = if (canQuiz) "Topic Mastery Quiz & Test" else "Topic Mastery Quiz",
                duration = "5 min",
                xpReward = 50,
                lessonType = "QUIZ",
                hasContent = canQuiz,
                quizLessonId = subtopic.quizLessonId
            )
        )

        return outlines
    }

    fun getLessonOutlineById(subtopic: DsaSubtopic, lessonId: String): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic).firstOrNull { it.lessonId == lessonId }
    }

    fun getPreviousLessonOutline(subtopic: DsaSubtopic, currentLessonNumber: Int): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic)
            .firstOrNull { it.lessonNumber == currentLessonNumber - 1 }
    }

    fun getNextLessonOutline(subtopic: DsaSubtopic, currentLessonNumber: Int): DsaLessonOutline? {
        return getLessonOutlinesForSubtopic(subtopic)
            .firstOrNull { it.lessonNumber == currentLessonNumber + 1 }
    }

    fun calculateTopicProgress(
        subtopic: DsaSubtopic,
        completedLessonIds: Set<String>
    ): TopicLessonProgress {
        val outlines = getLessonOutlinesForSubtopic(subtopic)
        val total = outlines.size
        val completed = outlines.count { completedLessonIds.contains(it.lessonId) }
        val percentage = if (total > 0) ((completed * 100) / total) else 0
        return TopicLessonProgress(
            completedCount = completed,
            totalCount = total,
            progressPercentage = percentage
        )
    }
}
