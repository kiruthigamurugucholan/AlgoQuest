package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.response.LearningResourceData
import com.example.myapplication.data.model.response.LearningResourceTopicDto
import com.example.myapplication.data.model.response.LearningResourceTopicsData
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ResourceNotFoundException(message: String) : Exception(message)

class LearningResourceRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    companion object {
        val defaultSupportedTopics: List<LearningResourceTopicDto> = listOf(
            // Searching
            LearningResourceTopicDto("linear_search", "Linear Search", "Searching"),
            LearningResourceTopicDto("binary_search", "Binary Search", "Searching"),
            // Sorting
            LearningResourceTopicDto("bubble_sort", "Bubble Sort", "Sorting"),
            LearningResourceTopicDto("selection_sort", "Selection Sort", "Sorting"),
            LearningResourceTopicDto("insertion_sort", "Insertion Sort", "Sorting"),
            LearningResourceTopicDto("merge_sort", "Merge Sort", "Sorting"),
            LearningResourceTopicDto("quick_sort", "Quick Sort", "Sorting"),
            // Data Structures
            LearningResourceTopicDto("array", "Array", "Data Structures"),
            LearningResourceTopicDto("linked_list", "Linked List", "Data Structures"),
            LearningResourceTopicDto("stack", "Stack", "Data Structures"),
            LearningResourceTopicDto("queue", "Queue", "Data Structures"),
            LearningResourceTopicDto("tree", "Tree", "Data Structures"),
            LearningResourceTopicDto("binary_tree", "Binary Tree", "Data Structures"),
            LearningResourceTopicDto("graph", "Graph", "Data Structures"),
            LearningResourceTopicDto("hash_table", "Hash Table", "Data Structures"),
            // Algorithms
            LearningResourceTopicDto("bfs", "Breadth First Search (BFS)", "Algorithms"),
            LearningResourceTopicDto("dfs", "Depth First Search (DFS)", "Algorithms"),
            LearningResourceTopicDto("recursion", "Recursion", "Algorithms"),
            LearningResourceTopicDto("greedy_algorithm", "Greedy Algorithm", "Algorithms"),
            LearningResourceTopicDto("dynamic_programming", "Dynamic Programming", "Algorithms")
        )

        val defaultCategories: List<String> = listOf(
            "Searching",
            "Sorting",
            "Data Structures",
            "Algorithms"
        )

        fun normalizeTopicQuery(raw: String): String {
            return raw.trim()
                .lowercase()
                .replace(Regex("[^a-z0-9]+"), "_")
                .trim('_')
        }

        fun isSafeExternalUrl(url: String?): Boolean {
            val trimmed = url?.trim() ?: return false
            return trimmed.startsWith("https://", ignoreCase = true) ||
                trimmed.startsWith("http://", ignoreCase = true)
        }
    }

    suspend fun getSupportedTopics(): Result<LearningResourceTopicsData> {
        return try {
            val response = api.getLearningResourceTopics()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null && body.data.topics.isNotEmpty()) {
                Result.success(body.data)
            } else {
                Result.success(
                    LearningResourceTopicsData(
                        count = defaultSupportedTopics.size,
                        categories = defaultCategories,
                        topics = defaultSupportedTopics
                    )
                )
            }
        } catch (_: Exception) {
            // Gracefully serve the built-in 20-topic selector even when offline
            Result.success(
                LearningResourceTopicsData(
                    count = defaultSupportedTopics.size,
                    categories = defaultCategories,
                    topics = defaultSupportedTopics
                )
            )
        }
    }

    suspend fun getResourceForTopic(
        topic: String,
        forceRefresh: Boolean = false
    ): Result<LearningResourceData> {
        val cleaned = topic.trim()
        if (cleaned.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter or select a DSA topic."))
        }

        return try {
            val response = api.getLearningResource(
                topic = cleaned,
                refresh = if (forceRefresh) 1 else null
            )
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                val errMsg = parseErrorMessage(response.errorBody()?.string())
                    ?: body?.message
                    ?: when (response.code()) {
                        404 -> "No learning resource found for \"$cleaned\". Try choosing one of the supported DSA topics."
                        503, 504 -> "The learning resource service is temporarily unavailable. Please try again."
                        else -> "Unable to load learning resource (Error ${response.code()})."
                    }
                if (response.code() == 404) {
                    Result.failure(ResourceNotFoundException(errMsg))
                } else {
                    Result.failure(Exception(errMsg))
                }
            }
        } catch (_: SocketTimeoutException) {
            Result.failure(Exception("Request timed out while fetching learning resources. Please retry."))
        } catch (_: UnknownHostException) {
            Result.failure(Exception("Cannot reach the AlgoQuest server. Check your network connection."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to retrieve learning resources."))
        }
    }

    private fun parseErrorMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) return null
        return try {
            val obj = JSONObject(rawError)
            obj.optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
