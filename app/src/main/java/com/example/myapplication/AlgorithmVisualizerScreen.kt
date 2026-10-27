package com.example.myapplication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val CardBg = Color(0xFF1A1F36)
private val SelectedBg = Color(0xFF25213E)
private val EliminatedColor = Color(0xFF23283E)
private val CodeBg = Color(0xFF131728)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

data class VizStep(
    val low: Int,
    val high: Int,
    val mid: Int,
    val description: String,
    val activeLine: Int,
    val isFound: Boolean = false
)

@Composable
fun AlgorithmVisualizerScreen(
    onBack: () -> Unit = {},
    onAskAi: (query: String) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val array = remember { listOf(2, 5, 8, 12, 16, 23, 38, 56, 72, 91) }
    val target = 23

    val steps = remember {
        listOf(
            VizStep(0, 9, 4, "Initial state: target = $target. Set low = 0 (val 2), high = 9 (val 91).", 1),
            VizStep(0, 9, 4, "Calculate mid = (0 + 9) / 2 = 4 (value 16). Compare 16 with $target.", 2),
            VizStep(0, 9, 4, "16 < $target: Target must be in the right half. Set low = mid + 1 = 5.", 4),
            VizStep(5, 9, 7, "New range [5..9]. Calculate mid = (5 + 9) / 2 = 7 (value 56).", 2),
            VizStep(5, 9, 7, "56 > $target: Target must be in left half. Set high = mid - 1 = 6.", 5),
            VizStep(5, 6, 5, "New range [5..6]. Calculate mid = (5 + 6) / 2 = 5 (value 23).", 2),
            VizStep(5, 6, 5, "23 == $target! Target found at index 5! Search complete in 3 steps.", 3, isFound = true)
        )
    }

    var currentStepIdx by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedAlg by remember { mutableIntStateOf(0) } // 0: Binary Search, 1: Bubble Sort, 2: Two Pointers

    val currentStep = steps[currentStepIdx]

    // Auto play effect
    LaunchedEffect(isPlaying, currentStepIdx) {
        if (isPlaying) {
            if (currentStepIdx < steps.size - 1) {
                delay(1600)
                currentStepIdx++
            } else {
                isPlaying = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header bar
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
                            .background(CardBg)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(16.dp) { d -> drawChevronLeft(d) }
                    }
                    Column {
                        Text(
                            text = "Visualizer Lab",
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Interactive execution trace",
                            style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                        )
                    }
                }
                Badge("LIVE VIZ", Cyan.copy(alpha = 0.09f), Cyan)
            }

            // Algorithm selector chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val algs = listOf("Binary Search", "Bubble Sort", "Two Pointers")
                algs.forEachIndexed { idx, name ->
                    val selected = idx == selectedAlg
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) Purple else CardBg)
                            .border(1.dp, if (selected) Purple else Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                            .clickable { selectedAlg = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = if (selected) Color.White else TextSecondary
                            )
                        )
                    }
                }
            }

            // Main Visualization Canvas / Container
            val vizShape = RoundedCornerShape(20.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(vizShape)
                    .background(SelectedBg)
                    .border(1.dp, Color.White.copy(alpha = 0.09f), vizShape)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Target info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target: $target",
                        style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Gold)
                    )
                    Text(
                        text = "Step ${currentStepIdx + 1} of ${steps.size}",
                        style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = TextMuted)
                    )
                }

                // Array elements visual boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    array.forEachIndexed { idx, value ->
                        val isMid = idx == currentStep.mid
                        val isLow = idx == currentStep.low
                        val isHigh = idx == currentStep.high
                        val isEliminated = idx < currentStep.low || idx > currentStep.high

                        val itemBg by animateColorAsState(
                            targetValue = when {
                                currentStep.isFound && isMid -> Green
                                isMid -> Gold
                                isEliminated -> EliminatedColor
                                else -> CardBg
                            },
                            animationSpec = tween(300),
                            label = "itemBg"
                        )

                        val borderCol = when {
                            isMid -> Gold
                            isLow -> Cyan
                            isHigh -> PurpleSoft
                            else -> Color.White.copy(alpha = 0.05f)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Pointer labels
                            Box(modifier = Modifier.height(18.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = when {
                                        isMid && isLow && isHigh -> "L M H"
                                        isMid && isLow -> "L M"
                                        isMid && isHigh -> "M H"
                                        isMid -> "M"
                                        isLow -> "L"
                                        isHigh -> "H"
                                        else -> ""
                                    },
                                    style = TextStyle(
                                        fontFamily = Mono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = when {
                                            isMid -> Gold
                                            isLow -> Cyan
                                            isHigh -> PurpleSoft
                                            else -> Color.Transparent
                                        }
                                    )
                                )
                            }

                            // Number card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(itemBg)
                                    .border(1.5.dp, borderCol, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = value.toString(),
                                    style = TextStyle(
                                        fontFamily = Mono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isEliminated) TextMuted.copy(alpha = 0.4f) else TextPrimary
                                    )
                                )
                            }

                            // Index label
                            Text(
                                text = "[$idx]",
                                style = TextStyle(fontFamily = Mono, fontSize = 9.sp, color = TextMuted)
                            )
                        }
                    }
                }

                // Legend indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    LegendItem("L: Low", Cyan)
                    LegendItem("M: Mid", Gold)
                    LegendItem("H: High", PurpleSoft)
                    LegendItem("Found", Green)
                }

                // Step explanation box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardBg)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(18.dp) { d -> drawSparkles(d, if (currentStep.isFound) Green else Cyan) }
                        Text(
                            text = currentStep.description,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = if (currentStep.isFound) Green else TextPrimary
                            )
                        )
                    }
                }

                // Playback Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset
                    ControlBtn(icon = { d -> drawReset(d) }, onClick = {
                        currentStepIdx = 0
                        isPlaying = false
                    })
                    // Prev
                    ControlBtn(icon = { d -> drawStepPrev(d) }, enabled = currentStepIdx > 0, onClick = {
                        if (currentStepIdx > 0) currentStepIdx--
                    })
                    // Play/Pause main button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Purple)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(20.dp) { d -> if (isPlaying) drawPause(d) else drawPlay(d) }
                    }
                    // Next
                    ControlBtn(icon = { d -> drawStepNext(d) }, enabled = currentStepIdx < steps.size - 1, onClick = {
                        if (currentStepIdx < steps.size - 1) currentStepIdx++
                    })
                    // Auto ask AI about this step
                    ControlBtn(icon = { d -> drawBrain(d) }, onClick = {
                        onAskAi("Can you explain step ${currentStepIdx + 1} of Binary Search where mid is at index ${currentStep.mid}?")
                    })
                }
            }

            // Code & Pseudocode trace section
            SectionTitle("Algorithm Code Trace")

            val codeLines = listOf(
                "fun binarySearch(arr: IntArray, target: Int): Int {",
                "    var low = 0; var high = arr.size - 1",
                "    while (low <= high) {",
                "        val mid = low + (high - low) / 2",
                "        if (arr[mid] == target) return mid",
                "        else if (arr[mid] < target) low = mid + 1",
                "        else high = mid - 1",
                "    }",
                "    return -1",
                "}"
            )

            val codeShape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(codeShape)
                    .background(CodeBg)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), codeShape)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                codeLines.forEachIndexed { idx, line ->
                    val isActive = when (currentStep.activeLine) {
                        1 -> idx == 1
                        2 -> idx == 3
                        3 -> idx == 4
                        4 -> idx == 5
                        5 -> idx == 6
                        else -> false
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isActive) Purple.copy(alpha = 0.25f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${idx + 1}".padStart(2, ' '),
                            style = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextMuted)
                        )
                        Text(
                            text = line,
                            style = TextStyle(
                                fontFamily = Mono,
                                fontSize = 12.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) Cyan else TextSecondary
                            )
                        )
                    }
                }
            }

            // Complexity Metrics Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComplexityCard(
                    title = "Time Complexity",
                    value = "O(log N)",
                    subtitle = "Halves search space each step",
                    color = Cyan,
                    modifier = Modifier.weight(1f)
                )
                ComplexityCard(
                    title = "Space Complexity",
                    value = "O(1)",
                    subtitle = "Constant iterative space",
                    color = Green,
                    modifier = Modifier.weight(1f)
                )
            }

            // Ask AI banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2E2458), CardBg)))
                    .border(1.dp, PurpleSoft.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { onAskAi("Explain why Binary Search requires a sorted array first?") }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Purple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconCanvas(18.dp) { d -> drawSparkles(d, PurpleSoft) }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Have a question about this step?",
                        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = TextPrimary)
                    )
                    Text(
                        text = "Ask Quest AI for custom explanations or code snippets",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                    )
                }
                IconCanvas(14.dp) { d -> drawChevronRight(d) }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 2, onSelectTab = onSelectTab)
    }
}

@Composable
private fun ControlBtn(
    icon: DrawScope.(Float) -> Unit,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (enabled) CardBg else CardBg.copy(alpha = 0.4f))
            .border(1.dp, Color.White.copy(alpha = if (enabled) 0.08f else 0.02f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        IconCanvas(16.dp, icon)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextSecondary)
        )
    }
}

@Composable
private fun ComplexityCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(CardBg)
            .border(1.dp, Color.White.copy(alpha = 0.06f), shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = title, style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary))
        Text(
            text = value,
            style = TextStyle(
                fontFamily = Mono,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
        )
        Text(text = subtitle, style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextPrimary
        )
    )
}

@Composable
private fun Badge(text: String, background: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .height(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Mono,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = textColor
            )
        )
    }
}

// ---------- Canvas icons ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawChevronLeft(d: Float) {
    val w = 1.8f * d
    drawLine(TextPrimary, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(TextPrimary, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChevronRight(d: Float) {
    val w = 1.6f * d
    drawLine(TextSecondary, Offset(5f * d, 3f * d), Offset(10f * d, 8f * d), w, StrokeCap.Round)
    drawLine(TextSecondary, Offset(10f * d, 8f * d), Offset(5f * d, 13f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawReset(d: Float) {
    val c = TextSecondary
    drawArc(
        color = c, startAngle = 30f, sweepAngle = 300f, useCenter = false,
        topLeft = Offset(2f * d, 2f * d), size = Size(14f * d, 14f * d),
        style = lineStroke(d)
    )
    drawLine(c, Offset(13f * d, 2f * d), Offset(15f * d, 6f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(15f * d, 6f * d), Offset(11f * d, 7f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawStepPrev(d: Float) {
    val c = TextSecondary
    drawLine(c, Offset(4f * d, 4f * d), Offset(4f * d, 14f * d), 1.8f * d, StrokeCap.Round)
    val path = Path().apply {
        moveTo(14f * d, 4f * d)
        lineTo(7f * d, 9f * d)
        lineTo(14f * d, 14f * d)
        close()
    }
    drawPath(path, c, style = lineStroke(d))
}

private fun DrawScope.drawStepNext(d: Float) {
    val c = TextSecondary
    drawLine(c, Offset(14f * d, 4f * d), Offset(14f * d, 14f * d), 1.8f * d, StrokeCap.Round)
    val path = Path().apply {
        moveTo(4f * d, 4f * d)
        lineTo(11f * d, 9f * d)
        lineTo(4f * d, 14f * d)
        close()
    }
    drawPath(path, c, style = lineStroke(d))
}

private fun DrawScope.drawPlay(d: Float) {
    val path = Path().apply {
        moveTo(6f * d, 4f * d)
        lineTo(15f * d, 10f * d)
        lineTo(6f * d, 16f * d)
        close()
    }
    drawPath(path, Color.White)
}

private fun DrawScope.drawPause(d: Float) {
    val w = 2.5f * d
    drawLine(Color.White, Offset(6f * d, 4f * d), Offset(6f * d, 16f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(12f * d, 4f * d), Offset(12f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawBrain(d: Float) {
    val c = PurpleSoft
    drawCircle(c, radius = 6f * d, center = Offset(9f * d, 9f * d), style = lineStroke(d))
    drawLine(c, Offset(9f * d, 3f * d), Offset(9f * d, 15f * d), 1.2f * d, StrokeCap.Round)
}

private fun DrawScope.drawSparkles(d: Float, color: Color) {
    drawStar(d, color, cx = 8f, cy = 11f, r = 5.5f)
    drawStar(d, color, cx = 14f, cy = 4.5f, r = 2.8f)
}

private fun DrawScope.drawStar(d: Float, color: Color, cx: Float, cy: Float, r: Float) {
    val i = r * 0.3f
    val path = Path().apply {
        moveTo(cx * d, (cy - r) * d)
        lineTo((cx + i) * d, (cy - i) * d)
        lineTo((cx + r) * d, cy * d)
        lineTo((cx + i) * d, (cy + i) * d)
        lineTo(cx * d, (cy + r) * d)
        lineTo((cx - i) * d, (cy + i) * d)
        lineTo((cx - r) * d, cy * d)
        lineTo((cx - i) * d, (cy - i) * d)
        close()
    }
    drawPath(path, color, style = lineStroke(d, 1.2f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AlgorithmVisualizerScreenPreview() {
    AlgorithmVisualizerScreen()
}
