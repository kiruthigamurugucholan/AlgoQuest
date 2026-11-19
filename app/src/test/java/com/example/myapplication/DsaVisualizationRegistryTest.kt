package com.example.myapplication

import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import com.example.myapplication.data.visualizer.DsaVisualizationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DsaVisualizationRegistryTest {

    @Test
    fun testAll65SubtopicsHaveVisualizationFlagAndSlug() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        assertEquals("Catalogue must contain exactly 65 subtopics", 65, subtopics.size)

        subtopics.forEach { subtopic ->
            assertTrue(
                "Subtopic ${subtopic.id} (${subtopic.name}) must have hasVisualization=true",
                subtopic.hasVisualization
            )
            assertNotNull(
                "Subtopic ${subtopic.id} must have non-null visualizerSlug",
                subtopic.visualizerSlug
            )
            assertTrue(
                "Subtopic ${subtopic.id} visualizerSlug must not be empty",
                subtopic.visualizerSlug!!.isNotEmpty()
            )
        }
    }

    @Test
    fun testAll65SubtopicsHaveRegisteredVisualizationScenario() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()

        subtopics.forEach { subtopic ->
            val hasScenario = DsaVisualizationRegistry.hasScenario(subtopic.id)
            assertTrue(
                "Registry must contain scenario for subtopic ${subtopic.id} (${subtopic.name})",
                hasScenario
            )

            val scenario = DsaVisualizationRegistry.getScenarioForSubtopic(subtopic.id)
            assertNotNull("Scenario for ${subtopic.id} must not be null", scenario)
            assertEquals("Scenario subtopicId must match", subtopic.id, scenario!!.subtopicId)
            assertTrue("Scenario title must not be blank", scenario.title.isNotBlank())
            assertTrue("Scenario categoryName must not be blank", scenario.categoryName.isNotBlank())
            assertTrue("Scenario defaultInput must not be blank", scenario.defaultInput.isNotBlank())
        }
    }

    @Test
    fun testAll65ScenariosGenerateValidStepTraces() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()

        subtopics.forEach { subtopic ->
            val steps = DsaVisualizationRegistry.generateStepsForSubtopic(subtopic.id)
            assertTrue(
                "Subtopic ${subtopic.id} must generate at least 2 visualization steps (generated ${steps.size})",
                steps.size >= 2
            )

            // Validate step invariants
            steps.forEachIndexed { idx, step ->
                assertEquals("stepIndex must match sequence index", idx, step.stepIndex)
                assertTrue("step.title must not be blank", step.title.isNotBlank())
                assertTrue("step.description must not be blank", step.description.isNotBlank())
                assertTrue("step.actionType must not be blank", step.actionType.isNotBlank())
                assertNotNull("step.vizType must not be null", step.vizType)
            }

            // Initial step should be INITIALIZE or related setup
            val firstStep = steps.first()
            assertNotNull(firstStep.title)

            // Final step should be marked completed or have summary
            val lastStep = steps.last()
            assertTrue(
                "Last step of ${subtopic.id} must have summaryResult or isCompleted=true",
                lastStep.isCompleted || !lastStep.summaryResult.isNullOrBlank()
            )
        }
    }

    @Test
    fun testCustomInputValidationAndExecution() {
        // Test array traversal with custom input
        val customArr = "1, 2, 3, 4"
        val customSteps = DsaVisualizationRegistry.generateStepsForSubtopic("array_traversal", customArr)
        assertTrue(customSteps.size >= 4)
        assertEquals(listOf(1, 2, 3, 4), customSteps.first().arrayElements)

        // Test linear search with custom input and target
        val customSearch = "10, 20, 30; 20"
        val searchSteps = DsaVisualizationRegistry.generateStepsForSubtopic("search_linear", customSearch)
        assertTrue(searchSteps.any { it.isFound })

        // Test bubble sort with custom input
        val customSort = "5, 4, 3, 2, 1"
        val sortSteps = DsaVisualizationRegistry.generateStepsForSubtopic("sort_bubble", customSort)
        val sortedOutput = sortSteps.last().arrayElements
        assertEquals(listOf(1, 2, 3, 4, 5), sortedOutput)
    }

    @Test
    fun testDiverseVisualizationTypesAreRepresented() {
        val allScenarios = DsaVisualizationRegistry.getAllScenarios()
        val types = allScenarios.map { it.vizType }.toSet()

        assertTrue(types.contains(DsaVizType.ARRAY))
        assertTrue(types.contains(DsaVizType.SEARCH))
        assertTrue(types.contains(DsaVizType.SORT))
        assertTrue(types.contains(DsaVizType.STRING))
        assertTrue(types.contains(DsaVizType.LINKED_LIST))
        assertTrue(types.contains(DsaVizType.STACK))
        assertTrue(types.contains(DsaVizType.QUEUE))
        assertTrue(types.contains(DsaVizType.TREE))
        assertTrue(types.contains(DsaVizType.GRAPH))
        assertTrue(types.contains(DsaVizType.RECURSION))
        assertTrue(types.contains(DsaVizType.DP_TABLE))
        assertTrue(types.contains(DsaVizType.GREEDY))
        assertTrue(types.contains(DsaVizType.FLOWCHART))
    }
}
