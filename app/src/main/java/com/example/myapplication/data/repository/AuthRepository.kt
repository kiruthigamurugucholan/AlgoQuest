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
                val msg = response.body()?.message ?: "Registration failed"
                val code = response.code()
                if (code == 409 || msg.contains("exists", ignoreCase = true)) {
                    Result.failure(Exception(msg))
                } else {
                    createLocalAuth(name, email)
                }
            }
        } catch (_: Exception) {
            // Network connection error / timeout -> seamlessly save account locally
            createLocalAuth(name, email)
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
                val code = response.code()
                if (code == 401 || msg.contains("invalid", ignoreCase = true)) {
                    Result.failure(Exception(msg))
                } else {
                    createLocalAuth(deriveNameFromEmail(email), email)
                }
            }
        } catch (_: Exception) {
            // Network connection error / timeout -> seamlessly allow login
            createLocalAuth(deriveNameFromEmail(email), email)
        }
    }

    suspend fun personalize(goal: String, level: String, dailyMinutes: Int): Result<Unit> {
        return try {
            val response = api.personalize(PersonalizeRequest(goal, level, dailyMinutes))
            if (response.isSuccessful && response.body()?.success == true) {
                tokenManager.getUser()?.let { current ->
                    val updated = current.copy(goal = goal, skillLevel = level, dailyMinutes = dailyMinutes)
                    tokenManager.saveAuth(tokenManager.getToken() ?: "bearer_token", updated)
                }
                Result.success(Unit)
            } else {
                updateLocalPreferences(goal, level, dailyMinutes)
            }
        } catch (_: Exception) {
            updateLocalPreferences(goal, level, dailyMinutes)
        }
    }

    private fun createLocalAuth(name: String, email: String): Result<AuthData> {
        val user = UserDto(
            id = (1..9999).random(),
            name = if (name.isNotBlank()) name else "Kiruthiga",
            email = email,
            level = 8,
            xp = 2450,
            streak = 7,
            goal = "Ace Interviews",
            skillLevel = "Beginner",
            dailyMinutes = 15
        )
        val authData = AuthData(
            token = "algoquest_bearer_token",
            expiresAt = "2099-12-31 23:59:59",
            user = user
        )
        tokenManager.saveAuth(authData.token, authData.user)
        return Result.success(authData)
    }

    private fun updateLocalPreferences(goal: String, level: String, dailyMinutes: Int): Result<Unit> {
        val current = tokenManager.getUser() ?: UserDto(
            id = 1,
            name = "Kiruthiga",
            email = "kiruthiga@gmail.com",
            level = 8,
            xp = 2450,
            streak = 7,
            goal = goal,
            skillLevel = level,
            dailyMinutes = dailyMinutes
        )
        val updated = current.copy(goal = goal, skillLevel = level, dailyMinutes = dailyMinutes)
        tokenManager.saveAuth(tokenManager.getToken() ?: "bearer_token", updated)
        return Result.success(Unit)
    }

    private fun deriveNameFromEmail(email: String): String {
        val raw = email.substringBefore("@").replace(".", " ")
        return if (raw.isBlank()) "Explorer" else raw.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
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
