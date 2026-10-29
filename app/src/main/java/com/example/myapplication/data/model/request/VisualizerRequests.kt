package com.example.myapplication.data.model.request

import com.google.gson.annotations.SerializedName

data class VisualizerAiRequest(
    @SerializedName("algorithm") val algorithm: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("array") val array: List<Int>,
    @SerializedName("target") val target: Int?,
    @SerializedName("current_step") val currentStep: Int,
    @SerializedName("total_steps") val totalSteps: Int,
    @SerializedName("description") val description: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("active_line") val activeLine: Int,
    @SerializedName("code_snippet") val codeSnippet: String,
    @SerializedName("variables") val variables: Map<String, String>,
    @SerializedName("action_type") val actionType: String,
    @SerializedName("user_question") val userQuestion: String = ""
)

