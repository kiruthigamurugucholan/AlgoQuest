package com.example.myapplication.data.model.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class PersonalizeRequest(
    @SerializedName("goal") val goal: String,
    @SerializedName("level") val level: String,
    @SerializedName("daily_minutes") val dailyMinutes: Int
)

data class QuizSubmitRequest(
    @SerializedName("answers") val answers: List<QuizAnswerItem>
)

data class QuizAnswerItem(
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("option_id") val optionId: Int
)

data class AiChatRequest(
    @SerializedName("message") val message: String
)

data class ProfileUpdateRequest(
    @SerializedName("name") val name: String
)

