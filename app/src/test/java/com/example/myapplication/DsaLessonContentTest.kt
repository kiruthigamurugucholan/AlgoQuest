package com.example.myapplication

import com.example.myapplication.data.model.catalogue.LessonSectionType
import com.example.myapplication.data.repository.DsaLessonContentRepository
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DsaLessonContentTest {

    @Test
    fun testIntroToDataStructuresLesson01ExistsAndMatchesCatalogueId() {
        val subtopic = DsaTopicCatalogueRepository.getSubtopicById("prog_intro_ds")
        assertNotNull("Subtopic prog_intro_ds must exist unchanged in catalogue", subtopic)

        val lesson = DsaLessonContentRepository.getLessonContent(DsaLessonContentRepository.LESSON_INTRO_DS_01_ID)
        assertNotNull("Lesson 01 for Introduction to Data Structures must be implemented", lesson)
        assertEquals("prog_intro_ds", lesson!!.subtopicId)
        assertEquals(1, lesson.lessonNumber)
        assertTrue(lesson.title.contains("Introduction to Data Structures"))
        assertTrue(DsaLessonContentRepository.hasImplementedContent(lesson.lessonId))
    }

    @Test
    fun testIntroToDataStructuresLesson01HasAllRequiredEducationalSections() {
        val lesson = DsaLessonContentRepository.getLessonContent(DsaLessonContentRepository.LESSON_INTRO_DS_01_ID)!!

        assertTrue("Lesson must have multiple structured sections", lesson.sections.size >= 6)

        val types = lesson.sections.map { it.type }.toSet()
        assertTrue("Must contain INTRODUCTION section", types.contains(LessonSectionType.INTRODUCTION))
        assertTrue("Must contain CONCEPT_EXPLANATION section", types.contains(LessonSectionType.CONCEPT_EXPLANATION))
        assertTrue("Must contain USE_CASES section", types.contains(LessonSectionType.USE_CASES))
        assertTrue("Must contain REAL_WORLD_EXAMPLES section", types.contains(LessonSectionType.REAL_WORLD_EXAMPLES))
        assertTrue("Must contain PERFORMANCE_IMPACT section", types.contains(LessonSectionType.PERFORMANCE_IMPACT))

        assertTrue("Must contain at least one code example", lesson.codeExamples.isNotEmpty())
        lesson.codeExamples.forEach { block ->
            assertTrue("Code block must contain non-empty code", block.code.isNotBlank())
            assertTrue(
                "Code block '${block.title}' must contain step-by-step explanations",
                block.stepByStepExplanation.isNotEmpty()
            )
        }

        assertTrue("Must contain key takeaways", lesson.keyTakeaways.size >= 4)
    }

    @Test
    fun testUnimplementedTopicsReturnComingSoonState() {
        val unimplementedSubtopicIds = listOf("prog_intro_algo", "tree_avl", "adv_dp", "graph_dijkstra")

        for (subId in unimplementedSubtopicIds) {
            val subtopic = DsaTopicCatalogueRepository.getSubtopicById(subId)
            assertNotNull("Subtopic $subId must exist in catalogue", subtopic)

            val outlines = DsaLessonContentRepository.getLessonOutlinesForSubtopic(subtopic!!)
            assertTrue("Subtopic $subId must still produce lesson outlines", outlines.isNotEmpty())

            val firstTheory = outlines.first()
            assertFalse("Unimplemented subtopic $subId must not claim theory content is ready", firstTheory.hasContent)
            assertNull(
                "getLessonContent must return null for unimplemented lesson ${firstTheory.lessonId}",
                DsaLessonContentRepository.getLessonContent(firstTheory.lessonId)
            )
        }
    }

    @Test
    fun testLessonOutlinesAndNavigationForIntroToDataStructures() {
        val subtopic = DsaTopicCatalogueRepository.getSubtopicById("prog_intro_ds")!!
        val outlines = DsaLessonContentRepository.getLessonOutlinesForSubtopic(subtopic)

        assertEquals(3, outlines.size)
        assertEquals(DsaLessonContentRepository.LESSON_INTRO_DS_01_ID, outlines[0].lessonId)
        assertTrue(outlines[0].hasContent)
        assertEquals("THEORY", outlines[0].lessonType)

        assertEquals("VISUALIZER", outlines[1].lessonType)
        assertFalse(outlines[1].hasContent)

        assertEquals("QUIZ", outlines[2].lessonType)
        assertFalse(outlines[2].hasContent)

        // Prev / Next boundary checks
        assertNull(DsaLessonContentRepository.getPreviousLessonOutline(subtopic, 1))
        assertEquals(outlines[1].lessonId, DsaLessonContentRepository.getNextLessonOutline(subtopic, 1)?.lessonId)
        assertEquals(outlines[0].lessonId, DsaLessonContentRepository.getPreviousLessonOutline(subtopic, 2)?.lessonId)
        assertEquals(outlines[2].lessonId, DsaLessonContentRepository.getNextLessonOutline(subtopic, 2)?.lessonId)
        assertNull(DsaLessonContentRepository.getNextLessonOutline(subtopic, 3))
    }

    @Test
    fun testBinarySearchOutlinePreservesVisualizerAndQuizMapping() {
        val bs = DsaTopicCatalogueRepository.getSubtopicById("search_binary")!!
        val outlines = DsaLessonContentRepository.getLessonOutlinesForSubtopic(bs)

        val viz = outlines.first { it.lessonType == "VISUALIZER" }
        assertTrue(viz.hasContent)
        assertEquals("binary-search", viz.visualizerSlug)

        val quiz = outlines.first { it.lessonType == "QUIZ" }
        assertTrue(quiz.hasContent)
        assertEquals(5, quiz.quizLessonId)
    }

    @Test
    fun testTopicProgressCalculationUpdatesAccurately() {
        val subtopic = DsaTopicCatalogueRepository.getSubtopicById("prog_intro_ds")!!

        val initial = DsaLessonContentRepository.calculateTopicProgress(subtopic, emptySet())
        assertEquals(0, initial.completedCount)
        assertEquals(3, initial.totalCount)
        assertEquals(0, initial.progressPercentage)

        val afterLesson1 = DsaLessonContentRepository.calculateTopicProgress(
            subtopic,
            setOf(DsaLessonContentRepository.LESSON_INTRO_DS_01_ID)
        )
        assertEquals(1, afterLesson1.completedCount)
        assertEquals(3, afterLesson1.totalCount)
        assertEquals(33, afterLesson1.progressPercentage)

        val allCompleted = DsaLessonContentRepository.calculateTopicProgress(
            subtopic,
            setOf("prog_intro_ds_l1", "prog_intro_ds_viz", "prog_intro_ds_quiz")
        )
        assertEquals(3, allCompleted.completedCount)
        assertEquals(100, allCompleted.progressPercentage)
    }
}
