package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.response.DashboardData
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient

class DashboardRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)

    suspend fun getDashboard(): Result<DashboardData> {
        return try {
            val response = api.getDashboard()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load dashboard"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

