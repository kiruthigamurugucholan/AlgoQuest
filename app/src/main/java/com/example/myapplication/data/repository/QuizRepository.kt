package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.QuizAnswerItem
import com.example.myapplication.data.model.request.QuizSubmitRequest
import com.example.myapplication.data.model.response.QuizDetailsDto
import com.example.myapplication.data.model.response.QuizResultData
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient

class QuizRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    suspend fun getQuiz(lessonId: Int): Result<QuizDetailsDto> {
        return try {
            val response = api.getQuiz(lessonId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.quiz)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load quiz"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitQuiz(quizId: Int, answers: List<QuizAnswerItem>): Result<QuizResultData> {
        return try {
            val response = api.submitQuiz(quizId, QuizSubmitRequest(answers))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to submit quiz"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

