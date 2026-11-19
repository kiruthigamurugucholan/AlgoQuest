package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object SearchingLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Linear Search Algorithm
        DsaLessonContent(
            lessonId = "search_linear_l1",
            subtopicId = "search_linear",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Linear Search Algorithm",
            subtitle = "Sequential element inspection for unsorted collections and sentinel optimizations",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the mechanics of sequential search across unsorted datasets.",
                "Implement boundary-checked linear search with early-exit conditions.",
                "Learn the Sentinel Search optimization to eliminate loop boundary checks.",
                "Analyze time complexity across Best, Worst, and Average cases."
            ),
            sections = listOf(
                LessonSection(
                    id = "lin_search_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Linear Search Works",
                    body = "Linear Search (or Sequential Search) starts at the beginning of a collection and checks every element one by one against the target value until a match is found or the end of the collection is reached.\n\nIt is the most straightforward search algorithm and requires no sorting or pre-processing.",
                    bulletPoints = listOf(
                        "Universal: Works on arrays, linked lists, files, streams, and unsorted inputs.",
                        "Simple: Requires no extra memory or data preparation.",
                        "Direct: Performs at most N comparisons."
                    )
                ),
                LessonSection(
                    id = "sentinel_optimization",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Sentinel Linear Search Optimization",
                    body = "In a standard linear search loop, two checks happen every iteration:\n1. Is index < array.size? (Boundary check)\n2. Is array[index] == target? (Equality check)\n\nIn Sentinel Search, we place the target value at the very end of the array (as a 'sentinel'). Now the loop can search without checking bounds! After the loop finishes, we check whether the index where it stopped was within the original array.",
                    bulletPoints = listOf(
                        "Halves the number of conditional branch evaluations per loop step."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Standard & Sentinel Linear Search",
                    language = "KOTLIN",
                    code = """// Standard Linear Search: O(N) time, O(1) space
fun linearSearch(arr: IntArray, target: Int): Int {
    for (i in arr.indices) {
        if (arr[i] == target) return i
    }
    return -1
}

// Sentinel Search: reduces branch checks per iteration
fun sentinelSearch(arr: IntArray, target: Int): Int {
    val n = arr.size
    if (n == 0) return -1
    val last = arr[n - 1]
    if (last == target) return n - 1
    
    arr[n - 1] = target // Place sentinel at end
    var i = 0
    while (arr[i] != target) {
        i++ // Only 1 comparison per iteration!
    }
    arr[n - 1] = last // Restore original element
    return if (i < n - 1) i else -1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-7", "Standard search iterates over all indices, returning upon match."),
                        CodeStepExplanation(2, "Lines 10-23", "Sentinel temporarily places target at last index to eliminate index < size checking inside while loop.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Linear search is universal and handles unsorted or streaming data.",
                "Time complexity: Best O(1), Worst O(N), Average O(N).",
                "Sentinel search reduces loop comparison overhead by guaranteeing a match will be found."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_srch_lin_1",
                    question = "What is the primary advantage of Sentinel Linear Search over standard Linear Search?",
                    options = listOf(
                        "It eliminates the array boundary check (i < N) inside the search loop",
                        "It reduces the time complexity to O(log N)",
                        "It sorts the array while searching",
                        "It works on binary trees instead of arrays"
                    ),
                    correctOptionIndex = 0,
                    explanation = "By placing the target at the end, the loop is guaranteed to terminate on target match, eliminating the conditional boundary check from every single iteration."
                ),
                LessonPracticeQuestion(
                    id = "pq_srch_lin_2",
                    question = "When should Linear Search be chosen over Binary Search?",
                    options = listOf(
                        "When the array is unsorted and sorting it would take longer than the search itself",
                        "When the array has more than 10,000,000 sorted elements",
                        "When memory addresses are stored in hexadecimal",
                        "When searching for floating point numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If data is unsorted and only searched once, sorting takes O(N log N) which is slower than a direct O(N) linear scan."
                )
            )
        ),

        // 2. Binary Search Algorithm
        DsaLessonContent(
            lessonId = "search_binary_l1",
            subtopicId = "search_binary",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Binary Search Algorithm",
            subtitle = "Halving the search space in sorted collections and integer overflow avoidance",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(log N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the fundamental prerequisite: elements must be sorted (monotonic order).",
                "Master the middle index calculation avoiding integer arithmetic overflow.",
                "Implement iterative and recursive binary search with exact loop invariants.",
                "Understand Lower Bound and Upper Bound variants for duplicate elements."
            ),
            sections = listOf(
                LessonSection(
                    id = "bin_search_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Power of Halving",
                    body = "Binary Search is one of the most efficient search algorithms in computer science. At each step, it compares the target with the middle element of the sorted collection:\n\n- If target == mid, the search succeeds.\n- If target < mid, the target must reside in the left half; discard the right half.\n- If target > mid, the target must reside in the right half; discard the left half.\n\nEach comparison halves the remaining search space. For 1,000,000 items, at most 20 comparisons are needed (log2(1,000,000) ≈ 19.93)!",
                    bulletPoints = listOf(
                        "Monotonic Invariant: Elements must follow a monotonic ascending or descending order.",
                        "Time: O(log N) worst and average case.",
                        "Space: O(1) auxiliary space when implemented iteratively."
                    )
                ),
                LessonSection(
                    id = "overflow_prevention",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Famous Integer Overflow Bug",
                    body = "For decades, many textbooks wrote the middle index calculation as:\nval mid = (low + high) / 2\n\nIn Java, C, and Kotlin, if low + high exceeds 2^31 - 1 (approx 2.14 billion), the addition overflows into a negative integer, causing an ArrayIndexOutOfBoundsException!\n\nThe mathematically identical, overflow-safe formula is:\nval mid = low + (high - low) / 2",
                    bulletPoints = listOf(
                        "Always use low + (high - low) / 2 in production code."
                    ),
                    highlightNote = "Joshua Bloch documented this bug in 2006 after it lurked in Java's java.util.Arrays for over 9 years."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Overflow-Safe Iterative Binary Search",
                    language = "KOTLIN",
                    code = """fun binarySearch(arr: IntArray, target: Int): Int {
    var low = 0
    var high = arr.size - 1
    
    // Invariant: If target exists, it is within arr[low..high]
    while (low <= high) {
        val mid = low + (high - low) / 2 // Overflow-safe midpoint
        
        when {
            arr[mid] == target -> return mid       // Found target!
            arr[mid] < target -> low = mid + 1     // Search right half
            else -> high = mid - 1                 // Search left half
        }
    }
    return -1 // Target not found
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-3", "Initialize low pointer at 0 and high pointer at arr.size - 1."),
                        CodeStepExplanation(2, "Line 7", "Compute midpoint safely using low + (high - low) / 2."),
                        CodeStepExplanation(3, "Lines 9-13", "Adjust low or high boundaries based on comparison with arr[mid].")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Binary search cuts the candidate search space in half with every comparison: O(log N).",
                "Mandatory requirement: data must be sorted.",
                "Always calculate mid as low + (high - low) / 2 to prevent 32-bit integer overflow.",
                "Iterative implementation guarantees O(1) auxiliary space."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_srch_bin_1",
                    question = "Why is 'low + (high - low) / 2' preferred over '(low + high) / 2'?",
                    options = listOf(
                        "It prevents integer overflow when low + high exceeds the maximum 32-bit integer limit",
                        "It runs faster in CPU registers because subtraction requires less energy",
                        "It produces floating point numbers automatically",
                        "It allows searching unsorted arrays"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If low and high are large (e.g., > 1 billion), adding them causes 32-bit signed integer overflow into negative values. Subtraction (high - low) never exceeds the integer range."
                ),
                LessonPracticeQuestion(
                    id = "pq_srch_bin_2",
                    question = "What is the maximum number of comparisons required to search through 1,048,576 sorted elements using Binary Search?",
                    options = listOf("20 comparisons", "1,000 comparisons", "524,288 comparisons", "1,048,576 comparisons"),
                    correctOptionIndex = 0,
                    explanation = "Because 1,048,576 is 2^20, binary search cuts the space in half at most log2(2^20) = 20 times."
                )
            )
        ),

        // 3. Jump Search Algorithm
        DsaLessonContent(
            lessonId = "search_jump_l1",
            subtopicId = "search_jump",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Jump Search Algorithm",
            subtitle = "Block jumping by sqrt(N) steps followed by local linear scans",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(sqrt(N))",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the Block Jump mechanism on sorted arrays.",
                "Prove why block size m = sqrt(N) minimizes total search steps.",
                "Compare Jump Search performance against Linear Search and Binary Search.",
                "Recognize systems where jumping forward is fast but jumping backward is costly."
            ),
            sections = listOf(
                LessonSection(
                    id = "jump_search_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Jump Search?",
                    body = "Like Binary Search, Jump Search operates on a sorted array. Instead of halving the space, it advances in fixed-size blocks of size m.\n\n1. Jump ahead by m steps until an element greater than or equal to target is found (or end of array is reached).\n2. Once the target's block is identified, perform a linear search backwards from that block's boundary to find the exact target.",
                    bulletPoints = listOf(
                        "Input Prerequisite: Array must be sorted.",
                        "Step Breakdown: (N / m) jumps + (m - 1) linear comparisons within block."
                    )
                ),
                LessonSection(
                    id = "optimal_block_size",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Derivation of Optimal Block Size m = sqrt(N)",
                    body = "Total comparisons in worst case: f(m) = (N / m) + m - 1.\nTo minimize this function, take the first derivative with respect to m and set to zero:\n-N / m^2 + 1 = 0 => m^2 = N => m = sqrt(N)!\n\nTherefore, setting step size to sqrt(N) guarantees the optimal runtime of O(sqrt(N)).",
                    bulletPoints = listOf(
                        "Linear Search: O(N)",
                        "Jump Search: O(sqrt(N))",
                        "Binary Search: O(log N)"
                    ),
                    highlightNote = "Jump search is ideal when jumping backward is expensive (e.g., streaming magnetic tape or forward-only disk readers)."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Jump Search in Kotlin",
                    language = "KOTLIN",
                    code = """import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

fun jumpSearch(arr: IntArray, target: Int): Int {
    val n = arr.size
    if (n == 0) return -1
    
    var step = floor(sqrt(n.toDouble())).toInt() // Optimal block size
    var prev = 0
    
    // Jump forward in blocks of size sqrt(N)
    while (arr[min(step, n) - 1] < target) {
        prev = step
        step += floor(sqrt(n.toDouble())).toInt()
        if (prev >= n) return -1 // Reached end of array
    }
    
    // Perform linear search within the identified block
    while (arr[prev] < target) {
        prev++
        if (prev == min(step, n)) return -1
    }
    
    return if (arr[prev] == target) prev else -1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 8-10", "Compute step size as floor(sqrt(N))."),
                        CodeStepExplanation(2, "Lines 14-18", "Jump forward in increments of step until finding block where element >= target."),
                        CodeStepExplanation(3, "Lines 21-24", "Execute linear scan inside the identified block to pinpoint exact match.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Jump search skips ahead by fixed blocks of size sqrt(N) on sorted arrays.",
                "Mathematical optimal step size is exactly m = sqrt(N).",
                "Time complexity is O(sqrt(N)) with O(1) auxiliary space.",
                "Provides a middle ground when binary search index hopping has high memory latency."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_srch_jmp_1",
                    question = "What is the optimal block jump size for an array of N elements in Jump Search?",
                    options = listOf("sqrt(N)", "N / 2", "log2(N)", "N / 4"),
                    correctOptionIndex = 0,
                    explanation = "Minimizing the total comparisons (N/m jumps + m linear scans) mathematically yields m = sqrt(N)."
                ),
                LessonPracticeQuestion(
                    id = "pq_srch_jmp_2",
                    question = "Where is Jump Search preferred over Binary Search?",
                    options = listOf(
                        "In hardware or media where jumping backwards is costly and sequential forward reads are cheap",
                        "When searching an unsorted array",
                        "When the dataset fits entirely in CPU L1 cache",
                        "When target values are negative"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Binary search jumps back and forth unpredictably. Jump search jumps forward only, requiring at most one short backwards linear scan, making it ideal for forward-streaming media."
                )
            )
        ),

        // 4. Interpolation Search Algorithm
        DsaLessonContent(
            lessonId = "search_interpolation_l1",
            subtopicId = "search_interpolation",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Interpolation Search Algorithm",
            subtitle = "Target-proportioned probing for uniformly distributed sorted values",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(log(log N)) average, O(N) worst",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the probe positioning formula based on target value interpolation.",
                "Recognize the Uniform Distribution prerequisite.",
                "Analyze time complexity: O(log(log N)) average vs O(N) worst case.",
                "Relate interpolation search to human dictionary lookups."
            ),
            sections = listOf(
                LessonSection(
                    id = "interp_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Searching Like a Human",
                    body = "When you look up 'Zebra' in a physical phone book or dictionary, you don't open to the exact middle (letter 'M') like Binary Search does. You open straight to the very end because you know 'Z' is close to the end.\n\nInterpolation Search mimics this intuition. If values are sorted AND uniformly distributed, it calculates a probe position proportioned to the target's numerical value.",
                    bulletPoints = listOf(
                        "Binary Search: Always probes mid = low + (high - low) / 2.",
                        "Interpolation Search: Probes pos = low + [(target - arr[low]) * (high - low)] / (arr[high] - arr[low])."
                    )
                ),
                LessonSection(
                    id = "interp_complexity",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "Asymptotic Performance & Distribution Sensitivity",
                    body = "The performance of Interpolation Search depends critically on data distribution:\n\n- Uniform Distribution (e.g., 10, 20, 30, 40...): Probes land directly near target in O(log(log N)) average time! For 1,000,000 items, log2(log2(1,000,000)) ≈ 4.3 comparisons.\n- Non-Uniform / Exponential Distribution (e.g., 1, 2, 4, 8, 1024, 1000000): Probe calculation repeatedly guesses poorly, degrading to O(N) linear time.",
                    bulletPoints = listOf(
                        "Average Case: O(log(log N))",
                        "Worst Case: O(N) when values grow exponentially."
                    ),
                    highlightNote = "Only use Interpolation Search when data is known to be uniformly distributed."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Interpolation Search in Kotlin",
                    language = "KOTLIN",
                    code = """fun interpolationSearch(arr: IntArray, target: Int): Int {
    var low = 0
    var high = arr.size - 1
    
    while (low <= high && target >= arr[low] && target <= arr[high]) {
        if (low == high) {
            return if (arr[low] == target) low else -1
        }
        
        // Target-proportioned probe formula
        val fraction = (target - arr[low]).toDouble() / (arr[high] - arr[low]).toDouble()
        val pos = low + (fraction * (high - low)).toInt()
        
        when {
            arr[pos] == target -> return pos
            arr[pos] < target -> low = pos + 1
            else -> high = pos - 1
        }
    }
    return -1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-5", "Condition checks ensure target is strictly bounded within arr[low] and arr[high]."),
                        CodeStepExplanation(2, "Lines 11-12", "Calculate probe position proportional to target value within value range."),
                        CodeStepExplanation(3, "Lines 14-18", "Adjust search boundaries similarly to binary search.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Interpolation search calculates probe position based on the target value's relative magnitude.",
                "For uniformly distributed data, it achieves ultra-fast O(log(log N)) average runtime.",
                "Degrades to O(N) worst-case on non-uniform or exponentially distributed datasets."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_srch_interp_1",
                    question = "What is the average time complexity of Interpolation Search on uniformly distributed sorted data?",
                    options = listOf("O(log(log N))", "O(log N)", "O(N)", "O(1)"),
                    correctOptionIndex = 0,
                    explanation = "Under uniform distribution, interpolation search doubles its search accuracy each step, achieving an expected O(log(log N)) runtime."
                ),
                LessonPracticeQuestion(
                    id = "pq_srch_interp_2",
                    question = "What happens to Interpolation Search if data is exponentially distributed (e.g., 1, 2, 4, 8, 16, 32...)?",
                    options = listOf(
                        "It degrades to O(N) worst-case linear time",
                        "It speeds up to O(1) constant time",
                        "It throws a DivideByZeroException",
                        "It automatically switches to Bubble Sort"
                    ),
                    correctOptionIndex = 0,
                    explanation = "When values grow exponentially, the linear interpolation formula makes inaccurate probes that advance boundaries by only 1 index at a time, resulting in O(N) worst-case performance."
                )
            )
        )
    )
}
