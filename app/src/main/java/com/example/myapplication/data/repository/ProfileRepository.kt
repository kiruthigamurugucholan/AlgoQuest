package com.example.myapplication.data.repository

import android.content.Context
import android.util.Log
import com.example.myapplication.data.model.request.ProfileUpdateRequest
import com.example.myapplication.data.model.response.AchievementDto
import com.example.myapplication.data.model.response.ApiResponse
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.model.response.ProfileUserDto
import com.example.myapplication.data.model.response.SkillMasteryDto
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.data.remote.ApiService
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.TokenManager
import com.google.gson.JsonParser
import retrofit2.Response

class ProfileRepository(context: Context) {
    private val api: ApiService = RetrofitClient.getApiService(context)
    private val tokenManager: TokenManager = TokenManager.getInstance(context)

    companion object {
        private const val TAG = "ProfileRepository"
    }

    suspend fun getProfile(): Result<ProfileData> {
        val cachedUser = tokenManager.getUser()
        val token = tokenManager.getToken()

        Log.d(TAG, "getProfile initiated. User ID: ${cachedUser?.id}, Token Present: ${!token.isNullOrBlank()}")

        // If no token and no cached user, authentication has not been restored or user is logged out
        if (token.isNullOrBlank() && cachedUser == null) {
            val errorMsg = "No active authentication session found. Please log in."
            Log.e(TAG, errorMsg)
            return Result.failure(Exception(errorMsg))
        }

        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                val data = response.body()!!.data!!
                Log.d(TAG, "Profile successfully loaded from server for user: ${data.user.name} (UID: ${data.user.id})")

                // Update cached user in TokenManager with fresh server-persisted stats
                cachedUser?.let { current ->
                    val updated = current.copy(
                        name = data.user.name,
                        xp = data.user.xp,
                        level = data.user.level,
                        streak = data.user.streak
                    )
                    tokenManager.saveAuth(token ?: "", updated)
                }

                Result.success(data)
            } else {
                val errorMsg = parseErrorMessage(response)
                Log.e(TAG, "Server returned error (HTTP ${response.code()}): $errorMsg")

                // If user is authenticated, fall back to cached profile so the user does not get stuck on a broken screen
                if (cachedUser != null) {
                    Log.i(TAG, "Falling back to cached profile for user UID: ${cachedUser.id}")
                    Result.success(buildCachedProfile(cachedUser))
                } else {
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception fetching profile: ${e.message}", e)
            if (cachedUser != null) {
                Log.i(TAG, "Falling back to cached profile for user UID: ${cachedUser.id} due to network exception")
                Result.success(buildCachedProfile(cachedUser))
            } else {
                val friendlyMsg = when (e) {
                    is java.net.ConnectException -> "Cannot connect to server. Ensure Apache and MySQL are running in XAMPP."
                    is java.net.SocketTimeoutException -> "Server connection timed out. Please check your network."
                    else -> e.localizedMessage ?: "Failed to load profile. Please tap Retry."
                }
                Result.failure(Exception(friendlyMsg))
            }
        }
    }

    private fun buildCachedProfile(user: UserDto): ProfileData {
        val name = user.name.ifBlank { "Explorer" }
        val parts = name.trim().split(" ")
        val initials = parts.mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifBlank { "AQ" }

        val profileUser = ProfileUserDto(
            id = user.id,
            name = name,
            email = user.email,
            initials = initials,
            joined = "Explorer",
            xp = user.xp,
            level = user.level,
            streak = user.streak,
            solvedLessons = 0,
            percentile = "Top 15% Global"
        )

        val skills = listOf(
            SkillMasteryDto("Array & Two Pointers", 0.40f, "40%", "green"),
            SkillMasteryDto("Searching Algorithms", 0.20f, "20%", "cyan"),
            SkillMasteryDto("Sorting Algorithms", 0.0f, "0%", "gold"),
            SkillMasteryDto("Trees & Graphs", 0.0f, "0%", "purple")
        )

        val achievements = listOf(
            AchievementDto(1, "first_step", "First Step", "Complete your first lesson", "🎯", 100, true, "Recently"),
            AchievementDto(2, "streak_3", "Streak Cadet", "Maintain a 3-day streak", "🔥", 150, user.streak >= 3, null),
            AchievementDto(3, "binary_searcher", "Binary Searcher", "Master Binary Search", "⚡", 250, false, null),
            AchievementDto(4, "quiz_master", "Quiz Master", "Score 100% on any chapter quiz", "🏆", 200, false, null)
        )

        return ProfileData(profileUser, skills, achievements)
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
            "Server error (HTTP ${response.code()})"
        } catch (_: Exception) {
            "Server error (HTTP ${response.code()})"
        }
    }

    suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val response = api.updateProfile(ProfileUpdateRequest(name))
            if (response.isSuccessful && response.body()?.success == true) {
                tokenManager.updateUserName(name)
                Result.success(Unit)
            } else {
                val msg = parseErrorMessage(response)
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating profile: ${e.message}", e)
            Result.failure(e)
        }
    }
}
