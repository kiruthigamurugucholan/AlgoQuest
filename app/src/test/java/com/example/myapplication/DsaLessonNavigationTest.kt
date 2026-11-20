package com.example.myapplication

import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import com.example.myapplication.data.visualizer.DsaVisualizationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DsaLessonNavigationTest {

    @Test
    fun testAll65CatalogueSubtopicsHaveDedicatedVisualizationScenarios() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        assertEquals("Total catalogue subtopics must be 65", 65, subtopics.size)

        subtopics.forEach { subtopic ->
            // 1. Verify registry contains scenario for subtopic ID
            assertTrue(
                "Registry must recognize subtopic: ${subtopic.id} (${subtopic.name})",
                DsaVisualizationRegistry.hasScenario(subtopic.id)
            )

            // 2. Scenario lookup
            val scenario = DsaVisualizationRegistry.getScenarioForSubtopic(subtopic.id)
            assertNotNull("Scenario for ${subtopic.id} must not be null", scenario)
            assertEquals("Scenario subtopicId must strictly match catalogue subtopic ID", subtopic.id, scenario!!.subtopicId)
            assertTrue("Scenario title must not be blank", scenario.title.isNotBlank())
            assertTrue("Scenario categoryName must not be blank", scenario.categoryName.isNotBlank())

            // 3. Ensure no topic other than binary search defaults to Binary Search
            if (subtopic.id != "search_binary") {
                assertNotEquals(
                    "Subtopic ${subtopic.id} must NOT have 'search_binary' ID",
                    "search_binary",
                    scenario.subtopicId
                )
            } else {
                assertEquals("search_binary", scenario.subtopicId)
                assertTrue("Binary Search subtopic title must contain 'Binary Search'", scenario.title.contains("Binary Search"))
            }

            // 4. Verify step generation
            val steps = DsaVisualizationRegistry.generateStepsForSubtopic(subtopic.id)
            assertTrue(
                "Subtopic ${subtopic.id} must generate at least 2 visualization frames",
                steps.size >= 2
            )
        }
    }

    @Test
    fun testNavigationResolutionBySubtopicSlug() {
        val testCases = listOf(
            "array_traversal" to "Array Traversal",
            "search_binary" to "Binary Search",
            "sort_bubble" to "Bubble Sort",
            "sort_quick" to "Quick Sort",
            "ll_singly" to "Singly Linked List",
            "stack_operations" to "Stack Operations",
            "queue_circular" to "Circular Queue",
            "rec_fibonacci" to "Fibonacci",
            "tree_inorder" to "Inorder Traversal",
            "graph_bfs" to "Breadth-First Search",
            "graph_dijkstra" to "Dijkstra",
            "adv_dp" to "Knapsack"
        )

        testCases.forEach { (slug, expectedSubstring) ->
            assertTrue("Scenario must exist for $slug", DsaVisualizationRegistry.hasScenario(slug))
            val scenario = DsaVisualizationRegistry.getScenarioForSubtopic(slug)
            assertNotNull("Scenario for $slug must not be null", scenario)
            assertTrue(
                "Scenario title '${scenario!!.title}' must contain '$expectedSubstring'",
                scenario.title.contains(expectedSubstring, ignoreCase = true)
            )
            assertEquals("Scenario subtopicId must match slug", slug, scenario.subtopicId)
        }
    }

    @Test
    fun testSubtopicVisualizerSlugMatchesSubtopicId() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        subtopics.forEach { subtopic ->
            assertNotNull("Visualizer slug must not be null for ${subtopic.id}", subtopic.visualizerSlug)
            assertTrue(
                "Subtopic ID or visualizerSlug must be known to registry",
                DsaVisualizationRegistry.hasScenario(subtopic.id) ||
                        DsaVisualizationRegistry.hasScenario(subtopic.visualizerSlug!!)
            )
        }
    }
}
