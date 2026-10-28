package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.AiChatRequest
import com.example.myapplication.data.model.response.AiMessageDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient

class AiChatRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    suspend fun sendMessage(message: String): Result<AiMessageDto> {
        return try {
            val response = api.sendAiChat(AiChatRequest(message))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.message)
            } else {
                createLocalAiReply(message)
            }
        } catch (_: Exception) {
            createLocalAiReply(message)
        }
    }

    private fun createLocalAiReply(query: String): Result<AiMessageDto> {
        val lower = query.lowercase()
        val (aiText, code, tags) = when {
            lower.contains("binary search") -> Triple(
                "Binary search works by repeatedly dividing in half the portion of the list that could contain the item.\n\nKey intuition: If the array is sorted, comparing the middle item eliminates 50% of remaining items instantly!",
                "fun binarySearch(arr: IntArray, target: Int): Int {\n    var low = 0; var high = arr.size - 1\n    while (low <= high) {\n        val mid = low + (high - low) / 2\n        if (arr[mid] == target) return mid\n        else if (arr[mid] < target) low = mid + 1\n        else high = mid - 1\n    }\n    return -1\n}",
                listOf("O(log N)", "Divide & Conquer")
            )
            lower.contains("log") || lower.contains("complexity") -> Triple(
                "O(log N) means log base 2 of N steps. For 1,000,000 items, binary search takes AT MOST 20 comparisons!\n\nFormula: 2^k = N -> k = log2(N)",
                "1,000 items   -> ~10 steps\n1,000,000     -> ~20 steps\n1,000,000,000 -> ~30 steps",
                listOf("Complexity", "Mathematics")
            )
            else -> Triple(
                "Great question! In algorithm design, we focus on identifying the underlying structure — whether it's linear scanning, divide & conquer, or dynamic programming.\n\nWould you like me to visualize this step-by-step for you?",
                null,
                listOf("Quest AI", "Concept breakdown")
            )
        }

        val msg = AiMessageDto(
            id = System.currentTimeMillis().toString(),
            sender = "ai",
            text = aiText,
            codeSnippet = code,
            timestamp = "Just now",
            tags = tags
        )
        return Result.success(msg)
    }

    suspend fun getHistory(): Result<List<AiMessageDto>> {
        return try {
            val response = api.getAiHistory()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.messages)
            } else {
                createLocalHistory()
            }
        } catch (_: Exception) {
            createLocalHistory()
        }
    }

    private fun createLocalHistory(): Result<List<AiMessageDto>> {
        val welcome = AiMessageDto(
            id = "1",
            sender = "ai",
            text = "Hey Explorer! I'm Quest AI, your personal Data Structures & Algorithms ally.\n\nAsk me anything — from binary search intuition to complex graph traversal algorithms!",
            codeSnippet = null,
            timestamp = "Just now",
            tags = listOf("AI Tutor", "DSA Ready")
        )
        return Result.success(listOf(welcome))
    }
}
