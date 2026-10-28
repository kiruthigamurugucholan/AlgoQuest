package com.example.myapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Cyan = Color(0xFF00D2FF)
private val Purple = Color(0xFF6C5CE7)
private val PurpleLight = Color(0xFFA29BFE)
private val PurpleDark = Color(0xFF5B4BD5)
private val PurpleSoft = Color(0xFFB5A7FF)
private val Green = Color(0xFF2ECC71)
private val CardBg = Color(0xFF1A1F36)
private val AiBubbleBg = Color(0xFF24213F)
private val UserBubbleBg = Color(0xFF6C5CE7)
private val FieldBg = Color(0xFF161A2E)
private val CodeBg = Color(0xFF121526)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif
private val Mono: FontFamily = FontFamily.Monospace

@Composable
fun QuestAiChatScreen(
    viewModel: AiChatViewModel? = null,
    onBack: () -> Unit = {},
    initialQuery: String = "",
    onSelectTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val vm = viewModel ?: remember {
        AiChatViewModel(context.applicationContext as android.app.Application)
    }
    val uiState by vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf(initialQuery) }

    // Load initial history or fire initial query
    LaunchedEffect(Unit) {
        vm.loadHistory()
        if (initialQuery.isNotBlank()) {
            vm.sendMessage(initialQuery)
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(uiState.messages.size, uiState.isThinking) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

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
                MiniMark(32.dp)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Quest AI Ally",
                        style = TextStyle(
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
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
                            text = "Online · REST Engine",
                            style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
                        )
                    }
                }
            }

            Badge("PRO", Purple.copy(alpha = 0.15f), PurpleSoft)
        }

        // Quick suggestions header
        val suggestions = listOf(
            "Binary Search intuitive demo",
            "Explain O(log N)",
            "Two Pointers vs Sliding Window",
            "Recursion stack trace"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.take(3).forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardBg)
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
                        .clickable {
                            inputText = suggestion
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = suggestion,
                        style = TextStyle(fontFamily = Inter, fontSize = 11.sp, color = TextSecondary)
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
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
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

        // Shared Bottom Navigation Bar
        AlgoQuestBottomBar(selectedTab = 3, onSelectTab = onSelectTab)
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
                style = TextStyle(fontFamily = Inter, fontSize = 14.sp, lineHeight = 20.sp, color = TextPrimary)
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
            .border(1.dp, Color.White.copy(alpha = 0.07f), cardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
            IconCanvas(16.dp) { d -> drawSparkles(d, Cyan) }
        }

        Text(
            text = msg.text,
            style = TextStyle(fontFamily = Inter, fontSize = 14.sp, lineHeight = 21.sp, color = TextPrimary)
        )

        msg.codeSnippet?.let { code ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CodeBg)
                    .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kotlin Snippet",
                        style = TextStyle(fontFamily = Mono, fontSize = 10.sp, color = TextMuted)
                    )
                    Text(
                        text = "Tap to Copy",
                        modifier = Modifier.clickable { onCopy(code) },
                        style = TextStyle(fontFamily = Inter, fontSize = 10.sp, color = PurpleSoft, fontWeight = FontWeight.SemiBold)
                    )
                }
                Text(
                    text = code,
                    style = TextStyle(fontFamily = Mono, fontSize = 12.sp, lineHeight = 18.sp, color = Cyan)
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
            text = "Quest AI is thinking...",
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

private fun DrawScope.drawChevronLeft(d: Float) {
    val w = 1.8f * d
    drawLine(TextPrimary, Offset(11f * d, 4f * d), Offset(5f * d, 10f * d), w, StrokeCap.Round)
    drawLine(TextPrimary, Offset(5f * d, 10f * d), Offset(11f * d, 16f * d), w, StrokeCap.Round)
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun QuestAiChatScreenPreview() {
    QuestAiChatScreen()
}
