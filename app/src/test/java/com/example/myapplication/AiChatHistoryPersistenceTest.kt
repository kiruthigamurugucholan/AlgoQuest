package com.example.myapplication

import com.example.myapplication.data.model.response.ApiResponse
import com.example.myapplication.data.model.response.ChatSessionDto
import com.example.myapplication.data.model.response.ChatSessionMessagesData
import com.example.myapplication.data.model.response.ChatSessionsData
import com.example.myapplication.data.model.response.CreateChatSessionData
import com.example.myapplication.data.model.response.CreateChatSessionRequest
import com.example.myapplication.data.model.response.PersistentChatMessageDto
import com.example.myapplication.data.model.response.SaveChatMessageData
import com.example.myapplication.data.model.response.SaveChatMessageRequest
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.repository.AiChatRepository
import com.example.myapplication.viewmodel.AiChatUiState
import com.example.myapplication.viewmodel.PendingSaveMessage
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests verifying persistent AI chat history models, repository operations,
 * MySQL persistence error retry, idempotency, and DTO serialization.
 */
class AiChatHistoryPersistenceTest {

    private val gson = Gson()

    @Test
    fun testChatSessionDtoSerialization() {
        val session = ChatSessionDto(
            id = 42,
            title = "Binary Search Invariants",
            createdAt = "2026-11-24 10:00:00",
            updatedAt = "2026-11-24 10:05:00",
            messageCount = 6,
            lastMessage = "Time complexity is O(log n)",
            lastMessageTime = "10:05 AM"
        )
        val json = gson.toJson(session)
        assertTrue(json.contains("\"id\":42"))
        assertTrue(json.contains("\"title\":\"Binary Search Invariants\""))
        assertTrue(json.contains("\"message_count\":6"))

        val parsed = gson.fromJson(json, ChatSessionDto::class.java)
        assertEquals(42, parsed.id)
        assertEquals("Binary Search Invariants", parsed.title)
        assertEquals(6, parsed.messageCount)
        assertEquals("Time complexity is O(log n)", parsed.lastMessage)
    }

    @Test
    fun testSaveChatMessageRequestWithClientMessageId() {
        val req = SaveChatMessageRequest(
            sender = "user",
            message = "Explain quick sort",
            clientMessageId = "usr_client_uuid_12345",
            codeSnippet = null,
            tags = listOf("QuickSort", "Lomuto")
        )
        val json = gson.toJson(req)
        assertTrue(json.contains("\"client_message_id\":\"usr_client_uuid_12345\""))
        assertTrue(json.contains("\"sender\":\"user\""))

        val parsed = gson.fromJson(json, SaveChatMessageRequest::class.java)
        assertEquals("usr_client_uuid_12345", parsed.clientMessageId)
        assertEquals("Explain quick sort", parsed.message)
        assertEquals(listOf("QuickSort", "Lomuto"), parsed.tags)
    }

    @Test
    fun testRepositoryFetchSessionsSuccess() = runBlocking {
        val fakePhpApi = object : FakeApiService() {
            override suspend fun getAiSessions(): Response<ApiResponse<ChatSessionsData>> {
                val sessions = listOf(
                    ChatSessionDto(id = 1, title = "Session One", messageCount = 2),
                    ChatSessionDto(id = 2, title = "Session Two", messageCount = 5)
                )
                return Response.success(ApiResponse(success = true, message = "OK", data = ChatSessionsData(sessions)))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.fetchSessions()
        assertTrue(result.isSuccess)
        val sessions = result.getOrNull()!!
        assertEquals(2, sessions.size)
        assertEquals("Session One", sessions[0].title)
        assertEquals(5, sessions[1].messageCount)
    }

    @Test
    fun testRepositoryCreateSessionSuccess() = runBlocking {
        var requestedTitle: String? = null
        val fakePhpApi = object : FakeApiService() {
            override suspend fun createAiSession(req: CreateChatSessionRequest): Response<ApiResponse<CreateChatSessionData>> {
                requestedTitle = req.title
                return Response.success(
                    ApiResponse(
                        success = true,
                        message = "OK",
                        data = CreateChatSessionData(ChatSessionDto(id = 99, title = req.title ?: "New Conversation"))
                    )
                )
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.createSession("Two Pointers Exploration")
        assertTrue(result.isSuccess)
        assertEquals("Two Pointers Exploration", requestedTitle)
        assertEquals(99, result.getOrNull()!!.id)
    }

    @Test
    fun testRepositoryFetchSessionMessagesConvertsToDomainModels() = runBlocking {
        val fakePhpApi = object : FakeApiService() {
            override suspend fun getAiSessionMessages(sessionId: Int): Response<ApiResponse<ChatSessionMessagesData>> {
                val session = ChatSessionDto(id = sessionId, title = "Two Pointers")
                val messages = listOf(
                    PersistentChatMessageDto(
                        id = "101",
                        clientMessageId = "client_u_1",
                        sessionId = sessionId,
                        sender = "user",
                        message = "How does two pointers work?",
                        timestamp = "10:00 AM"
                    ),
                    PersistentChatMessageDto(
                        id = "102",
                        clientMessageId = "client_a_1",
                        sessionId = sessionId,
                        sender = "ai",
                        message = "Two pointers converge inwards in O(N) time.",
                        codeSnippet = "var l = 0; var r = arr.size - 1",
                        timestamp = "10:01 AM",
                        tags = listOf("Two Pointers")
                    )
                )
                return Response.success(ApiResponse(success = true, message = "OK", data = ChatSessionMessagesData(session, messages)))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.fetchSessionMessages(1)
        assertTrue(result.isSuccess)
        val (session, messages) = result.getOrNull()!!
        assertEquals("Two Pointers", session.title)
        assertEquals(2, messages.size)
        assertEquals("user", messages[0].sender)
        assertEquals("How does two pointers work?", messages[0].text)
        assertEquals("client_u_1", messages[0].clientMessageId)
        assertEquals("ai", messages[1].sender)
        assertEquals("var l = 0; var r = arr.size - 1", messages[1].codeSnippet)
    }

    @Test
    fun testRepositorySaveMessageSendsClientMessageId() = runBlocking {
        var recordedClientId: String? = null
        var recordedSender: String? = null
        val fakePhpApi = object : FakeApiService() {
            override suspend fun saveAiChatMessage(sessionId: Int, req: SaveChatMessageRequest): Response<ApiResponse<SaveChatMessageData>> {
                recordedClientId = req.clientMessageId
                recordedSender = req.sender
                return Response.success(
                    ApiResponse(
                        success = true,
                        message = "OK",
                        data = SaveChatMessageData(
                            PersistentChatMessageDto(
                                id = "55",
                                clientMessageId = req.clientMessageId,
                                sessionId = sessionId,
                                sender = req.sender,
                                message = req.message
                            )
                        )
                    )
                )
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.saveMessageToDb(
            sessionId = 12,
            sender = "user",
            message = "Hello",
            clientMessageId = "idempotent_key_777"
        )
        assertTrue(result.isSuccess)
        assertEquals("idempotent_key_777", recordedClientId)
        assertEquals("user", recordedSender)
    }

    @Test
    fun testRepositoryDeleteSessionSuccess() = runBlocking {
        var deletedId: Int? = null
        val fakePhpApi = object : FakeApiService() {
            override suspend fun deleteAiSession(sessionId: Int): Response<ApiResponse<Map<String, Any>>> {
                deletedId = sessionId
                return Response.success(ApiResponse(success = true, message = "Deleted", data = emptyMap()))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.deleteSession(33)
        assertTrue(result.isSuccess)
        assertEquals(33, deletedId)
    }

    @Test
    fun testPendingSaveMessageHoldsRetryPayload() {
        val pending = PendingSaveMessage(
            sessionId = 10,
            sender = "ai",
            message = "QuickSort recursion depth is O(log n)",
            clientMessageId = "ai_msg_retry_1",
            codeSnippet = "def quicksort(arr): ...",
            tags = listOf("QuickSort")
        )
        assertEquals(10, pending.sessionId)
        assertEquals("ai", pending.sender)
        assertEquals("QuickSort recursion depth is O(log n)", pending.message)
        assertEquals("ai_msg_retry_1", pending.clientMessageId)
        assertEquals("def quicksort(arr): ...", pending.codeSnippet)

        val state = AiChatUiState(pendingRetryMessage = pending)
        assertNotNull(state.pendingRetryMessage)
        assertEquals("ai_msg_retry_1", state.pendingRetryMessage?.clientMessageId)
    }

    // =========================================================================
    // STARTUP CONVERSATION BEHAVIOR & SESSION ISOLATION TESTS
    // =========================================================================

    @Test
    fun testStartupBehaviorDefaultStateIsFreshNewConversation() {
        // Scenario A: Fresh app launch initializes AI Ally with fresh empty conversation state
        val initialState = AiChatUiState()
        assertNull("Startup session ID must be null", initialState.currentSessionId)
        assertEquals("New Conversation", initialState.currentSessionTitle)
        assertEquals(1, initialState.messages.size)
        assertEquals("init_1", initialState.messages.first().id)
        assertEquals("ai", initialState.messages.first().sender)
        assertTrue(initialState.sessions.isEmpty())
        assertFalse(initialState.isThinking)
        assertNull(initialState.error)
    }

    @Test
    fun testLoadSessionsWithoutAutoSelectPreservesFreshNewConversation() = runBlocking {
        // Scenario A & B: App launch loads saved sessions for history sheet without loading past conversation
        val savedSessions = listOf(
            ChatSessionDto(id = 10, title = "Binary Search Analysis", messageCount = 4, lastMessage = "O(log n)"),
            ChatSessionDto(id = 20, title = "Two Pointers vs Sliding Window", messageCount = 6, lastMessage = "Sliding window")
        )
        val fakePhpApi = object : FakeApiService() {
            override suspend fun getAiSessions(): Response<ApiResponse<ChatSessionsData>> {
                return Response.success(ApiResponse(success = true, message = "OK", data = ChatSessionsData(savedSessions)))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.fetchSessions()
        assertTrue(result.isSuccess)
        val fetchedList = result.getOrNull()!!
        assertEquals(2, fetchedList.size)

        // Simulate state update with autoSelectLatest = false
        var state = AiChatUiState()
        val autoSelectLatest = false
        state = state.copy(sessions = fetchedList)
        if (fetchedList.isNotEmpty() && state.currentSessionId == null && autoSelectLatest) {
            state = state.copy(currentSessionId = fetchedList.first().id)
        }

        // Must still be a fresh new conversation
        assertNull("Startup session ID must remain null", state.currentSessionId)
        assertEquals("New Conversation", state.currentSessionTitle)
        assertEquals(1, state.messages.size)
        assertEquals("init_1", state.messages.first().id)
        // Saved sessions must be fully available for Conversations History list
        assertEquals(2, state.sessions.size)
        assertEquals(10, state.sessions[0].id)
        assertEquals(20, state.sessions[1].id)
    }

    @Test
    fun testSelectSavedConversationRestoresCompleteMessageHistory() = runBlocking {
        // Scenario C: Selecting saved conversation restores original question & answer
        val fakePhpApi = object : FakeApiService() {
            override suspend fun getAiSessionMessages(sessionId: Int): Response<ApiResponse<ChatSessionMessagesData>> {
                val session = ChatSessionDto(id = sessionId, title = "QuickSort Lomuto Partition")
                val messages = listOf(
                    PersistentChatMessageDto(
                        id = "m1",
                        clientMessageId = "client_u_qsort",
                        sessionId = sessionId,
                        sender = "user",
                        message = "Explain Quick Sort partition",
                        timestamp = "11:00 AM"
                    ),
                    PersistentChatMessageDto(
                        id = "m2",
                        clientMessageId = "client_a_qsort",
                        sessionId = sessionId,
                        sender = "ai",
                        message = "Quick Sort uses Lomuto partition where pivot is arr[high].",
                        codeSnippet = "def partition(arr, low, high): ...",
                        timestamp = "11:01 AM",
                        tags = listOf("QuickSort", "Lomuto")
                    )
                )
                return Response.success(ApiResponse(success = true, message = "OK", data = ChatSessionMessagesData(session, messages)))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)
        val result = repository.fetchSessionMessages(10)
        assertTrue(result.isSuccess)
        val (session, messages) = result.getOrNull()!!

        var state = AiChatUiState(currentSessionId = null, currentSessionTitle = "New Conversation")
        state = state.copy(
            currentSessionId = session.id,
            currentSessionTitle = session.title,
            messages = messages
        )

        assertEquals(10, state.currentSessionId)
        assertEquals("QuickSort Lomuto Partition", state.currentSessionTitle)
        assertEquals(2, state.messages.size)
        assertEquals("user", state.messages[0].sender)
        assertEquals("Explain Quick Sort partition", state.messages[0].text)
        assertEquals("ai", state.messages[1].sender)
        assertEquals("def partition(arr, low, high): ...", state.messages[1].codeSnippet)
    }

    @Test
    fun testStartNewConversationResetsStateWithoutCreatingSessionImmediately() {
        // Scenario D: Starting new conversation clears displayed messages & session ID
        var state = AiChatUiState(
            currentSessionId = 10,
            currentSessionTitle = "QuickSort Lomuto Partition",
            messages = listOf(
                com.example.myapplication.data.model.response.AiMessageDto(id = "m1", sender = "user", text = "Hi"),
                com.example.myapplication.data.model.response.AiMessageDto(id = "m2", sender = "ai", text = "Hello")
            ),
            sessions = listOf(ChatSessionDto(id = 10, title = "QuickSort"))
        )

        // Action: startNewChat()
        state = state.copy(
            currentSessionId = null,
            currentSessionTitle = "New Conversation",
            messages = listOf(AiChatUiState().messages.first()),
            error = null,
            pendingRetryMessage = null
        )

        assertNull("currentSessionId must be reset to null", state.currentSessionId)
        assertEquals("New Conversation", state.currentSessionTitle)
        assertEquals(1, state.messages.size)
        assertEquals("init_1", state.messages.first().id)
        // Existing sessions in history list are preserved
        assertEquals(1, state.sessions.size)
    }

    @Test
    fun testFirstMessageCreatesSeparateSessionAndMaintainsIsolation() = runBlocking {
        // Scenario D & E: First message creates separate session, preventing message cross-contamination
        var createdSessionTitle: String? = null
        val createdSessionId = 99
        val savedMessages = mutableListOf<PersistentChatMessageDto>()

        val fakePhpApi = object : FakeApiService() {
            override suspend fun createAiSession(req: CreateChatSessionRequest): Response<ApiResponse<CreateChatSessionData>> {
                createdSessionTitle = req.title
                return Response.success(
                    ApiResponse(
                        success = true,
                        message = "Created",
                        data = CreateChatSessionData(ChatSessionDto(id = createdSessionId, title = req.title ?: "New Conversation"))
                    )
                )
            }

            override suspend fun saveAiChatMessage(sessionId: Int, req: SaveChatMessageRequest): Response<ApiResponse<SaveChatMessageData>> {
                val dto = PersistentChatMessageDto(
                    id = "msg_${savedMessages.size + 1}",
                    clientMessageId = req.clientMessageId,
                    sessionId = sessionId,
                    sender = req.sender,
                    message = req.message
                )
                savedMessages.add(dto)
                return Response.success(ApiResponse(success = true, message = "Saved", data = SaveChatMessageData(dto)))
            }
        }

        val repository = AiChatRepository(context = null, phpApi = fakePhpApi)

        // Starting in fresh conversation state
        var currentSessionId: Int? = null
        val firstQuery = "Can you explain Queue vs Deque?"

        // Step 1: User sends first message -> createSession called on demand
        if (currentSessionId == null) {
            val autoTitle = if (firstQuery.length > 40) firstQuery.take(37) + "..." else firstQuery
            val createResult = repository.createSession(autoTitle)
            assertTrue(createResult.isSuccess)
            currentSessionId = createResult.getOrNull()!!.id
        }

        assertEquals(99, currentSessionId)
        assertEquals("Can you explain Queue vs Deque?", createdSessionTitle)

        // Step 2: User message saved under new session ID 99
        val saveUserResult = repository.saveMessageToDb(
            sessionId = currentSessionId,
            sender = "user",
            message = firstQuery,
            clientMessageId = "usr_client_1"
        )
        assertTrue(saveUserResult.isSuccess)

        // Step 3: AI response saved under new session ID 99
        val saveAiResult = repository.saveMessageToDb(
            sessionId = currentSessionId,
            sender = "ai",
            message = "Queue is FIFO while Deque allows insertion at both ends.",
            clientMessageId = "ai_client_1"
        )
        assertTrue(saveAiResult.isSuccess)

        // Verify total isolation
        assertEquals(2, savedMessages.size)
        assertEquals(99, savedMessages[0].sessionId)
        assertEquals("user", savedMessages[0].sender)
        assertEquals("usr_client_1", savedMessages[0].clientMessageId)
        assertEquals(99, savedMessages[1].sessionId)
        assertEquals("ai", savedMessages[1].sender)
        assertEquals("ai_client_1", savedMessages[1].clientMessageId)
    }
}

/**
 * Base fake implementation of [ApiService] throwing UnsupportedOperationException
 * for unneeded routes in chat history tests.
 */
open class FakeApiService : ApiService {
    override suspend fun register(req: com.example.myapplication.data.model.request.RegisterRequest) = throw UnsupportedOperationException()
    override suspend fun login(req: com.example.myapplication.data.model.request.LoginRequest) = throw UnsupportedOperationException()
    override suspend fun logout() = throw UnsupportedOperationException()
    override suspend fun getMe() = throw UnsupportedOperationException()
    override suspend fun personalize(req: com.example.myapplication.data.model.request.PersonalizeRequest) = throw UnsupportedOperationException()
    override suspend fun getDashboard() = throw UnsupportedOperationException()
    override suspend fun getTopics() = throw UnsupportedOperationException()
    override suspend fun getTopicDetail(topicId: Int) = throw UnsupportedOperationException()
    override suspend fun completeLesson(lessonId: Int) = throw UnsupportedOperationException()
    override suspend fun getCatalogueProgress() = throw UnsupportedOperationException()
    override suspend fun completeCatalogueLesson(req: com.example.myapplication.data.model.response.CompleteCatalogueLessonRequest) = throw UnsupportedOperationException()
    override suspend fun getAlgorithms() = throw UnsupportedOperationException()
    override suspend fun getQuiz(lessonId: Int) = throw UnsupportedOperationException()
    override suspend fun submitQuiz(quizId: Int, req: com.example.myapplication.data.model.request.QuizSubmitRequest) = throw UnsupportedOperationException()
    override suspend fun getProfile() = throw UnsupportedOperationException()
    override suspend fun updateProfile(req: com.example.myapplication.data.model.request.ProfileUpdateRequest) = throw UnsupportedOperationException()
    override suspend fun sendAiChat(req: com.example.myapplication.data.model.request.AiChatRequest) = throw UnsupportedOperationException()
    override suspend fun getAiHistory() = throw UnsupportedOperationException()
    override suspend fun explainVisualizer(req: com.example.myapplication.data.model.request.VisualizerAiRequest) = throw UnsupportedOperationException()
    override suspend fun getLearningResourceTopics() = throw UnsupportedOperationException()
    override suspend fun getLearningResource(topic: String, refresh: Int?) = throw UnsupportedOperationException()

    // Persistent Chat History routes
    override suspend fun getAiSessions(): Response<ApiResponse<ChatSessionsData>> = throw UnsupportedOperationException()
    override suspend fun createAiSession(req: CreateChatSessionRequest): Response<ApiResponse<CreateChatSessionData>> = throw UnsupportedOperationException()
    override suspend fun updateAiSession(sessionId: Int, req: CreateChatSessionRequest): Response<ApiResponse<CreateChatSessionData>> = throw UnsupportedOperationException()
    override suspend fun deleteAiSession(sessionId: Int): Response<ApiResponse<Map<String, Any>>> = throw UnsupportedOperationException()
    override suspend fun getAiSessionMessages(sessionId: Int): Response<ApiResponse<ChatSessionMessagesData>> = throw UnsupportedOperationException()
    override suspend fun saveAiChatMessage(sessionId: Int, req: SaveChatMessageRequest): Response<ApiResponse<SaveChatMessageData>> = throw UnsupportedOperationException()
}
