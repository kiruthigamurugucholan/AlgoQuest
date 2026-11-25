package com.example.myapplication.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AlgoTheme
import com.example.myapplication.ui.theme.SyntaxColors
import com.example.myapplication.ui.theme.highlightCodeLine

/**
 * Utility for resolving and detecting programming language labels for code blocks.
 */
object LanguageDetector {
    private val LANGUAGE_MAP = mapOf(
        "py" to "PYTHON",
        "python" to "PYTHON",
        "python3" to "PYTHON",
        "kt" to "KOTLIN",
        "kotlin" to "KOTLIN",
        "java" to "JAVA",
        "c" to "C",
        "cpp" to "C++",
        "c++" to "C++",
        "csharp" to "C#",
        "c#" to "C#",
        "cs" to "C#",
        "js" to "JAVASCRIPT",
        "javascript" to "JAVASCRIPT",
        "ts" to "TYPESCRIPT",
        "typescript" to "TYPESCRIPT",
        "html" to "HTML",
        "css" to "CSS",
        "sql" to "SQL",
        "sh" to "BASH",
        "bash" to "BASH",
        "shell" to "BASH",
        "zsh" to "BASH",
        "go" to "GO",
        "golang" to "GO",
        "rs" to "RUST",
        "rust" to "RUST",
        "rb" to "RUBY",
        "ruby" to "RUBY",
        "swift" to "SWIFT",
        "dart" to "DART",
        "php" to "PHP",
        "json" to "JSON",
        "xml" to "XML",
        "yaml" to "YAML",
        "yml" to "YAML",
        "md" to "MARKDOWN",
        "markdown" to "MARKDOWN",
        "pseudo" to "PSEUDOCODE",
        "pseudocode" to "PSEUDOCODE"
    )

    fun resolve(language: String, code: String): String {
        val clean = language.trim().lowercase().split(" ", "\t", ":").firstOrNull().orEmpty()
        if (clean.isNotEmpty() && LANGUAGE_MAP.containsKey(clean)) {
            return LANGUAGE_MAP[clean]!!
        }
        if (clean.isNotEmpty()) {
            return clean.uppercase()
        }
        return detectFromContent(code)
    }

    fun detectFromContent(code: String): String {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) return "CODE"

        // Python heuristics
        if (Regex("\\b(def |elif |import numpy|import pandas|print\\(|self\\.|__init__)\\b").containsMatchIn(trimmed)) {
            return "PYTHON"
        }
        // Kotlin heuristics
        if (Regex("\\b(fun |val |var |println\\(|companion object|data class|sealed class)\\b").containsMatchIn(trimmed)) {
            return "KOTLIN"
        }
        // Java heuristics
        if (Regex("\\b(public class|public static void main|System\\.out\\.print|new ArrayList)\\b").containsMatchIn(trimmed)) {
            return "JAVA"
        }
        // C / C++ heuristics
        if (Regex("#include\\s*<|std::|cout\\s*<<|cin\\s*>>|nullptr|int main\\(\\)").containsMatchIn(trimmed)) {
            return "C++"
        }
        // C# heuristics
        if (Regex("\\b(namespace |Console\\.WriteLine|using System)\\b").containsMatchIn(trimmed)) {
            return "C#"
        }
        // JavaScript / TypeScript heuristics
        if (Regex("\\b(const |let |console\\.log|document\\.|export default)\\b").containsMatchIn(trimmed)) {
            return "JAVASCRIPT"
        }
        // SQL heuristics
        if (Regex("(?i)\\b(SELECT\\s+.+\\s+FROM|INSERT INTO|CREATE TABLE|ALTER TABLE)\\b").containsMatchIn(trimmed)) {
            return "SQL"
        }
        // HTML
        if (trimmed.startsWith("<!DOCTYPE") || (trimmed.startsWith("<") && trimmed.contains("</"))) {
            return "HTML"
        }
        // JSON
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return "JSON"
        }

        return "CODE"
    }
}

/**
 * Structured model representing top-level Markdown blocks.
 */
sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class BulletItem(val depth: Int, val text: String) : MarkdownBlock()
    data class NumberedItem(val number: String, val depth: Int, val text: String) : MarkdownBlock()
    data class Blockquote(val text: String) : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
    object Divider : MarkdownBlock()
}

/**
 * Parser that breaks raw Markdown text into a sequence of [MarkdownBlock] elements.
 */
object MarkdownParser {

    private val HEADING_REGEX = Regex("^(#{1,6})\\s+(.*)$")
    private val BULLET_REGEX = Regex("^(\\s*)[-*+]\\s+(.*)$")
    private val NUMBERED_REGEX = Regex("^(\\s*)(\\d+)[.)]\\s+(.*)$")
    private val DIVIDER_REGEX = Regex("^(?:---|___|\\*\\*\\*)\\s*$")
    private val BLOCKQUOTE_REGEX = Regex("^>\\s?(.*)$")
    private val TABLE_ROW_REGEX = Regex("^\\|.*\\|\\s*$")
    private val TABLE_SEPARATOR_REGEX = Regex("^\\|?\\s*[-:]+[-| :]*\\|?\\s*$")

    fun parse(markdown: String): List<MarkdownBlock> {
        if (markdown.isBlank()) return emptyList()

        val lines = markdown.lines()
        val blocks = mutableListOf<MarkdownBlock>()

        var i = 0
        val total = lines.size

        while (i < total) {
            val line = lines[i]
            val trimmed = line.trim()

            // 1. Code Block (```lang ... ```)
            if (trimmed.startsWith("```")) {
                val rawTag = trimmed.removePrefix("```").trim()
                val lang = rawTag.split(" ", "\t", ":").firstOrNull().orEmpty()
                val codeLines = mutableListOf<String>()
                i++
                while (i < total && !lines[i].trim().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                if (i < total && lines[i].trim().startsWith("```")) {
                    i++ // consume closing ```
                }
                blocks.add(MarkdownBlock.CodeBlock(lang, codeLines.joinToString("\n")))
                continue
            }

            // 2. Empty Line
            if (trimmed.isEmpty()) {
                i++
                continue
            }

            // 3. Horizontal Separator (---, ***, ___)
            if (DIVIDER_REGEX.matches(trimmed)) {
                blocks.add(MarkdownBlock.Divider)
                i++
                continue
            }

            // 4. Heading (# H1, ## H2, ### H3, #### H4)
            val headingMatch = HEADING_REGEX.matchEntire(trimmed)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val text = headingMatch.groupValues[2].trim()
                blocks.add(MarkdownBlock.Heading(level, text))
                i++
                continue
            }

            // 5. Blockquote (> ...)
            if (BLOCKQUOTE_REGEX.matches(trimmed)) {
                val quoteLines = mutableListOf<String>()
                while (i < total && BLOCKQUOTE_REGEX.matches(lines[i].trim())) {
                    val m = BLOCKQUOTE_REGEX.matchEntire(lines[i].trim())!!
                    quoteLines.add(m.groupValues[1])
                    i++
                }
                blocks.add(MarkdownBlock.Blockquote(quoteLines.joinToString("\n")))
                continue
            }

            // 6. Markdown Table (| Header 1 | Header 2 |)
            if (TABLE_ROW_REGEX.matches(trimmed) && i + 1 < total && TABLE_SEPARATOR_REGEX.matches(lines[i + 1].trim())) {
                val headerCells = parseTableRow(trimmed)
                i += 2 // skip header and separator line
                val rows = mutableListOf<List<String>>()
                while (i < total && TABLE_ROW_REGEX.matches(lines[i].trim())) {
                    rows.add(parseTableRow(lines[i].trim()))
                    i++
                }
                blocks.add(MarkdownBlock.Table(headerCells, rows))
                continue
            }

            // 7. Bullet List (- item, * item, + item)
            val bulletMatch = BULLET_REGEX.matchEntire(line)
            if (bulletMatch != null) {
                val indent = bulletMatch.groupValues[1].length / 2
                val text = bulletMatch.groupValues[2].trim()
                blocks.add(MarkdownBlock.BulletItem(indent, text))
                i++
                continue
            }

            // 8. Numbered List (1. item, 2. item)
            val numberedMatch = NUMBERED_REGEX.matchEntire(line)
            if (numberedMatch != null) {
                val indent = numberedMatch.groupValues[1].length / 2
                val num = numberedMatch.groupValues[2]
                val text = numberedMatch.groupValues[3].trim()
                blocks.add(MarkdownBlock.NumberedItem(num, indent, text))
                i++
                continue
            }

            // 9. Regular Paragraph
            val paraLines = mutableListOf<String>()
            while (i < total) {
                val curr = lines[i]
                val currTrimmed = curr.trim()
                if (currTrimmed.isEmpty() ||
                    currTrimmed.startsWith("```") ||
                    HEADING_REGEX.matches(currTrimmed) ||
                    DIVIDER_REGEX.matches(currTrimmed) ||
                    BLOCKQUOTE_REGEX.matches(currTrimmed) ||
                    BULLET_REGEX.matches(curr) ||
                    NUMBERED_REGEX.matches(curr) ||
                    (TABLE_ROW_REGEX.matches(currTrimmed) && i + 1 < total && TABLE_SEPARATOR_REGEX.matches(lines[i + 1].trim()))
                ) {
                    break
                }
                paraLines.add(currTrimmed)
                i++
            }
            if (paraLines.isNotEmpty()) {
                blocks.add(MarkdownBlock.Paragraph(paraLines.joinToString(" ")))
            }
        }

        return blocks
    }

    private fun parseTableRow(row: String): List<String> {
        val trimmed = row.removePrefix("|").removeSuffix("|")
        return trimmed.split("|").map { it.trim() }
    }
}

/**
 * Tokenizes and styles inline markdown elements (bold, italic, code, links).
 */
fun parseInlineMarkdown(
    text: String,
    primaryColor: Color,
    secondaryColor: Color,
    codeColor: Color,
    codeBgColor: Color,
    linkColor: Color,
    monoFont: FontFamily
): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val len = text.length

        while (cursor < len) {
            // 1. Inline Code (`code`)
            if (text[cursor] == '`') {
                val end = text.indexOf('`', cursor + 1)
                if (end != -1) {
                    val codeContent = text.substring(cursor + 1, end)
                    withStyle(
                        SpanStyle(
                            fontFamily = monoFont,
                            color = codeColor,
                            background = codeBgColor,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(" $codeContent ")
                    }
                    cursor = end + 1
                    continue
                }
            }

            // 2. Bold Italic (***text***)
            if (cursor + 2 < len && text.substring(cursor, cursor + 3) == "***") {
                val end = text.indexOf("***", cursor + 3)
                if (end != -1) {
                    val boldItalicContent = text.substring(cursor + 3, end)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = primaryColor)) {
                        append(boldItalicContent)
                    }
                    cursor = end + 3
                    continue
                }
            }

            // 3. Bold (**text** or __text__)
            if (cursor + 1 < len && (text.substring(cursor, cursor + 2) == "**" || text.substring(cursor, cursor + 2) == "__")) {
                val delim = text.substring(cursor, cursor + 2)
                val end = text.indexOf(delim, cursor + 2)
                if (end != -1) {
                    val boldContent = text.substring(cursor + 2, end)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = primaryColor)) {
                        append(boldContent)
                    }
                    cursor = end + 2
                    continue
                }
            }

            // 4. Italic (*text* or _text_)
            if (text[cursor] == '*' || text[cursor] == '_') {
                val delim = text[cursor]
                val end = text.indexOf(delim, cursor + 1)
                if (end != -1 && end > cursor + 1 && !text[cursor + 1].isWhitespace()) {
                    val italicContent = text.substring(cursor + 1, end)
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = secondaryColor)) {
                        append(italicContent)
                    }
                    cursor = end + 1
                    continue
                }
            }

            // 5. Link ([text](url))
            if (text[cursor] == '[') {
                val closeBracket = text.indexOf(']', cursor + 1)
                if (closeBracket != -1 && closeBracket + 1 < len && text[closeBracket + 1] == '(') {
                    val closeParen = text.indexOf(')', closeBracket + 2)
                    if (closeParen != -1) {
                        val linkText = text.substring(cursor + 1, closeBracket)
                        withStyle(SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)) {
                            append(linkText)
                        }
                        cursor = closeParen + 1
                        continue
                    }
                }
            }

            // 6. Simple Math / Complexity ($O(n)$ or $O(log n)$)
            if (text[cursor] == '$') {
                val end = text.indexOf('$', cursor + 1)
                if (end != -1 && end > cursor + 1) {
                    val mathContent = text.substring(cursor + 1, end)
                    withStyle(SpanStyle(fontFamily = monoFont, color = codeColor, fontWeight = FontWeight.SemiBold)) {
                        append(mathContent)
                    }
                    cursor = end + 1
                    continue
                }
            }

            // Default regular character
            append(text[cursor])
            cursor++
        }
    }
}

/**
 * Main Jetpack Compose Markdown renderer for AI Ally responses in AlgoQuest.
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    onCopyCode: (String) -> Unit = {}
) {
    val blocks = MarkdownParser.parse(markdown)

    val primaryColor = AlgoTheme.colors.textPrimary
    val secondaryColor = AlgoTheme.colors.textSecondary
    val codeColor = AlgoTheme.colors.cyan
    val codeBgColor = AlgoTheme.colors.codeBg
    val linkColor = AlgoTheme.colors.purpleSoft
    val borderColor = AlgoTheme.colors.border
    val monoFont = AlgoTheme.editorFontFamily

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    val (fontSize, fontWeight, color) = when (block.level) {
                        1 -> Triple(18.sp, FontWeight.Bold, primaryColor)
                        2 -> Triple(16.sp, FontWeight.Bold, primaryColor)
                        3 -> Triple(15.sp, FontWeight.SemiBold, AlgoTheme.colors.purpleSoft)
                        else -> Triple(14.sp, FontWeight.SemiBold, primaryColor)
                    }
                    Text(
                        text = parseInlineMarkdown(block.text, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = fontWeight,
                            fontSize = fontSize,
                            color = color
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = parseInlineMarkdown(block.text, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = primaryColor
                        )
                    )
                }

                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.depth * 14).dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AlgoTheme.colors.cyan)
                        )
                        Text(
                            text = parseInlineMarkdown(block.text, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = primaryColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownBlock.NumberedItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (block.depth * 14).dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${block.number}.",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = AlgoTheme.colors.purpleSoft
                            )
                        )
                        Text(
                            text = parseInlineMarkdown(block.text, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = primaryColor
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownBlock.Blockquote -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AlgoTheme.colors.cardBg.copy(alpha = 0.5f))
                            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(AlgoTheme.colors.purpleSoft)
                        )
                        Text(
                            text = parseInlineMarkdown(block.text, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp,
                                color = secondaryColor
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                is MarkdownBlock.Divider -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(1.dp)
                            .background(borderColor.copy(alpha = 0.4f))
                    )
                }

                is MarkdownBlock.CodeBlock -> {
                    MarkdownCodeBlockCard(
                        code = block.code,
                        language = block.language,
                        onCopy = onCopyCode
                    )
                }

                is MarkdownBlock.Table -> {
                    MarkdownTableCard(
                        headers = block.headers,
                        rows = block.rows,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        codeColor = codeColor,
                        codeBgColor = codeBgColor,
                        linkColor = linkColor,
                        monoFont = monoFont,
                        borderColor = borderColor
                    )
                }
            }
        }
    }
}

/**
 * Visually distinct, horizontally scrollable code block with accurate language labels, syntax highlighting, and copy button.
 */
@Composable
fun MarkdownCodeBlockCard(
    code: String,
    language: String = "",
    onCopy: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val langLabel = LanguageDetector.resolve(language, code)
    val codeBg = AlgoTheme.colors.codeBg
    val border = AlgoTheme.colors.border
    val syntaxColors = AlgoTheme.syntaxColors
    val monoFont = AlgoTheme.editorFontFamily

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(codeBg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Code Block Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AlgoTheme.colors.purple.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = langLabel,
                    style = TextStyle(
                        fontFamily = monoFont,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlgoTheme.colors.purpleSoft
                    )
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("AlgoQuest Code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        onCopy(code)
                    }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Copy Code",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AlgoTheme.colors.purpleSoft
                    )
                )
            }
        }

        // Horizontally Scrollable Monospaced Code Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column {
                code.lines().forEach { line ->
                    Text(
                        text = highlightCodeLine(line, syntaxColors),
                        style = TextStyle(
                            fontFamily = monoFont,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Horizontally scrollable Markdown Table with grid column widths ensuring layout alignment.
 */
@Composable
private fun MarkdownTableCard(
    headers: List<String>,
    rows: List<List<String>>,
    primaryColor: Color,
    secondaryColor: Color,
    codeColor: Color,
    codeBgColor: Color,
    linkColor: Color,
    monoFont: FontFamily,
    borderColor: Color
) {
    if (headers.isEmpty() && rows.isEmpty()) return

    // Calculate dynamic column widths so each column aligns vertically across all rows
    val colWidths = remember(headers, rows) {
        val colCount = maxOf(headers.size, rows.maxOfOrNull { it.size } ?: 0)
        (0 until colCount).map { colIdx ->
            val hLen = headers.getOrNull(colIdx)?.length ?: 0
            val maxCellLen = rows.maxOfOrNull { it.getOrNull(colIdx)?.length ?: 0 } ?: 0
            val longest = maxOf(hLen, maxCellLen)
            (longest * 8 + 36).coerceIn(85, 240).dp
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .horizontalScroll(rememberScrollState())
    ) {
        Column {
            // Header Row
            if (headers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .background(AlgoTheme.colors.cardBg)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    headers.forEachIndexed { colIdx, headerText ->
                        val colWidth = colWidths.getOrElse(colIdx) { 100.dp }
                        if (colIdx > 0) {
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(18.dp)
                                    .background(borderColor.copy(alpha = 0.5f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(colWidth)
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = parseInlineMarkdown(headerText, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = primaryColor
                                )
                            )
                        }
                    }
                }

                // Divider between header and body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(borderColor)
                )
            }

            // Data Rows
            rows.forEachIndexed { rowIdx, rowCells ->
                val rowBg = if (rowIdx % 2 == 0) Color.Transparent else AlgoTheme.colors.cardBg.copy(alpha = 0.35f)
                Row(
                    modifier = Modifier
                        .background(rowBg)
                        .padding(vertical = 7.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colCount = maxOf(headers.size, rowCells.size)
                    (0 until colCount).forEach { colIdx ->
                        val cellText = if (colIdx < rowCells.size) rowCells[colIdx] else ""
                        val colWidth = colWidths.getOrElse(colIdx) { 100.dp }
                        if (colIdx > 0) {
                            Spacer(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(16.dp)
                                    .background(borderColor.copy(alpha = 0.25f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(colWidth)
                                .padding(horizontal = 10.dp)
                        ) {
                            Text(
                                text = parseInlineMarkdown(cellText, primaryColor, secondaryColor, codeColor, codeBgColor, linkColor, monoFont),
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = secondaryColor
                                )
                            )
                        }
                    }
                }

                if (rowIdx < rows.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(borderColor.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}
