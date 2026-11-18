package com.example.myapplication

import com.example.myapplication.data.model.response.ApiResponse
import com.example.myapplication.data.model.response.LearningResourceData
import com.example.myapplication.data.model.response.LearningResourceDto
import com.example.myapplication.data.model.response.LearningResourceTopicsData
import com.example.myapplication.data.repository.LearningResourceRepository
import com.example.myapplication.util.LearningResourceTextFormatter
import com.example.myapplication.viewmodel.LearningResourceUiState
import com.example.myapplication.viewmodel.LearningResourceViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningResourceTest {

    @Test
    fun testDefaultCatalogHasTwentyTopicsAndFourCategories() {
        val topics = LearningResourceRepository.defaultSupportedTopics
        val categories = LearningResourceRepository.defaultCategories

        assertEquals(20, topics.size)
        assertEquals(4, categories.size)
        assertEquals(20, topics.map { it.slug }.toSet().size)

        val requiredSlugs = setOf(
            "linear_search", "binary_search",
            "bubble_sort", "selection_sort", "insertion_sort", "merge_sort", "quick_sort",
            "array", "linked_list", "stack", "queue", "tree", "binary_tree", "graph", "hash_table",
            "bfs", "dfs", "recursion", "greedy_algorithm", "dynamic_programming"
        )
        assertEquals(requiredSlugs, topics.map { it.slug }.toSet())
    }

    @Test
    fun testTopicQueryNormalization() {
        assertEquals("binary_search", LearningResourceRepository.normalizeTopicQuery("Binary Search"))
        assertEquals("quick_sort", LearningResourceRepository.normalizeTopicQuery("quick-sort"))
        assertEquals("bfs", LearningResourceRepository.normalizeTopicQuery("  BFS  "))
        assertEquals("dynamic_programming", LearningResourceRepository.normalizeTopicQuery("Dynamic   Programming!"))
    }

    @Test
    fun testTopicFilteringAndEmptyMatches() {
        val all = LearningResourceRepository.defaultSupportedTopics

        val sortingOnly = LearningResourceViewModel.filterTopics(all, "", "Sorting")
        assertEquals(5, sortingOnly.size)

        val searchBinary = LearningResourceViewModel.filterTopics(all, "binary", null)
        assertEquals(2, searchBinary.size)
        assertTrue(searchBinary.any { it.slug == "binary_search" })
        assertTrue(searchBinary.any { it.slug == "binary_tree" })

        val noMatches = LearningResourceViewModel.filterTopics(all, "quantum_warp_xyz", null)
        assertTrue(noMatches.isEmpty())
    }

    @Test
    fun testExternalUrlSafetyValidation() {
        assertTrue(LearningResourceRepository.isSafeExternalUrl("https://en.wikipedia.org/wiki/Binary_search"))
        assertTrue(LearningResourceRepository.isSafeExternalUrl("http://example.org/dsa"))
        assertFalse(LearningResourceRepository.isSafeExternalUrl("javascript:alert(1)"))
        assertFalse(LearningResourceRepository.isSafeExternalUrl("file:///etc/passwd"))
        assertFalse(LearningResourceRepository.isSafeExternalUrl(""))
        assertFalse(LearningResourceRepository.isSafeExternalUrl(null))
    }

    @Test
    fun testGsonParsesPhpLearningResourcePayload() {
        val sampleJson = """
            {
              "success": true,
              "message": "Learning resource retrieved successfully.",
              "data": {
                "cached": false,
                "resource": {
                  "id": 12,
                  "topic_slug": "binary_search",
                  "topic": "Binary Search",
                  "category": "Searching",
                  "title": "Binary search",
                  "description": "In computer science, binary search is a search algorithm that finds the position of a target value within a sorted array.",
                  "headings": ["Algorithm", "Performance", "Applications"],
                  "content": ["Paragraph 1", "Paragraph 2"],
                  "important_points": ["Runs in O(log n) worst-case time", "Requires sorted input"],
                  "source_name": "Wikipedia",
                  "source_url": "https://en.wikipedia.org/wiki/Binary_search",
                  "scraped_at": "2026-11-18T04:15:00Z",
                  "from_cache": false
                }
              }
            }
        """.trimIndent()

        val type = object : TypeToken<ApiResponse<LearningResourceData>>() {}.type
        val parsed: ApiResponse<LearningResourceData> = Gson().fromJson(sampleJson, type)

        assertTrue(parsed.success)
        assertNotNull(parsed.data)
        val res = parsed.data!!.resource
        assertEquals(12, res.id)
        assertEquals("binary_search", res.topicSlug)
        assertEquals("Binary Search", res.topic)
        assertEquals("Wikipedia", res.sourceName)
        assertEquals(3, res.headings.size)
        assertEquals(2, res.content.size)
        assertEquals(2, res.importantPoints.size)
        assertTrue(LearningResourceRepository.isSafeExternalUrl(res.sourceUrl))
    }

    @Test
    fun testUiStateRepresentations() {
        val loading = LearningResourceUiState.Loading("Binary Search")
        val empty = LearningResourceUiState.Empty("unknown")
        val error = LearningResourceUiState.Error("Timeout", "binary_search")
        val success = LearningResourceUiState.Success(
            resource = LearningResourceDto(topicSlug = "stack", title = "Stack"),
            cached = true
        )

        assertEquals("Binary Search", loading.topicLabel)
        assertEquals("unknown", empty.query)
        assertEquals("binary_search", error.failedTopic)
        assertTrue(success.cached)
        assertEquals("Stack", success.resource.title)
    }

    @Test
    fun testTextFormatterCleansPunctuationAndBrackets() {
        val rawDesc = "In computer science , binary search , also known as half-interval search , is a search algorithm that finds a target value within a sorted array ."
        val expectedDesc = "In computer science, binary search, also known as half-interval search, is a search algorithm that finds a target value within a sorted array."
        assertEquals(expectedDesc, LearningResourceTextFormatter.cleanText(rawDesc))

        val rawBfs = "In computer science , breadth-first search ( BFS ) uses a queue ( First In First Out ) ."
        val expectedBfs = "In computer science, breadth-first search (BFS) uses a queue (First In First Out)."
        assertEquals(expectedBfs, LearningResourceTextFormatter.cleanText(rawBfs))
    }

    @Test
    fun testTextFormatterFormatsMathAndBigOExpressions() {
        val rawMidpoint = "Set m (the position of the middle element) to L plus the floor of R − L 2 , which is the greatest integer less than or equal to R − L 2 ."
        val expectedMidpoint = "Set m (the position of the middle element) to L + floor((R − L) / 2), which is the greatest integer less than or equal to (R − L) / 2."
        assertEquals(expectedMidpoint, LearningResourceTextFormatter.cleanText(rawMidpoint))

        val rawCeiling = "While L ≠ R , Set m to L plus the ceiling of R − L 2 . If A m > T , set R to m − 1 . Else, A m ≤ T ; set L to m ."
        val expectedCeiling = "While L ≠ R, Set m to L + ceil((R − L) / 2). If A[m] > T, set R to m − 1. Else, A[m] ≤ T; set L to m."
        assertEquals(expectedCeiling, LearningResourceTextFormatter.cleanText(rawCeiling))

        val rawBigO = "Binary search runs in logarithmic time in the worst case , making O ( log \u2061 n ) comparisons, while Bubble sort takes O ( n 2 ) time."
        val expectedBigO = "Binary search runs in logarithmic time in the worst case, making O(log n) comparisons, while Bubble sort takes O(n²) time."
        assertEquals(expectedBigO, LearningResourceTextFormatter.cleanText(rawBigO))

        val rawArray = "Given an array A of n elements A 0 , A 1 , A 2 , … , A n − 1 sorted such that A 0 ≤ A 1 ≤ A 2 ≤ ⋯ ≤ A n − 1 , find T ."
        val expectedArray = "Given an array A of n elements A[0], A[1], A[2], …, A[n − 1] sorted such that A[0] ≤ A[1] ≤ A[2] ≤ ⋯ ≤ A[n − 1], find T."
        assertEquals(expectedArray, LearningResourceTextFormatter.cleanText(rawArray))
    }

    @Test
    fun testFormatResourceCleansAllFieldsAndPreservesUrl() {
        val raw = LearningResourceDto(
            id = 1,
            topicSlug = "binary_search",
            topic = "Binary Search",
            category = "Searching",
            title = "Binary search ",
            description = "Finds position in a sorted array .",
            headings = listOf("Algorithm ", "Performance ( O ( log n ) )"),
            content = listOf("Runs in O ( log \u2061 n ) time ."),
            importantPoints = listOf("If A m < T , set L to m + 1 ."),
            sourceName = "Wikipedia",
            sourceUrl = "https://en.wikipedia.org/wiki/Binary_search"
        )

        val formatted = LearningResourceTextFormatter.formatResource(raw)
        assertEquals("Binary search", formatted.title)
        assertEquals("Finds position in a sorted array.", formatted.description)
        assertEquals(listOf("Algorithm", "Performance (O(log n))"), formatted.headings)
        assertEquals(listOf("Runs in O(log n) time."), formatted.content)
        assertEquals(listOf("If A[m] < T, set L to m + 1."), formatted.importantPoints)
        assertEquals("https://en.wikipedia.org/wiki/Binary_search", formatted.sourceUrl)
    }
}
