package com.example.myapplication.util

import com.example.myapplication.data.model.response.LearningResourceDto

/**
 * Presentation-layer text cleaner and mathematical expression formatter
 * for scraped educational DSA resources.
 *
 * Cleans punctuation spacing, invisible Unicode math control characters,
 * flattened Wikipedia fractions (e.g. Binary Search midpoint formulas),
 * array subscript expressions, and Big-O complexity notation without
 * altering original educational meaning.
 */
object LearningResourceTextFormatter {

    private val invisibleCharsRegex = Regex("[\u2061\u200B\u200C\u200D\uFEFF]")
    private val whitespaceRegex = Regex("[\\u00A0\\t\\r\\n]+|\\s{2,}")

    // Flattened Wikipedia MathML midpoint fractions (Binary Search & similar)
    private val plusFloorRMinusLOver2 = Regex("""\bL\s+plus\s+the\s+floor\s+of\s+R\s*[−-]\s*L\s+2\b""")
    private val plusCeilRMinusLOver2 = Regex("""\bL\s+plus\s+the\s+ceiling\s+of\s+R\s*[−-]\s*L\s+2\b""")
    private val floorRMinusLOver2 = Regex("""\bfloor\s+of\s+R\s*[−-]\s*L\s+2\b""")
    private val ceilRMinusLOver2 = Regex("""\bceiling\s+of\s+R\s*[−-]\s*L\s+2\b""")
    private val floorLPlusROver2 = Regex("""\bfloor\s+of\s+L\s*\+\s*R\s+2\b""")
    private val ceilLPlusROver2 = Regex("""\bceiling\s+of\s+L\s*\+\s*R\s+2\b""")
    private val bareRMinusLOver2 = Regex("""\bR\s*[−-]\s*L\s+2(?=\s*[,.;:!?)\]]|\s*$)""")
    private val bareLPlusROver2 = Regex("""\bL\s*\+\s*R\s+2(?=\s*[,.;:!?)\]]|\s*$)""")

    // Flattened array subscript notation: "A m < T" -> "A[m] < T", "A 0 , A 1" -> "A[0], A[1]", "A n − 1" -> "A[n − 1]"
    private val arraySubscriptNMinus1 = Regex("""\bA\s+n\s*[−-]\s*1\b""")
    private val arraySubscriptVarComp = Regex("""\bA\s+([mijk])(?=\s*[<>≤≥≠=])""")
    private val arraySubscriptDigit = Regex("""\bA\s+(\d+)(?=\s*[,.;<>≤≥≠=])""")

    // Big-O / asymptotic notation spacing: "O ( log n )" -> "O(log n)", "O ( n 2 )" -> "O(n²)"
    private val bigORegex = Regex("""\b([OΩΘ])\s*\(\s*([^()]+?)\s*\)""")
    private val powerTwoInsideBigO = Regex("""\b([nNmMkKvVeE])\s+2\b""")
    private val powerThreeInsideBigO = Regex("""\b([nNmMkKvVeE])\s+3\b""")

    // Punctuation spacing rules
    private val spaceBeforePunctRegex = Regex("""\s+([,.;:!?)\]}])""")
    private val spaceAfterOpenBracketRegex = Regex("""([(\[{])\s+""")
    private val missingSpaceAfterCommaSemiRegex = Regex("""([,;])([A-Za-z])""")

    // Inline math token pattern used by Compose UI to visually style formulas in monospace
    val inlineMathHighlightRegex = Regex(
        """(?:L\s*\+\s*(?:floor|ceil)\(\([^)]+\)\s*/\s*2\)|(?:floor|ceil)\(\([^)]+\)\s*/\s*2\)|\([RL]\s*[−+-]\s*[RL]\)\s*/\s*2|[OΩΘ]\([^()]+\)|A\[[^\]]+\](?:\s*[<>≤≥≠=]\s*[A-Za-z0-9\[\]−+\s]+)?(?=[,.;\s]|$)|\b[LRm]\s*[<>≤≥≠=]\s*[LRm0-9](?:\s*[+−-]\s*1)?)"""
    )

    fun cleanText(raw: String): String {
        if (raw.isBlank()) return ""

        var text = raw
            .replace(invisibleCharsRegex, "")
            .replace(whitespaceRegex, " ")
            .trim()

        // 1. Format mathematical midpoint / fraction expressions cleanly
        text = text.replace(plusFloorRMinusLOver2, "L + floor((R − L) / 2)")
        text = text.replace(plusCeilRMinusLOver2, "L + ceil((R − L) / 2)")
        text = text.replace(floorRMinusLOver2, "floor((R − L) / 2)")
        text = text.replace(ceilRMinusLOver2, "ceil((R − L) / 2)")
        text = text.replace(floorLPlusROver2, "floor((L + R) / 2)")
        text = text.replace(ceilLPlusROver2, "ceil((L + R) / 2)")
        text = text.replace(bareRMinusLOver2, "(R − L) / 2")
        text = text.replace(bareLPlusROver2, "(L + R) / 2")

        // 2. Format flattened array subscript notation
        text = text.replace(arraySubscriptNMinus1, "A[n − 1]")
        text = text.replace(arraySubscriptVarComp, "A[$1]")
        text = text.replace(arraySubscriptDigit, "A[$1]")

        // 3. Tighten Big-O / asymptotic notation
        text = text.replace(bigORegex) { match ->
            val symbol = match.groupValues[1]
            var inner = match.groupValues[2].replace(Regex("\\s+"), " ").trim()
            inner = inner.replace(powerTwoInsideBigO, "$1²")
            inner = inner.replace(powerThreeInsideBigO, "$1³")
            "$symbol($inner)"
        }

        // 4. Clean punctuation & bracket spacing
        text = text.replace(spaceBeforePunctRegex, "$1")
        text = text.replace(spaceAfterOpenBracketRegex, "$1")
        text = text.replace(missingSpaceAfterCommaSemiRegex, "$1 $2")
        text = text.replace(Regex("\\s{2,}"), " ").trim()

        return text
    }

    fun formatResource(resource: LearningResourceDto): LearningResourceDto {
        return resource.copy(
            title = cleanText(resource.title),
            description = cleanText(resource.description),
            headings = resource.headings.map { cleanText(it) }.filter { it.isNotBlank() },
            content = resource.content.map { cleanText(it) }.filter { it.isNotBlank() },
            importantPoints = resource.importantPoints.map { cleanText(it) }.filter { it.isNotBlank() }
        )
    }
}
