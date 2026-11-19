package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object StringVisualizerGenerator {

    private fun parseStringInput(input: String?, defaultStr: String): String {
        if (input.isNullOrBlank()) return defaultStr
        val clean = input.trim()
        return if (clean.length in 3..12) clean else defaultStr
    }

    // 1. String Traversal
    fun generateStringTraversal(customInput: String?): List<DsaVisualizerStep> {
        val str = parseStringInput(customInput, "ALGORITHM")
        val chars = str.toList()
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = chars.size + 2

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "String Traversal: Length = ${chars.size}",
                description = "Traverse string character by character from index 0 to ${chars.size - 1}.",
                actionType = "INITIALIZE",
                variables = mapOf("length" to "${chars.size}", "string" to str),
                vizType = DsaVizType.STRING,
                stringCharacters = chars,
                pointerLabels = mapOf(0 to "i"),
                highlightedIndices = setOf(0)
            )
        )

        chars.forEachIndexed { idx, ch ->
            steps.add(
                DsaVisualizerStep(
                    stepIndex = idx + 1,
                    totalSteps = total,
                    title = "Visit Char at Index [$idx]: '$ch'",
                    description = "Character '$ch' has ASCII value ${ch.code}. Read in O(1) time.",
                    actionType = "VISIT",
                    variables = mapOf("index" to "$idx", "char" to "'$ch'", "ascii" to "${ch.code}"),
                    vizType = DsaVizType.STRING,
                    stringCharacters = chars,
                    pointerLabels = mapOf(idx to "i"),
                    highlightedIndices = setOf(idx),
                    sortedIndices = (0..idx).toSet()
                )
            )
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = total - 1,
                totalSteps = total,
                title = "String Traversal Complete",
                description = "Visited all ${chars.size} characters sequentially in O(N) linear time.",
                actionType = "COMPLETE",
                variables = mapOf("totalChars" to "${chars.size}", "status" to "SUCCESS"),
                vizType = DsaVizType.STRING,
                stringCharacters = chars,
                sortedIndices = chars.indices.toSet(),
                isCompleted = true,
                summaryResult = "Successfully traversed all ${chars.size} characters in string '$str'."
            )
        )

        return steps
    }

    // 2. String Reversal
    fun generateStringReversal(customInput: String?): List<DsaVisualizerStep> {
        val str = parseStringInput(customInput, "QUEST")
        val chars = str.toMutableList()
        val steps = mutableListOf<DsaVisualizerStep>()
        var left = 0
        var right = chars.size - 1

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Two-Pointer In-Place String Reversal",
                description = "Place Left pointer at index 0 ('${chars[left]}') and Right pointer at index $right ('${chars[right]}').",
                actionType = "INITIALIZE",
                variables = mapOf("L" to "$left", "R" to "$right"),
                vizType = DsaVizType.STRING,
                stringCharacters = chars.toList(),
                pointerLabels = mapOf(left to "L", right to "R"),
                highlightedIndices = setOf(left, right)
            )
        )

        while (left < right && steps.size < 8) {
            val oldL = chars[left]
            val oldR = chars[right]
            chars[left] = oldR
            chars[right] = oldL

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 6,
                    title = "Swap chars[$left] ('$oldL') & chars[$right] ('$oldR')",
                    description = "Swapped opposite ends. New string: ${chars.joinToString("")}. Advance pointers inward.",
                    actionType = "SWAP",
                    variables = mapOf("L" to "$left ('$oldR')", "R" to "$right ('$oldL')"),
                    vizType = DsaVizType.STRING,
                    stringCharacters = chars.toList(),
                    pointerLabels = mapOf(left to "L", right to "R"),
                    highlightedIndices = setOf(left, right),
                    swappedIndices = Pair(left, right)
                )
            )

            left++
            right--
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = "String Reversal Complete",
                description = "Reversed string in-place: '${chars.joinToString("")}' in O(N/2) time and O(1) space.",
                actionType = "COMPLETE",
                variables = mapOf("original" to str, "reversed" to chars.joinToString("")),
                vizType = DsaVizType.STRING,
                stringCharacters = chars.toList(),
                sortedIndices = chars.indices.toSet(),
                isCompleted = true,
                summaryResult = "Reversed '$str' -> '${chars.joinToString("")}' in O(N) time."
            )
        )

        return steps
    }

    // 3. Palindrome Checking
    fun generatePalindromeChecking(customInput: String?): List<DsaVisualizerStep> {
        val str = parseStringInput(customInput, "RACECAR")
        val chars = str.toList()
        val steps = mutableListOf<DsaVisualizerStep>()
        var left = 0
        var right = chars.size - 1
        var isPalindrome = true

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 6,
                title = "Palindrome Check: Symmetric Scan",
                description = "Compare characters at Left and Right boundaries moving inward to verify reflection.",
                actionType = "INITIALIZE",
                variables = mapOf("string" to str, "left" to "$left", "right" to "$right"),
                vizType = DsaVizType.STRING,
                stringCharacters = chars,
                pointerLabels = mapOf(left to "L", right to "R"),
                highlightedIndices = setOf(left, right)
            )
        )

        while (left < right && steps.size < 8) {
            val match = chars[left].equals(chars[right], ignoreCase = true)
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 6,
                    title = "Compare chars[$left] ('${chars[left]}') == chars[$right] ('${chars[right]}')",
                    description = if (match) "'${chars[left]}' matches '${chars[right]}'. Symmetric at this layer." else "'${chars[left]}' != '${chars[right]}'. String is NOT a palindrome!",
                    actionType = if (match) "COMPARE" else "MISMATCH",
                    variables = mapOf("L_char" to "'${chars[left]}'", "R_char" to "'${chars[right]}'", "matches" to "$match"),
                    vizType = DsaVizType.STRING,
                    stringCharacters = chars,
                    pointerLabels = mapOf(left to "L", right to "R"),
                    highlightedIndices = setOf(left, right),
                    isFound = match
                )
            )

            if (!match) {
                isPalindrome = false
                break
            }
            left++
            right--
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (isPalindrome) "Valid Palindrome Verified!" else "Not a Palindrome",
                description = if (isPalindrome) "'$str' reads identical forwards and backwards." else "Mismatched characters found.",
                actionType = "COMPLETE",
                variables = mapOf("isPalindrome" to "$isPalindrome"),
                vizType = DsaVizType.STRING,
                stringCharacters = chars,
                sortedIndices = if (isPalindrome) chars.indices.toSet() else emptySet(),
                isCompleted = true,
                summaryResult = if (isPalindrome) "'$str' is a valid palindrome!" else "'$str' is NOT a palindrome."
            )
        )

        return steps
    }

    // 4. Anagram Checking
    fun generateAnagramChecking(customInput: String?): List<DsaVisualizerStep> {
        val s1 = "LISTEN"
        val s2 = "SILENT"
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Anagram Check: String 1 = '$s1' vs String 2 = '$s2'",
                description = "Two strings are anagrams if both contain the exact same characters with identical frequencies.",
                actionType = "INITIALIZE",
                variables = mapOf("string1" to s1, "string2" to s2),
                vizType = DsaVizType.STRING,
                stringCharacters = s1.toList(),
                pointerLabels = mapOf(0 to "s1"),
                highlightedIndices = setOf(0)
            )
        )

        val freq1 = s1.groupingBy { it }.eachCount()
        val freq2 = s2.groupingBy { it }.eachCount()
        val isAnagram = freq1 == freq2

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "Count Character Frequencies",
                description = "Frequencies for '$s1': $freq1\nFrequencies for '$s2': $freq2",
                actionType = "COUNT",
                variables = freq1.mapKeys { "freq[${it.key}]" }.mapValues { "${it.value}" },
                vizType = DsaVizType.STRING,
                stringCharacters = s2.toList(),
                sortedIndices = s2.indices.toSet()
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = if (isAnagram) "Frequency Tables Match Exactly" else "Frequency Mismatch",
                description = if (isAnagram) "All character counts match perfectly in O(N) time!" else "Character counts differ.",
                actionType = "COMPLETE",
                variables = mapOf("isAnagram" to "$isAnagram", "status" to "VERIFIED"),
                vizType = DsaVizType.STRING,
                stringCharacters = s1.toList(),
                sortedIndices = s1.indices.toSet(),
                isCompleted = true,
                summaryResult = "'$s1' and '$s2' are valid anagrams."
            )
        )

        return steps
    }

    // 5. Pattern Matching
    fun generatePatternMatching(customInput: String?): List<DsaVisualizerStep> {
        val text = "AABAACAADAABAABA"
        val pattern = "AABA"
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 5,
                title = "Pattern Matching: Search for '$pattern' in '$text'",
                description = "Slide pattern window of length ${pattern.length} across text of length ${text.length}.",
                actionType = "INITIALIZE",
                variables = mapOf("pattern" to pattern, "patternLen" to "${pattern.length}", "textLen" to "${text.length}"),
                vizType = DsaVizType.STRING,
                stringCharacters = text.take(10).toList(),
                pointerLabels = mapOf(0 to "P[0]"),
                highlightedIndices = (0 until pattern.length).toSet()
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 5,
                title = "Window [0..3]: Match Found at Index 0!",
                description = "Substring text[0..3] ('${text.substring(0, 4)}') == pattern ('$pattern'). Match at index 0.",
                actionType = "FOUND",
                variables = mapOf("matchAt" to "0", "substring" to text.substring(0, 4)),
                vizType = DsaVizType.STRING,
                stringCharacters = text.take(10).toList(),
                pointerLabels = mapOf(0 to "MATCH"),
                highlightedIndices = (0 until pattern.length).toSet(),
                sortedIndices = (0 until pattern.length).toSet(),
                isFound = true
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 5,
                title = "Window [9..12]: Second Match Found at Index 9!",
                description = "Substring text[9..12] ('${text.substring(9, 13)}') == pattern ('$pattern'). Match at index 9.",
                actionType = "FOUND",
                variables = mapOf("matchAt" to "9", "substring" to text.substring(9, 13)),
                vizType = DsaVizType.STRING,
                stringCharacters = text.take(10).toList(),
                sortedIndices = (0 until pattern.length).toSet(),
                isCompleted = true,
                summaryResult = "Pattern '$pattern' matched at indices [0, 9] in O(N * M) time."
            )
        )

        return steps
    }
}
