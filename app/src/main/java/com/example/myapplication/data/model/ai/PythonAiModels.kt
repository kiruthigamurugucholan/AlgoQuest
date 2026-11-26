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
    @SerializedName("message") val message: String? = null,
    @SerializedName("timings") val timings: PythonAiTimings? = null
)

/**
 * Streaming chunk payload from Python Flask /chat/stream SSE endpoint.
 */
data class PythonAiStreamChunk(
    @SerializedName("token") val token: String? = null,
    @SerializedName("done") val done: Boolean = false,
    @SerializedName("answer") val answer: String? = null,
    @SerializedName("timings") val timings: PythonAiTimings? = null,
    @SerializedName("error") val error: String? = null
)

/**
 * Timing metrics for model evaluation and latency measurements.
 */
data class PythonAiTimings(
    @SerializedName("load_duration_ms") val loadDurationMs: Long? = null,
    @SerializedName("ttft_ms") val ttftMs: Long? = null,
    @SerializedName("total_duration_ms") val totalDurationMs: Long? = null,
    @SerializedName("eval_count") val evalCount: Int? = null,
    @SerializedName("tokens_per_second") val tokensPerSecond: Double? = null
)

/**
 * Health check response payload from Python Flask /health endpoint.
 */
data class PythonAiHealthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null
)
