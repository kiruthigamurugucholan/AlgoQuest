package com.example.myapplication.data.remote

import com.example.myapplication.data.model.ai.PythonAiChatRequest
import com.example.myapplication.data.model.ai.PythonAiChatResponse
import com.example.myapplication.data.model.ai.PythonAiHealthResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

/**
 * Retrofit API service interface specifically for the Python Flask + Ollama AI Ally backend.
 * Completely separate from the PHP/MySQL backend ApiService.
 */
interface AiApiService {

    @POST("chat")
    suspend fun chat(@Body req: PythonAiChatRequest): Response<PythonAiChatResponse>

    @Streaming
    @POST("chat/stream")
    suspend fun chatStream(@Body req: PythonAiChatRequest): Response<ResponseBody>

    @GET("health")
    suspend fun health(): Response<PythonAiHealthResponse>
}
