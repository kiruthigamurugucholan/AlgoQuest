package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object ArrayLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Array Traversal & Linear Search
        DsaLessonContent(
            lessonId = "array_traversal_l1",
            subtopicId = "array_traversal",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Array Traversal & Linear Search",
            subtitle = "Visiting every element sequentially in contiguous memory and scanning for targets",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand contiguous memory addressing and cache locality during array traversal.",
                "Implement robust boundary-checked forward and reverse linear scans.",
                "Analyze time complexity (Best: O(1), Worst: O(N), Average: O(N)).",
                "Learn when Linear Search is preferred over Binary Search (unsorted or small datasets)."
            ),
            sections = listOf(
                LessonSection(
                    id = "traversal_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Array Traversal?",
                    body = "Array traversal is the foundational operation of visiting every element in an array exactly once from start to finish.\n\nBecause arrays are stored contiguously in physical RAM, sequential traversal takes advantage of CPU spatial cache locality: when you read array[0], the CPU hardware automatically loads the next several memory bytes into ultra-fast L1/L2 cache lines.",
                    bulletPoints = listOf(
                        "Contiguous Memory: Elements arranged consecutively in address space.",
                        "Direct Indexing: Reading array[i] is an O(1) pointer arithmetic operation.",
                        "Sequential Access: Reading elements 0 through N-1 achieves maximum memory bus throughput."
                    )
                ),
                LessonSection(
                    id = "linear_search_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Linear Search Mechanics",
                    body = "Linear Search checks elements sequentially against a target value until either a match is identified or the array boundaries are exhausted.\n\nUnlike Binary Search, Linear Search makes ZERO assumptions about element ordering — it works identically on unsorted, random, or duplicate datasets.",
                    bulletPoints = listOf(
                        "Best Case O(1): Target is the very first element inspected (index 0).",
                        "Worst Case O(N): Target is at the final index or completely absent from the collection.",
                        "Average Case O(N): Target resides near the midpoint (N / 2 comparisons)."
                    ),
                    highlightNote = "Linear search is the only universal search algorithm for arbitrary unsorted arrays."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Array Traversal and Linear Search in Kotlin",
                    language = "KOTLIN",
                    code = """fun linearSearch(arr: IntArray, target: Int): Int {
    for (i in arr.indices) {
        if (arr[i] == target) {
            return i // Found target at index i
        }
    }
    return -1 // Target not present
}

fun printAll(arr: IntArray) {
    for (element in arr) {
        print("${'$'}element ")
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 2", "arr.indices generates safe 0 until arr.size range avoiding IndexOutOfBoundsException."),
                        CodeStepExplanation(2, "Lines 3-5", "Compare current element to target; immediate return upon first match."),
                        CodeStepExplanation(3, "Line 7", "Return -1 sentinel value if entire array scanned without match.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Array traversal accesses contiguous memory, maximizing CPU cache line hit rates.",
                "Linear search operates on unsorted arrays with O(N) worst-case and O(1) space complexity.",
                "Sentinel return values (like -1) unambiguously indicate search misses."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_trav_1",
                    question = "Under what scenario does Linear Search achieve its Best Case time complexity of O(1)?",
                    options = listOf(
                        "When the target element is located at index 0",
                        "When the array is already sorted in ascending order",
                        "When the target is located in the exact middle of the array",
                        "When the array contains exclusively positive integers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If the target is the first element inspected, the loop terminates on iteration 0 after exactly 1 comparison = O(1)."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_trav_2",
                    question = "Why is sequential array traversal faster in hardware than traversing a linked list of the same length?",
                    options = listOf(
                        "CPU cache locality loads adjacent array elements together into fast L1/L2 cache lines",
                        "Arrays automatically parallelize iterations across all CPU cores",
                        "Linked lists require dynamic memory encryption",
                        "Array pointers use less voltage than node references"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because arrays occupy contiguous RAM addresses, modern CPU prefetchers load contiguous blocks into high-speed L1/L2 cache, minimizing expensive DRAM access delays."
                )
            )
        ),

        // 2. Array Insertion Operations
        DsaLessonContent(
            lessonId = "array_insertion_l1",
            subtopicId = "array_insertion",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Array Insertion Operations",
            subtitle = "Shifting elements to preserve ordering and amortized analysis of dynamic resizing",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) to O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand why fixed-size arrays require shifting elements to insert at an arbitrary index.",
                "Compare insertion at the end (O(1)) vs insertion at index 0 (O(N)).",
                "Learn how Dynamic Arrays (like ArrayList) achieve O(1) amortized time via geometric doubling.",
                "Implement boundary-safe in-place shifting algorithms."
            ),
            sections = listOf(
                LessonSection(
                    id = "insertion_mechanics",
                    type = LessonSectionType.INTRODUCTION,
                    title = "The Cost of Preserving Contiguity",
                    body = "Because an array must maintain contiguous elements without gaps, inserting a value into index k requires making room by shifting all existing elements from index k to N-1 one position to the right.",
                    bulletPoints = listOf(
                        "Insert at End (Appended): If capacity exists, insert directly at index N in O(1) time.",
                        "Insert at Beginning (Index 0): Every single element must shift right -> O(N) operations.",
                        "Insert at Middle (Index k): Requires shifting N - k elements -> O(N) operations."
                    ),
                    highlightNote = "Insertion cost is directly proportional to how close the insertion point is to the beginning of the array."
                ),
                LessonSection(
                    id = "amortized_doubling",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Dynamic Arrays & Amortized O(1) Insertion",
                    body = "Standard arrays have fixed capacity. Dynamic arrays (like Kotlin's ArrayList) allocate an underlying fixed array. When full, the dynamic array:\n1. Allocates a new array with double the capacity (2X).\n2. Copies all N existing elements.\n3. Deallocates the old array.\n\nWhile this resizing step costs O(N), it occurs infrequently. Over N insertions, the total cost of all resizings is 1 + 2 + 4 + ... + N <= 2N. Dividing 2N by N yields Amortized O(1) cost per insertion!",
                    bulletPoints = listOf(
                        "Geometric Doubling: Prevents repeated O(N) reallocations.",
                        "Amortized Cost: Averaging total work over an entire sequence of operations."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Inserting Into Fixed Array With Right Shift",
                    language = "KOTLIN",
                    code = """// Inserts 'element' at 'targetIndex' in an array with spare capacity
fun insertAt(arr: IntArray, size: Int, targetIndex: Int, element: Int): Boolean {
    if (size >= arr.size || targetIndex < 0 || targetIndex > size) {
        return false // Exceeds capacity or invalid index
    }
    // Shift elements to the right from end back to targetIndex
    for (i in size - 1 downTo targetIndex) {
        arr[i + 1] = arr[i]
    }
    arr[targetIndex] = element // Place new element
    return true
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-4", "Validate bounds: ensure size is within capacity and index is legal."),
                        CodeStepExplanation(2, "Lines 6-8", "Iterate backwards from size-1 down to targetIndex to prevent overwriting values."),
                        CodeStepExplanation(3, "Line 9", "Assign the new element into now-vacant targetIndex slot.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Inserting at the start or middle requires shifting elements right: O(N) time.",
                "Appending to the end of an array with spare capacity is O(1) constant time.",
                "Dynamic arrays double capacity geometrically to deliver O(1) amortized append runtime."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_ins_1",
                    question = "Why must array elements be shifted from right-to-left when inserting a new item into index 0?",
                    options = listOf(
                        "To prevent overwriting adjacent elements before they have been moved",
                        "Because the CPU arithmetic logic unit only counts downwards",
                        "To keep memory addresses in descending order",
                        "Because left-to-right shifts cause stack overflow"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Moving from right to left (back to front) ensures that each element is safely copied into an empty or already-moved slot without destroying existing data."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_ins_2",
                    question = "What is the amortized time complexity of appending an element to a dynamic array that doubles its capacity when full?",
                    options = listOf("O(1) Amortized", "O(N) Amortized", "O(N^2) Amortized", "O(log N) Amortized"),
                    correctOptionIndex = 0,
                    explanation = "Even though resizing takes O(N) time occasionally, doubling capacity ensures resizing happens exponentially less often, giving an average of O(1) work per append."
                )
            )
        ),

        // 3. Array Deletion Operations
        DsaLessonContent(
            lessonId = "array_deletion_l1",
            subtopicId = "array_deletion",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Array Deletion Operations",
            subtitle = "Shifting elements left to eliminate gaps and tombstone soft-deletion strategies",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(1) to O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand why removing an element from a contiguous array necessitates left-shifting.",
                "Analyze time complexity: deleting last element O(1) vs deleting first element O(N).",
                "Implement boundary-safe left-shift algorithms.",
                "Explore Tombstone / Lazy Deletion as an optimization strategy."
            ),
            sections = listOf(
                LessonSection(
                    id = "deletion_mechanics",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Eliminating Gaps in Contiguous Storage",
                    body = "An array cannot have unallocated holes in the middle of its valid elements. Removing an element at index k requires left-shifting every subsequent element from k+1 to N-1 one index to the left.\n\n- Delete from End: Decrement size counter -> O(1) time.\n- Delete from Beginning (Index 0): Shift all N - 1 remaining elements left -> O(N) time.\n- Delete from Middle: Shift N - 1 - k elements left -> O(N) time.",
                    bulletPoints = listOf(
                        "Contiguity Invariant: Valid items must span consecutive indices 0 until size - 1.",
                        "Left Shift: Copies arr[i] = arr[i + 1] moving towards the beginning."
                    )
                ),
                LessonSection(
                    id = "lazy_deletion",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Lazy / Tombstone Deletion",
                    body = "When frequent deletions cause severe performance degradation, high-performance systems (like database indexes and garbage collectors) use Lazy Deletion:\n1. Instead of shifting immediately, mark the slot with a 'Tombstone' flag (e.g., DELETED).\n2. Reads skip tombstone slots in O(1).\n3. When tombstone density exceeds a threshold (e.g. 50%), perform a single compaction pass in O(N).",
                    bulletPoints = listOf(
                        "Compaction: Batching multiple shifts into one single linear scan.",
                        "Tombstones: Prevents expensive O(N) shifts on every individual delete."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Deleting by Index With Left Shift",
                    language = "KOTLIN",
                    code = """fun deleteAt(arr: IntArray, size: Int, targetIndex: Int): Int {
    if (targetIndex < 0 || targetIndex >= size) {
        return size // Invalid index, no change
    }
    // Shift elements left to cover the gap at targetIndex
    for (i in targetIndex until size - 1) {
        arr[i] = arr[i + 1]
    }
    arr[size - 1] = 0 // Clear trailing element
    return size - 1 // Return new active size
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-4", "Validate that targetIndex is within the current active size."),
                        CodeStepExplanation(2, "Lines 6-8", "Copy arr[i + 1] into arr[i] from targetIndex forward, eliminating the gap."),
                        CodeStepExplanation(3, "Lines 9-10", "Zero out trailing duplicate and return decremented size.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Deleting an element from the start or middle requires shifting remaining elements left: O(N) time.",
                "Deleting the last element is an O(1) constant operation (decrementing size).",
                "Lazy/Tombstone deletion trades temporary memory for faster batch compaction."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_del_1",
                    question = "What is the time complexity of deleting the first element (index 0) of an array with N elements?",
                    options = listOf("O(N) Linear", "O(1) Constant", "O(log N)", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "Removing index 0 requires shifting all remaining N - 1 elements left by one position, requiring O(N) copy operations."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_del_2",
                    question = "Why is deleting the LAST element of an array an O(1) operation?",
                    options = listOf(
                        "No existing elements need to be shifted in memory",
                        "Because the CPU automatically frees the entire array",
                        "Because the array converts to a linked list",
                        "Because last elements are always stored in cache"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Deleting the last element requires only decrementing the active size counter; no subsequent elements exist that need to be shifted."
                )
            )
        ),

        // 4. Common Array Operations & Transformations
        DsaLessonContent(
            lessonId = "array_operations_l1",
            subtopicId = "array_operations",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Common Array Operations & Transformations",
            subtitle = "Prefix sums, in-place rotation, Kadane's algorithm, and segment reversal",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) to O(N)",
            learningObjectives = listOf(
                "Master the Prefix Sum array technique to answer range-sum queries in O(1) time.",
                "Implement in-place array rotation by K positions using the Three-Reverse trick.",
                "Understand Kadane's Algorithm for maximum contiguous subarray in O(N) time.",
                "Recognize frequency counting using auxiliary arrays."
            ),
            sections = listOf(
                LessonSection(
                    id = "prefix_sum_technique",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Prefix Sums: O(1) Range Queries",
                    body = "Suppose you need to compute the sum of elements between indices L and R thousands of times. Calculating it naively takes O(N) per query.\n\nThe Prefix Sum array precomputes cumulative sums in O(N):\nprefix[i] = arr[0] + arr[1] + ... + arr[i]\n\nNow, any range sum [L..R] is computed instantly in O(1):\nSum(L..R) = prefix[R] - prefix[L - 1] (or prefix[R] if L == 0).",
                    bulletPoints = listOf(
                        "Precomputation: O(N) time and O(N) auxiliary space.",
                        "Query Time: O(1) constant time lookup."
                    )
                ),
                LessonSection(
                    id = "kadane_algorithm",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Kadane's Algorithm: Maximum Subarray",
                    body = "Given an array of positive and negative integers, find the contiguous subarray with the largest sum.\n\nKadane's algorithm maintains a running sum. At each element x:\n- Either extend the existing subarray: currentSum + x\n- Or start a new subarray beginning at x: max(x, currentSum + x)\n\nThis scans the array in single O(N) time and O(1) space, replacing naive O(N^3) or O(N^2) brute force.",
                    bulletPoints = listOf(
                        "Invariant: At step i, currentMax is the maximum subarray sum ending at index i.",
                        "Global Max: Tracks the highest currentMax seen across all steps."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Kadane's Algorithm & In-Place Array Rotation",
                    language = "KOTLIN",
                    code = """// 1. Kadane's Algorithm: O(N) time, O(1) space
fun maxSubArraySum(nums: IntArray): Int {
    var maxSoFar = nums[0]
    var currentMax = nums[0]
    for (i in 1 until nums.size) {
        currentMax = maxOf(nums[i], currentMax + nums[i])
        maxSoFar = maxOf(maxSoFar, currentMax)
    }
    return maxSoFar
}

// 2. Rotate Array by K steps right using 3 reversals: O(N) time, O(1) space
fun rotate(nums: IntArray, k: Int) {
    val shift = k % nums.size
    reverse(nums, 0, nums.size - 1)          // 1. Reverse entire array
    reverse(nums, 0, shift - 1)              // 2. Reverse first k elements
    reverse(nums, shift, nums.size - 1)      // 3. Reverse remaining elements
}

private fun reverse(arr: IntArray, start: Int, end: Int) {
    var l = start; var r = end
    while (l < r) {
        val t = arr[l]; arr[l] = arr[r]; arr[r] = t
        l++; r--
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-9", "Kadane's tracks local optimum and updates global max in single pass."),
                        CodeStepExplanation(2, "Lines 13-18", "The three-reversal algorithm rotates array elements in-place with zero extra memory allocations.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Prefix sums trade O(N) one-time precomputation for instantaneous O(1) range queries.",
                "Kadane's algorithm solves the maximum subarray problem in optimal O(N) time and O(1) space.",
                "In-place array rotation can be executed without auxiliary buffers via 3 strategic reversals."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_op_1",
                    question = "How does Kadane's algorithm determine whether to extend an existing subarray or start a new one at index i?",
                    options = listOf(
                        "It chooses maxOf(nums[i], currentMax + nums[i])",
                        "It resets currentMax to zero whenever nums[i] is positive",
                        "It checks if nums[i] is an even number",
                        "It sorts the array in descending order first"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If adding nums[i] to currentMax produces a value lower than nums[i] alone, it is mathematically better to abandon the previous subarray and start fresh at nums[i]."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_op_2",
                    question = "Given a prefix sum array, how is the sum of elements between indices L and R computed?",
                    options = listOf(
                        "prefix[R] - prefix[L - 1] (when L > 0)",
                        "prefix[R] + prefix[L]",
                        "prefix[R - L]",
                        "prefix[R] * prefix[L]"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because prefix[R] contains the sum from 0 to R, subtracting prefix[L - 1] strips away the sum before L, leaving exactly the range sum from L to R."
                )
            )
        ),

        // 5. Two Pointers Technique
        DsaLessonContent(
            lessonId = "array_two_pointers_l1",
            subtopicId = "array_two_pointers",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Two Pointers Technique",
            subtitle = "Converging and unidirectional pointer coordinates eliminating nested iteration",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand how Two Pointers collapses O(N^2) nested loops into O(N) linear scans.",
                "Master the Converging Pointers pattern on sorted arrays (e.g., Two Sum II).",
                "Master the Fast and Slow Pointer pattern for in-place deduplication and partition.",
                "Prove pointer movement correctness using monotonicity invariants."
            ),
            sections = listOf(
                LessonSection(
                    id = "two_pointers_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is the Two Pointers Technique?",
                    body = "The Two Pointers technique coordinates two index references moving across an array according to mathematical rules.\n\nInstead of checking every pair with nested loops (N * N comparisons = O(N^2)), two pointers eliminate redundant states based on the problem's monotonic properties, solving the problem in a single O(N) pass.",
                    bulletPoints = listOf(
                        "Opposite Direction (Converging): Left pointer starts at 0, Right pointer starts at N-1, moving inward.",
                        "Same Direction (Fast & Slow): One pointer leads for scanning while the slow pointer tracks valid placements.",
                        "Prerequisite: Opposing pointers usually require the array to be sorted."
                    )
                ),
                LessonSection(
                    id = "two_sum_sorted_logic",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Two Sum on Sorted Array Invariant",
                    body = "Suppose we must find two numbers in a sorted array that sum to Target:\n1. Initialize left = 0, right = N - 1.\n2. Compute currentSum = arr[left] + arr[right].\n3. If currentSum == Target, we found our pair!\n4. If currentSum < Target: Since the array is sorted, increasing our sum requires moving left++ (a larger value).\n5. If currentSum > Target: Decreasing our sum requires moving right-- (a smaller value).\n\nBecause each step eliminates at least one element permanently, the search completes in at most N steps -> O(N) time and O(1) space!",
                    bulletPoints = listOf(
                        "No back-tracking needed because array monotonicity guarantees discarded pairs cannot sum to Target."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Two Sum on Sorted Array & In-Place Deduplication",
                    language = "KOTLIN",
                    code = """// 1. Converging Two Pointers: O(N) time, O(1) space
fun twoSumSorted(arr: IntArray, target: Int): IntArray? {
    var left = 0
    var right = arr.size - 1
    while (left < right) {
        val sum = arr[left] + arr[right]
        when {
            sum == target -> return intArrayOf(left, right)
            sum < target -> left++   // Need larger sum
            else -> right--          // Need smaller sum
        }
    }
    return null
}

// 2. Fast & Slow Pointers: Remove duplicates from sorted array in-place
fun removeDuplicates(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var slow = 0
    for (fast in 1 until nums.size) {
        if (nums[fast] != nums[slow]) {
            slow++
            nums[slow] = nums[fast]
        }
    }
    return slow + 1 // Length of unique elements
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-13", "Opposite-end pointers converge inward based on sorted sum comparison."),
                        CodeStepExplanation(2, "Lines 16-25", "Fast pointer scans while slow pointer writes unique values in-place.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Two pointers reduce quadratic O(N^2) pair-matching to linear O(N) time.",
                "Converging pointers exploit sorted monotonicity to rule out entire sub-ranges.",
                "Fast/Slow pointers enable in-place array modification with O(1) auxiliary space."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_tp_1",
                    question = "Why does the converging two-pointer technique on a sorted array allow incrementing left when arr[left] + arr[right] < target?",
                    options = listOf(
                        "Because arr[left] is the smallest remaining value; pairing it with any other index would produce an even smaller sum",
                        "Because left pointers must always advance before right pointers",
                        "Because right pointers cannot decrement below the median",
                        "Because arrays sort in reverse order automatically"
                    ),
                    correctOptionIndex = 0,
                    explanation = "In a sorted array, arr[right] is the largest available element. If even arr[left] + arr[right] is smaller than target, arr[left] cannot possibly pair with any smaller element to reach target, so left can be safely incremented."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_tp_2",
                    question = "What is the auxiliary space complexity of the fast and slow pointer deduplication algorithm?",
                    options = listOf("O(1) Constant", "O(N) Linear", "O(log N)", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "Deduplication modifies the array in-place using two scalar integer index pointers, consuming O(1) auxiliary memory."
                )
            )
        ),

        // 6. Sliding Window Technique
        DsaLessonContent(
            lessonId = "array_sliding_window_l1",
            subtopicId = "array_sliding_window",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Sliding Window Technique",
            subtitle = "Maintaining continuous subarray states across fixed and dynamic window intervals",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N)",
            spaceComplexity = "O(1) to O(K)",
            learningObjectives = listOf(
                "Understand the Sliding Window paradigm and how it reuses overlapping computation.",
                "Implement Fixed-Size Sliding Windows (e.g., maximum sum of K consecutive elements).",
                "Implement Dynamic/Variable-Size Sliding Windows (e.g., smallest subarray with sum >= S).",
                "Prove why sliding window achieves O(N) runtime even with a nested while-loop."
            ),
            sections = listOf(
                LessonSection(
                    id = "sliding_window_concept",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is a Sliding Window?",
                    body = "Consider computing the sum of every contiguous subarray of size K in an array of size N.\nA naive algorithm calculates each window from scratch: K operations * (N - K + 1) windows = O(N * K) time.\n\nNotice that window [i..i+K-1] and window [i+1..i+K] share K - 1 identical elements! The Sliding Window maintains a running aggregate by simply:\n1. Adding the newly entering right element.\n2. Subtracting the departing left element.\nEach slide takes O(1) time, yielding total O(N) runtime!",
                    bulletPoints = listOf(
                        "Fixed Window: Window size K remains constant as it slides across the array.",
                        "Dynamic Window: Window size expands to satisfy a condition, then contracts to find the minimal/maximal valid bound."
                    )
                ),
                LessonSection(
                    id = "sliding_window_complexity_proof",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Dynamic Sliding Windows Run in O(N)",
                    body = "Beginners often see a while-loop nested inside a for-loop and assume O(N^2) time:\n\nfor (right in 0 until N) {\n    while (conditionViolated) {\n        left++\n    }\n}\n\nNotice the key invariant: both the left pointer and right pointer can only advance FORWARD from 0 to N. Neither pointer ever moves backwards. Therefore, each element is visited at most TWICE (once by right, once by left). The amortized time is strictly 2N = O(N)!",
                    bulletPoints = listOf(
                        "Monotonic pointer movement guarantees linear time."
                    ),
                    highlightNote = "Amortized Analysis Rule: Total pointer steps across the entire algorithm cannot exceed 2N."
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Fixed Window Max Sum & Dynamic Window Subarray Sum",
                    language = "KOTLIN",
                    code = """// 1. Fixed Window of size K: O(N) time, O(1) space
fun maxSubarraySumFixed(arr: IntArray, k: Int): Int {
    if (arr.size < k) return 0
    var windowSum = 0
    for (i in 0 until k) windowSum += arr[i] // Initial window
    
    var maxSum = windowSum
    for (i in k until arr.size) {
        windowSum += arr[i] - arr[i - k] // Add incoming, subtract outgoing
        maxSum = maxOf(maxSum, windowSum)
    }
    return maxSum
}

// 2. Dynamic Window: Smallest subarray length with sum >= target
fun minSubArrayLen(target: Int, nums: IntArray): Int {
    var minLen = Int.MAX_VALUE
    var currentSum = 0
    var left = 0
    for (right in nums.indices) {
        currentSum += nums[right]
        while (currentSum >= target) {
            minLen = minOf(minLen, right - left + 1)
            currentSum -= nums[left]
            left++
        }
    }
    return if (minLen == Int.MAX_VALUE) 0 else minLen
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-11", "Fixed window sums first k items, then adds arr[i] and subtracts arr[i-k] in O(1) per slide."),
                        CodeStepExplanation(2, "Lines 14-26", "Dynamic window expands right until sum >= target, then shrinks left to minimize window length.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Sliding window avoids recomputing shared overlapping elements between adjacent windows.",
                "Fixed windows slide in O(1) per step: add new element, subtract old element.",
                "Dynamic windows expand right and contract left; each element is processed at most twice = O(N) total."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_arr_sw_1",
                    question = "When sliding a fixed window of size K one step to the right, how many operations are needed to update the running sum?",
                    options = listOf(
                        "O(1) operations (1 addition and 1 subtraction)",
                        "O(K) operations (re-summing all K elements)",
                        "O(N) operations",
                        "O(log K) operations"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Updating the running sum requires only adding the incoming right element and subtracting the departing left element = exactly 2 arithmetic operations (O(1))."
                ),
                LessonPracticeQuestion(
                    id = "pq_arr_sw_2",
                    question = "Why does a dynamic sliding window with a nested while-loop execute in O(N) instead of O(N^2)?",
                    options = listOf(
                        "Both the left and right pointers only move forward and can advance at most N times total",
                        "Because the array is automatically sorted in memory",
                        "Because the compiler unwinds the while loop into constant instructions",
                        "Because the condition only evaluates on odd indices"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Because neither pointer ever moves backward, the right pointer advances N times and the left pointer advances at most N times across the entire algorithm, bounding total iterations to 2N = O(N)."
                )
            )
        )
    )
}
