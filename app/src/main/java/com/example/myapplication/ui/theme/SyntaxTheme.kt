package com.example.myapplication.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Immutable
data class SyntaxColors(
    val keyword: Color,
    val identifier: Color,
    val type: Color,
    val number: Color,
    val string: Color,
    val comment: Color,
    val operator: Color,
    val activeLineBg: Color,
    val activeLineIndicator: Color,
    val lineNumber: Color,
    val activeLineNumber: Color
)

val NeonSynthDark = SyntaxColors(
    keyword = Color(0xFFB5A7FF),
    identifier = Color(0xFF00D2FF),
    type = Color(0xFF38BDF8),
    number = Color(0xFFFFB84D),
    string = Color(0xFF2ECC71),
    comment = Color(0xFF64748B),
    operator = Color(0xFFFF5C8A),
    activeLineBg = Color(0x406C5CE7),
    activeLineIndicator = Color(0xFF00D2FF),
    lineNumber = Color(0xFF64748B),
    activeLineNumber = Color(0xFF00D2FF)
)

val NeonSynthLight = SyntaxColors(
    keyword = Color(0xFF6D28D9),
    identifier = Color(0xFF0284C7),
    type = Color(0xFF0369A1),
    number = Color(0xFFD97706),
    string = Color(0xFF16A34A),
    comment = Color(0xFF94A3B8),
    operator = Color(0xFFDB2777),
    activeLineBg = Color(0x266D28D9),
    activeLineIndicator = Color(0xFF0284C7),
    lineNumber = Color(0xFF94A3B8),
    activeLineNumber = Color(0xFF0284C7)
)

val DraculaDuskDark = SyntaxColors(
    keyword = Color(0xFFFF79C6),
    identifier = Color(0xFF8BE9FD),
    type = Color(0xFF50FA7B),
    number = Color(0xFFBD93F9),
    string = Color(0xFFF1FA8C),
    comment = Color(0xFF6272A4),
    operator = Color(0xFFFFB86C),
    activeLineBg = Color(0x4444475A),
    activeLineIndicator = Color(0xFFFF79C6),
    lineNumber = Color(0xFF6272A4),
    activeLineNumber = Color(0xFFFF79C6)
)

val DraculaDuskLight = SyntaxColors(
    keyword = Color(0xFFBE185D),
    identifier = Color(0xFF0E7490),
    type = Color(0xFF15803D),
    number = Color(0xFF7C3AED),
    string = Color(0xFFB45309),
    comment = Color(0xFF64748B),
    operator = Color(0xFFC2410C),
    activeLineBg = Color(0x26BE185D),
    activeLineIndicator = Color(0xFFBE185D),
    lineNumber = Color(0xFF94A3B8),
    activeLineNumber = Color(0xFFBE185D)
)

val CyberSolarizedDark = SyntaxColors(
    keyword = Color(0xFFCB4B16),
    identifier = Color(0xFF268BD2),
    type = Color(0xFFB58900),
    number = Color(0xFFD33682),
    string = Color(0xFF2AA198),
    comment = Color(0xFF586E75),
    operator = Color(0xFF859900),
    activeLineBg = Color(0x44073642),
    activeLineIndicator = Color(0xFF2AA198),
    lineNumber = Color(0xFF586E75),
    activeLineNumber = Color(0xFF2AA198)
)

val CyberSolarizedLight = SyntaxColors(
    keyword = Color(0xFFC2410C),
    identifier = Color(0xFF1D4ED8),
    type = Color(0xFFA16207),
    number = Color(0xFF9D174D),
    string = Color(0xFF0F766E),
    comment = Color(0xFF64748B),
    operator = Color(0xFF4D7C0F),
    activeLineBg = Color(0x260F766E),
    activeLineIndicator = Color(0xFF0F766E),
    lineNumber = Color(0xFF94A3B8),
    activeLineNumber = Color(0xFF0F766E)
)

fun getSyntaxColors(schemeName: String, isDark: Boolean): SyntaxColors {
    return when (schemeName) {
        "Dracula Dusk" -> if (isDark) DraculaDuskDark else DraculaDuskLight
        "Cyber Solarized" -> if (isDark) CyberSolarizedDark else CyberSolarizedLight
        else -> if (isDark) NeonSynthDark else NeonSynthLight
    }
}

fun getEditorFontFamily(fontName: String): FontFamily {
    return when (fontName) {
        "Fira Code" -> try {
            FontFamily(android.graphics.Typeface.create("fira code", android.graphics.Typeface.NORMAL))
        } catch (_: Throwable) {
            FontFamily.Monospace
        }
        "Roboto Mono" -> try {
            FontFamily(android.graphics.Typeface.create("roboto mono", android.graphics.Typeface.NORMAL))
        } catch (_: Throwable) {
            FontFamily.Monospace
        }
        else -> try {
            FontFamily(android.graphics.Typeface.create("jetbrains mono", android.graphics.Typeface.NORMAL))
        } catch (_: Throwable) {
            FontFamily.Monospace
        }
    }
}

private val KEYWORDS = setOf(
    "def", "fun", "function", "fn", "var", "val", "let", "const", "class",
    "if", "else", "elif", "while", "for", "in", "return", "break", "continue",
    "import", "from", "as", "package", "public", "private", "protected",
    "static", "void", "int", "float", "double", "boolean", "bool", "char", "string", "String",
    "true", "false", "True", "False", "null", "None", "nil", "new", "this", "self"
)

fun highlightCodeLine(lineText: String, colors: SyntaxColors): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val len = lineText.length

        while (i < len) {
            val ch = lineText[i]

            // Comments (# or //)
            if (ch == '#' || (ch == '/' && i + 1 < len && lineText[i + 1] == '/')) {
                withStyle(SpanStyle(color = colors.comment, fontWeight = FontWeight.Normal)) {
                    append(lineText.substring(i))
                }
                break
            }

            // String literals
            if (ch == '"' || ch == '\'') {
                val quote = ch
                val start = i
                i++
                while (i < len && lineText[i] != quote) {
                    if (lineText[i] == '\\' && i + 1 < len) i++
                    i++
                }
                if (i < len) i++
                withStyle(SpanStyle(color = colors.string)) {
                    append(lineText.substring(start, i))
                }
                continue
            }

            // Number literals
            if (ch.isDigit()) {
                val start = i
                while (i < len && (lineText[i].isDigit() || lineText[i] == '.' || lineText[i] == 'x' || lineText[i] == 'X')) {
                    i++
                }
                withStyle(SpanStyle(color = colors.number, fontWeight = FontWeight.SemiBold)) {
                    append(lineText.substring(start, i))
                }
                continue
            }

            // Word tokens (keywords / types / identifiers)
            if (ch.isLetter() || ch == '_') {
                val start = i
                while (i < len && (lineText[i].isLetterOrDigit() || lineText[i] == '_')) {
                    i++
                }
                val word = lineText.substring(start, i)
                val isKeyword = word in KEYWORDS
                val isType = !isKeyword && word.first().isUpperCase()
                val style = when {
                    isKeyword -> SpanStyle(color = colors.keyword, fontWeight = FontWeight.Bold)
                    isType -> SpanStyle(color = colors.type, fontWeight = FontWeight.SemiBold)
                    else -> SpanStyle(color = colors.identifier)
                }
                withStyle(style) {
                    append(word)
                }
                continue
            }

            // Operators
            if (ch in "=+-*/%<>&|^!~?:") {
                withStyle(SpanStyle(color = colors.operator, fontWeight = FontWeight.Medium)) {
                    append(ch)
                }
                i++
                continue
            }

            // Whitespace / punctuation / default
            withStyle(SpanStyle(color = colors.identifier)) {
                append(ch)
            }
            i++
        }
    }
}

