package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.ErrorRetryCard
import com.example.myapplication.ui.components.LoadingIndicator
import com.example.myapplication.ui.theme.AlgoActiveCardBg
import com.example.myapplication.ui.theme.AlgoBg
import com.example.myapplication.ui.theme.AlgoBorder
import com.example.myapplication.ui.theme.AlgoCardBg
import com.example.myapplication.ui.theme.AlgoCyan
import com.example.myapplication.ui.theme.AlgoGold
import com.example.myapplication.ui.theme.AlgoGreen
import com.example.myapplication.ui.theme.AlgoPink
import com.example.myapplication.ui.theme.AlgoPurple
import com.example.myapplication.ui.theme.AlgoPurpleSoft
import com.example.myapplication.ui.theme.AlgoTextMuted
import com.example.myapplication.ui.theme.AlgoTextPrimary
import com.example.myapplication.ui.theme.AlgoTextSecondary
import com.example.myapplication.viewmodel.QuizUiState
import com.example.myapplication.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    lessonId: Int = 5,
    quizViewModel: QuizViewModel,
    onBack: () -> Unit = {},
    onQuizPassed: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val uiState by quizViewModel.uiState.collectAsState()

    LaunchedEffect(lessonId) {
        quizViewModel.loadQuiz(lessonId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AlgoBg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AlgoCardBg)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, color = AlgoTextPrimary))
                    }
                    Column {
                        Text(
                            text = "Mastery Challenge",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = AlgoTextPrimary)
                        )
                        Text(
                            text = "Server-evaluated quiz",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = AlgoTextSecondary)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AlgoPurple.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+300 XP", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = AlgoGold))
                }
            }

            when (val state = uiState) {
                is QuizUiState.Loading -> {
                    LoadingIndicator(text = "Retrieving challenge questions...")
                }
                is QuizUiState.Error -> {
                    ErrorRetryCard(message = state.message, onRetry = { quizViewModel.loadQuiz(lessonId) })
                }
                is QuizUiState.Loaded -> {
                    val quiz = state.quiz
                    Text(
                        text = quiz.title,
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = AlgoTextPrimary)
                    )
                    quiz.description?.let {
                        Text(
                            text = it,
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = AlgoTextSecondary)
                        )
                    }

                    // Questions List
                    quiz.questions.forEachIndexed { qIdx, question ->
                        val cardShape = RoundedCornerShape(16.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(cardShape)
                                .background(AlgoCardBg)
                                .border(1.dp, Color.White.copy(alpha = 0.06f), cardShape)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Question ${qIdx + 1} of ${quiz.questions.size}",
                                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = AlgoCyan)
                            )
                            Text(
                                text = question.questionText,
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = AlgoTextPrimary)
                            )

                            // Options
                            question.options.forEach { opt ->
                                val isSelected = state.selectedAnswers[question.id] == opt.id
                                val optShape = RoundedCornerShape(12.dp)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(optShape)
                                        .background(if (isSelected) AlgoPurple.copy(alpha = 0.25f) else AlgoActiveCardBg)
                                        .border(1.dp, if (isSelected) AlgoPurple else AlgoBorder, optShape)
                                        .clickable { quizViewModel.selectOption(question.id, opt.id) }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) AlgoPurple else Color.Transparent)
                                            .border(1.5.dp, if (isSelected) AlgoPurple else AlgoBorder, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                        }
                                    }
                                    Text(
                                        text = opt.optionText,
                                        style = TextStyle(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else AlgoTextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Submit Button
                    val allAnswered = quiz.questions.all { state.selectedAnswers.containsKey(it.id) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (allAnswered && !state.isSubmitting) AlgoPurple else AlgoPurple.copy(alpha = 0.4f))
                            .clickable(enabled = allAnswered && !state.isSubmitting) {
                                quizViewModel.submitQuiz(quiz.id)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isSubmitting) "Evaluating with Server..." else "Submit Challenge",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.White)
                        )
                    }
                }
                is QuizUiState.Result -> {
                    val res = state.result
                    val resultShape = RoundedCornerShape(20.dp)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(resultShape)
                            .background(AlgoActiveCardBg)
                            .border(1.dp, if (res.passed) AlgoGreen.copy(alpha = 0.4f) else AlgoPink.copy(alpha = 0.4f), resultShape)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (res.passed) "🎉 Mastery Achieved!" else "Keep Practicing!",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = if (res.passed) AlgoGreen else AlgoPink)
                        )
                        Text(
                            text = "Score: ${res.score} / ${res.totalQuestions} (${res.percentage}%)",
                            style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AlgoTextPrimary)
                        )
                        if (res.passed) {
                            Text(
                                text = "+${res.xpAwarded} XP Awarded · Binary Searcher Badge Unlocked!",
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = AlgoGold)
                            )
                        } else {
                            Text(
                                text = "Passing score is ${res.passPercentage}%. Review the explanations below and try again.",
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = AlgoTextSecondary, textAlign = TextAlign.Center)
                            )
                        }
                    }

                    // Detailed review cards
                    Text(
                        text = "Question Review & Explanations",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AlgoTextPrimary)
                    )

                    res.review.forEachIndexed { idx, item ->
                        val cardShape = RoundedCornerShape(14.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(cardShape)
                                .background(AlgoCardBg)
                                .border(1.dp, if (item.isCorrect) AlgoGreen.copy(alpha = 0.3f) else AlgoPink.copy(alpha = 0.3f), cardShape)
                            .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Q${idx + 1}", style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = AlgoCyan))
                                Text(
                                    text = if (item.isCorrect) "CORRECT" else "INCORRECT",
                                    style = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (item.isCorrect) AlgoGreen else AlgoPink)
                                )
                            }
                            Text(text = item.questionText, style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = AlgoTextPrimary))
                            Text(text = "Correct answer: ${item.correctOptionText}", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, color = AlgoGreen))
                            item.explanation?.let {
                                Text(text = "Why: $it", style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, color = AlgoTextMuted))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AlgoPurple)
                            .clickable {
                                if (res.passed) {
                                    onQuizPassed()
                                } else {
                                    quizViewModel.loadQuiz(lessonId)
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (res.passed) "Continue Journey" else "Try Again",
                            style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.White)
                        )
                    }
                }
                else -> {}
            }
        }

        AlgoQuestBottomBar(selectedTab = 1, onSelectTab = onSelectTab)
    }
}

