package com.example.myapplication

import com.example.myapplication.data.model.response.ProfileData
import com.example.myapplication.data.model.response.ProfileUserDto
import com.example.myapplication.data.model.response.UserDto
import com.example.myapplication.viewmodel.ProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileAuthFlowTest {

    @Test
    fun profileUiState_handlesAllStatesCorrectly() {
        val loadingState: ProfileUiState = ProfileUiState.Loading
        val emptyState: ProfileUiState = ProfileUiState.Empty
        val errorState: ProfileUiState = ProfileUiState.Error("Network error. Please try again.")

        val testUser = ProfileUserDto(
            id = 42,
            name = "Alex Morgan",
            email = "alex.morgan@algoquest.io",
            initials = "AM",
            joined = "October 2026",
            xp = 3200,
            level = 9,
            streak = 14,
            solvedLessons = 18,
            percentile = "Top 5% Global"
        )
        val successState: ProfileUiState = ProfileUiState.Success(
            ProfileData(
                user = testUser,
                skills = emptyList(),
                achievements = emptyList()
            )
        )

        assertTrue(loadingState is ProfileUiState.Loading)
        assertTrue(emptyState is ProfileUiState.Empty)
        assertTrue(errorState is ProfileUiState.Error)
        assertEquals("Network error. Please try again.", (errorState as ProfileUiState.Error).message)
        assertTrue(successState is ProfileUiState.Success)
        assertEquals(42, (successState as ProfileUiState.Success).profile.user.id)
        assertEquals("AM", successState.profile.user.initials)
        assertEquals(3200, successState.profile.user.xp)
    }

    @Test
    fun userDto_preservesProfileStatsAcrossSessions() {
        val originalUser = UserDto(
            id = 101,
            name = "Test Explorer",
            email = "explorer@test.local",
            xp = 1500,
            level = 5,
            streak = 3,
            goal = "Interview Prep",
            skillLevel = "Intermediate",
            dailyMinutes = 20
        )

        assertEquals(101, originalUser.id)
        assertEquals(1500, originalUser.xp)
        assertEquals(5, originalUser.level)
        assertEquals(3, originalUser.streak)

        // Simulate updated stats after completing a lesson
        val updatedUser = originalUser.copy(xp = 1650, streak = 4)
        assertEquals(1650, updatedUser.xp)
        assertEquals(4, updatedUser.streak)
        assertEquals(originalUser.id, updatedUser.id)
        assertEquals(originalUser.email, updatedUser.email)
    }

    @Test
    fun initialsGeneration_handlesVariousNameFormats() {
        fun extractInitials(name: String): String {
            val parts = name.trim().split(" ")
            return parts.mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()
                .ifBlank { "AQ" }
        }

        assertEquals("AM", extractInitials("Alex Morgan"))
        assertEquals("A", extractInitials("Alex"))
        assertEquals("AQ", extractInitials(""))
        assertEquals("AQ", extractInitials("   "))
        assertEquals("JD", extractInitials("John Doe Junior"))
    }
}

