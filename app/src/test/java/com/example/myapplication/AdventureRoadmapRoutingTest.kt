package com.example.myapplication

import com.example.myapplication.data.repository.AdventureRoadmapRepository
import com.example.myapplication.data.repository.DsaTopicCatalogueRepository
import com.example.myapplication.data.visualizer.DsaVisualizationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdventureRoadmapRoutingTest {

    @Test
    fun testAdventureRoadmapHasSevenChaptersWithUniqueIds() {
        val chapters = AdventureRoadmapRepository.getAllChapters()
        assertEquals("Adventure Roadmap must contain exactly 7 chapters", 7, chapters.size)

        val ids = chapters.map { it.id }.toSet()
        assertEquals("Each chapter must have a unique ID", 7, ids.size)

        chapters.forEachIndexed { index, chapter ->
            val expectedChapterNum = index + 1
            assertEquals(expectedChapterNum, chapter.chapterNumber)
            assertEquals("Chapter %02d".format(expectedChapterNum), chapter.chapterLabel)
            assertTrue("Chapter title must not be blank", chapter.title.isNotBlank())
            assertTrue("Chapter subtitle must not be blank", chapter.subtitle.isNotBlank())
            assertTrue("Chapter description must not be blank", chapter.description.isNotBlank())
            assertTrue("Chapter totalXp must be positive", chapter.totalXp > 0)
            assertTrue("Chapter must contain at least 4 lessons", chapter.lessons.size >= 4)
        }
    }

    @Test
    fun testAllChaptersMapToDedicatedPrimarySubtopicsAndSlugs() {
        val expectedMappings = mapOf(
            1 to ("array_traversal" to "array_traversal"),
            2 to ("array_two_pointers" to "two-pointers"),
            3 to ("search_binary" to "binary-search"),
            4 to ("sort_bubble" to "bubble-sort"),
            5 to ("ll_singly" to "ll_singly"),
            6 to ("tree_bst" to "tree_bst"),
            7 to ("graph_bfs" to "graph_bfs")
        )

        expectedMappings.forEach { (chapterId, expected) ->
            val (expectedSubtopic, expectedSlug) = expected
            val chapter = AdventureRoadmapRepository.getChapterById(chapterId)
            assertNotNull("Chapter $chapterId must exist in repository", chapter)
            assertEquals("Chapter $chapterId primarySubtopicId must match", expectedSubtopic, chapter!!.primarySubtopicId)
            assertEquals("Chapter $chapterId primaryVisualizerSlug must match", expectedSlug, chapter.primaryVisualizerSlug)

            // Verify visualizer slug is recognized by registry
            assertTrue(
                "Primary slug '$expectedSlug' for Chapter $chapterId must be registered in DsaVisualizationRegistry",
                DsaVisualizationRegistry.hasScenario(expectedSlug)
            )

            // Verify non-Chapter 3 chapters DO NOT map to binary search
            if (chapterId != 3) {
                assertNotEquals("Chapter $chapterId must NOT map to search_binary", "search_binary", chapter.primarySubtopicId)
                assertNotEquals("Chapter $chapterId must NOT map to binary-search slug", "binary-search", chapter.primaryVisualizerSlug)
            }
        }
    }

    @Test
    fun testChapter01LessonRouting() {
        val chapter = AdventureRoadmapRepository.getChapterById(1)
        assertNotNull(chapter)
        assertEquals("Arrays & Memory", chapter!!.title)
        assertEquals(4, chapter.lessons.size)

        // Lesson 101: Memory Allocation
        val lesson1 = chapter.lessons[0]
        assertEquals(101, lesson1.id)
        assertEquals("array_traversal", lesson1.subtopicId)
        assertEquals("array_traversal_l1", lesson1.catalogueLessonId)

        // Lesson 103: Visualizer Lab
        val vizLesson = chapter.lessons.first { it.lessonType == "VISUALIZER" }
        assertEquals(103, vizLesson.id)
        assertEquals("array_traversal", vizLesson.visualizerSlug)
        assertTrue(DsaVisualizationRegistry.hasScenario(vizLesson.visualizerSlug!!))

        // Lesson 104: Quiz
        val quizLesson = chapter.lessons.first { it.lessonType == "QUIZ" }
        assertEquals(104, quizLesson.id)
        assertTrue(quizLesson.isQuiz)

        // Verify NO lesson in Chapter 01 routes to binary search
        chapter.lessons.forEach { lesson ->
            assertNotEquals("Chapter 01 lesson must NOT route to binary search", "search_binary", lesson.subtopicId)
            assertNotEquals("Chapter 01 lesson must NOT route to binary search slug", "binary-search", lesson.visualizerSlug)
        }
    }

    @Test
    fun testChapter02LessonRouting() {
        val chapter = AdventureRoadmapRepository.getChapterById(2)
        assertNotNull(chapter)
        assertEquals("Two Pointers & Sliding", chapter!!.title)
        assertEquals(5, chapter.lessons.size)

        // Two Pointers Visualizer (Lesson 202)
        val tpViz = chapter.lessons.first { it.id == 202 }
        assertEquals("VISUALIZER", tpViz.lessonType)
        assertEquals("two-pointers", tpViz.visualizerSlug)
        assertEquals("array_two_pointers", tpViz.subtopicId)
        assertTrue(DsaVisualizationRegistry.hasScenario(tpViz.visualizerSlug!!))

        // Sliding Window Visualizer (Lesson 204)
        val swViz = chapter.lessons.first { it.id == 204 }
        assertEquals("VISUALIZER", swViz.lessonType)
        assertEquals("array_sliding_window", swViz.visualizerSlug)
        assertEquals("array_sliding_window", swViz.subtopicId)
        assertTrue(DsaVisualizationRegistry.hasScenario(swViz.visualizerSlug!!))

        // No lesson in Chapter 02 should route to binary search
        chapter.lessons.forEach { lesson ->
            assertNotEquals("Chapter 02 lesson must NOT route to binary search", "search_binary", lesson.subtopicId)
            assertNotEquals("Chapter 02 lesson must NOT route to binary search slug", "binary-search", lesson.visualizerSlug)
        }
    }

    @Test
    fun testChapter03SearchingLessonRouting() {
        val chapter = AdventureRoadmapRepository.getChapterById(3)
        assertNotNull(chapter)
        assertEquals("Searching Algorithms", chapter!!.title)
        assertEquals(5, chapter.lessons.size)

        // Lesson 1: Linear Search
        val lesson1 = chapter.lessons[0]
        assertEquals(1, lesson1.id)
        assertEquals("search_linear", lesson1.subtopicId)
        assertEquals("search_linear", lesson1.visualizerSlug)

        // Lesson 2: Binary Search Core Implementation
        val lesson2 = chapter.lessons[1]
        assertEquals(2, lesson2.id)
        assertEquals("search_binary", lesson2.subtopicId)

        // Lesson 3: Binary Search Visualizer
        val lesson3 = chapter.lessons[2]
        assertEquals(3, lesson3.id)
        assertEquals("VISUALIZER", lesson3.lessonType)
        assertEquals("binary-search", lesson3.visualizerSlug)
        assertEquals("search_binary", lesson3.subtopicId)

        // Lesson 5: Quiz
        val lesson5 = chapter.lessons[4]
        assertEquals(5, lesson5.id)
        assertTrue(lesson5.isQuiz)
    }

    @Test
    fun testRemainingChaptersLessonRouting() {
        // Chapter 04: Sorting
        val ch4 = AdventureRoadmapRepository.getChapterById(4)!!
        assertEquals("sort_bubble", ch4.primarySubtopicId)
        val ch4Viz = ch4.lessons.filter { it.lessonType == "VISUALIZER" }
        assertTrue(ch4Viz.any { it.visualizerSlug == "bubble-sort" })
        assertTrue(ch4Viz.any { it.visualizerSlug == "sort_quick" })

        // Chapter 05: Linked Lists
        val ch5 = AdventureRoadmapRepository.getChapterById(5)!!
        assertEquals("ll_singly", ch5.primarySubtopicId)
        val ch5Viz = ch5.lessons.filter { it.lessonType == "VISUALIZER" }
        assertTrue(ch5Viz.any { it.visualizerSlug == "ll_singly" })
        assertTrue(ch5Viz.any { it.visualizerSlug == "ll_reversal" })

        // Chapter 06: Trees
        val ch6 = AdventureRoadmapRepository.getChapterById(6)!!
        assertEquals("tree_bst", ch6.primarySubtopicId)
        val ch6Viz = ch6.lessons.filter { it.lessonType == "VISUALIZER" }
        assertTrue(ch6Viz.any { it.visualizerSlug == "tree_bst" })
        assertTrue(ch6Viz.any { it.visualizerSlug == "tree_inorder" })

        // Chapter 07: Graphs
        val ch7 = AdventureRoadmapRepository.getChapterById(7)!!
        assertEquals("graph_bfs", ch7.primarySubtopicId)
        val ch7Viz = ch7.lessons.filter { it.lessonType == "VISUALIZER" }
        assertTrue(ch7Viz.any { it.visualizerSlug == "graph_bfs" })
        assertTrue(ch7Viz.any { it.visualizerSlug == "graph_dijkstra" })

        // Ensure none of chapters 4, 5, 6, 7 map to search_binary
        listOf(ch4, ch5, ch6, ch7).forEach { ch ->
            assertNotEquals("Chapter ${ch.id} must not map to search_binary", "search_binary", ch.primarySubtopicId)
            ch.lessons.forEach { lesson ->
                assertNotEquals("Chapter ${ch.id} lesson ${lesson.id} must not map to search_binary", "search_binary", lesson.subtopicId)
            }
        }
    }

    @Test
    fun testAllVisualizerSlugsInRoadmapAreRegisteredInDsaVisualizationRegistry() {
        val chapters = AdventureRoadmapRepository.getAllChapters()
        chapters.forEach { chapter ->
            // Primary slug must be valid
            assertTrue(
                "Primary slug '${chapter.primaryVisualizerSlug}' for chapter ${chapter.id} must exist in registry",
                DsaVisualizationRegistry.hasScenario(chapter.primaryVisualizerSlug)
            )

            chapter.lessons.forEach { lesson ->
                if (!lesson.visualizerSlug.isNullOrEmpty()) {
                    assertTrue(
                        "Lesson '${lesson.title}' slug '${lesson.visualizerSlug}' must exist in registry",
                        DsaVisualizationRegistry.hasScenario(lesson.visualizerSlug)
                    )
                    val steps = DsaVisualizationRegistry.generateStepsForSubtopic(lesson.visualizerSlug)
                    assertTrue(
                        "Lesson slug '${lesson.visualizerSlug}' must produce at least 2 visualization frames",
                        steps.size >= 2
                    )
                }
            }
        }
    }

    @Test
    fun testMissingMappingHandlingNeverDefaultsToBinarySearch() {
        val missingSlug = "completely_unknown_topic_xyz"
        assertFalse(DsaVisualizationRegistry.hasScenario(missingSlug))
        assertNull(DsaVisualizationRegistry.getScenarioForSubtopic(missingSlug))

        val steps = DsaVisualizationRegistry.generateStepsForSubtopic(missingSlug)
        assertTrue("Unknown topic must return empty steps, never binary search frames", steps.isEmpty())
    }

    @Test
    fun testDsaCatalogueRoutingRemainsIntactForSixtyFiveTopics() {
        val subtopics = DsaTopicCatalogueRepository.getAllSubtopics()
        assertEquals(65, subtopics.size)

        subtopics.forEach { subtopic ->
            assertTrue(
                "Catalogue subtopic ${subtopic.id} must be recognized by registry",
                DsaVisualizationRegistry.hasScenario(subtopic.id)
            )
            val scenario = DsaVisualizationRegistry.getScenarioForSubtopic(subtopic.id)
            assertNotNull(scenario)
            assertEquals(subtopic.id, scenario!!.subtopicId)
        }
    }
}
