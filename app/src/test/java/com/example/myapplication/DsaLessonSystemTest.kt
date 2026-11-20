package com.example.myapplication

import com.example.myapplication.data.model.catalogue.LessonSectionType
import com.example.myapplication.data.repository.DsaLessonContentRepository
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests verifying the complete AlgoQuest Lesson System across all 65 DSA topics,
 * 12 categories, educational content structures, algorithm steps, Prev/Next navigation,
 * progress tracking, and duplicate prevention.
 */
class DsaLessonSystemTest {

    @Test
    fun testAll65CatalogueSubtopicsHaveCompleteStructuredLessons() {
        val allSubtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        assertEquals("DSA catalogue must contain 65 subtopics", 65, allSubtopics.size)

        for (subtopic in allSubtopics) {
            val lessonId = "${subtopic.id}_l1"
            val lesson = DsaLessonContentRepository.getLessonContent(lessonId)
            assertNotNull("Lesson $lessonId for topic '${subtopic.name}' must exist", lesson)

            assertEquals("Lesson subtopicId must match", subtopic.id, lesson!!.subtopicId)
            assertTrue("Lesson title must not be blank", lesson.title.isNotBlank())
            assertTrue("Lesson subtitle must not be blank", lesson.subtitle.isNotBlank())
            assertTrue("Estimated duration must be positive", lesson.estimatedMinutes > 0)
            assertTrue("XP reward must be positive", lesson.xpReward > 0)

            // Learning objectives
            assertTrue("Lesson '${lesson.title}' must contain learning objectives", lesson.learningObjectives.isNotEmpty())

            // Structured educational sections
            assertTrue("Lesson '${lesson.title}' must contain at least 2 sections", lesson.sections.size >= 2)
            lesson.sections.forEach { section ->
                assertTrue("Section '${section.title}' must have a valid non-empty body", section.body.isNotBlank())
            }

            // Code examples and line-by-line explanation walkthrough
            assertTrue("Lesson '${lesson.title}' must contain code examples", lesson.codeExamples.isNotEmpty())
            lesson.codeExamples.forEach { codeBlock ->
                assertTrue("Code snippet in '${codeBlock.title}' must not be blank", codeBlock.code.isNotBlank())
                assertTrue("Language must not be blank", codeBlock.language.isNotBlank())
                assertTrue(
                    "Code block '${codeBlock.title}' must include step-by-step line walkthroughs",
                    codeBlock.stepByStepExplanation.isNotEmpty()
                )
                codeBlock.stepByStepExplanation.forEach { step ->
                    assertTrue("Step number must be > 0", step.stepNumber > 0)
                    assertTrue("Line reference must not be blank", step.lineReference.isNotBlank())
                    assertTrue("Explanation must not be blank", step.explanation.isNotBlank())
                }
            }

            // Key takeaways
            assertTrue("Lesson '${lesson.title}' must contain key takeaways", lesson.keyTakeaways.isNotEmpty())

            // Practice questions
            assertTrue("Lesson '${lesson.title}' must contain practice questions", lesson.practiceQuestions.isNotEmpty())
            lesson.practiceQuestions.forEach { pq ->
                assertTrue("Practice question '${pq.id}' text must not be blank", pq.question.isNotBlank())
                assertTrue("Practice question '${pq.id}' must have at least 2 options", pq.options.size >= 2)
                assertTrue("Correct option index must be in range", pq.correctOptionIndex in pq.options.indices)
                assertTrue("Explanation must not be blank", pq.explanation.isNotBlank())
            }
        }
    }

    @Test
    fun testLessonsDoNotDefaultToBinarySearchForOtherTopics() {
        val allSubtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        for (subtopic in allSubtopics) {
            val lessonId = "${subtopic.id}_l1"
            val lesson = DsaLessonContentRepository.getLessonContent(lessonId)!!

            if (subtopic.id != "search_binary") {
                assertNotEquals(
                    "Topic ${subtopic.id} must not have 'search_binary' as lesson subtopicId",
                    "search_binary",
                    lesson.subtopicId
                )
                assertFalse(
                    "Topic ${subtopic.id} (${subtopic.name}) must not be titled 'Binary Search Algorithm'",
                    lesson.title.contains("Binary Search Algorithm")
                )
            } else {
                assertEquals("search_binary", lesson.subtopicId)
                assertTrue("Binary search lesson title must identify binary search", lesson.title.contains("Binary Search"))
            }
        }
    }

    @Test
    fun testAlgorithmStepsAndRealWorldAnalogiesPresentInCoreAlgorithms() {
        // Linear Search
        val linearLesson = DsaLessonContentRepository.getLessonContent("search_linear_l1")
        assertNotNull(linearLesson)
        val linearTypes = linearLesson!!.sections.map { it.type }.toSet()
        assertTrue("Linear search must contain REAL_WORLD_EXAMPLES", linearTypes.contains(LessonSectionType.REAL_WORLD_EXAMPLES))
        assertTrue("Linear search must contain ALGORITHM_STEPS", linearTypes.contains(LessonSectionType.ALGORITHM_STEPS))

        val linearSteps = linearLesson.sections.first { it.type == LessonSectionType.ALGORITHM_STEPS }
        assertTrue("Linear search algorithm steps must have numbered bullets", linearSteps.bulletPoints.size >= 4)

        // Binary Search
        val binaryLesson = DsaLessonContentRepository.getLessonContent("search_binary_l1")
        assertNotNull(binaryLesson)
        val binaryTypes = binaryLesson!!.sections.map { it.type }.toSet()
        assertTrue("Binary search must contain REAL_WORLD_EXAMPLES", binaryTypes.contains(LessonSectionType.REAL_WORLD_EXAMPLES))
        assertTrue("Binary search must contain ALGORITHM_STEPS", binaryTypes.contains(LessonSectionType.ALGORITHM_STEPS))

        val binarySteps = binaryLesson.sections.first { it.type == LessonSectionType.ALGORITHM_STEPS }
        assertTrue("Binary search algorithm steps must have numbered bullets", binarySteps.bulletPoints.size >= 4)

        // Bubble Sort
        val bubbleLesson = DsaLessonContentRepository.getLessonContent("sort_bubble_l1")
        assertNotNull(bubbleLesson)
        val bubbleTypes = bubbleLesson!!.sections.map { it.type }.toSet()
        assertTrue("Bubble sort must contain REAL_WORLD_EXAMPLES", bubbleTypes.contains(LessonSectionType.REAL_WORLD_EXAMPLES))
        assertTrue("Bubble sort must contain ALGORITHM_STEPS", bubbleTypes.contains(LessonSectionType.ALGORITHM_STEPS))
    }

    @Test
    fun testNextAndPreviousSubtopicNavigationOrder() {
        val allSubtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        val firstSubtopic = allSubtopics.first()
        val secondSubtopic = allSubtopics[1]
        val lastSubtopic = allSubtopics.last()

        // First topic has no previous subtopic
        assertNull("First subtopic should have no previous subtopic", DsaTopicCatalogueRepository.getPreviousSubtopic(firstSubtopic.id))
        assertEquals("First subtopic's next should be second", secondSubtopic.id, DsaTopicCatalogueRepository.getNextSubtopic(firstSubtopic.id)?.id)

        // Second subtopic has first as previous
        assertEquals("Second subtopic's previous should be first", firstSubtopic.id, DsaTopicCatalogueRepository.getPreviousSubtopic(secondSubtopic.id)?.id)

        // Last subtopic has no next subtopic
        assertNull("Last subtopic should have no next subtopic", DsaTopicCatalogueRepository.getNextSubtopic(lastSubtopic.id))
        assertEquals("Last subtopic's previous should be 64th", allSubtopics[allSubtopics.size - 2].id, DsaTopicCatalogueRepository.getPreviousSubtopic(lastSubtopic.id)?.id)
    }

    @Test
    fun testUnauthoredOrUnknownLessonIdReturnsNullGracefully() {
        val unknownLesson = DsaLessonContentRepository.getLessonContent("unknown_topic_999_l1")
        assertNull("Unknown lesson should return null so Coming Soon screen is rendered", unknownLesson)
        assertFalse("Unknown lesson must return false for hasImplementedContent", DsaLessonContentRepository.hasImplementedContent("unknown_topic_999_l1"))
    }

    @Test
    fun testTopicProgressTrackingAndDuplicateHandling() {
        val subtopic = DsaTopicCatalogueRepository.getSubtopicById("search_binary")!!
        val outlines = DsaLessonContentRepository.getLessonOutlinesForSubtopic(subtopic)
        assertTrue("Outlines must contain at least 2 modules (Theory + Visualizer)", outlines.size >= 2)

        // Initial progress: 0 completed
        val initialProgress = DsaLessonContentRepository.calculateTopicProgress(subtopic, emptySet())
        assertEquals(0, initialProgress.completedCount)
        assertEquals(0, initialProgress.progressPercentage)

        // Mark 1 lesson complete
        val oneDone = DsaLessonContentRepository.calculateTopicProgress(subtopic, setOf("search_binary_l1"))
        assertEquals(1, oneDone.completedCount)
        assertTrue("Progress percentage must increase", oneDone.progressPercentage > 0)

        // Duplicate marking in set does not increase count
        val duplicateSet = setOf("search_binary_l1", "search_binary_l1")
        val duplicateCheck = DsaLessonContentRepository.calculateTopicProgress(subtopic, duplicateSet)
        assertEquals("Duplicate completed lesson IDs must not inflate completed count", 1, duplicateCheck.completedCount)
    }
}
