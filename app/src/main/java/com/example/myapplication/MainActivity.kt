package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 0 = Splash, 1 = Onboarding, 2 = Visualize Intro, 3 = AI Ally Intro,
            // 4 = Create Account, 5 = Login, 6 = Personalize, 7 = Home Dashboard,
            // 8 = Algorithm Visualizer Lab, 9 = Quest AI Chat, 10 = Quest Map,
            // 11 = Chapter Detail, 12 = Explorer Profile
            var screen by rememberSaveable { mutableIntStateOf(0) }
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

            when (screen) {
                0 -> SplashScreen(onFinished = { screen = 1 })
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
                    onCreate = { _, _, _ -> screen = 6 },
                    onLogin = { screen = 5 },
                    onGoogle = { screen = 6 }
                )
                5 -> LoginScreen(
                    onLogin = { _, _ -> screen = 7 },
                    onGoogle = { screen = 7 },
                    onCreateAccount = { screen = 4 }
                )
                6 -> PersonalizeScreen(onBuildPath = { _, _, _ -> screen = 7 })
                7 -> HomeScreen(
                    onContinueQuest = { screen = 8 },
                    onQuestMap = { screen = 10 },
                    onOpenChapter = { screen = 11 },
                    onAskAi = {
                        aiQuery = "Can you help me understand Binary Search?"
                        screen = 9
                    },
                    onOpenProfile = { screen = 12 },
                    onOpenVisualizer = { screen = 8 },
                    onSelectTab = ::onSelectTab
                )
                8 -> AlgorithmVisualizerScreen(
                    onBack = { screen = 7 },
                    onAskAi = { query ->
                        aiQuery = query
                        screen = 9
                    },
                    onSelectTab = ::onSelectTab
                )
                9 -> QuestAiChatScreen(
                    onBack = { screen = 7 },
                    initialQuery = aiQuery,
                    onSelectTab = ::onSelectTab
                )
                10 -> QuestMapScreen(
                    onBack = { screen = 7 },
                    onSelectChapter = { _ -> screen = 11 },
                    onSelectTab = ::onSelectTab
                )
                11 -> ChapterDetailScreen(
                    onBack = { screen = 10 },
                    onStartLesson = { _ -> screen = 8 },
                    onVisualize = { screen = 8 },
                    onAskAi = {
                        aiQuery = "Explain Searching Algorithms and Divide & Conquer"
                        screen = 9
                    },
                    onSelectTab = ::onSelectTab
                )
                12 -> ProfileScreen(
                    onBack = { screen = 7 },
                    onLogout = { screen = 5 },
                    onSelectTab = ::onSelectTab
                )
                else -> HomeScreen(
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
