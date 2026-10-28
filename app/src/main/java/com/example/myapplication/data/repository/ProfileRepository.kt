package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.ProfileUpdateRequest
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.TokenManager

class ProfileRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)
    private val tokenManager: TokenManager = TokenManager.getInstance(context)

    suspend fun getProfile(): Result<ProfileData> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to load profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val response = api.updateProfile(ProfileUpdateRequest(name))
            if (response.isSuccessful && response.body()?.success == true) {
                tokenManager.updateUserName(name)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

