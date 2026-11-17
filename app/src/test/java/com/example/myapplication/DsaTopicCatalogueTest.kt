package com.example.myapplication

import com.example.myapplication.data.model.catalogue.TopicDifficulty
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DsaTopicCatalogueTest {

    @Test
    fun testCategoryCountIsTwelve() {
        val categories = DsaTopicCatalogueRepository.getAllCategories()
        assertEquals("There must be exactly 12 DSA categories", 12, categories.size)
    }

    @Test
    fun testTotalSubtopicsCountIsSixtyFive() {
        val allSubtopics = DsaTopicCatalogueRepository.subtopics
        assertEquals("There must be exactly 65 DSA subtopics", 65, allSubtopics.size)
    }

    @Test
    fun testAllSubtopicIdsAreUnique() {
        val allSubtopics = DsaTopicCatalogueRepository.subtopics
        val uniqueIds = allSubtopics.map { it.id }.toSet()
        assertEquals("Every subtopic must have a unique ID", allSubtopics.size, uniqueIds.size)
    }

    @Test
    fun testExactSubtopicCountPerCategory() {
        val expectedCounts = mapOf(
            "programming_basics" to 5,
            "arrays" to 6,
            "searching" to 4,
            "sorting" to 7,
            "strings" to 5,
            "linked_lists" to 5,
            "stack" to 5,
            "queue" to 4,
            "recursion" to 5,
            "trees" to 7,
            "graphs" to 6,
            "advanced_algorithms" to 6
        )

        for ((categoryId, expectedCount) in expectedCounts) {
            val subtopics = DsaTopicCatalogueRepository.getSubtopicsForCategory(categoryId)
            assertEquals("Category '$categoryId' must have $expectedCount subtopics", expectedCount, subtopics.size)
        }
    }

    @Test
    fun testDifficultyDistribution() {
        val easyTopics = DsaTopicCatalogueRepository.filterSubtopics(TopicDifficulty.EASY)
        val mediumTopics = DsaTopicCatalogueRepository.filterSubtopics(TopicDifficulty.MEDIUM)
        val hardTopics = DsaTopicCatalogueRepository.filterSubtopics(TopicDifficulty.HARD)

        assertEquals("Easy topics count must be 32", 32, easyTopics.size)
        assertEquals("Medium topics count must be 22", 22, mediumTopics.size)
        assertEquals("Hard topics count must be 11", 11, hardTopics.size)
        assertEquals("Total difficulty distribution must sum to 65", 65, easyTopics.size + mediumTopics.size + hardTopics.size)
    }

    @Test
    fun testSupportedVisualizerAndQuizFlags() {
        // Binary Search
        val bs = DsaTopicCatalogueRepository.getSubtopicById("search_binary")
        assertNotNull("Binary Search subtopic must exist", bs)
        assertTrue(bs!!.hasVisualizer)
        assertEquals("binary-search", bs.visualizerSlug)
        assertTrue(bs.hasQuiz)
        assertEquals(5, bs.quizId)

        // Bubble Sort
        val bubble = DsaTopicCatalogueRepository.getSubtopicById("sort_bubble")
        assertNotNull("Bubble Sort subtopic must exist", bubble)
        assertTrue(bubble!!.hasVisualizer)
        assertEquals("bubble-sort", bubble.visualizerSlug)

        // Two Pointers
        val tp = DsaTopicCatalogueRepository.getSubtopicById("array_two_pointers")
        assertNotNull("Two Pointers subtopic must exist", tp)
        assertTrue(tp!!.hasVisualizer)
        assertEquals("two-pointers", tp.visualizerSlug)

        // Topic without visualizer/quiz
        val dsIntro = DsaTopicCatalogueRepository.getSubtopicById("prog_intro_ds")
        assertNotNull("Intro to DS subtopic must exist", dsIntro)
        assertFalse(dsIntro!!.hasVisualizer)
        assertNull(dsIntro.visualizerSlug)
        assertFalse(dsIntro.hasQuiz)
        assertNull(dsIntro.quizId)
    }

    @Test
    fun testSearchSubtopics() {
        val searchResults = DsaTopicCatalogueRepository.searchSubtopics("Binary")
        assertTrue("Search for 'Binary' should find multiple subtopics", searchResults.size >= 4)
        val titles = searchResults.map { it.title }
        assertTrue(titles.contains("Binary Search"))
        assertTrue(titles.contains("Recursive Binary Search"))
        assertTrue(titles.contains("Binary Trees"))
        assertTrue(titles.contains("Binary Search Trees"))
    }
}
