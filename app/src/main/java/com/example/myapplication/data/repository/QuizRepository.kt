package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.request.QuizAnswerItem
import com.example.myapplication.data.model.request.QuizSubmitRequest
import com.example.myapplication.data.model.response.QuizDetailsDto
import com.example.myapplication.data.model.response.QuizOptionDto
import com.example.myapplication.data.model.response.QuizQuestionDto
import com.example.myapplication.data.model.response.QuizResultData
import com.example.myapplication.data.model.response.QuizReviewItemDto
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
                createLocalQuiz(lessonId)
            }
        } catch (_: Exception) {
            createLocalQuiz(lessonId)
        }
    }

    private fun createLocalQuiz(lessonId: Int): Result<QuizDetailsDto> {
        val q1 = QuizQuestionDto(
            id = 1,
            questionText = "What is the time complexity of Binary Search on a sorted array of size N?",
            orderIndex = 1,
            options = listOf(
                QuizOptionDto(1, 1, "O(N)"),
                QuizOptionDto(2, 1, "O(log N)"),
                QuizOptionDto(3, 1, "O(N^2)"),
                QuizOptionDto(4, 1, "O(1)")
            )
        )

        val q2 = QuizQuestionDto(
            id = 2,
            questionText = "What is a mandatory prerequisite before applying Binary Search?",
            orderIndex = 2,
            options = listOf(
                QuizOptionDto(5, 2, "Array must be empty"),
                QuizOptionDto(6, 2, "Array elements must be sorted"),
                QuizOptionDto(7, 2, "Array size must be an even number"),
                QuizOptionDto(8, 2, "Array must contain negative numbers")
            )
        )

        val q3 = QuizQuestionDto(
            id = 3,
            questionText = "How do we prevent integer overflow when calculating mid index in Java/Kotlin?",
            orderIndex = 3,
            options = listOf(
                QuizOptionDto(9, 3, "mid = (low + high) / 2"),
                QuizOptionDto(10, 3, "mid = low + (high - low) / 2"),
                QuizOptionDto(11, 3, "mid = high * 2"),
                QuizOptionDto(12, 3, "mid = low - high")
            )
        )

        val quiz = QuizDetailsDto(
            id = 1,
            lessonId = lessonId,
            title = "Chapter 03 Mastery Challenge",
            description = "Test your Binary Search invariants and index calculation instincts.",
            passPercentage = 70,
            questions = listOf(q1, q2, q3)
        )

        return Result.success(quiz)
    }

    suspend fun submitQuiz(quizId: Int, answers: List<QuizAnswerItem>): Result<QuizResultData> {
        return try {
            val response = api.submitQuiz(quizId, QuizSubmitRequest(answers))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                createLocalResult(answers)
            }
        } catch (_: Exception) {
            createLocalResult(answers)
        }
    }

    private fun createLocalResult(answers: List<QuizAnswerItem>): Result<QuizResultData> {
        val correctAnswers = mapOf(1 to 2, 2 to 6, 3 to 10)
        var correctCount = 0

        val reviews = answers.map { item ->
            val correctOptionId = correctAnswers[item.questionId] ?: 2
            val isCorrect = item.optionId == correctOptionId
            if (isCorrect) correctCount++

            QuizReviewItemDto(
                questionId = item.questionId,
                questionText = when (item.questionId) {
                    1 -> "What is the time complexity of Binary Search?"
                    2 -> "What is a mandatory prerequisite?"
                    else -> "How to prevent integer overflow?"
                },
                selectedOptionId = item.optionId,
                correctOptionId = correctOptionId,
                correctOptionText = when (correctOptionId) {
                    2 -> "O(log N)"
                    6 -> "Array elements must be sorted"
                    else -> "mid = low + (high - low) / 2"
                },
                explanation = "Binary Search halves the remaining search space every single comparison step.",
                isCorrect = isCorrect
            )
        }

        val total = answers.size.coerceAtLeast(3)
        val score = correctCount
        val percentage = ((score.toFloat() / total.toFloat()) * 100).toInt()
        val passed = percentage >= 70

        return Result.success(
            QuizResultData(
                score = score,
                totalQuestions = total,
                percentage = percentage,
                passed = passed,
                passPercentage = 70,
                xpAwarded = if (passed) 300 else 50,
                review = reviews
            )
        )
    }
}
