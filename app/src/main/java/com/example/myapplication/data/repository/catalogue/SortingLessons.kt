package com.example.myapplication.data.repository.catalogue

import com.example.myapplication.data.model.catalogue.CodeStepExplanation
import com.example.myapplication.data.model.catalogue.DsaLessonContent
import com.example.myapplication.data.model.catalogue.LessonCodeBlock
import com.example.myapplication.data.model.catalogue.LessonPracticeQuestion
import com.example.myapplication.data.model.catalogue.LessonSection
import com.example.myapplication.data.model.catalogue.LessonSectionType

object SortingLessons {

    val lessons: List<DsaLessonContent> = listOf(
        // 1. Bubble Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_bubble_l1",
            subtopicId = "sort_bubble",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Bubble Sort Algorithm",
            subtitle = "Repeated adjacent element swapping with early-termination optimization",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N^2) worst, O(N) best",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the Bubble Sort invariant: after pass i, the i largest elements are in their final positions.",
                "Implement the 'swapped' boolean flag to achieve O(N) best-case on already sorted arrays.",
                "Analyze time and space complexity and understand algorithmic stability.",
                "Learn why Bubble Sort is primarily educational rather than used in production libraries."
            ),
            sections = listOf(
                LessonSection(
                    id = "bubble_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Bubble Sort Works",
                    body = "Bubble Sort repeatedly steps through the array, compares adjacent elements, and swaps them if they are in the wrong order.\n\nLike physical bubbles rising to the surface of water, the largest unsorted element 'bubbles up' to its correct final position at the end of the array with each complete pass.",
                    bulletPoints = listOf(
                        "In-Place: Modifies the array directly using O(1) auxiliary memory.",
                        "Stable: Does not swap equal elements, preserving their original relative order."
                    )
                ),
                LessonSection(
                    id = "bubble_optimization",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Swapped Flag Optimization",
                    body = "A naive Bubble Sort always executes N - 1 passes, even if the array is already sorted. By introducing a boolean 'swapped' flag:\n- If an entire pass completes with ZERO swaps, the array is already sorted!\n- We can break immediately, achieving Best-Case O(N) linear time.",
                    bulletPoints = listOf(
                        "Worst Case O(N^2): Reverse sorted array.",
                        "Average Case O(N^2): Random array.",
                        "Best Case O(N): Already sorted array with early exit flag."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Optimized Bubble Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun bubbleSort(arr: IntArray) {
    val n = arr.size
    for (i in 0 until n - 1) {
        var swapped = false
        // Last i elements are already in place
        for (j in 0 until n - 1 - i) {
            if (arr[j] > arr[j + 1]) {
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
                swapped = true
            }
        }
        if (!swapped) break // Early exit if already sorted!
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 3", "Outer loop controls number of passes (at most n - 1)."),
                        CodeStepExplanation(2, "Line 6", "Inner loop scans up to n - 1 - i, avoiding already sorted tail elements."),
                        CodeStepExplanation(3, "Lines 7-12", "Swap adjacent elements if out of order; set swapped to true."),
                        CodeStepExplanation(4, "Line 14", "If no elements were swapped during pass, terminate immediately.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Bubble sort compares and swaps adjacent pairs: largest elements bubble to the end.",
                "Worst and Average time complexity is O(N^2); Space complexity is O(1) in-place.",
                "The swapped flag enables O(N) best-case detection for sorted arrays.",
                "Bubble sort is stable: equal elements never swap."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_bub_1",
                    question = "What is the primary benefit of adding a 'swapped' boolean flag to Bubble Sort?",
                    options = listOf(
                        "It allows the algorithm to terminate in O(N) time if the array is already sorted",
                        "It reduces auxiliary space from O(N) to O(1)",
                        "It guarantees O(N log N) worst-case performance",
                        "It converts Bubble Sort into an unstable algorithm"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If a full pass finishes with zero swaps, the array is already in sorted order, so the algorithm can terminate immediately in O(N) time."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_bub_2",
                    question = "Why is Bubble Sort classified as a 'stable' sorting algorithm?",
                    options = listOf(
                        "It only swaps when arr[j] > arr[j + 1], never swapping equal elements",
                        "It never uses recursion",
                        "It uses exactly N/2 memory buffers",
                        "Its execution time is constant across all inputs"
                    ),
                    correctOptionIndex = 0,
                    explanation = "An algorithm is stable if it preserves the relative order of duplicate elements. Since Bubble Sort swaps strictly when the predecessor is strictly greater (>), equal values retain their relative positions."
                )
            )
        ),

        // 2. Selection Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_selection_l1",
            subtopicId = "sort_selection",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Selection Sort Algorithm",
            subtitle = "Repeatedly selecting the minimum element with a guaranteed upper bound of N swaps",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N^2) always",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the Selection Sort invariant: prefix arr[0..i-1] is fully sorted at step i.",
                "Observe why Selection Sort makes at most N swaps (useful when write operations are expensive).",
                "Analyze why Selection Sort is O(N^2) across Best, Worst, and Average cases.",
                "Understand why standard Selection Sort is inherently unstable."
            ),
            sections = listOf(
                LessonSection(
                    id = "selection_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Selection Sort Works",
                    body = "Selection Sort divides the array into a sorted prefix and an unsorted suffix.\n\nIn each pass, it scans the entire unsorted suffix to locate the minimum element, and then swaps that minimum element with the first element of the unsorted suffix. The sorted prefix grows by one element each pass.",
                    bulletPoints = listOf(
                        "Fixed Comparisons: Always makes N(N - 1)/2 comparisons regardless of initial order.",
                        "Minimal Swaps: Makes at most N - 1 swaps in total."
                    )
                ),
                LessonSection(
                    id = "selection_tradeoffs",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Key Trade-Offs: Swaps vs. Comparisons",
                    body = "Even though Selection Sort is O(N^2) in all cases due to comparisons, it has a distinct hardware advantage: it performs at most O(N) memory writes (swaps).\n\nIn hardware systems like EEPROM or Flash memory where memory write cycles degrade chip lifespan, Selection Sort can be preferable to algorithms that perform frequent writes.",
                    bulletPoints = listOf(
                        "Time: O(N^2) Best, Worst, and Average.",
                        "Space: O(1) Auxiliary.",
                        "Stability: Unstable (long-distance swaps can reorder equal elements)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Selection Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun selectionSort(arr: IntArray) {
    val n = arr.size
    for (i in 0 until n - 1) {
        var minIndex = i
        // Find index of minimum element in unsorted suffix
        for (j in i + 1 until n) {
            if (arr[j] < arr[minIndex]) {
                minIndex = j
            }
        }
        // Swap found minimum with first unsorted element
        if (minIndex != i) {
            val temp = arr[i]
            arr[i] = arr[minIndex]
            arr[minIndex] = temp
        }
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 4", "Assume current index i holds the minimum element."),
                        CodeStepExplanation(2, "Lines 6-10", "Scan all elements to the right to locate the true smallest element."),
                        CodeStepExplanation(3, "Lines 12-16", "Perform a single swap to place the minimum element into index i.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Selection sort locates the minimum element in the unsorted suffix and places it at the prefix boundary.",
                "Comparisons are always O(N^2) regardless of input ordering.",
                "Performs at most N - 1 swaps, minimizing expensive memory write cycles.",
                "Selection sort is an unstable sorting algorithm."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_sel_1",
                    question = "What is the maximum number of swap operations Selection Sort performs on an array of N elements?",
                    options = listOf("N - 1 swaps", "N^2 swaps", "N log N swaps", "2N swaps"),
                    correctOptionIndex = 0,
                    explanation = "In each pass i (from 0 to N-2), at most 1 swap is made to place the minimum element in position i, totaling at most N - 1 swaps."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_sel_2",
                    question = "Why does Selection Sort run in O(N^2) even when the input array is already sorted?",
                    options = listOf(
                        "The inner loop must still scan all remaining elements to confirm the minimum value",
                        "Because the array must be reversed first",
                        "Because swapping takes O(N) time per step",
                        "Because it allocates an auxiliary matrix"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Selection sort has no early-exit mechanism; to find the minimum of the remaining suffix, it must inspect every remaining element regardless of whether the array is already sorted."
                )
            )
        ),

        // 3. Insertion Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_insertion_l1",
            subtopicId = "sort_insertion",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Insertion Sort Algorithm",
            subtitle = "Progressively building a sorted subarray like arranging playing cards in hand",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N^2) worst, O(N) best",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand the Card Player analogy and sorted prefix invariant.",
                "Learn why Insertion Sort is extraordinarily fast on small or nearly-sorted datasets.",
                "Observe how modern hybrid algorithms (TimSort, IntroSort) use Insertion Sort for base cases.",
                "Verify stability and in-place O(1) space behavior."
            ),
            sections = listOf(
                LessonSection(
                    id = "insertion_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Insertion Sort Works",
                    body = "Imagine picking up playing cards one at a time. As you pick up each card, you slide it left into its proper sorted position among the cards already in your hand.\n\nInsertion Sort processes elements one by one, shifting greater elements to the right to make space for the incoming 'key' element.",
                    bulletPoints = listOf(
                        "Online Algorithm: Can sort a list as it receives it element-by-element.",
                        "Adaptive: Runs in O(N) time on arrays with few inversions."
                    )
                ),
                LessonSection(
                    id = "insertion_in_practice",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Production Engines Use Insertion Sort",
                    body = "Even though Insertion Sort is O(N^2) worst-case, it has minimal constant overhead and zero recursion stack cost. For small arrays (N <= 32 to 64 elements), Insertion Sort consistently outperforms QuickSort and MergeSort!\n\nThis is why Java's and Python's default sorting engine (TimSort) uses MergeSort for large collections but switches to Insertion Sort for chunks under 32 elements.",
                    bulletPoints = listOf(
                        "Best Case: O(N) when array is already sorted.",
                        "Worst Case: O(N^2) when array is reverse sorted.",
                        "Stable: Yes."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Insertion Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun insertionSort(arr: IntArray) {
    for (i in 1 until arr.size) {
        val key = arr[i]
        var j = i - 1
        // Shift elements greater than key to one position ahead
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j]
            j--
        }
        arr[j + 1] = key // Insert key in sorted position
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Line 2", "Start at index 1 because a single element (index 0) is already sorted by definition."),
                        CodeStepExplanation(2, "Line 3", "Store current item in temporary variable key."),
                        CodeStepExplanation(3, "Lines 6-9", "Shift all elements in sorted prefix that are greater than key one index right."),
                        CodeStepExplanation(4, "Line 10", "Place key into the vacated correct position.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Insertion sort inserts each element into its correct place within the sorted prefix.",
                "Runs in O(N) linear time on already-sorted or nearly-sorted datasets.",
                "Used as the base-case sorting engine in production hybrid algorithms like TimSort.",
                "Stable and operates in-place using O(1) auxiliary space."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_ins_1",
                    question = "Why do production sorting algorithms like TimSort switch to Insertion Sort for small arrays (N < 32)?",
                    options = listOf(
                        "Insertion sort has very low constant factors and no recursion overhead for small inputs",
                        "Insertion sort is O(log N) on small arrays",
                        "Because small arrays cannot be partitioned",
                        "Because merge sort fails when N < 64"
                    ),
                    correctOptionIndex = 0,
                    explanation = "For small N, the low constant overhead, lack of recursion, and cache-friendly shifts make Insertion Sort faster in hardware than divide-and-conquer algorithms."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_ins_2",
                    question = "What is the time complexity of Insertion Sort when the input array is already completely sorted?",
                    options = listOf("O(N) Linear", "O(N^2) Quadratic", "O(N log N)", "O(1) Constant"),
                    correctOptionIndex = 0,
                    explanation = "When already sorted, the while loop condition (arr[j] > key) fails immediately on the first comparison every time, performing exactly N - 1 comparisons = O(N)."
                )
            )
        ),

        // 4. Merge Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_merge_l1",
            subtopicId = "sort_merge",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Merge Sort Algorithm",
            subtitle = "Guaranteed O(N log N) divide-and-conquer sorting with 2-way linear merging",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N log N) in all cases",
            spaceComplexity = "O(N)",
            learningObjectives = listOf(
                "Master the Divide-and-Conquer paradigm: Divide into halves, Conquer recursively, and Combine via Merge.",
                "Analyze the recurrence relation T(N) = 2T(N/2) + O(N) using the Master Theorem.",
                "Implement the two-finger linear merge step.",
                "Understand why Merge Sort is stable and preferred for linked lists and external sorting."
            ),
            sections = listOf(
                LessonSection(
                    id = "merge_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Divide, Conquer, and Combine",
                    body = "Merge Sort is a classic divide-and-conquer algorithm invented by John von Neumann in 1945:\n\n1. Divide: Split the array of size N into two equal halves of size N/2.\n2. Conquer: Recursively sort both halves.\n3. Combine: Merge the two sorted halves into a single sorted array using two pointers in O(N) time.",
                    bulletPoints = listOf(
                        "Recursion Depth: log2(N) levels of halving.",
                        "Work Per Level: O(N) merging operations across all sub-arrays.",
                        "Total Time: O(N log N) in Best, Average, and Worst cases!"
                    )
                ),
                LessonSection(
                    id = "merge_space_tradeoff",
                    type = LessonSectionType.PERFORMANCE_IMPACT,
                    title = "The Memory Trade-Off",
                    body = "Unlike QuickSort and HeapSort, standard Merge Sort is NOT in-place. Merging two contiguous subarrays requires an auxiliary array of size O(N) to temporarily hold merged elements.\n\nHowever, Merge Sort guarantees stable sorting and predictable O(N log N) execution, making it the algorithm of choice for sorting objects, database external sorting, and linked lists.",
                    bulletPoints = listOf(
                        "Time: O(N log N) guaranteed under all conditions.",
                        "Space: O(N) auxiliary memory.",
                        "Stability: Stable."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Merge Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun mergeSort(arr: IntArray, left: Int = 0, right: Int = arr.size - 1) {
    if (left >= right) return // Base case: 0 or 1 element
    
    val mid = left + (right - left) / 2
    mergeSort(arr, left, mid)       // Sort left half
    mergeSort(arr, mid + 1, right)  // Sort right half
    merge(arr, left, mid, right)    // Merge both halves
}

private fun merge(arr: IntArray, left: Int, mid: Int, right: Int) {
    val temp = IntArray(right - left + 1)
    var i = left; var j = mid + 1; var k = 0
    
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) { // '<=' maintains stability!
            temp[k++] = arr[i++]
        } else {
            temp[k++] = arr[j++]
        }
    }
    while (i <= mid) temp[k++] = arr[i++]
    while (j <= right) temp[k++] = arr[j++]
    
    for (idx in temp.indices) arr[left + idx] = temp[idx]
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-6", "Divide array into two halves at mid and recursively sort each half."),
                        CodeStepExplanation(2, "Lines 13-22", "Two-pointer merge: compare elements from left and right halves and place smaller into temp."),
                        CodeStepExplanation(3, "Lines 24-25", "Copy any remaining elements from either half."),
                        CodeStepExplanation(4, "Line 27", "Copy merged sorted elements back into original array.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Merge sort guarantees O(N log N) runtime across best, average, and worst cases.",
                "Recurrence relation T(N) = 2T(N/2) + O(N) solves to O(N log N) via Master Theorem.",
                "Requires O(N) auxiliary memory buffer to merge subarrays.",
                "Preserves element stability (equal items maintain original relative sequence)."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_mrg_1",
                    question = "What is the worst-case time complexity of Merge Sort?",
                    options = listOf("O(N log N)", "O(N^2)", "O(N)", "O(log N)"),
                    correctOptionIndex = 0,
                    explanation = "Merge sort divides the problem in half at every level (log N levels) and performs O(N) work to merge per level, guaranteeing O(N log N) worst-case runtime."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_mrg_2",
                    question = "What is the primary drawback of Merge Sort compared to Quick Sort or Heap Sort on arrays?",
                    options = listOf(
                        "It requires O(N) auxiliary memory for the temporary merge buffer",
                        "Its time complexity degrades to O(N^2) on random data",
                        "It is an unstable sorting algorithm",
                        "It cannot sort negative numbers"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Standard array merge sort requires allocating a temporary buffer of size O(N) to merge subarrays, whereas Quick Sort and Heap Sort operate in-place with O(1) or O(log N) auxiliary memory."
                )
            )
        ),

        // 5. Quick Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_quick_l1",
            subtopicId = "sort_quick",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Quick Sort Algorithm",
            subtitle = "In-place partitioning around a pivot with O(N log N) average-case speed",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N log N) average, O(N^2) worst",
            spaceComplexity = "O(log N) stack",
            learningObjectives = listOf(
                "Understand the Partitioning Invariant: elements <= pivot on left, elements > pivot on right.",
                "Learn Lomuto vs. Hoare partitioning schemes.",
                "Analyze why choosing a poor pivot (e.g., first element on sorted data) causes O(N^2) degradation.",
                "Apply Randomized Pivot selection and Median-of-Three to guarantee O(N log N) performance."
            ),
            sections = listOf(
                LessonSection(
                    id = "quick_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "How Quick Sort Works",
                    body = "Invented by Tony Hoare in 1959, Quick Sort is one of the most widely used sorting algorithms in the world.\n\n1. Pick a Pivot: Choose an element from the array.\n2. Partition: Rearrange the array in-place so all elements smaller than pivot come before it, and all elements greater come after it. The pivot is now at its final sorted position!\n3. Recurse: Recursively apply Quick Sort to the left and right subarrays.",
                    bulletPoints = listOf(
                        "In-Place: Reorders elements without allocating O(N) auxiliary arrays.",
                        "Cache Friendly: Linear partition scans produce outstanding CPU cache performance."
                    )
                ),
                LessonSection(
                    id = "pivot_selection_pitfalls",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "The Worst-Case Trap & Randomized Pivots",
                    body = "If you always pick the first or last element as pivot:\n- On an already-sorted array, partitioning splits N elements into 0 and N - 1.\n- The recursion depth becomes N instead of log N, degrading runtime to O(N^2)!\n\nSolution: Pick a random pivot or use Median-of-Three (median of first, middle, and last elements). This mathematically eliminates the O(N^2) adversarial case in real-world scenarios.",
                    bulletPoints = listOf(
                        "Average Case: O(N log N)",
                        "Worst Case: O(N^2) when splits are unbalanced.",
                        "Auxiliary Space: O(log N) recursion call stack."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Quick Sort With Lomuto Partition in Kotlin",
                    language = "KOTLIN",
                    code = """fun quickSort(arr: IntArray, low: Int = 0, high: Int = arr.size - 1) {
    if (low < high) {
        val pivotIndex = partition(arr, low, high)
        quickSort(arr, low, pivotIndex - 1)  // Sort elements before pivot
        quickSort(arr, pivotIndex + 1, high) // Sort elements after pivot
    }
}

private fun partition(arr: IntArray, low: Int, high: Int): Int {
    val pivot = arr[high] // Pick rightmost element as pivot
    var i = low - 1       // Index of smaller element
    
    for (j in low until high) {
        if (arr[j] <= pivot) {
            i++
            val temp = arr[i]; arr[i] = arr[j]; arr[j] = temp
        }
    }
    // Place pivot at its final correct position (i + 1)
    val temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp
    return i + 1
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 2-6", "Check base case; partition array and recursively sort sub-arrays on either side of pivot."),
                        CodeStepExplanation(2, "Lines 10-17", "Lomuto partition: compare each item to pivot; swap smaller elements into lower boundary i."),
                        CodeStepExplanation(3, "Lines 20-21", "Swap pivot into position i + 1, where all left items are <= pivot and right items > pivot.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Quick sort partitions elements in-place around a chosen pivot element.",
                "Average runtime is O(N log N) with excellent real-world cache locality.",
                "Poor pivot selection on sorted arrays can degrade performance to O(N^2).",
                "Randomized pivots or median-of-three restore expected O(N log N) performance."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_qck_1",
                    question = "Under what condition does naive Quick Sort (picking the last element as pivot) exhibit its worst-case O(N^2) runtime?",
                    options = listOf(
                        "When the array is already sorted in ascending or descending order",
                        "When the array contains exclusively random uniformly distributed values",
                        "When the array length is a power of 2",
                        "When all elements are negative"
                    ),
                    correctOptionIndex = 0,
                    explanation = "On a sorted array, choosing the last element produces partitions of size 0 and N - 1, leading to N recursive levels of O(N) work each = O(N^2)."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_qck_2",
                    question = "What is the average auxiliary space complexity of Quick Sort?",
                    options = listOf("O(log N) for the recursion call stack", "O(N) for temporary buffers", "O(1) strictly", "O(N^2)"),
                    correctOptionIndex = 0,
                    explanation = "Quick sort operates in-place on the array itself, but the balanced recursion tree creates a Call Stack depth of O(log N) frames."
                )
            )
        ),

        // 6. Heap Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_heap_l1",
            subtopicId = "sort_heap",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Heap Sort Algorithm",
            subtitle = "In-place complete binary tree heapification guaranteeing O(N log N) time and O(1) space",
            estimatedMinutes = 10,
            xpReward = 45,
            timeComplexity = "O(N log N) in all cases",
            spaceComplexity = "O(1)",
            learningObjectives = listOf(
                "Understand Max-Heap properties and array representation: parent(i) = (i-1)/2, left(i) = 2i+1, right(i) = 2i+2.",
                "Learn the Build-Max-Heap phase and why it runs in O(N) linear time.",
                "Extract maximum elements sequentially and maintain heap invariants via Sift-Down.",
                "Compare Heap Sort with Quick Sort and Merge Sort (O(N log N) guaranteed + O(1) space)."
            ),
            sections = listOf(
                LessonSection(
                    id = "heap_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "What Is Heap Sort?",
                    body = "Heap Sort leverages a binary heap data structure to sort an array in-place without auxiliary memory buffers.\n\n1. Build Max-Heap: Transform the array into a Max-Heap where each parent node is greater than or equal to its children (arr[i] >= arr[2i + 1] and arr[2i + 2]).\n2. Extract Max: Swap the root (maximum element at index 0) with the last element of the heap.\n3. Heapify: Reduce heap size by 1 and call heapify on root to restore heap property.\n4. Repeat until heap size reaches 1.",
                    bulletPoints = listOf(
                        "Time: O(N log N) Best, Average, and Worst case.",
                        "Space: O(1) Auxiliary (strictly in-place).",
                        "Stability: Unstable."
                    )
                ),
                LessonSection(
                    id = "build_heap_linear",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Why Build-Heap Is O(N), Not O(N log N)",
                    body = "A common misconception is that building a heap of N elements takes N * log N operations. By heapifying from the bottom up (from index N/2 - 1 down to 0), most nodes are near the leaves and only need to sift down 1 or 2 levels.\n\nSumming node counts at each height h yields the convergent series:\nSum(h / 2^h) for h=1 to infinity = 2.\nTherefore, building a max heap takes strictly O(N) linear time!",
                    bulletPoints = listOf(
                        "Extracting N elements takes N * log N operations.",
                        "Total time: O(N) + O(N log N) = O(N log N)."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Heap Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun heapSort(arr: IntArray) {
    val n = arr.size
    // 1. Build Max Heap (O(N) time)
    for (i in n / 2 - 1 downTo 0) {
        heapify(arr, n, i)
    }
    // 2. Extract elements one by one from heap (O(N log N) time)
    for (i in n - 1 downTo 1) {
        val temp = arr[0]; arr[0] = arr[i]; arr[i] = temp // Move root to end
        heapify(arr, i, 0) // Restore heap on reduced size i
    }
}

private fun heapify(arr: IntArray, n: Int, i: Int) {
    var largest = i
    val left = 2 * i + 1
    val right = 2 * i + 2
    
    if (left < n && arr[left] > arr[largest]) largest = left
    if (right < n && arr[right] > arr[largest]) largest = right
    
    if (largest != i) {
        val swap = arr[i]; arr[i] = arr[largest]; arr[largest] = swap
        heapify(arr, n, largest) // Recursively sift down
    }
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 4-6", "Build max heap from bottom non-leaf nodes down to 0 in O(N) time."),
                        CodeStepExplanation(2, "Lines 8-11", "Repeatedly swap root (max) with last element, shrinking active heap boundary."),
                        CodeStepExplanation(3, "Lines 14-25", "heapify function ensures largest of parent, left child, and right child floats to parent.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Heap sort builds a max heap in O(N) time, then repeatedly extracts the root maximum in O(log N).",
                "Guarantees O(N log N) worst-case time with strictly O(1) auxiliary space.",
                "Does not require O(N) auxiliary arrays like Merge Sort.",
                "Does not degrade to O(N^2) on adversarial inputs like Quick Sort."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_hp_1",
                    question = "What is the time complexity of the Build-Max-Heap phase in Heap Sort?",
                    options = listOf("O(N) Linear", "O(N log N)", "O(N^2)", "O(log N)"),
                    correctOptionIndex = 0,
                    explanation = "Because the majority of nodes in a complete binary tree reside near the leaves (requiring few sift-down steps), the mathematical summation of work over all heights converges to O(N)."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_hp_2",
                    question = "What makes Heap Sort uniquely advantageous over standard Quick Sort and Merge Sort?",
                    options = listOf(
                        "It combines guaranteed O(N log N) worst-case time with strictly O(1) in-place auxiliary space",
                        "It is completely stable",
                        "It runs in O(N) on random arrays",
                        "It requires no element comparisons"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Merge Sort needs O(N) memory, and Quick Sort can degrade to O(N^2) time. Heap Sort guarantees O(N log N) worst-case while operating strictly in-place with O(1) space."
                )
            )
        ),

        // 7. Counting Sort Algorithm
        DsaLessonContent(
            lessonId = "sort_counting_l1",
            subtopicId = "sort_counting",
            lessonNumber = 1,
            title = "Core Theory & Invariants – Counting Sort Algorithm",
            subtitle = "Non-comparison sorting in linear O(N + K) time using integer frequency histograms",
            estimatedMinutes = 8,
            xpReward = 40,
            timeComplexity = "O(N + K)",
            spaceComplexity = "O(N + K)",
            learningObjectives = listOf(
                "Understand why comparison-based sorting has a theoretical lower bound of Ω(N log N).",
                "Learn how Counting Sort bypasses this bound for bounded integer keys.",
                "Implement frequency counting and prefix sum cumulative count arrays.",
                "Preserve algorithmic stability by iterating backwards through the input."
            ),
            sections = listOf(
                LessonSection(
                    id = "counting_intro",
                    type = LessonSectionType.INTRODUCTION,
                    title = "Breaking the Comparison Barrier",
                    body = "Any sorting algorithm that compares elements (like Merge Sort or Quick Sort) mathematically requires at least Ω(N log N) comparisons in the worst case (proven by decision tree models).\n\nCounting Sort is a non-comparison sorting algorithm. When keys are integers within a known bounded range [0..K], it counts the frequency of each distinct value and uses prefix sums to place elements directly into their sorted output positions in O(N + K) linear time!",
                    bulletPoints = listOf(
                        "N: Total number of input elements.",
                        "K: Range of the discrete integer values (Max - Min + 1).",
                        "Time: O(N + K) linear time."
                    )
                ),
                LessonSection(
                    id = "counting_stability",
                    type = LessonSectionType.CONCEPT_EXPLANATION,
                    title = "Cumulative Sums & Stability Preservation",
                    body = "To make Counting Sort stable (essential when sorting complex objects or serving as the engine for Radix Sort):\n1. Build frequency count of each integer.\n2. Transform counts into cumulative counts: count[i] += count[i - 1]. Now count[i] indicates the exact 1-based index where the last occurrence of value i belongs!\n3. Iterate BACKWARDS through the original input array, placing each element into output[count[val] - 1] and decrementing count[val].\n\nIterating backwards guarantees that the last occurrence in the input remains last in the output, preserving full stability!",
                    bulletPoints = listOf(
                        "Space Complexity: O(N + K) auxiliary memory."
                    )
                )
            ),
            codeExamples = listOf(
                LessonCodeBlock(
                    title = "Stable Counting Sort in Kotlin",
                    language = "KOTLIN",
                    code = """fun countingSort(arr: IntArray): IntArray {
    if (arr.isEmpty()) return arr
    val max = arr.maxOrNull() ?: 0
    val min = arr.minOrNull() ?: 0
    val range = max - min + 1
    
    val count = IntArray(range)
    val output = IntArray(arr.size)
    
    // 1. Store counts of each number
    for (num in arr) count[num - min]++
    
    // 2. Compute prefix cumulative counts
    for (i in 1 until range) count[i] += count[i - 1]
    
    // 3. Build output array iterating backwards for stability
    for (i in arr.size - 1 downTo 0) {
        val value = arr[i]
        val pos = count[value - min] - 1
        output[pos] = value
        count[value - min]--
    }
    return output
}""",
                    stepByStepExplanation = listOf(
                        CodeStepExplanation(1, "Lines 3-6", "Determine range K = max - min + 1 and allocate count array."),
                        CodeStepExplanation(2, "Line 12", "Record occurrences of each integer in O(N) time."),
                        CodeStepExplanation(3, "Line 15", "Prefix sum array determines ending slot index for each number in O(K) time."),
                        CodeStepExplanation(4, "Lines 18-23", "Iterate backwards from N-1 down to 0, ensuring duplicate stability.")
                    )
                )
            ),
            keyTakeaways = listOf(
                "Counting sort achieves O(N + K) linear time by eliminating element comparisons.",
                "Requires elements to be integers within a known bounded range K.",
                "Cumulative prefix counts enable stable element placement.",
                "Serves as the foundational subroutine for Radix Sort."
            ),
            practiceQuestions = listOf(
                LessonPracticeQuestion(
                    id = "pq_sort_cnt_1",
                    question = "Why does Counting Sort iterate backwards through the input array when placing elements into the output array?",
                    options = listOf(
                        "To preserve stability so duplicate elements retain their original relative order",
                        "Because memory addresses can only be read in reverse",
                        "To avoid integer overflow",
                        "To eliminate the need for a prefix sum array"
                    ),
                    correctOptionIndex = 0,
                    explanation = "Iterating backwards ensures that the element appearing later in the input is placed in the higher position of its allocated bucket and decremented, preserving stability."
                ),
                LessonPracticeQuestion(
                    id = "pq_sort_cnt_2",
                    question = "When does Counting Sort become inefficient compared to comparison-based sorting?",
                    options = listOf(
                        "When the range K is significantly larger than N (e.g., K = N^3)",
                        "When the array contains 0",
                        "When all elements are unique",
                        "When N is greater than 1,000"
                    ),
                    correctOptionIndex = 0,
                    explanation = "If the value range K is huge (e.g., sorting 10 numbers whose values are between 1 and 1,000,000,000), allocating and scanning the count array of size K dominates the runtime, making O(N log N) sorting far superior."
                )
            )
        )
    )
}
