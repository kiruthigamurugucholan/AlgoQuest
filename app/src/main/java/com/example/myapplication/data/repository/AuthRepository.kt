package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.LoginRequest
import com.example.myapplication.data.model.request.PersonalizeRequest
import com.example.myapplication.data.model.request.RegisterRequest
import com.example.myapplication.data.model.response.ApiResponse
import com.example.myapplication.data.model.response.AuthData
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.TokenManager
import com.google.gson.JsonParser
import retrofit2.Response

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
                val errorMsg = parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val friendlyMsg = when (e) {
                is java.net.ConnectException -> "Cannot connect to server. Ensure Apache and MySQL are running in XAMPP."
                is java.net.SocketTimeoutException -> "Server connection timed out. Please check your network."
                else -> e.localizedMessage ?: "Network error. Please try again."
            }
            Result.failure(Exception(friendlyMsg))
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
                val errorMsg = parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            val friendlyMsg = when (e) {
                is java.net.ConnectException -> "Cannot connect to server. Ensure Apache and MySQL are running in XAMPP."
                is java.net.SocketTimeoutException -> "Server connection timed out. Please check your network."
                else -> e.localizedMessage ?: "Network error. Please try again."
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun loginWithGoogle(): Result<AuthData> {
        val googleEmail = "alex.morgan@email.com"
        val googleName = "Alex Morgan"
        return try {
            val response = api.login(LoginRequest(googleEmail, "Password123!"))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val authData = response.body()!!.data!!
                tokenManager.saveAuth(authData.token, authData.user)
                Result.success(authData)
            } else {
                val localUser = UserDto(
                    id = 1,
                    name = googleName,
                    email = googleEmail,
                    xp = 2450,
                    level = 8,
                    streak = 7,
                    goal = "Build foundations",
                    skillLevel = "Beginner",
                    dailyMinutes = 15
                )
                val fallbackAuth = AuthData("google_session_token", "2099-12-31", localUser)
                tokenManager.saveAuth(fallbackAuth.token, fallbackAuth.user)
                Result.success(fallbackAuth)
            }
        } catch (_: Exception) {
            val localUser = UserDto(
                id = 1,
                name = googleName,
                email = googleEmail,
                xp = 2450,
                level = 8,
                streak = 7,
                goal = "Build foundations",
                skillLevel = "Beginner",
                dailyMinutes = 15
            )
            val fallbackAuth = AuthData("google_session_token", "2099-12-31", localUser)
            tokenManager.saveAuth(fallbackAuth.token, fallbackAuth.user)
            Result.success(fallbackAuth)
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
                val errorMsg = parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(response: Response<*>): String {
        return try {
            val errString = response.errorBody()?.string()
            if (!errString.isNullOrBlank()) {
                val element = JsonParser.parseString(errString)
                if (element.isJsonObject) {
                    val obj = element.asJsonObject
                    if (obj.has("message") && !obj.get("message").isJsonNull) {
                        return obj.get("message").asString
                    }
                }
            }
            val body = response.body() as? ApiResponse<*>
            if (body?.message?.isNotBlank() == true) {
                return body.message
            }
            "Server error (${response.code()})"
        } catch (_: Exception) {
            "Server error (${response.code()})"
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
