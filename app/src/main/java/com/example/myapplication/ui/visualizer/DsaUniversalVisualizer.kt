package com.example.myapplication.ui.visualizer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.visualizer.DsaVisualizerScenario
import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType
import com.example.myapplication.data.visualizer.DsaVisualizationRegistry
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.ui.visualizer.components.ArrayVisualizerView
import com.example.myapplication.ui.visualizer.components.DpTableVisualizerView
import com.example.myapplication.ui.visualizer.components.FlowchartVisualizerView
import com.example.myapplication.ui.visualizer.components.GraphVisualizerView
import com.example.myapplication.ui.visualizer.components.GreedyVisualizerView
import com.example.myapplication.ui.visualizer.components.LinkedListVisualizerView
import com.example.myapplication.ui.visualizer.components.QueueVisualizerView
import com.example.myapplication.ui.visualizer.components.RecursionVisualizerView
import com.example.myapplication.ui.visualizer.components.StackVisualizerView
import com.example.myapplication.ui.visualizer.components.TreeVisualizerView
import kotlinx.coroutines.delay

private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val Gold: Color @Composable get() = AlgoTheme.colors.gold
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val CodeBg: Color @Composable get() = AlgoTheme.colors.codeBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily @Composable get() = AlgoTheme.editorFontFamily

@Composable
fun DsaUniversalVisualizer(
    subtopicId: String,
    onCompleteLab: (() -> Unit)? = null,
    isLabCompleted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scenario: DsaVisualizerScenario = remember(subtopicId) {
        DsaVisualizationRegistry.getScenarioForSubtopic(subtopicId)
            ?: DsaVisualizationRegistry.getAllScenarios().first()
    }

    var customInputText by remember(subtopicId) { mutableStateOf(scenario.defaultInput) }
    var steps by remember(subtopicId) { mutableStateOf(scenario.generateSteps(scenario.defaultInput)) }
    var currentStepIdx by remember(subtopicId) { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var isInputExpanded by remember { mutableStateOf(false) }

    // Autoplay coroutine loop
    LaunchedEffect(isPlaying, currentStepIdx, playbackSpeed, steps.size) {
        if (isPlaying) {
            val baseDelay = (1200 / playbackSpeed).toLong()
            while (isPlaying && currentStepIdx < steps.size - 1) {
                delay(baseDelay)
                currentStepIdx++
            }
            if (currentStepIdx >= steps.size - 1) {
                isPlaying = false
            }
        }
    }

    val currentStep = steps.getOrElse(currentStepIdx) { steps.last() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header: Scenario title & Action badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scenario.title,
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "${scenario.categoryName} • Time: ${scenario.timeComplexity} • Space: ${scenario.spaceComplexity}",
                    style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Purple.copy(alpha = 0.2f))
                    .border(1.dp, PurpleSoft.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = currentStep.actionType,
                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Cyan)
                )
            }
        }

        // 2. Custom Input Bar (if supported)
        if (scenario.supportsCustomInput) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isInputExpanded) Purple.copy(alpha = 0.3f) else CodeBg)
                        .border(1.dp, if (isInputExpanded) Cyan else BorderColor, RoundedCornerShape(8.dp))
                        .clickable { isInputExpanded = !isInputExpanded }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isInputExpanded) "▲ Close Input Editor" else "⚙️ Custom Input",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = if (isInputExpanded) Cyan else TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CodeBg)
                        .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                        .clickable {
                            // Generate randomized array
                            val randList = (1..6).map { (10..99).random() }.distinct().take(6)
                            val newInput = randList.joinToString(", ")
                            customInputText = newInput
                            steps = scenario.generateSteps(newInput)
                            currentStepIdx = 0
                            isPlaying = false
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "🎲 Randomize",
                        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = Gold)
                    )
                }
            }

            AnimatedVisibility(
                visible = isInputExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CodeBg)
                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = scenario.inputHelpText,
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardBg)
                                .border(1.dp, BorderColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = customInputText,
                                onValueChange = { customInputText = it },
                                textStyle = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextPrimary),
                                cursorBrush = SolidColor(Cyan),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Purple)
                                .clickable {
                                    steps = scenario.generateSteps(customInputText)
                                    currentStepIdx = 0
                                    isPlaying = false
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Apply",
                                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            )
                        }
                    }
                }
            }
        }

        // 3. Main Dynamic Visualization Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CodeBg)
                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            when (currentStep.vizType) {
                DsaVizType.ARRAY, DsaVizType.SEARCH, DsaVizType.SORT, DsaVizType.STRING -> {
                    ArrayVisualizerView(step = currentStep)
                }
                DsaVizType.LINKED_LIST -> {
                    LinkedListVisualizerView(step = currentStep)
                }
                DsaVizType.STACK -> {
                    StackVisualizerView(step = currentStep)
                }
                DsaVizType.QUEUE -> {
                    QueueVisualizerView(step = currentStep)
                }
                DsaVizType.TREE -> {
                    TreeVisualizerView(step = currentStep)
                }
                DsaVizType.GRAPH -> {
                    GraphVisualizerView(step = currentStep)
                }
                DsaVizType.RECURSION -> {
                    RecursionVisualizerView(step = currentStep)
                }
                DsaVizType.DP_TABLE -> {
                    DpTableVisualizerView(step = currentStep)
                }
                DsaVizType.GREEDY -> {
                    GreedyVisualizerView(step = currentStep)
                }
                DsaVizType.FLOWCHART -> {
                    FlowchartVisualizerView(step = currentStep)
                }
            }
        }

        // 4. Live Variables Watch Window
        if (currentStep.variables.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VARS:",
                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Cyan)
                )
                currentStep.variables.forEach { (k, v) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CodeBg)
                            .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$k: $v",
                            style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = TextPrimary)
                        )
                    }
                }
            }
        }

        // 5. Step Title & Description
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CodeBg.copy(alpha = 0.6f))
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentStep.title,
                    style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = if (currentStep.isFound) Green else TextPrimary)
                )
                Text(
                    text = "Step ${currentStepIdx + 1} of ${steps.size}",
                    style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
                )
            }
            Text(
                text = currentStep.description,
                style = TextStyle(fontFamily = Inter, fontSize = 11.5.sp, lineHeight = 16.sp, color = TextSecondary)
            )
        }

        // 6. Step Scrubber Slider
        Slider(
            value = currentStepIdx.toFloat(),
            onValueChange = {
                isPlaying = false
                currentStepIdx = it.toInt().coerceIn(0, steps.size - 1)
            },
            valueRange = 0f..(steps.size - 1).toFloat(),
            steps = (steps.size - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = Cyan,
                activeTrackColor = Purple,
                inactiveTrackColor = CodeBg
            ),
            modifier = Modifier.height(20.dp)
        )

        // 7. Interactive Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            ControlIconBtn(label = "↺", onClick = {
                isPlaying = false
                currentStepIdx = 0
            })

            // Prev Step
            ControlIconBtn(
                label = "◀◀",
                enabled = currentStepIdx > 0,
                onClick = {
                    isPlaying = false
                    if (currentStepIdx > 0) currentStepIdx--
                }
            )

            // Play / Pause main button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Purple)
                    .clickable {
                        if (currentStepIdx >= steps.size - 1) {
                            currentStepIdx = 0
                        }
                        isPlaying = !isPlaying
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlaying) "⏸" else "▶",
                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                )
            }

            // Next Step
            ControlIconBtn(
                label = "▶▶",
                enabled = currentStepIdx < steps.size - 1,
                onClick = {
                    isPlaying = false
                    if (currentStepIdx < steps.size - 1) currentStepIdx++
                }
            )

            // Speed toggle
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeBg)
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .clickable {
                        playbackSpeed = when (playbackSpeed) {
                            0.5f -> 1f
                            1f -> 2f
                            else -> 0.5f
                        }
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${playbackSpeed}x",
                    style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Cyan)
                )
            }
        }

        // 8. Result & Completion Card
        if (currentStep.isCompleted || currentStepIdx == steps.size - 1) {
            val resultText = currentStep.summaryResult ?: "Visualization execution completed."
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Green.copy(alpha = 0.12f))
                    .border(1.dp, Green.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ Execution Result",
                        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Green)
                    )
                    if (isLabCompleted) {
                        Text(
                            text = "LAB COMPLETED ✓",
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Green)
                        )
                    }
                }
                Text(
                    text = resultText,
                    style = TextStyle(fontFamily = Inter, fontSize = 11.5.sp, color = TextPrimary)
                )

                if (onCompleteLab != null && !isLabCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Green)
                            .clickable { onCompleteLab() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Complete Visualizer Lab (+25 XP)",
                            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlIconBtn(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (enabled) CodeBg else CodeBg.copy(alpha = 0.4f))
            .border(1.dp, if (enabled) BorderColor else BorderColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (enabled) TextPrimary else TextMuted
            )
        )
    }
}
