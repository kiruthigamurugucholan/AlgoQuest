package com.example.myapplication.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * Standard uniform API response wrapper matching PHP backend Response utility
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null,
    @SerializedName("errors") val errors: Map<String, String>? = null
)

