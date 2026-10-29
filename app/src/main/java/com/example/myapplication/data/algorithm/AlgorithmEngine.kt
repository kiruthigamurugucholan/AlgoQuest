package com.example.myapplication.data.algorithm

/**
 * Supported algorithm types in AlgoQuest Visualizer Lab
 */
enum class AlgorithmType(val displayName: String, val slug: String) {
    BINARY_SEARCH("Binary Search", "binary-search"),
    BUBBLE_SORT("Bubble Sort", "bubble-sort"),
    TWO_POINTERS("Two Pointers", "two-pointers")
}

/**
 * State representing a single dynamic execution step of an algorithm.
 */
data class VizStep(
    val array: List<Int>,
    val pointer1: Int = -1, // e.g. low (BS), j (Bubble), left (Two Pointers)
    val pointer2: Int = -1, // e.g. high (BS), j+1 (Bubble), right (Two Pointers)
    val pointer3: Int = -1, // e.g. mid (BS)
    val pointerLabels: Map<Int, String> = emptyMap(),
    val description: String,
    val activeLine: Int,
    val isFound: Boolean = false,
    val isSwap: Boolean = false,
    val eliminatedIndices: Set<Int> = emptySet(),
    val variables: Map<String, String> = emptyMap(),
    val reason: String = "",
    val isCompleted: Boolean = false
)

/**
 * Validation result for custom user inputs
 */
sealed class ValidationResult {
    data class Success(val array: List<Int>, val target: Int? = null) : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * Input validator for custom arrays and targets
 */
object InputValidator {
    private const val MIN_ELEMENTS = 2
    private const val MAX_ELEMENTS = 12
    private const val MIN_VAL = -999
    private const val MAX_VAL = 999

    fun validate(
        arrayInput: String,
        targetInput: String?,
        algorithmType: AlgorithmType,
        autoSortIfRequired: Boolean = true
    ): ValidationResult {
        if (arrayInput.isBlank()) {
            return ValidationResult.Error("Please enter comma-separated numbers.")
        }

        val parts = arrayInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size < MIN_ELEMENTS) {
            return ValidationResult.Error("Please enter at least $MIN_ELEMENTS numbers (e.g. 2, 5, 8).")
        }
        if (parts.size > MAX_ELEMENTS) {
            return ValidationResult.Error("Maximum of $MAX_ELEMENTS numbers allowed for clear visualization.")
        }

        val parsedArray = mutableListOf<Int>()
        for (part in parts) {
            val num = part.toIntOrNull()
                ?: return ValidationResult.Error("'$part' is not a valid integer.")
            if (num < MIN_VAL || num > MAX_VAL) {
                return ValidationResult.Error("Values must be between $MIN_VAL and $MAX_VAL.")
            }
            parsedArray.add(num)
        }

        // Validate target for Binary Search and Two Pointers
        val target: Int? = if (algorithmType != AlgorithmType.BUBBLE_SORT) {
            if (targetInput.isNullOrBlank()) {
                return ValidationResult.Error("Target value is required for ${algorithmType.displayName}.")
            }
            val t = targetInput.trim().toIntOrNull()
                ?: return ValidationResult.Error("Target '$targetInput' must be a valid integer.")
            if (t < MIN_VAL || t > MAX_VAL) {
                return ValidationResult.Error("Target must be between $MIN_VAL and $MAX_VAL.")
            }
            t
        } else {
            null
        }

        // For Binary Search and Two Pointers, the array must be sorted
        val finalArray = if (algorithmType == AlgorithmType.BINARY_SEARCH || algorithmType == AlgorithmType.TWO_POINTERS) {
            val isSorted = parsedArray.zipWithNext().all { (a, b) -> a <= b }
            if (!isSorted) {
                if (autoSortIfRequired) {
                    parsedArray.sorted()
                } else {
                    return ValidationResult.Error("${algorithmType.displayName} requires a sorted array.")
                }
            } else {
                parsedArray
            }
        } else {
            parsedArray
        }

        return ValidationResult.Success(finalArray, target)
    }
}

/**
 * Dynamic Algorithm Execution Engine generating real execution traces.
 */
object AlgorithmEngine {

    /**
     * Dynamically generates steps for Binary Search.
     */
    fun generateBinarySearchSteps(array: List<Int>, target: Int): List<VizStep> {
        val steps = mutableListOf<VizStep>()
        val arr = array.toList()
        val n = arr.size

        if (n == 0) return emptyList()

        var low = 0
        var high = n - 1
        var iteration = 1
        val eliminated = mutableSetOf<Int>()

        // Initial setup step
        steps.add(
            VizStep(
                array = arr,
                pointer1 = low,
                pointer2 = high,
                pointer3 = -1,
                pointerLabels = mapOf(low to "L", high to "H"),
                description = "Initialized Binary Search: Target = $target. Search space: [0..${n - 1}].",
                activeLine = 1,
                variables = mapOf("low" to "$low", "high" to "$high", "target" to "$target"),
                reason = "Pointers initialized to the boundaries of the sorted array."
            )
        )

        var foundIndex = -1

        while (low <= high) {
            val mid = low + (high - low) / 2
            val midVal = arr[mid]

            // Step 1: Calculate mid
            val labelsWithMid = mutableMapOf<Int, String>()
            if (low == high && low == mid) {
                labelsWithMid[low] = "L M H"
            } else {
                if (low == mid) labelsWithMid[low] = "L M" else labelsWithMid[low] = "L"
                if (high == mid) labelsWithMid[high] = "M H" else labelsWithMid[high] = "H"
                if (mid != low && mid != high) labelsWithMid[mid] = "M"
            }

            steps.add(
                VizStep(
                    array = arr,
                    pointer1 = low,
                    pointer2 = high,
                    pointer3 = mid,
                    pointerLabels = labelsWithMid,
                    description = "Iteration $iteration: Mid index = ($low + $high) / 2 = $mid. Element at arr[$mid] is $midVal.",
                    activeLine = 3,
                    eliminatedIndices = eliminated.toSet(),
                    variables = mapOf("low" to "$low", "mid" to "$mid", "high" to "$high", "arr[mid]" to "$midVal", "target" to "$target"),
                    reason = "Midpoint computed using overflow-safe formula: low + (high - low) / 2."
                )
            )

            // Step 2: Compare midVal with target
            if (midVal == target) {
                foundIndex = mid
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = low,
                        pointer2 = high,
                        pointer3 = mid,
                        pointerLabels = mapOf(mid to "FOUND!"),
                        description = "Target found! arr[$mid] ($midVal) == Target ($target).",
                        activeLine = 4,
                        isFound = true,
                        isCompleted = true,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("low" to "$low", "mid" to "$mid", "high" to "$high", "result" to "Index $mid"),
                        reason = "Match discovered in $iteration iterations. Returning index $mid."
                    )
                )
                break
            } else if (midVal < target) {
                // Eliminate left half including mid
                for (i in low..mid) {
                    eliminated.add(i)
                }
                val newLow = mid + 1
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = low,
                        pointer2 = high,
                        pointer3 = mid,
                        pointerLabels = labelsWithMid,
                        description = "arr[$mid] ($midVal) < Target ($target): Target lies in the right partition. Discard [${low}..${mid}].",
                        activeLine = 5,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("low" to "$low", "mid" to "$mid", "high" to "$high", "new_low" to "$newLow"),
                        reason = "Because the array is sorted in ascending order, all elements <= arr[mid] are too small."
                    )
                )
                low = newLow
            } else {
                // Eliminate right half including mid
                for (i in mid..high) {
                    eliminated.add(i)
                }
                val newHigh = mid - 1
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = low,
                        pointer2 = high,
                        pointer3 = mid,
                        pointerLabels = labelsWithMid,
                        description = "arr[$mid] ($midVal) > Target ($target): Target lies in the left partition. Discard [${mid}..${high}].",
                        activeLine = 6,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("low" to "$low", "mid" to "$mid", "high" to "$high", "new_high" to "$newHigh"),
                        reason = "Because the array is sorted, all elements >= arr[mid] are strictly larger than target."
                    )
                )
                high = newHigh
            }

            iteration++
        }

        // If target was not found
        if (foundIndex == -1) {
            for (i in 0 until n) {
                eliminated.add(i)
            }
            steps.add(
                VizStep(
                    array = arr,
                    pointer1 = -1,
                    pointer2 = -1,
                    pointer3 = -1,
                    pointerLabels = emptyMap(),
                    description = "Search space exhausted (low > high: $low > $high). Target $target does not exist in array.",
                    activeLine = 8,
                    isFound = false,
                    isCompleted = true,
                    eliminatedIndices = eliminated.toSet(),
                    variables = mapOf("low" to "$low", "high" to "$high", "result" to "-1 (Not Found)"),
                    reason = "Loop boundary condition violated. Returning -1."
                )
            )
        }

        return steps
    }

    /**
     * Dynamically generates steps for Bubble Sort.
     */
    fun generateBubbleSortSteps(array: List<Int>): List<VizStep> {
        val steps = mutableListOf<VizStep>()
        val arr = array.toMutableList()
        val n = arr.size

        if (n == 0) return emptyList()

        val sortedIndices = mutableSetOf<Int>()

        // Initial state
        steps.add(
            VizStep(
                array = arr.toList(),
                pointer1 = -1,
                pointer2 = -1,
                description = "Initial array before sorting. Length = $n.",
                activeLine = 1,
                variables = mapOf("n" to "$n", "swaps" to "0"),
                reason = "Ready to start comparison passes."
            )
        )

        var totalSwaps = 0

        for (i in 0 until n - 1) {
            var swappedInPass = false

            for (j in 0 until n - i - 1) {
                val labels = mutableMapOf<Int, String>()
                labels[j] = "j"
                labels[j + 1] = "j+1"
                sortedIndices.forEach { labels[it] = "Sorted" }

                // Step A: Compare adjacent pair
                val shouldSwap = arr[j] > arr[j + 1]
                steps.add(
                    VizStep(
                        array = arr.toList(),
                        pointer1 = j,
                        pointer2 = j + 1,
                        pointerLabels = labels,
                        description = "Pass ${i + 1}: Compare arr[$j] (${arr[j]}) and arr[${j + 1}] (${arr[j + 1]})." +
                                if (shouldSwap) " Out of order (${arr[j]} > ${arr[j + 1]}), will swap!" else " In order (${arr[j]} <= ${arr[j + 1]}), keep position.",
                        activeLine = 4,
                        variables = mapOf("pass" to "${i + 1}", "j" to "$j", "arr[j]" to "${arr[j]}", "arr[j+1]" to "${arr[j + 1]}"),
                        reason = "Checking adjacent invariant: arr[j] must be <= arr[j+1] for non-decreasing order."
                    )
                )

                // Step B: Swap if needed
                if (shouldSwap) {
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    swappedInPass = true
                    totalSwaps++

                    val swapLabels = mutableMapOf<Int, String>()
                    swapLabels[j] = "Swapped"
                    swapLabels[j + 1] = "Swapped"
                    sortedIndices.forEach { swapLabels[it] = "Sorted" }

                    steps.add(
                        VizStep(
                            array = arr.toList(),
                            pointer1 = j,
                            pointer2 = j + 1,
                            pointerLabels = swapLabels,
                            description = "Swapped indices $j and ${j + 1}: [${arr[j]}, ${arr[j + 1]}]. Total swaps so far: $totalSwaps.",
                            activeLine = 5,
                            isSwap = true,
                            variables = mapOf("pass" to "${i + 1}", "swapped" to "true", "total_swaps" to "$totalSwaps"),
                            reason = "Larger element bubbles toward the right end."
                        )
                    )
                }
            }

            // Element at n - 1 - i is now settled
            val settledIndex = n - 1 - i
            sortedIndices.add(settledIndex)

            val settledLabels = mutableMapOf<Int, String>()
            sortedIndices.forEach { settledLabels[it] = "Sorted" }

            steps.add(
                VizStep(
                    array = arr.toList(),
                    pointer1 = settledIndex,
                    pointer2 = -1,
                    pointerLabels = settledLabels,
                    description = "Pass ${i + 1} complete. Largest unsorted element (${arr[settledIndex]}) settled at index $settledIndex.",
                    activeLine = 2,
                    isFound = true,
                    variables = mapOf("pass" to "${i + 1}", "settled_index" to "$settledIndex", "value" to "${arr[settledIndex]}"),
                    reason = "After pass i, the i-th largest element is guaranteed to be in its correct final position."
                )
            )

            // Early exit optimization if no swaps happened
            if (!swappedInPass) {
                (0 until n).forEach { sortedIndices.add(it) }
                break
            }
        }

        // Final completion step
        (0 until n).forEach { sortedIndices.add(it) }
        val finalLabels = sortedIndices.associateWith { "Sorted" }
        steps.add(
            VizStep(
                array = arr.toList(),
                pointer1 = -1,
                pointer2 = -1,
                pointerLabels = finalLabels,
                description = "Bubble Sort Complete! Array fully sorted in $totalSwaps total swaps.",
                activeLine = 8,
                isFound = true,
                isCompleted = true,
                variables = mapOf("status" to "Sorted", "total_swaps" to "$totalSwaps"),
                reason = "All adjacent inversions resolved. Invariant holds: arr[k] <= arr[k+1] for all k."
            )
        )

        return steps
    }

    /**
     * Dynamically generates steps for Two Pointers (Target Sum on sorted array).
     */
    fun generateTwoPointersSteps(array: List<Int>, targetSum: Int): List<VizStep> {
        val steps = mutableListOf<VizStep>()
        val arr = array.toList()
        val n = arr.size

        if (n < 2) return emptyList()

        var left = 0
        var right = n - 1
        var stepNum = 1
        val eliminated = mutableSetOf<Int>()

        steps.add(
            VizStep(
                array = arr,
                pointer1 = left,
                pointer2 = right,
                pointerLabels = mapOf(left to "L", right to "R"),
                description = "Initialized Two Pointers: Left at 0 (${arr[left]}), Right at ${n - 1} (${arr[right]}). Target Sum = $targetSum.",
                activeLine = 1,
                variables = mapOf("left" to "$left", "right" to "$right", "targetSum" to "$targetSum"),
                reason = "Pointers placed at opposite extremes of the sorted array to bound candidate sums."
            )
        )

        var found = false

        while (left < right) {
            val sum = arr[left] + arr[right]
            val labels = mapOf(left to "L", right to "R")

            steps.add(
                VizStep(
                    array = arr,
                    pointer1 = left,
                    pointer2 = right,
                    pointerLabels = labels,
                    description = "Step $stepNum: arr[$left] (${arr[left]}) + arr[$right] (${arr[right]}) = $sum.",
                    activeLine = 3,
                    eliminatedIndices = eliminated.toSet(),
                    variables = mapOf("left" to "$left", "right" to "$right", "sum" to "$sum", "target" to "$targetSum"),
                    reason = "Evaluate current pair sum against target sum $targetSum."
                )
            )

            if (sum == targetSum) {
                found = true
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = left,
                        pointer2 = right,
                        pointerLabels = mapOf(left to "MATCH", right to "MATCH"),
                        description = "Target Sum Found! arr[$left] (${arr[left]}) + arr[$right] (${arr[right]}) == $targetSum.",
                        activeLine = 4,
                        isFound = true,
                        isCompleted = true,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("left" to "$left", "right" to "$right", "result" to "Pair ($left, $right)"),
                        reason = "Match confirmed in $stepNum comparisons. Pair indices: [$left, $right]."
                    )
                )
                break
            } else if (sum < targetSum) {
                eliminated.add(left)
                val newLeft = left + 1
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = left,
                        pointer2 = right,
                        pointerLabels = labels,
                        description = "Sum $sum < $targetSum (Too small). Move Left pointer rightward: left = $newLeft (${arr[newLeft]}).",
                        activeLine = 5,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("left" to "$left", "right" to "$right", "action" to "left++", "new_left" to "$newLeft"),
                        reason = "Because the array is sorted, arr[left] paired with any element <= arr[right] will also be < targetSum."
                    )
                )
                left = newLeft
            } else {
                eliminated.add(right)
                val newRight = right - 1
                steps.add(
                    VizStep(
                        array = arr,
                        pointer1 = left,
                        pointer2 = right,
                        pointerLabels = labels,
                        description = "Sum $sum > $targetSum (Too large). Move Right pointer leftward: right = $newRight (${arr[newRight]}).",
                        activeLine = 6,
                        eliminatedIndices = eliminated.toSet(),
                        variables = mapOf("left" to "$left", "right" to "$right", "action" to "right--", "new_right" to "$newRight"),
                        reason = "Because the array is sorted, arr[right] paired with any element >= arr[left] will also be > targetSum."
                    )
                )
                right = newRight
            }

            stepNum++
        }

        if (!found) {
            (0 until n).forEach { eliminated.add(it) }
            steps.add(
                VizStep(
                    array = arr,
                    pointer1 = -1,
                    pointer2 = -1,
                    description = "Pointers met (left >= right: $left >= $right). No pair sums to $targetSum.",
                    activeLine = 8,
                    isFound = false,
                    isCompleted = true,
                    eliminatedIndices = eliminated.toSet(),
                    variables = mapOf("left" to "$left", "right" to "$right", "result" to "null (No pair found)"),
                    reason = "All candidate pairs exhausted in O(N) single-pass scan."
                )
            )
        }

        return steps
    }
}

