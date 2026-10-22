package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 0 = splash, 1 = onboarding 2, 2 = onboarding 3, 3 = AI ally, 4 = next screen (placeholder)
            var screen by rememberSaveable { mutableIntStateOf(0) }

            when (screen) {
                0 -> SplashScreen(onFinished = { screen = 1 })
                1 -> OnboardingScreen(
                    onStart = { screen = 2 },
                    onSkip = { screen = 4 },
                    onLogin = { screen = 4 }
                )
                2 -> VisualizeScreen(
                    onNext = { screen = 3 },
                    onSkip = { screen = 4 }
                )
                3 -> AiAllyScreen(onStart = { screen = 4 })
                else -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F1220)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Next screen goes here", color = Color.White)
                }
            }
        }
    }
}