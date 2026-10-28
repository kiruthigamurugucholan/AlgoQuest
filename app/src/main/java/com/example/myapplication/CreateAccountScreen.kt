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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.myapplication.viewmodel.AuthUiState
import com.example.myapplication.viewmodel.AuthViewModel
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
private val Green = Color(0xFF2ECC71)
private val Gold = Color(0xFFFFB84D)
private val Pink = Color(0xFFFF5C8A)
private val FieldBg = Color(0xFF1A1F36)
private val ButtonBg = Color(0xFF222943)
private val BorderColor = Color(0xFF303853)

private val Poppins: FontFamily = FontFamily.SansSerif
private val Inter: FontFamily = FontFamily.SansSerif

@Composable
fun CreateAccountScreen(
    authViewModel: AuthViewModel? = null,
    onCreate: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onLogin: () -> Unit = {},
    onGoogle: () -> Unit = {},
    initialName: String = "",
    initialEmail: String = "",
    initialPassword: String = "",
    initialAgreed: Boolean = false
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }
    var agreed by remember { mutableStateOf(initialAgreed) }
    var localError by remember { mutableStateOf("") }

    val authState by authViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(AuthUiState.Idle) }
    val isLoading = authState is AuthUiState.Loading
    val serverError = (authState as? AuthUiState.Error)?.message

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
        // Welcome
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoMark(48.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Your potential, unlocked.",
                    style = TextStyle(
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Start with curiosity. We’ll handle the map.",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = TextSecondary
                    )
                )
            }
        }

        FormField(
            label = "Your name",
            value = name,
            onValueChange = { name = it; localError = "" },
            placeholder = "Alex Morgan",
            icon = { d -> drawUser(d) },
            keyboardType = KeyboardType.Text
        )
        FormField(
            label = "Email address",
            value = email,
            onValueChange = { email = it; localError = "" },
            placeholder = "alex.morgan@email.com",
            icon = { d -> drawMail(d) },
            keyboardType = KeyboardType.Email
        )
        FormField(
            label = "Password",
            value = password,
            onValueChange = { password = it; localError = "" },
            placeholder = "At least 8 characters",
            icon = { d -> drawLock(d) },
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        // Password strength
        if (password.isNotEmpty()) {
            val (strengthText, strengthColor) = when {
                password.length >= 12 -> "Strong password · ${password.length} characters" to Green
                password.length >= 8 -> "Good password · ${password.length} characters" to Gold
                else -> "Use at least 8 characters" to Pink
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCanvas(14.dp) { d -> drawCircleCheck(d, strengthColor) }
                Text(
                    text = strengthText,
                    style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = strengthColor)
                )
            }
        }

        // Consent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { agreed = !agreed },
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            val boxShape = RoundedCornerShape(4.dp)
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(boxShape)
                    .background(if (agreed) Purple else Color.Transparent)
                    .border(1.dp, if (agreed) Purple else BorderColor, boxShape),
                contentAlignment = Alignment.Center
            ) {
                if (agreed) IconCanvas(12.dp) { d -> drawCheckMark(d) }
            }
            Text(
                text = "I agree to the Terms of Service and Privacy Policy.",
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    fontFamily = Inter,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondary
                )
            )
        }

        val displayError = localError.ifEmpty { serverError ?: "" }
        if (displayError.isNotEmpty()) {
            Text(
                text = displayError,
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = Pink)
            )
        }

        // Create account button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isLoading) Purple.copy(alpha = 0.5f) else Purple)
                .clickable(enabled = !isLoading) {
                    localError = when {
                        name.isBlank() -> "Please enter your name."
                        !email.contains("@") || !email.contains(".") ->
                            "Please enter a valid email address."
                        password.length < 8 -> "Password must be at least 8 characters."
                        !agreed -> "Please accept the Terms of Service and Privacy Policy."
                        else -> ""
                    }
                    if (localError.isEmpty()) {
                        if (authViewModel != null) {
                            authViewModel.register(name.trim(), email.trim(), password) {
                                onCreate(name.trim(), email.trim(), password)
                            }
                        } else {
                            onCreate(name.trim(), email.trim(), password)
                        }
                    }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Creating account...",
                    style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.White)
                )
            } else {
                IconCanvas(18.dp) { d -> drawArrowRight(d) }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Create account",
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
            }
        }

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f).height(1.dp).background(BorderColor))
            Text(
                text = "or continue with",
                style = TextStyle(fontFamily = Inter, fontSize = 12.sp, color = TextMuted)
            )
            Box(Modifier.weight(1f).height(1.dp).background(BorderColor))
        }

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

        Text(
            text = "Already have an account?  Log in",
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onLogin),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = "Your progress syncs securely across devices.",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = Inter,
                fontSize = 11.sp,
                color = TextMuted,
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

private fun DrawScope.drawUser(d: Float) {
    val c = TextSecondary
    drawCircle(c, radius = 3.2f * d, center = Offset(9f * d, 6f * d), style = lineStroke(d))
    drawArc(
        color = c, startAngle = 180f, sweepAngle = 180f, useCenter = false,
        topLeft = Offset(3f * d, 11f * d), size = Size(12f * d, 12f * d),
        style = lineStroke(d)
    )
}

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

private fun DrawScope.drawCircleCheck(d: Float, color: Color) {
    drawCircle(color, radius = 6f * d, center = Offset(7f * d, 7f * d), style = lineStroke(d, 1.2f))
    drawLine(color, Offset(4.5f * d, 7f * d), Offset(6.4f * d, 9f * d), 1.2f * d, StrokeCap.Round)
    drawLine(color, Offset(6.4f * d, 9f * d), Offset(9.7f * d, 5f * d), 1.2f * d, StrokeCap.Round)
}

private fun DrawScope.drawCheckMark(d: Float) {
    drawLine(Color.White, Offset(2.5f * d, 6.2f * d), Offset(5f * d, 8.7f * d), 1.8f * d, StrokeCap.Round)
    drawLine(Color.White, Offset(5f * d, 8.7f * d), Offset(9.5f * d, 3.5f * d), 1.8f * d, StrokeCap.Round)
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

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CreateAccountScreenPreview() {
    CreateAccountScreen(
        initialName = "Alex Morgan",
        initialEmail = "alex.morgan@email.com",
        initialPassword = "abcdefghijkl",
        initialAgreed = true
    )
}
