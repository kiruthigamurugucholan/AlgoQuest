package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.model.ai.PythonAiChatRequest
import com.example.myapplication.data.model.ai.PythonAiHistoryItem
import com.example.myapplication.data.model.ai.PythonAiStreamChunk
import com.example.myapplication.data.model.ai.PythonAiTimings
import com.example.myapplication.data.model.response.AiMessageDto
import com.example.myapplication.data.model.response.ChatSessionDto
import com.example.myapplication.data.model.response.CreateChatSessionRequest
import com.example.myapplication.data.model.response.PersistentChatMessageDto
import com.example.myapplication.data.model.response.SaveChatMessageRequest
import com.example.myapplication.data.remote.AiApiService
import com.example.myapplication.data.remote.AiConfig
import com.example.myapplication.data.remote.AiRetrofitClient
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Repository connecting AlgoQuest Android to the Python Flask + Ollama AI backend.
 *
 * Calls POST /chat on port 5001 running Qwen 3 (4B) and translates responses
 * into UI models for Quest AI Ally.
 */
class AiChatRepository @JvmOverloads constructor(
    private val context: Context? = null,
    private val api: AiApiService? = null,
    private val phpApi: ApiService? = null
) {
    companion object {
        private const val TAG = "AiChatRepository"
        private fun logD(msg: String) { try { Log.d(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logI(msg: String) { try { Log.i(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logW(msg: String) { try { Log.w(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logE(msg: String, tr: Throwable? = null) { try { Log.e(TAG, msg, tr) } catch (_: Throwable) { println("[$TAG] $msg: $tr") } }
    }

    private val activeApi: AiApiService
        get() = api ?: AiRetrofitClient.getAiApiService()

    private val activePhpApi: ApiService?
        get() = phpApi ?: (context?.let { RetrofitClient.getApiService(it) })

    private val sessionMessages = mutableListOf<AiMessageDto>()

    init {
        sessionMessages.add(
            AiMessageDto(
                id = "init_1",
                sender = "ai",
                text = "Hey explorer! I'm Quest AI, your personal Data Structures & Algorithms ally powered by Qwen 3 (4B) via Ollama.\n\nAsk me anything — from algorithm logic and complexity analysis to step-by-step code walkthroughs!",
                codeSnippet = null,
                timestamp = "Online",
                tags = listOf("Qwen 3 (4B)", "Ollama", "DSA Ready")
            )
        )
    }

    /**
     * Sends a DSA question to the Python Flask /chat endpoint on IO dispatcher.
     * Retries once on transient socket aborts using a fresh connection.
     */
    @JvmOverloads
    suspend fun sendMessage(
        message: String,
        history: List<PythonAiHistoryItem>? = null
    ): Result<AiMessageDto> = withContext(Dispatchers.IO) {
        val query = message.trim()
        if (query.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please provide a question."))
        }

        logD("sendMessage query: $query (history: ${history?.size ?: 0} turns)")
        var result = executeChatRequest(query, history)
        if (result.isFailure) {
            val ex = result.exceptionOrNull()
            logW("Initial chat request failed: ${ex?.javaClass?.simpleName}: ${ex?.message}")
            // Transient connection abort / reset — reset session and retry once with fresh socket
            if (ex is SocketException && ex !is ConnectException) {
                logI("Attempting single auto-retry with a fresh session...")
                AiRetrofitClient.resetSession()
                result = executeChatRequest(query, history)
                if (result.isSuccess) {
                    logI("Auto-retry succeeded!")
                } else {
                    logE("Auto-retry also failed: ${result.exceptionOrNull()?.message}")
                }
            }
        }
        result
    }

    /**
     * Streams tokens progressively from POST /chat/stream on the Python AI backend.
     * Emits incremental PythonAiStreamChunk payloads as Server-Sent Events arrive.
     */
    @JvmOverloads
    fun sendMessageStream(
        message: String,
        history: List<PythonAiHistoryItem>? = null
    ): Flow<PythonAiStreamChunk> = flow {
        val query = message.trim()
        if (query.isBlank()) {
            emit(PythonAiStreamChunk(error = "Please provide a question.", done = true))
            return@flow
        }

        logD("sendMessageStream executing for: $query (history: ${history?.size ?: 0} turns)")
        val response = try {
            activeApi.chatStream(PythonAiChatRequest(question = query, history = history))
        } catch (e: Exception) {
            logE("chatStream connection error: ${e.message}", e)
            emit(PythonAiStreamChunk(error = e.message ?: "Connection failure", done = true))
            return@flow
        }

        if (!response.isSuccessful || response.body() == null) {
            val code = response.code()
            val errorMsg = when (code) {
                504 -> "The AI model took too long to respond (server timeout 504)."
                400 -> "The question format was invalid (HTTP 400)."
                500 -> "The Python AI server encountered an internal error (HTTP 500)."
                else -> "AI Ally server error (HTTP $code): ${response.message()}"
            }
            logE("chatStream unsuccessful: HTTP $code - $errorMsg")
            emit(PythonAiStreamChunk(error = errorMsg, done = true))
            return@flow
        }

        val body = response.body()!!
        val gson = Gson()
        val reader = body.byteStream().bufferedReader()
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val l = line?.trim() ?: continue
                if (l.startsWith("data:")) {
                    val jsonStr = l.removePrefix("data:").trim()
                    if (jsonStr.isNotEmpty()) {
                        try {
                            val chunk = gson.fromJson(jsonStr, PythonAiStreamChunk::class.java)
                            if (chunk != null) {
                                emit(chunk)
                            }
                        } catch (e: Exception) {
                            logW("Failed to parse SSE JSON chunk: $jsonStr ($e)")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logE("Error reading SSE stream: ${e.message}", e)
            emit(PythonAiStreamChunk(error = e.message, done = true))
        } finally {
            try { reader.close() } catch (_: Exception) {}
            try { body.close() } catch (_: Exception) {}
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun executeChatRequest(
        query: String,
        history: List<PythonAiHistoryItem>? = null
    ): Result<AiMessageDto> {
        return try {
            val start = System.currentTimeMillis()
            logD("Executing POST /chat at ${AiConfig.getResolvedBaseUrl()}...")
            val response = activeApi.chat(PythonAiChatRequest(question = query, history = history))
            val durationMs = System.currentTimeMillis() - start
            logD("POST /chat responded in ${durationMs}ms with code ${response.code()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.status.equals("success", ignoreCase = true) && !body.answer.isNullOrBlank()) {
                    val (mainText, snippet) = extractCodeSnippet(body.answer)
                    val aiMessage = AiMessageDto(
                        id = "ai_${System.currentTimeMillis()}",
                        sender = "ai",
                        text = mainText,
                        codeSnippet = snippet,
                        timestamp = "Just now",
                        tags = listOf("Qwen 3 (4B)", "Ollama")
                    )
                    sessionMessages.add(aiMessage)
                    Result.success(aiMessage)
                } else {
                    val errorMsg = body.message ?: "AI backend returned an unexpected response."
                    logW("Server returned success status but error message: $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val code = response.code()
                val errorMsg = when (code) {
                    504 -> "The AI model took too long to respond (server timeout 504). Please try again with a more concise question."
                    400 -> "The question format was invalid (HTTP 400)."
                    500 -> "The Python AI server encountered an internal error (HTTP 500)."
                    else -> "AI Ally server error (HTTP $code): ${response.message()}"
                }
                logE("Unsuccessful response: code $code: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: SocketTimeoutException) {
            val timeoutMsg = "The AI model took too long to generate a response (connection timed out). Qwen 3 (4B) is running on CPU. Please try again with a simpler question."
            logE("SocketTimeoutException: $timeoutMsg", e)
            Result.failure(SocketTimeoutException(timeoutMsg))
        } catch (e: ConnectException) {
            val connectMsg = "Unable to connect to AI server at ${AiConfig.getResolvedBaseUrl()}. Please verify your Python Flask server is running on port 5001."
            logE("ConnectException: $connectMsg", e)
            Result.failure(ConnectException(connectMsg))
        } catch (e: SocketException) {
            val socketMsg = "Connection to AI server was interrupted (${e.message ?: "connection aborted"}). Please verify your device is connected to the same network as ${AiConfig.getResolvedBaseUrl()}."
            logE("SocketException: $socketMsg", e)
            Result.failure(SocketException(socketMsg))
        } catch (e: UnknownHostException) {
            val hostMsg = "Cannot resolve AI host at ${AiConfig.getResolvedBaseUrl()}."
            logE("UnknownHostException: $hostMsg", e)
            Result.failure(UnknownHostException(hostMsg))
        } catch (e: IOException) {
            val ioMsg = "Network error communicating with AI server: ${e.message ?: "I/O failure"}"
            logE("IOException: $ioMsg", e)
            Result.failure(IOException(ioMsg))
        } catch (e: Exception) {
            logE("Unhandled exception in executeChatRequest: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Returns in-memory session history of messages.
     */
    suspend fun getHistory(): Result<List<AiMessageDto>> {
        return Result.success(sessionMessages.toList())
    }

    fun setInMemoryMessages(messages: List<AiMessageDto>) {
        sessionMessages.clear()
        sessionMessages.addAll(messages)
    }

    /**
     * Fetches all chat sessions from MySQL via the PHP backend.
     */
    suspend fun fetchSessions(): Result<List<ChatSessionDto>> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val response = service.getAiSessions()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.sessions)
            } else {
                val err = response.body()?.message ?: "Failed to load chat sessions (HTTP ${response.code()})"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("fetchSessions error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Creates a new chat session in MySQL via the PHP backend.
     */
    suspend fun createSession(title: String? = null): Result<ChatSessionDto> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val response = service.createAiSession(CreateChatSessionRequest(title))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.session)
            } else {
                val err = response.body()?.message ?: "Failed to create chat session"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("createSession error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Fetches all messages for a specific session from MySQL.
     */
    suspend fun fetchSessionMessages(sessionId: Int): Result<Pair<ChatSessionDto, List<AiMessageDto>>> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val response = service.getAiSessionMessages(sessionId)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                val domainMessages = data.messages.map { m ->
                    val (displayText, snippet) = if (m.codeSnippet.isNullOrBlank() && m.sender == "ai") {
                        extractCodeSnippet(m.message)
                    } else {
                        Pair(m.message, m.codeSnippet)
                    }
                    AiMessageDto(
                        id = m.id,
                        sender = m.sender,
                        text = displayText,
                        codeSnippet = snippet,
                        timestamp = m.timestamp ?: "Saved",
                        tags = m.tags,
                        clientMessageId = m.clientMessageId
                    )
                }
                setInMemoryMessages(domainMessages)
                Result.success(Pair(data.session, domainMessages))
            } else {
                val err = response.body()?.message ?: "Failed to fetch session messages"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("fetchSessionMessages error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Persists a single message (user or AI) to MySQL via the PHP backend.
     * Uses clientMessageId for idempotency / duplicate prevention.
     */
    suspend fun saveMessageToDb(
        sessionId: Int,
        sender: String,
        message: String,
        clientMessageId: String? = null,
        codeSnippet: String? = null,
        tags: List<String>? = null
    ): Result<PersistentChatMessageDto> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val req = SaveChatMessageRequest(
                sender = sender,
                message = message,
                clientMessageId = clientMessageId,
                codeSnippet = codeSnippet,
                tags = tags
            )
            val response = service.saveAiChatMessage(sessionId, req)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.message)
            } else {
                val err = response.body()?.message ?: "Failed to persist chat message"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("saveMessageToDb error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Deletes a chat session and all its messages from MySQL.
     */
    suspend fun deleteSession(sessionId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val response = service.deleteAiSession(sessionId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val err = response.body()?.message ?: "Failed to delete chat session"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("deleteSession error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Renames a chat session title in MySQL.
     */
    suspend fun renameSession(sessionId: Int, newTitle: String): Result<ChatSessionDto> = withContext(Dispatchers.IO) {
        try {
            val service = activePhpApi ?: return@withContext Result.failure(IllegalStateException("API service not initialized"))
            val response = service.updateAiSession(sessionId, CreateChatSessionRequest(newTitle))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.session)
            } else {
                val err = response.body()?.message ?: "Failed to rename chat session"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            logW("renameSession error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Health check endpoint to verify Python Flask is reachable.
     */
    suspend fun checkHealth(): Result<String> {
        return try {
            val res = activeApi.health()
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.message ?: "AI server is healthy")
            } else {
                Result.failure(Exception("Health check returned HTTP ${res.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Extracts markdown code blocks (e.g. ```kotlin ... ```) into a separate snippet field
     * so that the UI can render it with syntax styling and the copy button.
     */
    fun extractCodeSnippet(fullAnswer: String): Pair<String, String?> {
        val codeFenceRegex = Regex("```(?:[a-zA-Z0-9_]*)\\n?([\\s\\S]*?)```")
        val match = codeFenceRegex.find(fullAnswer)
        return if (match != null) {
            val code = match.groupValues[1].trim()
            val textWithoutCode = fullAnswer.replace(match.value, "").trim()
            val displayText = if (textWithoutCode.isNotBlank()) textWithoutCode else "Here is the code implementation:"
            Pair(displayText, code)
        } else {
            Pair(fullAnswer.trim(), null)
        }
    }
}
