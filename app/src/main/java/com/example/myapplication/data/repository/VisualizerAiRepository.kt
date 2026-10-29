package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.VisualizerAiRequest
import com.example.myapplication.data.model.response.VisualizerAiData
import com.example.myapplication.data.remote.RetrofitClient

class VisualizerAiRepository(context: Context) {
    private val api = RetrofitClient.getApiService(context)

    suspend fun explainStep(request: VisualizerAiRequest): Result<VisualizerAiData> {
        return try {
            val response = api.explainVisualizer(request)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorMsg = response.body()?.message ?: "Unable to retrieve AI explanation (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

