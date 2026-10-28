package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.response.AlgorithmDto
import com.example.myapplication.data.model.response.LessonCompleteData
import com.example.myapplication.data.model.response.TopicDetailData
import com.example.myapplication.data.model.response.TopicItemDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient

class LearningRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    suspend fun getTopics(): Result<List<TopicItemDto>> {
        return try {
            val response = api.getTopics()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.topics)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load topics"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopicDetail(topicId: Int): Result<TopicDetailData> {
        return try {
            val response = api.getTopicDetail(topicId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load topic details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeLesson(lessonId: Int): Result<LessonCompleteData> {
        return try {
            val response = api.completeLesson(lessonId)
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to mark lesson complete"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlgorithms(): Result<List<AlgorithmDto>> {
        return try {
            val response = api.getAlgorithms()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.algorithms)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load algorithms"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

