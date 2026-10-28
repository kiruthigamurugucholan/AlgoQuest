package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.LoginRequest
import com.example.myapplication.data.model.request.PersonalizeRequest
import com.example.myapplication.data.model.request.RegisterRequest
import com.example.myapplication.data.model.response.AuthData
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.TokenManager

class AuthRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)
    private val tokenManager: TokenManager = TokenManager.getInstance(context)

    suspend fun register(name: String, email: String, pass: String): Result<AuthData> {
        return try {
            val response = api.register(RegisterRequest(name, email, pass))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val authData = response.body()!!.data!!
                tokenManager.saveAuth(authData.token, authData.user)
                Result.success(authData)
            } else {
                val msg = response.body()?.message ?: response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<AuthData> {
        return try {
            val response = api.login(LoginRequest(email, pass))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val authData = response.body()!!.data!!
                tokenManager.saveAuth(authData.token, authData.user)
                Result.success(authData)
            } else {
                val msg = response.body()?.message ?: "Invalid email or password"
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun personalize(goal: String, level: String, dailyMinutes: Int): Result<Unit> {
        return try {
            val response = api.personalize(PersonalizeRequest(goal, level, dailyMinutes))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to save preferences"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            api.logout()
        } catch (_: Exception) {}
        tokenManager.clear()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getSavedUser(): UserDto? = tokenManager.getUser()
}

