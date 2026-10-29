package com.example.myapplication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.algorithm.CodeLanguage
import com.example.myapplication.viewmodel.VisualizerViewModel

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val RedAlert = Color(0xFFFF6B6B)
private val CardBg = Color(0xFF1A1F36)
private val SelectedBg = Color(0xFF25213E)
private val EliminatedColor = Color(0xFF202336)
private val CodeBg = Color(0xFF131728)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun AlgorithmVisualizerScreen(
    viewModel: VisualizerViewModel? = null,
    onBack: () -> Unit = {},
    onAskAi: (query: String) -> Unit = {},
    onSelectTab: (Int) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val vm = viewModel ?: remember {
        VisualizerViewModel(context.applicationContext as android.app.Application)
    }

    val selectedAlgIdx by vm.selectedAlg.collectAsState()
    val currentLanguage by vm.currentLanguage.collectAsState()
    val editableCode by vm.editableCode.collectAsState()
    val currentStepIdx by vm.currentStepIdx.collectAsState()
    val isPlaying by vm.isPlaying.collectAsState()
    val speed by vm.speed.collectAsState()

    val isInputEditorOpen by vm.isInputEditorOpen.collectAsState()
    val customArrayInput by vm.customArrayInput.collectAsState()
    val customTargetInput by vm.customTargetInput.collectAsState()
    val inputError by vm.inputError.collectAsState()
    val codeStatusMessage by vm.codeStatusMessage.collectAsState()

    val isAiSheetOpen by vm.isAiSheetOpen.collectAsState()
    val aiLoading by vm.aiLoading.collectAsState()
    val aiResponse by vm.aiResponse.collectAsState()
    val aiError by vm.aiError.collectAsState()
    val selectedAiAction by vm.selectedAiAction.collectAsState()

    val frames = vm.getCurrentFrames()
    val currentFrame = vm.getCurrentFrame()
    val currentMeta = vm.algMetadata.getOrElse(selectedAlgIdx) { vm.algMetadata[0] }

    var userCustomQuestion by remember { mutableStateOf("") }
    var isCodeEditorExpanded by remember { mutableStateOf(true) }

    // Auto complete on final step
    LaunchedEffect(currentStepIdx) {
        if (frames.isNotEmpty() && currentStepIdx == frames.size - 1) {
            vm.completeVisualizerActivity(currentMeta.slug)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                                text = "Code Visualizer Lab",
                                style = TextStyle(
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "User-driven code execution & state tracing",
                                style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                            )
                        }
                    }
                    Badge("EXEC RUNTIME", Cyan.copy(alpha = 0.09f), Cyan)
                }

                // Algorithm selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val algs = listOf("Binary Search", "Bubble Sort", "Two Pointers")
                    algs.forEachIndexed { idx, name ->
                        val selected = idx == selectedAlgIdx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Purple else CardBg)
                                .border(1.dp, if (selected) Purple else Color.White.copy(alpha = 0.06f), RoundedCornerShape(10.dp))
                                .clickable { vm.selectAlgorithm(idx) },
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

                // Interactive Code Editor Section
                SectionTitle("Interactive Code Editor & Trace")

                val codeShape = RoundedCornerShape(16.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(codeShape)
                        .background(CodeBg)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), codeShape)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Editor Top Bar: Language Selector & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Supported Language Selection Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CodeLanguage.values().forEach { lang ->
                                val isSelected = currentLanguage == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Purple else CardBg)
                                        .border(1.dp, if (isSelected) Purple else Color.White.copy(alpha = 0.06f), RoundedCornerShape(6.dp))
                                    .clickable { vm.setLanguage(lang) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = lang.displayName,
                                    style = TextStyle(
                                        fontFamily = Mono,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                )
                            }
                        }
                    }

                    // Reset & Run Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CardBg)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                .clickable { vm.resetCodeToTemplate() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "↺ Template",
                                style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Purple)
                                .clickable { vm.runAndVisualizeCode() }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "▶ Run & Trace",
                                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                            )
                        }
                    }
                }

                codeStatusMessage?.let { msg ->
                    Text(
                        text = "ℹ️ $msg",
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = Gold)
                    )
                }

                // Synchronized Code Editor with Line Numbers & Active Line Highlighting
                val lines = editableCode.lines()
                val activeLineNum = currentFrame.activeLineNumber

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    lines.forEachIndexed { index, lineText ->
                        val lineNum = index + 1
                        val isExecuting = lineNum == activeLineNum

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isExecuting) Purple.copy(alpha = 0.35f) else Color.Transparent)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Line number column
                            Text(
                                text = "$lineNum".padStart(2, ' '),
                                style = TextStyle(
                                    fontFamily = Mono,
                                    fontSize = 11.sp,
                                    fontWeight = if (isExecuting) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isExecuting) Cyan else TextMuted
                                )
                            )

                            // Active execution indicator dot
                            if (isExecuting) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Cyan)
                                )
                            } else {
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            // Code line content
                            Text(
                                text = lineText,
                                style = TextStyle(
                                    fontFamily = Mono,
                                    fontSize = 11.sp,
                                    fontWeight = if (isExecuting) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isExecuting) TextPrimary else TextSecondary
                                )
                            )
                        }
                    }
                }

                // Code Editor Editable Input toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBg)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                        .clickable { isCodeEditorExpanded = !isCodeEditorExpanded }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isCodeEditorExpanded) "▲ Hide Raw Editor" else "▼ Edit Code Directly",
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = PurpleSoft)
                    )
                }

                if (isCodeEditorExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Direct Code Input (${currentLanguage.displayName}):",
                            style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CodeBg)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            BasicTextField(
                                value = editableCode,
                                onValueChange = { vm.onCodeEdited(it) },
                                textStyle = TextStyle(fontFamily = Mono, fontSize = 11.sp, color = TextPrimary),
                                cursorBrush = SolidColor(Cyan),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            )
                        }
                    }
                }
            }

            // Custom Input & Randomize Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isInputEditorOpen) Purple.copy(alpha = 0.25f) else CardBg)
                        .border(1.dp, if (isInputEditorOpen) Purple else Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .clickable { vm.toggleInputEditor() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isInputEditorOpen) "Close Input Panel" else "⚙️ Input Parameters",
                        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = if (isInputEditorOpen) PurpleSoft else TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardBg)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                        .clickable { vm.randomizeInput() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎲 Randomize Values",
                        style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = Cyan)
                    )
                }
            }

            // Collapsible Input Parameters Panel
            AnimatedVisibility(
                visible = isInputEditorOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBg)
                        .border(1.dp, Purple.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Set Custom Execution Inputs",
                        style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                    )

                    Text(
                        text = "Array (2–12 numbers, comma-separated):",
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CodeBg)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = customArrayInput,
                            onValueChange = { vm.onArrayInputChanged(it) },
                            textStyle = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = TextPrimary),
                            cursorBrush = SolidColor(Cyan),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (selectedAlgIdx != 1) { // Target needed for BS & Two Pointers
                        Text(
                            text = if (selectedAlgIdx == 0) "Target to Find:" else "Target Sum:",
                            style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = TextMuted)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CodeBg)
                                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = customTargetInput,
                                onValueChange = { vm.onTargetInputChanged(it) },
                                textStyle = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = TextPrimary),
                                cursorBrush = SolidColor(Gold),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    inputError?.let { err ->
                        Text(
                            text = "⚠️ $err",
                            style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = RedAlert)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Purple)
                                .clickable { vm.applyCustomInput() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Apply & Execute",
                                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color.White)
                            )
                        }
                    }
                }
            }

            // Main Dynamic Execution Canvas
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
                // Info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val targetLabel = when (selectedAlgIdx) {
                        0 -> currentFrame.variables["target"]?.let { "Target: $it" } ?: "Target: Active"
                        1 -> "Mode: Ascending"
                        else -> currentFrame.variables["targetSum"]?.let { "Target Sum: $it" } ?: "Target Sum: Active"
                    }
                    Text(
                        text = targetLabel,
                        style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Gold)
                    )
                    Text(
                        text = if (frames.isNotEmpty()) "Step ${currentStepIdx + 1} of ${frames.size}" else "Step 0 of 0",
                        style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = TextMuted)
                    )
                }

                // Array Elements Animated Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentFrame.arraySnapshot.forEachIndexed { idx, value ->
                        val isFound = currentFrame.isFound && (
                                idx == currentFrame.pointer3 ||
                                        (currentFrame.pointerLabels[idx]?.contains("FOUND") == true) ||
                                        (currentFrame.pointerLabels[idx]?.contains("MATCH") == true) ||
                                        (currentFrame.pointerLabels[idx]?.contains("Sorted") == true)
                                )
                        val isMid = idx == currentFrame.pointer3
                        val isP1 = idx == currentFrame.pointer1
                        val isP2 = idx == currentFrame.pointer2
                        val isEliminated = idx in currentFrame.eliminatedIndices

                        val itemBg by animateColorAsState(
                            targetValue = when {
                                isFound -> Green
                                currentFrame.isSwap && (isP1 || isP2) -> Gold
                                isMid -> Gold
                                isEliminated -> EliminatedColor
                                else -> CardBg
                            },
                            animationSpec = tween(300),
                            label = "itemBg"
                        )

                        val borderCol = when {
                            isFound -> Green
                            isMid -> Gold
                            isP1 -> Cyan
                            isP2 -> PurpleSoft
                            else -> Color.White.copy(alpha = 0.05f)
                        }

                        val labelText = currentFrame.pointerLabels[idx] ?: when {
                            isP1 && isP2 && isMid -> "L M H"
                            isP1 && isMid -> "L M"
                            isP2 && isMid -> "M H"
                            isMid -> "M"
                            isP1 -> if (selectedAlgIdx == 1) "j" else "L"
                            isP2 -> if (selectedAlgIdx == 1) "j+1" else if (selectedAlgIdx == 2) "R" else "H"
                            else -> ""
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Pointer label box
                            Box(modifier = Modifier.height(18.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = labelText,
                                    style = TextStyle(
                                        fontFamily = Mono,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = when {
                                            isFound -> Green
                                            isMid -> Gold
                                            isP1 -> Cyan
                                            isP2 -> PurpleSoft
                                            else -> Color.Transparent
                                        }
                                    )
                                )
                            }

                            // Value card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
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
                                        fontSize = 11.sp,
                                        color = if (isEliminated) TextMuted.copy(alpha = 0.35f) else TextPrimary
                                    )
                                )
                            }

                            // Index
                            Text(
                                text = "[$idx]",
                                style = TextStyle(fontFamily = Mono, fontSize = 8.sp, color = TextMuted)
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    when (selectedAlgIdx) {
                        0 -> {
                            LegendItem("L: Low", Cyan)
                            LegendItem("M: Mid", Gold)
                            LegendItem("H: High", PurpleSoft)
                            LegendItem("Found", Green)
                        }
                        1 -> {
                            LegendItem("j", Cyan)
                            LegendItem("j+1", PurpleSoft)
                            LegendItem("Swap", Gold)
                            LegendItem("Sorted", Green)
                        }
                        else -> {
                            LegendItem("L: Left", Cyan)
                            LegendItem("R: Right", PurpleSoft)
                            LegendItem("Match", Green)
                        }
                    }
                }

                // Live Variables Watch Window
                if (currentFrame.variables.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CodeBg.copy(alpha = 0.85f))
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VARS:",
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Cyan)
                        )
                        currentFrame.variables.forEach { (k, v) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CardBg)
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

                // Step Description Box
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
                        IconCanvas(18.dp) { d -> drawSparkles(d, if (currentFrame.isFound) Green else Cyan) }
                        Text(
                            text = currentFrame.description,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = if (currentFrame.isFound) Green else TextPrimary
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
                    ControlBtn(icon = { d -> drawReset(d) }, onClick = { vm.reset() })
                    ControlBtn(icon = { d -> drawStepPrev(d) }, enabled = currentStepIdx > 0, onClick = { vm.stepPrev() })
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Purple)
                            .clickable { vm.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(20.dp) { d -> if (isPlaying) drawPause(d) else drawPlay(d) }
                    }
                    ControlBtn(icon = { d -> drawStepNext(d) }, enabled = currentStepIdx < frames.size - 1, onClick = { vm.stepNext() })
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardBg)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .clickable { vm.toggleSpeed() }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${speed}x",
                            style = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Cyan)
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
                    value = currentMeta.timeComplexity,
                    subtitle = currentMeta.complexityNote,
                    color = Cyan,
                    modifier = Modifier.weight(1f)
                )
                ComplexityCard(
                    title = "Space Complexity",
                    value = currentMeta.spaceComplexity,
                    subtitle = "Auxiliary space",
                    color = Green,
                    modifier = Modifier.weight(1f)
                )
            }

            // Gemini AI Ally Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2E2458), CardBg)))
                    .border(1.dp, PurpleSoft.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { vm.openAiSheet("EXPLAIN_STEP") }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Purple.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconCanvas(18.dp) { d -> drawSparkles(d, PurpleSoft) }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quest AI Ally",
                            style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                        )
                        Badge("GEMINI", PurpleSoft.copy(alpha = 0.15f), PurpleSoft)
                    }
                    Text(
                        text = "Real-time step & line analysis, debugging, and hints",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                    )
                }
                IconCanvas(14.dp) { d -> drawChevronRight(d) }
            }
        }

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 2, onSelectTab = onSelectTab)
    }

    // Gemini AI Ally Bottom Sheet / Panel
    AnimatedVisibility(
        visible = isAiSheetOpen,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable { vm.closeAiSheet() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(SelectedBg)
                    .border(1.dp, Purple.copy(alpha = 0.3f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconCanvas(18.dp) { d -> drawSparkles(d, PurpleSoft) }
                        Text(
                            text = "Quest AI Ally • Code & Step Analysis",
                            style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextPrimary)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .clickable { vm.closeAiSheet() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", style = TextStyle(fontFamily = Mono, fontSize = 12.sp, color = TextMuted))
                    }
                }

                // 5 Gemini AI Actions (Requirement 8)
                val actionsRow1 = listOf(
                    "EXPLAIN_LINE" to "🔍 Explain This Line",
                    "EXPLAIN_STEP" to "💡 Explain This Step",
                    "DEBUG_CODE" to "🐞 Debug My Code"
                )

                val actionsRow2 = listOf(
                    "GIVE_HINT" to "✨ Give Me a Hint",
                    "EXPLAIN_COMPLEXITY" to "📊 Time Complexity"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    actionsRow1.forEach { (actionKey, label) ->
                        val isSelected = selectedAiAction == actionKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Purple else CardBg)
                                .border(1.dp, if (isSelected) Purple else Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .clickable { vm.requestAiExplanation(actionKey) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = TextStyle(fontFamily = Inter, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else TextSecondary),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    actionsRow2.forEach { (actionKey, label) ->
                        val isSelected = selectedAiAction == actionKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Purple else CardBg)
                                .border(1.dp, if (isSelected) Purple else Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .clickable { vm.requestAiExplanation(actionKey) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = TextStyle(fontFamily = Inter, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = if (isSelected) Color.White else TextSecondary),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // AI Response Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardBg)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (aiLoading) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PurpleSoft, strokeWidth = 2.dp)
                            Text(
                                text = "Quest AI is analyzing your code and execution state...",
                                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextSecondary)
                            )
                        }
                    } else if (aiError != null) {
                        Text(
                            text = "⚠️ $aiError",
                            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = RedAlert)
                        )
                    } else if (aiResponse != null) {
                        val res = aiResponse!!
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = res.title,
                                    style = TextStyle(fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Cyan)
                                )
                                Badge(res.source, PurpleSoft.copy(alpha = 0.1f), PurpleSoft)
                            }
                            Text(
                                text = res.explanation,
                                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, lineHeight = 18.sp, color = TextPrimary)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CodeBg)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "💡 ${res.keyTakeaway}",
                                    style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = Gold)
                                )
                            }
                            if (res.codeTip.isNotEmpty()) {
                                Text(
                                    text = "📌 ${res.codeTip}",
                                    style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = PurpleSoft)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
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
