package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.remote.TokenManager
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.AiChatViewModel
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.DashboardViewModel
import com.example.myapplication.viewmodel.LearningViewModel
import com.example.myapplication.viewmodel.ProfileViewModel
import com.example.myapplication.viewmodel.QuizViewModel
import com.example.myapplication.viewmodel.VisualizerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F1220)
                ) {
                    val tokenManager = remember { TokenManager.getInstance(applicationContext) }

                    val viewModelProvider = ViewModelProvider(this@MainActivity)
                    val authViewModel = viewModelProvider[AuthViewModel::class.java]
                    val dashboardViewModel = viewModelProvider[DashboardViewModel::class.java]
                    val learningViewModel = viewModelProvider[LearningViewModel::class.java]
                    val visualizerViewModel = viewModelProvider[VisualizerViewModel::class.java]
                    val quizViewModel = viewModelProvider[QuizViewModel::class.java]
                    val profileViewModel = viewModelProvider[ProfileViewModel::class.java]
                    val aiChatViewModel = viewModelProvider[AiChatViewModel::class.java]

                    // Navigation Screen indices:
                    // 0 = Splash, 1 = Onboarding, 2 = Visualize Intro, 3 = AI Ally Intro,
                    // 4 = Create Account, 5 = Login, 6 = Personalize, 7 = Home Dashboard,
                    // 8 = Algorithm Visualizer Lab, 9 = Quest AI Chat, 10 = Quest Map,
                    // 11 = Chapter Detail, 12 = Explorer Profile, 13 = Quiz Screen
                    var screen by rememberSaveable { mutableIntStateOf(0) }
                    var currentChapterId by rememberSaveable { mutableIntStateOf(3) }
                    var currentQuizLessonId by rememberSaveable { mutableIntStateOf(5) }
                    var aiQuery by rememberSaveable { mutableStateOf("") }

                    fun onSelectTab(tabIndex: Int) {
                        when (tabIndex) {
                            0 -> screen = 7  // Home
                            1 -> screen = 10 // Map
                            2 -> screen = 8  // Visualizer
                            3 -> screen = 9  // AI Ally
                            4 -> screen = 12 // Profile
                        }
                    }

                    // Back button handling
                    BackHandler(enabled = screen != 0 && screen != 7) {
                        when (screen) {
                            13 -> screen = 11 // Quiz -> Chapter Detail
                            11 -> screen = 10 // Chapter Detail -> Quest Map
                            10, 8, 9, 12 -> screen = 7 // Map, Viz, Chat, Profile -> Home
                            6 -> screen = 4 // Personalize -> Create Account
                            4, 5 -> screen = 1 // Auth -> Onboarding
                            2, 3 -> screen = 1 // Intros -> Onboarding
                            1 -> finish()
                        }
                    }

                    when (screen) {
                        0 -> SplashScreen(
                            onFinished = {
                                if (tokenManager.isLoggedIn()) {
                                    profileViewModel.loadProfile()
                                    dashboardViewModel.loadDashboard()
                                    learningViewModel.loadTopics()
                                    screen = 7
                                } else {
                                    screen = 1
                                }
                            }
                        )
                        1 -> OnboardingScreen(
                            onStart = { screen = 2 },
                            onSkip = { screen = 4 },
                            onLogin = { screen = 5 }
                        )
                        2 -> VisualizeScreen(
                            onNext = { screen = 3 },
                            onSkip = { screen = 4 }
                        )
                        3 -> AiAllyScreen(onStart = { screen = 4 })
                        4 -> CreateAccountScreen(
                            authViewModel = authViewModel,
                            onCreate = { _, _, _ ->
                                profileViewModel.loadProfile()
                                dashboardViewModel.loadDashboard()
                                learningViewModel.loadTopics()
                                screen = 6
                            },
                            onLogin = { screen = 5 },
                            onGoogle = {
                                authViewModel.loginWithGoogle {
                                    profileViewModel.loadProfile()
                                    dashboardViewModel.loadDashboard()
                                    learningViewModel.loadTopics()
                                    screen = 6
                                }
                            }
                        )
                        5 -> LoginScreen(
                            authViewModel = authViewModel,
                            onLogin = { _, _ ->
                                profileViewModel.loadProfile()
                                dashboardViewModel.loadDashboard()
                                learningViewModel.loadTopics()
                                screen = 7
                            },
                            onGoogle = {
                                authViewModel.loginWithGoogle {
                                    profileViewModel.loadProfile()
                                    dashboardViewModel.loadDashboard()
                                    learningViewModel.loadTopics()
                                    screen = 7
                                }
                            },
                            onCreateAccount = { screen = 4 }
                        )
                        6 -> PersonalizeScreen(
                            authViewModel = authViewModel,
                            onBuildPath = { _, _, _ ->
                                profileViewModel.loadProfile()
                                dashboardViewModel.loadDashboard()
                                learningViewModel.loadTopics()
                                screen = 7
                            }
                        )
                        7 -> HomeScreen(
                            dashboardViewModel = dashboardViewModel,
                            onContinueQuest = { screen = 8 },
                            onQuestMap = { screen = 10 },
                            onOpenChapter = {
                                currentChapterId = 3
                                screen = 11
                            },
                            onAskAi = {
                                aiQuery = "Can you help me understand Binary Search?"
                                screen = 9
                            },
                            onOpenProfile = { screen = 12 },
                            onOpenVisualizer = { screen = 8 },
                            onSelectTab = ::onSelectTab
                        )
                        8 -> AlgorithmVisualizerScreen(
                            viewModel = visualizerViewModel,
                            onBack = { screen = 7 },
                            onAskAi = { query ->
                                aiQuery = query
                                screen = 9
                            },
                            onSelectTab = ::onSelectTab
                        )
                        9 -> QuestAiChatScreen(
                            viewModel = aiChatViewModel,
                            onBack = { screen = 7 },
                            initialQuery = aiQuery,
                            onSelectTab = ::onSelectTab
                        )
                        10 -> QuestMapScreen(
                            learningViewModel = learningViewModel,
                            onBack = { screen = 7 },
                            onSelectChapter = { chId ->
                                currentChapterId = chId
                                screen = 11
                            },
                            onSelectTab = ::onSelectTab
                        )
                        11 -> ChapterDetailScreen(
                            topicId = currentChapterId,
                            learningViewModel = learningViewModel,
                            onBack = { screen = 10 },
                            onStartLesson = { lessonId ->
                                screen = 8
                            },
                            onOpenQuiz = { lessonId ->
                                currentQuizLessonId = lessonId
                                screen = 13
                            },
                            onVisualize = { screen = 8 },
                            onAskAi = {
                                aiQuery = "Explain Searching Algorithms and Divide & Conquer"
                                screen = 9
                            },
                            onSelectTab = ::onSelectTab
                        )
                        12 -> ProfileScreen(
                            viewModel = profileViewModel,
                            onBack = { screen = 7 },
                            onLogout = {
                                authViewModel.logout {
                                    profileViewModel.clearState()
                                    dashboardViewModel.clearState()
                                    learningViewModel.clearState()
                                    aiChatViewModel.clearState()
                                    quizViewModel.reset()
                                    screen = 5
                                }
                            },
                            onSelectTab = ::onSelectTab
                        )
                        13 -> QuizScreen(
                            lessonId = currentQuizLessonId,
                            quizViewModel = quizViewModel,
                            onBack = { screen = 11 },
                            onQuizPassed = {
                                learningViewModel.loadTopicDetail(currentChapterId)
                                screen = 11
                            },
                            onSelectTab = ::onSelectTab
                        )
                        else -> HomeScreen(
                            dashboardViewModel = dashboardViewModel,
                            onContinueQuest = { screen = 8 },
                            onQuestMap = { screen = 10 },
                            onOpenChapter = { screen = 11 },
                            onAskAi = { screen = 9 },
                            onOpenProfile = { screen = 12 },
                            onOpenVisualizer = { screen = 8 },
                            onSelectTab = ::onSelectTab
                        )
                    }
                }
            }
        }
    }
}
