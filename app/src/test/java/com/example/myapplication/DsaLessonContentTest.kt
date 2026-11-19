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
    fun testAll65CatalogueSubtopicsHaveImplementedLessons() {
        val allSubtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        assertEquals("DSA Catalogue must contain exactly 65 subtopics", 65, allSubtopics.size)

        val allCategories = DsaTopicCatalogueRepository.getAllCategories()
        assertEquals("DSA Catalogue must contain exactly 12 categories", 12, allCategories.size)

        for (subtopic in allSubtopics) {
            val expectedLessonId = "${subtopic.id}_l1"
            val lesson = DsaLessonContentRepository.getLessonContent(expectedLessonId)
            assertNotNull("Lesson for subtopic '${subtopic.id}' (${subtopic.name}) must be implemented", lesson)

            assertEquals("Lesson subtopicId must match catalogue subtopic id", subtopic.id, lesson!!.subtopicId)
            assertEquals("Default catalogue lesson should be lesson number 1", 1, lesson.lessonNumber)
            assertTrue("Lesson title must not be blank", lesson.title.isNotBlank())
            assertTrue("Lesson subtitle must not be blank", lesson.subtitle.isNotBlank())
            assertTrue("Estimated minutes must be positive", lesson.estimatedMinutes > 0)
            assertTrue("XP reward must be positive", lesson.xpReward > 0)

            // Learning objectives
            assertTrue(
                "Subtopic '${subtopic.id}' must have learning objectives",
                lesson.learningObjectives.isNotEmpty()
            )

            // Structured educational sections
            assertTrue(
                "Subtopic '${subtopic.id}' must have at least 2 structured sections",
                lesson.sections.size >= 2
            )
            lesson.sections.forEach { section ->
                assertTrue("Section title in '${subtopic.id}' must not be blank", section.title.isNotBlank())
                assertTrue("Section body in '${subtopic.id}' must not be blank", section.body.isNotBlank())
            }

            // Code examples and step-by-step walkthroughs
            assertTrue(
                "Subtopic '${subtopic.id}' must contain code examples",
                lesson.codeExamples.isNotEmpty()
            )
            lesson.codeExamples.forEach { codeBlock ->
                assertTrue("Code in '${subtopic.id}' must not be blank", codeBlock.code.isNotBlank())
                assertTrue(
                    "Code block '${codeBlock.title}' in '${subtopic.id}' must have step explanations",
                    codeBlock.stepByStepExplanation.isNotEmpty()
                )
            }

            // Key takeaways
            assertTrue(
                "Subtopic '${subtopic.id}' must have key takeaways",
                lesson.keyTakeaways.isNotEmpty()
            )

            // Interactive Practice Questions
            assertTrue(
                "Subtopic '${subtopic.id}' must have practice questions",
                lesson.practiceQuestions.isNotEmpty()
            )
            lesson.practiceQuestions.forEach { pq ->
                assertTrue("Question in '${subtopic.id}' must not be blank", pq.question.isNotBlank())
                assertTrue("Question '${pq.id}' must have at least 2 options", pq.options.size >= 2)
                assertTrue(
                    "Correct option index must be within options range",
                    pq.correctOptionIndex in pq.options.indices
                )
                assertTrue("Explanation must not be blank", pq.explanation.isNotBlank())
            }
        }
    }

    @Test
    fun testAll12CategoriesRepresentedInImplementedLessons() {
        val allLessons = DsaLessonContentRepository.getAllLessons()
        assertEquals("Total implemented lessons across catalogue must be 65", 65, allLessons.size)

        val categories = DsaTopicCatalogueRepository.getAllCategories()
        for (category in categories) {
            val subtopicsInCategory = DsaTopicCatalogueRepository.getSubtopicsForCategory(category.id)
            assertTrue("Category '${category.name}' must have subtopics", subtopicsInCategory.isNotEmpty())

            for (sub in subtopicsInCategory) {
                assertTrue(
                    "Lesson content for '${sub.id}' must exist",
                    DsaLessonContentRepository.hasImplementedContent("${sub.id}_l1")
                )
            }
        }
    }

    @Test
    fun testIntroToDataStructuresLesson01HasAllRequiredEducationalSections() {
        val lesson = DsaLessonContentRepository.getLessonContent(DsaLessonContentRepository.LESSON_INTRO_DS_01_ID)!!

        assertTrue("Lesson must have multiple structured sections", lesson.sections.size >= 4)

        val types = lesson.sections.map { it.type }.toSet()
        assertTrue("Must contain INTRODUCTION section", types.contains(LessonSectionType.INTRODUCTION))
        assertTrue("Must contain CONCEPT_EXPLANATION section", types.contains(LessonSectionType.CONCEPT_EXPLANATION))
        assertTrue("Must contain REAL_WORLD_EXAMPLES section", types.contains(LessonSectionType.REAL_WORLD_EXAMPLES))
        assertTrue("Must contain PERFORMANCE_IMPACT section", types.contains(LessonSectionType.PERFORMANCE_IMPACT))

        assertTrue("Must contain code examples", lesson.codeExamples.isNotEmpty())
        assertTrue("Must contain key takeaways", lesson.keyTakeaways.size >= 4)
        assertTrue("Must contain practice questions", lesson.practiceQuestions.size >= 2)
    }

    @Test
    fun testAllSubtopicOutlinesMarkTheoryReady() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        for (sub in subtopics) {
            val outlines = DsaLessonContentRepository.getLessonOutlinesForSubtopic(sub)
            assertTrue("Outlines must not be empty for ${sub.id}", outlines.isNotEmpty())
            val firstTheory = outlines.first { it.lessonType == "THEORY" }
            assertTrue(
                "Theory module for '${sub.id}' must have hasContent = true",
                firstTheory.hasContent
            )
        }
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
