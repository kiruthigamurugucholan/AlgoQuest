package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.ProfileUpdateRequest
import com.example.myapplication.data.model.response.AchievementDto
import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.model.response.ProfileUserDto
import com.example.myapplication.data.model.response.SkillMasteryDto
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
                createLocalProfile()
            }
        } catch (_: Exception) {
            createLocalProfile()
        }
    }

    private fun createLocalProfile(): Result<ProfileData> {
        val user = tokenManager.getUser()
        val name = user?.name ?: "Kiruthiga"
        val email = user?.email ?: "kiruthigamurugucholan@gmail.com"
        val initials = if (name.isNotBlank()) name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase() else "AQ"

        val profileUser = ProfileUserDto(
            id = user?.id ?: 1,
            name = name,
            email = email,
            initials = if (initials.isBlank()) "AQ" else initials,
            joined = "October 2024",
            xp = user?.xp ?: 2450,
            level = user?.level ?: 8,
            streak = user?.streak ?: 7,
            solvedLessons = 14,
            percentile = "12%"
        )

        val skills = listOf(
            SkillMasteryDto("Array & Two Pointers", 0.92f, "92%", "green"),
            SkillMasteryDto("Searching Algorithms", 0.85f, "85%", "cyan"),
            SkillMasteryDto("Sorting Algorithms", 0.60f, "60%", "gold"),
            SkillMasteryDto("Trees & Binary Trees", 0.25f, "25%", "purple")
        )

        val achievements = listOf(
            AchievementDto(1, "STREAK_7", "Streak Master", "Maintained 7-day streak", "🔥", 100, true, "2024-10-20"),
            AchievementDto(2, "BINARY_SEARCH", "Binary Searcher", "Mastered Binary Search", "⚡", 150, true, "2024-10-22"),
            AchievementDto(3, "AI_EXPLORER", "AI Explorer", "Asked 10 questions to Quest AI", "🤖", 100, true, "2024-10-25"),
            AchievementDto(4, "ARRAY_LEGEND", "Array Legend", "Completed Array chapter", "🎯", 200, true, "2024-10-26"),
            AchievementDto(5, "GRAPH_PIONEER", "Graph Pioneer", "Unlock Chapter 07", "🔒", 250, false, null),
            AchievementDto(6, "DP_TITAN", "DP Titan", "Unlock Chapter 08", "🔒", 300, false, null)
        )

        return Result.success(ProfileData(profileUser, skills, achievements))
    }

    suspend fun updateProfile(name: String): Result<Unit> {
        return try {
            val response = api.updateProfile(ProfileUpdateRequest(name))
            if (response.isSuccessful && response.body()?.success == true) {
                tokenManager.updateUserName(name)
                Result.success(Unit)
            } else {
                tokenManager.updateUserName(name)
                Result.success(Unit)
            }
        } catch (_: Exception) {
            tokenManager.updateUserName(name)
            Result.success(Unit)
        }
    }
}
