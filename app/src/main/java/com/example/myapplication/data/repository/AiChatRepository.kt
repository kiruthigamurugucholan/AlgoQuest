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
                Result.failure(Exception(response.body()?.message ?: "Failed to get AI response"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(): Result<List<AiMessageDto>> {
        return try {
            val response = api.getAiHistory()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.messages)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load chat history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

