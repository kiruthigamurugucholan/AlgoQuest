package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.remote.TokenManager
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.ui.theme.AppThemeMode
import com.example.myapplication.reminder.ReminderPreferences
import com.example.myapplication.reminder.ReminderScheduler
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.ThemePreferences
import com.example.myapplication.viewmodel.AiChatViewModel
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.DashboardViewModel
import com.example.myapplication.viewmodel.LearningViewModel
import com.example.myapplication.viewmodel.ProfileViewModel
import com.example.myapplication.viewmodel.QuizViewModel
import com.example.myapplication.viewmodel.VisualizerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var onOpenQuizFromNotification: ((Int) -> Unit)? = null
    private var onOpenProfileFromNotification: (() -> Unit)? = null
    private var pendingQuizLessonId: Int? = null
    private var pendingOpenProfile: Boolean = false

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: android.content.Intent?) {
        if (intent == null) return
        val destScreen = intent.getIntExtra(ReminderScheduler.EXTRA_DESTINATION_SCREEN, -1)
        if (destScreen == 13) {
            val lessonId = intent.getIntExtra(ReminderScheduler.EXTRA_QUIZ_LESSON_ID, 5)
            pendingQuizLessonId = lessonId
            onOpenQuizFromNotification?.invoke(lessonId)
        } else if (destScreen == 12) {
            pendingOpenProfile = true
            onOpenProfileFromNotification?.invoke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)
        ReminderScheduler.createNotificationChannel(applicationContext)
        val reminderPrefs = ReminderPreferences.getInstance(applicationContext)
        if (reminderPrefs.isReminderEnabled()) {
            ReminderScheduler.scheduleDailyReminder(applicationContext)
        }
        if (reminderPrefs.isStreakReminderEnabled()) {
            ReminderScheduler.scheduleStreakReminder(applicationContext)
        }
        setContent {
            val themePreferences = remember { ThemePreferences.getInstance(applicationContext) }
            var themeMode by rememberSaveable { mutableStateOf(themePreferences.getThemeMode()) }
            var syntaxScheme by rememberSaveable { mutableStateOf(themePreferences.getSyntaxScheme()) }
            var editorFont by rememberSaveable { mutableStateOf(themePreferences.getEditorFont()) }

            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> systemDark
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                syntaxScheme = syntaxScheme,
                editorFont = editorFont,
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AlgoTheme.colors.bg
                ) {
                    val tokenManager = remember { TokenManager.getInstance(applicationContext) }
                    val coroutineScope = rememberCoroutineScope()

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
                    // 11 = Chapter Detail, 12 = Explorer Profile, 13 = Quiz Screen, 14 = Settings Screen
                    var screen by rememberSaveable { mutableIntStateOf(0) }
                    var currentChapterId by rememberSaveable { mutableIntStateOf(3) }
                    var currentQuizLessonId by rememberSaveable { mutableIntStateOf(5) }
                    var aiQuery by rememberSaveable { mutableStateOf("") }
                    var previousScreenBeforeSettings by rememberSaveable { mutableIntStateOf(7) }

                    DisposableEffect(Unit) {
                        onOpenQuizFromNotification = { quizId ->
                            currentQuizLessonId = quizId
                            screen = 13
                        }
                        onOpenProfileFromNotification = {
                            screen = 12
                        }
                        onDispose {
                            onOpenQuizFromNotification = null
                            onOpenProfileFromNotification = null
                        }
                    }

                    fun tabToScreen(tab: Int): Int = when (tab) {
                        0 -> 7  // Home
                        1 -> 10 // Map
                        2 -> 8  // Visualizer
                        3 -> 9  // AI Ally
                        4 -> 12 // Profile
                        else -> 7
                    }

                    fun screenToTab(s: Int): Int = when (s) {
                        7 -> 0
                        10 -> 1
                        8 -> 2
                        9 -> 3
                        12 -> 4
                        else -> 0
                    }

                    val pagerState = rememberPagerState(
                        initialPage = screenToTab(screen),
                        pageCount = { 5 }
                    )

                    // Synchronize external screen changes with the pager
                    LaunchedEffect(screen) {
                        val targetTab = when (screen) {
                            7 -> 0
                            10 -> 1
                            8 -> 2
                            9 -> 3
                            12 -> 4
                            else -> -1
                        }
                        if (targetTab in 0..4 && pagerState.currentPage != targetTab && pagerState.targetPage != targetTab) {
                            pagerState.animateScrollToPage(targetTab)
                        }
                    }

                    // Synchronize swipe gesture page changes with screen state
                    LaunchedEffect(pagerState) {
                        snapshotFlow { pagerState.settledPage }.collect { settledTab ->
                            val currentScreenForTab = tabToScreen(settledTab)
                            if (screen in listOf(7, 10, 8, 9, 12) && screen != currentScreenForTab) {
                                screen = currentScreenForTab
                            }
                        }
                    }

                    fun onSelectTab(tabIndex: Int) {
                        screen = tabToScreen(tabIndex)
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tabIndex)
                        }
                    }

                    // Back button handling
                    BackHandler(enabled = screen != 0) {
                        when {
                            screen == 14 -> {
                                screen = previousScreenBeforeSettings
                                if (previousScreenBeforeSettings == 12) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(4) }
                                } else if (previousScreenBeforeSettings == 7) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                }
                            }
                            screen == 13 -> screen = 11 // Quiz -> Chapter Detail
                            screen == 11 -> screen = 10 // Chapter Detail -> Quest Map
                            screen in listOf(7, 10, 8, 9, 12) -> {
                                if (pagerState.currentPage != 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                    screen = 7
                                } else {
                                    finish()
                                }
                            }
                            screen == 6 -> screen = 4 // Personalize -> Create Account
                            screen in listOf(4, 5) -> screen = 1 // Auth -> Onboarding
                            screen in listOf(2, 3) -> screen = 1 // Intros -> Onboarding
                            screen == 1 -> finish()
                        }
                    }

                    when (screen) {
                        0 -> SplashScreen(
                            onFinished = {
                                if (tokenManager.isLoggedIn()) {
                                    profileViewModel.loadProfile()
                                    dashboardViewModel.loadDashboard()
                                    learningViewModel.loadTopics()
                                    if (pendingQuizLessonId != null) {
                                        currentQuizLessonId = pendingQuizLessonId!!
                                        pendingQuizLessonId = null
                                        screen = 13
                                    } else if (pendingOpenProfile) {
                                        pendingOpenProfile = false
                                        screen = 12
                                    } else {
                                        screen = 7
                                    }
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
                        11 -> ChapterDetailScreen(
                            topicId = currentChapterId,
                            learningViewModel = learningViewModel,
                            onBack = { screen = 10 },
                            onStartLesson = { _ ->
                                screen = 8
                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            },
                            onOpenQuiz = { lessonId ->
                                currentQuizLessonId = lessonId
                                screen = 13
                            },
                            onVisualize = {
                                screen = 8
                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                            },
                            onAskAi = {
                                aiQuery = "Explain Searching Algorithms and Divide & Conquer"
                                screen = 9
                                coroutineScope.launch { pagerState.animateScrollToPage(3) }
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
                        14 -> SettingsScreen(
                            themeMode = themeMode,
                            onSelectThemeMode = { mode ->
                                themeMode = mode
                                themePreferences.setThemeMode(mode)
                            },
                            syntaxScheme = syntaxScheme,
                            onSelectSyntaxScheme = { scheme ->
                                syntaxScheme = scheme
                                themePreferences.setSyntaxScheme(scheme)
                            },
                            editorFont = editorFont,
                            onSelectEditorFont = { font ->
                                editorFont = font
                                themePreferences.setEditorFont(font)
                            },
                            onBack = {
                                screen = previousScreenBeforeSettings
                                if (previousScreenBeforeSettings == 12) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(4) }
                                } else if (previousScreenBeforeSettings == 7) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                }
                            }
                        )
                        else -> {
                            // Main 5 Tabs managed by HorizontalPager with stationary bottom bar
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(AlgoTheme.colors.bg)
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    beyondViewportPageCount = 4,
                                    userScrollEnabled = false,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) { page ->
                                    when (page) {
                                        0 -> HomeScreen(
                                            dashboardViewModel = dashboardViewModel,
                                            profileViewModel = profileViewModel,
                                            onContinueQuest = {
                                                screen = 8
                                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                                            },
                                            onQuestMap = {
                                                screen = 10
                                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                                            },
                                            onOpenChapter = {
                                                currentChapterId = 3
                                                screen = 11
                                            },
                                            onAskAi = {
                                                aiQuery = "Can you help me understand Binary Search?"
                                                screen = 9
                                                coroutineScope.launch { pagerState.animateScrollToPage(3) }
                                            },
                                            onOpenProfile = {
                                                screen = 12
                                                coroutineScope.launch { pagerState.animateScrollToPage(4) }
                                            },
                                            onOpenVisualizer = {
                                                screen = 8
                                                coroutineScope.launch { pagerState.animateScrollToPage(2) }
                                            },
                                            onOpenSettings = {
                                                previousScreenBeforeSettings = 7
                                                screen = 14
                                            },
                                            onSelectTab = ::onSelectTab,
                                            showBottomBar = false
                                        )
                                        1 -> QuestMapScreen(
                                            learningViewModel = learningViewModel,
                                            onBack = {
                                                screen = 7
                                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                            },
                                            onSelectChapter = { chId ->
                                                currentChapterId = chId
                                                screen = 11
                                            },
                                            onSelectTab = ::onSelectTab,
                                            showBottomBar = false
                                        )
                                        2 -> AlgorithmVisualizerScreen(
                                            viewModel = visualizerViewModel,
                                            onBack = {
                                                screen = 7
                                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                            },
                                            onAskAi = { query ->
                                                aiQuery = query
                                                screen = 9
                                                coroutineScope.launch { pagerState.animateScrollToPage(3) }
                                            },
                                            onSelectTab = ::onSelectTab,
                                            showBottomBar = false
                                        )
                                        3 -> QuestAiChatScreen(
                                            viewModel = aiChatViewModel,
                                            onBack = {
                                                screen = 7
                                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                            },
                                            initialQuery = aiQuery,
                                            onSelectTab = ::onSelectTab,
                                            showBottomBar = false
                                        )
                                        4 -> ProfileScreen(
                                            viewModel = profileViewModel,
                                            onBack = {
                                                screen = 7
                                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                                            },
                                            onOpenSettings = {
                                                previousScreenBeforeSettings = 12
                                                screen = 14
                                            },
                                            onLogout = {
                                                authViewModel.logout {
                                                    profileViewModel.clearState()
                                                    dashboardViewModel.clearState()
                                                    learningViewModel.clearState()
                                                    aiChatViewModel.clearState()
                                                    quizViewModel.reset()
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage(0)
                                                    }
                                                    screen = 5
                                                }
                                            },
                                            onSelectTab = ::onSelectTab,
                                            showBottomBar = false
                                        )
                                    }
                                }

                                AlgoQuestBottomBar(
                                    selectedTab = pagerState.currentPage,
                                    onSelectTab = ::onSelectTab
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
