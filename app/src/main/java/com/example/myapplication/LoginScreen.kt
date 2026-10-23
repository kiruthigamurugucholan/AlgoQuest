package com.example.myapplication

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val Bg = Color(0xFF0F1220)
private val TextPrimary = Color(0xFFF4F6FF)
private val TextSecondary = Color(0xFFC3CBE2)
private val TextMuted = Color(0xFF98A2C0)
private val Purple = Color(0xFF6C5CE7)
private val PurpleLight = Color(0xFFA29BFE)
private val PurpleDark = Color(0xFF5B4BD5)
private val LinkColor = Color(0xFFAA9FFF)
private val Gold = Color(0xFFFFB84D)
private val Pink = Color(0xFFFF5C8A)
private val FieldBg = Color(0xFF1A1F36)
private val ButtonBg = Color(0xFF222943)
private val CardBg = Color(0xFF272347)
private val BorderColor = Color(0xFF303853)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    userName: String = "",
    initialEmail: String = "",
    initialPassword: String = ""
) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome identity
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LogoMark(48.dp)
            val greeting = if (userName.isNotBlank()) "is waiting, $userName." else "is waiting."
            Text(
                text = "Your next “aha!” moment\n$greeting",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            )
        }

        FormField(
            label = "Email address",
            value = email,
            onValueChange = { email = it },
            placeholder = "alex.morgan@email.com",
            icon = { d -> drawMail(d) },
            keyboardType = KeyboardType.Email
        )
        FormField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "Enter your password",
            icon = { d -> drawLock(d) },
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Text(
            text = "Forgot password?",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onForgotPassword),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 13.sp,
                color = LinkColor,
                textAlign = TextAlign.End
            )
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Pink)
            )
        }

        // Log in button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Purple)
                .clickable {
                    error = when {
                        !email.contains("@") || !email.contains(".") ->
                            "Please enter a valid email address."
                        password.isEmpty() -> "Please enter your password."
                        else -> ""
                    }
                    if (error.isEmpty()) onLogin(email.trim(), password)
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCanvas(18.dp) { d -> drawArrowRight(d) }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Log in",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.White
                )
            )
        }

        Text(
            text = "or pick up where you left off with",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        )

        // Google button
        val buttonShape = RoundedCornerShape(16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(buttonShape)
                .background(ButtonBg)
                .border(1.dp, BorderColor, buttonShape)
                .clickable(onClick = onGoogle),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCanvas(18.dp) { d -> drawChrome(d) }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Continue with Google",
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }

        // Streak reminder card
        val cardShape = RoundedCornerShape(16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(CardBg)
                .border(1.dp, Color.White.copy(alpha = 0.07f), cardShape)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCanvas(28.dp) { d -> drawFlame(d) }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Keep your 7-day streak alive",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Just 15 minutes today can make a difference.",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondary
                    )
                )
            }
        }

        Text(
            text = "New to AlgoQuest?  Create an account",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCreateAccount),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: DrawScope.(Float) -> Unit,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = TextPrimary
            )
        )
        val shape = RoundedCornerShape(16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(shape)
                .background(FieldBg)
                .border(1.dp, BorderColor, shape)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCanvas(18.dp, icon)
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(fontFamily = Inter, fontSize = 14.sp, color = TextMuted)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Inter, fontSize = 14.sp, color = TextPrimary),
                    cursorBrush = SolidColor(Purple),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = if (isPassword) PasswordVisualTransformation()
                    else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LogoMark(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.27f))
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

// ---------- Small icons drawn in code ----------

@Composable
private fun IconCanvas(size: Dp, draw: DrawScope.(Float) -> Unit) {
    Canvas(Modifier.size(size)) { draw(this, density) }
}

private fun lineStroke(d: Float, w: Float = 1.5f) =
    Stroke(width = w * d, cap = StrokeCap.Round, join = StrokeJoin.Round)

private fun DrawScope.drawMail(d: Float) {
    val c = TextSecondary
    drawRoundRect(
        color = c, topLeft = Offset(2f * d, 4f * d), size = Size(14f * d, 10f * d),
        cornerRadius = CornerRadius(2f * d), style = lineStroke(d)
    )
    drawLine(c, Offset(2.5f * d, 5.5f * d), Offset(9f * d, 10f * d), 1.5f * d, StrokeCap.Round)
    drawLine(c, Offset(9f * d, 10f * d), Offset(15.5f * d, 5.5f * d), 1.5f * d, StrokeCap.Round)
}

private fun DrawScope.drawLock(d: Float) {
    val c = TextSecondary
    drawRoundRect(
        color = c, topLeft = Offset(3f * d, 8f * d), size = Size(12f * d, 8f * d),
        cornerRadius = CornerRadius(2f * d), style = lineStroke(d)
    )
    drawArc(
        color = c, startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(5.5f * d, 2.5f * d), size = Size(7f * d, 7f * d),
        style = lineStroke(d)
    )
}

private fun DrawScope.drawArrowRight(d: Float) {
    val w = 2f * d
    drawLine(Color.White, Offset(3f * d, 9f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 4f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
    drawLine(Color.White, Offset(10f * d, 14f * d), Offset(15f * d, 9f * d), w, StrokeCap.Round)
}

private fun DrawScope.drawChrome(d: Float) {
    val c = Color.White
    val center = Offset(9f * d, 9f * d)
    drawCircle(c, radius = 8f * d, center = center, style = lineStroke(d, 1.4f))
    drawCircle(c, radius = 3f * d, center = center, style = lineStroke(d, 1.4f))
    listOf(-30.0, 90.0, 210.0).forEach { deg ->
        val rad = deg * PI / 180.0
        val ca = cos(rad).toFloat()
        val sa = sin(rad).toFloat()
        drawLine(
            c,
            Offset((9f + ca * 3f) * d, (9f + sa * 3f) * d),
            Offset((9f + ca * 8f) * d, (9f + sa * 8f) * d),
            1.4f * d, StrokeCap.Round
        )
    }
}

private fun DrawScope.drawFlame(d: Float) {
    val outer = Path().apply {
        moveTo(14f * d, 3.5f * d)
        cubicTo(15f * d, 8f * d, 21f * d, 10.5f * d, 21f * d, 17f * d)
        cubicTo(21f * d, 21.5f * d, 17.8f * d, 24.5f * d, 14f * d, 24.5f * d)
        cubicTo(10.2f * d, 24.5f * d, 7f * d, 21.5f * d, 7f * d, 17.5f * d)
        cubicTo(7f * d, 14.5f * d, 8.5f * d, 12.5f * d, 10f * d, 11f * d)
        cubicTo(10.5f * d, 12.8f * d, 11.5f * d, 13.8f * d, 12.5f * d, 14f * d)
        cubicTo(12f * d, 10.5f * d, 12.5f * d, 6.5f * d, 14f * d, 3.5f * d)
        close()
    }
    drawPath(outer, Gold, style = lineStroke(d, 1.6f))

    val inner = Path().apply {
        moveTo(14f * d, 15.5f * d)
        cubicTo(16.2f * d, 17.5f * d, 17f * d, 19f * d, 17f * d, 20.2f * d)
        cubicTo(17f * d, 21.7f * d, 15.7f * d, 22.8f * d, 14f * d, 22.8f * d)
        cubicTo(12.3f * d, 22.8f * d, 11f * d, 21.7f * d, 11f * d, 20.3f * d)
        cubicTo(11f * d, 19.3f * d, 11.7f * d, 18.5f * d, 12.4f * d, 17.7f * d)
        cubicTo(12.9f * d, 18.5f * d, 13.3f * d, 18.7f * d, 13.7f * d, 18.7f * d)
        cubicTo(13.9f * d, 17.7f * d, 14f * d, 16.7f * d, 14f * d, 15.5f * d)
        close()
    }
    drawPath(inner, Gold, style = lineStroke(d, 1.6f))
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        userName = "Alex",
        initialEmail = "alex.morgan@email.com",
        initialPassword = "abcdefghijkl"
    )
}

