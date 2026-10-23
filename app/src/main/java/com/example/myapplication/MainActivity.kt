package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 0 = splash, 1 = onboarding 2, 2 = onboarding 3, 3 = AI ally,
            // 4 = create account, 5 = login, 6 = personalize, 7 = home
            var screen by rememberSaveable { mutableIntStateOf(0) }

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
                else -> HomeScreen()
            }
        }
    }
}