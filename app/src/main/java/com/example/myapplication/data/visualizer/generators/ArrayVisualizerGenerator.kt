package com.example.myapplication.data.visualizer.generators

import com.example.myapplication.data.model.visualizer.DsaVisualizerStep
import com.example.myapplication.data.model.visualizer.DsaVizType

object ArrayVisualizerGenerator {

    private fun parseArrayInput(input: String?, defaultArr: List<Int>): List<Int> {
        if (input.isNullOrBlank()) return defaultArr
        val parsed = input.split(",").mapNotNull { it.trim().toIntOrNull() }
        return if (parsed.size in 2..10) parsed else defaultArr
    }

    // 1. Array Traversal
    fun generateArrayTraversal(customInput: String?): List<DsaVisualizerStep> {
        val arr = parseArrayInput(customInput, listOf(14, 27, 33, 49, 58, 62))
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = arr.size + 2

        // Init step
        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "Initialize Array Traversal",
                description = "Start array traversal at index 0. Array contains ${arr.size} elements.",
                actionType = "INITIALIZE",
                variables = mapOf("size" to "${arr.size}", "index" to "0"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(0 to "i"),
                highlightedIndices = setOf(0)
            )
        )

        var runningSum = 0
        arr.forEachIndexed { idx, value ->
            runningSum += value
            steps.add(
                DsaVisualizerStep(
                    stepIndex = idx + 1,
                    totalSteps = total,
                    title = "Visit Element at Index [$idx]",
                    description = "Reading arr[$idx] = $value. Accumulated running sum = $runningSum.",
                    actionType = "VISIT",
                    variables = mapOf("index" to "$idx", "value" to "$value", "sum" to "$runningSum"),
                    vizType = DsaVizType.ARRAY,
                    arrayElements = arr,
                    pointerLabels = mapOf(idx to "i"),
                    highlightedIndices = setOf(idx),
                    sortedIndices = (0..idx).toSet()
                )
            )
        }

        // Completion step
        steps.add(
            DsaVisualizerStep(
                stepIndex = total - 1,
                totalSteps = total,
                title = "Traversal Completed",
                description = "Visited all ${arr.size} elements sequentially in O(N) time. Total sum = $runningSum.",
                actionType = "COMPLETE",
                variables = mapOf("totalVisited" to "${arr.size}", "sum" to "$runningSum"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                sortedIndices = arr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Successfully visited all ${arr.size} elements. Final sum: $runningSum"
            )
        )

        return steps
    }

    // 2. Array Insertion
    fun generateArrayInsertion(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(10, 20, 30, 40, 50))
        val insertIndex = (initialArr.size / 2).coerceIn(0, initialArr.size - 1)
        val insertValue = 99
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = (initialArr.size - insertIndex) + 4

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "Insertion Request: Insert $insertValue at index [$insertIndex]",
                description = "To insert $insertValue at index $insertIndex, elements from index $insertIndex onwards must shift right by 1 position.",
                actionType = "INITIALIZE",
                variables = mapOf("targetIndex" to "$insertIndex", "newValue" to "$insertValue"),
                vizType = DsaVizType.ARRAY,
                arrayElements = initialArr,
                pointerLabels = mapOf(insertIndex to "target"),
                highlightedIndices = setOf(insertIndex)
            )
        )

        // Shifting steps
        val workingList = initialArr.toMutableList()
        workingList.add(0) // allocate slot

        for (i in workingList.size - 1 downTo insertIndex + 1) {
            workingList[i] = workingList[i - 1]
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = total,
                    title = "Shift Element Right",
                    description = "Shift element ${workingList[i]} from index [${i - 1}] to index [$i] to make space.",
                    actionType = "SHIFT",
                    variables = mapOf("fromIndex" to "${i - 1}", "toIndex" to "$i", "value" to "${workingList[i]}"),
                    vizType = DsaVizType.ARRAY,
                    arrayElements = workingList.toList(),
                    pointerLabels = mapOf(i to "shiftTo", i - 1 to "shiftFrom"),
                    highlightedIndices = setOf(i, i - 1)
                )
            )
        }

        // Insert element
        workingList[insertIndex] = insertValue
        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = total,
                title = "Place New Element",
                description = "Assigned value $insertValue into vacant index [$insertIndex].",
                actionType = "INSERT",
                variables = mapOf("index" to "$insertIndex", "inserted" to "$insertValue"),
                vizType = DsaVizType.ARRAY,
                arrayElements = workingList.toList(),
                pointerLabels = mapOf(insertIndex to "new"),
                highlightedIndices = setOf(insertIndex)
            )
        )

        // Completed
        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = total,
                title = "Insertion Complete",
                description = "Array size expanded to ${workingList.size}. All subsequent elements shifted right successfully.",
                actionType = "COMPLETE",
                variables = mapOf("newSize" to "${workingList.size}"),
                vizType = DsaVizType.ARRAY,
                arrayElements = workingList.toList(),
                sortedIndices = workingList.indices.toSet(),
                isCompleted = true,
                summaryResult = "Inserted $insertValue at index [$insertIndex] in O(N) shift time."
            )
        )

        return steps
    }

    // 3. Array Deletion
    fun generateArrayDeletion(customInput: String?): List<DsaVisualizerStep> {
        val initialArr = parseArrayInput(customInput, listOf(15, 25, 35, 45, 55, 65))
        val deleteIndex = 2 // delete 35
        val deletedValue = initialArr[deleteIndex]
        val steps = mutableListOf<DsaVisualizerStep>()
        val total = (initialArr.size - deleteIndex) + 3

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = total,
                title = "Deletion Target: Index [$deleteIndex] (Value: $deletedValue)",
                description = "Remove value $deletedValue at index $deleteIndex. Subsequent elements will shift left by 1 position.",
                actionType = "INITIALIZE",
                variables = mapOf("targetIndex" to "$deleteIndex", "deletedValue" to "$deletedValue"),
                vizType = DsaVizType.ARRAY,
                arrayElements = initialArr,
                pointerLabels = mapOf(deleteIndex to "delete"),
                highlightedIndices = setOf(deleteIndex)
            )
        )

        val workingList = initialArr.toMutableList()
        for (i in deleteIndex until workingList.size - 1) {
            workingList[i] = workingList[i + 1]
            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = total,
                    title = "Shift Element Left",
                    description = "Shift value ${workingList[i]} from index [${i + 1}] to index [$i] to close gap.",
                    actionType = "SHIFT",
                    variables = mapOf("fromIndex" to "${i + 1}", "toIndex" to "$i", "value" to "${workingList[i]}"),
                    vizType = DsaVizType.ARRAY,
                    arrayElements = workingList.toList(),
                    pointerLabels = mapOf(i to "dest", i + 1 to "src"),
                    highlightedIndices = setOf(i, i + 1)
                )
            )
        }

        workingList.removeAt(workingList.size - 1)

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = total,
                title = "Deletion Complete",
                description = "Element removed and contiguous memory compacted. New size: ${workingList.size}.",
                actionType = "COMPLETE",
                variables = mapOf("newSize" to "${workingList.size}", "deleted" to "$deletedValue"),
                vizType = DsaVizType.ARRAY,
                arrayElements = workingList.toList(),
                sortedIndices = workingList.indices.toSet(),
                isCompleted = true,
                summaryResult = "Deleted $deletedValue from index [$deleteIndex]. Compacting took O(N) operations."
            )
        )

        return steps
    }

    // 4. Array Operations (Access & In-place Update)
    fun generateArrayOperations(customInput: String?): List<DsaVisualizerStep> {
        val arr = parseArrayInput(customInput, listOf(12, 24, 36, 48, 60, 72))
        val targetIdx = 3
        val oldVal = arr[targetIdx]
        val updatedVal = 99
        val steps = mutableListOf<DsaVisualizerStep>()

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 4,
                title = "Direct Memory Access: O(1) Index Lookup",
                description = "Address calculation: baseAddress + (index * elementSize). Accessing index [$targetIdx] directly.",
                actionType = "ACCESS",
                variables = mapOf("index" to "$targetIdx", "value" to "$oldVal"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(targetIdx to "lookup"),
                highlightedIndices = setOf(targetIdx)
            )
        )

        val updatedArr = arr.toMutableList()
        updatedArr[targetIdx] = updatedVal

        steps.add(
            DsaVisualizerStep(
                stepIndex = 1,
                totalSteps = 4,
                title = "In-Place Value Update",
                description = "Overwriting arr[$targetIdx] = $updatedVal (previous value was $oldVal). Constant O(1) time.",
                actionType = "UPDATE",
                variables = mapOf("index" to "$targetIdx", "oldValue" to "$oldVal", "newValue" to "$updatedVal"),
                vizType = DsaVizType.ARRAY,
                arrayElements = updatedArr.toList(),
                pointerLabels = mapOf(targetIdx to "updated"),
                highlightedIndices = setOf(targetIdx)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 2,
                totalSteps = 4,
                title = "Bounds Checking & Safety Verification",
                description = "Verified index $targetIdx is within legal boundary [0 .. ${arr.size - 1}]. No IndexOutOfBoundsException.",
                actionType = "VERIFY",
                variables = mapOf("lowerBound" to "0", "upperBound" to "${arr.size - 1}", "valid" to "true"),
                vizType = DsaVizType.ARRAY,
                arrayElements = updatedArr.toList(),
                sortedIndices = setOf(targetIdx)
            )
        )

        steps.add(
            DsaVisualizerStep(
                stepIndex = 3,
                totalSteps = 4,
                title = "Array Operations Verified",
                description = "Demonstrated random access and in-place mutation in constant O(1) time and space.",
                actionType = "COMPLETE",
                variables = mapOf("status" to "SUCCESS"),
                vizType = DsaVizType.ARRAY,
                arrayElements = updatedArr.toList(),
                sortedIndices = updatedArr.indices.toSet(),
                isCompleted = true,
                summaryResult = "Direct access and updates complete in O(1) time complexity."
            )
        )

        return steps
    }

    // 5. Two Pointers
    fun generateTwoPointers(customInput: String?): List<DsaVisualizerStep> {
        val arr = parseArrayInput(customInput, listOf(2, 4, 7, 11, 15, 18, 22)).sorted()
        val targetSum = 18 // 7 + 11
        val steps = mutableListOf<DsaVisualizerStep>()

        var left = 0
        var right = arr.size - 1
        var found = false

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = 8,
                title = "Two Pointers Initialization: Target Sum = $targetSum",
                description = "Place Left pointer at index 0 and Right pointer at index ${arr.size - 1}. Sorted array ensures predictable sum changes.",
                actionType = "INITIALIZE",
                variables = mapOf("left" to "$left", "right" to "$right", "targetSum" to "$targetSum"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(left to "L", right to "R"),
                highlightedIndices = setOf(left, right)
            )
        )

        while (left < right && steps.size < 12) {
            val currentSum = arr[left] + arr[right]
            val isMatch = currentSum == targetSum

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = 8,
                    title = "Evaluate Sum: arr[$left] + arr[$right] = $currentSum",
                    description = "arr[$left] (${arr[left]}) + arr[$right] (${arr[right]}) = $currentSum. ${if (isMatch) "Matches target $targetSum!" else if (currentSum < targetSum) "Sum is less than $targetSum, increment Left." else "Sum is greater than $targetSum, decrement Right."}",
                    actionType = if (isMatch) "FOUND" else "COMPARE",
                    variables = mapOf(
                        "L" to "$left (${arr[left]})",
                        "R" to "$right (${arr[right]})",
                        "sum" to "$currentSum",
                        "target" to "$targetSum"
                    ),
                    vizType = DsaVizType.ARRAY,
                    arrayElements = arr,
                    pointerLabels = mapOf(left to "L", right to "R"),
                    highlightedIndices = setOf(left, right),
                    isFound = isMatch
                )
            )

            if (isMatch) {
                found = true
                break
            } else if (currentSum < targetSum) {
                left++
            } else {
                right--
            }
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = if (found) "Target Pair Found!" else "No Pair Matching Target Sum",
                description = if (found) "Found pair arr[$left]=${arr[left]} and arr[$right]=${arr[right]} summing to $targetSum." else "Search space exhausted without finding pair.",
                actionType = "COMPLETE",
                variables = mapOf("targetSum" to "$targetSum", "found" to "$found"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = if (found) mapOf(left to "PAIR", right to "PAIR") else emptyMap(),
                highlightedIndices = if (found) setOf(left, right) else emptySet(),
                sortedIndices = if (found) setOf(left, right) else emptySet(),
                isCompleted = true,
                summaryResult = if (found) "Pair (${arr[left]}, ${arr[right]}) found in O(N) single-pass time!" else "No pair equals $targetSum."
            )
        )

        return steps
    }

    // 6. Sliding Window
    fun generateSlidingWindow(customInput: String?): List<DsaVisualizerStep> {
        val arr = parseArrayInput(customInput, listOf(2, 1, 5, 1, 3, 2))
        val k = 3
        val steps = mutableListOf<DsaVisualizerStep>()

        var windowSum = arr.take(k).sum()
        var maxSum = windowSum
        var bestStart = 0

        steps.add(
            DsaVisualizerStep(
                stepIndex = 0,
                totalSteps = (arr.size - k + 2),
                title = "Initial Window [0..${k - 1}] of Size k=$k",
                description = "Sum of initial window elements ${arr.take(k)} = $windowSum. Current maximum sum = $maxSum.",
                actionType = "INITIALIZE",
                variables = mapOf("windowSize" to "$k", "windowSum" to "$windowSum", "maxSum" to "$maxSum"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                pointerLabels = mapOf(0 to "start", k - 1 to "end"),
                highlightedIndices = (0 until k).toSet()
            )
        )

        for (i in k until arr.size) {
            val outgoing = arr[i - k]
            val incoming = arr[i]
            windowSum = windowSum - outgoing + incoming
            val newMax = windowSum > maxSum
            if (newMax) {
                maxSum = windowSum
                bestStart = i - k + 1
            }

            steps.add(
                DsaVisualizerStep(
                    stepIndex = steps.size,
                    totalSteps = (arr.size - k + 2),
                    title = "Slide Window to [${i - k + 1}..$i]",
                    description = "Subtract outgoing arr[${i - k}] ($outgoing), add incoming arr[$i] ($incoming). New window sum = $windowSum.${if (newMax) " New max sum!" else ""}",
                    actionType = "SLIDE",
                    variables = mapOf(
                        "outgoing" to "$outgoing",
                        "incoming" to "$incoming",
                        "windowSum" to "$windowSum",
                        "maxSum" to "$maxSum"
                    ),
                    vizType = DsaVizType.ARRAY,
                    arrayElements = arr,
                    pointerLabels = mapOf(i - k + 1 to "L", i to "R"),
                    highlightedIndices = (i - k + 1..i).toSet()
                )
            )
        }

        steps.add(
            DsaVisualizerStep(
                stepIndex = steps.size,
                totalSteps = steps.size + 1,
                title = "Sliding Window Completed",
                description = "Maximum contiguous subarray sum of size $k is $maxSum (window indices [$bestStart..${bestStart + k - 1}]).",
                actionType = "COMPLETE",
                variables = mapOf("k" to "$k", "maxSum" to "$maxSum", "bestWindow" to "[$bestStart..${bestStart + k - 1}]"),
                vizType = DsaVizType.ARRAY,
                arrayElements = arr,
                highlightedIndices = (bestStart until bestStart + k).toSet(),
                sortedIndices = (bestStart until bestStart + k).toSet(),
                isCompleted = true,
                summaryResult = "Max subarray sum of size $k is $maxSum computed in O(N) time."
            )
        )

        return steps
    }
}
