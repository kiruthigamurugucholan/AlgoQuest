package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ai.PythonAiHistoryItem
import com.example.myapplication.data.model.ai.PythonAiTimings
import com.example.myapplication.data.model.response.AiMessageDto
import com.example.myapplication.data.model.response.ChatSessionDto
import com.example.myapplication.data.remote.AiConfig
import com.example.myapplication.data.repository.AiChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID

data class PendingSaveMessage(
    val sessionId: Int?,
    val sender: String,
    val message: String,
    val clientMessageId: String,
    val codeSnippet: String? = null,
    val tags: List<String>? = null,
    val sessionTitle: String? = null,
    val userQuestion: String? = null,
    val userClientMessageId: String? = null
)

val DEFAULT_AI_WELCOME_MESSAGE = AiMessageDto(
    id = "init_1",
    sender = "ai",
    text = "Hey explorer! I'm Quest AI, your personal Data Structures & Algorithms ally powered by Qwen 3 (4B) via Ollama.\n\nAsk me anything — from binary search invariants to two-pointer techniques!",
    codeSnippet = null,
    timestamp = "Online",
    tags = listOf("Qwen 3 (4B)", "Ollama", "DSA Ready")
)

data class AiChatUiState(
    val messages: List<AiMessageDto> = listOf(DEFAULT_AI_WELCOME_MESSAGE),
    val isThinking: Boolean = false,
    val error: String? = null,
    val currentSessionId: Int? = null,
    val currentSessionTitle: String? = "New Conversation",
    val sessions: List<ChatSessionDto> = emptyList(),
    val isLoadingSessions: Boolean = false,
    val pendingRetryMessage: PendingSaveMessage? = null
)

class AiChatViewModel @JvmOverloads constructor(
    application: Application,
    val repository: AiChatRepository = AiChatRepository(application)
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "AiChatViewModel"
        private fun logD(msg: String) { try { Log.d(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logI(msg: String) { try { Log.i(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logW(msg: String) { try { Log.w(TAG, msg) } catch (_: Throwable) { println("[$TAG] $msg") } }
        private fun logE(msg: String, tr: Throwable? = null) { try { Log.e(TAG, msg, tr) } catch (_: Throwable) { println("[$TAG] $msg: $tr") } }

        val DEFAULT_WELCOME_MESSAGE = DEFAULT_AI_WELCOME_MESSAGE
    }

    class Factory(
        private val application: Application,
        private val repository: AiChatRepository? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AiChatViewModel::class.java)) {
                val repo = repository ?: AiChatRepository(application)
                return AiChatViewModel(application, repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    private val _uiState = MutableStateFlow(
        AiChatUiState(
            messages = listOf(DEFAULT_WELCOME_MESSAGE),
            currentSessionTitle = "New Conversation"
        )
    )
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    private var activeJob: Job? = null

    /**
     * Loads chat sessions from MySQL into the session list for the history drawer.
     * By default, autoSelectLatest is false so that on app launch, a fresh New Conversation is shown.
     */
    fun loadSessions(autoSelectLatest: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSessions = true)
            repository.fetchSessions().fold(
                onSuccess = { sessionList ->
                    logI("Fetched ${sessionList.size} chat sessions from MySQL")
                    _uiState.value = _uiState.value.copy(
                        sessions = sessionList,
                        isLoadingSessions = false,
                        error = null
                    )
                    if (sessionList.isNotEmpty() && _uiState.value.currentSessionId == null && autoSelectLatest) {
                        selectSession(sessionList.first().id)
                    } else if (sessionList.isEmpty() && _uiState.value.currentSessionId == null) {
                        _uiState.value = _uiState.value.copy(messages = listOf(DEFAULT_WELCOME_MESSAGE))
                    }
                },
                onFailure = { err ->
                    logW("Failed to load sessions from MySQL: ${err.message}. Falling back to in-memory history.")
                    _uiState.value = _uiState.value.copy(
                        isLoadingSessions = false,
                        error = "Unable to connect to database (${err.message ?: "network error"}). Showing offline chat."
                    )
                    // Fall back to in-memory messages if MySQL is unreachable
                    repository.getHistory().onSuccess { historyList ->
                        if (historyList.isNotEmpty()) {
                            _uiState.value = _uiState.value.copy(messages = historyList)
                        }
                    }
                }
            )
        }
    }

    /**
     * Startup conversation initialization: loads saved sessions metadata from MySQL
     * into the history list without automatically selecting or loading any past conversation.
     * The active screen remains a fresh New Conversation with the default welcome message.
     */
    fun loadHistory() {
        loadSessions(autoSelectLatest = false)
    }

    /**
     * Switches to an existing session and loads all past messages in order.
     */
    fun selectSession(sessionId: Int) {
        activeJob?.cancel()
        activeJob = null

        viewModelScope.launch {
            repository.fetchSessionMessages(sessionId).fold(
                onSuccess = { (session, messages) ->
                    logI("Loaded ${messages.size} messages for session #$sessionId")
                    _uiState.value = _uiState.value.copy(
                        currentSessionId = session.id,
                        currentSessionTitle = session.title,
                        messages = if (messages.isNotEmpty()) messages else listOf(DEFAULT_WELCOME_MESSAGE),
                        error = null,
                        pendingRetryMessage = null
                    )
                },
                onFailure = { err ->
                    logE("Failed to load messages for session #$sessionId: ${err.message}")
                    _uiState.value = _uiState.value.copy(error = "Could not load conversation: ${err.message}")
                }
            )
        }
    }

    /**
     * Starts a fresh new chat session.
     */
    fun startNewChat() {
        activeJob?.cancel()
        activeJob = null
        repository.setInMemoryMessages(listOf(DEFAULT_WELCOME_MESSAGE))
        _uiState.value = _uiState.value.copy(
            currentSessionId = null,
            currentSessionTitle = "New Conversation",
            messages = listOf(DEFAULT_WELCOME_MESSAGE),
            error = null,
            pendingRetryMessage = null
        )
    }

    /**
     * Deletes a conversation session from MySQL and switches to another if active.
     */
    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            repository.deleteSession(sessionId).fold(
                onSuccess = {
                    logI("Deleted session #$sessionId")
                    val updated = _uiState.value.sessions.filter { it.id != sessionId }
                    val isCurrentDeleted = _uiState.value.currentSessionId == sessionId
                    _uiState.value = _uiState.value.copy(sessions = updated)

                    if (isCurrentDeleted) {
                        startNewChat()
                    }
                },
                onFailure = { err ->
                    logE("Failed to delete session #$sessionId: ${err.message}")
                    _uiState.value = _uiState.value.copy(error = "Failed to delete session: ${err.message}")
                }
            )
        }
    }

    /**
     * Renames an existing chat session.
     */
    fun renameSession(sessionId: Int, newTitle: String) {
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            repository.renameSession(sessionId, trimmed).fold(
                onSuccess = { updatedSession ->
                    val updatedList = _uiState.value.sessions.map { s ->
                        if (s.id == sessionId) s.copy(title = updatedSession.title) else s
                    }
                    val currentTitle = if (_uiState.value.currentSessionId == sessionId) updatedSession.title else _uiState.value.currentSessionTitle
                    _uiState.value = _uiState.value.copy(
                        sessions = updatedList,
                        currentSessionTitle = currentTitle
                    )
                },
                onFailure = { err ->
                    logE("Failed to rename session: ${err.message}")
                    _uiState.value = _uiState.value.copy(error = "Could not rename session: ${err.message}")
                }
            )
        }
    }

    /**
     * Retries saving a pending message that failed during background MySQL synchronization.
     */
    fun retryPendingSave() {
        val pending = _uiState.value.pendingRetryMessage ?: return
        viewModelScope.launch {
            if (pending.sessionId != null) {
                repository.saveMessageToDb(
                    sessionId = pending.sessionId,
                    sender = pending.sender,
                    message = pending.message,
                    clientMessageId = pending.clientMessageId,
                    codeSnippet = pending.codeSnippet,
                    tags = pending.tags
                ).fold(
                    onSuccess = {
                        logI("Pending message synced successfully to MySQL!")
                        _uiState.value = _uiState.value.copy(pendingRetryMessage = null, error = null)
                        repository.fetchSessions().onSuccess { sList ->
                            _uiState.value = _uiState.value.copy(sessions = sList)
                        }
                    },
                    onFailure = { err ->
                        logW("Retry sync still failed: ${err.message}")
                        _uiState.value = _uiState.value.copy(error = "Retry sync failed: ${err.message}")
                    }
                )
            } else {
                // First create the session, then persist user question and AI answer
                val title = pending.sessionTitle ?: "New Conversation"
                repository.createSession(title).fold(
                    onSuccess = { created ->
                        val sid = created.id
                        _uiState.value = _uiState.value.copy(
                            currentSessionId = sid,
                            currentSessionTitle = created.title
                        )
                        // Save user message if available
                        if (!pending.userQuestion.isNullOrBlank()) {
                            repository.saveMessageToDb(
                                sessionId = sid,
                                sender = "user",
                                message = pending.userQuestion,
                                clientMessageId = pending.userClientMessageId
                            )
                        }
                        // Save AI message
                        repository.saveMessageToDb(
                            sessionId = sid,
                            sender = pending.sender,
                            message = pending.message,
                            clientMessageId = pending.clientMessageId,
                            codeSnippet = pending.codeSnippet,
                            tags = pending.tags
                        )
                        _uiState.value = _uiState.value.copy(pendingRetryMessage = null, error = null)
                        repository.fetchSessions().onSuccess { sList ->
                            _uiState.value = _uiState.value.copy(sessions = sList)
                        }
                    },
                    onFailure = { err ->
                        logW("Retry creating session failed: ${err.message}")
                        _uiState.value = _uiState.value.copy(error = "Retry sync failed: ${err.message}")
                    }
                )
            }
        }
    }

    /**
     * Sends a question to Quest AI Ally:
     * 1. Creates a session in MySQL if none active.
     * 2. Persists user question with unique client_message_id (idempotent).
     * 3. Fetches verified DSA answer from Python Flask + Ollama Qwen 3 (4B).
     * 4. Persists AI answer to MySQL. If persistence fails, still renders answer with retry option.
     */
    fun sendMessage(query: String) {
        val trimmed = query.trim()

        if (trimmed.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a question.")
            return
        }

        if (_uiState.value.isThinking || activeJob?.isActive == true) {
            logD("sendMessage ignored: request already in progress")
            return
        }

        logD("sendMessage: $trimmed")

        // 1. Unique client message ID for idempotency & duplicate prevention
        val userClientMsgId = "usr_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"

        // 2. Post user message to UI immediately
        val userMsg = AiMessageDto(
            id = "user_${System.currentTimeMillis()}",
            sender = "user",
            text = trimmed,
            codeSnippet = null,
            timestamp = "Just now",
            clientMessageId = userClientMsgId
        )
        val updated = _uiState.value.messages.toMutableList().apply { add(userMsg) }
        _uiState.value = _uiState.value.copy(
            messages = updated,
            isThinking = true,
            error = null
        )

        // 3. Launch background generation and persistence
        activeJob = viewModelScope.launch {
            try {
                // Ensure active session exists in MySQL
                var activeSessionId = _uiState.value.currentSessionId
                var userMessageSaved = false
                val autoTitle = if (trimmed.length > 40) trimmed.take(37) + "..." else trimmed

                if (activeSessionId == null) {
                    repository.createSession(autoTitle).fold(
                        onSuccess = { created ->
                            activeSessionId = created.id
                            _uiState.value = _uiState.value.copy(
                                currentSessionId = created.id,
                                currentSessionTitle = created.title
                            )
                            repository.fetchSessions().onSuccess { sList ->
                                _uiState.value = _uiState.value.copy(sessions = sList)
                            }
                        },
                        onFailure = { err ->
                            logW("Could not auto-create session in MySQL: ${err.message}")
                        }
                    )
                }

                // Asynchronously save user question to MySQL if session is available
                activeSessionId?.let { sid ->
                    repository.saveMessageToDb(
                        sessionId = sid,
                        sender = "user",
                        message = trimmed,
                        clientMessageId = userClientMsgId
                    ).onSuccess {
                        userMessageSaved = true
                    }
                }

                // Extract recent conversation history (last 4-6 turns, max 600 chars each)
                val historyItems = _uiState.value.messages
                    .filter { m ->
                        m.id != "init_1" &&
                        m.id != userMsg.id &&
                        m.timestamp != "Online" &&
                        m.timestamp != "Generating..." &&
                        !m.text.startsWith("Error:") &&
                        m.text.isNotBlank()
                    }
                    .takeLast(6)
                    .map { m ->
                        val role = if (m.sender.equals("user", ignoreCase = true)) "user" else "assistant"
                        val fullText = if (!m.codeSnippet.isNullOrBlank()) {
                            "${m.text}\n```python\n${m.codeSnippet}\n```"
                        } else {
                            m.text
                        }
                        val content = if (fullText.length > 600) fullText.take(597) + "..." else fullText
                        PythonAiHistoryItem(role = role, content = content)
                    }

                // 4. Progressive SSE streaming from Python Flask /chat/stream
                val aiMsgId = "ai_${System.currentTimeMillis()}"
                val accumulated = StringBuilder()
                var firstTokenReceived = false
                var streamFailed = false
                var streamErrorMsg: String? = null
                var streamAnswer: String? = null
                var streamTimings: PythonAiTimings? = null

                try {
                    repository.sendMessageStream(trimmed, historyItems).collect { chunk ->
                        if (!chunk.error.isNullOrBlank()) {
                            streamFailed = true
                            streamErrorMsg = chunk.error
                            return@collect
                        }

                        if (!chunk.token.isNullOrEmpty()) {
                            accumulated.append(chunk.token)
                            if (!firstTokenReceived) {
                                firstTokenReceived = true
                                val provisionalAi = AiMessageDto(
                                    id = aiMsgId,
                                    sender = "ai",
                                    text = accumulated.toString(),
                                    codeSnippet = null,
                                    timestamp = "Generating...",
                                    tags = listOf("Qwen 3 (4B)", "Streaming")
                                )
                                val withProvisional = _uiState.value.messages.toMutableList().apply { add(provisionalAi) }
                                _uiState.value = _uiState.value.copy(
                                    messages = withProvisional,
                                    isThinking = false
                                )
                            } else {
                                val streamedMessages = _uiState.value.messages.map { m ->
                                    if (m.id == aiMsgId) m.copy(text = accumulated.toString()) else m
                                }
                                _uiState.value = _uiState.value.copy(messages = streamedMessages)
                            }
                        }

                        if (chunk.done) {
                            if (!chunk.answer.isNullOrBlank()) {
                                streamAnswer = chunk.answer
                            }
                            streamTimings = chunk.timings
                        }
                    }
                } catch (e: Exception) {
                    logW("Stream collection exception: ${e.message}")
                    streamFailed = true
                    streamErrorMsg = e.message
                }

                // 5. Handle stream completion or fallback to non-streaming
                if (!streamFailed && (firstTokenReceived || !streamAnswer.isNullOrBlank())) {
                    val rawAnswer = streamAnswer ?: accumulated.toString()
                    val (displayText, snippet) = repository.extractCodeSnippet(rawAnswer)

                    val tags = mutableListOf("Qwen 3 (4B)")
                    streamTimings?.tokensPerSecond?.let { spd ->
                        if (spd > 0) tags.add(String.format(java.util.Locale.US, "%.1f tok/s", spd))
                    }
                    streamTimings?.ttftMs?.let { ttft ->
                        if (ttft > 0) tags.add("${ttft}ms TTFT")
                    }

                    val finalAi = AiMessageDto(
                        id = aiMsgId,
                        sender = "ai",
                        text = displayText,
                        codeSnippet = snippet,
                        timestamp = "Just now",
                        tags = tags
                    )

                    val finalized = _uiState.value.messages.map { m ->
                        if (m.id == aiMsgId) finalAi else m
                    }
                    _uiState.value = _uiState.value.copy(
                        messages = finalized,
                        isThinking = false,
                        error = null
                    )

                    // Persist final AI answer to MySQL exactly once
                    persistAiMessage(
                        aiMsg = finalAi,
                        autoTitle = autoTitle,
                        trimmed = trimmed,
                        userClientMsgId = userClientMsgId,
                        activeSessionId = activeSessionId,
                        userMessageSaved = userMessageSaved
                    )
                } else {
                    // Fallback to non-streaming /chat endpoint
                    // Clean up any provisional message that was added during streaming before falling back
                    if (firstTokenReceived) {
                        val cleanedMessages = _uiState.value.messages.filter { it.id != aiMsgId }
                        _uiState.value = _uiState.value.copy(
                            messages = cleanedMessages,
                            isThinking = true
                        )
                    }
                    logW("Stream failed or yielded no tokens ($streamErrorMsg). Falling back to non-streaming POST /chat...")
                    repository.sendMessage(trimmed, historyItems).fold(
                        onSuccess = { aiMsg ->
                            logI("Fallback non-streaming AI generation succeeded (${aiMsg.text.length} chars)")
                            val withAi = _uiState.value.messages.toMutableList().apply { add(aiMsg) }
                            _uiState.value = _uiState.value.copy(
                                messages = withAi,
                                isThinking = false,
                                error = null
                            )
                            persistAiMessage(
                                aiMsg = aiMsg,
                                autoTitle = autoTitle,
                                trimmed = trimmed,
                                userClientMsgId = userClientMsgId,
                                activeSessionId = activeSessionId,
                                userMessageSaved = userMessageSaved
                            )
                        },
                        onFailure = { err ->
                            handleChatFailure(err)
                        }
                    )
                }
            } finally {
                activeJob = null
                if (_uiState.value.isThinking) {
                    _uiState.value = _uiState.value.copy(isThinking = false)
                }
            }
        }
    }

    private suspend fun persistAiMessage(
        aiMsg: AiMessageDto,
        autoTitle: String,
        trimmed: String,
        userClientMsgId: String,
        activeSessionId: Int?,
        userMessageSaved: Boolean
    ) {
        val aiClientMsgId = "ai_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
        var currentSid = activeSessionId ?: _uiState.value.currentSessionId
        var userSaved = userMessageSaved

        if (currentSid == null) {
            repository.createSession(autoTitle).onSuccess { created ->
                currentSid = created.id
                _uiState.value = _uiState.value.copy(
                    currentSessionId = created.id,
                    currentSessionTitle = created.title
                )
                if (!userSaved) {
                    repository.saveMessageToDb(
                        sessionId = created.id,
                        sender = "user",
                        message = trimmed,
                        clientMessageId = userClientMsgId
                    )
                    userSaved = true
                }
            }
        }

        if (currentSid != null) {
            if (!userSaved) {
                repository.saveMessageToDb(
                    sessionId = currentSid,
                    sender = "user",
                    message = trimmed,
                    clientMessageId = userClientMsgId
                )
            }
            repository.saveMessageToDb(
                sessionId = currentSid,
                sender = "ai",
                message = aiMsg.text,
                clientMessageId = aiClientMsgId,
                codeSnippet = aiMsg.codeSnippet,
                tags = aiMsg.tags
            ).fold(
                onSuccess = {
                    logI("AI message successfully saved to MySQL")
                    repository.fetchSessions().onSuccess { sList ->
                        _uiState.value = _uiState.value.copy(sessions = sList)
                    }
                },
                onFailure = { saveErr ->
                    logW("AI message generated but MySQL persistence failed: ${saveErr.message}")
                    _uiState.value = _uiState.value.copy(
                        pendingRetryMessage = PendingSaveMessage(
                            sessionId = currentSid,
                            sender = "ai",
                            message = aiMsg.text,
                            clientMessageId = aiClientMsgId,
                            codeSnippet = aiMsg.codeSnippet,
                            tags = aiMsg.tags
                        )
                    )
                }
            )
        } else {
            logW("Could not create session in MySQL for AI response, setting pending retry")
            _uiState.value = _uiState.value.copy(
                pendingRetryMessage = PendingSaveMessage(
                    sessionId = null,
                    sender = "ai",
                    message = aiMsg.text,
                    clientMessageId = aiClientMsgId,
                    codeSnippet = aiMsg.codeSnippet,
                    tags = aiMsg.tags,
                    sessionTitle = autoTitle,
                    userQuestion = trimmed,
                    userClientMessageId = userClientMsgId
                )
            )
        }

        try {
            com.example.myapplication.data.repository.ProfileRepository(getApplication())
                .getProfile()
                .onSuccess { profileData ->
                    com.example.myapplication.reminder.ReminderScheduler.checkAndNotifyAchievements(
                        getApplication(),
                        profileData
                    )
                }
        } catch (_: Exception) {}
    }

    private fun handleChatFailure(err: Throwable) {
        logE("AI generation failed: ${err.javaClass.simpleName}: ${err.message}")
        val userFriendlyMsg = when (err) {
            is SocketTimeoutException ->
                "The AI model took too long to respond. Qwen 3 (4B) is running on CPU. Please try again with a more focused question."
            is ConnectException ->
                "Unable to connect to AI server at ${AiConfig.getResolvedBaseUrl()}. Please make sure your Python Flask server is running on port 5001."
            is SocketException ->
                "Connection to the AI server was interrupted (connection aborted). Please make sure your device is connected to the same network as ${AiConfig.getResolvedBaseUrl()} and try again."
            is UnknownHostException ->
                "Cannot reach the AI host at ${AiConfig.getResolvedBaseUrl()}."
            else ->
                err.message ?: "Failed to get AI response."
        }

        val errorAiMsg = AiMessageDto(
            id = "err_${System.currentTimeMillis()}",
            sender = "ai",
            text = "⚠️ $userFriendlyMsg",
            codeSnippet = null,
            timestamp = "Error",
            tags = listOf("Offline / Error")
        )
        val withError = _uiState.value.messages.toMutableList().apply { add(errorAiMsg) }
        _uiState.value = _uiState.value.copy(
            messages = withError,
            isThinking = false,
            error = userFriendlyMsg
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearState() {
        activeJob?.cancel()
        activeJob = null
        repository.setInMemoryMessages(listOf(DEFAULT_WELCOME_MESSAGE))
        _uiState.value = AiChatUiState(
            messages = listOf(DEFAULT_WELCOME_MESSAGE),
            sessions = emptyList(),
            currentSessionId = null,
            currentSessionTitle = "New Conversation",
            isThinking = false,
            error = null,
            pendingRetryMessage = null
        )
    }

    override fun onCleared() {
        super.onCleared()
        activeJob?.cancel()
        activeJob = null
    }
}
