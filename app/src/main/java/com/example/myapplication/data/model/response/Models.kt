package com.example.myapplication.data.model.response

import com.google.gson.annotations.SerializedName

// --- Auth & User ---
data class AuthData(
    @SerializedName("token") val token: String,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("user") val user: UserDto
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("xp") val xp: Int = 0,
    @SerializedName("level") val level: Int = 1,
    @SerializedName("streak") val streak: Int = 1,
    @SerializedName("goal") val goal: String? = null,
    @SerializedName("skill_level") val skillLevel: String? = null,
    @SerializedName("daily_minutes") val dailyMinutes: Int? = 15
)

// --- Dashboard ---
data class DashboardData(
    @SerializedName("user") val user: DashboardUserDto,
    @SerializedName("daily_quest") val dailyQuest: DailyQuestDto,
    @SerializedName("active_topic") val activeTopic: ActiveTopicDto?
)

data class DashboardUserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("xp") val xp: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("streak") val streak: Int,
    @SerializedName("percentile") val percentile: String,
    @SerializedName("total_lessons_solved") val totalLessonsSolved: Int
)

data class DailyQuestDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("xp_reward") val xpReward: Int,
    @SerializedName("current_step") val currentStep: Int,
    @SerializedName("required_steps") val requiredSteps: Int,
    @SerializedName("is_completed") val isCompleted: Boolean
)

data class ActiveTopicDto(
    @SerializedName("id") val id: Int,
    @SerializedName("chapter_number") val chapterNumber: Int,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("completed_lessons") val completedLessons: Int,
    @SerializedName("total_lessons") val totalLessons: Int
)

// --- Quest Map & Topics ---
data class TopicsData(
    @SerializedName("topics") val topics: List<TopicItemDto>
)

data class TopicItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("chapter_number") val chapterNumber: Int,
    @SerializedName("chapter_label") val chapterLabel: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("icon_type") val iconType: String,
    @SerializedName("completed_lessons") val completedLessons: Int,
    @SerializedName("total_lessons") val totalLessons: Int,
    @SerializedName("lessons_label") val lessonsLabel: String,
    @SerializedName("status") val status: String // "COMPLETED", "ACTIVE", "NEXT", "LOCKED"
)

// --- Chapter Detail ---
data class TopicDetailData(
    @SerializedName("topic") val topic: TopicHeaderDto,
    @SerializedName("lessons") val lessons: List<LessonItemDto>
)

data class TopicHeaderDto(
    @SerializedName("id") val id: Int,
    @SerializedName("chapter_number") val chapterNumber: Int,
    @SerializedName("chapter_label") val chapterLabel: String,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("progress_percentage") val progressPercentage: Int,
    @SerializedName("completed_lessons") val completedLessons: Int,
    @SerializedName("total_lessons") val totalLessons: Int
)

data class LessonItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("xp") val xp: String,
    @SerializedName("xp_reward") val xpReward: Int,
    @SerializedName("lesson_type") val lessonType: String, // "READING", "VISUALIZER", "QUIZ"
    @SerializedName("is_completed") val isCompleted: Boolean,
    @SerializedName("is_current") val isCurrent: Boolean,
    @SerializedName("is_locked") val isLocked: Boolean
)

data class LessonCompleteData(
    @SerializedName("xp_awarded") val xpAwarded: Int,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("streak") val streak: Int
)

// --- Quiz ---
data class QuizData(
    @SerializedName("quiz") val quiz: QuizDetailsDto
)

data class QuizDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("lesson_id") val lessonId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("pass_percentage") val passPercentage: Int,
    @SerializedName("questions") val questions: List<QuizQuestionDto>
)

data class QuizQuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("order_index") val orderIndex: Int,
    @SerializedName("options") val options: List<QuizOptionDto>
)

data class QuizOptionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("option_text") val optionText: String
)

data class QuizResultData(
    @SerializedName("score") val score: Int,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("percentage") val percentage: Int,
    @SerializedName("passed") val passed: Boolean,
    @SerializedName("pass_percentage") val passPercentage: Int,
    @SerializedName("xp_awarded") val xpAwarded: Int,
    @SerializedName("review") val review: List<QuizReviewItemDto>
)

data class QuizReviewItemDto(
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("selected_option_id") val selectedOptionId: Int?,
    @SerializedName("correct_option_id") val correctOptionId: Int,
    @SerializedName("correct_option_text") val correctOptionText: String,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("is_correct") val isCorrect: Boolean
)

// --- Profile & Badges ---
data class ProfileData(
    @SerializedName("user") val user: ProfileUserDto,
    @SerializedName("skills") val skills: List<SkillMasteryDto>,
    @SerializedName("achievements") val achievements: List<AchievementDto>
)

data class ProfileUserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("initials") val initials: String,
    @SerializedName("joined") val joined: String,
    @SerializedName("xp") val xp: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("streak") val streak: Int,
    @SerializedName("solved_lessons") val solvedLessons: Int,
    @SerializedName("percentile") val percentile: String
)

data class SkillMasteryDto(
    @SerializedName("title") val title: String,
    @SerializedName("progress") val progress: Float,
    @SerializedName("percent") val percent: String,
    @SerializedName("color_tag") val colorTag: String
)

data class AchievementDto(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon_emoji") val iconEmoji: String,
    @SerializedName("xp_reward") val xpReward: Int,
    @SerializedName("is_unlocked") val isUnlocked: Boolean,
    @SerializedName("unlocked_at") val unlockedAt: String?
)

// --- Algorithms ---
data class AlgorithmsData(
    @SerializedName("algorithms") val algorithms: List<AlgorithmDto>
)

data class AlgorithmDto(
    @SerializedName("slug") val slug: String,
    @SerializedName("name") val name: String,
    @SerializedName("time_complexity") val timeComplexity: String,
    @SerializedName("space_complexity") val spaceComplexity: String,
    @SerializedName("description") val description: String,
    @SerializedName("code_snippet") val codeSnippet: String
)

// --- AI Chat ---
data class AiChatData(
    @SerializedName("message") val message: AiMessageDto
)

data class AiHistoryData(
    @SerializedName("messages") val messages: List<AiMessageDto>
)

data class AiMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("sender") val sender: String,
    @SerializedName("text") val text: String,
    @SerializedName("code_snippet") val codeSnippet: String?,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("tags") val tags: List<String> = emptyList()
)

