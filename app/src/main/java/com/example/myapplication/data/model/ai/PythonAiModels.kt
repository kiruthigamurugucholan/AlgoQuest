package com.example.myapplication.data.model.ai

import com.google.gson.annotations.SerializedName

/**
 * Request payload for Python Flask /chat endpoint.
 */
data class PythonAiChatRequest(
    @SerializedName("question") val question: String
)

/**
 * Response payload from Python Flask /chat endpoint.
 */
data class PythonAiChatResponse(
    @SerializedName("status") val status: String,
    @SerializedName("question") val question: String? = null,
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("message") val message: String? = null
)

/**
 * Health check response payload from Python Flask /health endpoint.
 */
data class PythonAiHealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null
)
