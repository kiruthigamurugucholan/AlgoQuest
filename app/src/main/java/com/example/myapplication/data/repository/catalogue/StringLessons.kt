package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object StringLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. String Traversal & Manipulation
        DsaLessonContent(
            lessonId = "string_traversal_l1",
            subtopicId = "string_traversal",
            lessonNumber = 1,
            title = "Core Theory & Invariants – String Traversal & Manipulation",
            subtitle = "String immutability, StringBuilder efficiency, and character frequency mapping",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Understand String immutability in Java/Kotlin and why repeated '+=' concatenation creates O(N^2) garbage.",
                "Master StringBuilder for O(1) amortized character appends.",
                "Implement constant-space frequency arrays using character arithmetic (char - 'a').",
                "Navigate Unicode code points and UTF-16 surrogate pairs."
            ),
            sections = listOf(
                LessonSection(
                    id = "string_immutability",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The String Immutability Trap",
                    body = "In Kotlin and Java, String objects are strictly IMMUTABLE. Once created in memory, their contents can never be altered.\n\nWhen you execute `str += char` inside a loop of size N, the JVM allocates a brand new String and copies all existing characters into it on every single iteration! Total characters copied = 1 + 2 + 3 + ... + N = O(N^2) time and memory churn.\n\nStringBuilder maintains a dynamic character buffer, allowing appends in O(1) amortized time and O(N) total runtime.",
                    bulletPoints = listOf(
                        "Naive Concatenation: O(N^2) time with heavy Garbage Collector stress.",
                        "StringBuilder: O(N) linear time."
                    )
                ),
                LessonSection(
                    id = "char_arithmetic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Character Index Arithmetic",
                    body = "Characters in memory are stored as numerical ASCII/Unicode values ('a' = 97, 'b' = 98...).\n\nInstead of allocating an expensive HashMap<Char, Int> to count letter frequencies, we can use a fixed-size primitive IntArray of size 26:\nval index = char - 'a'\n\nThis gives instant O(1) lookups with zero heap object allocations and strictly O(1) auxiliary space!",
                    bulletPoints = listOf(
                        "Space: IntArray(26) consumes only 104 bytes in RAM.",
                        "Direct Access: char - 'a' maps directly to array indices 0..25."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "StringBuilder vs. Frequency Array Mapping",
                    language = "KOTLIN",
                    code = """// 1. Efficient traversal and building with StringBuilder: O(N)
fun buildProcessedString(input: String): String {
    val sb = StringBuilder()
    for (ch in input) {
        if (ch.isLetterOrDigit()) {
            sb.append(ch.lowercaseChar())
        }
    }
    return sb.toString()
}

// 2. Character frequency array: O(N) time, O(1) auxiliary space
fun countLetterFrequencies(s: String): IntArray {
    val freq = IntArray(26) // Strictly 26 slots
    for (ch in s) {
        if (ch in 'a'..'z') {
            freq[ch - 'a']++
        }
    }
    return freq
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-9", "StringBuilder accumulates characters in a dynamic buffer without reallocating full strings."),
                        CodeStepExplanation(2, "Lines 13-20", "Character arithmetic ch - 'a' maps lowercase English letters to bounds 0..25.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Strings are immutable; repeated concatenation inside loops creates O(N^2) time and memory overhead.",
                "Always use StringBuilder for loop-based string modifications.",
                "A fixed 26-element integer array replaces HashMaps for lowercase ASCII frequency counting in O(1) space."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_str_trav_1",
                    question = "Why does concatenating a character to a String N times inside a loop (str += ch) result in O(N^2) time complexity?",
                    options = listOf(
                        "Because Strings are immutable, so every concatenation copies all existing characters into a newly allocated String",
                        "Because strings must be converted to binary on every append",
                        "Because character arithmetic takes logarithmic time",
                        "Because the JVM synchronizes all string threads"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Due to immutability, each '+=' creates a new String of size k and copies all prior characters, totaling 1 + 2 + ... + N = N(N+1)/2 = O(N^2) operations."
                ),
                LessonPracticeQuestion(
                    id = "pq_str_trav_2",
                    question = "What is the memory complexity of a character frequency counter using 'IntArray(26)' for lowercase English letters?",
                    options = listOf("O(1) Constant Space", "O(N) Linear Space", "O(26^N)", "O(log N)"),
                    correctOptionIndex = 0,
                    explanation = "Because the array length is fixed at 26 regardless of the input string's length N, the space complexity is O(1) constant."
                )
            )
        ),

        // 2. String Reversal Techniques
        DsaLessonContent(
            lessonId = "string_reversal_l1",
            subtopicId = "string_reversal",
            lessonNumber = 1,
            title = "Core Theory & Invariants – String Reversal Techniques",
            subtitle = "Two-pointer in-place character swapping and the word-reversal algorithm",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Reverse a character array in-place using two converging pointers.",
                "Understand why converting String to CharArray is required for mutable in-place swaps.",
                "Master the Sentence Word Reversal problem (reverse each word, then reverse the whole string).",
                "Analyze time complexity: N / 2 swaps = O(N)."
            ),
            sections = listOf(
                LessonSection(
                    id = "string_rev_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "In-Place Character Reversal",
                    body = "Because Strings cannot be mutated directly, in-place reversal is achieved by converting the String into a mutable character array (CharArray):\n1. Initialize left pointer at 0 and right pointer at length - 1.\n2. Swap chars[left] and chars[right].\n3. Increment left and decrement right.\n4. Terminate when left >= right.\n\nThis performs exactly N / 2 swap operations in O(N) time.",
                    bulletPoints = listOf(
                        "Swaps: N / 2 operations.",
                        "Time: O(N) linear time."
                    )
                ),
                LessonSection(
                    id = "word_reversal_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Reverse Words in a Sentence Algorithm",
                    body = "Suppose you must reverse the order of words in 'the sky is blue' to 'blue is sky the' in O(N) time:\n\nStep 1: Reverse each individual word in-place:\n'eht yks si eulb'\n\nStep 2: Reverse the entire string from beginning to end:\n'blue is sky the'!\n\nThis two-pass reversal solves the problem cleanly without allocating separate word lists.",
                    bulletPoints = listOf(
                        "Pass 1: O(N) to reverse individual words.",
                        "Pass 2: O(N) to reverse entire sentence.",
                        "Total Time: O(N) with O(1) auxiliary space (excluding char buffer)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Two-Pointer In-Place Reversal in Kotlin",
                    language = "KOTLIN",
                    code = """fun reverseString(s: CharArray) {
    var left = 0
    var right = s.size - 1
    while (left < right) {
        val temp = s[left]
        s[left] = s[right]
        s[right] = temp
        left++
        right--
    }
}

fun reverseWords(sentence: String): String {
    val chars = sentence.toCharArray()
    var start = 0
    for (end in 0..chars.size) {
        if (end == chars.size || chars[end] == ' ') {
            reverseSegment(chars, start, end - 1)
            start = end + 1
        }
    }
    reverseSegment(chars, 0, chars.size - 1)
    return String(chars)
}

private fun reverseSegment(arr: CharArray, l: Int, r: Int) {
    var i = l; var j = r
    while (i < j) {
        val t = arr[i]; arr[i] = arr[j]; arr[j] = t
        i++; j--
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 1-11", "Standard two-pointer converging swap pattern running in N/2 iterations."),
                        CodeStepExplanation(2, "Lines 14-23", "Two-step word reversal: reverses each individual word, then reverses whole buffer.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Strings must be converted to CharArray for in-place pointer swapping.",
                "Two-pointer reversal performs N/2 swaps in O(N) time and O(1) auxiliary space.",
                "Word reversal in sentences is accomplished elegantly via word reversal followed by full sentence reversal."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_str_rev_1",
                    question = "How many character swap operations does the two-pointer string reversal algorithm perform on a string of length N?",
                    options = listOf("N / 2 swaps", "N swaps", "N^2 swaps", "log2(N) swaps"),
                    correctOptionIndex = 0,
                    explanation = "Because each swap exchanges two characters simultaneously (one from the left and one from the right), exactly N / 2 swaps are performed."
                ),
                LessonPracticeQuestion(
                    id = "pq_str_rev_2",
                    question = "In the 'reverse words in sentence' algorithm, what is the effect of reversing each word individually and then reversing the entire string?",
                    options = listOf(
                        "The words appear in reversed sequence while each word's internal letters return to their original order",
                        "The string is converted to uppercase",
                        "All vowels are removed",
                        "The sentence remains identical to the original"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Reversing each word reverses its letters. Then reversing the full string reverses word positions and simultaneously reverses their letters back to the correct reading order."
                )
            )
        ),

        // 3. Palindrome Checking
        DsaLessonContent(
            lessonId = "string_palindrome_l1",
            subtopicId = "string_palindrome",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Palindrome Checking",
            subtitle = "Symmetric boundary verification, two-pointer filtering, and alphanumeric normalization",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the formal Palindrome invariant: s[i] == s[N - 1 - i] for all i.",
                "Implement two-pointer validation with early termination on mismatch.",
                "Handle practical real-world edge cases: case-insensitivity and skipping punctuation.",
                "Achieve O(1) auxiliary space without allocating filtered intermediate strings."
            ),
            sections = listOf(
                LessonSection(
                    id = "palindrome_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Palindrome?",
                    body = "A palindrome is a sequence that reads identically forward and backward (e.g., 'radar', 'level', 'racecar').\n\nIn technical interviews, palindrome problems typically require validating phrases with spaces and punctuation: 'A man, a plan, a canal: Panama!' is considered valid when ignoring case and non-alphanumeric characters.",
                    bulletPoints = listOf(
                        "Symmetry Invariant: Character at left pointer must match character at right pointer.",
                        "Boundary: Loop terminates as soon as left >= right."
                    )
                ),
                LessonSection(
                    id = "palindrome_in_place",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Zero-Allocation Two-Pointer Filtering",
                    body = "A naive solution creates a new string with punctuation removed, then reverses and compares: consumes O(N) extra memory.\n\nThe optimal two-pointer solution operates directly on the original string in O(1) space:\n- If left character is not alphanumeric, advance left++.\n- If right character is not alphanumeric, decrement right--.\n- When both point to alphanumeric characters, compare case-insensitively.\n- If they mismatch, return false immediately!",
                    bulletPoints = listOf(
                        "Early Exit: Returns false on the very first character mismatch.",
                        "Auxiliary Space: Strictly O(1)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "In-Place Valid Palindrome in Kotlin",
                    language = "KOTLIN",
                    code = """fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.length - 1
    
    while (left < right) {
        // Skip non-alphanumeric characters from left
        while (left < right && !s[left].isLetterOrDigit()) {
            left++
        }
        // Skip non-alphanumeric characters from right
        while (left < right && !s[right].isLetterOrDigit()) {
            right--
        }
        
        // Compare characters case-insensitively
        if (s[left].lowercaseChar() != s[right].lowercaseChar()) {
            return false // Mismatch found
        }
        left++
        right--
    }
    return true // All pairs matched!
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 6-12", "Inner while loops skip whitespace and punctuation from both ends in-place."),
                        CodeStepExplanation(2, "Lines 15-17", "Convert both valid characters to lowercase and check equality; early exit on mismatch."),
                        CodeStepExplanation(3, "Line 21", "Return true once left and right pointers cross without discrepancy.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "A palindrome possesses identical characters mirrored across its central axis.",
                "Two converging pointers validate palindromes in O(N) time.",
                "Skipping non-alphanumeric characters in-place achieves O(1) auxiliary space.",
                "Early exit terminates execution on the first mismatched character pair."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_str_pal_1",
                    question = "What is the auxiliary space complexity of the in-place two-pointer palindrome validation algorithm?",
                    options = listOf("O(1) Constant Space", "O(N) Linear Space", "O(log N)", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "The algorithm reads directly from the original string and uses only two integer index pointers, creating zero extra memory allocations."
                ),
                LessonPracticeQuestion(
                    id = "pq_str_pal_2",
                    question = "Why is filtering characters using two pointers faster than creating a cleaned string via regex and reversing it?",
                    options = listOf(
                        "It avoids allocating extra heap strings and can terminate early on the first mismatch",
                        "Because regex cannot process lowercase characters",
                        "Because strings cannot be reversed in Kotlin",
                        "Because two pointers run on multiple threads"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Regex and string creation allocate new heap objects and copy all characters even if the first two characters mismatch. Two pointers avoid allocation and terminate early on the first discrepancy."
                )
            )
        ),

        // 4. Anagram Detection
        DsaLessonContent(
            lessonId = "string_anagram_l1",
            subtopicId = "string_anagram",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Anagram Detection",
            subtitle = "Character frequency balance, sorting comparisons, and prime hashing invariants",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) for fixed alphabet",
            learningObjectives = listOf(
                "Define Anagram invariant: identical multiset of characters with identical frequencies.",
                "Compare Sorting approach O(N log N) vs Frequency Counter approach O(N).",
                "Implement single frequency array optimization (increment for s1, decrement for s2).",
                "Handle early termination checks (length mismatch)."
            ),
            sections = listOf(
                LessonSection(
                    id = "anagram_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is an Anagram?",
                    body = "An anagram is a word or phrase formed by rearranging the letters of a different word or phrase, using all original letters exactly once (e.g., 'listen' and 'silent', 'anagram' and 'nagaram').\n\nTwo strings are valid anagrams if and only if their character frequency distributions are identical.",
                    bulletPoints = listOf(
                        "Length Invariant: Strings of different lengths can NEVER be anagrams (instant O(1) early exit).",
                        "Multiset Invariant: Each distinct character must appear the exact same number of times."
                    )
                ),
                LessonSection(
                    id = "anagram_approaches",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Sorting vs. Frequency Counter",
                    body = "There are two primary algorithms to verify anagrams:\n\n1. Sorting: Sort both strings alphabetically and compare them: s1.sort() == s2.sort(). Time = O(N log N), Space = O(N).\n\n2. Frequency Counter (Optimal): Allocate an IntArray(26). For each character in s1, increment its count. For each character in s2, decrement its count. If all 26 buckets are zero at the end, they are anagrams!\nTime = O(N) linear, Space = O(1) constant.",
                    bulletPoints = listOf(
                        "Frequency array solves anagram detection in optimal linear time."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Linear Anagram Detection in Kotlin",
                    language = "KOTLIN",
                    code = """fun isAnagram(s: String, t: String): Boolean {
    // 1. Length check: instant O(1) early exit
    if (s.length != t.length) return false
    
    // 2. Frequency table for 26 lowercase English letters
    val counts = IntArray(26)
    
    // Increment for s, decrement for t simultaneously
    for (i in s.indices) {
        counts[s[i] - 'a']++
        counts[t[i] - 'a']--
    }
    
    // 3. Verify all frequency balances are zero
    for (count in counts) {
        if (count != 0) return false
    }
    return true
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 3", "Early exit: strings with unequal lengths cannot be anagrams."),
                        CodeStepExplanation(2, "Lines 9-12", "Single-pass loop increments count for character in s and decrements for t."),
                        CodeStepExplanation(3, "Lines 15-18", "Confirm that all frequency slots returned to zero balance in O(1) time.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Strings of unequal length cannot be anagrams: check length first for O(1) rejection.",
                "Sorting strings takes O(N log N) time; frequency counting takes O(N) linear time.",
                "A single 26-slot integer array tracks balance by incrementing for string A and decrementing for string B.",
                "Frequency counting consumes strictly O(1) auxiliary memory for fixed alphabets."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_str_ana_1",
                    question = "Why is the frequency counter approach O(N) faster than the sorting approach O(N log N) for anagram detection?",
                    options = listOf(
                        "It reads each character in a single pass without rearranging elements into sorted order",
                        "Because sorting uses multi-threading",
                        "Because frequency arrays run on GPU registers",
                        "Because HashMaps run in O(0) time"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Sorting requires O(N log N) comparisons, whereas counting character frequencies scans the string once in O(N) time and checks 26 integer slots in O(1) time."
                ),
                LessonPracticeQuestion(
                    id = "pq_str_ana_2",
                    question = "What is the purpose of incrementing the count for string S and decrementing for string T in the same loop?",
                    options = listOf(
                        "If the strings are anagrams, every frequency slot will net exactly zero at the end",
                        "It converts negative numbers into positive numbers",
                        "It prevents ArrayIndexOutOfBoundsException",
                        "It allows searching for palindromes simultaneously"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because valid anagrams have identical character frequencies, incrementing for S and decrementing for T cancels out every letter, leaving every bucket at zero."
                )
            )
        ),

        // 5. Pattern Matching & Search
        DsaLessonContent(
            lessonId = "string_pattern_matching_l1",
            subtopicId = "string_pattern_matching",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Pattern Matching & Search",
            subtitle = "From naive sliding comparisons to the Knuth-Morris-Pratt (KMP) failure function",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N + M) for KMP, O(N * M) naive",
            spaceComplexity = "O(M) for KMP LPS array",
            learningObjectives = listOf(
                "Understand the Substring Search problem: locating pattern P (length M) in text T (length N).",
                "Analyze why Naive Pattern Matching suffers O(N * M) worst-case back-tracking.",
                "Understand the KMP Longest Prefix Suffix (LPS) failure function.",
                "Apply the LPS array to slide the pattern without ever back-tracking the text pointer."
            ),
            sections = listOf(
                LessonSection(
                    id = "naive_pattern_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Substring Search Problem",
                    body = "Given a text string T of length N and a pattern string P of length M, find the starting index of P within T.\n\nNaive search aligns P at every index 0 to N - M and compares characters. If a mismatch occurs, it shifts P by only 1 position and restarts comparisons from the beginning of P.\n- Worst Case: Text = 'AAAAAAAAAB', Pattern = 'AAAB'. Takes O(N * M) operations due to repeated back-tracking!",
                    bulletPoints = listOf(
                        "Naive Time: O(N * M) worst case.",
                        "KMP Time: O(N + M) guaranteed linear time."
                    )
                ),
                LessonSection(
                    id = "kmp_lps_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Knuth-Morris-Pratt (KMP) & The LPS Array",
                    body = "Invented by Donald Knuth, James Morris, and Vaughan Pratt, KMP avoids back-tracking the text pointer entirely by precomputing the Longest Proper Prefix which is also a Suffix (LPS array):\n\nLPS[i] stores the length of the longest proper prefix of P[0..i] that matches a suffix of P[0..i].\n\nWhen a mismatch occurs at P[j]:\nInstead of restarting at P[0], we jump to P[LPS[j - 1]]. We already know the first LPS[j - 1] characters match! The text pointer i NEVER moves backward.",
                    bulletPoints = listOf(
                        "LPS Precomputation: O(M) time and O(M) space.",
                        "Text Search: O(N) time with zero back-tracking.",
                        "Total Time: O(N + M)."
                    ),
                    highlightNote = "KMP is ideal for streaming data where input cannot be rewound."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Knuth-Morris-Pratt (KMP) Search in Kotlin",
                    language = "KOTLIN",
                    code = """fun kmpSearch(text: String, pattern: String): List<Int> {
    val matches = mutableListOf<Int>()
    val n = text.length; val m = pattern.length
    if (m == 0 || n < m) return matches
    
    // 1. Precompute LPS (Longest Prefix Suffix) array
    val lps = computeLps(pattern)
    
    var i = 0 // text pointer (never moves backward!)
    var j = 0 // pattern pointer
    
    while (i < n) {
        if (text[i] == pattern[j]) {
            i++; j++
        }
        if (j == m) {
            matches.add(i - j) // Match found at index (i - j)
            j = lps[j - 1]     // Look for next match
        } else if (i < n && text[i] != pattern[j]) {
            if (j != 0) {
                j = lps[j - 1] // Fall back using LPS without moving i
            } else {
                i++
            }
        }
    }
    return matches
}

private fun computeLps(pattern: String): IntArray {
    val lps = IntArray(pattern.length)
    var len = 0; var i = 1
    while (i < pattern.length) {
        if (pattern[i] == pattern[len]) {
            len++; lps[i] = len; i++
        } else {
            if (len != 0) len = lps[len - 1] else { lps[i] = 0; i++ }
        }
    }
    return lps
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 29-39", "computeLps precomputes prefix-suffix overlap for the pattern in O(M) time."),
                        CodeStepExplanation(2, "Lines 11-14", "Advance both pointers when text[i] matches pattern[j]."),
                        CodeStepExplanation(3, "Lines 18-24", "On mismatch, jump pattern index j to lps[j-1] while leaving text pointer i stationary.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Naive substring matching takes O(N * M) worst-case time due to repeated back-tracking.",
                "KMP precomputes the LPS (Longest Prefix Suffix) array in O(M) time.",
                "The text pointer in KMP moves strictly forward, ensuring guaranteed O(N + M) total runtime.",
                "Essential for network packet inspection and DNA sequence searching."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_str_pat_1",
                    question = "Why does the Knuth-Morris-Pratt (KMP) algorithm guarantee O(N + M) runtime?",
                    options = listOf(
                        "The text pointer i never backtracks; on a mismatch, the pattern pointer jumps using the precomputed LPS table",
                        "It sorts the text using QuickSort first",
                        "It converts the text to a binary search tree",
                        "It limits text length to 1,000 characters"
                    ),
                    correctOptionIndex = 0,
                    explanation = "By using the LPS array, KMP shifts the pattern forward and resumes searching from a known overlapping prefix without ever decrementing or rewinding the text pointer i."
                ),
                LessonPracticeQuestion(
                    id = "pq_str_pat_2",
                    question = "What does each entry LPS[i] represent in the KMP algorithm?",
                    options = listOf(
                        "The length of the longest proper prefix of pattern[0..i] that is also a suffix of pattern[0..i]",
                        "The ASCII value of character at index i",
                        "The number of times the pattern appeared in the text",
                        "The memory address of the next character"
                    ),
                    correctOptionIndex = 0,
                    explanation = "LPS stands for Longest Proper Prefix which is also a Suffix; it records how many characters of the pattern prefix can be safely reused after a mismatch at position i."
                )
            )
        )
    )
}
