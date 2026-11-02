package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class AlgoQuestColors(
    val bg: Color,
    val cardBg: Color,
    val activeCardBg: Color,
    val selectedBg: Color,
    val fieldBg: Color,
    val codeBg: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val cyan: Color,
    val purple: Color,
    val purpleSoft: Color,
    val purpleLight: Color,
    val purpleDark: Color,
    val green: Color,
    val gold: Color,
    val pink: Color,
    val link: Color,
    val eliminated: Color,
    val isDark: Boolean
)

val DarkAlgoQuestColors = AlgoQuestColors(
    bg = AlgoBg,
    cardBg = AlgoCardBg,
    activeCardBg = AlgoActiveCardBg,
    selectedBg = AlgoSelectedBg,
    fieldBg = AlgoFieldBg,
    codeBg = AlgoCodeBg,
    border = AlgoBorder,
    textPrimary = AlgoTextPrimary,
    textSecondary = AlgoTextSecondary,
    textMuted = AlgoTextMuted,
    cyan = AlgoCyan,
    purple = AlgoPurple,
    purpleSoft = AlgoPurpleSoft,
    purpleLight = AlgoPurpleLight,
    purpleDark = AlgoPurpleDark,
    green = AlgoGreen,
    gold = AlgoGold,
    pink = AlgoPink,
    link = AlgoLink,
    eliminated = AlgoEliminated,
    isDark = true
)

val LightAlgoQuestColors = AlgoQuestColors(
    bg = AlgoLightBg,
    cardBg = AlgoLightCardBg,
    activeCardBg = AlgoLightActiveCardBg,
    selectedBg = AlgoLightSelectedBg,
    fieldBg = AlgoLightFieldBg,
    codeBg = AlgoLightCodeBg,
    border = AlgoLightBorder,
    textPrimary = AlgoLightTextPrimary,
    textSecondary = AlgoLightTextSecondary,
    textMuted = AlgoLightTextMuted,
    cyan = AlgoLightCyan,
    purple = AlgoLightPurple,
    purpleSoft = AlgoLightPurpleSoft,
    purpleLight = AlgoLightPurpleSoft,
    purpleDark = AlgoLightPurple,
    green = AlgoLightGreen,
    gold = AlgoLightGold,
    pink = AlgoLightPink,
    link = AlgoLightLink,
    eliminated = AlgoLightEliminated,
    isDark = false
)

val LocalAlgoQuestColors = staticCompositionLocalOf { DarkAlgoQuestColors }
val LocalSyntaxColors = staticCompositionLocalOf { NeonSynthDark }
val LocalEditorFontFamily = staticCompositionLocalOf<androidx.compose.ui.text.font.FontFamily> { androidx.compose.ui.text.font.FontFamily.Monospace }
val LocalEditorFontName = staticCompositionLocalOf { "JetBrains Mono" }

object AlgoTheme {
    val colors: AlgoQuestColors
        @Composable
        get() = LocalAlgoQuestColors.current

    val syntaxColors: SyntaxColors
        @Composable
        get() = LocalSyntaxColors.current

    val editorFontFamily: androidx.compose.ui.text.font.FontFamily
        @Composable
        get() = LocalEditorFontFamily.current

    val editorFontName: String
        @Composable
        get() = LocalEditorFontName.current
}

private val DarkColorScheme = darkColorScheme(
    primary = AlgoPurple,
    secondary = AlgoCyan,
    tertiary = AlgoGold,
    background = AlgoBg,
    surface = AlgoCardBg,
    onBackground = AlgoTextPrimary,
    onSurface = AlgoTextPrimary,
    surfaceContainer = AlgoCardBg
)

private val LightColorScheme = lightColorScheme(
    primary = AlgoLightPurple,
    secondary = AlgoLightCyan,
    tertiary = AlgoLightGold,
    background = AlgoLightBg,
    surface = AlgoLightCardBg,
    onBackground = AlgoLightTextPrimary,
    onSurface = AlgoLightTextPrimary,
    surfaceContainer = AlgoLightCardBg
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    syntaxScheme: String = "Neon Synth",
    editorFont: String = "JetBrains Mono",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customColors = if (darkTheme) DarkAlgoQuestColors else LightAlgoQuestColors
    val syntaxColors = getSyntaxColors(syntaxScheme, isDark = darkTheme)
    val editorFontFamily = getEditorFontFamily(editorFont)

    CompositionLocalProvider(
        LocalAlgoQuestColors provides customColors,
        LocalSyntaxColors provides syntaxColors,
        LocalEditorFontFamily provides editorFontFamily,
        LocalEditorFontName provides editorFont
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
