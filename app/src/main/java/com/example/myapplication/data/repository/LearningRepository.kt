package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.response.AlgorithmDto
import com.example.myapplication.data.model.response.LessonCompleteData
import com.example.myapplication.data.model.response.LessonItemDto
import com.example.myapplication.data.model.response.TopicDetailData
import com.example.myapplication.data.model.response.TopicHeaderDto
import com.example.myapplication.data.model.response.TopicItemDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient

class LearningRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    suspend fun getTopics(): Result<List<TopicItemDto>> {
        return try {
            val response = api.getTopics()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.topics)
            } else {
                createLocalTopics()
            }
        } catch (_: Exception) {
            createLocalTopics()
        }
    }

    private fun createLocalTopics(): Result<List<TopicItemDto>> {
        val list = listOf(
            TopicItemDto(1, 1, "CHAPTER 01", "Arrays & Memory", "Fundamental contiguous data structures", "Learn array memory layouts and pointer arithmetic", 100, "array", 4, 4, "4/4 Lessons", "COMPLETED"),
            TopicItemDto(2, 2, "CHAPTER 02", "Two Pointers & Sliding Window", "In-place linear search optimization", "Master sub-array optimization techniques", 150, "pointer", 5, 5, "5/5 Lessons", "COMPLETED"),
            TopicItemDto(3, 3, "CHAPTER 03", "Searching Algorithms", "Divide & Conquer binary search", "Master O(log N) binary search and ternary search", 200, "search", 2, 5, "2/5 Lessons", "ACTIVE"),
            TopicItemDto(4, 4, "CHAPTER 04", "Sorting & Divide/Conquer", "Comparison vs non-comparison sorts", "Master Bubble Sort, QuickSort, and MergeSort", 250, "sort", 0, 5, "0/5 Lessons", "NEXT"),
            TopicItemDto(5, 5, "CHAPTER 05", "Linked Lists & Pointers", "Singly & doubly linked dynamic lists", "Pointer manipulation and cycle detection", 300, "link", 0, 5, "Locked", "LOCKED"),
            TopicItemDto(6, 6, "CHAPTER 06", "Trees & Binary Search Trees", "Hierarchical data representation", "BST traversal, balance, and search operations", 350, "tree", 0, 5, "Locked", "LOCKED"),
            TopicItemDto(7, 7, "CHAPTER 07", "Graphs & BFS / DFS", "Network nodes and edge connections", "Shortest path and topological sorting", 400, "graph", 0, 5, "Locked", "LOCKED")
        )
        return Result.success(list)
    }

    suspend fun getTopicDetail(topicId: Int): Result<TopicDetailData> {
        return try {
            val response = api.getTopicDetail(topicId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                createLocalTopicDetail(topicId)
            }
        } catch (_: Exception) {
            createLocalTopicDetail(topicId)
        }
    }

    private fun createLocalTopicDetail(topicId: Int): Result<TopicDetailData> {
        val header = TopicHeaderDto(
            id = topicId,
            chapterNumber = topicId,
            chapterLabel = String.format("CHAPTER %02d", topicId),
            title = if (topicId == 3) "Searching Algorithms" else "Data Structures Chapter $topicId",
            subtitle = "Divide & Conquer Search Techniques",
            description = "Learn how cutting your search space in half reduces O(N) linear lookups into ultra-fast O(log N) operations.",
            totalXp = 900,
            progressPercentage = 40,
            completedLessons = 2,
            totalLessons = 5
        )

        val lessons = listOf(
            LessonItemDto(1, "Linear vs Binary Search: The 1M Step Difference", "8 min", 8, "+100 XP", 100, "READING", isCompleted = true, isCurrent = false, isLocked = false),
            LessonItemDto(2, "Binary Search Core Implementation & Invariants", "12 min", 12, "+150 XP", 150, "VISUALIZER", isCompleted = true, isCurrent = false, isLocked = false),
            LessonItemDto(3, "Finding First & Last Position of Target", "15 min", 15, "+150 XP", 150, "VISUALIZER", isCompleted = false, isCurrent = true, isLocked = false),
            LessonItemDto(4, "Search in Rotated Sorted Array", "18 min", 18, "+200 XP", 200, "READING", isCompleted = false, isCurrent = false, isLocked = true),
            LessonItemDto(5, "Chapter 03 Mastery Test & Challenge", "10 min", 10, "+300 XP", 300, "QUIZ", isCompleted = false, isCurrent = false, isLocked = false)
        )

        return Result.success(TopicDetailData(header, lessons))
    }

    suspend fun completeLesson(lessonId: Int): Result<LessonCompleteData> {
        return try {
            val response = api.completeLesson(lessonId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.success(LessonCompleteData(xpAwarded = 150, totalXp = 2600, level = 8, streak = 7))
            }
        } catch (_: Exception) {
            Result.success(LessonCompleteData(xpAwarded = 150, totalXp = 2600, level = 8, streak = 7))
        }
    }

    suspend fun getAlgorithms(): Result<List<AlgorithmDto>> {
        return try {
            val response = api.getAlgorithms()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.algorithms)
            } else {
                createLocalAlgorithms()
            }
        } catch (_: Exception) {
            createLocalAlgorithms()
        }
    }

    private fun createLocalAlgorithms(): Result<List<AlgorithmDto>> {
        val list = listOf(
            AlgorithmDto("binary-search", "Binary Search", "O(log N)", "O(1)", "Halves search space each step", "fun binarySearch(arr: IntArray, target: Int): Int"),
            AlgorithmDto("bubble-sort", "Bubble Sort", "O(N^2)", "O(1)", "Swaps adjacent pairs in nested passes", "fun bubbleSort(arr: IntArray)"),
            AlgorithmDto("two-pointers", "Two Pointers", "O(N)", "O(1)", "Linear inward scan on sorted data", "fun twoSumSorted(arr: IntArray, target: Int)")
        )
        return Result.success(list)
    }
}
