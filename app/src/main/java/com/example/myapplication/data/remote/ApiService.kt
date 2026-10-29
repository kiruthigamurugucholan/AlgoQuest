package com.example.myapplication.data.remote

import com.example.myapplication.data.model.request.AiChatRequest
import com.example.myapplication.data.model.request.LoginRequest
import com.example.myapplication.data.model.request.PersonalizeRequest
import com.example.myapplication.data.model.request.ProfileUpdateRequest
import com.example.myapplication.data.model.request.QuizSubmitRequest
import com.example.myapplication.data.model.request.RegisterRequest
import com.example.myapplication.data.model.response.AiChatData
import com.example.myapplication.data.model.response.AiHistoryData
import com.example.myapplication.data.model.response.AlgorithmsData
import com.example.myapplication.data.model.response.ApiResponse
import com.example.myapplication.data.model.response.AuthData
import com.example.myapplication.data.model.response.DashboardData
import com.example.myapplication.data.model.response.LessonCompleteData
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.model.response.QuizData
import com.example.myapplication.data.model.response.QuizResultData
import com.example.myapplication.data.model.response.TopicDetailData
import com.example.myapplication.data.model.response.TopicsData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body req: RegisterRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<ApiResponse<AuthData>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<Map<String, Any>>>

    @POST("user/personalize")
    suspend fun personalize(@Body req: PersonalizeRequest): Response<ApiResponse<Map<String, Any>>>

    @GET("dashboard")
    suspend fun getDashboard(): Response<ApiResponse<DashboardData>>

    @GET("topics")
    suspend fun getTopics(): Response<ApiResponse<TopicsData>>

    @GET("topics/{id}")
    suspend fun getTopicDetail(@Path("id") topicId: Int): Response<ApiResponse<TopicDetailData>>

    @POST("lessons/{id}/complete")
    suspend fun completeLesson(@Path("id") lessonId: Int): Response<ApiResponse<LessonCompleteData>>

    @GET("algorithms")
    suspend fun getAlgorithms(): Response<ApiResponse<AlgorithmsData>>

    @GET("quizzes/lesson/{lessonId}")
    suspend fun getQuiz(@Path("lessonId") lessonId: Int): Response<ApiResponse<QuizData>>

    @POST("quizzes/{id}/submit")
    suspend fun submitQuiz(
        @Path("id") quizId: Int,
        @Body req: QuizSubmitRequest
    ): Response<ApiResponse<QuizResultData>>

    @GET("profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileData>>

    @PUT("profile")
    suspend fun updateProfile(@Body req: ProfileUpdateRequest): Response<ApiResponse<Map<String, Any>>>

    @POST("ai/chat")
    suspend fun sendAiChat(@Body req: AiChatRequest): Response<ApiResponse<AiChatData>>

    @GET("ai/history")
    suspend fun getAiHistory(): Response<ApiResponse<AiHistoryData>>

    @POST("ai/visualizer-explain")
    suspend fun explainVisualizer(@Body req: com.example.myapplication.data.model.request.VisualizerAiRequest): Response<ApiResponse<com.example.myapplication.data.model.response.VisualizerAiData>>
}

