package com.example.myapplication

import com.example.myapplication.data.model.ai.PythonAiChatRequest
import com.example.myapplication.data.model.ai.PythonAiChatResponse
import com.example.myapplication.data.model.ai.PythonAiHistoryItem
import com.example.myapplication.data.model.ai.PythonAiHealthResponse
import com.example.myapplication.data.remote.AiApiService
import com.example.myapplication.data.remote.AiConfig
import com.example.myapplication.data.remote.AiRetrofitClient
import com.example.myapplication.data.repository.AiChatRepository
import com.example.myapplication.viewmodel.AiChatViewModel
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * Unit tests verifying the Python Flask + Ollama AI Ally integration,
 * including connection config, serialization, timeout handling, error reporting,
 * and markdown code block extraction.
 */
class PythonAiIntegrationTest {

    private val gson = Gson()

    @Before
    fun setup() {
        AiConfig.customBaseUrl = null
    }

    @Test
    fun testAiConfigUrlResolutionAndOverrides() {
        // Default emulator URL
        assertEquals("http://10.0.2.2:5001/", AiConfig.DEFAULT_EMULATOR_BASE_URL)

        // Custom URL override without trailing slash
        AiConfig.customBaseUrl = "http://192.168.1.100:5001"
        assertEquals("http://192.168.1.100:5001/", AiConfig.getResolvedBaseUrl())

        // Custom URL override with trailing slash
        AiConfig.customBaseUrl = "http://localhost:5001/"
        assertEquals("http://localhost:5001/", AiConfig.getResolvedBaseUrl())

        // Extended timeouts
        assertTrue("Connect timeout must be at least 15s", AiConfig.CONNECT_TIMEOUT_SECONDS >= 15L)
        assertTrue("Read timeout must be at least 600s for CPU Qwen model", AiConfig.READ_TIMEOUT_SECONDS >= 600L)
        assertTrue("Write timeout must be positive", AiConfig.WRITE_TIMEOUT_SECONDS > 0)
        assertTrue("Call timeout must be at least 600s", AiConfig.CALL_TIMEOUT_SECONDS >= 600L)
        assertEquals(660L, AiConfig.READ_TIMEOUT_SECONDS)
        assertEquals(660L, AiConfig.CALL_TIMEOUT_SECONDS)
    }

    @Test
    fun testPythonAiModelsSerialization() {
        // Request serialization
        val request = PythonAiChatRequest(question = "Explain bubble sort")
        val jsonReq = gson.toJson(request)
        assertTrue("JSON must contain question key", jsonReq.contains("\"question\":\"Explain bubble sort\""))

        // Response deserialization
        val sampleJson = """
            {
                "status": "success",
                "question": "Explain bubble sort",
                "answer": "Bubble sort repeatedly swaps adjacent elements."
            }
        """.trimIndent()
        val response = gson.fromJson(sampleJson, PythonAiChatResponse::class.java)
        assertEquals("success", response.status)
        assertEquals("Explain bubble sort", response.question)
        assertEquals("Bubble sort repeatedly swaps adjacent elements.", response.answer)
        assertNull(response.message)

        // Error response deserialization
        val errorJson = """
            {
                "status": "error",
                "message": "The AI model took too long to respond. Please try again."
            }
        """.trimIndent()
        val errResponse = gson.fromJson(errorJson, PythonAiChatResponse::class.java)
        assertEquals("error", errResponse.status)
        assertEquals("The AI model took too long to respond. Please try again.", errResponse.message)
        assertNull(errResponse.answer)
    }

    @Test
    fun testPythonAiChatRequestWithHistorySerialization() {
        val history = listOf(
            PythonAiHistoryItem(role = "user", content = "What is Merge Sort?"),
            PythonAiHistoryItem(role = "assistant", content = "Merge Sort is O(n log n)")
        )
        val request = PythonAiChatRequest(
            question = "Why does Merge Sort have O(n log n) time complexity?",
            history = history
        )
        val json = gson.toJson(request)
        assertTrue(json.contains("\"question\":\"Why does Merge Sort have O(n log n) time complexity?\""))
        assertTrue(json.contains("\"role\":\"user\""))
        assertTrue(json.contains("\"content\":\"What is Merge Sort?\""))
        assertTrue(json.contains("\"role\":\"assistant\""))

        val deserialized = gson.fromJson(json, PythonAiChatRequest::class.java)
        assertEquals("Why does Merge Sort have O(n log n) time complexity?", deserialized.question)
        assertEquals(2, deserialized.history?.size)
        assertEquals("user", deserialized.history?.get(0)?.role)
        assertEquals("What is Merge Sort?", deserialized.history?.get(0)?.content)
    }

    @Test
    fun testRepositoryParsesSuccessfulAnswerWithCodeSnippet() = runBlocking {
        val fakeApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                val fullAnswer = """
                    Binary search works by dividing the search interval in half.
                    ```kotlin
                    fun binarySearch(arr: IntArray, target: Int): Int {
                        var low = 0
                        var high = arr.size - 1
                        while (low <= high) {
                            val mid = low + (high - low) / 2
                            if (arr[mid] == target) return mid
                            else if (arr[mid] < target) low = mid + 1
                            else high = mid - 1
                        }
                        return -1
                    }
                    ```
                    Time complexity is O(log N).
                """.trimIndent()

                return Response.success(
                    PythonAiChatResponse(
                        status = "success",
                        question = req.question,
                        answer = fullAnswer
                    )
                )
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.error(404, "".toResponseBody("text/plain".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> {
                return Response.success(PythonAiHealthResponse(status = "success", message = "OK"))
            }
        }

        val repository = AiChatRepository(context = null, api = fakeApi)
        val result = repository.sendMessage("How does binary search work?")

        assertTrue("Result must be success", result.isSuccess)
        val msg = result.getOrNull()
        assertNotNull(msg)
        assertEquals("ai", msg!!.sender)
        assertTrue("Text must contain explanation", msg.text.contains("Binary search works"))
        assertNotNull("Code snippet should be extracted", msg.codeSnippet)
        assertTrue("Code snippet must contain Kotlin code", msg.codeSnippet!!.contains("fun binarySearch"))
        assertTrue("Tags should mention Qwen 3 (4B)", msg.tags.contains("Qwen 3 (4B)"))
    }

    @Test
    fun testRepositoryHandlesTimeoutGracefully() = runBlocking {
        val timingOutApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                throw SocketTimeoutException("Read timed out")
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> {
                throw SocketTimeoutException("Read timed out")
            }

            override suspend fun health(): Response<PythonAiHealthResponse> {
                throw SocketTimeoutException("Read timed out")
            }
        }

        val repository = AiChatRepository(context = null, api = timingOutApi)
        val result = repository.sendMessage("Deep question")

        assertTrue("Result must be failure", result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue("Exception must be SocketTimeoutException", exception is SocketTimeoutException)
        assertTrue("Message must mention CPU or timeout", exception!!.message!!.contains("timed out", ignoreCase = true))
    }

    @Test
    fun testRepositoryHandlesHttp504GatewayTimeout() = runBlocking {
        val timeout504Api = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                val errorBody = """{"status":"error","message":"The AI model took too long to respond. Qwen 3 (4B) is running on CPU."}"""
                return Response.error(504, errorBody.toResponseBody("application/json".toMediaType()))
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.error(504, "".toResponseBody("text/plain".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.error(504, "Timeout".toResponseBody("application/json".toMediaType()))
        }

        val repository = AiChatRepository(context = null, api = timeout504Api)
        val result = repository.sendMessage("Quick sort trace query")

        assertTrue("HTTP 504 must result in failure", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue("Error message must mention 504 or timeout", ex!!.message!!.contains("504") || ex.message!!.contains("timeout", ignoreCase = true))
    }

    @Test
    fun testRepositoryHandlesConnectionRefusedGracefully() = runBlocking {
        val offlineApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                throw ConnectException("Failed to connect to /10.0.2.2:5001")
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> {
                throw ConnectException("Failed to connect to /10.0.2.2:5001")
            }

            override suspend fun health(): Response<PythonAiHealthResponse> {
                throw ConnectException("Failed to connect to /10.0.2.2:5001")
            }
        }

        val repository = AiChatRepository(context = null, api = offlineApi)
        val result = repository.sendMessage("Test query")

        assertTrue("Result must be failure", result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue("Exception must be ConnectException", exception is ConnectException)
        assertTrue("Message must mention port 5001", exception!!.message!!.contains("5001"))
    }

    @Test
    fun testRepositoryHandlesConnectionAbortGracefullyAndRetries() = runBlocking {
        var callCount = 0
        val retryableApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                callCount++
                if (callCount == 1) {
                    throw SocketException("Software caused connection abort")
                }
                return Response.success(
                    PythonAiChatResponse(
                        status = "success",
                        question = req.question,
                        answer = "Recovered after transient connection abort!"
                    )
                )
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.error(404, "".toResponseBody("text/plain".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> {
                return Response.success(PythonAiHealthResponse(status = "success"))
            }
        }

        val repository = AiChatRepository(context = null, api = retryableApi)
        val result = repository.sendMessage("Testing transient abort")

        assertTrue("Result must succeed on retry", result.isSuccess)
        assertEquals("Must have called API twice due to retry", 2, callCount)
        val msg = result.getOrNull()
        assertNotNull(msg)
        assertEquals("Recovered after transient connection abort!", msg!!.text)
    }

    @Test
    fun testRepositoryHandlesPersistentSocketAbortWithUserFriendlyMessage() = runBlocking {
        val abortingApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                throw SocketException("Software caused connection abort")
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> {
                throw SocketException("Software caused connection abort")
            }

            override suspend fun health(): Response<PythonAiHealthResponse> {
                throw SocketException("Software caused connection abort")
            }
        }

        val repository = AiChatRepository(context = null, api = abortingApi)
        val result = repository.sendMessage("Testing persistent abort")

        assertTrue("Result must be failure when persistent", result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue("Exception must be SocketException", exception is SocketException)
        assertTrue("Message must mention connection interrupted or network", exception!!.message!!.contains("interrupted", ignoreCase = true))
    }

    @Test
    fun testRepositoryRejectsEmptyQuestion() = runBlocking {
        val fakeApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                return Response.success(PythonAiChatResponse(status = "success", answer = "ok"))
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.error(404, "".toResponseBody("text/plain".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.success(PythonAiHealthResponse(status = "success"))
        }

        val repository = AiChatRepository(context = null, api = fakeApi)

        val emptyResult = repository.sendMessage("")
        assertTrue("Empty question must fail", emptyResult.isFailure)

        val whitespaceResult = repository.sendMessage("   ")
        assertTrue("Whitespace question must fail", whitespaceResult.isFailure)
    }

    @Test
    fun testRepositoryHandlesBackendErrorResponse() = runBlocking {
        val errorApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                val errorBody = """{"status":"error","message":"Model out of memory"}"""
                return Response.error(500, errorBody.toResponseBody("application/json".toMediaType()))
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.error(500, "".toResponseBody("text/plain".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.error(500, "Error".toResponseBody("application/json".toMediaType()))
        }

        val repository = AiChatRepository(context = null, api = errorApi)
        val result = repository.sendMessage("Tricky question")

        assertTrue("HTTP 500 must result in failure", result.isFailure)
        val ex = result.exceptionOrNull()
        assertNotNull(ex)
        assertTrue("Error message must contain HTTP 500", ex!!.message!!.contains("500"))
    }

    @Test
    fun testAiChatViewModelHasApplicationConstructor() {
        // Guard against NoSuchMethodException: AiChatViewModel.<init> [class android.app.Application]
        val constructor = AiChatViewModel::class.java.getConstructor(android.app.Application::class.java)
        assertNotNull("AiChatViewModel must have (Application) constructor in bytecode", constructor)
    }

    @Test
    fun testAiChatViewModelHasTwoArgConstructor() {
        val constructor = AiChatViewModel::class.java.getConstructor(
            android.app.Application::class.java,
            AiChatRepository::class.java
        )
        assertNotNull("AiChatViewModel must have (Application, AiChatRepository) constructor in bytecode", constructor)
    }

    @Test
    fun testAiChatViewModelFactoryRejectsUnknownViewModel() {
        class OtherViewModel : ViewModel()
        // Create an instance without calling android framework methods on Application
        val dummyApp = java.lang.reflect.Proxy.newProxyInstance(
            android.app.Application::class.java.classLoader,
            emptyArray()
        ) { _, _, _ -> null } as? android.app.Application
            ?: android.app.Application()

        val factory = AiChatViewModel.Factory(dummyApp)
        try {
            factory.create(OtherViewModel::class.java)
            org.junit.Assert.fail("Factory must throw IllegalArgumentException for unknown ViewModel")
        } catch (e: IllegalArgumentException) {
            assertTrue("Exception message mentions unknown class", e.message!!.contains("Unknown ViewModel class"))
        }
    }

    @Test
    fun testKeepScreenOnFlagMatchesWindowManagerConstant() {
        assertEquals(
            "FLAG_KEEP_SCREEN_ON must match the Android WindowManager constant (0x80 = 128)",
            128,
            android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

    @Test
    fun testAiChatUiStateThinkingTransitions() {
        val initialState = com.example.myapplication.viewmodel.AiChatUiState()
        assertFalse("Initial isThinking must be false to avoid keeping screen awake on startup", initialState.isThinking)

        val thinkingState = initialState.copy(isThinking = true)
        assertTrue("When query starts, isThinking must be true to keep screen awake", thinkingState.isThinking)

        val completedState = thinkingState.copy(isThinking = false)
        assertFalse("When query finishes, isThinking must return to false to clear keep-screen-on flag", completedState.isThinking)

        val errorState = thinkingState.copy(isThinking = false, error = "Connection aborted")
        assertFalse("On error, isThinking must be false to clear keep-screen-on flag", errorState.isThinking)
        assertEquals("Connection aborted", errorState.error)
    }

    @Test
    fun testRepositoryStreamsTokensProgressively() = runBlocking {
        val sseContent = "data: {\"token\": \"Binary \", \"done\": false}\n\n" +
                "data: {\"token\": \"search \", \"done\": false}\n\n" +
                "data: {\"token\": \"walkthrough\", \"done\": false}\n\n" +
                "data: {\"token\": \"\", \"done\": true, \"answer\": \"Binary search walkthrough\", \"timings\": {\"ttft_ms\": 120, \"total_duration_ms\": 300, \"tokens_per_second\": 7.5}}\n\n"

        val streamingApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> {
                return Response.success(PythonAiChatResponse(status = "success", answer = "ok"))
            }

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> {
                return Response.success(sseContent.toResponseBody("text/event-stream".toMediaType()))
            }

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.success(PythonAiHealthResponse(status = "success"))
        }

        val repository = AiChatRepository(context = null, api = streamingApi)
        val chunks = mutableListOf<String>()
        var finalAnswer: String? = null
        var timings: com.example.myapplication.data.model.ai.PythonAiTimings? = null

        repository.sendMessageStream("Binary search").collect { chunk ->
            chunk.token?.let { if (it.isNotEmpty()) chunks.add(it) }
            if (chunk.done) {
                finalAnswer = chunk.answer
                timings = chunk.timings
            }
        }

        assertEquals(3, chunks.size)
        assertEquals("Binary ", chunks[0])
        assertEquals("search ", chunks[1])
        assertEquals("walkthrough", chunks[2])
        assertEquals("Binary search walkthrough", finalAnswer)
        assertNotNull(timings)
        assertEquals(120L, timings!!.ttftMs)
        assertEquals(7.5, timings!!.tokensPerSecond!!, 0.01)
    }

    @Test
    fun testRepositoryStreamsErrorBeforeFirstToken() = runBlocking {
        val sseErrorContent = "data: {\"error\": \"Server timeout 504\", \"done\": true}\n\n"

        val streamingApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> =
                Response.success(PythonAiChatResponse(status = "success", answer = "fallback answer"))

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.success(sseErrorContent.toResponseBody("text/event-stream".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.success(PythonAiHealthResponse(status = "success"))
        }

        val repository = AiChatRepository(context = null, api = streamingApi)
        val chunks = mutableListOf<com.example.myapplication.data.model.ai.PythonAiStreamChunk>()

        repository.sendMessageStream("Merge sort").collect { chunk ->
            chunks.add(chunk)
        }

        assertEquals(1, chunks.size)
        assertEquals("Server timeout 504", chunks[0].error)
        assertTrue(chunks[0].done)
    }

    @Test
    fun testRepositoryStreamsErrorAfterHeadingPrefill() = runBlocking {
        val sseErrorAfterHeading = "data: {\"token\": \"### Merge Sort Walkthrough\\n\\n\", \"done\": false}\n\n" +
                "data: {\"error\": \"500 Server Error: Ollama failed\", \"done\": true}\n\n"

        val streamingApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> =
                Response.success(PythonAiChatResponse(status = "success", answer = "fallback answer"))

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.success(sseErrorAfterHeading.toResponseBody("text/event-stream".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.success(PythonAiHealthResponse(status = "success"))
        }

        val repository = AiChatRepository(context = null, api = streamingApi)
        val chunks = mutableListOf<com.example.myapplication.data.model.ai.PythonAiStreamChunk>()

        repository.sendMessageStream("Merge sort").collect { chunk ->
            chunks.add(chunk)
        }

        assertEquals(2, chunks.size)
        assertEquals("### Merge Sort Walkthrough\n\n", chunks[0].token)
        assertFalse(chunks[0].done)
        assertEquals("500 Server Error: Ollama failed", chunks[1].error)
        assertTrue(chunks[1].done)
    }

    @Test
    fun testRepositoryStreamsErrorAfterMultipleTokens() = runBlocking {
        val sseErrorAfterTokens = "data: {\"token\": \"### Merge Sort\\n\\n\", \"done\": false}\n\n" +
                "data: {\"token\": \"Merge Sort divides\", \"done\": false}\n\n" +
                "data: {\"token\": \" the array\", \"done\": false}\n\n" +
                "data: {\"error\": \"Connection terminated mid-stream\", \"done\": true}\n\n"

        val streamingApi = object : AiApiService {
            override suspend fun chat(req: PythonAiChatRequest): Response<PythonAiChatResponse> =
                Response.success(PythonAiChatResponse(status = "success", answer = "fallback"))

            override suspend fun chatStream(req: PythonAiChatRequest): Response<okhttp3.ResponseBody> =
                Response.success(sseErrorAfterTokens.toResponseBody("text/event-stream".toMediaType()))

            override suspend fun health(): Response<PythonAiHealthResponse> =
                Response.success(PythonAiHealthResponse(status = "success"))
        }

        val repository = AiChatRepository(context = null, api = streamingApi)
        val chunks = mutableListOf<com.example.myapplication.data.model.ai.PythonAiStreamChunk>()

        repository.sendMessageStream("Merge sort").collect { chunk ->
            chunks.add(chunk)
        }

        assertEquals(4, chunks.size)
        assertEquals("Connection terminated mid-stream", chunks[3].error)
        assertTrue(chunks[3].done)
    }
}
