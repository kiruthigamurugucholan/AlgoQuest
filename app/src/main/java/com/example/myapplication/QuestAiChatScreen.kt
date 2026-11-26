package com.example.myapplication

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import com.example.myapplication.ui.navigation.preventPagerSwipe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.response.AiMessageDto
import com.example.myapplication.viewmodel.AiChatViewModel
import com.example.myapplication.ui.components.MarkdownText
import com.example.myapplication.ui.components.MarkdownCodeBlockCard
import com.example.myapplication.ui.components.LanguageDetector

import com.example.myapplication.ui.theme.AlgoTheme

private val Bg: Color @Composable get() = AlgoTheme.colors.bg
private val TextPrimary: Color @Composable get() = AlgoTheme.colors.textPrimary
private val TextSecondary: Color @Composable get() = AlgoTheme.colors.textSecondary
private val TextMuted: Color @Composable get() = AlgoTheme.colors.textMuted
private val Cyan: Color @Composable get() = AlgoTheme.colors.cyan
private val Purple: Color @Composable get() = AlgoTheme.colors.purple
private val PurpleLight: Color @Composable get() = AlgoTheme.colors.purpleLight
private val PurpleDark: Color @Composable get() = AlgoTheme.colors.purpleDark
private val PurpleSoft: Color @Composable get() = AlgoTheme.colors.purpleSoft
private val Green: Color @Composable get() = AlgoTheme.colors.green
private val CardBg: Color @Composable get() = AlgoTheme.colors.cardBg
private val AiBubbleBg: Color @Composable get() = AlgoTheme.colors.activeCardBg
private val UserBubbleBg: Color @Composable get() = AlgoTheme.colors.purple
private val FieldBg: Color @Composable get() = AlgoTheme.colors.fieldBg
private val CodeBg: Color @Composable get() = AlgoTheme.colors.codeBg
private val BorderColor: Color @Composable get() = AlgoTheme.colors.border

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily @Composable get() = AlgoTheme.editorFontFamily

@Composable
fun QuestAiChatScreen(
    viewModel: AiChatViewModel? = null,
    onBack: () -> Unit = {},
    initialQuery: String = "",
    onConsumeInitialQuery: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    showBottomBar: Boolean = true
) {
    val context = LocalContext.current
    val vm = viewModel ?: remember {
        AiChatViewModel(context.applicationContext as android.app.Application)
    }
    val uiState by vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    var showSessionsSheet by remember { mutableStateOf(false) }

    // Keep screen awake only while Quest AI is actively generating a response on CPU.
    // Automatically and immediately clears FLAG_KEEP_SCREEN_ON when response completes,
    // errors, request is cancelled, or this composable is disposed.
    DisposableEffect(uiState.isThinking) {
        val window = context.findActivity()?.window
        if (uiState.isThinking) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Load initial history or fire initial query
    LaunchedEffect(Unit) {
        vm.loadHistory()
    }

    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotBlank()) {
            val q = initialQuery
            onConsumeInitialQuery()
            vm.sendMessage(q)
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(uiState.messages.size, uiState.isThinking) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Bg)
                .statusBarsPadding()
                .imePadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val textPrimary = TextPrimary
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(16.dp) { d -> drawChevronLeft(d, textPrimary) }
                    }
                    MiniMark(32.dp)
                    Column(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = uiState.currentSessionTitle ?: "Quest AI Ally",
                            style = TextStyle(
                                fontFamily = Poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            ),
                            maxLines = 1
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Green)
                            )
                            Text(
                                text = "Online · Ollama Qwen 3 (4B)",
                                style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val historyIconColor = PurpleSoft

                    // Conversation History Drawer Button - visibly placed next to PRO badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .border(
                                1.dp,
                                if (AlgoTheme.colors.isDark) Purple.copy(alpha = 0.5f) else Purple.copy(alpha = 0.35f),
                                CircleShape
                            )
                            .clickable {
                                vm.loadSessions(autoSelectLatest = false)
                                showSessionsSheet = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        IconCanvas(18.dp) { d -> drawHistoryList(d, historyIconColor) }
                    }

                    Badge("PRO", Purple.copy(alpha = 0.15f), PurpleSoft)
                }
            }

        // Quick suggestions header with horizontal scrolling
        val suggestions = listOf(
            "Binary Search intuitive demo",
            "Explain O(log N)",
            "Two Pointers vs Sliding Window",
            "Recursion stack trace"
        )
        val suggestionsScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .preventPagerSwipe()
                .horizontalScroll(suggestionsScrollState)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            suggestions.forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBg)
                        .border(
                            1.dp,
                            if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            inputText = suggestion
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = suggestion,
                        style = TextStyle(
                            fontFamily = Inter,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                if (msg.sender == "user") {
                    UserMessageItem(msg)
                } else {
                    AiMessageItem(msg, onCopy = { snippet ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AlgoQuest Code", snippet)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            if (uiState.isThinking) {
                item {
                    ThinkingIndicator()
                }
            }
        }

        uiState.error?.let { err ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE74C3C).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFFE74C3C).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = err,
                    style = TextStyle(fontFamily = Inter, fontSize = 11.5.sp, color = Color(0xFFE74C3C)),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "✕",
                    style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Color(0xFFE74C3C), fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { vm.clearError() }
                )
            }
        }

        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FieldBg)
                    .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, RoundedCornerShape(16.dp))
                    .preventPagerSwipe()
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "Ask Quest AI anything...",
                        style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextMuted)
                    )
                }
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Inter, fontSize = 14.sp, color = TextPrimary),
                    cursorBrush = SolidColor(Purple),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (inputText.isNotBlank() && !uiState.isThinking) Purple else Purple.copy(alpha = 0.4f))
                    .clickable(enabled = inputText.isNotBlank() && !uiState.isThinking) {
                        val query = inputText.trim()
                        inputText = ""
                        vm.sendMessage(query)
                    },
                contentAlignment = Alignment.Center
            ) {
                IconCanvas(18.dp) { d -> drawSend(d) }
            }
        }

        // Retry banner if AI answer generated but MySQL persistence failed
        uiState.pendingRetryMessage?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE67E22).copy(alpha = 0.12f))
                    .border(1.dp, Color(0xFFE67E22).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️ Response generated, but cloud sync failed.",
                    style = TextStyle(fontFamily = Inter, fontSize = 11.5.sp, color = Color(0xFFE67E22)),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE67E22))
                        .clickable { vm.retryPendingSave() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Retry Sync",
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    )
                }
            }
        }

        // Shared Bottom Navigation Bar
        if (showBottomBar) {
            AlgoQuestBottomBar(selectedTab = 3, onSelectTab = onSelectTab)
        }
    }

    // Persistent Chat History Sessions Bottom Sheet Overlay
    if (showSessionsSheet) {
        SessionsBottomSheetOverlay(
            uiState = uiState,
            onSelectSession = { sid ->
                vm.selectSession(sid)
                showSessionsSheet = false
            },
            onNewChat = {
                vm.startNewChat()
                showSessionsSheet = false
            },
            onDeleteSession = { sid ->
                vm.deleteSession(sid)
            },
            onRefresh = {
                vm.loadSessions(autoSelectLatest = false)
            },
            onDismiss = { showSessionsSheet = false }
        )
    }
}
}

@Composable
private fun UserMessageItem(msg: AiMessageDto) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .width(270.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp))
                .background(UserBubbleBg)
                .padding(14.dp)
        ) {
            Text(
                text = msg.text,
                style = TextStyle(fontFamily = Inter, fontSize = 14.sp, lineHeight = 20.sp, color = Color.White)
            )
        }
    }
}

@Composable
private fun AiMessageItem(
    msg: AiMessageDto,
    onCopy: (String) -> Unit = {}
) {
    val cardShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(AiBubbleBg)
            .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.07f) else BorderColor, cardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val sparkleColor = Cyan
        val isStreaming = msg.timestamp == "Generating..." || msg.tags.contains("Streaming")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniMark(24.dp)
            Text(
                text = "Quest AI",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            )
            IconCanvas(16.dp) { d -> drawSparkles(d, sparkleColor) }
        }

        MarkdownText(
            markdown = if (isStreaming && msg.text.isNotBlank()) "${msg.text} ▌" else msg.text,
            modifier = Modifier.fillMaxWidth(),
            onCopyCode = onCopy
        )

        if (isStreaming) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Cyan)
                )
                Text(
                    text = "Streaming response...",
                    style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = Cyan)
                )
            }
        }

        msg.codeSnippet?.let { code ->
            if (!msg.text.contains(code)) {
                MarkdownCodeBlockCard(
                    code = code,
                    language = LanguageDetector.resolve("", code),
                    onCopy = onCopy
                )
            }
        }

        if (msg.tags.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                msg.tags.forEach { tag ->
                    Badge(tag, Cyan.copy(alpha = 0.09f), Cyan)
                }
            }
        }
    }
}

@Composable
private fun ThinkingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniMark(24.dp)
        Text(
            text = "Quest AI is connecting to Qwen 3 (4B)...",
            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = PurpleSoft)
        )
    }
}

@Composable
private fun MiniMark(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.linearGradient(
                    colors = listOf(PurpleLight, Purple, PurpleDark),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val k = this.size.width / 104f
            val w = 3f * k
            fun o(x: Float, y: Float) = Offset(x * k, y * k)
            val white = Color.White
            drawLine(white, o(37f, 30f), o(22f, 52f), w, StrokeCap.Round)
            drawLine(white, o(22f, 52f), o(37f, 74f), w, StrokeCap.Round)
            drawLine(white, o(67f, 30f), o(82f, 52f), w, StrokeCap.Round)
            drawLine(white, o(82f, 52f), o(67f, 74f), w, StrokeCap.Round)
            drawLine(white, o(60f, 23f), o(45f, 79f), w, StrokeCap.Round)
        }
    }
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
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = textColor
            )
        )
    }
}

// ---------- Small canvas icons ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun DrawScope.drawChevronLeft(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawSend(d: Float) {
    val path = Path().apply {
        moveTo(3f * d, 3f * d)
        lineTo(16f * d, 9f * d)
        lineTo(3f * d, 15f * d)
        lineTo(6f * d, 9f * d)
        close()
    }
    drawPath(path, Color.White)
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
    drawPath(path, color, style = Stroke(width = 1.2f * d, join = StrokeJoin.Round))
}

private fun DrawScope.drawPlus(d: Float, color: Color) {
    val w = 1.8f * d
    val mid = 8f * d
    drawLine(color, Offset(mid, 3.5f * d), Offset(mid, 12.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(3.5f * d, mid), Offset(12.5f * d, mid), w, StrokeCap.Round)
}

private fun DrawScope.drawHistoryList(d: Float, color: Color) {
    val w = 2.0f * d
    drawLine(color, Offset(3.5f * d, 5.5f * d), Offset(14.5f * d, 5.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(3.5f * d, 9.5f * d), Offset(14.5f * d, 9.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(3.5f * d, 13.5f * d), Offset(10.5f * d, 13.5f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawTrash(d: Float, color: Color) {
    val w = 1.4f * d
    drawLine(color, Offset(3f * d, 5f * d), Offset(13f * d, 5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5f * d, 5f * d), Offset(5.5f * d, 13f * d), w, StrokeCap.Round)
    drawLine(color, Offset(11f * d, 5f * d), Offset(10.5f * d, 13f * d), w, StrokeCap.Round)
    drawLine(color, Offset(5.5f * d, 13f * d), Offset(10.5f * d, 13f * d), w, StrokeCap.Round)
    drawLine(color, Offset(6f * d, 3f * d), Offset(10f * d, 3f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawCheck(d: Float, color: Color) {
    val w = 1.8f * d
    drawLine(color, Offset(3.5f * d, 8f * d), Offset(6.5f * d, 11.5f * d), w, StrokeCap.Round)
    drawLine(color, Offset(6.5f * d, 11.5f * d), Offset(12.5f * d, 4.5f * d), w, StrokeCap.Round)
}

@Composable
private fun SessionsBottomSheetOverlay(
    uiState: com.example.myapplication.viewmodel.AiChatUiState,
    onSelectSession: (Int) -> Unit,
    onNewChat: () -> Unit,
    onDeleteSession: (Int) -> Unit,
    onRefresh: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val checkColor = Green
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(CardBg)
                .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.08f) else BorderColor, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(enabled = false) {}
                .padding(20.dp)
        ) {
            // Drag handle indicator
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conversations",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                    )
                    Badge("${uiState.sessions.size}", Purple.copy(alpha = 0.15f), PurpleSoft)
                }

                // Close Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(FieldBg)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextSecondary))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Clear + New Chat Action inside Conversations Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Purple, PurpleLight)
                        )
                    )
                    .clickable(onClick = onNewChat),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconCanvas(16.dp) { d -> drawPlus(d, Color.White) }
                    Text(
                        text = "+ Start a New Conversation",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (uiState.isLoadingSessions && uiState.sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading saved conversations...", style = TextStyle(fontFamily = Inter, fontSize = 13.sp, color = TextMuted))
                }
            } else if (uiState.sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (uiState.error != null) "Could not load conversations" else "No saved conversations yet",
                            style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextPrimary)
                        )
                        Text(
                            text = if (uiState.error != null) uiState.error!! else "Ask your first DSA question to start saving!",
                            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextMuted)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FieldBg)
                                .border(1.dp, if (AlgoTheme.colors.isDark) Color.White.copy(alpha = 0.1f) else BorderColor, RoundedCornerShape(8.dp))
                                .clickable { onRefresh() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Refresh History", style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = PurpleSoft, fontWeight = FontWeight.Medium))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.sessions, key = { it.id }) { session ->
                        val isSelected = session.id == uiState.currentSessionId
                        val itemBg = if (isSelected) Purple.copy(alpha = 0.15f) else FieldBg
                        val itemBorder = if (isSelected) Purple.copy(alpha = 0.6f) else BorderColor.copy(alpha = 0.3f)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(itemBg)
                                .border(1.dp, itemBorder, RoundedCornerShape(14.dp))
                                .clickable { onSelectSession(session.id) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = session.title,
                                        style = TextStyle(
                                            fontFamily = Inter,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            fontSize = 13.5.sp,
                                            color = if (isSelected) PurpleSoft else TextPrimary
                                        ),
                                        maxLines = 1
                                    )
                                    if (session.messageCount > 0) {
                                        Badge("${session.messageCount}", Cyan.copy(alpha = 0.1f), Cyan)
                                    }
                                }
                                val preview = if (!session.lastMessage.isNullOrBlank()) session.lastMessage else "No messages"
                                Text(
                                    text = preview,
                                    style = TextStyle(fontFamily = Inter, fontSize = 11.5.sp, color = TextMuted),
                                    maxLines = 1
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isSelected) {
                                    IconCanvas(16.dp) { d -> drawCheck(d, checkColor) }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CardBg)
                                        .clickable { onDeleteSession(session.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconCanvas(14.dp) { d -> drawTrash(d, Color(0xFFE74C3C).copy(alpha = 0.8f)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QuestAiChatScreenPreview() {
    QuestAiChatScreen()
}

/**
 * Safely resolves an [Activity] from a given [Context], traversing any [ContextWrapper] layers.
 */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
